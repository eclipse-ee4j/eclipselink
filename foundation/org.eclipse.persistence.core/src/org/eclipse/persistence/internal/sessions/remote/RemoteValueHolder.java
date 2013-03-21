/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.remote;

import java.rmi.server.ObjID;
import java.io.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.*;

/**
 * Remote value holders can be invoked locally and remotely.
 * In both situations the associated indirect object is invoked.
 */
public class RemoteValueHolder extends DatabaseValueHolder implements Externalizable {
    // This is a unique id for remote value holder.
    protected ObjID id;

    // Primary key row for target object.
    protected Object targetObjectPrimaryKeys;
    protected transient DatabaseMapping mapping;
    protected transient ObjectLevelReadQuery query;

    // The server side original value holder.
    protected transient ValueHolderInterface wrappedServerValueHolder;

    // point back to the object holding the remote value holder;
    // for the moment, used only by TransparentIndirection
    protected transient Object serverIndirectionObject;

    public RemoteValueHolder() {
        // This assigns unique id to the remote value holder when it is created.
        this.id = new ObjID();
    }
    
    public RemoteValueHolder(ObjID id) {
        this.id = id;
    }

    /**
     * If the reference object is mapped thru one to one mapping and the object derives its primary key value
     * from this relationship then remote value holder has a primary key row for.
     */
    protected boolean canDoCacheCheck() {
        if ((getMapping() == null) || (getQuery() == null)) {
            return false;
        }

        return (getTargetObjectPrimaryKeys() != null) && getMapping().isOneToOneMapping() && (!getQuery().shouldRefreshIdentityMapResult()) && (!getQuery().shouldRefreshRemoteIdentityMapResult()) && (getQuery().shouldMaintainCache());
    }

    /**
     * Only the id is checked for equality check.
     */
    public boolean equals(Object object) {
        if (!(object instanceof RemoteValueHolder)) {
            return false;
        }

        return getID().equals(((RemoteValueHolder)object).getID());
    }

    /**
     * This method is used to remove the RemoteValueHolder from the dispatcher on Garbage
     * collection from the client
     */
    public void finalize() {
        if ((this.session != null) && (this.session instanceof RemoteSession) && ((RemoteSession)this.session).shouldEnableDistributedIndirectionGarbageCollection()) {
            // This must be on the client
            RemoveServerSideRemoteValueHolderCommand command = new RemoveServerSideRemoteValueHolderCommand(getID());
            ((DistributedSession)getSession()).getRemoteConnection().processCommand(command);
            //remove this value holder from the session dispatcher as it will no longer be needed
        }
    }

    /**
     * Return the unique id.
     */
    public ObjID getID() {
        return id;
    }

    /**
     * Return the associated mapping.
     */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * Get object from the cache if there is one.
     */
    protected Object getObjectFromCache() {
        Object cachedObject = null;

        if (getMapping().isOneToOneMapping()) {
            ClassDescriptor descriptor = ((ObjectReferenceMapping)getMapping()).getReferenceDescriptor();
            cachedObject = getSession().getIdentityMapAccessorInstance().getFromIdentityMap(getTargetObjectPrimaryKeys(), descriptor.getJavaClass(), descriptor);
        }

        return cachedObject;
    }

    /**
     * Return the associated query.
     */
    public ObjectLevelReadQuery getQuery() {
        return query;
    }

    /**
     * Return the object on the server that holds on
     * to the remote value holder.
     * Currently used only by TransparentIndirection so we
     * can get back to the original IndirectContainer.
     */
    public Object getServerIndirectionObject() {
        return serverIndirectionObject;
    }

    /**
     * Get target object primary key row.
     */
    protected Object getTargetObjectPrimaryKeys() {
        return targetObjectPrimaryKeys;
    }

    /**
     * Return the original value holder.
     * This is null on the client side because it is tagged transient.
     * This is how we know whether the remote value holder is
     * being invoked on the client or on the server.
     */
    public ValueHolderInterface getWrappedServerValueHolder() {
        return wrappedServerValueHolder;
    }

    /**
     * Return the hashcode for id, because it is unqiue.
     */
    public int hashCode() {
        return getID().hashCode();
    }

    /**
     * Return the object.
     */
    public synchronized Object instantiate() {
        Object valueOfServerValueHolder = null;

        if (getWrappedServerValueHolder() != null) {// server invocation
            valueOfServerValueHolder = getWrappedServerValueHolder().getValue();
        } else {// client invocation
            // check whether object exists on the client
            if (canDoCacheCheck()) {
                valueOfServerValueHolder = getObjectFromCache();
            }

            // does not exist on the client - so invoke the value holder on the server
            if (valueOfServerValueHolder == null) {
                valueOfServerValueHolder = ((DistributedSession)getSession()).instantiateRemoteValueHolderOnServer(this);
            }
        }
        return valueOfServerValueHolder;
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is easy to instantiate.
     * @return true if getValue() won't trigger a database read.
     */
    public boolean isEasilyInstantiated() {
        // Nothing is easily instantiated when on the client side.
        return this.isInstantiated || ((this.wrappedServerValueHolder != null)
                && (!(this.wrappedServerValueHolder instanceof DatabaseValueHolder) || ((DatabaseValueHolder)this.wrappedServerValueHolder).isEasilyInstantiated()));
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is a pessimistic locking one.  Such valueholders
     * are special in that they can be triggered multiple times by different
     * UnitsOfWork.  Each time a lock query will be issued.  Hence even if
     * instantiated it may have to be instantiated again, and once instantatiated
     * all fields can not be reset.
     * Note: This method is not thread-safe.  It must be used in a synchronizaed manner
     */
    public boolean isPessimisticLockingValueHolder() {
        // This abstract method needs to be implemented but is not meaningfull for
        // this subclass.
        if (getWrappedServerValueHolder() != null) {
            return ((getWrappedServerValueHolder() instanceof DatabaseValueHolder) && ((DatabaseValueHolder)getWrappedServerValueHolder()).isPessimisticLockingValueHolder());
        } else {
            // Pessimistic locking may not be supported on remote sessions, but if 
            // it is make every attempt to do the right thing.
            return ((getQuery() != null) && getQuery().isLockQuery(getSession()));
        }
    }

    /**
     * Triggers UnitOfWork valueholders directly without triggering the wrapped
     * valueholder (this).
     * <p>
     * When in transaction and/or for pessimistic locking the UnitOfWorkValueHolder
     * needs to be triggered directly without triggering the wrapped valueholder.
     * However only the wrapped valueholder knows how to trigger the indirection,
     * i.e. it may be a batchValueHolder, and it stores all the info like the row
     * and the query.
     * Note: This method is not thread-safe.  It must be used in a synchronizaed manner
     */
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        if ((getWrappedServerValueHolder() != null) && (getWrappedServerValueHolder() instanceof DatabaseValueHolder)) {
            DatabaseValueHolder wrapped = (DatabaseValueHolder)getWrappedServerValueHolder();
            return wrapped.instantiateForUnitOfWorkValueHolder(unitOfWorkValueHolder);
        }

        // The scenario of triggering a valueholder in transaction when
        // the RemoteUnitOfWork is on the client side may be impossible.
        return unitOfWorkValueHolder.buildCloneFor(getValue());
    }

    /**
     *  Override the default serialization for a remote valueholder so as not to serialize the value
     *  Note: Changed for bug 3145211.  We used to use the java.io.Serializable interface, but need to convert
     *  to Externalizable interface to avoid sending extra data through the superclass's serialization
     */
    public void readExternal(ObjectInput in) throws IOException, java.lang.ClassNotFoundException {
        this.id = (ObjID)in.readObject();
        this.targetObjectPrimaryKeys = in.readObject();
        this.row = (AbstractRecord)in.readObject();
        this.isInstantiated = in.readBoolean();
    }

    /**
     * Set the unique id.
     */
    protected void setID(ObjID anID) {
        this.id = anID;
    }

    /**
     * Set mapping
     */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * Set the query.
     */
    public void setQuery(ObjectLevelReadQuery query) {
        this.query = query;
    }

    /**
     * Set the object on the server that holds on
     * to the remote value holder.
     * Currently used only by TransparentIndirection so we
     * can get back to the original IndirectContainer.
     */
    public void setServerIndirectionObject(Object serverIndirectionObject) {
        this.serverIndirectionObject = serverIndirectionObject;
    }

    /**
     * Set target object primary keys.
     */
    public void setTargetObjectPrimaryKeys(Object primaryKeys) {
        this.targetObjectPrimaryKeys = primaryKeys;
    }

    /**
     * Set the object.
     */
    public void setValue(Object theValue) {
        super.setValue(theValue);
        if (getWrappedServerValueHolder() != null) {
            // This is a local setting of remote value holder
            // and will only happen with basic indirection
            getWrappedServerValueHolder().setValue(theValue);
        }
    }

    /**
     * Set the original value holder.
     */
    public void setWrappedServerValueHolder(ValueHolderInterface wrappedServerValueHolder) {
        this.wrappedServerValueHolder = wrappedServerValueHolder;
    }

    /**
     *  Override the default serialization for a remote valueholder so as not to serialize the value
     *  Note: Changed for bug 3145211.  We used to use the java.io.Serializable interface, but need to convert
     *  to Externalizable interface to avoid sending extra data through the superclass's serialization
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(id);
        out.writeObject(targetObjectPrimaryKeys);
        out.writeObject(row);
        out.writeBoolean(false);
    }
}
