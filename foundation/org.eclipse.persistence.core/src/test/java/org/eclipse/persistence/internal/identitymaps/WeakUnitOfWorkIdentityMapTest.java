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

package org.eclipse.persistence.internal.identitymaps;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link WeakUnitOfWorkIdentityMap#cleanupDeadCacheKeys()} removes the cache keys whose objects were
 * garbage collected.
 *
 * <p>The garbage collector clears a weak reference before it enqueues it, so a reference taken from the queue never
 * returns its object. {@link Reference#enqueue()} does the same (clear, then enqueue), which lets the
 * test simulate a collection deterministically.
 */
public class WeakUnitOfWorkIdentityMapTest {

    private final WeakUnitOfWorkIdentityMap map = new WeakUnitOfWorkIdentityMap(32, null, null, true);

    @Test
    public void removesCollectedKeyAndKeepsLiveKey() {
        Object live = new Object();
        CacheKey liveKey = register(1, live);
        Object dead = new Object();
        CacheKey deadKey = register(2, dead);

        collect(deadKey);
        Reference.reachabilityFence(dead);

        try {
            assertDoesNotThrow(map::cleanupDeadCacheKeys);
            assertEquals(1, map.getSize());
            assertNull(map.getCacheKey(2, false));
            assertNull(deadKey.getOwningMap());
            assertSame(liveKey, map.getCacheKey(1, false));
            assertSame(live, liveKey.getObject());
            assertSame(map, liveKey.getOwningMap());
        } finally {
            Reference.reachabilityFence(live);
        }
    }

    @Test
    public void removesEveryCollectedKey() {
        Object firstObject = new Object();
        Object secondObject = new Object();
        CacheKey first = register(1, firstObject);
        CacheKey second = register(2, secondObject);
        collect(first);
        collect(second);
        Reference.reachabilityFence(firstObject);
        Reference.reachabilityFence(secondObject);

        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(0, map.getSize());
        assertNull(map.getCacheKey(1, false));
        assertNull(map.getCacheKey(2, false));
        assertNull(first.getOwningMap());
        assertNull(second.getOwningMap());
    }

    @Test
    public void registrationAutomaticallyRemovesCollectedKey() {
        List<Object> retained = new ArrayList<>();
        Object live = new Object();
        retained.add(live);
        CacheKey liveKey = register(1, live);
        Object dead = new Object();
        CacheKey deadKey = register(2, dead);
        collect(deadKey);
        Reference.reachabilityFence(dead);

        try {
            for (int id = 3; id <= 33; id++) {
                Object object = new Object();
                retained.add(object);
                register(id, object);
            }
            assertSame(deadKey, map.getCacheKey(2, false));
            assertSame(map, deadKey.getOwningMap());

            // Cleanup is checked before incrementing the counter, so insertion 34 drains a size-32 map.
            Object last = new Object();
            retained.add(last);
            register(34, last);

            assertEquals(33, map.getSize());
            assertNull(map.getCacheKey(2, false));
            assertNull(deadKey.getOwningMap());
            assertSame(liveKey, map.getCacheKey(1, false));
            assertSame(live, liveKey.getObject());
            assertSame(map, liveKey.getOwningMap());
            assertDoesNotThrow(map::cleanupDeadCacheKeys);
            assertEquals(33, map.getSize());
        } finally {
            Reference.reachabilityFence(retained);
        }
    }

    @Test
    public void emptyAndRepeatedCleanupPreserveLiveKey() {
        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(0, map.getSize());

        Object live = new Object();
        CacheKey liveKey = register(1, live);
        try {
            assertDoesNotThrow(map::cleanupDeadCacheKeys);
            assertDoesNotThrow(map::cleanupDeadCacheKeys);
            assertEquals(1, map.getSize());
            assertSame(liveKey, map.getCacheKey(1, false));
            assertSame(live, liveKey.getObject());
            assertSame(map, liveKey.getOwningMap());
        } finally {
            Reference.reachabilityFence(live);
        }
    }

    /**
     * Registers an object the way a unit of work builds a clone: acquire an empty cache key, then set the object.
     */
    private CacheKey register(Object primaryKey, Object object) {
        CacheKey key = map.acquireLock(primaryKey, false, false);
        key.setObject(object);
        return key;
    }

    private static void collect(CacheKey key) {
        assertTrue(((QueueableWeakCacheKey) key).reference.enqueue());
        assertNull(key.getObject());
    }
}
