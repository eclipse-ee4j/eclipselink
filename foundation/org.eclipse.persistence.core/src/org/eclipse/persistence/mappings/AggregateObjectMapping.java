/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.beans.PropertyChangeListener;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>:Two objects can be considered to be related by aggregation if there is a strict
 * 1:1 relationship between the objects. This means that the target (child or owned) object
 * cannot exist without the source (parent) object.
 *
 * In TopLink, it also means the data for the owned object is stored in the same table as
 * the parent.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class AggregateObjectMapping extends AggregateMapping implements RelationalMapping, MapKeyMapping, EmbeddableMapping {

    /**
     * If <em>all</em> the fields in the database row for the aggregate object are NULL,
     * then, by default, the mapping will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls).
     * To change this behavior, set the value of this variable to false. Then the mapping
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     */
    protected boolean isNullAllowed;

    protected DatabaseTable aggregateKeyTable  = null;
    
    /** Map the name of a field in the aggregate descriptor to a field in the source table. */
    /** 322233 - changed to store the source DatabaseField to hold Case and other colunm info*/
    protected transient Map<String, DatabaseField> aggregateToSourceFields;

    /**
     * Map of nested attributes that need to apply an override name to their
     * a nested aggregate mapping's database field. Aggregate to source fields 
     * map is the existing EclipseLink functionality and works well when all 
     * embeddable mappings have unique database fields. This map adds specific 
     * attribute to database field override.
     * @see addFieldTranslation()
     */
    protected transient Map<String, DatabaseField> nestedFieldTranslations;
    
    /** 
     * List of many to many mapping overrides to apply at initialize time to 
     * their cloned aggregate mappings. 
     */
    protected transient List<ManyToManyMapping> overrideManyToManyMappings;
    
    /** 
     * List of unidirectional one to many mapping overrides to apply at 
     * initialize time to their cloned aggregate mappings. 
     */
    protected transient List<UnidirectionalOneToManyMapping> overrideUnidirectionalOneToManyMappings;
    
    /**
     * List of maps id mappings that need to be set to read only at initialize
     * time on their cloned aggregate mappings.
     */
    protected transient List<DatabaseMapping> mapsIdMappings;
    
    /**
     * Default constructor.
     */
    public AggregateObjectMapping() {
        aggregateToSourceFields = new HashMap(5);
        nestedFieldTranslations = new HashMap<String, DatabaseField>();
        mapsIdMappings = new ArrayList<DatabaseMapping>();
        overrideManyToManyMappings = new ArrayList<ManyToManyMapping>();
        overrideUnidirectionalOneToManyMappings = new ArrayList<UnidirectionalOneToManyMapping>();
        isNullAllowed = true;
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the selection query is being initialized to add the fields for the map key to the query
     */
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression){
        for (DatabaseField field : getReferenceDescriptor().getAllFields()) {
             if (selectionQuery.isObjectLevelReadQuery()) {
                 ((ObjectLevelReadQuery)selectionQuery).addAdditionalField(baseExpression.getField(field));
             } else if (selectionQuery.isDataReadQuery()) {
                ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(baseExpression.getField(field));
            }
        }
    }

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query
     */
    public void addFieldsForMapKey(AbstractRecord joinRow){
        for (DatabaseMapping mapping : getReferenceDescriptor().getMappings()) {
            if (!mapping.isReadOnly()) {
                for (DatabaseField field : mapping.getFields()) {
                    if (field.isUpdatable()){
                        joinRow.put(field, null);
                    }
                }
            }
        }
    }
    
    /**
     * PUBLIC:
     * Add a field name translation that maps from a field name in the
     * source table to a field name in the aggregate descriptor.
     */
    public void addFieldNameTranslation(String sourceFieldName, String aggregateFieldName) {
        // 322233 - changed to store the sourceField instead of sourceFieldName
        addFieldTranslation(new DatabaseField(sourceFieldName), aggregateFieldName);
    }
    
    /**
     * PUBLIC:
     * Add a field translation that maps from a field in the
     * source table to a field name in the aggregate descriptor.
     */
    public void addFieldTranslation(DatabaseField sourceField, String aggregateFieldName) {
        //AggregateObjectMapping does not seem to support Aggregates on multiple tables
        String unQualifiedAggregateFieldName = aggregateFieldName.substring(aggregateFieldName.lastIndexOf('.') + 1);// -1 is returned for no ".".
        getAggregateToSourceFields().put(unQualifiedAggregateFieldName, sourceField);
    }
    
    /**
     * INTERNAL:
     * In JPA users may specify a maps id mapping on a shared embeddable 
     * descriptor. These mappings need to be set to read-only at initialize 
     * time, after the reference descriptor is cloned.
     */
    public void addMapsIdMapping(DatabaseMapping mapping) {
        mapsIdMappings.add(mapping);
    }

    /**
     * INTERNAL:
     * Add a nested field translation that maps from a field in the source table 
     * to a field name in a nested aggregate descriptor. These are handled 
     * slightly different that regular field translations in that they are 
     * unique based on the attribute name. It solves the case where multiple 
     * nested embeddables have mappings to similarly named default columns.
     */
    public void addNestedFieldTranslation(String attributeName, DatabaseField sourceField, String aggregateFieldName) {
        // Aggregate field name is redundant here as we will look up the field
        // through the attribute name. This method signature is to  satisfy the 
        // Embeddable interface. AggregateCollectionMapping uses the aggregate 
        // field name.
        nestedFieldTranslations.put(attributeName, sourceField);
    }

    /**
     * INTERNAL:
     * In JPA users may specify overrides to apply to a many to many mapping
     * on a shared embeddable descriptor. These settings are applied at
     * initialize time, after the reference descriptor is cloned.
     */
    public void addOverrideManyToManyMapping(ManyToManyMapping mapping) {
        overrideManyToManyMappings.add(mapping);
    }
    
    /**
     * INTERNAL:
     * In JPA users may specify overrides to apply to a unidirectional one to 
     * many mapping on a shared embeddable descriptor. These settings are 
     * applied at initialize time, after the reference descriptor is cloned.
     */
    public void addOverrideUnidirectionalOneToManyMapping(UnidirectionalOneToManyMapping mapping) {
        overrideUnidirectionalOneToManyMappings.add(mapping);
    }
    
    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy.  Add the target of this mapping to the deleted 
     * objects list if necessary
     *
     * This method is used for removal of private owned relationships.  
     * AggregateObjectMappings are dealt with in their parent delete, so this is a no-op.
     * 
     * @param object
     * @param manager
     */
    public void addKeyToDeletedObjectsList(Object object, Map deletedObjects){
    }
    
    /**
     * INTERNAL:
     * Return whether all the aggregate fields in the specified
     * row are NULL.
     */
    protected boolean allAggregateFieldsAreNull(AbstractRecord databaseRow) {
        Vector fields = getReferenceFields();
        int size = fields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField field = (DatabaseField)fields.get(index);
            Object value = databaseRow.get(field);
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * PUBLIC:
     * If <em>all</em> the fields in the database row for the aggregate object are NULL,
     * then, by default, the mapping will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls). This behavior can be
     * explicitly set by calling #allowNull().
     * To change this behavior, call #dontAllowNull(). Then the mapping
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * In either situation, when writing, the mapping will place a NULL in all the
     * fields in the database row for the aggregate object.
     * 
     * Note: Any aggregate that has a relationship mapping automatically does not allow
     * null.
     */
    public void allowNull() {
        setIsNullAllowed(true);
    }

    /**
     * INTERNAL:
     * Return whether the query's backup object has an attribute
     * value of null.
     */
    protected boolean backupAttributeValueIsNull(WriteObjectQuery query) {
        if (query.getSession().isUnitOfWork()) {
            Object backupAttributeValue = getAttributeValueFromObject(query.getBackupClone());
            if (backupAttributeValue == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * Build and return an aggregate object from the specified row.
     * If a null value is allowed and all the appropriate fields in the row are NULL, return a null.
     * If an aggregate is referenced by the target object, return it (maintain identity) 
     * Otherwise, simply create a new aggregate object and return it.
     */
    public Object buildAggregateFromRow(AbstractRecord databaseRow, Object targetObject, CacheKey cacheKey, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, boolean buildShallowOriginal, AbstractSession executionSession, boolean targetIsProtected) throws DatabaseException {
        // check for all NULLs
        if (isNullAllowed() && allAggregateFieldsAreNull(databaseRow)) {
            return null;
        }

        // maintain object identity (even if not refreshing) if target object references the aggregate
        // if aggregate is not referenced by the target object, construct a new aggregate
        Object aggregate = null;
        ClassDescriptor descriptor = getReferenceDescriptor();
        boolean refreshing = true;
        if (targetObject != null){
            if (descriptor.hasInheritance()) {
                Class newAggregateClass = descriptor.getInheritancePolicy().classFromRow(databaseRow, executionSession);
                descriptor = getReferenceDescriptor(newAggregateClass, executionSession);
                aggregate = getMatchingAttributeValueFromObject(databaseRow, targetObject, executionSession, descriptor);
                if ((aggregate != null) && (aggregate.getClass() != newAggregateClass)) {
                    // if the class has changed out from underneath us, we cannot preserve object identity
                    // build a new instance of the *new* class
                    aggregate = descriptor.getObjectBuilder().buildNewInstance();
                    refreshing = false;
                }
            } else {
                aggregate = getMatchingAttributeValueFromObject(databaseRow, targetObject, executionSession, descriptor);
            }
        }

        if (aggregate == null) {
            aggregate = descriptor.getObjectBuilder().buildNewInstance();
            refreshing = false;
        }
        
        ObjectBuildingQuery nestedQuery = sourceQuery;
        if (sourceQuery.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery objectQuery = (ObjectLevelReadQuery)sourceQuery;
            ObjectLevelReadQuery nestedObjectQuery = (ObjectLevelReadQuery)nestedQuery;
            String attributeName = getAttributeName();
            if ((objectQuery.isPartialAttribute(attributeName) || ((joinManager != null) && joinManager.isAttributeJoined(this.descriptor, this)))) {
                // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                nestedObjectQuery = (ObjectLevelReadQuery)objectQuery.deepClone();
                // Must cascade the nested partial/join expression and filter the nested ones.
                if (objectQuery.hasPartialAttributeExpressions()) {
                    nestedObjectQuery.setPartialAttributeExpressions(extractNestedExpressions(objectQuery.getPartialAttributeExpressions(), nestedObjectQuery.getExpressionBuilder(), false));
                } else if ( nestedObjectQuery.getJoinedAttributeManager().isToManyJoin()) {
                    // need the data results to build the child object(s).
                    List dataResults = new ArrayList();
                    //setDataResults does processing and calculations (such as DataResultsByPrimaryKey) that we do not want to do on the 
                    //actual data, but it has no other direct set method 
                    nestedObjectQuery.getJoinedAttributeManager().setDataResults(dataResults, executionSession);
                    //set the dataResults and DataResultsByPrimaryKey directly from the parent 
                    dataResults.addAll(joinManager.getDataResults_());
                    nestedObjectQuery.getJoinedAttributeManager().getDataResultsByPrimaryKey().putAll(joinManager.getDataResultsByPrimaryKey());
                }
                nestedObjectQuery.setDescriptor(descriptor);
                //need to use the new joinManager which has the proper aggregate descriptor set
                joinManager = nestedObjectQuery.getJoinedAttributeManager();
            }
            if (objectQuery.isAttributeBatchRead(this.descriptor, attributeName)) {
                if(nestedObjectQuery == objectQuery) {
                    // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                    nestedObjectQuery = (ObjectLevelReadQuery)nestedObjectQuery.clone();
                }
                // Must carry over properties for batching to work.
                nestedObjectQuery.setProperties(objectQuery.getProperties());
                // Computed nested batch attribute expressions.
                nestedObjectQuery.getBatchFetchPolicy().setAttributeExpressions(extractNestedExpressions(objectQuery.getBatchReadAttributeExpressions(), nestedObjectQuery.getExpressionBuilder(), false));
            }
            FetchGroup sourceFG = sourceQuery.getExecutionFetchGroup();
            if (sourceFG != null) {
                // Currently FetchGroups for Aggregates not supported because EntityFetchGroup (kept on the entity) is just a list of attributes.
                // Therefore clone the query (if not already cloned) and clear the fetch group.
                if(nestedObjectQuery == objectQuery) {
                    // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                    nestedObjectQuery = (ObjectLevelReadQuery)nestedObjectQuery.clone();
                }
                nestedObjectQuery.setFetchGroup(null);
                nestedObjectQuery.setFetchGroupName(null);
                nestedObjectQuery.setShouldUseDefaultFetchGroup(false);
                nestedObjectQuery.prepareFetchGroup();
            }
            nestedQuery = nestedObjectQuery;
        }

        if (buildShallowOriginal) {
            descriptor.getObjectBuilder().buildAttributesIntoShallowObject(aggregate, databaseRow, nestedQuery);
        } else if (executionSession.isUnitOfWork()) {
            descriptor.getObjectBuilder().buildAttributesIntoWorkingCopyClone(aggregate, buildWrapperCacheKeyForAggregate(cacheKey), nestedQuery, joinManager, databaseRow, (UnitOfWorkImpl)executionSession, refreshing);
        } else {
            descriptor.getObjectBuilder().buildAttributesIntoObject(aggregate, buildWrapperCacheKeyForAggregate(cacheKey), databaseRow, nestedQuery, joinManager, refreshing, executionSession);
        }
        return aggregate;
    }

    /**
     * INTERNAL:
     * Wrap the aggregate represented by this mapping in a cachekey so it can be processed my 
     * methods down the stack.
     * @param owningCacheKey - the cache key holding the object to extract the aggregate from
     * @return
     */
    protected CacheKey buildWrapperCacheKeyForAggregate(CacheKey owningCacheKey){
        if (isMapKeyMapping){
            return owningCacheKey;
        }
        CacheKey aggregateKey = null;
        if (owningCacheKey != null){
            Object object = owningCacheKey.getObject();
            if (owningCacheKey.getObject() != null){
                Object aggregate = getAttributeValueFromObject(object);
                aggregateKey = new CacheKey(null, aggregate, null);
                aggregateKey.setProtectedForeignKeys(owningCacheKey.getProtectedForeignKeys());
                aggregateKey.setRecord(owningCacheKey.getRecord());
                aggregateKey.setIsolated(owningCacheKey.isIsolated());
                aggregateKey.setReadTime(owningCacheKey.getReadTime());
            }
        }
        return aggregateKey;
    }
        
    /**
     * INTERNAL:
     * Write null values for all aggregate fields into the parent row.
     */
    protected void writeNullReferenceRow(AbstractRecord record) {
        List<DatabaseField> fields = getReferenceFields();
        int size = fields.size();
        for (int index = 0; index < size; index++) {
            record.put(fields.get(index), null);
        }
        if (size > 0) {
            // EL Bug 319759 - if a field is null, then the update call cache should not be used
            record.setNullValueInFields(true);
        }
    }

    /**
     * INTERNAL:
     * Used to allow object level comparisons.
     * In the case of an Aggregate which has no primary key must do an attribute
     * by attribute comparison.
     */
    public Expression buildObjectJoinExpression(Expression expression, Object value, AbstractSession session) {
        Expression attributeByAttributeComparison = null;
        Expression join = null;
        Object attributeValue = null;

        // value need not be unwrapped as it is an aggregate, nor should it
        // influence a call to getReferenceDescriptor.
        ClassDescriptor referenceDescriptor = getReferenceDescriptor();
        if ((value != null) && !referenceDescriptor.getJavaClass().isInstance(value)) {
            throw QueryException.incorrectClassForObjectComparison(expression, value, this);
        }
        Enumeration mappings = referenceDescriptor.getMappings().elements();
        for (; mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (value == null) {
                attributeValue = null;
            } else {
                attributeValue = mapping.getAttributeValueFromObject(value);
            }
            join = expression.get(mapping.getAttributeName()).equal(attributeValue);
            if (attributeByAttributeComparison == null) {
                attributeByAttributeComparison = join;
            } else {
                attributeByAttributeComparison = attributeByAttributeComparison.and(join);
            }
        }
        return attributeByAttributeComparison;
    }

    /**
     * INTERNAL:
     * Used to allow object level comparisons.
     */
    public Expression buildObjectJoinExpression(Expression expression, Expression argument, AbstractSession session) {
        Expression attributeByAttributeComparison = null;

        //Enumeration mappingsEnum = getSourceToTargetKeyFields().elements();
        Enumeration mappingsEnum = getReferenceDescriptor().getMappings().elements();
        for (; mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            String attributeName = mapping.getAttributeName();
            Expression join = expression.get(attributeName).equal(argument.get(attributeName));
            if (attributeByAttributeComparison == null) {
                attributeByAttributeComparison = join;
            } else {
                attributeByAttributeComparison = attributeByAttributeComparison.and(join);
            }
        }
        return attributeByAttributeComparison;
    }

    /**
     * INTERNAL:
     * Write the aggregate values into the parent row.
     */
    protected void writeToRowFromAggregate(AbstractRecord record, Object object, Object attributeValue, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (attributeValue == null) {
            if (this.isNullAllowed) {
                writeNullReferenceRow(record);
            } else {
                throw DescriptorException.nullForNonNullAggregate(object, this);
            }
        } else {
            if (!session.isClassReadOnly(attributeValue.getClass())) {
                getObjectBuilder(attributeValue, session).buildRow(record, attributeValue, session, writeType);
            }
        }
    }

    /**
     * INTERNAL:
     * Build and return a database row built with the values from
     * the specified attribute value.
     */
    protected void writeToRowFromAggregateWithChangeRecord(AbstractRecord record, ChangeRecord changeRecord, ObjectChangeSet objectChangeSet, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (objectChangeSet == null) {
            if (this.isNullAllowed) {
                writeNullReferenceRow(record);
            } else {
                Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
                throw DescriptorException.nullForNonNullAggregate(object, this);
            }
        } else {
            if (!session.isClassReadOnly(objectChangeSet.getClassType(session))) {
                getReferenceDescriptor(objectChangeSet.getClassType(session), session).getObjectBuilder().buildRowWithChangeSet(record, objectChangeSet, session, writeType);
            }
        }
    }

    /**
     * INTERNAL:
     * Build and return a database row built with the changed values from
     * the specified attribute value.
     */
    protected void writeToRowFromAggregateForUpdate(AbstractRecord record, WriteObjectQuery query, Object attributeValue) throws DescriptorException {
        if (attributeValue == null) {
            if (this.isNullAllowed) {
                if (backupAttributeValueIsNull(query)) {
                    // both attributes are null - no update required
                } else {
                    writeNullReferenceRow(record);
                }
            } else {
                throw DescriptorException.nullForNonNullAggregate(query.getObject(), this);
            }
        } else if ((query.getBackupClone() != null) && ((getMatchingBackupAttributeValue(query, attributeValue) == null) || !(attributeValue.getClass().equals(getMatchingBackupAttributeValue(query, attributeValue).getClass())))) {
            getObjectBuilder(attributeValue, query.getSession()).buildRow(record, attributeValue, query.getSession(), WriteType.UPDATE);
        } else {
            if (!query.getSession().isClassReadOnly(attributeValue.getClass())) {
                WriteObjectQuery clonedQuery = (WriteObjectQuery)query.clone();
                clonedQuery.setObject(attributeValue);
                if (query.getSession().isUnitOfWork()) {
                    Object backupAttributeValue = getMatchingBackupAttributeValue(query, attributeValue);
                    if (backupAttributeValue == null) {
                        backupAttributeValue = getObjectBuilder(attributeValue, query.getSession()).buildNewInstance();
                    }
                    clonedQuery.setBackupClone(backupAttributeValue);
                }
                getObjectBuilder(attributeValue, query.getSession()).buildRowForUpdate(record, clonedQuery);
            }
        }
    }

    /**
     * INTERNAL:
     * Clone the attribute from the original and assign it to the clone.
     */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession) {
        Object attributeValue = getAttributeValueFromObject(original);
        Object aggregateClone = buildClonePart(original, cacheKey, attributeValue, cloningSession);

        if (aggregateClone != null && cloningSession.isUnitOfWork()) {
            ClassDescriptor descriptor = getReferenceDescriptor(aggregateClone, (AbstractSession)cloningSession);
            descriptor.getObjectChangePolicy().setAggregateChangeListener(clone, aggregateClone, (UnitOfWorkImpl)cloningSession, descriptor, getAttributeName());
        }

        setAttributeValueInObject(clone, aggregateClone);
    }
    
    /**
     * INTERNAL:
     * Build a clone of the given element in a unitOfWork
     * @param element
     * @param cloningSession
     * @param isExisting
     * @return
     */
    public Object buildElementClone(Object attributeValue, Object parent, CacheKey parentCacheKey, AbstractSession cloningSession, boolean isExisting){
        Object aggregateClone = buildClonePart(attributeValue, parentCacheKey, cloningSession, isExisting);
        if (aggregateClone != null && cloningSession.isUnitOfWork()) {
            ClassDescriptor descriptor = getReferenceDescriptor(aggregateClone, (AbstractSession)cloningSession);
            descriptor.getObjectChangePolicy().setAggregateChangeListener(parent, aggregateClone, (UnitOfWorkImpl)cloningSession, descriptor, getAttributeName());
        }
        return aggregateClone;
    }
           
    /**
     * INTERNAL:
     * Set the change listener in the aggregate.
     */
    public void setChangeListener(Object clone, PropertyChangeListener listener, UnitOfWorkImpl uow) {
        Object attributeValue = getAttributeValueFromObject(clone);
        if (attributeValue != null) {
            ClassDescriptor descriptor = getReferenceDescriptor(attributeValue, uow);
            descriptor.getObjectChangePolicy().setAggregateChangeListener(clone, attributeValue, uow, descriptor, getAttributeName());
        }
    }

    /**
     * INTERNAL:
     * A combination of readFromRowIntoObject and buildClone.
     * <p>
     * buildClone assumes the attribute value exists on the original and can
     * simply be copied.
     * <p>
     * readFromRowIntoObject assumes that one is building an original.
     * <p>
     * Both of the above assumptions are false in this method, and actually
     * attempts to do both at the same time.
     * <p>
     * Extract value from the row and set the attribute to this value in the
     * working copy clone.
     * In order to bypass the shared cache when in transaction a UnitOfWork must
     * be able to populate working copies directly from the row.
     */
    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // This method is a combination of buildggregateFromRow and
        // buildClonePart on the super class.
        // none of buildClonePart used, as not an orignal new object, nor
        // do we worry about creating heavy clones for aggregate objects.
        Object clonedAttributeValue = buildAggregateFromRow(databaseRow, clone, null, joinManager, sourceQuery, false, executionSession, true);
        ClassDescriptor descriptor = getReferenceDescriptor(clonedAttributeValue, unitOfWork);
        if (clonedAttributeValue != null) {
            descriptor.getObjectChangePolicy().setAggregateChangeListener(clone, clonedAttributeValue, unitOfWork, descriptor, getAttributeName());
        }
        setAttributeValueInObject(clone, clonedAttributeValue);
        return;
    }

    /**
     * INTERNAL:
     * Builds a shallow original object.  Only direct attributes and primary
     * keys are populated.  In this way the minimum original required for
     * instantiating a working copy clone can be built without placing it in
     * the shared cache (no concern over cycles).
     */
    public void buildShallowOriginalFromRow(AbstractRecord databaseRow, Object original, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) {
        Object aggregate = buildAggregateFromRow(databaseRow, original, null, joinManager, sourceQuery, true, executionSession, true);// shallow only.
        setAttributeValueInObject(original, aggregate);
    }

    /**
     * INTERNAL:
     * Certain key mappings favor different types of selection query.  Return the appropriate
     * type of selectionQuery
     * @return
     */
    public ReadQuery buildSelectionQueryForDirectCollectionKeyMapping(ContainerPolicy containerPolicy){
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(referenceClass);
        query.setDescriptor(getReferenceDescriptor());
        query.setContainerPolicy(containerPolicy);
        return query;
    }
    
    /**
     * INTERNAL:
     * Build and return a "template" database row with all the fields
     * set to null.
     */
    protected AbstractRecord buildTemplateInsertRow(AbstractSession session) {
        AbstractRecord result = getReferenceDescriptor().getObjectBuilder().buildTemplateInsertRow(session);
        List processedMappings = (List)getReferenceDescriptor().getMappings().clone();
        if (getReferenceDescriptor().hasInheritance()) {
            for (ClassDescriptor child : getReferenceDescriptor().getInheritancePolicy().getChildDescriptors()) {
                for (DatabaseMapping mapping : child.getMappings()) {
                    // Only write mappings once.
                    if (!processedMappings.contains(mapping)) {
                        mapping.writeInsertFieldsIntoRow(result, session);
                        processedMappings.add(mapping);
                    }
                }
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit to the map key
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, boolean getAttributeValueFromObject){
        ObjectBuilder builder = getReferenceDescriptor(object.getClass(), uow).getObjectBuilder();
        builder.cascadeDiscoverAndPersistUnregisteredNewObjects(object, newObjects, unregisteredExistingObjects, visitedObjects, uow);
    }
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        Object objectReferenced = null;
        if (getAttributeValueFromObject){
            //objects referenced by this mapping are not registered as they have
            // no identity, however mappings from the referenced object may need cascading.
            objectReferenced = getAttributeValueFromObject(object);
        } else {
            objectReferenced = object;
        }
        if ((objectReferenced == null)) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadePerformRemove(objectReferenced, uow, visitedObjects);
        }
    }    
    
    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        cascadePerformRemoveIfRequired(object, uow, visitedObjects, true);
    }

    /**
     * INTERNAL:
     * Cascade perform removal of orphaned private owned objects from the UnitOfWorkChangeSet
     */
    public void cascadePerformRemovePrivateOwnedObjectFromChangeSetIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue == null) {
            return;
        }
        if (!visitedObjects.containsKey(attributeValue)) {
            visitedObjects.put(attributeValue, attributeValue);
            ObjectBuilder builder = getReferenceDescriptor(attributeValue, uow).getObjectBuilder();
            // cascade perform remove any related objects via ObjectBuilder for an aggregate object
            builder.cascadePerformRemovePrivateOwnedObjectFromChangeSet(attributeValue, uow, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        Object objectReferenced = null;
        //aggregate objects are not registered but their mappings should be.
        if (getAttributeValueFromObject){
            objectReferenced = getAttributeValueFromObject(object);
        } else {
            objectReferenced = object;
        }
        if ((objectReferenced == null)) {
            return;
        }
        if (!visitedObjects.containsKey(objectReferenced)) {
            visitedObjects.put(objectReferenced, objectReferenced);
            ObjectBuilder builder = getReferenceDescriptor(objectReferenced.getClass(), uow).getObjectBuilder();
            builder.cascadeRegisterNewForCreate(objectReferenced, uow, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        cascadeRegisterNewIfRequired(object, uow, visitedObjects, true);
    }

    /**
     * INTERNAL:
     * Clone the aggregate to source field names. AggregateCollectionMapping
     * needs each nested embedded mapping to have its own  list of aggregate
     * to source field names so that it can apply nested override names to
     * shared aggregate object mappings.
     */
    public Object clone() {
        AggregateObjectMapping mappingObject = (AggregateObjectMapping) super.clone();
        
        Map<String, DatabaseField> aggregateToSourceFields = new HashMap<String, DatabaseField>();
        aggregateToSourceFields.putAll(getAggregateToSourceFields());
        mappingObject.setAggregateToSourceFields(aggregateToSourceFields);

        return mappingObject;
    }
    
    /**
     * INTERNAL:
     * Return the fields handled by the mapping.
     */
    @Override
    protected Vector<DatabaseField> collectFields() {
        return getReferenceFields();
    }

    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    @Override
    public void collectQueryParameters(Set<DatabaseField> record){
        for (DatabaseMapping mapping : getReferenceDescriptor().getMappings()){
            if ((mapping.isForeignReferenceMapping() && !mapping.isCacheable()) || (mapping.isAggregateObjectMapping() && mapping.getReferenceDescriptor().hasNoncacheableMappings())){
                ((ForeignReferenceMapping) mapping).collectQueryParameters(record);
            }
        }
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        Object key = buildAggregateFromRow(dbRow, null, parentCacheKey, null, query, false, session, isTargetProtected);
        return key;
    }
    
    /**
     * INTERNAL:
     * Creates the Array of simple types used to recreate this map.  
     */
    public Object createSerializableMapKeyInfo(Object key, AbstractSession session){
        return key; // Embeddables have no identity so they are not reduced to PK.
    }

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.  
     * This may return the value directly in case of a simple key or will be used as the FK to load a related entity.
     */
    public Object createMapComponentFromSerializableKeyInfo(Object keyInfo, AbstractSession session){
        return keyInfo; // Embeddables have no identity so they are not reduced to PK.
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection and a join query is executed.  Returns the key.
     */
    public Object createMapComponentFromJoinedRow(AbstractRecord dbRow, JoinedAttributeManager joinManger, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return createMapComponentFromRow(dbRow, query, parentCacheKey, session, isTargetProtected);
    }
    
    /**
     * INTERNAL:
     * Create a query key that links to the map key
     * @return
     */
    public QueryKey createQueryKeyForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy, Delete the passed object if necessary.
     * 
     * This method is used for removal of private owned relationships. 
     * AggregateObjectMappings are dealt with in their parent delete, so this is a no-op.
     * 
     * @param objectDeleted
     * @param session
     */
    public void deleteMapKey(Object objectDeleted, AbstractSession session){
    }

    /**
     * PUBLIC:
     * If <em>all</em> the fields in the database row for the aggregate object are NULL,
     * then, by default, the mapping will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls). This behavior can be
     * explicitly set by calling #allowNull().
     * To change this behavior, call #dontAllowNull(). Then the mapping
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * In either situation, when writing, the mapping will place a NULL in all the
     * fields in the database row for the aggregate object.
     * 
     * Note: Any aggregate that has a relationship mapping automatically does not allow
     * null.
     */
    public void dontAllowNull() {
        setIsNullAllowed(false);
    }
    
    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query.
     */
    public Map extractIdentityFieldsForQuery(Object object, AbstractSession session){
        Map keyFields = new HashMap();
        ClassDescriptor descriptor =getReferenceDescriptor();
        boolean usePrimaryKeyFields = (descriptor.getPrimaryKeyFields() != null && ! descriptor.getPrimaryKeyFields().isEmpty()) ? true : false;
        Iterator <DatabaseMapping> i = descriptor.getMappings().iterator();
        while (i.hasNext()){
            DatabaseMapping mapping = i.next();
            if (!mapping.isReadOnly() && (!usePrimaryKeyFields || (usePrimaryKeyFields && mapping.isPrimaryKeyMapping()))){
                Iterator<DatabaseField> fields = mapping.getFields().iterator();
                while (fields.hasNext()){
                    DatabaseField field = fields.next();
                    if (field.isUpdatable()){
                        Object value = descriptor.getObjectBuilder().extractValueFromObjectForField(object, field, session);
                        keyFields.put(field, value);
                    }
                }
            }
        }
        
        return keyFields;
    }
    
    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query
     * @return
     */
    public List<DatabaseTable> getAdditionalTablesForJoinQuery(){
        return getReferenceDescriptor().getTables();
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria necessary to select the target object when this mapping
     * is a map key.
     * 
     * AggregateObjectMappings do not need any additional selection criteria when they are map keys
     * @return
     */
    public Expression getAdditionalSelectionCriteriaForMapKey(){
        return null;
    }

    /**
     * INTERNAL:
     * Return a collection of the aggregate to source field  associations.
     */
    public Vector<Association> getAggregateToSourceFieldAssociations() {
        Vector<Association> associations = new Vector(getAggregateToSourceFields().size());
        Iterator aggregateEnum = getAggregateToSourceFields().keySet().iterator();
        Iterator sourceEnum = getAggregateToSourceFields().values().iterator();
        while (aggregateEnum.hasNext()) {
            associations.addElement(new Association(aggregateEnum.next(), sourceEnum.next()));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Return the hashtable that stores aggregate field name to source fields.
     */
    public Map<String, DatabaseField> getAggregateToSourceFields() {
        return aggregateToSourceFields;
    }

    /**
     * PUBLIC:
     * The classification type for the attribute this mapping represents
     */
    public Class getAttributeClassification() {
        return getReferenceClass();
    }

    /**
     * INTERNAL:
     * Return the classification for the field contained in the mapping.
     * This is used to convert the row value to a consistent Java value.
     */
    public Class getFieldClassification(DatabaseField fieldToClassify) {
        DatabaseMapping mapping = getReferenceDescriptor().getObjectBuilder().getMappingForField(fieldToClassify);
        if (mapping == null) {
            return null;// Means that the mapping is read-only
        }
        return mapping.getFieldClassification(fieldToClassify);
    }
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields
     * @return
     */
    public List<DatabaseField> getIdentityFieldsForMapKey(){
        ClassDescriptor descriptor =getReferenceDescriptor();
        if (descriptor.getPrimaryKeyFields() != null){
            return descriptor.getPrimaryKeyFields();
        } else {
            return getAllFieldsForMapKey();
        }
    }
    
    /**
     * INTERNAL:
     * Get all the fields for the map key
     */
    public List<DatabaseField> getAllFieldsForMapKey(){
        return getReferenceDescriptor().getAllFields();
    }
    

    /**
     * INTERNAL:
     * Return a Map of any foreign keys defined within the the MapKey
     * @return
     */
    public Map<DatabaseField, DatabaseField> getForeignKeyFieldsForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * This is used to preserve object identity during a refreshObject()
     * query. Return the object corresponding to the specified database row.
     * The default is to simply return the attribute value.
     */
    protected Object getMatchingAttributeValueFromObject(AbstractRecord row, Object targetObject, AbstractSession session, ClassDescriptor descriptor) {
        return getAttributeValueFromObject(targetObject);
    }

    /**
     * INTERNAL:
     * This is used to match up objects during an update in a UOW.
     * Return the object corresponding to the specified attribute value.
     * The default is to simply return the backup attribute value.
     */
    protected Object getMatchingBackupAttributeValue(WriteObjectQuery query, Object attributeValue) {
        return getAttributeValueFromObject(query.getBackupClone());
    }

    /**
     * INTERNAL:
     * Return the query that is used when this mapping is part of a joined relationship
     * 
     * This method is used when this mapping is used to map the key in a Map
     * @return
     */
    public ObjectLevelReadQuery getNestedJoinQuery(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session){
        return null;
    }
    
    /**
     * INTERNAL:
     * Since aggregate object mappings clone their descriptors, for inheritance the correct child clone must be found.
     */
    public ClassDescriptor getReferenceDescriptor(Class theClass, AbstractSession session) {
        if (this.referenceDescriptor.getJavaClass() == theClass) {
            return this.referenceDescriptor;
        }

        ClassDescriptor subDescriptor = this.referenceDescriptor.getInheritancePolicy().getSubclassDescriptor(theClass);
        if (subDescriptor == null) {
            throw DescriptorException.noSubClassMatch(theClass, this);
        } else {
            return subDescriptor;
        }
    }

    
    /**
     * INTERNAL:
     * Return the fields used to build the aggregate object.
     */
    protected Vector<DatabaseField> getReferenceFields() {
        return getReferenceDescriptor().getAllFields();
    }
    /**
     * INTERNAL:
     * If required, get the targetVersion of the source object from the merge manager.
     * 
     * Used with MapKeyContainerPolicy to abstract getting the target version of a source key
     * @return
     */
    public Object getTargetVersionOfSourceObject(Object object, Object parent, MergeManager mergeManager, AbstractSession targetSession){
        if (mergeManager.getSession().isUnitOfWork()){
            UnitOfWorkImpl uow = (UnitOfWorkImpl)mergeManager.getSession();
            Object aggregateObject = buildClonePart(object, null, targetSession, uow.isOriginalNewObject(parent));
            return aggregateObject;
        }
        return object;
    }
    
    /**
     * INTERNAL:
     * Return the class this key mapping maps or the descriptor for it
     * @return
     */
    public Object getMapKeyTargetType(){
        return getReferenceDescriptor();
    }
    
    /**
     * INTERNAL:
     * Return if the mapping has any ownership or other dependency over its target object(s).
     */
    public boolean hasDependency() {
        return getReferenceDescriptor().hasDependencyOnParts();
    }
    
    /**
     * INTERNAL:
     * For an aggregate mapping the reference descriptor is cloned. The cloned descriptor is then
     * assigned primary keys and table names before initialize. Once the cloned descriptor is initialized
     * it is assigned as reference descriptor in the aggregate mapping. This is a very specific
     * behavior for aggregate mappings. The original descriptor is used only for creating clones and
     * after that the aggregate mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);

        ClassDescriptor clonedDescriptor = (ClassDescriptor)getReferenceDescriptor().clone();
        if (clonedDescriptor.isChildDescriptor()) {
            ClassDescriptor parentDescriptor = session.getDescriptor(clonedDescriptor.getInheritancePolicy().getParentClass());
            initializeParentInheritance(parentDescriptor, clonedDescriptor, session);
        }

        setReferenceDescriptor(clonedDescriptor);

        // Apply any override m2m mappings to their cloned mappings.
        for (ManyToManyMapping overrideMapping : overrideManyToManyMappings) {
            DatabaseMapping mapping = clonedDescriptor.getMappingForAttributeName(overrideMapping.getAttributeName());
            
            if (mapping.isManyToManyMapping()) {
                ManyToManyMapping mappingClone = (ManyToManyMapping) mapping;
                mappingClone.setRelationTable(overrideMapping.getRelationTable());
                mappingClone.setSourceKeyFields(overrideMapping.getSourceKeyFields());
                mappingClone.setSourceRelationKeyFields(overrideMapping.getSourceRelationKeyFields());
                mappingClone.setTargetKeyFields(overrideMapping.getTargetKeyFields());
                mappingClone.setTargetRelationKeyFields(overrideMapping.getTargetRelationKeyFields());
            } 
            
            // Else, silently ignored for now. These override mappings are set 
            // and controlled through JPA metadata processing.
        }
        
        // Apply any override uni-directional 12m mappings to their cloned mappings.
        for (UnidirectionalOneToManyMapping overrideMapping : overrideUnidirectionalOneToManyMappings) {
            DatabaseMapping mapping = clonedDescriptor.getMappingForAttributeName(overrideMapping.getAttributeName());
            
            if (mapping.isUnidirectionalOneToManyMapping()) {
                UnidirectionalOneToManyMapping mappingClone = (UnidirectionalOneToManyMapping) mapping;
                mappingClone.setSourceKeyFields(overrideMapping.getSourceKeyFields());
                mappingClone.setTargetForeignKeyFields(overrideMapping.getTargetForeignKeyFields());
            }
            
            // Else, silently ignored for now. These override mappings are set 
            // and controlled through JPA metadata processing.
        }
        
        // Mark any mapsId mappings as read-only.
        for (DatabaseMapping mapsIdMapping : mapsIdMappings) {
            DatabaseMapping mapping = clonedDescriptor.getMappingForAttributeName(mapsIdMapping.getAttributeName());
            
            if (mapping != null) {
                mapping.setIsReadOnly(true);
            }
            
            // Else, silently ignored for now. Maps id mappings are set and 
            // controlled through JPA metadata processing.
        }
        
        // disallow null for aggregates with relationships
        for (DatabaseMapping mapping: getReferenceDescriptor().getMappings()){
            if (mapping.isForeignReferenceMapping() || mapping.isAbstractCompositeDirectCollectionMapping()){
                isNullAllowed = false;
            }
        }
        
        initializeReferenceDescriptor(clonedDescriptor);
        clonedDescriptor.preInitialize(session);
        clonedDescriptor.initialize(session);
        translateFields(clonedDescriptor, session);

        if (clonedDescriptor.hasInheritance() && clonedDescriptor.getInheritancePolicy().hasChildren()) {
            //clone child descriptors
            initializeChildInheritance(clonedDescriptor, session);
        }

        setFields(collectFields());
        
        // Add the nested pre delete mappings to the source entity.
        if (clonedDescriptor.hasPreDeleteMappings()) {
            getDescriptor().getPreDeleteMappings().addAll(clonedDescriptor.getPreDeleteMappings());
        }
    }

    /**
     * INTERNAL:
     * For an aggregate mapping the reference descriptor is cloned.
     * If the reference descriptor is involved in an inheritance tree,
     * all the parent and child descriptors are cloned also.
     * The cloned descriptors are then assigned primary keys and
     * table names before initialize.
     * This is a very specific behavior for aggregate mappings.
     */
    public void initializeChildInheritance(ClassDescriptor parentDescriptor, AbstractSession session) throws DescriptorException {
        //recursive call to the further children descriptors
        if (parentDescriptor.getInheritancePolicy().hasChildren()) {
            //setFields(clonedChildDescriptor.getFields());		
            List<ClassDescriptor> childDescriptors = parentDescriptor.getInheritancePolicy().getChildDescriptors();
            List<ClassDescriptor> cloneChildDescriptors = new ArrayList();
            for (ClassDescriptor child : childDescriptors) {
                ClassDescriptor clonedChildDescriptor = (ClassDescriptor)child.clone();
                clonedChildDescriptor.getInheritancePolicy().setParentDescriptor(parentDescriptor);
                initializeReferenceDescriptor(clonedChildDescriptor);
                clonedChildDescriptor.preInitialize(session);
                clonedChildDescriptor.initialize(session);
                translateFields(clonedChildDescriptor, session);
                cloneChildDescriptors.add(clonedChildDescriptor);
                initializeChildInheritance(clonedChildDescriptor, session);
            }
            parentDescriptor.getInheritancePolicy().setChildDescriptors(cloneChildDescriptors);
        }
    }

    /**
     * INTERNAL:
     * For an aggregate mapping the reference descriptor is cloned.
     * If the reference descriptor is involved in an inheritance tree,
     * all the parent and child descriptors are cloned also.
     * The cloned descriptors are then assigned primary keys and
     * table names before initialize.
     * This is a very specific behavior for aggregate mappings.
     */
    public void initializeParentInheritance(ClassDescriptor parentDescriptor, ClassDescriptor childDescriptor, AbstractSession session) throws DescriptorException {
        ClassDescriptor clonedParentDescriptor = (ClassDescriptor)parentDescriptor.clone();

        //recursive call to the further parent descriptors
        if (clonedParentDescriptor.getInheritancePolicy().isChildDescriptor()) {
            ClassDescriptor parentToParentDescriptor = session.getDescriptor(clonedParentDescriptor.getJavaClass());
            initializeParentInheritance(parentToParentDescriptor, parentDescriptor, session);
        }

        initializeReferenceDescriptor(clonedParentDescriptor);
        Vector children = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        children.addElement(childDescriptor);
        clonedParentDescriptor.getInheritancePolicy().setChildDescriptors(children);
        clonedParentDescriptor.preInitialize(session);
        clonedParentDescriptor.initialize(session);
        translateFields(clonedParentDescriptor, session);
    }

    /**
     * INTERNAL:
     * Initialize the cloned reference descriptor with table names and primary keys
     */
    protected void initializeReferenceDescriptor(ClassDescriptor clonedDescriptor) {
        if (aggregateKeyTable != null){
            clonedDescriptor.setDefaultTable(aggregateKeyTable);
            Vector<DatabaseTable> tables = new Vector<DatabaseTable>(1);
            tables.add(aggregateKeyTable);
            clonedDescriptor.setTables(tables);
        } else {
            // Must ensure default tables remains the same.
            clonedDescriptor.setDefaultTable(getDescriptor().getDefaultTable());
            clonedDescriptor.setTables(getDescriptor().getTables());
            clonedDescriptor.setPrimaryKeyFields(getDescriptor().getPrimaryKeyFields());
        }
 
    }
    
    /**
     * INTERNAL:
     * Called when iterating through descriptors to handle iteration on this mapping when it is used as a MapKey
     * @param iterator
     * @param element
     */
    public void iterateOnMapKey(DescriptorIterator iterator, Object element){
        super.iterateOnAttributeValue(iterator, element);
    }
    
    /**
     * INTERNAL:
     * Return whether this mapping should be traversed when we are locking
     * @return
     */
    public boolean isLockableMapping(){
        return true;
    }
    
    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public boolean isAggregateObjectMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        // This can be called before and after initialization.
        // Use the mapping reference descriptor when initialized, otherwise find the uninitialized one.
        ClassDescriptor referencedDescriptor = getReferenceDescriptor();
        if (referencedDescriptor == null) {
            Iterator ordered = project.getOrderedDescriptors().iterator();
            while (ordered.hasNext() && referencedDescriptor == null){
                ClassDescriptor descriptor = (ClassDescriptor)ordered.next();
                if (descriptor.getJavaClassName().equals(getReferenceClassName())){
                    referencedDescriptor = descriptor;
                }
            }
        }
        if (referencedDescriptor != null) {
            if (!referencedDescriptor.supportsChangeTracking(project)) {
                return false;
            }
            // Also check subclasses.
            if (referencedDescriptor.hasInheritance()) {
                for (Iterator iterator = referencedDescriptor.getInheritancePolicy().getChildDescriptors().iterator(); iterator.hasNext(); ) {
                    ClassDescriptor subclassDescriptor = (ClassDescriptor)iterator.next();
                    if (!subclassDescriptor.supportsChangeTracking(project)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    /**
     * INTERNAL
     * Return true if this mapping supports cascaded version optimistic locking.
     */
    public boolean isCascadedLockingSupported() {
        return true;
    }
    
    /**
     * PUBLIC:
     * Return if all the fields in the database row for the aggregate object are NULL,
     * then, by default, the mapping will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls).
     * To change this behavior, set the value of this variable to false. Then the mapping
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * 
     * Note: Any aggregate that has a relationship mapping automatically does not allow
     * null.
     */
    public boolean isNullAllowed() {
        return isNullAllowed;
    }

    /**
     * INTERNAL:
     * For an aggregate mapping the reference descriptor is cloned. The cloned descriptor is then
     * assigned primary keys and table names before initialize. Once the cloned descriptor is initialized
     * it is assigned as reference descriptor in the aggregate mapping. This is a very specific
     * behavior for aggregate mappings. The original descriptor is used only for creating clones and
     * after that the aggregate mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void postInitialize(AbstractSession session) throws DescriptorException {
        super.postInitialize(session);

        if (getReferenceDescriptor() != null) {
            referenceDescriptor.setCacheIsolation(descriptor.getCacheIsolation());
            // Changed as part of fix for bug#4410581 aggregate mapping can not be set to use change tracking if owning descriptor does not use it.
            // Basically the policies should be the same, but we also allow deferred with attribute for CMP2 (courser grained).
            if (getDescriptor().getObjectChangePolicy().getClass().equals(DeferredChangeDetectionPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new DeferredChangeDetectionPolicy());
            } else if (getDescriptor().getObjectChangePolicy().getClass().equals(ObjectChangeTrackingPolicy.class)
                    && getReferenceDescriptor().getObjectChangePolicy().getClass().equals(AttributeChangeTrackingPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new ObjectChangeTrackingPolicy());
            }

            //need to set the primary key classification as the mappings for the pk fields might not be available
            if (getReferenceDescriptor().isAggregateDescriptor()){
                getReferenceDescriptor().getObjectBuilder().setPrimaryKeyClassifications(this.getDescriptor().getObjectBuilder().getPrimaryKeyClassifications());
                getReferenceDescriptor().setHasSimplePrimaryKey(this.getDescriptor().hasSimplePrimaryKey());
            }

            getReferenceDescriptor().postInitialize(session);
        } 
    }
    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping
     */
    public void preinitializeMapKey(DatabaseTable table) {
        setTableForAggregateMappingKey(table);
    }
    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key after initializing the mapping.
     */
    public void postInitializeMapKey(MappedKeyMapContainerPolicy policy) {
        return;
    }
    
    /**
     * INTERNAL:
     * Build an aggregate object from the specified return row and put it
     * in the specified target object.
     * Return row is merged into object after execution of insert or update call
     * according to ReturningPolicy.
     */
    public Object readFromReturnRowIntoObject(AbstractRecord row, Object targetObject, CacheKey parentCacheKey, ReadObjectQuery query, Collection handledMappings) throws DatabaseException {
        Object aggregate = getAttributeValueFromObject(targetObject);
        if (aggregate == null) {
            aggregate = readFromRowIntoObject(row, null, targetObject, parentCacheKey, query, query.getSession(), true);
            if (handledMappings != null) {
                handledMappings.add(this);
            }
            return aggregate;
        }

        for (int i = 0; i < getReferenceFields().size(); i++) {
            DatabaseField field = getReferenceFields().elementAt(i);
            if (row.containsKey(field)) {
                getObjectBuilder(aggregate, query.getSession()).assignReturnValueForField(aggregate, parentCacheKey, query, row, field, handledMappings);
            }
        }

        if (isNullAllowed()) {
            boolean allAttributesNull = true;
            for (int i = 0; (i < getReferenceFields().size()) && allAttributesNull; i++) {
                DatabaseField field = fields.elementAt(i);
                if (row.containsKey(field)) {
                    allAttributesNull = row.get(field) == null;
                } else {
                    Object fieldValue = valueFromObject(targetObject, field, query.getSession());
                    if (fieldValue == null) {
                        Object baseValue = getDescriptor().getObjectBuilder().getBaseValueForField(field, targetObject);
                        if (baseValue != null) {
                            DatabaseMapping baseMapping = getDescriptor().getObjectBuilder().getBaseMappingForField(field);
                            if (baseMapping.isForeignReferenceMapping()) {
                                ForeignReferenceMapping refMapping = (ForeignReferenceMapping)baseMapping;
                                if (refMapping.usesIndirection()) {
                                    allAttributesNull = refMapping.getIndirectionPolicy().objectIsInstantiated(baseValue);
                                }
                            } else if (baseMapping.isTransformationMapping()) {
                                AbstractTransformationMapping transMapping = (AbstractTransformationMapping)baseMapping;
                                if (transMapping.usesIndirection()) {
                                    allAttributesNull = transMapping.getIndirectionPolicy().objectIsInstantiated(baseValue);
                                }
                            }
                        }
                    } else {
                        allAttributesNull = false;
                    }
                }
            }
            if (allAttributesNull) {
                aggregate = null;
                setAttributeValueInObject(targetObject, aggregate);
            }
        }
        if (handledMappings != null) {
            handledMappings.add(this);
        }
        return aggregate;
    }

    /**
     * INTERNAL:
     * Build an aggregate object from the specified row and put it
     * in the specified target object.
     */
    public Object readFromRowIntoObject(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object targetObject, CacheKey parentCacheKey, ObjectBuildingQuery sourceQuery, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        Object aggregate = buildAggregateFromRow(databaseRow, targetObject, parentCacheKey, joinManager, sourceQuery, false, executionSession, isTargetProtected);// don't just build a shallow original
        setAttributeValueInObject(targetObject, aggregate);
        return aggregate;
    }

    /**
     * INTERNAL:
     * Rehash any hashtables based on fields.
     * This is used to clone descriptors for aggregates, which hammer field names.
     */
    public void rehashFieldDependancies(AbstractSession session) {
        getReferenceDescriptor().rehashFieldDependancies(session);
    }

    /**
     * INTERNAL:
     * Return whether this mapping requires extra queries to update the rows if it is
     * used as a key in a map.  This will typically be true if there are any parts to this mapping
     * that are not read-only.
     */
    public boolean requiresDataModificationEventsForMapKey(){
        if (getReferenceDescriptor() != null){
            Iterator<DatabaseMapping> i = getReferenceDescriptor().getMappings().iterator();
            while (i.hasNext()){
                DatabaseMapping mapping = i.next();
                if (!mapping.isReadOnly()){
                    Iterator<DatabaseField> fields = mapping.getFields().iterator();
                    while (fields.hasNext()){
                        DatabaseField field = fields.next();
                        if (field.isUpdatable()){
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * INTERNAL:
     * Set a collection of the aggregate to source field name associations.
     */
    public void setAggregateToSourceFieldAssociations(Vector<Association> fieldAssociations) {
        Hashtable fieldNames = new Hashtable(fieldAssociations.size() + 1);
        for (Enumeration associationsEnum = fieldAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            fieldNames.put(association.getKey(), association.getValue());
        }

        setAggregateToSourceFields(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the hashtable that stores target field name to the source field name.
     */
    public void setAggregateToSourceFields(Map<String, DatabaseField> aggregateToSource) {
        aggregateToSourceFields = aggregateToSource;
    }

    /**
     * PUBLIC:
     * Configure if all the fields in the database row for the aggregate object are NULL,
     * then, by default, the mapping will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls).
     * To change this behavior, set the value of this variable to false. Then the mapping
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * 
     * Note: Any aggregate that has a relationship mapping automatically does not allow
     * null.
     */
    public void setIsNullAllowed(boolean isNullAllowed) {
        this.isNullAllowed = isNullAllowed;
    }

    /** 
     * INTERNAL:
     * If this mapping is used as the key of a CollectionTableMapMapping, the table used by this
     * mapping will be the relation table.  Set this table.
     */
    public void setTableForAggregateMappingKey(DatabaseTable table){
        aggregateKeyTable = table;
    }
    
    /**
     * INTERNAL:
     * Apply the field translation from the sourceField to the mappingField.
     */
    protected void translateField(DatabaseField sourceField, DatabaseField mappingField, ClassDescriptor clonedDescriptor) {
        // Do not modify non-translated fields.
        if (sourceField != null) {
            //merge fieldInSource into the field from the Aggregate descriptor
            mappingField.setName(sourceField.getName());
            mappingField.setUseDelimiters(sourceField.shouldUseDelimiters());
            mappingField.useUpperCaseForComparisons(sourceField.getUseUpperCaseForComparisons());
            mappingField.setNameForComparisons(sourceField.getNameForComparisons());
            //copy type information 
            mappingField.setNullable(sourceField.isNullable());
            mappingField.setUpdatable(sourceField.isUpdatable());
            mappingField.setInsertable(sourceField.isInsertable());
            mappingField.setUnique(sourceField.isUnique());
            mappingField.setScale(sourceField.getScale());
            mappingField.setLength(sourceField.getLength());
            mappingField.setPrecision(sourceField.getPrecision());
            mappingField.setColumnDefinition(sourceField.getColumnDefinition());

            // Check if the translated field specified a table qualifier.
            if (sourceField.hasTableName()) {
                mappingField.setTable(clonedDescriptor.getTable(sourceField.getTable().getName()));
            }
        }
    }
    
    /**
     * INTERNAL:
     * If field names are different in the source and aggregate objects then the translation
     * is done here. The aggregate field name is converted to source field name from the
     * field name mappings stored.
     */
    protected void translateFields(ClassDescriptor clonedDescriptor, AbstractSession session) {
        // Once the cloned descriptor is initialized, go through our nested 
        // field name translations. Any errors are silently ignored as
        // validation is assumed to be done before hand (JPA metadata processing
        // does validate any nested field translation)
        for (String attributeName : nestedFieldTranslations.keySet()) {
            DatabaseMapping mapping = null;
            ClassDescriptor currentDescriptor = clonedDescriptor;
            String currentAttributeName = attributeName.substring(0, attributeName.indexOf("."));
            String remainingAttributeName = attributeName.substring(attributeName.indexOf(".")+ 1);
            
            while (currentAttributeName != null || currentDescriptor != null) {
                mapping = currentDescriptor.getMappingForAttributeName(currentAttributeName);
                currentDescriptor = mapping.getReferenceDescriptor();
                
                // Keep breaking down the dot notation name.
                if (remainingAttributeName != null && remainingAttributeName.contains(".")) {
                    currentAttributeName = remainingAttributeName.substring(0, remainingAttributeName.indexOf("."));
                    remainingAttributeName = remainingAttributeName.substring(remainingAttributeName.indexOf(".")+ 1);
                } else {
                    currentAttributeName = remainingAttributeName;
                    // Null out the dot notation name to break the loop.
                    remainingAttributeName = null;
                }
            }
            
            // This shouldn't be possible since we validate in metadata processing. That is
			// the mapping can't be null and can't be anything but a direct to field mapping.
            if (mapping != null && mapping.isDirectToFieldMapping()) {
                // Translate the mapping field with the override field.
                translateField(nestedFieldTranslations.get(attributeName), ((DirectToFieldMapping) mapping).getField(), clonedDescriptor);
            }
        }
        
        // EL Bug 326977
        Vector fieldsToTranslate = (Vector) clonedDescriptor.getFields().clone();
        for (Iterator qkIterator = clonedDescriptor.getQueryKeys().values().iterator(); qkIterator.hasNext();) {
            QueryKey queryKey = (QueryKey)qkIterator.next();
            if (queryKey.isDirectQueryKey()) {
                DatabaseField field = ((DirectQueryKey)queryKey).getField();
                fieldsToTranslate.add(field);
            }
        }
        
        // EL Bug 332080 - translate foreign reference mapping source key fields
        if (!clonedDescriptor.getObjectBuilder().isSimple()) {
            for (Iterator dcIterator = clonedDescriptor.getMappings().iterator(); dcIterator.hasNext();) {
                DatabaseMapping mapping = (DatabaseMapping)dcIterator.next();
                if (mapping.isForeignReferenceMapping()) {
                    Collection fkFields = ((ForeignReferenceMapping)mapping).getFieldsForTranslationInAggregate();
                    if (fkFields != null && !fkFields.isEmpty()) {
                        fieldsToTranslate.addAll(fkFields);
                    }
                }
            }
        }
        
        for (Iterator entry = fieldsToTranslate.iterator(); entry.hasNext();) {
            DatabaseField field = (DatabaseField)entry.next();
            //322233 - get the source DatabaseField from the translation map. 
            translateField(getAggregateToSourceFields().get(field.getName()), field, clonedDescriptor);
        }

        clonedDescriptor.rehashFieldDependancies(session);
    }

    /**
     * INTERNAL:
     * Allow the key mapping to unwrap the object
     * @param key
     * @param session
     * @return
     */
    
    public Object unwrapKey(Object key, AbstractSession session){
        return key;
    }
    
    /**
     * INTERNAL:
     * Allow the key mapping to wrap the object
     * @param key
     * @param session
     * @return
     */
    public Object wrapKey(Object key, AbstractSession session){
        return key;
    }
    
    /**
     * INTERNAL:
     * A subclass should implement this method if it wants different behavior.
     * Write the foreign key values from the attribute to the row.
     */
    @Override
    public void writeFromAttributeIntoRow(Object attribute, AbstractRecord row, AbstractSession session){
        writeToRowFromAggregate(row, null, attribute, session, WriteType.UNDEFINED);
    }
    
    /**
     * INTERNAL:
     * Extract value of the field from the object
     */
    @Override
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) throws DescriptorException {
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue == null) {
            if (isNullAllowed()) {
                return null;
            } else {
                throw DescriptorException.nullForNonNullAggregate(object, this);
            }
        } else {
            return getObjectBuilder(attributeValue, session).extractValueFromObjectForField(attributeValue, field, session);
        }
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the appropriate
     * values to the specified database row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord databaseRow, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (isReadOnly()) {
            return;
        }
        writeToRowFromAggregate(databaseRow, object, getAttributeValueFromObject(object), session, writeType);
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the appropriate
     * values to the specified database row.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord databaseRow, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (isReadOnly()) {
            return;
        }
        writeToRowFromAggregateWithChangeRecord(databaseRow, changeRecord, (ObjectChangeSet)((AggregateChangeRecord)changeRecord).getChangedObject(), session, writeType);
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the changed
     * values to the specified database row.
     */
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery query, AbstractRecord databaseRow) throws DescriptorException {
        if (isReadOnly()) {
            return;
        }
        writeToRowFromAggregateForUpdate(databaseRow, query, getAttributeValueFromObject(query.getObject()));        
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    public void writeInsertFieldsIntoRow(AbstractRecord databaseRow, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }

        AbstractRecord targetRow = buildTemplateInsertRow(session);
        for (Enumeration keyEnum = targetRow.keys(); keyEnum.hasMoreElements();) {
            DatabaseField field = (DatabaseField)keyEnum.nextElement();
            Object value = targetRow.get(field);

            //CR-3286097 - Should use add not put, to avoid linear search.
            databaseRow.add(field, value);
        }
    }

    /**
     * INTERNAL:
     * Add a primary key join column (secondary field).
     * If this contain primary keys and the descriptor(or its subclass) has multiple tables
     * (secondary tables or joined inheritance strategy), this should also know the primary key 
     * join columns to handle some cases properly.
     */
    public void addPrimaryKeyJoinField(DatabaseField primaryKeyField, DatabaseField secondaryField) {
        // now it doesn't need to manage this as a separate table here,
        // it's enough just to add the mapping to ObjectBuilder.mappingsByField 
        ObjectBuilder builder = getReferenceDescriptor().getObjectBuilder();
        DatabaseMapping mapping = builder.getMappingForField(primaryKeyField);
        if (mapping != null) {
            builder.getMappingsByField().put(secondaryField, mapping); 
        }
    }
}
