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
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TypedQuery;

import java.sql.Connection;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.jpa.entityagent.model.AgentCacheRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
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

/** Cursors detach rows in initial buffers and later pages before returning them. */
@RunWith(EmfRunner.class)
public class TestEntityAgentCursors {
    @Emf(name = "agent-cursor-materialization", createTables = DDLGen.DROP_CREATE,
            classes = {AgentCallbackFetchOwner.class, AgentCallbackFetchTarget.class, AgentCacheRecord.class, AgentRaceRecord.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    public static final class Projection {
        public final AgentCallbackFetchOwner owner;

        public Projection(AgentCallbackFetchOwner owner) {
            this.owner = owner;
        }
    }

    @Before
    public void prepare() {
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
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    for (String table : new String[] {"EA_RACE_RECORD", "EA_CACHE_RECORD", "EA_CB_FETCH_LINK", "EA_CB_FETCH_TAG", "EA_CB_FETCH_NOTE",
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

    @Test
    public void agentCursorUsesDetachedMaterialization() {
        EntityAgent agent = emf.createEntityAgent();
        try {
            org.eclipse.persistence.queries.Cursor cursor = agent.createQuery("select r from AgentCallbackFetchOwner r", AgentCallbackFetchOwner.class)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            try {
                AgentCallbackFetchOwner record = (AgentCallbackFetchOwner) cursor.next();
                var holder = ((IndirectContainer) record.notes).getValueHolder();
                boolean retainsSession = holder instanceof org.eclipse.persistence.internal.indirection.DatabaseValueHolder value && value.getSession() != null;
                assertCallback(record);
                assertFalse(retainsSession);
                agent.fetch(record.notes);
                assertEquals(1, record.notes.size());
                agent.close();
                assertTrue(cursor.isClosed());
                assertEquals(1, record.tags.size());
            } finally { cursor.close(); }
        } finally { if (agent.isOpen()) agent.close(); }
    }

    @Test
    public void ordinaryManagerCursorPreservesManagedMaterialization() {
        try (EntityManager manager = emf.createEntityManager()) {
            org.eclipse.persistence.queries.Cursor cursor = manager.createQuery("select r from AgentCallbackFetchOwner r", AgentCallbackFetchOwner.class)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            try {
                AgentCallbackFetchOwner record = (AgentCallbackFetchOwner) cursor.next();
                assertTrue(manager.contains(record));
                assertCallback(record);
                assertEquals(1, record.notes.size());
            } finally { cursor.close(); }
        }
    }

    private void detachedNotes(AgentCallbackFetchOwner row) {
        var container = (IndirectContainer) row.notes;
        assertFalse(container.isInstantiated());
        var holder = container.getValueHolder();
        assertTrue(holder instanceof org.eclipse.persistence.internal.indirection.DatabaseValueHolder);
        assertNull(((org.eclipse.persistence.internal.indirection.DatabaseValueHolder) holder).getSession());
    }

    private void released(org.eclipse.persistence.queries.Cursor cursor) {
        assertTrue(cursor.isClosed());
        assertNull(cursor.getQuery().getProperty("eclipselink.cursor.row-materializer"));
        assertNull(cursor.getPolicy().getQuery().getProperty("eclipselink.cursor.row-materializer"));
        assertNull(cursor.getQuery().getProperty("eclipselink.cursor.resource-release"));
        assertNull(cursor.getQuery().getProperty("eclipselink.result-mapping.execution-context"));
        assertNull(cursor.getQuery().getProperty("eclipselink.report.constructor-argument-preparer"));
        assertNull(cursor.getPolicy().getQuery().getProperty("eclipselink.result-mapping.execution-context"));
        assertNull(cursor.getPolicy().getQuery().getProperty("eclipselink.report.constructor-argument-preparer"));
    }

    private org.eclipse.persistence.queries.Cursor cursor(EntityAgent agent, String jpql, int initial) {
        return agent.createQuery(jpql, AgentCallbackFetchOwner.class)
                .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, initial)
                .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1)
                .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
    }

    @Test
    public void initialBufferAndLaterPagesAreDetachedOnceAndFetchableByAnotherAgent() {
        EntityAgent first = emf.createEntityAgent();
        org.eclipse.persistence.queries.Cursor cursor = null;
        try {
            for (int id = 2; id <= 3; id++) {
                AgentCallbackFetchOwner owner = new AgentCallbackFetchOwner();
                owner.id = id; owner.ownerLabel = "owner " + id;
                owner.tags.add("tag"); owner.notes.add("note");
                AgentCallbackFetchTarget target = first.get(AgentCallbackFetchTarget.class, 1);
                owner.favorite = target; owner.targets.add(target); first.insert(owner);
            }
            cursor = cursor(first, "select r from AgentCallbackFetchOwner r order by r.id", 1);
            assertEquals(1, cursor.getObjectCollection().size());
            java.util.List<AgentCallbackFetchOwner> rows = new java.util.ArrayList<>();
            while (cursor.hasNext()) {
                AgentCallbackFetchOwner row = (AgentCallbackFetchOwner) cursor.next();
                assertCallback(row); detachedNotes(row); rows.add(row);
            }
            assertEquals(3, rows.size()); released(cursor);
            first.close();
            try (EntityAgent second = emf.createEntityAgent()) {
                for (AgentCallbackFetchOwner row : rows) {
                    assertThrows(IllegalStateException.class, () -> row.notes.size());
                    second.fetch(row.notes);
                    assertEquals(java.util.List.of("note"), row.notes);
                    assertEquals(1, row.callbackCount);
                }
            }
        } finally { if (cursor != null) cursor.close(); if (first.isOpen()) first.close(); }
    }

    @Test
    public void joinedFinalRowAndInitiallyExhaustedCursorReleaseAllOwnerHooks() {
        for (boolean join : new boolean[] {false, true}) {
            for (int initial : new int[] {0, 10}) {
                EntityAgent agent = emf.createEntityAgent();
                org.eclipse.persistence.queries.Cursor cursor = cursor(agent,
                        "select r from AgentCallbackFetchOwner r" + (join ? " join fetch r.targets" : ""), initial);
                try {
                    AgentCallbackFetchOwner row = (AgentCallbackFetchOwner) cursor.next();
                    assertEquals(1, row.callbackCount);
                    assertEquals("target", row.callbackFavorite);
                    assertEquals(2, row.callbackRows);
                    assertEquals(join, !row.targetsDeferredBeforeCallback);
                    detachedNotes(row);
                    assertFalse(cursor.hasNext()); released(cursor);
                    agent.close();
                    assertEquals(1, row.tags.size());
                    assertEquals(1, row.targets.size());
                    assertThrows(IllegalStateException.class, () -> row.notes.size());
                    cursor.close(); released(cursor);
                } finally { cursor.close(); if (agent.isOpen()) agent.close(); }
            }
        }
    }

    @Test
    public void namedCursorTemplateAndReusableQueryNeverRetainAgentMaterializer() {
        String name = "agent-cursor-owned-template";
        try (EntityAgent original = emf.createEntityAgent()) {
            var query = original.createQuery("select r from AgentCallbackFetchOwner r", AgentCallbackFetchOwner.class)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1);
            var cursor = query.unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            assertCallback((AgentCallbackFetchOwner) cursor.next()); cursor.close(); released(cursor);
            assertNull(query.unwrap(org.eclipse.persistence.queries.DatabaseQuery.class).getProperty("eclipselink.cursor.row-materializer"));
            emf.addNamedQuery(name, query);
        }
        var template = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession().getQuery(name);
        assertNull(template.getProperty("eclipselink.cursor.row-materializer"));
        try (EntityAgent later = emf.createEntityAgent()) {
            var cursor = later.createNamedQuery(name, AgentCallbackFetchOwner.class)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            try { assertCallback((AgentCallbackFetchOwner) cursor.next()); }
            finally { cursor.close(); }
            released(cursor);
        }
        assertNull(template.getProperty("eclipselink.cursor.row-materializer"));
    }

    @Test
    public void ordinaryManagerJoinedCursorKeepsManagedIdentityAndCloses() {
        try (EntityManager manager = emf.createEntityManager()) {
            var cursor = manager.createQuery("select r from AgentCallbackFetchOwner r join fetch r.targets", AgentCallbackFetchOwner.class)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            try {
                AgentCallbackFetchOwner row = (AgentCallbackFetchOwner) cursor.next();
                assertTrue(manager.contains(row)); assertEquals(1, row.callbackCount);
                assertEquals("target", row.callbackFavorite); assertEquals(2, row.callbackRows);
                assertEquals(1, row.notes.size()); assertFalse(cursor.hasNext());
            } finally { cursor.close(); }
            released(cursor);
        }
    }

    @Test
    public void callbackFailureStillAllowsOwnedCursorDisposal() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var cursor = cursor(agent, "select r from AgentCallbackFetchOwner r", 0);
            AgentCallbackFetchOwner.AFTER_LOAD.set(row -> { throw new IllegalStateException("owned cursor callback failure"); });
            try { assertThrows(IllegalStateException.class, cursor::next); }
            finally { AgentCallbackFetchOwner.AFTER_LOAD.remove(); cursor.close(); }
            released(cursor);
            assertEquals("target", agent.get(AgentCallbackFetchTarget.class, 1).label);
        }
    }

    @Test
    public void scalarAndConstructorCursorDisposalClearsEveryOwnerProperty() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            for (int initial : new int[] {0, 10}) {
                var scalar = agent.createQuery("select r.ownerLabel from AgentCallbackFetchOwner r", String.class)
                        .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                        .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, initial)
                        .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
                try { assertEquals("owner", scalar.next()); assertFalse(scalar.hasNext()); }
                finally { scalar.close(); }
                released(scalar);
                var constructor = agent.createQuery("select new org.eclipse.persistence.jpa.entityagent.TestEntityAgentCursors$Projection(r) from AgentCallbackFetchOwner r", Projection.class)
                        .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                        .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, initial)
                        .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
                try {
                    var value = (Projection) constructor.next();
                    assertCallback(value.owner); detachedNotes(value.owner); assertFalse(constructor.hasNext());
                } finally { constructor.close(); }
                released(constructor);
            }
        }
    }

    private AgentCacheRecord sharedCacheRecord(long id) {
        return (AgentCacheRecord) emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class)
                .getServerSession().getIdentityMapAccessor().getFromIdentityMap(id, AgentCacheRecord.class);
    }

    @Test
    public void cursorRefreshModeAppliesToBothInitialAndLaterRows() {
        emf.runInTransaction(manager -> {
            manager.persist(new AgentCacheRecord(1001L, "stored"));
            manager.persist(new AgentCacheRecord(1002L, "stored"));
        });
        try (EntityManager manager = emf.createEntityManager()) {
            assertEquals("loaded:stored", manager.find(AgentCacheRecord.class, 1001L).label);
            assertEquals("loaded:stored", manager.find(AgentCacheRecord.class, 1002L).label);
        }
        assertEquals("stored", sharedCacheRecord(1001L).label);
        assertEquals("stored", sharedCacheRecord(1002L).label);
        try (EntityAgent agent = emf.createEntityAgent(jakarta.persistence.CacheStoreMode.BYPASS)) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    assertEquals(2, statement.executeUpdate("UPDATE EA_CACHE_RECORD SET LABEL='fresh'"));
                }
            });
            var cursor = agent.createQuery("select r from AgentCacheRecord r order by r.id", AgentCacheRecord.class)
                    .setCacheStoreMode(jakarta.persistence.CacheStoreMode.REFRESH)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 1)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
            try {
                assertEquals(1, cursor.getObjectCollection().size());
                assertEquals("fresh", sharedCacheRecord(1001L).label);
                assertEquals("stored", sharedCacheRecord(1002L).label);
                for (long id : new long[] {1001L, 1002L}) {
                    AgentCacheRecord row = (AgentCacheRecord) cursor.next();
                    assertEquals(Long.valueOf(id), row.id);
                    assertEquals("loaded:fresh", row.label); assertEquals(1, row.loads);
                    assertEquals("fresh", sharedCacheRecord(id).label);
                    assertEquals(0, sharedCacheRecord(id).loads);
                    row.label = "caller change";
                    assertEquals("fresh", sharedCacheRecord(id).label);
                }
                assertFalse(cursor.hasNext());
            } finally { cursor.close(); }
            released(cursor);
        }
    }
    @Test
    public void agentInitialBufferErrorReleasesJdbcResources() throws Exception { initialCursorFailure(true, true); }
    @Test
    public void agentInitialBufferRuntimeFailureReleasesJdbcResources() throws Exception { initialCursorFailure(true, false); }
    @Test
    public void managerInitialBufferErrorReleasesJdbcResources() throws Exception { initialCursorFailure(false, true); }
    @Test
    public void managerInitialBufferRuntimeFailureReleasesJdbcResources() throws Exception { initialCursorFailure(false, false); }

    private void initialCursorFailure(boolean agentMode, boolean errorMode) throws Exception {
        try (EntityAgent seed = emf.createEntityAgent()) {
            AgentCallbackFetchOwner row = new AgentCallbackFetchOwner();
            row.id = 2; row.ownerLabel = "second"; row.tags.add("tag"); row.notes.add("note");
            row.favorite = seed.get(AgentCallbackFetchTarget.class, 1); row.targets.add(row.favorite); seed.insert(row);
        }
        emf.getCache().evictAll();
        var server = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
        var pool = server.getReadConnectionPool();
        int before = pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size();
        java.util.List<java.sql.Statement> statements = new java.util.ArrayList<>();
        java.util.List<java.sql.ResultSet> results = new java.util.ArrayList<>();
        var listener = new org.eclipse.persistence.sessions.SessionEventAdapter() {
            @Override public void postExecuteCall(org.eclipse.persistence.sessions.SessionEvent event) {
                if (event.getCall() instanceof org.eclipse.persistence.internal.databaseaccess.DatabaseCall call && call.isCursorReturned()) {
                    statements.add(call.getStatement()); results.add(call.getResult());
                }
            }
        };
        server.getEventManager().addListener(listener);
        Throwable expected = errorMode ? new AssertionError("owned initial cursor error") : new IllegalStateException("owned initial cursor runtime");
        Throwable caught = null; Throwable closeFailure = null;
        EntityAgent agent = agentMode ? emf.createEntityAgent() : null;
        EntityManager manager = agentMode ? null : emf.createEntityManager();
        AgentCallbackFetchOwner.AFTER_LOAD.set(row -> { if (expected instanceof Error e) throw e; throw (RuntimeException) expected; });
        try {
            var query = agentMode ? agent.createQuery("select r from AgentCallbackFetchOwner r order by r.id", AgentCallbackFetchOwner.class)
                    : manager.createQuery("select r from AgentCallbackFetchOwner r order by r.id", AgentCallbackFetchOwner.class);
            query.setHint(org.eclipse.persistence.config.QueryHints.CURSOR, true)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_INITIAL_SIZE, 1)
                    .setHint(org.eclipse.persistence.config.QueryHints.CURSOR_PAGE_SIZE, 1)
                    .unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getResultCursor();
        } catch (Throwable failure) { caught = failure; }
        finally {
            AgentCallbackFetchOwner.AFTER_LOAD.remove();
            try { if (agentMode) agent.close(); else manager.close(); } catch (Throwable failure) { closeFailure = failure; }
            server.getEventManager().removeListener(listener);
        }
        boolean statementsClosed = !statements.isEmpty();
        for (var statement : statements) statementsClosed &= statement != null && statement.isClosed();
        boolean resultsClosed = !results.isEmpty();
        for (var result : results) resultsClosed &= result != null && result.isClosed();
        int after = pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size();
        boolean peer = false;
        try (EntityAgent other = emf.createEntityAgent()) { peer = "target".equals(other.get(AgentCallbackFetchTarget.class, 1).label); }
        assertSame(expected, caught); assertNull(closeFailure); assertEquals(1, statements.size()); assertEquals(1, results.size());
        assertTrue("Initial failure must close actual JDBC statement", statementsClosed);
        assertTrue("Initial failure must close actual JDBC result", resultsClosed);
        assertEquals(before, after); assertTrue(peer);
    }

    public static final class VersionedProjection {
        public final AgentRaceRecord record;
        public VersionedProjection(AgentRaceRecord record) { this.record = record; }
    }

    private <T> TypedQuery<T> cursorOptions(TypedQuery<T> query) {
        return query.setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 0).setHint(QueryHints.CURSOR_PAGE_SIZE, 1);
    }
    @Test public void agentProjectionRetainsNoneAfterQueryChangesToPessimistic() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var query = cursorOptions(agent.createQuery("select new org.eclipse.persistence.jpa.entityagent.TestEntityAgentCursors$Projection(r) from AgentCallbackFetchOwner r", TestEntityAgentCursors.Projection.class));
            Cursor cursor = query.unwrap(JpaQuery.class).getResultCursor();
            try {
                assertEquals(0, cursor.getObjectCollection().size()); assertFalse(agent.getTransaction().isActive());
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                var row = (TestEntityAgentCursors.Projection) cursor.next();
                assertEquals(1, row.owner.callbackCount); assertEquals("target", row.owner.callbackFavorite);
                assertFalse(agent.getTransaction().isActive());
            } finally { cursor.close(); }
        }
    }
    private void versionedObligation(boolean mutate) {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
            agent.insert(new AgentRaceRecord(1, "before"));
            agent.getTransaction().begin();
            Cursor cursor = null;
            try {
                var query = cursorOptions(agent.createQuery("select new org.eclipse.persistence.jpa.entityagent.TestEntityAgentCursors$VersionedProjection(r) from AgentRaceRecord r", VersionedProjection.class)).setLockMode(LockModeType.OPTIMISTIC);
                cursor = query.unwrap(JpaQuery.class).getResultCursor();
                assertEquals(0, cursor.getObjectCollection().size());
                if (mutate) query.setLockMode(LockModeType.NONE);
                var projection = (VersionedProjection) cursor.next();
                assertEquals("before", projection.record.getName());
                cursor.close();
                AgentRaceRecord changed = peer.get(AgentRaceRecord.class, 1L); changed.name = "competing"; peer.update(changed);
                assertTrue(changed.getVersion() > projection.record.getVersion());
                RuntimeException failure = assertThrows(RuntimeException.class, () -> agent.getTransaction().commit());
                assertTrue("Original optimistic obligation must reject commit: " + failure.getClass().getName(), failure instanceof OptimisticLockException || failure instanceof RollbackException);
                assertEquals("competing", peer.get(AgentRaceRecord.class, 1L).getName());
            } finally {
                if (cursor != null) cursor.close();
                if (agent.getTransaction().isActive()) agent.getTransaction().rollback();
            }
        }
    }
    @Test public void agentProjectionRetainsOriginalOptimisticAfterQueryChangesToNone() { versionedObligation(true); }
    @Test public void unchangedOptimisticProjectionRejectsCompetingVersion() { versionedObligation(false); }
    @Test public void ordinaryManagerProjectionRetainsOriginalNoneAndManagedIdentity() {
        try (EntityManager manager = emf.createEntityManager()) {
            var query = cursorOptions(manager.createQuery("select new org.eclipse.persistence.jpa.entityagent.TestEntityAgentCursors$Projection(r) from AgentCallbackFetchOwner r", TestEntityAgentCursors.Projection.class));
            Cursor cursor = query.unwrap(JpaQuery.class).getResultCursor();
            try {
                assertEquals(0, cursor.getObjectCollection().size()); query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                var row = (TestEntityAgentCursors.Projection) cursor.next();
                assertTrue(manager.contains(row.owner)); assertEquals(1, row.owner.callbackCount); assertEquals("target", row.owner.callbackFavorite);
                assertFalse(manager.getTransaction().isActive());
            } finally { cursor.close(); }
        }
    }

}
