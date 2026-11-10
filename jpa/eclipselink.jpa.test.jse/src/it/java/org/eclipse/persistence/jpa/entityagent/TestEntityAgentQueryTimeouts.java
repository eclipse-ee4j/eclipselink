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

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TypedQuery;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentSpecialRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/** Actual cancellation and competing row locks in the owned H2/PostgreSQL fixtures. */
@RunWith(EmfRunner.class)
public class TestEntityAgentQueryTimeouts {
    @Emf(name = "agent-query-timeouts", createTables = DDLGen.DROP_CREATE,
            classes = {AgentRaceRecord.class, AgentRecord.class, AgentSpecialRecord.class, AgentTarget.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;
    private boolean supported;
    private boolean postgresql;

    @Before
    public void prepareRows() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            String product = agent.callWithConnection((Connection connection) -> connection.getMetaData().getDatabaseProductName());
            postgresql = "PostgreSQL".equals(product);
            supported = postgresql || "H2".equals(product);
            assumeTrue("Requires the owned H2 or PostgreSQL cancellation/lock fixture", supported);
        }
        clearRows();
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.insert(new AgentRaceRecord(601, "locked"));
            AgentTarget target = new AgentTarget(603, "target");
            agent.insert(target);
            AgentRecord record = new AgentRecord(603, "scope");
            record.tags.add("tag");
            record.targets.add(target);
            agent.insert(record);
        }
    }

    @After
    public void clearRows() {
        if (!supported) return;
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    for (String table : new String[] {"EA_LINK", "EA_TAG", "EA_SPECIAL", "EA_DETAILS", "EA_RECORD", "EA_TARGET", "EA_RACE_RECORD"}) {
                        statement.executeUpdate("DELETE FROM " + table);
                    }
                }
            });
        }
    }

    private TypedQuery<?> delayedQuery(EntityAgent agent) {
        return (postgresql
                ? agent.createNativeQuery("select 1 from pg_sleep(5)", Integer.class)
                : agent.createNativeQuery("select sum(sin(X)) from system_range(1, 100000000)", Double.class))
                .setTimeout(1000);
    }

    private void assertConnectionUsable(EntityAgent agent) {
        assertEquals(Integer.valueOf(1), agent.createNativeQuery("select 1", Integer.class).getSingleResult());
    }

    @Test
    public void queryTimeoutCancelsOnlyTheStatementOutsideATransaction() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            long started = System.nanoTime();
            assertThrows(QueryTimeoutException.class, () -> delayedQuery(agent).getSingleResult());
            assertTrue("The configured timeout must cancel the request", System.nanoTime() - started < 10_000_000_000L);
            assertFalse(agent.getTransaction().isActive());
            assertConnectionUsable(agent);
            agent.insert(new AgentRaceRecord(602, "after timeout"));
            assertEquals("after timeout", agent.get(AgentRaceRecord.class, 602L).name);
        }
    }

    @Test
    public void queryTimeoutExceptionMatchesActualTransactionOutcome() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            agent.insert(new AgentRaceRecord(602, "pending"));
            PersistenceException failure = assertThrows(PersistenceException.class, () -> delayedQuery(agent).getSingleResult());
            if (failure instanceof QueryTimeoutException) {
                assertFalse(agent.getTransaction().getRollbackOnly());
                assertConnectionUsable(agent);
                assertEquals("pending", agent.get(AgentRaceRecord.class, 602L).name);
                agent.getTransaction().commit();
                assertEquals("pending", peer.get(AgentRaceRecord.class, 602L).name);
            } else {
                assertTrue(agent.getTransaction().getRollbackOnly());
                assertThrows(RollbackException.class, agent.getTransaction()::commit);
                assertNull(peer.find(AgentRaceRecord.class, 602L));
                assertConnectionUsable(agent);
            }
        }
    }

    @Test
    public void pessimisticNoWaitReportsWhetherOnlyStatementOrTransactionFailed() {
        try (EntityAgent owner = emf.createEntityAgent(); EntityAgent contender = emf.createEntityAgent()) {
            owner.getTransaction().begin();
            owner.find(AgentRaceRecord.class, 601L, LockModeType.PESSIMISTIC_WRITE);
            contender.getTransaction().begin();
            TypedQuery<AgentRaceRecord> query = contender.createQuery(
                    "select r from AgentRaceRecord r where r.id = 601", AgentRaceRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setHint("jakarta.persistence.lock.timeout", 0).setTimeout(2000);
            PersistenceException failure = assertThrows(PersistenceException.class, query::getSingleResult);
            if (failure instanceof LockTimeoutException) {
                assertFalse(contender.getTransaction().getRollbackOnly());
                assertConnectionUsable(contender);
            } else {
                assertTrue("A transaction-level lock failure needs PessimisticLockException", failure instanceof PessimisticLockException);
                assertTrue(contender.getTransaction().getRollbackOnly());
            }
            contender.getTransaction().rollback();
            assertConnectionUsable(contender);
            assertEquals("locked", owner.get(AgentRaceRecord.class, 601L).name);
            owner.getTransaction().commit();
            contender.getTransaction().begin();
            assertEquals("locked", query.getSingleResult().name);
            contender.getTransaction().commit();
        }
    }

    private void contend(String sql, boolean locked) {
        try (EntityAgent contender = emf.createEntityAgent()) {
            contender.getTransaction().begin();
            contender.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    statement.execute(postgresql ? "SET LOCAL lock_timeout='200ms'" : "SET LOCK_TIMEOUT 200");
                }
            });
            Runnable change = () -> contender.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    assertEquals(1, statement.executeUpdate(sql));
                }
            });
            if (locked) {
                PersistenceException failure = assertThrows(PersistenceException.class, change::run);
                Throwable cause = failure;
                while (cause != null && !(cause instanceof SQLException)) cause = cause.getCause();
                assertNotNull("A competing row lock must fail in JDBC", cause);
                SQLException database = (SQLException) cause;
                assertTrue("Expected lock timeout", postgresql ? "55P03".equals(database.getSQLState()) : database.getErrorCode() == 50200);
            } else {
                change.run();
            }
            contender.getTransaction().rollback();
        }
    }

    @Test
    public void normalScopeDoesNotLockCollectionJoinOrTargetRows() {
        try (EntityAgent owner = emf.createEntityAgent()) {
            owner.getTransaction().begin();
            owner.createQuery("select r from AgentRecord r where r.id = 603", AgentRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.NORMAL).getSingleResult();
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", false);
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
            owner.getTransaction().rollback();
        }
    }

    @Test
    public void extendedScopeLocksOwnedCollectionAndJoinRowsButNotTargets() {
        try (EntityAgent owner = emf.createEntityAgent()) {
            owner.getTransaction().begin();
            owner.createQuery("select r from AgentRecord r where r.id = 603", AgentRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.EXTENDED).getSingleResult();
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", true);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", true);
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
            owner.getTransaction().rollback();
        }
    }

    @Test
    public void fetchedScopeLocksFetchedTargetsAndLinksButNotUnfetchedCollections() {
        AgentRecord detached;
        try (EntityAgent owner = emf.createEntityAgent()) {
            var graph = owner.createEntityGraph(AgentRecord.class);
            graph.addAttributeNode("targets").addOption(FetchType.EAGER);
            graph.addAttributeNode("tags").addOption(FetchType.LAZY);
            owner.getTransaction().begin();
            detached = owner.createQuery("select r from AgentRecord r where r.id = 603")
                    .withEntityGraph(graph).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setLockScope(PessimisticLockScope.FETCHED).getSingleResult();
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(detached, "targets"));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(detached, "tags"));
            assertEquals("target", detached.targets.get(0).label);
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", true);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", true);
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
            owner.fetch(detached.tags);
            assertEquals(1, detached.tags.size());
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
            owner.getTransaction().rollback();
        }
        assertEquals("target", detached.targets.get(0).label);
        contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
        contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", false);
    }

    @Test
    public void ordinaryManagerNormalScopePreservesManagedIdentityAndTargetAvailability() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentRecord record = manager.createQuery("select r from AgentRecord r where r.id = 603", AgentRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.NORMAL).getSingleResult();
            assertTrue(manager.contains(record));
            assertSame(record, manager.find(AgentRecord.class, 603L));
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", false);
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
            manager.getTransaction().rollback();
        }
    }

    @Test
    public void ordinaryManagerExtendedScopeLocksOwnedRowsAndLeavesTargetUnlocked() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentRecord record = manager.createQuery("select r from AgentRecord r where r.id = 603", AgentRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.EXTENDED).getSingleResult();
            assertTrue(manager.contains(record));
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", true);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", true);
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
            manager.getTransaction().rollback();
        }
    }

    @Test
    public void ordinaryManagerFetchedScopeLocksOnlyItsFetchedTargetGraph() {
        try (EntityManager manager = emf.createEntityManager()) {
            var graph = manager.createEntityGraph(AgentRecord.class);
            graph.addAttributeNode("targets").addOption(FetchType.EAGER);
            graph.addAttributeNode("tags").addOption(FetchType.LAZY);
            manager.getTransaction().begin();
            AgentRecord record = manager.createQuery("select r from AgentRecord r where r.id = 603")
                    .withEntityGraph(graph).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setLockScope(PessimisticLockScope.FETCHED).getSingleResult();
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(record, "targets"));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(record, "tags"));
            assertTrue(manager.contains(record));
            assertTrue(manager.contains(record.targets.get(0)));
            contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", true);
            contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", true);
            contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
            manager.getTransaction().rollback();
        }
    }

    @Test
    public void fetchedScopeIncludesPreviouslyLoadedManagedCollectionDuringLockRefresh() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentRecord record = manager.find(AgentRecord.class, 603L);
                assertEquals(1, record.tags.size());
                AgentRecord locked = manager.createQuery("select r from AgentRecord r where r.id = 603", AgentRecord.class)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.FETCHED).getSingleResult();
                assertSame(record, locked);
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(locked, "tags"));
                contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", true);
            } finally {
                if (manager.getTransaction().isActive()) manager.getTransaction().rollback();
            }
        }
        contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
    }
    @Test
    public void ordinaryManagerLockOptionUpgradesAnExistingNormalLockToExtended() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentRecord record = manager.find(AgentRecord.class, 603L);
                manager.lock(record, LockModeType.PESSIMISTIC_WRITE, PessimisticLockScope.NORMAL);
                assertTrue(manager.contains(record));
                assertSame(record, manager.find(AgentRecord.class, 603L));
                contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
                contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", false);
                contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);

                manager.lock(record, LockModeType.PESSIMISTIC_WRITE, PessimisticLockScope.EXTENDED);
                assertTrue(manager.getTransaction().isActive());
                assertFalse(manager.getTransaction().getRollbackOnly());
                assertTrue(manager.contains(record));
                assertSame(record, manager.find(AgentRecord.class, 603L));
                contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", true);
                contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", true);
                contend("UPDATE EA_TARGET SET LABEL='changed' WHERE ID=603", false);
            } finally {
                if (manager.getTransaction().isActive()) manager.getTransaction().rollback();
            }
        }
        contend("UPDATE EA_TAG SET TAG_VALUE='changed' WHERE RECORD_ID=603", false);
        contend("DELETE FROM EA_LINK WHERE RECORD_ID=603", false);
    }
}
