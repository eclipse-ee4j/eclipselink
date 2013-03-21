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
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 *     08/23/2010-2.2 Michael O'Brien 
 *        - 323043: application.xml module ordering may cause weaving not to occur causing an NPE.
 *                       warn if expected "_persistence_*_vh" method not found
 *                       instead of throwing NPE during deploy validation.
 *     07/19/2011-2.2.1 Guy Pelletier 
 *       - 338812: ManyToMany mapping in aggregate object violate integrity constraint on deletion
 *     04/09/2012-2.4 Guy Pelletier 
 *       - 374377: OrderBy with ElementCollection doesn't work
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.beans.PropertyChangeListener;

import java.util.*;

import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.internal.descriptors.changetracking.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>: Abstract class for relationship mappings which store collection of objects
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public abstract class CollectionMapping extends ForeignReferenceMapping implements ContainerMapping {

    /** Used for delete all in m-m, dc and delete all optimization in 1-m. */
    protected transient ModifyQuery deleteAllQuery;
    protected transient boolean hasCustomDeleteAllQuery;
    protected ContainerPolicy containerPolicy;
    protected boolean hasOrderBy;

    /** Field holds the order of elements in the list in the db, requires collection of type List, may be not null only in case isListOrderFieldSupported==true */
    protected DatabaseField listOrderField;
    /** Indicates whether the mapping supports listOrderField, if it doesn't attempt to set listOrderField throws exception. */
    protected boolean isListOrderFieldSupported;
    /** Query used when order of list members is changed. Used only if listOrderField!=null */
    protected transient DataModifyQuery changeOrderTargetQuery;
    /** 
     * Specifies what should be done if the list of values read from listOrserField is invalid
     * (there should be no nulls, no duplicates, no "holes").
     **/ 
    protected OrderCorrectionType orderCorrectionType;
    
    /** Store if the mapping can batch delete reference objects. */
    protected Boolean mustDeleteReferenceObjectsOneByOne = null;
    
    /** Flag to indicate if collection needs to be synchronized instead of cloning during merge. */
    protected static boolean isSynchronizeOnMerge = Boolean.getBoolean("eclipselink.synchronizeCollectionOnMerge");
    
    /**
     * PUBLIC:
     * Default constructor.
     */
    public CollectionMapping() {
        this.selectionQuery = new ReadAllQuery();
        this.hasCustomDeleteAllQuery = false;
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
        this.hasOrderBy = false;
        this.isListOrderFieldSupported = false;
    }

    /**
     * PUBLIC:
     * Provide order support for queryKeyName in ascending order
     */
    public void addAscendingOrdering(String queryKeyName) {
        this.hasOrderBy = true;
        if (queryKeyName == null) {
            return;
        }

        ((ReadAllQuery)getSelectionQuery()).addAscendingOrdering(queryKeyName);
    }

    /**
     * PUBLIC:
     * Provide order support for queryKeyName in descending order.
     */
    public void addDescendingOrdering(String queryKeyName) {
        this.hasOrderBy = true;
        if (queryKeyName == null) {
            return;
        }

        ((ReadAllQuery)getSelectionQuery()).addDescendingOrdering(queryKeyName);
    }
    
    /**
     * PUBLIC:
     * Provide order support for queryKeyName in descending or ascending order.
     * Called from the jpa metadata processing of an order by value.
     */
    public void addOrderBy(String queryKeyName, boolean isDescending) {
        if (isDescending) {
            addDescendingOrdering(queryKeyName);
        } else {
            addAscendingOrdering(queryKeyName);
        }
    }
    
    /**
     * PUBLIC:
     * Provide order support for queryKeyName in ascending or descending order.
     * Called from the jpa metadata processing of an order by value. The 
     * aggregate name may be chained through the dot notation.
     */
    public void addAggregateOrderBy(String aggregateName, String queryKeyName, boolean isDescending) {
        this.hasOrderBy = true;
        
        ReadAllQuery readAllQuery = (ReadAllQuery) getSelectionQuery();
        ExpressionBuilder builder = readAllQuery.getExpressionBuilder();
        Expression expression = null;
        
        if (aggregateName.contains(".")) {
            StringTokenizer st = new StringTokenizer(aggregateName, ".");
            while (st.hasMoreTokens()) {
                if (expression == null) {
                    expression = builder.get(st.nextToken());
                } else {
                    expression = expression.get(st.nextToken());
                }
            }
            
            expression = expression.get(queryKeyName);
        } else {
            // Single level aggregate
            if (aggregateName.equals("")) {
                expression = builder.get(queryKeyName);
            } else {
                expression = builder.get(aggregateName).get(queryKeyName);
            }
        }
        
        if (isDescending) {
            readAllQuery.addOrdering(expression.descending());
        } else {
            readAllQuery.addOrdering(expression.ascending());
        }
    }

    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy
     * the vector without re-registering the target objects.
     */
    @Override
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        // Check for null
        if (attributeValue == null) {
            return this.containerPolicy.containerInstance(1);
        } else {
            return this.containerPolicy.cloneFor(attributeValue);
        }
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the objects, use the attribute value.
     */
    @Override
    public Object buildCloneForPartObject(Object attributeValue, Object original, CacheKey cacheKey, Object clone, AbstractSession cloningSession, Integer refreshCascade, boolean isExisting, boolean isFromSharedCache) {
        ContainerPolicy containerPolicy = this.containerPolicy;
        if (attributeValue == null) {
            Object container = containerPolicy.containerInstance(1);
            if (cloningSession.isUnitOfWork() && (this.getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy()) && ((clone != null) && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)) && (container instanceof CollectionChangeTracker)) {
                ((CollectionChangeTracker)container).setTrackedAttributeName(this.getAttributeName());
                ((CollectionChangeTracker)container)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
            }
            return container;
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
            // Clone is used while merging into cache. It can operate directly without synchronize/clone.
            temporaryCollection = attributeValue;
        }
        for (Object valuesIterator = containerPolicy.iteratorFor(temporaryCollection);containerPolicy.hasNext(valuesIterator);){
            containerPolicy.addNextValueFromIteratorInto(valuesIterator, clone, cacheKey, clonedAttributeValue, this, refreshCascade, cloningSession, isExisting, isFromSharedCache);
        }
        if (cloningSession.isUnitOfWork() && (this.getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy()) && ((clone != null) && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)) && (clonedAttributeValue instanceof CollectionChangeTracker)) {
            ((CollectionChangeTracker)clonedAttributeValue).setTrackedAttributeName(this.getAttributeName());
            ((CollectionChangeTracker)clonedAttributeValue)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
        }
        if(temporaryCollection instanceof IndirectList) {
            ((IndirectList)clonedAttributeValue).setIsListOrderBrokenInDb(((IndirectList)temporaryCollection).isListOrderBrokenInDb());
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Performs a first level clone of the attribute.  This generally means on the container will be cloned.
     */
    public Object buildContainerClone(Object attributeValue, AbstractSession cloningSession){
        Object newContainer = this.containerPolicy.containerInstance(this.containerPolicy.sizeFor(attributeValue));
        Object valuesIterator = this.containerPolicy.iteratorFor(attributeValue);
        while (this.containerPolicy.hasNext(valuesIterator)) {
            Object originalValue = this.containerPolicy.next(valuesIterator, cloningSession);
            this.containerPolicy.addInto(originalValue, newContainer, cloningSession);
        }
        return newContainer;
    }
    
   /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    @Override
    public void buildCopy(Object copy, Object original, CopyGroup group) {
        Object attributeValue = getRealCollectionAttributeValueFromObject(original, group.getSession());
        Object valuesIterator = this.containerPolicy.iteratorFor(attributeValue);
        attributeValue = this.containerPolicy.containerInstance(this.containerPolicy.sizeFor(attributeValue));
        while (this.containerPolicy.hasNext(valuesIterator)) {
            Object originalValue = this.containerPolicy.next(valuesIterator, group.getSession());
            Object copyValue = originalValue;
            if (group.shouldCascadeAllParts() || (group.shouldCascadePrivateParts() && isPrivateOwned()) || group.shouldCascadeTree()) {
                copyValue = group.getSession().copyInternal(originalValue, group);
            } else {
                // Check for backrefs to copies.
                copyValue = group.getCopies().get(originalValue);
                if (copyValue == null) {
                    copyValue = originalValue;
                }
            }
            this.containerPolicy.addInto(copyValue, attributeValue, group.getSession());
        }
        // if value holder is used, then the value holder shared with original substituted for a new ValueHolder.
        getIndirectionPolicy().reset(copy);
        setRealAttributeValueInObject(copy, attributeValue);
    }

    /**
     * INTERNAL:
     * Clone the element, if necessary.
     */
    public Object buildElementUnitOfWorkClone(Object element, Object parent, Integer refreshCascade, UnitOfWorkImpl unitOfWork, boolean isExisting, boolean isFromSharedCache) {
        // optimize registration to knowledge of existence
        if (refreshCascade != null ){
            switch(refreshCascade){
            case ObjectBuildingQuery.CascadeAllParts : 
                return unitOfWork.mergeClone(element, MergeManager.CASCADE_ALL_PARTS, true);
            case ObjectBuildingQuery.CascadePrivateParts :
                return unitOfWork.mergeClone(element, MergeManager.CASCADE_PRIVATE_PARTS, true);
            case ObjectBuildingQuery.CascadeByMapping :
                return unitOfWork.mergeClone(element, MergeManager.CASCADE_BY_MAPPING, true);
            default:
                return unitOfWork.mergeClone(element, MergeManager.NO_CASCADE, true);
            }
        }else{
            if (isExisting) {
                return unitOfWork.registerExistingObject(element, isFromSharedCache);
            } else {// not known whether existing or not
                return unitOfWork.registerObject(element);
            }
        }
    }

    /**
     * INTERNAL:
     * Clone the element, if necessary.
     */
    public Object buildElementClone(Object element, Object parent, CacheKey parentCacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache) {
        if (cloningSession.isUnitOfWork()){
            return buildElementUnitOfWorkClone(element, parent, refreshCascade, (UnitOfWorkImpl)cloningSession, isExisting, isFromSharedCache);
        }
        if (referenceDescriptor.getCachePolicy().isProtectedIsolation()){
            return cloningSession.createProtectedInstanceFromCachedData(element, refreshCascade, referenceDescriptor);
        }
        return element;
    }

    /**
     * INTERNAL:
     * This method will access the target relationship and create a list of information to rebuild the relationship.
     * This method is used in combination with the CachedValueHolder to store references to PK's to be loaded
     * from a cache instead of a query.
     */
    @Override
    public Object[] buildReferencesPKList(Object entity, Object attribute, AbstractSession session){
        Object container = indirectionPolicy.getRealAttributeValueFromObject(entity, attribute);
        return containerPolicy.buildReferencesPKList(container, session);
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    @Override
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        if (!this.cascadeRemove) {
            return;
        }
        Object cloneAttribute = getAttributeValueFromObject(object);
        if (cloneAttribute == null) {
            return;
        }
        // PERF: If private owned and not instantiated, then avoid instantiating, delete-all will handle deletion.
        if ((this.isPrivateOwned) && usesIndirection() && (!mustDeleteReferenceObjectsOneByOne())) {
            if (!this.indirectionPolicy.objectIsEasilyInstantiated(cloneAttribute)) {
                return;
            }
        }

        ContainerPolicy cp = this.containerPolicy;
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object wrappedObject = cp.nextEntry(cloneIter, uow);
            Object nextObject = cp.unwrapIteratorResult(wrappedObject);
            if ((nextObject != null) && (!visitedObjects.containsKey(nextObject))) {
                visitedObjects.put(nextObject, nextObject);
                if (this.isCascadeOnDeleteSetOnDatabase && isOneToManyMapping()) {
                    uow.getCascadeDeleteObjects().add(nextObject);
                }
                uow.performRemove(nextObject, visitedObjects);
                cp.cascadePerformRemoveIfRequired(wrappedObject, uow, visitedObjects);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade perform removal of orphaned private owned objects from the UnitOfWorkChangeSet
     */
    @Override
    public void cascadePerformRemovePrivateOwnedObjectFromChangeSetIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // if the object is not instantiated, do not instantiate or cascade
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue != null && this.indirectionPolicy.objectIsInstantiated(attributeValue)) {
            Object realObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
            ContainerPolicy cp = this.containerPolicy;
            for (Object cloneIter = cp.iteratorFor(realObjectCollection); cp.hasNext(cloneIter);) {
                Object nextObject = cp.next(cloneIter, uow);
                if (nextObject != null && !visitedObjects.containsKey(nextObject)) {
                    visitedObjects.put(nextObject, nextObject);
                    // remove the object from the UnitOfWork ChangeSet
                    uow.performRemovePrivateOwnedObjectFromChangeSet(nextObject, visitedObjects);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    @Override
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
        Object cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!this.indirectionPolicy.objectIsInstantiated(cloneAttribute))) {
            if (cloneAttribute instanceof IndirectCollection)  {
                IndirectCollection collection = (IndirectCollection)cloneAttribute;
                if (collection.hasDeferredChanges()) {
                    Iterator iterator = collection.getAddedElements().iterator();
                    boolean cascade = isCascadePersist();
                    while (iterator.hasNext()) {
                        Object nextObject = iterator.next();
                        // remove private owned object from uow list
                        if (isCandidateForPrivateOwnedRemoval()){
                            uow.removePrivateOwnedObject(this, nextObject);
                        }
                        uow.discoverAndPersistUnregisteredNewObjects(nextObject, cascade, newObjects, unregisteredExistingObjects, visitedObjects, cascadeErrors);
                    }
                }
            }
            return;
        }

        ContainerPolicy containerPolicy = this.containerPolicy;
        Object cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object iterator = containerPolicy.iteratorFor(cloneObjectCollection);
        boolean cascade = isCascadePersist();
        while (containerPolicy.hasNext(iterator)) {
            Object wrappedObject = containerPolicy.nextEntry(iterator, uow);
            Object nextObject = containerPolicy.unwrapIteratorResult(wrappedObject);
            // remove private owned object from uow list
            if (isCandidateForPrivateOwnedRemoval()) {
                uow.removePrivateOwnedObject(this, nextObject);
            }
            uow.discoverAndPersistUnregisteredNewObjects(nextObject, cascade, newObjects, unregisteredExistingObjects, visitedObjects, cascadeErrors);
            containerPolicy.cascadeDiscoverAndPersistUnregisteredNewObjects(wrappedObject, newObjects, unregisteredExistingObjects, visitedObjects, uow, cascadeErrors);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    @Override
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        if (!this.cascadePersist) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(object);
        if ((attributeValue == null)
            // Also check if the source is new, then must always cascade.
            || (!this.indirectionPolicy.objectIsInstantiated(attributeValue) && !uow.isCloneNewObject(object))) {
            return;
        }

        ContainerPolicy cp = this.containerPolicy;
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        // add private owned objects to uow list if mapping is a candidate and uow should discover new objects and the source object is new.
        boolean shouldAddPrivateOwnedObject = isCandidateForPrivateOwnedRemoval() && uow.shouldDiscoverNewObjects() && uow.isCloneNewObject(object); 
        while (cp.hasNext(cloneIter)) {
            Object wrappedObject = cp.nextEntry(cloneIter, uow);
            Object nextObject = cp.unwrapIteratorResult(wrappedObject);
            if (shouldAddPrivateOwnedObject && nextObject != null) {
                uow.addPrivateOwnedObject(this, nextObject);
            }
            uow.registerNewObjectForPersist(nextObject, visitedObjects);
            cp.cascadeRegisterNewIfRequired(wrappedObject, uow, visitedObjects);
        }
    }

    /**
     * INTERNAL: 
     * This method is used to store the FK fields that can be cached that correspond to noncacheable mappings
     * the FK field values will be used to re-issue the query when cloning the shared cache entity
     */
    @Override
    public void collectQueryParameters(Set<DatabaseField> record){
        //no-op for mappings that do not support PROTECTED cache isolation
    }

    /**
     * INTERNAL:
     * Used by AttributeLevelChangeTracking to update a changeRecord with calculated changes
     * as apposed to detected changes.  If an attribute can not be change tracked it's
     * changes can be detected through this process.
     */
    @Override
    public void calculateDeferredChanges(ChangeRecord changeRecord, AbstractSession session) {
        CollectionChangeRecord collectionRecord = (CollectionChangeRecord)changeRecord;
        // TODO: Handle events that fired after collection was replaced.
        compareCollectionsForChange(collectionRecord.getOriginalCollection(), collectionRecord.getLatestCollection(), collectionRecord, session);
        
        if(this.isPrivateOwned()) {
            postCalculateChanges(collectionRecord, (UnitOfWorkImpl)session);
        }
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    @Override
    public Object clone() {
        CollectionMapping clone = (CollectionMapping)super.clone();
        clone.setDeleteAllQuery((ModifyQuery)getDeleteAllQuery().clone());
        if (this.listOrderField != null) {
            clone.listOrderField = this.listOrderField.clone();
        }
        if(this.changeOrderTargetQuery != null) {
            clone.changeOrderTargetQuery = (DataModifyQuery)this.changeOrderTargetQuery.clone();
        }
        
        // Clone the container policy.
        clone.containerPolicy = (ContainerPolicy) this.containerPolicy.clone();
        
        return clone;
    }

    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     */
    public void compareCollectionsForChange(Object oldCollection, Object newCollection, ChangeRecord changeRecord, AbstractSession session) {
        this.containerPolicy.compareCollectionsForChange(oldCollection, newCollection, (CollectionChangeRecord) changeRecord, session, getReferenceDescriptor());
    }

    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two collections.
     */
    @Override
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

        Object backUpObjectCollection = null;

        cloneAttribute = getAttributeValueFromObject(clone);

        if ((cloneAttribute != null) && (!this.indirectionPolicy.objectIsInstantiated(cloneAttribute))) {
            return null;
        }

        if (!owner.isNew()) {// if the changeSet is for a new object then we must record all of the attributes
            backUpAttribute = getAttributeValueFromObject(backUp);

            if ((cloneAttribute == null) && (backUpAttribute == null)) {
                return null;
            }

            backUpObjectCollection = getRealCollectionAttributeValueFromObject(backUp, session);
        }

        Object cloneObjectCollection = null;
        if (cloneAttribute != null) {
            cloneObjectCollection = getRealCollectionAttributeValueFromObject(clone, session);
        } else {
            cloneObjectCollection = this.containerPolicy.containerInstance(1);
        }

        CollectionChangeRecord changeRecord = new CollectionChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        compareCollectionsForChange(backUpObjectCollection, cloneObjectCollection, changeRecord, session);
        if (changeRecord.hasChanges()) {
            changeRecord.setOriginalCollection(backUpObjectCollection);
            return changeRecord;
        }
        return null;
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    @Override
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstObjectCollection = getRealCollectionAttributeValueFromObject(firstObject, session);
        Object secondObjectCollection = getRealCollectionAttributeValueFromObject(secondObject, session);

        return super.compareObjects(firstObjectCollection, secondObjectCollection, session);
    }

    /**
     * INTERNAL:
     * Write the changes defined in the change set for the mapping.
     * Mapping added or removed events are raised to allow the mapping to write the changes as required.
     */
    public void writeChanges(ObjectChangeSet changeSet, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        CollectionChangeRecord record = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (record != null) {
            for (ObjectChangeSet removedChangeSet : record.getRemoveObjectList().values()) {
                objectRemovedDuringUpdate(query, this.containerPolicy.getCloneDataFromChangeSet(removedChangeSet), null);
                if (removedChangeSet.getOldKey() != null){
                    this.containerPolicy.propogatePostUpdate(query, removedChangeSet.getOldKey());
                }
            }
            Map extraData = null;
            Object currentObjects = null;
            for (ObjectChangeSet addedChangeSet : record.getAddObjectList().values()) {
                if (this.listOrderField != null) {
                    extraData = new HashMap(1);
                    Integer addedIndexInList = record.getOrderedAddObjectIndices().get(addedChangeSet);
                    if (addedIndexInList == null) {
                        if (currentObjects == null) {
                            currentObjects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
                        }
                        addedIndexInList = ((List)currentObjects).indexOf(addedChangeSet.getUnitOfWorkClone());
                    }
                    extraData.put(this.listOrderField, addedIndexInList);
                }
                objectAddedDuringUpdate(query, this.containerPolicy.getCloneDataFromChangeSet(addedChangeSet), addedChangeSet, extraData);
                if (addedChangeSet.getNewKey() != null){
                    this.containerPolicy.propogatePostUpdate(query, addedChangeSet.getNewKey());
                }
            }
            if (this.listOrderField != null) {
                // This is a hacky check for attribute change tracking, if the backup clone is different, then is using deferred.
                List previousList = (List)getRealCollectionAttributeValueFromObject(query.getBackupClone(), query.getSession());;
                int previousSize = previousList.size();
                if (currentObjects == null) {
                    currentObjects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
                }
                List currentList = (List)currentObjects;
                int currentSize = currentList.size();
                boolean shouldRepairOrder = false;
                if(currentList instanceof IndirectList) {
                    shouldRepairOrder = ((IndirectList)currentList).isListOrderBrokenInDb();
                }
                if(previousList == currentList) {
                    // previousList is not available
                    
                    // The same size as previous list,
                    // at the i-th position holds the index of the i-th original object in the current list (-1 if the object was removed): 
                    // for example: {0, -1, 1, -1, 3} means that:
                    //   previous(0) == current(0);
                    //   previous(1) was removed;
                    //   previous(2) == current(1);
                    //   previous(3) was removed;
                    //   previous(4) == current(3);
                    // current(1) and current(3) were also on previous list, but with different indexes: they are the ones that should have their index changed. 
                    List<Integer> currentIndexes = record.getCurrentIndexesOfOriginalObjects(currentList);
                    for(int i=0; i < currentIndexes.size(); i++) {
                        int currentIndex = currentIndexes.get(i);
                        if((currentIndex >= 0) && (currentIndex != i || shouldRepairOrder)) {
                            objectOrderChangedDuringUpdate(query, currentList.get(currentIndex), currentIndex);
                        }
                    }
                } else {
                    for (int i=0; i < previousSize; i++) {
                        // TODO: should we check for previousObject != null? 
                        Object prevObject = previousList.get(i);
                        Object currentObject = null;
                        if(i < currentSize) {
                            currentObject = currentList.get(i);
                        }
                        if(prevObject != currentObject || shouldRepairOrder) {
                            // object has either been removed or its index in the List has changed
                            int newIndex = currentList.indexOf(prevObject);
                            if(newIndex >= 0) {
                                objectOrderChangedDuringUpdate(query, prevObject, newIndex);
                            }
                        }
                    }
                }
                if (shouldRepairOrder) {
                    ((IndirectList)currentList).setIsListOrderBrokenInDb(false);
                    record.setOrderHasBeenRepaired(true);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * The memory objects are compared and only the changes are written to the database.
     */
    protected void compareObjectsAndWrite(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object currentObjects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        Object previousObjects = readPrivateOwnedForObject(query);
        if (previousObjects == null) {
            previousObjects = getContainerPolicy().containerInstance(1);
        }        
        if (this.listOrderField != null && this.isAggregateCollectionMapping()) {
            compareListsAndWrite((List)previousObjects, (List)currentObjects, query);
            return;
        }
        
        ContainerPolicy cp = this.containerPolicy;

        Map previousObjectsByKey = new HashMap(cp.sizeFor(previousObjects)); // Read from db or from backup in uow.
        Map currentObjectsByKey = new HashMap(cp.sizeFor(currentObjects)); // Current value of object's attribute (clone in uow).

        Map keysOfCurrentObjects = new IdentityHashMap(cp.sizeFor(currentObjects) + 1);

        // First index the current objects by their primary key.
        for (Object currentObjectsIter = cp.iteratorFor(currentObjects);
                 cp.hasNext(currentObjectsIter);) {
            Object currentObject = cp.next(currentObjectsIter, query.getSession());
            try {
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(currentObject, query.getSession());
                currentObjectsByKey.put(primaryKey, currentObject);
                keysOfCurrentObjects.put(currentObject, primaryKey);
            } catch (NullPointerException e) {
                // For CR#2646 quietly discard nulls added to a collection mapping.
                // This try-catch is essentially a null check on currentObject, for
                // ideally the customer should check for these themselves.
                if (currentObject != null) {
                    throw e;
                }
            }
        }

        // Next index the previous objects (read from db or from backup in uow)
        // and process the difference to current (optimized in same loop).
        for (Object previousObjectsIter = cp.iteratorFor(previousObjects);
                 cp.hasNext(previousObjectsIter);) {
            Object wrappedObject = cp.nextEntry(previousObjectsIter, query.getSession());
            Map mapKeyFields = containerPolicy.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession());
            Object previousObject = containerPolicy.unwrapIteratorResult(wrappedObject);
            Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(previousObject, query.getSession());
            previousObjectsByKey.put(primaryKey, previousObject);
            // Delete must occur first, in case object with same pk is removed and added,
            // (technically should not happen, but same applies to unique constraints)
            if (!currentObjectsByKey.containsKey(primaryKey)) {
                objectRemovedDuringUpdate(query, wrappedObject, mapKeyFields);
                cp.propogatePostUpdate(query, wrappedObject);
            }
        }

        for (Object currentObjectsIter = cp.iteratorFor(currentObjects);
                 cp.hasNext(currentObjectsIter);) {
            Object wrappedObject = cp.nextEntry(currentObjectsIter, query.getSession());
            Object currentObject = containerPolicy.unwrapIteratorResult(wrappedObject);
            try {
                Map mapKeyFields = containerPolicy.getKeyMappingDataForWriteQuery(wrappedObject, query.getSession());
                Object primaryKey = keysOfCurrentObjects.get(currentObject);

                if (!(previousObjectsByKey.containsKey(primaryKey))) {
                    objectAddedDuringUpdate(query, currentObject, null, mapKeyFields);
                    cp.propogatePostUpdate(query, wrappedObject);
                } else {
                    objectUnchangedDuringUpdate(query, currentObject, previousObjectsByKey, primaryKey);
                }
            } catch (NullPointerException e) {
                // For CR#2646 skip currentObject if it is null.
                if (currentObject != null) {
                    throw e;
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Old and new lists are compared and only the changes are written to the database.
     * Currently there's no support for listOrderField in CollectionMapping in case there's no change sets,
     * so this method currently never called (currently only overriding method in AggregateCollectionMapping is called).
     * This method should be implemented to support listOrderField functionality without change sets.
     */
    protected void compareListsAndWrite(List previousList, List currentList, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
    }
    
    /**
     * Compare two objects if their parts are not private owned
     */
    @Override
    protected boolean compareObjectsWithoutPrivateOwned(Object firstCollection, Object secondCollection, AbstractSession session) {
        if(this.listOrderField != null) {
            return compareLists((List)firstCollection, (List)secondCollection, session, false);
        }
        
        ContainerPolicy cp = this.containerPolicy;
        if (cp.sizeFor(firstCollection) != cp.sizeFor(secondCollection)) {
            return false;
        }

        Object firstIter = cp.iteratorFor(firstCollection);
        Object secondIter = cp.iteratorFor(secondCollection);

        Map keyValues = new HashMap();

        if (isMapKeyMapping()) {
            while (cp.hasNext(secondIter)) {
                Map.Entry secondObject = (Map.Entry)cp.nextEntry(secondIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject.getValue(), session);
                Object key = secondObject.getKey();
                keyValues.put(key, primaryKey);
            }    
            while (cp.hasNext(firstIter)) {
                Map.Entry firstObject = (Map.Entry)cp.nextEntry(firstIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject.getValue(), session);
                Object key = firstObject.getKey();
                if (!primaryKey.equals(keyValues.get(key))) {
                    return false;
                }
            }
        } else {        
            while (cp.hasNext(secondIter)) {
                Object secondObject = cp.next(secondIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
                keyValues.put(primaryKey, primaryKey);
            }    
            while (cp.hasNext(firstIter)) {
                Object firstObject = cp.next(firstIter, session);
                Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);
                if (!keyValues.containsKey(primaryKey)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Compare two objects if their parts are private owned
     */
    @Override
    protected boolean compareObjectsWithPrivateOwned(Object firstCollection, Object secondCollection, AbstractSession session) {
        if(this.listOrderField != null) {
            return compareLists((List)firstCollection, (List)secondCollection, session, true);
        }

        ContainerPolicy cp = this.containerPolicy;
        if (cp.sizeFor(firstCollection) != cp.sizeFor(secondCollection)) {
            return false;
        }

        Object firstIter = cp.iteratorFor(firstCollection);
        Object secondIter = cp.iteratorFor(secondCollection);

        Map keyValueToObject = new HashMap(cp.sizeFor(firstCollection));

        while (cp.hasNext(secondIter)) {
            Object secondObject = cp.next(secondIter, session);
            Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
            keyValueToObject.put(primaryKey, secondObject);
        }

        while (cp.hasNext(firstIter)) {
            Object firstObject = cp.next(firstIter, session);
            Object primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);
            
            if (keyValueToObject.containsKey(primaryKey)) {
                Object object = keyValueToObject.get(primaryKey);

                if (!session.compareObjects(firstObject, object)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare two lists. For equality the order of the elements should be the same. 
     * Used only if listOrderField != null
     */
    protected boolean compareLists(List firstList, List secondList, AbstractSession session, boolean withPrivateOwned) {
        if (firstList.size() != secondList.size()) {
            return false;
        }

        int size = firstList.size();
        for(int i=0; i < size; i++) {
            Object firstObject = firstList.get(i);
            Object secondObject = secondList.get(i);
            if(withPrivateOwned) {
                if(!session.compareObjects(firstObject, secondObject)) {
                    return false;
                }
            } else {
                Object firstKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);
                Object secondKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
                if (!firstKey.equals(secondKey)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader 
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        containerPolicy.convertClassNamesToClasses(classLoader);
    }
    
    /**
     * INTERNAL:
     * Extract the value from the batch optimized query, this should be supported by most query types.
     */
    @Override
    public Object extractResultFromBatchQuery(ReadQuery batchQuery, CacheKey parentCacheKey, AbstractRecord sourceRow, AbstractSession session, ObjectLevelReadQuery originalQuery) throws QueryException {
        Object result = super.extractResultFromBatchQuery(batchQuery, parentCacheKey, sourceRow, session, originalQuery);
        // The source object might not have any target objects.
        if (result == null) {
            return this.containerPolicy.containerInstance();
        } else {
            return result;
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
        // Execute query and index resulting object sets by key.
        ReadAllQuery batchQuery = (ReadAllQuery)query;
        ComplexQueryResult complexResult = (ComplexQueryResult)session.executeQuery(batchQuery, translationRow);
        Object results = complexResult.getResult();
        Iterator<AbstractRecord> rowsIterator = ((List<AbstractRecord>)complexResult.getData()).iterator();
        ContainerPolicy queryContainerPolicy = batchQuery.getContainerPolicy();
        if (this.containerPolicy.shouldAddAll()) {
            // Indexed list mappings require special add that include the row data with the index.
            Map<Object, List[]> referenceObjectsAndRowsByKey = new HashMap();
            for (Object objectsIterator = queryContainerPolicy.iteratorFor(results); queryContainerPolicy.hasNext(objectsIterator);) {
                Object eachReferenceObject = queryContainerPolicy.next(objectsIterator, session);
                AbstractRecord row = rowsIterator.next();
                Object eachReferenceKey = extractKeyFromTargetRow(row, session);                        
                List[] objectsAndRows = referenceObjectsAndRowsByKey.get(eachReferenceKey);
                if (objectsAndRows == null) {
                    objectsAndRows = new List[]{new ArrayList(), new ArrayList()};
                    referenceObjectsAndRowsByKey.put(eachReferenceKey, objectsAndRows);
                }
                objectsAndRows[0].add(eachReferenceObject);
                objectsAndRows[1].add(row);
            }

            Iterator<Map.Entry<Object, List[]>> iterator = referenceObjectsAndRowsByKey.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Object, List[]> entry = iterator.next();
                Object eachReferenceKey = entry.getKey(); 
                List objects = entry.getValue()[0];
                List<AbstractRecord> rows = entry.getValue()[1];
                Object container = this.containerPolicy.containerInstance(objects.size());
                this.containerPolicy.addAll(objects, container, query.getSession(), rows, batchQuery, parentCacheKey, true);
                referenceObjectsByKey.put(eachReferenceKey, container);
            }
        } else {
            // Non-indexed list, either normal collection, or a map key.
            for (Object objectsIterator = queryContainerPolicy.iteratorFor(results); queryContainerPolicy.hasNext(objectsIterator);) {
                Object eachReferenceObject = queryContainerPolicy.next(objectsIterator, session);
                AbstractRecord row = rowsIterator.next();
                // Handle duplicate rows in the ComplexQueryResult being replaced with null, as a
                // result of duplicate filtering being true for constructing the ComplexQueryResult
                while (row == null && rowsIterator.hasNext()) {
                    row = rowsIterator.next();
                }
                Object eachReferenceKey = extractKeyFromTargetRow(row, session);
                
                Object container = referenceObjectsByKey.get(eachReferenceKey);
                if ((container == null) || (container == Helper.NULL_VALUE)) {
                    container = this.containerPolicy.containerInstance();
                    referenceObjectsByKey.put(eachReferenceKey, container);
                }
                this.containerPolicy.addInto(eachReferenceObject, container, session, row, batchQuery, parentCacheKey, true);
            }
        }
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the target row.
     * Used for batch reading, most following same order and fields as in the mapping.
     * The method should be overridden by classes that support batch reading.
     */
    protected Object extractKeyFromTargetRow(AbstractRecord row, AbstractSession session) {
        throw QueryException.batchReadingNotSupported(this, null);
    }
    
    /**
     * INTERNAL:
     * We are not using a remote valueholder
     * so we need to replace the reference object(s) with
     * the corresponding object(s) from the remote session.
     */
    @Override
    public void fixRealObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        //bug 4147755 getRealAttribute... / setReal
        Object attributeValue = getRealAttributeValueFromObject(object, session);

        // the object collection could be null, check here to avoid NPE
        if (attributeValue == null) {
            setAttributeValueInObject(object, null);
            return;
        }

        ObjectLevelReadQuery tempQuery = query;
        if (!tempQuery.shouldMaintainCache()) {
            if ((!tempQuery.shouldCascadeParts()) || (tempQuery.shouldCascadePrivateParts() && (!isPrivateOwned()))) {
                tempQuery = null;
            }
        }

        Object remoteAttributeValue = session.getObjectsCorrespondingToAll(attributeValue, objectDescriptors, processedObjects, tempQuery, this.containerPolicy);
        setRealAttributeValueInObject(object, remoteAttributeValue);
    }


    /**
     * INTERNAL:
     * Returns the receiver's containerPolicy.
     */
    public ContainerPolicy getContainerPolicy() {
        return containerPolicy;
    }

    protected ModifyQuery getDeleteAllQuery() {
        if (deleteAllQuery == null) {
            deleteAllQuery = new DataModifyQuery();
        }
        return deleteAllQuery;
    }

    /**
     * INTERNAL:
     * Returns the join criteria stored in the mapping selection query. This criteria
     * is used to read reference objects across the tables from the database.
     */
    @Override
    public Expression getJoinCriteria(ObjectExpression context, Expression base) {
        Expression selectionCriteria = getSelectionCriteria();
        Expression keySelectionCriteria = this.containerPolicy.getKeySelectionCriteria();
        if (keySelectionCriteria != null) {
            selectionCriteria = selectionCriteria.and(keySelectionCriteria);
        }
        return context.getBaseExpression().twist(selectionCriteria, base);
    }

    /**
     * INTERNAL:
     * return the object on the client corresponding to the specified object.
     * CollectionMappings have to worry about
     * maintaining object identity.
     */
    @Override
    public Object getObjectCorrespondingTo(Object object, DistributedSession session, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query) {
        return session.getObjectsCorrespondingToAll(object, objectDescriptors, processedObjects, query, this.containerPolicy);
    }

    /**
     * INTERNAL:
     * Return the ordering query keys.
     * Used for Workbench integration.
     */
    public List<Expression> getOrderByQueryKeyExpressions() {
        List<Expression>  expressions = new ArrayList<Expression> ();

        if ((getSelectionQuery() != null) && getSelectionQuery().isReadAllQuery()) {
            for (Expression orderExpression : ((ReadAllQuery)getSelectionQuery()).getOrderByExpressions()) {
                if (orderExpression.isFunctionExpression() && ((FunctionExpression)orderExpression).getBaseExpression().isQueryKeyExpression()) {
                    expressions.add(orderExpression);
                }
            }
        }

        return expressions;
    }

    /**
     * INTERNAL:
     * Get the container policy from the selection query for this mapping. This 
     * method is overridden in DirectCollectionMapping since  its selection 
     * query is a DataReadQuery.
     */
    protected ContainerPolicy getSelectionQueryContainerPolicy() {
        return ((ReadAllQuery) getSelectionQuery()).getContainerPolicy();
    }

    /**
     * Convenience method.
     * Return the value of an attribute, unwrapping value holders if necessary.
     * If the value is null, build a new container.
     */
    @Override
    public Object getRealCollectionAttributeValueFromObject(Object object, AbstractSession session) throws DescriptorException {
        Object value = getRealAttributeValueFromObject(object, session);
        if (value == null) {
            value = this.containerPolicy.containerInstance(1);
        }
        return value;
    }

    /**
     * PUBLIC:
     * Field holds the order of elements in the list in the db, requires collection of type List;
     * may be not null only in case isListOrderFieldSupported==true.
     */
    public DatabaseField getListOrderField() {
        return listOrderField;
    }
    
    /**
     * INTERNAL:
     * Returns list of primary key fields from the reference descriptor.
     */
    public List<DatabaseField> getTargetPrimaryKeyFields() {
        return getReferenceDescriptor().getPrimaryKeyFields();
    }
    
    /**
     * PUBLIC:
     * Specifies what should be done if the list of values read from listOrserField is invalid
     * (there should be no nulls, no duplicates, no "holes").
     */
    public OrderCorrectionType getOrderCorrectionType() {
        return this.orderCorrectionType;
    }
    
    protected boolean hasCustomDeleteAllQuery() {
        return hasCustomDeleteAllQuery;
    }

    /**
     * INTERNAL:
     * Return true if ascending or descending ordering has been set on this 
     * mapping via the @OrderBy annotation.
     */
    public boolean hasOrderBy() {
        return hasOrderBy;
    }
    
    /**
     * INTERNAL:
     * Initialize the state of mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFields(collectFields());
        this.containerPolicy.prepare(getSelectionQuery(), session);

        // Check that the container policy is correct for the collection type.
        if ((!usesIndirection()) && (!getAttributeAccessor().getAttributeClass().isAssignableFrom(this.containerPolicy.getContainerClass()))) {
            throw DescriptorException.incorrectCollectionPolicy(this, getAttributeAccessor().getAttributeClass(), this.containerPolicy.getContainerClass());
        }
        
        if(listOrderField != null) {
            initializeListOrderField(session);
        }
    }

    /**
     * INTERNAL:
     * Initializes listOrderField. 
     * Precondition: listOrderField != null.
     */
    protected void initializeListOrderField(AbstractSession session) {
        if(!List.class.isAssignableFrom(getAttributeAccessor().getAttributeClass())) {
            throw DescriptorException.listOrderFieldRequiersList(getDescriptor(), this);
        }
        
        boolean isAttributeAssignableFromIndirectList = getAttributeAccessor().getAttributeClass().isAssignableFrom(IndirectList.class);
        
        if(this.orderCorrectionType == null) {
            // set default validation mode
            if(isAttributeAssignableFromIndirectList) {
                this.orderCorrectionType = OrderCorrectionType.READ_WRITE;
            } else {
                this.orderCorrectionType = OrderCorrectionType.READ;
            }
        } else if(this.orderCorrectionType == OrderCorrectionType.READ_WRITE) {
            //OrderValidationMode.CORRECTION sets container class to IndirectList, make sure the attribute is of compatible type.
            if(!isAttributeAssignableFromIndirectList) {
                throw DescriptorException.listOrderFieldRequiersIndirectList(getDescriptor(), this);
            }
        }

        ContainerPolicy originalQueryContainerPolicy = getSelectionQueryContainerPolicy();
        
        if(!this.containerPolicy.isOrderedListPolicy()) {
            setContainerPolicy(new OrderedListContainerPolicy(this.containerPolicy.getContainerClass()));
            // re-prepare replaced container policy as we are initializing
            getContainerPolicy().prepare(getSelectionQuery(), session);
        }
        OrderedListContainerPolicy orderedListContainerPolicy = (OrderedListContainerPolicy)this.containerPolicy;  
        orderedListContainerPolicy.setListOrderField(this.listOrderField);
        orderedListContainerPolicy.setOrderCorrectionType(this.orderCorrectionType);

        // If ContainerPolicy's container class is IndirectList, originalQueryContainerPolicy's container class is not (likely Vector)
        // and orderCorrectionType doesn't require query to use IndirectList - then query will keep a separate container policy
        // that uses its original container class (likely Vector) - this is the same optimization as used in useTransparentList method.
        if(this.containerPolicy.getContainerClass().isAssignableFrom(IndirectList.class) && 
           !IndirectList.class.isAssignableFrom(originalQueryContainerPolicy.getContainerClass()) &&
           this.orderCorrectionType != OrderCorrectionType.READ_WRITE ||
           originalQueryContainerPolicy == this.getSelectionQueryContainerPolicy())
        {
            OrderedListContainerPolicy queryOrderedListContainerPolicy; 
            if(originalQueryContainerPolicy.getClass().equals(orderedListContainerPolicy.getClass())) {
                // original query container policy
                queryOrderedListContainerPolicy = (OrderedListContainerPolicy)originalQueryContainerPolicy;  
                queryOrderedListContainerPolicy.setListOrderField(this.listOrderField);
                queryOrderedListContainerPolicy.setOrderCorrectionType(this.orderCorrectionType);
            } else {
                // clone mapping's container policy
                queryOrderedListContainerPolicy = (OrderedListContainerPolicy)orderedListContainerPolicy.clone();
                queryOrderedListContainerPolicy.setContainerClass(originalQueryContainerPolicy.getContainerClass());
                setSelectionQueryContainerPolicy(queryOrderedListContainerPolicy);
            }
        }
        
        if(this.listOrderField.getType() == null) {
            this.listOrderField.setType(Integer.class);
        }
        
        buildListOrderField();
        
        // DirectCollectMap - that uses DataReadQuery - adds listOrderField to selection query in initializeSelectionStatement method.
        if (getSelectionQuery().isReadAllQuery()) {
            if(shouldUseListOrderFieldTableExpression()) {
                initializeListOrderFieldTable(session);
            }
        }
        
        initializeChangeOrderTargetQuery(session);
    }
    
    /**
     * INTERNAL:
     * Initializes listOrderField's table, does nothing by default. 
     * Precondition: listOrderField != null.
     */
    protected void initializeListOrderFieldTable(AbstractSession session) {        
    }
    
    /**
     * INTERNAL:
     * Verifies listOrderField's table, if none found sets the default one. 
     * Precondition: listOrderField != null.
     */
    protected void buildListOrderField() {
        if(this.listOrderField.hasTableName()) {
            if(!this.getReferenceDescriptor().getDefaultTable().equals(this.listOrderField.getTable())) {
                throw DescriptorException.listOrderFieldTableIsWrong(this.getDescriptor(), this, this.listOrderField.getTable(), this.getReferenceDescriptor().getDefaultTable());
            }
        } else {
            this.listOrderField.setTable(this.getReferenceDescriptor().getDefaultTable());
        }
        this.listOrderField = this.getReferenceDescriptor().buildField(this.listOrderField);
    }
    
    
    /**
     * ADVANCED:
     * This method should only be called after this mapping's indirection policy has been set
     * 
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    public Boolean shouldUseLazyInstantiationForIndirectCollection() {
        if (getIndirectionPolicy() == null){
            return null;
        }
        return getIndirectionPolicy().shouldUseLazyInstantiation();
    }
    
    /**
     * INTERNAL:
     * Indicates whether getListOrderFieldExpression method should create field expression based on table expression.  
     */
    public boolean shouldUseListOrderFieldTableExpression() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Initialize changeOrderTargetQuery.
     */
    protected void initializeChangeOrderTargetQuery(AbstractSession session) {
    }
    
    /**
     * INTERNAL:
     * Return whether this mapping is a Collection type.
     */
    @Override
    public boolean isCollectionMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return if this mapping has a mapped key that uses a OneToOne (object).
     */
    public boolean isMapKeyObjectRelationship() {
        return this.containerPolicy.isMapKeyObject();
    }
    
    /**
     * INTERNAL:
     * The referenced object is checked if it is instantiated or not,
     * also check if it has been changed (as indirect collections avoid instantiation on add/remove.
     */
    public boolean isAttributeValueInstantiatedOrChanged(Object object) {
        return this.indirectionPolicy.objectIsInstantiatedOrChanged(getAttributeValueFromObject(object));
    }

    /**
     * INTERNAL:
     * Iterate on the specified element.
     */
    public void iterateOnElement(DescriptorIterator iterator, Object element) {
        iterator.iterateReferenceObjectForMapping(element, this);
    }

    /**
     * INTERNAL:
     * Iterate on the attribute value.
     * The value holder has already been processed.
     */
    @Override
    public void iterateOnRealAttributeValue(DescriptorIterator iterator, Object realAttributeValue) {
        if (realAttributeValue == null) {
            return;
        }
        ContainerPolicy cp = this.containerPolicy;
        for (Object iter = cp.iteratorFor(realAttributeValue); cp.hasNext(iter);) {
            Object wrappedObject = cp.nextEntry(iter, iterator.getSession());
            Object object = cp.unwrapIteratorResult(wrappedObject);
            iterateOnElement(iterator, object);
            cp.iterateOnMapKey(iterator, wrappedObject);
        }
    }

    /**
     * Force instantiation of the load group.
     */
    @Override
    public void load(final Object object, AttributeItem item, final AbstractSession session) {
        instantiateAttribute(object, session);
        if (item.getGroup() != null) {
            Object value = getRealAttributeValueFromObject(object, session);
            ContainerPolicy cp = this.containerPolicy;
            for (Object iterator = cp.iteratorFor(value); cp.hasNext(iterator);) {
                Object wrappedObject = cp.nextEntry(iterator, session);
                Object nestedObject = cp.unwrapIteratorResult(wrappedObject);
                session.load(nestedObject, item.getGroup(nestedObject.getClass()));
            }
        }
    }
    
    /**
     * ADVANCED:
     * Return whether the reference objects must be deleted
     * one by one, as opposed to with a single DELETE statement.
     */
    public boolean mustDeleteReferenceObjectsOneByOne() {
        return this.mustDeleteReferenceObjectsOneByOne == null || this.mustDeleteReferenceObjectsOneByOne;
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Because this is a collection mapping, values are added to or removed from the
     * collection based on the changeset
     */
    @Override
    public void mergeChangesIntoObject(Object target, ChangeRecord chgRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()&& !this.isCacheable && !targetSession.isProtectedSession()){
            setAttributeValueInObject(target, this.indirectionPolicy.buildIndirectObject(new ValueHolder(null)));
            return;
        }
        Object valueOfTarget = null;
        Object valueOfSource = null;
        ContainerPolicy containerPolicy = this.containerPolicy;
        CollectionChangeRecord changeRecord = (CollectionChangeRecord) chgRecord;
        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)changeRecord.getOwner().getUOWChangeSet();

        // Collect the changes into a vector. Check to see if the target has an instantiated 
        // collection, if it does then iterate over the changes and merge the collections.
        if (isAttributeValueInstantiated(target)) {
            // If it is new will need a new collection.
            if (changeRecord.getOwner().isNew()) {
                valueOfTarget = containerPolicy.containerInstance(changeRecord.getAddObjectList().size());
            } else {
                if (isSynchronizeOnMerge) {
                    valueOfTarget = getRealCollectionAttributeValueFromObject(target, mergeManager.getSession());
                } else {
                    // Clone instead of synchronization to avoid possible deadlocks.
                    valueOfTarget = containerPolicy.cloneFor(getRealCollectionAttributeValueFromObject(target, mergeManager.getSession()));
                }
            }

            containerPolicy.mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts(mergeManager), mergeManager, targetSession, isSynchronizeOnMerge);
        } else { 
            // The valueholder has not been instantiated
            if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
                return; // do nothing
            }
            // PERF: Also avoid merge if source has not been instantiated for indirect collection adds.
            if (!isAttributeValueInstantiated(source)) {
                return;
            }
            // If I'm not merging on another server then create instance of the collection
            valueOfSource = getRealCollectionAttributeValueFromObject(source, mergeManager.getSession());
            Object iterator = containerPolicy.iteratorFor(valueOfSource);
            valueOfTarget = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource));
            while (containerPolicy.hasNext(iterator)) {
                // CR#2195 Problem with merging Collection mapping in unit of work and inheritance.
                Object objectToMerge = containerPolicy.next(iterator, mergeManager.getSession());
                if (shouldMergeCascadeParts(mergeManager) && (valueOfSource != null)) {
                    ObjectChangeSet changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(objectToMerge);
                    mergeManager.mergeChanges(objectToMerge, changeSet, targetSession);
                }

                // Let the mergemanager get it because I don't have the change for the object.
                // CR#2188 Problem with merging Collection mapping in unit of work and transparent indirection.
                containerPolicy.addInto(mergeManager.getTargetVersionOfSourceObject(objectToMerge, referenceDescriptor, targetSession), valueOfTarget, mergeManager.getSession());
            }
        }
        if (valueOfTarget == null) {
            valueOfTarget = containerPolicy.containerInstance();
        }
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. This merge is only called when a changeSet for the target
     * does not exist or the target is uninitialized
     */
    @Override
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.descriptor.getCachePolicy().isProtectedIsolation() && !this.isCacheable && !targetSession.isProtectedSession()){
            setAttributeValueInObject(target, this.indirectionPolicy.buildIndirectObject(new ValueHolder(null)));
            return;
        }
        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!isAttributeValueInstantiated(source))) {
                setAttributeValueInObject(target, this.indirectionPolicy.getOriginalIndirectionObject(getAttributeValueFromObject(source), targetSession));
                return;
            }
        }
        if (!shouldMergeCascadeReference(mergeManager)) {
            // This is only going to happen on mergeClone, and we should not attempt to merge the reference
            return;
        }
        if (mergeManager.shouldRefreshRemoteObject() && usesIndirection()) {
            mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }
        if (mergeManager.isForRefresh()) {
            if (!isAttributeValueInstantiated(target)) {
                if(shouldRefreshCascadeParts(mergeManager)){
                    // We must clone and set the value holder from the source to the target.
                    // This ensures any cascaded refresh will be applied to the UOW backup valueholder
                    Object attributeValue = getAttributeValueFromObject(source);
                    Object clonedAttributeValue = this.indirectionPolicy.cloneAttribute(attributeValue, source, null, target, null, mergeManager.getSession(), false); // building clone from an original not a row. 
                    setAttributeValueInObject(target, clonedAttributeValue);
                }
                
                // This will occur when the clone's value has not been instantiated yet and we do not need
                // the refresh that attribute
                return;
            }
        } else if (!isAttributeValueInstantiatedOrChanged(source)) {
            // I am merging from a clone into an original.  No need to do merge if the attribute was never
            // modified
            return;
        }

        Object valueOfSource = getRealCollectionAttributeValueFromObject(source, mergeManager.getSession());

        // There is a very special case when merging into the shared cache that the original
        // has been refreshed and now has non-instantiated indirection objects.
        // Force instantiation is not necessary and can cause problem with JTS drivers.
        AbstractSession mergeSession = mergeManager.getSession();
        Object valueOfTarget = getRealCollectionAttributeValueFromObject(target, mergeSession);
        ContainerPolicy containerPolicy = this.containerPolicy;
        // BUG#5190470 Must force instantiation of indirection collections.
        containerPolicy.sizeFor(valueOfTarget);
        boolean fireChangeEvents = false;
        ObjectChangeListener listener = null;
        Object valueOfSourceCloned = null;
        if (!mergeManager.isForRefresh()) {
            // EL Bug 338504 - No Need to clone in this case.
            valueOfSourceCloned = valueOfSource;
            // if we are copying from original to clone then the source will be     
            // instantiated anyway and we must continue to use the UnitOfWork 
            // valueholder in the case of transparent indirection
            Object newContainer = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSourceCloned));
            if ((this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) && (target instanceof ChangeTracker) && (((ChangeTracker)target)._persistence_getPropertyChangeListener() != null)) {
                // Avoid triggering events if we are dealing with the same list.
                // We rebuild the new container though since any cascade merge
                // activity such as lifecycle methods etc will be captured on
                // newly registered objects and not the clones and we need to
                // make sure the target has these updates once we are done.
                fireChangeEvents = (valueOfSourceCloned != valueOfTarget);
                // Collections may not be indirect list or may have been replaced with user collection.
                Object iterator = containerPolicy.iteratorFor(valueOfTarget);
                listener = (ObjectChangeListener)((ChangeTracker)target)._persistence_getPropertyChangeListener();
                if (fireChangeEvents) {
                    // Objects removed from the first position in the list, so the index of the removed object is always 0. 
                    // When event is processed the index is used only in listOrderField case, ignored otherwise.  
                    Integer zero = Integer.valueOf(0);
                    while (containerPolicy.hasNext(iterator)) {
                        CollectionChangeEvent event = containerPolicy.createChangeEvent(target, getAttributeName(), valueOfTarget, containerPolicy.next(iterator, mergeSession), CollectionChangeEvent.REMOVE, zero, false);
                        listener.internalPropertyChange(event);
                    }                        
                }
                if (newContainer instanceof ChangeTracker) {
                    ((CollectionChangeTracker)newContainer).setTrackedAttributeName(getAttributeName());
                    ((CollectionChangeTracker)newContainer)._persistence_setPropertyChangeListener(listener);
                }
                if (valueOfTarget instanceof ChangeTracker) {
                    ((ChangeTracker)valueOfTarget)._persistence_setPropertyChangeListener(null);//remove listener 
                }
            }
            valueOfTarget = newContainer;
        } else {
            if (isSynchronizeOnMerge) {
                // EL Bug 338504 - It needs to iterate on object which can possibly 
                // cause a deadlock scenario while merging changes from original
                // to the working copy during rollback of the transaction. So, clone 
                // the original object instead of synchronizing on it and use cloned
                // object to iterate and merge changes to the working copy.
                synchronized(valueOfSource) {
                    valueOfSourceCloned = containerPolicy.cloneFor(valueOfSource);
                }
            } else {
                valueOfSourceCloned = valueOfSource;
            }
            //bug 3953038 - set a new collection in the object until merge completes, this
            //              prevents rel-maint. from adding duplicates.
            setRealAttributeValueInObject(target, containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSourceCloned)));
            containerPolicy.clear(valueOfTarget);
        }

        Object sourceIterator = containerPolicy.iteratorFor(valueOfSourceCloned);
        // Index of the added object - objects are added to the end of the list. 
        // When event is processed the index is used only in listOrderField case, ignored otherwise.  
        int i = 0;
        while (containerPolicy.hasNext(sourceIterator)) {
            Object wrappedObject = containerPolicy.nextEntry(sourceIterator, mergeManager.getSession());
            Object object = containerPolicy.unwrapIteratorResult(wrappedObject);
            if (object == null) {
                continue;// skip the null
            }
            if (shouldMergeCascadeParts(mergeManager)) {
                Object mergedObject = null;
                if ((mergeManager.getSession().isUnitOfWork()) && (((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet() != null)) {
                    // If it is a unit of work, we have to check if I have a change Set for this object
                    mergedObject = mergeManager.mergeChanges(mergeManager.getObjectToMerge(object, referenceDescriptor, targetSession), (ObjectChangeSet)((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet().getObjectChangeSetForClone(object), targetSession);
                    if (listener != null && !fireChangeEvents && mergedObject != object){
                        // we are merging a collection into itself that contained detached or new Entities.  make sure to remove the
                        // old change records // bug 302293
                        this.descriptor.getObjectChangePolicy().updateListenerForSelfMerge(listener, this, object, mergedObject, (UnitOfWorkImpl) mergeManager.getSession());
                    }
                } else {
                    mergedObject = mergeManager.mergeChanges(mergeManager.getObjectToMerge(object, referenceDescriptor, targetSession), null, targetSession);
                }
            }
            wrappedObject = containerPolicy.createWrappedObjectFromExistingWrappedObject(wrappedObject, source, referenceDescriptor, mergeManager, targetSession);
            if (isSynchronizeOnMerge) {
                synchronized (valueOfTarget) {
                    if (fireChangeEvents) {
                        //Collections may not be indirect list or may have been replaced with user collection.
                        //bug 304251: let the ContainerPolicy decide what changeevent object to create
                        CollectionChangeEvent event = containerPolicy.createChangeEvent(target, getAttributeName(), valueOfTarget, wrappedObject, CollectionChangeEvent.ADD, i++, false);
                        listener.internalPropertyChange(event);
                    }
                    containerPolicy.addInto(wrappedObject, valueOfTarget, mergeManager.getSession());
                }
            } else {
                if (fireChangeEvents) {
                    //Collections may not be indirect list or may have been replaced with user collection.
                    //bug 304251: let the ContainerPolicy decide what changeevent object to create
                    CollectionChangeEvent event = containerPolicy.createChangeEvent(target, getAttributeName(), valueOfTarget, wrappedObject, CollectionChangeEvent.ADD, i++, false);
                    listener.internalPropertyChange(event);
                }
                containerPolicy.addInto(wrappedObject, valueOfTarget, mergeManager.getSession());
            }
        }
        if (fireChangeEvents && (this.descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy())) {
            // check that there were changes, if not then remove the record.
            ObjectChangeSet changeSet = ((AttributeChangeListener)((ChangeTracker)target)._persistence_getPropertyChangeListener()).getObjectChangeSet();
            //Bug4910642  Add NullPointer check
            if (changeSet != null) {
                CollectionChangeRecord changeRecord = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(getAttributeName());
                if (changeRecord != null) {
                    if (!changeRecord.isDeferred()) {
                        if (!changeRecord.hasChanges()) {
                            changeSet.removeChange(getAttributeName());
                        }
                    } else {
                        // Must reset the latest collection.
                        changeRecord.setLatestCollection(valueOfTarget);
                    }
                }
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
        if (!shouldObjectModifyCascadeToParts(query)) {// Called always for M-M
            return;
        }

        // Only cascade dependents writes in uow.
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        // Insert must not be done for uow or cascaded queries and we must cascade to cascade policy.
        // We should distinguish between insert and write (optimization/paraniod).
        if (isPrivateOwned()) {
            InsertObjectQuery insertQuery = new InsertObjectQuery();
            insertQuery.setIsExecutionClone(true);
            insertQuery.setObject(containerPolicy.unwrapIteratorResult(objectAdded));
            insertQuery.setCascadePolicy(query.getCascadePolicy());
            query.getSession().executeQuery(insertQuery);
        } else {
            // Always write for updates, either private or in uow if calling this method.
            UnitOfWorkChangeSet uowChangeSet = null;
            if ((changeSet == null) && query.getSession().isUnitOfWork() && (((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet() != null)) {
                uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
                changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(query.getObject());
            }
            WriteObjectQuery writeQuery = new WriteObjectQuery();
            writeQuery.setIsExecutionClone(true);
            writeQuery.setObject(containerPolicy.unwrapIteratorResult(objectAdded));
            writeQuery.setObjectChangeSet(changeSet);
            writeQuery.setCascadePolicy(query.getCascadePolicy());
            query.getSession().executeQuery(writeQuery);
        }
    }

    protected void objectOrderChangedDuringUpdate(WriteObjectQuery query, Object orderChangedObject, int orderIndex) {
        prepareTranslationRow(query.getTranslationRow(), query.getObject(), query.getDescriptor(), query.getSession());
        AbstractRecord databaseRow = new DatabaseRecord();

        // Extract target field and its value. Construct insert statement and execute it
        List<DatabaseField> targetPrimaryKeyFields = getTargetPrimaryKeyFields();
        int size = targetPrimaryKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetPrimaryKey = targetPrimaryKeyFields.get(index);
            Object targetKeyValue = getReferenceDescriptor().getObjectBuilder().extractValueFromObjectForField(orderChangedObject, targetPrimaryKey, query.getSession());
            databaseRow.put(targetPrimaryKey, targetKeyValue);
        }
        databaseRow.put(listOrderField, orderIndex);
  
        query.getSession().executeQuery(changeOrderTargetQuery, databaseRow);
    }
    
    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted, Map extraData) throws DatabaseException, OptimisticLockException {
        if (isPrivateOwned()) {// Must check ownership for uow and cascading.
            if (!query.shouldCascadeOnlyDependentParts()) {
                containerPolicy.deleteWrappedObject(objectDeleted, query.getSession());
            }
        }
    }

    /**
     * INTERNAL:
     * An object is still in the collection, update it as it may have changed.
     */
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {// Called always for M-M
            return;
        }

        // Only cascade dependents writes in uow.
        if (query.shouldCascadeOnlyDependentParts()) {
            return;
        }

        // Always write for updates, either private or in uow if calling this method.
        WriteObjectQuery writeQuery = new WriteObjectQuery();
        writeQuery.setIsExecutionClone(true);
        writeQuery.setObject(object);
        writeQuery.setCascadePolicy(query.getCascadePolicy());
        query.getSession().executeQuery(writeQuery);
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void postCalculateChanges(org.eclipse.persistence.sessions.changesets.ChangeRecord changeRecord, UnitOfWorkImpl uow) {
        // no need for private owned check.  This code is only registered for private owned mappings.
        // targets are added to and/or removed to/from the source.
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeRecord;
        Iterator it = collectionChangeRecord.getRemoveObjectList().values().iterator();
        while(it.hasNext()) {
            ObjectChangeSet ocs = (ObjectChangeSet)it.next();
            containerPolicy.postCalculateChanges(ocs, referenceDescriptor, this, uow);
        }
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void recordPrivateOwnedRemovals(Object object, UnitOfWorkImpl uow) {
        // no need for private owned check.  This code is only registered for private owned mappings.
        // targets are added to and/or removed to/from the source.
        if (mustDeleteReferenceObjectsOneByOne()) {
            Iterator it = (Iterator) containerPolicy.iteratorFor(getRealAttributeValueFromObject(object, uow));
            while (it.hasNext()) {
                Object clone = it.next();
                containerPolicy.recordPrivateOwnedRemovals(clone, referenceDescriptor, uow);
            }
        }
    }

    /**
     * INTERNAL:
     * Add additional fields
     */
    @Override
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        super.postPrepareNestedBatchQuery(batchQuery, query);
        ReadAllQuery mappingBatchQuery = (ReadAllQuery)batchQuery;
        mappingBatchQuery.setShouldIncludeData(true);
        this.containerPolicy.addAdditionalFieldsToQuery(mappingBatchQuery, getAdditionalFieldsBaseExpression(mappingBatchQuery));
    }
    
    /**
     * INTERNAL:
     * Return the base expression to use for adding fields to the query.
     * Normally this is the query's builder, but may be the join table for m-m.
     */
    protected Expression getAdditionalFieldsBaseExpression(ReadQuery query) {
        return ((ReadAllQuery)query).getExpressionBuilder();
    }
    
    /**
     * INTERNAL:
     * copies the non primary key information into the row currently used only in ManyToMany
     */
    protected void prepareTranslationRow(AbstractRecord translationRow, Object object, ClassDescriptor descriptor, AbstractSession session) {
        //Do nothing for the generic Collection Mapping
    }

    /**
     * INTERNAL:
     * A subclass should implement this method if it wants different behavior.
     * Recurse thru the parts to delete the reference objects after the actual object is deleted.
     */
    @Override
    public void postDelete(DeleteObjectQuery query) throws DatabaseException {
        if (this.containerPolicy.propagatesEventsToCollection()){
            Object queryObject = query.getObject();
            Object values = getAttributeValueFromObject(queryObject);
            Object iterator = containerPolicy.iteratorFor(values);
            while (containerPolicy.hasNext(iterator)){
                Object wrappedObject = containerPolicy.nextEntry(iterator, query.getSession());
                containerPolicy.propogatePostDelete(query, wrappedObject);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Ensure the container policy is post initialized
     */
    @Override
    public void postInitialize(AbstractSession session) {
        super.postInitialize(session);
        this.containerPolicy.postInitialize(session);
        if (this.referenceDescriptor != null && this.mustDeleteReferenceObjectsOneByOne == null) {
            this.mustDeleteReferenceObjectsOneByOne = this.referenceDescriptor.hasDependencyOnParts()
                    || this.referenceDescriptor.usesOptimisticLocking()
                    || (this.referenceDescriptor.hasInheritance() && this.referenceDescriptor.getInheritancePolicy().shouldReadSubclasses())
                    || this.referenceDescriptor.hasMultipleTables() || this.containerPolicy.propagatesEventsToCollection()
                    || this.referenceDescriptor.hasRelationshipsExceptBackpointer(descriptor);
        } else if (this.mustDeleteReferenceObjectsOneByOne == null) {
            this.mustDeleteReferenceObjectsOneByOne = false;
        }
    }
    
    /**
     * INTERNAL:
     * A subclass should implement this method if it wants different behavior.
     * Recurse thru the parts to delete the reference objects after the actual object is deleted.
     */
    @Override
    public void postInsert(WriteObjectQuery query) throws DatabaseException {
        if (this.containerPolicy.propagatesEventsToCollection()){
            Object queryObject = query.getObject();
            Object values = getAttributeValueFromObject(queryObject);
            Object iterator = containerPolicy.iteratorFor(values);
            while (containerPolicy.hasNext(iterator)){
                Object wrappedObject = containerPolicy.nextEntry(iterator, query.getSession());
                containerPolicy.propogatePostInsert(query, wrappedObject);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Propagate preInsert event to container policy if necessary
     */
    @Override
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (this.containerPolicy.propagatesEventsToCollection()){
            Object queryObject = query.getObject();
            Object values = getAttributeValueFromObject(queryObject);
            Object iterator = containerPolicy.iteratorFor(values);
            while (containerPolicy.hasNext(iterator)){
                Object wrappedObject = containerPolicy.nextEntry(iterator, query.getSession());
                containerPolicy.propogatePreInsert(query, wrappedObject);
            }
        }
    }
    

    /**
     * INTERNAL:
     * Propagate preUpdate event to container policy if necessary
     */
    @Override
    public void preUpdate(WriteObjectQuery query) throws DatabaseException {
        if (this.containerPolicy.propagatesEventsToCollection()){
            Object queryObject = query.getObject();
            Object values = getAttributeValueFromObject(queryObject);
            Object iterator = containerPolicy.iteratorFor(values);
            while (containerPolicy.hasNext(iterator)){
                Object wrappedObject = containerPolicy.nextEntry(iterator, query.getSession());
                containerPolicy.propogatePreUpdate(query, wrappedObject);
            }
        }
    }
    
    /**
     * INTERNAL:
     * An object is still in the collection, update it as it may have changed.
     */
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object, Map backupclones, Object key) throws DatabaseException, OptimisticLockException {
        objectUnchangedDuringUpdate(query, object);
    }

    /**
     * INTERNAL:
     * All the privately owned parts are read
     */
    protected Object readPrivateOwnedForObject(ObjectLevelModifyQuery modifyQuery) throws DatabaseException {
        if (modifyQuery.getSession().isUnitOfWork()) {
            return getRealCollectionAttributeValueFromObject(modifyQuery.getBackupClone(), modifyQuery.getSession());
        } else {
            // cr 3819
            prepareTranslationRow(modifyQuery.getTranslationRow(), modifyQuery.getObject(), modifyQuery.getDescriptor(), modifyQuery.getSession());
            return modifyQuery.getSession().executeQuery(getSelectionQuery(), modifyQuery.getTranslationRow());
        }
    }

    /**
     * INTERNAL:
     * replace the value holders in the specified reference object(s)
     */
    @Override
    public Map replaceValueHoldersIn(Object object, RemoteSessionController controller) {
        return controller.replaceValueHoldersInAll(object, this.containerPolicy);
    }

    /**
     * ADVANCED:
     * Configure the mapping to use a container policy.
     * The policy manages the access to the collection.
     */
    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
        ((ReadAllQuery)getSelectionQuery()).setContainerPolicy(containerPolicy);
    }

    /**
     * PUBLIC:
     * The default delete all query for mapping can be overridden by specifying the new query.
     * This query is responsible for doing the deletion required by the mapping,
     * such as deletion of all the rows from join table for M-M, or optimized delete all of target objects for 1-M.
     */
    public void setCustomDeleteAllQuery(ModifyQuery query) {
        setDeleteAllQuery(query);
        setHasCustomDeleteAllQuery(true);
    }

    protected void setDeleteAllQuery(ModifyQuery query) {
        deleteAllQuery = query;
    }

    /**
     * PUBLIC:
     * Set the receiver's delete all SQL string. This allows the user to override the SQL
     * generated by TopLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row, through replacing the field names
     * marked by '#' with the values for those fields.
     * This SQL is responsible for doing the deletion required by the mapping,
     * such as deletion of all the rows from join table for M-M, or optimized delete all of target objects for 1-M.
     * Example, 'delete from PROJ_EMP where EMP_ID = #EMP_ID'.
     */
    public void setDeleteAllSQLString(String sqlString) {
        DataModifyQuery query = new DataModifyQuery();
        query.setSQLString(sqlString);
        setCustomDeleteAllQuery(query);
    }

    /**
     * PUBLIC:
     * Set the receiver's delete all call. This allows the user to override the SQL
     * generated by TopLink, with there own SQL or procedure call. The arguments are
     * translated from the fields of the source row.
     * This call is responsible for doing the deletion required by the mapping,
     * such as deletion of all the rows from join table for M-M, or optimized delete all of target objects for 1-M.
     * Example, 'new SQLCall("delete from PROJ_EMP where EMP_ID = #EMP_ID")'.
     */
    public void setDeleteAllCall(Call call) {
        DataModifyQuery query = new DataModifyQuery();
        query.setCall(call);
        setCustomDeleteAllQuery(query);
    }

    protected void setHasCustomDeleteAllQuery(boolean bool) {
        hasCustomDeleteAllQuery = bool;
    }
    
    /**
     * INTERNAL:
     * Set the container policy on the selection query for this mapping. This 
     * method is overridden in DirectCollectionMapping since  its selection 
     * query is a DataReadQuery.
     */
    protected void setSelectionQueryContainerPolicy(ContainerPolicy containerPolicy) {
        ((ReadAllQuery) getSelectionQuery()).setContainerPolicy(containerPolicy);
    }

    /**
     * PUBLIC:
     * Set the name of the session to execute the mapping's queries under.
     * This can be used by the session broker to override the default session
     * to be used for the target class.
     */
    public void setSessionName(String name) {
        getDeleteAllQuery().setSessionName(name);
        getSelectionQuery().setSessionName(name);
    }
    /**
     * ADVANCED:
     * Calling this method will only affect behavior of mappings using transparent indirection
     * This method should only be called after this mapping's indirection policy has been set
     * 
     * IndirectList and IndirectSet can be configured not to instantiate the list from the
     * database when you add and remove from them.  IndirectList defaults to this behavior. When
     * Set to true, the collection associated with this TransparentIndirection will be setup so as
     * not to instantiate for adds and removes.  The weakness of this setting for an IndirectSet is
     * that when the set is not instantiated, if a duplicate element is added, it will not be
     * detected until commit time.
     */
    public void setUseLazyInstantiationForIndirectCollection(Boolean useLazyInstantiation) {
        if (getIndirectionPolicy() != null){
            getIndirectionPolicy().setUseLazyInstantiation(useLazyInstantiation);
        }
    }
    
    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            collectionChangeRecord = new CollectionChangeRecord(changeSet);
            collectionChangeRecord.setAttribute(getAttributeName());
            collectionChangeRecord.setMapping(this);
            changeSet.addChange(collectionChangeRecord);
        }
        this.containerPolicy.recordAddToCollectionInChangeRecord((ObjectChangeSet)changeSetToAdd, collectionChangeRecord);
        if (referenceKey != null) {
            ((ObjectChangeSet)changeSetToAdd).setNewKey(referenceKey);
        }
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            collectionChangeRecord = new CollectionChangeRecord(changeSet);
            collectionChangeRecord.setAttribute(getAttributeName());
            collectionChangeRecord.setMapping(this);
            changeSet.addChange(collectionChangeRecord);
        }
        this.containerPolicy.recordRemoveFromCollectionInChangeRecord((ObjectChangeSet)changeSetToRemove, collectionChangeRecord);
        if (referenceKey != null) {
            ((ObjectChangeSet)changeSetToRemove).setOldKey(referenceKey);
        }
    }

    /**
     * INTERNAL:
     * Either create a new change record or update with the new value.  This is used
     * by attribute change tracking.
     * Specifically in a collection mapping this will be called when the customer
     * Set a new collection.  In this case we will need to mark the change record
     * with the new and the old versions of the collection.
     * And mark the ObjectChangeSet with the attribute name then when the changes are calculated
     * force a compare on the collections to determine changes.
     */
    @Override
    public void updateChangeRecord(Object clone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            collectionChangeRecord = new CollectionChangeRecord(objectChangeSet);
            collectionChangeRecord.setAttribute(getAttributeName());
            collectionChangeRecord.setMapping(this);
            objectChangeSet.addChange(collectionChangeRecord);
        }
        // the order is essential - the record should be set to deferred before recreateOriginalCollection is called -
        // otherwise will keep altering the change record while adding/removing each element into/from the original collection. 
        collectionChangeRecord.setIsDeferred(true);
        objectChangeSet.deferredDetectionRequiredOn(getAttributeName());
        if (collectionChangeRecord.getOriginalCollection() == null) {
            collectionChangeRecord.recreateOriginalCollection(oldValue, uow);
        }
        collectionChangeRecord.setLatestCollection(newValue);        
    }
    
    /**
     * INTERNAL:
     * Update a ChangeRecord to replace the ChangeSet for the old entity with the changeSet for the new Entity.  This is
     * used when an Entity is merged into itself and the Entity reference new or detached entities.
     */
    public void updateChangeRecordForSelfMerge(ChangeRecord changeRecord, Object source, Object target, UnitOfWorkChangeSet parentUOWChangeSet, UnitOfWorkImpl unitOfWork){
        getContainerPolicy().updateChangeRecordForSelfMerge(changeRecord, source, target, this, parentUOWChangeSet, unitOfWork);
    }
    
    /**
     * INTERNAL:
     * Add or removes a new value and its change set to the collection change record based on the event passed in.  This is used by
     * attribute change tracking.
     */
    public void updateCollectionChangeRecord(CollectionChangeEvent event, ObjectChangeSet changeSet, UnitOfWorkImpl uow) {
        if (event !=null && event.getNewValue() != null) {
            Object newValue = event.getNewValue();
            ClassDescriptor descriptor;

            //PERF: Use referenceDescriptor if it does not have inheritance
            if (!getReferenceDescriptor().hasInheritance()) {
                descriptor = getReferenceDescriptor();
            } else {
                descriptor = uow.getDescriptor(newValue);
            }
            newValue = descriptor.getObjectBuilder().unwrapObject(newValue, uow);
            ObjectChangeSet changeSetToAdd = descriptor.getObjectBuilder().createObjectChangeSet(newValue, (UnitOfWorkChangeSet)changeSet.getUOWChangeSet(), uow);

            CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
            if (collectionChangeRecord == null) {
                collectionChangeRecord = new CollectionChangeRecord(changeSet);
                collectionChangeRecord.setAttribute(getAttributeName());
                collectionChangeRecord.setMapping(this);
                changeSet.addChange(collectionChangeRecord);
            }
            if(!collectionChangeRecord.isDeferred()) {
                this.containerPolicy.recordUpdateToCollectionInChangeRecord(event, changeSetToAdd, collectionChangeRecord);
            }
        }
    }
           
    /**
     * INTERNAL:
     * Set the change listener in the collection.
     * If the collection is not indirect it must be re-built.
     * This is used for resuming or flushing units of work.
     */
    @Override
    public void setChangeListener(Object clone, PropertyChangeListener listener, UnitOfWorkImpl uow) {
        if (this.indirectionPolicy.usesTransparentIndirection() && isAttributeValueInstantiated(clone)) {
            Object attributeValue = getRealAttributeValueFromObject(clone, uow);
            if (!(attributeValue instanceof CollectionChangeTracker)) {
                Object container = attributeValue;
                ContainerPolicy containerPolicy = this.containerPolicy;
                if (attributeValue == null) {
                    container = containerPolicy.containerInstance(1);
                } else {
                    container = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));
                    for (Object iterator = containerPolicy.iteratorFor(attributeValue); containerPolicy.hasNext(iterator);) {
                        containerPolicy.addInto(containerPolicy.nextEntry(iterator, uow), container, uow);
                    }
                }
                setRealAttributeValueInObject(clone, container);
                ((CollectionChangeTracker)container).setTrackedAttributeName(getAttributeName());
                ((CollectionChangeTracker)container)._persistence_setPropertyChangeListener(listener);
            } else {
                ((CollectionChangeTracker)attributeValue).setTrackedAttributeName(getAttributeName());
                ((CollectionChangeTracker)attributeValue)._persistence_setPropertyChangeListener(listener);
            }
        }
        if (this.indirectionPolicy.usesTransparentIndirection()){
            ((IndirectCollection)getRealAttributeValueFromObject(clone, uow)).clearDeferredChanges();
        }
        
    }

    /**
     * PUBLIC:
     * indicates whether the mapping supports listOrderField, if it doesn't attempt to set listOrderField throws exception.
     */
    public boolean isListOrderFieldSupported() {
        return isListOrderFieldSupported;
    }
    
    /**
     * PUBLIC:
     * Field holds the order of elements in the list in the db, requires collection of type List.
     * Throws exception if the mapping doesn't support listOrderField.
     */
    public void setListOrderField(DatabaseField field) {
        if(field != null) { 
            if(isListOrderFieldSupported) {
                this.listOrderField = field;
            } else {
                throw ValidationException.listOrderFieldNotSupported(this);
            }
        } else {
            this.listOrderField = null;
        }
    }
    
    /**
     * PUBLIC:
     * Field holds the order of elements in the list in the db, requires collection of type List.
     * Throws exception if the mapping doesn't support listOrderField.
     */
    public void setListOrderFieldName(String fieldName) {
        setListOrderField(new DatabaseField(fieldName));
    }
    
    
    /**
     * ADVANCED::
     * Return whether the reference objects must be deleted
     * one by one, as opposed to with a single DELETE statement.
     * Note: Calling this method disables an optimization of the delete
     * behavior
     */
    public void setMustDeleteReferenceObjectsOneByOne(Boolean deleteOneByOne) {
        this.mustDeleteReferenceObjectsOneByOne = deleteOneByOne;
    }
    
    /**
     * PUBLIC:
     * Specifies what should be done if the list of values read from listOrserField is invalid
     * (there should be no nulls, no duplicates, no "holes").
     */
    public void setOrderCorrectionType(OrderCorrectionType orderCorrectionType) {
        this.orderCorrectionType = orderCorrectionType;
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * Note that if listOrderField is used then setListOrderField method 
     * should be called before this method.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Collection</code> interface.
     */
    public void useCollectionClass(Class concreteClass) {
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(concreteClass, hasOrderBy() || listOrderField != null);
        setContainerPolicy(policy);
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.SortedSet</code> interface.
     */
    public void useSortedSetClass(Class concreteClass, Comparator comparator) {
        try {
            SortedCollectionContainerPolicy policy = (SortedCollectionContainerPolicy)ContainerPolicy.buildPolicyFor(concreteClass);
            policy.setComparator(comparator);
            setContainerPolicy(policy);
        } catch (ClassCastException e) {
            useCollectionClass(concreteClass);
        }
    }
    
    /**
     * INTERNAL:
     * Configure the mapping to use an instance of the specified container class name
     * to hold the target objects. This method is used by MW.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.SortedSet</code> interface.
     */
    public void useSortedSetClassName(String className) {
        this.useSortedSetClassName(className, null);
    }
    
    /**
     * INTERNAL:
     * Configure the mapping to use an instance of the specified container class name
     * to hold the target objects. This method is used by MW.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.SortedSet</code> interface.
     */
    public void useSortedSetClassName(String className, String comparatorClassName) {
        SortedCollectionContainerPolicy policy = new SortedCollectionContainerPolicy(className);
        policy.setComparatorClassName(comparatorClassName);
        setContainerPolicy(policy);
    }
    
    /**
     * INTERNAL:
     * Used to set the collection class by name.
     * This is required when building from metadata to allow the correct class loader to be used.
     */
    public void useCollectionClassName(String concreteClassName) {
        setContainerPolicy(new CollectionContainerPolicy(concreteClassName));
    }

    /**
     * INTERNAL:
     * Used to set the collection class by name.
     * This is required when building from metadata to allow the correct class loader to be used.
     */
    public void useListClassName(String concreteClassName) {
        setContainerPolicy(new ListContainerPolicy(concreteClassName));
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects. The key used to index a value in the
     * <code>Map</code> is the value returned by a call to the specified
     * zero-argument method.
     * The method must be implemented by the class (or a superclass) of any
     * value to be inserted into the <code>Map</code>.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Map</code> interface.
     * <p>To facilitate resolving the method, the mapping's referenceClass
     * must set before calling this method.
     */
    public void useMapClass(Class concreteClass, String keyName) {
        // the reference class has to be specified before coming here
        if (getReferenceClassName() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(concreteClass);
        policy.setKeyName(keyName, getReferenceClassName());

        setContainerPolicy(policy);
    }
    
    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container 
     * class to hold the target objects. The key used to index a value in the
     * <code>Map</code> is an instance of the composite primary key class.
     * <p> To facilitate resolving the primary key class, the mapping's 
     * referenceClass must set before calling this method.
     * <p> The container class must implement (directly or indirectly) the
     * <code>java.util.Map</code> interface.
     */
    public void useMapClass(Class concreteClass) {
        useMapClass(concreteClass, null);
    }

    /**
     * INTERNAL:
     * Not sure were this is used, MW?
     */
    public void useMapClassName(String concreteClassName, String methodName) {
        // the reference class has to be specified before coming here
        if (getReferenceClassName() == null) {
            throw DescriptorException.referenceClassNotSpecified(this);
        }
        MapContainerPolicy policy = new MapContainerPolicy(concreteClassName);
        policy.setKeyName(methodName, getReferenceClass().getName());

        setContainerPolicy(policy);
    }

    /**
     * PUBLIC:
     * If transparent indirection is used, a special collection will be placed in the source
     * object's attribute.
     * Fetching of the contents of the collection from the database will be delayed
     * until absolutely necessary. (Any message sent to the collection will cause
     * the contents to be faulted in from the database.)
     * This can result in rather significant performance gains, without having to change
     * the source object's attribute from Collection (or List or Vector) to
     * ValueHolderInterface.
     */
    public void useTransparentCollection() {
        setIndirectionPolicy(new TransparentIndirectionPolicy());
        useCollectionClass(ClassConstants.IndirectList_Class);
    }
    
    /**
     * PUBLIC:
     * If transparent indirection is used, a special collection will be placed in the source
     * object's attribute.
     * Fetching of the contents of the collection from the database will be delayed
     * until absolutely necessary. (Any message sent to the collection will cause
     * the contents to be faulted in from the database.)
     * This can result in rather significant performance gains, without having to change
     * the source object's attribute from Set to
     * ValueHolderInterface.
     */
    public void useTransparentSet() {
        setIndirectionPolicy(new TransparentIndirectionPolicy());
        useCollectionClass(IndirectSet.class);
        setSelectionQueryContainerPolicy(ContainerPolicy.buildPolicyFor(HashSet.class));
    }

    /**
     * PUBLIC:
     * If transparent indirection is used, a special collection will be placed in the source
     * object's attribute.
     * Fetching of the contents of the collection from the database will be delayed
     * until absolutely necessary. (Any message sent to the collection will cause
     * the contents to be faulted in from the database.)
     * This can result in rather significant performance gains, without having to change
     * the source object's attribute from List to
     * ValueHolderInterface.
     */
    public void useTransparentList() {
        setIndirectionPolicy(new TransparentIndirectionPolicy());
        useCollectionClass(ClassConstants.IndirectList_Class);
        setSelectionQueryContainerPolicy(ContainerPolicy.buildPolicyFor(Vector.class, hasOrderBy() || listOrderField != null));
    }

    /**
     * PUBLIC:
     * If transparent indirection is used, a special map will be placed in the source
     * object's attribute.
     * Fetching of the contents of the map from the database will be delayed
     * until absolutely necessary. (Any message sent to the map will cause
     * the contents to be faulted in from the database.)
     * This can result in rather significant performance gains, without having to change
     * the source object's attribute from Map (or Map or Hashtable) to
     * ValueHolderInterface.<p>
     * The key used in the Map is the value returned by a call to the zero parameter
     * method named methodName. The method should be a zero argument method implemented (or
     * inherited) by the value to be inserted into the Map.
     */
    public void useTransparentMap(String methodName) {
        setIndirectionPolicy(new TransparentIndirectionPolicy());
        useMapClass(ClassConstants.IndirectMap_Class, methodName);
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(Hashtable.class);
        policy.setKeyName(methodName, getReferenceClass());
        setSelectionQueryContainerPolicy(policy);
    }

    /**
     * INTERNAL:
     * To validate mappings declaration
     */
    @Override
    public void validateBeforeInitialization(AbstractSession session) throws DescriptorException {
        super.validateBeforeInitialization(session);

        this.indirectionPolicy.validateContainerPolicy(session.getIntegrityChecker());

        if (getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
            Class attributeType = ((InstanceVariableAttributeAccessor)getAttributeAccessor()).getAttributeType();
            this.indirectionPolicy.validateDeclaredAttributeTypeForCollection(attributeType, session.getIntegrityChecker());
        } else if (getAttributeAccessor().isMethodAttributeAccessor()) {
            // 323403
            Class returnType = ((MethodAttributeAccessor)getAttributeAccessor()).getGetMethodReturnType();
            this.indirectionPolicy.validateGetMethodReturnTypeForCollection(returnType, session.getIntegrityChecker());

            Class parameterType = ((MethodAttributeAccessor)getAttributeAccessor()).getSetMethodParameterType();
            this.indirectionPolicy.validateSetMethodParameterTypeForCollection(parameterType, session.getIntegrityChecker());
        }
    }

    /**
     * INTERNAL:
     * Checks if object is deleted from the database or not.
     */
    @Override
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        // Row is built for translation
        if (isReadOnly()) {
            return true;
        }

        if (isPrivateOwned() || isCascadeRemove()) {
            Object objects = getRealCollectionAttributeValueFromObject(object, session);

            ContainerPolicy containerPolicy = this.containerPolicy;
            for (Object iter = containerPolicy.iteratorFor(objects); containerPolicy.hasNext(iter);) {
                if (!session.verifyDelete(containerPolicy.next(iter, session))) {
                    return false;
                }
            }
        }

        AbstractRecord row = getDescriptor().getObjectBuilder().buildRowForTranslation(object, session);

        //cr 3819 added the line below to fix the translationtable to ensure that it
        // contains the required values
        prepareTranslationRow(row, object, getDescriptor(), session);
        Object value = session.executeQuery(getSelectionQuery(), row);

        return this.containerPolicy.isEmpty(value);
    }

    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    @Override
    public boolean isChangeTrackingSupported(Project project) {
        return this.indirectionPolicy.usesTransparentIndirection();
    }

    /**
     * INTERNAL:
     * Directly build a change record without comparison
     */
    @Override
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(clone);
        if ((cloneAttribute != null) && (!this.indirectionPolicy.objectIsInstantiated(cloneAttribute))) {
            return null;
        }

        // 2612538 - the default size of Map (32) is appropriate
        IdentityHashMap cloneKeyValues = new IdentityHashMap();
        ContainerPolicy cp = this.containerPolicy;
        Object cloneObjectCollection = null;
        if (cloneAttribute != null) {
            cloneObjectCollection = getRealCollectionAttributeValueFromObject(clone, session);
        } else {
            cloneObjectCollection = cp.containerInstance(1);
        }
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);

        while (cp.hasNext(cloneIter)) {
            Object firstObject = cp.next(cloneIter, session);
            if (firstObject != null) {
                cloneKeyValues.put(firstObject, firstObject);
            }
        }

        CollectionChangeRecord changeRecord = new CollectionChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        changeRecord.addAdditionChange(cloneKeyValues, cp, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
        if (changeRecord.hasChanges()) {
            return changeRecord;
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * This method is used to load a relationship from a list of PKs. This list
     * may be available if the relationship has been cached.
     */
    @Override
    public Object valueFromPKList(Object[] pks, AbstractRecord foreignKeys, AbstractSession session){
        ContainerPolicy cp = this.containerPolicy;
        return cp.valueFromPKList(pks, foreignKeys, this, session);
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * To get here the mapping's isJoiningSupported() should return true.
     */
    @Override
    protected Object valueFromRowInternalWithJoin(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey parentCacheKey, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {

        Object value = this.containerPolicy.containerInstance();
        // Extract the primary key of the source object, to filter only the joined rows for that object.
        Object sourceKey = this.descriptor.getObjectBuilder().extractPrimaryKeyFromRow(row, executionSession);
        // If the query was using joining, all of the result rows by primary key will have been computed.
        List<AbstractRecord> rows = joinManager.getDataResultsByPrimaryKey().get(sourceKey);
        // If no 1-m rows were fetch joined, then get the value normally,
        // this can occur with pagination where the last row may not be complete.
        if (rows == null) {
            return valueFromRowInternal(row, joinManager, sourceQuery, executionSession);
        }
        int size = rows.size();
        if (size > 0) {
            // A nested query must be built to pass to the descriptor that looks like the real query execution would,
            // these should be cached on the query during prepare.
            ObjectLevelReadQuery nestedQuery = prepareNestedJoinQueryClone(row, rows, joinManager, sourceQuery, executionSession);
            
            // A set of target cache keys must be maintained to avoid duplicates from multiple 1-m joins.
            Set targetPrimaryKeys = new HashSet();
            
            ArrayList targetObjects = null;
            ArrayList<AbstractRecord> targetRows = null;
            boolean shouldAddAll = this.containerPolicy.shouldAddAll();
            if (shouldAddAll) {
                targetObjects = new ArrayList(size);
                targetRows = new ArrayList(size);
            }
            
            // For each rows, extract the target row and build the target object and add to the collection.
            for (int index = 0; index < size; index++) {
                AbstractRecord sourceRow = rows.get(index);
                AbstractRecord targetRow = sourceRow;
                // The field for many objects may be in the row,
                // so build the subpartion of the row through the computed values in the query,
                // this also helps the field indexing match.
                targetRow = trimRowForJoin(targetRow, joinManager, executionSession);
                
                // Partial object queries must select the primary key of the source and related objects.
                // If the target joined rows in null (outerjoin) means an empty collection.
                Object targetKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(targetRow, executionSession);
                if (targetKey == null) {
                    // A null primary key means an empty collection returned as nulls from an outerjoin.
                    return this.indirectionPolicy.valueFromRow(value);
                }
                
                // Only build/add the target object once, skip duplicates from multiple 1-m joins.
                if (!targetPrimaryKeys.contains(targetKey)) {
                    nestedQuery.setTranslationRow(targetRow);
                    targetPrimaryKeys.add(targetKey);
                    Object targetObject = getReferenceDescriptor().getObjectBuilder().buildObject(nestedQuery, targetRow);
                    Object targetMapKey = this.containerPolicy.buildKeyFromJoinedRow(targetRow, joinManager, nestedQuery, parentCacheKey, executionSession, isTargetProtected);
                    nestedQuery.setTranslationRow(null);
                    if (targetMapKey == null){
                        if (shouldAddAll) {
                            targetObjects.add(targetObject);
                            targetRows.add(targetRow);
                        } else {
                            this.containerPolicy.addInto(targetObject, value, executionSession);
                        }
                    } else {
                        this.containerPolicy.addInto(targetMapKey, targetObject, value, executionSession);
                    }
                }
            }
            if (shouldAddAll) {
                this.containerPolicy.addAll(targetObjects, value, executionSession, targetRows, nestedQuery, parentCacheKey, isTargetProtected);
            }
        }
        return this.indirectionPolicy.valueFromRow(value);
    }
}
