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
 *     05/14/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     02/11/2013-2.5 Guy Pelletier 
 *       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.*;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.descriptors.CascadeLockingPolicy;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * <p><b>Purpose</b>: One to one mappings are used to represent a pointer references
 * between two java objects. This mappings is usually represented by a single pointer
 * (stored in an instance variable) between the source and target objects. In the relational
 * database tables, these mappings are normally implemented using foreign keys.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class OneToOneMapping extends ObjectReferenceMapping implements RelationalMapping, MapKeyMapping {

    /** Maps the source foreign/primary key fields to the target primary/foreign key fields. */
    protected Map<DatabaseField, DatabaseField> sourceToTargetKeyFields;

    /** Maps the target primary/foreign key fields to the source foreign/primary key fields. */
    protected Map<DatabaseField, DatabaseField> targetToSourceKeyFields;

    /** Keeps track of which fields are foreign keys on a per field basis (can have mixed foreign key relationships). */
    /** These are used for non-unit of work modification to check if the value of the 1-1 was changed and a deletion is required. */
    protected boolean shouldVerifyDelete;
    protected transient Expression privateOwnedCriteria;
    
    public DatabaseTable keyTableForMapKey = null;

    protected static final String setObject = "setObject";
    
    /** Mechanism holds relationTable and all fields and queries associated with it. */
    protected RelationTableMechanism mechanism;

    /**
     * Define if this mapping is really for a OneToOne relationship.
     * This is a backward compatibility issue, in that before the ManyToOneMapping
     * was created OneToOneMapping was used for both.
     */
    protected boolean isOneToOneRelationship = false;
    
    /**
     * Defines if this mapping was built using primary key join columns.
     */
    protected boolean isOneToOnePrimaryKeyRelationship = false;
    
    /**
     * Keep track of which fields are insertable and updatable.
     */
    protected HashSet<DatabaseField> insertableFields = new HashSet<DatabaseField>();
    protected HashSet<DatabaseField> updatableFields = new HashSet<DatabaseField>();
    
    /**
     * Mode for writeFromObjectIntoRowInternal method
     */
    protected static enum ShallowMode {
        Insert,
        UpdateAfterInsert,
        UpdateBeforeDelete
    }
    
    /**
     * PUBLIC:
     * Default constructor.
     */
    public OneToOneMapping() {
        this.selectionQuery = new ReadObjectQuery();
        this.sourceToTargetKeyFields = new HashMap(2);
        this.targetToSourceKeyFields = new HashMap(2);
        this.foreignKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.isForeignKeyRelationship = false;
        this.shouldVerifyDelete = true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map.
     * Called when the selection query is being initialized to add the fields for the map key to the query.
     */
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression){
        for (DatabaseField field : getForeignKeyFields()) {
            if (selectionQuery.isObjectLevelReadQuery()){
                ((ObjectLevelReadQuery)selectionQuery).addAdditionalField(baseExpression.getField(field));
            } else if (selectionQuery.isDataReadQuery()){
                ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(field);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query
     */
    public void addFieldsForMapKey(AbstractRecord joinRow){
        Iterator i = getForeignKeyFields().iterator();
        while (i.hasNext()){
            joinRow.put((DatabaseField)i.next(), null);
        }
    }
    
    /**
     * PUBLIC:
     * Define the foreign key relationship in the 1-1 mapping.
     * This method is used for composite foreign key relationships,
     * that is the source object's table has multiple foreign key fields to
     * the target object's primary key fields.
     * Both the source foreign key field and the target foreign key field must 
     * be specified.
     * When a foreign key is specified TopLink will automatically populate the 
     * value for that field from the target object when the object is written to 
     * the database. If the foreign key is also mapped through a direct-to-field 
     * then the direct-to-field must be set read-only.
     */
    @Override
    public void addForeignKeyField(DatabaseField sourceForeignKeyField, DatabaseField targetPrimaryKeyField) {
        setIsForeignKeyRelationship(true);
        getForeignKeyFields().addElement(sourceForeignKeyField);

        getSourceToTargetKeyFields().put(sourceForeignKeyField, targetPrimaryKeyField);
        getTargetToSourceKeyFields().put(targetPrimaryKeyField, sourceForeignKeyField);
    }
    
    /**
     * PUBLIC:
     * Define the foreign key relationship in the 1-1 mapping.
     * This method is used for composite foreign key relationships,
     * that is the source object's table has multiple foreign key fields to
     * the target object's primary key fields.
     * Both the source foreign key field name and the target foreign key field 
     * name must be specified.
     * When a foreign key is specified TopLink will automatically populate the 
     * value for that field from the target object when the object is written to 
     * the database. If the foreign key is also mapped through a direct-to-field 
     * then the direct-to-field must be set read-only.
     */
    public void addForeignKeyFieldName(String sourceForeignKeyFieldName, String targetPrimaryKeyFieldName) {
        addForeignKeyField(new DatabaseField(sourceForeignKeyFieldName), new DatabaseField(targetPrimaryKeyFieldName));
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-1 mapping.
     * This method is used for composite target foreign key relationships,
     * that is the target object's table has multiple foreign key fields to
     * the source object's primary key fields.
     * Both the target foreign key field and the source primary key field must 
     * be specified.
     * The distinction between a foreign key and target foreign key is that the 
     * 1-1 mapping will not populate the target foreign key value when written 
     * (because it is in the target table). Normally 1-1's are through foreign 
     * keys but in bi-directional 1-1's the back reference will be a target 
     * foreign key. In obscure composite legacy data models a 1-1 may consist of 
     * a foreign key part and a target foreign key part, in this case both 
     * method will be called with the correct parts.
     */
    @Override
    public void addTargetForeignKeyField(DatabaseField targetForeignKeyField, DatabaseField sourcePrimaryKeyField) {
        getSourceToTargetKeyFields().put(sourcePrimaryKeyField, targetForeignKeyField);
        getTargetToSourceKeyFields().put(targetForeignKeyField, sourcePrimaryKeyField);
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-1 mapping.
     * This method is used for composite target foreign key relationships,
     * that is the target object's table has multiple foreign key fields to
     * the source object's primary key fields.
     * Both the target foreign key field name and the source primary key field 
     * name must be specified.
     * The distinction between a foreign key and target foreign key is that the 
     * 1-1 mapping will not populate the target foreign key value when written 
     * (because it is in the target table). Normally 1-1's are through foreign 
     * keys but in bi-directional 1-1's the back reference will be a target 
     * foreign key. In obscure composite legacy data models a 1-1 may consist of 
     * a foreign key part and a target foreign key part, in this case both 
     * method will be called with the correct parts.
     */
    public void addTargetForeignKeyFieldName(String targetForeignKeyFieldName, String sourcePrimaryKeyFieldName) {
        addTargetForeignKeyField(new DatabaseField(targetForeignKeyFieldName), new DatabaseField(sourcePrimaryKeyFieldName));
    }

    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy.  Add the target of this mapping to the deleted 
     * objects list if necessary
     *
     * This method is used for removal of private owned relationships.
     */
    public void addKeyToDeletedObjectsList(Object object, Map deletedObjects){
        deletedObjects.put(object, object);
    }
    
    /**
     * Build a clone of the given element in a unitOfWork.
     */
    public Object buildElementClone(Object attributeValue, Object parent, CacheKey cacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache){
        return buildCloneForPartObject(attributeValue, null, cacheKey, parent, cloningSession, refreshCascade, isExisting, isFromSharedCache);
    }
    
    /**
     * INTERNAL:
     * Used to allow object level comparisons.
     */
    public Expression buildObjectJoinExpression(Expression expression, Object value, AbstractSession session) {
        Expression base = ((ObjectExpression)expression).getBaseExpression();
        Expression foreignKeyJoin = null;

        if(this.mechanism == null) {
            // Allow for equal null.
            if (value == null) {
                if (!isForeignKeyRelationship()) {
                    // ELBug#331352
                    // Need to do a join and compare target foreign key to null.
                    for (DatabaseField field : getSourceToTargetKeyFields().values()) {
                        Expression join = null;
                        join = expression.getField(field).equal(null);
                        if (foreignKeyJoin == null) {
                            foreignKeyJoin = join;
                        } else {
                            foreignKeyJoin = foreignKeyJoin.and(join);
                        }
                    }
                } else {
                    for (DatabaseField field : getSourceToTargetKeyFields().keySet()) {
                        Expression join = null;
                        join = base.getField(field).equal(null);
                        if (foreignKeyJoin == null) {
                            foreignKeyJoin = join;
                        } else {
                            foreignKeyJoin = foreignKeyJoin.and(join);
                        }
                    }
                }
            } else {
                if (!getReferenceDescriptor().getJavaClass().isInstance(value)) {
                    // Bug 3894351 - ensure any proxys are triggered so we can do a proper class comparison
                    value = ProxyIndirectionPolicy.getValueFromProxy(value);
                    if (!getReferenceDescriptor().getJavaClass().isInstance(value)) {
                        throw QueryException.incorrectClassForObjectComparison(base, value, this);
                    }
                }
    
                Iterator keyIterator = Arrays.asList(((CacheId)extractKeyFromReferenceObject(value, session)).getPrimaryKey()).iterator();
                for (DatabaseField field : getSourceToTargetKeyFields().keySet()) {
                    Expression join = null;
                    join = base.getField(field).equal(keyIterator.next());
                    if (foreignKeyJoin == null) {
                        foreignKeyJoin = join;
                    } else {
                        foreignKeyJoin = foreignKeyJoin.and(join);
                    }
                }
            }
        } else {
            int size = this.mechanism.sourceKeyFields.size();
            Object key = null;
            if (value != null) {
                if (!getReferenceDescriptor().getJavaClass().isInstance(value)) {
                    // Bug 3894351 - ensure any proxys are triggered so we can do a proper class comparison
                    value = ProxyIndirectionPolicy.getValueFromProxy(value);
                    if (!getReferenceDescriptor().getJavaClass().isInstance(value)) {
                        throw QueryException.incorrectClassForObjectComparison(base, value, this);
                    }
                }
                key = extractKeyFromReferenceObject(value, session);
                boolean allNulls = true;
                for (int i=0; i < size; i++) {
                    if (((CacheId)key).getPrimaryKey()[i] != null) {
                        allNulls = false;
                        break;
                    }
                }
                // the same case
                if (allNulls) {
                    value = null;
                }
            }
            if (value != null) {
                for(int i=0; i < size; i++) {
                    DatabaseField field = this.mechanism.sourceKeyFields.get(i);
                    Expression join = null;
                    join = base.getField(field).equal(((CacheId)key).getPrimaryKey()[i]);
                    if (foreignKeyJoin == null) {
                        foreignKeyJoin = join;
                    } else {
                        foreignKeyJoin = foreignKeyJoin.and(join);
                    }
                }
            } else {
                ReportQuery subQuery = new ReportQuery(this.descriptor.getJavaClass(), new ExpressionBuilder());
                Expression relationTableExp = subQuery.getExpressionBuilder().getTable(this.mechanism.relationTable);
                Expression subSelectExp = null;
                for(int i=0; i < size; i++) {
                    subSelectExp = relationTableExp.getField(this.mechanism.sourceRelationKeyFields.get(i)).equal(base.getField(this.mechanism.sourceKeyFields.get(i))).and(subSelectExp);
                }
                subQuery.setSelectionCriteria(subSelectExp);
                subQuery.dontRetrievePrimaryKeys();
                subQuery.addAttribute("", subQuery.getExpressionBuilder().getField(this.mechanism.sourceKeyFields.get(0)));
                foreignKeyJoin = base.notExists(subQuery);
            }
        }
        return foreignKeyJoin;
    }

    /**
     * INTERNAL:
     * Used to allow object level comparisons.
     */
    public Expression buildObjectJoinExpression(Expression expression, Expression argument, AbstractSession session) {
        Expression base = ((org.eclipse.persistence.internal.expressions.ObjectExpression)expression).getBaseExpression();
        Expression foreignKeyJoin = null;
        if(this.mechanism == null) {
            if (expression==argument){
                for (Iterator sourceFieldsEnum = getSourceToTargetKeyFields().keySet().iterator();
                         sourceFieldsEnum.hasNext();) {
                    DatabaseField field = (DatabaseField)sourceFieldsEnum.next();
                    Expression join = base.getField(field);
                    join = join.equal(join);
                    if (foreignKeyJoin == null) {
                        foreignKeyJoin = join;
                    } else {
                        foreignKeyJoin = foreignKeyJoin.and(join);
                    }
                }
            }else{
                Iterator targetFieldsEnum = getSourceToTargetKeyFields().values().iterator();
                for (Iterator sourceFieldsEnum = getSourceToTargetKeyFields().keySet().iterator();
                         sourceFieldsEnum.hasNext();) {
                    DatabaseField sourceField = (DatabaseField)sourceFieldsEnum.next();
                    DatabaseField targetField = (DatabaseField)targetFieldsEnum.next();
                    Expression join = null;
                    join = base.getField(sourceField).equal(argument.getField(targetField));
                    if (foreignKeyJoin == null) {
                        foreignKeyJoin = join;
                    } else {
                        foreignKeyJoin = foreignKeyJoin.and(join);
                    }
                }
            }
        } else {
            if (expression==argument){
                foreignKeyJoin = (new ConstantExpression(0, base)).equal(new ConstantExpression(0, base));
            }else{
                int size = this.mechanism.sourceKeyFields.size();
                Expression relTable = base.getTable(this.mechanism.getRelationTable());
                for(int i=0; i < size; i++) {
                    Expression source = base.getField(this.mechanism.sourceKeyFields.get(i));
                    Expression sourceRel = relTable.getField(this.mechanism.sourceRelationKeyFields.get(i));
                    Expression targetRel = relTable.getField(this.mechanism.targetRelationKeyFields.get(i));
                    Expression target = argument.getField(this.mechanism.targetKeyFields.get(i));
                    foreignKeyJoin = source.equal(sourceRel).and(targetRel.equal(target)).and(foreignKeyJoin);
                }
            }
        }
        return foreignKeyJoin;
    }

    /**
     * INTERNAL:
     * Certain key mappings favor different types of selection query.  Return the appropriate
     * type of selectionQuery
     * @return
     */
    public ReadQuery buildSelectionQueryForDirectCollectionKeyMapping(ContainerPolicy containerPolicy){
        DataReadQuery query = new DataReadQuery();
        query.setSQLStatement(new SQLSelectStatement());
        query.setContainerPolicy(containerPolicy);
        return query;
    }
    
    /**
     * INTERNAL:
     * This methods clones all the fields and ensures that each collection refers to
     * the same clones.
     */
    @Override
    public Object clone() {
        OneToOneMapping clone = (OneToOneMapping)super.clone();
        if(this.mechanism == null) {
            clone.setForeignKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getForeignKeyFields().size()));
            clone.setSourceToTargetKeyFields(new HashMap(getSourceToTargetKeyFields().size()));
            clone.setTargetToSourceKeyFields(new HashMap(getTargetToSourceKeyFields().size()));
            Hashtable setOfFields = new Hashtable(getTargetToSourceKeyFields().size());
    
            //clone foreign keys and save the clones in a set
            for (Enumeration enumtr = getForeignKeyFields().elements(); enumtr.hasMoreElements();) {
                DatabaseField field = (DatabaseField)enumtr.nextElement();
                DatabaseField fieldClone = field.clone();
                setOfFields.put(field, fieldClone);
                clone.getForeignKeyFields().addElement(fieldClone);
            }
    
            //get clones from set for source hashtable.  If they do not exist, create a new one.
            for (Iterator sourceEnum = getSourceToTargetKeyFields().keySet().iterator();
                     sourceEnum.hasNext();) {
                DatabaseField sourceField = (DatabaseField)sourceEnum.next();
                DatabaseField targetField = getSourceToTargetKeyFields().get(sourceField);
    
                DatabaseField targetClone;
                DatabaseField sourceClone;
    
                targetClone = (DatabaseField)setOfFields.get(targetField);
                if (targetClone == null) {
                    targetClone = targetField.clone();
                    setOfFields.put(targetField, targetClone);
                }
                sourceClone = (DatabaseField)setOfFields.get(sourceField);
                if (sourceClone == null) {
                    sourceClone = sourceField.clone();
                    setOfFields.put(sourceField, sourceClone);
                }
                clone.getSourceToTargetKeyFields().put(sourceClone, targetClone);
            }
    
            //get clones from set for target hashtable.  If they do not exist, create a new one.
            for (Iterator targetEnum = getTargetToSourceKeyFields().keySet().iterator();
                     targetEnum.hasNext();) {
                DatabaseField targetField = (DatabaseField)targetEnum.next();
                DatabaseField sourceField = getTargetToSourceKeyFields().get(targetField);
    
                DatabaseField targetClone;
                DatabaseField sourceClone;
    
                targetClone = (DatabaseField)setOfFields.get(targetField);
                if (targetClone == null) {
                    targetClone = targetField.clone();
                    setOfFields.put(targetField, targetClone);
                }
                sourceClone = (DatabaseField)setOfFields.get(sourceField);
                if (sourceClone == null) {
                    sourceClone = sourceField.clone();
                    setOfFields.put(sourceField, sourceClone);
                }
                clone.getTargetToSourceKeyFields().put(targetClone, sourceClone);
            }
        } else {
            clone.mechanism = (RelationTableMechanism)this.mechanism.clone();
        }
        return clone;
    }
    
    @Override
    public void collectQueryParameters(Set<DatabaseField> cacheFields){
        for (DatabaseField field : sourceToTargetKeyFields.keySet()) {
            cacheFields.add(field);
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
     * Creates the Array of simple types used to recreate this map.  
     */
    public Object createSerializableMapKeyInfo(Object key, AbstractSession session){
        return referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(key, session);
    }

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.  
     * This may return the value directly in case of a simple key or will be used as the FK to load a related entity.
     */
    public List<Object> createMapComponentsFromSerializableKeyInfo(Object[] keyInfo, AbstractSession session){
        List<Object> orderedResult = new ArrayList<Object>(keyInfo.length);
        Map<Object, Object> fromCache = session.getIdentityMapAccessor().getAllFromIdentityMapWithEntityPK(keyInfo, referenceDescriptor);
        DatabaseRecord translationRow = new DatabaseRecord();
        List foreignKeyValues = new ArrayList(keyInfo.length - fromCache.size());
        
        CacheKeyType cacheKeyType = referenceDescriptor.getCachePolicy().getCacheKeyType();
        for (int index = 0; index < keyInfo.length; ++index){
            Object pk = keyInfo[index];
            if (!fromCache.containsKey(pk)){
                if (cacheKeyType == CacheKeyType.CACHE_ID){
                    foreignKeyValues.add(Arrays.asList(((CacheId)pk).getPrimaryKey()));
                }else{
                    foreignKeyValues.add(pk);
                }
            }
        }
        if (!foreignKeyValues.isEmpty()){
            translationRow.put(ForeignReferenceMapping.QUERY_BATCH_PARAMETER, foreignKeyValues);
            ReadAllQuery query = new ReadAllQuery(referenceDescriptor.getJavaClass());
            query.setIsExecutionClone(true);
            query.setTranslationRow(translationRow);
            query.setSession(session);
            query.setSelectionCriteria(referenceDescriptor.buildBatchCriteriaByPK(query.getExpressionBuilder(), query));
            Collection<Object> temp = (Collection<Object>) session.executeQuery(query);
            for (Object element: temp){
                Object pk = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(element, session);
                fromCache.put(pk, element);
            }
        }
        for(Object key : keyInfo){
            orderedResult.add(fromCache.get(key));
        }
        return orderedResult;
    }

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.  
     * This key object may be a shallow stub of the actual object if the key is an Entity type.
     */
    public Object createStubbedMapComponentFromSerializableKeyInfo(Object keyInfo, AbstractSession session) {
        ObjectBuilder builder = this.referenceDescriptor.getObjectBuilder();
        ObjectBuildingQuery clonedQuery = (ObjectBuildingQuery) getSelectionQuery().clone();
        clonedQuery.setSession(session);
        Object newObject = referenceDescriptor.getInstantiationPolicy().buildNewInstance();
        builder.buildPrimaryKeyAttributesIntoObject(newObject, builder.buildRowFromPrimaryKeyValues(keyInfo, session), clonedQuery, session);
        return newObject;
    }
    
    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromJoinedRow(AbstractRecord dbRow, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return valueFromRowInternalWithJoin(dbRow, joinManager, query, parentCacheKey, session, isTargetProtected);
    }
    
    /**
     * INTERNAL:
     * Create a query key that links to the map key
     * @return
     */
    public QueryKey createQueryKeyForMapKey(){
        OneToOneQueryKey key = new OneToOneQueryKey();
        key.setDescriptor(getReferenceDescriptor());
        key.setReferenceClass(getReferenceClass());
        key.setJoinCriteria(getAdditionalSelectionCriteriaForMapKey());
        return key;
    }
    
    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy, Delete the passed object if necessary.
     * 
     * This method is used for removal of private owned relationships
     * 
     * @param objectDeleted
     * @param session
     */
    public void deleteMapKey(Object objectDeleted, AbstractSession session){
        session.deleteObject(objectDeleted);
    }
    
    /**
     * INTERNAL:
     * Adds locking clause to the target query to extend pessimistic lock scope.
     */
    @Override
    protected void extendPessimisticLockScopeInTargetQuery(ObjectLevelReadQuery targetQuery, ObjectBuildingQuery sourceQuery) {
        if(this.mechanism == null) {
            super.extendPessimisticLockScopeInTargetQuery(targetQuery, sourceQuery);
        } else {
            this.mechanism.setRelationTableLockingClause(targetQuery, sourceQuery);
        }
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
        if(this.mechanism == null) {
            ExpressionBuilder builder = sourceQuery.getExpressionBuilder();
            Iterator<Map.Entry<DatabaseField, DatabaseField>> it = this.getSourceToTargetKeyFields().entrySet().iterator();
            Map.Entry<DatabaseField, DatabaseField> entry = it.next();
            exp = builder.getField(entry.getKey()).equal(builder.get(this.getAttributeName()).getField(entry.getValue())).and(exp);
        } else {
            exp = this.mechanism.joinRelationTableField(exp, sourceQuery.getExpressionBuilder());
        }
        sourceQuery.setSelectionCriteria(exp);
    }

    /**
     * INTERNAL:
     * Extract the foreign key value from the source row.
     */
    @Override
    protected Object extractBatchKeyFromRow(AbstractRecord row, AbstractSession session) {
        if (this.mechanism != null) {
            return this.mechanism.extractBatchKeyFromRow(row, session);             
        }
        Object[] key;
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();
        key = new Object[this.sourceToTargetKeyFields.size()];
        int index = 0;
        for (DatabaseField field : this.sourceToTargetKeyFields.keySet()) {
            Object value = row.get(field);
            if (value == null) {
                return null;
            }
            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, field.getType());
            } catch (ConversionException exception) {
                throw ConversionException.couldNotBeConverted(this, this.descriptor, exception);
            }
            key[index] = value;
            index++;
        }
        return new CacheId(key);
    }
    
    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query
     */
    public Map extractIdentityFieldsForQuery(Object object, AbstractSession session){
        Map keyFields = new HashMap();
         for (int index = 0; index < getForeignKeyFields().size(); index++) {
            DatabaseField targetRelationField = getForeignKeyFields().elementAt(index);
            DatabaseField targetKey = getSourceToTargetKeyFields().get(targetRelationField);
            Object value = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(object, targetKey, session);
            keyFields.put(targetRelationField, value);
        }
        return keyFields;
    }
    
    /**
     * INTERNAL:
     * Extract the key value from the reference object.
     */
    protected Object extractKeyFromReferenceObject(Object object, AbstractSession session) {
        ObjectBuilder objectBuilder = getReferenceDescriptor().getObjectBuilder();
        Object[] key;
        if (this.mechanism == null) {
            key = new Object[getSourceToTargetKeyFields().size()];
            int index = 0;
            for (DatabaseField field : getSourceToTargetKeyFields().values()) {
                if (object == null) {
                    key[index] = null;
                } else {
                    key[index] = objectBuilder.extractValueFromObjectForField(object, field, session);
                }
                index++;
            }
        } else {
            int size = this.mechanism.targetKeyFields.size();
            key = new Object[size];
            for (int i = 0; i < size; i++) {
                if (object == null) {
                    key[i] = null;
                } else {
                    DatabaseField field = this.mechanism.targetKeyFields.get(i);
                    key[i] = objectBuilder.extractValueFromObjectForField(object, field, session);
                }
            }            
        }

        return new CacheId(key);
    }

    /**
     * INTERNAL:
     * Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    @Override
    public Object extractPrimaryKeysForReferenceObjectFromRow(AbstractRecord row) {
        List primaryKeyFields = getReferenceDescriptor().getPrimaryKeyFields();
        Object[] result = new  Object[primaryKeyFields.size()];
        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField targetKeyField = (DatabaseField)primaryKeyFields.get(index);
            DatabaseField sourceKeyField = getTargetToSourceKeyFields().get(targetKeyField);
            if (sourceKeyField == null) {
                return null;
            }
            result[index] = row.get(sourceKeyField);
            if (getReferenceDescriptor().getCachePolicy().getCacheKeyType() == CacheKeyType.ID_VALUE) {
                return result[index];
            }
        }
        return new CacheId(result);
    }

    /**
     * INTERNAL:
     * Allow the mapping the do any further batch preparation.
     */
    @Override
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        super.postPrepareNestedBatchQuery(batchQuery, query);
        // Force a distinct to filter out m-1 duplicates.
        // Only set if really a m-1, not a 1-1
        if (!isOneToOneRelationship()) {
            ((ObjectLevelReadQuery)batchQuery).useDistinct();
        }
        if (this.mechanism != null) {
            this.mechanism.postPrepareNestedBatchQuery(batchQuery, query);
        }
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    @Override
    protected Expression buildBatchCriteria(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        if (this.mechanism == null) {
            int size = this.sourceToTargetKeyFields.size();
            if (size > 1) {
                // Support composite keys using nested IN.
                List<Expression> fields = new ArrayList<Expression>(size);
                for (DatabaseField targetForeignKeyField : this.sourceToTargetKeyFields.values()) {
                    fields.add(builder.getField(targetForeignKeyField));
                }
                return query.getSession().getPlatform().buildBatchCriteriaForComplexId(builder, fields);
            } else {
                return query.getSession().getPlatform().buildBatchCriteria(builder, builder.getField(this.sourceToTargetKeyFields.values().iterator().next()));
            }
        } else {
            return this.mechanism.buildBatchCriteria(builder, query);
        }
    }

    /**
     * INTERNAL:
     * Prepare and execute the batch query and store the
     * results for each source object in a map keyed by the
     * mappings source keys of the source objects.
     */
    @Override
    protected void executeBatchQuery(DatabaseQuery query, CacheKey parentCacheKey, Map referenceObjectsByKey, AbstractSession session, AbstractRecord translationRow) {
        // Execute query and index resulting objects by key.
        List results;
        ObjectBuilder builder = query.getDescriptor().getObjectBuilder();
        if (this.mechanism == null) {
            results = (List)session.executeQuery(query, translationRow);
            for (Object eachReferenceObject : results) {
                Object eachReferenceKey = extractKeyFromReferenceObject(eachReferenceObject, session);    
                referenceObjectsByKey.put(eachReferenceKey, builder.wrapObject(eachReferenceObject, session));
            }
        } else {
            ComplexQueryResult complexResult = (ComplexQueryResult)session.executeQuery(query, translationRow);
            results = (List)complexResult.getResult();
            List<AbstractRecord> rows = (List)complexResult.getData();
            int size = results.size();
            for (int index = 0; index < size; index++) {
                AbstractRecord row = rows.get(index);
                Object key = this.mechanism.extractKeyFromTargetRow(row, session);
                referenceObjectsByKey.put(key, builder.wrapObject(results.get(index), session));
            }
        }
    }

    /**
     * INTERNAL:
     * Check if the target object is in the cache if possible based on the target key value.
     * Return null if the target key is not the primary key, or if the query is refreshing.
     */
    @Override
    protected Object checkCacheForBatchKey(AbstractRecord sourceRow, Object foreignKey, Map batchObjects, ReadQuery batchQuery, ObjectLevelReadQuery originalQuery, AbstractSession session) {
        if (((ReadAllQuery)batchQuery).shouldRefreshIdentityMapResult() || (!((ReadAllQuery)batchQuery).shouldMaintainCache())) {
            return null;
        }
        // Check the cache using the source row and selection query.
        Object cachedObject = this.selectionQuery.checkEarlyReturn(session, sourceRow);
        if ((cachedObject != null) && (batchObjects != null)) {
            batchObjects.put(foreignKey, cachedObject);
        }
        return cachedObject;
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria necessary to select the target object when this mapping
     * is a map key.
     * @return
     */
    public Expression getAdditionalSelectionCriteriaForMapKey(){
        return buildSelectionCriteria(false, false);
    }

    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query
     */
    public List<DatabaseTable> getAdditionalTablesForJoinQuery(){
        List<DatabaseTable> tables = new ArrayList<DatabaseTable>(getReferenceDescriptor().getTables().size() + 1);
        tables.addAll(getReferenceDescriptor().getTables());
        if (keyTableForMapKey != null){
            tables.add(keyTableForMapKey);
        }
        return tables;
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
     * Return the classification for the field contained in the mapping.
     * This is used to convert the row value to a consistent java value.
     */
    @Override
    public Class getFieldClassification(DatabaseField fieldToClassify) throws DescriptorException {
        DatabaseField fieldInTarget = getSourceToTargetKeyFields().get(fieldToClassify);
        if (fieldInTarget == null) {
            return null;// Can be registered as multiple table secondary field mapping
        }
        DatabaseMapping mapping = getReferenceDescriptor().getObjectBuilder().getMappingForField(fieldInTarget);
        if (mapping == null) {
            return null;// Means that the mapping is read-only
        }
        return mapping.getFieldClassification(fieldInTarget);
    }

    /**
     * PUBLIC:
     * Return the foreign key field names associated with the mapping.
     * These are only the source fields that are writable.
     */
    public Vector getForeignKeyFieldNames() {
        Vector fieldNames = new Vector(getForeignKeyFields().size());
        for (Enumeration fieldsEnum = getForeignKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }
    
    /**
     * INTERNAL:
     * Return source key fields for translation by an AggregateObjectMapping
     */
    @Override
    public Collection getFieldsForTranslationInAggregate() {
        return getSourceToTargetKeyFields().keySet();
    }

    /**
     * Return the appropriate map that maps the "foreign keys"
     * to the "primary keys".
     */
    protected Map getForeignKeysToPrimaryKeys() {
        if (this.isForeignKeyRelationship()) {
            return this.getSourceToTargetKeyFields();
        } else {
            return this.getTargetToSourceKeyFields();
        }
    }
    

    /**
     * INTERNAL:
     * Return a Map of any foreign keys defined within the the MapKey
     * @return
     */
    public Map<DatabaseField, DatabaseField> getForeignKeyFieldsForMapKey(){
        return getSourceToTargetKeyFields();
    }
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields
     * @return
     */
    public List<DatabaseField> getIdentityFieldsForMapKey(){
        return getForeignKeyFields();
    }
    
    
    /**
     * INTERNAL:
     * Return the query that is used when this mapping is part of a joined relationship
     * 
     * This method is used when this mapping is used to map the key in a Map
     */
    public ObjectLevelReadQuery getNestedJoinQuery(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session){
        return prepareNestedJoins(joinManager, query, session);
    }
    
    /**
     * INTERNAL:
     * Get all the fields for the map key
     */
    public List<DatabaseField> getAllFieldsForMapKey(){
        List<DatabaseField> fields = new ArrayList(getReferenceDescriptor().getAllFields().size() + getForeignKeyFields().size());
        fields.addAll(getReferenceDescriptor().getAllFields());
        fields.addAll(getForeignKeyFields());
        return fields;
    }
    
    /**
     * INTERNAL:
     * Return a vector of the foreign key fields in the same order
     * as the corresponding primary key fields are in their descriptor.
     */
    public Vector getOrderedForeignKeyFields() {
        List primaryKeyFields = getPrimaryKeyDescriptor().getPrimaryKeyFields();
        Vector result = new Vector(primaryKeyFields.size());

        for (int index = 0; index < primaryKeyFields.size(); index++) {
            DatabaseField pkField = (DatabaseField)primaryKeyFields.get(index);
            boolean found = false;
            for (Iterator fkStream = this.getForeignKeysToPrimaryKeys().keySet().iterator();
                     fkStream.hasNext();) {
                DatabaseField fkField = (DatabaseField)fkStream.next();

                if (this.getForeignKeysToPrimaryKeys().get(fkField).equals(pkField)) {
                    found = true;
                    result.addElement(fkField);
                    break;
                }
            }
            if (!found) {
                throw DescriptorException.missingForeignKeyTranslation(this, pkField);
            }
        }
        return result;
    }

    /**
     * Return the descriptor for whichever side of the
     * relation has the "primary key".
    */
    protected ClassDescriptor getPrimaryKeyDescriptor() {
        if (this.isForeignKeyRelationship()) {
            return this.getReferenceDescriptor();
        } else {
            return this.getDescriptor();
        }
    }

    /**
     * INTERNAL:
     * The private owned criteria is only used outside of the unit of work to compare the previous value of the reference.
     */
    public Expression getPrivateOwnedCriteria() {
        if (privateOwnedCriteria == null) {
            initializePrivateOwnedCriteria();
        }
        return privateOwnedCriteria;
    }
    
    /**
     * INTERNAL:
     * Return a collection of the source to target field value associations.
     */
    public Vector getSourceToTargetKeyFieldAssociations() {
        Vector associations = new Vector(getSourceToTargetKeyFields().size());
        Iterator sourceFieldEnum = getSourceToTargetKeyFields().keySet().iterator();
        Iterator targetFieldEnum = getSourceToTargetKeyFields().values().iterator();
        while (sourceFieldEnum.hasNext()) {
            Object fieldValue = ((DatabaseField)sourceFieldEnum.next()).getQualifiedName();
            Object attributeValue = ((DatabaseField)targetFieldEnum.next()).getQualifiedName();
            associations.addElement(new Association(fieldValue, attributeValue));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Returns the source keys to target keys fields association.
     */
    public Map<DatabaseField, DatabaseField> getSourceToTargetKeyFields() {
        return sourceToTargetKeyFields;
    }
    
    /**
     * INTERNAL:
     * Returns the target keys to source keys fields association.
     */
    public Map<DatabaseField, DatabaseField> getTargetToSourceKeyFields() {
        return targetToSourceKeyFields;
    }

    /**
     * INTERNAL:
     * If required, get the targetVersion of the source object from the merge manager
     * 
     * Used with MapKeyContainerPolicy to abstract getting the target version of a source key
     * @return
     */
    public Object getTargetVersionOfSourceObject(Object object, Object parent, MergeManager mergeManager, AbstractSession targetSession){
       return  mergeManager.getTargetVersionOfSourceObject(object, referenceDescriptor, targetSession);
    }
    
    /**
     * INTERNAL:
     * Return the class this key mapping maps or the descriptor for it
     * @return
     */
    public Class getMapKeyTargetType(){
        return getReferenceClass();
    }
    
    /**
     * INTERNAL:
     * Initialize the mapping.
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
        if (isForeignKeyRelationship() && !isMapKeyMapping()) {
            getDescriptor().addPreDeleteMapping(this);
        }
        
        // Capture our foreign key field specifications here. We need to build
        // the fields first to ensure they have a table associated with them. 
        // Also must be careful to not set the flags based on a previously
        // built field (multiple mappings to the same field) since we need to
        // capture the flags from the field set directly on this mapping.
        for (DatabaseField field : getForeignKeyFields()) {
            DatabaseField builtField = getDescriptor().buildField(field, keyTableForMapKey);
            
            if (builtField == field || builtField.isTranslated()) {
                // same instance or translated, look at the built field.
                updateInsertableAndUpdatableFields(builtField);    
            } else {
                // previously built field and not translated, look at the original field.
                updateInsertableAndUpdatableFields(field);
            }
        }
        
        if (this.mechanism != null) {
            if (this.mechanism.hasRelationTable()) {
                if(!this.foreignKeyFields.isEmpty() || !this.sourceToTargetKeyFields.isEmpty() || !this.targetToSourceKeyFields.isEmpty()) {
                    throw DescriptorException.oneToOneMappingConflict(this.getDescriptor(), this);
                }
                this.foreignKeyFields = null;
                this.sourceToTargetKeyFields = null;
                this.targetToSourceKeyFields = null;
                
                this.mechanism.initialize(session, this);
            } else {
                this.mechanism = null;
            }
        }
        
        if (this.mechanism == null) {
            // Must set table of foreign keys.
            for (int index = 0; index < getForeignKeyFields().size(); index++) {
                DatabaseField foreignKeyField = getForeignKeyFields().get(index);
                foreignKeyField = getDescriptor().buildField(foreignKeyField, keyTableForMapKey);
                getForeignKeyFields().set(index, foreignKeyField);
            }
    
            // If only a selection criteria is specified then the foreign keys do not have to be initialized.
            if (!(getTargetToSourceKeyFields().isEmpty() && getSourceToTargetKeyFields().isEmpty())) {
                if (getTargetToSourceKeyFields().isEmpty() || getSourceToTargetKeyFields().isEmpty()) {
                    initializeForeignKeysWithDefaults(session);
                } else {
                    initializeForeignKeys(session);
                }
            }
            
            // Check if any foreign keys reference a secondary table.
            if (getReferenceDescriptor().getTables().size() > 1) {
                DatabaseTable firstTable = getReferenceDescriptor().getTables().get(0);
                for (DatabaseField field : getSourceToTargetKeyFields().values()) {
                    if (!field.getTable().equals(firstTable)) {
                        getReferenceDescriptor().setHasMultipleTableConstraintDependecy(true);
                    }
                }
            }
            
            // Check if any foreign keys reference a secondary table.
            if (getDescriptor().getTables().size() > 1) {
                DatabaseTable firstTable = getDescriptor().getTables().get(0);
                for (DatabaseField field : getSourceToTargetKeyFields().keySet()) {
                    if (!field.getTable().equals(firstTable)) {
                        getDescriptor().setHasMultipleTableConstraintDependecy(true);
                    }
                }
            }
        }

        if (shouldInitializeSelectionCriteria()) {
            if (shouldForceInitializationOfSelectionCriteria()) {
                setSelectionCriteria(buildSelectionCriteria());
            } else {
                setSelectionCriteria(buildSelectionCriteria(true, true));
            }
        } else {
            setShouldVerifyDelete(false);
        }

        setFields(collectFields());
        
        if (getReferenceDescriptor().hasTablePerClassPolicy()) {
            // This will do nothing if we have already prepared for this 
            // source mapping or if the source mapping does not require
            // any special prepare logic.
            getReferenceDescriptor().getTablePerClassPolicy().prepareChildrenSelectionQuery(this, session);              
        }
    }

    /**
     * INTERNAL:
     * The foreign keys primary keys are stored as database fields in the map.
     */
    protected void initializeForeignKeys(AbstractSession session) {
        HashMap<DatabaseField, DatabaseField> newSourceToTargetKeyFields = new HashMap(getSourceToTargetKeyFields().size());
        HashMap<DatabaseField, DatabaseField> newTargetToSourceKeyFields = new HashMap(getTargetToSourceKeyFields().size());
        Iterator<Map.Entry<DatabaseField, DatabaseField>> iterator = getSourceToTargetKeyFields().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DatabaseField, DatabaseField> entry = iterator.next();
            DatabaseField sourceField = entry.getKey();
            sourceField = getDescriptor().buildField(sourceField, keyTableForMapKey);
            if (usesIndirection()) {
                sourceField.setKeepInRow(true);
            }
            DatabaseField targetField = entry.getValue();
            targetField = getReferenceDescriptor().buildField(targetField, keyTableForMapKey);
            newSourceToTargetKeyFields.put(sourceField, targetField);
            newTargetToSourceKeyFields.put(targetField, sourceField);
        }
        setSourceToTargetKeyFields(newSourceToTargetKeyFields);
        setTargetToSourceKeyFields(newTargetToSourceKeyFields);
    }

    /**
     * INTERNAL:
     * The foreign keys primary keys are stored as database fields in the map.
     */
    protected void initializeForeignKeysWithDefaults(AbstractSession session) {
        if (isForeignKeyRelationship()) {
            if (getSourceToTargetKeyFields().size() != 1) {
                throw DescriptorException.foreignKeysDefinedIncorrectly(this);
            }
            List<DatabaseField> targetKeys = getReferenceDescriptor().getPrimaryKeyFields();
            if (targetKeys.size() != 1) {
                //target and source keys are not the same size.
                throw DescriptorException.sizeMismatchOfForeignKeys(this);
            }

            //grab the only element out of the map
            DatabaseField sourceField = getSourceToTargetKeyFields().keySet().iterator().next();
            sourceField = getDescriptor().buildField(sourceField);
            if (usesIndirection()) {
                sourceField.setKeepInRow(true);
            }
            getSourceToTargetKeyFields().clear();
            getTargetToSourceKeyFields().clear();
            getSourceToTargetKeyFields().put(sourceField, targetKeys.get(0));
            getTargetToSourceKeyFields().put(targetKeys.get(0), sourceField);
        } else {
            if (getTargetToSourceKeyFields().size() != 1) {
                throw DescriptorException.foreignKeysDefinedIncorrectly(this);
            }
            List<DatabaseField> sourceKeys = getDescriptor().getPrimaryKeyFields();
            if (sourceKeys.size() != 1) {
                //target and source keys are not the same size.
                throw DescriptorException.sizeMismatchOfForeignKeys(this);
            }

            //grab the only element out of the map
            DatabaseField targetField = getTargetToSourceKeyFields().keySet().iterator().next();
            targetField = getReferenceDescriptor().buildField(targetField);
            getSourceToTargetKeyFields().clear();
            getTargetToSourceKeyFields().clear();
            getTargetToSourceKeyFields().put(targetField, sourceKeys.get(0));
            getSourceToTargetKeyFields().put(sourceKeys.get(0), targetField);
        }
    }

    /**
     * INTERNAL:
     * Selection criteria is created with source foreign keys and target keys.
     */
    protected void initializePrivateOwnedCriteria() {
        if (!isForeignKeyRelationship()) {
            setPrivateOwnedCriteria(getSelectionCriteria());
        } else {
            Expression pkCriteria = getDescriptor().getObjectBuilder().getPrimaryKeyExpression();
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression backRef = builder.getManualQueryKey(getAttributeName() + "-back-ref", getDescriptor());
            Expression newPKCriteria = pkCriteria.rebuildOn(backRef);
            Expression twistedSelection = backRef.twist(getSelectionCriteria(), builder);
            if (getDescriptor().getQueryManager().getAdditionalJoinExpression() != null) {
                // We don't have to twist the additional join because it's all against the same node, which is our base
                // but we do have to rebuild it onto the manual query key
                Expression rebuiltAdditional = getDescriptor().getQueryManager().getAdditionalJoinExpression().rebuildOn(backRef);
                if (twistedSelection == null) {
                    twistedSelection = rebuiltAdditional;
                } else {
                    twistedSelection = twistedSelection.and(rebuiltAdditional);
                }
            }
            setPrivateOwnedCriteria(newPKCriteria.and(twistedSelection));
        }
    }

    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping
     */
    public void preinitializeMapKey(DatabaseTable table) throws DescriptorException {
        keyTableForMapKey = table;
    }
    
    /**
     * INTERNAL:
     * Need to set the field type for the foreign key fields for a map key, as the fields are not contained in any descriptor.
     */
    public void postInitializeMapKey(MappedKeyMapContainerPolicy policy) {
        for (DatabaseField foreignKey : getSourceToTargetKeyFields().keySet()) {
            if (foreignKey.getType() == null) {
                foreignKey.setType(getFieldClassification(foreignKey));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Prepare a cascade locking policy.
     */
    @Override
    public void prepareCascadeLockingPolicy() {
        CascadeLockingPolicy policy = new CascadeLockingPolicy(getDescriptor(), getReferenceDescriptor());
        policy.setQueryKeyFields(getSourceToTargetKeyFields(), ! isForeignKeyRelationship());
        getReferenceDescriptor().addCascadeLockingPolicy(policy);
    }
    
    /**
     * This method would allow customers to get the potential selection criteria for a mapping
     * prior to initialization.  This would allow them to more easily create an amendment method
     * that would amend the SQL for the join.
     */
    public Expression buildSelectionCriteria() {
        return buildSelectionCriteria(true, false);
    }
    
    /**
     * INTERNAL:
     * Build the selection criteria for this mapping.  Allows several variations.
     * 
     * Either a parameter can be used for the join or simply the database field
     * 
     * The existing selection criteria can be built upon or a whole new criteria can be built.
     */
    public Expression buildSelectionCriteria(boolean useParameter, boolean usePreviousSelectionCriteria){
        Expression criteria = null;
        if (usePreviousSelectionCriteria){
            criteria = getSelectionCriteria();
        }

        if(this.mechanism == null) {
            Expression builder = new ExpressionBuilder();
            // CR3922
            if (getSourceToTargetKeyFields().isEmpty()) {
                throw DescriptorException.noForeignKeysAreSpecified(this);
            }
    
            for (Iterator keys = getSourceToTargetKeyFields().keySet().iterator(); keys.hasNext();) {
                DatabaseField foreignKey = (DatabaseField)keys.next();
                DatabaseField targetKey = getSourceToTargetKeyFields().get(foreignKey);
    
                Expression expression = null;
                if (useParameter){
                    expression = builder.getField(targetKey).equal(builder.getParameter(foreignKey));
                } else {
                    expression = builder.getField(targetKey).equal(builder.getField(foreignKey));
                }
                
                if (criteria == null) {
                    criteria = expression;
                } else {
                    criteria = expression.and(criteria);
                }
            }
        } else {
            criteria = this.mechanism.buildSelectionCriteria(this, criteria);
        }
        return criteria;
    }

    /**
     * INTERNAL:
     * Builds a shallow original object.  Only direct attributes and primary
     * keys are populated.  In this way the minimum original required for
     * instantiating a working copy clone can be built without placing it in
     * the shared cache (no concern over cycles).
     */
    @Override
    public void buildShallowOriginalFromRow(AbstractRecord databaseRow, Object original, JoinedAttributeManager joinManager, ObjectBuildingQuery query, AbstractSession executionSession) {
        // Now we are only building this original so we can extract the primary
        // key out of it.  If the primary key is stored across a 1-1 a value
        // holder needs to be built/triggered to get at it.
        // In this case recursively build the shallow original across the 1-1.
        // We only need the primary key for that object, and we know
        // what that primary key is: it is the foreign key in our row.
        ClassDescriptor descriptor = getReferenceDescriptor();
        AbstractRecord targetRow = new DatabaseRecord();

        for (Iterator keys = getSourceToTargetKeyFields().keySet().iterator(); keys.hasNext();) {
            DatabaseField foreignKey = (DatabaseField)keys.next();
            DatabaseField targetKey = getSourceToTargetKeyFields().get(foreignKey);

            targetRow.put(targetKey, databaseRow.get(foreignKey));
        }

        Object targetObject = descriptor.getObjectBuilder().buildNewInstance();
        descriptor.getObjectBuilder().buildAttributesIntoShallowObject(targetObject, databaseRow, query);
        targetObject = getIndirectionPolicy().valueFromRow(targetObject);

        setAttributeValueInObject(original, targetObject);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isOneToOneMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isOwned(){
        return this.hasRelationTable() && ! this.isReadOnly;
    }

    /**
     * INTERNAL:
     * Reads the private owned object.
     */
    @Override
    protected Object readPrivateOwnedForObject(ObjectLevelModifyQuery modifyQuery) throws DatabaseException {
        if (modifyQuery.getSession().isUnitOfWork()) {
            return super.readPrivateOwnedForObject(modifyQuery);
        } else {
            if (!shouldVerifyDelete()) {
                return null;
            }
            ReadObjectQuery readQuery = (ReadObjectQuery)getSelectionQuery().clone();

            readQuery.setSelectionCriteria(getPrivateOwnedCriteria());
            return modifyQuery.getSession().executeQuery(readQuery, modifyQuery.getTranslationRow());
        }
    }

    /**
     * INTERNAL:
     * Rehash any map based on fields.
     * This is used to clone descriptors for aggregates, which hammer field names,
     * it is probably better not to hammer the field name and this should be refactored.
     */
    @Override
    public void rehashFieldDependancies(AbstractSession session) {
        setSourceToTargetKeyFields(Helper.rehashMap(getSourceToTargetKeyFields()));
        
        // Go through the fks again and make updates for any translated fields.
        for (DatabaseField field : getSourceToTargetKeyFields().keySet()) {
            if (field.isTranslated()) {
                updateInsertableAndUpdatableFields(field);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Return whether this mapping requires extra queries to update the rows if it is
     * used as a key in a map.  This will typically be true if there are any parts to this mapping
     * that are not read-only.
     */
    public boolean requiresDataModificationEventsForMapKey() {
        return true;
    }

    /**
     * Return if this mapping is really for a OneToOne relationship.
     * This is a backward compatibility issue, in that before the ManyToOneMapping
     * was created OneToOneMapping was used for both.
     * false means it may be a OneToOne or a ManyToOne (unknown).
     */
    public boolean isOneToOneRelationship() {
        return isOneToOneRelationship;
    }
    
    /**
     * Return if this mapping is mapped using primary key join columns.
     */
    public boolean isOneToOnePrimaryKeyRelationship() {
        return isOneToOnePrimaryKeyRelationship;
    }
    
    /**
     * Define if this mapping is really for a OneToOne relationship.
     * This is a backward compatibility issue, in that before the ManyToOneMapping
     * was created OneToOneMapping was used for both.
     */
    public void setIsOneToOneRelationship(boolean isOneToOneRelationship) {
        this.isOneToOneRelationship = isOneToOneRelationship;
    }
    
    /**
     * Set if this mapping is defined using primary key join columns.
     */
    public void setIsOneToOnePrimaryKeyRelationship(boolean isOneToOnePrimaryKeyRelationship) {
        this.isOneToOnePrimaryKeyRelationship = isOneToOnePrimaryKeyRelationship;
    }

    /**
     * PUBLIC:
     * Define the foreign key relationship in the 1-1 mapping.
     * This method is used for singleton foreign key relationships only,
     * that is the source object's table has a foreign key field to
     * the target object's primary key field.
     * Only the source foreign key field name is specified.
     * When a foreign key is specified TopLink will automatically populate the value
     * for that field from the target object when the object is written to the database.
     * If the foreign key is also mapped through a direct-to-field then the direct-to-field must
     * be set read-only.
     */
    public void setForeignKeyFieldName(String sourceForeignKeyFieldName) {
        DatabaseField sourceField = new DatabaseField(sourceForeignKeyFieldName);

        setIsForeignKeyRelationship(true);
        getForeignKeyFields().addElement(sourceField);
        getSourceToTargetKeyFields().put(sourceField, new DatabaseField());
    }

    /**
     * PUBLIC:
     * Return the foreign key field names associated with the mapping.
     * These are only the source fields that are writable.
     */
    public void setForeignKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setForeignKeyFields(fields);
    }

    /**
     * INTERNAL:
     * Private owned criteria is used to verify the deletion of the target.
     * It joins from the source table on the foreign key to the target table,
     * with a parameterization of the primary key of the source object.
     */
    protected void setPrivateOwnedCriteria(Expression expression) {
        privateOwnedCriteria = expression;
    }

    /**
     * PUBLIC:
     * Verify delete is used during delete and update on private 1:1's outside of a unit of work only.
     * It checks for the previous value of the target object through joining the source and target tables.
     * By default it is always done, but may be disabled for performance on distributed database reasons.
     * In the unit of work the previous value is obtained from the backup-clone so it is never used.
     */
    public void setShouldVerifyDelete(boolean shouldVerifyDelete) {
        this.shouldVerifyDelete = shouldVerifyDelete;
    }

    /**
     * INTERNAL:
     * Set a collection of the source to target field associations.
     */
    public void setSourceToTargetKeyFieldAssociations(Vector sourceToTargetKeyFieldAssociations) {
        setSourceToTargetKeyFields(new HashMap(sourceToTargetKeyFieldAssociations.size() + 1));
        setTargetToSourceKeyFields(new HashMap(sourceToTargetKeyFieldAssociations.size() + 1));
        for (Enumeration associationsEnum = sourceToTargetKeyFieldAssociations.elements();
                 associationsEnum.hasMoreElements();) {
            Association association = (Association)associationsEnum.nextElement();
            DatabaseField sourceField = new DatabaseField((String)association.getKey());
            DatabaseField targetField = new DatabaseField((String)association.getValue());
            getSourceToTargetKeyFields().put(sourceField, targetField);
            getTargetToSourceKeyFields().put(targetField, sourceField);
        }
    }

    /**
     * INTERNAL:
     * Set the source keys to target keys fields association.
     */
    public void setSourceToTargetKeyFields(Map<DatabaseField, DatabaseField> sourceToTargetKeyFields) {
        this.sourceToTargetKeyFields = sourceToTargetKeyFields;
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-1 mapping.
     * This method is used for singleton target foreign key relationships only,
     * that is the target object's table has a foreign key field to
     * the source object's primary key field.
     * The target foreign key field name is specified.
     * The distinction between a foreign key and target foreign key is that the 1-1
     * mapping will not populate the target foreign key value when written (because it is in the target table).
     * Normally 1-1's are through foreign keys but in bi-directional 1-1's
     * the back reference will be a target foreign key.
     */
    public void setTargetForeignKeyFieldName(String targetForeignKeyFieldName) {
        DatabaseField targetField = new DatabaseField(targetForeignKeyFieldName);
        getTargetToSourceKeyFields().put(targetField, new DatabaseField());
    }

    /**
     * INTERNAL:
     * Set the target keys to source keys fields association.
     */
    public void setTargetToSourceKeyFields(Map<DatabaseField, DatabaseField> targetToSourceKeyFields) {
        this.targetToSourceKeyFields = targetToSourceKeyFields;
    }


    /**
     * PUBLIC:
     * Verify delete is used during delete and update outside of a unit of work only.
     * It checks for the previous value of the target object through joining the source and target tables.
     */
    public boolean shouldVerifyDelete() {
        return shouldVerifyDelete;
    }
    
    /**
     * INTERNAL:
     * By default returns true. Will also return true if:
     * 1 - WriteType is INSERT and the field is insertable.
     * 2 - WriteType is UPDATE and the field is updatable.
     */
    protected boolean shouldWriteField(DatabaseField field, WriteType writeType) {
        if (writeType.equals(WriteType.INSERT)) {
            return insertableFields.contains(field);
        } else if (writeType.equals(WriteType.UPDATE)) {
            return updatableFields.contains(field);
        } else {
            return true; // UNDEFINED, default is to write.
        }
    }

    /**
     * INTERNAL
     * Return true if this mapping supports cascaded version optimistic locking.
     */
    @Override
    public boolean isCascadedLockingSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return if this mapping support joining.
     */
    @Override
    public boolean isJoiningSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Called when iterating through descriptors to handle iteration on this mapping when it is used as a MapKey
     */
    public void iterateOnMapKey(DescriptorIterator iterator, Object element){
        this.getIndirectionPolicy().iterateOnAttributeValue(iterator, element);
    }

    /**
     * INTERNAL:
     * Allow the key mapping to unwrap the object.
     */    
    public Object unwrapKey(Object key, AbstractSession session){
        return getDescriptor().getObjectBuilder().unwrapObject(key, session);
    }
    
    /**
     * INTERNAL:
     * Add the field to the updatable and/or insertable list. Remove any 
     * previous field under the same name, otherwise shouldn't matter if we 
     * leave an old name (before translation) in the list as it should 'never' 
     * be used anyway.
     */
    protected void updateInsertableAndUpdatableFields(DatabaseField field) {
        insertableFields.remove(field);
        updatableFields.remove(field);
    
        if (field.isInsertable()) {
            insertableFields.add(field);
        }
    
        if (field.isUpdatable()) {
            updatableFields.add(field);
        }
    }

    /**
     * INTERNAL:
     * Allow the key mapping to wrap the object.
     */    
    public Object wrapKey(Object key, AbstractSession session){
        return getDescriptor().getObjectBuilder().wrapObject(key, session);
    }
    
    /**
     * INTERNAL:
     * A subclass should implement this method if it wants different behavior.
     * Write the foreign key values from the attribute to the row.
     */
    @Override
    public void writeFromAttributeIntoRow(Object attribute, AbstractRecord row, AbstractSession session)
    {
          for (Enumeration fieldsEnum = getForeignKeyFields().elements(); fieldsEnum.hasMoreElements();) {
                  DatabaseField sourceKey = (DatabaseField) fieldsEnum.nextElement();
                  DatabaseField targetKey = getSourceToTargetKeyFields().get(sourceKey);
                  Object referenceValue = null;
                          // If privately owned part is null then method cannot be invoked.
                  if (attribute != null) {
                          referenceValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(attribute, targetKey, session);
                  }
                  row.add(sourceKey, referenceValue);
          }
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public Object valueFromObject(Object object, DatabaseField field, AbstractSession session) {
        // First check if the value can be obtained from the value holder's row.
        Object attributeValue = getAttributeValueFromObject(object);
        AbstractRecord referenceRow = this.indirectionPolicy.extractReferenceRow(attributeValue);
        if (referenceRow != null) {
            Object value = referenceRow.get(field);
            Class type = getFieldClassification(field);
            if ((value == null) || (value.getClass() != type)) {
                // Must ensure the classification to get a cache hit.
                try {
                    value = session.getDatasourcePlatform().convertObject(value, type);
                } catch (ConversionException exception) {
                    throw ConversionException.couldNotBeConverted(this, getDescriptor(), exception);
                }
            }
            return value;
        }

        Object referenceObject = getRealAttributeValueFromAttribute(attributeValue, object, session);
        if (referenceObject == null) {
            return null;
        }
        DatabaseField targetField;
        if(this.mechanism == null) {
            targetField = this.sourceToTargetKeyFields.get(field);
        } else {
            targetField = this.mechanism.targetKeyFields.get(this.mechanism.sourceKeyFields.indexOf(field));
        }

        return this.referenceDescriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetField, session);
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * Check for batch + aggregation reading.
     */
    @Override
    protected Object valueFromRowInternalWithJoin(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey parentCacheKey, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        // PERF: Direct variable access.
        Object referenceObject;
        // CR #... the field for many objects may be in the row,
        // so build the subpartion of the row through the computed values in the query,
        // this also helps the field indexing match.
        AbstractRecord targetRow = trimRowForJoin(row, joinManager, executionSession);
        // PERF: Only check for null row if an outer-join was used.
        if (((joinManager != null) && joinManager.hasOuterJoinedAttributeQuery()) && !sourceQuery.hasPartialAttributeExpressions()) {
            Object key = this.referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromRow(targetRow, executionSession);
            if (key == null) {
                return this.indirectionPolicy.nullValueFromRow();
            }
        }
        // A nested query must be built to pass to the descriptor that looks like the real query execution would,
        // these should be cached on the query during prepare.
        ObjectLevelReadQuery nestedQuery = prepareNestedJoinQueryClone(row, null, joinManager, sourceQuery, executionSession);
        nestedQuery.setTranslationRow(targetRow);
        nestedQuery.setRequiresDeferredLocks(sourceQuery.requiresDeferredLocks());
        referenceObject = this.referenceDescriptor.getObjectBuilder().buildObject(nestedQuery, targetRow);

        // For bug 3641713 buildObject doesn't wrap if called on a UnitOfWork for performance reasons,
        // must wrap here as this is the last time we can look at the query and tell whether to wrap or not.
        if (nestedQuery.shouldUseWrapperPolicy() && executionSession.isUnitOfWork()) {
            referenceObject = this.referenceDescriptor.getObjectBuilder().wrapObject(referenceObject, executionSession);
        }
        return this.indirectionPolicy.valueFromRow(referenceObject);
    }
    
    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * Check for batch + aggregation reading.
     */
    @Override
    protected Object valueFromRowInternal(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {
        // If any field in the foreign key is null then it means there are no referenced objects
        // Skip for partial objects as fk may not be present.
        int size = this.fields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField field = this.fields.get(index);
            if (row.get(field) == null) {
                return this.indirectionPolicy.nullValueFromRow();
            }
        }

        // Call the default which executes the selection query,
        // or wraps the query with a value holder.
        return super.valueFromRowInternal(row, joinManager, sourceQuery, executionSession);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord databaseRow, AbstractSession session, WriteType writeType) {
        if (this.isReadOnly || (!this.isForeignKeyRelationship)) {
            return;
        }
        writeFromObjectIntoRowInternal(object, databaseRow, session, null, writeType);
    }
    
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * The fields and the values added to the row depend on ShallowMode mode:
     *   null - all fields with their values from object;
     *   Insert - nullable fields added with value null, non nullable fields added with their values from object;
     *   UpdateAfterInsert - nullable fields added with with their non-null values from object, non nullable fields (and nullable with null values) are ignored;
     *   UpdateBeforeDelete - the same fields as for UpdateAfterShallowInsert - but all values are nulls.
     */
    protected void writeFromObjectIntoRowInternal(Object object, AbstractRecord databaseRow, AbstractSession session, ShallowMode mode, WriteType writeType) {
        List<DatabaseField> foreignKeyFields = getForeignKeyFields();
        if (mode != null) {
            List<DatabaseField> nonNullableFields = null;
            
            for (DatabaseField field : foreignKeyFields) {
                if (field.isNullable()) {
                    if (mode == ShallowMode.Insert && shouldWriteField(field, writeType)) {
                        // add a nullable field with a null value
                        databaseRow.add(field, null);
                    }
                } else {
                    if (nonNullableFields == null) {
                        nonNullableFields = new ArrayList<DatabaseField>();
                    }
                    
                    nonNullableFields.add(field);
                }
            }
            
            if (nonNullableFields == null) {
                // all foreignKeyFields are nullable
                if (mode == ShallowMode.Insert) {
                    // nothing else to do
                    return;
                }
                // UpdateAfterInsert or UpdateBeforeDelete: all nullable foreignKeyFields will be processed
            } else {
                if (mode == ShallowMode.Insert) {
                    // all non nullable foreignKeyFields will be processed
                    foreignKeyFields = nonNullableFields;
                } else {
                    // UpdateAfterInsert or UpdateBeforeDelete
                    if (foreignKeyFields.size() == nonNullableFields.size()) {
                        // all fields are non nullable - nothing else to do
                        return;
                    } else {
                        // all nullable foreignKeyFields will be processed
                        foreignKeyFields = new ArrayList<DatabaseField>(foreignKeyFields);
                        foreignKeyFields.removeAll(nonNullableFields);
                    }
                }
            }
        }
        
        Object attributeValue = getAttributeValueFromObject(object);
        // If the value holder has the row, avoid instantiation and just use it.
        AbstractRecord referenceRow = this.indirectionPolicy.extractReferenceRow(attributeValue);
        if (referenceRow == null) {
            // Extract from object.
            Object referenceObject = getRealAttributeValueFromAttribute(attributeValue, object, session);
            
            for (DatabaseField sourceKey : foreignKeyFields) {    
                Object referenceValue = null;
                
                // If privately owned part is null then method cannot be invoked.
                if (referenceObject != null) {
                    DatabaseField targetKey = this.sourceToTargetKeyFields.get(sourceKey);
                    referenceValue = this.referenceDescriptor.getObjectBuilder().extractValueFromObjectForField(referenceObject, targetKey, session);
                }
                
                if (mode == null) {
                    // EL Bug 319759 - if a field is null, then the update call cache should not be used
                    if (referenceValue == null) {
                        databaseRow.setNullValueInFields(true);
                    }
                } else {
                    if (referenceValue == null) {
                        if (mode != ShallowMode.Insert) {
                            // both UpdateAfterInsert and UpdateBeforeDelete ignore null values
                            continue;
                        }
                    } else {
                        if (mode == ShallowMode.UpdateBeforeDelete) {
                            // UpdateBeforeDelete adds nulls instead of non nulls
                            referenceValue = null;
                        }
                    }
                }

                // Check updatable and insertable based on the write type.
                if (shouldWriteField(sourceKey, writeType)) {
                    databaseRow.add(sourceKey, referenceValue);
                }
            }
        } else {
            for (DatabaseField sourceKey : foreignKeyFields) {
                Object referenceValue = referenceRow.get(sourceKey);
                
                if (mode == null) {
                    // EL Bug 319759 - if a field is null, then the update call cache should not be used
                    if (referenceValue == null) {
                        databaseRow.setNullValueInFields(true);
                    }
                } else {
                    if (referenceValue == null) {
                        if (mode != ShallowMode.Insert) {
                            // both UpdateAfterInsert and UpdateBeforeDelete ignore null values
                            continue;
                        }
                    } else {
                        if (mode == ShallowMode.UpdateBeforeDelete) {
                            // UpdateBeforeDelete adds nulls instead of non nulls
                            referenceValue = null;
                        }
                    }
                }
                
                // Check updatable and insertable based on the write type.
                if (shouldWriteField(sourceKey, writeType)) {
                    databaseRow.add(sourceKey, referenceValue);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsert(Object object, AbstractRecord databaseRow, AbstractSession session) {
        if (this.isReadOnly || (!this.isForeignKeyRelationship)) {
            return;
        }
        writeFromObjectIntoRowInternal(object, databaseRow, session, ShallowMode.Insert, WriteType.INSERT);
    }

    /**
     * INTERNAL:
     * This row is built for update after shallow insert which happens in case of bidirectional inserts.
     * It contains the foreign keys with non null values that were set to null for shallow insert.
     */
    @Override
    public void writeFromObjectIntoRowForUpdateAfterShallowInsert(Object object, AbstractRecord databaseRow, AbstractSession session, DatabaseTable table) {
        if (this.isReadOnly || (!this.isForeignKeyRelationship) || !getFields().get(0).getTable().equals(table) || isPrimaryKeyMapping()) {
            return;
        }
        writeFromObjectIntoRowInternal(object, databaseRow, session, ShallowMode.UpdateAfterInsert, WriteType.UNDEFINED);
    }

    /**
     * INTERNAL:
     * This row is built for update before shallow delete which happens in case of bidirectional inserts.
     * It contains the same fields as the row built by writeFromObjectIntoRowForUpdateAfterShallowInsert, but all the values are null.
     */
    @Override
    public void writeFromObjectIntoRowForUpdateBeforeShallowDelete(Object object, AbstractRecord databaseRow, AbstractSession session, DatabaseTable table) {
        if (this.isReadOnly || (!this.isForeignKeyRelationship) || !getFields().get(0).getTable().equals(table) || isPrimaryKeyMapping()) {
            return;
        }
        writeFromObjectIntoRowInternal(object, databaseRow, session, ShallowMode.UpdateBeforeDelete, WriteType.UNDEFINED);
    }
    
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * Validation preventing primary key updates is implemented here.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord databaseRow, AbstractSession session, WriteType writeType) {
        if ((!this.isReadOnly) && this.isPrimaryKeyMapping && (!changeRecord.getOwner().isNew())) {
           throw ValidationException.primaryKeyUpdateDisallowed(changeRecord.getOwner().getClassName(), changeRecord.getAttribute());
        }
        
        // The object must be used here as the foreign key may include more than just the
        // primary key of the referenced object and the changeSet may not have the required information.
        Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
        writeFromObjectIntoRow(object, databaseRow, session, writeType);
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsertWithChangeRecord(ChangeRecord ChangeRecord, AbstractRecord databaseRow, AbstractSession session) {
        if (isReadOnly() || (!isForeignKeyRelationship())) {
            return;
        }

        for (Enumeration fieldsEnum = getForeignKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            DatabaseField sourceKey = (DatabaseField)fieldsEnum.nextElement();
            databaseRow.add(sourceKey, null);
        }
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord databaseRow, AbstractSession session) {
        if (isReadOnly() || (!isForeignKeyRelationship())) {
            return;
        }

        for (Enumeration fieldsEnum = getForeignKeyFields().elements(); fieldsEnum.hasMoreElements();) {
            DatabaseField sourceKey = (DatabaseField)fieldsEnum.nextElement();

            if (shouldWriteField(sourceKey, WriteType.INSERT)) {
                databaseRow.add(sourceKey, null);
            }
        }
    }

    /**
     * PUBLIC:
     * Indicates whether the mapping has RelationTableMechanism.
     */
    public boolean hasRelationTableMechanism() {
        return this.mechanism != null;
    }

    /**
     * PUBLIC:
     * Indicates whether the mapping has RelationTable.
     */
    public boolean hasRelationTable() {
        return this.mechanism != null && this.mechanism.hasRelationTable();
    }

    /**
     * PUBLIC:
     * Returns RelationTableMechanism that may be owned by the mapping,
     * that allows to configure the mapping to use relation table (just like ManyToManyMapping).
     * By default its null, should be created and set into the mapping before use.
     */
    public RelationTableMechanism getRelationTableMechanism() {
        return this.mechanism;
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
     * Set RelationTableMechanism into the mapping,
     * that allows to configure the mapping to use relation table (just like ManyToManyMapping).
     */
    public void setRelationTableMechanism(RelationTableMechanism mechanism) {
        this.mechanism = mechanism;
    }
    
    /**
     * PUBLIC:
     * Return RelationTable.
     */
    public DatabaseTable getRelationTable() {
        if(this.mechanism != null) {
            return this.mechanism.getRelationTable();
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    @Override
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if ((this.mechanism != null) && !this.isReadOnly && !this.isCascadeOnDeleteSetOnDatabase) {
            AbstractRecord sourceRow = this.mechanism.buildRelationTableSourceRow(query.getObject(), query.getSession(), this);
            query.getSession().executeQuery(this.mechanism.deleteQuery, sourceRow);
        }
        super.preDelete(query);
    }
    
    /**
     * INTERNAL:
     * Insert into relation table. This follows following steps.
     * <p>- Extract primary key and its value from the source object.
     * <p>- Extract target key and its value from the target object.
     * <p>- Construct a insert statement with above fields and values for relation table.
     * <p>- execute the statement.
     */
    @Override
    public void postInsert(WriteObjectQuery query) throws DatabaseException {
        super.postInsert(query);
        if(this.mechanism != null && !isReadOnly()) {
            Object targetObject = getRealAttributeValueFromObject(query.getObject(), query.getSession());
            if (targetObject == null) {
                return;
            }
            
            // Batch data modification in the uow
            if (query.shouldCascadeOnlyDependentParts()) {
                // Hey I might actually want to use an inner class here... ok array for now.
                Object[] event = new Object[3];
                event[0] = setObject;
                event[1] = this.mechanism.buildRelationTableSourceRow(query.getObject(), query.getSession(), this);
                // targetObject may not have pk yet - wait to extract targetRow until the event is processed
                event[2] = targetObject;
                query.getSession().getCommitManager().addDataModificationEvent(this, event);
            } else {
                AbstractRecord sourceAndTargetRow = this.mechanism.buildRelationTableSourceAndTargetRow(query.getObject(), targetObject, query.getSession(), this);
                query.getSession().executeQuery(this.mechanism.insertQuery, sourceAndTargetRow);
            }
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
        if(this.mechanism == null) {
            super.postUpdate(query);
        } else {
            // If object is not instantiated then it's not changed.
            if (!isAttributeValueInstantiated(query.getObject())) {
                return;
            }
            
            AbstractRecord sourceRow = null;
            if(!isReadOnly()) {    
                sourceRow = this.mechanism.buildRelationTableSourceRow(query.getObject(), query.getSession(), this);
                query.getSession().executeQuery(this.mechanism.deleteQuery, sourceRow);
            }
            
            super.postUpdate(query);

            if(sourceRow != null) {    
                Object targetObject = getRealAttributeValueFromObject(query.getObject(), query.getSession());
                if (targetObject == null) {
                    return;
                }
                // Batch data modification in the uow
                if (query.shouldCascadeOnlyDependentParts()) {
                    // Hey I might actually want to use an inner class here... ok array for now.
                    Object[] event = new Object[3];
                    event[0] = setObject;
                    event[1] = sourceRow;
                    // targetObject may not have pk yet - wait to extract targetRow until the event is processed
                    event[2] = targetObject;
                    query.getSession().getCommitManager().addDataModificationEvent(this, event);
                } else {
                    AbstractRecord sourceAndTargetRow = this.mechanism.addRelationTableTargetRow(targetObject, query.getExecutionSession(), sourceRow, this);
                    query.getSession().executeQuery(this.mechanism.insertQuery, sourceAndTargetRow);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Perform the commit event.
     * This is used in the uow to delay data modifications.
     */
    @Override
    public void performDataModificationEvent(Object[] event, AbstractSession session) throws DatabaseException, DescriptorException {
        // Hey I might actually want to use an inner class here... ok array for now.
        if (event[0] == setObject) {
            AbstractRecord sourceAndTargetRow = this.mechanism.addRelationTableTargetRow(event[2], session, (AbstractRecord)event[1], this);
            session.executeQuery(this.mechanism.insertQuery, sourceAndTargetRow);
        } else {
            throw DescriptorException.invalidDataModificationEventCode(event[0], this);
        }
    }
    
    /**
     * INTERNAL:
     * Return all the fields populated by this mapping, these are foreign keys only.
     */
    @Override
    protected Vector<DatabaseField> collectFields() {
        if(this.mechanism != null) {
            return new Vector(0);
        } else {
            return super.collectFields();
        }
    }
    
    /**
     * INTERNAL:
     * Order by foreign key fields if a foreign key mapping (avoids joins).
     */
    @Override
    public List<Expression> getOrderByNormalizedExpressions(Expression base) {
        if (this.foreignKeyFields.size() > 0) {
            List<Expression> orderBys = new ArrayList(this.foreignKeyFields.size());
            for (DatabaseField field : this.foreignKeyFields) {
                orderBys.add(((QueryKeyExpression)base).getBaseExpression().getField(field));
            }
            return orderBys;
        }
        return super.getOrderByNormalizedExpressions(base);
    }
}
