/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.IdentityHashtable;

/**
 * <p>
 * <b>Purpose</b>: This class holds the record of the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: Collections must be compared to each other and added and removed objects must
 * be recorded seperately.
 */
public class CollectionChangeRecord extends DeferrableChangeRecord implements org.eclipse.persistence.sessions.changesets.CollectionChangeRecord {

    /**
     * Contains the added values to the collection and their corresponding ChangeSets.
     */
    protected IdentityHashtable addObjectList;
    
    /** 
     * Contains the added values to the collection and their corresponding ChangeSets in order.
     */
    protected transient Vector orderedAddObjects;
    
    /**
     * Contains the added values index to the collection. 
     */
    protected IdentityHashtable orderedAddObjectIndices;
    
    /** 
     * Contains the removed values to the collection and their corresponding ChangeSets.
     */
    protected Hashtable orderedRemoveObjects;
    
    /**
     * Contains the removed values index to the collection. 
     */
    protected transient Vector orderedRemoveObjectIndices;
    
    /**
     * Contains a list of extra adds.  These extra adds are used by attribute change tracking
     * to replicate behaviour when someone adds the same object to a list and removes it once.
     * In this case the object should still appear once in the change set.
     */
     protected transient List addOverFlow;

    /**
     * Contains the removed values from the collection and their corresponding ChangeSets.
     */
    protected IdentityHashtable removeObjectList;

    /**
     * Contain the same added values as in addObjectList.  It is only used by SDK mapping of this change record.
     */
    protected transient Vector sdkAddObjects;

    /**
     * Contain the same added values as in addObjectList.  It is only used by SDK mapping of this change record.
     */
    protected transient Vector sdkRemoveObjects;
        
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
     * This method takes a IdentityHashtable of objects, converts these into ObjectChangeSets.
     */
    public void addAdditionChange(IdentityHashMap objectChanges, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        Iterator enumtr = objectChanges.keySet().iterator();
        while (enumtr.hasNext()) {
            Object object = enumtr.next();
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            if (change.hasKeys()){
                // if change set has keys this is a map comparison.  Maps are
                // not support in change tracking so do not need to prevent duplicates
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
     * This method takes a Vector of objects and converts them into 
     * ObjectChangeSets. This method should only be called from a 
     * ListContainerPolicy. Additions to the list are made by index, hence,
     * the second IdentityHashtable of objectChangesIndices.
     */
    public void addOrderedAdditionChange(Vector objectChanges, IdentityHashtable objectChangesIndices, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        Enumeration e = objectChanges.elements();
        
        while (e.hasMoreElements()) {
            Object object = e.nextElement();
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);
            
            getOrderedAddObjects().add(change);
            getOrderedAddObjectIndices().put(change, (Integer) objectChangesIndices.get(object));
        }
    }
    
    /**
     * This method takes a Hashtable of objects and converts them into 
     * ObjectChangeSets. This method should only be called from a
     * ListContainerPolicy. Deletions from the list is made by index, hence,
     * the second Vector of indicesToRemove.
     */
    public void addOrderedRemoveChange(Vector indicesToRemove, Hashtable objectChanges, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        orderedRemoveObjectIndices = indicesToRemove;
        Enumeration e = orderedRemoveObjectIndices.elements();
        
        while (e.hasMoreElements()) {
            Integer index = (Integer) e.nextElement();
            Object object = objectChanges.get(index);
            ObjectChangeSet change = session.getDescriptor(object.getClass()).getObjectBuilder().createObjectChangeSet(object, changeSet, session);

            getOrderedRemoveObjects().put(index, change);
        }
    }

    /**
     * This method takes a IdentityHashtable of objects, converts these into ObjectChangeSets.
     */
    public void addRemoveChange(IdentityHashMap objectChanges, UnitOfWorkChangeSet changeSet, AbstractSession session) {
        // There is no need to keep track of removed new objects because it will not be in the backup,
        // It will not be in the backup because it is new.
        Iterator enumtr = objectChanges.keySet().iterator();
        while (enumtr.hasNext()) {
            Object object = enumtr.next();
            ClassDescriptor descriptor = session.getDescriptor(object.getClass());
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
    public IdentityHashtable getAddObjectList() {
        if (addObjectList == null) {
            addObjectList = new IdentityHashtable(10);
        }
        return addObjectList;
    }

    /**
     * Returns a list of extra adds.
     * These extra adds are used by attribute change tracking
     * to replicate behaviour when someone adds the same object to a list and removes it once.
     * In this case the object should still appear once in the change set.
     */
    public List getAddOverFlow() {
        if (addOverFlow == null) {
            addOverFlow = new ArrayList();
        }
        return addOverFlow;
    }

   /**
     * PUBLIC:
     * This method returns the IdentityHashtable that contains the removed values from the collection
     * and their corresponding ChangeSets.
     */
    public IdentityHashtable getRemoveObjectList() {
        if (removeObjectList == null) {
            removeObjectList = new IdentityHashtable(10);
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
                    (this.orderedRemoveObjects == null || this.orderedRemoveObjects.isEmpty()))) 
                || getOwner().isNew();
    }

    /**
     * This method will be used to merge one record into another.
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        Enumeration addEnum = ((CollectionChangeRecord)mergeFromRecord).getAddObjectList().keys();
        while (addEnum.hasMoreElements()) {
            ObjectChangeSet mergingObject = (ObjectChangeSet)addEnum.nextElement();
            ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(mergingObject, mergeFromChangeSet);
            if (getRemoveObjectList().containsKey(localChangeSet)) {
                getRemoveObjectList().remove(localChangeSet);
            } else {
                getAddObjectList().put(localChangeSet, localChangeSet);
            }
        }
        Enumeration removeEnum = ((CollectionChangeRecord)mergeFromRecord).getRemoveObjectList().keys();
        while (removeEnum.hasMoreElements()) {
            ObjectChangeSet mergingObject = (ObjectChangeSet)removeEnum.nextElement();
            ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet(mergingObject, mergeFromChangeSet);
            if (getAddObjectList().containsKey(localChangeSet)) {
                getAddObjectList().remove(localChangeSet);
            } else {
                getRemoveObjectList().put(localChangeSet, localChangeSet);
            }
        }
    }

    /**
     * Ensure this change record is ready to by sent remotely for cache synchronization
     * In general, this means setting the CacheSynchronizationType on any ObjectChangeSets
     * associated with this ChangeRecord.
     */
    public void prepareForSynchronization(AbstractSession session) {
        Enumeration changes = getAddObjectList().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (changedObject.getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                changedObject.setSynchronizationType(syncType);
                changedObject.prepareChangeRecordsForSynchronization(session);
            }
        }
        changes = getRemoveObjectList().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (changedObject.getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                changedObject.setSynchronizationType(syncType);
                changedObject.prepareChangeRecordsForSynchronization(session);
            }
        }
        changes = getOrderedAddObjects().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (changedObject.getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                changedObject.setSynchronizationType(syncType);
                changedObject.prepareChangeRecordsForSynchronization(session);
            }
        }
        changes = getOrderedRemoveObjects().elements();
        while (changes.hasMoreElements()) {
            ObjectChangeSet changedObject = (ObjectChangeSet)changes.nextElement();
            if (changedObject.getSynchronizationType() == ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
                ClassDescriptor descriptor = session.getDescriptor(changedObject.getClassType(session));
                int syncType = descriptor.getCacheSynchronizationType();
                changedObject.setSynchronizationType(syncType);
                changedObject.prepareChangeRecordsForSynchronization(session);
            }
        }
    }

    /**
     * Sets the Added objects list.
     */
    public void setAddObjectList(IdentityHashtable objectChangesList) {
        this.addObjectList = objectChangesList;
    }

    /**
     * Sets the removed objects list.
     */
    public void setRemoveObjectList(IdentityHashtable objectChangesList) {
        this.removeObjectList = objectChangesList;
    }

    /**
     * This method will be used to update the objectsChangeSets references.
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        IdentityHashtable addList = new IdentityHashtable(this.getAddObjectList().size() + 1);
        IdentityHashtable removeList = new IdentityHashtable(this.getRemoveObjectList().size() + 1);
        // If we have ordered lists we need to iterate through those.
        if (getOrderedAddObjects().size() > 0 || getOrderedRemoveObjectIndices().size() > 0) {
            // Do the ordered adds first ...
            Vector orderedAddList = new Vector(getOrderedAddObjects().size());
            IdentityHashtable orderedAddListIndices = new IdentityHashtable(getOrderedAddObjectIndices().size());
            
            for (int i = 0; i < getOrderedAddObjects().size(); i++) {
                ObjectChangeSet changeSet = (ObjectChangeSet) getOrderedAddObjects().elementAt(i);
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
            Hashtable orderedRemoveList = new Hashtable(getOrderedRemoveObjects().size());
            Enumeration changes = getOrderedRemoveObjects().keys();
            
            while (changes.hasMoreElements()) {
                Object index = changes.nextElement();
                ObjectChangeSet changeSet = (ObjectChangeSet) getOrderedRemoveObjects().get(index);
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
            Enumeration changes = getAddObjectList().elements();
            while (changes.hasMoreElements()) {
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)changes.nextElement(), mergeFromChangeSet);
                addList.put(localChangeSet, localChangeSet);
            }
        
            changes = getRemoveObjectList().elements();
            while (changes.hasMoreElements()) {
                ObjectChangeSet localChangeSet = mergeToChangeSet.findOrIntegrateObjectChangeSet((ObjectChangeSet)changes.nextElement(), mergeFromChangeSet);
                removeList.put(localChangeSet, localChangeSet);
            }
        }
        
        setAddObjectList(addList);
        setRemoveObjectList(removeList);
    }

    /**
     * This method used by SDK mapping that only supports Collection type not IdentityHashtable.
     * This method is mapped in org.eclipse.persistence.internal.sessions.coordination.CommandProject.
     */
    public Vector getAddObjectsForSDK() {
        if (sdkAddObjects == null) {
            sdkAddObjects = new Vector();

            for (Enumeration enumtr = this.getAddObjectList().keys(); enumtr.hasMoreElements();) {
                sdkAddObjects.add(enumtr.nextElement());
            }
        }
        return sdkAddObjects;
    }

    /**
     * This method used by SDK mapping that only supports Collection type not IdentityHashtable.
     * This method is mapped in org.eclipse.persistence.internal.sessions.coordination.CommandProject.
     */
    public void setAddObjectsForSDK(Vector addObjects) {
        sdkAddObjects = addObjects;

        // build the equivalent addObjectList
        IdentityHashtable newList = new IdentityHashtable();
        for (int i = 0; i < sdkAddObjects.size(); i++) {
            Object change = sdkAddObjects.elementAt(i);
            newList.put(change, change);
        }
        this.setAddObjectList(newList);
    }

    /**
     * This method used by SDK mapping that only supports Collection type not IdentityHashtable.
     * This method is mapped in org.eclipse.persistence.internal.sessions.coordination.CommandProject.
     */
    public Vector getRemoveObjectsForSDK() {
        if (sdkRemoveObjects == null) {
            sdkRemoveObjects = new Vector();

            for (Enumeration enumtr = this.getRemoveObjectList().keys(); enumtr.hasMoreElements();) {
                sdkRemoveObjects.add(enumtr.nextElement());
            }
        }
        return sdkRemoveObjects;
    }

    /**
     * This method used by SDK mapping that only supports Collection type not IdentityHashtable.
     * This method is mapped in org.eclipse.persistence.internal.sessions.coordination.CommandProject.
     */
    public void setRemoveObjectsForSDK(Vector removeObjects) {
        sdkRemoveObjects = removeObjects;

        // build the equivalent removeObjectList
        IdentityHashtable newList = new IdentityHashtable();
        for (int i = 0; i < sdkRemoveObjects.size(); i++) {
            Object change = sdkRemoveObjects.elementAt(i);
            newList.put(change, change);
        }
        this.setRemoveObjectList(newList);
    }
    
    /**
     * This method returns the collection of ChangeSets in the order they were 
     * added to the collection. This list includes those objects that were 
     * moved within the collection.
     */
    public Vector getOrderedAddObjects() {
        if (orderedAddObjects == null) {
            orderedAddObjects = new Vector();
        }
        
        return orderedAddObjects;
    }
    
    /**
     * This method returns the index of an object added to the collection.
     */
    public Integer getOrderedAddObjectIndex(ObjectChangeSet changes) {
        return (Integer) getOrderedAddObjectIndices().get(changes);
    }
    
    /**
     * This method returns the collection of ChangeSets that they were 
     * added to the collection.
     */
    public IdentityHashtable getOrderedAddObjectIndices() {
        if (orderedAddObjectIndices == null) {
            orderedAddObjectIndices = new IdentityHashtable();
        }
        
        return orderedAddObjectIndices;
    }
    
    /**
     * This method returns the ordered list of indices to remove from the collection.
     */
    public Vector getOrderedRemoveObjectIndices() {
        if (orderedRemoveObjectIndices == null) {
            orderedRemoveObjectIndices = new Vector();
        }
        
        return orderedRemoveObjectIndices;
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
    public Hashtable getOrderedRemoveObjects() {
        if (orderedRemoveObjects == null) {
            orderedRemoveObjects = new Hashtable();
        }
        
        return orderedRemoveObjects;
    }
    
    /**
     * Sets collection of ChangeSets (and their respective index) that they 
     * were added to the collection.
     */
    public void setOrderedAddObjectIndices(IdentityHashtable orderedAddObjectIndices) {
        this.orderedAddObjectIndices = orderedAddObjectIndices;
    }
    
    /**
     * Sets collection of ChangeSets that they were added to the collection.
     */
    public void setOrderedAddObjects(Vector orderedAddObjects) {
        this.orderedAddObjects = orderedAddObjects;
    }
    
    /**
     * Sets collection of ChangeSets that they were remvoved from the collection.
     */
    public void setOrderedRemoveObjects(Hashtable orderedRemoveObjects) {
        this.orderedRemoveObjects = orderedRemoveObjects;
    }
}