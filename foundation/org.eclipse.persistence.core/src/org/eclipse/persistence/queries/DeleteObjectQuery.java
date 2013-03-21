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

import java.util.Set;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CommitManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.tools.profiler.QueryMonitor;

/**
 * <p><b>Purpose</b>: Used for deleting objects.
 * <p>
 * <p><b>Responsibilities</b>:
 * Extract primary key from object and delete it.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class DeleteObjectQuery extends ObjectLevelModifyQuery {
    /** PERF: By default only the translation row is used for deletes, the full row can be requested for custom deletes. */
    protected boolean isFullRowRequired = false;
    
    /** Indicates whether the query should use optimistic locking. */
    protected boolean usesOptimisticLocking;
    
    public DeleteObjectQuery() {
        super();
    }

    public DeleteObjectQuery(Object objectToDelete) {
        this();
        setObject(objectToDelete);
    }

    public DeleteObjectQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * ADVANCED:
     * Return if the full row is required by the delete query.
     * This can be set on custom delete queries if more than the objects primary key and version is required.
     */
    public boolean isFullRowRequired() 
    {
        return isFullRowRequired;
    }

    /**
     * ADVANCED:
     * Set if the full row is required by the delete query.
     * This can be set on custom delete queries if more than the objects primary key and version is required.
     */
    public void setIsFullRowRequired(boolean isFullRowRequired) 
    {
        this.isFullRowRequired = isFullRowRequired;
    }
    
    /**
     * INTERNAL:
     * Check to see if a custom query should be used for this query.
     * This is done before the query is copied and prepared/executed.
     * null means there is none.
     */
    protected DatabaseQuery checkForCustomQuery(AbstractSession session, AbstractRecord translationRow) {
        checkDescriptor(session);

        // check if user defined a custom query
        DescriptorQueryManager queryManager = this.descriptor.getQueryManager();
        if ((!isCallQuery())// this is not a hand-coded (custom SQL, SDK etc.) call
                 && (!isUserDefined())// and this is not a user-defined query (in the query manager)
                 && queryManager.hasDeleteQuery()) {// and there is a user-defined query (in the query manager)
            return queryManager.getDeleteQuery();
        }

        return null;
    }

    /**
     * INTERNAL:
     * Code was moved from UnitOfWork.internalExecuteQuery
     *
     * @param unitOfWork
     * @param translationRow
     * @return
     * @throws org.eclipse.persistence.exceptions.DatabaseException
     * @throws org.eclipse.persistence.exceptions.OptimisticLockException
     */
    protected Object executeInUnitOfWorkObjectLevelModifyQuery(UnitOfWorkImpl unitOfWork, AbstractRecord translationRow) throws DatabaseException, OptimisticLockException {
        Object result = unitOfWork.processDeleteObjectQuery(this);
        if (result != null) {
            // if the above method returned something then the unit of work
            //was not writing so the object has been stored to delete later
            //so return the object.  See the above method for the cases
            //where this object will be returned.
            return result;
        }
        return super.executeInUnitOfWorkObjectLevelModifyQuery(unitOfWork, translationRow);
    }

    /**
     * INTERNAL:
     * Returns the specific default redirector for this query type.  There are numerous default query redirectors.
     * See ClassDescriptor for their types.
     */
    protected QueryRedirector getDefaultRedirector(){
        return descriptor.getDefaultDeleteObjectQueryRedirector();
    }

    /**
     * INTERNAL:
     * Perform the work to delete an object.
     * @return object - the object being deleted.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException {
        AbstractSession session = getSession();
        CommitManager commitManager = session.getCommitManager();
        Object object = getObject();
        boolean isUnitOfWork = session.isUnitOfWork();
        try {
            // Check if the object has already been deleted, then no work is required.
            if (commitManager.isCommitCompletedInPostOrIgnore(object)) {
                return object;
            }
            ClassDescriptor descriptor = this.descriptor;
            // Check whether the object is already being deleted,
            // if it is, then there is a cycle, and the foreign keys must be nulled.
            if (commitManager.isCommitInPreModify(object)) {
                if (!commitManager.isShallowCommitted(object)) {
                    getQueryMechanism().updateForeignKeyFieldBeforeDelete();
                    if ((descriptor.getHistoryPolicy() != null) && descriptor.getHistoryPolicy().shouldHandleWrites()) {
                        descriptor.getHistoryPolicy().postUpdate(this);
                    }
                    commitManager.markShallowCommit(object);
                }
                return object;
            }
            commitManager.markPreModifyCommitInProgress(getObject());
            if (!isUnitOfWork) {
                session.beginTransaction();
            }
            DescriptorEventManager eventManager = descriptor.getEventManager();
            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                // Need to run pre-delete selector if available
                eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PreDeleteEvent, this));
            }

            // Verify if deep shallow modify is turned on
            if (shouldCascadeParts()) {
                descriptor.getQueryManager().preDelete(this);
            }

            // Check for deletion dependencies.
            if (isUnitOfWork) {
                Set dependencies = ((UnitOfWorkImpl)session).getDeletionDependencies(object);
                if (dependencies != null) {
                    for (Object dependency : dependencies) {
                        if (!commitManager.isCommitCompletedInPostOrIgnore(dependency)) {
                            ClassDescriptor dependencyDescriptor = session.getDescriptor(dependency);
                            // PERF: Get the descriptor query, to avoid extra query creation.
                            DeleteObjectQuery deleteQuery = dependencyDescriptor.getQueryManager().getDeleteQuery();
                            if (deleteQuery == null) {
                                deleteQuery = new DeleteObjectQuery();
                                deleteQuery.setDescriptor(dependencyDescriptor);
                            } else {
                                // Ensure original query has been prepared.
                                deleteQuery.checkPrepare(session, deleteQuery.getTranslationRow());
                                deleteQuery = (DeleteObjectQuery)deleteQuery.clone();
                            }
                            deleteQuery.setIsExecutionClone(true);
                            deleteQuery.setObject(dependency);
                            session.executeQuery(deleteQuery);
                        }
                    }
                }
            }
            
            // CR#2660080 missing aboutToDelete event.		
            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(DescriptorEventManager.AboutToDeleteEvent, this);
                event.setRecord(getModifyRow());
                eventManager.executeEvent(event);
            }
            
            if (QueryMonitor.shouldMonitor()) {
                QueryMonitor.incrementDelete(this);
            }
            int rowCount = 0;
            // If the object was/will be deleted from a cascade delete constraint, ignore it.
            if (isUnitOfWork && ((UnitOfWorkImpl)session).hasCascadeDeleteObjects()
                    && ((UnitOfWorkImpl)session).getCascadeDeleteObjects().contains(object)) {
                // Cascade delete does not check optimistic lock, assume ok.
                rowCount = 1;
            } else {
                rowCount = getQueryMechanism().deleteObject().intValue();
            }

            if (rowCount < 1) {
                if (session.hasEventManager()) {
                    session.getEventManager().noRowsModified(this, object);
                }
            }

            if (this.usesOptimisticLocking) {
                descriptor.getOptimisticLockingPolicy().validateDelete(rowCount, object, this);
            }

            commitManager.markPostModifyCommitInProgress(getObject());
            // Verify if deep shallow modify is turned on
            if (shouldCascadeParts()) {
                descriptor.getQueryManager().postDelete(this);
            }

            if ((descriptor.getHistoryPolicy() != null) && descriptor.getHistoryPolicy().shouldHandleWrites()) {
                descriptor.getHistoryPolicy().postDelete(this);
            }

            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                // Need to run post-delete selector if available
                eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PostDeleteEvent, this));
            }

            if (!isUnitOfWork) {
                session.commitTransaction();
            }
            commitManager.markCommitCompleted(object);

            // CR3510313, avoid removing aggregate collections from cache (maintain cache is false).
            if (shouldMaintainCache()) {
                if (isUnitOfWork) {
                    ((UnitOfWorkImpl)session).addObjectDeletedDuringCommit(object, descriptor);
                } else {
                    session.getIdentityMapAccessorInstance().removeFromIdentityMap(getPrimaryKey(), descriptor.getJavaClass(), descriptor, object);
                }
            }
            return object;

        } catch (RuntimeException exception) {
            if (!isUnitOfWork) {
                session.rollbackTransaction();
            }
            commitManager.markCommitCompleted(object);
            throw exception;
        }
    }

    /**
     * PUBLIC:
     * Return if this is a delete object query.
     */
    public boolean isDeleteObjectQuery() {
        return true;
    }

    /**
     * PUBLIC: (REQUIRED)
     * Set the object required for modification.
     */
    @Override
    public void setObject(Object object) {
        if (isPrepared()) {
            if (this.usesOptimisticLocking != shouldUseOptimisticLocking(object)) {
                setIsPrepared(false);
            }
        }
        super.setObject(object);
    }
    
    /**
     * INTERNAL:
     * Determines whether the query should use optimistic locking with the passed object.
     */
    protected boolean shouldUseOptimisticLocking(Object object) {
        if (this.descriptor.usesOptimisticLocking()) {
            if (object != null) {
                FetchGroupManager fetchGroupManager = this.descriptor.getFetchGroupManager();
                if (fetchGroupManager != null) {
                    FetchGroup fetchGroup = fetchGroupManager.getObjectFetchGroup(object);
                    if (fetchGroup == fetchGroupManager.getIdEntityFetchGroup()) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * INTERNAL:
     * Indicating whether the query should use optimistic locking.
     */
    public boolean usesOptimisticLocking() {
        return this.usesOptimisticLocking; 
    }
    
    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() {
        super.prepare();
        this.usesOptimisticLocking = shouldUseOptimisticLocking(this.object);
        if (this.name == null) {
            this.name = "delete" + this.descriptor.getJavaClass().getSimpleName();
        }
        getQueryMechanism().prepareDeleteObject();
    }

    /**
     * INTERNAL:
     * Set the properties needed to be cascaded into the custom query.
     * A custom query is set by the user, or used by default to allow caching of the prepared query.
     * In a unit of work the custom query is used directly, so this step is bypassed.
     */
    protected void prepareCustomQuery(DatabaseQuery customQuery) {
        DeleteObjectQuery customDeleteQuery = (DeleteObjectQuery)customQuery;
        customDeleteQuery.setObject(getObject());
        customDeleteQuery.setObjectChangeSet(getObjectChangeSet());
        customDeleteQuery.setCascadePolicy(getCascadePolicy());
        customDeleteQuery.setShouldMaintainCache(shouldMaintainCache());
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session. In particular,
     * verify that the object is not null and contains a valid primary key.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        // Set the translation row
        if ((this.translationRow == null) || this.translationRow.isEmpty()) {
            if (this.isFullRowRequired) {
                this.translationRow = this.descriptor.getObjectBuilder().buildRow(this.object, this.session, WriteType.UNDEFINED);
            } else {
                this.translationRow = this.descriptor.getObjectBuilder().buildRowForTranslation(this.object, this.session);
            }
        }

        // Add the write lock field if required		
        if (this.usesOptimisticLocking) {
            this.descriptor.getOptimisticLockingPolicy().addLockValuesToTranslationRow(this);
        }
    }
}
