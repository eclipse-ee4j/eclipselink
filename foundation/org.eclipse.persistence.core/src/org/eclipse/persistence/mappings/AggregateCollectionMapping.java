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
 *     12/30/2010-2.3 Guy Pelletier  
 *       - 312253: Descriptor exception with Embeddable on DDL gen
 *     07/27/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     02/11/2013-2.5 Guy Pelletier 
 *       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.SQLUpdateStatement;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AggregateCollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * <p><b>Purpose</b>: The aggregate collection mapping is used to represent the aggregate relationship between a single
 * source object and a collection of target objects. The target objects cannot exist without the existence of the
 * source object (privately owned)
 * Unlike the normal aggregate mapping, there is a target table being mapped from the target objects.
 * Unlike normal 1:m mapping, there is no 1:1 back reference mapping, as foreign key constraints have been resolved by the aggregation.
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 */
public class AggregateCollectionMapping extends CollectionMapping implements RelationalMapping, MapComponentMapping, EmbeddableMapping {

    /** This is a key in the target table which is a foreign key in the target table. */
    protected Vector<DatabaseField> targetForeignKeyFields;

    /** This is a primary key in the source table that is used as foreign key in the target table */
    protected Vector<DatabaseField> sourceKeyFields;

    /** Foreign keys in the target table to the related keys in the source table */
    protected Map<DatabaseField, DatabaseField> targetForeignKeyToSourceKeys;

    /** Map the name of a field in the aggregate collection descriptor to a field in the actual table specified in the mapping. */
    protected Map<String, DatabaseField> aggregateToSourceFields;

    /** Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */
    protected Map<String, Map<String, DatabaseField>> nestedAggregateToSourceFields;

    /** In RemoteSession case the mapping needs the reference descriptor serialized from the server, 
     * but referenceDescriptor attribute defined as transient in the superclass. To overcome that
     * in non-remote case referenceDescriptor is assigned to remoteReferenceDescriptor; in remote - another way around.
     */  
    protected ClassDescriptor remoteReferenceDescriptor;
    
    /** Default source table that should be used with the default source fields of this mapping. */
    protected DatabaseTable defaultSourceTable;

    /** Indicates whether the entire target object is primary key - in that case the object can't be updated in the db,
     * but rather deleted and then re-inserted. 
     */
    protected boolean isEntireObjectPK;
    
    /** These queries used to update listOrderField
     */
    protected transient DataModifyQuery updateListOrderFieldQuery;
    protected transient DataModifyQuery bulkUpdateListOrderFieldQuery;
    protected transient DataModifyQuery pkUpdateListOrderFieldQuery;

    /** indicates whether listOrderField value could be updated in the db. Used only if listOrderField!=null */
    protected boolean isListOrderFieldUpdatable;
    
    protected static final String min = "min";
    protected static final String max = "max";
    protected static final String shift = "shift";

    protected static final String pk = "pk";
    protected static final String bulk = "bulk";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public AggregateCollectionMapping() {
        this.aggregateToSourceFields = new HashMap(5);
        this.nestedAggregateToSourceFields = new HashMap<String, Map<String, DatabaseField>>(5);
        this.targetForeignKeyToSourceKeys = new HashMap(5);
        this.sourceKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.targetForeignKeyFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        this.deleteAllQuery = new DeleteAllQuery();
        //aggregates should always cascade all operations
        this.setCascadeAll(true);
        this.isListOrderFieldSupported = true;
        this.isListOrderFieldUpdatable = true;
        this.isPrivateOwned = true;        
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * In JPA users may specify overrides to apply to a many to many mapping
     * on a shared embeddable descriptor. These settings are applied at
     * initialize time, after the reference descriptor is cloned. In an
     * aggregate collection case, this is not supported and currently silently
     * ignored and does nothing.
     */
    public void addOverrideManyToManyMapping(ManyToManyMapping mapping) {
        // Not supported at this time ...
    }
    
    /**
     * INTERNAL:
     * In JPA users may specify overrides to apply to a unidirectional one to 
     * many mapping on a shared embeddable descriptor. These settings are 
     * applied at initialize time, after the reference descriptor is cloned. In 
     * an aggregate collection case, this is not supported and currently 
     * silently ignored and does nothing.
     */
    public void addOverrideUnidirectionalOneToManyMapping(UnidirectionalOneToManyMapping mapping) {
        // Not supported at this time ...
    }
    
    /**
     * Add a converter to be applied to a mapping of the aggregate descriptor.
     */
    public void addConverter(Converter converter, String attributeName) {
        // Not supported at this time ...
    }
    
    /**
     * PUBLIC:
     * Maps a field name in the aggregate descriptor
     * to a field name in the source table.
     */
    public void addFieldNameTranslation(String sourceFieldName, String aggregateFieldName) {
        addFieldTranslation(new DatabaseField(sourceFieldName), aggregateFieldName);
    }
    
    /**
     * PUBLIC:
     * Maps a field name in the aggregate descriptor
     * to a field in the source table.
     */
    public void addFieldTranslation(DatabaseField sourceField, String aggregateField) {
        aggregateToSourceFields.put(aggregateField, sourceField);
    }

    /**
     * PUBLIC:
     * 
     * Maps a field name in the aggregate descriptor
     * to a field name in the source table.
     */
    public void addFieldTranslations(Map<String, DatabaseField> map) {
        aggregateToSourceFields.putAll(map);
    }

    /**
     * PUBLIC:
     * Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */
    public void addNestedFieldNameTranslation(String attributeName, String sourceFieldName, String aggregateFieldName) {
        addNestedFieldTranslation(attributeName, new DatabaseField(sourceFieldName), aggregateFieldName);
    }
    
    /**
     * PUBLIC:
     * Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFieldNames
     * that should be applied to this mapping.
     */
    public void addNestedFieldTranslation(String attributeName, DatabaseField sourceField, String aggregateFieldName) {
        Map<String, DatabaseField> attributeFieldNameTranslation = nestedAggregateToSourceFields.get(attributeName);
        
        if (attributeFieldNameTranslation == null) {
            attributeFieldNameTranslation = new HashMap<String, DatabaseField>(5);
            nestedAggregateToSourceFields.put(attributeName, attributeFieldNameTranslation);
        }
        
        attributeFieldNameTranslation.put(aggregateFieldName, sourceField);
    }

    /**
     * PUBLIC:
     * Map the name of an attribute of the reference descriptor mapped with AggregateCollectionMapping to aggregateToSourceFields
     * that should be applied to this mapping.
     */
    public void addNestedFieldNameTranslations(String attributeName, Map<String, DatabaseField> map) {
        Map<String, DatabaseField> attributeFieldNameTranslation = nestedAggregateToSourceFields.get(attributeName);
        if (attributeFieldNameTranslation == null) {
            nestedAggregateToSourceFields.put(attributeName, map);
        } else {
            attributeFieldNameTranslation.putAll(map);
        }
    }

    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-M aggregate collection mapping.
     * Both the target foreign key field and the source primary key field must be specified.
     */
    @Override
    public void addTargetForeignKeyField(DatabaseField targetForeignKey, DatabaseField sourceKey) {
        getTargetForeignKeyFields().addElement(targetForeignKey);
        getSourceKeyFields().addElement(sourceKey);
    }
    
    /**
     * PUBLIC:
     * Define the target foreign key relationship in the 1-M aggregate collection mapping.
     * Both the target foreign key field name and the source primary key field name must be specified.
     */
    public void addTargetForeignKeyFieldName(String targetForeignKey, String sourceKey) {
        addTargetForeignKeyField(new DatabaseField(targetForeignKey), new DatabaseField(sourceKey));
    }

    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy the vector without re-registering the target objects.
     */
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        if (attributeValue == null) {
            return containerPolicy.containerInstance(1);
        }

        Object clonedAttributeValue = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));
        if (isSynchronizeOnMerge) {
            synchronized (attributeValue) {
                for (Object valuesIterator = containerPolicy.iteratorFor(attributeValue);
                         containerPolicy.hasNext(valuesIterator);) {
                    Object wrappedElement = containerPolicy.nextEntry(valuesIterator, unitOfWork);
                    Object cloneValue = buildElementBackupClone(containerPolicy.unwrapIteratorResult(wrappedElement), unitOfWork);
                    containerPolicy.addInto(containerPolicy.keyFromIterator(valuesIterator), cloneValue, clonedAttributeValue, unitOfWork);
                }
            }
        } else {
             for (Object valuesIterator = containerPolicy.iteratorFor(attributeValue);
                     containerPolicy.hasNext(valuesIterator);) {
                Object wrappedElement = containerPolicy.nextEntry(valuesIterator, unitOfWork);
                Object cloneValue = buildElementBackupClone(containerPolicy.unwrapIteratorResult(wrappedElement), unitOfWork);
                containerPolicy.addInto(containerPolicy.keyFromIterator(valuesIterator), cloneValue, clonedAttributeValue, unitOfWork);
            }
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the objects, use the attribute value.
     * this is identical to the super class except that the element must be added to the new
     * aggregates collection so that the referenced objects will be cloned correctly
     */
    @Override
    public Object buildCloneForPartObject(Object attributeValue, Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession, Integer refreshCascade, boolean isExisting, boolean isFromSharedCache) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        if (attributeValue == null) {
            return containerPolicy.containerInstance(1);
        }
        Object clonedAttributeValue = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));

        Object temporaryCollection = null;
        if (isSynchronizeOnMerge) {
            // I need to synchronize here to prevent the collection from changing while I am cloning it.
            // This will occur when I am merging into the cache and I am instantiating a UOW valueHolder at the same time
            // I can not synchronize around the clone, as this will cause deadlocks, so I will need to copy the collection then create the clones
            // I will use a temporary collection to help speed up the process
            synchronized (attributeValue) {
                temporaryCollection = containerPolicy.cloneFor(attributeValue);
            }
        } else {
            temporaryCollection = attributeValue;
        }
        for (Object valuesIterator = containerPolicy.iteratorFor(temporaryCollection);
                 containerPolicy.hasNext(valuesIterator);) {
            Object wrappedElement = containerPolicy.nextEntry(valuesIterator, cloningSession);
            Object originalElement = containerPolicy.unwrapIteratorResult(wrappedElement);

            //need to add to aggregate list in the case that there are related objects.
            if (cloningSession.isUnitOfWork() && ((UnitOfWorkImpl)cloningSession).isOriginalNewObject(original)) {
                ((UnitOfWorkImpl)cloningSession).addNewAggregate(originalElement);
            }
            Object cloneValue = buildElementClone(originalElement, clone, cacheKey, refreshCascade, cloningSession, isExisting, isFromSharedCache);
            Object clonedKey = containerPolicy.buildCloneForKey(containerPolicy.keyFromIterator(valuesIterator), clone, cacheKey, refreshCascade, cloningSession, isExisting, isFromSharedCache);
            containerPolicy.addInto(clonedKey, cloneValue, clonedAttributeValue, cloningSession);
        }
        if(temporaryCollection instanceof IndirectList) {
            ((IndirectList)clonedAttributeValue).setIsListOrderBrokenInDb(((IndirectList)temporaryCollection).isListOrderBrokenInDb());
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Clone the aggregate collection, if necessary.
     */
    protected Object buildElementBackupClone(Object element, UnitOfWorkImpl unitOfWork) {
        // Do not clone for read-only.
        if (unitOfWork.isClassReadOnly(element.getClass(), getReferenceDescriptor())) {
            return element;
        }

        ClassDescriptor aggregateDescriptor = getReferenceDescriptor(element.getClass(), unitOfWork);
        Object clonedElement = aggregateDescriptor.getObjectBuilder().buildBackupClone(element, unitOfWork);

        return clonedElement;
    }

    /**
     * INTERNAL:
     * Clone the aggregate collection, if necessary.
     */
    public Object buildElementClone(Object element, Object parent, CacheKey parentCacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache) {
        // Do not clone for read-only.
        if (cloningSession.isUnitOfWork() && cloningSession.isClassReadOnly(element.getClass(), getReferenceDescriptor())) {
            return element;
        }

        ClassDescriptor aggregateDescriptor = getReferenceDescriptor(element.getClass(), cloningSession);

        // bug 2612602 as we are building the working copy make sure that we call to correct clone method.
        Object clonedElement = aggregateDescriptor.getObjectBuilder().instantiateWorkingCopyClone(element, cloningSession);
        aggregateDescriptor.getObjectBuilder().populateAttributesForClone(element, parentCacheKey, clonedElement, refreshCascade, cloningSession);
        
        if (cloningSession.isUnitOfWork()){
            // CR 4155 add the originals to the UnitOfWork so that we can find it later in the merge
            // as aggregates have no identity.  If we don't do this we will loose indirection information.
            ((UnitOfWorkImpl)cloningSession).getCloneToOriginals().put(clonedElement, element);
        }
        return clonedElement;
    }

    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    @Override
    public void collectQueryParameters(Set<DatabaseField> cacheFields){
        for (DatabaseField field : getSourceKeyFields()) {
            cacheFields.add(field);
        }
    }

    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
        //aggregate objects are not registered but their mappings should be.
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object wrappedObject = cp.nextEntry(cloneIter, uow);
            Object nextObject = cp.unwrapIteratorResult(wrappedObject);
            if (nextObject != null) {
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadeDiscoverAndPersistUnregisteredNewObjects(nextObject, newObjects, unregisteredExistingObjects, visitedObjects, uow, cascadeErrors);
                cp.cascadeDiscoverAndPersistUnregisteredNewObjects(wrappedObject, newObjects, unregisteredExistingObjects, visitedObjects, uow, cascadeErrors);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // Aggregate objects are not registered but their mappings should be.
        Object attributeValue = getAttributeValueFromObject(object);
        if ((attributeValue == null)
            // Also check if the source is new, then must always cascade.
            || (!this.indirectionPolicy.objectIsInstantiated(attributeValue) && !uow.isCloneNewObject(object))) {
            return;
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = this.containerPolicy;
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object wrappedObject = cp.nextEntry(cloneIter, uow);
            Object nextObject = cp.unwrapIteratorResult(wrappedObject);
            if (nextObject != null && (! visitedObjects.containsKey(nextObject))){
                visitedObjects.put(nextObject, nextObject);
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadeRegisterNewForCreate(nextObject, uow, visitedObjects);
                cp.cascadeRegisterNewIfRequired(wrappedObject, uow, visitedObjects);
            }
        }
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        //aggregate objects are not registered but their mappings should be.
        Object cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null)) {
            return;
        }
        // PERF: If not instantiated, then avoid instantiating, delete-all will handle deletion.
        if (usesIndirection() && (!mustDeleteReferenceObjectsOneByOne())) {
            if (!this.indirectionPolicy.objectIsInstantiated(cloneAttribute)) {
                return;
            }
        }

        ObjectBuilder builder = null;
        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object wrappedObject = cp.nextEntry(cloneIter, uow);
            Object nextObject = cp.unwrapIteratorResult(wrappedObject);
            if (nextObject != null && ( ! visitedObjects.containsKey(nextObject) ) ){
                visitedObjects.put(nextObject, nextObject);
                if (this.isCascadeOnDeleteSetOnDatabase) {
                    uow.getCascadeDeleteObjects().add(nextObject);
                }
                builder = getReferenceDescriptor(nextObject.getClass(), uow).getObjectBuilder();
                builder.cascadePerformRemove(nextObject, uow, visitedObjects);
                cp.cascadePerformRemoveIfRequired(wrappedObject, uow, visitedObjects);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade perform removal of orphaned private owned objects from the UnitOfWorkChangeSet
     */
    public void cascadePerformRemovePrivateOwnedObjectFromChangeSetIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // if the object is not instantiated, do not instantiate or cascade
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue != null && getIndirectionPolicy().objectIsInstantiated(attributeValue)) {
            Object cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
            ContainerPolicy cp = getContainerPolicy();
            for (Object cloneIter = cp.iteratorFor(cloneObjectCollection); cp.hasNext(cloneIter);) {
                Object referencedObject = cp.next(cloneIter, uow);
                if (referencedObject != null && !visitedObjects.containsKey(referencedObject)) {
                    visitedObjects.put(referencedObject, referencedObject);
                    ObjectBuilder builder = getReferenceDescriptor(referencedObject.getClass(), uow).getObjectBuilder();
                    builder.cascadePerformRemovePrivateOwnedObjectFromChangeSet(referencedObject, uow, visitedObjects);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    public Object clone() {
        AggregateCollectionMapping mappingObject = (AggregateCollectionMapping)super.clone();
        mappingObject.setTargetForeignKeyToSourceKeys(new HashMap(getTargetForeignKeyToSourceKeys()));
        mappingObject.setSourceKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getSourceKeyFields()));
        mappingObject.setTargetForeignKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getTargetForeignKeyFields()));
        mappingObject.aggregateToSourceFields = new HashMap(this.aggregateToSourceFields);
        mappingObject.nestedAggregateToSourceFields = new HashMap(this.nestedAggregateToSourceFields);
        if(updateListOrderFieldQuery != null) {
            mappingObject.updateListOrderFieldQuery = this.updateListOrderFieldQuery; 
        }
        if(bulkUpdateListOrderFieldQuery != null) {
            mappingObject.bulkUpdateListOrderFieldQuery = this.bulkUpdateListOrderFieldQuery; 
        }
        if(pkUpdateListOrderFieldQuery != null) {
            mappingObject.pkUpdateListOrderFieldQuery = this.pkUpdateListOrderFieldQuery; 
        }

        return mappingObject;
    }

    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two aggregate collections
     * @return ChangeRecord
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

        cloneAttribute = getAttributeValueFromObject(clone);

        if ((cloneAttribute != null) && (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            //If the clone's valueholder was not triggered then no changes were made.
            return null;
        }
        if (!owner.isNew()) {
            backUpAttribute = getAttributeValueFromObject(backUp);
            if ((backUpAttribute == null) && (cloneAttribute == null)) {
                return null;
            }
            ContainerPolicy cp = getContainerPolicy();
            Object backupCollection = null;
            Object cloneCollection = null;

            cloneCollection = getRealCollectionAttributeValueFromObject(clone, session);
            backupCollection = getRealCollectionAttributeValueFromObject(backUp, session);

            if (cp.sizeFor(backupCollection) != cp.sizeFor(cloneCollection)) {
                return convertToChangeRecord(cloneCollection, backupCollection, owner, session);
            }

            boolean change = false;
            if (cp.isMapPolicy()){
                change = compareMapCollectionForChange((Map)cloneCollection, (Map)backupCollection, session);
            } else {
                Object cloneIterator = cp.iteratorFor(cloneCollection);
                Object backUpIterator = cp.iteratorFor(backupCollection);
                // For bug 2863721 must use a different UnitOfWorkChangeSet as here just
                // seeing if changes are needed.  If changes are needed then a
                // real changeSet will be created later.
                UnitOfWorkChangeSet uowComparisonChangeSet = new UnitOfWorkChangeSet(session);
                while (cp.hasNext(cloneIterator)) {
                    Object cloneObject = cp.next(cloneIterator, session);
    
                    // For CR#2285 assume that if null is added the collection has changed.
                    if (cloneObject == null) {
                        change = true;
                        break;
                    }
                    Object backUpObject = null;
                    if (cp.hasNext(backUpIterator)) {
                            backUpObject = cp.next(backUpIterator, session);
                    } else {
                        change = true;
                        break;
                    }
                    if (cloneObject.getClass().equals(backUpObject.getClass())) {
                        ObjectBuilder builder = getReferenceDescriptor(cloneObject.getClass(), session).getObjectBuilder();
                        ObjectChangeSet initialChanges = builder.createObjectChangeSet(cloneObject, uowComparisonChangeSet, owner.isNew(), session);
        
                        //compare for changes will return null if no change is detected and I need to remove the changeSet
                        ObjectChangeSet changes = builder.compareForChange(cloneObject, backUpObject, uowComparisonChangeSet, session);
                        if (changes != null) {
                            change = true;
                            break;
                        }
                    } else {
                        change = true;
                        break;
                    }
                }
                if (cp.hasNext(backUpIterator)){
                    change = true;
                }
            }
            if ((change == true)) {
                return convertToChangeRecord(cloneCollection, backupCollection, owner, session);
            } else {
                return null;
            }
        }

        return convertToChangeRecord(getRealCollectionAttributeValueFromObject(clone, session), containerPolicy.containerInstance(), owner, session);
    }
  
    
    /**
     * INTERNAL:
     * Determine if an AggregateCollection that is contained as a map has changed by comparing the values in the
     * clone to the values in the backup.
     * @param cloneObjectCollection
     * @param backUpCollection
     * @param session
     * @return
     */
    protected boolean compareMapCollectionForChange(Map cloneObjectCollection, Map backUpCollection, AbstractSession session){
        HashMap originalKeyValues = new HashMap(10);
        HashMap cloneKeyValues = new HashMap(10);

        Object backUpIter = containerPolicy.iteratorFor(backUpCollection);
        while (containerPolicy.hasNext(backUpIter)) {// Make a lookup of the objects
            Map.Entry entry = (Map.Entry)containerPolicy.nextEntry(backUpIter, session);
            originalKeyValues.put(entry.getKey(), entry.getValue());
        }

        UnitOfWorkChangeSet uowComparisonChangeSet = new UnitOfWorkChangeSet(session);
        Object cloneIter = containerPolicy.iteratorFor(cloneObjectCollection);
        while (containerPolicy.hasNext(cloneIter)) {//Compare them with the objects from the clone
            Map.Entry wrappedFirstObject = (Map.Entry)containerPolicy.nextEntry(cloneIter, session);
            Object firstValue = wrappedFirstObject.getValue();
            Object firstKey = wrappedFirstObject.getKey();
            Object backupValue = originalKeyValues.get(firstKey);
            if (!originalKeyValues.containsKey(firstKey)) {
                return true;
            } else if ((backupValue == null) && (firstValue != null)) {//the object was not in the backup
                return true;
            } else {
                ObjectBuilder builder = getReferenceDescriptor(firstValue.getClass(), session).getObjectBuilder();

                ObjectChangeSet changes = builder.compareForChange(firstValue, backupValue, uowComparisonChangeSet, session);
                if (changes != null) {
                        return true;
                } else {
                    originalKeyValues.remove(firstKey);
                }
            }
        }
        return !originalKeyValues.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Old and new lists are compared and only the changes are written to the database.
     * Called only if listOrderField != null
     */
    protected void compareListsAndWrite(List previousList, List currentList, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if(this.isListOrderFieldUpdatable) {
            compareListsAndWrite_UpdatableListOrderField(previousList, currentList, query);
        } else {
            compareListsAndWrite_NonUpdatableListOrderField(previousList, currentList, query);
        }
    }
    
    /**
     * INTERNAL:
     * Old and new lists are compared and only the changes are written to the database.
     * Called only if listOrderField != null
     */
    protected void compareListsAndWrite_NonUpdatableListOrderField(List previousList, List currentList, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        boolean shouldRepairOrder = false;
        if(currentList instanceof IndirectList) {
            shouldRepairOrder = ((IndirectList)currentList).isListOrderBrokenInDb();
        }
        
        HashMap<Object, Object[]> previousAndCurrentByKey = new HashMap<Object, Object[]>();
        int pkSize = getReferenceDescriptor().getPrimaryKeyFields().size();
        
        // First index the current objects by their primary key.
        for (int i=0; i < currentList.size(); i++) {
            Object currentObject = currentList.get(i);
            try {
                CacheId primaryKey = (CacheId)getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(currentObject, query.getSession());
                primaryKey.add(i);
                Object[] previousAndCurrent = new Object[]{null, currentObject};
                previousAndCurrentByKey.put(primaryKey, previousAndCurrent);
            } catch (NullPointerException e) {
                // For CR#2646 quietly discard nulls added to a collection mapping.
                // This try-catch is essentially a null check on currentObject, for
                // ideally the customer should check for these themselves.
                if (currentObject != null) {
                    throw e;
                }
            }
        }

        if (shouldRepairOrder) {
            DeleteAllQuery deleteAllQuery = (DeleteAllQuery)this.deleteAllQuery;
            if (this.isCascadeOnDeleteSetOnDatabase) {
                deleteAllQuery = (DeleteAllQuery)deleteAllQuery.clone();
                deleteAllQuery.setIsInMemoryOnly(false);
            }
            deleteAllQuery.executeDeleteAll(query.getSession().getSessionForClass(getReferenceClass()), query.getTranslationRow(), new Vector(previousList));
        } else {
            // Next index the previous objects (read from db or from backup in uow)
            for(int i=0; i < previousList.size(); i++) {
                Object previousObject = previousList.get(i);
                CacheId primaryKey = (CacheId)getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(previousObject, query.getSession());
                primaryKey.add(i);
                Object[] previousAndCurrent = previousAndCurrentByKey.get(primaryKey);
                if (previousAndCurrent == null) {
                    // there's no current object - that means that previous object should be deleted
                    DatabaseRecord extraData = new DatabaseRecord(1);
                    extraData.put(this.listOrderField, i);
                    objectRemovedDuringUpdate(query, previousObject, extraData);
                } else {
                    previousAndCurrent[0] = previousObject;
                }
            }
        }

        Iterator<Map.Entry<Object, Object[]>> it = previousAndCurrentByKey.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Object, Object[]> entry = it.next();
            Object key = entry.getKey();
            Object[] previousAndCurrent = entry.getValue();
            // previousObject may be null, meaning currentObject has been added to the list
            Object previousObject = previousAndCurrent[0];
            // currentObject is not null
            Object currentObject = previousAndCurrent[1];
            
            if(previousObject == null) {
                // there's no previous object - that means that current object should be added.
                // index of currentObject in currentList
                int iCurrent = (Integer)((CacheId)key).getPrimaryKey()[pkSize];
                DatabaseRecord extraData = new DatabaseRecord(1);
                extraData.put(this.listOrderField, iCurrent);
                
                objectAddedDuringUpdate(query, currentObject, null, extraData);
            } else {
                if(!this.isEntireObjectPK) {
                    objectUnchangedDuringUpdate(query, currentObject, previousObject);
                }
            }
        }        

        if(shouldRepairOrder) {
            ((IndirectList)currentList).setIsListOrderBrokenInDb(false);
        }
    }
    
    /**
     * INTERNAL:
     * Old and new lists are compared and only the changes are written to the database.
     * Called only if listOrderField != null
     */
    protected void compareListsAndWrite_UpdatableListOrderField(List previousList, List currentList, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        boolean shouldRepairOrder = false;
        if(currentList instanceof IndirectList) {
            shouldRepairOrder = ((IndirectList)currentList).isListOrderBrokenInDb();
        }
        
        // Object[] = {previousObject, currentObject, previousIndex, currentIndex}
        HashMap<Object, Object[]> previousAndCurrentByKey = new HashMap<Object, Object[]>();
        // a SortedMap, current index mapped by previous index, both indexes must exist and be not equal.
        TreeMap<Integer, Integer> currentIndexByPreviousIndex = new TreeMap<Integer, Integer>();

        // First index the current objects by their primary key.
        for(int i=0; i < currentList.size(); i++) {
            Object currentObject = currentList.get(i);
            try {
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(currentObject, query.getSession());
                Object[] previousAndCurrent = new Object[]{null, currentObject, null, i};
                previousAndCurrentByKey.put(primaryKey, previousAndCurrent);
            } catch (NullPointerException e) {
                // For CR#2646 quietly discard nulls added to a collection mapping.
                // This try-catch is essentially a null check on currentObject, for
                // ideally the customer should check for these themselves.
                if (currentObject != null) {
                    throw e;
                }
            }
        }

        // Next index the previous objects (read from db or from backup in uow), also remove the objects to be removed.
        for(int i=0; i < previousList.size(); i++) {
            Object previousObject = previousList.get(i);
            Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(previousObject, query.getSession());
            Object[] previousAndCurrent = previousAndCurrentByKey.get(primaryKey);
            if(previousAndCurrent == null) {
                // there's no current object - that means that previous object should be deleted
                objectRemovedDuringUpdate(query, previousObject, null);
            } else {
                previousAndCurrent[0] = previousObject;
                previousAndCurrent[2] = i;
                int iCurrent = (Integer)previousAndCurrent[3];
                if(i != iCurrent || shouldRepairOrder) {
                    currentIndexByPreviousIndex.put(i, iCurrent);
                }
            }
        }

        // some order indexes should be changed
        if(!currentIndexByPreviousIndex.isEmpty()) {
            boolean shouldUpdateOrderUsingPk = shouldRepairOrder;
            if(!shouldUpdateOrderUsingPk) {
                // search for cycles in order changes, such as, for instance: 
                //   previous index  1, 2 
                //   current  index  2, 1
                // or
                //   previous index  1, 3, 5 
                //   current  index  3, 5, 1
                // those objects order index can't be updated using their previous order index value - should use pk in where clause instead.
                // For now, if a cycle is found let's update all order indexes using pk.
                // Ideally that should be refined in the future so that only indexes participating in cycles updated using pks - others still through bulk update.
                boolean isCycleFound = false;
                int iCurrentMax = -1;
                Iterator<Integer> itCurrentIndexes = currentIndexByPreviousIndex.values().iterator();
                while(itCurrentIndexes.hasNext() && !isCycleFound) {
                    int iCurrent = itCurrentIndexes.next();
                    if(iCurrent > iCurrentMax) {
                        iCurrentMax = iCurrent;
                    } else {
                        isCycleFound = true;
                    }
                }
                shouldUpdateOrderUsingPk = isCycleFound;
            }

            if(shouldUpdateOrderUsingPk) {
                Iterator<Map.Entry<Object, Object[]>> it = previousAndCurrentByKey.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<Object, Object[]> entry = it.next();
                    Object key = entry.getKey();
                    Object[] previousAndCurrent = entry.getValue();
                    // previousObject may be null, meaning currentObject has been added to the list
                    Object previousObject = previousAndCurrent[0];                    
                    if(previousObject != null) {
                        Object currentObject = previousAndCurrent[1];                    
                        if(!this.isEntireObjectPK) {
                            objectUnchangedDuringUpdate(query, currentObject, previousObject);
                        }
                        int iPrevious = (Integer)previousAndCurrent[2];
                        int iCurrent = (Integer)previousAndCurrent[3];
                        if(iPrevious != iCurrent || shouldRepairOrder) {
                            objectChangedListOrderDuringUpdate(query, key, iCurrent);
                        }
                    }
                }        
            } else {
                // update the objects - but not their order values
                if(!this.isEntireObjectPK) {
                    Iterator<Map.Entry<Object, Object[]>> iterator = previousAndCurrentByKey.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Object[]> entry = iterator.next();
                        Object[] previousAndCurrent = entry.getValue();
                        // previousObject may be null, meaning currentObject has been added to the list
                        Object previousObject = previousAndCurrent[0];
                        if( previousObject != null) {
                            Object currentObject = previousAndCurrent[1];                    
                            objectUnchangedDuringUpdate(query, currentObject, previousObject);
                        }
                    }        
                }
                
                // a bulk update query will be executed for each bunch of adjacent previous indexes from which current indexes could be obtained with a shift, for instance:
                //   previous index  1, 2, 3 
                //   current  index  5, 6, 7
                // the sql will look like:
                //   UPDATE ... SET ListOrderField = ListOrderField + 4 WHERE 1 <= ListOrderField AND ListOrderField <= 3 AND FK = ...  
                int iMin = -1;
                int iMax = -1;
                int iShift = 0;
                // each index corresponds to a bunch of objects to be shifted
                ArrayList<Integer> iMinList = new ArrayList();
                ArrayList<Integer> iMaxList = new ArrayList();
                ArrayList<Integer> iShiftList = new ArrayList();
                Iterator<Map.Entry<Integer, Integer>> itEntries = currentIndexByPreviousIndex.entrySet().iterator();
                while(itEntries.hasNext()) {
                    Map.Entry<Integer, Integer> entry = itEntries.next();
                    int iPrevious = entry.getKey();
                    int iCurrent = entry.getValue();
                    if(iMin >= 0) {
                        // the shift should be the same for all indexes participating in bulk update
                        int iPreviousExpected = iMax + 1;
                        if(iPrevious == iPreviousExpected && iCurrent == iPreviousExpected + iShift) {
                            iMax++;
                        } else {
                            iMinList.add(iMin);
                            iMaxList.add(iMax);
                            iShiftList.add(iShift);
                            iMin = -1;
                        }
                    }
                    if(iMin == -1) {
                        // start defining a new bulk update - define iShift, iFirst, iLast
                        iMin = iPrevious;
                        iMax = iPrevious;
                        iShift = iCurrent - iPrevious;
                    }
                }
                if(iMin >= 0) {
                    iMinList.add(iMin);
                    iMaxList.add(iMax);
                    iShiftList.add(iShift);
                }

                // Order is important - shouldn't override indexes in one bunch while shifting another one.
                // Look for the left-most and right-most bunches and update them first.
                while(!iMinList.isEmpty()) {
                    int iMinLeft = previousList.size() + 1;
                    int iMinRight = -1;
                    int indexShiftLeft = -1;
                    int indexShiftRight = -1;
                    for(int i=0; i < iMinList.size(); i++) {
                        iMin = iMinList.get(i);
                        iShift = iShiftList.get(i);
                        if(iShift < 0) {
                            if(iMin < iMinLeft) {
                                iMinLeft = iMin;
                                indexShiftLeft = i;
                            }
                        } else {
                            // iShift > 0
                            if(iMin > iMinRight) {
                                iMinRight = iMin;
                                indexShiftRight = i;
                            }
                        }
                    }
                    if(indexShiftLeft >= 0) {
                        objectChangedListOrderDuringUpdate(query, iMinList.get(indexShiftLeft), iMaxList.get(indexShiftLeft), iShiftList.get(indexShiftLeft));
                    }
                    if(indexShiftRight >= 0) {
                        objectChangedListOrderDuringUpdate(query, iMinList.get(indexShiftRight), iMaxList.get(indexShiftRight), iShiftList.get(indexShiftRight));
                    }
                    if(indexShiftLeft >= 0) {
                        iMinList.remove(indexShiftLeft);
                        iMaxList.remove(indexShiftLeft);
                        iShiftList.remove(indexShiftLeft);
                    }
                    if(indexShiftRight >= 0) {
                        iMinList.remove(indexShiftRight);
                        iMaxList.remove(indexShiftRight);
                        iShiftList.remove(indexShiftRight);
                    }
                }
            }
        }
        
        // Add the new objects
        Iterator<Map.Entry<Object, Object[]>> iterator = previousAndCurrentByKey.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object[]> entry = iterator.next();
            Object[] previousAndCurrent = entry.getValue();
            // previousObject may be null, meaning currentObject has been added to the list
            Object previousObject = previousAndCurrent[0];
            if (previousObject == null) {
                // there's no previous object - that means that current object should be added.
                // currentObject is not null
                Object currentObject = previousAndCurrent[1];
                // index of currentObject in currentList
                int iCurrent = (Integer)previousAndCurrent[3];
                
                DatabaseRecord extraData = new DatabaseRecord(1);
                extraData.put(this.listOrderField, iCurrent);
                
                objectAddedDuringUpdate(query, currentObject, null, extraData);
            }
        }        
        if (shouldRepairOrder) {
            ((IndirectList)currentList).setIsListOrderBrokenInDb(false);
        }
    }
    
    protected int objectChangedListOrderDuringUpdate(WriteObjectQuery query, int iMin, int iMax, int iShift) {
        DataModifyQuery updateQuery;
        AbstractRecord translationRow = query.getTranslationRow().clone();
        translationRow.put(min, iMin);
        if(iMin == iMax) {
            translationRow.put(this.listOrderField, iMin + iShift);
            updateQuery = updateListOrderFieldQuery;
        } else {
            translationRow.put(max, iMax);
            translationRow.put(shift, iShift);
            updateQuery = bulkUpdateListOrderFieldQuery;
        }
        return (Integer)query.getSession().executeQuery(updateQuery, translationRow);
    }

    protected int objectChangedListOrderDuringUpdate(WriteObjectQuery query, Object key, int newOrderValue) {
        AbstractRecord translationRow = query.getTranslationRow().clone();
        translationRow.put(this.listOrderField, newOrderValue);
        getReferenceDescriptor().getObjectBuilder().writeIntoRowFromPrimaryKeyValues(translationRow, key, query.getSession(), true);
        return (Integer)query.getSession().executeQuery(this.pkUpdateListOrderFieldQuery, translationRow);
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstCollection = getRealCollectionAttributeValueFromObject(firstObject, session);
        Object secondCollection = getRealCollectionAttributeValueFromObject(secondObject, session);
        if(this.listOrderField != null) {
            return this.compareLists((List)firstCollection, (List)secondCollection, session);
        }
        ContainerPolicy containerPolicy = getContainerPolicy();

        if (containerPolicy.sizeFor(firstCollection) != containerPolicy.sizeFor(secondCollection)) {
            return false;
        }

        if (containerPolicy.sizeFor(firstCollection) == 0) {
            return true;
        }
        
        if (isMapKeyMapping()) {
            Object firstIter = containerPolicy.iteratorFor(firstCollection);
            Object secondIter = containerPolicy.iteratorFor(secondCollection);

            Map keyValues = new HashMap();
            while (containerPolicy.hasNext(secondIter)) {
                Map.Entry secondEntry = (Map.Entry)containerPolicy.nextEntry(secondIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondEntry.getValue(), session);
                Object key = secondEntry.getKey();
                keyValues.put(key, primaryKey);
            }    
            while (containerPolicy.hasNext(firstIter)) {
                Map.Entry firstEntry = (Map.Entry)containerPolicy.nextEntry(firstIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstEntry.getValue(), session);
                Object key = firstEntry.getKey();
                if (!primaryKey.equals(keyValues.get(key))) {
                    return false;
                }
            }
        } else { 
            //iterator the first aggregate collection    
            for (Object iterFirst = containerPolicy.iteratorFor(firstCollection);
                     containerPolicy.hasNext(iterFirst);) {
                //fetch the next object from the first iterator.
                Object firstAggregateObject = containerPolicy.next(iterFirst, session);
    
                //iterator the second aggregate collection    
                for (Object iterSecond = containerPolicy.iteratorFor(secondCollection); true;) {
                    //fetch the next object from the second iterator.
                    Object secondAggregateObject = containerPolicy.next(iterSecond, session);
    
                    //matched object found, break to outer FOR loop            
                    if (getReferenceDescriptor().getObjectBuilder().compareObjects(firstAggregateObject, secondAggregateObject, session)) {
                        break;
                    }
    
                    if (!containerPolicy.hasNext(iterSecond)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareLists(List firstList, List secondList, AbstractSession session) {
        if (firstList.size() != secondList.size()) {
            return false;
        }

        int size = firstList.size();
        for(int i=0; i < size; i++) {
            Object firstObject = firstList.get(i);
            Object secondObject = secondList.get(i);
            if (!getReferenceDescriptor().getObjectBuilder().compareObjects(firstObject, secondObject, session)) {
                return false;
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * This method is used to convert the contents of an aggregateCollection into a
     * changeRecord
     * @return org.eclipse.persistence.internal.sessions.AggregateCollectionChangeRecord the changerecord representing this AggregateCollectionMapping
     * @param owner org.eclipse.persistence.internal.sessions.ObjectChangeSet the ChangeSet that uses this record
     * @param cloneCollection Object the collection to convert
     * @param session org.eclipse.persistence.internal.sessions.AbstractSession
     */
    protected ChangeRecord convertToChangeRecord(Object cloneCollection, Object backupCollection, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = getContainerPolicy();
        Object cloneIter = cp.iteratorFor(cloneCollection);
        Vector collectionChanges = new Vector(2);
        while (cp.hasNext(cloneIter)) {
            Object entry = cp.nextEntry(cloneIter, session);
            Object aggregateObject = cp.unwrapIteratorResult(entry);
            // For CR#2258 quietly ignore nulls inserted into a collection.
            if (aggregateObject != null) {
                ObjectChangeSet changes = getReferenceDescriptor(aggregateObject.getClass(), session).getObjectBuilder().compareForChange(aggregateObject, null, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
                changes.setNewKey(cp.keyFromIterator(cloneIter));
                collectionChanges.addElement(changes);
            }
        }

        //cr 3013 Removed if collection is empty return null block, which prevents recording clear() change
        AggregateCollectionChangeRecord changeRecord = new AggregateCollectionChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.setChangedValues(collectionChanges);
        changeRecord.setOriginalCollection(backupCollection);
        
        getContainerPolicy().compareCollectionsForChange(backupCollection, cloneCollection, changeRecord, session, remoteReferenceDescriptor);
        
        return changeRecord;
    }

    /**
     * INTERNAL
     * Called when a DatabaseMapping is used to map the key in a collection.  Returns the key.
     */
    public Object createMapComponentFromRow(AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return valueFromRow(dbRow, null, query, parentCacheKey, query.getExecutionSession(), isTargetProtected, null);
    }
    
    /**
     * To delete all the entries matching the selection criteria from the table stored in the
     * referenced descriptor
     */
    protected void deleteAll(DeleteObjectQuery query, AbstractSession session) throws DatabaseException {
        Object attribute = getAttributeValueFromObject(query.getObject()); 
        if (usesIndirection()) {
           if (!this.indirectionPolicy.objectIsInstantiated(attribute)) {
               // An empty Vector indicates to DeleteAllQuery that no objects should be removed from cache
               ((DeleteAllQuery)this.deleteAllQuery).executeDeleteAll(session.getSessionForClass(this.referenceClass), query.getTranslationRow(), new Vector(0));
               return;
           }
        }
        Object referenceObjects = getRealCollectionAttributeValueFromObject(query.getObject(), session);
        // PERF: Avoid delete if empty.
        if (session.isUnitOfWork() && this.containerPolicy.isEmpty(referenceObjects)) {
            return;
        }
        ((DeleteAllQuery)this.deleteAllQuery).executeDeleteAll(session.getSessionForClass(this.referenceClass), query.getTranslationRow(), this.containerPolicy.vectorFor(referenceObjects, session));
    }

    /**
     * INTERNAL:
     * Execute a descriptor event for the specified event code.
     */
    protected void executeEvent(int eventCode, ObjectLevelModifyQuery query) {
        ClassDescriptor referenceDescriptor = getReferenceDescriptor(query.getObject().getClass(), query.getSession());

        // PERF: Avoid events if no listeners.
        if (referenceDescriptor.getEventManager().hasAnyEventListeners()) {
            referenceDescriptor.getEventManager().executeEvent(new DescriptorEvent(eventCode, query));
        }
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the target row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    @Override
    protected Object extractKeyFromTargetRow(AbstractRecord row, AbstractSession session) {
        int size = this.targetForeignKeyFields.size();
        Object[] key = new Object[size];
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();
        for (int index = 0; index < size; index++) {
            DatabaseField targetField = this.targetForeignKeyFields.get(index);
            DatabaseField sourceField = this.sourceKeyFields.get(index);
            Object value = row.get(targetField);
            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, sourceField.getType());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }
            key[index] = value;
        }
        return new CacheId(key);
    }

    /**
     * INTERNAL:
     * Extract the primary key value from the source row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    @Override
    protected Object extractBatchKeyFromRow(AbstractRecord row, AbstractSession session) {
        int size = this.sourceKeyFields.size();
        Object[] key = new Object[size];
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();
        for (int index = 0; index < size; index++) {
            DatabaseField field = this.sourceKeyFields.get(index);
            Object value = row.get(field);
            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, field.getType());
            } catch (ConversionException exception) {
                throw ConversionException.couldNotBeConverted(this, this.descriptor, exception);
            }
            key[index] = value;
        }        
        return new CacheId(key);
    }
    
    /**
     * INTERNAL:
     * Return the selection criteria used to IN batch fetching.
     */
    @Override
    protected Expression buildBatchCriteria(ExpressionBuilder builder, ObjectLevelReadQuery query) {
        int size = this.targetForeignKeyFields.size();
        if (size > 1) {
            // Support composite keys using nested IN.
            List<Expression> fields = new ArrayList<Expression>(size);
            for (DatabaseField targetForeignKeyField : this.targetForeignKeyFields) {
                fields.add(builder.getField(targetForeignKeyField));
            }
            return query.getSession().getPlatform().buildBatchCriteriaForComplexId(builder, fields);
        } else {
            return query.getSession().getPlatform().buildBatchCriteria(builder, builder.getField(this.targetForeignKeyFields.get(0)));
        }
    }

    /**
     * INTERNAL:
     * Allow the mapping the do any further batch preparation.
     */
    @Override
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        super.postPrepareNestedBatchQuery(batchQuery, query);
        ReadAllQuery aggregateBatchQuery = (ReadAllQuery)batchQuery;
        for (DatabaseField relationField : getTargetForeignKeyFields()) {
            aggregateBatchQuery.getAdditionalFields().add(relationField);
        }
    }

    /**
     * INTERNAL:
     * return the aggregate Record with the primary keys from the source table and target table
     */
    public AbstractRecord getAggregateRow(ObjectLevelModifyQuery query, Object object) {
        Vector referenceObjectKeys = getReferenceObjectKeys(query);
        AbstractRecord aggregateRow = new DatabaseRecord();
        Vector keys = getTargetForeignKeyFields();
        for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++) {
            aggregateRow.put(keys.elementAt(keyIndex), referenceObjectKeys.elementAt(keyIndex));
        }
        getReferenceDescriptor(object.getClass(), query.getSession()).getObjectBuilder().buildRow(aggregateRow, object, query.getSession(), WriteType.UNDEFINED);

        return aggregateRow;
    }

    /**
     * Delete all criteria is created with target foreign keys and source keys.
     * This criteria is then used to delete target records from the table.
     */
    protected Expression getDeleteAllCriteria(AbstractSession session) {
        Expression expression;
        Expression criteria = null;
        Expression builder = new ExpressionBuilder();

        for (Iterator keys = getTargetForeignKeyToSourceKeys().keySet().iterator(); keys.hasNext();) {
            DatabaseField targetForeignKey = (DatabaseField)keys.next();
            DatabaseField sourceKey = getTargetForeignKeyToSourceKeys().get(targetForeignKey);

            expression = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));

            criteria = expression.and(criteria);
        }

        return criteria;
    }

    /**
     * Overrides CollectionMappig because this mapping requires a DeleteAllQuery instead of a ModifyQuery.  
     */
    protected ModifyQuery getDeleteAllQuery() {
        if (deleteAllQuery == null) {
            deleteAllQuery = new DeleteAllQuery();//this is casted to a DeleteAllQuery
        }
        return deleteAllQuery;
    }

    /**
     * INTERNAL:
     * Return the referenceDescriptor. This is a descriptor which is associated with the reference class.
     * NOTE: If you are looking for the descriptor for a specific aggregate object, use
     * #getReferenceDescriptor(Object). This will ensure you get the right descriptor if the object's
     * descriptor is part of an inheritance tree.
     */
    public ClassDescriptor getReferenceDescriptor() {
        if (referenceDescriptor == null) {
            referenceDescriptor = remoteReferenceDescriptor;
        }
        return referenceDescriptor;
    }

    /**
     * INTERNAL:
     * for inheritance purpose
     */
    public ClassDescriptor getReferenceDescriptor(Class theClass, AbstractSession session) {
        if (this.referenceDescriptor.getJavaClass() == theClass) {
            return this.referenceDescriptor;
        } else {
            ClassDescriptor subDescriptor;
            // Since aggregate collection mappings clone their descriptors, for inheritance the correct child clone must be found.
            subDescriptor = this.referenceDescriptor.getInheritancePolicy().getSubclassDescriptor(theClass);
            if (subDescriptor == null) {
                throw DescriptorException.noSubClassMatch(theClass, this);
            } else {
                return subDescriptor;
            }
        }
    }

    /**
     * INTERNAL:
     * get reference object keys
     */
    public Vector getReferenceObjectKeys(ObjectLevelModifyQuery query) throws DatabaseException, OptimisticLockException {
        Vector referenceObjectKeys = new Vector(getSourceKeyFields().size());

        //For CR#2587-S.M.  For nested aggregate collections the source keys can easily be read from the original query.
        AbstractRecord translationRow = query.getTranslationRow();

        for (Enumeration sourcekeys = getSourceKeyFields().elements();
                 sourcekeys.hasMoreElements();) {
            DatabaseField sourceKey = (DatabaseField)sourcekeys.nextElement();

            // CR#2587.  Try first to get the source key from the original query.  If that fails try to get it from the object. 
            Object referenceKey = null;
            if ((translationRow != null) && (translationRow.containsKey(sourceKey))) {
                referenceKey = translationRow.get(sourceKey);
            } else {
                referenceKey = getDescriptor().getObjectBuilder().extractValueFromObjectForField(query.getObject(), sourceKey, query.getSession());
            }
            referenceObjectKeys.addElement(referenceKey);
        }

        return referenceObjectKeys;
    }

    /**
     * PUBLIC:
     * Return the source key field names associated with the mapping.
     * These are in-order with the targetForeignKeyFieldNames.
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
     * Return the source key names associated with the mapping
     */
    public Vector<DatabaseField> getSourceKeyFields() {
        return sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Return the target foregin key field names associated with the mapping.
     * These are in-order with the sourceKeyFieldNames.
     */
    public Vector getTargetForeignKeyFieldNames() {
        Vector fieldNames = new Vector(getTargetForeignKeyFields().size());
        for (Enumeration fieldsEnum = getTargetForeignKeyFields().elements();
                 fieldsEnum.hasMoreElements();) {
            fieldNames.addElement(((DatabaseField)fieldsEnum.nextElement()).getQualifiedName());
        }

        return fieldNames;
    }

    /**
     * INTERNAL:
     * Return the target foregin key fields associated with the mapping
     */
    public Vector<DatabaseField> getTargetForeignKeyFields() {
        return targetForeignKeyFields;
    }

    /**
     * INTERNAL:
     */
    public Map<DatabaseField, DatabaseField> getTargetForeignKeyToSourceKeys() {
        return targetForeignKeyToSourceKeys;
    }

    /**
     * INTERNAL:
     * For aggregate collection mapping the reference descriptor is cloned. The cloned descriptor is then
     * assigned primary keys and table names before initialize. Once cloned descriptor is initialized
     * it is assigned as reference descriptor in the aggregate mapping. This is very specific
     * behavior for aggregate mappings. The original descriptor is used only for creating clones and
     * after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        if (session.hasBroker()) {
            if (getReferenceClass() == null) {
                throw DescriptorException.referenceClassNotSpecified(this);
            }
            // substitute session that owns the mapping for the session that owns reference descriptor.
            session = session.getBroker().getSessionForClass(getReferenceClass());
        }
        
        super.initialize(session);
        if (getDescriptor() != null) { // descriptor will only be null in special case where the mapping has not been added to a descriptor prior to initialization.
            getDescriptor().addMappingsPostCalculateChanges(this); // always equivalent to Private Owned
        }
        if (!getReferenceDescriptor().isAggregateCollectionDescriptor()) {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregateCollection(getReferenceClass().getName(), this));
        }
        if (shouldInitializeSelectionCriteria()) {
            if (isSourceKeySpecified()) {
                initializeTargetForeignKeyToSourceKeys(session);
            } else {
                initializeTargetForeignKeyToSourceKeysWithDefaults(session);
            }

            initializeSelectionCriteria(session);
            getContainerPolicy().addAdditionalFieldsToQuery(getSelectionQuery(), getAdditionalFieldsBaseExpression(getSelectionQuery()));
        }

        // Aggregate 1:m never maintains cache as target objects are aggregates.
        getSelectionQuery().setShouldMaintainCache(false);
        // Add foreign key fields to select, as field values may be required for relationships.
        for (DatabaseField relationField : getTargetForeignKeyFields()) {
            ((ReadAllQuery)getSelectionQuery()).getAdditionalFields().add(relationField);
        }

        initializeDeleteAllQuery(session);
        if (this.listOrderField != null) {
            initializeUpdateListOrderQuery(session, "");
            initializeUpdateListOrderQuery(session, bulk);
            initializeUpdateListOrderQuery(session, pk);
        }

        if (getDescriptor() != null) {
            // Check if any foreign keys reference a secondary table.
            if (getDescriptor().getTables().size() > 1) {
                DatabaseTable firstTable = getDescriptor().getTables().get(0);
                for (DatabaseField field : getSourceKeyFields()) {
                    if (!field.getTable().equals(firstTable)) {
                        getDescriptor().setHasMultipleTableConstraintDependecy(true);
                    }
                }
            }
        }

        // Aggregate collections do not have a cache key when build, so cannot be cached if they have references to isolated classes.
        if ((this.referenceDescriptor != null) && this.referenceDescriptor.hasNoncacheableMappings()) {
            this.isCacheable = false;
        }
    }

    /**
     * Initialize and set the descriptor for the referenced class in this mapping.
     */
    protected void initializeReferenceDescriptor(AbstractSession session) throws DescriptorException {
        super.initializeReferenceDescriptor(session);
        
        HashMap<DatabaseField, DatabaseField> fieldTranslation = null;
        HashMap<DatabaseTable, DatabaseTable> tableTranslation = null;

        ClassDescriptor referenceDescriptor = getReferenceDescriptor();
        ClassDescriptor clonedDescriptor = (ClassDescriptor) referenceDescriptor.clone();
        if (clonedDescriptor.isAggregateDescriptor()) {
            clonedDescriptor.descriptorIsAggregateCollection();
        }
        
        int nAggregateTables = 0;
        if (referenceDescriptor.getTables() != null) {
            nAggregateTables = referenceDescriptor.getTables().size();
        }
        
        if (! aggregateToSourceFields.isEmpty()) {
            DatabaseTable aggregateDefaultTable = null;
            if (nAggregateTables != 0) {
                aggregateDefaultTable = referenceDescriptor.getTables().get(0);
            } else {
                aggregateDefaultTable = new DatabaseTable();
            }
            
            tableTranslation = new HashMap<DatabaseTable, DatabaseTable>();
            fieldTranslation = new HashMap<DatabaseField, DatabaseField>();
            
            for (String aggregateFieldName : aggregateToSourceFields.keySet()) {
                DatabaseField aggregateField = new DatabaseField(aggregateFieldName);
                // 322233 - continue using a string for the Aggregate field name 
                // because the table may or may not have been set. DatabaseFields without a table
                // will match any DatabaseField with a table if the name is the same, breaking
                // legacy support for AggregateCollection inheritance models
                if (! aggregateField.hasTableName()) {
                    aggregateField.setTable(aggregateDefaultTable);
                }
                
                DatabaseField sourceField = aggregateToSourceFields.get(aggregateFieldName);
                if (! sourceField.hasTableName()) {
                    if (defaultSourceTable == null) {
                        // TODO: throw exception: source field doesn't have table
                    } else {
                        sourceField.setTable(defaultSourceTable);
                    }
                }
                
                DatabaseTable sourceTable = sourceField.getTable();
                DatabaseTable savedSourceTable = tableTranslation.get(aggregateField.getTable());
                if (savedSourceTable == null) {
                    tableTranslation.put(aggregateField.getTable(), sourceTable);
                } else {
                    if (! sourceTable.equals(savedSourceTable)) {
                        // TODO: throw exception: aggregate table mapped to two source tables
                    }
                }
                
                sourceField.setIsTranslated(true);
                fieldTranslation.put(aggregateField, sourceField);
            }
         
            // Translate the table and fields now.
            translateTablesAndFields(clonedDescriptor, fieldTranslation, tableTranslation);
        } else {
            if (nAggregateTables == 0) {
                if (defaultSourceTable == null) {
                    // TODO: throw exception
                } else {
                    clonedDescriptor.addTable(defaultSourceTable);
                }
            }
        }
        
        updateNestedAggregateMappings(clonedDescriptor, session);
        
        if (clonedDescriptor.isChildDescriptor()) {
            ClassDescriptor parentDescriptor = session.getDescriptor(clonedDescriptor.getInheritancePolicy().getParentClass());
            initializeParentInheritance(parentDescriptor, clonedDescriptor, session, fieldTranslation, tableTranslation);
        }

        if (clonedDescriptor.isAggregateDescriptor()) {
            clonedDescriptor.descriptorIsAggregateCollection();
        }

        setReferenceDescriptor(clonedDescriptor);
        
        clonedDescriptor.preInitialize(session);
        getContainerPolicy().initialize(session, clonedDescriptor.getDefaultTable());
        if (clonedDescriptor.getPrimaryKeyFields().isEmpty()) {
            this.isEntireObjectPK = true;
            clonedDescriptor.getAdditionalAggregateCollectionKeyFields().addAll(this.getTargetForeignKeyFields());
            if(this.listOrderField != null && !this.isListOrderFieldUpdatable) {
                clonedDescriptor.getAdditionalAggregateCollectionKeyFields().add(this.listOrderField);
            }
        }
        List<DatabaseField> identityFields = getContainerPolicy().getIdentityFieldsForMapKey();
        if (identityFields != null){
            clonedDescriptor.getAdditionalAggregateCollectionKeyFields().addAll(identityFields);
        }
        clonedDescriptor.initialize(session);
        if (clonedDescriptor.hasInheritance() && clonedDescriptor.getInheritancePolicy().hasChildren()) {
            //clone child descriptors
            initializeChildInheritance(clonedDescriptor, session, fieldTranslation, tableTranslation);
        }
    }

    protected void initializeUpdateListOrderQuery(AbstractSession session, String queryType) {
        DataModifyQuery query = new DataModifyQuery();
        if(queryType == pk) {
            this.pkUpdateListOrderFieldQuery = query;
        } else if(queryType == bulk) {
            this.bulkUpdateListOrderFieldQuery = query;
        } else {
            this.updateListOrderFieldQuery = query;
        }

        query.setSessionName(session.getName());
        
        // Build where clause expression.
        Expression whereClause = null;
        Expression builder = new ExpressionBuilder();

        AbstractRecord modifyRow = new DatabaseRecord();

        if(queryType == pk) {
            Iterator<DatabaseField> it = getReferenceDescriptor().getPrimaryKeyFields().iterator();
            while(it.hasNext()) {
                DatabaseField pkField = it.next();
                DatabaseField sourceField = targetForeignKeyToSourceKeys.get(pkField);
                DatabaseField parameterField = sourceField != null ? sourceField : pkField;
                Expression expression = builder.getField(pkField).equal(builder.getParameter(parameterField));
                whereClause = expression.and(whereClause);
            }
            modifyRow.add(this.listOrderField, null);
        } else {
            Iterator<Map.Entry<DatabaseField, DatabaseField>> it = targetForeignKeyToSourceKeys.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<DatabaseField, DatabaseField> entry = it.next();
                Expression expression = builder.getField(entry.getKey()).equal(builder.getParameter(entry.getValue()));
                whereClause = expression.and(whereClause);
            }
            Expression listOrderExpression;
            if(queryType == bulk) {
                listOrderExpression = builder.getField(this.listOrderField).between(builder.getParameter(min), builder.getParameter(max));
                modifyRow.add(this.listOrderField, ExpressionMath.add(builder.getField(this.listOrderField), builder.getParameter(shift)));
            } else {
                listOrderExpression = builder.getField(this.listOrderField).equal(builder.getParameter(min));
                modifyRow.add(this.listOrderField, null);
            }
            whereClause = listOrderExpression.and(whereClause);
        }

        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(getReferenceDescriptor().getDefaultTable());
        statement.setWhereClause(whereClause);
        statement.setModifyRow(modifyRow);
        query.setSQLStatement(statement);
    }
    
    /**
     * INTERNAL:
     * Clone and prepare the JoinedAttributeManager nested JoinedAttributeManager.
     * This is used for nested joining as the JoinedAttributeManager passed to the joined build object.
     */
    public ObjectLevelReadQuery prepareNestedJoins(JoinedAttributeManager joinManager, ObjectBuildingQuery baseQuery, AbstractSession session) {
        ObjectLevelReadQuery nestedQuery = super.prepareNestedJoins(joinManager, baseQuery, session);
        nestedQuery.setShouldMaintainCache(false);
        return nestedQuery;
    }
    
    /**
     * INTERNAL:
     * Called in case fieldTranslation != null
     * Sets new primary keys, tables, appends fieldTranslation to fieldMap so that all fields in mappings, inheritance etc. translated to the new ones.
     */
    protected static void translateTablesAndFields(ClassDescriptor descriptor, HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) {
        int nTables = 0;
        if(descriptor.getTables() != null) {
            nTables = descriptor.getTables().size(); 
        }
        DatabaseTable defaultAggregateTable = null;
        if(nTables == 0) {
            defaultAggregateTable = new DatabaseTable();
            DatabaseTable defaultSourceTable = tableTranslation.get(defaultAggregateTable);
            if(defaultSourceTable == null) {
                //TODO: throw exception
            }
            descriptor.addTable(defaultSourceTable);
        } else {
            defaultAggregateTable = descriptor.getTables().get(0);
            Vector newTables = NonSynchronizedVector.newInstance(nTables);
            for(int i=0; i < nTables; i++) {
                DatabaseTable table = tableTranslation.get(descriptor.getTables().get(i));
                if(table == null) {
                    //TODO: throw exception
                }
                if(!newTables.contains(table)) {
                    newTables.add(table);
                }
            }
            descriptor.setTables(newTables);
        }
        
        int nPrimaryKeyFields = 0;
        if(descriptor.getPrimaryKeyFields() != null) {
            nPrimaryKeyFields = descriptor.getPrimaryKeyFields().size(); 
        }
        if(nPrimaryKeyFields > 0) {
            ArrayList<DatabaseField> newPrimaryKeyFields = new ArrayList(nPrimaryKeyFields);
            for(int i=0; i < nPrimaryKeyFields; i++) {
                DatabaseField pkField = descriptor.getPrimaryKeyFields().get(i);
                if(!pkField.hasTableName() && nTables > 0) {
                    pkField = new DatabaseField(pkField.getName(), defaultAggregateTable);
                }
                DatabaseField field = fieldTranslation.get(pkField);
                if(field == null) {
                    //TODO: throw exception: pk not translated
                }
                newPrimaryKeyFields.add(field);
            }
            descriptor.setPrimaryKeyFields(newPrimaryKeyFields);
        }
        
        // put fieldTranslation into fieldsMap so that all the fields in the mappings, inheritance policy etc
        // are translated to the new ones.
        descriptor.getObjectBuilder().getFieldsMap().putAll(fieldTranslation);
    }
    
    /**
     * INTERNAL:
     * Called in case nestedAggregateToSourceFieldNames != null
     * Updates AggregateObjectMappings and AggregateCollectionMappings of the 
     * reference descriptor.  
     */
    protected void updateNestedAggregateMappings(ClassDescriptor descriptor, AbstractSession session) {
        if (! nestedAggregateToSourceFields.isEmpty()) {
            Iterator<Map.Entry<String, Map<String, DatabaseField>>> it = nestedAggregateToSourceFields.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry<String, Map<String, DatabaseField>> entry = it.next();
                String attribute = entry.getKey();
                String nestedAttribute = null;
                int indexOfDot = attribute.indexOf('.');
                
                // attribute "homes.sellingPonts" is divided into attribute "homes" and nestedAttribute "sellingPoints" 
                if (indexOfDot >= 0) {
                    nestedAttribute = attribute.substring(indexOfDot + 1, attribute.length());
                    attribute = attribute.substring(0, indexOfDot);
                }
                
                DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
                if (mapping == null) {
                    //TODO: may have been already processed by the parent, may be processed later by a child.
                    //Should add method verifyNestedAggregateToSourceFieldNames that would go through
                    //all the children and detect the wrong attribute.
                    continue;
                }
            
                if (mapping.isAggregateCollectionMapping()) {
                    AggregateCollectionMapping nestedAggregateCollectionMapping = (AggregateCollectionMapping)mapping;
                    if (nestedAttribute == null) {
                        nestedAggregateCollectionMapping.addFieldTranslations(entry.getValue());
                    } else {
                        nestedAggregateCollectionMapping.addNestedFieldNameTranslations(nestedAttribute, entry.getValue());
                    }
                } else if (mapping.isAggregateObjectMapping()) {
                    // We have a nested aggregate object mapping (which in turn may have more nested aggregate object mappings). 
                    // However, at this point we have all the field name translations in the nested list. Since we have the clone 
                    // of the first nested aggregate object from the aggregate collection mapping, we will add all the field name 
                    // translations to it since we do not need to look up nested mappings field names. The way nested aggregate 
                    // object mappings handle field name translations will work if we set all the translations on the root of the 
                    // nested objects. This in turn allows sharing nested aggregate objects and allowing different name translations 
                    // for each different chain. Given this aggregate chain "record.location.venue.history" where record is an 
                    // aggregate collection mapping, metadata processing from JPA will (and a direct user may opt to) add all the 
                    // attribute overrides from location, venue and history under separate attribute names, that is,
                    //  - addNestedFieldNameTranslation("location", ..., ...);
                    //  - addNestedFieldNameTranslation("location.venue", ..., ...);
                    //  - addNestedFieldNameTranslation("location.venue.history", ..., ...);
                    // 
                    // This will add all the field name translations to the 'location' aggregate object mapping since we extract
                    // the attribute name as the string up to the first dot.
                    // Simply adding all the nestedFieldNameTranslations to 'location' would work as well.
                    AggregateObjectMapping nestedAggregateObjectMapping = (AggregateObjectMapping) mapping;
                    
                    Map<String, DatabaseField> entries = entry.getValue();
                    for (String aggregateFieldName : entries.keySet()) {
                        DatabaseField sourceField = entries.get(aggregateFieldName);
                        nestedAggregateObjectMapping.addFieldTranslation(sourceField, aggregateFieldName);
                    }
                } else {
                    // TODO: throw exception: mapping corresponding to attribute is not a mapping that accepts field name translations.
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * For aggregate mapping the reference descriptor is cloned. Also the involved inheritance descriptor, its children
     * and parents all need to be cloned. The cloned descriptors are then assigned primary keys and table names before
     * initialize. Once cloned descriptor is initialized it is assigned as reference descriptor in the aggregate mapping.
     * This is very specific behavior for aggregate mappings. The original descriptor is used only for creating clones
     * and after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initializeChildInheritance(ClassDescriptor parentDescriptor, AbstractSession session, 
                HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) throws DescriptorException {
        //recursive call to further children descriptors
        if (parentDescriptor.getInheritancePolicy().hasChildren()) {
            //setFields(clonedChildDescriptor.getFields());        
            List<ClassDescriptor> childDescriptors = parentDescriptor.getInheritancePolicy().getChildDescriptors();
            List<ClassDescriptor> cloneChildDescriptors = new ArrayList(childDescriptors.size());
            for (ClassDescriptor childDescriptor : childDescriptors) {
                ClassDescriptor clonedChildDescriptor = (ClassDescriptor)childDescriptor.clone();
                if (fieldTranslation != null) {
                    translateTablesAndFields(clonedChildDescriptor, fieldTranslation, tableTranslation); 
                }
                
                updateNestedAggregateMappings(clonedChildDescriptor, session);
                
                if (clonedChildDescriptor.isAggregateDescriptor()) {
                    clonedChildDescriptor.descriptorIsAggregateCollection();
                }
                if (!clonedChildDescriptor.isAggregateCollectionDescriptor()) {
                    session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregate(clonedChildDescriptor.getJavaClass().getName(), this));
                }

                clonedChildDescriptor.getInheritancePolicy().setParentDescriptor(parentDescriptor);
                clonedChildDescriptor.preInitialize(session);
                clonedChildDescriptor.initialize(session);
                cloneChildDescriptors.add(clonedChildDescriptor);
                initializeChildInheritance(clonedChildDescriptor, session, fieldTranslation, tableTranslation);
            }
            parentDescriptor.getInheritancePolicy().setChildDescriptors(cloneChildDescriptors);
        }
    }

    /**
     * INTERNAL:
     * Initialize delete all query. This query is used to delete the collection of objects from the
     * target table.
     */
    protected void initializeDeleteAllQuery(AbstractSession session) {
        DeleteAllQuery query = (DeleteAllQuery)getDeleteAllQuery();
        query.setReferenceClass(getReferenceClass());
        query.setDescriptor(getReferenceDescriptor());
        query.setShouldMaintainCache(false);
        query.setIsInMemoryOnly(isCascadeOnDeleteSetOnDatabase());
        if (query.getPartitioningPolicy() == null) {
            query.setPartitioningPolicy(getPartitioningPolicy());
        }
        if (!hasCustomDeleteAllQuery()) {
            if (getSelectionCriteria() == null) {
                query.setSelectionCriteria(getDeleteAllCriteria(session));
            } else {
                query.setSelectionCriteria(getSelectionCriteria());
            }
        }
    }

    /**
     * INTERNAL:
     * For aggregate mapping the reference descriptor is cloned. Also the involved inheritance descriptor, its children
     * and parents all need to be cloned. The cloned descriptors are then assigned primary keys and table names before
     * initialize. Once cloned descriptor is initialized it is assigned as reference descriptor in the aggregate mapping.
     * This is very specific behavior for aggregate mappings. The original descriptor is used only for creating clones
     * and after that mapping never uses it.
     * Some initialization is done in postInitialize to ensure the target descriptor's references are initialized.
     */
    public void initializeParentInheritance(ClassDescriptor parentDescriptor, ClassDescriptor childDescriptor, AbstractSession session, 
                HashMap<DatabaseField, DatabaseField> fieldTranslation, HashMap<DatabaseTable, DatabaseTable> tableTranslation) throws DescriptorException {

        ClassDescriptor clonedParentDescriptor = (ClassDescriptor)parentDescriptor.clone();
        if(clonedParentDescriptor.isAggregateDescriptor()) {
            clonedParentDescriptor.descriptorIsAggregateCollection();
        }
        if (!clonedParentDescriptor.isAggregateCollectionDescriptor()) {
            session.getIntegrityChecker().handleError(DescriptorException.referenceDescriptorIsNotAggregateCollection(parentDescriptor.getJavaClass().getName(), this));
        }
        if (fieldTranslation != null) {
            translateTablesAndFields(clonedParentDescriptor, fieldTranslation, tableTranslation); 
        }
        
        updateNestedAggregateMappings(clonedParentDescriptor, session);
        
        //recursive call to the further parent descriptors
        if (clonedParentDescriptor.getInheritancePolicy().isChildDescriptor()) {
            ClassDescriptor parentToParentDescriptor = session.getDescriptor(clonedParentDescriptor.getJavaClass());
            initializeParentInheritance(parentToParentDescriptor, parentDescriptor, session, fieldTranslation, tableTranslation);
        }

        Vector children = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1);
        children.addElement(childDescriptor);
        clonedParentDescriptor.getInheritancePolicy().setChildDescriptors(children);
        clonedParentDescriptor.preInitialize(session);
        clonedParentDescriptor.initialize(session);
    }

    /**
     * INTERNAL:
     * Selection criteria is created with target foreign keys and source keys.
     * This criteria is then used to read records from the target table.
     */
    protected void initializeSelectionCriteria(AbstractSession session) {
        Expression expression;
        Expression criteria;
        Expression builder = new ExpressionBuilder();

        for (Iterator keys = getTargetForeignKeyToSourceKeys().keySet().iterator(); keys.hasNext();) {
            DatabaseField targetForeignKey = (DatabaseField)keys.next();
            DatabaseField sourceKey = getTargetForeignKeyToSourceKeys().get(targetForeignKey);

            expression = builder.getField(targetForeignKey).equal(builder.getParameter(sourceKey));

            criteria = expression.and(getSelectionCriteria());
            setSelectionCriteria(criteria);
        }
    }

    /**
     * INTERNAL:
     * The foreign keys and the primary key names are converted to DatabaseFields and stored.
     */
    protected void initializeTargetForeignKeyToSourceKeys(AbstractSession session) throws DescriptorException {
        if (getTargetForeignKeyFields().isEmpty()) {
            throw DescriptorException.noTargetForeignKeysSpecified(this);
        }

        for (int index = 0;  index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField foreignKeyfield = getTargetForeignKeyFields().get(index);
            foreignKeyfield = getReferenceDescriptor().buildField(foreignKeyfield);
            getTargetForeignKeyFields().set(index, foreignKeyfield);
        }

        for (int index = 0;  index < getSourceKeyFields().size(); index++) {
            DatabaseField sourceKeyfield = getSourceKeyFields().get(index);
            sourceKeyfield = getDescriptor().buildField(sourceKeyfield);
            if (usesIndirection()) {
                sourceKeyfield.setKeepInRow(true);
            }
            getSourceKeyFields().set(index, sourceKeyfield);
        }

        if (getTargetForeignKeyFields().size() != getSourceKeyFields().size()) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }

        Iterator<DatabaseField> targetForeignKeysEnum = getTargetForeignKeyFields().iterator();
        Iterator<DatabaseField> sourceKeysEnum = getSourceKeyFields().iterator();
        while (targetForeignKeysEnum.hasNext()) {
            getTargetForeignKeyToSourceKeys().put(targetForeignKeysEnum.next(), sourceKeysEnum.next());
        }
    }

    /**
     * INTERNAL:
     * The foreign keys and the primary key names are converted to DatabaseFields and stored. The source keys
     * are not specified by the user so primary keys are extracted from the reference descriptor.
     */
    protected void initializeTargetForeignKeyToSourceKeysWithDefaults(AbstractSession session) throws DescriptorException {
        if (getTargetForeignKeyFields().isEmpty()) {
            throw DescriptorException.noTargetForeignKeysSpecified(this);
        }

        List<DatabaseField> sourceKeys = getDescriptor().getPrimaryKeyFields();
        if (usesIndirection()) {
            for (DatabaseField field : sourceKeys) {
                field.setKeepInRow(true);
            }
        }
        setSourceKeyFields(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(sourceKeys));
        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            DatabaseField foreignKeyfield = getTargetForeignKeyFields().get(index);
            foreignKeyfield = getReferenceDescriptor().buildField(foreignKeyfield);
            getTargetForeignKeyFields().set(index, foreignKeyfield);
        }

        if (getTargetForeignKeyFields().size() != sourceKeys.size()) {
            throw DescriptorException.targetForeignKeysSizeMismatch(this);
        }

        for (int index = 0; index < getTargetForeignKeyFields().size(); index++) {
            getTargetForeignKeyToSourceKeys().put(getTargetForeignKeyFields().get(index), sourceKeys.get(index));
        }
    }

    /**
     * INTERNAL:
     * Iterate on the specified element.
     */
    public void iterateOnElement(DescriptorIterator iterator, Object element) {
        // CR#... Aggregate collections must iterate as aggregates, not regular mappings.
        // For some reason the element can be null, this makes absolutely no sense, but we have a test case for it...
        if (element != null) {
            iterator.iterateForAggregateMapping(element, this, getReferenceDescriptor(element.getClass(), iterator.getSession()));
        }
    }

    /**
     * INTERNAL:
     */
    public boolean isAggregateCollectionMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isElementCollectionMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this mapping support joining.
     */
    public boolean isJoiningSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isOwned(){
        return true;
    }

    /**
     * Checks if source key is specified or not.
     */
    protected boolean isSourceKeySpecified() {
        return !(getSourceKeyFields().isEmpty());
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Because this is a collection mapping, values are added to or removed from the
     * collection based on the changeset
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()){
            if (!this.isCacheable && !targetSession.isProtectedSession()){
                setAttributeValueInObject(target, this.indirectionPolicy.buildIndirectObject(new ValueHolder(null)));
            }
            return;
        }
        //Check to see if the target has an instantiated collection
        if (!isAttributeValueInstantiatedOrChanged(target)) {
            //Then do nothing.
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        AbstractSession session = mergeManager.getSession();
        Object valueOfTarget = null;

        //At this point the source's indirection must be instantiated or the changeSet would never have
        // been created
        Object sourceAggregate = null;

        //On a distributed cache if our changes are for the same version as the target object
        //then load the changes from database.
        // CR 4143  
        // CR 4155 Always replace the collection with the query results as we will not be able to
        // find the originals for merging and indirection information may be lost.
        if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
            ClassDescriptor descriptor = getDescriptor();
            AbstractRecord parentRow = descriptor.getObjectBuilder().extractPrimaryKeyRowFromObject(target, session);
            Object result = getIndirectionPolicy().valueFromQuery(getSelectionQuery(), parentRow, session);//fix for indirection
            setAttributeValueInObject(target, result);
            return;
        }

        // iterate over the changes and merge the collections
        Vector aggregateObjects = ((AggregateCollectionChangeRecord)changeRecord).getChangedValues();
        int size = aggregateObjects.size();
        valueOfTarget = containerPolicy.containerInstance(size);
        // Next iterate over the changes and add them to the container
        ObjectChangeSet objectChanges = null;
        for (int index = 0; index < size; ++index) {
            objectChanges = (ObjectChangeSet)aggregateObjects.elementAt(index);
            Class localClassType = objectChanges.getClassType(session);
            sourceAggregate = objectChanges.getUnitOfWorkClone();

            // cr 4155 Load the target from the UnitOfWork.  This will be the original
            // aggregate object that has the original indirection in it.
            Object targetAggregate = ((UnitOfWorkImpl)mergeManager.getSession()).getCloneToOriginals().get(sourceAggregate);

            if (targetAggregate == null) {
                targetAggregate = getReferenceDescriptor(localClassType, session).getObjectBuilder().buildNewInstance();
            }
            getReferenceDescriptor(localClassType, session).getObjectBuilder().mergeChangesIntoObject(targetAggregate, objectChanges, sourceAggregate, mergeManager, targetSession);
            containerPolicy.addInto(objectChanges.getNewKey(), targetAggregate, valueOfTarget, session);
        }
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (!this.isCacheable && !targetSession.isProtectedSession()){
                setAttributeValueInObject(target, this.indirectionPolicy.buildIndirectObject(new ValueHolder(null)));
            }
            return;
        }
        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!isAttributeValueInstantiatedOrChanged(source))) {
                setAttributeValueInObject(target, getIndirectionPolicy().getOriginalIndirectionObject(getAttributeValueFromObject(source), targetSession));
                return;
            }
        }
        if (!shouldMergeCascadeReference(mergeManager)) {
            // This is only going to happen on mergeClone, and we should not attempt to merge the reference
            return;
        }
        if (mergeManager.shouldRefreshRemoteObject() && shouldMergeCascadeParts(mergeManager) && usesIndirection()) {
            mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }
        if (mergeManager.isForRefresh()) {
            if (!isAttributeValueInstantiatedOrChanged(target)) {
                // This will occur when the clone's value has not been instantiated yet and we do not need
                // the refresh that attribute
                return;
            }
        } else if (!isAttributeValueInstantiatedOrChanged(source)) {
            // I am merging from a clone into an original.  No need to do merge if the attribute was never
            // modified
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        Object valueOfSource = getRealCollectionAttributeValueFromObject(source, mergeManager.getSession());
        Object valueOfTarget = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource));
        for (Object sourceValuesIterator = containerPolicy.iteratorFor(valueOfSource);
                 containerPolicy.hasNext(sourceValuesIterator);) {
            Object wrappedSourceValue = containerPolicy.nextEntry(sourceValuesIterator, mergeManager.getSession());
            Object sourceValue = containerPolicy.unwrapIteratorResult(wrappedSourceValue);

            // For some odd reason support for having null in the collection was added. This does not make sense...
            Object originalValue = null;
            if (sourceValue != null) {
                //CR#2896 - TW
                originalValue = getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().buildNewInstance();
                getReferenceDescriptor(sourceValue.getClass(), mergeManager.getSession()).getObjectBuilder().mergeIntoObject(originalValue, true, sourceValue, mergeManager, targetSession);
                containerPolicy.addInto(containerPolicy.keyFromIterator(sourceValuesIterator), originalValue, valueOfTarget, mergeManager.getSession());
            }
        }

        // Must re-set variable to allow for set method to re-morph changes if the collection is not being stored directly.
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it if private.
     */
    protected void objectAddedDuringUpdate(ObjectLevelModifyQuery query, Object objectAdded, ObjectChangeSet changeSet, Map extraData) throws DatabaseException, OptimisticLockException {
        // Insert must not be done for uow or cascaded queries and we must cascade to cascade policy.
        InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, objectAdded);
        ContainerPolicy.copyMapDataToRow(extraData, insertQuery.getModifyRow());
        if(this.listOrderField != null && extraData != null) {
            insertQuery.getModifyRow().put(this.listOrderField, extraData.get(this.listOrderField));
        }
        query.getSession().executeQuery(insertQuery, insertQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted, Map extraData) throws DatabaseException, OptimisticLockException {
        // Delete must not be done for uow or cascaded queries and we must cascade to cascade policy.
        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        deleteQuery.setIsExecutionClone(true);
        prepareModifyQueryForDelete(query, deleteQuery, objectDeleted, extraData);
        ContainerPolicy.copyMapDataToRow(extraData, deleteQuery.getTranslationRow());
        query.getSession().executeQuery(deleteQuery, deleteQuery.getTranslationRow());
        
        if (containerPolicy.shouldIncludeKeyInDeleteEvent()){
            query.getSession().deleteObject(containerPolicy.keyFromEntry(objectDeleted));
        }
    }

    /**
     * INTERNAL:
     * An object is still in the collection, update it as it may have changed.
     */
    @Override
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object, Map backupCloneKeyedCache, Object cachedKey) throws DatabaseException, OptimisticLockException {
        // Always write for updates, either private or in uow if calling this method.
        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        updateQuery.setIsExecutionClone(true);
        Object backupclone = backupCloneKeyedCache.get(cachedKey);
        updateQuery.setBackupClone(backupclone);
        prepareModifyQueryForUpdate(query, updateQuery, object);
        query.getSession().executeQuery(updateQuery, updateQuery.getTranslationRow());
    }
    
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object, Object backupClone) throws DatabaseException, OptimisticLockException {
        // Always write for updates, either private or in uow if calling this method.
        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        updateQuery.setIsExecutionClone(true);
        updateQuery.setBackupClone(backupClone);
        prepareModifyQueryForUpdate(query, updateQuery, object);
        query.getSession().executeQuery(updateQuery, updateQuery.getTranslationRow());
    }

    /**
     * INTERNAL:
     * For aggregate collection mapping the reference descriptor is cloned. The cloned descriptor is then
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
        
        // Need to set the types on the foreign key fields, as not mapped in the object.
        for (int index = 0; index < getSourceKeyFields().size(); index++) {
            DatabaseField foreignKey = getSourceKeyFields().get(index);
            DatabaseField targetKey = getTargetForeignKeyFields().get(index);
            if (targetKey.getType() == null) {
                targetKey.setType(getDescriptor().getObjectBuilder().getFieldClassification(foreignKey));
            }
        }
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        int index = 0;
        // insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, object);
            ContainerPolicy.copyMapDataToRow(cp.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession()), insertQuery.getModifyRow());
            if(this.listOrderField != null) {
                insertQuery.getModifyRow().add(this.listOrderField, index++);
            }
            query.getSession().executeQuery(insertQuery, insertQuery.getTranslationRow());
            cp.propogatePostInsert(query, wrappedObject);
        }
    }

    /**
     * INTERNAL:
     * Update the privately owned parts
     */
    public void postUpdate(WriteObjectQuery writeQuery) throws DatabaseException, OptimisticLockException {
        if (this.isReadOnly) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiatedOrChanged(writeQuery.getObject())) {
            return;
        }
        // OLD COMMIT - TODO This should not be used.
        compareObjectsAndWrite(writeQuery);
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }
        AbstractSession session = query.getSession();

        // If privately owned parts have their privately own parts, delete those one by one
        // else delete everything in one shot.
        int index = 0;
        if (mustDeleteReferenceObjectsOneByOne()) {
            Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
            ContainerPolicy cp = getContainerPolicy();
            if (this.isCascadeOnDeleteSetOnDatabase && session.isUnitOfWork()) {
                for (Object iterator = cp.iteratorFor(objects); cp.hasNext(iterator);) {
                    Object wrappedObject = cp.nextEntry(iterator, session);
                    Object object = cp.unwrapIteratorResult(wrappedObject);
                    ((UnitOfWorkImpl)session).getCascadeDeleteObjects().add(object);
                }
            }
            for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
                Object wrappedObject = cp.nextEntry(iter, session);
                DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                deleteQuery.setIsExecutionClone(true);
                Map extraData = null;
                if (this.listOrderField != null) {
                    extraData = new DatabaseRecord(1);
                    extraData.put(this.listOrderField, index++);
                }
                prepareModifyQueryForDelete(query, deleteQuery, wrappedObject, extraData);
                session.executeQuery(deleteQuery, deleteQuery.getTranslationRow());
                cp.propogatePreDelete(query, wrappedObject);
            }
            if (!session.isUnitOfWork()) {
                // This deletes any objects on the database, as the collection in memory may has been changed.
                // This is not required for unit of work, as the update would have already deleted these objects,
                // and the backup copy will include the same objects causing double deletes.
                verifyDeleteForUpdate(query);
            }
        } else {
            deleteAll(query, session);
        }
    }

    /**
     * INTERNAL:
     * The message is passed to its reference class descriptor.
     */
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isReadOnly()) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        int index = 0;
        // pre-insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, query.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            InsertObjectQuery insertQuery = getAndPrepareModifyQueryForInsert(query, object);
            ContainerPolicy.copyMapDataToRow(cp.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession()), insertQuery.getModifyRow());
            if(this.listOrderField != null) {
                insertQuery.getModifyRow().add(this.listOrderField, index++);
            }

            // aggregates do not actually use a query to write to the database so the pre-write must be called here
            executeEvent(DescriptorEventManager.PreWriteEvent, insertQuery);
            executeEvent(DescriptorEventManager.PreInsertEvent, insertQuery);
            getReferenceDescriptor(object.getClass(), query.getSession()).getQueryManager().preInsert(insertQuery);
            cp.propogatePreInsert(query, wrappedObject);
        }
    }

    /**
     * INTERNAL:
     * Returns clone of InsertObjectQuery from the reference descriptor, if it is not set - create it.
     */
    protected InsertObjectQuery getInsertObjectQuery(AbstractSession session, ClassDescriptor desc) {
        InsertObjectQuery insertQuery = desc.getQueryManager().getInsertQuery();
        if (insertQuery == null) {
            insertQuery = new InsertObjectQuery();
            desc.getQueryManager().setInsertQuery(insertQuery);
        }
        if (insertQuery.getModifyRow() == null) {
            AbstractRecord modifyRow = new DatabaseRecord();
            for (int i = 0; i < getTargetForeignKeyFields().size(); i++) {
                DatabaseField field = getTargetForeignKeyFields().elementAt(i);
                modifyRow.put(field, null);
            }
            desc.getObjectBuilder().buildTemplateInsertRow(session, modifyRow);
            getContainerPolicy().addFieldsForMapKey(modifyRow);
            if(this.listOrderField != null) {
                modifyRow.put(this.listOrderField, null);
            }
            insertQuery.setModifyRow(modifyRow);
        }
        return insertQuery;
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for post insert/update and pre delete
     */
    public InsertObjectQuery getAndPrepareModifyQueryForInsert(ObjectLevelModifyQuery originalQuery, Object object) {
        AbstractSession session = originalQuery.getSession();
        ClassDescriptor objReferenceDescriptor = getReferenceDescriptor(object.getClass(), session);
        InsertObjectQuery insertQueryFromDescriptor = getInsertObjectQuery(session, objReferenceDescriptor);
        insertQueryFromDescriptor.checkPrepare(session, insertQueryFromDescriptor.getModifyRow());

        InsertObjectQuery insertQuery = (InsertObjectQuery)insertQueryFromDescriptor.clone();
        insertQuery.setObject(object);
        insertQuery.setDescriptor(objReferenceDescriptor);

        AbstractRecord targetForeignKeyRow = new DatabaseRecord();
        Vector referenceObjectKeys = getReferenceObjectKeys(originalQuery);
        for (int keyIndex = 0; keyIndex < getTargetForeignKeyFields().size(); keyIndex++) {
            targetForeignKeyRow.put(getTargetForeignKeyFields().elementAt(keyIndex), referenceObjectKeys.elementAt(keyIndex));
        }

        insertQuery.setModifyRow(targetForeignKeyRow);
        insertQuery.setTranslationRow(targetForeignKeyRow);
        insertQuery.setSession(session);
        insertQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        insertQuery.dontMaintainCache();

        // For bug 2863721 must set a backup clone for compatibility with
        // old event mechanism, even though for AggregateCollections there is no
        // way to get a backup directly from a clone.
        if (session.isUnitOfWork()) {
            Object backupAttributeValue = getReferenceDescriptor(object.getClass(), session).getObjectBuilder().buildNewInstance();
            insertQuery.setBackupClone(backupAttributeValue);
        }
        return insertQuery;
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for pre delete
     */
    public void prepareModifyQueryForDelete(ObjectLevelModifyQuery originalQuery, ObjectLevelModifyQuery modifyQuery, Object wrappedObject, Map extraData) {
        Object object = getContainerPolicy().unwrapIteratorResult(wrappedObject);
        AbstractRecord aggregateRow = getAggregateRow(originalQuery, object);
        ContainerPolicy.copyMapDataToRow(containerPolicy.getKeyMappingDataForWriteQuery(wrappedObject, modifyQuery.getSession()), aggregateRow);
        if(this.listOrderField != null && extraData != null) {
            aggregateRow.put(this.listOrderField, extraData.get(this.listOrderField));
        }
        modifyQuery.setObject(object);
        modifyQuery.setDescriptor(getReferenceDescriptor(object.getClass(), originalQuery.getSession()));
        modifyQuery.setPrimaryKey(getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(aggregateRow, originalQuery.getSession()));
        modifyQuery.setModifyRow(aggregateRow);
        modifyQuery.setTranslationRow(aggregateRow);
        modifyQuery.setSession(originalQuery.getSession());
        if (originalQuery.shouldCascadeOnlyDependentParts()) {
            //This query is the result of being in a UnitOfWork therefor use the Aggregate Collection
            //specific cascade policy to prevent cascading the delete now
            modifyQuery.setCascadePolicy(DatabaseQuery.CascadeAggregateDelete);
        } else {
            modifyQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        }
        modifyQuery.dontMaintainCache();
    }

    /**
     * INTERNAL:
     * setup the modifyQuery for update,
     */
    public void prepareModifyQueryForUpdate(ObjectLevelModifyQuery originalQuery, ObjectLevelModifyQuery modifyQuery, Object object) {
        AbstractRecord aggregateRow = getAggregateRow(originalQuery, object);
        modifyQuery.setObject(object);
        modifyQuery.setDescriptor(getReferenceDescriptor(object.getClass(), originalQuery.getSession()));
        modifyQuery.setPrimaryKey(getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(aggregateRow, originalQuery.getSession()));
        modifyQuery.setTranslationRow(aggregateRow);
        modifyQuery.setSession(originalQuery.getSession());
        modifyQuery.setCascadePolicy(originalQuery.getCascadePolicy());
        modifyQuery.dontMaintainCache();
    }

    /**
     * INTERNAL:
     * Set the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    protected void setReferenceDescriptor(ClassDescriptor aDescriptor) {
        this.referenceDescriptor = aDescriptor;
        this.remoteReferenceDescriptor = this.referenceDescriptor;
    }

    /**
     * PUBLIC:
     * Set the source key field names associated with the mapping.
     * These must be in-order with the targetForeignKeyFieldNames.
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
     * set all the primary key names associated with this mapping
     */
    public void setSourceKeyFields(Vector<DatabaseField> sourceKeyFields) {
        this.sourceKeyFields = sourceKeyFields;
    }

    /**
     * PUBLIC:
     * Set the target foregin key field names associated with the mapping.
     * These must be in-order with the sourceKeyFieldNames.
     */
    public void setTargetForeignKeyFieldNames(Vector fieldNames) {
        Vector fields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(fieldNames.size());
        for (Enumeration fieldNamesEnum = fieldNames.elements(); fieldNamesEnum.hasMoreElements();) {
            fields.addElement(new DatabaseField((String)fieldNamesEnum.nextElement()));
        }

        setTargetForeignKeyFields(fields);
    }

    /**
     * INTERNAL:
     * set the target foregin key fields associated with the mapping
     */
    public void setTargetForeignKeyFields(Vector<DatabaseField> targetForeignKeyFields) {
        this.targetForeignKeyFields = targetForeignKeyFields;
    }

    protected void setTargetForeignKeyToSourceKeys(Map<DatabaseField, DatabaseField> targetForeignKeyToSourceKeys) {
        this.targetForeignKeyToSourceKeys = targetForeignKeyToSourceKeys;
    }

    /**
     * Returns true as any process leading to object modification should also affect its privately owned parts
     * Usually used by write, insert, update and delete.
     */
    protected boolean shouldObjectModifyCascadeToParts(ObjectLevelModifyQuery query) {
        if (isReadOnly()) {
            return false;
        }

        return true;
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps. PLEASE ENSURE that the changes
     * have been made in the object model first.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        AggregateCollectionChangeRecord collectionChangeRecord = (AggregateCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (AggregateCollectionChangeRecord)convertToChangeRecord(cloneCollection, containerPolicy.containerInstance(), changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            collectionChangeRecord.getChangedValues().add(changeSetToAdd);
        }
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.  PLEASE ENSURE that the changes
     * have been made in the object model first.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        AggregateCollectionChangeRecord collectionChangeRecord = (AggregateCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());

        if (collectionChangeRecord == null) {
            //if there is no change for this attribute then create a changeSet for it. no need to modify the resulting
            // change record as it should be built from the clone which has the changes allready
            Object cloneObject = ((UnitOfWorkChangeSet)changeSet.getUOWChangeSet()).getUOWCloneForObjectChangeSet(changeSet);
            Object cloneCollection = this.getRealAttributeValueFromObject(cloneObject, session);
            collectionChangeRecord = (AggregateCollectionChangeRecord)convertToChangeRecord(cloneCollection, containerPolicy.containerInstance(), changeSet, session);
            changeSet.addChange(collectionChangeRecord);
        } else {
            collectionChangeRecord.getChangedValues().remove(changeSetToRemove);
        }
    }

    /**
     * INTERNAL:
     * Checks if object is deleted from the database or not.
     */
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        // Row is built for translation
        if (isReadOnly()) {
            return true;
        }

        AbstractRecord row = getDescriptor().getObjectBuilder().buildRowForTranslation(object, session);
        Object value = session.executeQuery(getSelectionQuery(), row);

        return getContainerPolicy().isEmpty(value);
    }

    /**
     *    Verifying deletes make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found than those are all read and removed one
     * by one taking their privately owned parts into account.
     */
    protected void verifyDeleteForUpdate(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = readPrivateOwnedForObject(query);

        // Delete all these object one by one.
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * INTERNAL:
     * AggregateCollection contents should not be considered for addition to the UnitOfWork
     * private owned objects list for removal.
     */
    public boolean isCandidateForPrivateOwnedRemoval() {
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
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        return false;
    }

    /**
     * INTERNAL:
     * Once a descriptor is serialized to the remote session, all its mappings and reference descriptors are traversed.
     * Usually the mappings are initialized and the serialized reference descriptors are replaced with local descriptors
     * if they already exist in the remote session.
     */
    public void remoteInitialization(DistributedSession session) {
        super.remoteInitialization(session);
        getReferenceDescriptor().remoteInitialization(session);
    }
    
    /**
     * PUBLIC:
     * indicates whether listOrderField value could be updated in the db. Used only if listOrderField!=null 
     */
    public boolean isListOrderFieldUpdatable() {
        return this.isListOrderFieldUpdatable;
    }

   /**
    * PUBLIC:
    * indicates whether listOrderField value could be updated in the db. Used only if listOrderField!=null
    * Default value is true. 
    */
    public void setIsListOrderFieldUpdatable(boolean isUpdatable) {
       this.isListOrderFieldUpdatable = isUpdatable;
    }
    
    /**
     * PUBLIC:
     * Set a default source table to use with the source fields of this mapping.
     */
    public void setDefaultSourceTable(DatabaseTable table) {
        defaultSourceTable = table;
    }
}