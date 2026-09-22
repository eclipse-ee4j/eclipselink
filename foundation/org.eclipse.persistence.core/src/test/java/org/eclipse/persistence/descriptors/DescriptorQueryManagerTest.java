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

package org.eclipse.persistence.descriptors;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.junit.jupiter.api.Test;

public class DescriptorQueryManagerTest {

    private static final int MAX_ATTEMPTS = 100000;

    @Test
    public void concurrentWritesToExpressionQueryCacheDoNotThrowNullPointerException() throws Exception {
        DescriptorQueryManager queryManager = new DescriptorQueryManager();
        ReadAllQuery query = new ReadAllQuery();
        assertConcurrentCacheAccessDoesNotFail(
                () -> queryManager.cachedExpressionQueries = null,
                () -> queryManager.getCachedExpressionQuery(query));
    }

    @Test
    public void concurrentWritesToUpdateCallCacheDoNotThrowNullPointerException() throws Exception {
        DescriptorQueryManager queryManager = new DescriptorQueryManager();
        List<DatabaseField> updateFields = List.of(new DatabaseField("id"));
        assertConcurrentCacheAccessDoesNotFail(
                () -> queryManager.cachedUpdateCalls = null,
                () -> queryManager.getCachedUpdateCalls(updateFields));
    }

    private void assertConcurrentCacheAccessDoesNotFail(Runnable dropCache, Runnable accessCache) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean(true);
        try {
            Future<?> writer = executor.submit(() -> {
                ready.countDown();
                start.await();
                while (running.get()) {
                    dropCache.run();
                }
                return null;
            });

            Future<?> reader = executor.submit(() -> {
                ready.countDown();
                start.await();
                for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                    accessCache.run();
                }
                return null;
            });

            ready.await();
            start.countDown();

            try {
                reader.get();
            } catch (ExecutionException exception) {
                throw new AssertionError("Parallel cache writes caused failure", exception.getCause());
            } finally {
                running.set(false);
            }

            writer.get();
        } finally {
            running.set(false);
            start.countDown();
            executor.shutdownNow();
        }
    }
}
