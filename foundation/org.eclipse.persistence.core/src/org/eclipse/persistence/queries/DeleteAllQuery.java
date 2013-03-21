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

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose</b>:
 * Query used to delete a collection of objects.
 * This is used by mappings to delete all of their target objects in a single database call.
 * The SQL/SQLStatements must be provided.
 * <p>
 * DeleteAll can also be used with an Expression (or JPQL) to dynamically delete
 * a set of objects from the database, and invalidate them in the cache.
 *
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Stores & retrieves the objects to delete.
 * <li> Store the where clause used for the deletion.
 * </ul>
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DeleteAllQuery extends ModifyAllQuery {

    /** List containing objects to be deleted, these should be removed from the identity map after deletion. */
    protected List<Object> objects;

    /**
     * Defines if objects should be remove from the persistence context only (no database).
     * This is used if delete was already cascaded by the database.
     */
    protected boolean isInMemoryOnly;

    /**
     * PUBLIC:
     */
    public DeleteAllQuery() {
        super();
    }

    /**
     * PUBLIC:
     * Create a new delete all query for the class specified.
     */
    public DeleteAllQuery(Class referenceClass) {
        super(referenceClass);
    }

    /**
     * PUBLIC:
     * Create a new delete all query for the class and the selection criteria
     * specified.
     */
    public DeleteAllQuery(Class referenceClass, Expression selectionCriteria) {
        super(referenceClass, selectionCriteria);
    }

    /**
     * INTERNAL:
     * Return if objects should be remove from the persistence context only (no database).
     * This is used if delete was already cascaded by the database.
     */
    public boolean isInMemoryOnly() {
        return isInMemoryOnly;
    }

    /**
     * INTERNAL:
     * Set if objects should be remove from the persistence context only (no database).
     * This is used if delete was already cascaded by the database.
     */
    public void setIsInMemoryOnly(boolean isInMemoryOnly) {
        this.isInMemoryOnly = isInMemoryOnly;
    }

    /**
     * PUBLIC:
     * Return if this is a delete all query.
     */
    public boolean isDeleteAllQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * This method has to be broken.  If commit manager is not active either
     * an exception should be thrown (ObjectLevelModify case), or a transaction
     * should be started early and execute on parent if remote (dataModify case).
     * A modify query is NEVER executed on the parent, unless remote session.
     */
    public Object executeInUnitOfWork(UnitOfWorkImpl unitOfWork, AbstractRecord translationRow) throws DatabaseException {
        if (this.objects != null) {
            if (unitOfWork.isAfterWriteChangesButBeforeCommit()) {
                throw ValidationException.illegalOperationForUnitOfWorkLifecycle(unitOfWork.getLifecycle(), "executeQuery(DeleteAllQuery)");
            }
    
            // This must be broken, see comment.
            if (!unitOfWork.getCommitManager().isActive()) {
                return unitOfWork.getParent().executeQuery(this, translationRow);
            }
            result = (Integer)super.execute(unitOfWork, translationRow);
            return result;
        } else {
            return super.executeInUnitOfWork(unitOfWork, translationRow);
        }
    }

    /**
     * INTERNAL:
     * Perform the work to delete a collection of objects.
     * This skips the optimistic lock check and should not called for objects using locking.
     * @exception  DatabaseException - an error has occurred on the database.
     * @return Integer the number of objects (rows) deleted.
     */
    public Object executeDatabaseQuery() throws DatabaseException {
        // CR# 4286
        if (this.objects != null) {

            if (isExpressionQuery() && getSelectionCriteria() == null) {
                // DeleteAllQuery has objects so it *must* have selectionCriteria, too
                throw QueryException.deleteAllQuerySpecifiesObjectsButNotSelectionCriteria(getDescriptor(), this, this.objects.toString());
            }
            
            // Optimistic lock check not required because objects are deleted individually in that case.
            try {
                this.session.beginTransaction();
    
                // Need to run pre-delete selector if available.
                // PERF: Avoid events if no listeners.
                if (this.descriptor.getEventManager().hasAnyEventListeners()) {
                    for (Object object : this.objects) {
                        DescriptorEvent event = new DescriptorEvent(object);
                        event.setEventCode(DescriptorEventManager.PreDeleteEvent);
                        event.setSession(this.session);
                        event.setQuery(this);
                        this.descriptor.getEventManager().executeEvent(event);
                    }
                }

                if (this.isInMemoryOnly) {
                    result = Integer.valueOf(0);
                } else {
                    result = this.queryMechanism.deleteAll();
                }
    
                // Need to run post-delete selector if available.
                // PERF: Avoid events if no listeners.
                if (this.descriptor.getEventManager().hasAnyEventListeners()) {
                    for (Object object : this.objects) {
                        DescriptorEvent event = new DescriptorEvent(object);
                        event.setEventCode(DescriptorEventManager.PostDeleteEvent);
                        event.setSession(this.session);
                        event.setQuery(this);
                        this.descriptor.getEventManager().executeEvent(event);
                    }
                }
    
                if (shouldMaintainCache()) {
                    // remove from the cache.
                    for (Object deleted : this.objects) {
                        if (this.session.isUnitOfWork()) {
                            //BUG #2612169: Unwrap is needed
                            deleted = this.descriptor.getObjectBuilder().unwrapObject(deleted, getSession());
                            ((UnitOfWorkImpl)this.session).addObjectDeletedDuringCommit(deleted, this.descriptor);
                        } else {
                            this.session.getIdentityMapAccessor().removeFromIdentityMap(deleted);
                        }
                    }
                }
    
                this.session.commitTransaction();
    
            } catch (RuntimeException exception) {
                this.session.rollbackTransaction();
                throw exception;
            }
        } else {
            if (this.isInMemoryOnly) {
                result = Integer.valueOf(0);
            } else {
                result = this.queryMechanism.deleteAll();// fire the SQL to the database
            }
            mergeChangesIntoSharedCache();
        }
        
        return result;
    }

    /**
     * INTERNAL:
     * Delete all queries are executed specially to avoid cloning and ensure preparing.
     */
    public void executeDeleteAll(AbstractSession session, AbstractRecord translationRow, Vector objects) throws DatabaseException {
        this.checkPrepare(session, translationRow);
        DeleteAllQuery queryToExecute = (DeleteAllQuery)clone();

        // Then prepare for the single execution.
        queryToExecute.setTranslationRow(translationRow);
        queryToExecute.setSession(session);
        queryToExecute.setObjects(objects);
        queryToExecute.prepareForExecution();
        queryToExecute.executeDatabaseQuery();
    }

    /**
     * INTERNAL:
     * Returns the specific default redirector for this query type.  There are numerous default query redirectors.
     * See ClassDescriptor for their types.
     */
    protected QueryRedirector getDefaultRedirector(){
        // 364001 - Do not return redirector if objects list is null.
        if (objects == null) {
            return null;
        }
        
        return descriptor.getDefaultDeleteObjectQueryRedirector();
    }


    /**
     * PUBLIC:
     * Return the objects that are to be deleted
     */
    public List<Object> getObjects() {
        return objects;
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() throws QueryException {
        super.prepare();

        if (getReferenceClass() == null) {
            throw QueryException.referenceClassMissing(this);
        }

        if (getDescriptor() == null) {
            ClassDescriptor referenceDescriptor = getSession().getDescriptor(getReferenceClass());
            if (referenceDescriptor == null) {
                throw QueryException.descriptorIsMissing(getReferenceClass(), this);
            }
            setDescriptor(referenceDescriptor);
        }

        if (getDescriptor().isAggregateDescriptor()) {
            throw QueryException.aggregateObjectCannotBeDeletedOrWritten(getDescriptor(), this);
        }

        getQueryMechanism().prepareDeleteAll();
    }

    /**
     * PUBLIC (REQUIRED):
     * Set the objects to be deleted.
     * Also REQUIRED is a selection criteria or SQL string that performs the deletion of the objects.
     * This does not generate the SQL call from the deleted objects.
     * <p>
     * List objects used as an indicator of one of two possible
     * ways the query may behave:
     * <p> objects != null - the "old" functionality used by OneToMany mapping
     *     objects deleted from the cache, either selection expression or custom sql
     *     should be provided for deletion from db;
     * <p> objects == null - the "new" functionality (on par with UpdateAllQuery)
     *     the cache is either left alone or in-memory query finds the cached objects to be deleted,
     *     and these objects are invalidated in cache.
     * <p>
     * Note that empty objects is still objects != case.
     *     Signal that no cache altering is required.
     *     Used by AggregationCollectionMapping and OneToManyMapping in case they use indirection
     *     and the ValueHolder has not been instantiated.
     */
    public void setObjects(List<Object> objectCollection) {
        this.objects = objectCollection;
    }
}
