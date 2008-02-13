/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectCollection;

/**
 * <p><b>Purpose</b>:
 * Used to support collections in read queries.
 * <p>
 * <p><b>Responsibilities</b>:
 * Map the results into the appropriate collection instance.
 * Generically support special collections like cursored stream and virtual collection.
 *
 * @author James Sutherland
 * @since TOPLink/Java 1.2
 */
public abstract class ContainerPolicy implements Cloneable, Serializable {

    /** The descriptor is used to wrap and unwrap objects using the wrapper policy. **/
    protected transient ClassDescriptor elementDescriptor;
    protected transient Constructor constructor;

    /**
     * Default constructor.
     */
    public ContainerPolicy() {
    }

    /**
     * INTERNAL:
     * Add element to container however that needs to be done for the type of container.
     * Valid for some subclasses only.
     * Return whether the container changed.
     */
    protected boolean addInto(Object key, Object element, Object container) {
        throw QueryException.cannotAddToContainer(element, container, this);
    }

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed
     */
    public boolean addInto(Object element, Object container, AbstractSession session) {
        return addInto(null, element, container, session);
    }

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of JDK 1.1 and 1.2.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed
     */
    public boolean addInto(Object key, Object element, Object container, AbstractSession session) {
        Object elementToAdd = element;
        // PERF: Using direct access.
        if (this.elementDescriptor != null) {
            elementToAdd = this.elementDescriptor.getObjectBuilder().wrapObject(element, session);
        }
        return addInto(key, elementToAdd, container);
    }

    /**
    * INTERNAL:
    * It is illegal to send this message to this receiver. Try one of my subclasses.
    * Throws an exception.
    *
    * @see #ListContainerPolicy
    */
    public void addIntoWithOrder(Integer index, Object element, Object container) {
        throw QueryException.methodDoesNotExistInContainerClass("set", getContainerClass());
    }

    /**
    * INTERNAL:
    * It is illegal to send this message to this receiver. Try one of my subclasses.
    * Throws an exception.
    *
    * @see #ListContainerPolicy
    */
    public void addIntoWithOrder(Integer index, Object element, Object container, AbstractSession session) {
        Object elementToAdd = element;
        // PERF: Using direct access.
        if (this.elementDescriptor != null) {
            elementToAdd = this.elementDescriptor.getObjectBuilder().wrapObject(element, session);
        }
        addIntoWithOrder(index, elementToAdd, container);
    }

    /**
    * INTERNAL:
    * It is illegal to send this message to this receiver. Try one of my subclasses.
    * Throws an exception.
    *
    * @see #ListContainerPolicy
    */
    public void addIntoWithOrder(Vector indexes, Hashtable elements, Object container, AbstractSession session) {
        throw QueryException.methodDoesNotExistInContainerClass("set", getContainerClass());
    }

    /**
     * INTERNAL:
     * Return a container populated with the contents of the specified Vector.
     */
    public Object buildContainerFromVector(Vector vector, AbstractSession session) {
        Object container = containerInstance(vector.size());
        int size = vector.size();
        for (int index = 0; index < size; index++) {
            addInto(vector.get(index), container, session);
        }
        return container;
    }

    /**
     * INTERNAL:
     * Return the appropriate container policy for the specified
     * concrete container class.
     */
    public static ContainerPolicy buildPolicyFor(Class concreteContainerClass) {
        return buildPolicyFor(concreteContainerClass, false);
    }
    
    /**
     * INTERNAL:
     * Return the appropriate container policy for the specified
     * concrete container class.
     */
    public static ContainerPolicy buildPolicyFor(Class concreteContainerClass, boolean hasOrdering) {
        if (Helper.classImplementsInterface(concreteContainerClass, ClassConstants.List_Class)) {
            if (hasOrdering) {
                return new OrderedListContainerPolicy(concreteContainerClass);
            } else {
                return new ListContainerPolicy(concreteContainerClass);
            }
        } else if (Helper.classImplementsInterface(concreteContainerClass, ClassConstants.SortedSet_Class)) {
            return new SortedCollectionContainerPolicy(concreteContainerClass);
        } else if (Helper.classImplementsInterface(concreteContainerClass, ClassConstants.Collection_Class)) {
            return new CollectionContainerPolicy(concreteContainerClass);
        } else if (Helper.classImplementsInterface(concreteContainerClass, ClassConstants.Map_Class)) {
            return new MapContainerPolicy(concreteContainerClass);
        } else if (concreteContainerClass.equals(ClassConstants.CursoredStream_Class)) {
            return new CursoredStreamPolicy();
        } else if (concreteContainerClass.equals(ClassConstants.ScrollableCursor_Class)) {
            return new ScrollableCursorPolicy();
        }

        throw ValidationException.illegalContainerClass(concreteContainerClass);
    }

    /**
     * INTERNAL:
     * Remove all the elements from the specified container.
     * Valid only for certain subclasses.
     */
    public void clear(Object container) {
        throw QueryException.methodNotValid(this, "clear(Object container)");
    }
    
    /**
     * INTERNAL:
     * Return if the policy is equal to the other.
     * By default if they are the same class, they are considered equal.
     * This is used for query parse caching.
     */
    public boolean equals(Object object) {
        return (object != null) && (getClass().equals(object.getClass()));
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public ContainerPolicy clone(ReadQuery query) {
        return (ContainerPolicy)clone();
    }

    /**
     * INTERNAL:
     * Return a clone of the specified container. Can only be called for select subclasses.
     */
    public Object cloneFor(Object container) {
        throw QueryException.cannotCreateClone(this, container);
    }

    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     */
    public void compareCollectionsForChange(Object oldCollection, Object newCollection, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor) {
        // 2612538 - the default size of Map (32) is appropriate
        IdentityHashMap originalKeyValues = new IdentityHashMap();
        IdentityHashMap cloneKeyValues = new IdentityHashMap();

        // Collect the values from the oldCollection.
        if (oldCollection != null) {
            Object backUpIter = iteratorFor(oldCollection);
            
            while (hasNext(backUpIter)) {
                Object secondObject = next(backUpIter, session);
    
                // CR2378 null check to prevent a null pointer exception - XC
                if (secondObject != null) {
                    originalKeyValues.put(secondObject, secondObject);
                }
            }
        }
        
        if (newCollection != null){
            // Collect the objects from the new Collection.
            Object cloneIter = iteratorFor(newCollection);
            
            while (hasNext(cloneIter)) {
                Object firstObject = next(cloneIter, session);
    
                // CR2378 null check to prevent a null pointer exception - XC
                // If value is null then nothing can be done with it.
                if (firstObject != null) {
                    if (originalKeyValues.containsKey(firstObject)) {
                        // There is an original in the cache
                        if ((compareKeys(firstObject, session))) {
                            // The keys have not changed
                            originalKeyValues.remove(firstObject);
                        } else {
                            // The keys have changed, create a changeSet 
                            // (it will be resused later) and set the old key 
                            // value to be used to remove.
                            Object backUpVersion = null;
    
                            // CR4172 compare the keys from the back up to the 
                            // clone not from the original to the clone.
                            if (((UnitOfWorkImpl)session).isClassReadOnly(firstObject.getClass())) {
                                backUpVersion = ((UnitOfWorkImpl)session).getOriginalVersionOfObject(firstObject);
                            } else {
                                backUpVersion = ((UnitOfWorkImpl)session).getBackupClone(firstObject);
                            }
                            
                            ObjectChangeSet changeSet = referenceDescriptor.getObjectBuilder().createObjectChangeSet(firstObject, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
                            changeSet.setOldKey(keyFrom(backUpVersion, session));
                            changeSet.setNewKey(keyFrom(firstObject, session));
                            cloneKeyValues.put(firstObject, firstObject);
                        }
                    } else {
                        // Place it in the add collection
                        cloneKeyValues.put(firstObject, firstObject);
                    }
                }
            }
        }

        changeRecord.addAdditionChange(cloneKeyValues, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addRemoveChange(originalKeyValues, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
    }
    
    /**
     * INTERNAL:
     * Return true if keys are the same in the source as the backup.  False otherwise
     * in the case of readonly compare against the original
     * For non map container policies return true always, because these policies have no concepts of Keys
     */
    public boolean compareKeys(Object sourceKey, AbstractSession session) {
        return true;
    }

    /**
     * INTERNAL:
     * Build a new container, add the contents of each of the specified containers
     * to it, and return it.
     * Both of the containers must use the same container policy (namely, this one).
     */
    public Object concatenateContainers(Object firstContainer, Object secondContainer) {
        Object container = containerInstance(sizeFor(firstContainer) + sizeFor(secondContainer));

        for (Object firstIter = iteratorFor(firstContainer); hasNext(firstIter);) {
            addInto(null, next(firstIter), container);
        }

        for (Object secondIter = iteratorFor(secondContainer); hasNext(secondIter);) {
            addInto(null, next(secondIter), container);
        }
        return container;
    }

    /**
     * INTERNAL:
     * Return an instance of the container class.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    public Object containerInstance() {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(getContainerClass()));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.newInstanceFromClass(getContainerClass());
            }
        } catch (Exception ex) {
            throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), ex);
        }
    }

    /**
     * INTERNAL:
     * Return an instance of the container class with the specified initial capacity.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    public Object containerInstance(int initialCapacity) {
        if (getConstructor() == null) {
            return containerInstance();
        }
        try {
            Object[] arguments = new Object[1];

            //Code change for 3732.  No longer need to add 1 as this was for JDK 1.1
            arguments[0] = new Integer(initialCapacity);
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(getConstructor(), arguments));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.invokeConstructor(getConstructor(), arguments);
            }
        } catch (Exception ex) {
            throw QueryException.couldNotInstantiateContainerClass(getContainerClass(), ex);
        }
    }

    /**
     * INTERNAL:
     * Return whether element exists in container.
     */
    protected boolean contains(Object element, Object container) {
        throw QueryException.methodNotValid(this, "contains(Object element, Object container)");
    }

    /**
     * INTERNAL:
     * Check if the object is contained in the collection.
     * This is used to check contains in a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public boolean contains(Object element, Object container, AbstractSession session) {
        if (hasElementDescriptor() && getElementDescriptor().hasWrapperPolicy()) {
            // The wrapper for the object must be removed.
            Object iterator = iteratorFor(container);
            while (hasNext(iterator)) {
                Object next = next(iterator);
                if (getElementDescriptor().getObjectBuilder().unwrapObject(next, session).equals(element)) {
                    return true;
                }
            }
            return false;
        } else {
            return contains(element, container);
        }
    }

    /**
     * INTERNAL:
     * Return whether element exists in container.
     */
    protected boolean containsKey(Object element, Object container) {
        throw QueryException.methodNotValid(this, "containsKey(Object element, Object container)");
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this ContainerPolicy to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){};

    /**
     * INTERNAL:
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public Object execute() {
        throw QueryException.methodNotValid(this, "execute()");
    }

    /**
     * INTERNAL:
     * Return the size constructor if available.
     */
    protected Constructor getConstructor() {
        return constructor;
    }

    /**
     * INTERNAL:
     * Return the class used for the container.
     */
    public Class getContainerClass() {
        throw QueryException.methodNotValid(this, "getContainerClass()");
    }

    /**
     * INTERNAL:
     * Used by the MW
     */
    public String getContainerClassName() {
        throw QueryException.methodNotValid(this, "getContainerClassName()");
    }

    /**
     * INTERNAL:
     * Used for wrapping and unwrapping with the wrapper policy.
     */
    public ClassDescriptor getElementDescriptor() {
        return elementDescriptor;
    }

    /**
     * INTERNAL:
     * Used for wrapping and unwrapping with the wrapper policy.
     */
    public boolean hasElementDescriptor() {
        return elementDescriptor != null;
    }

    /**
     * INTERNAL:
     * Return whether the iterator has more objects.
     * The iterator is the one returned from #iteratorFor().
     * Valid for some subclasses only.
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    public abstract boolean hasNext(Object iterator);

    /**
     * INTERNAL:
     * Returns true if the collection has order
     */
    public boolean hasOrder() {
        return false;
    }

    /**
     * INTERNAL:
     * Find the size constructor.
     * Providing a size is important for performance.
     */
    public void initializeConstructor() {
        try {
            Constructor constructor = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(getContainerClass(), new Class[] { ClassConstants.PINT }, false));
                } catch (PrivilegedActionException exception) {
                    // If there is no constructor then the default will be used.
                    return;
                }
            } else {
                constructor = PrivilegedAccessHelper.getConstructorFor(getContainerClass(), new Class[] { ClassConstants.PINT }, false);
            }
            setConstructor(constructor);
        } catch (Exception exception) {
            // If there is no constructor then the default will be used.
            return;
        }
    }

    public boolean isCollectionPolicy() {
        return false;
    }

    public boolean isCursoredStreamPolicy() {
        return false;
    }
    
    public boolean isScrollableCursorPolicy() {
        return false;
    }

    public boolean isCursorPolicy() {
        return false;
    }

    public boolean isDirectMapPolicy() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether the container is empty.
     */
    public boolean isEmpty(Object container) {
        return sizeFor(container) == 0;
    }

    public boolean isListPolicy() {
        return false;
    }

    public boolean isMapPolicy() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether the specified object is of a valid container type.
     *
     * @see org.eclipse.persistence.internal.queries.CollectionContainerPolicy#isValidContainer(Object)
     * @see org.eclipse.persistence.internal.queries.MapContainerPolicy#isValidContainer(Object)
     */
    public boolean isValidContainer(Object container) {
        throw QueryException.methodNotValid(this, "isValidContainer(Object container)");
    }

    /**
     * INTERNAL:
     * Return whether the specified type is a valid container type.
     */
    public boolean isValidContainerType(Class containerType) {
        throw QueryException.methodNotValid(this, "isValidContainerType(Class containerType)");
    }

    /**
     * INTERNAL:
     * Return an iterator for the given container.
     * This iterator can then be used as a parameter to #hasNext()
     * and #next().
     *
     * @see ContainerPolicy#hasNext(java.lang.Object)
     * @see ContainerPolicy#next(java.lang.Object)
     */
    public abstract Object iteratorFor(Object container);

    /**
     * INTERNAL:
     * Return the key for the specified element.
     *
     * @param element java.lang.Object
     * @return java.lang.Object
     */
    public Object keyFrom(Object element, AbstractSession session) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    public Object mergeCascadeParts(ObjectChangeSet objectChanges, MergeManager mergeManager, AbstractSession parentSession) {
        Object object = null;
                
        if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
            // CR 2855 - Try to find the object first we may have merged it already.
            object = objectChanges.getTargetVersionOfSourceObject(parentSession);
                        
            if ((object == null) && (objectChanges.isNew() || objectChanges.isAggregate()) && objectChanges.containsChangesFromSynchronization()) {
                if (!mergeManager.getObjectsAlreadyMerged().containsKey(objectChanges)) {
                    // CR 2855 - If we haven't merged this object already then 
                    // build a new object otherwise leave it as null which will 
                    // stop the recursion.
                    // CR 3424  - Need to build the right instance based on 
                    // class type instead of referenceDescriptor.
                    Class objectClass = objectChanges.getClassType(mergeManager.getSession());
                    object = mergeManager.getSession().getDescriptor(objectClass).getObjectBuilder().buildNewInstance();
                    // Store the change set to prevent us from creating this new object again.
                    mergeManager.getObjectsAlreadyMerged().put(objectChanges, object);
                } else {
                    // CR 4012 - We have all ready created the object, must be 
                    // in a cyclic merge on a new object so get it out of the 
                    // already merged collection
                    object = mergeManager.getObjectsAlreadyMerged().get(objectChanges);
                }
            } else {
                object = objectChanges.getTargetVersionOfSourceObject(parentSession, true);
            }
                        
            if (objectChanges.containsChangesFromSynchronization()) {
                mergeManager.mergeChanges(object, objectChanges);
            }
        } else {
            mergeManager.mergeChanges(objectChanges.getUnitOfWorkClone(), objectChanges);
        }
        
        return object;            
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    public void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession parentSession) {
        ObjectChangeSet objectChanges;        
        // Step 1 - iterate over the removed changes and remove them from the container.
        Iterator removeObjects = changeRecord.getRemoveObjectList().keySet().iterator();
        // Ensure the collection is synchronized while changes are being made,
        // clone also synchronizes on collection (does not have cache key read-lock for indirection).
        // Must synchronize of the real collection as the clone does so.
        Object synchronizedValueOfTarget = valueOfTarget;
        if (valueOfTarget instanceof IndirectCollection) {
            synchronizedValueOfTarget = ((IndirectCollection)valueOfTarget).getDelegateObject();
        }
        synchronized (synchronizedValueOfTarget) {
            while (removeObjects.hasNext()) {
                objectChanges = (ObjectChangeSet) removeObjects.next();                
                removeFrom(objectChanges.getOldKey(), objectChanges.getTargetVersionOfSourceObject(mergeManager.getSession()), valueOfTarget, parentSession);
                if (!mergeManager.shouldMergeChangesIntoDistributedCache()) {
                    mergeManager.registerRemovedNewObjectIfRequired(objectChanges.getUnitOfWorkClone());
                }
            }
            
            // Step 2 - iterate over the added changes and add them to the container.
            Iterator addObjects = changeRecord.getAddObjectList().keySet().iterator();                
            while (addObjects.hasNext()) {
                objectChanges = (ObjectChangeSet) addObjects.next();
                Object object = null;                    
                if (shouldMergeCascadeParts) {
                    object = mergeCascadeParts(objectChanges, mergeManager, parentSession);
                }                    
                if (object == null) {
                    // Retrieve the object to be added to the collection.
                    object = objectChanges.getTargetVersionOfSourceObject(mergeManager.getSession(), false);
                }
                // I am assuming that at this point the above merge will have created a new object if required
                if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
                    //bug#4458089 and 4454532- check if collection contains new item before adding during merge into distributed cache					
                    if (!contains(object, valueOfTarget, mergeManager.getSession())) {
                        addInto(objectChanges.getNewKey(), object, valueOfTarget, mergeManager.getSession());
                    }
                } else {
                    addInto(objectChanges.getNewKey(), object, valueOfTarget, mergeManager.getSession());    
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * Valid for some subclasses only.
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    protected abstract Object next(Object iterator);

    /**
     * INTERNAL:
     * Return the next object from the iterator.
     * This is used to stream over a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public Object next(Object iterator, AbstractSession session) {
        Object next = next(iterator);
        if (hasElementDescriptor()) {
            next = getElementDescriptor().getObjectBuilder().unwrapObject(next, session);
        }
        return next;
    }

    /**
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public boolean overridesRead() {
        return false;
    }

    /**
     * Prepare and validate.
     * Allow subclasses to override.
     */
    public void prepare(DatabaseQuery query, AbstractSession session) throws QueryException {
        if (query.isReadAllQuery() && (!query.isReportQuery()) && query.shouldUseWrapperPolicy()) {
            setElementDescriptor(query.getDescriptor());
            //make sure DataReadQuery points to this container policy
        } else if (query.isDataReadQuery()) {
            ((DataReadQuery)query).setContainerPolicy(this);
        }
    }

    /**
     * Prepare and validate.
     * Allow subclasses to override.
     */
    public void prepareForExecution() throws QueryException {
    }

    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each containerplicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    public void recordAddToCollectionInChangeRecord(ObjectChangeSet changeSetToAdd, CollectionChangeRecord collectionChangeRecord){
        if (collectionChangeRecord.getRemoveObjectList().containsKey(changeSetToAdd)) {
            collectionChangeRecord.getRemoveObjectList().remove(changeSetToAdd);
        } else {
            collectionChangeRecord.getAddObjectList().put(changeSetToAdd, changeSetToAdd);
        }
    }
    
    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each containerplicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    public void recordRemoveFromCollectionInChangeRecord(ObjectChangeSet changeSetToRemove, CollectionChangeRecord collectionChangeRecord){
        if(collectionChangeRecord.getAddObjectList().containsKey(changeSetToRemove)) {
            collectionChangeRecord.getAddObjectList().remove(changeSetToRemove);
        } else {
            collectionChangeRecord.getRemoveObjectList().put(changeSetToRemove, changeSetToRemove);
        }
    }
    
    /**
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public Object remoteExecute() {
        return null;
    }

    /**
     * INTERNAL:
     * Remove all the elements from container.
     * Valid only for certain subclasses.
     */
    public void removeAllElements(Object container) {
        clear(container);
    }

    /**
     * INTERNAL:
     * Remove element from container.
     * Valid for some subclasses only.
     */
    protected boolean removeFrom(Object key, Object element, Object container) {
        throw QueryException.cannotRemoveFromContainer(element, container, this);
    }

    /**
     * INTERNAL:
     * Remove the object from the collection.
     * This is used to remove from a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public boolean removeFrom(Object key, Object element, Object container, AbstractSession session) {
        Object objectToRemove = element;
        if (hasElementDescriptor() && getElementDescriptor().hasWrapperPolicy()) {
            // The wrapper for the object must be removed.
            Object iterator = iteratorFor(container);
            while (hasNext(iterator)) {
                Object next = next(iterator);
                if (getElementDescriptor().getObjectBuilder().unwrapObject(next, session).equals(element)) {
                    objectToRemove = next;
                    break;
                }
            }
        }

        return removeFrom(key, objectToRemove, container);
    }

    /**
     * INTERNAL:
     * Remove the object from the collection.
     * This is used to remove from a collection independent of JDK 1.1 and 1.2.
     * The session may be required to unwrap for the wrapper policy.
     */
    public boolean removeFrom(Object element, Object container, AbstractSession session) {
        return removeFrom(null, element, container, session);
    }

    /**
    * INTERNAL:
    * It is illegal to send this message to this receiver. Try one of my subclasses.
    * Throws an exception.
    *
    * @see #ListContainerPolicy
    */
    public void removeFromWithOrder(int beginIndex, Object container) {
        throw QueryException.methodDoesNotExistInContainerClass("remove(index)", getContainerClass());
    }

    /**
     * INTERNAL:
     * Set the size constructor if available.
     */
    protected void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }

    /**
     * INTERNAL:
     * Set the class used for the container.
     */
    public void setContainerClass(Class containerClass) {
        throw QueryException.methodNotValid(this, "getContainerClass()");
    }

    /**
     * INTERNAL:
     * Used by the MW
     */
    public void setContainerClassName(String containerClassName) {
        throw QueryException.methodNotValid(this, "getContainerClassName()");
    }

    /**
     * INTERNAL:
     * Used for wrapping and unwrapping with the wrapper policy.
     */
    public void setElementDescriptor(ClassDescriptor elementDescriptor) {
        this.elementDescriptor = elementDescriptor;
    }

    /**
     * INTERNAL:
     * It is illegal to send this message to this receiver. Try one of my 
     * subclasses. Throws an exception.
     *
     * @see #MapContainerPolicy
     */
    public void setKeyName(String instanceVariableName, String elementClassName) {
        throw ValidationException.containerPolicyDoesNotUseKeys(this, instanceVariableName);
    }

    /**
     * INTERNAL:
     * Sets the key name to be used to generate the key in a Map type container 
     * class. The key name, may be the name of a field or method.
     * An instance of the class is provided in the case when the descriptor is being 
     * built in code.
     */
    public void setKeyName(String instanceVariableName, Class elementClass) {
        throw ValidationException.containerPolicyDoesNotUseKeys(this, instanceVariableName);
   }

    /**
     * INTERNAL:
     * Return the size of container.
     */
    public int sizeFor(Object container) {
        throw QueryException.methodNotValid(this, "sizeFor(Object container)");
    }

    public String toString() {
        return Helper.getShortClassName(this.getClass()) + "(" + toStringInfo() + ")";
    }

    protected Object toStringInfo() {
        return "";
    }

    /**
     * INTERNAL:
     * over ride in MapPolicy subclass
     */
    public void validateElementAndRehashIfRequired(Object sourceValue, Object target, AbstractSession session, Object targetVersionOfSource) {
        //do nothing
    }

    /**
     * INTERNAL:
     * Return a Vector populated with the contents of container.
     * Added for bug 2766379, must implement a version of vectorFor that
     * handles wrapped objects.
     */
    public Vector vectorFor(Object container, AbstractSession session) {
        Vector result = new Vector(sizeFor(container));

        for (Object iter = iteratorFor(container); hasNext(iter);) {
            result.addElement(next(iter, session));
        }
        return result;
    }
}
