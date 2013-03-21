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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Unit of work specific identity map which avoid additional overhead not required in unit of work,
 * such as locking and synchronization.
 * @author James Sutherland
 * @since TopLink 10.1.3.1
 */
public class UnitOfWorkIdentityMap extends FullIdentityMap {

    protected UnitOfWorkIdentityMap() {        
    }
    
    public UnitOfWorkIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super();
        this.maxSize = size;
        // PERF: Use a HashMap as more efficient than a ConcurrentMap and single threaded.
        this.cacheKeys = new HashMap(size);
        this.descriptor = descriptor;
        this.session = session;
        this.isIsolated = isolated;
    }
    
    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new CacheKey(primaryKey, object, writeLockValue, readTime, true);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete) {
        CacheKey cacheKey = getCacheKey(primaryKey, false);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            if (cacheKey == null) {
                return newCacheKey;
            }
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            if (cacheKey == null) {
                return newCacheKey;
            }
        }
        return cacheKey;
    }
    
    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            if (cacheKey == null) {
                return newCacheKey;
            }
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireLockWithWait(Object primaryKey, boolean forMerge, int wait) {
        CacheKey cacheKey = getCacheKey(primaryKey, forMerge);
        if (cacheKey == null) {
            CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
            cacheKey = putCacheKeyIfAbsent(newCacheKey);
            if (cacheKey == null) {
                return newCacheKey;
            }
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey) {
        return acquireReadLockOnCacheKeyNoWait(primaryKey);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null, 0);
        CacheKey cacheKey = getCacheKey(newCacheKey, false);
        if (cacheKey == null) {
            return newCacheKey;
        }
        return cacheKey;
    }
    
    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    protected CacheKey getCacheKeyWithReadLock(Object primaryKey) {
        return getCacheKey(primaryKey, false);
    }
    
    /**
     * Use hashmap put, as no concurrency in unit of work.
     */
    protected CacheKey putCacheKeyIfAbsent(CacheKey searchKey) {
        searchKey.setOwningMap(this);
        this.cacheKeys.put(searchKey.getKey(), searchKey);
        return null;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public Object remove(CacheKey cacheKey) {
        if (cacheKey != null) {
            this.cacheKeys.remove(cacheKey.getKey());
            cacheKey.setOwningMap(null);
        } else {
            return null;
        }
        return cacheKey.getObject();
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public void resetCacheKey(CacheKey key, Object object, Object writeLockValue, long readTime) {
        key.setObject(object);
        key.setWriteLockValue(writeLockValue);
        key.setReadTime(readTime);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    @Override
    public void setWriteLockValue(Object primaryKey, Object writeLockValue) {
        CacheKey cacheKey = getCacheKeyForLock(primaryKey);
        if (cacheKey != null) {
            cacheKey.setWriteLockValue(writeLockValue);
        }
    }
}
