/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.entityagent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentUpsertRaces {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = AgentRaceRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @After
    public void releaseCallback() {
        AgentRaceRecord.beforeUpsert = null;
    }

    @Test
    public void competingMissingRowsPreserveIdVersionAndSingleCallbackPair() throws Exception {
        CyclicBarrier ready = new CyclicBarrier(2);
        AgentRaceRecord.beforeUpsert = entity -> {
            try {
                ready.await(30, TimeUnit.SECONDS);
            } catch (Exception failure) {
                throw new IllegalStateException(failure);
            }
        };
        AgentRaceRecord first = new AgentRaceRecord(1, "first");
        AgentRaceRecord second = new AgentRaceRecord(1, "second");
        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstResult = executor.submit(() -> upsert(first));
            var secondResult = executor.submit(() -> upsert(second));
            PersistenceException firstFailure = firstResult.get(45, TimeUnit.SECONDS);
            PersistenceException secondFailure = secondResult.get(45, TimeUnit.SECONDS);
            assertTrue((firstFailure == null) != (secondFailure == null));
            assertNotNull(firstFailure == null ? secondFailure.getCause() : firstFailure.getCause());
            assertEquals(1, first.preUpserts);
            assertEquals(1, second.preUpserts);
            assertEquals(1, first.postUpserts + second.postUpserts);
            AgentRaceRecord winner = firstFailure == null ? first : second;
            AgentRaceRecord loser = firstFailure == null ? second : first;
            assertEquals(1L, winner.id);
            assertEquals(1L, loser.id);
            assertEquals(0, loser.getVersion());
            try (EntityAgent observer = emf.createEntityAgent()) {
                AgentRaceRecord stored = observer.get(AgentRaceRecord.class, 1L);
                assertEquals(winner.getName(), stored.getName());
                assertEquals(winner.getVersion(), stored.getVersion());
            }
        }
    }

    @Test
    public void deletionAfterExistenceReadIsAnOptimisticConflict() throws Exception {
        AgentRaceRecord detached;
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.insert(new AgentRaceRecord(2, "initial"));
            detached = agent.get(AgentRaceRecord.class, 2L);
        }
        int version = detached.getVersion();
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch deleted = new CountDownLatch(1);
        AgentRaceRecord.beforeUpsert = entity -> {
            ready.countDown();
            try {
                if (!deleted.await(30, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Competing delete did not complete");
                }
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(failure);
            }
        };
        try (var executor = Executors.newSingleThreadExecutor()) {
            var result = executor.submit(() -> upsert(detached));
            try {
                assertTrue(ready.await(30, TimeUnit.SECONDS));
                try (EntityAgent peer = emf.createEntityAgent()) {
                    peer.delete(peer.get(AgentRaceRecord.class, 2L));
                }
            } finally {
                deleted.countDown();
            }
            assertTrue(result.get(45, TimeUnit.SECONDS) instanceof OptimisticLockException);
            assertEquals(version, detached.getVersion());
            assertEquals(1, detached.preUpserts);
            assertEquals(0, detached.postUpserts);
            try (EntityAgent observer = emf.createEntityAgent()) {
                assertNull(observer.find(AgentRaceRecord.class, 2L));
            }
        }
    }

    private PersistenceException upsert(AgentRaceRecord entity) {
        try (EntityAgent agent = emf.createEntityAgent()) {
            try {
                agent.upsert(entity);
                return null;
            } catch (PersistenceException failure) {
                return failure;
            }
        }
    }
}
