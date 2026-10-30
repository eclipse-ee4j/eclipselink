/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.junit.After;
import org.junit.Test;

/**
 * Verifies that {@link ConcurrencyManager} and {@link WriteLockManager} static helpers tolerate a {@code null}
 * thread.
 *
 * <p>{@link ConcurrencyManager#getActiveThread()} is cleared to {@code null} by {@code release()}, so another thread
 * that read it just before (for example in {@code beforeCompletion}/{@code afterCompletion} under asynchronous
 * dispatch) can end up passing {@code null} to these helpers. They are backed by a {@code ConcurrentHashMap}, which
 * rejects {@code null} keys with a {@link NullPointerException}.
 */
public class NullThreadLockManagerTest {

    private final Thread thread = new Thread(() -> { }, "null-thread-lock-manager-test");

    @After
    public void cleanUp() {
        WriteLockManager.clearMapThreadToObjectIdsWithChagenSet(thread);
        WriteLockManager.clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(thread);
    }

    @Test
    public void getDeferredLockManager_nullThread_returnsNull() {
        assertNull(ConcurrencyManager.getDeferredLockManager(null));
    }

    @Test
    public void clearMapThreadToObjectIdsWithChangeSet_nullThread_isNoOp() {
        WriteLockManager.clearMapThreadToObjectIdsWithChagenSet(null);
    }

    @Test
    public void populateMapThreadToObjectIdsWithChangeSet_nullThread_isNoOp() {
        int before = WriteLockManager.getMapWriteLockManagerThreadToObjectIdsWithChangeSetSnapshot().size();

        WriteLockManager.populateMapThreadToObjectIdsWithChagenSet(null, List.of());

        assertEquals(before, WriteLockManager.getMapWriteLockManagerThreadToObjectIdsWithChangeSetSnapshot().size());
    }

    @Test
    public void populateMapThreadToObjectIdsWithChangeSet_skipsChangeSetsWithoutId() {
        // a default ObjectChangeSet has no id
        ObjectChangeSet withoutId = new ObjectChangeSet();

        WriteLockManager.populateMapThreadToObjectIdsWithChagenSet(thread, List.of(withoutId));

        Set<Object> ids = WriteLockManager.getMapWriteLockManagerThreadToObjectIdsWithChangeSetSnapshot().get(thread);
        assertTrue("A change set without an id must not be recorded", ids == null || ids.isEmpty());
    }

    @Test
    public void clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired_nullThread_isNoOp() {
        WriteLockManager.clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(null);
    }

    @Test
    public void removeCacheKeyThatCouldNotBeAcquired_nullThread_isNoOp() {
        WriteLockManager
                .removeCacheKeyFromMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(null, new ConcurrencyManager());
    }

    /**
     * The "add" path mutates the set returned for the thread, so a null thread must not get an immutable set
     * ({@code UnsupportedOperationException}).
     */
    @Test
    public void addCacheKeyThatCouldNotBeAcquired_nullThread_doesNotThrow() throws InterruptedException {
        int before = WriteLockManager.getThreadToFailToAcquireCacheKeysSnapshot().size();

        WriteLockManager.addCacheKeyToMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(
                null, new ConcurrencyManager(), System.currentTimeMillis());

        assertEquals(before, WriteLockManager.getThreadToFailToAcquireCacheKeysSnapshot().size());
    }
}
