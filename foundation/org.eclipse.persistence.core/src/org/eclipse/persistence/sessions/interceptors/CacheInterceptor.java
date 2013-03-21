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
 *     Gordon Yorke - Initial Feature development
 ******************************************************************************/
package org.eclipse.persistence.sessions.interceptors;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;


/**
 * <p>
 * <b>Purpose</b>: Define a class through which Cache access can be
 * intercepted.
 * <p>
 * <b>Description</b>: EclipseLink makes extensive use of caching
 * functionality. This class provides the mechanism for intercepting the cache
 * access. Once intercepted applications can allow the process to continue by
 * calling the method on the class attribute targetIdentityMap as described in
 * the API javadocs or redirect the process entirely. * As with IdentityMaps an
 * entire class inheritance hierarchy will share the same interceptor.
 * <p>
 * To implement a CacheInterceptor users should subclass this class implementing
 * those methods they wish to intercept.
 * <p>
 * <b>Configuration</b>: Interceptors may be added to a session by using a
 * Session or Descriptor customizer to set the class name of the interceptor on
 * the target ClassDescriptor through the method
 * setCacheInterceptorClass(Class).
 */
public abstract class CacheInterceptor implements IdentityMap {
    /**
     * This attribute stores the actual IdentityMap as configured by the user.
     */
    protected IdentityMap targetIdentityMap;

    protected AbstractSession interceptedSession;
    
    public CacheInterceptor(IdentityMap targetIdentityMap, AbstractSession interceptedSession){
        this.targetIdentityMap = targetIdentityMap;
        this.interceptedSession = interceptedSession;
    }
    /**
     * Acquire a deferred lock on the object.
     * This is used while reading if the object has relationships without indirection.
     * This first thread will get an active lock.
     * Other threads will get deferred locks, all threads will wait until all other threads are complete before releasing their locks.
     */
    public CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete){
        return createCacheKeyInterceptor(this.targetIdentityMap.acquireDeferredLock(primaryKey, isCacheCheckComplete));
    }

    /**
     * Acquire an active lock on the object.
     * This is used by reading (when using indirection or no relationships) and by merge.
     */
    public CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete){
        return createCacheKeyInterceptor(this.targetIdentityMap.acquireLock(primaryKey, forMerge, isCacheCheckComplete));
    }

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge){
        CacheKey cacheKeyToBeWrapped = this.targetIdentityMap.acquireLockNoWait(primaryKey, forMerge);
        if (cacheKeyToBeWrapped != null){
            return createCacheKeyInterceptor(cacheKeyToBeWrapped);
        }
        return null;
    }
    
    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockWithWait(Object primaryKey, boolean forMerge, int wait) {
        CacheKey cacheKeyToBeWrapped = this.targetIdentityMap.acquireLockWithWait(primaryKey, forMerge, wait);
        if (cacheKeyToBeWrapped != null) {
            return createCacheKeyInterceptor(cacheKeyToBeWrapped);
        }
        return null;
    }

    /**
     * Acquire a read lock on the object.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey) {
        return createCacheKeyInterceptor(this.targetIdentityMap.acquireReadLockOnCacheKey(primaryKey));
    }
    
    /**
     * Acquire a read lock on the object, if not already locked.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey) {
        CacheKey cacheKeyToBeWrapped = this.targetIdentityMap.acquireReadLockOnCacheKeyNoWait(primaryKey);
        if (cacheKeyToBeWrapped != null){
            return createCacheKeyInterceptor(cacheKeyToBeWrapped);
        }
        return null;
    }
    
    /**
     * Add all locked CacheKeys to the map grouped by thread.
     * Used to print all the locks in the identity map.
     */
    public void collectLocks(HashMap threadList) {
        this.targetIdentityMap.collectLocks(threadList);
    }

    /**
     * Clone the map and all of the CacheKeys.
     * This is used by UnitOfWork commitAndResumeOnFailure to avoid corrupting the cache during a failed commit.
     */
    public abstract Object clone();

    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param primaryKey is the primary key for the object to search for.
     */
    public boolean containsKey(Object primaryKey) {
        return this.targetIdentityMap.containsKey(primaryKey);
    }
    

    protected abstract CacheKeyInterceptor createCacheKeyInterceptor(CacheKey wrappedCacheKey);
    
    /**
     * Allow for the cache to be iterated on.  This method is only used during debugging when
     * validateCache() has been called to print out the contents of the cache.
     */
    public Enumeration elements() {
        return this.targetIdentityMap.elements();
    }

    /**
     * Return the object cached in the identity map or null if it could not be found.
     * User API.
     */
    public Object get(Object primaryKey){
        return this.targetIdentityMap.get(primaryKey);
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
    public abstract Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session);

    /**
     * Get the cache key (with object) for the primary key.
     */
    public CacheKey getCacheKey(Object primaryKey, boolean forMerge) {
        return this.targetIdentityMap.getCacheKey(primaryKey, forMerge);
    }

    /**
     * Get the cache key (with object) for the primary key.
     */
    public CacheKey getCacheKeyForLock(Object primaryKey) {
        return this.targetIdentityMap.getCacheKeyForLock(primaryKey);
    }

    /**
     * Return the descriptor that this is the map for.
     */
    public ClassDescriptor getDescriptor() {
        return this.targetIdentityMap.getDescriptor();
    }

    /**
     * Return the class that this is the map for.
     */
    public Class getDescriptorClass() {
        return this.targetIdentityMap.getDescriptorClass();
    }
    
    /**
     * @return The maxSize for the IdentityMap (NOTE: some subclasses may use this differently).
     */
    public int getMaxSize() {
        return this.targetIdentityMap.getMaxSize();
    }

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    public int getSize() {
        return this.targetIdentityMap.getSize();
    }

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    public int getSize(Class myClass, boolean recurse) {
        return this.targetIdentityMap.getSize(myClass, recurse);
    }

    /**
     * Return the instance of the IdentityMap that this intercpetor is wrapping.  Any call that
     * is to be passed on to the underlying EclipseLink cache should be passed to this IdentityMap.
     */
    public IdentityMap getTargetIdenttyMap() {
        return this.targetIdentityMap;
    }
    
    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB2.
     */
    public Object getWrapper(Object primaryKey) {
        return this.targetIdentityMap.getWrapper(primaryKey);
    }
    
    /**
     * Get the write lock value from the cache key associated to the primarykey.
     * User API.
     */
    public Object getWriteLockValue(Object primaryKey) {
        return this.targetIdentityMap.getWriteLockValue(primaryKey);
    }
    
    /**
     * Allow for the CacheKeys to be iterated on.
     */
    public Enumeration keys() {
        return this.targetIdentityMap.keys();
    }

    /**
     * Allow for the CacheKeys to be iterated on. - value should be true
     * if readloacks are to be used, false otherwise.
     */
    public Enumeration keys(boolean checkReadLocks) {
        return this.targetIdentityMap.keys(checkReadLocks);
    }

    /**
     * Notify the cache that a lazy relationship has been triggered in the object
     * and the cache may need to be updated
     */
    public void lazyRelationshipLoaded(Object rootEntity, ValueHolderInterface valueHolder, ForeignReferenceMapping mapping){
        this.targetIdentityMap.lazyRelationshipLoaded(rootEntity, valueHolder, mapping);
    }

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    public CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return this.targetIdentityMap.put(primaryKey, object, writeLockValue, readTime);
    }

    /**
     * Remove the CacheKey with the primaryKey from the map.
     * This is used by DeleteObjectQuery and merge.
     * This is also an advanced (very) user API.
     */
    public Object remove(Object primaryKey, Object object) {
        return this.targetIdentityMap.remove(primaryKey, object);
    }

    /**
     * Remove the CacheKey from the map.
     */
    public Object remove(CacheKey cacheKey) {
        if (cacheKey.isWrapper()){
            return this.targetIdentityMap.remove(cacheKey.getWrappedCacheKey());
        }
        return this.targetIdentityMap.remove(cacheKey);
    }

    /**
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    public void updateMaxSize(int maxSize){
        this.targetIdentityMap.updateMaxSize(maxSize);
    }
    
    /**
     * Set the descriptor that this is the map for.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.targetIdentityMap.setDescriptor(descriptor);
    }

    /**
     * Update the wrapper object in the CacheKey associated with the given primaryKey,
     * this is used for EJB2.
     */
    public void setWrapper(Object primaryKey, Object wrapper) {
        this.targetIdentityMap.setWrapper(primaryKey, wrapper);
    }

    /**
     * Update the write lock value of the CacheKey associated with the given primaryKey.
     * This is used by UpdateObjectQuery, and is also an advanced (very) user API.
     */
    public void setWriteLockValue(Object primaryKey, Object writeLockValue) {
        this.targetIdentityMap.setWriteLockValue(primaryKey, writeLockValue);
    }

    public String toString() {
        return Helper.getShortClassName("Intercepted " + this.targetIdentityMap.getClass()) + "[" + getSize() + "]";
    }
}
