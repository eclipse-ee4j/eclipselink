/*
 * Copyright (c) 2026 DruID. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.junit.identitymaps;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Fixture;
import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 15, unit = TimeUnit.SECONDS)
public class CacheKeyCloneWaitTest {
    @Test
    public void unitOfWorkDoesNotWaitForItsOwnEmptyKey() throws Exception {
        doesNotWaitForItsOwnEmptyKey(Route.UNIT_OF_WORK);
    }

    @Test
    public void isolatedClientDoesNotWaitForItsOwnEmptyKey() throws Exception {
        doesNotWaitForItsOwnEmptyKey(Route.ISOLATED_CLIENT);
    }

    @Test
    public void unitOfWorkWaitsUntilConstructionCompletes() throws Exception {
        waitsUntilConstructionCompletes(Route.UNIT_OF_WORK, false);
    }

    @Test
    public void isolatedClientWaitsUntilConstructionCompletes() throws Exception {
        waitsUntilConstructionCompletes(Route.ISOLATED_CLIENT, false);
    }

    @Test
    public void readOnlyUnitOfWorkWaitsUntilConstructionCompletes() throws Exception {
        waitsUntilConstructionCompletes(Route.UNIT_OF_WORK, true);
    }

    @Test
    public void interruptedReadOnlyUnitOfWorkDoesNotReturnAnUnfinishedObject() throws Exception {
        try (Fixture fixture = new Fixture(Route.UNIT_OF_WORK)) {
            fixture.readOnly = true;
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.referenceKey.getInstanceLock().lock();
            try {
                fixture.referenceKey.setObject(fixture.reference);
                fixture.worker.get().interrupt();
            } finally {
                fixture.referenceKey.getInstanceLock().unlock();
            }
            assertNull(read.get(2, TimeUnit.SECONDS));
            fixture.finishConstruction(true);
            fixture.assertReleased();
        }
    }

    private void doesNotWaitForItsOwnEmptyKey(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            fixture.finishConstruction(false);
            fixture.acquireReferenceKeyBeforeRead = true;
            Future<Object> read = fixture.start();
            assertNull(read.get(2, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    private void waitsUntilConstructionCompletes(Route route, boolean readOnly) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            fixture.readOnly = readOnly;
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.referenceKey.getInstanceLock().lock();
            try {
                fixture.referenceKey.setObject(fixture.reference);
                fixture.referenceKey.getInstanceLockCondition().signalAll();
            } finally {
                fixture.referenceKey.getInstanceLock().unlock();
            }
            assertThrows(TimeoutException.class, () -> read.get(100, TimeUnit.MILLISECONDS));
            fixture.finishConstruction(true);
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }
}
