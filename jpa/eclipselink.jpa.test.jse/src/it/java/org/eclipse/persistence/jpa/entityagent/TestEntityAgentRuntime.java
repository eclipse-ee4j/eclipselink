/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TransactionRequiredException;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jpa.entityagent.model.AgentAddress;
import org.eclipse.persistence.jpa.entityagent.model.AgentRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentSpecialRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentRuntime {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentRecord.class, AgentSpecialRecord.class, AgentTarget.class, AgentRaceRecord.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Test
    public void immediateWritesAndIndependentObservation() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent observer = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(201, "first");
            agent.insert(record);
            assertEquals("first", observer.get(AgentRecord.class, 201L).name);
            record.name = "second";
            agent.update(record);
            assertEquals("second", observer.get(AgentRecord.class, 201L).name);
            agent.delete(record);
            assertNull(observer.find(AgentRecord.class, 201L));
        }
    }

    @Test
    public void transactionUsesSameConnectionAndRollbackUndoesImmediateWork() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent observer = emf.createEntityAgent()) {
            EntityTransaction transaction = agent.getTransaction();
            transaction.begin();
            AgentRecord record = new AgentRecord(202, "uncommitted");
            agent.insert(record);
            assertEquals("uncommitted", agent.get(AgentRecord.class, 202L).name);
            Connection first = agent.<Connection, Connection>callWithConnection(connection -> connection);
            assertSame(first, agent.<Connection, Connection>callWithConnection(connection -> connection));
            transaction.rollback();
            assertNull(observer.find(AgentRecord.class, 202L));
            transaction.begin();
            agent.insert(new AgentRecord(202, "committed"));
            transaction.commit();
            assertEquals("committed", observer.get(AgentRecord.class, 202L).name);
            agent.delete(observer.get(AgentRecord.class, 202L));
        }
    }

    @Test
    public void detachedReadsNeverWriteAndNeverReuseMutableInstances() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(203, "stored");
            agent.insert(record);
            AgentRecord first = agent.get(AgentRecord.class, 203L);
            AgentRecord second = agent.get(AgentRecord.class, 203L);
            assertNotSame(first, second);
            first.name = "not-written";
            agent.getTransaction().begin();
            agent.getTransaction().commit();
            assertEquals("stored", agent.get(AgentRecord.class, 203L).name);
            agent.delete(record);
        }
    }

    @Test
    public void missingAndStaleWritesRemainRecoverable() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(204, "original");
            agent.insert(record);
            AgentRecord stale = agent.get(AgentRecord.class, 204L);
            record.name = "newer";
            agent.update(record);
            Integer staleVersion = stale.version;
            agent.getTransaction().begin();
            stale.name = "stale";
            OptimisticLockException failure = assertThrows(OptimisticLockException.class, () -> agent.update(stale));
            assertSame(stale, failure.getEntity());
            assertEquals(staleVersion, stale.version);
            assertFalse(agent.getTransaction().getRollbackOnly());
            agent.insert(new AgentRecord(205, "after-failure"));
            agent.getTransaction().commit();
            assertEquals("after-failure", agent.get(AgentRecord.class, 205L).name);
            assertThrows(OptimisticLockException.class, () -> agent.update(new AgentRecord(9999, "missing")));
            assertThrows(OptimisticLockException.class, () -> agent.delete(new AgentRecord(9999, "missing")));
            agent.delete(record);
            agent.delete(agent.get(AgentRecord.class, 205L));
        }
    }

    @Test
    public void multipleWritesAreOrderedAndFailFast() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.insert(new AgentRecord(207, "duplicate"));
            assertThrows(EntityExistsException.class, () -> agent.insertMultiple(List.of(
                    new AgentRecord(206, "prefix"), new AgentRecord(207, "duplicate"), new AgentRecord(208, "unattempted"))));
            assertEquals("prefix", agent.get(AgentRecord.class, 206L).name);
            assertNull(agent.find(AgentRecord.class, 208L));
            agent.deleteMultiple(List.of(agent.get(AgentRecord.class, 206L), agent.get(AgentRecord.class, 207L)));
        }
    }

    @Test
    public void findMultiplePreservesPositionsAndDuplicates() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(209, "positional");
            agent.insert(record);
            List<AgentRecord> result = agent.findMultiple(AgentRecord.class, List.of(209L, 9999L, 209L));
            assertEquals(3, result.size());
            assertEquals(record.id, result.get(0).id);
            assertNull(result.get(1));
            assertEquals(record.id, result.get(2).id);
            assertThrows(EntityNotFoundException.class, () -> agent.getMultiple(AgentRecord.class, List.of(209L, 9999L)));
            assertThrows(IllegalArgumentException.class, () -> agent.find(AgentRecord.class, "wrong-type"));
            assertThrows(IllegalArgumentException.class, () -> agent.findMultiple(AgentRecord.class, Arrays.asList(209L, null)));
            agent.delete(record);
        }
    }

    @Test
    public void factoryHelpersCommitRollbackAndCloseTheHandler() {
        EntityAgent[] escaped = new EntityAgent[1];
        long result = emf.callInTransaction(EntityAgent.class, agent -> {
            escaped[0] = agent;
            agent.insert(new AgentRecord(210, "helper"));
            return agent.get(AgentRecord.class, 210L).id;
        });
        assertEquals(210L, result);
        assertFalse(escaped[0].isOpen());
        assertThrows(IllegalStateException.class, () -> emf.runInTransaction(EntityAgent.class, agent -> {
            agent.insert(new AgentRecord(211, "rolled-back"));
            throw new IllegalStateException("test work failure");
        }));
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNull(agent.find(AgentRecord.class, 211L));
            agent.delete(agent.get(AgentRecord.class, 210L));
        }
    }

    @Test
    public void optionsSnapshotsAndClosedLifetime() {
        EntityAgent agent = emf.createEntityAgent(CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS);
        EntityTransaction transaction = agent.getTransaction();
        agent.getOptions().clear();
        assertEquals(CacheRetrieveMode.BYPASS, agent.getCacheRetrieveMode());
        agent.setProperty("unknown.entityagent.property", "ignored");
        assertFalse(agent.getProperties().containsKey("unknown.entityagent.property"));
        Map<String, Object> properties = agent.getProperties();
        properties.clear();
        assertFalse(agent.getProperties().isEmpty());
        agent.close();
        assertFalse(agent.isOpen());
        assertSame(transaction, agent.getTransaction());
        agent.getProperties();
        assertThrows(IllegalStateException.class, agent::close);
        assertThrows(IllegalStateException.class, transaction::begin);
        assertThrows(IllegalStateException.class, agent::getEntityManagerFactory);
        assertThrows(IllegalStateException.class, () -> agent.insert(new AgentRecord(212, "closed")));
    }

    @Test
    public void connectionCallbackFailureMarksOnlyActiveTransaction() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            assertThrows(IllegalArgumentException.class, () -> agent.runWithConnection(connection -> {
                throw new IllegalArgumentException("callback failure");
            }));
            assertTrue(agent.getTransaction().getRollbackOnly());
            assertThrows(RollbackException.class, () -> agent.getTransaction().commit());
            assertFalse(agent.getTransaction().isActive());
            assertEquals("value", agent.callWithConnection(connection -> "value"));
        }
    }

    @Test
    public void locksRequireOwnActiveTransaction() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(213, "locked");
            agent.insert(record);
            peer.getTransaction().begin();
            assertThrows(TransactionRequiredException.class,
                    () -> agent.find(AgentRecord.class, 213L, LockModeType.PESSIMISTIC_WRITE));
            peer.getTransaction().rollback();
            agent.getTransaction().begin();
            agent.get(AgentRecord.class, 213L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            agent.getTransaction().rollback();
            assertEquals(record.version, agent.get(AgentRecord.class, 213L).version);
            agent.delete(record);
        }
    }

    @Test
    public void joinedInheritanceSecondaryTablesEmbeddablesAndOwnedCollections() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentTarget target = new AgentTarget(214, "independent");
            agent.insert(target);
            AgentSpecialRecord record = new AgentSpecialRecord(214, "rich", "joined");
            record.note = "secondary";
            record.address = new AgentAddress("Austin");
            record.tags.addAll(List.of("first", "second"));
            record.targets.add(target);
            agent.insert(record);
            var graph = agent.createEntityGraph(AgentSpecialRecord.class);
            graph.addAttributeNodes("tags", "targets");
            AgentSpecialRecord read = agent.get(graph, 214L);
            assertEquals("joined", read.specialty);
            assertEquals("secondary", read.note);
            assertEquals("Austin", read.address.city);
            assertEquals(List.of("first", "second"), read.tags);
            assertEquals(1, read.targets.size());
            read.tags.remove("first");
            read.tags.add("third");
            read.targets.clear();
            read.address.city = "Chicago";
            agent.update(read);
            AgentSpecialRecord changed = agent.get(graph, 214L);
            assertEquals(List.of("second", "third"), changed.tags);
            assertTrue(changed.targets.isEmpty());
            assertEquals("Chicago", changed.address.city);
            agent.delete(changed);
            assertEquals("independent", agent.get(AgentTarget.class, 214L).label);
            agent.delete(target);
        }
    }

    @Test
    public void uncommittedOwnedGraphUsesTheAgentConnectionAndRemainsUsableAfterClose() throws Exception {
        AgentRecord detached;
        try (EntityAgent agent = emf.createEntityAgent();
                var executor = java.util.concurrent.Executors.newSingleThreadExecutor()) {
            assertTrue(new AgentRecord() instanceof org.eclipse.persistence.internal.weaving.PersistenceWeaved);
            agent.getTransaction().begin();
            AgentTarget target = new AgentTarget(216, "uncommitted target");
            agent.insert(target);
            AgentRecord source = new AgentRecord(216, "uncommitted owner");
            source.targets.add(target);
            source.favorite = target;
            source.lazyNote = "uncommitted basic";
            source.tags.add("uncommitted tag");
            agent.insert(source);
            var graph = agent.createEntityGraph(AgentRecord.class);
            graph.addAttributeNodes("favorite", "tags", "targets");
            detached = agent.get(graph, 216L);
            assertEquals("uncommitted basic", detached.getLazyNote());
            assertEquals("uncommitted target", detached.getFavorite().label);
            assertEquals(List.of("uncommitted tag"), detached.tags);
            assertEquals(1, detached.targets.size());
            assertEquals("uncommitted target", detached.targets.getFirst().label);
            assertFalse(emf.getCache().contains(AgentTarget.class, 216L));
            var started = new java.util.concurrent.CountDownLatch(1);
            var visible = executor.submit(() -> {
                try (EntityAgent observer = emf.createEntityAgent()) {
                    started.countDown();
                    return observer.find(AgentRecord.class, 216L) != null
                            || observer.find(AgentTarget.class, 216L) != null;
                }
            });
            try {
                assertTrue(started.await(10, java.util.concurrent.TimeUnit.SECONDS));
                try {
                    assertFalse(visible.get(100, java.util.concurrent.TimeUnit.MILLISECONDS));
                } catch (java.util.concurrent.TimeoutException expected) {
                    // Lock-based READ_COMMITTED may block instead of returning an older snapshot.
                }
            } finally {
                agent.getTransaction().rollback();
            }
            assertFalse(visible.get(30, java.util.concurrent.TimeUnit.SECONDS));
        }
        assertEquals("uncommitted basic", detached.getLazyNote());
        assertEquals("uncommitted target", detached.getFavorite().label);
        assertEquals(List.of("uncommitted tag"), detached.tags);
        assertEquals("uncommitted target", detached.targets.getFirst().label);
    }

    @Test
    public void fetchDetachedManagerCollectionUsesCurrentAgentTransaction() {
        List<String> association;
        try (EntityAgent setup = emf.createEntityAgent()) {
            AgentRecord source = new AgentRecord(217, "detached collection");
            source.tags.add("before");
            setup.insert(source);
        }
        try (EntityManager manager = emf.createEntityManager()) {
            AgentRecord source = manager.find(AgentRecord.class, 217L);
            association = source.tags;
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(source, "tags"));
            manager.detach(source);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            agent.<Connection>runWithConnection(connection -> {
                try (var sql = connection.prepareStatement("INSERT INTO EA_TAG (RECORD_ID, TAG_VALUE) VALUES (?, ?)")) {
                    sql.setLong(1, 217L);
                    sql.setString(2, "during");
                    assertEquals(1, sql.executeUpdate());
                }
            });
            assertSame(association, agent.fetch(association));
            assertEquals(java.util.Set.of("before", "during"), new java.util.HashSet<>(association));
            agent.getTransaction().rollback();
        }
        assertEquals(2, association.size());
    }

    @Test
    public void fetchDetachedManagerEntityAssociationUsesCurrentAgentTransaction() {
        try (EntityAgent setup = emf.createEntityAgent()) {
            setup.insert(new AgentTarget(218, "before"));
        }
        emf.getCache().evict(AgentTarget.class, 218L);
        AgentTarget association;
        try (EntityManager manager = emf.createEntityManager()) {
            association = manager.getReference(AgentTarget.class, 218L);
            assertTrue(association instanceof org.eclipse.persistence.queries.FetchGroupTracker);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(association, "label"));
            manager.detach(association);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            agent.update(new AgentTarget(218, "during"));
            assertSame(association, agent.fetch(association));
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(association, "label"));
            assertEquals("during", association.label);
            association.label = "caller change";
            assertSame(association, agent.fetch(association));
            assertEquals("An already fetched association is unchanged", "caller change", association.label);
            agent.getTransaction().rollback();
        }
        assertEquals("caller change", association.label);
        try (EntityAgent observer = emf.createEntityAgent()) {
            assertEquals("before", observer.get(AgentTarget.class, 218L).label);
        }
    }

    @Test
    public void fetchingMissingDetachedEntityAssociationPreservesUnloadedState() {
        AgentTarget association;
        try (EntityManager manager = emf.createEntityManager()) {
            association = manager.getReference(AgentTarget.class, 999218L);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(association, "label"));
            manager.detach(association);
        }
        var tracker = (org.eclipse.persistence.queries.FetchGroupTracker) association;
        var originalGroup = tracker._persistence_getFetchGroup();
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertThrows(EntityNotFoundException.class, () -> agent.fetch(association));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(association, "label"));
            assertSame(originalGroup, tracker._persistence_getFetchGroup());
        }
    }

    @Test
    public void managerLockOptionsRequireTransactionEvenForNone() {
        try (EntityAgent setup = emf.createEntityAgent()) {
            setup.insert(new AgentRecord(250, "managed lock"));
        }
        try (EntityManager manager = emf.createEntityManager()) {
            AgentRecord managed = manager.find(AgentRecord.class, 250L);
            assertThrows(TransactionRequiredException.class,
                    () -> manager.lock(managed, LockModeType.NONE, new jakarta.persistence.LockOption[0]));
        }
    }

    @Test
    public void managerPessimisticRoutesRejectAStaleManagedVersion() {
        int sequence = 260;
        for (LockModeType mode : List.of(LockModeType.PESSIMISTIC_READ, LockModeType.PESSIMISTIC_WRITE,
                LockModeType.PESSIMISTIC_FORCE_INCREMENT)) {
            for (int route = 0; route < 5; route++) {
                long id = sequence++;
                try (EntityAgent writer = emf.createEntityAgent(); EntityManager manager = emf.createEntityManager()) {
                    AgentRaceRecord record = new AgentRaceRecord(id, "before");
                    writer.insert(record);
                    AgentRaceRecord managed = manager.find(AgentRaceRecord.class, id);
                    record.name = "concurrent";
                    writer.update(record);
                    manager.getTransaction().begin();
                    int selectedRoute = route;
                    assertThrows("route " + route + " mode " + mode, OptimisticLockException.class,
                            () -> lockManaged(manager, managed, mode, selectedRoute));
                    assertTrue(manager.getTransaction().getRollbackOnly());
                    manager.getTransaction().rollback();
                }
            }
        }
    }

    private static void lockManaged(EntityManager manager, AgentRaceRecord managed, LockModeType mode, int route) {
        switch (route) {
            case 0 -> manager.find(AgentRaceRecord.class, managed.id, mode);
            case 1 -> manager.find(AgentRaceRecord.class, managed.id, mode, Map.of());
            case 2 -> manager.lock(managed, mode);
            case 3 -> manager.lock(managed, mode, Map.of());
            case 4 -> manager.lock(managed, mode, new jakarta.persistence.LockOption[0]);
            default -> throw new AssertionError(route);
        }
    }

    @Test
    public void managerExplicitRefreshWithLockReplacesItsStaleVersion() {
        try (EntityAgent writer = emf.createEntityAgent(); EntityManager manager = emf.createEntityManager()) {
            AgentRaceRecord record = new AgentRaceRecord(280, "before refresh");
            writer.insert(record);
            AgentRaceRecord managed = manager.find(AgentRaceRecord.class, record.id);
            record.name = "after refresh";
            writer.update(record);
            manager.getTransaction().begin();
            manager.refresh(managed, LockModeType.PESSIMISTIC_WRITE);
            assertEquals(record.name, managed.name);
            assertEquals(record.version, managed.version);
            manager.getTransaction().commit();
        }
    }

    @Test
    public void ordinaryEntityManagerRetainsIdentityAndDeferredDirtyWrites() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentRecord record = new AgentRecord(215, "managed");
            manager.persist(record);
            assertSame(record, manager.find(AgentRecord.class, 215L));
            record.name = "dirty";
            manager.getTransaction().commit();
            manager.clear();
            assertEquals("dirty", manager.find(AgentRecord.class, 215L).name);
            manager.getTransaction().begin();
            manager.remove(manager.find(AgentRecord.class, 215L));
            manager.getTransaction().commit();
        }
    }
}
