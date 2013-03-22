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
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.IdentityHashMap;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.queries.ContainerPolicy;

/**
 * <p>
 * <b>Purpose</b>: This class holds the record of the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: Collections must be compared to each other and added and removed objects must
 * be recorded separately.
 */
public class CollectionChangeRecord extends DeferrableChangeRecord implements org.eclipse.persistence.sessions.changesets.CollectionChangeRecord {

    /**
     * Contains the added values to the collection and their corresponding ChangeSets.
     */
    protected Map<ObjectChangeSet, ObjectChangeSet> addObjectList;
    
    /** 
     * Contains the added values to the collection and their corresponding ChangeSets in order.
     */
    protected List<ObjectChangeSet> orderedAddObjects;
    
    /**
     * Contains the added values index to the collection. 
     */
    protected Map<ObjectChangeSet, Integer> orderedAddObjectIndices;
    
    /**
     * Contains OrderedChangeObjects representing each change made to the collection. 
     */
    protected List<OrderedChangeObject> orderedChangeObjectList;
    
    /** 
     * Contains the removed values to the collection and their corresponding ChangeSets.
     */
    protected Map<Integer, ObjectChangeSet> orderedRemoveObjects;
    
    /**
     * Contains the removed values index to the collection. 
     */
    protected transient List<Integer> orderedRemoveObjectIndices;
    
    /**
     * Contains a list of extra adds.  These extra adds are used by attribute change tracking
     * to replicate behavior when someone adds the same object to a list and removes it once.
     * In this case the object should still appear once in the change set.
     */
     protected transient List<ObjectChangeSet> addOverFlow;

    /**
     * Contains the removed values from the collection and their corresponding ChangeSets.
     */
    protected Map<ObjectChangeSet, ObjectChangeSet> removeObjectList;
        
    /**
     * Indicates whether IndirectList's order has been repaired.
     */
    protected boolean orderHasBeenRepaired;
    
    /**
     * This default constructor.
     */
    public CollectionChangeRecord() {
        super();
    }

    /**
     * Constructor for the ChangeRecord representing a collection mapping
     * @param owner the changeSet that uses this record
     */
    public CollectionChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }
    
    /**
     * This method takes a Map of objects, converts these into ObjectChangeSets.
     */
    public void addAdditionChange(Map objectChanges, ContainerPolicy cp, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        Iterator enumtr = objectChanges.values().iterator();
        while (enumtr.hasNext()) {
            Object object = cp.unwrapElement(enumtr.next());
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            if (change.hasKeys()){
                // if change set has keys this is a map comparison.  Maps are
                // not supported in change tracking so do not need to prevent duplicates
                // when map support is added this will have to be refactored
                getAddObjectList().put(change, change);
            } else {
                if (getRemoveObjectList().containsKey(change)) {
                    getRemoveObjectList().remove(change);
                } else {
                    getAddObjectList().put(change, change);
                }
            }
        }
    }
    
    /**
     * This method takes a list of objects and converts them into 
     * ObjectChangeSets. This method should only be called from a 
     * ListContainerPolicy. Additions to the list are made by index, hence,
     * the second Map of objectChangesIndices.
     */
    public void addOrderedAdditionChange(List<Object> orderedObjectsToAdd, Map<Object, Integer> objectChangesIndices, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        for (Object object : orderedObjectsToAdd) {
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            getOrderedAddObjects().add(change);
            getOrderedAddObjectIndices().put(change, objectChangesIndices.get(object));
        }
    }
    
    /**
     * This method takes a map of objects and converts them into 
     * ObjectChangeSets. This method should only be called from a
     * ListContainerPolicy. Deletions from the list is made by index, hence,
     * the second Vector of indicesToRemove.
     */
    public void addOrderedRemoveChange(List<Integer> indicesToRemove, Map objectChanges, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        this.orderedRemoveObjectIndices = indicesToRemove;
        
        for (Integer index : indicesToRemove) {
            Object object = objectChanges.get(index);
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            getOrderedRemoveObjects().put(index, change);
        }
    }

    /**
     * This method takes a Map of objects, converts these into ObjectChangeSets.
     */
    public void addRemoveChange(Map objectChanges, ContainerPolicy cp, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        // There is no need to keep track of removed new objects because it will not be in the backup,
        // It will not be in the backup because it is new.
        if(objectChanges.isEmpty()) {
            return;
        }
        ClassDescriptor descriptor = this.mapping.getReferenceDescriptor();
        boolean hasChildren = descriptor.hasInheritance() && descriptor.getInheritancePolicy().hasChildren();
        Iterator enumtr = cp.getChangeValuesFrom(objectChanges);
        while (enumtr.hasNext()) {
            Object object = cp.unwrapElement(enumtr.next());
            if(hasChildren) {
                descriptor = getReferenceDescriptor(object, session);
            }
            ObjectChangeSet change = descriptor.getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            if (change.hasKeys()) {
                // if change set has keys this is a map comparison.  Maps are
                // not support in change tracking so do not need to prevent duplicates
                // when map support is added this will have to be refactored
                getRemoveObjectList().put(change, change);
            } else {
                if (getAddObjectList().containsKey(change)) {
                    getAddObjectList().remove(change);
                } else {
                    getRemoveObjectList().put(change, change);
                }
            }
        }
    }

    /**
     * ADVANCED:
     * This method returns the collection of ChangeSets that were added to the collection.
     */
    public Map<ObjectChangeSet, ObjectChangeSet> getAddObjectList() {
        if (addObjectList == null) {
            addObjectList = new IdentityHashMap(10);
        }
        return addObjectList;
    }

    /**
     * Returns a list of extra adds.
     * These extra adds are used by attribute change tracking
     * to replicate behavior when someone adds the same object to a list and removes it once.
     * In this case the object should still appear once in the change set.
     */
    public List<ObjectChangeSet> getAddOverFlow() {
        if (addOverFlow == null) {
            addOverFlow = new ArrayList();
        }
        return addOverFlow;
    }

    /**
     * Returns descriptor corresponding to the object.
     */
    ClassDescriptor getReferenceDescriptor(Object object, AbstractSession session) {
        return session.getClassDescriptor(object);
    }
    
    /**
     * PUBLIC:
     * This method returns the Map that contains the removed values from the collection
     * and their corresponding ChangeSets.
     */
    public Map<ObjectChangeSet, ObjectChangeSet> getRemoveObjectList() {
        if (removeObjectList == null) {
            removeObjectList = new IdentityHashMap();
        }
        return removeObjectList;
    }

    /**
     * PUBLIC:
     * Returns true if the change set has changes.
     */
    public boolean hasChanges() {
        return (!(  (this.addObjectList == null || this.addObjectList.isEmpty()) && 
                    (this.removeObjectList == null || this.removeObjectList.isEmpty()) && 
                    (this.orderedAddObjects == null || this.orderedAddObjects.isEmpty()) && 
                    (this.orderedRemoveObjects == null || this.orderedRemoveObjects.isEmpty()) &&
                    (this.orderedChangeObjectList == null || this.orderedChangeObjectList.isEmpty()))) 
                || getOwner().isNew();
    }

    /**
     * This method will be used to merge one record into another.
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        if (((DeferrableChangeRecord)mergeFromRecord).isDeferred()){
            if (this.hasChanges()){
                //merging into existing change record need to combine changes
                ((DeferrableChangeRecord)mergeFromRecord).getMapping().calculateDeferredChanges(mergeFromRecord, mergeToChangeSet.getSession());
            }else{
                if (! this.isDeferred){
                    this.originalCollection = ((DeferrableChangeRecord)mergeFromRecord).originalCollection;
                }
                this.isDeferred = true;
                this.latestCollection = ((DeferrableChangeRecord)mergeFromRecord).latestCollection;
                return;
            }
        }
        Iterator addEnum = ((CollectionChangeRecord)mergeFromRecord).getAddObjectList().keySet().iterator();
        while (addEnum.hasNext()) {
            ObjectChangeSet mergingObject = (ObjectChangeSet)addEnum.next();
            ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(mergingObject, mergeFromChangeSet);
            if (getRemoveObjectList().containsKey(localChangeSet)) {
                getRemoveObjectList().remove(localChangeSet);
            } else {
                getAddObjectList().put(localChangeSet, localChangeSet);
            }
        }
        Iterator removeEnum = ((CollectionChangeRecord)mergeFromRecord).getRemoveObjectList().keySet().iterator();
        while (removeEnum.hasNext()) {
            ObjectChangeSet mergingObject = (ObjectChangeSet)removeEnum.next();
            ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(mergingObject, mergeFromChangeSet);
            if (getAddObjectList().containsKey(localChangeSet)) {
                getAddObjectList().remove(localChangeSet);
            } else {
                getRemoveObjectList().put(localChangeSet, localChangeSet);
            }
        }
        //237545: merge the changes for ordered list's attribute change tracking. (still need to check if deferred changes need to be merged)
        Iterator orderedChangeObjectEnum = ((CollectionChangeRecord)mergeFromRecord).getOrderedChangeObjectList().iterator();
        while (orderedChangeObjectEnum.hasNext()) {
            OrderedChangeObject changeObject = (OrderedChangeObject)orderedChangeObjectEnum.next();
            ObjectChangeSet mergingObject = changeObject.getChangeSet();
            ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(mergingObject, mergeFromChangeSet);
            
            OrderedChangeObject orderedChangeObject = new OrderedChangeObject(changeObject.getChangeType(), changeObject.getIndex(), localChangeSet);;
            getOrderedChangeObjectList().add(orderedChangeObject);
        }
    }

    /**
     * Sets the Added objects list.
     */
    public void setAddObjectList(Map<ObjectChangeSet, ObjectChangeSet> objectChangesList) {
        this.addObjectList = objectChangesList;
    }

    /**
     * Sets the removed objects list.
     */
    public void setRemoveObjectList(Map<ObjectChangeSet, ObjectChangeSet> objectChangesList) {
        this.removeObjectList = objectChangesList;
    }

    /**
     * This method will be used to update the objectsChangeSets references.
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        Map addList = new IdentityHashMap(this.getAddObjectList().size() + 1);
        Map removeList = new IdentityHashMap(this.getRemoveObjectList().size() + 1);
        // If we have ordered lists we need to iterate through those.
        if (getOrderedAddObjects().size() > 0 || getOrderedRemoveObjectIndices().size() > 0) {
            // Do the ordered adds first ...
            List<ObjectChangeSet> orderedAddList = new ArrayList(getOrderedAddObjects().size());
            Map orderedAddListIndices = new IdentityHashMap(getOrderedAddObjectIndices().size());
            
            for (int i = 0; i < getOrderedAddObjects().size(); i++) {
                ObjectChangeSet changeSet = getOrderedAddObjects().get(i);
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(changeSet, mergeFromChangeSet);
                
                orderedAddList.add(localChangeSet);
                orderedAddListIndices.put(localChangeSet, getOrderedAddObjectIndices().get(changeSet));    
                
                // Object was actually added and not moved.
                if (getAddObjectList().containsKey(changeSet)) {
                    addList.put(localChangeSet, localChangeSet);
                }
            }
            
            setOrderedAddObjects(orderedAddList);
            setOrderedAddObjectIndices(orderedAddListIndices);
            
            // Do the ordered removes now ...
            Map orderedRemoveList = new HashMap(getOrderedRemoveObjects().size());
            for (Object index : getOrderedRemoveObjects().keySet()) {
                ObjectChangeSet changeSet = getOrderedRemoveObjects().get(index);
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(changeSet, mergeFromChangeSet);
                
                orderedRemoveList.put(index, localChangeSet);
                
                // Object was actually removed and not moved.
                if (getRemoveObjectList().containsKey(changeSet)) {
                    removeList.put(localChangeSet, localChangeSet);
                }
            }
            
            setOrderedRemoveObjects(orderedRemoveList);
            // Don't need to worry about the vector of indices (Integer's), just leave them as is.
        } else {
            Iterator changes = getAddObjectList().values().iterator();
            while (changes.hasNext()) {
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)changes.next(), mergeFromChangeSet);
                addList.put(localChangeSet, localChangeSet);
            }
        
            changes = getRemoveObjectList().values().iterator();
            while (changes.hasNext()) {
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)changes.next(), mergeFromChangeSet);
                removeList.put(localChangeSet, localChangeSet);
            }
        }
        
        setAddObjectList(addList);
        setRemoveObjectList(removeList);
    }
    
    /**
     * This method returns the collection of ChangeSets in the order they were 
     * added to the collection. This list includes those objects that were 
     * moved within the collection.
     */
    public List<ObjectChangeSet> getOrderedAddObjects() {
        if (orderedAddObjects == null) {
            orderedAddObjects = new ArrayList();
        }
        
        return orderedAddObjects;
    }
    
    /**
     * This method returns the index of an object added to the collection.
     */
    public Integer getOrderedAddObjectIndex(ObjectChangeSet changes) {
        return getOrderedAddObjectIndices().get(changes);
    }
    
    /**
     * This method returns the collection of ChangeSets that they were 
     * added to the collection.
     */
    public Map<ObjectChangeSet, Integer> getOrderedAddObjectIndices() {
        if (orderedAddObjectIndices == null) {
            orderedAddObjectIndices = new IdentityHashMap();
        }
        
        return orderedAddObjectIndices;
    }
    
    /**
     * This method returns the Vector of OrderedChangeObjects. These objects represent 
     * all changes made to the collection, and their order in the vector represents the order
     * they were performed.  
     */
    public List<OrderedChangeObject> getOrderedChangeObjectList() {
        if (orderedChangeObjectList == null) {
            orderedChangeObjectList = new ArrayList();
        }
        
        return orderedChangeObjectList;
    }
    
    /**
     * This method returns the ordered list of indices to remove from the collection.
     */
    public List<Integer> getOrderedRemoveObjectIndices() {
        if (this.orderedRemoveObjectIndices == null) {
            this.orderedRemoveObjectIndices = new ArrayList();
        }
        
        return this.orderedRemoveObjectIndices;
    }
    
    /**
     * This method returns the index of an object removed from the collection.
     */
    public Object getOrderedRemoveObject(Integer index) {
        return getOrderedRemoveObjects().get(index);
    }
    
    /**
     * This method returns the collection of ChangeSets of objects removed from
     * the collection.
     */
    public Map<Integer, ObjectChangeSet> getOrderedRemoveObjects() {
        if (this.orderedRemoveObjects == null) {
            this.orderedRemoveObjects = new HashMap();
        }
        
        return this.orderedRemoveObjects;
    }
    
    /**
     * Sets collection of ChangeSets (and their respective index) that they 
     * were added to the collection.
     */
    public void setOrderedAddObjectIndices(Map<ObjectChangeSet, Integer> orderedAddObjectIndices) {
        this.orderedAddObjectIndices = orderedAddObjectIndices;
    }
    
    /**
     * Sets collection of ChangeSets that they were added to the collection.
     */
    public void setOrderedAddObjects(List<ObjectChangeSet> orderedAddObjects) {
        this.orderedAddObjects = orderedAddObjects;
    }
    
    public void setOrderedChangeObjectList(List<OrderedChangeObject> orderedChangeObjectList) {
        this.orderedChangeObjectList = orderedChangeObjectList;
    }
    
    /**
     * Sets collection of ChangeSets that they were removed from the collection.
     */
    public void setOrderedRemoveObjects(Map<Integer, ObjectChangeSet> orderedRemoveObjects) {
        this.orderedRemoveObjects = orderedRemoveObjects;
    }
    
    /**
     * The same size as original list,
     * at the i-th position holds the index of the i-th original object in the current list (-1 if the object was removed):
     * for example: {0, -1, 1, -1, 3} means that:
     *   previous(0) == current(0);
     *   previous(1) was removed;
     *   previous(2) == current(1);
     *   previous(3) was removed;
     *   previous(4) == current(3);
     */
    public List<Integer> getCurrentIndexesOfOriginalObjects(List newList) {
        int newSize = newList.size();
        List<Integer> currentIndexes = new ArrayList(newSize);
        for(int i=0; i < newSize; i++) {
            currentIndexes.add(i);
        }
        if(orderedChangeObjectList != null) {
            for (int i = this.orderedChangeObjectList.size() - 1; i>=0; i--) {
                OrderedChangeObject  orderedChange = orderedChangeObjectList.get(i);
                Object obj = orderedChange.getAddedOrRemovedObject();
                Integer index = orderedChange.getIndex();
                int changeType = orderedChange.getChangeType();
                if(changeType == CollectionChangeEvent.ADD) {
                    // the object was added - remove the corresponding index
                    if(index == null) {
                        currentIndexes.remove(currentIndexes.size()-1);
                    } else {
                        currentIndexes.remove(index.intValue());
                    }
                } else if(changeType == CollectionChangeEvent.REMOVE) {
                    // the object was removed - add its index in the new list 
                    if(index == null) {
                        throw ValidationException.collectionRemoveEventWithNoIndex(getMapping());
                    } else {
                        currentIndexes.add(index.intValue(), newList.indexOf(obj));
                    }
                }
            }
        }
        return currentIndexes;
    }
    
   /**
    * Recreates the original state of currentCollection.
    */
   public void internalRecreateOriginalCollection(Object currentCollection, AbstractSession session) {
       ContainerPolicy cp = this.mapping.getContainerPolicy();
       if(orderedChangeObjectList == null || orderedChangeObjectList.isEmpty()) {
           if(this.removeObjectList != null) {
               Iterator it = this.removeObjectList.keySet().iterator();
               while(it.hasNext()) {
                   ObjectChangeSet changeSet = (ObjectChangeSet)it.next(); 
                   cp.addInto(changeSet.getUnitOfWorkClone(), currentCollection, session);
               }
           }
           if(this.addObjectList != null) {
               Iterator it = this.addObjectList.keySet().iterator();
               while(it.hasNext()) {
                   ObjectChangeSet changeSet = (ObjectChangeSet)it.next(); 
                   cp.removeFrom(changeSet.getUnitOfWorkClone(), currentCollection, session);
               }
           }
       } else {
           List originalList = (List)currentCollection;
           for (int i = this.orderedChangeObjectList.size() - 1; i>=0; i--) {
               OrderedChangeObject  orderedChange = this.orderedChangeObjectList.get(i);
               Object obj = orderedChange.getAddedOrRemovedObject();
               Integer index = orderedChange.getIndex();
               int changeType = orderedChange.getChangeType();
               if(changeType == CollectionChangeEvent.ADD) {
                   // the object was added - remove the corresponding index
                   if(index == null) {
                       originalList.remove(originalList.size()-1);
                   } else {
                       originalList.remove(index.intValue());
                   }
               } else if(changeType == CollectionChangeEvent.REMOVE) {
                   // the object was removed - add its index in the new list 
                   if(index == null) {
                       throw ValidationException.collectionRemoveEventWithNoIndex(getMapping());
                   } else {
                       originalList.add(index.intValue(), obj);
                   }
               }
           }
       }
   }
  
   public void setOrderHasBeenRepaired(boolean hasBeenRepaired) {
       this.orderHasBeenRepaired = hasBeenRepaired;
   }
   public boolean orderHasBeenRepaired() {
       return this.orderHasBeenRepaired;
   }

   /**
    * Clears info about added / removed objects set by change tracker.
    */
   public void clearChanges() {
       if(orderedChangeObjectList != null) {
           this.orderedChangeObjectList.clear();
       }
       if(this.removeObjectList != null) {
           this.removeObjectList.clear();
       }
       if(this.addObjectList != null) {
           this.addObjectList.clear();
       }
   }
}
