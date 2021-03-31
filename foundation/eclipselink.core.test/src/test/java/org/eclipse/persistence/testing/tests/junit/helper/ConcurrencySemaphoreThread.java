/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.helper;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.persistence.internal.helper.ConcurrencySemaphore;
import org.junit.Assert;

import static org.junit.Assert.assertTrue;

public class ConcurrencySemaphoreThread implements Runnable {

    /**
     * Semaphore related properties.
     */
    private static final ThreadLocal<Boolean> SEMAPHORE_THREAD_LOCAL_VAR = new ThreadLocal<>();
    private static final int SEMAPHORE_MAX_NUMBER_THREADS = 12;
    private static final Semaphore SEMAPHORE_LIMIT_MAX_NUMBER_OF_THREADS_IN_TEST = new Semaphore(SEMAPHORE_MAX_NUMBER_THREADS);
    private ConcurrencySemaphore testSemaphore = new ConcurrencySemaphore(SEMAPHORE_THREAD_LOCAL_VAR, SEMAPHORE_MAX_NUMBER_THREADS, SEMAPHORE_LIMIT_MAX_NUMBER_OF_THREADS_IN_TEST, this, "object_builder_semaphore_acquired_01");

    private static AtomicInteger currentNoOfThreads = new AtomicInteger(0);

    public void run() {
        boolean semaphoreWasAcquired = false;
        try {
            //Semaphore call
            semaphoreWasAcquired = testSemaphore.acquireSemaphoreIfAppropriate(true);
            //Current No of threads there can't be more than SEMAPHORE_MAX_NUMBER_THREADS
            assertTrue(currentNoOfThreads.incrementAndGet() <= SEMAPHORE_MAX_NUMBER_THREADS);
            //Instead of some code which should take some time is there sleep.
            Thread.currentThread().sleep(300);
        } catch (InterruptedException ex) {
            Assert.fail("Semaphore Test thread was interrupted.  Test failed to run properly");
        } finally {
            currentNoOfThreads.decrementAndGet();
            testSemaphore.releaseSemaphoreAllowOtherThreadsToStartDoingObjectBuilding(semaphoreWasAcquired);
        }
    }
}
