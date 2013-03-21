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
package org.eclipse.persistence.internal.queries;

import java.util.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

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
public abstract class ContainerPolicy implements CoreContainerPolicy<AbstractSession>, Cloneable, Serializable {
    private static final long serialVersionUID = 6971791021041582975L;

    /**
     * Allow the default collection class to be set.
     */
    protected static Class defaultContainerClass = ClassConstants.Vector_class;
    
    /** The descriptor is used to wrap and unwrap objects using the wrapper policy. **/
    protected transient ClassDescriptor elementDescriptor;
    protected transient Constructor constructor;

    /**
     * ADVANCED:
     * Return the default collection class.
     */
    public static Class getDefaultContainerClass() {
        return defaultContainerClass;
    }
    
    /**
     * ADVANCED:
     * Allow the default collection class to be set.
     */
    public static void setDefaultContainerClass(Class collectionClass) {
        defaultContainerClass = collectionClass;
    }
    
    /**
     * Default constructor.
     */
    public ContainerPolicy() {
    }
    
    /**
     * INTERNAL:
     * Called when the selection query is being initialized to add any required additional fields to the
     * query.  By default, there are not additional fields required but this method is overridden by subclasses.
     * 
     * @see MappedKeyMapContinerPolicy
     */
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression){
    }

    /**
     * INTERNAL:
     * Called when the insert query is being initialized to ensure the fields for the key are in the insert query
     * 
     * @see MappedKeyMapContainerPolicy
     */
    public void addFieldsForMapKey(AbstractRecord joinRow){
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
     * This is used to add to a collection independent of type.
     * The session may be required to wrap for the wrapper policy.
     * The row may be required by subclasses, such as MappedKeyMap.
     * Return whether the container changed.
     */
    public boolean addInto(Object element, Object container, AbstractSession session, AbstractRecord dbRow, ObjectBuildingQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        return addInto(null, element, container, session);
    }

    /**
     * INTERNAL:
     * This is used for ordered List containers to add all of the elements
     * to the collection in the order of the index field in the row.
     * This is currently only used by OrderListContainerPolicy, so this is just a stub.
     * The passing of the query is to allow future compatibility with Maps (ordered Map).
     */
    public boolean addAll(List elements, Object container, AbstractSession session, List<AbstractRecord> dbRows, ObjectBuildingQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        boolean changed = false;
        for(int i=0; i < elements.size(); i++) {
            changed |= addInto(elements.get(i), container, session, dbRows.get(i), query, parentCacheKey, isTargetProtected);
        }
        return changed;
    }

    /**
     * INTERNAL:
     * This is used for adding to a direct map or direct collection from the database.
     * The row data may also be requires, as in the case of indexed ordered lists,
     * or direct maps.
     */
    public boolean addInto(Object element, Object container, AbstractSession session, AbstractRecord row, DataReadQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        return addInto(null, element, container, session);
    }

    /**
     * INTERNAL:
     * This is used for ordered List containers to add all of the elements
     * to the collection in the order of the index field in the row.
     * This is currently only used by OrderListContainerPolicy, so this is just a stub.
     * The passing of the query is to allow future compatibility with Maps (ordered Map).
     */
    public boolean addAll(List elements, Object container, AbstractSession session, List<AbstractRecord> rows, DataReadQuery query, CacheKey parentCacheKey, boolean isTargetProtected) {
        boolean changed = false;
        for(int i=0; i < elements.size(); i++) {
            changed |= addInto(elements.get(i), container, session, rows.get(i), query, parentCacheKey, isTargetProtected);
        }
        return changed;
    }

    /**
     * INTERNAL:
     * Add element to container.
     * This is used to add to a collection independent of type.
     * The session may be required to wrap for the wrapper policy.
     * Return whether the container changed.
     */
    public boolean addInto(Object key, Object element, Object container, AbstractSession session) {
        throw QueryException.cannotAddToContainer(element, container, this);
    }
    
    /**
     * INTERNAL:
     * Used for joining.  Add any queries necessary for joining to the join manager
     * This method will be overridden by subclasses that handle map keys
     */
    public void addNestedJoinsQueriesForMapKey(JoinedAttributeManager joinManager, ObjectLevelReadQuery query, AbstractSession session){
    }

    /**
     * INTERNAL:
     * This method is used to add the next value from an iterator built using ContainerPolicy's iteratorFor() method
     * into the toCollection.
     * This method is overridden by subclasses to provide extended functionality for map keys
     * 
     * @see MappedKeyMapContainerPolicy
     * 
     * @param valuesIterator
     * @param toCollection
     * @param mapping
     * @param unitOfWork
     * @param isExisting
     */
    public void addNextValueFromIteratorInto(Object valuesIterator, Object parent, CacheKey parentCacheKey, Object toCollection, CollectionMapping mapping, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isFromSharedCache){
        Object cloneValue = mapping.buildElementClone(next(valuesIterator, cloningSession), parent, parentCacheKey, refreshCascade, cloningSession, isExisting, isFromSharedCache);
        // add the object to the uow list of private owned objects if it is a candidate and the
        // uow should discover new objects
        if (cloningSession.isUnitOfWork() && !isExisting && mapping.isCandidateForPrivateOwnedRemoval() && ((UnitOfWorkImpl) cloningSession).shouldDiscoverNewObjects() && cloneValue != null && ((UnitOfWorkImpl) cloningSession).isCloneNewObject(cloneValue)) {
            ((UnitOfWorkImpl) cloningSession).addPrivateOwnedObject(mapping, cloneValue);
        }
        addInto(cloneValue, toCollection, cloningSession);
    }
    
    /**
     * Build a clone for the key of a Map represented by this container policy if necessary.
     * By default, the key is not cloned since in standard EclipseLink Mappings it will not be 
     * an Entity
     * @param key
     * @param cloningSession
     * @param isExisting
     * @return
     */
    public Object buildCloneForKey(Object key, Object parent , CacheKey parentCacheKey, Integer refreshCascade, AbstractSession cloningSession, boolean isExisting, boolean isCacheCheckComplete){
        return key;
        
    }
    
    /**
     * INTERNAL:
     * Return an object representing an entry in the collection represented by this container policy
     * This method will be overridden to allow MapContainerPolicy to return a construct that
     * contains the key and the value
     * 
     * @see MappedKeyMapContainerPolicy
     * @param objectAdded
     * @param changeSet
     * @return
     */
    public Object buildCollectionEntry(Object objectAdded, ObjectChangeSet changeSet){
        return objectAdded;
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
     * Extract the key for the map from the provided row
     * overridden by subclasses that deal with map keys
     * @param row
     * @param query
     * @param session
     * @return
     */
    public Object buildKey(AbstractRecord row, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return null;
    }
    
    /**
     * INTERNAL:
     * This method will access the target relationship and create a list of information to rebuild the collection.
     * This method is used in combination with the CachedValueHolder to store references to PK's to be loaded
     * from a cache instead of a query.
     * @see ContainerPolicy.buildReferencesPKList()
     * @see MappedKeyMapContainerPolicy.buildReferencesPKList()
     */
    public Object[] buildReferencesPKList(Object container, AbstractSession session){
        Object[] result = new Object[this.sizeFor(container)];
        Iterator iterator = (Iterator)this.iteratorFor(container);
        int index = 0;
        while(iterator.hasNext()){
            Object target = iterator.next();
            if (target != null){
                result[index] = elementDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(target, session);
                ++index;
            }
        }
        return result;

    }

    /**
     * Extract the key for the map from the provided row
     * overridden by subclasses that deal with map keys
     * @param row
     * @param query
     * @param session
     * @return
     */
    public Object buildKeyFromJoinedRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey parentCacheKey, AbstractSession session, boolean isTargetProtected){
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the appropriate container policy for the default container class.
     */
    public static ContainerPolicy buildDefaultPolicy() {
        return buildPolicyFor(ContainerPolicy.getDefaultContainerClass());
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
            } else if (concreteContainerClass == ClassConstants.Vector_class) {
                return new VectorContainerPolicy(concreteContainerClass);
            }  else if (concreteContainerClass == ClassConstants.IndirectList_Class) {
                return new IndirectListContainerPolicy(concreteContainerClass);
            }   else if (concreteContainerClass == ClassConstants.ArrayList_class) {
                return new ArrayListContainerPolicy(concreteContainerClass);
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
     * This 
     * Certain key mappings favor different types of selection query.  Return the appropriate
     * type of selectionQuery
     * @return
     */
    public ReadQuery buildSelectionQueryForDirectCollectionMapping(){
        DirectReadQuery query = new DirectReadQuery();
        query.setSQLStatement(new SQLSelectStatement());
        query.setContainerPolicy(this);
        return query;
    }
    
    /**
     * INTERNAL:
     * Remove all the elements from the specified container.
     * Valid only for certain subclasses.
     */
    @Override
    public void clear(Object container) {
        throw QueryException.methodNotValid(this, "clear(Object container)");
    }
    
    /**
     * INTERNAL:
     * Creates a CollectionChangeEvent for the container
     */
    public abstract CollectionChangeEvent createChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementChanged, int changeType, Integer index, boolean isChangeApplied);
    
    /**
     * INTERNAL:
     * Return if the policy is equal to the other.
     * By default if they are the same class, they are considered equal.
     * This is used for query parse caching.
     */
    @Override
    public boolean equals(Object object) {
        return (object != null) && (getClass().equals(object.getClass()));
    }
    
    /**
     * INTERNAL:
     * Cascade DiscoverAndPersistUnregisteredNewObjects to any mappings managed by the container policy.  Be default, this is a no-op, but
     * will be overridden by subclasses
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, Set cascadeErrors) {
    }
    
    /**
     * INTERNAL:
     * Cascade performRemove to any mappings managed by the container policy.  Be default, this is a no-op, but
     * will be overridden by subclasses
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew to any mappings managed by the container policy.  Be default, this is a no-op, but
     * will be overridden by subclasses
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
    }
    
    @Override
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
     * Create change sets that contain map keys.
     * This method will be overridden by subclasses that handle map keys
     * @param originalKeyValues
     * @param changeRecord
     * @param session
     * @param referenceDescriptor
     */
    protected void createChangeSetForKeys(Map originalKeyValues, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor){
    }

    /**
     * INTERNAL:
     * Iterate over the list of new objects and create change sets for them
     * This method is overridden by subclasses to handle map keys
     * @param originalKeyValues
     * @param cloneKeyValues
     * @param newCollection
     * @param changeRecord
     * @param session
     * @param referenceDescriptor
     */
    protected void collectObjectForNewCollection(Map originalKeyValues, Map cloneKeyValues, Object newCollection, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor){
        Object cloneIter = iteratorFor(newCollection);
        
        while (hasNext(cloneIter)) {
            Object wrappedFirstObject = nextEntry(cloneIter, session);
            Object firstObject = unwrapIteratorResult(wrappedFirstObject);
            // CR2378 null check to prevent a null pointer exception - XC
            // If value is null then nothing can be done with it.
            if (firstObject != null) {
                Object key = firstObject;
                if (changeRecord.getMapping().isAggregateCollectionMapping()){
                    key = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(firstObject, session);
                }
                if (originalKeyValues.containsKey(key)) {
                    // There is an original in the cache
                    if ((compareKeys(firstObject, session))) {
                        // The keys have not changed
                        originalKeyValues.remove(key);
                    } else {
                        // The keys have changed, create a changeSet 
                        // (it will be reused later) and set the old key 
                        // value to be used to remove.
                        Object backUpVersion = null;

                        // CR4172 compare the keys from the back up to the 
                        // clone not from the original to the clone.
                        if (((UnitOfWorkImpl)session).isClassReadOnly(firstObject.getClass())) {
                            backUpVersion = firstObject;
                        } else {
                            backUpVersion = ((UnitOfWorkImpl)session).getBackupClone(firstObject, referenceDescriptor);
                        }
                        
                        ObjectChangeSet changeSet = referenceDescriptor.getObjectBuilder().createObjectChangeSet(firstObject, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
                        changeSet.setOldKey(keyFrom(backUpVersion, session));
                        changeSet.setNewKey(keyFrom(firstObject, session));
                        cloneKeyValues.put(key, firstObject);
                    }
                } else {
                    // Place it in the add collection
                    buildChangeSetForNewObjectInCollection(wrappedFirstObject, referenceDescriptor, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
                    cloneKeyValues.put(key, firstObject);
                }
            }
        }
        
    }
    
    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     */
    public void compareCollectionsForChange(Object oldCollection, Object newCollection, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor) {
        // 2612538 - the default size of Map (32) is appropriate
        Map originalKeyValues = null;
        Map cloneKeyValues = null;
        if (changeRecord.getMapping().isAggregateCollectionMapping()){
            originalKeyValues = new HashMap();
            cloneKeyValues = new HashMap();
        }else{
            originalKeyValues = new IdentityHashMap();
            cloneKeyValues = new IdentityHashMap();
        }
        // Collect the values from the oldCollection.
        if (oldCollection != null) {
            Object backUpIter = iteratorFor(oldCollection);
            
            while (hasNext(backUpIter)) {
                Object wrappedSecondObject = nextEntry(backUpIter, session);
                Object secondObject = unwrapIteratorResult(wrappedSecondObject);
    
                // CR2378 null check to prevent a null pointer exception - XC
                if (secondObject != null) {
                    Object key = secondObject;
                    if (changeRecord.getMapping().isAggregateCollectionMapping()){
                        key = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(secondObject, session);
                    }
                    originalKeyValues.put(key, wrappedSecondObject);
                }
            }
        }
        if (newCollection != null){
            collectObjectForNewCollection(originalKeyValues, cloneKeyValues, newCollection, changeRecord, session, referenceDescriptor);
        }
        createChangeSetForKeys(originalKeyValues, changeRecord, session, referenceDescriptor);
        changeRecord.clearChanges();
        changeRecord.addAdditionChange(cloneKeyValues, this, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addRemoveChange(originalKeyValues, this, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.setIsDeferred(false);
        changeRecord.setLatestCollection(null);
    }
    
    public void buildChangeSetForNewObjectInCollection(Object object, ClassDescriptor referenceDescriptor, UnitOfWorkChangeSet uowChangeSet, AbstractSession session){
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
    public Object concatenateContainers(Object firstContainer, Object secondContainer, AbstractSession session) {
        Object container = containerInstance(sizeFor(firstContainer) + sizeFor(secondContainer));

        for (Object firstIter = iteratorFor(firstContainer); hasNext(firstIter);) {
            addInto(null, next(firstIter), container, session);
        }

        for (Object secondIter = iteratorFor(secondContainer); hasNext(secondIter);) {
            addInto(null, next(secondIter), container, session);
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
        Class containerClass = getContainerClass();
        // PERF: Avoid reflection for common cases.
        if (containerClass == ClassConstants.IndirectList_Class) {
            return new IndirectList();
        } else if (containerClass == ClassConstants.IndirectSet_Class) {
            return new IndirectSet();
        } else if (containerClass == ClassConstants.ArrayList_class) {
            return new ArrayList();
        } else if (containerClass == ClassConstants.Vector_class) {
            return new Vector();
        } else if (containerClass == ClassConstants.HashSet_class) {
            return new HashSet();
        }
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(containerClass));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.couldNotInstantiateContainerClass(containerClass, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.newInstanceFromClass(containerClass);
            }
        } catch (Exception ex) {
            throw QueryException.couldNotInstantiateContainerClass(containerClass, ex);
        }
    }

    /**
     * INTERNAL:
     * Return an instance of the container class with the specified initial capacity.
     * Null should never be returned.
     * A ValidationException is thrown on error.
     */
    public Object containerInstance(int initialCapacity) {
        if (this.constructor == null) {
            return containerInstance();
        }
        Class containerClass = getContainerClass();
        try {
            // PERF: Avoid reflection for common cases.
            if (containerClass == ClassConstants.IndirectList_Class) {
                return new IndirectList(initialCapacity);
            } else if (containerClass == ClassConstants.IndirectSet_Class) {
                return new IndirectSet(initialCapacity);
            } else if (containerClass == ClassConstants.ArrayList_class) {
                return new ArrayList(initialCapacity);
            } else if (containerClass == ClassConstants.Vector_class) {
                return new Vector(initialCapacity);
            } else if (containerClass == ClassConstants.HashSet_class) {
                return new HashSet(initialCapacity);
            }
            Object[] arguments = new Object[1];
            //Code change for 3732.  No longer need to add 1 as this was for JDK 1.1
            arguments[0] = Integer.valueOf(initialCapacity);
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(this.constructor, arguments));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.couldNotInstantiateContainerClass(containerClass, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.invokeConstructor(this.constructor, arguments);
            }
        } catch (Exception ex) {
            throw QueryException.couldNotInstantiateContainerClass(containerClass, ex);
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
    @Override
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
     * Convert all the class-name-based settings in this ContainerPolicy to actual class-based
     * settings
     * This method is implemented by subclasses as necessary.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){};

    /**
     * INTERNAL:
     * This method will actually potentially wrap an object in two ways.  It will first wrap the object
     * based on the referenceDescriptor's wrapper policy.  It will also potentially do some wrapping based
     * on what is required by the container policy.
     * 
     * @see MappedKeyMapContainerPolicy
     * @param wrappedObject
     * @param parent if this is an aggregate, the owner of the aggregate
     * @param referenceDescriptor
     * @param mergeManager
     * @return
     */
    public Object createWrappedObjectFromExistingWrappedObject(Object wrappedObject, Object parent, ClassDescriptor referenceDescriptor, MergeManager mergeManager, AbstractSession targetSession){
        return referenceDescriptor.getObjectBuilder().wrapObject(mergeManager.getTargetVersionOfSourceObject(unwrapIteratorResult(wrappedObject), referenceDescriptor, targetSession), mergeManager.getSession());
    }
    
    /**
     * INTERNAL:
     * Delete the passed object
     * This may be overridden by subclasses to deal with composite objects
     * 
     * @see MappedKeyMapContainerPolicy
     * @param objectDeleted
     * @param session
     */
    public void deleteWrappedObject(Object objectDeleted, AbstractSession session){
        session.deleteObject(objectDeleted);
    }
    
    /**
     * INTERNAL:
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public Object execute() {
        throw QueryException.methodNotValid(this, "execute()");
    }

    /**
     * INTERNAL:
     * Return any tables that will be required when this mapping is used as part of a join query.
     */
    public List<DatabaseTable> getAdditionalTablesForJoinQuery(){
        return null;
    }
    
    /**
     * INTERNAL:
     * Return any additional fields required by the policy for a fetch join.
     * This method will be overridden by ContainerPolicies that handle map keys.
     */
    public List<DatabaseField> getAdditionalFieldsForJoin(CollectionMapping baseMapping) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used to create an iterator on a the Map object passed to CollectionChangeRecord.addRemoveChange()
     * to access the values to be removed.  In the case of some container policies the values will actually
     * be the keys.
     */
    
    public Iterator getChangeValuesFrom(Map map){
        return map.values().iterator();
    }
    
    /**
     * INTERNAL:
     * Used when objects are added or removed during an update.
     * This method returns either the clone from the ChangeSet or a packaged
     * version of it that contains things like map keys.
     */
    public Object getCloneDataFromChangeSet(ObjectChangeSet changeSet){
        return changeSet.getUnitOfWorkClone();
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
     * Return the reference descriptor for the map key if it exists
     */
    public ClassDescriptor getDescriptorForMapKey(){
        return null;
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
     * Return the fields that make up the identity of the key if this mapping is a list
     * This method will be overridden by subclasses.
     */
    public List<DatabaseField> getIdentityFieldsForMapKey(){
        return null;
    }
    
    /**
     * INTERNAL:
     * Add any non-Foreign-key data from an Object describe by a MapKeyMapping to a database row
     * This is typically used in write queries to ensure all the data stored in the collection table is included
     * in the query.
     */
    public Map getKeyMappingDataForWriteQuery(Object object, AbstractSession session){
        return null;
    }
    
    
    /**
     * INTERNAL:
     * Get the selection criteria for the map key
     * This will be overridden by container policies that allow maps
     */
    public Expression getKeySelectionCriteria(){
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the type of the map key, this will be overridden by container policies that allow maps.
     */
    public Object getKeyType(){
        return null;
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
     * Provide a hook to allow initialization of Container Policy parts
     */
    public void initialize(AbstractSession session, DatabaseTable keyTable){
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
    @Override
    public boolean isEmpty(Object container) {
        return sizeFor(container) == 0;
    }

    @Override
    public boolean isListPolicy() {
        return false;
    }

    public boolean isOrderedListPolicy() {
        return false;
    }
    
    public boolean isMapPolicy() {
        return false;
    }
    
    public boolean isMappedKeyMapPolicy(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return if the map key this container policy represents is a OneToOne.
     */
    public boolean isMapKeyObject() {
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
     * Used in Descriptor Iteration to iterate on map keys.
     * This method is a no-op here, but will be overridden by subclasses
     */
    public void iterateOnMapKey(DescriptorIterator iterator, Object element) {
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
     * Return the key for the specified element..
     */
    public Object keyFrom(Object element, AbstractSession session) {
        return null;
    }
    
    /**
     * Get the key from the passed in Map.Entry
     * This method will be overridden by ContainerPolicies that allows maps.
     */
    public Object keyFromEntry(Object entry){
        return null;
    }
    
    public Object keyFromIterator(Object iterator){
        return null;
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    public Object mergeCascadeParts(ObjectChangeSet objectChanges, MergeManager mergeManager, AbstractSession targetSession) {
        Object object = null;
                
        if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
            // CR 2855 - Try to find the object first we may have merged it already.
            object = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession);
                        
            if ((object == null) && (objectChanges.isNew() || objectChanges.isAggregate()) && objectChanges.containsChangesFromSynchronization()) {
                if (!mergeManager.isAlreadyMerged(objectChanges, targetSession)) {
                    // CR 2855 - If we haven't merged this object already then 
                    // build a new object otherwise leave it as null which will 
                    // stop the recursion.
                    // CR 3424  - Need to build the right instance based on 
                    // class type instead of referenceDescriptor.
                    Class objectClass = objectChanges.getClassType(mergeManager.getSession());
                    object = mergeManager.getSession().getDescriptor(objectClass).getObjectBuilder().buildNewInstance();
                    // Store the change set to prevent us from creating this new object again.
                    mergeManager.recordMerge(objectChanges, object, targetSession);
                } else {
                    // CR 4012 - We have all ready created the object, must be 
                    // in a cyclic merge on a new object so get it out of the 
                    // already merged collection
                    object = mergeManager.getMergedObject(objectChanges, targetSession);
                }
            } else {
                object = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession, true);
            }
                        
            if (objectChanges.containsChangesFromSynchronization()) {
                mergeManager.mergeChanges(object, objectChanges, targetSession);
            }
        } else {
            mergeManager.mergeChanges(objectChanges.getUnitOfWorkClone(), objectChanges, targetSession);
        }
        
        return object;            
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     * Synchronize if system property is specified. If not, default to clone the 
     * target collection. No need to synchronize if the collection is new. 
     */
    public void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession targetSession, boolean isSynchronizeOnMerge) {
        if (isSynchronizeOnMerge && !changeRecord.getOwner().isNew()) {
            // Ensure the collection is synchronized while changes are being made,
            // clone also synchronizes on collection (does not have cache key read-lock for indirection).
            // Must synchronize of the real collection as the clone does so.
            Object synchronizedValueOfTarget = valueOfTarget;
            if (valueOfTarget instanceof IndirectCollection) {
                synchronizedValueOfTarget = ((IndirectCollection)valueOfTarget).getDelegateObject();
            }
            synchronized (synchronizedValueOfTarget) {
                mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts, mergeManager, targetSession);
            }
        } else {
            // Using cloned target object passed instead of synchronization.
            mergeChanges(changeRecord, valueOfTarget, shouldMergeCascadeParts, mergeManager, targetSession);
        }
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    protected void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession targetSession) {
        ObjectChangeSet objectChanges;        
        // Step 1 - iterate over the removed changes and remove them from the container.
        Iterator removeObjects = changeRecord.getRemoveObjectList().keySet().iterator();
        while (removeObjects.hasNext()) {
            objectChanges = (ObjectChangeSet) removeObjects.next();                
            removeFrom(objectChanges.getOldKey(), objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession), valueOfTarget, targetSession);
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
                object = mergeCascadeParts(objectChanges, mergeManager, targetSession);
            }                    
            if (object == null) {
                // Retrieve the object to be added to the collection.
                object = objectChanges.getTargetVersionOfSourceObject(mergeManager, targetSession, false);
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
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    @Override
    public Object nextEntry(Object iterator){
        return next(iterator);
    }

    /**
     * INTERNAL:
     * Return the next object on the queue. The iterator is the one
     * returned from #iteratorFor().
     * 
     * In the case of a Map, this will return a MapEntry to allow use of the key
     *
     * @see ContainerPolicy#iteratorFor(Object iterator, AbstractSession session)
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    public Object nextEntry(Object iterator, AbstractSession session) {
        return next(iterator, session);
    }
    
    /**
     * This can be used by collection such as cursored stream to gain control over execution.
     */
    public boolean overridesRead() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Some subclasses need to post initialize mappings associated with them
     */
    public void postInitialize(AbstractSession session) {
    }

    /**
     * INTERNAL:
     * Add the provided object to the deleted objects list on the commit manager.
     * This may be overridden by subclasses to process a composite object
     * 
     * @see MappedKeyMapContainerPolicy
     * @param object
     * @param manager
     */
    public void postCalculateChanges(ObjectChangeSet ocs, ClassDescriptor referenceDescriptor, DatabaseMapping mapping, UnitOfWorkImpl uow){
        if (mapping.isForeignReferenceMapping()){
            Object clone = ocs.getUnitOfWorkClone();
            uow.addDeletedPrivateOwnedObjects(mapping, clone);
        }
    }

    /**
     * INTERNAL:
     * Add the provided object to the deleted objects list on the commit manager.
     * This may be overridden by subclasses to process a composite object.
     */
    public void postCalculateChanges(Object key, Object value, ClassDescriptor referenceDescriptor, DatabaseMapping mapping, UnitOfWorkImpl uow){
        if (! mapping.isDirectCollectionMapping() && ! mapping.isAggregateCollectionMapping()){
            uow.addDeletedPrivateOwnedObjects(mapping, value);
        }
    }

    /**
     * INTERNAL:
     * Add the provided object to the deleted objects list on the commit manager.
     * This may be overridden by subclasses to process a composite object.
     */
    public void recordPrivateOwnedRemovals(Object object, ClassDescriptor referenceDescriptor, UnitOfWorkImpl uow){
        if (referenceDescriptor != null){
            referenceDescriptor.getObjectBuilder().recordPrivateOwnedRemovals(unwrapIteratorResult(object), uow, false);
        }
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
     * INTERNAL:
     * This method is used to check the key mapping to ensure that it does not write to
     * a field that is written by another mapping.
     * This method will be overridden by subclasses that deal MapKeys
     * 
     */
    public void processAdditionalWritableMapKeyFields(AbstractSession session){
    }
    
    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePostDelete(DeleteObjectQuery query, Object object) {
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePostInsert(WriteObjectQuery query, Object object) {
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePostUpdate(WriteObjectQuery query, Object object) {
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePreDelete(DeleteObjectQuery query, Object object) {
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePreInsert(WriteObjectQuery query, Object object) {
    }

    /**
     * INTERNAL:
     * Propagate the postDeleteEvent to any additional objects the query is aware of
     * This method will be overridden by subclasses that deal MapKeys
     */
    public void propogatePreUpdate(WriteObjectQuery query, Object object) {
    }
    
    /**
     * INTERNAL:
     * Returns false.  Most container policies do not need to propagate events within the collections
     * This will be overridden by subclasses
     */
    public boolean propagatesEventsToCollection(){
        return false;
    }
    
    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each ContainerPolicy type will implement specific behavior for the collection 
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
     * Each ContainerPolicy type will implement specific behavior for the collection 
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
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the same instance multiple times.
     * Each ContainerPolicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    public void recordUpdateToCollectionInChangeRecord(CollectionChangeEvent event, ObjectChangeSet changeSet, CollectionChangeRecord collectionChangeRecord){
        if (event.getChangeType() == CollectionChangeEvent.ADD) {
            recordAddToCollectionInChangeRecord(changeSet, collectionChangeRecord);
        } else if (event.getChangeType() == CollectionChangeEvent.REMOVE) {
            recordRemoveFromCollectionInChangeRecord(changeSet, collectionChangeRecord);
        } else {
            throw ValidationException.wrongCollectionChangeEventType(event.getChangeType());
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
    @Override
    public boolean removeFrom(Object element, Object container, AbstractSession session) {
        return removeFrom(null, element, container, session);
    }

    /**
     * INTERNAL:
     * Returns whether this ContainerPolicy should requires data modification events when
     * objects are added or deleted during update
     * @return
     */
    public boolean requiresDataModificationEvents(){
        return false;
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
     * @see MapContainerPolicy
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
     * Indicates whether addAll method should be called to add entire collection,
     * or it's possible to call addInto multiple times instead.
     * @return
     */
    public boolean shouldAddAll(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return whether data for a map key must be included on a Delete data modification event
     * This will be overridden by subclasses that handle maps.
     */
    public boolean shouldIncludeKeyInDeleteEvent(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Certain types of container policies require an extra update statement after a relationship
     * is inserted.  Return whether this update statement is required.
     */
    public boolean shouldUpdateForeignKeysPostInsert(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return the size of container.
     */
    @Override
    public int sizeFor(Object container) {
        throw QueryException.methodNotValid(this, "sizeFor(Object container)");
    }

    @Override
    public String toString() {
        return Helper.getShortClassName(this.getClass()) + "(" + toStringInfo() + ")";
    }

    protected Object toStringInfo() {
        return "";
    }

    /**
     * INTERNAL:
     * Update the joined mapping indices
     * This method is a no-op, but will be overridden by subclasses
     */
    public int updateJoinedMappingIndexesForMapKey(Map<DatabaseMapping, Object> indexList, int index){
        return 0;
    }

    /**
     * INTERNAL:
     * Update a ChangeRecord to replace the ChangeSet for the old entity with the changeSet for the new Entity.  This is
     * used when an Entity is merged into itself and the Entity reference new or detached entities.
     */
    public void updateChangeRecordForSelfMerge(ChangeRecord changeRecord, Object source, Object target, ForeignReferenceMapping mapping, UnitOfWorkChangeSet parentUOWChangeSet, UnitOfWorkImpl unitOfWork){
       Map<ObjectChangeSet, ObjectChangeSet> list = ((CollectionChangeRecord)changeRecord).getAddObjectList();
        ObjectChangeSet sourceSet = parentUOWChangeSet.getCloneToObjectChangeSet().get(source);
        if (list.containsKey(sourceSet)){
            ObjectChangeSet targetSet = ((UnitOfWorkChangeSet)unitOfWork.getUnitOfWorkChangeSet()).findOrCreateLocalObjectChangeSet(target, mapping.getReferenceDescriptor(), unitOfWork.isCloneNewObject(target));
            targetSet.setNewKey(sourceSet.getNewKey());
            targetSet.setOldKey(sourceSet.getOldKey());
            parentUOWChangeSet.addObjectChangeSetForIdentity(targetSet, target);
            list.remove(sourceSet);
            list.put(targetSet, targetSet);
            return;
        }
    }

    /**
     * INTERNAL: 
     * MapContainerPolicy's iterator iterates on the Entries of a Map.
     * This method returns the object from the iterator
     * 
     * @see MapContainerPolicy.nextWrapped(Object iterator)
     */
    public Object unwrapElement(Object object){
       return object;
    }

    /**
     * INTERNAL:
     * Depending on the container, the entries returned of iteration using the ContainerPolicy.iteratorFor() method
     * may be wrapped.  This method unwraps the values.
     * 
     * @see MapContainerPolicy.unwrapIteratorResult(Object object)
     */
    public Object unwrapIteratorResult(Object object){
        return object;
    }

    /**
     * INTERNAL:
     * This method is used to load a relationship from a list of PKs. This list
     * may be available if the relationship has been cached.
     */
    public Object valueFromPKList(Object[] pks, AbstractRecord foreignKeys, ForeignReferenceMapping mapping, AbstractSession session){
        
        Object result = containerInstance(pks.length);
        Map<Object, Object> fromCache = session.getIdentityMapAccessor().getAllFromIdentityMapWithEntityPK(pks, elementDescriptor);
        for (Object entity: fromCache.values()){
            addInto(entity, result, session);
        }
        DatabaseRecord translationRow = new DatabaseRecord();
        List foreignKeyValues = new ArrayList(pks.length - fromCache.size());
        for (int index = 0; index < pks.length; ++index){
            Object pk = pks[index];
            if (!fromCache.containsKey(pk)){
                if (this.elementDescriptor.getCachePolicy().getCacheKeyType() == CacheKeyType.CACHE_ID){
                    foreignKeyValues.add(Arrays.asList(((CacheId)pk).getPrimaryKey()));
                }else{
                    foreignKeyValues.add(pk);
                }
            }
        }
        if (!foreignKeyValues.isEmpty()){
            translationRow.put(ForeignReferenceMapping.QUERY_BATCH_PARAMETER, foreignKeyValues);
    
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(elementDescriptor.getJavaClass());
            query.setIsExecutionClone(true);
            query.setTranslationRow(translationRow);
            query.setSession(session);
            query.setSelectionCriteria(elementDescriptor.buildBatchCriteriaByPK(query.getExpressionBuilder(), query));
            Collection<Object> temp = (Collection<Object>) session.executeQuery(query);
            if (temp.size() < foreignKeyValues.size()){
                //Not enough results have been found, this must be a stale collection with a removed
                //element.  Execute a reload based on FK.
                return session.executeQuery(mapping.getSelectionQuery(), foreignKeys);
            }
            for (Object element: temp){
                addInto(element, result, session);
            }
        }
        return result;
    }
    
    /**
     * INTERNAL:
     * Return a Vector populated with the contents of container.
     * Added for bug 2766379, must implement a version of vectorFor that
     * handles wrapped objects.
     */
    @Override
    public Vector vectorFor(Object container, AbstractSession session) {
        Vector result = new Vector(sizeFor(container));

        for (Object iter = iteratorFor(container); hasNext(iter);) {
            result.addElement(next(iter, session));
        }
        return result;
    }
    
    /**
     * INTERNAL:
     * convenience method to copy the keys and values from a Map into an AbstractRecord
     * @param mappingData a Map containing a database field as the key and the value of that field as the value
     * @param databaseRow
     */
    public static void copyMapDataToRow(Map mappingData, AbstractRecord databaseRow){
        if (mappingData != null){
            for (Iterator<Map.Entry> keys = mappingData.entrySet().iterator(); keys.hasNext();) {
                Map.Entry entry = keys.next();
                Object entryKey = entry.getKey();
                Object entryValue = entry.getValue();
                databaseRow.put(entryKey, entryValue);
            }
        }
    }

}
