/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.mappings;

import java.beans.PropertyChangeListener;

import java.util.*;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.internal.descriptors.changetracking.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
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
    protected transient boolean hasOrderBy;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public CollectionMapping() {
        this.selectionQuery = new ReadAllQuery();
        this.hasCustomDeleteAllQuery = false;
        this.containerPolicy = ContainerPolicy.buildDefaultPolicy();
        this.hasOrderBy = false;
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
     * Called from the EJBAnnotationsProcessor when an @OrderBy is found.
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
     * Provide order support for queryKeyName in ascending order.
     * Called from the EJBAnnotationsProcessor when an @OrderBy on an
     * aggregate is found.
     */
    public void addAggregateOrderBy(String aggregateName, String queryKeyName, boolean isDescending) {
        this.hasOrderBy = true;
        
        ReadAllQuery readAllQuery = (ReadAllQuery) getSelectionQuery();
        ExpressionBuilder builder = readAllQuery.getExpressionBuilder();
        Expression expression = builder.get(aggregateName).get(queryKeyName);
        
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
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        // Check for null
        if (attributeValue == null) {
            return getContainerPolicy().containerInstance(1);
        } else {
            return getContainerPolicy().cloneFor(attributeValue);
        }
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the objects, use the attribute value.
     */
    public Object buildCloneForPartObject(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        ContainerPolicy containerPolicy = getContainerPolicy();
        if (attributeValue == null) {
            Object container = containerPolicy.containerInstance(1);
            if ((this.getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy()) && ((clone != null) && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)) && (container instanceof CollectionChangeTracker)) {
                ((CollectionChangeTracker)container).setTrackedAttributeName(this.getAttributeName());
                ((CollectionChangeTracker)container)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
            }
            return container;
        }
        Object clonedAttributeValue = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));

        // I need to synchronize here to prevent the collection from changing while I am cloning it.
        // This will occur when I am merging into the cache and I am instantiating a UOW valueHolder at the same time
        // I can not synchronize around the clone, as this will cause deadlocks, so I will need to copy the collection then create the clones
        // I will use a temporary collection to help speed up the process
        Object temporaryCollection = null;
        synchronized (attributeValue) {
            temporaryCollection = containerPolicy.cloneFor(attributeValue);
        }
        for (Object valuesIterator = containerPolicy.iteratorFor(temporaryCollection); containerPolicy.hasNext(valuesIterator);) {
            Object cloneValue = buildElementClone(containerPolicy.next(valuesIterator, unitOfWork), unitOfWork, isExisting);
            // add the object to the uow list of private owned objects if it is a candidate and the uow should discover new objects
            if (!isExisting && isCandidateForPrivateOwnedRemoval() && unitOfWork.shouldDiscoverNewObjects() && cloneValue != null && unitOfWork.isObjectNew(cloneValue)) {
                unitOfWork.addPrivateOwnedObject(this, cloneValue);
            }
            containerPolicy.addInto(cloneValue, clonedAttributeValue, unitOfWork);
        }
        if ((this.getDescriptor().getObjectChangePolicy().isObjectChangeTrackingPolicy()) && ((clone != null) && (((ChangeTracker)clone)._persistence_getPropertyChangeListener() != null)) && (clonedAttributeValue instanceof CollectionChangeTracker)) {
            ((CollectionChangeTracker)clonedAttributeValue).setTrackedAttributeName(this.getAttributeName());
            ((CollectionChangeTracker)clonedAttributeValue)._persistence_setPropertyChangeListener(((ChangeTracker)clone)._persistence_getPropertyChangeListener());
        }
        return clonedAttributeValue;
    }

    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    public void buildCopy(Object copy, Object original, ObjectCopyingPolicy policy) {
        Object attributeValue = getRealCollectionAttributeValueFromObject(original, policy.getSession());
        Object valuesIterator = getContainerPolicy().iteratorFor(attributeValue);
        attributeValue = getContainerPolicy().containerInstance(getContainerPolicy().sizeFor(attributeValue));
        while (getContainerPolicy().hasNext(valuesIterator)) {
            Object originalValue = getContainerPolicy().next(valuesIterator, policy.getSession());
            Object copyValue = originalValue;
            if (policy.shouldCascadeAllParts() || (policy.shouldCascadePrivateParts() && isPrivateOwned())) {
                copyValue = policy.getSession().copyObject(originalValue, policy);
            } else {
                // Check for backrefs to copies.
                copyValue = policy.getCopies().get(originalValue);
                if (copyValue == null) {
                    copyValue = originalValue;
                }
            }
            getContainerPolicy().addInto(copyValue, attributeValue, policy.getSession());
        }
        setRealAttributeValueInObject(copy, attributeValue);
    }

    /**
     * INTERNAL:
     * Clone the element, if necessary.
     */
    protected Object buildElementClone(Object element, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        // optimize registration to knowledge of existence
        if (isExisting) {
            return unitOfWork.registerExistingObject(element);
        } else {// not known whether existing or not
            return unitOfWork.registerObject(element);
        }
    }

    /**
     * INTERNAL:
     * This method will access the target relationship and create a list of PKs of the target entities.
     * This method is used in combination with the CachedValueHolder to store references to PK's to be loaded
     * from a cache instead of a query.
     */
    public Object[] buildReferencesPKList(Object entity, Object attribute, AbstractSession session){
        ClassDescriptor referenceDescriptor = getReferenceDescriptor();
        Object collection = getIndirectionPolicy().getRealAttributeValueFromObject(entity, attribute);
        Object[] result = new Object[getContainerPolicy().sizeFor(collection)];
        Iterator iterator = (Iterator)getContainerPolicy().iteratorFor(collection);
        int index = 0;
        while(iterator.hasNext()){
            Object target = iterator.next();
            if (target != null){
                Vector pks = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(target, session);
                CMPPolicy policy = referenceDescriptor.getCMPPolicy();
                if (policy != null && policy.isCMP3Policy()){
                    result[index] = policy.createPrimaryKeyInstance(pks);
                }else{
                    result[index] = pks;
                }
                ++index;
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!this.isCascadeRemove())) {
            return;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if ((nextObject != null) && (!visitedObjects.containsKey(nextObject))) {
                visitedObjects.put(nextObject, nextObject);
                uow.performRemove(nextObject, visitedObjects);
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
            Object realObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
            ContainerPolicy cp = getContainerPolicy();
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
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow) {
        Object cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
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
                        uow.discoverAndPersistUnregisteredNewObjects(nextObject, cascade, newObjects, unregisteredExistingObjects, visitedObjects);
                    }
                }
            }
            return;
        }

        ContainerPolicy containerPolicy = getContainerPolicy();
        Object cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object iterator = containerPolicy.iteratorFor(cloneObjectCollection);
        boolean cascade = isCascadePersist();
        while (containerPolicy.hasNext(iterator)) {
            Object nextObject = containerPolicy.next(iterator, uow);
            // remove private owned object from uow list
            if (isCandidateForPrivateOwnedRemoval()) {
                uow.removePrivateOwnedObject(this, nextObject);
            }
            uow.discoverAndPersistUnregisteredNewObjects(nextObject, cascade, newObjects, unregisteredExistingObjects, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(object);
        if ((cloneAttribute == null) || (!this.isCascadePersist()) || (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return;
        }

        ContainerPolicy cp = getContainerPolicy();
        Object cloneObjectCollection = null;
        cloneObjectCollection = getRealCollectionAttributeValueFromObject(object, uow);
        Object cloneIter = cp.iteratorFor(cloneObjectCollection);
        // add private owned objects to uow list if mapping is a candidate and uow should discover new objects and the source object is new.
        boolean shouldAddPrivateOwnedObject = isCandidateForPrivateOwnedRemoval() && uow.shouldDiscoverNewObjects() && uow.isObjectNew(object); 
        while (cp.hasNext(cloneIter)) {
            Object nextObject = cp.next(cloneIter, uow);
            if (shouldAddPrivateOwnedObject && nextObject != null) {
                uow.addPrivateOwnedObject(this, nextObject);
            }
            uow.registerNewObjectForPersist(nextObject, visitedObjects);
        }
    }

    /**
     * INTERNAL:
     * Used by AttributeLevelChangeTracking to update a changeRecord with calculated changes
     * as apposed to detected changes.  If an attribute can not be change tracked it's
     * changes can be detected through this process.
     */
    public void calculateDeferredChanges(ChangeRecord changeRecord, AbstractSession session) {
        CollectionChangeRecord collectionRecord = (CollectionChangeRecord)changeRecord;
        // TODO: Handle events that fired after collection was replaced.
        compareCollectionsForChange(collectionRecord.getOriginalCollection(), collectionRecord.getLatestCollection(), collectionRecord, session);
    }

    /**
     * INTERNAL:
     * Cascade the merge to the component object, if appropriate.
     */
    public void cascadeMerge(Object sourceElement, MergeManager mergeManager) {
        if (shouldMergeCascadeParts(mergeManager)) {
            mergeManager.mergeChanges(mergeManager.getObjectToMerge(sourceElement), null);
        }
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy.
     */
    public Object clone() {
        CollectionMapping clone = (CollectionMapping)super.clone();
        clone.setDeleteAllQuery((ModifyQuery)getDeleteAllQuery().clone());
        return clone;
    }

    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     */
    public void compareCollectionsForChange(Object oldCollection, Object newCollection, ChangeRecord changeRecord, AbstractSession session) {
        getContainerPolicy().compareCollectionsForChange(oldCollection, newCollection, (CollectionChangeRecord) changeRecord, session, getReferenceDescriptor());
    }

    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two collections
     * @return prototype.changeset.ChangeRecord
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

        Object backUpObjectCollection = null;

        cloneAttribute = getAttributeValueFromObject(clone);

        if ((cloneAttribute != null) && (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return null;
        }

        if (!owner.isNew()) {// if the changeSet is for a new object then we must record all off the attributes
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
            cloneObjectCollection = getContainerPolicy().containerInstance(1);
        }

        CollectionChangeRecord changeRecord = new CollectionChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        compareCollectionsForChange(backUpObjectCollection, cloneObjectCollection, changeRecord, session);
        if (changeRecord.hasChanges()) {
            return changeRecord;
        }
        return null;
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstObjectCollection = getRealCollectionAttributeValueFromObject(firstObject, session);
        Object secondObjectCollection = getRealCollectionAttributeValueFromObject(secondObject, session);

        return super.compareObjects(firstObjectCollection, secondObjectCollection, session);
    }

    /**
     * INTERNAL:
     * The memory objects are compared and only the changes are written to the database
     */
    protected void compareObjectsAndWrite(Object previousObjects, Object currentObjects, WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        // If it is for an aggregate collection let it continue so that all of the correct values are deleted
        // and then re-added  This could be changed to make AggregateCollection changes smarter.
        if ((query.getObjectChangeSet() != null) && !this.isAggregateCollectionMapping()) {
            ObjectChangeSet changeSet = query.getObjectChangeSet();
            CollectionChangeRecord record = (CollectionChangeRecord)changeSet.getChangesForAttributeNamed(this.getAttributeName());
            if (record != null) {
                ObjectChangeSet removedChangeSet = null;
                ObjectChangeSet addedChangeSet = null;
                Iterator removedObjects = record.getRemoveObjectList().values().iterator();
                while (removedObjects.hasNext()) {
                    removedChangeSet = (ObjectChangeSet)removedObjects.next();
                    objectRemovedDuringUpdate(query, removedChangeSet.getUnitOfWorkClone());
                }
                Iterator addedObjects = record.getAddObjectList().values().iterator();
                while (addedObjects.hasNext()) {
                    addedChangeSet = (ObjectChangeSet)addedObjects.next();
                    objectAddedDuringUpdate(query, addedChangeSet.getUnitOfWorkClone(), addedChangeSet);
                }
            }
            return;
        }
        ContainerPolicy cp = getContainerPolicy();

        Hashtable previousObjectsByKey = new Hashtable(cp.sizeFor(previousObjects) + 2); // Read from db or from backup in uow.
        Hashtable currentObjectsByKey = new Hashtable(cp.sizeFor(currentObjects) + 2); // Current value of object's attribute (clone in uow).

        Map cacheKeysOfCurrentObjects = new IdentityHashMap(cp.sizeFor(currentObjects) + 1);

        // First index the current objects by their primary key.
        for (Object currentObjectsIter = cp.iteratorFor(currentObjects);
                 cp.hasNext(currentObjectsIter);) {
            Object currentObject = cp.next(currentObjectsIter, query.getSession());
            try {
                Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(currentObject, query.getSession());
                CacheKey key = new CacheKey(primaryKey);
                currentObjectsByKey.put(key, currentObject);
                cacheKeysOfCurrentObjects.put(currentObject, key);
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
            Object previousObject = cp.next(previousObjectsIter, query.getSession());
            Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(previousObject, query.getSession());
            CacheKey key = new CacheKey(primaryKey);
            previousObjectsByKey.put(key, previousObject);

            // Delete must occur first, in case object with same pk is removed and added,
            // (technically should not happen, but same applies to unique constraints)
            if (!currentObjectsByKey.containsKey(key)) {
                objectRemovedDuringUpdate(query, previousObject);
            }
        }

        for (Object currentObjectsIter = cp.iteratorFor(currentObjects);
                 cp.hasNext(currentObjectsIter);) {
            Object currentObject = cp.next(currentObjectsIter, query.getSession());
            try {
                CacheKey cacheKey = (CacheKey)cacheKeysOfCurrentObjects.get(currentObject);

                if (!(previousObjectsByKey.containsKey(cacheKey))) {
                    objectAddedDuringUpdate(query, currentObject, null);
                } else {
                    objectUnchangedDuringUpdate(query, currentObject, previousObjectsByKey, cacheKey);
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
     * Compare two objects if their parts are not private owned
     */
    protected boolean compareObjectsWithoutPrivateOwned(Object firstCollection, Object secondCollection, AbstractSession session) {
        ContainerPolicy cp = getContainerPolicy();
        if (cp.sizeFor(firstCollection) != cp.sizeFor(secondCollection)) {
            return false;
        }

        Object firstIter = cp.iteratorFor(firstCollection);
        Object secondIter = cp.iteratorFor(secondCollection);

        Vector keyValue = new Vector();

        while (cp.hasNext(secondIter)) {
            Object secondObject = cp.next(secondIter, session);
            Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
            keyValue.addElement(new CacheKey(primaryKey));
        }

        while (cp.hasNext(firstIter)) {
            Object firstObject = cp.next(firstIter, session);
            Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);

            if (!keyValue.contains(new CacheKey(primaryKey))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare two objects if their parts are private owned
     */
    protected boolean compareObjectsWithPrivateOwned(Object firstCollection, Object secondCollection, AbstractSession session) {
        ContainerPolicy cp = getContainerPolicy();
        if (cp.sizeFor(firstCollection) != cp.sizeFor(secondCollection)) {
            return false;
        }

        Object firstIter = cp.iteratorFor(firstCollection);
        Object secondIter = cp.iteratorFor(secondCollection);

        Hashtable keyValueToObject = new Hashtable(cp.sizeFor(firstCollection) + 2);
        CacheKey cacheKey;

        while (cp.hasNext(secondIter)) {
            Object secondObject = cp.next(secondIter, session);
            Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
            keyValueToObject.put(new CacheKey(primaryKey), secondObject);
        }

        while (cp.hasNext(firstIter)) {
            Object firstObject = cp.next(firstIter, session);
            Vector primaryKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);
            cacheKey = new CacheKey(primaryKey);

            if (keyValueToObject.containsKey(cacheKey)) {
                Object object = keyValueToObject.get(cacheKey);

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
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);
        containerPolicy.convertClassNamesToClasses(classLoader);
    }

    /**
     * INTERNAL:
     * We are not using a remote valueholder
     * so we need to replace the reference object(s) with
     * the corresponding object(s) from the remote session.
     */
    public void fixRealObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
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

        Object remoteAttributeValue = session.getObjectsCorrespondingToAll(attributeValue, objectDescriptors, processedObjects, tempQuery, getContainerPolicy());
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
     * return the object on the client corresponding to the specified object.
     * CollectionMappings have to worry about
     * maintaining object identity.
     */
    public Object getObjectCorrespondingTo(Object object, RemoteSession session, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query) {
        return session.getObjectsCorrespondingToAll(object, objectDescriptors, processedObjects, query, getContainerPolicy());
    }

    /**
     * INTERNAL:
     * Return the ordering query keys.
     * Used for Workbench integration.
     */
    public Vector getOrderByQueryKeyExpressions() {
        Vector expressions = new Vector();

        if ((getSelectionQuery() != null) && getSelectionQuery().isReadAllQuery()) {
            Enumeration orderExpressions = ((ReadAllQuery)getSelectionQuery()).getOrderByExpressions().elements();

            while (orderExpressions.hasMoreElements()) {
                Expression orderExpression = (Expression)orderExpressions.nextElement();

                if (orderExpression.isFunctionExpression() && ((FunctionExpression)orderExpression).getBaseExpression().isQueryKeyExpression()) {
                    expressions.add(orderExpression);
                }
            }
        }

        return expressions;
    }

    /**
     * Convenience method.
     * Return the value of an attribute, unwrapping value holders if necessary.
     * If the value is null, build a new container.
     */
    public Object getRealCollectionAttributeValueFromObject(Object object, AbstractSession session) throws DescriptorException {
        Object value = getRealAttributeValueFromObject(object, session);
        if (value == null) {
            value = getContainerPolicy().containerInstance(1);
        }
        return value;
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
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFields(collectFields());
        getContainerPolicy().prepare(getSelectionQuery(), session);

        // Check that the container policy is correct for the collection type.
        if ((!usesIndirection()) && (!getAttributeAccessor().getAttributeClass().isAssignableFrom(getContainerPolicy().getContainerClass()))) {
            throw DescriptorException.incorrectCollectionPolicy(this, getAttributeAccessor().getAttributeClass(), getContainerPolicy().getContainerClass());
        }
    }

    /**
     * INTERNAL:
     */
    public boolean isCollectionMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * The referenced object is checked if it is instantiated or not,
     * also check if it has been changed (as indirect collections avoid instantiation on add/remove.
     */
    public boolean isAttributeValueInstantiatedOrChanged(Object object) {
        return getIndirectionPolicy().objectIsInstantiatedOrChanged(getAttributeValueFromObject(object));
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
    public void iterateOnRealAttributeValue(DescriptorIterator iterator, Object realAttributeValue) {
        if (realAttributeValue == null) {
            return;
        }
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(realAttributeValue); cp.hasNext(iter);) {
            iterateOnElement(iterator, cp.next(iter, iterator.getSession()));
        }
    }

    /**
     * Return whether the reference objects must be deleted
     * one by one, as opposed to with a single DELETE statement.
     */
    protected boolean mustDeleteReferenceObjectsOneByOne() {
        ClassDescriptor referenceDescriptor = this.getReferenceDescriptor();
        return referenceDescriptor.hasDependencyOnParts() || referenceDescriptor.usesOptimisticLocking() || (referenceDescriptor.hasInheritance() && referenceDescriptor.getInheritancePolicy().shouldReadSubclasses()) || referenceDescriptor.hasMultipleTables();
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Because this is a collection mapping, values are added to or removed from the
     * collection based on the changeset
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord chgRecord, Object source, MergeManager mergeManager) {
        Object valueOfTarget = null;
        Object valueOfSource = null;
        AbstractSession parentSession = null;
        ContainerPolicy containerPolicy = getContainerPolicy();
        CollectionChangeRecord changeRecord = (CollectionChangeRecord) chgRecord;
        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)changeRecord.getOwner().getUOWChangeSet();

        // Collect the changes into a vector. Check to see if the target has an instantiated 
        // collection, if it does then iterate over the changes and merge the collections.
        if (isAttributeValueInstantiated(target)) {
            // If it is new will need a new collection.
            if (changeRecord.getOwner().isNew()) {
                valueOfTarget = containerPolicy.containerInstance(changeRecord.getAddObjectList().size());
            } else {
                valueOfTarget = getRealCollectionAttributeValueFromObject(target, mergeManager.getSession());
            }

            // Remove must happen before add to allow for changes in hash keys.
            // This is required to return the appropriate object from the parent when unwrapping.
            if (mergeManager.getSession().isUnitOfWork()) {
                parentSession = ((UnitOfWorkImpl)mergeManager.getSession()).getParent();
            } else {
                parentSession = mergeManager.getSession();
            }
            
            containerPolicy.mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts(mergeManager), mergeManager, parentSession);
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
                    mergeManager.mergeChanges(objectToMerge, changeSet);
                }

                // Let the mergemanager get it because I don't have the change for the object.
                // CR#2188 Problem with merging Collection mapping in unit of work and transparent indirection.
                containerPolicy.addInto(mergeManager.getTargetVersionOfSourceObject(objectToMerge), valueOfTarget, mergeManager.getSession());
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
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal() && (!isAttributeValueInstantiated(source))) {
                //this may be a batch valueholder built into a UOW make sure to reset the session before placing in shared cache
                setAttributeValueInObject(target, getIndirectionPolicy().getOriginalIndirectionObjectForMerge(getAttributeValueFromObject(source), mergeManager.getSession()));
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
        if (mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            if (!isAttributeValueInstantiated(target)) {
                // We must clone and set the value holder from the source to the target.
                Object attributeValue = getAttributeValueFromObject(source);
                Object clonedAttributeValue = getIndirectionPolicy().cloneAttribute(attributeValue, source, target, (UnitOfWorkImpl) mergeManager.getSession(), false); // building clone from an original not a row. 
                setAttributeValueInObject(target, clonedAttributeValue);
                
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
        ContainerPolicy containerPolicy = getContainerPolicy();
        // BUG#5190470 Must force instantiation of indirection collections.
        containerPolicy.sizeFor(valueOfTarget);
        boolean fireChangeEvents = false;
        if (!mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            // if we are copying from original to clone then the source will be     
            // instantiated anyway and we must continue to use the UnitOfWork 
            // valueholder in the case of transparent indirection
            Object newContainer = containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource));
            if ((this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) && (target instanceof ChangeTracker) && (((ChangeTracker)target)._persistence_getPropertyChangeListener() != null)) {
                // Avoid triggering events if we are dealing with the same list.
                // We rebuild the new container though since any cascade merge
                // activity such as lifecycle methods etc will be captured on
                // newly registered objects and not the clones and we need to
                // make sure the target has these updates once we are done.
                fireChangeEvents = valueOfSource != valueOfTarget;
                // Collections may not be indirect list or may have been replaced with user collection.
                Object iterator = containerPolicy.iteratorFor(valueOfTarget);
                PropertyChangeListener listener = ((ChangeTracker)target)._persistence_getPropertyChangeListener();
                if (fireChangeEvents) {
                    while (containerPolicy.hasNext(iterator)) {
                        ((ObjectChangeListener)listener).internalPropertyChange(new CollectionChangeEvent(target, getAttributeName(), valueOfTarget, containerPolicy.next(iterator, mergeSession), CollectionChangeEvent.REMOVE));// make the remove change event fire.
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
            //bug 3953038 - set a new collection in the object until merge completes, this
            //              prevents rel-maint. from adding duplicates.
            setRealAttributeValueInObject(target, containerPolicy.containerInstance(containerPolicy.sizeFor(valueOfSource)));
            containerPolicy.clear(valueOfTarget);
        }

        synchronized (valueOfSource) {
            Object sourceIterator = containerPolicy.iteratorFor(valueOfSource);
            while (containerPolicy.hasNext(sourceIterator)) {
                Object object = containerPolicy.next(sourceIterator, mergeManager.getSession());
                if (object == null) {
                    continue;// skip the null
                }
                if (shouldMergeCascadeParts(mergeManager)) {
                    if ((mergeManager.getSession().isUnitOfWork()) && (((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet() != null)) {
                        // If it is a unit of work, we have to check if I have a change Set for this object
                        mergeManager.mergeChanges(mergeManager.getObjectToMerge(object), (ObjectChangeSet)((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet().getObjectChangeSetForClone(object));
                    } else {
                        mergeManager.mergeChanges(mergeManager.getObjectToMerge(object), null);
                    }
                }
                object = this.referenceDescriptor.getObjectBuilder().wrapObject(mergeManager.getTargetVersionOfSourceObject(object), mergeManager.getSession());
                synchronized (valueOfTarget) {
                    if (fireChangeEvents) {
                        //Collections may not be indirect list or may have been replaced with user collection.
                        ((ObjectChangeListener)((ChangeTracker)target)._persistence_getPropertyChangeListener()).internalPropertyChange(new CollectionChangeEvent(target, getAttributeName(), valueOfTarget, object, CollectionChangeEvent.ADD));// make the add change event fire.
                    }
                    containerPolicy.addInto(object, valueOfTarget, mergeManager.getSession());
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
        }

        // Must re-set variable to allow for set method to re-morph changes if the collection is not being stored directly.
        setRealAttributeValueInObject(target, valueOfTarget);
    }

    /**
     * INTERNAL:
     * An object was added to the collection during an update, insert it if private.
     */
    protected void objectAddedDuringUpdate(ObjectLevelModifyQuery query, Object objectAdded, ObjectChangeSet changeSet) throws DatabaseException, OptimisticLockException {
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
            insertQuery.setObject(objectAdded);
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
            writeQuery.setObject(objectAdded);
            writeQuery.setObjectChangeSet(changeSet);
            writeQuery.setCascadePolicy(query.getCascadePolicy());
            query.getSession().executeQuery(writeQuery);
        }
    }

    /**
     * INTERNAL:
     * An object was removed to the collection during an update, delete it if private.
     */
    protected void objectRemovedDuringUpdate(ObjectLevelModifyQuery query, Object objectDeleted) throws DatabaseException, OptimisticLockException {
        if (isPrivateOwned()) {// Must check ownership for uow and cascading.
            if (query.shouldCascadeOnlyDependentParts()) {
                // If the session is a unit of work
                if (query.getSession().isUnitOfWork()) {
                    // ...and the object has not been explicitly deleted in the unit of work
                    if (!(((UnitOfWorkImpl)query.getSession()).getDeletedObjects().containsKey(objectDeleted))) {
                        query.getSession().getCommitManager().addObjectToDelete(objectDeleted);
                    }
                } else {
                    query.getSession().getCommitManager().addObjectToDelete(objectDeleted);
                }
            } else {
                query.getSession().deleteObject(objectDeleted);
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
     * copies the non primary key information into the row currently used only in ManyToMany
     */
    protected void prepareTranslationRow(AbstractRecord translationRow, Object object, AbstractSession session) {
        //Do nothing for the generic Collection Mapping
    }

    /**
     * INTERNAL:
     * An object is still in the collection, update it as it may have changed.
     */
    protected void objectUnchangedDuringUpdate(ObjectLevelModifyQuery query, Object object, Hashtable backupclones, CacheKey keys) throws DatabaseException, OptimisticLockException {
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
            prepareTranslationRow(modifyQuery.getTranslationRow(), modifyQuery.getObject(), modifyQuery.getSession());
            return modifyQuery.getSession().executeQuery(getSelectionQuery(), modifyQuery.getTranslationRow());
        }
    }

    /**
     * INTERNAL:
     * replace the value holders in the specified reference object(s)
     */
    public Map replaceValueHoldersIn(Object object, RemoteSessionController controller) {
        return controller.replaceValueHoldersInAll(object, getContainerPolicy());
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
        getContainerPolicy().recordAddToCollectionInChangeRecord((ObjectChangeSet)changeSetToAdd, collectionChangeRecord);
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
        getContainerPolicy().recordRemoveFromCollectionInChangeRecord((ObjectChangeSet)changeSetToRemove, collectionChangeRecord);
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
    public void updateChangeRecord(Object clone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (collectionChangeRecord == null) {
            collectionChangeRecord = new CollectionChangeRecord(objectChangeSet);
            collectionChangeRecord.setAttribute(getAttributeName());
            collectionChangeRecord.setMapping(this);
            objectChangeSet.addChange(collectionChangeRecord);
        }
        if (collectionChangeRecord.getOriginalCollection() == null) {
            collectionChangeRecord.setOriginalCollection(oldValue);
        }
        collectionChangeRecord.setLatestCollection(newValue);
        collectionChangeRecord.setIsDeferred(true);
        
        objectChangeSet.deferredDetectionRequiredOn(getAttributeName());
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
            getContainerPolicy().recordUpdateToCollectionInChangeRecord(event, changeSetToAdd, collectionChangeRecord);
        }
    }
           
    /**
     * INTERNAL:
     * Set the change listener in the collection.
     * If the collection is not indirect it must be re-built.
     * This is used for resuming or flushing units of work.
     */
    public void setChangeListener(Object clone, PropertyChangeListener listener, UnitOfWorkImpl uow) {
        if (getIndirectionPolicy().usesTransparentIndirection() && isAttributeValueInstantiated(clone)) {
            Object attributeValue = getRealAttributeValueFromObject(clone, uow);
            if (!(attributeValue instanceof CollectionChangeTracker)) {
                Object container = attributeValue;
                ContainerPolicy containerPolicy = getContainerPolicy();
                if (attributeValue == null) {
                    container = containerPolicy.containerInstance(1);
                } else {
                    container = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));
                    for (Object iterator = containerPolicy.iteratorFor(attributeValue); containerPolicy.hasNext(iterator);) {
                        containerPolicy.addInto(containerPolicy.next(iterator, uow), container, uow);
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
    }

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Collection</code> interface.
     */
    public void useCollectionClass(Class concreteClass) {
        ContainerPolicy policy = ContainerPolicy.buildPolicyFor(concreteClass, hasOrderBy());
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
     * Not sure were this is used, MW?
     */
    public void useCollectionClassName(String concreteClassName) {
        setContainerPolicy(new CollectionContainerPolicy(concreteClassName));
    }

    /**
     * INTERNAL:
     * Not sure were this is used, MW?
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
        setSelectionQueryContainerPolicy(ContainerPolicy.buildPolicyFor(Vector.class));
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
    public void validateBeforeInitialization(AbstractSession session) throws DescriptorException {
        super.validateBeforeInitialization(session);

        getIndirectionPolicy().validateContainerPolicy(session.getIntegrityChecker());

        if (getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
            Class attributeType = ((InstanceVariableAttributeAccessor)getAttributeAccessor()).getAttributeType();
            getIndirectionPolicy().validateDeclaredAttributeTypeForCollection(attributeType, session.getIntegrityChecker());
        } else if (getAttributeAccessor().isMethodAttributeAccessor()) {
            Class returnType = ((MethodAttributeAccessor)getAttributeAccessor()).getGetMethodReturnType();
            getIndirectionPolicy().validateGetMethodReturnTypeForCollection(returnType, session.getIntegrityChecker());

            Class parameterType = ((MethodAttributeAccessor)getAttributeAccessor()).getSetMethodParameterType();
            getIndirectionPolicy().validateSetMethodParameterTypeForCollection(parameterType, session.getIntegrityChecker());
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

        if (isPrivateOwned()) {
            Object objects = getRealCollectionAttributeValueFromObject(object, session);

            ContainerPolicy containerPolicy = getContainerPolicy();
            for (Object iter = containerPolicy.iteratorFor(objects); containerPolicy.hasNext(iter);) {
                if (!session.verifyDelete(containerPolicy.next(iter, session))) {
                    return false;
                }
            }
        }

        AbstractRecord row = getDescriptor().getObjectBuilder().buildRowForTranslation(object, session);

        //cr 3819 added the line below to fix the translationtable to ensure that it
        // contains the required values
        prepareTranslationRow(row, object, session);
        Object value = session.executeQuery(getSelectionQuery(), row);

        return getContainerPolicy().isEmpty(value);
    }

    /**
     * INTERNAL:
     * Add a new value and its change set to the collection change record.  This is used by
     * attribute change tracking.
     */
    public void addToCollectionChangeRecord(Object newKey, Object newValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        if (newValue != null) {
            ClassDescriptor descriptor;

            //PERF: Use referenceDescriptor if it does not have inheritance
            if (!getReferenceDescriptor().hasInheritance()) {
                descriptor = getReferenceDescriptor();
            } else {
                descriptor = uow.getDescriptor(newValue);
            }
            newValue = descriptor.getObjectBuilder().unwrapObject(newValue, uow);
            ObjectChangeSet newSet = descriptor.getObjectBuilder().createObjectChangeSet(newValue, (UnitOfWorkChangeSet)objectChangeSet.getUOWChangeSet(), uow);
            simpleAddToCollectionChangeRecord(newKey, newSet, objectChangeSet, uow);
        }
    }

    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        return getIndirectionPolicy().usesTransparentIndirection();
    }

    /**
     * INTERNAL:
     * Remove a value and its change set from the collection change record.  This is used by
     * attribute change tracking.
     */
    public void removeFromCollectionChangeRecord(Object newKey, Object newValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        if (newValue != null) {
            ClassDescriptor descriptor;

            //PERF: Use referenceDescriptor if it does not have inheritance
            if (!getReferenceDescriptor().hasInheritance()) {
                descriptor = getReferenceDescriptor();
            } else {
                descriptor = uow.getDescriptor(newValue);
            }
            newValue = descriptor.getObjectBuilder().unwrapObject(newValue, uow);
            ObjectChangeSet newSet = descriptor.getObjectBuilder().createObjectChangeSet(newValue, (UnitOfWorkChangeSet)objectChangeSet.getUOWChangeSet(), uow);
            simpleRemoveFromCollectionChangeRecord(newKey, newSet, objectChangeSet, uow);
        }
    }

    /**
     * INTERNAL:
     * Directly build a change record without comparison
     */
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        cloneAttribute = getAttributeValueFromObject(clone);
        if ((cloneAttribute != null) && (!getIndirectionPolicy().objectIsInstantiated(cloneAttribute))) {
            return null;
        }

        // 2612538 - the default size of Map (32) is appropriate
        IdentityHashMap cloneKeyValues = new IdentityHashMap();
        ContainerPolicy cp = getContainerPolicy();
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
        changeRecord.addAdditionChange(cloneKeyValues, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
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
    public Object valueFromPKList(Object[] pks, AbstractSession session){
        ContainerPolicy cp = getContainerPolicy();
        Object result = cp.containerInstance();
        for (int index = 0; index < pks.length; ++index){
            Vector pk = null;
            if (getReferenceDescriptor().hasCMPPolicy()){
                pk = getReferenceDescriptor().getCMPPolicy().createPkVectorFromKey(pks[index], session);
            }else{
                pk = (Vector)pks[index];
            }
            ReadObjectQuery query = new ReadObjectQuery();
            query.setReferenceClass(getReferenceClass());
            query.setSelectionKey(pk);
            query.setIsExecutionClone(true);
            cp.addInto(session.executeQuery(query), result, session);
        }
        return result;
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * To get here the mapping's isJoiningSupported() should return true.
     */
    protected Object valueFromRowInternalWithJoin(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, AbstractSession executionSession) throws DatabaseException {

        Object value = getContainerPolicy().containerInstance();
        // Extract the primary key of the source object, to filter only the joined rows for that object.
        Vector sourceKey = getDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(row, executionSession);
        CacheKey sourceCacheKey = new CacheKey(sourceKey);
        // If the query was using joining, all of the result rows by primary key will have been computed.
        List rows = joinManager.getDataResultsByPrimaryKey().get(sourceCacheKey);
        // A nested query must be built to pass to the descriptor that looks like the real query execution would,
        // these should be cached on the query during prepare.
        ObjectLevelReadQuery nestedQuery = prepareNestedJoinQueryClone(row, rows, joinManager, sourceQuery, executionSession);
        
        // A set of target cache keys must be maintained to avoid duplicates from multiple 1-m joins.
        Set targetCacheKeys = new HashSet();
        // For each rows, extract the target row and build the target object and add to the collection.
        for (int index = 0; index < rows.size(); index++) {
            AbstractRecord sourceRow = (AbstractRecord)rows.get(index);
            AbstractRecord targetRow = sourceRow;            
            // The field for many objects may be in the row,
            // so build the subpartion of the row through the computed values in the query,
            // this also helps the field indexing match.
            targetRow = trimRowForJoin(targetRow, joinManager, executionSession);
            
            // Partial object queries must select the primary key of the source and related objects.
            // If the target joined rows in null (outerjoin) means an empty collection.
            Vector targetKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(targetRow, executionSession);
            if (targetKey == null) {
                // A null primary key means an empty collection returned as nulls from an outerjoin.
                return getIndirectionPolicy().valueFromRow(value);
            }
            
            CacheKey targetCacheKey = new CacheKey(targetKey);            
            // Only build/add the target object once, skip duplicates from multiple 1-m joins.
            if (!targetCacheKeys.contains(targetCacheKey)) {
                nestedQuery.setTranslationRow(targetRow);
                targetCacheKeys.add(targetCacheKey);
                Object targetObject = getReferenceDescriptor().getObjectBuilder().buildObject(nestedQuery, targetRow);
                nestedQuery.setTranslationRow(null);
                getContainerPolicy().addInto(targetObject, value, executionSession);
            }
        }
        return getIndirectionPolicy().valueFromRow(value);
    }
}
