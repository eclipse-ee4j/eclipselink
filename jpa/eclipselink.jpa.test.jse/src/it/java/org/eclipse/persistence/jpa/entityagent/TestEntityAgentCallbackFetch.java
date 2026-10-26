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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;

import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.jpa.entityagent.model.AgentCallbackFetchOwner;
import org.eclipse.persistence.jpa.entityagent.model.AgentCallbackFetchTarget;
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

/** PostLoad fetches belong to materialization; later navigation is a separate operation. */
@RunWith(EmfRunner.class)
public class TestEntityAgentCallbackFetch {
    @Emf(name = "agent-callback-fetch", createTables = DDLGen.DROP_CREATE,
            classes = {AgentCallbackFetchOwner.class, AgentCallbackFetchTarget.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;
    private boolean supported;
    private boolean postgresql;

    @Before
    public void prepare() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            String product = agent.callWithConnection((Connection connection) -> connection.getMetaData().getDatabaseProductName());
            postgresql = "PostgreSQL".equals(product);
            supported = postgresql || "H2".equals(product);
            assumeTrue("Requires the owned H2 or PostgreSQL lock fixture", supported);
        }
        assertTrue(org.eclipse.persistence.internal.weaving.PersistenceWeaved.class.isAssignableFrom(AgentCallbackFetchOwner.class));
        cleanup();
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchTarget target = new AgentCallbackFetchTarget();
            target.id = 1;
            target.label = "target";
            agent.insert(target);
            AgentCallbackFetchOwner owner = new AgentCallbackFetchOwner();
            owner.id = 1;
            owner.ownerLabel = "owner";
            owner.tags.add("tag");
            owner.notes.add("note");
            owner.targets.add(target);
            owner.favorite = target;
            agent.insert(owner);
        }
    }

    @After
    public void cleanup() {
        if (!supported) return;
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    for (String table : new String[] {"EA_CB_FETCH_LINK", "EA_CB_FETCH_TAG", "EA_CB_FETCH_NOTE",
                            "EA_CB_FETCH_OWNER", "EA_CB_FETCH_TARGET"}) {
                        statement.executeUpdate("DELETE FROM " + table);
                    }
                }
            });
        }
    }

    private void assertCallback(AgentCallbackFetchOwner record) {
        assertEquals(1, record.callbackCount);
        assertTrue("Tags must be genuinely deferred when PostLoad starts", record.tagsDeferredBeforeCallback);
        assertTrue("Targets must be genuinely deferred when PostLoad starts", record.targetsDeferredBeforeCallback);
        assertTrue("The to-one target state must be genuinely deferred when PostLoad starts", record.favoriteDeferredBeforeCallback);
        assertEquals("target", record.callbackFavorite);
        assertSame(record.targets.get(0), record.getFavorite());
        assertEquals(2, record.callbackRows);
        assertTrue(emf.getPersistenceUnitUtil().isLoaded(record, "tags"));
        assertTrue(emf.getPersistenceUnitUtil().isLoaded(record, "targets"));
        assertTrue(record.notes instanceof IndirectContainer);
        assertFalse(((IndirectContainer) record.notes).isInstantiated());
    }

    private void contend(String sql, boolean locked) {
        try (EntityAgent contender = emf.createEntityAgent()) {
            contender.getTransaction().begin();
            try {
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
                    assertNotNull("A competing lock must fail in JDBC", cause);
                    SQLException database = (SQLException) cause;
                    assertTrue("Expected lock timeout", postgresql ? "55P03".equals(database.getSQLState())
                            : database.getErrorCode() == 50200);
                } else {
                    change.run();
                }
            } finally {
                if (contender.getTransaction().isActive()) contender.getTransaction().rollback();
            }
        }
    }

    private void assertCallbackRowsLocked(boolean locked) {
        contend("UPDATE EA_CB_FETCH_TAG SET TAG_VALUE='changed' WHERE OWNER_ID=1", locked);
        contend("DELETE FROM EA_CB_FETCH_LINK WHERE OWNER_ID=1", locked);
        contend("UPDATE EA_CB_FETCH_TARGET SET LABEL='changed' WHERE ID=1", locked);
    }

    @Test
    public void agentPostLoadFetchesAndLocksRowsOnlyWithinMaterialization() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var graph = agent.createEntityGraph(AgentCallbackFetchOwner.class);
            for (String attribute : new String[] {"tags", "targets", "notes", "favorite"}) {
                graph.addAttributeNode(attribute).addOption(FetchType.LAZY);
            }
            agent.getTransaction().begin();
            try {
                AgentCallbackFetchOwner record = agent.createQuery("select r from AgentCallbackFetchOwner r where r.id=1")
                        .withEntityGraph(graph).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .setLockScope(PessimisticLockScope.FETCHED).getSingleResult();
                assertCallback(record);
                assertCallbackRowsLocked(true);
                contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
                assertThrows(IllegalStateException.class, record.notes::size);
                agent.fetch(record.notes);
                assertEquals(1, record.notes.size());
                contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
            } finally {
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
        }
        assertCallbackRowsLocked(false);
    }

    @Test
    public void agentPostLoadCanFetchWithoutAReadLock() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchOwner record = agent.find(AgentCallbackFetchOwner.class, 1);
            assertCallback(record);
            assertFalse(agent.getTransaction().isActive());
            assertCallbackRowsLocked(false);
            assertThrows(IllegalStateException.class, record.notes::size);
        }
    }

    @Test
    public void failedPostLoadClearsTemporaryLoadersOnTheEscapedCopy() {
        AtomicReference<AgentCallbackFetchOwner> escaped = new AtomicReference<>();
        AgentCallbackFetchOwner.AFTER_LOAD.set(record -> {
            escaped.set(record);
            throw new IllegalStateException("owned callback failure");
        });
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            try {
                assertThrows(RuntimeException.class, () -> agent.createQuery(
                        "select r from AgentCallbackFetchOwner r where r.id=1", AgentCallbackFetchOwner.class)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE).setLockScope(PessimisticLockScope.FETCHED)
                        .getSingleResult());
                assertNotNull("The callback must reach its deliberate failure", escaped.get());
                assertThrows(IllegalStateException.class, escaped.get().notes::size);
            } finally {
                AgentCallbackFetchOwner.AFTER_LOAD.remove();
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
            agent.fetch(escaped.get().notes);
            assertEquals(1, escaped.get().notes.size());
            contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
        } finally {
            AgentCallbackFetchOwner.AFTER_LOAD.remove();
        }
        assertCallbackRowsLocked(false);
    }

    @Test
    public void ordinaryManagerPostLoadFetchesAndLocksRowsOnlyWithinMaterialization() {
        try (EntityManager manager = emf.createEntityManager()) {
            var graph = manager.createEntityGraph(AgentCallbackFetchOwner.class);
            for (String attribute : new String[] {"tags", "targets", "notes", "favorite"}) {
                graph.addAttributeNode(attribute).addOption(FetchType.LAZY);
            }
            manager.getTransaction().begin();
            try {
                AgentCallbackFetchOwner record = manager.createQuery("select r from AgentCallbackFetchOwner r where r.id=1")
                        .withEntityGraph(graph).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .setLockScope(PessimisticLockScope.FETCHED).getSingleResult();
                assertTrue(manager.contains(record));
                assertCallback(record);
                assertCallbackRowsLocked(true);
                contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
                assertEquals(1, record.notes.size());
                contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
            } finally {
                if (manager.getTransaction().isActive()) manager.getTransaction().rollback();
            }
        }
        assertCallbackRowsLocked(false);
    }

    @Test
    public void directRefreshRetainsCallbackAssociationAccess() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchOwner record = agent.find(AgentCallbackFetchOwner.class, 1);
            assertCallback(record);
            agent.refresh(record);
            assertEquals(2, record.callbackCount);
            assertEquals("target", record.callbackFavorite);
            assertEquals(2, record.callbackRows);
        }
    }

    @Test
    public void refreshMultipleRetainsCallbackAssociationAccess() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchOwner record = agent.find(AgentCallbackFetchOwner.class, 1);
            assertCallback(record);
            agent.refreshMultiple(java.util.List.of(record));
            assertEquals(2, record.callbackCount);
            assertEquals("target", record.callbackFavorite);
            assertEquals(2, record.callbackRows);
        }
    }

    @Test
    public void detachedReferenceFetchRetainsCallbackAssociationAccess() {
        emf.getCache().evict(AgentCallbackFetchOwner.class, 1);
        AgentCallbackFetchOwner reference;
        try (EntityManager manager = emf.createEntityManager()) {
            AgentCallbackFetchOwner.PARTIAL_PREPARATION.set(Boolean.TRUE);
            try { reference = manager.getReference(AgentCallbackFetchOwner.class, 1); }
            finally { AgentCallbackFetchOwner.PARTIAL_PREPARATION.remove(); }
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(reference, "ownerLabel"));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(reference));
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertSame(reference, agent.fetch(reference));
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(reference));
            assertEquals(1, reference.callbackCount);
            assertEquals("target", reference.callbackFavorite);
            assertEquals(2, reference.callbackRows);
        }
    }


    @Test
    public void refreshKeepsFetchedLocksThroughCallback() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchOwner record = agent.find(AgentCallbackFetchOwner.class, 1);
            agent.setProperty("jakarta.persistence.lock.scope", PessimisticLockScope.FETCHED);
            agent.getTransaction().begin();
            try {
                AgentCallbackFetchOwner.AFTER_LOAD.set(value -> assertCallbackRowsLocked(true));
                agent.refresh(record, LockModeType.PESSIMISTIC_WRITE);
                assertEquals(2, record.callbackCount);
                assertCallbackRowsLocked(true);
                assertThrows(IllegalStateException.class, record.notes::size);
                agent.fetch(record.notes);
                assertEquals(1, record.notes.size());
                contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
            } finally {
                AgentCallbackFetchOwner.AFTER_LOAD.remove();
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
        }
        assertCallbackRowsLocked(false);
    }

    @Test
    public void failedRefreshClearsEscapedResolversAndLockContext() {
        AgentCallbackFetchOwner escaped;
        try (EntityAgent agent = emf.createEntityAgent()) {
            escaped = agent.find(AgentCallbackFetchOwner.class, 1);
            agent.setProperty("jakarta.persistence.lock.scope", PessimisticLockScope.FETCHED);
            agent.getTransaction().begin();
            try {
                AgentCallbackFetchOwner.AFTER_LOAD.set(value -> { throw new IllegalStateException("owned refresh callback failure"); });
                assertThrows(RuntimeException.class, () -> agent.refresh(escaped, LockModeType.PESSIMISTIC_WRITE));
                assertThrows(IllegalStateException.class, escaped.notes::size);
            } finally {
                AgentCallbackFetchOwner.AFTER_LOAD.remove();
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
        }
        try (EntityAgent other = emf.createEntityAgent()) {
            other.fetch(escaped.notes);
            assertEquals(1, escaped.notes.size());
            contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
        }
        assertCallbackRowsLocked(false);
    }

    @Test
    public void ordinaryManagerRefreshPreservesCallbackAccess() {
        try (EntityManager manager = emf.createEntityManager()) {
            AgentCallbackFetchOwner record = manager.find(AgentCallbackFetchOwner.class, 1);
            assertEquals(1, record.callbackCount);
            java.util.concurrent.atomic.AtomicInteger callbacks = new java.util.concurrent.atomic.AtomicInteger();
            AgentCallbackFetchOwner.AFTER_LOAD.set(value -> callbacks.incrementAndGet());
            manager.getTransaction().begin();
            try {
                manager.refresh(record);
                assertEquals(1, callbacks.get());
                assertEquals(2, record.callbackRows);
                assertEquals("target", record.callbackFavorite);
            } finally {
                AgentCallbackFetchOwner.AFTER_LOAD.remove();
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void nestedReadRestoresRefreshFetchedScope() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackFetchOwner record = agent.find(AgentCallbackFetchOwner.class, 1);
            agent.setProperty("jakarta.persistence.lock.scope", PessimisticLockScope.FETCHED);
            agent.getTransaction().begin();
            try {
                AgentCallbackFetchOwner.AFTER_LOAD.set(value -> {
                    assertEquals("target", agent.find(AgentCallbackFetchTarget.class, 1).getLabel());
                    assertEquals(1, value.notes.size());
                    contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", true);
                });
                agent.refresh(record, LockModeType.PESSIMISTIC_WRITE);
                assertEquals(1, record.notes.size());
            } finally {
                AgentCallbackFetchOwner.AFTER_LOAD.remove();
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
        }
        contend("UPDATE EA_CB_FETCH_NOTE SET NOTE_VALUE='changed' WHERE OWNER_ID=1", false);
    }
}
