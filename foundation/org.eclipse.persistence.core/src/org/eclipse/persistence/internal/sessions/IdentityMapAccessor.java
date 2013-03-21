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
 *     31/05/2010-2.1 Michael O'Brien
 *         - 312503: invalidateClass(Class, recurseFlag) - when recurseFlag=false
 *         (non-default) will now invalidate the implementing subtree
 *           from [Class] down.  Previously only the single Class inside the tree was invalidated.
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.CacheIndex;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;

/**
 * INTERNAL:
 * Internal subclass that provides access to identity maps through the session.
 * Implements the IdentityMapAccessor interface which provides all publicly available
 * identity map functionality to users.
 * This is the main class that should be used to access identity maps.  In general, any
 * function that accesses the identity map manager should go through this class
 * Any session specific functionality appears in subclasses
 */
public class IdentityMapAccessor implements org.eclipse.persistence.sessions.IdentityMapAccessor, java.io.Serializable {

    /** This is the identity map manager for this accessor.  It should only be accessed through the getter **/
    protected IdentityMapManager identityMapManager = null;
    protected AbstractSession session = null;

    /**
     * INTERNAL:
     */
    public IdentityMapAccessor() {
    }
    
    /**
     * INTERNAL:
     * An IdentityMapAccessor sits between the session and the identityMapManager
     * It needs references in both directions.
     */
    public IdentityMapAccessor(AbstractSession session) {
        this.session = session;
    }
    
    /**
     * INTERNAL:
     * An IdentityMapAccessor sits between the session and the identityMapManager
     * It needs references in both directions.
     */
    public IdentityMapAccessor(AbstractSession session, IdentityMapManager identityMapManager) {
        this.session = session;
        this.identityMapManager = identityMapManager;
    }

    /**
     * INTERNAL:
     * Deferred lock the identity map for the object, this is used for avoiding deadlock
     * The return cacheKey should be used to release the deferred lock.
     */
    public CacheKey acquireDeferredLock(Object primarKey, Class javaClass, ClassDescriptor descriptor, boolean isCacheCheckComplete) {
        return getIdentityMapManager().acquireDeferredLock(primarKey, javaClass, descriptor, isCacheCheckComplete);
    }

    /**
     * INTERNAL:
     * Lock the identity map for the object, this must be done when building objects.
     * The return cacheKey should be used to release the lock.
     */
    public CacheKey acquireLock(Object primarKey, Class javaClass, ClassDescriptor descriptor, boolean isCacheCheckComplete) {
        return acquireLock(primarKey, javaClass, false, descriptor, isCacheCheckComplete);
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * Called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLock(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor, boolean isCacheCheckComplete) {
        return getIdentityMapManager().acquireLock(primaryKey, domainClass, forMerge, descriptor, isCacheCheckComplete);
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * Called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLockNoWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor) {
        return getIdentityMapManager().acquireLockNoWait(primaryKey, domainClass, forMerge, descriptor);
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * Called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLockWithWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor, int wait) {
        return getIdentityMapManager().acquireLockWithWait(primaryKey, domainClass, forMerge, descriptor, wait);
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        return getIdentityMapManager().acquireReadLockOnCacheKey(primaryKey, domainClass, descriptor);
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     * If no readlock can be acquired then do not wait but return null.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        return getIdentityMapManager().acquireReadLockOnCacheKeyNoWait(primaryKey, domainClass, descriptor);
    }

    /**
     * INTERNAL:
     * Lock the entire cache if the cache isolation requires.
     * By default concurrent reads and writes are allowed.
     * By write, unit of work merge is meant.
     */
    public boolean acquireWriteLock() {
        return getIdentityMapManager().acquireWriteLock();
    }

    /**
     * ADVANCED:
     * Clear all the query caches
     */
    public void clearQueryCache() {
        getIdentityMapManager().clearQueryCache();
    }

    /**
     * ADVANCED:
     * Clear the query class associated with the passed-in read query
     */
    public void clearQueryCache(ReadQuery query) {
        getIdentityMapManager().clearQueryCache(query);
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the session
     */
    public void clearQueryCache(String sessionQueryName) {
        getIdentityMapManager().clearQueryCache((ReadQuery)session.getQuery(sessionQueryName));
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the descriptor for the given class
     */
    public void clearQueryCache(String descriptorQueryName, Class queryClass) {
        getIdentityMapManager().clearQueryCache((ReadQuery)session.getDescriptor(queryClass).getQueryManager().getQuery(descriptorQueryName));
    }

    /**
     * ADVANCED:
     * Return if their is an object for the primary key.
     */
    public boolean containsObjectInIdentityMap(Object object) {
        return containsObjectInIdentityMap(getSession().getId(object), object.getClass());
    }

    /**
     * Convert the primary key Vector into a primary key object.
     * This is used to support the old deprecated Vector API.
     */
    public Object primaryKeyFromVector(Vector primaryKeyVector) {
        if (primaryKeyVector == null) {
            return null;
        } else if (primaryKeyVector.size() == 1) {
            return primaryKeyVector.get(0);            
        } else {
            return new CacheId(primaryKeyVector.toArray());
        }
    }
    
    /**
     * ADVANCED:
     * Return if their is an object for the primary key.
     */
    @Deprecated
    public boolean containsObjectInIdentityMap(Vector primaryKey, Class theClass) {
        return containsObjectInIdentityMap(primaryKeyFromVector(primaryKey), theClass);
    }

    /**
     * ADVANCED:
     * Return if their is an object for the primary key.
     */
    public boolean containsObjectInIdentityMap(Object primaryKey, Class theClass) {
        ClassDescriptor descriptor = getSession().getDescriptor(theClass);
        return containsObjectInIdentityMap(primaryKey, theClass, descriptor);
    }
    
    /**
     * INTERNAL:
     * Return if their is an object for the primary key.
     */
    public boolean containsObjectInIdentityMap(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        return getIdentityMapManager().containsKey(primaryKey, theClass, descriptor);
    }

    /**
     * ADVANCED:
     * Return if their is an object for the row containing primary key and the class.
     */
    public boolean containsObjectInIdentityMap(Record rowContainingPrimaryKey, Class theClass) {
        return containsObjectInIdentityMap(extractPrimaryKeyFromRow(rowContainingPrimaryKey, theClass), theClass);
    }

    /**
     * INTERNAL:
     * Extract primary key from a row.
     */
    protected Object extractPrimaryKeyFromRow(Record rowContainingPrimaryKey, Class theClass) {
        return this.session.getDescriptor(theClass).getObjectBuilder().extractPrimaryKeyFromRow((AbstractRecord)rowContainingPrimaryKey, this.session);
    }

    /**
     * INTERNAL:
     * Retrieve the cache key for the given object from the identity maps.
     * @param object the object to get the cache key for.
     */
    public CacheKey getCacheKeyForObject(Object object, ClassDescriptor descriptor) {
        return getCacheKeyForObject(getSession().keyFromObject(object, descriptor), object.getClass(), descriptor, false);
    }

    /**
     * INTERNAL:
     * This method is used to get a list of those classes with IdentityMaps in the Session.
     */
    public Vector getClassesRegistered() {
        return getIdentityMapManager().getClassesRegistered();
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow) throws QueryException {
        return getAllFromIdentityMap(selectionCriteria, theClass, translationRow, InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION, true);
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy) throws QueryException {
        return getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, true);
    }
    
    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy) throws QueryException {
        return getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, true);
    }
    
    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     * Only return objects that are invalid in the cache if specified.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException {
        int policy = 0;
        if (valueHolderPolicy != null) {
            policy = valueHolderPolicy.getPolicy();
        }
        return getAllFromIdentityMap(selectionCriteria, theClass, translationRow, policy, shouldReturnInvalidatedObjects);
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     * Only return objects that are invalid in the cache if specified.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException {
        return getIdentityMapManager().getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, shouldReturnInvalidatedObjects);
    }
    
    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param ClassDescriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor){
        return getIdentityMapManager().getAllFromIdentityMapWithEntityPK(pkList, descriptor, getSession());
    }

    /**
     * ADVANCED:
     * Return the object from the identity with primary and class of the given object.
     */
    public Object getFromIdentityMap(Object object) {
        return getFromIdentityMap(getSession().getId(object), object.getClass());
    }

    /**
     * INTERNAL:
     * Retrieve the cache key for the given object.
     * @return CacheKey
     */
    public CacheKey getCacheKeyForObject(Object object) {
        return getCacheKeyForObject(getSession().getId(object), object.getClass(), getSession().getDescriptor(object.getClass()), false);
    }
    
    /**
     * INTERNAL:
     * Retrieve the cache key for the given identity information.
     * @param primaryKey the primary key of the cache key to be retrieved.
     * @param myClass the class of the cache key to be retrieved.
     */
    public CacheKey getCacheKeyForObjectForLock(Object primaryKey, Class myClass, ClassDescriptor descriptor) {
        return getIdentityMapManager().getCacheKeyForObjectForLock(primaryKey, myClass, descriptor);
    }

    /**
     * INTERNAL:
     * Retrieve the cache key for the given identity information.
     * @param primaryKey the primary key of the cache key to be retrieved.
     * @param myClass the class of the cache key to be retrieved.
     */
    public CacheKey getCacheKeyForObject(Object primaryKey, Class myClass, ClassDescriptor descriptor, boolean forMerge) {
        return getIdentityMapManager().getCacheKeyForObject(primaryKey, myClass, descriptor, forMerge);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     */
    @Deprecated
    public Object getFromIdentityMap(Vector primaryKey, Class theClass) {
        return getFromIdentityMap(primaryKeyFromVector(primaryKey), theClass);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     */
    public Object getFromIdentityMap(Object primaryKey, Class theClass) {
        return getFromIdentityMap(primaryKey, theClass, true);
    }
    
    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     */
    public Object getFromIdentityMap(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        return getFromIdentityMap(primaryKey, null, theClass, true, descriptor);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested.
     */
    @Deprecated
    public Object getFromIdentityMap(Vector primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects) {
        return getFromIdentityMap(primaryKeyFromVector(primaryKey), theClass, shouldReturnInvalidatedObjects);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested.
     */
    public Object getFromIdentityMap(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects) {
        return getFromIdentityMap(primaryKey, null, theClass, shouldReturnInvalidatedObjects, getSession().getDescriptor(theClass));
    }
    
    /**
     * INTERNAL:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested.
     */
    public Object getFromIdentityMap(Object primaryKey, Object object, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return getIdentityMapManager().getFromIdentityMap(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }
    
    /**
     * INTERNAL:
     * Return the object from the local identity map with the primary and class.
     * This avoids checking the parent cache for the unit of work.
     */
    public Object getFromLocalIdentityMap(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return getIdentityMapManager().getFromIdentityMap(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * Return the object from the local identity map with the primary and class.
     * This avoids checking the parent cache for the unit of work.
     */
    public Object getFromLocalIdentityMapWithDeferredLock(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor){
        return getIdentityMapManager().getFromIdentityMapWithDeferredLock(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     */
    public Object getFromIdentityMap(Record rowContainingPrimaryKey, Class theClass) {
        return getFromIdentityMap(extractPrimaryKeyFromRow(rowContainingPrimaryKey, theClass), theClass);
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested.
     */
    public Object getFromIdentityMap(Record rowContainingPrimaryKey, Class theClass, boolean shouldReturnInvalidatedObjects) {
        return getFromIdentityMap(extractPrimaryKeyFromRow(rowContainingPrimaryKey, theClass), theClass, shouldReturnInvalidatedObjects);
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow) throws QueryException {
        return getFromIdentityMap(selectionCriteria, theClass, translationRow, InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION);
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, InMemoryQueryIndirectionPolicy valueHolderPolicy) throws QueryException {
        int policy = 0;
        if (valueHolderPolicy != null) {
            policy = valueHolderPolicy.getPolicy();
        }
        return getFromIdentityMap(selectionCriteria, theClass, translationRow, policy, false);
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy) throws QueryException {
        return getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, false);
    }
    
    /**
     * INTERNAL:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean conforming) {
        return getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, conforming, true, getSession().getDescriptor(theClass));
    }

    /**
     * INTERNAL:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean conforming, boolean shouldReturnInvalidatedObjects) {
        return getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, conforming, shouldReturnInvalidatedObjects, getSession().getDescriptor(theClass));
    }
    
    /**
     * INTERNAL:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean conforming, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return getIdentityMapManager().getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, conforming, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * Return the object from the identity with the primary and class.
     */
    public Object getFromIdentityMapWithDeferredLock(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        return getFromIdentityMapWithDeferredLock(primaryKey, theClass, true, descriptor);
    }

    /**
     * INTERNAL:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested
     */
    public Object getFromIdentityMapWithDeferredLock(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return getIdentityMapManager().getFromIdentityMapWithDeferredLock(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * Get the IdentityMapManager for this IdentityMapAccessor
     * This method should be used for all IdentityMapManager access since it may
     * be overridden in sub classes.
     */
    public IdentityMapManager getIdentityMapManager() {
        if (session.hasBroker()) {
            return getSession().getBroker().getIdentityMapAccessorInstance().getIdentityMapManager();
        }
        return identityMapManager;
    }    
    
    /**
     * INTERNAL: (public to allow test cases to check)
     * Return the identity map for the class, if missing create a new one.
     */
    public IdentityMap getIdentityMap(Class theClass) {
        ClassDescriptor descriptor = getSession().getDescriptor(theClass);
        if (descriptor == null) {
            throw ValidationException.missingDescriptor(theClass.toString());
        }
        return getIdentityMap(descriptor);
    }

    /**
     * INTERNAL:
     * Get the identity map for the given class from the IdentityMapManager
     */
    public IdentityMap getIdentityMap(ClassDescriptor descriptor) {
        return getIdentityMap(descriptor, false);
    }

    /**
     * INTERNAL:
     * Get the identity map for the given class from the IdentityMapManager
     */
    public IdentityMap getIdentityMap(ClassDescriptor descriptor, boolean returnNullIfMissing) {
        return getIdentityMapManager().getIdentityMap(descriptor, returnNullIfMissing);
    }

    /**
     * INTERNAL:
     * Get the cached results associated with a query.  Results are cached by the
     * values of the parameters to the query so different parameters will have
     * different cached results.
     */
    public Object getQueryResult(ReadQuery query, List parameters, boolean checkExpiry) {
        return getIdentityMapManager().getQueryResult(query, parameters, checkExpiry);
    }

    /**
     * ADVANCED:
     * Return the remaining life of this object.  This method is associated with use of
     * cache invalidation and returns the difference between the next expiry
     * time of the object and its read time.  The method will return 0 for invalidated objects.
     */
    public long getRemainingValidTime(Object object) {
        Object primaryKey = getSession().getId(object);
        ClassDescriptor descriptor = getSession().getDescriptor(object);
        CacheKey key = getCacheKeyForObjectForLock(primaryKey, object.getClass(), descriptor);
        if (key == null) {
            throw QueryException.objectDoesNotExistInCache(object);
        }
        return descriptor.getCacheInvalidationPolicy().getRemainingValidTime(key);
    }

    /**
     * INTERNAL:
     * get the session associated with this IdentityMapAccessor
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB.
     */
    public Object getWrapper(Object primaryKey, Class theClass) {
        return getIdentityMapManager().getWrapper(primaryKey, theClass);
    }

    /**
     * INTERNAL:
     * Returns the single write Lock manager for this session
     */
    public WriteLockManager getWriteLockManager() {
        return getIdentityMapManager().getWriteLockManager();
    }

    /**
    * ADVANCED:
    * Extract the write lock value from the identity map.
    */
    public Object getWriteLockValue(Object object) {
        return getWriteLockValue(getSession().getId(object), object.getClass());
    }

    /**
     * ADVANCED:
     * Extract the write lock value from the identity map.
     */
    @Deprecated
    public Object getWriteLockValue(Vector primaryKey, Class theClass) {
        return getWriteLockValue(primaryKeyFromVector(primaryKey), theClass);
    }

    /**
     * ADVANCED:
     * Extract the write lock value from the identity map.
     */
    public Object getWriteLockValue(Object primaryKey, Class theClass) {
        return getWriteLockValue(primaryKey, theClass, getSession().getDescriptor(theClass));
    }
        
    /**
     * ADVANCED:
     * Extract the write lock value from the identity map.
     */
    public Object getWriteLockValue(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        return getIdentityMapManager().getWriteLockValue(primaryKey, theClass, descriptor);
    }

    /**
     * PUBLIC:
     * Reset the entire object cache.
     * <p> NOTE: be careful using this method. This method blows away both this session's and its parents caches,
     * this includes the server cache or any other cache. This throws away any objects that have been read in.
     * Extreme caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeAllIdentityMaps() {
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "initialize_all_identitymaps");
        getIdentityMapManager().initializeIdentityMaps();
    }

    /**
     * PUBLIC:
     * Reset the identity map for only the instances of the class.
     * For inheritance the user must make sure that they only use the root class.
     * Caution must be used in doing this to ensure that the objects within the identity map
     * are not referenced from other objects of other classes or from the application.
     */
    public void initializeIdentityMap(Class theClass) {
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "initialize_identitymap", theClass);
        getIdentityMapManager().initializeIdentityMap(theClass);
    }

    /**
     * PUBLIC:
     * Reset the entire local object cache.
     * This throws away any objects that have been read in.
     * Extreme caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeIdentityMaps() {
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "initialize_identitymaps");
        getIdentityMapManager().initializeIdentityMaps();
        if (getSession().hasCommitManager()) {
            getSession().getCommitManager().reinitialize();
        }
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * If the object does not exist in the cache, this method will return
     * without any action.
     */
    public void invalidateObject(Object object) {
        invalidateObject(object, false);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Object object, boolean invalidateCluster) {
        invalidateObject(getSession().getId(object), object.getClass(), invalidateCluster);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * If the object does not exist in the cache, this method will return
     * without any action.
     */
    @Deprecated
    public void invalidateObject(Vector primaryKey, Class theClass, boolean invalidateCluster) {
        invalidateObject(primaryKeyFromVector(primaryKey), theClass, invalidateCluster);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * If the object does not exist in the cache, this method will return
     * without any action.
     */
    @Deprecated
    public void invalidateObject(Vector primaryKey, Class theClass) {
        invalidateObject(primaryKeyFromVector(primaryKey), theClass);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * If the object does not exist in the cache, this method will return
     * without any action.
     */
    public void invalidateObject(Object primaryKey, Class theClass) {
        invalidateObject(primaryKey, theClass, false);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Object primaryKey, Class theClass, boolean invalidateCluster) {
        if (primaryKey == null) {
            return;
        }
        ClassDescriptor descriptor = getSession().getDescriptor(theClass);
        //forward the call to getCacheKeyForObject locally in case subclasses overload
        CacheKey key = getCacheKeyForObjectForLock(primaryKey, theClass, descriptor);
        if (key != null) {
            key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
        }
        if (invalidateCluster) {
            CommandManager rcm = getSession().getCommandManager();
            if (rcm != null) {
                UnitOfWorkChangeSet changeSet = new UnitOfWorkChangeSet(getSession());
                ObjectChangeSet objectChangeSet = new ObjectChangeSet(primaryKey, descriptor, null, changeSet, false);
                objectChangeSet.setSynchronizationType(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
                changeSet.getAllChangeSets().put(objectChangeSet, objectChangeSet);
                MergeChangeSetCommand command = new MergeChangeSetCommand();
                command.setChangeSet(changeSet);
                try {
                    command.convertChangeSetToByteArray(getSession());
                } catch (java.io.IOException exception) {
                    throw CommunicationException.unableToPropagateChanges(command.getServiceId().getId(), exception);
                }
                rcm.propagateCommand(command);
            }
        }
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * If the object does not exist in the cache, this method will return
     * without any action.
     */
    public void invalidateObject(Record rowContainingPrimaryKey, Class theClass) {
        invalidateObject(rowContainingPrimaryKey, theClass, false);
    }

    /**
     * ADVANCED:
     * Set an object to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObject(Record rowContainingPrimaryKey, Class theClass, boolean invalidateCluster) {
        invalidateObject(extractPrimaryKeyFromRow(rowContainingPrimaryKey, theClass), theClass, invalidateCluster);
    }

    /**
     * ADVANCED:
     * Set all of the objects from the given Expression to be invalid in the cache.
     */
    public void invalidateObjects(Expression selectionCriteria) {
        invalidateObjects(selectionCriteria, selectionCriteria.getBuilder().getQueryClass(), new DatabaseRecord(0), true);
    }

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and invalidates matching Objects.
     * If the expression is too complex either all or none object of theClass invalidated (depending on shouldInvalidateOnException value).
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param shouldInvalidateOnException boolean indicates weather to invalidate the object if conform threw exception.
     */
    public void invalidateObjects(Expression selectionCriteria, Class theClass, Record translationRow, boolean shouldInvalidateOnException) {
        getIdentityMapManager().invalidateObjects(selectionCriteria, theClass, translationRow, shouldInvalidateOnException);
    }
    
    /**
     * ADVANCED:
     * Set all of the objects in the given collection to be invalid in the cache.
     * This method will take no action for any objects in the collection that do not exist in the cache.
     */
    public void invalidateObjects(Collection collection) {
        invalidateObjects(collection, false);
    }
    
    /**
     * ADVANCED:
     * Set all of the objects in the given collection to be invalid in the cache.
     * @param invalidateCluster if true the invalidation will be broadcast to each server in the cluster.
     */
    public void invalidateObjects(Collection collection, boolean invalidateCluster) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            invalidateObject(iterator.next(), invalidateCluster);
        }
    }

    /**
     * ADVANCED:
     * Set all of the objects of a specific class to be invalid in the cache.
     * Will set the recurseAndInvalidateToParentRoot flag on inheritance to true.
     */
    public void invalidateClass(Class myClass) {
        invalidateClass(myClass, true);
    }

    /**
     * ADVANCED:
     * Set all of the objects of a specific class to be invalid in the cache.
     * User can set the recurse flag to false if they do not want to invalidate
     * all the classes within an inheritance tree and instead invalidate the subtree rooted at myClass.
     * @param myClass - the class where we start invalidation
     * @param recurseAndInvalidateToParentRoot - default is true where we invalidate
     *   up the inheritance tree to the root descriptor
     */
    public void invalidateClass(Class myClass, boolean recurseAndInvalidateToParentRoot) {
        //forward the call to getIdentityMap locally in case subclasses overload
        IdentityMap identityMap = this.getIdentityMap(myClass); // will always return the root IdentityMap

        //bug 227430: Deadlock in IdentityMapAccessor. 
        //removed synchronization that would result in deadlock
        //no need to synchronize as changes to identity map will not aversely affect this code
        //bug 275724: IdentityMapAccessor.invalidateClass() should not check ReadLock when invalidating
        Enumeration keys = identityMap.keys(false); // do not check readlock
        while (keys.hasMoreElements()) {
            CacheKey key = (CacheKey)keys.nextElement();
            Object obj = key.getObject();
            // 312503: When recurse is false we also invalidate all assignable implementing subclasses of [obj]
            if (recurseAndInvalidateToParentRoot || ((obj != null) && (null != myClass) && myClass.isAssignableFrom(obj.getClass()))) {
                key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
            }
        }
        invalidateQueryCache(myClass);
    }

    /**
     * Invalidate/remove any results for the class from the query cache.
     * This is used to invalidate the query cache on any change.
     */
    public void invalidateQueryCache(Class classThatChanged) {
        getIdentityMapManager().invalidateQueryCache(classThatChanged);
    }
    
    /**
     * ADVANCED:
     * Set all of the objects from all identity maps to be invalid in the cache.
     */
    public void invalidateAll() {
        Iterator identiyMapClasses = getIdentityMapManager().getIdentityMapClasses();
        
        while (identiyMapClasses.hasNext()) {
            invalidateClass((Class) identiyMapClasses.next());
        }
    }
    
    /**
     * ADVANCED:
     * Return if this object is valid in the cache.
     */
    public boolean isValid(Object object) {
        return isValid(getSession().getId(object), object.getClass());
    }

    /**
     * ADVANCED:
     * Return if this object is valid in the cache.
     */
    @Deprecated
    public boolean isValid(Vector primaryKey, Class theClass) {
        return isValid(primaryKeyFromVector(primaryKey), theClass);
    }

    /**
     * ADVANCED:
     * Return if this object is valid in the cache.
     */
    public boolean isValid(Object primaryKey, Class theClass) {
        ClassDescriptor descriptor = getSession().getDescriptor(theClass);
        //forward the call to getCacheKeyForObject locally in case subclasses overload
        CacheKey key = getCacheKeyForObjectForLock(primaryKey, theClass, descriptor);
        if (key == null) {
            throw QueryException.classPkDoesNotExistInCache(theClass, primaryKey);
        }
        return !descriptor.getCacheInvalidationPolicy().isInvalidated(key);
    }

    /**
     * ADVANCED:
     * Return if this object is valid in the cache.
     */
    public boolean isValid(Record rowContainingPrimaryKey, Class theClass) {
        return isValid(extractPrimaryKeyFromRow(rowContainingPrimaryKey, theClass), theClass);
    }

    /**
     * PUBLIC:
     * Used to print all the objects in the identity map of the passed in class.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMap(Class businessClass) {
        if (getSession().shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            getIdentityMapManager().printIdentityMap(businessClass);
        }
    }

    /**
     * PUBLIC:
     * Used to print all the objects in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMaps() {
        if (getSession().shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            getIdentityMapManager().printIdentityMaps();
        }
    }

    /**
     * PUBLIC:
     * Used to print all the locks in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at FINEST level.
     */
    public void printIdentityMapLocks() {
        if (getSession().shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            getIdentityMapManager().printLocks();
        }
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     */
    public Object putInIdentityMap(Object object) {
        return putInIdentityMap(object, getSession().getId(object));
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     */
    @Deprecated
    public Object putInIdentityMap(Object object, Vector key) {
        return putInIdentityMap(object, primaryKeyFromVector(key));
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     */
    public Object putInIdentityMap(Object object, Object key) {
        return putInIdentityMap(object, key, null);
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     */
    @Deprecated
    public Object putInIdentityMap(Object object, Vector key, Object writeLockValue) {
        return putInIdentityMap(object, key, writeLockValue, 0);
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     */
    public Object putInIdentityMap(Object object, Object key, Object writeLockValue) {
        return putInIdentityMap(object, key, writeLockValue, 0);
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     */
    @Deprecated
    public Object putInIdentityMap(Object object, Vector key, Object writeLockValue, long readTime) {
        return putInIdentityMap(object, primaryKeyFromVector(key), writeLockValue, readTime);
    }

    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     */
    public Object putInIdentityMap(Object object, Object key, Object writeLockValue, long readTime) {
        ClassDescriptor descriptor = getSession().getDescriptor(object);
        return putInIdentityMap(object, key, writeLockValue, readTime, descriptor);
    }
    
    /**
     * ADVANCED:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     */
    public Object putInIdentityMap(Object object, Object key, Object writeLockValue, long readTime, ClassDescriptor descriptor) {
        CacheKey cacheKey = internalPutInIdentityMap(object, key, writeLockValue, readTime, descriptor);
        if (cacheKey == null) {
            return null;
        }
        return cacheKey.getObject();
    }

    /**
     * INTERNAL:
     * Set the results for a query.
     * Query results are cached based on the parameter values provided to the query
     * different parameter values access different caches.
     */
    public void putQueryResult(ReadQuery query, List parameters, Object results) {
        getIdentityMapManager().putQueryResult(query, parameters, results);
    }

    /**
     * Index the cache key by the index values.
     */
    public void putCacheKeyByIndex(CacheIndex index, CacheId indexValues, CacheKey cacheKey, ClassDescriptor descriptor) {
        getIdentityMapManager().putCacheKeyByIndex(index, indexValues, cacheKey, descriptor);        
    }
    
    /**
     * Return the cache key for the cache index or null if not found.
     */
    public CacheKey getCacheKeyByIndex(CacheIndex index, CacheId indexValues, boolean shouldCheckExpiry, ClassDescriptor descriptor) { 
        return getIdentityMapManager().getCacheKeyByIndex(index, indexValues, shouldCheckExpiry, descriptor);        
    }
    
    /**
     * INTERNAL:
     * Register the object with the cache.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     * Return the cache-key.
     */
    public CacheKey internalPutInIdentityMap(Object object, Object key, Object writeLockValue, long readTime, ClassDescriptor descriptor) {
        return getIdentityMapManager().putInIdentityMap(object, key, writeLockValue, readTime, descriptor);
    }

    /**
    * INTERNAL:
    * Lock the entire cache if the cache isolation requires.
    * By default concurrent reads and writes are allowed.
    * By write, unit of work merge is meant.
    */
    public void releaseWriteLock() {
        getIdentityMapManager().releaseWriteLock();
    }

    /**
     * ADVANCED:
     * Remove the object from the object cache.
     * Caution should be used when calling to avoid violating object identity.
     * The application should only call this is it knows that no references to the object exist.
     */
    public Object removeFromIdentityMap(Object object) {
        Class theClass = object.getClass();
        return removeFromIdentityMap(getSession().getId(object), theClass, getSession().getDescriptor(theClass), object);
    }

    /**
     * ADVANCED:
     * Remove the object from the object cache.
     */
    @Deprecated
    public Object removeFromIdentityMap(Vector key, Class theClass) {
        return removeFromIdentityMap(primaryKeyFromVector(key), theClass);
    }
    
    /**
     * ADVANCED:
     * Remove the object from the object cache.
     */
    public Object removeFromIdentityMap(Object key, Class theClass) {
        ClassDescriptor descriptor = getSession().getDescriptor(theClass);
        if (descriptor == null){
            return null;
        }
        return removeFromIdentityMap(key, theClass, descriptor, null);
    }
    
    /**
     * INTERNAL:
     * Remove the object from the object cache.
     */
    public Object removeFromIdentityMap(Object key, Class theClass, ClassDescriptor descriptor, Object object) {
        return getIdentityMapManager().removeFromIdentityMap(key, theClass, descriptor, object);
    }

    /**
     * INTERNAL:
     * Set the IdentityMapManager for this IdentityMapAccessor
     */
    public void setIdentityMapManager(IdentityMapManager identityMapManager) {
        this.identityMapManager = identityMapManager;
    }

    /**
     * INTERNAL:
     * Update the wrapper object the cache key associated with the given primary key,
     * this is used for EJB.
     */
    public void setWrapper(Object primaryKey, Class theClass, Object wrapper) {
        getIdentityMapManager().setWrapper(primaryKey, theClass, wrapper);
    }

    /**
     * ADVANCED:
     * Update the write lock value in the cache.
     */
    public void updateWriteLockValue(Object object, Object writeLockValue) {
        updateWriteLockValue(getSession().getId(object), object.getClass(), writeLockValue);
    }

    /**
     * ADVANCED:
     * Update the write lock value in the cache.
     */
    @Deprecated
    public void updateWriteLockValue(Vector primaryKey, Class theClass, Object writeLockValue) {
        updateWriteLockValue(primaryKeyFromVector(primaryKey), theClass, writeLockValue);
    }

    /**
     * ADVANCED:
     * Update the write lock value in the cache.
     */
    public void updateWriteLockValue(Object primaryKey, Class theClass, Object writeLockValue) {
        getIdentityMapManager().setWriteLockValue(primaryKey, theClass, writeLockValue);
    }

    /**
     * INTERNAL:
     * This can be used to help debugging an object identity problem.
     * An object identity problem is when an object in the cache references an object not in the cache.
     * This method will validate that all cached objects are in a correct state.
     */
    public void validateCache() {
        //pass certain calls to this in order to allow subclasses to implement own behavior
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "validate_cache");
        // This define an inner class for process the iteration operation, don't be scared, its just an inner class.
        DescriptorIterator iterator = new DescriptorIterator() {
            public void iterate(Object object) {
                if (!containsObjectInIdentityMap(IdentityMapAccessor.this.session.getDescriptor(object.getClass()).getObjectBuilder().extractPrimaryKeyFromObject(object, IdentityMapAccessor.this.getSession()), object.getClass())) {
                    IdentityMapAccessor.this.session.log(SessionLog.FINEST, SessionLog.CACHE, "stack_of_visited_objects_that_refer_to_the_corrupt_object", getVisitedStack());
                    IdentityMapAccessor.this.session.log(SessionLog.FINER, SessionLog.CACHE, "corrupt_object_referenced_through_mapping", getCurrentMapping());
                    IdentityMapAccessor.this.session.log(SessionLog.FINER, SessionLog.CACHE, "corrupt_object", object);
                }
            }
        };

        iterator.setSession(getSession());
        Iterator descriptors = getSession().getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            IdentityMap cache = getIdentityMap(descriptor, true);
            if (cache != null) {
                for (Enumeration mapEnum = cache.elements(); mapEnum.hasMoreElements();) {
                    iterator.startIterationOn(mapEnum.nextElement());
                }
            }
        }
    }
}
