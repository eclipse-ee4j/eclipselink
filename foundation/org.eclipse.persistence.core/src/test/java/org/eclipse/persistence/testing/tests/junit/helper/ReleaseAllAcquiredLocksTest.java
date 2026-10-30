/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.junit.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.junit.After;
import org.junit.Test;

/**
 * A merge that has transitioned to deferred locks releases its keys through the current thread's
 * {@link DeferredLockManager}. Keys that reach {@link MergeManager#getAcquiredLocks()} while that manager is
 * absent (the {@code appendLock} fallback) must still be released, or they stay locked by a thread that has
 * long moved on.
 */
public class ReleaseAllAcquiredLocksTest {

    private final WriteLockManager writeLockManager = new WriteLockManager();

    @After
    public void cleanUp() {
        ConcurrencyManager.removeDeferredLockManager(Thread.currentThread());
    }

    @Test
    public void transitionedMergeWithoutDeferredLockManager_releasesKeysTrackedOnlyByTheMergeManager() {
        MergeManager mergeManager = new MergeManager(null);
        mergeManager.transitionToDeferredLocks(); // transitioned while acquiredLocks was empty: no manager was created
        assertNull(ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
        CacheKey first = acquiredKey(1L);
        CacheKey second = acquiredKey(2L);
        mergeManager.getAcquiredLocks().add(first); // what appendLock's fallback does
        mergeManager.getAcquiredLocks().add(second);

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertFree(first);
        assertFree(second);
        assertTrue(mergeManager.getAcquiredLocks().isEmpty());
    }

    @Test
    public void transitionedMergeWithDeferredLockManager_releasesThroughTheDeferredLockManager() {
        MergeManager mergeManager = new MergeManager(null);
        CacheKey tracked = acquiredKey(3L);
        mergeManager.getAcquiredLocks().add(tracked);
        writeLockManager.transitionToDeferredLocks(mergeManager); // manager created, threadDepth 1, activeLocks [tracked]
        DeferredLockManager lockManager = ConcurrencyManager.getDeferredLockManager(Thread.currentThread());
        assertNotNull(lockManager);
        CacheKey appended = acquiredKey(4L);
        lockManager.getActiveLocks().add(appended); // appendLock with a live manager

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertFree(tracked);
        assertFree(appended);
        assertNull(ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
    }

    @Test
    public void transitionedMerge_mixedTrackedAndUntrackedKeys_releasesBoth() {
        MergeManager mergeManager = new MergeManager(null);
        CacheKey tracked = acquiredKey(5L);
        mergeManager.getAcquiredLocks().add(tracked);
        writeLockManager.transitionToDeferredLocks(mergeManager);
        CacheKey untracked = acquiredKey(6L);
        mergeManager.getAcquiredLocks().add(untracked); // in acquiredLocks, not in the manager's activeLocks

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertFree(tracked);
        assertFree(untracked);
    }

    @Test
    public void untrackedKeyWithTheSamePrimaryKeyAsATrackedOne_isReleasedDirectly() {
        // CacheKey.equals compares primary keys; tracking must be decided by identity
        MergeManager mergeManager = new MergeManager(null);
        CacheKey tracked = acquiredKey(7L);
        mergeManager.getAcquiredLocks().add(tracked);
        writeLockManager.transitionToDeferredLocks(mergeManager);
        CacheKey lookalike = acquiredKey(7L);
        mergeManager.getAcquiredLocks().add(lookalike);

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertFree(tracked);
        assertFree(lookalike);
    }

    @Test
    public void notTransitionedMerge_releasesEveryKeyAsBefore() {
        MergeManager mergeManager = new MergeManager(null);
        CacheKey first = acquiredKey(8L);
        CacheKey second = acquiredKey(9L);
        mergeManager.getAcquiredLocks().add(first);
        mergeManager.getAcquiredLocks().add(second);

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertFree(first);
        assertFree(second);
    }

    @Test
    public void keyAcquiredByAnotherThread_isLeftAlone() throws InterruptedException {
        MergeManager mergeManager = new MergeManager(null);
        mergeManager.transitionToDeferredLocks();
        CacheKey other = new CacheKey(10L);
        Thread owner = new Thread(other::acquire, "release-all-acquired-locks-test-owner");
        owner.start();
        owner.join();
        mergeManager.getAcquiredLocks().add(other);

        writeLockManager.releaseAllAcquiredLocks(mergeManager);

        assertTrue(other.isAcquired());
        assertSame(owner, other.getActiveThread());
        other.setActiveThread(Thread.currentThread());
        other.release();
    }

    private static CacheKey acquiredKey(Object primaryKey) {
        CacheKey key = new CacheKey(primaryKey);
        key.acquire();
        assertTrue(key.isAcquired());
        assertSame(Thread.currentThread(), key.getActiveThread());
        return key;
    }

    private static void assertFree(CacheKey key) {
        assertFalse("still acquired: " + key, key.isAcquired());
        assertNull("still owned: " + key, key.getActiveThread());
    }
}
