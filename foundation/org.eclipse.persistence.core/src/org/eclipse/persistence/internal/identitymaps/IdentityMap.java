/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Gordon Yorke - Part of the Cache Interceptor feature. (ER 219683)
//     12/14/2017-3.0 Tomas Kraus
//       - 522635: ConcurrentModificationException when triggering lazy load from conforming query
package org.eclipse.persistence.internal.identitymaps;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * <p><b>Purpose</b>: Provides the interface for IdentityMap interaction.
 * <p><b>Responsibilities</b>:
 * Provides access to all of the public interface for the EclipseLink IdentityMaps.
 * This interface can be used if implementing custom identity maps that are radically
 * different than the stock IdentityMaps otherwise simply extending the appropriate
 * IdentityMap implementation is the best approach.
 *
 * @see CacheKey
 * @since EclipseLink 1.0M7
 */
public interface IdentityMap extends Cloneable{

    /**
     * Acquire a deferred lock on the object.
     * This is used while reading if the object has relationships without indirection.
     * This first thread will get an active lock.
     * Other threads will get deferred locks, all threads will wait until all other threads are complete before releasing their locks.
     */
    CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete);

    /**
     * Acquire an active lock on the object.
     * This is used by reading (when using indirection or no relationships) and by merge.
     */
    CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete);

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge);

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    CacheKey acquireLockWithWait(Object primaryKey, boolean forMerge, int wait);

    /**
     * Acquire a read lock on the object.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    CacheKey acquireReadLockOnCacheKey(Object primaryKey);

    /**
     * Acquire a read lock on the object, if not already locked.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey);

    /**
     * Clone the map and all of the CacheKeys.
     * This is used by UnitOfWork commitAndResumeOnFailure to avoid corrupting the cache during a failed commit.
     */
    Object clone();

        /**
     * Add all locked CacheKeys to the map grouped by thread.
     * Used to print all the locks in the identity map.
     */
    void collectLocks(HashMap threadList);


    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param primaryKey is the primary key for the object to search for.
     */
    boolean containsKey(Object primaryKey);

    /**
     * Allow for the cache to be iterated on.
     */
    Enumeration elements();

    /**
     * Return the object cached in the identity map or null if it could not be found.
     * User API.
     */
    Object get(Object primaryKey);

    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param descriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session);

    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param descriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    Map<Object, CacheKey> getAllCacheKeysFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session);

    /**
     * Get the cache key (with object) for the primary key.
     */
    CacheKey getCacheKey(Object primaryKey, boolean forMerge);

    /**
     * Get the cache key (with object) for the primary key in order to acquire a lock.
     */
    CacheKey getCacheKeyForLock(Object primaryKey);

        /**
     * Return the class that this is the map for.
     */
    Class getDescriptorClass();

    /**
     * Return the descriptor that this is the map for.
     */
    ClassDescriptor getDescriptor();

    /**
     * @return The maxSize for the IdentityMap (NOTE: some subclasses may use this differently).
     */
    int getMaxSize();

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    int getSize();

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    int getSize(Class myClass, boolean recurse);

    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB2.
     */
    Object getWrapper(Object primaryKey);

    /**
     * Get the write lock value from the cache key associated to the primarykey.
     * User API.
     */
    Object getWriteLockValue(Object primaryKey);

    /**
     * Allow for the CacheKeys to be iterated on.
     * Read locks should be checked
     */
    Enumeration<CacheKey> keys();

    /**
     * Allow for the CacheKeys to be iterated on using copy of keys enumeration.
     * This is thread safe access to keys.
     *
     * @return clone of the CacheKeys enumeration
     */
    Enumeration<CacheKey> cloneKeys();

    /**
     * Allow for the CacheKeys to be iterated on.
     * @param checkReadLocks - true if readLocks should be checked, false otherwise.
     */
    Enumeration<CacheKey> keys(boolean checkReadLocks);

    /**
     * Notify the cache that a lazy relationship has been triggered in the object
     * and the cache may need to be updated
     */
    void lazyRelationshipLoaded(Object rootEntity, ValueHolderInterface valueHolder,  ForeignReferenceMapping mapping);

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime);

    /**
     * This method may be called during initialize all identity maps.  It allows the identity map
     * or interceptor the opportunity to release any resources before being thrown away.
     */
    void release();

    /**
     * Remove the CacheKey with the primaryKey from the map.
     * This is used by DeleteObjectQuery and merge.
     * This is also an advanced (very) user API.
     */
    Object remove(Object primaryKey, Object object);

    /**
     * Remove the CacheKey from the map.
     */
    Object remove(CacheKey cacheKey);

    /**
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    void updateMaxSize(int maxSize);

    /**
     * Set the descriptor that this is the map for.
     */
    void setDescriptor(ClassDescriptor descriptor);

    /**
     * Update the wrapper object in the CacheKey associated with the given primaryKey,
     * this is used for EJB2.
     */
    void setWrapper(Object primaryKey, Object wrapper);

    /**
     * Update the write lock value of the CacheKey associated with the given primaryKey.
     * This is used by UpdateObjectQuery, and is also an advanced (very) user API.
     */
    void setWriteLockValue(Object primaryKey, Object writeLockValue);

    @Override
    String toString();
}
