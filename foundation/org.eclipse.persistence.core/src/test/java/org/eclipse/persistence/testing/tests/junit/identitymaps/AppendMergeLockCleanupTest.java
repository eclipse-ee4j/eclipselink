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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Fixture;
import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 15, unit = TimeUnit.SECONDS)
public class AppendMergeLockCleanupTest {
    @Test
    public void releasesDeferredLocksAfterWaitingForAnAppendedObject() throws Exception {
        try (Fixture fixture = new Fixture(Route.CHANGE_SET)) {
            fixture.appendLock = true;
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(true);
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @Test
    public void preservesWaitFailureAndReleasesDeferredLocks() throws Exception {
        try (Fixture fixture = new Fixture(Route.CHANGE_SET)) {
            fixture.appendLock = true;
            RuntimeException expected = ConcurrencyException.maxTriesLockOnBuildObjectExceded(
                    fixture.referenceKey.getActiveThread(), Thread.currentThread());
            fixture.referenceKey.failureDuringObjectWait = expected;
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(true);
            ExecutionException failure = assertThrows(ExecutionException.class, () -> read.get(5, TimeUnit.SECONDS));
            assertSame(expected, failure.getCause());
            fixture.assertReleased();
        }
    }
}
