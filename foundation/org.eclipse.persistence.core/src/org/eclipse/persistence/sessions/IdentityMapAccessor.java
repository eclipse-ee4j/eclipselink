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
package org.eclipse.persistence.sessions;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * PUBLIC:
 * IdentityMapAccessor provides the public interface into all functionality associated with
 * EclipseLink's cache. An appropriate IdentityMapAccessor can be obtained from a session 
 * with its getIdentityMapAccessor() method.
 * Methods that used to be called on the Session to access identity maps can now be called
 * through the IdentityMapAccessor.
 * <p>
 * For instance, to initialize identity maps the code used to be:
 * <code>
 * session.initializeIdentityIdentityMaps()<br></code>
 * With this class, the code now is:<code><br>
 * session.getIdentityMapAccessor().initializeIdentityMaps()
 * </code>
 * @see org.eclipse.persistence.sessions.Session
 */
public interface IdentityMapAccessor {

    /**
     * ADVANCED:
     * Clear all the query caches
     */
    public void clearQueryCache();

    /**
     * ADVANCED:
     * Clear the query class associated with the passed-in read query.
     */
    public void clearQueryCache(ReadQuery query);

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the session.
     */
    public void clearQueryCache(String sessionQueryName);

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the descriptor for the given class.
     */
    public void clearQueryCache(String descriptorQueryName, Class queryClass);

    /**
     * ADVANCED:
     * Invalidate/remove any results for any query for the class from the query cache.
     * This is used to invalidate the query cache on any change.
     */
    public void invalidateQueryCache(Class classThatChanged);

    /**
     * ADVANCED:
     * Returns true if the identity map contains an Object with the same primary 
     * key and Class type of the given domainObject.
     */
    public boolean containsObjectInIdentityMap(Object domainObject);

    /**
     * ADVANCED:
     * Returns true if the identity map contains an Object with the same
     * primary key and Class type as those specified.
     * @deprecated since EclipseLink 2.1, replaced by containsObjectInIdentityMap(Object, Class)
     * @see #containsObjectInIdentityMap(Object, Class)
     */
    @Deprecated
    public boolean containsObjectInIdentityMap(Vector primaryKey, Class theClass);
    
    /**
     * ADVANCED:
     * Returns true if the identity map contains an Object with the same
     * primary key and Class type as those specified.
     */
    public boolean containsObjectInIdentityMap(Object primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Returns true if the identity map contains an Object with the same primary key 
     * of the specified row (i.e. the database record) and Class type.
     */
    public boolean containsObjectInIdentityMap(Record rowContainingPrimaryKey, Class theClass);

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and returns matching Objects.
     * If the expression is too complex an exception will be thrown.
     * Only returns Objects that are invalid from the map if specified with the 
     * boolean shouldReturnInvalidatedObjects.
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param valueHolderPolicy see 
     * {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @param shouldReturnInvalidatedObjects boolean - true if only invalid Objects should be returned
     * @return Vector of Objects
     * @throws QueryException
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException;

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and returns matching Objects.
     * If the expression is too complex an exception will be thrown.
     * Only returns Objects that are invalid from the map if specified with the 
     * boolean shouldReturnInvalidatedObjects.
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param valueHolderPolicy see 
     * {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @param shouldReturnInvalidatedObjects boolean - true if only invalid Objects should be returned
     * @return Vector of Objects
     * @throws QueryException
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException;

    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param ClassDescriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor);
    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and returns matching Objects.
     * If the expression is too complex an exception will be thrown.
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param valueHolderPolicy  see 
     * {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @return Vector of Objects with type theClass and matching the selectionCriteria
     * @throws QueryException
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy) throws QueryException;

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and returns matching Objects.
     * If the expression is too complex an exception will be thrown.
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param valueHolderPolicy  see 
     * {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @return Vector of Objects with type theClass and matching the selectionCriteria
     * @throws QueryException
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy) throws QueryException;

    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key 
     * and Class type of the given domainObject. 
     * @param domainObject Object
     * @return Object from identity map, may be null.
     */
    public Object getFromIdentityMap(Object domainObject);

    
    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key 
     * and Class type as those specified.
     * @return Object from identity map, may be null.
     * @deprecated since EclipseLink 2.1, replaced by getFromIdentityMap(Object, Class)
     * @see #getFromIdentityMap(Object, Class)
     */
    @Deprecated
    public Object getFromIdentityMap(Vector primaryKey, Class theClass);
   
    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key 
     * and Class type as those specified.
     * @return Object from identity map, may be null.
     */
    public Object getFromIdentityMap(Object primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key 
     * of the specified row (i.e. the database record) and Class type.
     * @param rowContainingPrimaryKey Record
     * @param theClass Class
     * @return Object from identity map, may be null.
     */
    public Object getFromIdentityMap(Record rowContainingPrimaryKey, Class theClass);

    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key and Class type
     * as specified. May return null and will only return an Object that is invalidated 
     * if specified with the boolean shouldReturnInvalidatedObjects.
     * @return Object from identity map, may be null.
     * @deprecated since EclipseLink 2.1, replaced by getFromIdentityMap(Object, Class, boolean)
     * @see #getFromIdentityMap(Object, Class, boolean)
     */
    @Deprecated
    public Object getFromIdentityMap(Vector primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects);
    
    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key and Class type
     * as specified. May return null and will only return an Object that is invalidated 
     * if specified with the boolean shouldReturnInvalidatedObjects.
     * @return Object from identity map, may be null.
     */
    public Object getFromIdentityMap(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects);

    /**
     * ADVANCED:
     * Returns the Object from the identity map with the same primary key of the specified 
     * row and Class type. May return null and will only Only return an Object that is 
     * invalidated if specified with the boolean shouldReturnInvalidatedObjects.
     * @param rowContainingPrimaryKey Record
     * @param theClass Class
     * @param shouldReturnInvalidatedObjects boolean
     * @return Object from identity map, may be null.
     */
    public Object getFromIdentityMap(Record rowContainingPrimaryKey, Class theClass, boolean shouldReturnInvalidatedObjects);

    /**
     * ADVANCED:
     * Queries the cache in-memory and returns an Object from this identity map.
     * If the Object is not found with the passed in Class type, Row and selectionCriteria, 
     * null is returned. If the expression is too complex an exception will be thrown.
     * @param selectionCriteria Expression
     * @param theClass Class
     * @param translationRow Record
     * @return Object from identity map, may be null
     * @throws QueryException
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow) throws QueryException;

    /**
     * ADVANCED:
     * Queries the cache in-memory and returns an Object from this identity map.
     * If the Object is not found with the passed in Class type, Row and selectionCriteria, 
     * null is returned. This method allows for control of un-instantiated indirection access 
     * with valueHolderPolicy. If the expression is too complex an exception will be thrown.
     * @param selectionCriteria Expression
     * @param theClass Class
     * @param translationRow Record
     * @param valueHolderPolicy 
     * see {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @return Object from identity map, may be null
     * @throws QueryException
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy) throws QueryException;

    /**
     * ADVANCED:
     * Queries the cache in-memory and returns an Object from this identity map.
     * If the Object is not found with the passed in Class type, Row and selectionCriteria, 
     * null is returned. This method allows for control of un-instantiated indirection access 
     * with valueHolderPolicy. If the expression is too complex an exception will be thrown.
     * @param selectionCriteria Expression
     * @param theClass Class
     * @param translationRow Record
     * @param valueHolderPolicy 
     * see {@link org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy InMemoryQueryIndirectionPolicy}
     * @return Object from identity map, may be null
     * @throws QueryException
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy) throws QueryException;

    /**
     * ADVANCED:
     * Returns the remaining life of the given Object.  This method is associated with use of
     * cache invalidation feature and returns the difference between the next expiry
     * time of the Object and its read time.  The method will return 0 for invalidated Objects.
     * @param object Object under consideration
     * @return long time in milliseconds
     */
    public long getRemainingValidTime(Object object);

    /**
     * ADVANCED:
     * Extracts and returns the write lock value from the identity map through the given Object.
     * Write lock values are used when optimistic locking is stored in the cache instead of the object.
     * @param domainObject Object
     * @return Object for versioning
     */
    public Object getWriteLockValue(Object domainObject);

    /**
     * ADVANCED:
     * Extracts the write lock value from the identity map through the passed in primaryKey and Class type.
     * Write lock values are used when optimistic locking is stored in the cache instead of the object.
     * @deprecated since EclipseLink 2.1, replaced by getWriteLockValue(Object, Class)
     * @see #getWriteLockValue(Object, Class)
     */
    @Deprecated
    public Object getWriteLockValue(Vector primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Extracts the write lock value from the identity map through the passed in primaryKey and Class type.
     * Write lock values are used when optimistic locking is stored in the cache instead of the object.
     */
    public Object getWriteLockValue(Object primaryKey, Class theClass);

    /**
     * PUBLIC:
     * Resets the entire Object cache.
     * <p> NOTE: Be careful using this method. This method blows away both this session's and its parent's caches.
     * This includes the server cache or any other cache. This throws away any Objects that have been read in.
     * Extreme caution should be used before doing this because Object identity will no longer
     * be maintained for any Objects currently read in.  This should only be called
     * if the application knows that it no longer has references to Objects held in the cache.
     */
    public void initializeAllIdentityMaps();

    /**
     * PUBLIC:
     * Resets the identity map for only the instances of the given Class type.
     * For inheritance the user must make sure that they only use the root class.
     * <p> NOTE: Caution must be used in doing this to ensure that the Objects within the identity map
     * are not referenced from other Objects of other classes or from the application.
     * @param theClass Class
     */
    public void initializeIdentityMap(Class theClass);

    /**
     * PUBLIC:
     * Resets the entire local Object cache.
     * <p> NOTE: This throws away any Objects that have been read in.
     * Extreme caution should be used before doing this because Object identity will no longer
     * be maintained for any Objects currently read in.  This should only be called
     * if the application knows that it no longer has references to Objects held in the cache.
     */
    public void initializeIdentityMaps();

    /**
     * ADVANCED:
     * Sets an Object to be invalid in the cache.
     * If this Object does not exist in the cache, this method will return
     * without any action.
     * @param object Object
     */
    public void invalidateObject(Object object);

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Object object, boolean invalidateCluster);

    /**
     * ADVANCED:
     * Sets an Object with the specified primary key and Class type to be invalid in 
     * the cache. If the Object does not exist in the cache, 
     * this method will return without any action.
     * @deprecated since EclipseLink 2.1, replaced by invalidateObject(Object, Class)
     * @see #invalidateObject(Object, Class)
     */
    @Deprecated
    public void invalidateObject(Vector primaryKey, Class theClass);
    
    /**
     * ADVANCED:
     * Sets an Object with the specified primary key and Class type to be invalid in 
     * the cache. If the Object does not exist in the cache, 
     * this method will return without any action.
     */
    public void invalidateObject(Object primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     * @deprecated since EclipseLink 2.1, replaced by invalidateObject(Object, Class, boolean)
     * @see #invalidateObject(Object, Class, boolean)
     */
    @Deprecated
    public void invalidateObject(Vector primaryKey, Class theClass, boolean invalidateCluster);
    
    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Object primaryKey, Class theClass, boolean invalidateCluster);
    
    /**
     * ADVANCED:
     * Sets an Object with the specified primary key of the passed in Row and Class type to 
     * be invalid in the cache. If the Object does not exist in the cache, 
     * this method will return without any action.
     */
    public void invalidateObject(Record rowContainingPrimaryKey, Class theClass);

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Record rowContainingPrimaryKey, Class theClass, boolean invalidateCluster);
    
    /**
     * ADVANCED:
     * Sets all of the Objects in the given collection to be invalid in the TopLink identity maps.
     * This method will take no action for any Objects in the collection that do not exist in the cache.
     * @param collection objects to be invalidated
     */
    public void invalidateObjects(Collection collection);

    /**
     * ADVANCED:
     * Set all of the objects in the given collection to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObjects(Collection collection, boolean invalidateCluster);
    
    /**
     * ADVANCED:
     * Sets all of the Objects matching the given Expression to be invalid in the cache.
     * <p>
     * <b>Example</b> - Invalidating Employee Objects with non-null first names: 
     * <p> 
     * <code>
     *  ExpressionBuilder eb = new ExpressionBuilder(Employee.class);    <br>
     *  Expression exp = eb.get("firstName").notNull();                  <br>
     *  session.getIdentityMapAccessor().invalidateObjects(exp);         <br> 
     * </code>
     * @param selectionCriteria Expression
     */
    public void invalidateObjects(Expression selectionCriteria);

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and invalidates matching Objects.
     * If the expression is too complex either all or none object of theClass invalidated (depending on shouldInvalidateOnException value).
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param shouldInvalidateOnException boolean indicates weather to invalidate the object if conform threw exception.
     */
    public void invalidateObjects(Expression selectionCriteria, Class theClass, Record translationRow, boolean shouldInvalidateOnException);
    
    /**
     * ADVANCED:
     * Sets all of the Objects for all classes to be invalid in the cache. 
     * It will recurse on inheritance.
     */
    public void invalidateAll();
    
    /**
     * ADVANCED:
     * Sets all of the Objects of the specified Class type to be invalid in the cache.
     * Will set the recurse on inheritance to true.
     */
    public void invalidateClass(Class theClass);

    /**
     * ADVANCED:
     * Sets all of the Objects of the specified Class type to be invalid in the cache.
     * User can set the recurse flag to false if they do not want to invalidate
     * all the same Class types within an inheritance tree.
     * @param theClass Class
     * @param recurse boolean
     */
    public void invalidateClass(Class theClass, boolean recurse);

    /**
     * ADVANCED:
     * Returns true if an Object with the same primary key and Class type of the
     * the given Object is valid in the cache.
     */
    public boolean isValid(Object object);

    /**
     * ADVANCED:
     * Returns true if the Object described by the given primary key and Class type is valid 
     * in the cache.
     * @deprecated since EclipseLink 2.1, replaced by isValid(Object, Class)
     * @see #isValid(Object, Class)
     */
    @Deprecated
    public boolean isValid(Vector primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Returns true if the Object described by the given primary key and Class type is valid 
     * in the cache.
     */
    public boolean isValid(Object primaryKey, Class theClass);

    /**
     * ADVANCED:
     * Returns true if this Object with the given primary key of the Row and Class type 
     * given is valid in the cache.
     */
    public boolean isValid(Record rowContainingPrimaryKey, Class theClass);

    /**
     * PUBLIC:
     * Used to print all the Objects in the identity map of the given Class type.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMap(Class theClass);

    /**
     * PUBLIC:
     * Used to print all the Objects in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMaps();

    /**
     * PUBLIC:
     * Used to print all the locks in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at FINEST level.
     */
    public void printIdentityMapLocks();

    /**
     * ADVANCED:
     * Registers the given Object with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     * @param domainObject Object
     * @return Object
     */
    public Object putInIdentityMap(Object domainObject);

    /**
     * ADVANCED:
     * Registers the Object and given key with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     * @deprecated since EclipseLink 2.1, replaced by putInIdentityMap(Object, Object)
     * @see #putInIdentityMap(Object, Object)
     */
    @Deprecated
    public Object putInIdentityMap(Object domainObject, Vector key);

    /**
     * ADVANCED:
     * Registers the Object and given key with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     */
    public Object putInIdentityMap(Object domainObject, Object key);

    /**
     * ADVANCED:
     * Registers the Object and given key with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     * @deprecated since EclipseLink 2.1, replaced by putInIdentityMap(Object, Object, Object)
     * @see #putInIdentityMap(Object, Object, Object)
     */
    @Deprecated
    public Object putInIdentityMap(Object domainObject, Vector key, Object writeLockValue);

    /**
     * ADVANCED:
     * Registers the Object and given key with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     */
    public Object putInIdentityMap(Object domainObject, Object key, Object writeLockValue);

    /**
     * ADVANCED:
     * Registers the given Object with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     * @param domainObject Object
     * @param key Object
     * @param writeLockValue Object for versioning
     * @param readTime long, time in milliseconds
     * @return Object the Object put into the identity map
     * @deprecated since EclipseLink 2.1, replaced by putInIdentityMap(Object, Object, Object, long)
     * @see #putInIdentityMap(Object, Object, Object, long)
     */
    @Deprecated
    public Object putInIdentityMap(Object domainObject, Vector key, Object writeLockValue, long readTime);

    /**
     * ADVANCED:
     * Registers the given Object with the identity map.
     * The Object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     * @param domainObject Object
     * @param key Object
     * @param writeLockValue Object for versioning
     * @param readTime long, time in milliseconds
     * @return Object the Object put into the identity map
     */
    public Object putInIdentityMap(Object domainObject, Object key, Object writeLockValue, long readTime);

    /**
     * ADVANCED:
     * Removes the Object from the Object cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     * @param domainObject Object
     * @return Object the Object removed from the identity map
     */
    public Object removeFromIdentityMap(Object domainObject);

    /**
     * ADVANCED:
     * Removes the Object with given primary key and Class from the Object cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     * @return Object the Object removed from the identity map
     * @deprecated since EclipseLink 2.1, replaced by removeFromIdentityMap(Object, Class)
     * @see #removeFromIdentityMap(Object, Class)
     */
    @Deprecated
    public Object removeFromIdentityMap(Vector key, Class theClass);

    /**
     * ADVANCED:
     * Removes the Object with given primary key and Class from the Object cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     * @return Object the Object removed from the identity map
     */
    public Object removeFromIdentityMap(Object key, Class theClass);

    /**
     * ADVANCED:
     * Updates the write lock value in the identity map for cache key of the primary key
     * the given Object.
     * @param domainObject Object
     * @param writeLockValue Object for versioning
     */
    public void updateWriteLockValue(Object domainObject, Object writeLockValue);

    /**
     * ADVANCED:
     * Updates the write lock value in the cache for the Object with same primary key as the given Object.
     * The write lock values is used when optimistic locking is stored in the cache instead of in the object.
     * @deprecated since EclipseLink 2.1, replaced by updateWriteLockValue(Object, Class, Object)
     * @see #updateWriteLockValue(Object, Class, Object)
     */
    @Deprecated
    public void updateWriteLockValue(Vector primaryKey, Class theClass, Object writeLockValue);

    /**
     * ADVANCED:
     * Updates the write lock value in the cache for the Object with same primary key as the given Object.
     * The write lock values is used when optimistic locking is stored in the cache instead of in the object.
     */
    public void updateWriteLockValue(Object primaryKey, Class theClass, Object writeLockValue);

    /**
     * ADVANCED:
     * This can be used to help debugging an Object identity problem.
     * An Object identity problem is when an Object in the cache references an 
     * Object that is not in the cache. This method will validate that all cached 
     * Objects are in a correct state.
     */
    public void validateCache();
}
