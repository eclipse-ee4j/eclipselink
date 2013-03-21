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
 *     07/19/2011-2.2.1 Guy Pelletier 
 *       - 338812: ManyToMany mapping in aggregate object violate integrity constraint on deletion
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>: Many to many mappings are used to represent the relationships
 * between a collection of source objects and a collection of target objects.
 * The mapping requires the creation of an intermediate table for managing the
 * associations between the source and target records.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class ManyToManyMapping extends CollectionMapping implements RelationalMapping, MapComponentMapping {

    /** Used for data modification events. */
    protected static final String PostInsert = "postInsert";
    protected static final String ObjectRemoved = "objectRemoved";
    protected static final String ObjectAdded = "objectAdded";

    /** Mechanism holds relationTable and all fields and queries associated with it. */
    protected RelationTableMechanism mechanism;
    protected HistoryPolicy historyPolicy;

    /**
     * 266912: Since: EclipseLink 2.0 for the Metamodel API
     * For 1:1 and m:m mappings - track the original externally defined mapping if different 
     * Note: This field will provide differentiation for the following 
     *   external to internal representations for mapping types<br>
     *   - A OneToManyMapping will be represented by a ManyToManyMapping if unidirectional<br>
     *   - A ManyToOneMapping will be represented by a OneToOneMapping (without a FK constraint)<br>      
     */
    protected boolean isDefinedAsOneToManyMapping = false;
    
    /**
     * PUBLIC:
     * Default constructor.
     */
    public ManyToManyMapping() {
        this.mechanism = new RelationTableMechanism();
        this.isListOrderFieldSupported = true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isOwned(){
        return !isReadOnly;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the source table. This method is used if the keys are composite.
     */
    public void addSourceRelationKeyField(DatabaseField sourceRelationKeyField, DatabaseField sourcePrimaryKeyField) {
        this.mechanism.addSourceRelationKeyField(sourceRelationKeyField, sourcePrimaryKeyField);
    }
    
    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the source table. This method is used if the keys are composite.
     */
    public void addSourceRelationKeyFieldName(String sourceRelationKeyFieldName, String sourcePrimaryKeyFieldName) {
        this.mechanism.addSourceRelationKeyFieldName(sourceRelationKeyFieldName, sourcePrimaryKeyFieldName);
    }

    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the target table. This method is used if the keys are composite.
     */
    public void addTargetRelationKeyField(DatabaseField targetRelationKeyField, DatabaseField targetPrimaryKeyField) {
        this.mechanism.addTargetRelationKeyField(targetRelationKeyField, targetPrimaryKeyField);
    }
    
    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the target table. This method is used if the keys are composite.
     */
    public void addTargetRelationKeyFieldName(String targetRelationKeyFieldName, String targetPrimaryKeyFieldName) {
        this.mechanism.addTargetRelationKeyFieldName(targetRelationKeyFieldName, targetPrimaryKeyFieldName);
    }

    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    @Override
    public void collectQueryParameters(Set<DatabaseField> cacheFields){
        this.mechanism.collectQueryParameters(cacheFields);
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        ManyToManyMapping clone = (ManyToManyMapping)super.clone();        
        clone.mechanism = (RelationTableMechanism)this.mechanism.clone();

        return clone;
    }

    /**
     * INTERNAL:
     * Delete join tables before the start of the deletion process to avoid constraint errors.
     */
    @Override
    public void earlyPreDelete(DeleteObjectQuery query, Object object) {
        AbstractSession querySession = query.getSession();
        if (!this.isCascadeOnDeleteSetOnDatabase) {
            prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), querySession);
            querySession.executeQuery(this.deleteAllQuery, query.getTranslationRow());
        }

        if ((this.historyPolicy != null) && this.historyPolicy.shouldHandleWrites()) {
            if (this.isCascadeOnDeleteSetOnDatabase) {
                prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), querySession);
            }
            this.historyPolicy.mappingLogicalDelete(this.deleteAllQuery, query.getTranslationRow(), querySession);
        }
    }
    
    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return session.executeQuery(getSelectionQuery(), dbRow);
    }
    
    /**
     * INTERNAL:
     * Adds locking clause to the target query to extend pessimistic lock scope.
     */
    @Override
    protected void extendPessimisticLockScopeInTargetQuery(ObjectLevelReadQuery targetQuery, ObjectBuildingQuery sourceQuery) {
        this.mechanism.setRelationTableLockingClause(targetQuery, sourceQuery);
    }
    
    /**
     * INTERNAL:
     * Called only if both 
     * shouldExtendPessimisticLockScope and shouldExtendPessimisticLockScopeInSourceQuery are true.
     * Adds fields to be locked to the where clause of the source query.
     * Note that the sourceQuery must be ObjectLevelReadQuery so that it has ExpressionBuilder.
     * 
     * This method must be implemented in subclasses that allow
     * setting shouldExtendPessimisticLockScopeInSourceQuery to true.
     */
    @Override
    public void extendPessimisticLockScopeInSourceQuery(ObjectLevelReadQuery sourceQuery) {
        Expression exp = sourceQuery.getSelectionCriteria();
        exp = this.mechanism.joinRelationTableField(exp, sourceQuery.getExpressionBuilder());
        sourceQuery.setSelectionCriteria(exp);
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the relation row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    @Override
    protected Object extractKeyFromTargetRow(AbstractRecord row, AbstractSession session) {
        return this.mechanism.extractKeyFromTargetRow(row, session);
    }

    /**
     * INTERNAL:
     * Extract the primary key value from the source row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    @Override
    protected Object extractBatchKeyFromRow(AbstractRecord row, AbstractSession session) {
        return this.mechanism.extractBatchKeyFromRow(row, session);
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    @Override
    protected Expression buildBatchCriteria(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        return this.mechanism.buildBatchCriteria(builder, query);
    }

    /**
     * INTERNAL:
     * Add additional fields and check for history.
     */
    @Override
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        super.postPrepareNestedBatchQuery(batchQuery, query);
        ReadAllQuery mappingBatchQuery = (ReadAllQuery)batchQuery;
        this.mechanism.postPrepareNestedBatchQuery(batchQuery, query);
        if (this.historyPolicy != null) {
            ExpressionBuilder builder = mappingBatchQuery.getExpressionBuilder();
            Expression twisted = batchQuery.getSelectionCriteria();
            if (query.getSession().getAsOfClause() != null) {
                builder.asOf(query.getSession().getAsOfClause());
            } else if (builder.getAsOfClause() == null) {
                builder.asOf(AsOfClause.NO_CLAUSE);
            }
            twisted = twisted.and(this.historyPolicy.additionalHistoryExpression(builder, builder));
            mappingBatchQuery.setSelectionCriteria(twisted);
        }
    }
    
    /**
     * INTERNAL:
     * Return the base expression to use for adding fields to the query.
     * Normally this is the query's builder, but may be the join table for m-m.
     */
    @Override
    protected Expression getAdditionalFieldsBaseExpression(ReadQuery query) {
        return ((ReadAllQuery)query).getExpressionBuilder().getTable(getRelationTable());
    }
    

    protected DataModifyQuery getDeleteQuery() {
        return this.mechanism.getDeleteQuery();
    }

    /**
     * INTERNAL:
     * Should be overridden by subclass that allows setting
     * extendPessimisticLockScope to DEDICATED_QUERY. 
     */
    @Override
    protected ReadQuery getExtendPessimisticLockScopeDedicatedQuery(AbstractSession session, short lockMode) {
        if(this.mechanism != null) {
            return this.mechanism.getLockRelationTableQueryClone(session, lockMode);            
        } else {
            return super.getExtendPessimisticLockScopeDedicatedQuery(session, lockMode);
        }
    }
    
    /**
     * INTERNAL:
     * Return source key fields for translation by an AggregateObjectMapping
     */
    @Override
    public Collection getFieldsForTranslationInAggregate() {
        return getRelationTableMechanism().getSourceKeyFields();
    }
    
    protected DataModifyQuery getInsertQuery() {
        return this.mechanism.getInsertQuery();
    }

    /**
     * INTERNAL:
     * Returns the join criteria stored in the mapping selection query. This criteria
     * is used to read reference objects across the tables from the database.
     */
    @Override
    public Expression getJoinCriteria(ObjectExpression context, Expression base) {
        if (getHistoryPolicy() != null) {
            Expression result = super.getJoinCriteria(context, base);
            Expression historyCriteria = getHistoryPolicy().additionalHistoryExpression(context, base);
            if (result != null) {
                return result.and(historyCriteria);
            } else if (historyCriteria != null) {
                return historyCriteria;
            } else {
                return null;
            }
        } else {
            return super.getJoinCriteria(context, base);
        }
    }

    /**
     * PUBLIC:
     * Allows history tracking on the m-m join table.
     */
    public HistoryPolicy getHistoryPolicy() {
        return historyPolicy;
    }

    /**
     * PUBLIC:
     * Returns RelationTableMechanism that may be owned by the mapping.
     * Note that all RelationTableMechanism methods are accessible
     * through the mapping directly.
     * The only reason this method is provided
     * is to allow a uniform approach to RelationTableMechanism
     * in both ManyToManyMapping and OneToOneMapping
     * that uses RelationTableMechanism.
     */
    public RelationTableMechanism getRelationTableMechanism() {
        return this.mechanism;
    }
    
    /**
     * INTERNAL:
     * Return the relation table associated with the mapping.
     */
    public DatabaseTable getRelationTable() {
        return this.mechanism.getRelationTable();
    }

    /**
     * PUBLIC:
     * Return the relation table name associated with the mapping.
     */
    public String getRelationTableName() {
        return this.mechanism.getRelationTableName();
    }

    //CR#2407  This method is added to include table qualifier.

    /**
     * PUBLIC:
     * Return the relation table qualified name associated with the mapping.
     */
    public String getRelationTableQualifiedName() {
        return this.mechanism.getRelationTableQualifiedName();
    }

    /**
     * PUBLIC:
     * Return the source key field names associated with the mapping.
     * These are in-order with the sourceRelationKeyFieldNames.
     */
    public Vector getSourceKeyFieldNames() {
        return this.mechanism.getSourceKeyFieldNames();
    }

    /**
     * INTERNAL:
     * Return all the source key fields associated with the mapping.
     */
    public Vector<DatabaseField> getSourceKeyFields() {
        return this.mechanism.getSourceKeyFields();
    }

    /**
     * PUBLIC:
     * Return the source relation key field names associated with the mapping.
     * These are in-order with the sourceKeyFieldNames.
     */
    public Vector getSourceRelationKeyFieldNames() {
        return this.mechanism.getSourceRelationKeyFieldNames();
    }

    /**
     * INTERNAL:
     * Return all the source relation key fields associated with the mapping.
     */
    public Vector<DatabaseField> getSourceRelationKeyFields() {
        return this.mechanism.getSourceRelationKeyFields();
    }

    /**
     * PUBLIC:
     * Return the target key field names associated with the mapping.
     * These are in-order with the targetRelationKeyFieldNames.
     */
    public Vector getTargetKeyFieldNames() {
        return this.mechanism.getTargetKeyFieldNames();
    }

    /**
     * INTERNAL:
     * Return all the target keys associated with the mapping.
     */
    public Vector<DatabaseField> getTargetKeyFields() {
        return this.mechanism.getTargetKeyFields();
    }

    /**
     * PUBLIC:
     * Return the target relation key field names associated with the mapping.
     * These are in-order with the targetKeyFieldNames.
     */
    public Vector getTargetRelationKeyFieldNames() {
        return this.mechanism.getTargetRelationKeyFieldNames();
    }

    /**
     * INTERNAL:
     * Return all the target relation key fields associated with the mapping.
     */
    public Vector<DatabaseField> getTargetRelationKeyFields() {
        return this.mechanism.getTargetRelationKeyFields();
    }

    protected boolean hasCustomDeleteQuery() {
        return this.mechanism.hasCustomDeleteQuery();
    }

    protected boolean hasCustomInsertQuery() {
        return this.mechanism.hasCustomInsertQuery();
    }

    /**
     * INTERNAL:
     * The join table is a dependency if not read-only.
     */
    @Override
    public boolean hasDependency() {
        return this.isPrivateOwned || (!this.isReadOnly);
    }

    /**
     * INTERNAL:
     * Initialize mappings
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        if (session.hasBroker()) {
            if (getReferenceClass() == null) {
                throw DescriptorException.referenceClassNotSpecified(this);
            }
            // substitute session that owns the mapping for the session that owns reference descriptor.
            session = session.getBroker().getSessionForClass(getReferenceClass());
        }
        
        super.initialize(session);
        getDescriptor().addPreDeleteMapping(this);

        if(this.mechanism != null) {
            this.mechanism.initialize(session, this);
        } else {
            throw DescriptorException.noRelationTableMechanism(this);
        }

        if (shouldInitializeSelectionCriteria()) {
            if (shouldForceInitializationOfSelectionCriteria()) {
                initializeSelectionCriteriaAndAddFieldsToQuery(null);
            } else {
                initializeSelectionCriteriaAndAddFieldsToQuery(getSelectionCriteria());
            }
        }
        if (!getSelectionQuery().hasSessionName()) {
            getSelectionQuery().setSessionName(session.getName());
        }
        
        initializeDeleteAllQuery(session);
        if (getHistoryPolicy() != null) {
            getHistoryPolicy().initialize(session);
        }
        
        if (getReferenceDescriptor() != null && getReferenceDescriptor().hasTablePerClassPolicy()) {
            // This will do nothing if we have already prepared for this 
            // source mapping or if the source mapping does not require
            // any special prepare logic.
            getReferenceDescriptor().getTablePerClassPolicy().prepareChildrenSelectionQuery(this, session);              
        }
    }

    /**
     * INTERNAL:
     * Verifies listOrderField's table: it must be relation table.
     * Precondition: listOrderField != null.
     */
    @Override
    protected void buildListOrderField() {
        if(this.listOrderField.hasTableName()) {
            if(!getRelationTable().equals(this.listOrderField.getTable())) {
                throw DescriptorException.listOrderFieldTableIsWrong(this.getDescriptor(), this, this.listOrderField.getTable(), getRelationTable());
            }
        } else {
            listOrderField.setTable(getRelationTable());
        }
        this.listOrderField = getDescriptor().buildField(this.listOrderField, getRelationTable());
    }
    
    /**
     * INTERNAL:
     * Indicates whether getListOrderFieldExpression method should create field expression on table expression.  
     */
    @Override
    public boolean shouldUseListOrderFieldTableExpression() {
        return true;
    }
        
    /**
     * INTERNAL:
     * Initialize changeOrderTargetQuery.
     */
    protected void initializeChangeOrderTargetQuery(AbstractSession session) {
        boolean hasChangeOrderTargetQuery = changeOrderTargetQuery != null;
        if(!hasChangeOrderTargetQuery) {
            changeOrderTargetQuery = new DataModifyQuery();
        }
        
        changeOrderTargetQuery = new DataModifyQuery();
        if (!changeOrderTargetQuery.hasSessionName()) {
            changeOrderTargetQuery.setSessionName(session.getName());
        }
        if (hasChangeOrderTargetQuery) {
            return;
        }

        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        List<DatabaseField> sourceRelationKeyFields = getSourceRelationKeyFields();
        int size = sourceRelationKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField sourceRelationKeyField = sourceRelationKeyFields.get(index);
            Expression expression = builder.getField(sourceRelationKeyField).equal(builder.getParameter(sourceRelationKeyField));
            whereClause = expression.and(whereClause);
        }

        List<DatabaseField> targetRelationKeyFields = getTargetRelationKeyFields();
        size = targetRelationKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetRelationKeyField = targetRelationKeyFields.get(index);
            Expression expression = builder.getField(targetRelationKeyField).equal(builder.getParameter(targetRelationKeyField));
            whereClause = expression.and(whereClause);
        }

        AbstractRecord modifyRow = new DatabaseRecord();
        modifyRow.add(listOrderField, null);

        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(listOrderField.getTable());
        statement.setWhereClause(whereClause);
        statement.setModifyRow(modifyRow);
        changeOrderTargetQuery.setSQLStatement(statement);
    }
    
    /**
     * Initialize delete all query. This query is used to all relevant rows from the
     * relation table.
     */
    protected void initializeDeleteAllQuery(AbstractSession session) {
        if (!getDeleteAllQuery().hasSessionName()) {
            getDeleteAllQuery().setSessionName(session.getName());
        }
        getDeleteAllQuery().setName(getAttributeName());
        if (getDeleteAllQuery().getPartitioningPolicy() == null) {
            getDeleteAllQuery().setPartitioningPolicy(getPartitioningPolicy());
        }
        if (hasCustomDeleteAllQuery()) {
            return;
        }

        Expression expression = null;
        Expression subExpression;
        Expression builder = new ExpressionBuilder();
        SQLDeleteStatement statement = new SQLDeleteStatement();

        // Construct an expression to delete from the relation table.
        for (int index = 0; index < getSourceRelationKeyFields().size(); index++) {
            DatabaseField sourceRelationKey = getSourceRelationKeyFields().elementAt(index);
            DatabaseField sourceKey = getSourceKeyFields().elementAt(index);

            subExpression = builder.getField(sourceRelationKey).equal(builder.getParameter(sourceKey));
            expression = subExpression.and(expression);
        }

        // All the entries are deleted in one shot.
        statement.setWhereClause(expression);
        statement.setTable(getRelationTable());
        getDeleteAllQuery().setSQLStatement(statement);
    }

    /**
     * INTERNAL:
     * Initializes listOrderField's table. 
     * Precondition: listOrderField != null.
     */
    @Override
    protected void initializeListOrderFieldTable(AbstractSession session) {
        this.mechanism.initializeRelationTable(session, this);
    }
    
    /**
     * INTERNAL:
     * Selection criteria is created to read target records from the table.
     */
    protected void initializeSelectionCriteriaAndAddFieldsToQuery(Expression startCriteria) {
        setSelectionCriteria(this.mechanism.buildSelectionCriteriaAndAddFieldsToQuery(this, startCriteria));
    }    

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it.
     */
    protected void insertAddedObjectEntry(ObjectLevelModifyQuery query, Object objectAdded, Map extraData) throws DatabaseException, OptimisticLockException {
        //cr 3819 added the line below to fix the translationtable to ensure that it
        // contains the required values
        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), query.getSession());
        AbstractRecord databaseRow = this.mechanism.buildRelationTableSourceAndTargetRow(query.getTranslationRow(), containerPolicy.unwrapIteratorResult(objectAdded), query.getSession(), this);
        ContainerPolicy.copyMapDataToRow(getContainerPolicy().getKeyMappingDataForWriteQuery(objectAdded, query.getSession()), databaseRow);
        
        if(listOrderField != null && extraData != null) {
            databaseRow.put(listOrderField, extraData.get(listOrderField));
        }
        
        query.getExecutionSession().executeQuery(this.mechanism.getInsertQuery(), databaseRow);
        if ((getHistoryPolicy() != null) && getHistoryPolicy().shouldHandleWrites()) {
            getHistoryPolicy().mappingLogicalInsert(this.mechanism.getInsertQuery(), databaseRow, query.getSession());
        }
    }

    /**
     * INTERNAL:
     * Insert into relation table. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct a insert statement with above fields and values for relation table.
     * <p>- execute the statement.
     * <p>- Repeat above three statements until all the target objects are done.
     */
    public void insertIntoRelationTable(WriteObjectQuery query) throws DatabaseException {
        if (isReadOnly()) {
            return;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        if (cp.isEmpty(objects)) {
            return;
        }

        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), query.getSession());
        AbstractRecord databaseRow = this.mechanism.buildRelationTableSourceRow(query.getTranslationRow());

        int orderIndex = 0;
        // Extract target field and its value. Construct insert statement and execute it
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            databaseRow = this.mechanism.addRelationTableTargetRow(object, query.getExecutionSession(), databaseRow, this);

            ContainerPolicy.copyMapDataToRow(cp.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession()), databaseRow);
            
            if(listOrderField != null) {
                databaseRow.put(listOrderField, orderIndex++);
            }

            query.getExecutionSession().executeQuery(this.mechanism.getInsertQuery(), databaseRow);
            if ((getHistoryPolicy() != null) && getHistoryPolicy().shouldHandleWrites()) {
                getHistoryPolicy().mappingLogicalInsert(this.mechanism.getInsertQuery(), databaseRow, query.getSession());
            }
        }
    }

    /**
     * INTERNAL:
     * Write the target objects if the cascade policy requires them to be written first.
     * They must be written within a unit of work to ensure that they exist.
     */
    public void insertTargetObjects(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // Only cascade dependents writes in uow.
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        ContainerPolicy cp = getContainerPolicy();
        if (cp.isEmpty(objects)) {
            return;
        }

        // Write each of the target objects
        for (Object objectsIterator = cp.iteratorFor(objects); cp.hasNext(objectsIterator);) {
            Object wrappedObject = cp.next(objectsIterator, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            if (isPrivateOwned()) {
                // no need to set changeset as insert is a straight copy anyway
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setIsExecutionClone(true);
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                ObjectChangeSet changeSet = null;
                UnitOfWorkChangeSet uowChangeSet = null;
                if (query.getSession().isUnitOfWork() && (((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet() != null)) {
                    uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
                    changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(object);
                }
                WriteObjectQuery writeQuery = new WriteObjectQuery();
                writeQuery.setIsExecutionClone(true);
                writeQuery.setObject(object);
                writeQuery.setObjectChangeSet(changeSet);
                writeQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(writeQuery);
            }
            cp.propogatePostInsert(query, wrappedObject);
        }
    }

    /**
     * INTERNAL:
     * Return whether this mapping was originally defined as a OneToMany.
     */
    public boolean isDefinedAsOneToManyMapping() {
        return isDefinedAsOneToManyMapping;
    }
    
    /**
     * INTERNAL:
     * Return if this mapping support joining.
     */
    @Override
    public boolean isJoiningSupported() {
        return true;
    }

    @Override
    public boolean isManyToManyMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Ensure the container policy is post initialized
     */
    @Override
    public void postInitialize(AbstractSession session) {
        super.postInitialize(session);
        this.mustDeleteReferenceObjectsOneByOne = true;
    }

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it if private.
     */
    @Override
    protected void objectAddedDuringUpdate(ObjectLevelModifyQuery query, Object objectAdded, ObjectChangeSet changeSet, Map extraData) throws DatabaseException, OptimisticLockException {
        // First insert/update object.
        super.objectAddedDuringUpdate(query, objectAdded, changeSet, extraData);

        // In the uow data queries are cached until the end of the commit.
        if (query.shouldCascadeOnlyDependentParts()) {
            // Hey I might actually want to use an inner class here... ok array for now.
            Object[] event = new Object[4];
            event[0] = ObjectAdded;
            event[1] = query;
            event[2] = objectAdded;
            event[3] = extraData;
            query.getSession().getCommitManager().addDataModificationEvent(this, event);
        } else {
            insertAddedObjectEntry(query, objectAdded, extraData);
        }
    }

    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    @Override
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted, Map extraData) throws DatabaseException, OptimisticLockException {
        Object unwrappedObjectDeleted = getContainerPolicy().unwrapIteratorResult(objectDeleted);
        AbstractRecord databaseRow = this.mechanism.buildRelationTableSourceAndTargetRow(query.getTranslationRow(), unwrappedObjectDeleted, query.getSession(), this);

        // In the uow data queries are cached until the end of the commit.
        if (query.shouldCascadeOnlyDependentParts()) {
            // Hey I might actually want to use an inner class here... ok array for now.
            Object[] event = new Object[3];
            event[0] = ObjectRemoved;
            event[1] = this.mechanism.getDeleteQuery();
            event[2] = databaseRow;
            query.getSession().getCommitManager().addDataModificationEvent(this, event);
        } else {
            query.getSession().executeQuery(this.mechanism.getDeleteQuery(), databaseRow);
            if ((getHistoryPolicy() != null) && getHistoryPolicy().shouldHandleWrites()) {
                getHistoryPolicy().mappingLogicalDelete(this.mechanism.getDeleteQuery(), databaseRow, query.getSession());
            }
        }

        // Delete object after join entry is delete if private.
        super.objectRemovedDuringUpdate(query, objectDeleted, extraData);
    }

    @Override
    protected void objectOrderChangedDuringUpdate(WriteObjectQuery query, Object orderChangedObject, int orderIndex) {
        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), query.getSession());
        AbstractRecord databaseRow = this.mechanism.buildRelationTableSourceAndTargetRow(query.getTranslationRow(), orderChangedObject, query.getSession(), this);
        databaseRow.put(listOrderField, orderIndex);
  
        query.getSession().executeQuery(changeOrderTargetQuery, databaseRow);
    }

    /**
     * INTERNAL:
     * Perform the commit event.
     * This is used in the uow to delay data modifications.
     */
    @Override
    public void performDataModificationEvent(Object[] event, AbstractSession session) throws DatabaseException, DescriptorException {
        // Hey I might actually want to use an inner class here... ok array for now.
        if (event[0] == PostInsert) {
            insertIntoRelationTable((WriteObjectQuery)event[1]);
        } else if (event[0] == ObjectRemoved) {
            session.executeQuery((DataModifyQuery)event[1], (AbstractRecord)event[2]);
            if ((getHistoryPolicy() != null) && getHistoryPolicy().shouldHandleWrites()) {
                getHistoryPolicy().mappingLogicalDelete((DataModifyQuery)event[1], (AbstractRecord)event[2], session);
            }
        } else if (event[0] == ObjectAdded) {
            insertAddedObjectEntry((WriteObjectQuery)event[1], event[2], (Map)event[3]);
        } else {
            throw DescriptorException.invalidDataModificationEventCode(event[0], this);
        }
    }

    /**
     * INTERNAL:
     * Insert into relation table. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct a insert statement with above fields and values for relation table.
     * <p>- execute the statement.
     * <p>- Repeat above three statements until all the target objects are done.
     */
    @Override
    public void postInsert(WriteObjectQuery query) throws DatabaseException {
        insertTargetObjects(query);
        // Batch data modification in the uow
        if (query.shouldCascadeOnlyDependentParts()) {
            // Hey I might actually want to use an inner class here... ok array for now.
            Object[] event = new Object[2];
            event[0] = PostInsert;
            event[1] = query;
            query.getSession().getCommitManager().addDataModificationEvent(this, event);
        } else {
            insertIntoRelationTable(query);
        }
    }

    /**
     * INTERNAL:
     * Update the relation table with the entries related to this mapping.
     * Delete entries removed, insert entries added.
     * If private also insert/delete/update target objects.
     */
    @Override
    public void postUpdate(WriteObjectQuery query) throws DatabaseException {
        if (this.isReadOnly) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }
        if (query.getObjectChangeSet() != null) {
            // UnitOfWork
            writeChanges(query.getObjectChangeSet(), query);
        } else {
            // OLD COMMIT
            compareObjectsAndWrite(query);
        }
    }

    /**
     * INTERNAL:
     * Delete entries related to this mapping from the relation table.
     */
    @Override
    public void preDelete(DeleteObjectQuery query) throws DatabaseException {
        AbstractSession session = query.getSession();
        Object objectsIterator = null;
        ContainerPolicy containerPolicy = getContainerPolicy();
        
        if (this.isReadOnly) {
            return;
        }
        Object objects = null;
        
        boolean cascade = shouldObjectModifyCascadeToParts(query);
        if (containerPolicy.propagatesEventsToCollection() || cascade) {
            // if processed during UnitOfWork commit process the private owned delete will occur during change calculation
            objects = getRealCollectionAttributeValueFromObject(query.getObject(), session);
            //this must be done up here because the select must be done before the entry in the relation table is deleted.
            // TODO: Hmm given the below code, the rows are already deleted, so this code is broken.
            // Assuming it was a cascade remove, it will have been instantiated, so may be ok?
            objectsIterator = containerPolicy.iteratorFor(objects);
        }

        // This has already been done in a unit of work.
        if (!session.isUnitOfWork()) {
            earlyPreDelete(query, query.getObject());
        }

        // If privately owned delete the objects, this does not handle removed objects (i.e. verify delete, not req in uow).
        // Does not try to optimize delete all like 1-m, (rarely used and hard to do).
        if (containerPolicy.propagatesEventsToCollection() || cascade) {
            if (objects != null) {
                //objectsIterator will not be null because cascade check will still return true.
                while (containerPolicy.hasNext(objectsIterator)) {
                    Object wrappedObject = containerPolicy.nextEntry(objectsIterator, session);
                    Object object = containerPolicy.unwrapIteratorResult(wrappedObject);
                    if (cascade){
                        // PERF: Avoid query execution if already deleted.
                        if (!session.getCommitManager().isCommitCompletedInPostOrIgnore(object)) {
                            DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                            deleteQuery.setIsExecutionClone(true);
                            deleteQuery.setObject(object);
                            deleteQuery.setCascadePolicy(query.getCascadePolicy());
                            session.executeQuery(deleteQuery);
                        }
                    }
                    containerPolicy.propogatePreDelete(query, wrappedObject);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * The translation row may require additional fields than the primary key if the mapping in not on the primary key.
     */
    @Override
    protected void prepareTranslationRow(AbstractRecord translationRow, Object object, ClassDescriptor descriptor, AbstractSession session) {
        // Make sure that each source key field is in the translation row.
        for (Enumeration sourceFieldsEnum = getSourceKeyFields().elements();
                 sourceFieldsEnum.hasMoreElements();) {
            DatabaseField sourceKey = (DatabaseField)sourceFieldsEnum.nextElement();
            if (!translationRow.containsKey(sourceKey)) {
                Object value = descriptor.getObjectBuilder().extractValueFromObjectForField(object, sourceKey, session);
                translationRow.put(sourceKey, value);
            }
        }
    }

    /**
     * PUBLIC:
     * The default delete query for mapping can be overridden by specifying the new query.
     * This query must delete the row from the M-M join table.
     */
    public void setCustomDeleteQuery(DataModifyQuery query) {
        this.mechanism.setCustomDeleteQuery(query);
    }

    /**
     * PUBLIC:
     * The default insert query for mapping can be overridden by specifying the new query.
     * This query must insert the row into the M-M join table.
     */
    public void setCustomInsertQuery(DataModifyQuery query) {
        this.mechanism.setCustomInsertQuery(query);
    }

    protected void setDeleteQuery(DataModifyQuery deleteQuery) {
        this.mechanism.setDeleteQuery(deleteQuery);
    }

    /**
     * PUBLIC:
     * Set the receiver's delete SQL string. This allows the user to override the SQL
     * generated by TOPLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row, through replacing the field names
     * marked by '#' with the values for those fields.
     * This is used to delete a single entry from the M-M join table.
     * Example, 'delete from PROJ_EMP where PROJ_ID = #PROJ_ID AND EMP_ID = #EMP_ID'.
     */
    public void setDeleteSQLString(String sqlString) {
        this.mechanism.setDeleteSQLString(sqlString);
    }

    /**
     * INTERNAL:
     * Set whether this mapping was originally defined as a OneToMany
     * @param isDefinedAsOneToManyMapping
     */
    public void setDefinedAsOneToManyMapping(boolean isDefinedAsOneToManyMapping) {
        this.isDefinedAsOneToManyMapping = isDefinedAsOneToManyMapping;
    }
    
    /**
     * PUBLIC:
     * Set the receiver's delete Call. This allows the user to override the SQL
     * generated by TOPLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row.
     * This is used to delete a single entry from the M-M join table.
     * Example, 'new SQLCall("delete from PROJ_EMP where PROJ_ID = #PROJ_ID AND EMP_ID = #EMP_ID")'.
     */
    public void setDeleteCall(Call call) {
        this.mechanism.setDeleteCall(call);
    }

    protected void setInsertQuery(DataModifyQuery insertQuery) {
        this.mechanism.setInsertQuery(insertQuery);
    }

    /**
     * PUBLIC:
     * Set the receiver's insert SQL string. This allows the user to override the SQL
     * generated by TOPLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row, through replacing the field names
     * marked by '#' with the values for those fields.
     * This is used to insert an entry into the M-M join table.
     * Example, 'insert into PROJ_EMP (EMP_ID, PROJ_ID) values (#EMP_ID, #PROJ_ID)'.
     */
    public void setInsertSQLString(String sqlString) {
        this.mechanism.setInsertSQLString(sqlString);
    }
    
    /**
     * PUBLIC:
     * Set the receiver's insert Call. This allows the user to override the SQL
     * generated by TOPLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row.
     * This is used to insert an entry into the M-M join table.
     * Example, 'new SQLCall("insert into PROJ_EMP (EMP_ID, PROJ_ID) values (#EMP_ID, #PROJ_ID)")'.
     */
    public void setInsertCall(Call call) {
        this.mechanism.setInsertCall(call);
    }

    /**
     * PUBLIC:
     * Allows to set RelationTableMechanism to be owned by the mapping.
     * It's not necessary to explicitly set the mechanism:
     * one is created by mapping's constructor. 
     * The only reason this method is provided
     * is to allow a uniform approach to RelationTableMechanism
     * in both ManyToManyMapping and OneToOneMapping
     * that uses RelationTableMechanism.
     * ManyToManyMapping must have RelationTableMechanism,
     * never set it to null.
     */
    void setRelationTableMechanism(RelationTableMechanism mechanism) {
        this.mechanism = mechanism;
    }
    
    /**
     * PUBLIC:
     * Set the relational table.
     * This is the join table that store both the source and target primary keys.
     */
    public void setRelationTable(DatabaseTable relationTable) {
        this.mechanism.setRelationTable(relationTable);
    }
    
    /**
     * PUBLIC:
     * Enable history tracking on the m-m join table.
     */
    public void setHistoryPolicy(HistoryPolicy policy) {
        this.historyPolicy = policy;
        if (policy != null) {
            policy.setMapping(this);
        }
    }

    /**
     * PUBLIC:
     * Set the name of the relational table.
     * This is the join table that store both the source and target primary keys.
     */
    public void setRelationTableName(String tableName) {
        this.mechanism.setRelationTableName(tableName);
    }

    /**
     * PUBLIC:
     * Set the name of the session to execute the mapping's queries under.
     * This can be used by the session broker to override the default session
     * to be used for the target class.
     */
    @Override
    public void setSessionName(String name) {
        super.setSessionName(name);
        this.mechanism.setSessionName(name);
    }

    /**
     * PUBLIC:
     * Set the source key field names associated with the mapping.
     * These must be in-order with the sourceRelationKeyFieldNames.
     */
    public void setSourceKeyFieldNames(Vector fieldNames) {
        this.mechanism.setSourceKeyFieldNames(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the source fields.
     */
    public void setSourceKeyFields(Vector<DatabaseField> sourceKeyFields) {
        this.mechanism.setSourceKeyFields(sourceKeyFields);
    }

    /**
     * PUBLIC:
     * Set the source key field in the relation table.
     * This is the name of the foreign key in the relation table to the source's primary key field.
     * This method is used if the source primary key is a singleton only.
     */
    public void setSourceRelationKeyFieldName(String sourceRelationKeyFieldName) {
        this.mechanism.setSourceRelationKeyFieldName(sourceRelationKeyFieldName);
    }

    /**
     * PUBLIC:
     * Set the source relation key field names associated with the mapping.
     * These must be in-order with the sourceKeyFieldNames.
     */
    public void setSourceRelationKeyFieldNames(Vector fieldNames) {
        this.mechanism.setSourceRelationKeyFieldNames(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the source fields.
     */
    public void setSourceRelationKeyFields(Vector<DatabaseField> sourceRelationKeyFields) {
        this.mechanism.setSourceRelationKeyFields(sourceRelationKeyFields);
    }

    /**
     * INTERNAL:
     * Set the target key field names associated with the mapping.
     * These must be in-order with the targetRelationKeyFieldNames.
     */
    public void setTargetKeyFieldNames(Vector fieldNames) {
        this.mechanism.setTargetKeyFieldNames(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the target fields.
     */
    public void setTargetKeyFields(Vector<DatabaseField> targetKeyFields) {
        this.mechanism.setTargetKeyFields(targetKeyFields);
    }

    /**
     * PUBLIC:
     * Set the target key field in the relation table.
     * This is the name of the foreign key in the relation table to the target's primary key field.
     * This method is used if the target's primary key is a singleton only.
     */
    public void setTargetRelationKeyFieldName(String targetRelationKeyFieldName) {
        this.mechanism.setTargetRelationKeyFieldName(targetRelationKeyFieldName);
    }

    /**
     * INTERNAL:
     * Set the target relation key field names associated with the mapping.
     * These must be in-order with the targetKeyFieldNames.
     */
    public void setTargetRelationKeyFieldNames(Vector fieldNames) {
        this.mechanism.setTargetRelationKeyFieldNames(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the target fields.
     */
    public void setTargetRelationKeyFields(Vector<DatabaseField> targetRelationKeyFields) {
        this.mechanism.setTargetRelationKeyFields(targetRelationKeyFields);
    }
    
    /**
     * INTERNAL:
     * Append the temporal selection to the query selection criteria.
     */
    @Override
    protected ReadQuery prepareHistoricalQuery(ReadQuery targetQuery, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) {
        if (getHistoryPolicy() != null) {
            if (targetQuery == getSelectionQuery()) {
                targetQuery = (ObjectLevelReadQuery)targetQuery.clone();
                targetQuery.setIsExecutionClone(true);
            }
            if (targetQuery.getSelectionCriteria() == getSelectionQuery().getSelectionCriteria()) {
                targetQuery.setSelectionCriteria((Expression)targetQuery.getSelectionCriteria().clone());
            }
            if (sourceQuery.getSession().getAsOfClause() != null) {
                ((ObjectLevelReadQuery)targetQuery).setAsOfClause(sourceQuery.getSession().getAsOfClause());
            } else if (((ObjectLevelReadQuery)targetQuery).getAsOfClause() == null) {
                ((ObjectLevelReadQuery)targetQuery).setAsOfClause(AsOfClause.NO_CLAUSE);
            }
            Expression temporalExpression = (this).getHistoryPolicy().additionalHistoryExpression(targetQuery.getSelectionCriteria().getBuilder(), targetQuery.getSelectionCriteria().getBuilder());
            targetQuery.setSelectionCriteria(targetQuery.getSelectionCriteria().and(temporalExpression));
        }
        return targetQuery;
    }
}
