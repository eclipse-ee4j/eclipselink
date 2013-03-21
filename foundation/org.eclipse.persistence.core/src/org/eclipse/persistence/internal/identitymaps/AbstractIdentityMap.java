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
package org.eclipse.persistence.internal.identitymaps;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Caches objects, and allows their retrieval  by their primary key.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Store CacheKeys containing objects and possibly writeLockValues.
 * <li> Insert & retrieve objects from the cache.
 * <li> Allow retrieval and modification of writeLockValue for a cached object.
 * </ul>
 * @see CacheKey
 * @since TOPLink/Java 1.0
 */
public abstract class AbstractIdentityMap implements IdentityMap, Serializable, Cloneable {

    /** The initial or maximum size of the cache depending upon the concrete implementation. */
    protected int maxSize;

    /** PERF: Store the descriptor to allow lastAccessed cache lookup optimization. */
    protected transient ClassDescriptor descriptor;
    
    /** Is this identity map within an IsolatedClientSession */
    protected boolean isIsolated;
    
    /** Session that the map is on */
    protected AbstractSession session;
    
    public AbstractIdentityMap(){
    }

    /**
     * Instantiate an new IdentityMap with it's maximum size.<p>
     * <b>NOTE</b>: Subclasses may provide different behavior for maxSize.
     * @param size is the maximum size to be allocated for the receiver.
     */
    public AbstractIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        this.maxSize = size;
        this.descriptor = descriptor;
        this.isIsolated = isolated;
        this.session = session;
    }

    /**
     * Acquire a deferred lock on the object.
     * This is used while reading if the object has relationships without indirection.
     * This first thread will get an active lock.
     * Other threads will get deferred locks, all threads will wait until all other threads are complete before releasing their locks.
     */
    public CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete) {
        CacheKey cacheKey = getCacheKey(primaryKey, false);
        if (cacheKey == null) {
            // Create and lock a new cacheKey.
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            newCacheKey.acquireDeferredLock();
            // PERF: To avoid synchronization, getIfAbsentPut is used.
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            // Return if the newCacheKey was put as it was already locked, otherwise must still lock it.
            if (cacheKey == null) {
                return newCacheKey;
            } else {
                newCacheKey.releaseDeferredLock();
            }
        }
        // Acquire a lock on the cache key.
        cacheKey.acquireDeferredLock();
        return cacheKey;
    }

    /**
     * Acquire an active lock on the object.
     * This is used by reading (when using indirection or no relationships) and by merge.
     */
    public CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            // Create and lock a new cacheKey.
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            newCacheKey.acquire(forMerge);
            // PERF: To avoid synchronization, getIfAbsentPut is used.
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            // Return if the newCacheKey was put as it was already locked, otherwise must still lock it.
            if (cacheKey == null) {
                return newCacheKey;
            } else {
                newCacheKey.release();
            }
        }
        // Acquire a lock on the cache key.
        cacheKey.acquire();
        return cacheKey;
    }

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            // Create and lock a new cacheKey.
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            newCacheKey.acquire(forMerge);
            // PERF: To avoid synchronization, getIfAbsentPut is used.
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            // Return if the newCacheKey was put as already lock, otherwise must still lock.
            if (cacheKey == null) {
                return newCacheKey;
            } else {
                newCacheKey.release();
            }
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        if (!cacheKey.acquireNoWait(forMerge)) {
            return null;
        }
        return cacheKey;
    }

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockWithWait(Object primaryKey, boolean forMerge, int wait) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            // Create and lock a new cacheKey.
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            newCacheKey.acquire(forMerge);
            // PERF: To avoid synchronization, getIfAbsentPut is used.
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            // Return if the newCacheKey was put as already lock, otherwise must still lock.
            if (cacheKey == null) {
                return newCacheKey;
            } else {
                newCacheKey.release();
            }
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        if (!cacheKey.acquireWithWait(forMerge, wait)) {
            return null;
        }
        return cacheKey;
    }

    /**
     * Acquire a read lock on the object.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey) {
        CacheKey cacheKey = getCacheKey(primaryKey, false);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            // Lock new cacheKey.
            newCacheKey.acquireReadLock();
            // Create one but not put it in the cache, as we are only reading
            // and should not be writing to the identitymap.
            return newCacheKey;
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        cacheKey.acquireReadLock();
        return cacheKey;
    }

    /**
     * Acquire a read lock on the object, if not already locked.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey) {
        CacheKey cacheKey = getCacheKey(primaryKey, false);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            // Lock new cacheKey.
            newCacheKey.acquireReadLock();
            // Create one but not put it in the cache, as we are only reading
            // and should not be writing to the identitymap.
            return newCacheKey;
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        if (!cacheKey.acquireReadLockNoWait()) {
            return null;
        }
        return cacheKey;
    }
    
    /**
     * Add all locked CacheKeys to the map grouped by thread.
     * Used to print all the locks in the identity map.
     */
    public abstract void collectLocks(HashMap threadList);

    /**
     * Clone the map and all of the CacheKeys.
     * This is used by UnitOfWork commitAndResumeOnFailure to avoid corrupting the cache during a failed commit.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
    }

    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param primaryKey is the primary key for the object to search for.
     */
    public boolean containsKey(Object primaryKey) {
        return getCacheKeyWithReadLock(primaryKey) != null;
    }

    /**
     * Create the correct type of CacheKey for this map.
     */
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new CacheKey(primaryKey, object, writeLockValue, readTime, this.isIsolated);
    }

    /**
     * Allow for the cache to be iterated on.
     */
    public abstract Enumeration elements();

    /**
     * Return the object cached in the identity map or null if it could not be found.
     * User API.
     */
    public Object get(Object primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        }
        return cacheKey.getObject();
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
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session){
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        CacheKey cachedObject = null;
        long currentTime = System.currentTimeMillis();
        for (Object pk : pkList){
            cachedObject = getCacheKey(pk, false);
            if ((cachedObject != null && cachedObject.getObject() != null && !descriptor.getCacheInvalidationPolicy().isInvalidated(cachedObject, currentTime))){
                map.put(pk, cachedObject.getObject());
            }
        }
        return map;
    }

    /**
     * Get the cache key (with object) for the primary key.
     */
    public abstract CacheKey getCacheKey(Object primaryKey, boolean forMerge);

    /**
     * Get the cache key (with object) for the primary key.
     */
    public CacheKey getCacheKeyForLock(Object primaryKey) {
        return getCacheKey(primaryKey, true);
    }
    
    /**
     * Return the CacheKey (with object) matching the searchKey.
     * If the CacheKey is missing then put the searchKey in the map.
     * The searchKey should have already been locked. 
     */
    protected abstract CacheKey putCacheKeyIfAbsent(CacheKey cacheKey);

    /**
     * Get the cache key (with object) for the primary key with read lock.
     */
    protected CacheKey getCacheKeyWithReadLock(Object primaryKey) {
        CacheKey key = getCacheKey(primaryKey, false);
        if (key != null) {
            key.checkReadLock();
        }
        return key;
    }

    /**
     * Returns the class which should be used as an identity map in a descriptor by default.
     */
    public static Class getDefaultIdentityMapClass() {
        return ClassConstants.SoftCacheWeakIdentityMap_Class;
    }

    /**
     * @return The maxSize for the IdentityMap (NOTE: some subclasses may use this differently).
     */
    public int getMaxSize() {
        if (maxSize == -1) {
            maxSize = 100;
        }
        return maxSize;
    }

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    public abstract int getSize();

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    public abstract int getSize(Class myClass, boolean recurse);

    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB2.
     */
    public Object getWrapper(Object primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        } else {
            return cacheKey.getWrapper();
        }
    }

    /**
     * Get the write lock value from the cache key associated to the primarykey.
     * User API.
     */
    public Object getWriteLockValue(Object primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        } else {
            return cacheKey.getWriteLockValue();
        }
    }

    /**
     * Allow for the CacheKeys to be iterated on.
     */
    public abstract Enumeration keys();

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    public abstract CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime);

    /**
     * This method may be called during initialize all identity maps.  It allows the identity map
     * or interceptor the opportunity to release any resources before being thrown away.
     */
    public void release(){
        //no-op
    }

    /**
     * Remove the CacheKey with the primaryKey from the map.
     * This is used by DeleteObjectQuery and merge.
     * This is also an advanced (very) user API.
     */
    public Object remove(Object primaryKey, Object object) {
        CacheKey key = getCacheKeyForLock(primaryKey);
        return remove(key);
    }

    /**
     * Remove the CacheKey from the map.
     */
    public abstract Object remove(CacheKey cacheKey);

    /**
     * Set the maximum size for the receiver.
     * @param size is the new maximum size.
     */
    protected synchronized void setMaxSize(int size) {
        maxSize = size;
    }

    /**
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    public void updateMaxSize(int maxSize) {
        setMaxSize(maxSize);
    }
    
    /**
     * Return the class that this is the map for.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }
    
    /**
     * Return the class that this is the map for.
     */
    public Class getDescriptorClass() {
        if (descriptor == null) {
            return null;
        }
        return descriptor.getJavaClass();
    }
    
    /**
     * Set the descriptor that this is the map for.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Update the wrapper object in the CacheKey associated with the given primaryKey,
     * this is used for EJB2.
     */
    public void setWrapper(Object primaryKey, Object wrapper) {
        CacheKey cacheKey = getCacheKeyForLock(primaryKey);
        if (cacheKey != null) {
            cacheKey.setWrapper(wrapper);
        }
    }

    /**
     * Update the write lock value of the CacheKey associated with the given primaryKey.
     * This is used by UpdateObjectQuery, and is also an advanced (very) user API.
     */
    public void setWriteLockValue(Object primaryKey, Object writeLockValue) {
        CacheKey cacheKey = getCacheKeyForLock(primaryKey);
        if (cacheKey != null) {
            //lock/release the cache key during the lock value updating
            cacheKey.acquire();
            cacheKey.setWriteLockValue(writeLockValue);
            cacheKey.release();
        }
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "[" + getSize() + "]";
    }
}
