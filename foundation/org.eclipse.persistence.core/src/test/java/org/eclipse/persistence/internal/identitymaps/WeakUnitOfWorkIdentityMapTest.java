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
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
 * returns its object. {@link java.lang.ref.Reference#enqueue()} does the same (clear, then enqueue), which lets the
 * test simulate a collection deterministically.
 */
public class WeakUnitOfWorkIdentityMapTest {

    private final WeakUnitOfWorkIdentityMap map = new WeakUnitOfWorkIdentityMap(32, null, null, true);
    private final List<Object> registeredObjects = new ArrayList<>();

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
        assertNull(deadKey.getOwningMap());
        assertSame(map, liveKey.getOwningMap());
        Reference.reachabilityFence(live);
    }

    @Test
    public void removesEveryCollectedKey() {
        collect(register(1, new Object()));
        collect(register(2, new Object()));

        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(0, map.getSize());
    }

    @Test
    public void keepsRepopulatedKey() {
        CacheKey key = register(1, new Object());
        collect(key);
        Object live = new Object();
        assertSame(key, register(1, live));

        map.cleanupDeadCacheKeys();

        assertEquals(1, map.getSize());
        assertSame(key, map.getCacheKey(1, false));
        assertSame(live, key.getObject());
        assertSame(map, key.getOwningMap());
        Reference.reachabilityFence(live);
    }

    @Test
    public void keepsReplacementAfterRemoval() {
        CacheKey oldKey = register(1, new Object());
        map.remove(oldKey);
        collect(oldKey);
        Object live = new Object();
        CacheKey replacement = register(1, live);
        assertNotSame(oldKey, replacement);

        map.cleanupDeadCacheKeys();

        assertEquals(1, map.getSize());
        assertSame(replacement, map.getCacheKey(1, false));
        assertSame(live, replacement.getObject());
        assertSame(map, replacement.getOwningMap());
        assertNull(oldKey.getOwningMap());
        Reference.reachabilityFence(live);
    }

    @Test
    public void waitsForCurrentReferenceEvenWhenItIsCleared() {
        CacheKey key = register(1, new Object());
        collect(key);
        Object replacement = new Object();
        key.setObject(replacement);
        Reference<?> current = ((QueueableWeakCacheKey) key).reference;
        current.clear();

        map.cleanupDeadCacheKeys();

        assertSame(key, map.getCacheKey(1, false));
        assertSame(map, key.getOwningMap());
        assertNull(key.getObject());
        assertTrue(current.enqueue());
        map.cleanupDeadCacheKeys();
        assertEquals(0, map.getSize());
        assertNull(key.getOwningMap());
        Reference.reachabilityFence(replacement);
    }

    @Test
    public void drainsMixedEventsAndIgnoresRepeatedCleanup() {
        Object live = new Object();
        CacheKey liveKey = register(1, live);
        CacheKey repopulated = register(2, new Object());
        collect(repopulated);
        Object replacement = new Object();
        repopulated.setObject(replacement);
        CacheKey dead = register(3, new Object());
        collect(dead);
        CacheKey removed = register(4, new Object());
        map.remove(removed);
        collect(removed);

        map.cleanupDeadCacheKeys();
        map.cleanupDeadCacheKeys();

        assertEquals(2, map.getSize());
        assertSame(liveKey, map.getCacheKey(1, false));
        assertSame(live, liveKey.getObject());
        assertSame(repopulated, map.getCacheKey(2, false));
        assertSame(replacement, repopulated.getObject());
        assertSame(map, repopulated.getOwningMap());
        assertNull(map.getCacheKey(3, false));
        assertNull(dead.getOwningMap());
        assertNull(map.getCacheKey(4, false));
        assertNull(removed.getOwningMap());
        Reference.reachabilityFence(live);
        Reference.reachabilityFence(replacement);
    }

    @Test
    public void insertionCleanupKeepsReplacementAndRepopulatedKeys() {
        WeakUnitOfWorkIdentityMap smallMap = new WeakUnitOfWorkIdentityMap(2, null, null, true);
        CacheKey oldKey = register(smallMap, 1, new Object());
        collect(oldKey);
        CacheKey repopulated = register(smallMap, 2, new Object());
        collect(repopulated);
        Object repopulatedObject = new Object();
        repopulated.setObject(repopulatedObject);
        CacheKey dead = register(smallMap, 3, new Object());
        collect(dead);
        Object live = new Object();

        // The fourth insertion crosses the normal cleanup threshold while replacing the old owner.
        CacheKey replacement = smallMap.put(1, live, null, 0);

        assertNotSame(oldKey, replacement);
        assertEquals(2, smallMap.getSize());
        assertSame(replacement, smallMap.getCacheKey(1, false));
        assertSame(live, replacement.getObject());
        assertSame(smallMap, replacement.getOwningMap());
        assertSame(smallMap, oldKey.getOwningMap());
        assertSame(repopulated, smallMap.getCacheKey(2, false));
        assertSame(repopulatedObject, repopulated.getObject());
        assertSame(smallMap, repopulated.getOwningMap());
        assertNull(smallMap.getCacheKey(3, false));
        assertNull(dead.getOwningMap());
        Reference.reachabilityFence(live);
        Reference.reachabilityFence(repopulatedObject);
    }

    @Test
    public void cleanupOfEmptyMapIsHarmless() {
        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertDoesNotThrow(map::cleanupDeadCacheKeys);
        assertEquals(0, map.getSize());
    }

    @Test
    public void removesCurrentKeyFromClonedMap() {
        CacheKey original = register(1, new Object());
        Object live = new Object();
        CacheKey originalLive = register(2, live);
        WeakUnitOfWorkIdentityMap clonedMap = (WeakUnitOfWorkIdentityMap) map.clone();
        CacheKey cloned = clonedKey(clonedMap, original);
        CacheKey clonedLive = clonedKey(clonedMap, originalLive);

        collect(cloned);
        clonedMap.cleanupDeadCacheKeys();

        assertNull(clonedMap.getCacheKey(1, false));
        assertEquals(1, clonedMap.getSize());
        assertNull(cloned.getOwningMap());
        assertSame(original, map.getCacheKey(1, false));
        assertSame(map, original.getOwningMap());
        assertSame(clonedLive, clonedMap.getCacheKey(2, false));
        assertSame(live, clonedLive.getObject());
        assertSame(map, clonedLive.getOwningMap());
        assertSame(map, originalLive.getOwningMap());
        Reference.reachabilityFence(live);
    }

    @Test
    public void removesClonedKeyAfterOriginalOwnerIsRepopulated() {
        CacheKey original = register(1, new Object());
        WeakUnitOfWorkIdentityMap clonedMap = (WeakUnitOfWorkIdentityMap) map.clone();
        CacheKey cloned = clonedKey(clonedMap, original);
        Object live = new Object();
        assertSame(original, register(1, live));
        assertNotSame(((QueueableWeakCacheKey) original).reference, ((QueueableWeakCacheKey) cloned).reference);

        collect(cloned);
        clonedMap.cleanupDeadCacheKeys();

        assertNull(clonedMap.getCacheKey(1, false));
        assertEquals(0, clonedMap.getSize());
        assertNull(cloned.getOwningMap());
        assertSame(original, map.getCacheKey(1, false));
        assertSame(live, original.getObject());
        assertSame(map, original.getOwningMap());
        Reference.reachabilityFence(live);
    }

    @Test
    public void clonedMapIgnoresStaleEventsAndWaitsForCurrentReference() {
        CacheKey originalRepopulated = register(1, new Object());
        CacheKey originalReplaced = register(2, new Object());
        CacheKey originalCleared = register(3, new Object());
        WeakUnitOfWorkIdentityMap clonedMap = (WeakUnitOfWorkIdentityMap) map.clone();
        CacheKey repopulated = clonedKey(clonedMap, originalRepopulated);
        CacheKey replaced = clonedKey(clonedMap, originalReplaced);
        CacheKey cleared = clonedKey(clonedMap, originalCleared);
        collect(repopulated);
        collect(replaced);
        collect(cleared);
        Object liveRepopulated = new Object();
        assertSame(repopulated, register(clonedMap, 1, liveRepopulated));
        Object liveReplacement = new Object();
        CacheKey replacement = clonedMap.put(2, liveReplacement, null, 0);
        assertNotSame(replaced, replacement);
        Object clearedObject = new Object();
        assertSame(cleared, register(clonedMap, 3, clearedObject));
        Reference<?> current = ((QueueableWeakCacheKey) cleared).reference;
        current.clear();

        clonedMap.cleanupDeadCacheKeys();

        assertEquals(3, clonedMap.getSize());
        assertSame(repopulated, clonedMap.getCacheKey(1, false));
        assertSame(liveRepopulated, repopulated.getObject());
        assertSame(map, repopulated.getOwningMap());
        assertSame(replacement, clonedMap.getCacheKey(2, false));
        assertSame(liveReplacement, replacement.getObject());
        assertSame(clonedMap, replacement.getOwningMap());
        assertSame(map, replaced.getOwningMap());
        assertSame(cleared, clonedMap.getCacheKey(3, false));
        assertNull(cleared.getObject());
        assertSame(map, cleared.getOwningMap());

        assertTrue(current.enqueue());
        clonedMap.cleanupDeadCacheKeys();

        assertEquals(2, clonedMap.getSize());
        assertNull(clonedMap.getCacheKey(3, false));
        assertNull(cleared.getOwningMap());
        assertSame(map, originalRepopulated.getOwningMap());
        assertSame(map, originalReplaced.getOwningMap());
        assertSame(map, originalCleared.getOwningMap());
        Reference.reachabilityFence(liveRepopulated);
        Reference.reachabilityFence(liveReplacement);
        Reference.reachabilityFence(clearedObject);
    }

    private CacheKey clonedKey(WeakUnitOfWorkIdentityMap clonedMap, CacheKey original) {
        assertNotSame(map, clonedMap);
        CacheKey cloned = clonedMap.getCacheKey(original.getKey(), false);
        assertNotSame(original, cloned);
        assertSame(((QueueableWeakCacheKey) original).reference, ((QueueableWeakCacheKey) cloned).reference);
        assertSame(original.getOwningMap(), cloned.getOwningMap());
        return cloned;
    }

    /**
     * Registers an object the way a unit of work builds a clone: acquire an empty cache key, then set the object.
     */
    private CacheKey register(Object primaryKey, Object object) {
        return register(map, primaryKey, object);
    }

    private CacheKey register(WeakUnitOfWorkIdentityMap map, Object primaryKey, Object object) {
        registeredObjects.add(object);
        CacheKey key = map.acquireLock(primaryKey, false, false);
        key.setObject(object);
        return key;
    }

    private void collect(CacheKey key) {
        assertTrue(((QueueableWeakCacheKey) key).reference.enqueue());
        assertNull(key.getObject());
        Reference.reachabilityFence(registeredObjects);
    }
}
