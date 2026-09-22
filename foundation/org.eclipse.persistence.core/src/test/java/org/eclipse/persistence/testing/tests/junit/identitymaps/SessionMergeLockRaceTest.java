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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Fixture;
import org.eclipse.persistence.testing.tests.junit.identitymaps.CacheKeyConcurrencyTest.Route;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionMergeLockRaceTest {
    @Test(timeout = 15000)
    public void changeSetDoesNotWaitForAnEmptyKeyNowOwnedByTheReader() throws Exception {
        releasesKeyBetweenReadAndDeferredAcquisition(Route.CHANGE_SET);
    }

    @Test(timeout = 15000)
    public void originDoesNotWaitForAnEmptyKeyNowOwnedByTheReader() throws Exception {
        releasesKeyBetweenReadAndDeferredAcquisition(Route.SESSION_ORIGIN);
    }

    @Test(timeout = 15000)
    public void waitLoopBalancesTheEarlyReturnWhenTheOtherOwnerHasFinished() throws Exception {
        releasesKeyBetweenReadAndDeferredAcquisition(Route.SESSION_WAITLOOP);
    }

    private void releasesKeyBetweenReadAndDeferredAcquisition(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            CountDownLatch beforeAcquire = new CountDownLatch(1);
            CountDownLatch ownerReleased = new CountDownLatch(1);
            fixture.referenceKey.beforeDeferredAcquire = () -> {
                beforeAcquire.countDown();
                try {
                    assertTrue(ownerReleased.await(5, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            };
            Future<Object> read = fixture.start();
            try {
                assertTrue(beforeAcquire.await(5, TimeUnit.SECONDS));
                // A failed/cancelled construction can release its key without publishing an object.
                fixture.finishConstruction(false);
                ownerReleased.countDown();
                assertNull(read.get(2, TimeUnit.SECONDS));
                fixture.assertReleased();
            } finally {
                ownerReleased.countDown();
            }
        }
    }
}
