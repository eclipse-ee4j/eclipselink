/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/19/2009-2.0 Gordon Yorke
//       - 239825: XML configuration for Interceptors and Default redirectors
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.interceptors.CacheInterceptor;
import org.eclipse.persistence.sessions.interceptors.CacheKeyInterceptor;

public class CacheAuditor extends CacheInterceptor {

    protected boolean shouldThrow;

    protected int accessCount;

    protected Boolean lastAcquireNoWait = null;

    public CacheAuditor(IdentityMap targetIdentityMap, AbstractSession interceptedSession) {
        super(targetIdentityMap, interceptedSession);
        this.accessCount = 0;
    }

    @Override
    public Object clone() {
        return new CacheAuditor(targetIdentityMap, interceptedSession);
    }

    @Override
    protected CacheKeyInterceptor createCacheKeyInterceptor(CacheKey wrappedCacheKey) {
        return new CacheKeyInterceptor(wrappedCacheKey){
            public void release() {
                wrappedKey.release();
                if (shouldThrow){
                    throw new javax.persistence.OptimisticLockException("FALSE-EXCEPTION");
                }
            }

        };
    }

    public void release() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor#acquireDeferredLock(java.lang.Object)
     */
    @Override
    public CacheKey acquireDeferredLock(Object primaryKey, boolean isCacheCheckComplete) {
        ++accessCount;
        return super.acquireDeferredLock(primaryKey, isCacheCheckComplete);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor#acquireLock(java.lang.Object, boolean)
     */
    @Override
    public CacheKey acquireLock(Object primaryKey, boolean forMerge, boolean isCacheCheckComplete) {
        ++accessCount;
        return super.acquireLock(primaryKey, forMerge, isCacheCheckComplete);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor#acquireLockNoWait(java.lang.Object, boolean)
     */
    @Override
    public CacheKey acquireLockNoWait(Object primaryKey, boolean forMerge) {
        this.lastAcquireNoWait = forMerge;
        return super.acquireLockNoWait(primaryKey, forMerge);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor#getCacheKey(java.lang.Object, boolean)
     */
    @Override
    public CacheKey getCacheKey(Object primaryKey, boolean forMerge) {
        ++accessCount;
        return super.getCacheKey(primaryKey, forMerge);
    }

    /**
     * @return the shouldThrow
     */
    public boolean isShouldThrow() {
        return shouldThrow;
    }

    /**
     * @param shouldThrow the shouldThrow to set
     */
    public void setShouldThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    @Override
    public void lazyRelationshipLoaded(Object rootEntity, ValueHolderInterface valueHolder, ForeignReferenceMapping mapping) {
        // TODO Auto-generated method stub

    }

    /**
     * @return the accessCount
     */
    public int getAccessCount() {
        return accessCount;
    }

    /**
     * @return the lastAcquireNoWait
     */
    public Boolean getLastAcquireNoWait() {
        return lastAcquireNoWait;
    }

    /**
     * @param accessCount the accessCount to set
     */
    public void resetAccessCount() {
        this.accessCount = 0;
        this.lastAcquireNoWait = null;
    }

    @Override
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Object, CacheKey> getAllCacheKeysFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session) {
        // TODO Auto-generated method stub
        return null;
}
}

