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
 package org.eclipse.persistence.mappings.structures;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.eis.EISCollectionChangeRecord;
import org.eclipse.persistence.eis.EISOrderedCollectionChangeRecord;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Helper class to consolidate all the heinous comparing
 * and merging code for the Array collection mappings.
 * @see ArrayCollectionMapping
 */

public class ArrayCollectionMappingHelper {
    /** The mapping that needs help comparing and merging. */
    private ArrayCollectionMapping mapping;
    private static Object XXX = new Object();// object used to marked cleared out slots when comparing

    /**
     * Constructor.
     */
    public ArrayCollectionMappingHelper(ArrayCollectionMapping mapping) {
        super();
        this.mapping = mapping;
    }
    
    /**
     * Convenience method.
     */
    private boolean mapKeyHasChanged(Object element, AbstractSession session) {
        return this.getMapping().mapKeyHasChanged(element, session);
    }
    
    /**
     * Convenience method.
     */
    private Object getRealCollectionAttributeValueFromObject(Object object, AbstractSession session) {
        return this.getMapping().getRealCollectionAttributeValueFromObject(object, session);
    }
    
    /**
     * Convenience method.
     */
    private Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession) {
        return this.getMapping().buildAddedElementFromChangeSet(changeSet, mergeManager, targetSession);
    }

    /**
     * Convenience method.
     */
    private Object buildChangeSet(Object element, ObjectChangeSet owner, AbstractSession session) {
        return this.getMapping().buildChangeSet(element, owner, session);
    }

    /**
     * Convenience method.
     */
    private Object buildElementFromElement(Object element, MergeManager mergeManager, AbstractSession targetSession) {
        return this.getMapping().buildElementFromElement(element, mergeManager, targetSession);
    }

    /**
     * Convenience method.
     */
    private Object buildRemovedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession) {
        return this.getMapping().buildRemovedElementFromChangeSet(changeSet, mergeManager, targetSession);
    }
    
    /**
     * Convenience method.
     * Check for null values before delegating to the mapping.
     */
    protected boolean compareElements(Object element1, Object element2, AbstractSession session) {
        if ((element1 == null) && (element2 == null)) {
            return true;
        }
        if ((element1 == null) || (element2 == null)) {
            return false;
        }
        if (element2 == XXX) {// if element2 was marked as cleared out, it is not a match
            return false;
        }
        return this.getMapping().compareElements(element1, element2, session);
    }

    /**
     * Convenience method.
     * Check for null values before delegating to the mapping.
     */
    protected boolean compareElementsForChange(Object element1, Object element2, AbstractSession session) {
        if ((element1 == null) && (element2 == null)) {
            return true;
        }
        if ((element1 == null) || (element2 == null)) {
            return false;
        }
        if (element2 == XXX) {// if element2 was marked as cleared out, it is not a match
            return false;
        }
        return this.getMapping().compareElementsForChange(element1, element2, session);
    }
    
    /**
     * INTERNAL:
     * Return the mapping.
     */
    public ArrayCollectionMapping getMapping() {
        return mapping;
    }

    /**
     * INTERNAL:
     * Build and return the change record that results
     * from comparing the two collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();
        Object cloneCollection = this.getRealCollectionAttributeValueFromObject(clone, session);

        Object backupCollection = null;
        if (owner.isNew()) {
            backupCollection = cp.containerInstance(1);
        } else {
            backupCollection = this.getRealCollectionAttributeValueFromObject(backup, session);
        }

        if (cp.hasOrder()) {
            return this.compareAttributeValuesForChangeWithOrder(cloneCollection, backupCollection, owner, session);
        } else {
            return this.compareAttributeValuesForChangeWithoutOrder(cloneCollection, backupCollection, owner, session);
        }
    }

    /**
     * Build and return the change record that results
     * from comparing the two collection attributes.
     * The order of the elements is significant.
     */
    private ChangeRecord compareAttributeValuesForChangeWithOrder(Object cloneCollection, Object backupCollection, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();

        Vector cloneVector = cp.vectorFor(cloneCollection, session);// convert it to a Vector so we can preserve the order and use indexes
        Vector backupVector = cp.vectorFor(backupCollection, session);// "clone" it so we can clear out the slots

        EISOrderedCollectionChangeRecord changeRecord = new EISOrderedCollectionChangeRecord(owner, this.getAttributeName(), this.getDatabaseMapping());

        for (int i = 0; i < cloneVector.size(); i++) {
            Object cloneElement = cloneVector.elementAt(i);
            boolean found = false;
            for (int j = 0; j < backupVector.size(); j++) {
                if (this.compareElementsForChange(cloneElement, backupVector.elementAt(j), session)) {
                    // the clone element was found in the backup collection
                    found = true;
                    backupVector.setElementAt(XXX, j);// clear out the matching backup element

                    changeRecord.addMovedChangeSet(this.buildChangeSet(cloneElement, owner, session), j, i);
                    break;// matching backup element found - skip the rest of them
                }
            }
            if (!found) {
                // the clone element was not found, so it must have been added
                changeRecord.addAddedChangeSet(this.buildChangeSet(cloneElement, owner, session), i);
            }
        }

        for (int i = 0; i < backupVector.size(); i++) {
            Object backupElement = backupVector.elementAt(i);
            if (backupElement != XXX) {
                // the backup element was not in the clone collection, so it must have been removed
                changeRecord.addRemovedChangeSet(this.buildChangeSet(backupElement, owner, session), i);
            }
        }

        if (changeRecord.hasChanges()) {
            return changeRecord;
        } else {
            return null;
        }
    }

    /**
     * Build and return the change record that results
     * from comparing the two collection attributes.
     * Ignore the order of the elements.
     */
    private ChangeRecord compareAttributeValuesForChangeWithoutOrder(Object cloneCollection, Object backupCollection, ObjectChangeSet owner, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();

        Vector backupVector = cp.vectorFor(backupCollection, session);// "clone" it so we can clear out the slots

        EISCollectionChangeRecord changeRecord = new EISCollectionChangeRecord(owner, this.getAttributeName(), this.getDatabaseMapping());
        for (Object cloneIter = cp.iteratorFor(cloneCollection); cp.hasNext(cloneIter);) {
            Object cloneElement = cp.next(cloneIter, session);

            boolean found = false;
            for (int i = 0; i < backupVector.size(); i++) {
                if (this.compareElementsForChange(cloneElement, backupVector.elementAt(i), session)) {
                    // the clone element was found in the backup collection
                    found = true;
                    backupVector.setElementAt(XXX, i);// clear out the matching backup element
                    if (this.mapKeyHasChanged(cloneElement, session)) {
                        changeRecord.addChangedMapKeyChangeSet(this.buildChangeSet(cloneElement, owner, session));
                    }
                    break;// matching backup element found - skip the rest of them
                }
            }
            if (!found) {
                // the clone element was not found, so it must have been added
                changeRecord.addAddedChangeSet(this.buildChangeSet(cloneElement, owner, session));
            }
        }

        for (int i = 0; i < backupVector.size(); i++) {
            Object backupElement = backupVector.elementAt(i);
            if (backupElement != XXX) {
                // the backup element was not in the clone collection, so it must have been removed
                changeRecord.addRemovedChangeSet(this.buildChangeSet(backupElement, owner, session));
            }
        }

        if (changeRecord.hasChanges()) {
            return changeRecord;
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        return this.compareAttributeValues(this.getRealCollectionAttributeValueFromObject(object1, session), this.getRealCollectionAttributeValueFromObject(object2, session), session);
    }
    
    /**
     * Compare the attributes. Return true if they are alike.
     * Assume the passed-in attributes are non-null.
     */
    private boolean compareAttributeValues(Object collection1, Object collection2, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();

        if (cp.sizeFor(collection1) != cp.sizeFor(collection2)) {
            return false;
        }

        // if they are both empty, go no further...
        if (cp.sizeFor(collection1) == 0) {
            return true;
        }

        if (cp.hasOrder()) {
            return this.compareAttributeValuesWithOrder(collection1, collection2, session);
        } else {
            return this.compareAttributeValuesWithoutOrder(collection1, collection2, session);
        }
    }
    
    /**
     * Compare the attributes. Return true if they are alike.
     * The order of the elements is significant.
     */
    private boolean compareAttributeValuesWithOrder(Object collection1, Object collection2, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();

        Object iter1 = cp.iteratorFor(collection1);
        Object iter2 = cp.iteratorFor(collection2);

        while (cp.hasNext(iter1)) {
            if (!this.compareElements(cp.next(iter1, session), cp.next(iter2, session), session)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare the attributes. Return true if they are alike.
     * Ignore the order of the elements.
     */
    private boolean compareAttributeValuesWithoutOrder(Object collection1, Object collection2, AbstractSession session) {
        ContainerPolicy cp = this.getContainerPolicy();

        Vector vector2 = cp.vectorFor(collection2, session);// "clone" it so we can clear out the slots

        for (Object iter1 = cp.iteratorFor(collection1); cp.hasNext(iter1);) {
            Object element1 = cp.next(iter1, session);

            boolean found = false;
            for (int i = 0; i < vector2.size(); i++) {
                if (this.compareElements(element1, vector2.elementAt(i), session)) {
                    found = true;
                    vector2.setElementAt(XXX, i);// clear out the matching element
                    break;// matching element found - skip the rest of them
                }
            }
            if (!found) {
                return false;
            }
        }

        // look for elements that were not in collection1
        for (Enumeration stream = vector2.elements(); stream.hasMoreElements();) {
            if (stream.nextElement() != XXX) {
                return false;
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        if (this.getContainerPolicy().hasOrder()) {
            this.mergeChangesIntoObjectWithOrder(target, changeRecord, source, mergeManager, targetSession);
        } else {
            this.mergeChangesIntoObjectWithoutOrder(target, changeRecord, source, mergeManager, targetSession);
        }
    }

    /**
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    private void mergeChangesIntoObjectWithOrder(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        ContainerPolicy cp = this.getContainerPolicy();
        AbstractSession session = mergeManager.getSession();

        Vector changes = ((EISOrderedCollectionChangeRecord)changeRecord).getNewCollection();
        Object targetCollection = cp.containerInstance(changes.size());

        for (Enumeration stream = changes.elements(); stream.hasMoreElements();) {
            Object targetElement = this.buildAddedElementFromChangeSet(stream.nextElement(), mergeManager, targetSession);
            cp.addInto(targetElement, targetCollection, session);
        }

        // reset the attribute to allow for set method to re-morph changes if the collection is not being stored directly
        this.setRealAttributeValueInObject(target, targetCollection);
    }

    /**
     * Merge changes from the source to the target object.
     * Make the necessary removals and adds and map key modifications.
     */
    private void mergeChangesIntoObjectWithoutOrder(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        EISCollectionChangeRecord sdkChangeRecord = (EISCollectionChangeRecord)changeRecord;
        ContainerPolicy cp = this.getContainerPolicy();
        AbstractSession session = mergeManager.getSession();

        Object targetCollection = null;
        if (sdkChangeRecord.getOwner().isNew()) {
            targetCollection = cp.containerInstance(sdkChangeRecord.getAdds().size());
        } else {
            targetCollection = this.getRealCollectionAttributeValueFromObject(target, session);
        }

        Vector removes = sdkChangeRecord.getRemoves();
        Vector adds = sdkChangeRecord.getAdds();
        Vector changedMapKeys = sdkChangeRecord.getChangedMapKeys();

        synchronized (targetCollection) {
            for (Enumeration stream = removes.elements(); stream.hasMoreElements();) {
                Object removeElement = this.buildRemovedElementFromChangeSet(stream.nextElement(), mergeManager, targetSession);

                Object targetElement = null;
                for (Object iter = cp.iteratorFor(targetCollection); cp.hasNext(iter);) {
                    targetElement = cp.next(iter, session);
                    if (this.compareElements(targetElement, removeElement, session)) {
                        break;// matching element found - skip the rest of them
                    }
                }
                if (targetElement != null) {
                    // a matching element was found, remove it
                    cp.removeFrom(targetElement, targetCollection, session);
                }
            }

            for (Enumeration stream = adds.elements(); stream.hasMoreElements();) {
                Object addElement = this.buildAddedElementFromChangeSet(stream.nextElement(), mergeManager, targetSession);
                cp.addInto(addElement, targetCollection, session);
            }

            for (Enumeration stream = changedMapKeys.elements(); stream.hasMoreElements();) {
                Object changedMapKeyElement = this.buildAddedElementFromChangeSet(stream.nextElement(), mergeManager, targetSession);
                Object originalElement = ((UnitOfWorkImpl)session).getOriginalVersionOfObject(changedMapKeyElement);
                cp.removeFrom(originalElement, targetCollection, session);
                cp.addInto(changedMapKeyElement, targetCollection, session);
            }
        }

        // reset the attribute to allow for set method to re-morph changes if the collection is not being stored directly
        this.setRealAttributeValueInObject(target, targetCollection);
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        ContainerPolicy cp = this.getContainerPolicy();
        AbstractSession session = mergeManager.getSession();

        Object sourceCollection = this.getRealCollectionAttributeValueFromObject(source, session);
        Object targetCollection = cp.containerInstance(cp.sizeFor(sourceCollection));

        for (Object iter = cp.iteratorFor(sourceCollection); cp.hasNext(iter);) {
            Object targetElement = this.buildElementFromElement(cp.next(iter, session), mergeManager, targetSession);
            cp.addInto(targetElement, targetCollection, session);
        }

        // reset the attribute to allow for set method to re-morph changes if the collection is not being stored directly
        this.setRealAttributeValueInObject(target, targetCollection);
    }
    
    /**
     * ADVANCED:
     * This method is used to add an object to a collection once the changeSet is applied.
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        if (this.getContainerPolicy().hasOrder()) {
            this.simpleAddToCollectionChangeRecordWithOrder(referenceKey, changeSetToAdd, changeSet, session);
        } else {
            this.simpleAddToCollectionChangeRecordWithoutOrder(referenceKey, changeSetToAdd, changeSet, session);
        }
    }

    /**
     * Add stuff to an ordered collection.
     */
    private void simpleAddToCollectionChangeRecordWithOrder(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        EISOrderedCollectionChangeRecord changeRecord = (EISOrderedCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            changeRecord = new EISOrderedCollectionChangeRecord(changeSet, this.getAttributeName(), this.getDatabaseMapping());
            changeSet.addChange(changeRecord);
        }
        changeRecord.simpleAddChangeSet(changeSetToAdd);
    }

    /**
     * Add stuff to an unordered collection.
     */
    private void simpleAddToCollectionChangeRecordWithoutOrder(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        EISCollectionChangeRecord changeRecord = (EISCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            changeRecord = new EISCollectionChangeRecord(changeSet, this.getAttributeName(), this.getDatabaseMapping());
            changeSet.addChange(changeRecord);
        }
        changeRecord.simpleAddChangeSet(changeSetToAdd);
    }

    /**
     * ADVANCED:
     * This method is used to remove an object from a collection once the changeSet is applied.
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        if (this.getContainerPolicy().hasOrder()) {
            this.simpleRemoveFromCollectionChangeRecordWithOrder(referenceKey, changeSetToRemove, changeSet, session);
        } else {
            this.simpleRemoveFromCollectionChangeRecordWithoutOrder(referenceKey, changeSetToRemove, changeSet, session);
        }
    }

    /**
     * Remove stuff from an ordered collection.
     */
    private void simpleRemoveFromCollectionChangeRecordWithOrder(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        EISOrderedCollectionChangeRecord changeRecord = (EISOrderedCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            changeRecord = new EISOrderedCollectionChangeRecord(changeSet, this.getAttributeName(), this.getDatabaseMapping());
            changeSet.addChange(changeRecord);
        }
        changeRecord.simpleRemoveChangeSet(changeSetToRemove);
    }

    /**
     * Remove stuff from an unordered collection.
     */
    private void simpleRemoveFromCollectionChangeRecordWithoutOrder(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        EISCollectionChangeRecord changeRecord = (EISCollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            changeRecord = new EISCollectionChangeRecord(changeSet, this.getAttributeName(), this.getDatabaseMapping());
            changeSet.addChange(changeRecord);
        }
        changeRecord.simpleRemoveChangeSet(changeSetToRemove);
    }
    
    
    /**
     * Convenience method.
     */
    private void setRealAttributeValueInObject(Object object, Object attributeValue) {
        this.getMapping().setRealAttributeValueInObject(object, attributeValue);
    }
    
    /**
     * Convenience method.
     */
    private String getAttributeName() {
        return this.getMapping().getAttributeName();
    }

    /**
     * Convenience method.
     */
    private ContainerPolicy getContainerPolicy() {
        return this.getMapping().getContainerPolicy();
    }

    /**
     * INTERNAL:
     * Return the mapping, casted a bit more generally.
     */
    public DatabaseMapping getDatabaseMapping() {
        return (DatabaseMapping)this.getMapping();
    }
    


}
