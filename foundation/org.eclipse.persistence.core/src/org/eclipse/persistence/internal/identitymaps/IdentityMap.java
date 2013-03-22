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
 *     Gordon Yorke - Part of the Cache Interceptor feature. (ER 219683)
 ******************************************************************************/  
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
    public CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete);
    
    /**
     * Acquire an active lock on the object.
     * This is used by reading (when using indirection or no relationships) and by merge.
     */
    public CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete);
    
    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge);

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockWithWait(Object primaryKey, boolean forMerge, int wait);

    /**
     * Acquire a read lock on the object.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey);

    /**
     * Acquire a read lock on the object, if not already locked.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey);
    
    /**
     * Clone the map and all of the CacheKeys.
     * This is used by UnitOfWork commitAndResumeOnFailure to avoid corrupting the cache during a failed commit.
     */
    public Object clone();

        /**
     * Add all locked CacheKeys to the map grouped by thread.
     * Used to print all the locks in the identity map.
     */
    public void collectLocks(HashMap threadList);

    
    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param primaryKey is the primary key for the object to search for.
     */
    public boolean containsKey(Object primaryKey);

    /**
     * Allow for the cache to be iterated on.
     */
    public Enumeration elements();

    /**
     * Return the object cached in the identity map or null if it could not be found.
     * User API.
     */
    public Object get(Object primaryKey);

    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param ClassDescriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session);

    /**
     * Get the cache key (with object) for the primary key.
     */
    public CacheKey getCacheKey(Object primaryKey, boolean forMerge);

    /**
     * Get the cache key (with object) for the primary key in order to acquire a lock.
     */
    public CacheKey getCacheKeyForLock(Object primaryKey);

        /**
     * Return the class that this is the map for.
     */
    public Class getDescriptorClass();
    
    /**
     * Return the descriptor that this is the map for.
     */
    public ClassDescriptor getDescriptor();
    
    /**
     * @return The maxSize for the IdentityMap (NOTE: some subclasses may use this differently).
     */
    public int getMaxSize();

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    public int getSize();

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    public int getSize(Class myClass, boolean recurse);

    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB2.
     */
    public Object getWrapper(Object primaryKey);
    
    /**
     * Get the write lock value from the cache key associated to the primarykey.
     * User API.
     */
    public Object getWriteLockValue(Object primaryKey);

    /**
     * Allow for the CacheKeys to be iterated on.
     * Read locks should be checked
     */
    public Enumeration keys();

    /**
     * Allow for the CacheKeys to be iterated on.
     * @param checkReadLocks - true if readLocks should be checked, false otherwise.
     */
    public Enumeration keys(boolean checkReadLocks);
    
    /**
     * Notify the cache that a lazy relationship has been triggered in the object
     * and the cache may need to be updated
     */
    public void lazyRelationshipLoaded(Object rootEntity, ValueHolderInterface valueHolder,  ForeignReferenceMapping mapping);

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    public CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime);

    /**
     * This method may be called during initialize all identity maps.  It allows the identity map
     * or interceptor the opportunity to release any resources before being thrown away.
     */
    public void release();

    /**
     * Remove the CacheKey with the primaryKey from the map.
     * This is used by DeleteObjectQuery and merge.
     * This is also an advanced (very) user API.
     */
    public Object remove(Object primaryKey, Object object);

    /**
     * Remove the CacheKey from the map.
     */
    public Object remove(CacheKey cacheKey);

    /**
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    public void updateMaxSize(int maxSize);
    
    /**
     * Set the descriptor that this is the map for.
     */
    public void setDescriptor(ClassDescriptor descriptor);

    /**
     * Update the wrapper object in the CacheKey associated with the given primaryKey,
     * this is used for EJB2.
     */
    public void setWrapper(Object primaryKey, Object wrapper);
    
    /**
     * Update the write lock value of the CacheKey associated with the given primaryKey.
     * This is used by UpdateObjectQuery, and is also an advanced (very) user API.
     */
    public void setWriteLockValue(Object primaryKey, Object writeLockValue);

    public String toString();
}
