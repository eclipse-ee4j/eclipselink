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
 * <p><b>Purpose</b>: A WeakIdentityMap holds all objects referenced by the application only.
 * The weak identity map is similar to the full identity map except for the fact that it allows
 * full garbage collection.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Guarantees identity.
 * <li> Allows garbage collection.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class WeakIdentityMap extends FullIdentityMap {

    /** Keep track of a counter to amortize cleanup of dead cache keys */
    protected volatile int cleanupCount;

    /** PERF: Keep track of a cleanup size to avoid cleanup bottleneck for large caches. */
    protected volatile int cleanupSize;

    public WeakIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super(size, descriptor, session, isolated);
        this.cleanupCount = 0;
        this.cleanupSize = size;
    }
    
    /**
     * Search for any cache keys that have been garbage collected and remove them.
     * This must be done because although the objects held by the cache keys will garbage collect,
     * the keys themselves will not and must be cleaned up.  This is a linear operation so
     * is amortized through the cleanupCount to occur only once per cycle averaging to make
     * the total time still constant.
     */
    protected void cleanupDeadCacheKeys() {
        for (Iterator iterator = getCacheKeys().values().iterator(); iterator.hasNext();) {
            CacheKey key = (CacheKey)iterator.next();
            if (key.getObject() == null) {
                // Check lock first.
                // Change for CR 2317
                // Change for Bug 5840635 - use acquireIfUnownedNoWait()
                // Check lock first, only acquire if mutex's active thread is unset
                // as this thread could be entering a cache key that could be acquiring a lock
                // and we don't want to remove the cache key.
                if (key.acquireIfUnownedNoWait()) {
                    try {
                        if (key.getObject() == null) {
                            iterator.remove();
                        }
                    } finally {
                        key.release();
                    }
                }
            }
        }
    }

    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new WeakCacheKey(primaryKey, object, writeLockValue, readTime, isIsolated);
    }

    /**
     * Need to check for cleanup on put.
     */
    @Override
    protected CacheKey putCacheKeyIfAbsent(CacheKey searchKey) {
        CacheKey cacheKey = super.putCacheKeyIfAbsent(searchKey);
        if (cacheKey == null) {
            checkCleanup();
        }
        return cacheKey;
    }

    /**
     * Check if garbage collected cache keys need to be cleaned up.
     */
    protected void checkCleanup() {
        // PERF: Avoid synchronization if cleanup not required (counts are volatile).
        if (this.cleanupCount > this.cleanupSize) {
            synchronized (this) {
                if (this.cleanupCount > this.cleanupSize) {
                    if ((this.session !=  null) && this.session.isConcurrent()) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                cleanupDeadCacheKeys();
                                WeakIdentityMap.this.cleanupCount = 0;
                                // PERF: Avoid cleanup bottleneck for large cache sizes, increase next cleanup.
                                int size = getSize();
                                if (size > WeakIdentityMap.this.cleanupSize) {
                                    WeakIdentityMap.this.cleanupSize = size;
                                }
                            }
                        };
                        this.session.getServerPlatform().launchContainerRunnable(runnable);
                    } else {
                        cleanupDeadCacheKeys();
                        this.cleanupCount = 0;
                        // PERF: Avoid cleanup bottleneck for large cache sizes, increase next cleanup.
                        int size = getSize();
                        if (size > this.cleanupSize) {
                            this.cleanupSize = size;
                        }
                    }
                }
            }
        }
        this.cleanupCount++;
    }
}
