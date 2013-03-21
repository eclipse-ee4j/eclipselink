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
 * Change record used by DirectMapMapping.
 * Only needs to track unique keys added/removed.
 */
public class DirectMapChangeRecord extends DeferrableChangeRecord {
    protected HashMap addObjectsList;
    protected HashMap removeObjectsList;

    public DirectMapChangeRecord() {
        super();
    }

    public DirectMapChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * Returns true if the change set has changes.
     */
    public boolean hasChanges() {
        return (!((this.addObjectsList == null || this.addObjectsList.isEmpty()) && ((this.removeObjectsList == null || this.removeObjectsList.isEmpty())))) || getOwner().isNew();
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
        Iterator addKeys = ((DirectMapChangeRecord)mergeFromRecord).getAddObjects().keySet().iterator();
        while (addKeys.hasNext()) {
            Object key = addKeys.next();

            if (!this.getAddObjects().containsKey(key)) {
                if (this.getRemoveObjects().containsKey(key)) {
                    this.getRemoveObjects().remove(key);
                } else {
                    this.getAddObjects().put(key, ((DirectMapChangeRecord)mergeFromRecord).getAddObjects().get(key));
                }
            }
        }

        Iterator removeKeys = ((DirectMapChangeRecord)mergeFromRecord).getRemoveObjects().keySet().iterator();
        while (removeKeys.hasNext()) {
            Object key = removeKeys.next();

            if (!this.getRemoveObjects().containsKey(key)) {
                if (this.getAddObjects().containsKey(key)) {
                    this.getAddObjects().remove(key);
                } else {
                    this.getRemoveObjects().put(key, ((DirectMapChangeRecord)mergeFromRecord).getRemoveObjects().get(key));
                }
            }
        }
    }

    /**
     * Adds the items that were added to the collection.
     */
    public void addAdditionChange(HashMap additions) {
        if (getAddObjects().size() == 0) {
            addObjectsList = additions;
            return;
        }

        for (Iterator i = additions.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            if (getAddObjects().containsKey(key)) {
                getAddObjects().put(key, additions.get(key));
            } else if (additions.get(key).equals(getAddObjects().get(key))) {
                getAddObjects().put(key, additions.get(key));
            }
        }
    }

    /**
     * Adds the items that were removed from the collection.
     */
    public void addRemoveChange(HashMap subtractions) {
        if (getRemoveObjects().size() == 0) {
            this.removeObjectsList = subtractions;
            return;
        }

        for (Iterator i = subtractions.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            if (!getRemoveObjects().containsKey(key)) {
                getRemoveObjects().put(key, subtractions.get(key));
            } else if (subtractions.get(key).equals(getRemoveObjects().get(key))) {
                getRemoveObjects().put(key, subtractions.get(key));
            }
        }
    }

    /**
     * Adds the items that were added to the collection.
     */
    public void addAdditionChange(Object key, Object value) {
        if ( getRemoveObjects().containsKey(key) ) { 
            if ( value.equals(getRemoveObjects().get(key)) ) {
                getRemoveObjects().remove(key);
            }else {
                getAddObjects().put(key, value);
            }
        } else {
            getAddObjects().put(key, value);
        }
    }

    /**
     * Adds the items that were removed from the collection.
     */
    public void addRemoveChange(Object key, Object value) {
        //if an entry already exists in the remove it must remain untill added
        // as it contains the original removal.
        if (getAddObjects().containsKey(key)) {
            getAddObjects().remove(key);
        } else if (!getRemoveObjects().containsKey(key)) {
            getRemoveObjects().put(key, value);
        }
    }
    
    /**
     * Sets the added items list.
     */
    public void setAddObjects(HashMap addObjects) {
        this.addObjectsList = addObjects;
    }

    /**
     * Returns the added items list.
     */
    public HashMap getAddObjects() {
        if (addObjectsList == null) {
            addObjectsList = new HashMap();
        }
        return addObjectsList;
    }

    /**
     * Sets the removed items list.
     */
    public void setRemoveObjects(HashMap removeObjects) {
        this.removeObjectsList = removeObjects;
    }

    /**
     * Returns the removed items list.
     */
    public HashMap getRemoveObjects() {
        if (removeObjectsList == null) {
            removeObjectsList = new HashMap();
        }
        
        return removeObjectsList;
    }

    /**
     * This method will be used to update the objectsChangeSets references.
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        // Nothing for this record type to do as it does not reference any changesets.
    }

   /**
    * Recreates the original state of the collection.
    */
  public void internalRecreateOriginalCollection(Object currentMap, AbstractSession session) {
      ContainerPolicy cp = this.mapping.getContainerPolicy();
       if(this.removeObjectsList != null) {
           Iterator it = this.removeObjectsList.entrySet().iterator();
           while(it.hasNext()) {
               Map.Entry entry = (Map.Entry)it.next();
               cp.addInto(entry.getKey(), entry.getValue(), currentMap, session);
           }
       }
       if(this.addObjectsList != null) {
           Iterator it = this.addObjectsList.entrySet().iterator();
           while(it.hasNext()) {
               Map.Entry entry = (Map.Entry)it.next();
               cp.removeFrom(entry.getKey(), entry.getValue(), currentMap, session);
           }
       }
   }

  /**
   * Clears info about added / removed objects set by change tracker.
   */
  public void clearChanges() {
      if(this.removeObjectsList != null) {
          this.removeObjectsList.clear();
      }
      if(this.addObjectsList != null) {
          this.addObjectsList.clear();
      }
  }
}
