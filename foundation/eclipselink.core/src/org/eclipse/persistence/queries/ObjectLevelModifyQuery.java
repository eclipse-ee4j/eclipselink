/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.queries;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all object modify queries.
 *
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Stores & retrieves the object to modify.
 * <li> Stores & retrieves the primary key of the objects.
 * </ul>
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public abstract class ObjectLevelModifyQuery extends ModifyQuery {

    /* Primary key of the object to be modified. */
    protected Vector primaryKey;

    /* The object being modified. */
    protected Object object;

    /* A changeSet representing the object being modified */
    protected ObjectChangeSet objectChangeSet;

    /* The clone of the object being modified from unit of work. */
    protected Object backupClone;

    /**
     * PUBLIC:
     * Initialize the state of the query.
     */
    public ObjectLevelModifyQuery() {
        this.cascadePolicy = CascadePrivateParts;
    }

    /**
     * INTERNAL:
     * Ensure that the descriptor has been set.
     */
    public void checkDescriptor(AbstractSession session) throws QueryException {
        if (getDescriptor() == null) {
            if (getObject() == null) {
                throw QueryException.objectToModifyNotSpecified(this);
            }

            //Bug#3947714  Pass the object instead of class in case object is proxy            
            ClassDescriptor referenceDescriptor = session.getDescriptor(getObject());
            if (referenceDescriptor == null) {
                throw QueryException.descriptorIsMissing(getObject().getClass(), this);
            }
            setDescriptor(referenceDescriptor);
        }
    }

    /**
     * INTERNAL:
     * All have done is move code from UnitOfWork.internalExecuteQuery
     */
    public Object executeInUnitOfWork(UnitOfWorkImpl unitOfWork, AbstractRecord translationRow) throws DatabaseException {
        if (unitOfWork.isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.illegalOperationForUnitOfWorkLifecycle(unitOfWork.getLifecycle(), "executeQuery(ObjectLevelModifyQuery)");
        }
        return executeInUnitOfWorkObjectLevelModifyQuery(unitOfWork, translationRow);
    }

    /**
     * INTERNAL:
     * This code was moved from UnitOfWork.internalExecuteQuery
     * @param unitOfWork
     * @param translationRow
     * @return
     * @throws org.eclipse.persistence.exceptions.DatabaseException
     * @throws org.eclipse.persistence.exceptions.OptimisticLockException
     */
    protected Object executeInUnitOfWorkObjectLevelModifyQuery(UnitOfWorkImpl unitOfWork, AbstractRecord translationRow) throws DatabaseException, OptimisticLockException {
        if (!unitOfWork.getCommitManager().isActive()) {
            throw QueryException.invalidQuery(this);
        }

        if ((getObject() != null) && (unitOfWork.isClassReadOnly(getObject().getClass()))) {
            return getObject();
        }

        // CR#3216 - Apply check to ObjectLevelModifyQuery not just WriteObjectQuery
        if (unitOfWork.shouldPerformNoValidation() && unitOfWork.getUnregisteredExistingObjects().containsKey(getObject())) {
            //if the object is an unregistered existing object then skip it.  This
            // Will only be in the collection if validation is turned off
            return null;
        }

        return super.executeInUnitOfWork(unitOfWork, translationRow);
    }

    /**
     * INTERNAL:
     * Return the backup clone of the object from the unit of work.
     */
    public Object getBackupClone() {
        // PERF: A backup clone is only required for the old commit,
        // So avoid its creation for normal commit.	
        if ((backupClone == null) && getSession().isUnitOfWork()) {
            setBackupClone(((UnitOfWorkImpl)getSession()).getBackupCloneForCommit(getObject()));
        }
        return backupClone;
    }

    /**
     * PUBLIC:
     * Return the object required for modification.
     */
    public Object getObject() {
        return object;
    }

    /**
     * PUBLIC:
     * Return the ObjectChangeSet representing the object being changed
     */
    public ObjectChangeSet getObjectChangeSet() {
        return this.objectChangeSet;
    }

    /**
     * INTERNAL:
     * Get the primary key for the query
     */
    public Vector getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Return the domain class associated with this query.
     */
    public Class getReferenceClass() {
        if (getObject() == null) {
            return null;
        }
        return getObject().getClass();        
    }

    /**
     * INTERNAL:
     * Return the reference class for a query
     * Note: Although the API is designed to avoid classpath dependencies for the MW, since the object
     * is specified at runtime, this will not be an issue.
     */
    public String getReferenceClassName() {
        if (getReferenceClass() != null) {
            return getReferenceClass().getName();
        } else {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Return if this is an object level modify query.
     */
    public boolean isObjectLevelModifyQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     * In particular check that the tables on the descriptor are set.
     */
    protected void prepare() throws QueryException {
        checkDescriptor(getSession());

        if (getObject() != null) {// Prepare can be called without the object set yet.
            setObject(getDescriptor().getObjectBuilder().unwrapObject(getObject(), getSession()));
        }

        if (getDescriptor().isAggregateDescriptor()) {
            throw QueryException.aggregateObjectCannotBeDeletedOrWritten(getDescriptor(), this);
        }

        super.prepare();
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     * In particular check that the tables on the descriptor are set.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        if (getObject() == null) {
            throw QueryException.objectToModifyNotSpecified(this);
        }

        setObject(getDescriptor().getObjectBuilder().unwrapObject(getObject(), getSession()));

        if (getPrimaryKey() == null) {
            if (getObjectChangeSet() != null) {
                setPrimaryKey(getObjectChangeSet().getPrimaryKeys());
            } else {
                setPrimaryKey(getDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(getObject(), getSession()));
            }
        }
    }

    /**
     * INTERNAL:
     * Set the backup clone of the object from the unit of work.
     */
    public void setBackupClone(Object backupClone) {
        this.backupClone = backupClone;
    }

    /**
     * PUBLIC: (REQUIRED)
     * Set the object required for modification.
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * INTERNAL:
     * Set the ObjectChangeSet representing the object to be written
     */
    public void setObjectChangeSet(ObjectChangeSet changeSet) {
        this.objectChangeSet = changeSet;
    }

    /**
     * INTERNAL:
     * Set the primary key for the query.
     */
    public void setPrimaryKey(Vector primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + String.valueOf(getObject()) + ")";
    }
}
