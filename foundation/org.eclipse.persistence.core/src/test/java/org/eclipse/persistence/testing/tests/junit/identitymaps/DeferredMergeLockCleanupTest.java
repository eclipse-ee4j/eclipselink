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

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Fixture;
import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Route;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DeferredMergeLockCleanupTest {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> routes() {
        return Arrays.asList(new Object[][] {{Route.CHANGE_SET}, {Route.SESSION_ORIGIN}, {Route.SESSION_WAITLOOP}});
    }

    private final Route route;

    public DeferredMergeLockCleanupTest(Route route) {
        this.route = route;
    }

    @Test(timeout = 15000)
    public void balancesDeferredAcquisitionWhenTheObjectIsAlreadyAvailable() throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            fixture.referenceKey.setObject(fixture.reference);
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(true);
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @Test(timeout = 15000)
    public void preservesTheOriginalExceptionAndReleasesDeferredLocks() throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            RuntimeException expected = new IllegalStateException("Injected failure after deferred acquisition");
            fixture.referenceKey.failureAfterDeferredAcquire = expected;
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(true);
            ExecutionException failure = assertThrows(ExecutionException.class, () -> read.get(5, TimeUnit.SECONDS));
            assertSame(expected, failure.getCause());
            fixture.assertReleased();
        }
    }
}
