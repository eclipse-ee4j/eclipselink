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
package org.eclipse.persistence.internal.indirection;

import java.rmi.server.ObjID;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.sessions.remote.RemoteValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.SessionLog;

/**
 * A UnitOfWorkValueHolder is put in a clone object.
 * It wraps the value holder in the original object to delay
 * cloning the attribute in a unit of work until it is
 * needed by the application.
 * This value holder is used only in the unit of work.
 *
 * @author    Sati
 */
public abstract class UnitOfWorkValueHolder extends DatabaseValueHolder implements WrappingValueHolder{

    /** The value holder in the original object. */
    protected transient ValueHolderInterface wrappedValueHolder;

    /** The mapping for the attribute. */
    protected transient DatabaseMapping mapping;

    /** The value holder stored in the backup copy, should not be transient. */
    protected ValueHolder backupValueHolder;

    /** These cannot be transient because they are required for a remote unit of work.
    When the remote uow is serialized to the server to be committed, these
    are used to reconstruct the value holder on the server.
    They should be null for non-remote sessions. */
    protected UnitOfWorkImpl remoteUnitOfWork;
    protected Object sourceObject;

    /** This attribute is used specifically for relationship support.  It mimics the
     * sourceObject attribute which is used for RemoteValueholder
     */
    protected transient Object relationshipSourceObject;
    protected String sourceAttributeName;
    protected ObjID wrappedValueHolderRemoteID;

    protected UnitOfWorkValueHolder() {
        super();
    }
    
    protected UnitOfWorkValueHolder(ValueHolderInterface attributeValue, Object clone, DatabaseMapping mapping, UnitOfWorkImpl unitOfWork) {
        this.wrappedValueHolder = attributeValue;
        this.mapping = mapping;
        this.session = unitOfWork;
        this.sourceAttributeName = mapping.getAttributeName();
        this.relationshipSourceObject = clone;

        if (unitOfWork.isRemoteUnitOfWork()) {
            if (attributeValue instanceof RemoteValueHolder) {
                this.wrappedValueHolderRemoteID = ((RemoteValueHolder)attributeValue).getID();
            }
            this.remoteUnitOfWork = unitOfWork;
            this.sourceObject = clone;
        }
    }

    /**
     * Backup the clone attribute value.
     */
    protected abstract Object buildBackupCloneFor(Object cloneAttributeValue);

    /**
     * Clone the original attribute value.
     */
    public abstract Object buildCloneFor(Object originalAttributeValue);

    protected ValueHolder getBackupValueHolder() {
        return backupValueHolder;
    }

    public DatabaseMapping getMapping() {
        return mapping;
    }

    protected UnitOfWorkImpl getRemoteUnitOfWork() {
        return remoteUnitOfWork;
    }

    protected String getSourceAttributeName() {
        return sourceAttributeName;
    }

    protected Object getSourceObject() {
        return sourceObject;
    }

    protected Object getRelationshipSourceObject() {
        return this.relationshipSourceObject;
    }

    protected UnitOfWorkImpl getUnitOfWork() {
        return (UnitOfWorkImpl)this.session;
    }

    /**
     * This is used for a remote unit of work.
     * If the value holder is sent back to the server uninstantiated and
     * it needs to be instantiated, then we must find the original
     * object and get the appropriate attribute from it.
     */
    protected Object getValueFromServerObject() {
        setSession(getRemoteUnitOfWork());
        Object primaryKey = getSession().getId(getSourceObject());
        Object originalObject = getUnitOfWork().getParent().getIdentityMapAccessor().getFromIdentityMap(primaryKey, getSourceObject().getClass());
        if (originalObject == null) {
            originalObject = getUnitOfWork().getParent().readObject(getSourceObject());
        }
        ClassDescriptor descriptor = getSession().getDescriptor(originalObject);
        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(getSourceAttributeName());
        setMapping(mapping);
        return getMapping().getRealAttributeValueFromObject(originalObject, getSession());
    }

    /**
     * a.k.a getValueFromWrappedValueholder.
     * The old name is no longer correct, as query based valueholders are now
     * sometimes triggered directly without triggering the underlying valueholder.
     */
    protected Object instantiateImpl() {
        if (this.wrappedValueHolder instanceof DatabaseValueHolder) {
            // Bug 3835202 - Ensure access to valueholders is thread safe.  Several of the methods
            // called below are not threadsafe alone.
            synchronized (this.wrappedValueHolder) {
                DatabaseValueHolder wrapped = (DatabaseValueHolder)this.wrappedValueHolder;
                UnitOfWorkImpl unitOfWork = getUnitOfWork();
                if (!wrapped.isEasilyInstantiated()) {
                    if (wrapped.isPessimisticLockingValueHolder()) {
                        if (!unitOfWork.getCommitManager().isActive() && !unitOfWork.wasTransactionBegunPrematurely()) {
                            unitOfWork.beginEarlyTransaction();
                        }
                        unitOfWork.log(SessionLog.FINEST, SessionLog.TRANSACTION, "instantiate_pl_relationship");
                    }
                    if (unitOfWork.getCommitManager().isActive() || unitOfWork.wasTransactionBegunPrematurely()) {
                        // At this point the wrapped valueholder is not triggered,
                        // and we are in transaction.  So just trigger the
                        // UnitOfWork valueholder on the UnitOfWork only.
                        return wrapped.instantiateForUnitOfWorkValueHolder(this);
                    }
                }
            }
        }
        return buildCloneFor(this.wrappedValueHolder.getValue());
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is easy to instantiate.
     * @return true if getValue() won't trigger a database read.
     */
    public boolean isEasilyInstantiated() {
        return this.isInstantiated || ((this.wrappedValueHolder != null)
                && (!(this.wrappedValueHolder instanceof DatabaseValueHolder) || ((DatabaseValueHolder)this.wrappedValueHolder).isEasilyInstantiated()));
    }

    /**
     * INTERNAL:
     * Answers if this valueholder is a pessimistic locking one.  Such valueholders
     * are special in that they can be triggered multiple times by different
     * UnitsOfWork.  Each time a lock query will be issued.  Hence even if
     * instantiated it may have to be instantiated again, and once instantatiated
     * all fields can not be reset.
     */
    public boolean isPessimisticLockingValueHolder() {
        // This abstract method needs to be implemented but is not meaningfull for
        // this subclass.
        return ((this.wrappedValueHolder != null) && (this.wrappedValueHolder instanceof DatabaseValueHolder) && ((DatabaseValueHolder)this.wrappedValueHolder).isPessimisticLockingValueHolder());
    }

    @Override
    public ValueHolderInterface getWrappedValueHolder() {
        return wrappedValueHolder;
    }

    /**
     * returns wrapped ValueHolder ObjID if available
     */
    public ObjID getWrappedValueHolderRemoteID() {
        return this.wrappedValueHolderRemoteID;
    }

    /**
     * Used to determine if this is a remote uow value holder that was serialized to the server.
     * It has no reference to its wrapper value holder, so must find its original object to be able to instantiate.
     */
    public boolean isSerializedRemoteUnitOfWorkValueHolder() {
        return (this.remoteUnitOfWork != null) && (this.remoteUnitOfWork.getParent() != null) && (this.wrappedValueHolder == null);
    }
    
    /**
     * Get the value from the wrapped value holder, instantiating it
     * if necessary, and clone it.
     */
    protected Object instantiate() {
        Object originalAttributeValue;
        Object cloneAttributeValue;
        if (isSerializedRemoteUnitOfWorkValueHolder()) {
            originalAttributeValue = getValueFromServerObject();
            cloneAttributeValue = buildCloneFor(originalAttributeValue);
        } else {
            if (getUnitOfWork() == null) {
                throw ValidationException.instantiatingValueholderWithNullSession();
            }
            cloneAttributeValue = instantiateImpl();
        }

        // Set the value in the backup clone also.
        // In some cases we may want to force instantiation before the backup is built
        if (this.backupValueHolder != null) {
            this.backupValueHolder.setValue(buildBackupCloneFor(cloneAttributeValue));
        }
        return cloneAttributeValue;
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
     */
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        // This abstract method needs to be implemented but is not meaningful for
        // this subclass.
        return instantiate();
    }

    /**
     * Releases a wrapped valueholder privately owned by a particular unit of work.
     * <p>
     * When unit of work clones are built directly from rows no object in the shared
     * cache points to this valueholder, so it can store the unit of work as its
     * session.  However once that UnitOfWork commits and the valueholder is merged
     * into the shared cache, the session needs to be reset to the root session, ie.
     * the server session.
     */
    @Override
    public void releaseWrappedValueHolder(AbstractSession targetSession) {
        // On UnitOfWork dont want to do anything.
        return;
    }

    /**
     * Reset all the fields that are not needed after instantiation.
     */
    protected void resetFields() {
        //do nothing.  nothing should be reset to null;
    }

    public void setBackupValueHolder(ValueHolder backupValueHolder) {
        this.backupValueHolder = backupValueHolder;
    }

    protected void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    protected void setRemoteUnitOfWork(UnitOfWorkImpl remoteUnitOfWork) {
        this.remoteUnitOfWork = remoteUnitOfWork;
    }

    protected void setSourceAttributeName(String name) {
        sourceAttributeName = name;
    }

    protected void setSourceObject(Object sourceObject) {
        this.sourceObject = sourceObject;
    }

    protected void setRelationshipSourceObject(Object relationshipSourceObject) {
        this.relationshipSourceObject = relationshipSourceObject;
    }

    protected void setWrappedValueHolder(DatabaseValueHolder valueHolder) {
        wrappedValueHolder = valueHolder;
    }
    
    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    public boolean shouldAllowInstantiationDeferral() {
        return ((WeavedAttributeValueHolderInterface)this.wrappedValueHolder).shouldAllowInstantiationDeferral();
    }

}
