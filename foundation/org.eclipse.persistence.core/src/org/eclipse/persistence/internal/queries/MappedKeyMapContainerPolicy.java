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
 *     tware - initial implementation
 *     tware - implemenation of basic CRUD functionality
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.internal.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.core.queries.CoreMappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * A MappedKeyMapContainerPolicy should be used for mappings to implementers of Map.
 * It differs from MapContainerPolicy by allowing the MapKey to be an otherwise unmapped
 * column in a table rather than a mapped element of the value in the map.
 * 
 * This container policy holds a reference to a KeyMapping that will be used to construct the key
 * from the database and a reference to its owner which creates the value for the map.
 * 
 * The key of the map can be any implementer of MapKeyMapping and the data representing the
 * key can either be stored in the target table of the value mapping, or in a collection table that
 * associates the source to the target.   The data can either be everything necessary to compose the
 * key, or foreign keys that allow the key to be retrieved
 * 
 * @see MapContainerPolicy
 * @see MapKeyMapping
 * @see MapComponentMapping
 * 
 * @author tware
 *
 */
public class MappedKeyMapContainerPolicy extends MapContainerPolicy implements CoreMappedKeyMapContainerPolicy<AbstractSession> {
    
    protected MapKeyMapping keyMapping;

    protected MapComponentMapping valueMapping;
    
    public DatabaseQuery keyQuery;
    
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public MappedKeyMapContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public MappedKeyMapContainerPolicy(Class containerClass) {
        super(containerClass);
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public MappedKeyMapContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Called when the selection query is being initialize to add the fields for the key to the query
     */
    @Override
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression) {
        keyMapping.addAdditionalFieldsToQuery(selectionQuery, baseExpression);
    }
    
    /**
     * INTERNAL:
     * Add any non-Foreign-key data from an Object describe by a MapKeyMapping to a database row
     * This is typically used in write queries to ensure all the data stored in the collection table is included
     * in the query.
     */
    @Override
    public Map getKeyMappingDataForWriteQuery(Object object, AbstractSession session) {
        if (((DatabaseMapping)keyMapping).isReadOnly()) {
            return null;
        }
        Object keyValue = ((Map.Entry)object).getKey();
        return keyMapping.extractIdentityFieldsForQuery(keyValue, session);
    }

    /**
     * INTERNAL:
     * Return the type of the map key, this will be overridden by container policies that allow maps.
     */
    @Override
    public Object getKeyType() {
        return keyMapping.getMapKeyTargetType();
    }
    
    /**
     * INTERNAL:
     * Called when the insert query is being initialized to ensure the fields for the key are in the insert query
     * 
     * @see MappedKeyMapContainerPolicy
     */
    @Override
    public void addFieldsForMapKey(AbstractRecord joinRow) {
        if (((DatabaseMapping)keyMapping).isReadOnly()) {
            return;
        }
        keyMapping.addFieldsForMapKey(joinRow);
    }
    
    /**
     * INTERNAL:
     * Add element into container which implements the Map interface.
     * The may be used by merging/cloning passing a Map.Entry.
     */
    @Override
    public boolean addInto(Object element, Object container, AbstractSession session) {
        if (element instanceof Map.Entry) {
            Map.Entry record = (Map.Entry)element;
            Object key = record.getKey();
            Object value = record.getValue();
            return addInto(key, value, container, session);
        }
        throw QueryException.cannotAddToContainer(element, container, this);
    }
    
    /**
     * INTERNAL:
     * This is used for ordered List containers to add all of the elements
     * to the collection in the order of the index field in the row.
     * This is currently only used by OrderListContainerPolicy, so this is just a stub.
     * The passing of the query is to allow future compatibility with Maps (ordered Map).
     */
    @Override
    public boolean addInto(Object element, Object container, AbstractSession session, AbstractRecord row, DataReadQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        Object key = this.keyMapping.createMapComponentFromRow(row, null, parentCacheKey, session, isTargetProtected);        
        Object value = this.valueMapping.createMapComponentFromRow(row, null, parentCacheKey, session, isTargetProtected);
        return addInto(key, value, container, session);
    }

    /**
     * INTERNAL:
     * Add element to that implements the Map interface
     * use the row to compute the key
     */ 
    @Override
    public boolean addInto(Object element, Object container, AbstractSession session, AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        Object key = null;
        Object value = null;
        
        // we are a direct collection mapping.  This means the key will be element and the value will come
        // from dbRow
        if ((valueMapping != null) && (((DatabaseMapping)valueMapping).isDirectCollectionMapping()) && (session.getDescriptor(element.getClass()) != null)) {
            key = element;
            value = valueMapping.createMapComponentFromRow(dbRow, null, parentCacheKey, session, isTargetProtected);
        } else if (keyMapping != null) {
            value = element;
            try{
                key = keyMapping.createMapComponentFromRow(dbRow, query, parentCacheKey, session, isTargetProtected);
            } catch (Exception e) {
                throw QueryException.exceptionWhileReadingMapKey(element, e);
            }
        }
        return addInto(key, value, container, session);
    }
    
    /**
     * INTERNAL:
     * Used for joining.  Add any queries necessary for joining to the join manager
     */
    @Override
    public void addNestedJoinsQueriesForMapKey(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session){
        ObjectLevelReadQuery nestedQuery = keyMapping.getNestedJoinQuery(joinManager, query, session);
        if (nestedQuery != null){
            joinManager.getJoinedMappingQueries_().put((DatabaseMapping)keyMapping, nestedQuery);
        }
    }
    
    /**
     * Build a clone for the key of a Map represented by this container policy.
     */
    @Override
    public Object buildCloneForKey(Object key, Object parent, CacheKey parentCacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isCacheCheckComplete){
        return keyMapping.buildElementClone(key, parent, parentCacheKey, refreshCascade, cloningSession, isExisting, isCacheCheckComplete);

    }
    
    /**
     * INTERNAL:
     * Certain key mappings favor different types of selection query.  Return the appropriate
     * type of selectionQuery.
     */
    @Override
    public ReadQuery buildSelectionQueryForDirectCollectionMapping(){
        ReadQuery query = keyMapping.buildSelectionQueryForDirectCollectionKeyMapping(this);
        return query;
    }

    /**
     * Extract the key for the map from the provided row.
     */
    @Override
    public Object buildKey(AbstractRecord row, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return keyMapping.createMapComponentFromRow(row, query, parentCacheKey, session, isTargetProtected);
    }
        
    /**
     * Extract the key for the map from the provided row.
     */
    @Override
    public Object buildKeyFromJoinedRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return keyMapping.createMapComponentFromJoinedRow(row, joinManager, query, parentCacheKey, session, isTargetProtected);
    }

    /**
     * INTERNAL:
     * This method will access the target relationship and create a list of information to rebuild the collection.
     * For the MapContainerPolicy this return will consist of an array with serial Map entry key and value elements.
     * @see ObjectReferenceMapping.buildReferencesPKList
     * @see ContainerPolicy.buildReferencesPKList
     */
    @Override
    public Object[] buildReferencesPKList(Object container, AbstractSession session){
        Object[] result = new Object[this.sizeFor(container)*2];
        Iterator iterator = (Iterator)this.iteratorFor(container);
        boolean isElementCollection = ((DatabaseMapping)valueMapping).isElementCollectionMapping();
        int index = 0;
        while(iterator.hasNext()){
            Map.Entry entry = (Entry) iterator.next();
            result[index] = keyMapping.createSerializableMapKeyInfo(entry.getKey(), session);
            ++index;
            if (isElementCollection) {
                result[index] = entry.getValue();
            } else {
                result[index] = elementDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(entry.getValue(), session);
            }
            ++index;
        }
        return result;

    }

    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit to the map key
     */
    @Override
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
        keyMapping.cascadeDiscoverAndPersistUnregisteredNewObjects(((Map.Entry)object).getKey(), newObjects, unregisteredExistingObjects, visitedObjects, uow, false, cascadeErrors);     
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew to any mappings managed by the container policy. This will cascade the register to the key mapping.
     */
    @Override
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        keyMapping.cascadePerformRemoveIfRequired(((Map.Entry)object).getKey(), uow, visitedObjects, false);
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew to any mappings managed by the container policy. This will cascade the register to the key mapping.
     */
    @Override
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        keyMapping.cascadeRegisterNewIfRequired(((Map.Entry)object).getKey(), uow, visitedObjects, false);
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        MappedKeyMapContainerPolicy clone = (MappedKeyMapContainerPolicy) super.clone();
        
        clone.keyMapping = (MapKeyMapping) this.keyMapping.clone();
        
        if (this.keyQuery != null) {
            clone.keyQuery = (DatabaseQuery) this.keyQuery.clone();
        }
        
        return clone;
    }
    
    /**
     * INTERNAL:
     * Return true if keys are the same.  False otherwise
     */
    public boolean compareContainers(Object firstObjectMap, Object secondObjectMap) {
        if (sizeFor(firstObjectMap) != sizeFor(secondObjectMap)) {
            return false;
        }

        for (Object firstIterator = iteratorFor(firstObjectMap); hasNext(firstIterator);) {
            Map.Entry entry = (Map.Entry)nextEntry(firstIterator);
            Object key = entry.getKey();
            if (!((Map)firstObjectMap).get(key).equals(((Map)secondObjectMap).get(key))) {
                return false;
            }
        }
        return true;
    }
        
    /**
     * INTERNAL:
     * Return true if keys are the same in the source as the backup.  False otherwise
     * in the case of read-only compare against the original.
     */
    @Override
    public boolean compareKeys(Object sourceValue, AbstractSession session) {
        // Key is not stored in the object, only in the Map and the DB
        // As a result, a change in the object will not change how this object is hashed
        if (keyMapping != null){
            return true;
        }
        return super.compareKeys(sourceValue, session);
    }
    
    /**
     * INTERNAL:
     * Create change sets that contain map keys.
     */
    @Override
    protected void createChangeSetForKeys(Map originalKeyValues, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor){
        Iterator originalKeyValuesIterator = originalKeyValues.values().iterator();
        while (originalKeyValuesIterator.hasNext()){
            Association association = (Association)originalKeyValuesIterator.next();
            Object object = association.getValue();
            ObjectChangeSet changeSet = referenceDescriptor.getObjectBuilder().createObjectChangeSet(object, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
            changeSet.setOldKey(association.getKey());
        }
    }
    
    /**
     * INTERNAL:
     * Create a query key that links to the map key.
     */
    @Override
    public QueryKey createQueryKeyForMapKey() {
        return keyMapping.createQueryKeyForMapKey();
    }
    
    /**
     * INTERNAL:
     * This method will actually potentially wrap an object in two ways.  It will first wrap the object
     * based on the referenceDescriptor's wrapper policy.  It will also potentially do some wrapping based
     * on what is required by the container policy.
     */
    @Override
    public Object createWrappedObjectFromExistingWrappedObject(Object wrappedObject, Object parent, ClassDescriptor referenceDescriptor, MergeManager mergeManager, AbstractSession targetSession){
        Object key = ((Map.Entry)wrappedObject).getKey();
        key = keyMapping.getTargetVersionOfSourceObject(key, parent, mergeManager, targetSession);
        key = keyMapping.wrapKey(key, mergeManager.getSession());
        Object value = referenceDescriptor.getObjectBuilder().wrapObject(mergeManager.getTargetVersionOfSourceObject(unwrapIteratorResult(wrappedObject), referenceDescriptor, targetSession), mergeManager.getSession());
        return new Association(key, value);
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this ContainerPolicy to actual class-based
     * settings
     * @param classLoader 
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        ((DatabaseMapping)keyMapping).convertClassNamesToClasses(classLoader);
    }
    
    
    /**
     * INTERNAL:
     * Delete the key and value of the passed association passed object.
     */
    @Override
    public void deleteWrappedObject(Object objectDeleted, AbstractSession session) {
        if (((DatabaseMapping)keyMapping).isPrivateOwned()){
            keyMapping.deleteMapKey(((Map.Entry)objectDeleted).getKey(), session);
        }
        session.deleteObject(unwrapIteratorResult(objectDeleted));
    }
    
    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query.
     */
    @Override
    public List<DatabaseTable> getAdditionalTablesForJoinQuery() {
        return keyMapping.getAdditionalTablesForJoinQuery();
    }
    
    /**
     * INTERNAL:
     * Return any additional fields required by the policy for a fetch join.
     */
    @Override
    public List<DatabaseField> getAdditionalFieldsForJoin(CollectionMapping baseMapping) {
        return keyMapping.getAllFieldsForMapKey();
    }
    
    /**
     * INTERNAL:
     * Return a Map of any foreign keys defined within the the MapKey.
     */
    public Map<DatabaseField, DatabaseField> getForeignKeyFieldsForMapKey() {
        return keyMapping.getForeignKeyFieldsForMapKey();
    }
    
    /**
     * INTERNAL:
     * Return the reference descriptor for the map key if it exists.
     */
    @Override
    public ClassDescriptor getDescriptorForMapKey() {
        return keyMapping.getReferenceDescriptor();
    }
    
    /**
     * INTERNAL:
     * Used when objects are added or removed during an update.
     * This method returns either the clone from the ChangeSet or a packaged
     * version of it that contains things like map keys.
     */
    @Override
    public Object getCloneDataFromChangeSet(ObjectChangeSet changeSet) {
        Object key = changeSet.getNewKey();
        if (key == null) {
            key = changeSet.getOldKey();
        }
        return new Association(key ,changeSet.getUnitOfWorkClone());
    }
    
    
    /**
     * INTERNAL:
     * Return the DatabaseField that represents the key in a DirectMapMapping.  If the
     * keyMapping is not a DirectMapping, this will return null.
     */
    @Override
    public DatabaseField getDirectKeyField(CollectionMapping baseMapping) {
        if ((keyMapping != null) && ((DatabaseMapping)keyMapping).isDirectToFieldMapping()) {
            return ((AbstractDirectMapping)keyMapping).getField();
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields.
     */
    @Override
    public List<DatabaseField> getIdentityFieldsForMapKey() {
        return keyMapping.getIdentityFieldsForMapKey();
    }
    
    /**
     * INTERNAL:
     * Get the Converter for the key of this mapping if one exists.
     */
    public Converter getKeyConverter() {
        if ((keyMapping != null) && ((DatabaseMapping)keyMapping).isDirectToFieldMapping()) {
            return ((AbstractDirectMapping)keyMapping).getConverter();
        }
        return null;
    }
    
    public MapKeyMapping getKeyMapping() {
        return keyMapping;
    }

    /**
     * INTERNAL:
     * Some map keys must be obtained from the database.  This query is used to obtain the key.
     */
    public DatabaseQuery getKeyQuery() {
        return keyQuery;
    }
    
    /**
     * INTERNAL:
     * Get the selection criteria for the map key.
     */
    @Override
    public Expression getKeySelectionCriteria() {
        return keyMapping.getAdditionalSelectionCriteriaForMapKey();
    }
    
    public MapComponentMapping getValueMapping(){
        return valueMapping;
    }

    /**
     * INTERNAL:
     * Initialize the key mapping
     */
    @Override
    public void initialize(AbstractSession session, DatabaseTable keyTable) {
        getKeyMapping().preinitializeMapKey(keyTable);
        ((DatabaseMapping)keyMapping).initialize(session);
    }
    
    /**
     * CollectionTableMapContainerPolicy is for mappings where the key is stored in a table separately from the map
     * element.
     */
    @Override
    protected boolean isKeyAvailableFromElement() {
        return false;
    }

    @Override
    public boolean isMappedKeyMapPolicy() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return whether a map key this container policy represents is an attribute
     * By default this method will return false since only subclasses actually represent maps.
     */
    @Override
    public boolean isMapKeyAttribute() {
        return ((DatabaseMapping)keyMapping).isAbstractDirectMapping();
    }
    
    /**
     * INTERNAL:
     * Return if the map key this container policy represents is a OneToOne.
     */
    @Override
    public boolean isMapKeyObject() {
        return ((DatabaseMapping)keyMapping).isOneToOneMapping();
    }
    
    /**
     * INTERNAL:
     * Used in Descriptor Iteration to iterate on map keys.
     */
    @Override
    public void iterateOnMapKey(DescriptorIterator iterator, Object element) {
        Object key = ((Map.Entry)element).getKey();
        keyMapping.iterateOnMapKey(iterator, key);
    }
    
    /**
     * INTERNAL:
     * Add the provided object to the deleted objects list on the commit manager.
     * This may be overridden by subclasses to process a composite object.
     */
    @Override
    public void postCalculateChanges(ObjectChangeSet ocs, ClassDescriptor referenceDescriptor, DatabaseMapping mapping, UnitOfWorkImpl uow) {
        if (((DatabaseMapping)getKeyMapping()).isForeignReferenceMapping() && ((DatabaseMapping)getKeyMapping()).isPrivateOwned()) {
            Object key = ocs.getOldKey();
            uow.addDeletedPrivateOwnedObjects((DatabaseMapping)getKeyMapping(), key);
        }
        super.postCalculateChanges(ocs, referenceDescriptor, mapping, uow);
    }

    /**
     * INTERNAL:
     * Add the provided object to the deleted objects list on the commit manager.
     * This may be overridden by subclasses to process a composite object.
     */
    @Override
    public void postCalculateChanges(Object key, Object value, ClassDescriptor referenceDescriptor, DatabaseMapping mapping, UnitOfWorkImpl uow) {
        if (((DatabaseMapping)getKeyMapping()).isForeignReferenceMapping() && ((DatabaseMapping)getKeyMapping()).isPrivateOwned()) {
            uow.addDeletedPrivateOwnedObjects((DatabaseMapping)getKeyMapping(), key);
        }
        super.postCalculateChanges(key, value, referenceDescriptor, mapping, uow);
    }

    /**
     * INTERNAL:
     * This method is used to check the key mapping to ensure that it does not write to
     * a field that is written by another mapping.  There are two possibilities:
     * 
     * 1. The conflicting mapping has already been processed.  In that case, we add MultipleWritableMappings
     * exception to the integrity checker right away
     * 2. There are no conflicting mappings.  In that case, we store the list of fields that this mapping
     * has processed on the descriptor for the target so they can be checked as the descriptor initializes.
     */
    @Override
    public void processAdditionalWritableMapKeyFields(AbstractSession session) {
        if (!((DatabaseMapping)getKeyMapping()).isReadOnly() && (this.valueMapping instanceof CollectionMapping)) {
            CollectionMapping mapping = (CollectionMapping)valueMapping;
            Iterator<DatabaseField> i = getIdentityFieldsForMapKey().iterator();
            while (i.hasNext()){
                DatabaseField field = i.next();
                if (mapping.getReferenceDescriptor().getObjectBuilder().getMappingsByField().containsKey(field) || mapping.getReferenceDescriptor().getAdditionalWritableMapKeyFields().contains(field)) {  
                    session.getIntegrityChecker().handleError(DescriptorException.multipleWriteMappingsForField(field.toString(), mapping));
                } else {
                    mapping.getReferenceDescriptor().getAdditionalWritableMapKeyFields().add(field);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Add the key and value from provided association to the deleted objects list on the commit manager.
     */
    @Override
    public void recordPrivateOwnedRemovals(Object object,ClassDescriptor referenceDescriptor, UnitOfWorkImpl uow){
        if (((DatabaseMapping)keyMapping).isPrivateOwned()){
            Object key = ((Map.Entry)object).getKey();
            ((DatabaseMapping)keyMapping).getReferenceDescriptor().getObjectBuilder().recordPrivateOwnedRemovals(key, uow, false);
        }
        super.recordPrivateOwnedRemovals(((Map.Entry)object).getValue(), referenceDescriptor, uow);
    }

    /**
     * INTERNAL:
     * Returns whether this ContainerPolicy requires data modification events when
     * objects are added or deleted during update.
     */
    @Override
    public boolean requiresDataModificationEvents(){
        return keyMapping.requiresDataModificationEventsForMapKey();
    }
    
    /**
     * INTERNAL:
     * Return the key for the specified element.
     */
    @Override
    public Object keyFrom(Object element, AbstractSession session) {
        // key is mapped to the database table and not the object and therefore cannot be extracted from the object
        if (keyMapping != null){
            return null;
        }
        return super.keyFrom(element, session);
    }   
    
    /**
     * INTERNAL:
     * Some subclasses need to post initialize mappings associated with them.
     */
    @Override
    public void postInitialize(AbstractSession session) {
        ((DatabaseMapping)this.keyMapping).postInitialize(session);
        this.keyMapping.postInitializeMapKey(this);
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    @Override
    public void propogatePostDelete(DeleteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()){
            ((AggregateObjectMapping)keyMapping).postDeleteAttributeValue(query, ((Map.Entry)object).getKey());
        }
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    @Override
    public void propogatePostInsert(WriteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()){
            ((AggregateObjectMapping)keyMapping).postInsertAttributeValue(query, ((Map.Entry)object).getKey());
        }
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    @Override
    public void propogatePostUpdate(WriteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()){
            Object key = object;
            if (object instanceof Map.Entry){
                key = ((Map.Entry)object).getKey();
            }
            ((AggregateObjectMapping)keyMapping).postUpdateAttributeValue(query, key);
        }
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    @Override
    public void propogatePreDelete(DeleteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()){
            ((AggregateObjectMapping)keyMapping).preDeleteAttributeValue(query, ((Map.Entry)object).getKey());
        }
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    public void propogatePreInsert(WriteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()){
            ((AggregateObjectMapping)keyMapping).preInsertAttributeValue(query, ((Map.Entry)object).getKey());
        }
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     */
    @Override
    public void propogatePreUpdate(WriteObjectQuery query, Object object) {
        if (propagatesEventsToCollection()) {
            ((AggregateObjectMapping)keyMapping).preUpdateAttributeValue(query, ((Map.Entry)object).getKey());
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if the key mapping is an AggregateObjectMapping.
     * Aggregates need events propagated to them because they are not explicitly
     * deleted, updated or inserted
     */
    public boolean propagatesEventsToCollection() {
        return ((DatabaseMapping)keyMapping).isAggregateObjectMapping();
    }
    
    /**
     * INTERNAL:
     * Set the DatabaseField that will represent the key in a DirectMapMapping.
     */
    public void setKeyField(DatabaseField keyField, ClassDescriptor descriptor) {
        if (keyMapping == null) {
            AbstractDirectMapping newKeyMapping = new DirectToFieldMapping();
            newKeyMapping.setField(keyField);
            newKeyMapping.setDescriptor(descriptor);
            setKeyMapping(newKeyMapping);
        }
        if (((DatabaseMapping)keyMapping).isDirectToFieldMapping()) {
            ((AbstractDirectMapping)keyMapping).setField(keyField);;
        }
    }
    
    /**
     * INTERNAL:
     * Used during initialization of DirectMapMapping.  Sets the descriptor associated with
     * the key.
     */
    public void setDescriptorForKeyMapping(ClassDescriptor descriptor){
        ((DatabaseMapping)keyMapping).setDescriptor(descriptor);
    }
    
    /**
     * INTERNAL:
     * Set a converter on the KeyField of a DirectCollectionMapping.
     */
    public void setKeyConverter(Converter keyConverter, DirectMapMapping mapping){
        if (((DatabaseMapping)keyMapping).isDirectToFieldMapping()){
            ((AbstractDirectMapping)keyMapping).setConverter(keyConverter);
        } else {
            throw DescriptorException.cannotSetConverterForNonDirectMapping(mapping.getDescriptor(), mapping, keyConverter.getClass().getName());
        }
    }
    
    /**
     * INTERNAL:
     * Set the name of the class to be used as a converter for the key of a DirectMapMaping.
     */
    public void setKeyConverterClassName(String keyConverterClassName, DirectMapMapping mapping){
        if (((DatabaseMapping)keyMapping).isDirectToFieldMapping()){
            ((AbstractDirectMapping)keyMapping).setConverterClassName(keyConverterClassName);
        } else {
            throw DescriptorException.cannotSetConverterForNonDirectMapping(mapping.getDescriptor(), mapping, keyConverterClassName);
        }

    }
    
    public void setKeyMapping(MapKeyMapping mapping){
        if (((DatabaseMapping)mapping).isForeignReferenceMapping() && ((ForeignReferenceMapping)mapping).getIndirectionPolicy().usesIndirection()){
            throw ValidationException.mapKeyCannotUseIndirection((DatabaseMapping)mapping);
        }
        this.keyMapping = mapping;
        ((DatabaseMapping)mapping).setIsMapKeyMapping(true);
    }
    
    /**
     * INTERNAL:
     * Some map keys must be obtained from the database.  This query is used to obtain the key
     * @param keyQuery
     */
    public void setKeyQuery(DatabaseQuery keyQuery) {
        this.keyQuery = keyQuery;
    }
    
    public void setValueMapping(MapComponentMapping mapping) {
        this.valueMapping = mapping;
    }
    
    /**
     * INTERNAL:
     * Return whether data for a map key must be included on a Delete datamodification event
     * If the keyMapping is privateOwned, that data should be.
     */
    @Override
    public boolean shouldIncludeKeyInDeleteEvent() {
        return ((DatabaseMapping)keyMapping).isPrivateOwned();
    }
    
    
    /**
     * INTERNAL:
     * Certain types of container policies require an extra update statement after a relationship
     * is inserted.  Return whether this update statement is required.
     */
    @Override
    public boolean shouldUpdateForeignKeysPostInsert() {
        return !((DatabaseMapping)keyMapping).isReadOnly();
    }

    /**
     * INTERNAL:
     * Update the joined mapping indices
     * Adds the key mapping and it's index to the list of joined mappings.
     */
    @Override
    public int updateJoinedMappingIndexesForMapKey(Map<DatabaseMapping, Object> indexList, int index){
        indexList.put((DatabaseMapping)keyMapping, index);
        return getAdditionalFieldsForJoin(null).size();
    }
    
    /**
     * INTERNAL:
     * Allow the key to be unwrapped.  This will be overridden by container policies that
     * allow keys that are entities.
     */
    @Override
    public Object unwrapKey(Object key, AbstractSession session){
        return keyMapping.unwrapKey(key, session);
    }

    /**
     * INTERNAL:
     * This method is used to load a relationship from a list of PKs. This list
     * may be available if the relationship has been cached.
     */
    @Override
    public Object valueFromPKList(Object[] pks, AbstractRecord foreignKeys, ForeignReferenceMapping mapping, AbstractSession session){
        int mapSize = pks.length/2;
        Object result = containerInstance(mapSize);
        Object[] keys = new Object[mapSize];
        Object[] values = new Object[mapSize];
        for (int index = 0; index < pks.length; ++index){
            keys[index/2] = pks[index];
            ++index;
            values[index/2] = pks[index];
        }
        
        List<Object> keyObjects = keyMapping.createMapComponentsFromSerializableKeyInfo(keys, session);
        if (((DatabaseMapping)valueMapping).isElementCollectionMapping()) {
            for(int i = 0; i < mapSize; i++){
                addInto(keyObjects.get(i), values[i], result, session);
            }
        } else {
            Map<Object, Object> fromCache = session.getIdentityMapAccessor().getAllFromIdentityMapWithEntityPK(values, elementDescriptor);
            DatabaseRecord translationRow = new DatabaseRecord();
            List foreignKeyValues = new ArrayList(pks.length - fromCache.size());
            
            CacheKeyType cacheKeyType = this.elementDescriptor.getCachePolicy().getCacheKeyType();
            for (int index = 0; index < mapSize; ++index){
                Object pk = values[index];
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
                ReadAllQuery query = new ReadAllQuery(elementDescriptor.getJavaClass());
                query.setIsExecutionClone(true);
                query.setTranslationRow(translationRow);
                query.setSession(session);
                query.setSelectionCriteria(elementDescriptor.buildBatchCriteriaByPK(query.getExpressionBuilder(), query));
                Collection<Object> temp = (Collection<Object>) session.executeQuery(query);
                if (temp.size() < foreignKeyValues.size()){
                    //Not enough results have been found, this must be a stale collection with a removed
                    //element.  Execute a reload based on FK.
                    return session.executeQuery(mapping.getSelectionQuery(), foreignKeys);
                }
                for (Object element: temp){
                    Object pk = elementDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(element, session);
                    fromCache.put(pk, element);
                }
            }
        
            Iterator keyIterator = keyObjects.iterator();
            for(Object key : values){
                addInto(keyIterator.next(), fromCache.get(key), result, session);
            }
        }
        return result;
    }
    
}
