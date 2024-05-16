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

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.helper.ReadLockManager;

public class ConcurrencyManagerState {


    /**
     * Threads and the read locks each thread acquired.
     */
    private final Map<Thread, ReadLockManager> readLockManagerMapClone;

    /**
     * Threads and the deferred locks of the thread.
     */
    private final Map<Thread, DeferredLockManager> deferredLockManagerMapClone;

    /**
     * Threads that are waiting to acquire write locks (either stuck in the concurency manager code or
     * write lock manager code)
     */
    private final Map<Thread, Set<ConcurrencyManager>> unifiedMapOfThreadsStuckTryingToAcquireWriteLock;

    /**
     * Name of the method that go stuck waiting to acquire resource and created the trace.
     */
    private final Map<Thread, String> unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName;

    /**
     * information about threads that are waiting to acquire READ locks
     */
    private final Map<Thread, ConcurrencyManager> mapThreadToWaitOnAcquireReadLockClone;

    /**
     * Name of the method that go stuck waiting to acquire resource and created the trace.
     */
    private final Map<Thread, String> mapThreadToWaitOnAcquireReadLockCloneMethodName;

    /**
     * information about threads that are waiting for object building to finish that have acquired deferred locks
     */
    private final Set<Thread> setThreadWaitingToReleaseDeferredLocksClone;

    /**
     * Stores an explanation created by the isBuildObjectComplete from the currency manager to be returning false, that
     * build object is not yet complete.
     */
    private final Map<Thread, String> mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone;

    /**
     * information about cache keys and their relationship to threads.
     */
    private final Map<ConcurrencyManager, CacheKeyToThreadRelationships> mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey;

    /**
     * The write lock manager has been tweaked to store information about objects ids that the current thread has in its
     * hands and that will required for write locks to be acquired by a committing thread. This information is
     * especially interesting if any thread participating in a dead lock is getting stuck in the acquisition of write
     * locks as part of the commit process. This information might end up revealing a thread that has done too many
     * changes and is creating a bigger risk fo dead lock. The more resources an individual thread tries to grab the
     * worse it is for the concurrency layer. The size of the change set can be interesting.
     */
    private final Map<Thread, Set<Object>> mapThreadToObjectIdWithWriteLockManagerChangesClone;

    /**
     * This field relates to the
     * {@link org.eclipse.persistence.internal.sessions.AbstractSession#THREADS_INVOLVED_WITH_MERGE_MANAGER_WAITING_FOR_DEFERRED_CACHE_KEYS_TO_NO_LONGER_BE_ACQUIRED}
     * and to the bug https://github.com/eclipse-ee4j/eclipselink/issues/2094 it allows us to keep an eye on threads
     * that are at post-commit phase and are trying to merge their change set into the original objects in the cache.
     * When this is taking place some of the cache keys that the merge manager is needing might be locked by other
     * threads. This can lead to deadlocks, if our merge manager thread happens the be the owner of cache keys that
     * matter to the owner of the cache keys the merge manager will need to acquire.
     */
    private Map<Thread, String> mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired;

    /**
     * Create a new ConcurrencyManagerState.
     *
     * @param readLockManagerMapClone
     * @param deferredLockManagerMapClone
     * @param unifiedMapOfThreadsStuckTryingToAcquireWriteLock
     * @param unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName
     * @param mapThreadToWaitOnAcquireReadLockClone
     * @param mapThreadToWaitOnAcquireReadLockCloneMethodName
     * @param setThreadWaitingToReleaseDeferredLocksClone
     * @param mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone
     * @param mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey
     * @param mapThreadToObjectIdWithWriteLockManagerChangesClone
     * @param mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired
     */
    public ConcurrencyManagerState(
            Map<Thread, ReadLockManager> readLockManagerMapClone,
            Map<Thread, DeferredLockManager> deferredLockManagerMapClone,
            Map<Thread, Set<ConcurrencyManager>> unifiedMapOfThreadsStuckTryingToAcquireWriteLock,
            Map<Thread, String> unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName,
            Map<Thread, ConcurrencyManager> mapThreadToWaitOnAcquireReadLockClone,
            Map<Thread, String> mapThreadToWaitOnAcquireReadLockCloneMethodName,
            Set<Thread> setThreadWaitingToReleaseDeferredLocksClone,
            Map<Thread, String> mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone,
            Map<ConcurrencyManager, CacheKeyToThreadRelationships> mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey,
            Map<Thread, Set<Object>> mapThreadToObjectIdWithWriteLockManagerChangesClone,
            Map<Thread, String> mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired) {
        super();
        this.readLockManagerMapClone = readLockManagerMapClone;
        this.deferredLockManagerMapClone = deferredLockManagerMapClone;
        this.unifiedMapOfThreadsStuckTryingToAcquireWriteLock = unifiedMapOfThreadsStuckTryingToAcquireWriteLock;
        this.unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName = unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName;
        this.mapThreadToWaitOnAcquireReadLockClone = mapThreadToWaitOnAcquireReadLockClone;
        this.mapThreadToWaitOnAcquireReadLockCloneMethodName = mapThreadToWaitOnAcquireReadLockCloneMethodName;
        this.setThreadWaitingToReleaseDeferredLocksClone = setThreadWaitingToReleaseDeferredLocksClone;
        this.mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone = mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone;
        this.mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey = mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey;
        this.mapThreadToObjectIdWithWriteLockManagerChangesClone = mapThreadToObjectIdWithWriteLockManagerChangesClone;
        this.mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired = mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired;
    }

    /** Getter for {@link #readLockManagerMapClone} */
    public Map<Thread, ReadLockManager> getReadLockManagerMapClone() {
        return unmodifiableMap(readLockManagerMapClone);
    }

    /** Getter for {@link #deferredLockManagerMapClone} */
    public Map<Thread, DeferredLockManager> getDeferredLockManagerMapClone() {
        return unmodifiableMap(deferredLockManagerMapClone);
    }

    /** Getter for {@link #unifiedMapOfThreadsStuckTryingToAcquireWriteLock} */
    public Map<Thread, Set<ConcurrencyManager>> getUnifiedMapOfThreadsStuckTryingToAcquireWriteLock() {
        return unmodifiableMap(unifiedMapOfThreadsStuckTryingToAcquireWriteLock);
    }

    /** Getter for {@link #mapThreadToWaitOnAcquireReadLockClone} */
    public Map<Thread, ConcurrencyManager> getMapThreadToWaitOnAcquireReadLockClone() {
        return unmodifiableMap(mapThreadToWaitOnAcquireReadLockClone);
    }

    /** Getter for {@link #setThreadWaitingToReleaseDeferredLocksClone} */
    public Set<Thread> getSetThreadWaitingToReleaseDeferredLocksClone() {
        return unmodifiableSet(setThreadWaitingToReleaseDeferredLocksClone);
    }

    /** Getter for {@link #mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone} */
    public Map<Thread, String> getMapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone() {
        return unmodifiableMap(mapThreadsThatAreCurrentlyWaitingToReleaseDeferredLocksJustificationClone);
    }

    /** Getter for {@link #mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey} */
    public Map<ConcurrencyManager, CacheKeyToThreadRelationships> getMapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey() {
        return unmodifiableMap(mapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey);
    }

    /** Getter for {@link #mapThreadToObjectIdWithWriteLockManagerChangesClone} */
    public Map<Thread, Set<Object>> getMapThreadToObjectIdWithWriteLockManagerChangesClone() {
        return unmodifiableMap(mapThreadToObjectIdWithWriteLockManagerChangesClone);
    }

    /** Getter for {@link #unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName} */
    public Map<Thread, String> getUnifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName() {
        return unmodifiableMap(unifiedMapOfThreadsStuckTryingToAcquireWriteLockMethodName);
    }

    /** Getter for {@link #mapThreadToWaitOnAcquireReadLockCloneMethodName} */
    public Map<Thread, String> getMapThreadToWaitOnAcquireReadLockCloneMethodName() {
        return unmodifiableMap(mapThreadToWaitOnAcquireReadLockCloneMethodName);
    }

    /** Getter for {@link #mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired} */
    public Map<Thread, String> getMapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired() {
        return unmodifiableMap(mapThreadsInvolvedWithMergeManagerWaitingForDeferredCacheKeysToNoLongerBeAcquired);
    }


}
