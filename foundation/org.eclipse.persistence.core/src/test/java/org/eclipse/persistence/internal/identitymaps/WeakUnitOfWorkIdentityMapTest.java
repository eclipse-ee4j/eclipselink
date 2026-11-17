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

import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link WeakUnitOfWorkIdentityMap#cleanupDeadCacheKeys()} removes the cache keys whose objects were
 * garbage collected.
 *
 * <p>The garbage collector clears a weak reference before it enqueues it, so a reference taken from the queue never
 * returns its object. {@link java.lang.ref.Reference#enqueue()} does the same (clear, then enqueue), which lets the
 * test simulate a collection deterministically.
 */
public class WeakUnitOfWorkIdentityMapTest {

    private final WeakUnitOfWorkIdentityMap map = new WeakUnitOfWorkIdentityMap(32, null, null, true);

    @Test
    public void removesCollectedKeyAndKeepsLiveKey() {
        Object live = new Object();
        CacheKey liveKey = register(1, live);
        CacheKey deadKey = register(2, new Object());

        collect(deadKey);

        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(1, map.getSize());
        assertNull(map.getCacheKey(2, false));
        assertSame(liveKey, map.getCacheKey(1, false));
        assertSame(live, liveKey.getObject());
    }

    @Test
    public void removesEveryCollectedKey() {
        collect(register(1, new Object()));
        collect(register(2, new Object()));

        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(0, map.getSize());
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
    }
}
