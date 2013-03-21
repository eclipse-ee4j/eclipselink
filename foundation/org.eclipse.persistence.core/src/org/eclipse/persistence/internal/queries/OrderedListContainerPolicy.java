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
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.ListIterator;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.IndexedObject;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.OrderedChangeObject;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * <p><b>Purpose</b>: A OrderedListContainerPolicy is ContainerPolicy whose
 * container class implements the List interface and is ordered by an @OrderBy.
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a List.
 *
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 * @see ListContainerPolicy
 */
public class OrderedListContainerPolicy extends ListContainerPolicy {
    protected static final String NOT_SET = "NOT_SET";
    protected DatabaseField listOrderField;    
    protected OrderCorrectionType orderCorrectionType;
        
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public OrderedListContainerPolicy() {
        super();
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public OrderedListContainerPolicy(Class containerClass) {
        super(containerClass);
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public OrderedListContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Add a list of elements to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * The row may be required by subclasses
     * Return whether the container changed
     */
    @Override
    public boolean addAll(List elements, Object container, AbstractSession session, List<AbstractRecord> dbRows, ObjectBuildingQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        if(this.listOrderField == null) {
            return super.addAll(elements, container, session, dbRows, query, parentCacheKey, isTargetProtected);
        } else {
            return addAll(elements, container, session, dbRows, query, parentCacheKey);
        }
    }
    
    /**
     * INTERNAL:
     * Add a list of elements to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * The row may be required by subclasses
     * Return whether the container changed
     */
    @Override
    public boolean addAll(List elements, Object container, AbstractSession session, List<AbstractRecord> dbRows, DataReadQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        if(this.listOrderField == null) {
            return super.addAll(elements, container, session, dbRows, query, parentCacheKey, isTargetProtected);
        } else {
            return addAll(elements, container, session, dbRows, query, parentCacheKey);
        }
    }

    protected boolean addAll(List elements, Object container, AbstractSession session, List<AbstractRecord> dbRows, ReadQuery query, CacheKey parentCacheKey) {
        int size = dbRows.size();

        if(this.elementDescriptor != null && this.elementDescriptor.getObjectBuilder().hasWrapperPolicy()) {
            ObjectBuilder objectBuilder = this.elementDescriptor.getObjectBuilder();
            List wrappedElements = new ArrayList(size);
            for(int i=0; i < size; i++) {
                wrappedElements.add(objectBuilder.wrapObject(elements.get(i), session));
            }
            elements = wrappedElements;
        }
        
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();

        // populate container with a dummy so the container.set(i, obj) could be used later.
        for(int i=0; i < size; i++) {
            ((List)container).add(NOT_SET);
        }
        // insert the elements into container        
        boolean failed = false;
        for(int i=0; i < size; i++) {
            AbstractRecord row = dbRows.get(i);
            Object orderValue = row.get(this.listOrderField);
            // order value is null
            if(orderValue == null) {
                failed = true;
                break;
            }
            int intOrderValue = ((Integer)conversionManager.convertObject(orderValue, Integer.class)).intValue();
            try {
                // one or more elements have the same order value
                if(NOT_SET != ((List)container).set(intOrderValue, elements.get(i))) {
                    failed = true;
                    break;
                }
            } catch(IndexOutOfBoundsException indexException) {
                // order value negative or greater/equal to size
                failed = true;
                break;
            }
        }

        if(failed) {
            ((List)container).clear();
            
            // extract order list - it will be set into exception or used by order correction.
            List<Integer> orderList = new ArrayList(size);
            for(int i=0; i < size; i++) {
                AbstractRecord row = dbRows.get(i);
                Object orderValue = row.get(this.listOrderField);
                if(orderValue == null) {
                    orderList.add(null);
                } else {
                    orderList.add((Integer)conversionManager.convertObject(orderValue, Integer.class));
                }
            }
            
            if(this.orderCorrectionType == OrderCorrectionType.READ || this.orderCorrectionType == OrderCorrectionType.READ_WRITE) {
                // pair each element with its order index
                List<IndexedObject> indexedElements = new ArrayList(size);
                for(int i=0; i < size; i++) {
                    indexedElements.add(new IndexedObject(orderList.get(i), elements.get(i)));
                }

                // put elements in order and add to container 
                ((List)container).addAll(correctOrderList(indexedElements));

                if(this.orderCorrectionType == OrderCorrectionType.READ_WRITE) {
                    // mark IndirectList to have broken order
                    ((IndirectList)container).setIsListOrderBrokenInDb(true);
                }
            } else {
                // this.orderCorrectionType == OrderCorrectionType.EXCEPTION
                throw QueryException.listOrderFieldWrongValue(query, this.listOrderField, orderList);                
            }
        }
        return size > 0;
    }

    /**
     * INTERNAL:
     * Add element into a container which implements the List interface.
     * Add at a particular index.
     */
    protected void addIntoAtIndex(Integer index, Object object, Object container, AbstractSession session) {
        if (hasElementDescriptor()) {
            object = getElementDescriptor().getObjectBuilder().wrapObject(object, session);
        }
        
        try {
            if (index == null || (index.intValue() > sizeFor(container))) {
                // The index can be larger than the size on a merge,
                // so should be added to the end, it may also be null if the 
                // index was unknown, such as an event using the add API.
                ((List)container).add(object);
            } else {
                ((List)container).add(index.intValue(), object);
            }
        } catch (ClassCastException ex1) {
            throw QueryException.cannotAddElement(object, container, ex1);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotAddElement(object, container, ex2);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotAddElement(object, container, ex3);
        }
    }
    
    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     * This algorithm is a work in progress. It works great and all, but like 
     * anything, you can always make it better.
     */
    @Override
    public void compareCollectionsForChange(Object oldList, Object newList, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor) {    
        List orderedObjectsToAdd = new ArrayList();
        Map indicesToRemove = new HashMap();
        Map oldListIndexValue = new HashMap();
        IdentityHashMap oldListValueIndex = new IdentityHashMap();
        IdentityHashMap objectsToAdd = new IdentityHashMap();
        Map<Object, Integer> newListValueIndex = new IdentityHashMap();
        
        // Step 1 - Go through the old list.
        if (oldList != null) {
            ListIterator iterator = (ListIterator)iteratorFor(oldList);
        
            while (iterator.hasNext()) {
                Integer index = Integer.valueOf(iterator.nextIndex());
                Object value = iterator.next();
                oldListValueIndex.put(value, index);
                oldListIndexValue.put(index, value);
                indicesToRemove.put(index, index);
            }
        }
            
        // Step 2 - Go though the new list.
        if (newList != null) {
            // Step i - Gather the list info.
            ListIterator iterator = (ListIterator)iteratorFor(newList);
            while (iterator.hasNext()) {
                newListValueIndex.put(iterator.next(), Integer.valueOf(iterator.previousIndex()));
            }
        
            // Step ii - Go through the new list again.        
            int index = 0;
            int offset = 0;
            iterator = (ListIterator)iteratorFor(newList);
            while (iterator.hasNext()) {
                index = iterator.nextIndex();
                Object currentObject = iterator.next();
            
                // If value is null then nothing can be done with it.
                if (currentObject != null) {
                    if (oldListValueIndex.containsKey(currentObject)) {
                        int oldIndex = ((Integer) oldListValueIndex.get(currentObject)).intValue();
                        oldListValueIndex.remove(currentObject);
                    
                        if (index == oldIndex) {
                            indicesToRemove.remove(Integer.valueOf(oldIndex));
                            offset = 0; // Reset the offset, assume we're back on track.
                        } else if (index == (oldIndex + offset)) {
                            // We're in the right spot according to the offset.
                            indicesToRemove.remove(Integer.valueOf(oldIndex));
                        } else {
                            // Time to be clever and figure out why we're not in the right spot!
                            int movedObjects = 0;
                            int deletedObjects = 0;
                            boolean moved = true;
                        
                            if (oldIndex < index) {
                                ++offset;    
                            } else {
                                for (int i = oldIndex - 1; i >= index; i--) {
                                    Object oldObject = oldListIndexValue.get(Integer.valueOf(i));
                                    if (newListValueIndex.containsKey(oldObject)) {
                                        ++movedObjects;
                                    } else {
                                        ++deletedObjects;
                                    }
                                }
                            
                                if (index == ((oldIndex + offset) - deletedObjects)) {
                                    // We fell into place because of deleted objects.
                                    offset = offset - deletedObjects;
                                    moved = false;
                                } else if (movedObjects > 1) {
                                    // Assume we moved down, bumping everyone by one. 
                                    ++offset;
                                } else {
                                    // Assume we moved down unless the object that was
                                    // here before is directly beside us.
                                    Object oldObject = oldListIndexValue.get(Integer.valueOf(index));
                                
                                    if (newListValueIndex.containsKey(oldObject)) {
                                        if (((newListValueIndex.get(oldObject)).intValue() - index) > 1) {
                                            moved = false; // Assume the old object moved up.
                                            --offset; 
                                        }
                                    }
                                }
                            }
                        
                            if (moved) {
                                // Add ourselves to the ordered add list.
                                orderedObjectsToAdd.add(currentObject);
                            } else {
                                // Take us off the removed list.
                                indicesToRemove.remove(Integer.valueOf(oldIndex));    
                            }
                        }
                    } else {
                        ++offset;
                        objectsToAdd.put(currentObject, currentObject);
                        orderedObjectsToAdd.add(currentObject);
                    }
                } else {
                    // If we find nulls we need decrease our offset.
                    offset--;
                }
            }
        }
        
        // Sort the remove indices that are left and set the data on the collection change 
        // record to be processed on the merge.
        List orderedIndicesToRemove = new ArrayList(indicesToRemove.values());
        Collections.sort(orderedIndicesToRemove);
        changeRecord.addAdditionChange(objectsToAdd, this, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addRemoveChange(oldListValueIndex, this, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addOrderedAdditionChange(orderedObjectsToAdd, newListValueIndex, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addOrderedRemoveChange(orderedIndicesToRemove, oldListIndexValue, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);                
    }
    
    /**
     * PUBLIC:
     * Correct object's order in the list.
     * The method is called only in one case - when the list of order indexes read from db is invalid
     * (contains nulls, duplicates, negative values, values greater/equal list size).
     * Each element of the indexedObjects is a pair of the order index and the corresponding object.
     * The goal of the method is to return back the list of objects (not indexed objects!) in the correct order.
     * The objects should not be altered.
     * 
     * The default implementation of the method sorts indexedObjects according to their indexes (null less than any non-null).
     * For example: 
     *   indexedObjects = {{2, objectA}, {5, ObjectB}}  returns {objectA, objectB};
     *   indexedObjects = {{2, objectA}, {-1, ObjectB}}  returns {objectB, objectA};
     *   indexedObjects = {{2, objectA}, {null, ObjectB}}  returns {objectB, objectA};
     *   
     * This method could be overridden by the user.
     */
    public List correctOrderList(List<IndexedObject> indexedObjects) {
        Collections.sort(indexedObjects);
        int size = indexedObjects.size();
        List objects = new ArrayList(size);
        for(int i=0; i < size; i++) {
            objects.add(indexedObjects.get(i).getObject());
        }
        return objects;
    }
    
    /**
     * INTERNAL:
     * Used to create an iterator on a the Map object passed to CollectionChangeRecord.addRemoveChange()
     * to access the values to be removed.  In the case of some container policies the values will actually
     * be the keys.
     */
    @Override    
    public Iterator getChangeValuesFrom(Map map) {
        return map.keySet().iterator();
    }

    public DatabaseField getListOrderField() {
        return this.listOrderField;
    }

    public void setListOrderField(DatabaseField field) {
        this.listOrderField = field;
    }
    
    public OrderCorrectionType getOrderCorrectionType() {
        return this.orderCorrectionType;
    }
    
    public void setOrderCorrectionType(OrderCorrectionType orderCorrectionType) {
        if(this.orderCorrectionType == orderCorrectionType) {
            return;
        }
        if(orderCorrectionType == OrderCorrectionType.READ_WRITE) {
            if(getContainerClass() == null || !IndirectList.class.isAssignableFrom(getContainerClass())) {
                setContainerClass(IndirectList.class);
            }
        }
        this.orderCorrectionType = orderCorrectionType;
    }
    
    /**
     * INTERNAL:
     * Return an list iterator for the given container.
     */
    @Override
    public Object iteratorFor(Object container) {
        return ((List)container).listIterator();
    }

    @Override
    public boolean isOrderedListPolicy() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     * Synchronize if system property is specified. If not, default to clone the 
     * target collection. No need to synchronize if the collection is new. 
     */
    @Override
    public void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession targetSession, boolean isSynchronizeOnMerge) {
        if (isSynchronizeOnMerge && !changeRecord.getOwner().isNew()) {
            // Ensure the collection is synchronized while changes are being made,
            // clone also synchronizes on collection (does not have cache key read-lock for indirection).
            // Must synchronize of the real collection as the clone does so.
            Object synchronizedValueOfTarget = valueOfTarget;
            if (valueOfTarget instanceof IndirectCollection) {
                synchronizedValueOfTarget = ((IndirectCollection)valueOfTarget).getDelegateObject();
            }
            synchronized(synchronizedValueOfTarget) {
                mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts, mergeManager, targetSession);
            }
        } else {
            mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts, mergeManager, targetSession);
        }
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    @Override
    protected void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession targetSession) {
        ObjectChangeSet objectChanges;
        if(changeRecord.orderHasBeenRepaired() && valueOfTarget instanceof IndirectList) {
            ((IndirectList)valueOfTarget).setIsListOrderBrokenInDb(false);
        }
        if (!changeRecord.getOrderedChangeObjectList().isEmpty()) {                
            Iterator objects =changeRecord.getOrderedChangeObjectList().iterator();                
            while (objects.hasNext()){
                OrderedChangeObject changeObject = (OrderedChangeObject)objects.next();
                objectChanges = changeObject.getChangeSet();
                if (changeObject.getChangeType() == CollectionChangeEvent.REMOVE){
                    boolean objectRemoved = changeRecord.getRemoveObjectList().containsKey(objectChanges);
                    Object objectToRemove = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession);
                    
                    //if objectToRemove is null, we can't look it up in the collection. 
                    // This should not happen unless identity is lost.
                    if (objectToRemove != null) {
                        Integer index = changeObject.getIndex();
                        if (index!=null){
                            if (objectToRemove.equals(get(index, valueOfTarget, mergeManager.getSession()))) {
                                removeFromAtIndex(index, valueOfTarget);
                            } else {
                                // Object is in the cache, but the collection doesn't have it at the location we expect
                                // Collection is invalid with respect to these changes, so invalidate the parent and abort 
                                Object key = changeRecord.getOwner().getId();
                                targetSession.getIdentityMapAccessor().invalidateObject(key, changeRecord.getOwner().getClassType(targetSession));
                                return;
                            }
                        } else {
                            removeFrom(objectToRemove, valueOfTarget, targetSession);
                        }
                        
                        if ((! mergeManager.shouldMergeChangesIntoDistributedCache()) && changeRecord.getMapping().isPrivateOwned()) {
                            // Check that the object was actually removed and not moved.
                            if (objectRemoved) {
                                mergeManager.registerRemovedNewObjectIfRequired(objectChanges.getUnitOfWorkClone());
                            }
                        }
                    }
                    
                    
                } else { //getChangeType == add
                    boolean objectAdded = changeRecord.getAddObjectList().containsKey(objectChanges);
                    Object object = null;
                    // The object was actually added and not moved.
                    if (objectAdded && shouldMergeCascadeParts) {
                        object = mergeCascadeParts(objectChanges, mergeManager, targetSession);
                    }
                    
                    if (object == null) {
                        // Retrieve the object to be added to the collection.
                        object = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession);
                    }
                    
                    // Assume at this point the above merge will have created a new 
                    // object if required and that the object was actually added and 
                    // not moved.
                    if (objectAdded && mergeManager.shouldMergeChangesIntoDistributedCache()) {
                        // Bugs 4458089 & 4454532 - check if collection contains new item before adding 
                        // during merge into distributed cache                  
                        if (! contains(object, valueOfTarget, mergeManager.getSession())) {
                            addIntoAtIndex(changeObject.getIndex(), object, valueOfTarget, mergeManager.getSession());                                
                        }
                    } else {
                        addIntoAtIndex(changeObject.getIndex(), object, valueOfTarget, targetSession);
                    }
                }
            }
        } else {
            //Deferred change tracking merge behavior
            // Step 1 - iterate over the removed changes and remove them from the container.
            List<Integer> removedIndices = changeRecord.getOrderedRemoveObjectIndices();

            if (removedIndices.isEmpty()) {
                // Check if we have removed objects via a 
                // simpleRemoveFromCollectionChangeRecord API call.
                Iterator removedObjects = changeRecord.getRemoveObjectList().keySet().iterator();
            
                while (removedObjects.hasNext()) {
                    objectChanges = (ObjectChangeSet) removedObjects.next();
                    removeFrom(objectChanges.getOldKey(), objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession), valueOfTarget, targetSession);
                    registerRemoveNewObjectIfRequired(objectChanges, mergeManager);
                }
            } else {
                for (int i = removedIndices.size() - 1; i >= 0; i--) {
                    Integer index = removedIndices.get(i);
                    objectChanges = (ObjectChangeSet) changeRecord.getOrderedRemoveObject(index);
                    Object objectToRemove = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession);
                    if ( (objectToRemove!=null) && 
                                (objectToRemove.equals(get(index, valueOfTarget, mergeManager.getSession()) )) ) {
                        removeFromAtIndex(index, valueOfTarget);
                        // The object was actually removed and not moved.
                        if (changeRecord.getRemoveObjectList().containsKey(objectChanges)) {
                            registerRemoveNewObjectIfRequired(objectChanges, mergeManager);
                        }
                    } else {
                    
                        //Object is either not in the cache, or not at the location we expect
                        // Collection is invalid with respect to these changes, so invalidate the parent and abort 
                        Object key = changeRecord.getOwner().getId();
                        targetSession.getIdentityMapAccessor().invalidateObject(key, changeRecord.getOwner().getClassType(targetSession));
                        return;
                    }
                }
            }
            
            // Step 2 - iterate over the added changes and add them to the container.
            for (ObjectChangeSet addChangeSet : changeRecord.getOrderedAddObjects()) {
                objectChanges =  addChangeSet;
                boolean objectAdded = changeRecord.getAddObjectList().containsKey(objectChanges);
                Object object = null;
                
                // The object was actually added and not moved.
                if (objectAdded && shouldMergeCascadeParts) {
                    object = mergeCascadeParts(objectChanges, mergeManager, targetSession);
                }
                
                if (object == null) {
                    // Retrieve the object to be added to the collection.
                    object = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession);
                }

                // Assume at this point the above merge will have created a new 
                // object if required and that the object was actually added and 
                // not moved.
                if (objectAdded && mergeManager.shouldMergeChangesIntoDistributedCache()) {
                    // Bugs 4458089 & 4454532 - check if collection contains new item before adding 
                    // during merge into distributed cache					
                    if (! contains(object, valueOfTarget, mergeManager.getSession())) {
                        addIntoAtIndex(changeRecord.getOrderedAddObjectIndex(objectChanges), object, valueOfTarget, mergeManager.getSession());                                
                    }
                } else {
                    addIntoAtIndex(changeRecord.getOrderedAddObjectIndex(objectChanges), object, valueOfTarget, targetSession);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void registerRemoveNewObjectIfRequired(ObjectChangeSet objectChanges, MergeManager mergeManager) {
        if (! mergeManager.shouldMergeChangesIntoDistributedCache()) {
            mergeManager.registerRemovedNewObjectIfRequired(objectChanges.getUnitOfWorkClone());
        }
    }
    
    /**
     * INTERNAL:
     * Remove the element at the specified index.
     */
    protected void removeFromAtIndex(int index, Object container) {
        try {
            ((List) container).remove(index);
        } catch (ClassCastException ex1) {
            throw QueryException.cannotRemoveFromContainer(Integer.valueOf(index), container, this);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotRemoveFromContainer(Integer.valueOf(index), container, this);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotRemoveFromContainer(Integer.valueOf(index), container, this);
        }
    }
    
    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each ContainerPolicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    @Override
    public void recordAddToCollectionInChangeRecord(ObjectChangeSet changeSetToAdd, CollectionChangeRecord collectionChangeRecord){
        OrderedChangeObject orderedChangeObject = new OrderedChangeObject(CollectionChangeEvent.ADD, null, changeSetToAdd);;
        collectionChangeRecord.getOrderedChangeObjectList().add(orderedChangeObject);
    }

    @Override
    public void recordRemoveFromCollectionInChangeRecord(ObjectChangeSet changeSetToRemove, CollectionChangeRecord collectionChangeRecord){
        OrderedChangeObject orderedChangeObject = new OrderedChangeObject(CollectionChangeEvent.REMOVE, null, changeSetToRemove);;
        collectionChangeRecord.getOrderedChangeObjectList().add(orderedChangeObject);
    }

    @Override
    public void recordUpdateToCollectionInChangeRecord(CollectionChangeEvent event, ObjectChangeSet changeSet, CollectionChangeRecord collectionChangeRecord){
        int changeType = event.getChangeType();
        if (changeType == CollectionChangeEvent.ADD) {
            super.recordAddToCollectionInChangeRecord(changeSet, collectionChangeRecord);
        } else if (changeType == CollectionChangeEvent.REMOVE) {
            super.recordRemoveFromCollectionInChangeRecord(changeSet, collectionChangeRecord);
        } else {
            throw ValidationException.wrongCollectionChangeEventType(changeType);
        }

        OrderedChangeObject orderedChangeObject = new OrderedChangeObject(changeType, event.getIndex(), changeSet, event.getNewValue());
        collectionChangeRecord.getOrderedChangeObjectList().add(orderedChangeObject);
    }
    
    /**
     * INTERNAL:
     * Indicates whether addAll method should be called to add entire collection,
     * or it's possible to call addInto multiple times instead.
     */
    @Override
    public boolean shouldAddAll() {
        return this.listOrderField != null;
    }
    
    /**
     * INTERNAL:
     * Return any additional fields required by the policy for a fetch join.
     */
    @Override
    public List<DatabaseField> getAdditionalFieldsForJoin(CollectionMapping baseMapping) {
        if (this.listOrderField != null) {
            List<DatabaseField> fields = new ArrayList<DatabaseField>(1);
            fields.add(this.listOrderField);
            return fields;
        }
        return null;
    }

    /**
     * INTERNAL:
     * Add the index field count.
     */
    @Override
    public int updateJoinedMappingIndexesForMapKey(Map<DatabaseMapping, Object> indexList, int index) {
        if (this.listOrderField != null) {
            return 1;            
        }
        return 0;
    }
    
    /**
     * INTERNAL:
     * Update a ChangeRecord to replace the ChangeSet for the old entity with the changeSet for the new Entity.  This is
     * used when an Entity is merged into itself and the Entity reference new or detached entities.
     */
    @Override
    public void updateChangeRecordForSelfMerge(ChangeRecord changeRecord, Object source, Object target, ForeignReferenceMapping mapping, UnitOfWorkChangeSet parentUOWChangeSet, UnitOfWorkImpl unitOfWork){
        ObjectChangeSet sourceSet = parentUOWChangeSet.getCloneToObjectChangeSet().get(source);
        for (OrderedChangeObject changeObject : ((CollectionChangeRecord)changeRecord).getOrderedChangeObjectList()){
            if (changeObject.getChangeSet() == sourceSet){
                changeObject.setChangeSet(((UnitOfWorkChangeSet)unitOfWork.getUnitOfWorkChangeSet()).findOrCreateLocalObjectChangeSet(target, mapping.getReferenceDescriptor(), unitOfWork.isCloneNewObject(target)));
            }
            return;
        }
    }
    
    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query.
     */
    @Override
    public List<DatabaseTable> getAdditionalTablesForJoinQuery() {
        if (this.listOrderField != null) {
            List tables = new ArrayList(1);
            tables.add(this.listOrderField.getTable());
            return tables;
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Add the index field to the query. 
     */
    @Override
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression) {
        if (this.listOrderField != null) {
            if (selectionQuery.isObjectLevelReadQuery()) {
                ((ObjectLevelReadQuery)selectionQuery).addAdditionalField(baseExpression.getField(this.listOrderField));
            } else {
                ((SQLSelectStatement)((DataReadQuery)selectionQuery).getSQLStatement()).addField(baseExpression.getField(this.listOrderField));
            }
        }
    }
}
