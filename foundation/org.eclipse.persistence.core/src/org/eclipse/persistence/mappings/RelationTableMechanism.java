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
 *     07/16/2009 Andrei Ilitchev 
 *       - Bug 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.TablePerMultitenantPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.ForUpdateClause;
import org.eclipse.persistence.internal.expressions.ForUpdateOfClause;
import org.eclipse.persistence.internal.expressions.SQLDeleteStatement;
import org.eclipse.persistence.internal.expressions.SQLInsertStatement;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping.ExtendPessimisticLockScope;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DirectReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>: Contains relation table functionality
 * that was originally defined in ManyToManyMapping
 * and now is shared with OneToOneMapping. 
 */
public class RelationTableMechanism  implements Cloneable, java.io.Serializable {
    /** The intermediate relation table. */
    protected DatabaseTable relationTable;

    /** The field in the source table that corresponds to the key in the relation table */
    protected Vector<DatabaseField> sourceKeyFields;

    /**  The field in the target table that corresponds to the key in the relation table */
    protected Vector<DatabaseField> targetKeyFields;

    /** The field in the intermediate table that corresponds to the key in the source table */
    protected Vector<DatabaseField> sourceRelationKeyFields;

    /** The field in the intermediate table that corresponds to the key in the target table */
    protected Vector<DatabaseField> targetRelationKeyFields;

    /** Query used for single row deletion. */
    protected DataModifyQuery deleteQuery;
    protected boolean hasCustomDeleteQuery;

    /** Used for insertion. */
    protected DataModifyQuery insertQuery;
    protected boolean hasCustomInsertQuery;
    
    protected ReadQuery lockRelationTableQuery;

    public RelationTableMechanism() {
        this.insertQuery = new DataModifyQuery();
        this.deleteQuery = new DataModifyQuery();
        this.sourceRelationKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.targetRelationKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.sourceKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.targetKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.hasCustomDeleteQuery = false;
        this.hasCustomInsertQuery = false;
    }

    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the source table. This method is used if the keys are composite.
     */
    public void addSourceRelationKeyField(DatabaseField sourceRelationKeyField, DatabaseField sourcePrimaryKeyField) {
        getSourceRelationKeyFields().addElement(sourceRelationKeyField);
        getSourceKeyFields().addElement(sourcePrimaryKeyField);
    }
    
    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the source table. This method is used if the keys are composite.
     */
    public void addSourceRelationKeyFieldName(String sourceRelationKeyFieldName, String sourcePrimaryKeyFieldName) {
        addSourceRelationKeyField(new DatabaseField(sourceRelationKeyFieldName), new DatabaseField(sourcePrimaryKeyFieldName));
    }

    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the target table. This method is used if the keys are composite.
     */
    public void addTargetRelationKeyField(DatabaseField targetRelationKeyField, DatabaseField targetPrimaryKeyField) {
        getTargetRelationKeyFields().addElement(targetRelationKeyField);
        getTargetKeyFields().addElement(targetPrimaryKeyField);
    }
    
    /**
     * PUBLIC:
     * Add the fields in the intermediate table that corresponds to the primary 
     * key in the target table. This method is used if the keys are composite.
     */
    public void addTargetRelationKeyFieldName(String targetRelationKeyFieldName, String targetPrimaryKeyFieldName) {
        addTargetRelationKeyField(new DatabaseField(targetRelationKeyFieldName), new DatabaseField(targetPrimaryKeyFieldName));
    }

    /**
     * INTERNAL:
     * Selection criteria is created to read target records from the table.
     */
    Expression buildSelectionCriteria(ForeignReferenceMapping mapping, Expression criteria) {
        return buildSelectionCriteriaAndAddFieldsToQueryInternal(mapping, criteria, true, false);
    }
    
    Expression buildSelectionCriteriaAndAddFieldsToQuery(ForeignReferenceMapping mapping, Expression criteria) {
        return buildSelectionCriteriaAndAddFieldsToQueryInternal(mapping, criteria, true, true);
    }
    
    /**
     * INTERNAL:
     * Build the selection criteria to join the source, relation, and target tables.
     */
    public Expression buildSelectionCriteriaAndAddFieldsToQueryInternal(ForeignReferenceMapping mapping, Expression criteria, boolean shouldAddTargetFields, boolean shouldAddFieldsToQuery) {
        Expression builder = new ExpressionBuilder();
        Expression linkTable = builder.getTable(this.relationTable);

        if (shouldAddTargetFields) {
            Iterator<DatabaseField> targetKeyIterator = getTargetKeyFields().iterator();
            Iterator<DatabaseField> relationKeyIterator = getTargetRelationKeyFields().iterator();
            while (targetKeyIterator.hasNext()) {
                DatabaseField relationKey = relationKeyIterator.next();
                DatabaseField targetKey = targetKeyIterator.next();    
                Expression expression = builder.getField(targetKey).equal(linkTable.getField(relationKey));    
                if (criteria == null) {
                    criteria = expression;
                } else {
                    criteria = expression.and(criteria);
                }
            }
        }

        Iterator<DatabaseField> relationKeyIterator = getSourceRelationKeyFields().iterator();
        Iterator<DatabaseField> sourceKeyIterator = getSourceKeyFields().iterator();

        while (relationKeyIterator.hasNext()) {
            DatabaseField relationKey = relationKeyIterator.next();
            DatabaseField sourceKey = sourceKeyIterator.next();            
            Expression expression = linkTable.getField(relationKey).equal(builder.getParameter(sourceKey));
            if (criteria == null) {
                criteria = expression;
            } else {
                criteria = expression.and(criteria);
            }
        }
        
        if (shouldAddFieldsToQuery && mapping.isCollectionMapping()) {
            ((CollectionMapping)mapping).getContainerPolicy().addAdditionalFieldsToQuery(mapping.getSelectionQuery(), linkTable);
        }
        
        return criteria;
    }
    
    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    protected void collectQueryParameters(Set<DatabaseField> cacheFields){
        for (DatabaseField field : getSourceKeyFields()) {
            cacheFields.add(field);
        }
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    public Object clone() {
        RelationTableMechanism clone;
        try {
            clone = (RelationTableMechanism)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }

        clone.setTargetKeyFields(cloneFields(getTargetKeyFields()));
        clone.setSourceKeyFields(cloneFields(getSourceKeyFields()));
        clone.setTargetRelationKeyFields(cloneFields(getTargetRelationKeyFields()));
        clone.setSourceRelationKeyFields(cloneFields(getSourceRelationKeyFields()));
        
        clone.setInsertQuery((DataModifyQuery) insertQuery.clone());
        clone.setDeleteQuery((DataModifyQuery) deleteQuery.clone());
        if(lockRelationTableQuery != null) {
            clone.lockRelationTableQuery = (DirectReadQuery)lockRelationTableQuery.clone();
        }

        return clone;
    }

    /**
     * INTERNAL:
     * Helper method to clone vector of fields (used in aggregate initialization cloning).
     */
    protected Vector cloneFields(Vector fields) {
        Vector clonedFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        for (Enumeration fieldsEnum = fields.elements(); fieldsEnum.hasMoreElements();) {
            clonedFields.addElement(((DatabaseField)fieldsEnum.nextElement()).clone());
        }

        return clonedFields;
    }

    protected DataModifyQuery getDeleteQuery() {
        return deleteQuery;
    }

    /**
     * INTERNAL:
     * Returns a query that
     */
    ReadQuery getLockRelationTableQueryClone(AbstractSession session, short lockMode) {
        DirectReadQuery lockRelationTableQueryClone = (DirectReadQuery)lockRelationTableQuery.clone(); 
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.addTable(this.relationTable);
        statement.addField(this.sourceRelationKeyFields.get(0).clone());
        statement.setWhereClause((Expression)lockRelationTableQuery.getSelectionCriteria().clone());
        statement.setLockingClause(new ForUpdateClause(lockMode));
        statement.normalize(session, null);
        lockRelationTableQueryClone.setSQLStatement(statement);
        lockRelationTableQueryClone.setIsExecutionClone(true);
        return lockRelationTableQueryClone;
    }
    
    /**
     * INTERNAL:
     * Return relation table locking clause.
     */
    public void setRelationTableLockingClause(ObjectLevelReadQuery targetQuery, ObjectBuildingQuery sourceQuery) {
        ForUpdateOfClause lockingClause = new ForUpdateOfClause();
        lockingClause.setLockMode(sourceQuery.getLockMode());
        FieldExpression exp = (FieldExpression)targetQuery.getExpressionBuilder().getTable(this.relationTable).getField(this.sourceRelationKeyFields.get(0));
        lockingClause.addLockedExpression(exp);
        targetQuery.setLockingClause(lockingClause);
        // locking clause is not compatible with DISTINCT 
        targetQuery.setShouldOuterJoinSubclasses(true);
    }
    
    protected DataModifyQuery getInsertQuery() {
        return insertQuery;
    }

    /**
     * INTERNAL:
     * Return the relation table associated with the mapping.
     */
    public DatabaseTable getRelationTable() {
        return relationTable;
    }

    /**
     * PUBLIC:
     * Return the relation table name associated with the mapping.
     */
    public String getRelationTableName() {
        if (relationTable == null) {
            return null;
        }
        return relationTable.getName();
    }

    /**
     * PUBLIC:
     * Return the relation table qualified name associated with the mapping.
     */
    public String getRelationTableQualifiedName() {
        if (relationTable == null) {
            return null;
        }
        return relationTable.getQualifiedName();
    }

    /**
     * PUBLIC:
     * Return the source key field names associated with the mapping.
     * These are in-order with the sourceRelationKeyFieldNames.
     */
    public Vector getSourceKeyFieldNames() {
        Vector fieldNames = new Vector(getSourceKeyFields().size());
        for (Enumeration fieldsEnum = getSourceKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    protected Expression buildBatchCriteria(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        Expression linkTable = builder.getTable(this.relationTable);
        Expression criteria = null;
        int size = this.targetRelationKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField relationKey = this.targetRelationKeyFields.get(index);
            DatabaseField targetKey = this.targetKeyFields.get(index);
            criteria = builder.getField(targetKey).equal(linkTable.getField(relationKey)).and(criteria);
        }
        size = this.sourceRelationKeyFields.size();
        if (size > 1) {
            // Support composite keys using nested IN.
            List<Expression> fields = new ArrayList<Expression>(size);
            for (DatabaseField sourceRelationKeyField : this.sourceRelationKeyFields) {
                fields.add(linkTable.getField(sourceRelationKeyField));
            }            
            return criteria.and(query.getSession().getPlatform().buildBatchCriteriaForComplexId(builder, fields));
        } else {
            return criteria.and(query.getSession().getPlatform().buildBatchCriteria(builder, linkTable.getField(this.sourceRelationKeyFields.get(0))));
        }
    }

    /**
     * INTERNAL:
     * Add the addition join fields to the batch query.
     */
    public void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        ReadAllQuery mappingBatchQuery = (ReadAllQuery)batchQuery;
        mappingBatchQuery.setShouldIncludeData(true);
        Expression linkTable = mappingBatchQuery.getExpressionBuilder().getTable(this.relationTable);
        for (DatabaseField relationField : this.sourceRelationKeyFields) {
            mappingBatchQuery.getAdditionalFields().add(linkTable.getField(relationField));
        }
    }

    /**
     * INTERNAL:
     * Extract the foreign key value from the source row.
     */
    protected Object extractBatchKeyFromRow(AbstractRecord row, AbstractSession session) {
        Object[] key;
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();
        List<DatabaseField> sourceKeyFields = this.sourceKeyFields;
        int size = sourceKeyFields.size();
        key = new Object[size];
        for (int index = 0; index < size; index++) {                
            DatabaseField field = sourceKeyFields.get(index);
            Object value = row.get(field);                
            // Must ensure the classification gets a cache hit.
            key[index] = conversionManager.convertObject(value, field.getType());
        }
        return new CacheId(key);
    }
    
    /**
     * INTERNAL:
     * Extract the source primary key value from the relation row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Object extractKeyFromTargetRow(AbstractRecord row, AbstractSession session) {
        int size = getSourceRelationKeyFields().size();
        Object[] key = new Object[size];
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();
        for (int index = 0; index < size; index++) {
            DatabaseField relationField = this.sourceRelationKeyFields.get(index);
            DatabaseField sourceField = this.sourceKeyFields.get(index);
            Object value = row.get(relationField);
            // Must ensure the classification gets a cache hit.
            value = conversionManager.convertObject(value, sourceField.getType());
            key[index] = value;
        }
        return new CacheId(key);
    }
    
    /**
     * INTERNAL:
     * Return all the source key fields associated with the mapping.
     */
    public Vector<DatabaseField> getSourceKeyFields() {
        return sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Return the source relation key field names associated with the mapping.
     * These are in-order with the sourceKeyFieldNames.
     */
    public Vector getSourceRelationKeyFieldNames() {
        Vector fieldNames = new Vector(getSourceRelationKeyFields().size());
        for (Enumeration fieldsEnum = getSourceRelationKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return all the source relation key fields associated with the mapping.
     */
    public Vector<DatabaseField> getSourceRelationKeyFields() {
        return sourceRelationKeyFields;
    }

    /**
     * PUBLIC:
     * Return the target key field names associated with the mapping.
     * These are in-order with the targetRelationKeyFieldNames.
     */
    public Vector getTargetKeyFieldNames() {
        Vector fieldNames = new Vector(getTargetKeyFields().size());
        for (Enumeration fieldsEnum = getTargetKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return the relation field for the target field.
     */
    public DatabaseField getRelationFieldForTargetField(DatabaseField targetField) {
        int index = this.targetKeyFields.indexOf(targetField);
        if (index == -1) {
            return null;
        }
        return this.targetRelationKeyFields.get(index);
    }

    /**
     * INTERNAL:
     * Return all the target keys associated with the mapping.
     */
    public Vector<DatabaseField> getTargetKeyFields() {
        return targetKeyFields;
    }

    /**
     * PUBLIC:
     * Return the target relation key field names associated with the mapping.
     * These are in-order with the targetKeyFieldNames.
     */
    public Vector getTargetRelationKeyFieldNames() {
        Vector fieldNames = new Vector(getTargetRelationKeyFields().size());
        for (Enumeration fieldsEnum = getTargetRelationKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return all the target relation key fields associated with the mapping.
     */
    public Vector<DatabaseField> getTargetRelationKeyFields() {
        return targetRelationKeyFields;
    }

    protected boolean hasCustomDeleteQuery() {
        return hasCustomDeleteQuery;
    }

    protected boolean hasCustomInsertQuery() {
        return hasCustomInsertQuery;
    }
    
    /**
     * INTERNAL:
     * Indicates whether the mechanism has relation table.
     */
    public boolean hasRelationTable() {
        return relationTable != null && relationTable.getName().length() > 0;
    }
    
    /**
     * INTERNAL:
     * Initialize
     */
    public void initialize(AbstractSession session, ForeignReferenceMapping mapping) throws DescriptorException {
        initializeRelationTable(session, mapping);
        initializeSourceRelationKeys(mapping);
        initializeTargetRelationKeys(mapping);

        if (isSingleSourceRelationKeySpecified()) {
            initializeSourceKeysWithDefaults(mapping);
        } else {
            initializeSourceKeys(mapping);
        }

        if (isSingleTargetRelationKeySpecified()) {
            initializeTargetKeysWithDefaults(session, mapping);
        } else {
            initializeTargetKeys(session, mapping);
        }
      
        if (getRelationTable().getName().indexOf(' ') != -1) {
            //table names contains a space so needs to be quoted.
            String beginQuote = ((DatasourcePlatform)session.getDatasourcePlatform()).getStartDelimiter();
            String endQuote = ((DatasourcePlatform)session.getDatasourcePlatform()).getEndDelimiter();
            //Ensure this table name hasn't already been quoted.
            if (getRelationTable().getName().indexOf(beginQuote) == -1) {
                getRelationTable().setName(beginQuote + getRelationTable().getName() + endQuote);
            }
        }
        
        if (mapping.isCollectionMapping()) {
            ((CollectionMapping)mapping).getContainerPolicy().initialize(session, getRelationTable());
        }
        
        initializeInsertQuery(session, mapping);
        initializeDeleteQuery(session, mapping);
        
        if (mapping.extendPessimisticLockScope != ExtendPessimisticLockScope.NONE) {
            initializeExtendPessipisticLockScope(session, mapping);
        }
    }

    /**
     * INTERNAL:
     * Initialize delete query. This query is used to delete a specific row from the join table in uow,
     * given the objects on both sides of the relation.
     */
    protected void initializeDeleteQuery(AbstractSession session, ForeignReferenceMapping mapping) {
        if (!getDeleteQuery().hasSessionName()) {
            getDeleteQuery().setSessionName(session.getName());
        }
        if (getDeleteQuery().getPartitioningPolicy() == null) {
            getDeleteQuery().setPartitioningPolicy(mapping.getPartitioningPolicy());
        }
        getInsertQuery().setName(mapping.getAttributeName());
        if (hasCustomDeleteQuery()) {
            return;
        }

        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        for (DatabaseField relationKey : getSourceRelationKeyFields()) {
            Expression expression = builder.getField(relationKey).equal(builder.getParameter(relationKey));
            whereClause = expression.and(whereClause);
        }

        if (mapping.isCollectionMapping()) {
            for (DatabaseField relationKey : getTargetRelationKeyFields()) {    
                Expression expression = builder.getField(relationKey).equal(builder.getParameter(relationKey));
                whereClause = expression.and(whereClause);
            }
        }

        SQLDeleteStatement statement = new SQLDeleteStatement();
        statement.setTable(getRelationTable());
        statement.setWhereClause(whereClause);
        getDeleteQuery().setSQLStatement(statement);
    }

    /**
     * INTERNAL:
     * Initialize extendPessimisticLockeScope and lockRelationTableQuery (if required).
     */
    protected void initializeExtendPessipisticLockScope(AbstractSession session, ForeignReferenceMapping mapping) {
        if(mapping.usesIndirection()) {
            if(session.getPlatform().isForUpdateCompatibleWithDistinct() && session.getPlatform().supportsLockingQueriesWithMultipleTables()) {
                mapping.extendPessimisticLockScope = ExtendPessimisticLockScope.SOURCE_QUERY; 
            } else {
                mapping.extendPessimisticLockScope = ExtendPessimisticLockScope.DEDICATED_QUERY;                     
            }
        } else {
            if(session.getPlatform().supportsIndividualTableLocking() && session.getPlatform().supportsLockingQueriesWithMultipleTables()) {
                mapping.extendPessimisticLockScope = ExtendPessimisticLockScope.TARGET_QUERY; 
            } else {
                mapping.extendPessimisticLockScope = ExtendPessimisticLockScope.DEDICATED_QUERY;                     
            }
        }
        
        if(mapping.extendPessimisticLockScope == ExtendPessimisticLockScope.DEDICATED_QUERY) {
            Expression startCriteria = mapping.getSelectionQuery().getSelectionCriteria();
            if(startCriteria != null) {
                startCriteria = (Expression)startCriteria.clone();
            }
            initializeLockRelationTableQuery(session, mapping, startCriteria);
        }
    }
    
    /**
     * INTERNAL:
     * Initialize insert query. This query is used to insert the collection of objects into the
     * relation table.
     */
    protected void initializeInsertQuery(AbstractSession session, ForeignReferenceMapping mapping) {
        if (!getInsertQuery().hasSessionName()) {
            getInsertQuery().setSessionName(session.getName());
        }
        if (getInsertQuery().getPartitioningPolicy() == null) {
            getInsertQuery().setPartitioningPolicy(mapping.getPartitioningPolicy());
        }
        getInsertQuery().setName(mapping.getAttributeName());
        if (hasCustomInsertQuery()) {
            return;
        }

        SQLInsertStatement statement = new SQLInsertStatement();
        statement.setTable(getRelationTable());
        AbstractRecord joinRow = new DatabaseRecord();
        for (DatabaseField field : getTargetRelationKeyFields()) {
            joinRow.put(field, null);
        }
        for (DatabaseField field : getSourceRelationKeyFields()) {
            joinRow.put(field, null);
        }
        if (mapping.isCollectionMapping()) {
            CollectionMapping collectionMapping = (CollectionMapping)mapping;
            if (collectionMapping.getListOrderField() != null) {
                joinRow.put(collectionMapping.getListOrderField(), null);
            }
            collectionMapping.getContainerPolicy().addFieldsForMapKey(joinRow);
        }
        statement.setModifyRow(joinRow);
        getInsertQuery().setSQLStatement(statement);
        getInsertQuery().setModifyRow(joinRow);
    }

    /**
     * INTERNAL:
     * Initialize lockRelationTableQuery.
     */
    protected void initializeLockRelationTableQuery(AbstractSession session, ForeignReferenceMapping mapping, Expression startCriteria) {
        lockRelationTableQuery = new DirectReadQuery();
        Expression criteria = buildSelectionCriteriaAndAddFieldsToQueryInternal(mapping, startCriteria, false, false);
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.addTable(this.relationTable);
        statement.addField(this.sourceRelationKeyFields.get(0).clone());
        statement.setWhereClause(criteria);
        statement.normalize(session, null);
        lockRelationTableQuery.setSQLStatement(statement);
        lockRelationTableQuery.setSessionName(session.getName());
    }
    
    /**
     * INTERNAL:
     * Set the table qualifier on the relation table if required
     */
    protected void initializeRelationTable(AbstractSession session, ForeignReferenceMapping mapping) throws DescriptorException {
        Platform platform = session.getDatasourcePlatform();

        // We need to look up the relation table name from the reference
        // descriptor if we are the non owning side of a bidirectional mapping
        // to a table per tenant descriptor.
        if (mapping.isReadOnly() && mapping.getReferenceDescriptor().hasTablePerMultitenantPolicy()) {
            setRelationTable(((TablePerMultitenantPolicy) mapping.getReferenceDescriptor().getMultitenantPolicy()).getTable(getRelationTable()));
        }
        
        if (!hasRelationTable()) {
            throw DescriptorException.noRelationTable(mapping);
        }

        if (platform.getTableQualifier().length() > 0) {
            if (getRelationTable().getTableQualifier().length() == 0) {
                getRelationTable().setTableQualifier(platform.getTableQualifier());
            }
        }
    }
    /**
     * INTERNAL:
     * All the source key field names are converted to DatabaseField and stored.
     */
    protected void initializeSourceKeys(ForeignReferenceMapping mapping) {
        for (int index = 0; index < getSourceKeyFields().size(); index++) {
            DatabaseField field = mapping.getDescriptor().buildField(getSourceKeyFields().get(index));
            if (((ForeignReferenceMapping)mapping).usesIndirection()) {
                field.setKeepInRow(true);
            }
            getSourceKeyFields().set(index, field);
        }
    }

    /**
     * INTERNAL:
     * If a user does not specify the source key then the primary keys of the source table are used.
     */
    protected void initializeSourceKeysWithDefaults(DatabaseMapping mapping) {
        List<DatabaseField> primaryKeyFields = mapping.getDescriptor().getPrimaryKeyFields();
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField field = primaryKeyFields.get(index);
            if (((ForeignReferenceMapping)mapping).usesIndirection()) {
                field.setKeepInRow(true);
            }
            getSourceKeyFields().addElement(field);
        }
    }

    /**
     * INTERNAL:
     * All the source relation key field names are converted to DatabaseField and stored.
     */
    protected void initializeSourceRelationKeys(ForeignReferenceMapping mapping) throws DescriptorException {
        if (getSourceRelationKeyFields().size() == 0) {
            throw DescriptorException.noSourceRelationKeysSpecified(mapping);
        }

        for (Enumeration entry = getSourceRelationKeyFields().elements(); entry.hasMoreElements();) {
            DatabaseField field = (DatabaseField)entry.nextElement();
            
            // Update the fields table first if the mapping is from a table per tenant entity.
            ClassDescriptor sourceDescriptor = mapping.getDescriptor();
            if (sourceDescriptor.hasTablePerMultitenantPolicy()) {
                field.setTable(((TablePerMultitenantPolicy) sourceDescriptor.getMultitenantPolicy()).getTable(field.getTable()));
            }
            
            if (field.hasTableName() && (!(field.getTableName().equals(getRelationTable().getName())))) {
                throw DescriptorException.relationKeyFieldNotProperlySpecified(field, mapping);
            }
            field.setTable(getRelationTable());
        }
    }

    /**
     * INTERNAL:
     * All the target key field names are converted to DatabaseField and stored.
     */
    protected void initializeTargetKeys(AbstractSession session, ForeignReferenceMapping mapping) {
        for (int index = 0; index < getTargetKeyFields().size(); index++) {
            DatabaseField field = mapping.getReferenceDescriptor().buildField(getTargetKeyFields().get(index));
            getTargetKeyFields().set(index, field);
        }
    }

    /**
     * INTERNAL:
     * If a user does not specify the target key then the primary keys of the target table are used.
     */
    protected void initializeTargetKeysWithDefaults(AbstractSession session, ForeignReferenceMapping mapping) {
        List<DatabaseField> primaryKeyFields = mapping.getReferenceDescriptor().getPrimaryKeyFields();
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            getTargetKeyFields().addElement(primaryKeyFields.get(index));
        }
    }

    /**
     * INTERNAL:
     * All the target relation key field names are converted to DatabaseField and stored.
     */
    protected void initializeTargetRelationKeys(ForeignReferenceMapping mapping) {
        if (getTargetRelationKeyFields().size() == 0) {
            throw DescriptorException.noTargetRelationKeysSpecified(mapping);
        }

        for (Enumeration targetEnum = getTargetRelationKeyFields().elements(); targetEnum.hasMoreElements();) {
            DatabaseField field = (DatabaseField)targetEnum.nextElement();
            
            // Update the fields table first if the mapping is from a table per tenant entity.
            ClassDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
            if (referenceDescriptor.hasTablePerMultitenantPolicy()) {
                field.setTable(((TablePerMultitenantPolicy) referenceDescriptor.getMultitenantPolicy()).getTable(field.getTable()));
            }
            
            if (field.hasTableName() && (!(field.getTableName().equals(getRelationTable().getName())))) {
                throw DescriptorException.relationKeyFieldNotProperlySpecified(field, mapping);
            }
            field.setTable(getRelationTable());
        }
    }

    /**
     * INTERNAL:
     * Checks if a single source key was specified.
     */
    protected boolean isSingleSourceRelationKeySpecified() {
        return getSourceKeyFields().isEmpty();
    }

    /**
     * INTERNAL:
     * Checks if a single target key was specified.
     */
    protected boolean isSingleTargetRelationKeySpecified() {
        return getTargetKeyFields().isEmpty();
    }

    /**
     * INTERNAL:
     * Adds to the passed expression a single relation table field joined to source field.
     * Used to extend pessimistic locking clause in source query. 
     */
    public Expression joinRelationTableField(Expression expression, Expression baseExpression) {
        return baseExpression.getField(this.sourceKeyFields.get(0)).equal(baseExpression.getTable(relationTable).getField(this.sourceRelationKeyFields.get(0))).and(expression);
    }
            
    /**
     * PUBLIC:
     * The default delete query for mapping can be overridden by specifying the new query.
     * This query must delete the row from the M-M join table.
     */
    public void setCustomDeleteQuery(DataModifyQuery query) {
        setDeleteQuery(query);
        setHasCustomDeleteQuery(true);
    }

    /**
     * PUBLIC:
     * The default insert query for mapping can be overridden by specifying the new query.
     * This query must insert the row into the M-M join table.
     */
    public void setCustomInsertQuery(DataModifyQuery query) {
        setInsertQuery(query);
        setHasCustomInsertQuery(true);
    }

    protected void setDeleteQuery(DataModifyQuery deleteQuery) {
        this.deleteQuery = deleteQuery;
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
        DataModifyQuery query = new DataModifyQuery();
        query.setSQLString(sqlString);
        setCustomDeleteQuery(query);
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
        DataModifyQuery query = new DataModifyQuery();
        query.setCall(call);
        setCustomDeleteQuery(query);
    }

    protected void setHasCustomDeleteQuery(boolean hasCustomDeleteQuery) {
        this.hasCustomDeleteQuery = hasCustomDeleteQuery;
    }

    protected void setHasCustomInsertQuery(boolean bool) {
        hasCustomInsertQuery = bool;
    }

    protected void setInsertQuery(DataModifyQuery insertQuery) {
        this.insertQuery = insertQuery;
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
        DataModifyQuery query = new DataModifyQuery();
        query.setSQLString(sqlString);
        setCustomInsertQuery(query);
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
        DataModifyQuery query = new DataModifyQuery();
        query.setCall(call);
        setCustomInsertQuery(query);
    }

    /**
     * PUBLIC:
     * Set the relational table.
     * This is the join table that store both the source and target primary keys.
     */
    public void setRelationTable(DatabaseTable relationTable) {
        this.relationTable = relationTable;
    }
    
    /**
     * PUBLIC:
     * Set the name of the relational table.
     * This is the join table that store both the source and target primary keys.
     */
    public void setRelationTableName(String tableName) {
        relationTable = new DatabaseTable(tableName);
    }

    /**
     * PUBLIC:
     * Set the name of the session to execute the mapping's queries under.
     * This can be used by the session broker to override the default session
     * to be used for the target class.
     */
    public void setSessionName(String name) {
        getInsertQuery().setSessionName(name);
        getDeleteQuery().setSessionName(name);
    }

    /**
     * PUBLIC:
     * Set the source key field names associated with the mapping.
     * These must be in-order with the sourceRelationKeyFieldNames.
     */
    public void setSourceKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setSourceKeyFields(fields);
    }

    /**
     * INTERNAL:
     * Set the source fields.
     */
    public void setSourceKeyFields(Vector<DatabaseField> sourceKeyFields) {
        this.sourceKeyFields = sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Set the source key field in the relation table.
     * This is the name of the foreign key in the relation table to the source's primary key field.
     * This method is used if the source primary key is a singleton only.
     */
    public void setSourceRelationKeyFieldName(String sourceRelationKeyFieldName) {
        getSourceRelationKeyFields().addElement(new DatabaseField(sourceRelationKeyFieldName));
    }

    /**
     * PUBLIC:
     * Set the source relation key field names associated with the mapping.
     * These must be in-order with the sourceKeyFieldNames.
     */
    public void setSourceRelationKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setSourceRelationKeyFields(fields);
    }

    /**
     * INTERNAL:
     * Set the source fields.
     */
    public void setSourceRelationKeyFields(Vector<DatabaseField> sourceRelationKeyFields) {
        this.sourceRelationKeyFields = sourceRelationKeyFields;
    }

    /**
     * INTERNAL:
     * Set the target key field names associated with the mapping.
     * These must be in-order with the targetRelationKeyFieldNames.
     */
    public void setTargetKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setTargetKeyFields(fields);
    }

    /**
     * INTERNAL:
     * Set the target fields.
     */
    public void setTargetKeyFields(Vector<DatabaseField> targetKeyFields) {
        this.targetKeyFields = targetKeyFields;
    }

    /**
     * PUBLIC:
     * Set the target key field in the relation table.
     * This is the name of the foreign key in the relation table to the target's primary key field.
     * This method is used if the target's primary key is a singleton only.
     */
    public void setTargetRelationKeyFieldName(String targetRelationKeyFieldName) {
        getTargetRelationKeyFields().addElement(new DatabaseField(targetRelationKeyFieldName));
    }

    /**
     * INTERNAL:
     * Set the target relation key field names associated with the mapping.
     * These must be in-order with the targetKeyFieldNames.
     */
    public void setTargetRelationKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setTargetRelationKeyFields(fields);
    }

    /**
     * INTERNAL:
     * Set the target fields.
     */
    public void setTargetRelationKeyFields(Vector<DatabaseField> targetRelationKeyFields) {
        this.targetRelationKeyFields = targetRelationKeyFields;
    }
    
    /**
     * INTERNAL:
     * Create a row that contains source relation fields with values extracted from the source object.
     */
    public AbstractRecord buildRelationTableSourceRow(Object sourceObject, AbstractSession session, ForeignReferenceMapping mapping) {
        AbstractRecord databaseRow = new DatabaseRecord();
        return addRelationTableSourceRow(sourceObject, session, databaseRow, mapping);
    }

    /**
     * INTERNAL:
     * Add to a row source relation fields with values extracted from the source object.
     */
    public AbstractRecord addRelationTableSourceRow(Object sourceObject, AbstractSession session, AbstractRecord databaseRow, ForeignReferenceMapping mapping) {
        ObjectBuilder builder = mapping.getDescriptor().getObjectBuilder(); 
        int size = sourceKeyFields.size();
        for(int i=0; i < size; i++) {
            Object sourceValue = builder.extractValueFromObjectForField(sourceObject, sourceKeyFields.get(i), session);
            databaseRow.put(sourceRelationKeyFields.get(i), sourceValue);
        }
        return databaseRow;
    }

    /**
     * INTERNAL:
     * Create a row that contains source relation fields with values extracted from the source row.
     */
    public AbstractRecord buildRelationTableSourceRow(AbstractRecord sourceRow) {
        AbstractRecord databaseRow = new DatabaseRecord();
        return addRelationTableSourceRow(sourceRow, databaseRow);
    }

    /**
     * INTERNAL:
     * Add to a row source relation fields with values extracted from the source row.
     */
    public AbstractRecord addRelationTableSourceRow(AbstractRecord sourceRow, AbstractRecord databaseRow) {
        int size = sourceKeyFields.size();
        for(int i=0; i < size; i++) {
            Object sourceValue = sourceRow.get(sourceKeyFields.get(i));
            databaseRow.put(sourceRelationKeyFields.get(i), sourceValue);
        }
        return databaseRow;
    }

    /**
     * INTERNAL:
     * Add to a row target relation fields with values extracted from the target object.
     */
    public AbstractRecord addRelationTableTargetRow(Object targetObject, AbstractSession session, AbstractRecord databaseRow, ForeignReferenceMapping mapping) {
        ObjectBuilder builder = mapping.getReferenceDescriptor().getObjectBuilder(); 
        int size = targetKeyFields.size();
        for(int i=0; i < size; i++) {
            Object sourceValue = builder.extractValueFromObjectForField(targetObject, targetKeyFields.get(i), session);
            databaseRow.put(targetRelationKeyFields.get(i), sourceValue);
        }
        return databaseRow;
    }

    /**
     * INTERNAL:
     * Create a row that contains source relation fields with values extracted from the source object
     * and target relation fields with values extracted from the target object.
     */
    public AbstractRecord buildRelationTableSourceAndTargetRow(Object sourceObject, Object targetObject, AbstractSession session, ForeignReferenceMapping mapping) {
        AbstractRecord databaseRow = buildRelationTableSourceRow(sourceObject, session, mapping);
        databaseRow = addRelationTableTargetRow(targetObject, session, databaseRow, mapping);
        return databaseRow;
    }

    /**
     * INTERNAL:
     * Create a row that contains source relation fields with values extracted from the source row
     * and target relation fields with values extracted from the target object.
     */
    public AbstractRecord buildRelationTableSourceAndTargetRow(AbstractRecord sourceRow, Object targetObject, AbstractSession session, ForeignReferenceMapping mapping) {
        AbstractRecord databaseRow = buildRelationTableSourceRow(sourceRow);
        databaseRow = addRelationTableTargetRow(targetObject, session, databaseRow, mapping);
        return databaseRow;
    }
}
