/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper.type;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.helper.ReadLockManager;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class CacheKeyToThreadRelationships {

    /**
     * The cache key the dto is describing (e.g. the DB )
     */
    private final ConcurrencyManager cacheKeyBeingDescribed;

    /**
     * A list of all threads that we know to have increased and noy yet the crease the number of readers count on the
     * cache key.
     *
     * <P>
     * References: <br>
     * See {@link ReadLockManager#getReadLocks()}
     */
    private final List<Thread> threadsThatAcquiredReadLock = new ArrayList<>();

    /**
     * Threads that have this cache key in their deferred lock list
     * <P>
     * References: <br>
     * See {@link DeferredLockManager#getDeferredLocks()}
     */
    private final List<Thread> threadsThatAcquiredDeferredLock = new ArrayList<>();

    /**
     * Threads that have this cache key in their active lock list.
     *
     * <P>
     * References: <br>
     * See {@link DeferredLockManager#getActiveLocks()} and {@link ConcurrencyManager#getActiveThread()}.
     *
     * <P>
     * NOTE: <br>
     * There should be only one at most on this list. The same thread thta is referred as the active thread by the cache
     * key itself.
     */
    private final List<Thread> threadsThatAcquiredActiveLock = new ArrayList<>();

    /**
     * These are threads that have registered themselves as waiting for the cache key. See
     * {@link org.eclipse.persistence.internal.helper.ConcurrencyManager#getThreadsToWaitOnAcquire()}
     *
     * (acquire lock for writing or as deferred - the cache key must be found with number of readers 0).
     */
    private final List<Thread> threadsKnownToBeStuckTryingToAcquireLock = new ArrayList<>();

    /**
     * These are threads stuck on the
     * {@link org.eclipse.persistence.internal.helper.ConcurrencyManager#acquireReadLock()}
     */
    private final List<Thread> threadsKnownToBeStuckTryingToAcquireLockForReading = new ArrayList<>();

    /**
     * Create a new CacheKeyToThreadRelationships.
     *
     * @param cacheKeyBeingDescribed
     */
    public CacheKeyToThreadRelationships(ConcurrencyManager cacheKeyBeingDescribed) {
        super();
        this.cacheKeyBeingDescribed = cacheKeyBeingDescribed;
    }

    /** Getter for {@link #cacheKeyBeingDescribed} */
    public ConcurrencyManager getCacheKeyBeingDescribed() {
        return cacheKeyBeingDescribed;
    }

    /** Getter for {@link #threadsThatAcquiredReadLock} */
    public List<Thread> getThreadsThatAcquiredReadLock() {
        return unmodifiableList(threadsThatAcquiredReadLock);
    }

    /** Getter for {@link #threadsThatAcquiredDeferredLock} */
    public List<Thread> getThreadsThatAcquiredDeferredLock() {
        return unmodifiableList(threadsThatAcquiredDeferredLock);
    }

    /** Getter for {@link #threadsThatAcquiredActiveLock} */
    public List<Thread> getThreadsThatAcquiredActiveLock() {
        return unmodifiableList(threadsThatAcquiredActiveLock);
    }

    /** Getter for {@link #threadsKnownToBeStuckTryingToAcquireLock} */
    public List<Thread> getThreadsKnownToBeStuckTryingToAcquireLock() {
        return unmodifiableList(threadsKnownToBeStuckTryingToAcquireLock);
    }

    /** Getter for {@link #threadsThatAcquiredReadLock} */
    public List<String> getThreadNamesThatAcquiredReadLock() {
        return mapThreadToThreadName(threadsThatAcquiredReadLock);
    }

    /** Getter for {@link #threadsThatAcquiredDeferredLock} */
    public List<String> getThreadNamesThatAcquiredDeferredLock() {
        return mapThreadToThreadName(threadsThatAcquiredDeferredLock);
    }

    /** Getter for {@link #threadsThatAcquiredActiveLock} */
    public List<String> getThreadNamesThatAcquiredActiveLock() {
        return mapThreadToThreadName(threadsThatAcquiredActiveLock);
    }

    /** Getter for {@link #threadsKnownToBeStuckTryingToAcquireLock} */
    public List<String> getThreadNamesKnownToBeStuckTryingToAcquireLock() {
        return mapThreadToThreadName(threadsKnownToBeStuckTryingToAcquireLock);
    }

    /**
     * Map a list of threads to their thread names
     *
     * @param threads
     *            the threads to map
     * @return the thread names
     */
    protected List<String> mapThreadToThreadName(List<Thread> threads) {
        List<String> result = new ArrayList<>();
        for (Thread currentThread : threads) {
            result.add(currentThread.getName());
        }
        return result;
    }

    /** Setter for {@link #threadsThatAcquiredReadLock} */
    public void addThreadsThatAcquiredReadLock(Thread thread) {
        threadsThatAcquiredReadLock.add(thread);
    }

    /** Setter for {@link #threadsThatAcquiredDeferredLock} */
    public void addThreadsThatAcquiredDeferredLock(Thread thread) {
        threadsThatAcquiredDeferredLock.add(thread);
    }

    /** Setter for {@link #threadsThatAcquiredActiveLock} */
    public void addThreadsThatAcquiredActiveLock(Thread thread) {
        threadsThatAcquiredActiveLock.add(thread);
    }

    /** Setter for {@link #threadsKnownToBeStuckTryingToAcquireLock} */
    public void addThreadsKnownToBeStuckTryingToAcquireLock(Thread thread) {
        threadsKnownToBeStuckTryingToAcquireLock.add(thread);
    }

    /** Setter for {@link #threadsKnownToBeStuckTryingToAcquireLockForReading} */
    public void addThreadsKnownToBeStuckTryingToAcquireLockForReading(Thread thread) {
        threadsKnownToBeStuckTryingToAcquireLockForReading.add(thread);
    }

    /** Getter for {@link #threadsKnownToBeStuckTryingToAcquireLockForReading} */
    public List<String> getThreadNamesKnownToBeStuckTryingToAcquireLockForReading() {
        return mapThreadToThreadName(threadsKnownToBeStuckTryingToAcquireLockForReading);
    }

    /** Getter for {@link #threadsKnownToBeStuckTryingToAcquireLockForReading} */
    public List<Thread> getThreadsKnownToBeStuckTryingToAcquireLockForReading() {
        return unmodifiableList(threadsKnownToBeStuckTryingToAcquireLockForReading);
    }
}
