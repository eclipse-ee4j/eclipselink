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

import java.util.*;

import org.eclipse.persistence.internal.queries.ContainerPolicy;

/**
 * <p>
 * <b>Purpose</b>: This class holds the record of the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: Collections must be compared to each other and added and removed objects must
 * be recorded separately.
 */
public class DirectCollectionChangeRecord extends DeferrableChangeRecord implements org.eclipse.persistence.sessions.changesets.DirectCollectionChangeRecord {
    protected HashMap addObjectMap;
    protected HashMap removeObjectMap;
    /**
     * Contains the number of objects that must be inserted to once the value is removed
     * in the database as a delete where value = "value" will remove all instances
     * of that value in the database not just one.
     */
    protected HashMap commitAddMap;
    
    /**
     * Used only in case listOrderField != null in the mapping.
     * Maps each object which has been added or removed or which order in the list has changed
     * to an array of two (non-intersecting) sets of indexes - old and new.
     */
    protected Map changedIndexes;
    protected int oldSize;
    protected int newSize;
    
    protected boolean isFirstToAddAlreadyInCollection;
    protected boolean isFirstToRemoveAlreadyOutCollection;
    
    protected boolean isFirstToAdd = true;
    protected boolean isFirstToRemove = true;

    /**
     * Indicates whether IndirectList's order has been repaired.
     */
    protected boolean orderHasBeenRepaired;
    
    /**
     * This default constructor.
     */
    public DirectCollectionChangeRecord() {
        super();
    }

    /**
     * This constructor returns a changeRecord representing the DirectCollection mapping.
     */
    public DirectCollectionChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * This method takes a map of primitive objects and adds them to the add list.
     * The map stores the number of times the object is in the list.
     */
    public void addAdditionChange(HashMap additions, HashMap databaseCount) {
        Iterator enumtr = additions.keySet().iterator();
        while (enumtr.hasNext()) {
            Object object = enumtr.next();
            if (databaseCount.containsKey(object)){
                getCommitAddMap().put(object, databaseCount.get(object));
            }
            addAdditionChange(object, (Integer)additions.get(object));
        }
    }

    /**
     * This method takes a single addition value and records it.
     */
    public void addAdditionChange(Object key, Integer count){
        if (getRemoveObjectMap().containsKey(key)) {
            int removeValue = ((Integer)getRemoveObjectMap().get(key)).intValue();
            int addition = count.intValue();
            int result = removeValue - addition;
            if (result > 0 ) { // more removes still
                getRemoveObjectMap().put(key, Integer.valueOf(result));
            } else if (result < 0) { // more adds now
                getRemoveObjectMap().remove(key);
                getAddObjectMap().put(key, Integer.valueOf(Math.abs(result)));
            } else { // equal
                getRemoveObjectMap().remove(key);
            }
        } else {
            if (this.getAddObjectMap().containsKey(key)) {
                int addValue = ((Integer)this.getAddObjectMap().get(key)).intValue();
                addValue += count.intValue();
                this.getAddObjectMap().put(key, Integer.valueOf(addValue));
            } else {
                this.getAddObjectMap().put(key, count);
            }
        }
        if(this.isFirstToAdd) {
            this.isFirstToAdd = false;
            if(this.isFirstToAddAlreadyInCollection) {
                return;
            }
        }
        // this is an attribute change track add keep count
        int addValue = count.intValue();
        int commitValue = 0;
        if (getCommitAddMap().containsKey(key)) {
            commitValue = ((Integer)getCommitAddMap().get(key)).intValue();
        }
        getCommitAddMap().put(key, Integer.valueOf(addValue + commitValue));
    }
    
    /**
     * This method takes a hashtable of primitive objects and adds them to the remove list.
     * Each reference in the hashtable lists the number of this object that needs to be removed from the
     * collection.
     */
    public void addRemoveChange(HashMap additions, HashMap databaseCount) {
        Iterator enumtr = additions.keySet().iterator();
        while (enumtr.hasNext()) {
            Object object = enumtr.next();
            if (databaseCount.containsKey(object)){
                getCommitAddMap().put(object, databaseCount.get(object));
            }
            addRemoveChange(object, (Integer)additions.get(object));
        }
    }
    
    /**
     * This method takes a single remove change and integrates it with this changeset.
     */
    public void addRemoveChange(Object key, Integer count){
        if (getAddObjectMap().containsKey(key)) {
            int removeValue = ((Integer)getAddObjectMap().get(key)).intValue();
            int addition = count.intValue();
            int result = removeValue - addition;
            if (result > 0 ) { // more removes still
                getAddObjectMap().put(key, Integer.valueOf(result));
            } else if (result < 0) { // more adds now
                getAddObjectMap().remove(key);
                getRemoveObjectMap().put(key, Integer.valueOf(Math.abs(result)));
            } else { // equal
                getAddObjectMap().remove(key);
            }
        } else {
            if (this.getRemoveObjectMap().containsKey(key)){
                int addValue = ((Integer)this.getRemoveObjectMap().get(key)).intValue();
                addValue += count.intValue();
                this.getRemoveObjectMap().put(key, Integer.valueOf(addValue));
            } else {
                this.getRemoveObjectMap().put(key, count);
            }
        }
        if(this.isFirstToRemove) {
            this.isFirstToRemove = false;
            if(this.isFirstToRemoveAlreadyOutCollection) {
                return;
            }
        }   
        int removeValue = count.intValue();
        int commitValue = 0;
        if (getCommitAddMap().containsKey(key)){
            commitValue = ((Integer)getCommitAddMap().get(key)).intValue();
        }
        getCommitAddMap().put(key, Integer.valueOf(commitValue - removeValue));
    }

    /**
     * This method takes a hashtable of primitives and adds them to the commit list.
     * This count value provided is the number of instances that will need to be
     * inserted into the database once a remove has occurred.  This is only set
     * once for each object type.
     */
    public void setCommitAddition(Hashtable additions){
        Enumeration enumtr = additions.keys();
        while (enumtr.hasMoreElements()) {
            Object object = enumtr.nextElement();
            getCommitAddMap().put(object, additions.get(object));
        }
    }

    /**
     * This method will iterate over the collection and store the database counts for
     * the objects within the collection, this is used for minimal updates.
     */
    public void storeDatabaseCounts(Object collection, ContainerPolicy containerPolicy, AbstractSession session){
        Object iterator = containerPolicy.iteratorFor(collection);
        while (containerPolicy.hasNext(iterator)) {
            Object object = containerPolicy.next(iterator, session);
            incrementDatabaseCount(object);
        }
    }

    /**
     * Increment the count for object
     */
    public void incrementDatabaseCount(Object object){
        if (getCommitAddMap().containsKey(object)) {
            int count = ((Integer)getCommitAddMap().get(object)).intValue();
            getCommitAddMap().put(object, Integer.valueOf(++count));
        } else {
            getCommitAddMap().put(object, Integer.valueOf(1));
        }
    }

    /**
     * Decrement the count for object
     */
    public void decrementDatabaseCount(Object object){
        if (getCommitAddMap().containsKey(object)) {
            int count = ((Integer)getCommitAddMap().get(object)).intValue();
            if(count > 1) {
                getCommitAddMap().put(object, Integer.valueOf(--count));
            } else {
                getCommitAddMap().remove(object);
            }
        }
    }

    /**
     * ADVANCED:
     * This method returns the list of added objects.
     */
    public Vector getAddObjectList(){
        Vector vector = new Vector();
        for (Iterator iterator = getAddObjectMap().keySet().iterator(); iterator.hasNext();){
            Object object = iterator.next();
            int count = ((Integer)getAddObjectMap().get(object)).intValue();
            while (count > 0){
                vector.add(object);
                --count;
            }
        }
        return vector;
    }

    /**
     * This method returns the collection of objects that were added to the collection.
     */
    public HashMap getAddObjectMap() {
        if (this.addObjectMap == null) {
            this.addObjectMap = new HashMap(1);
        }
        return addObjectMap;
    }

    /**
     * This method returns the collection of objects that were added to the collection.
     */
    public HashMap getCommitAddMap() {
        if (this.commitAddMap == null) {
            this.commitAddMap = new HashMap(1);
        }
        return commitAddMap;
    }

    /**
     * ADVANCED:
     * This method returns the list of removed objects.
     */
    public Vector getRemoveObjectList(){
        Vector vector = new Vector();
        for (Iterator iterator = getRemoveObjectMap().keySet().iterator(); iterator.hasNext();){
            Object object = iterator.next();
            int count = ((Integer)getRemoveObjectMap().get(object)).intValue();
            while (count > 0){
                vector.add(object);
                --count;
            }
        }
        return vector;
    }

    /**
     * This method returns the collection of objects that were removed from the collection.
     */
    public HashMap getRemoveObjectMap() {
        if (this.removeObjectMap == null) {
            removeObjectMap = new HashMap(1);
        }
        return removeObjectMap;
    }

    /**
     * Returns true if the change set has changes.
     */
    public boolean hasChanges() {
        return (!((this.addObjectMap == null || this.addObjectMap.isEmpty())
            && (this.removeObjectMap == null || this.removeObjectMap.isEmpty())
            && (this.changedIndexes == null || this.changedIndexes.isEmpty()))) || getOwner().isNew();
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
                this.isDeferred = true;
                this.originalCollection = ((DeferrableChangeRecord)mergeFromRecord).originalCollection;
                this.latestCollection = ((DeferrableChangeRecord)mergeFromRecord).latestCollection;
                return;
            }
        }
        HashMap addMapToMerge = ((DirectCollectionChangeRecord)mergeFromRecord).getAddObjectMap();
        HashMap removeMapToMerge = ((DirectCollectionChangeRecord)mergeFromRecord).getRemoveObjectMap();
        //merge additions
        for (Iterator iterator = addMapToMerge.keySet().iterator(); iterator.hasNext();){
            Object added = iterator.next();
            if (!((DirectCollectionChangeRecord)mergeFromRecord).getCommitAddMap().containsKey(added)){
                // we have not recorded a change of this type in this class before so  add it
                this.getCommitAddMap().put(added, ((DirectCollectionChangeRecord)mergeFromRecord).getCommitAddMap().get(added));
            }
            this.addAdditionChange(added, (Integer)addMapToMerge.get(added));
        }
        //merge removals
        for (Iterator iterator = removeMapToMerge.keySet().iterator(); iterator.hasNext();){
            Object removed = iterator.next();
            if (!((DirectCollectionChangeRecord)mergeFromRecord).getCommitAddMap().containsKey(removed)){
                // we have not recorded a change of this type in this class before so  add it
                this.getCommitAddMap().put(removed, ((DirectCollectionChangeRecord)mergeFromRecord).getCommitAddMap().get(removed));
            }
            this.addRemoveChange(removed, (Integer)removeMapToMerge.get(removed));
        }

        if(this.changedIndexes != null) {
            if(((DirectCollectionChangeRecord)mergeFromRecord).getChangedIndexes() != null) {
                Iterator<Map.Entry<Object, Set[]>> itEntries = ((DirectCollectionChangeRecord)mergeFromRecord).getChangedIndexes().entrySet().iterator();  
                while(itEntries.hasNext()) {
                    Map.Entry<Object, Set[]> entry = itEntries.next();
                    Object obj = entry.getValue();
                    Set[] indexes = entry.getValue();
                    if(this.changedIndexes.containsKey(obj)) {
                        // we assuming that these are two consecutive change records:
                        // oldIndexes[1] should be equal to newIndexes[0]
                        ((Set[])(this.changedIndexes.get(obj)))[1] = indexes[1];  
                    } else {
                        this.changedIndexes.put(obj, indexes);
                    }
                }
                this.newSize = ((DirectCollectionChangeRecord)mergeFromRecord).getNewSize();
            }
        } else {
            if(((DirectCollectionChangeRecord)mergeFromRecord).getChangedIndexes() != null) {
                this.changedIndexes = new HashMap(((DirectCollectionChangeRecord)mergeFromRecord).getChangedIndexes());
                this.oldSize = ((DirectCollectionChangeRecord)mergeFromRecord).getOldSize();
                this.newSize = ((DirectCollectionChangeRecord)mergeFromRecord).getNewSize();
            }
        }
   }

    /**
     * This method will be used to update the objectsChangeSets references
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        //nothing for this record type to do as it does not reference any changesets
    }
    
    public static class NULL {
        // This is a placeholder for null instances.
        public NULL(){
        }        
        public boolean equals(Object object){
            return object instanceof NULL;
        }        
    }
    
    public void setFirstToAddAlreadyInCollection(boolean flag) {
        this.isFirstToAddAlreadyInCollection = flag;
    }
    public boolean isFirstToAddAlreadyInCollection() {
        return this.isFirstToAddAlreadyInCollection;
    }

    public void setFirstToRemoveAlreadyOutCollection(boolean flag) {
        this.isFirstToRemoveAlreadyOutCollection = flag;
    }
    public boolean isFirstToRemoveAlreadyOutCollection() {
        return this.isFirstToRemoveAlreadyOutCollection;
    }
    
    public void setChangedIndexes(Map changedIndexes) {
        this.changedIndexes = changedIndexes;
    }
    public Map getChangedIndexes() {
        return this.changedIndexes;
    }
    public void setOldSize(int size) {
        this.oldSize = size;
    }
    public int getOldSize() {
        return this.oldSize;
    }
    public void setNewSize(int size) {
        this.newSize = size;
    }
    public int getNewSize() {
        return this.newSize;
    }

   /**
    * Recreates the original state of the collection.
    */
  public void internalRecreateOriginalCollection(Object currentCollection, AbstractSession session) {
      ContainerPolicy cp = this.mapping.getContainerPolicy();
       if(this.removeObjectMap != null) {
           Iterator it = this.removeObjectMap.entrySet().iterator();
           while(it.hasNext()) {
               Map.Entry entry = (Map.Entry)it.next();
               Object obj = entry.getKey();
               int n = (Integer)entry.getValue();
               for(int i=0; i < n; i++) {
                   cp.addInto(obj, currentCollection, session);
               }
           }
       }
       if(this.addObjectMap != null) {
           Iterator it = this.addObjectMap.entrySet().iterator();
           while(it.hasNext()) {
               Map.Entry entry = (Map.Entry)it.next();
               Object obj = entry.getKey();
               int n = (Integer)entry.getValue();
               for(int i=0; i < n; i++) {
                   cp.removeFrom(obj, currentCollection, session);
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
       if(this.removeObjectMap != null) {
           this.removeObjectMap.clear();
       }
       if(this.addObjectMap != null) {
           this.addObjectMap.clear();
       }
       if(this.addObjectMap != null) {
           this.addObjectMap.clear();
       }
       if(this.removeObjectMap != null) {
           this.removeObjectMap.clear();
       }
       if(this.commitAddMap != null) {
           this.commitAddMap.clear();
       }
   }
}
