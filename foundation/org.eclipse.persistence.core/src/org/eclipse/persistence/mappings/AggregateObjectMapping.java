/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;
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
     * then, by default, TopLink will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls).
     * To change this behavior, set the value of this variable to false. Then TopLink
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     */
    protected boolean isNullAllowed;

    protected DatabaseTable aggregateKeyTable  = null;
    
    /** Map the name of a field in the aggregate descriptor to a field in the source table. */
    protected transient Map<String, String> aggregateToSourceFieldNames;

    /**
     * Default constructor.
     */
    public AggregateObjectMapping() {
        aggregateToSourceFieldNames = new HashMap(5);
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
        Iterator i = getReferenceDescriptor().getMappings().iterator();
        while (i.hasNext()){
            DatabaseMapping mapping = (DatabaseMapping)i.next();
            Iterator selectFields = mapping.getSelectFields().iterator();
            while (selectFields.hasNext()){
                DatabaseField field = (DatabaseField)selectFields.next();
                if (selectionQuery.isObjectLevelReadQuery()){
                    if (baseExpression != null){
                        ((ObjectLevelReadQuery)selectionQuery).addAdditionalField(baseExpression.getField(field));
                    } else {
                        ((ObjectLevelReadQuery)selectionQuery).addAdditionalField((DatabaseField)field.clone());
                    }
                } else if (selectionQuery.isDataReadQuery()){
                    if (baseExpression == null){
                        ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField((DatabaseField)field.clone());
                        ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addTable((DatabaseTable)field.getTable().clone());
                    } else {
                        ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(baseExpression.getTable(field.getTable()).getField(field));
                    }
                    
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query
     */
    public void addFieldsForMapKey(AbstractRecord joinRow){
        Iterator i = getReferenceDescriptor().getMappings().iterator();
        while (i.hasNext()){
            joinRow.put(((DirectToFieldMapping)i.next()).getField(), null);
        }
    }
    
    /**
     * PUBLIC:
     * Add a field name translation that maps from a field name in the
     * source table to a field name in the aggregate descriptor.
     */
    public void addFieldNameTranslation(String sourceFieldName, String aggregateFieldName) {
        String unQualifiedAggregateFieldName = aggregateFieldName.substring(aggregateFieldName.lastIndexOf('.') + 1);// -1 is returned for no ".".
        getAggregateToSourceFieldNames().put(unQualifiedAggregateFieldName, sourceFieldName);
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
     * then, by default, TopLink will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls). This behavior can be
     * explicitly set by calling #allowNull().
     * To change this behavior, call #dontAllowNull(). Then TopLink
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * In either situation, when writing, TopLink will place a NULL in all the
     * fields in the database row for the aggregate object.
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
    public Object buildAggregateFromRow(AbstractRecord databaseRow, Object targetObject, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, boolean buildShallowOriginal, AbstractSession executionSession) throws DatabaseException {
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
        if (sourceQuery.isObjectLevelReadQuery()){
            if (((ObjectLevelReadQuery)nestedQuery).isPartialAttribute(getAttributeName()) || ((joinManager != null) && joinManager.isAttributeJoined(getDescriptor(), getAttributeName()))) {
                // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                nestedQuery = (ObjectLevelReadQuery)nestedQuery.deepClone();
                // Must cascade the nested partial/join expression and filter the nested ones.
                if (((ObjectLevelReadQuery)nestedQuery).hasPartialAttributeExpressions()) {
                    ((ObjectLevelReadQuery)nestedQuery).setPartialAttributeExpressions(extractNestedExpressions(((ObjectLevelReadQuery)sourceQuery).getPartialAttributeExpressions(), ((ObjectLevelReadQuery)nestedQuery).getExpressionBuilder(), false));
                } else {
                    ((ObjectLevelReadQuery)nestedQuery).getJoinedAttributeManager().setJoinedAttributeExpressions_(extractNestedExpressions(joinManager.getJoinedAttributeExpressions(), joinManager.getBaseExpressionBuilder(), false));
                }
                nestedQuery.setDescriptor(descriptor);
            }
        }
        if (sourceQuery.isReadAllQuery() && ((ReadAllQuery)sourceQuery).isAttributeBatchRead(getDescriptor(), getAttributeName())) {
            // A nested query must be built to pass to the descriptor that looks like the real query execution would.
            nestedQuery = (ObjectLevelReadQuery)sourceQuery.clone();
            // Must carry over properties for batching to work.
            nestedQuery.setProperties(sourceQuery.getProperties());
            // Computed nested batch attribute expressions.
            ((ReadAllQuery)nestedQuery).setBatchReadAttributeExpressions(extractNestedExpressions(((ReadAllQuery)sourceQuery).getBatchReadAttributeExpressions(), ((ReadAllQuery)nestedQuery).getExpressionBuilder(), false));
        }                               

        if (buildShallowOriginal) {
            descriptor.getObjectBuilder().buildAttributesIntoShallowObject(aggregate, databaseRow, nestedQuery);
        } else if (executionSession.isUnitOfWork()) {
            descriptor.getObjectBuilder().buildAttributesIntoWorkingCopyClone(aggregate, nestedQuery, joinManager, databaseRow, (UnitOfWorkImpl)executionSession, refreshing);
        } else {
            descriptor.getObjectBuilder().buildAttributesIntoObject(aggregate, databaseRow, nestedQuery, joinManager, refreshing);
        }
        return aggregate;
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
    protected void writeToRowFromAggregate(AbstractRecord record, Object object, Object attributeValue, AbstractSession session) throws DescriptorException {
        if (attributeValue == null) {
            if (this.isNullAllowed) {
                writeNullReferenceRow(record);
            } else {
                throw DescriptorException.nullForNonNullAggregate(object, this);
            }
        } else {
            if (!session.isClassReadOnly(attributeValue.getClass())) {
                getObjectBuilder(attributeValue, session).buildRow(record, attributeValue, session);
            }
        }
    }

    /**
     * INTERNAL:
     * Build and return a database row built with the values from
     * the specified attribute value.
     */
    protected void writeToRowFromAggregateWithChangeRecord(AbstractRecord record, ChangeRecord changeRecord, ObjectChangeSet objectChangeSet, AbstractSession session) throws DescriptorException {
        if (objectChangeSet == null) {
            if (this.isNullAllowed) {
                writeNullReferenceRow(record);
            } else {
                Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
                throw DescriptorException.nullForNonNullAggregate(object, this);
            }
        } else {
            if (!session.isClassReadOnly(objectChangeSet.getClassType(session))) {
                getReferenceDescriptor(objectChangeSet.getClassType(session), session).getObjectBuilder().buildRowWithChangeSet(record, objectChangeSet, session);
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
            getObjectBuilder(attributeValue, query.getSession()).buildRow(record, attributeValue, query.getSession());
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
    public void buildClone(Object original, Object clone, UnitOfWorkImpl unitOfWork) {
        Object attributeValue = getAttributeValueFromObject(original);
        Object aggregateClone = buildClonePart(original, attributeValue, unitOfWork);

        if (aggregateClone != null) {
            ClassDescriptor descriptor = getReferenceDescriptor(aggregateClone, unitOfWork);
            descriptor.getObjectChangePolicy().setAggregateChangeListener(clone, aggregateClone, unitOfWork, descriptor, getAttributeName());
        }

        setAttributeValueInObject(clone, aggregateClone);
    }
    
    /**
     * INTERNAL:
     * Build a clone of the given element in a unitOfWork
     * @param element
     * @param unitOfWork
     * @param isExisting
     * @return
     */
    public Object buildElementClone(Object attributeValue, UnitOfWorkImpl unitOfWork, boolean isExisting){
        // TODO: Aggregate Change listener not currently added should investigate whether that is possible
        return buildClonePart(attributeValue, unitOfWork, isExisting);
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
    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        // This method is a combination of buildggregateFromRow and
        // buildClonePart on the super class.
        // none of buildClonePart used, as not an orignal new object, nor
        // do we worry about creating heavy clones for aggregate objects.
        Object clonedAttributeValue = buildAggregateFromRow(databaseRow, clone, joinManager, sourceQuery, false, executionSession);
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
        Object aggregate = buildAggregateFromRow(databaseRow, original, joinManager, sourceQuery, true, executionSession);// shallow only.
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
            Enumeration children = getReferenceDescriptor().getInheritancePolicy().getChildDescriptors().elements();
            while (children.hasMoreElements()) {
                Enumeration mappings = ((ClassDescriptor)children.nextElement()).getMappings().elements();
                while (mappings.hasMoreElements()) {
                    DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();

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
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, however mappings from the referenced object may need cascading.
        Object objectReferenced = getAttributeValueFromObject(object);
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
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //aggregate objects are not registered but their mappings should be.
        Object objectReferenced = getAttributeValueFromObject(object);
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
     * Return the fields handled by the mapping.
     */
    protected Vector<DatabaseField> collectFields() {
        return getReferenceFields();
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, AbstractSession session){
        Object key = getReferenceDescriptor().getObjectBuilder().buildNewInstance();
        key = buildAggregateFromRow(dbRow, null, null, query, false, session);
        return key;
    }
    
    
    /**
     * PUBLIC:
     * If <em>all</em> the fields in the database row for the aggregate object are NULL,
     * then, by default, TopLink will place a null in the appropriate source object
     * (as opposed to an aggregate object filled with nulls). This behavior can be
     * explicitly set by calling #allowNull().
     * To change this behavior, call #dontAllowNull(). Then TopLink
     * will build a new instance of the aggregate object that is filled with nulls
     * and place it in the source object.
     * In either situation, when writing, TopLink will place a NULL in all the
     * fields in the database row for the aggregate object.
     */
    public void dontAllowNull() {
        setIsNullAllowed(false);
    }

    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query
     * @return
     */
    public Map extractIdentityFieldsForQuery(Object object, AbstractSession session){
        Map keyFields = new HashMap();
        ClassDescriptor descriptor =getReferenceDescriptor();
        Iterator i = descriptor.getMappings().iterator();
        while (i.hasNext()){
            DatabaseMapping mapping = (DatabaseMapping)i.next();
            DatabaseField field = mapping.getField();
            Object value = descriptor.getObjectBuilder().extractValueFromObjectForField(object, field, session);
            keyFields.put(field, value);
        }
        return keyFields;
    }
    
    /**
     * INTERNAL:
     * Return a collection of the aggregate to source field name associations.
     */
    public Vector<Association> getAggregateToSourceFieldNameAssociations() {
        Vector<Association> associations = new Vector(getAggregateToSourceFieldNames().size());
        Iterator aggregateEnum = getAggregateToSourceFieldNames().keySet().iterator();
        Iterator sourceEnum = getAggregateToSourceFieldNames().values().iterator();
        while (aggregateEnum.hasNext()) {
            associations.addElement(new Association(aggregateEnum.next(), sourceEnum.next()));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Return the hashtable that stores aggregate field name to source field name.
     */
    public Map<String, String> getAggregateToSourceFieldNames() {
        return aggregateToSourceFieldNames;
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
        return getReferenceDescriptor().getAllFields();
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
     * Since aggregate object mappings clone their descriptors, for inheritance the correct child clone must be found.
     */
    protected ClassDescriptor getReferenceDescriptor(Class theClass, AbstractSession session) {
        if (getReferenceDescriptor().getJavaClass().equals(theClass)) {
            return getReferenceDescriptor();
        }

        ClassDescriptor subDescriptor = getReferenceDescriptor().getInheritancePolicy().getSubclassDescriptor(theClass);
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
    public Object getTargetVersionOfSourceObject(Object object, MergeManager mergeManager){
       return object;
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

        initializeReferenceDescriptor(clonedDescriptor);
        clonedDescriptor.preInitialize(session);
        clonedDescriptor.initialize(session);
        translateFields(clonedDescriptor, session);

        if (clonedDescriptor.hasInheritance() && clonedDescriptor.getInheritancePolicy().hasChildren()) {
            //clone child descriptors
            initializeChildInheritance(clonedDescriptor, session);
        }

        setFields(collectFields());
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
            Vector childDescriptors = parentDescriptor.getInheritancePolicy().getChildDescriptors();
            Vector cloneChildDescriptors = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            for (Enumeration enumtr = childDescriptors.elements(); enumtr.hasMoreElements();) {
                ClassDescriptor clonedChildDescriptor = (ClassDescriptor)((ClassDescriptor)enumtr.nextElement()).clone();
                clonedChildDescriptor.getInheritancePolicy().setParentDescriptor(parentDescriptor);
                initializeReferenceDescriptor(clonedChildDescriptor);
                clonedChildDescriptor.preInitialize(session);
                clonedChildDescriptor.initialize(session);
                translateFields(clonedChildDescriptor, session);
                cloneChildDescriptors.addElement(clonedChildDescriptor);
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
     * INTERNAL:
     * Return setting.
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
            // Changed as part of fix for bug#4410581 aggregate mapping can not be set to use change tracking if owning descriptor does not use it.
            // Basically the policies should be the same, but we also allow deferred with attribute for CMP2 (courser grained).
            if (getDescriptor().getObjectChangePolicy().getClass().equals(DeferredChangeDetectionPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new DeferredChangeDetectionPolicy());
            } else if (getDescriptor().getObjectChangePolicy().getClass().equals(ObjectChangeTrackingPolicy.class)
                    && getReferenceDescriptor().getObjectChangePolicy().getClass().equals(AttributeChangeTrackingPolicy.class)) {
                getReferenceDescriptor().setObjectChangePolicy(new ObjectChangeTrackingPolicy());
            }
            getReferenceDescriptor().postInitialize(session);
        }
    }
    
    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping
     */
    public void preinitializeMapKey(DatabaseTable table) throws DescriptorException {
        setTableForAggregateMappingKey(table);
    }

    
    /**
     * INTERNAL:
     * Allow the selectionQuery to be modified when this MapComponentMapping is used as the value in a Map
     */
    public void postInitializeMapValueSelectionQuery(ReadQuery selectionQuery, AbstractSession session){
        selectionQuery.setShouldMaintainCache(false);
    }
    
    /**
     * INTERNAL:
     * Build an aggregate object from the specified return row and put it
     * in the specified target object.
     * Return row is merged into object after execution of insert or update call
     * accordiing to ReturningPolicy.
     */
    public Object readFromReturnRowIntoObject(AbstractRecord row, Object targetObject, ReadObjectQuery query, Collection handledMappings) throws DatabaseException {
        Object aggregate = getAttributeValueFromObject(targetObject);
        if (aggregate == null) {
            aggregate = readFromRowIntoObject(row, null, targetObject, query, query.getSession());
            if (handledMappings != null) {
                handledMappings.add(this);
            }
            return aggregate;
        }

        for (int i = 0; i < getReferenceFields().size(); i++) {
            DatabaseField field = getReferenceFields().elementAt(i);
            if (row.containsKey(field)) {
                getObjectBuilder(aggregate, query.getSession()).assignReturnValueForField(aggregate, query, row, field, handledMappings);
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
    public Object readFromRowIntoObject(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object targetObject, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        Object aggregate = buildAggregateFromRow(databaseRow, targetObject, joinManager, sourceQuery, false, executionSession);// don't just build a shallow original
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
     * Set a collection of the aggregate to source field name associations.
     */
    public void setAggregateToSourceFieldNameAssociations(Vector<Association> fieldAssociations) {
        Hashtable fieldNames = new Hashtable(fieldAssociations.size() + 1);
        for (Enumeration associationsEnum = fieldAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            fieldNames.put(association.getKey(), association.getValue());
        }

        setAggregateToSourceFieldNames(fieldNames);
    }

    /**
     * INTERNAL:
     * Set the hashtable that stores target field name to the source field name.
     */
    protected void setAggregateToSourceFieldNames(Map<String, String> aggregateToSource) {
        aggregateToSourceFieldNames = aggregateToSource;
    }

    /**
     * INTERNAL:
     * Will be used by Gromit only.
     */
    public void setIsNullAllowed(boolean aBoolean) {
        isNullAllowed = aBoolean;
    }

    /** 
     * INTERNAL:
     * If this mapping is used as the key of a CollectionTableMapMapping, the table used by this
     * mapping will be the relation table.  Set this table
     * @return
     */
    public void setTableForAggregateMappingKey(DatabaseTable table){
        aggregateKeyTable = table;
    }
    
    /**
     * INTERNAL:
     * If field names are different in the source and aggregate objects then the translation
     * is done here. The aggregate field name is converted to source field name from the
     * field name mappings stored.
     */
    protected void translateFields(ClassDescriptor clonedDescriptor, AbstractSession session) {
        for (Enumeration entry = clonedDescriptor.getFields().elements(); entry.hasMoreElements();) {
            DatabaseField field = (DatabaseField)entry.nextElement();
            String nameInAggregate = field.getName();
            String nameInSource = getAggregateToSourceFieldNames().get(nameInAggregate);

            // Do not modify non-translated fields.
            if (nameInSource != null) {
                DatabaseField fieldInSource = new DatabaseField(nameInSource);

                // Check if the translated field specified a table qualifier.
                if (fieldInSource.getName().equals(nameInSource)) {
                    // No table so just set the field name.
                    field.setName(nameInSource);
                } else {
                    // There is a table, so set the name and table.
                    field.setName(fieldInSource.getName());
                    field.setTable(clonedDescriptor.getTable(fieldInSource.getTable().getName()));
                }
            }
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
    public void writeFromAttributeIntoRow(Object attribute, AbstractRecord row, AbstractSession session){
        writeToRowFromAggregate(row, null, attribute, session);
    }
    /**
     * INTERNAL:
     * Extract value of the field from the object
     */
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
    public void writeFromObjectIntoRow(Object object, AbstractRecord databaseRow, AbstractSession session) throws DescriptorException {
        if (isReadOnly()) {
            return;
        }
        writeToRowFromAggregate(databaseRow, object, getAttributeValueFromObject(object), session);
    }

    /**
     * INTERNAL:
     * Get the attribute value from the object and add the appropriate
     * values to the specified database row.
     */
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord databaseRow, AbstractSession session) throws DescriptorException {
        if (isReadOnly()) {
            return;
        }
        writeToRowFromAggregateWithChangeRecord(databaseRow, changeRecord, (ObjectChangeSet)((AggregateChangeRecord)changeRecord).getChangedObject(), session);
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
