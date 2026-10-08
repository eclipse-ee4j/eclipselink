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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.junit.jupiter.api.Test;

/**
 * The read locks taken for one clone are released in a loop - in {@code UnitOfWorkImpl.cloneAndRegisterObject},
 * {@code AbstractSession.cloneAndRegisterObject},
 * {@code IsolatedClientSessionIdentityMapAccessor.getAndCloneCacheKeyFromParent} and twice in
 * {@code WriteLockManager.acquireLocksForClone} - and each of those loops sits in a {@code finally}. A single
 * release that blows up must not abort the loop: every cache key behind the failing one would keep a reader that
 * nobody removes, and such an entry can never be built again, because {@code acquireDeferredLock} waits for the
 * maximum and then fails for the remaining life of the session.
 *
 * @see <a href="https://github.com/eclipse-ee4j/eclipselink/issues/2609">issue 2609</a>
 */
class CacheKeyReadLockReleaseTest {

    /**
     * Three read-locked cache keys of one clone, of which the middle one has meanwhile been taken down to zero
     * readers by somebody else - the state a concurrent releaser leaves behind. Releasing it once more raises
     * {@link ConcurrencyException#signalAttemptedBeforeWait()}.
     */
    private static List<CacheKey> lockedSetWithADrainedKeyInTheMiddle() {
        List<CacheKey> keys = List.of(new CacheKey(1), new CacheKey(2), new CacheKey(3));
        keys.forEach(CacheKey::acquireReadLock);
        keys.get(1).releaseReadLock();
        return keys;
    }

    @Test
    void plainReleaseAbortsTheLoopAndStrandsTheRest() {
        List<CacheKey> keys = lockedSetWithADrainedKeyInTheMiddle();

        assertThrows(ConcurrencyException.class, () -> keys.forEach(CacheKey::releaseReadLock));

        assertEquals(0, keys.get(0).getNumberOfReaders(), "released before the failing key");
        assertEquals(1, keys.get(2).getNumberOfReaders(), "stranded behind the failing key");
    }

    @Test
    void quietReleaseFinishesTheLoop() {
        List<CacheKey> keys = lockedSetWithADrainedKeyInTheMiddle();

        keys.forEach(CacheKey::releaseReadLockQuietly);

        keys.forEach(key -> assertEquals(0, key.getNumberOfReaders(), "every key of the set is free"));
    }

    @Test
    void quietReleaseOfADrainedKeyDoesNotThrow() {
        CacheKey key = new CacheKey(1);

        assertDoesNotThrow(key::releaseReadLockQuietly);

        assertEquals(0, key.getNumberOfReaders());
    }
}
