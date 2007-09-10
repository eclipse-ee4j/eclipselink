/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CommitManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
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
        DescriptorQueryManager queryManager = getDescriptor().getQueryManager();
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
     * Perform the work to delete an object.
     * @return object - the object being deleted.
     */
    public Object executeDatabaseQuery() throws DatabaseException, OptimisticLockException {
        AbstractSession session = getSession();
        CommitManager commitManager = session.getCommitManager();
        Object object = getObject();
        boolean isUnitOfWork = session.isUnitOfWork();
        try {
            // Check if the object has already been commited, then no work is required
            if (commitManager.isCommitCompleted(object) || commitManager.isCommitInPostModify(object) || commitManager.isCommitInPreModify(object)) {
                return object;
            }
            commitManager.markPreModifyCommitInProgress(getObject());
            if (!isUnitOfWork) {
                session.beginTransaction();
            }
            ClassDescriptor descriptor = getDescriptor();
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
            int rowCount = getQueryMechanism().deleteObject().intValue();

            if (rowCount < 1) {
                session.getEventManager().noRowsModified(this, object);
            }

            if (descriptor.usesOptimisticLocking()) {
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
                    session.getIdentityMapAccessorInstance().removeFromIdentityMap(getPrimaryKey(), descriptor.getJavaClass(), descriptor);
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
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() {
        super.prepare();

        getQueryMechanism().prepareDeleteObject();
        
        //Bug6001602, set FullRowRequired to true only when query is call query or is user defined
        //and the call has more than one parameter.
        if (isCallQuery() || isUserDefined()) {
            if(getCall()!=null && getCall().getParameters()!=null && getCall().getParameters().size()>1){
                isFullRowRequired=true;            
           }
        }
    }

    /**
     * INTERNAL:
     * Set the properties needed to be cascaded into the custom query.
     */
    protected void prepareCustomQuery(DatabaseQuery customQuery) {
        DeleteObjectQuery customDeleteQuery = (DeleteObjectQuery)customQuery;
        // Bug#3947714  In case getObject() is proxy.
        if (customQuery.getSession().getProject().hasProxyIndirection()) {
            setObject(ProxyIndirectionPolicy.getValueFromProxy(getObject()));
        }
        customDeleteQuery.setObject(getObject());
        customDeleteQuery.setObjectChangeSet(getObjectChangeSet());
        customDeleteQuery.setCascadePolicy(getCascadePolicy());
        customDeleteQuery.setShouldMaintainCache(shouldMaintainCache());
        if (customDeleteQuery.isFullRowRequired()) {
            customDeleteQuery.setTranslationRow(customDeleteQuery.getDescriptor().getObjectBuilder().buildRow(getObject(), customDeleteQuery.getSession()));
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session. In particular,
     * verify that the object is not null and contains a valid primary key.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        // Set the tranlation row
        if ((getTranslationRow() == null) || (getTranslationRow().isEmpty())) {
            setTranslationRow(getDescriptor().getObjectBuilder().buildRowForTranslation(getObject(), getSession()));
        }

        // Add the write lock field if required		
        if (getDescriptor().usesOptimisticLocking()) {
            getDescriptor().getOptimisticLockingPolicy().addLockValuesToTranslationRow(this);
        }
    }
}