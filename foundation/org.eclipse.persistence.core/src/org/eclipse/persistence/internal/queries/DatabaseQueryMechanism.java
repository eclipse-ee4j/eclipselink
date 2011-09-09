/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.queries;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.tools.profiler.QueryMonitor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all database query mechanism objects.
 * DatabaseQueryMechanism is actually a helper class and currently is required
 * for all types of queries.  Most of the work performed by the query framework is
 * performed in the query mechanism.  The query mechanism contains the internal
 * knowledge necessary to perform the specific database operation.
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide a common protocol for query mechanism objects.
 * Provides all of the database specific work for the assigned query.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public abstract class DatabaseQueryMechanism implements Cloneable, Serializable {

    /** The database query that uses this mechanism. */
    protected DatabaseQuery query;

    /**
     * Initialize the state of the query.
     */
    public DatabaseQueryMechanism() {
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public DatabaseQueryMechanism(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * Add the initial write lock value to the row for insert.
     */
    protected void addWriteLockFieldForInsert() {
        if (getDescriptor().usesOptimisticLocking()) {
            getDescriptor().getOptimisticLockingPolicy().setupWriteFieldsForInsert(getWriteObjectQuery());
        }
    }

    /**
     * Internal:
     * In the case of EJBQL, an expression needs to be generated. Build the required expression.
     */
    public void buildSelectionCriteria(AbstractSession session) {
        // Default is do nothing
    }

    /**
     * Perform a cache lookup for the query. If the translation row contains 
     * all the parameters (which are part of the primary key) from the prepared 
     * call, then a cache check will be performed.
     * 
     * If the object is found in the cache, return it; otherwise return null.
     */
    public Object checkCacheForObject(AbstractRecord translationRow, AbstractSession session) {
        // Null check added for CR#4295 - TW
        if ((translationRow == null) || (translationRow.isEmpty())) {
            return null;
        }
        
        // Bug 5529564
        // The query wasn't prepared most likely because arguments were 
        // provided. Use them for the cache lookup.
        List queryFields;
        if (query.getCall() == null) {
            // TODO: This is a bug, arguments is a list of Strings, not fields.
            // Also if the call was null, it would be an expression, if it was not
            // prepared the parameters would be empty.
            queryFields = query.getArguments();
        } else {
            // The query was prepared. Use the parameters of the call to look
            // up the object in cache.
            queryFields = query.getCall().getParameters();
        }
        
        ClassDescriptor descriptor = getDescriptor();
        List<DatabaseField> primaryKeyFields = descriptor.getPrimaryKeyFields();
        
        // Check that the query is by primary key.
        for (DatabaseField primaryKeyField : primaryKeyFields) {
            if (!queryFields.contains(primaryKeyField)) {
                return null;
            }
        }
        Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromRow(translationRow, session);
        if (primaryKey == null) {
            return null;
        }
        
        if (query.isObjectBuildingQuery() && ((ObjectBuildingQuery)query).requiresDeferredLocks() || descriptor.shouldAcquireCascadedLocks()) {
            return session.getIdentityMapAccessorInstance().getFromIdentityMapWithDeferredLock(primaryKey, getReadObjectQuery().getReferenceClass(), false, descriptor);
        } else {
            return session.getIdentityMapAccessorInstance().getFromLocalIdentityMap(primaryKey, getReadObjectQuery().getReferenceClass(), false, descriptor);
        }        
    }

    /**
     * Clone the mechanism
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Clone the mechanism for the specified query clone.
     */
    public DatabaseQueryMechanism clone(DatabaseQuery queryClone) {
        DatabaseQueryMechanism clone = (DatabaseQueryMechanism)clone();
        clone.setQuery(queryClone);
        return clone;
    }

    /**
     * Read all rows from the database using a cursored stream.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public abstract DatabaseCall cursorSelectAllRows() throws DatabaseException;

    /**
     * Delete a collection of objects
     * This should be overridden by subclasses.
     * @exception  DatabaseException - an error has occurred on the database
     */
    public boolean isJPQLCallQueryMechanism() {
        return false;
    }

    public abstract Integer deleteAll() throws DatabaseException;

    /**
     * Delete an object
     * This should be overridden by subclasses.
     * @exception DatabaseException
     * @return the row count.
     */
    public abstract Integer deleteObject() throws DatabaseException;

    /**
     * Execute a non selecting SQL call
     * This should be overridden by subclasses.
     * @exception DatabaseException
     * @return the row count.
     */
    public abstract Integer executeNoSelect() throws DatabaseException;

    /**
     * Execute a select SQL call and return the rows.
     * This should be overriden by subclasses.
     * @exception DatabaseException
     */
    public abstract Vector executeSelect() throws DatabaseException;

    /**
     * Check whether the object already exists on the database; then
     * perform an insert or update, as appropriate.
     * This write is used for non-unit of work (old-commit) operations.
     * Return the object being written.
     */
    public Object executeWrite() throws DatabaseException, OptimisticLockException {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        Object object = writeQuery.getObject();
        CommitManager commitManager = getSession().getCommitManager();

        // if the object has already been committed, no work is required
        if (commitManager.isCommitCompletedOrInPost(object)) {
            return object;
        }

        // check whether the object is already being committed -
        // if it is and it is new, then a shallow insert must be done
        if (commitManager.isCommitInPreModify(object)) {
            shallowInsertObjectForWrite(object, writeQuery, commitManager);
            return object;
        }

        try {
            getSession().beginTransaction();

            if (writeQuery.getObjectChangeSet() == null) {
                // PERF: Avoid events if no listeners.
                if (getDescriptor().getEventManager().hasAnyEventListeners()) {
                    // only throw the events if there is no changeset otherwise the event will be thrown twice
                    // once by the calculate changes code and here
                    getDescriptor().getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PreWriteEvent, writeQuery));
                }
            }
            writeQuery.executeCommit();

            // PERF: Avoid events if no listeners.
            if (getDescriptor().getEventManager().hasAnyEventListeners()) {
                getDescriptor().getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PostWriteEvent, writeQuery));
            }

            getSession().commitTransaction();

            // notify the commit manager of the completion to the commit
            commitManager.markCommitCompleted(object);

            return object;

        } catch (RuntimeException exception) {
            getSession().rollbackTransaction();
            commitManager.markCommitCompleted(object);
            throw exception;
        }
    }

    /**
     * Execute the call that was deferred to the commit manager.
     * This is used to allow multiple table batching and deadlock avoidance.
     */
    public void executeDeferredCall(DatasourceCall call) {
        // Do nothing by default.
    }
    
    /**
     * Check whether the object already exists on the database; then
     * perform an insert or update, as appropriate.
     * This method was moved here, from WriteObjectQuery.execute(),
     * so we can hide the source.
     * Return the object being written.
     */
    public Object executeWriteWithChangeSet() throws DatabaseException, OptimisticLockException {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        ObjectChangeSet objectChangeSet = writeQuery.getObjectChangeSet();
        ClassDescriptor descriptor = getDescriptor();
        AbstractSession session = getSession();
        CommitManager commitManager = session.getCommitManager();
        Object object = writeQuery.getObject();
        // If there are no changes then there is no work required
        // Check for forcedUpdate Version and Optimistic read lock (hasForcedChanges() set in ObjectChangePolicy)
        if (!objectChangeSet.hasChanges() && !objectChangeSet.hasForcedChanges()) {
            commitManager.markCommitCompleted(object);
            return object;
        }
        // If the object has already been committed, no work is required
        // need to check for the object to ensure insert wasn't completed already.
        if (commitManager.isCommitCompletedOrInPost(object)) {
            return object;
        }
        try {
            writeQuery.executeCommitWithChangeSet();

            // PERF: Avoid events if no listeners.
            if (descriptor.getEventManager().hasAnyEventListeners()) {
                descriptor.getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PostWriteEvent, writeQuery));
            }

            // Notify the commit manager of the completion to the commit.
            commitManager.markCommitCompleted(object);

            return object;

        } catch (RuntimeException exception) {
            commitManager.markCommitCompleted(object);
            throw exception;
        }
    }

    /**
     * Convenience method
     */
    protected ClassDescriptor getDescriptor() {
        return this.query.getDescriptor();
    }

    /**
     * Convenience method
     */
    public AbstractRecord getModifyRow() {
        if (this.query.isModifyQuery()) {
            return ((ModifyQuery)this.query).getModifyRow();
        } else {
            return null;
        }
    }

    /**
     * Return the query that uses the mechanism.
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * Convenience method
     */
    protected ReadObjectQuery getReadObjectQuery() {
        return (ReadObjectQuery)this.query;
    }

    /**
     * Return the selection criteria for the mechanism.
     * By default this is null. This method exists because both statement and expression
     * mechanisms use an expression and some code in the mappings depends on returning this.
     */
    public Expression getSelectionCriteria() {
        return null;
    }

    /**
     * Convenience method
     */
    protected AbstractSession getSession() {
        return this.query.getSession();
    }

    /**
     * Convenience method
     */
    protected AbstractRecord getTranslationRow() {
        return this.query.getTranslationRow();
    }

    /**
     * Convenience method
     */
    protected WriteObjectQuery getWriteObjectQuery() {
        return (WriteObjectQuery)this.query;
    }

    /**
     * Insert an object.
     */
    public abstract void insertObject() throws DatabaseException;

    /**
     *  Insert an object and provide the opportunity to reprepare prior to the insert.
     *  This will be overridden
     *  CR#3237
     */
    public void insertObject(boolean reprepare) {
        insertObject();
    }

    /**
     * Insert an object in the database.
     * This is used for both uow and non-uow (old-commit and change-set) operations.
     */
    public void insertObjectForWrite() {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        ClassDescriptor descriptor = getDescriptor();
        DescriptorQueryManager queryManager = descriptor.getQueryManager();
        // check for user-defined query
        if ((!writeQuery.isUserDefined())// this is not a user-defined query
                 && queryManager.hasInsertQuery()// there is a user-defined query
                 && isExpressionQueryMechanism()) {// this is not a hand-coded call (custom SQL etc.)
            performUserDefinedInsert();
            return;
        }
        Object object = writeQuery.getObject();
        AbstractSession session = writeQuery.getSession();
        ObjectChangeSet changeSet = writeQuery.getObjectChangeSet();
        CommitManager commitManager = session.getCommitManager();
        DescriptorEventManager eventManager = descriptor.getEventManager();

        // This must be done after the custom query check, otherwise it will be done twice.
        commitManager.markPreModifyCommitInProgress(object);

        if (changeSet == null) {
            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                // only throw the events if there is no changeset otherwise the event will be thrown twice
                // once by the calculate changes code and here
                eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PreInsertEvent, writeQuery));
            }
        }

        // check whether deep shallow modify is turned on
        if (writeQuery.shouldCascadeParts()) {
            queryManager.preInsert(writeQuery);
        }

        // In a unit of work/writeObjects the preInsert may have caused a shallow insert of this object,
        // in this case this second write must do an update.
        if (commitManager.isShallowCommitted(object)) {
            updateForeignKeyFieldAfterInsert();
        } else {
            AbstractRecord modifyRow = writeQuery.getModifyRow();
            if (modifyRow == null) {// Maybe have been passed in as in aggregate collection.
                if (writeQuery.shouldCascadeParts()) {
                    writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRow(object, session, WriteType.INSERT));
                } else {
                    writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRowForShallowInsert(object, session));
                }
            } else {
                if (writeQuery.shouldCascadeParts()) {
                    writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRow(modifyRow, object, session, WriteType.INSERT));
                } else {
                    writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRowForShallowInsert(modifyRow, object, session));
                }
            }
            modifyRow = getModifyRow();
            // the modify row and the translation row are the same for insert
            writeQuery.setTranslationRow(modifyRow);
            if (!descriptor.isAggregateCollectionDescriptor()) {// Should/cannot be recomputed in aggregate collection.
                writeQuery.setPrimaryKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, session));
            }
            addWriteLockFieldForInsert();

            // CR#3237
            // Store the size of the modify row so we can determine if the user has added to the row in the insert.
            int modifyRowSize = modifyRow.size();

            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(DescriptorEventManager.AboutToInsertEvent, writeQuery);
                event.setRecord(modifyRow);
                eventManager.executeEvent(event);
            }

            if (QueryMonitor.shouldMonitor()) {
                QueryMonitor.incrementInsert(writeQuery);
            }
            // CR#3237
            // Call insert with a boolean that tells it to reprepare if the user has altered the modify row.
            insertObject(modifyRowSize != modifyRow.size());

            // register the object before post insert to resolve possible cycles
            registerObjectInIdentityMap(object, descriptor, session);
        }

        commitManager.markPostModifyCommitInProgress(object);
        // Verify if deep shallow modify is turned on.
        if (writeQuery.shouldCascadeParts()) {
            queryManager.postInsert(writeQuery);
        }
        if ((descriptor.getHistoryPolicy() != null) && descriptor.getHistoryPolicy().shouldHandleWrites()) {
            descriptor.getHistoryPolicy().postInsert(writeQuery);
        }

        // PERF: Avoid events if no listeners.
        if (eventManager.hasAnyEventListeners()) {
            eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PostInsertEvent, writeQuery));
        }
    }

    /**
     * Return true if this is a call query mechanism
     */
    public boolean isCallQueryMechanism() {
        return false;
    }

    /**
     * Return true if this is an expression query mechanism
     */
    public boolean isExpressionQueryMechanism() {
        return false;
    }

    /**
     * Return true if this is a query by example mechanism
     */
    public boolean isQueryByExampleMechanism() {
        return false;
    }

    /**
     * Return true if this is a statement query mechanism
     */
    public boolean isStatementQueryMechanism() {
        return false;
    }

    /**
     * Insert the object using the user defined query.
     * This ensures that the query is cloned and prepared correctly.
     */
    protected void performUserDefinedInsert() {
        performUserDefinedWrite(getDescriptor().getQueryManager().getInsertQuery());
    }

    /**
     * Update the object using the user defined query.
     * This ensures that the query is cloned and prepared correctly.
     */
    protected void performUserDefinedUpdate() {
        performUserDefinedWrite(getDescriptor().getQueryManager().getUpdateQuery());
    }

    /**
     * Write the object using the specified user-defined query.
     * This ensures that the query is cloned and prepared correctly.
     */
    protected void performUserDefinedWrite(WriteObjectQuery userDefinedWriteQuery) {
        WriteObjectQuery query = getWriteObjectQuery();
        userDefinedWriteQuery.checkPrepare(query.getSession(), query.getTranslationRow());

        WriteObjectQuery writeQuery = (WriteObjectQuery)userDefinedWriteQuery.clone();
        writeQuery.setIsExecutionClone(true);
        writeQuery.setObject(query.getObject());
        writeQuery.setObjectChangeSet(query.getObjectChangeSet());
        writeQuery.setCascadePolicy(query.getCascadePolicy());
        writeQuery.setShouldMaintainCache(query.shouldMaintainCache());
        writeQuery.setTranslationRow(query.getTranslationRow());
        writeQuery.setModifyRow(query.getModifyRow());
        writeQuery.setPrimaryKey(query.getPrimaryKey());
        writeQuery.setSession(query.getSession());

        // If there is a changeset, the change set method must be used.
        if (writeQuery.getObjectChangeSet() != null) {
            writeQuery.executeCommitWithChangeSet();
        } else {
            writeQuery.executeCommit();
        }
    }

    /**
     * This is different from 'prepareForExecution()'
     * in that this is called on the original query,
     * and the other is called on the clone of the query.
     * This query is copied for concurrency so this prepare can only setup things that
     * will apply to any future execution of this query.
     */
    public void prepare() throws QueryException {
        // the default is to do nothing
    }

    /**
     * Pre-pare for a cursored execute.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareCursorSelectAllRows() throws QueryException;

    /**
     * Prepare for a delete all.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareDeleteAll() throws QueryException;

    /**
     * Prepare for a delete.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareDeleteObject() throws QueryException;

    /**
     * Pre-pare for a select execute.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareDoesExist(DatabaseField field) throws QueryException;

    /**
     * Prepare for a raw (non-object), non-selecting call.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareExecuteNoSelect() throws QueryException;

    /**
     * Prepare for a raw (non-object) select call.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareExecuteSelect() throws QueryException;

    /**
     * Prepare for an insert.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareInsertObject() throws QueryException;

    /**
     * Pre-pare for a select execute.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareReportQuerySelectAllRows() throws QueryException;

    /**
     * Pre-pare a report query for a sub-select.
     */
    public abstract void prepareReportQuerySubSelect() throws QueryException;

    /**
     * Prepare for a select returning (possibly) multiple rows.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareSelectAllRows() throws QueryException;

    /**
     * Prepare for a select returning a single row.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareSelectOneRow() throws QueryException;

    /**
     * Prepare for an update.
     * This is sent to the original query before cloning.
     */
    public abstract void prepareUpdateObject() throws QueryException;

    /**
       * Prepare for an update all.
       * This is sent to the original query before cloning.
       */
    public abstract void prepareUpdateAll() throws QueryException;

    /**
     * Store the query object in the identity map.
     */
    protected void registerObjectInIdentityMap(Object object, ClassDescriptor descriptor, AbstractSession session) {
        WriteObjectQuery query = getWriteObjectQuery();
        if (query.shouldMaintainCache()) {
            if (descriptor.usesOptimisticLocking()) {
                Object optimisticLockValue = descriptor.getOptimisticLockingPolicy().getValueToPutInCache(query.getModifyRow(), session);
                session.getIdentityMapAccessorInstance().putInIdentityMap(object, query.getPrimaryKey(), optimisticLockValue, System.currentTimeMillis(), descriptor);
            } else {
                session.getIdentityMapAccessorInstance().putInIdentityMap(object, query.getPrimaryKey(), null, System.currentTimeMillis(), descriptor);
            }
        }
    }

    /**
     * INTERNAL:
     * Read all rows from the database.
     */
    public abstract Vector selectAllReportQueryRows() throws DatabaseException;

    /**
     * Read and return rows from the database.
     */
    public abstract Vector selectAllRows() throws DatabaseException;

    /**
     * Read and return a row from the database.
     */
    public abstract AbstractRecord selectOneRow() throws DatabaseException;

    /**
     * Read and return a row from the database for an existence check.
     */
    public abstract AbstractRecord selectRowForDoesExist(DatabaseField field) throws DatabaseException;
    
    /**
     * Set the query that uses this mechanism.
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * Shallow insert the specified object, if necessary.
     */
    protected void shallowInsertObjectForWrite(Object object, WriteObjectQuery writeQuery, CommitManager commitManager) throws DatabaseException, OptimisticLockException {
        boolean doesExist;

        if (getSession().isUnitOfWork()) {
            UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession();
            doesExist = !uow.isCloneNewObject(object);
            if (doesExist) {
                doesExist = uow.isObjectRegistered(object);
            }
        } else {
            // clone and initialize the does exist query
            DoesExistQuery existQuery = (DoesExistQuery)getDescriptor().getQueryManager().getDoesExistQuery().clone();
            existQuery.setObject(object);
            existQuery.setPrimaryKey(writeQuery.getPrimaryKey());
            existQuery.setDescriptor(getDescriptor());
            existQuery.setTranslationRow(getTranslationRow());

            doesExist = ((Boolean)getSession().executeQuery(existQuery)).booleanValue();
        }

        if (!doesExist) {
            // a shallow insert must be performed
            writeQuery.dontCascadeParts();
            insertObjectForWrite();
            // mark this object as shallow committed so that the insert will do an update
            commitManager.markShallowCommit(object);
        }
    }

    /**
     * Update the foreign key fields when resolving a bi-directonal reference in a UOW.
     * This must always be dynamic as it is called within an insert query and is really part of the insert
     * and does not fire update events or worry about locking.
     */
    protected void updateForeignKeyFieldAfterInsert() {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        Object object = writeQuery.getObject();

        writeQuery.setPrimaryKey(getDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(object, getSession()));
        // reset the translation row because the insert has occurred and the id has
        // been assigned to the object, but not the row
        writeQuery.setTranslationRow(getDescriptor().getObjectBuilder().buildRowForTranslation(object, getSession()));

        updateForeignKeyFieldAfterInsert(writeQuery);
    }

    /**
       * Issue update SQL statement
       */
    public abstract Integer updateAll() throws DatabaseException;

    /**
     * Update an object.
     * Return the row count.
     */
    public abstract Integer updateObject() throws DatabaseException;

    /**
     * Update the foreign key fields when resolving a bi-directonal reference in a UOW.
     * This must always be dynamic as it is called within an insert query and is really part of the insert
     * and does not fire update events or worry about locking.
     */
    protected abstract void updateForeignKeyFieldAfterInsert(WriteObjectQuery writeQuery);

    protected void updateObjectAndRowWithReturnRow(Collection returnFields, boolean isFirstCallForInsert) {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        AbstractRecord outputRow = (AbstractRecord)writeQuery.getProperties().get("output");
        if ((outputRow == null) || outputRow.isEmpty()) {
            return;
        }
        AbstractRecord row = new DatabaseRecord();
        for (Iterator iterator = returnFields.iterator(); iterator.hasNext();) {
            DatabaseField field = (DatabaseField)iterator.next();
            if (outputRow.containsKey(field)) {
                row.put(field, outputRow.get(field));
            }
        }
        if (row.isEmpty()) {
            return;
        }

        Object object = writeQuery.getObject();

        getDescriptor().getObjectBuilder().assignReturnRow(object, getSession(), row);

        Object primaryKey = null;
        if (isFirstCallForInsert) {
            AbstractRecord pkToModify = new DatabaseRecord();
            List primaryKeyFields = getDescriptor().getPrimaryKeyFields();
            for (int i = 0; i < primaryKeyFields.size(); i++) {
                DatabaseField field = (DatabaseField)primaryKeyFields.get(i);
                if (row.containsKey(field)) {
                    pkToModify.put(field, row.get(field));
                }
            }
            if (!pkToModify.isEmpty()) {
                primaryKey = getDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(object, getSession());
                writeQuery.setPrimaryKey(primaryKey);
                // Now I need to update the row
                getModifyRow().putAll(pkToModify);
                getDescriptor().getObjectBuilder().addPrimaryKeyForNonDefaultTable(getModifyRow(), object, getSession());
            }
        }

        // update the changeSet if there is one
        if (getSession().isUnitOfWork()) {
            ObjectChangeSet objectChangeSet = writeQuery.getObjectChangeSet();
            if ((objectChangeSet == null) && (((UnitOfWorkImpl)getSession()).getUnitOfWorkChangeSet() != null)) {
                objectChangeSet = (ObjectChangeSet)((UnitOfWorkImpl)getSession()).getUnitOfWorkChangeSet().getObjectChangeSetForClone(object);
            }
            if (objectChangeSet != null) {
                updateChangeSet(getDescriptor(), objectChangeSet, row, object);
                if (primaryKey != null) {
                    objectChangeSet.setId(primaryKey);
                }
            }
        }
    }

    /**
     * Update the change set with all of the field values in the row.
     * This handles writable and read-only mappings, direct and nested aggregates.
     * It is used from ReturningPolicy and VersionLockingPolicy.
     */
    public void updateChangeSet(ClassDescriptor desc, ObjectChangeSet objectChangeSet, AbstractRecord row, Object object) {
        int size = row.size();
        HashSet handledMappings = null;
        if (size > 1) {
            // PERF: Only create set if more than one fields, used to avoid duplicate in transformation mappings.
            handledMappings = new HashSet(size);
        }
        for (int i = 0; i < size; i++) {
            DatabaseField field = (DatabaseField)row.getFields().get(i);
            updateChangeSet(desc, objectChangeSet, field, object, handledMappings);
        }
    }
    
    /**
     * Update the change set with the field value.
     * This handles writable and read-only mappings, direct and nested aggregates.
     * It is used from ReturningPolicy and VersionLockingPolicy.
     */
    protected void updateChangeSet(ClassDescriptor desc, ObjectChangeSet objectChangeSet, DatabaseField field, Object object) {
        updateChangeSet(desc, objectChangeSet, field, object, null);
    }
    
    /**
     * Update the change set with the field value.
     * This handles writable and read-only mappings, direct and nested aggregates.
     * It is used from ReturningPolicy and VersionLockingPolicy.
     */
    protected void updateChangeSet(ClassDescriptor desc, ObjectChangeSet objectChangeSet, DatabaseField field, Object object, Collection handledMappings) {
        DatabaseMapping mapping;
        List readOnlyMappings = desc.getObjectBuilder().getReadOnlyMappingsForField(field);
        if (readOnlyMappings != null) {
            int size = readOnlyMappings.size();
            for (int index = 0; index < size; index++) {
                mapping = (DatabaseMapping)readOnlyMappings.get(index);
                updateChangeSet(mapping, objectChangeSet, field, object, handledMappings);
            }
        }
        mapping = desc.getObjectBuilder().getMappingForField(field);
        if (mapping != null) {
            updateChangeSet(mapping, objectChangeSet, field, object, handledMappings);
        }
    }

    protected void updateChangeSet(DatabaseMapping mapping, ObjectChangeSet objectChangeSet, DatabaseField field, Object object, Collection handledMappings) {
        if ((handledMappings != null) && handledMappings.contains(mapping)) {
            return;
        }
        if (mapping.isDirectToFieldMapping()) {
            Object attributeValue = mapping.getAttributeValueFromObject(object);
            objectChangeSet.updateChangeRecordForAttribute(mapping, attributeValue, getSession());
        } else if (mapping.isAggregateObjectMapping()) {
            Object aggregate = mapping.getAttributeValueFromObject(object);
            AggregateChangeRecord record = (AggregateChangeRecord)objectChangeSet.getChangesForAttributeNamed(mapping.getAttributeName());
            if (aggregate != null) {
                if (record == null) {
                    record = new AggregateChangeRecord(objectChangeSet);
                    record.setAttribute(mapping.getAttributeName());
                    record.setMapping(mapping);
                    objectChangeSet.addChange(record);
                }
                ObjectChangeSet aggregateChangeSet = (ObjectChangeSet)record.getChangedObject();
                ClassDescriptor aggregateDescriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
                if (aggregateChangeSet == null) {
                    aggregateChangeSet = aggregateDescriptor.getObjectBuilder().createObjectChangeSet(aggregate, (UnitOfWorkChangeSet)((UnitOfWorkImpl)getSession()).getUnitOfWorkChangeSet(), getSession());
                    record.setChangedObject(aggregateChangeSet);
                }
                updateChangeSet(aggregateDescriptor, aggregateChangeSet, field, aggregate, handledMappings);
            } else {
                if (record != null) {
                    record.setChangedObject(null);
                }
            }
        } else if (mapping.isTransformationMapping()) {
            TransformationMappingChangeRecord record = (TransformationMappingChangeRecord)objectChangeSet.getChangesForAttributeNamed(mapping.getAttributeName());
            if (record == null) {
                record = new TransformationMappingChangeRecord(objectChangeSet);
                record.setAttribute(mapping.getAttributeName());
                record.setMapping(mapping);
                objectChangeSet.addChange(record);
            }
            AbstractRecord transformationRow = new DatabaseRecord(mapping.getFields().size());
            int size = mapping.getFields().size();
            for (int i = 0; i < size; i++) {
                DatabaseField fieldToAdd = mapping.getFields().get(i);
                Object value = ((AbstractTransformationMapping)mapping).valueFromObject(object, fieldToAdd, getSession());
                transformationRow.add(fieldToAdd, value);
            }
            record.setRow(transformationRow);
            if (handledMappings != null) {
                handledMappings.add(mapping);
            }
        } else {
            getSession().log(SessionLog.FINEST, SessionLog.QUERY, "field_for_unsupported_mapping_returned", field, getDescriptor());
        }
    }

    /**
     * Update the object's primary key by fetching a new sequence number from the accessor.
     */
    protected void updateObjectAndRowWithSequenceNumber() throws DatabaseException {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        Object object = writeQuery.getObject();
        ClassDescriptor descriptor = writeQuery.getDescriptor();
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        AbstractSession session = writeQuery.getSession();
        Object sequenceValue = objectBuilder.assignSequenceNumber(object, session);
        if (sequenceValue == null) {
            return;
        }
        Object primaryKey = objectBuilder.extractPrimaryKeyFromObject(object, getSession());
        writeQuery.setPrimaryKey(primaryKey);
        DatabaseField sequenceNumberField = descriptor.getSequenceNumberField();
        AbstractRecord modifyRow = getModifyRow();
        // Update the row.
        modifyRow.put(sequenceNumberField, sequenceValue);
        if (descriptor.hasMultipleTables()) {
            objectBuilder.addPrimaryKeyForNonDefaultTable(modifyRow, object, session);
        }
        // Update the changeSet if there is one.
        if (session.isUnitOfWork()) {
            ObjectChangeSet objectChangeSet = writeQuery.getObjectChangeSet();
            if ((objectChangeSet == null) && (((UnitOfWorkImpl)session).getUnitOfWorkChangeSet() != null)) {
                objectChangeSet = (ObjectChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet().getObjectChangeSetForClone(object);
            }
            if (objectChangeSet != null && (!objectChangeSet.isNew() || query.getDescriptor().shouldUseFullChangeSetsForNewObjects())) {
                updateChangeSet(descriptor, objectChangeSet, sequenceNumberField, object);
                objectChangeSet.setId(primaryKey);
            }
        }
    }

    /**
     * Update the object.
     * This is only used for non-unit-of-work updates.
     */
    public void updateObjectForWrite() {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        ClassDescriptor descriptor = getDescriptor();
        DescriptorQueryManager queryManager = descriptor.getQueryManager();
        // check for user-defined query
        if ((!writeQuery.isUserDefined())// this is not a user-defined query
                 && queryManager.hasUpdateQuery()// there is a user-defined query
                 && isExpressionQueryMechanism()) {// this is not a hand-coded call (custom SQL etc.)
            performUserDefinedUpdate();
            return;
        }
        Object object = writeQuery.getObject();
        AbstractSession session = getSession();
        CommitManager commitManager = session.getCommitManager();
        // This must be done after the custom query check, otherwise it will be done twice.
        commitManager.markPreModifyCommitInProgress(object);
        DescriptorEventManager eventManager = descriptor.getEventManager();
        if (writeQuery.getObjectChangeSet() == null) {
            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                // only throw the events if there is no changeset otherwise the event will be thrown twice
                // once by the calculate changes code and here
                eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PreUpdateEvent, writeQuery));
            }
        }

        // Verify if deep shallow modify is turned on
        if (writeQuery.shouldCascadeParts()) {
            queryManager.preUpdate(writeQuery);
        }

        // The row must not be built until after preUpdate in case the object reference has changed.
        // For a user defined update in the uow to row must be built twice to check if any update is required.
        if ((writeQuery.isUserDefined() || writeQuery.isCallQuery()) && (!getSession().isUnitOfWork())) {
            writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRow(object, getSession(), WriteType.UNDEFINED));
        } else {
            writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRowForUpdate(writeQuery));
        }

        // Optimistic read lock implementation
        Boolean shouldModifyVersionField = null;
        if (session.isUnitOfWork() && ((UnitOfWorkImpl)session).hasOptimisticReadLockObjects()) {
            shouldModifyVersionField = (Boolean)((UnitOfWorkImpl)session).getOptimisticReadLockObjects().get(writeQuery.getObject());
        }

        if (!getModifyRow().isEmpty() || (shouldModifyVersionField != null) || ((descriptor.getCMPPolicy() != null) && (descriptor.getCMPPolicy().getForceUpdate()))) {
            // If user defined the entire row is required. Must not be built until change is known.
            if ((writeQuery.isUserDefined() || writeQuery.isCallQuery()) && getSession().isUnitOfWork()) {
                writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRow(object, getSession(), WriteType.UNDEFINED));
            }

            // Update the write lock field if required.
            if (descriptor.usesOptimisticLocking()) {
                OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
                policy.addLockValuesToTranslationRow(writeQuery);

                if (!getModifyRow().isEmpty() || shouldModifyVersionField.booleanValue()) {
                    // Update the row with newer lock value.
                    policy.updateRowAndObjectForUpdate(writeQuery, object);
                } else if (!shouldModifyVersionField.booleanValue() && (policy instanceof VersionLockingPolicy)) {
                    // Add the existing write lock value to the for a "read" lock (requires something to update).
                    ((VersionLockingPolicy)policy).writeLockValueIntoRow(writeQuery, object);
                }
            }

            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(DescriptorEventManager.AboutToUpdateEvent, writeQuery);
                event.setRecord(getModifyRow());
                eventManager.executeEvent(event);
            }

            if (QueryMonitor.shouldMonitor()) {
                QueryMonitor.incrementUpdate(getWriteObjectQuery());
            }
            int rowCount = updateObject().intValue();

            if (rowCount < 1) {
                if (session.hasEventManager()) {
                    session.getEventManager().noRowsModified(writeQuery, object);
                }
            }
            if (descriptor.usesOptimisticLocking()) {
                descriptor.getOptimisticLockingPolicy().validateUpdate(rowCount, object, writeQuery);
            }
        }

        commitManager.markPostModifyCommitInProgress(object);

        // Verify if deep shallow modify is turned on
        if (writeQuery.shouldCascadeParts()) {
            queryManager.postUpdate(writeQuery);
        }
        if ((descriptor.getHistoryPolicy() != null) && descriptor.getHistoryPolicy().shouldHandleWrites()) {
            descriptor.getHistoryPolicy().postUpdate(writeQuery);
        }

        // PERF: Avoid events if no listeners.
        if (eventManager.hasAnyEventListeners()) {
            eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PostUpdateEvent, writeQuery));
        }
    }

    /**
     * Update the object.
     * This is used by the unit-of-work update.
     */
    public void updateObjectForWriteWithChangeSet() {
        WriteObjectQuery writeQuery = getWriteObjectQuery();
        ObjectChangeSet changeSet = writeQuery.getObjectChangeSet();
        Object object = writeQuery.getObject();
        ClassDescriptor descriptor = getDescriptor();
        DescriptorQueryManager queryManager = descriptor.getQueryManager();
        AbstractSession session = getSession();
        CommitManager commitManager = session.getCommitManager();
        // check for user-defined query
        if ((!writeQuery.isUserDefined())// this is not a user-defined query
                 && queryManager.hasUpdateQuery()// there is a user-defined query
                 && isExpressionQueryMechanism()) {// this is not a hand-coded call (custom SQL etc.)
            // This must be done here because the user defined update does not use a changeset so it will not be set otherwise
            commitManager.markPreModifyCommitInProgress(object);
            performUserDefinedUpdate();
            return;
        }
        // This must be done after the custom query check, otherwise it will be done twice.
        commitManager.markPreModifyCommitInProgress(object);
        DescriptorEventManager eventManager = descriptor.getEventManager();

        if (changeSet.hasChanges()) {
            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(DescriptorEventManager.PreUpdateWithChangesEvent, writeQuery);
                eventManager.executeEvent(event);

                // PreUpdateWithChangesEvent listeners may have altered the object - should recalculate the change set.
                UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)session).getUnitOfWorkChangeSet();
                if (!uowChangeSet.isChangeSetFromOutsideUOW() && writeQuery.getObjectChangeSet().shouldRecalculateAfterUpdateEvent()){
                    // writeQuery.getObjectChangeSet() is mapped to object in uowChangeSet.
                    // It is first cleared then re-populated by calculateChanges method.
                    if (!descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy() ){
                        // clear the change set without clearing the maps keys since they are not alterable by the event
                        // if the map is changed, it will be changed in the owning object and the
                        // change set will be changed there as well.
                        writeQuery.getObjectChangeSet().clear(false);
                    }
                    if (descriptor.getObjectChangePolicy().calculateChangesForExistingObject(object, uowChangeSet, ((UnitOfWorkImpl)session), descriptor, false) == null) {
                        // calculateChanges returns null in case the changeSet doesn't have changes.
                        // It should be removed from the list of ObjectChangeSets that have changes in uowChangeSet.
                        uowChangeSet.getAllChangeSets().remove(writeQuery.getObjectChangeSet());
                    }
                }
            }
        }
         
        // Verify if deep shallow modify is turned on
        if (writeQuery.shouldCascadeParts()) {
            queryManager.preUpdate(writeQuery);
        }

        // The row must not be built until after preUpdate in case the object reference has changed.
        // For a user defined update in the uow to row must be built twice to check if any update is required.
        writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRowForUpdateWithChangeSet(writeQuery));
            
    	Boolean shouldModifyVersionField = changeSet.shouldModifyVersionField();
        if (!getModifyRow().isEmpty() || (shouldModifyVersionField != null) || changeSet.hasCmpPolicyForcedUpdate()) {
            // If user defined the entire row is required. Must not be built until change is known.
            if (writeQuery.isUserDefined() || writeQuery.isCallQuery()) {
                writeQuery.setModifyRow(descriptor.getObjectBuilder().buildRow(object, session, WriteType.UNDEFINED));
            }
            OptimisticLockingPolicy lockingPolicy = descriptor.getOptimisticLockingPolicy();

            // Update the write lock field if required.
            if (lockingPolicy != null) {
                lockingPolicy.addLockValuesToTranslationRow(writeQuery);

                if (!getModifyRow().isEmpty() || shouldModifyVersionField.booleanValue()) {
                    // Update the row with newer lock value.
                    lockingPolicy.updateRowAndObjectForUpdate(writeQuery, object);
                } else if (!shouldModifyVersionField.booleanValue() && (lockingPolicy instanceof VersionLockingPolicy)) {
                    // Add the existing write lock value to the for a "read" lock (requires something to update).
                    ((VersionLockingPolicy)lockingPolicy).writeLockValueIntoRow(writeQuery, object);
                }
            }

            // PERF: Avoid events if no listeners.
            if (eventManager.hasAnyEventListeners()) {
                DescriptorEvent event = new DescriptorEvent(DescriptorEventManager.AboutToUpdateEvent, writeQuery);
                event.setRecord(getModifyRow());
                eventManager.executeEvent(event);
            }

            if (QueryMonitor.shouldMonitor()) {
                QueryMonitor.incrementUpdate(getWriteObjectQuery());
            }
            int rowCount = updateObject().intValue();

            if (rowCount < 1) {
                if (session.hasEventManager()) {
                    session.getEventManager().noRowsModified(writeQuery, object);
                }
            }
            if (lockingPolicy != null) {
                lockingPolicy.validateUpdate(rowCount, object, writeQuery);
            }
        }

        commitManager.markPostModifyCommitInProgress(object);

        // Verify if deep shallow modify is turned on
        if (writeQuery.shouldCascadeParts()) {
            queryManager.postUpdate(writeQuery);
        }
        if ((descriptor.getHistoryPolicy() != null) && descriptor.getHistoryPolicy().shouldHandleWrites()) {
            descriptor.getHistoryPolicy().postUpdate(writeQuery);
        }

        // PERF: Avoid events if no listeners.
        if (eventManager.hasAnyEventListeners()) {
            eventManager.executeEvent(new DescriptorEvent(DescriptorEventManager.PostUpdateEvent, writeQuery));
        }
    }
}
