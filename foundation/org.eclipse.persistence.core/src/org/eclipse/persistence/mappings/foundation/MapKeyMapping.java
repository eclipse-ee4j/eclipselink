/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial API check-in for MappedKeyMapContainerPolicy
package org.eclipse.persistence.mappings.foundation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * MapKeyMapping is implemented by DatabaseMappings that can be used to map the key in a map
 * that uses a MappedKeyMapContainerPolicy.  This interface provides the facilities to retreive data
 * for the key from the database, to get data from the object to put in the database, and to appropriately
 * initialize the mappings.
 *
 * @see MappedKeyMapContainerPolicy
 * @see AbstractDirectMapping
 * @see org.eclipse.persistence.mappings.AggregateObjectMapping
 * @see org.eclipse.persistence.mappings.OneToOneMapping
 * @author tware
 *
 */
public interface MapKeyMapping extends MapComponentMapping {

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the selection query is being initialized to add the fields for the map key to the query
     */
    void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression);

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query
     */
    void addFieldsForMapKey(AbstractRecord joinRow);

    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy.  Add the target of this mapping to the deleted
     * objects list if necessary
     *
     * This method is used for removal of private owned relationships
     *
     * @param object
     * @param deletedObjects
     */
    void addKeyToDeletedObjectsList(Object object, Map deletedObjects);

    /**
     * Build a clone of the given element in a unitOfWork
     * @param element
     * @param cloningSession
     * @param isExisting
     * @return
     */
    Object buildElementClone(Object element, Object parent, CacheKey cacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache);

    /**
     * INTERNAL:
     * Depending on the MapKeyMapping, a different selection query may be required to retrieve the
     * map when the map is based on a DirectCollectionMapping
     * @return
     */
    ReadQuery buildSelectionQueryForDirectCollectionKeyMapping(ContainerPolicy containerPolicy);

    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit to the map key
     */
    void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, boolean getAttributeValueFromObject, Set cascadeErrors);

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject);

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject);

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection and a join query is used.
     * Returns the key.
     */
    Object createMapComponentFromJoinedRow(AbstractRecord dbRow, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected);

    /**
     * INTERNAL:
     * Create a query key that links to the map key
     * @return
     */
    QueryKey createQueryKeyForMapKey();

    /**
     * INTERNAL:
     * Creates the Array of simple types used to recreate this map.
     */
    Object createSerializableMapKeyInfo(Object key, AbstractSession session);

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.
     * This may return the value directly in case of a simple key or will be used as the FK to load a related entity.
     */
    List<Object> createMapComponentsFromSerializableKeyInfo(Object[] keyInfo, AbstractSession session);

    /**
     * INTERNAL:
     * Create an instance of the Key object from the key information extracted from the map.
     * This key object may be a shallow stub of the actual object if the key is an Entity type.
     */
    Object createStubbedMapComponentFromSerializableKeyInfo(Object keyInfo, AbstractSession session);

    /**
     * INTERNAL:
     * For mappings used as MapKeys in MappedKeyContainerPolicy, Delete the passed object if necessary.
     *
     * This method is used for removal of private owned relationships
     *
     * @param objectDeleted
     * @param session
     */
    void deleteMapKey(Object objectDeleted, AbstractSession session);

    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query
     * @return
     */
    List<DatabaseTable> getAdditionalTablesForJoinQuery();

    /**
     * INTERNAL:
     * Get all the fields for the map key
     */
    List<DatabaseField> getAllFieldsForMapKey();

    /**
     * INTERNAL:
     * Return a Map of any foreign keys defined within the the MapKey
     * @return
     */
    Map<DatabaseField, DatabaseField> getForeignKeyFieldsForMapKey();

    /**
     * INTERNAL:
     * Get the descriptor for the Map Key
     * @return
     */
    ClassDescriptor getReferenceDescriptor();

    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields
     * @return
     */
    List<DatabaseField> getIdentityFieldsForMapKey();

    /**
     * INTERNAL:
     * Return the query that is used when this mapping is part of a joined relationship
     * @return
     */
    ObjectLevelReadQuery getNestedJoinQuery(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session);

    /**
     * INTERNAL:
     * Return the selection criteria necessary to select the target object
     * @return
     */
    Expression getAdditionalSelectionCriteriaForMapKey();

    /**
     * INTERNAL:
     * If required, get the targetVersion of the source object from the merge manager
     * @return
     */
    Object getTargetVersionOfSourceObject(Object object, Object parent, MergeManager mergeManager, AbstractSession targetSession);

    /**
     * INTERNAL:
     * Return the class this key mapping maps or the descriptor for it
     * @return
     */
    Object getMapKeyTargetType();

    /**
     * INTERNAL:
     * Called when iterating through descriptors to handle iteration on this mapping when it is used as a MapKey
     * @param iterator
     * @param element
     */
    void iterateOnMapKey(DescriptorIterator iterator, Object element);

    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query
     * @return
     */
    Map extractIdentityFieldsForQuery(Object key, AbstractSession session);

    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping
     */
    void preinitializeMapKey(DatabaseTable table) throws DescriptorException;

    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key after initializing the mapping
     */
    void postInitializeMapKey(MappedKeyMapContainerPolicy policy) throws DescriptorException;

    /**
     * INTERNAL:
     * Return whether this mapping requires extra queries to update the rows if it is
     * used as a key in a map.  This will typically be true if there are any parts to this mapping
     * that are not read-only.
     */
    boolean requiresDataModificationEventsForMapKey();

    /**
     * INTERNAL:
     * Allow the key mapping to unwrap the object
     * @param key
     * @param session
     * @return
     */

    Object unwrapKey(Object key, AbstractSession session);

    /**
     * INTERNAL:
     * Allow the key mapping to wrap the object
     * @param key
     * @param session
     * @return
     */
    Object wrapKey(Object key, AbstractSession session);

}
