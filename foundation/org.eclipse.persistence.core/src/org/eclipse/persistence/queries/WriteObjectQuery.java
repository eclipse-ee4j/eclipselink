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
package org.eclipse.persistence.queries;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * <p><b>Purpose</b>:
 * Used for inserting or updating objects
 * WriteObjectQuery determines whether to perform a insert or an update on the database.
 *
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Determines whether to perform a insert or an update on the database.
 * <li> Stores object in identity map for insert if required.
 * </ul>
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class WriteObjectQuery extends ObjectLevelModifyQuery {
    public WriteObjectQuery() {
        super();
    }

    public WriteObjectQuery(Object objectToWrite) {
        this();
        setObject(objectToWrite);
    }

    public WriteObjectQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * INTERNAL:
     * Perform a does exist check to decide whether to perform an insert or update and
     * delegate the work to the mechanism.  Does exists check will also perform an
     * optimistic lock check if required.
     * @exception  DatabaseException - an error has occurred on the database
     * @exception  OptimisticLockException - an error has occurred using the optimistic lock feature
     * @return object - the object being written.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException {
        if (getObjectChangeSet() != null) {
            return getQueryMechanism().executeWriteWithChangeSet();
        } else {
            return getQueryMechanism().executeWrite();
        }
    }

    /**
     * INTERNAL:
     * Perform a does exist check to decide whether to perform an insert or update and
     * delegate the work to the mechanism.
     */
    public void executeCommit() throws DatabaseException, OptimisticLockException {
        boolean doesExist;

        if (getSession().isUnitOfWork()) {
            doesExist = !((UnitOfWorkImpl)getSession()).isCloneNewObject(getObject());
            if (doesExist) {
                doesExist = ((UnitOfWorkImpl)getSession()).isObjectRegistered(getObject());
            }
        } else {
            //Initialize does exist query
            DoesExistQuery existQuery = (DoesExistQuery)this.descriptor.getQueryManager().getDoesExistQuery().clone();
            existQuery.setObject(getObject());
            existQuery.setPrimaryKey(getPrimaryKey());
            existQuery.setDescriptor(this.descriptor);
            existQuery.setTranslationRow(getTranslationRow());

            doesExist = ((Boolean)getSession().executeQuery(existQuery)).booleanValue();
        }

        // Do insert of update
        if (doesExist) {            
            // Must do an update
            getQueryMechanism().updateObjectForWrite();
        } else {
            // Must do an insert
            getQueryMechanism().insertObjectForWrite();
        }
    }

    /**
     * INTERNAL:
     * Perform a does exist check to decide whether to perform an insert or update and
     * delegate the work to the mechanism.
     */
    public void executeCommitWithChangeSet() throws DatabaseException, OptimisticLockException {
        // Do insert of update                    
        if (!getObjectChangeSet().isNew()) {
            // Must do an update            
            if (!getSession().getCommitManager().isCommitInPreModify(getObject())) {
                //If the changeSet is in the PreModify then it is in the process of being written
                getQueryMechanism().updateObjectForWriteWithChangeSet();
            }
        } else {
            // check whether the object is already being committed -
            // if it is and it is new, then a shallow insert must be done
            if (getSession().getCommitManager().isCommitInPreModify(getObject())) {
                // a shallow insert must be performed
                this.dontCascadeParts();
                getQueryMechanism().insertObjectForWrite();
                getSession().getCommitManager().markShallowCommit(object);
            } else {
                // Must do an insert
                getQueryMechanism().insertObjectForWrite();
            }
        }
    }

    /**
     * INTERNAL:
     * Returns the specific default redirector for this query type.  There are numerous default query redirectors.
     * See ClassDescriptor for their types.
     */
    protected QueryRedirector getDefaultRedirector(){
        return descriptor.getDefaultQueryRedirector();
    }

    /**
     * PUBLIC:
     * Return if this is a write object query.
     */
    public boolean isWriteObjectQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        // Set the tranlation row, it may already be set in the custom query situation.
        // PERF: Only build the translation row for updates, as inserts do not have a where clause.
        if ((!isInsertObjectQuery()) && ((getTranslationRow() == null) || (getTranslationRow().isEmpty()))) {
            setTranslationRow(this.descriptor.getObjectBuilder().buildRowForTranslation(getObject(), getSession()));
        }
    }
}
