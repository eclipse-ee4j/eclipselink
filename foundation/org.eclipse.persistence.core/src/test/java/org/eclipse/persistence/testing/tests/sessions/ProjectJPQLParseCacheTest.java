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

package org.eclipse.persistence.testing.tests.sessions;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.persistence.sessions.Project;
import org.junit.Test;

public class ProjectJPQLParseCacheTest {

    private static final int READER_COUNT = 100;
    private static final int WRITER_COUNT = 10;
    private static final int MAX_ATTEMPTS = 100000;
    private static final long LATCH_TIMEOUT_SECONDS = 2;
    private static final long TASK_TIMEOUT_SECONDS = 8;

    @Test
    public void concurrentWritesToJPQLParseCacheDoNotThrowNullPointerException() throws Exception {
        TestProject project = new TestProject();
        String query = "select e from Employee e";
        assertConcurrentCacheAccessDoesNotFail(
                project::dropJPQLParseCache,
                () -> project.getJPQLParseCache().get(query));
    }

    private void assertConcurrentCacheAccessDoesNotFail(Runnable dropCache, Runnable accessCache) throws Exception {
        int threadCount = READER_COUNT + WRITER_COUNT;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            List<Future<?>> writerFutures = new ArrayList<>(WRITER_COUNT);
            for (int thread = 0; thread < WRITER_COUNT; thread++) {
                writerFutures.add(executor.submit(() -> {
                    ready.countDown();
                    assertTrue(start.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                    while (running.get()) {
                        dropCache.run();
                    }
                    return null;
                }));
            }

            List<Future<?>> readerFutures = new ArrayList<>(READER_COUNT);
            for (int thread = 0; thread < READER_COUNT; thread++) {
                readerFutures.add(executor.submit(() -> {
                    ready.countDown();
                    assertTrue(start.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                    for (int attempt = 0; attempt < MAX_ATTEMPTS && running.get(); attempt++) {
                        try {
                            accessCache.run();
                        } catch (Throwable exception) {
                            failure.compareAndSet(null, exception);
                            running.set(false);
                        }
                    }
                    return null;
                }));
            }

            assertTrue(ready.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            start.countDown();
            for (Future<?> reader : readerFutures) {
                reader.get(TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
            running.set(false);
            for (Future<?> writer : writerFutures) {
                writer.get(TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        } finally {
            running.set(false);
            start.countDown();
            executor.shutdownNow();
        }

        Throwable exception = failure.get();
        if (exception != null) {
            throw new AssertionError("Parallel cache writes caused cache access failure", exception);
        }
    }

    private static class TestProject extends Project {

        private void dropJPQLParseCache() {
            this.jpqlParseCache = null;
        }
    }
}
