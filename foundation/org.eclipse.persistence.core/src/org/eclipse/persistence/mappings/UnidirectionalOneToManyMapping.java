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
 *     ailitchev - Uni-directional OneToMany
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.descriptors.CascadeLockingPolicy;
import org.eclipse.persistence.internal.expressions.SQLUpdateStatement;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.queries.ComplexQueryResult;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>: UnidirectionalOneToManyMapping doesn't have 1:1 back reference mapping.
 *
 * @author Andrei Ilitchev
 * @since Eclipselink 1.1
 */
public class UnidirectionalOneToManyMapping extends OneToManyMapping {

    /** Used for data modification events. */
    protected static final String PostInsert = "postInsert";
    protected static final String ObjectRemoved = "objectRemoved";
    protected static final String ObjectAdded = "objectAdded";
    
    /** Query used to update all target rows before the source object is deleted. */
    protected transient DataModifyQuery preDeleteQuery;
    protected transient boolean hasCustomPreDeleteQuery;
    
    /** 
     * Query used to update a single target row setting its foreign key to point to the source. 
     * Run once for each target added to the source.
     * Example: 
     *   for Employee with managedEmployees attribute mapped with UnidirectionalOneToMany
     *   the query looks like:
     *   UPDATE EMPLOYEE SET MANAGER_ID = 1 WHERE (EMP_ID = 2)
     *   where 1 is id of the source, and 2 is the id of the target to be added.  
     **/
    protected transient DataModifyQuery addTargetQuery;
    protected transient boolean hasCustomAddTargetQuery;

    /** 
     * Query used to update a single target row changing its foreign key value from the one pointing to the source to null. 
     * Run once for each target removed from the source. 
     * Example: 
     *   for Employee with managedEmployees attribute mapped with UnidirectionalOneToMany
     *   the query looks like:
     *   UPDATE EMPLOYEE SET MANAGER_ID = null WHERE ((MANAGER_ID = 1) AND (EMP_ID = 2))
     *   where 1 is id of the source, and 2 is the id of the target to be removed.  
     **/
    protected transient DataModifyQuery removeTargetQuery;
    protected transient boolean hasCustomRemoveTargetQuery;

    /** 
     * Query used to update all target rows changing target foreign key value from the one pointing to the source to null. 
     * Run before the source object is deleted.
     * Example: 
     *   for Employee with managedEmployees attribute mapped with UnidirectionalOneToMany
     *   the query looks like:
     *   UPDATE EMPLOYEE SET MANAGER_ID = null WHERE (MANAGER_ID = 1)
     *   where 1 is id of the source to be deleted.  
     **/
    protected transient DataModifyQuery removeAllTargetsQuery;
    protected transient boolean hasCustomRemoveAllTargetsQuery;
    
    /**
     * Indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source. 
     **/
    protected transient boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget;

    /**
     * Indicates whether target's optimistic locking value should be incremented on
     * the source deletion. 
     * Note that if the flag is set to true then the indirection will be triggered on
     * source delete - in order to verify all targets' versions.
     **/
    protected transient boolean shouldIncrementTargetLockValueOnDeleteSource;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public UnidirectionalOneToManyMapping() {
        super();
        this.addTargetQuery = new DataModifyQuery();
        this.removeTargetQuery = new DataModifyQuery();
        this.removeAllTargetsQuery = new DataModifyQuery();
        this.shouldIncrementTargetLockValueOnAddOrRemoveTarget = true;
        this.shouldIncrementTargetLockValueOnDeleteSource = true;
    }
    
    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two collections
     * @return org.eclipse.persistence.internal.sessions.ChangeRecord
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        ChangeRecord record = super.compareForChange(clone, backUp, owner, session);
        if(record != null && getReferenceDescriptor().getOptimisticLockingPolicy() != null) {
            postCalculateChanges(record, session);
        }
        return record;
    }

    /**
     * INTERNAL:
     * Extract the primary key value from the source row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Vector extractPrimaryKeyFromRow(AbstractRecord row, AbstractSession session) {
        int size = sourceKeyFields.size();
        Vector key = new Vector(size);
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();

        for (int index=0; index < size; index++) {
            DatabaseField field = sourceKeyFields.get(index);
            Object value = row.get(field);

            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, field.getType());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }

        return key;
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the target row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Vector extractSourceKeyFromRow(AbstractRecord row, AbstractSession session) {
        int size = sourceKeyFields.size();
        Vector key = new Vector(size);
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();

        for (int index = 0; index < size; index++) {
            DatabaseField targetField = targetForeignKeyFields.get(index);
            DatabaseField sourceField = sourceKeyFields.get(index);
            Object value = row.get(targetField);

            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, sourceField.getType());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }

        return key;
    }

    /**
     * INTERNAL:
     * Extract the value from the batch optimized query.
     */
    public Object extractResultFromBatchQuery(DatabaseQuery query, AbstractRecord databaseRow, AbstractSession session, AbstractRecord argumentRow) {
        //this can be null, because either one exists in the query or it will be created
        Hashtable referenceObjectsByKey = null;
        synchronized (query) {
            referenceObjectsByKey = getBatchReadObjects(query, session);
            if (referenceObjectsByKey == null) {
                ReadAllQuery batchQuery = (ReadAllQuery)query;
                ComplexQueryResult complexResult = (ComplexQueryResult)session.executeQuery(batchQuery, argumentRow);
                // Batch query created in ForeignReferenceMapping.prepareNestedBatchQuery without specifying container policy - uses ListContainerPolicy by default.
                List results = (List)complexResult.getResult();
                referenceObjectsByKey = new Hashtable();
                List rows = (List)complexResult.getData();
                int size = results.size();
                for (int index = 0; index < size; index++) {
                    Object eachReferenceObject = results.get(index);
                    CacheKey eachReferenceKey = new CacheKey(extractSourceKeyFromRow((AbstractRecord)rows.get(index), session));
                    if (!referenceObjectsByKey.containsKey(eachReferenceKey)) {
                        referenceObjectsByKey.put(eachReferenceKey, containerPolicy.containerInstance());
                    }
                    containerPolicy.addInto(eachReferenceObject, referenceObjectsByKey.get(eachReferenceKey), session);
                }
                setBatchReadObjects(referenceObjectsByKey, query, session);
            }
        }
        Object result = referenceObjectsByKey.get(new CacheKey(extractPrimaryKeyFromRow(databaseRow, session)));

        // The source object might not have any target objects
        if (result == null) {
            return containerPolicy.containerInstance();
        } else {
            return result;
        }
    }

    /**
     * INTERNAL:
     */
    public boolean isUnidirectionalOneToManyMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        initializeAddTargetQuery(session);
        initializeRemoveTargetQuery(session);
        initializeRemoveAllTargetsQuery(session);
        if(getReferenceDescriptor().getOptimisticLockingPolicy() != null) {
            if(shouldIncrementTargetLockValueOnAddOrRemoveTarget) {
                descriptor.addMappingsPostCalculateChanges(this);
            }
            if(shouldIncrementTargetLockValueOnDeleteSource && !isPrivateOwned) {
                descriptor.addMappingsPostCalculateChangesOnDeleted(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize addTargetQuery.
     */
    protected void initializeAddTargetQuery(AbstractSession session) {
        if (!addTargetQuery.hasSessionName()) {
            addTargetQuery.setSessionName(session.getName());
        }
        if (hasCustomAddTargetQuery) {
            return;
        }

        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        List<DatabaseField> targetPrimaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        int size = targetPrimaryKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
            Expression expression = builder.getField(targetPrimaryKey).equal(builder.getParameter(targetPrimaryKey));
            whereClause = expression.and(whereClause);
        }

        AbstractRecord modifyRow = new DatabaseRecord();
        size = targetForeignKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            modifyRow.put(targetForeignKey, null);
        }

        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(getReferenceDescriptor().getDefaultTable());
        statement.setWhereClause(whereClause);
        statement.setModifyRow(modifyRow);
        addTargetQuery.setSQLStatement(statement);
    }

    /**
     * INTERNAL:
     * Initialize removeTargetQuery.
     */
    protected void initializeRemoveTargetQuery(AbstractSession session) {
        if (!removeTargetQuery.hasSessionName()) {
            removeTargetQuery.setSessionName(session.getName());
        }
        if (hasCustomRemoveTargetQuery) {
            return;
        }

        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        List<DatabaseField> targetPrimaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        int size = targetPrimaryKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
            Expression expression = builder.getField(targetPrimaryKey).equal(builder.getParameter(targetPrimaryKey));
            whereClause = expression.and(whereClause);
        }

        AbstractRecord modifyRow = new DatabaseRecord();
        size = targetForeignKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            modifyRow.put(targetForeignKey, builder.value(null));
            Expression expression = builder.getField(targetForeignKey).equal(builder.getParameter(targetForeignKey));
            whereClause = expression.and(whereClause);
        }

        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(getReferenceDescriptor().getDefaultTable());
        statement.setWhereClause(whereClause);
        statement.setModifyRow(modifyRow);
        removeTargetQuery.setSQLStatement(statement);
    }

    /**
     * INTERNAL:
     * Initialize removeAllTargetsQuery.
     */
    protected void initializeRemoveAllTargetsQuery(AbstractSession session) {
        if (!removeAllTargetsQuery.hasSessionName()) {
            removeAllTargetsQuery.setSessionName(session.getName());
        }
        if (hasCustomRemoveAllTargetsQuery) {
            return;
        }

        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        AbstractRecord modifyRow = new DatabaseRecord();
        int size = targetForeignKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            modifyRow.put(targetForeignKey, builder.value(null));
            Expression expression = builder.getField(targetForeignKey).equal(builder.getParameter(targetForeignKey));
            whereClause = expression.and(whereClause);
        }

        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(getReferenceDescriptor().getDefaultTable());
        statement.setWhereClause(whereClause);
        statement.setModifyRow(modifyRow);
        removeAllTargetsQuery.setSQLStatement(statement);
    }

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it if private.
     */
    protected void objectAddedDuringUpdate(ObjectLevelModifyQuery query, Object objectAdded, ObjectChangeSet changeSet) throws DatabaseException, OptimisticLockException {
        // First insert/update object.
        super.objectAddedDuringUpdate(query, objectAdded, changeSet);

        // In the uow data queries are cached until the end of the commit.
        if (query.shouldCascadeOnlyDependentParts()) {
            // Hey I might actually want to use an inner class here... ok array for now.
            Object[] event = new Object[3];
            event[0] = ObjectAdded;
            event[1] = query;
            event[2] = objectAdded;
            query.getSession().getCommitManager().addDataModificationEvent(this, event);
        } else {
            updateTargetForeignKeyPostUpdateSource_ObjectAdded(query, objectAdded);
        }
    }

    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted) throws DatabaseException, OptimisticLockException {
        if(!isPrivateOwned()) {
            // In the uow data queries are cached until the end of the commit.
            if (query.shouldCascadeOnlyDependentParts()) {
                // Hey I might actually want to use an inner class here... ok array for now.
                Object[] event = new Object[3];
                event[0] = ObjectRemoved;
                event[1] = query;
                event[2] = objectDeleted;
                query.getSession().getCommitManager().addDataModificationEvent(this, event);
            } else {
                updateTargetForeignKeyPostUpdateSource_ObjectRemoved(query, objectDeleted);
            }
        }

        // Delete object after join entry is delete if private.
        super.objectRemovedDuringUpdate(query, objectDeleted);
    }

    /**
     * INTERNAL:
     * Perform the commit event.
     * This is used in the uow to delay data modifications.
     */
    public void performDataModificationEvent(Object[] event, AbstractSession session) throws DatabaseException, DescriptorException {
        // Hey I might actually want to use an inner class here... ok array for now.
        if (event[0] == PostInsert) {
            updateTargetForeignKeysPostInsertSource((WriteObjectQuery)event[1]);
        } else if (event[0] == ObjectRemoved) {
            updateTargetForeignKeyPostUpdateSource_ObjectRemoved((WriteObjectQuery)event[1], event[2]);
        } else if (event[0] == ObjectAdded) {
            updateTargetForeignKeyPostUpdateSource_ObjectAdded((WriteObjectQuery)event[1], event[2]);
        } else {
            throw DescriptorException.invalidDataModificationEventCode(event[0], this);
        }
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (shouldObjectModifyCascadeToParts(query)) {
            super.preDelete(query);
        } else {
            updateTargetForeignKeyPreDeleteSource(query);
        }

    }
    
    /**
     * Prepare a cascade locking policy.
     */
    public void prepareCascadeLockingPolicy() {
        CascadeLockingPolicy policy = new CascadeLockingPolicy(getDescriptor(), getReferenceDescriptor());
        policy.setQueryKeyFields(getSourceKeysToTargetForeignKeys());
        policy.setShouldHandleUnmappedFields(true);
        getReferenceDescriptor().addCascadeLockingPolicy(policy);
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    public void postCalculateChanges(org.eclipse.persistence.sessions.changesets.ChangeRecord changeRecord, AbstractSession session) {
        // targets are added to and/or removed to/from the source.
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeRecord;
        Iterator it = collectionChangeRecord.getAddObjectList().values().iterator();
        while(it.hasNext()) {
            ObjectChangeSet change = (ObjectChangeSet)it.next();
            if(!change.hasChanges()) {
                change.setShouldModifyVersionField(Boolean.TRUE);
                ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, session, false);
            }
        }
        // in the mapping is privately owned then the target will be deleted - no need to modify target version.
        if(!isPrivateOwned()) {
            it = collectionChangeRecord.getRemoveObjectList().values().iterator();
            while(it.hasNext()) {
                ObjectChangeSet change = (ObjectChangeSet)it.next(); 
                if(!change.hasChanges()) {
                    change.setShouldModifyVersionField(Boolean.TRUE);
                    ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, session, false);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require objects to be deleted contribute to change set creation.
     */
    public void postCalculateChangesOnDeleted(Object deletedObject, UnitOfWorkChangeSet uowChangeSet, AbstractSession session) {        
        // the source is deleted:
        // trigger the indirection - we have to get optimistic lock exception
        // in case another thread has updated one of the targets:
        // triggered indirection caches the target with the old version,
        // then the version update waits until the other thread (which is locking the version field) commits,
        // then the version update is executed and it throws optimistic lock exception.
        Object col = getRealCollectionAttributeValueFromObject(deletedObject, session);
        if(col != null) {
            Object iterator = containerPolicy.iteratorFor(col);
            while (containerPolicy.hasNext(iterator)) {
                Object target = containerPolicy.next(iterator, session);
                ObjectChangeSet change = referenceDescriptor.getObjectBuilder().createObjectChangeSet(target, (org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)uowChangeSet, session);
                if(!change.hasChanges()) {
                    change.setShouldModifyVersionField(Boolean.TRUE);
                    ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, session, false);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Insert target foreign key into the reference table. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct a insert statement with above fields and values for relation table.
     * <p>- execute the statement.
     * <p>- Repeat above three statements until all the target objects are done.
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException {
        super.postInsert(query);
        if (isReadOnly()) {
            return;
        }

        if (query.shouldCascadeOnlyDependentParts()) {
            // Hey I might actually want to use an inner class here... ok array for now.
            Object[] event = new Object[2];
            event[0] = PostInsert;
            event[1] = query;
            query.getSession().getCommitManager().addDataModificationEvent(this, event);
        } else {
            updateTargetForeignKeysPostInsertSource(query);
        }
    }

    /**
     * INTERNAL:
     * Add additional fields
     */
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ReadAllQuery query) {
        ReadAllQuery mappingBatchQuery = (ReadAllQuery)batchQuery;
        mappingBatchQuery.setShouldIncludeData(true);
        int size = targetForeignKeyFields.size();
        for(int i=0; i < size; i++) {
            mappingBatchQuery.addAdditionalField(targetForeignKeyFields.get(i));
        }        
    }
    
    /**
     * INTERNAL:
     * Update the relation table with the entries related to this mapping.
     * Delete entries removed, insert entries added.
     * If private also insert/delete/update target objects.
     */
    public void postUpdate(WriteObjectQuery query) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }

        Object objectsInMemoryModel = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // This accesses the backup in uow otherwise goes to database (may be better of to delete all in non uow case).
        Object currentObjectsInDB = readPrivateOwnedForObject(query);
        if (currentObjectsInDB == null) {
            currentObjectsInDB = getContainerPolicy().containerInstance(1);
        }
        compareObjectsAndWrite(currentObjectsInDB, objectsInMemoryModel, query);
    }

    /**
     * INTERNAL:
     * The translation row may require additional fields than the primary key if the mapping in not on the primary key.
     */
    protected void prepareTranslationRow(AbstractRecord translationRow, Object object, AbstractSession session) {
        // Make sure that each source key field is in the translation row.
        int size = sourceKeyFields.size();
        for(int i=0; i < size; i++) {
            DatabaseField sourceKey = sourceKeyFields.get(i);
            if (!translationRow.containsKey(sourceKey)) {
                Object value = getDescriptor().getObjectBuilder().extractValueFromObjectForField(object, sourceKey, session);
                translationRow.put(sourceKey, value);
            }
        }
    }

    /**
     * PUBLIC:
     */
    public void setAddTargetSQLString(String sqlString) {
        DataModifyQuery query = new DataModifyQuery();
        query.setSQLString(sqlString);
        setCustomAddTargetQuery(query);
    }
    
    /**
     * PUBLIC:
     * The default add target query for mapping can be overridden by specifying the new query.
     * This query must set new value to target foreign key.
     */
    public void setCustomAddTargetQuery(DataModifyQuery query) {
        addTargetQuery = query;
        hasCustomAddTargetQuery = true;
    }

    /**
     * PUBLIC:
     * The default remove target query for mapping can be overridden by specifying the new query.
     * In case target foreign key references the source, this query must set target foreign key to null.
     */
    public void setCustomRemoveTargetQuery(DataModifyQuery query) {
        removeTargetQuery = query;
        hasCustomRemoveTargetQuery = true;
    }

    /**
     * PUBLIC:
     * The default remove all targets query for mapping can be overridden by specifying the new query.
     * This query must set all target foreign keys that reference the source to null.  
     */
    public void setCustomRemoveAllTargetsQuery(DataModifyQuery query) {
        removeAllTargetsQuery = query;
        hasCustomRemoveAllTargetsQuery = true;
    }

    /**
     * PUBLIC:
     * Set the name of the session to execute the mapping's queries under.
     * This can be used by the session broker to override the default session
     * to be used for the target class.
     */
    public void setSessionName(String name) {
        super.setSessionName(name);
        addTargetQuery.setSessionName(name);
        removeTargetQuery.setSessionName(name);
        removeAllTargetsQuery.setSessionName(name);
    }

    /**
     * PUBLIC:
     * Set value that indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source (default value is true).
     **/
    public void setShouldIncrementTargetLockValueOnAddOrRemoveTarget(boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget) {
        this.shouldIncrementTargetLockValueOnAddOrRemoveTarget = shouldIncrementTargetLockValueOnAddOrRemoveTarget;
    }

    /**
     * PUBLIC:
     * Set value that indicates whether target's optimistic locking value should be incremented on
     * the source deletion (default value is true).
     **/
    public void setShouldIncrementTargetLockValueOnDeleteSource(boolean shouldIncrementTargetLockValueOnDeleteSource) {
        this.shouldIncrementTargetLockValueOnDeleteSource = shouldIncrementTargetLockValueOnDeleteSource;
    }

    /**
     * PUBLIC:
     * Indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source (default value is true).
     **/
    public boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget() {
        return shouldIncrementTargetLockValueOnAddOrRemoveTarget;
    }

    /**
     * PUBLIC:
     * Indicates whether target's optimistic locking value should be incremented on
     * the source deletion (default value is true). 
     **/
    public boolean shouldIncrementTargetLockValueOnDeleteSource() {
        return shouldIncrementTargetLockValueOnDeleteSource;
    }

    /**
     * INTERNAL:
     * Update target foreign keys after a new source was inserted. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct an update statement with above fields and values for target table.
     * <p>- execute the statement.
     * <p>- Repeat above three statements until all the target objects are done.
     */
    public void updateTargetForeignKeysPostInsertSource(WriteObjectQuery query) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        if (cp.isEmpty(objects)) {
            return;
        }

        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getSession());
        AbstractRecord databaseRow = new DatabaseRecord();

        // Extract primary key and value from the source.
        int size = sourceKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField sourceKey = sourceKeyFields.get(index);
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            Object sourceKeyValue = query.getTranslationRow().get(sourceKey);
            databaseRow.put(targetForeignKey, sourceKeyValue);
        }

        // Extract target field and its value. Construct insert statement and execute it
        List<DatabaseField> targetPrimaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        size = targetPrimaryKeyFields.size();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object object = cp.next(iter, query.getSession());
            for(int index = 0; index < size; index++) {
                DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
                Object targetKeyValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(object, targetPrimaryKey, query.getSession());
                databaseRow.put(targetPrimaryKey, targetKeyValue);
            }

            query.getSession().executeQuery(addTargetQuery, databaseRow);
        }
    }

    /**
     * INTERNAL:
     * Update target foreign key after a target object was added to the source. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct an update statement with above fields and values for target table.
     * <p>- execute the statement.
     */
    public void updateTargetForeignKeyPostUpdateSource_ObjectAdded(ObjectLevelModifyQuery query, Object objectAdded) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getSession());
        AbstractRecord databaseRow = new DatabaseRecord();

        // Extract primary key and value from the source.
        int size = sourceKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField sourceKey = sourceKeyFields.get(index);
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            Object sourceKeyValue = query.getTranslationRow().get(sourceKey);
            databaseRow.put(targetForeignKey, sourceKeyValue);
        }

        // Extract target field and its value. Construct insert statement and execute it
        List<DatabaseField> targetPrimaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        size = targetPrimaryKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
            Object targetKeyValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(objectAdded, targetPrimaryKey, query.getSession());
            databaseRow.put(targetPrimaryKey, targetKeyValue);
        }

        query.getSession().executeQuery(addTargetQuery, databaseRow);
    }

    /**
     * INTERNAL:
     * Update target foreign key after a target object was removed from the source. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct an update statement with above fields and values for target table.
     * <p>- execute the statement.
     */
    public void updateTargetForeignKeyPostUpdateSource_ObjectRemoved(ObjectLevelModifyQuery query, Object objectRemoved) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getSession());
        AbstractRecord databaseRow = new DatabaseRecord();

        // Extract primary key and value from the source.
        int size = sourceKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField sourceKey = sourceKeyFields.get(index);
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            Object sourceKeyValue = query.getTranslationRow().get(sourceKey);
            databaseRow.put(targetForeignKey, sourceKeyValue);
        }

        // Extract target field and its value. Construct insert statement and execute it
        List<DatabaseField> targetPrimaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        size = targetPrimaryKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
            Object targetKeyValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(objectRemoved, targetPrimaryKey, query.getSession());
            databaseRow.put(targetPrimaryKey, targetKeyValue);
        }

        query.getSession().executeQuery(removeTargetQuery, databaseRow);
    }

    /**
     * INTERNAL:
     * Update target foreign key after a target object was removed from the source. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct an update statement with above fields and values for target table.
     * <p>- execute the statement.
     */
    public void updateTargetForeignKeyPreDeleteSource(ObjectLevelModifyQuery query) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        AbstractRecord databaseRow = new DatabaseRecord();

        // Extract primary key and value from the source.
        int size = sourceKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField sourceKey = sourceKeyFields.get(index);
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            Object sourceKeyValue = query.getTranslationRow().get(sourceKey);
            databaseRow.put(targetForeignKey, sourceKeyValue);
        }

        query.getSession().executeQuery(removeAllTargetsQuery, databaseRow);
    }
}
