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

import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * DatabaseValueHolder wraps a database-stored object and implements
 * behavior to access it. The object is read only once from database
 * after which is cached for faster access.
 *
 * @see ValueHolderInterface
 * @author    Dorin Sandu
 */
public abstract class DatabaseValueHolder implements WeavedAttributeValueHolderInterface, Cloneable, Serializable {

    /** Stores the object after it is read from the database. */
    protected Object value;

    /** Indicates whether the object has been read from the database or not. */
    protected boolean isInstantiated;

    /** Stores the session for the database that contains the object. */
    protected transient AbstractSession session;

    /** Stores the row representation of the object. */
    // Cannot be transient as may be required to extract the pk from a serialized object.
    protected AbstractRecord row;
  
    /**
     * The variable below is used as part of the implementation of WeavedAttributeValueHolderInterface
     * It is used to track whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     * Set internally in EclipseLink when the state of coordination between a weaved valueholder and the underlying property is known
     */
    protected boolean isCoordinatedWithProperty = false;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError();
        }
    }

    /**
     * Return the row.
     */
    public AbstractRecord getRow() {
        return row;
    }

    /**
     * Return the session.
     */
    public AbstractSession getSession() {
        return session;
    }

    public ValueHolderInterface getWrappedValueHolder() {
        return null;
    }

    /**
     * Return the object.
     */
    public Object getValue() {
        if (!this.isInstantiated) {
            synchronized (this) {
                if (!this.isInstantiated) {
                    // The value must be set directly because the setValue can also cause instantiation under UOW.
                    privilegedSetValue(instantiate());
                    this.isInstantiated = true;
                    postInstantiate();
                    resetFields();
                }
            }
        }
        return value;
    }

    /**
     * Instantiate the object.
     */
    protected abstract Object instantiate() throws DatabaseException;

    /**
     * Triggers UnitOfWork valueholders directly without triggering the wrapped
     * valueholder (this).
     * <p>
     * When in transaction and/or for pessimistic locking the UnitOfWorkValueHolder
     * needs to be triggered directly without triggering the wrapped valueholder.
     * However only the wrapped valueholder knows how to trigger the indirection,
     * i.e. it may be a batchValueHolder, and it stores all the info like the row
     * and the query.
     * Note: Implementations of this method are not necessarily thread-safe.  They must 
     * be used in a synchronized manner
     */
    public abstract Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder);

    /**
     * This method is used as part of the implementation of WeavedAttributeValueHolderInterface
     * It is used to check whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     */
    public boolean isCoordinatedWithProperty(){
        return isCoordinatedWithProperty;
    }
    
    /**
     * This method is used as part of the implementation of WeavedAttributeValueHolderInterface.
     * 
     * A DatabaseValueHolder is set up by TopLink and will never be a newly weaved valueholder.
     * As a result, this method is stubbed out.
     */
    public boolean isNewlyWeavedValueHolder(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Answers if this valueholder is easy to instantiate.
     * @return true if getValue() won't trigger a database read.
     */
    public boolean isEasilyInstantiated() {
        return this.isInstantiated;
    }

    /**
     * Return a boolean indicating whether the object
     * has been read from the database or not.
     */
    public boolean isInstantiated() {
        return isInstantiated;
    }

    /**
     * Answers if this valueholder is a pessimistic locking one.  Such valueholders
     * are special in that they can be triggered multiple times by different
     * UnitsOfWork.  Each time a lock query will be issued.  Hence even if
     * instantiated it may have to be instantiated again, and once instantiated
     * all fields can not be reset.
     * Note: Implementations of this method are not necessarily thread-safe.  They must 
     * be used in a synchronizaed manner
     */
    public abstract boolean isPessimisticLockingValueHolder();

    /**
     * Answers if this valueholder is referenced only by a UnitOfWork valueholder.
     * I.e. it was built in valueFromRow which was called by buildCloneFromRow.
     * <p>
     * Sometimes in transaction a UnitOfWork clone, and all valueholders, are built
     * directly from the row; however a UnitOfWorkValueHolder does not know how to
     * instantiate itself so wraps this which does.
     * <p>
     * On a successful merge must be released to the session cache with
     * releaseWrappedValueHolder.
     */
    protected boolean isTransactionalValueHolder() {
        return ((session != null) && session.isUnitOfWork());
    }
    
    /**
     * Used to determine if this is a remote uow value holder that was serialized to the server.
     * It has no reference to its wrapper value holder, so must find its original object to be able to instantiate.
     */
    public boolean isSerializedRemoteUnitOfWorkValueHolder() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Run any extra code required after the valueholder instantiates
     * @see QueryBasedValueHolder
     */
    public void postInstantiate(){
        //noop
    }
    
    /**
     * Set the object. This is used only by the privileged methods. One must be very careful in using this method.
     */
    public void privilegedSetValue(Object value) {
        this.value = value;
        isCoordinatedWithProperty = false;
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
    public void releaseWrappedValueHolder(AbstractSession targetSession) {
        AbstractSession session = this.session;
        if ((session != null) && session.isUnitOfWork()) {
            this.session = targetSession;
        }
    }

    /**
     * Reset all the fields that are not needed after instantiation.
     */
    protected void resetFields() {
        this.row = null;
        this.session = null;
    }
    
    /**
     * This method is used as part of the implementation of WeavedAttributeValueHolderInterface
     * It is used internally by EclipseLink to set whether a valueholder that has been weaved into a class is coordinated
     * with the underlying property
     */
    public void setIsCoordinatedWithProperty(boolean coordinated){
        this.isCoordinatedWithProperty = coordinated;
    }

    /**
     * This method is used as part of the implementation of WeavedAttributeValueHolderInterface
     * 
     * A DatabaseValueHolder is set up by EclipseLink and will never be a newly weaved valueholder 
     * As a result, this method is stubbed out.
     */
    public void setIsNewlyWeavedValueHolder(boolean isNew){
    }
    
    /**
     * Set the instantiated flag to true.
     */
    public void setInstantiated() {
        isInstantiated = true;
    }

    /**
     * Set the row.
     */
    public void setRow(AbstractRecord row) {
        this.row = row;
    }

    /**
     * Set the session.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * Set the instantiated flag to false.
     */
    public void setUninstantiated() {
        isInstantiated = false;
    }

    /**
     * Set the object.
     */
    public void setValue(Object value) {
        this.value = value;
        setInstantiated();
    }

    /**
     * INTERNAL:
     * Return if add/remove should trigger instantiation or avoid.
     * Current instantiation is avoided is using change tracking.
     */
    public boolean shouldAllowInstantiationDeferral() {
        return true;
    }

    public String toString() {
        if (isInstantiated()) {
            return "{" + getValue() + "}";
        } else {
            return "{" + Helper.getShortClassName(getClass()) + ": " + ToStringLocalization.buildMessage("not_instantiated", (Object[])null) + "}";
        }
    }
}
