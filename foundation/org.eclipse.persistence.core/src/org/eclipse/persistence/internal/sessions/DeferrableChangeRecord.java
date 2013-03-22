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

import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.internal.queries.ContainerPolicy;

/**
 * Abstract change record for collection type records that allow deferrable change detection.
 * Used for change tracking when user sets entire collection.
 */
public abstract class DeferrableChangeRecord extends ChangeRecord {

    /**
     * Used for change tracking when user sets entire collection.
     */
    protected transient Object originalCollection;

    /**
     * Used for change tracking when user sets entire collection.
     */
    protected transient Object latestCollection;

    /**
     * Defines if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    protected boolean isDeferred = false;
    
    public DeferrableChangeRecord() {
        super();
    }

    public DeferrableChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * Returns if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    public boolean isDeferred() {
        return isDeferred;
    }

    /**
     * Sets if this change should be calculated at commit time using the two collections.
     * This is used to handle collection replacement.
     */
    public void setIsDeferred(boolean isDeferred) {
        this.isDeferred = isDeferred;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the last collection that was set on the object.
     */
    public Object getLatestCollection() {
        return latestCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the original collection that was set on the object when it was cloned.
     */
    public Object getOriginalCollection() {
        return originalCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the last collection that was set on the object.
     */
    public void setLatestCollection(Object latestCollection) {
        this.latestCollection = latestCollection;
    }

    /**
     * Used for change tracking when user sets entire collection.
     * This is the original collection that was set on the object when it was cloned.
     */
    public void setOriginalCollection(Object originalCollection) {
        this.originalCollection = originalCollection;
    }
    
    /**
     * Recreates the original state of currentCollection.
     */
    abstract public void internalRecreateOriginalCollection(Object currentCollection, AbstractSession session);

    /**
     * Clears info about added / removed objects set by change tracker.
     * Called after the change info has been already used for creation of originalCollection.
     * Also called to make sure there is no change info before comparison for change is performed
     * (change info is still in the record after comparison for change is performed 
     *  and may cause wrong results when the second comparison for change performed on the same change record).
     */
    abstract public void clearChanges();
    
    /**
     * Recreates the original state of the collection.
     */
    public void recreateOriginalCollection(Object currentCollection, AbstractSession session) {
       if(currentCollection == null) {
           this.setOriginalCollection(null);
           return;
       }
       if(currentCollection instanceof IndirectCollection) {
           // to avoid raising event when we add/remove elements from this collection later in this method.
           setOriginalCollection(((IndirectCollection)currentCollection).getDelegateObject());
       } else {
           setOriginalCollection(currentCollection);
       }
       internalRecreateOriginalCollection(this.originalCollection, session);
       clearChanges();
    }
   
    /**
     * ADVANCED:
     * If the owning UnitOfWork has shouldChangeRecordKeepOldValue set to true,
     * then return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue() {
        if(this.originalCollection != null) {
            return this.originalCollection;
        } else {
            if(getOwner() != null) {
                Object obj = ((org.eclipse.persistence.internal.sessions.ObjectChangeSet)getOwner()).getUnitOfWorkClone();
                AbstractSession session = ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)getOwner().getUOWChangeSet()).getSession();
                if(obj != null && session != null) {
                    Object currentCollection = this.mapping.getAttributeValueFromObject(obj);
                    ContainerPolicy cp = this.mapping.getContainerPolicy();
                    Object cloneCurrentCollection = cp.containerInstance(cp.sizeFor(currentCollection)); 
                    for (Object valuesIterator = cp.iteratorFor(currentCollection); cp.hasNext(valuesIterator);) {
                        Object member = cp.next(valuesIterator, session);
                        cp.addInto(cp.keyFromIterator(valuesIterator), member, cloneCurrentCollection , session);
                    }
                    return getOldValue(cloneCurrentCollection, session);
                }
            }
            return null;
        }
    }
    
    public Object getOldValue(Object currentCollection, AbstractSession session) {
        if(currentCollection != null) {
            if(currentCollection  instanceof IndirectCollection) {
                currentCollection = ((IndirectCollection)currentCollection).getDelegateObject();
            }
            internalRecreateOriginalCollection(currentCollection, session);
        }
        return currentCollection;
    }    
}
