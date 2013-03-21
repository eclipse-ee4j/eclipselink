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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * <p><b>Purpose</b>: A FullIdentityMap holds all objects stored within it for the life of the application.
 * <p><b>Responsibilities</b>:<ul>
 *    <li> Guarantees identity
 * <li> Holds all cached objects indefinitely.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class FullIdentityMap extends AbstractIdentityMap {

    /** Map of CacheKeys stored using their key. */
    protected Map<Object, CacheKey> cacheKeys;
    
    /**
     * Used to allow subclasses to build different map type.
     */
    public FullIdentityMap() {        
    }
    
    public FullIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super(size, descriptor, session, isolated);
        this.cacheKeys = new ConcurrentHashMap(size);
    }
    
    /**
     * INTERNAL:
     * Clones itself.
     */
    @Override
    public Object clone() {
        FullIdentityMap clone = (FullIdentityMap)super.clone();
        clone.setCacheKeys(new ConcurrentHashMap(this.cacheKeys.size()));

        for (Iterator cacheKeysIterator = this.cacheKeys.values().iterator(); cacheKeysIterator.hasNext();) {
            CacheKey key = (CacheKey)((CacheKey)cacheKeysIterator.next()).clone();
            clone.getCacheKeys().put(key.getKey(), key);
        }

        return clone;
    }

    /**
     * INTERNAL:
     * Used to print all the Locks in every identity map in this session.
     */
    @Override
    public void collectLocks(HashMap threadList) {
        Iterator cacheKeyIterator = this.cacheKeys.values().iterator();
        while (cacheKeyIterator.hasNext()) {
            CacheKey cacheKey = (CacheKey)cacheKeyIterator.next();
            if (cacheKey.isAcquired()) {
                Thread activeThread = cacheKey.getActiveThread();
                Set set = (Set)threadList.get(activeThread);
                if (set == null) {
                    set = new HashSet();
                    threadList.put(activeThread, set);
                }
                set.add(cacheKey);
            }
        }
    }

    /**
     * Allow for the cache to be iterated on.
     */
    @Override
    public Enumeration elements() {
        return new IdentityMapEnumeration(this);
    }

    /**
     * Return the cache key matching the primary key of the searchKey.
     * If no object for the key exists, return null.
     */
    @Override
    public CacheKey getCacheKey(Object searchKey, boolean forMerge) {
        return this.cacheKeys.get(searchKey);
    }    
        
    /**
     * Return the CacheKey (with object) matching the searchKey.
     * If the CacheKey is missing then put the searchKey in the map.
     * The searchKey should have already been locked. 
     */
    @Override
    protected CacheKey putCacheKeyIfAbsent(CacheKey searchKey) {
        searchKey.setOwningMap(this);
        return (CacheKey)((ConcurrentMap)this.cacheKeys).putIfAbsent(searchKey.getKey(), searchKey);
    }

    /**
     * Return the cache keys.
     */
    public Map<Object, CacheKey> getCacheKeys() {
        return cacheKeys;
    }

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    @Override
    public int getSize() {
        return this.cacheKeys.size();
    }

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    @Override
    public int getSize(Class myClass, boolean recurse) {
        int count = 0;
        Iterator keys = this.cacheKeys.values().iterator();

        while (keys.hasNext()) {
            CacheKey key = (CacheKey)keys.next();
            Object object = key.getObject();

            if (object != null) {
                if (recurse && myClass.isInstance(object)) {
                    count++;
                } else if (object.getClass().equals(myClass)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Allow for the cache keys to be iterated on.
     * Read locks will be checked.
     */
    @Override
    public Enumeration keys() {
        return keys(true);
    }

    /**
     * Allow for the cache keys to be iterated on.
     * @param checkReadLocks - true if readLocks should be checked, false otherwise.
     */
    public Enumeration keys(boolean checkReadLocks) {
        return new IdentityMapKeyEnumeration(this, checkReadLocks);
    }
    
    /**
     * Notify the cache that a lazy relationship has been triggered in the object
     * and the cache may need to be updated
     */
    public void lazyRelationshipLoaded(Object object, ValueHolderInterface valueHolder, ForeignReferenceMapping mapping){
        //NO-OP
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
    @Override
    public CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        CacheKey newCacheKey = createCacheKey(primaryKey, object, writeLockValue, readTime);
        // Find the cache key in the map, reset it, or put the new one.
        CacheKey cacheKey = putCacheKeyIfAbsent(newCacheKey);
        if (cacheKey != null) {
            // The cache key is locked inside resetCacheKey() to keep other threads from accessing the object.
            resetCacheKey(cacheKey, object, writeLockValue, readTime);
        } else {
            return newCacheKey;
        }

        return cacheKey;
    }

    /**
     * Removes the CacheKey from the map.
     * @return the object held within the CacheKey or null if no object cached for given cacheKey.
     */
    @Override
    public Object remove(CacheKey cacheKey) {
        if (cacheKey != null) {
            // Cache key needs to be locked when removing from the map.
            cacheKey.acquire();
            this.cacheKeys.remove(cacheKey.getKey());
            cacheKey.setOwningMap(null);
            // Cache key needs to be released after removing from the map.
            cacheKey.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
            cacheKey.release();
            return cacheKey.getObject();
        } else {
            return null;
        }
    }

    /**
     * Reset the cache key with new data.
     */
    public void resetCacheKey(CacheKey key, Object object, Object writeLockValue, long readTime) {
        key.acquire();
        key.setObject(object);
        key.setWriteLockValue(writeLockValue);
        key.setReadTime(readTime);
        key.release();
    }

    protected void setCacheKeys(Map<Object, CacheKey> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }
}
