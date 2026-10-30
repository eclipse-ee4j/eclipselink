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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.junit.After;
import org.junit.Test;

/**
 * {@link CacheKey#acquireLock(org.eclipse.persistence.queries.ObjectBuildingQuery)} retries a deferred acquire
 * while another thread is still building the object and gives up with
 * {@link ConcurrencyException#maxTriesLockOnBuildObjectExceded}. The retries only run to exhaustion inside an
 * outer deferred build (at nesting depth 1 the loop's own {@code releaseDeferredLock()} waits for the owner
 * instead). Giving up must leave the thread's {@link DeferredLockManager} as it was before the call: an extra
 * nesting level makes every later outermost {@code releaseDeferredLock()} on this thread release nothing.
 * <p>
 * The exhaustion takes about 10 s (1000 retries of 10 ms, hard-coded in the loop).
 */
public class DeferredLockMaxTriesTest {

    private final CountDownLatch acquired = new CountDownLatch(1);
    private final CountDownLatch finish = new CountDownLatch(1);
    private Thread owner;

    @After
    public void cleanUp() throws InterruptedException {
        finish.countDown();
        if (owner != null) {
            owner.join(TimeUnit.SECONDS.toMillis(5));
        }
        ConcurrencyManager.removeDeferredLockManager(Thread.currentThread());
    }

    @Test
    public void givingUpInsideAnOuterDeferredBuild_restoresTheOuterNestingLevel() throws InterruptedException {
        CacheKey outer = new CacheKey(2L);
        outer.acquireDeferredLock(); // this thread owns it: threadDepth 1, activeLocks [outer]
        DeferredLockManager lockManager = ConcurrencyManager.getDeferredLockManager(Thread.currentThread());
        assertNotNull(lockManager);
        assertEquals(1, lockManager.getThreadDepth());
        CacheKey key = heldByAnotherThreadWithoutObject(3L);

        try {
            key.acquireLock(deferredQuery());
            fail("expected maxTriesLockOnBuildObjectExceded");
        } catch (ConcurrencyException exception) {
            assertEquals(ConcurrencyException.MAX_TRIES_EXCEDED_FOR_LOCK_ON_BUILD_OBJECT, exception.getErrorCode());
        }

        assertSame(lockManager, ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
        assertEquals("the failed retry's nesting level was not undone", 1, lockManager.getThreadDepth());
        assertFalse("the failed retry left a stale deferred entry", lockManager.getDeferredLocks().contains(key));
        assertTrue(key.isAcquired());
        assertSame(owner, key.getActiveThread());

        outer.releaseDeferredLock(); // outermost: must release outer and drop the manager
        assertFalse(outer.isAcquired());
        assertNull(ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
    }

    private CacheKey heldByAnotherThreadWithoutObject(Object primaryKey) throws InterruptedException {
        CacheKey key = new CacheKey(primaryKey);
        owner = new Thread(() -> {
            key.acquire();
            acquired.countDown();
            try {
                finish.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            key.release();
        }, "deferred-lock-max-tries-owner");
        owner.start();
        assertTrue(acquired.await(5, TimeUnit.SECONDS));
        assertNull(key.getObject());
        return key;
    }

    private static ReadObjectQuery deferredQuery() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setRequiresDeferredLocks(true);
        return query;
    }
}
