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
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventManager;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(EmfRunner.class)
public class TestQueryPagination {
    private static final String QUERY = "select r from AgentQueryRecord r where r.id >= :minimum order by r.id";
    private static final String NAMED_QUERY = "EAQuery.pagination";
    private static final String PRESET_QUERY = "EAQuery.presetPagination";

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> {
            AgentQueryGroup group = new AgentQueryGroup(1, "Pagination");
            em.persist(group);
            em.persist(new AgentQueryRecord(1, "Alpha", group));
            em.persist(new AgentQueryRecord(2, "Beta", group));
            em.persist(new AgentQueryRecord(3, "Gamma", group));
        });
        try (EntityManager em = emf.createEntityManager()) {
            emf.addNamedQuery(NAMED_QUERY, em.createQuery(QUERY, AgentQueryRecord.class));
            emf.addNamedQuery(PRESET_QUERY, em.createQuery(QUERY, AgentQueryRecord.class)
                    .setFirstResult(1).setMaxResults(1));
        }
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("delete from AgentQueryRecord").executeUpdate();
            em.createQuery("delete from AgentQueryGroup").executeUpdate();
        });
        emf.getCache().evictAll();
    }

    @Test
    public void entityManagerDirectQueryUsesCurrentPage() {
        try (EntityManager em = emf.createEntityManager()) {
            assertFinitePages(() -> em.createQuery(QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityManagerNamedQueryUsesCurrentPage() {
        try (EntityManager em = emf.createEntityManager()) {
            assertFinitePages(() -> em.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityAgentDirectQueryUsesCurrentPage() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertFinitePages(() -> agent.createQuery(QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityAgentNamedQueryUsesCurrentPage() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertFinitePages(() -> agent.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityManagerDirectQueryChangesPaginationShape() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPaginationShapes(() -> em.createQuery(QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityManagerNamedQueryChangesPaginationShape() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPaginationShapes(() -> em.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityAgentDirectQueryChangesPaginationShape() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertPaginationShapes(() -> agent.createQuery(QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityAgentNamedQueryChangesPaginationShape() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertPaginationShapes(() -> agent.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class));
        }
    }

    @Test
    public void entityManagerPresetReferenceUsesCurrentPage() {
        TypedQueryReference<AgentQueryRecord> reference = emf.getNamedQueries(AgentQueryRecord.class).get(PRESET_QUERY);
        try (EntityManager em = emf.createEntityManager()) {
            assertPresetReference(() -> em.createQuery(reference));
        }
    }

    @Test
    public void entityAgentPresetReferenceUsesCurrentPage() {
        TypedQueryReference<AgentQueryRecord> reference = emf.getNamedQueries(AgentQueryRecord.class).get(PRESET_QUERY);
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertPresetReference(() -> agent.createQuery(reference));
        }
    }

    @Test
    public void entityManagerDirectQueryResetsMaximum() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMaximumReset(() -> em.createQuery(QUERY, AgentQueryRecord.class), false, false);
        }
    }

    @Test
    public void entityAgentDirectQueryResetsMaximum() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMaximumReset(() -> agent.createQuery(QUERY, AgentQueryRecord.class), false, false);
        }
    }

    @Test
    public void entityManagerNamedQueryResetsMaximum() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMaximumReset(() -> em.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class), false, false);
        }
    }

    @Test
    public void entityAgentNamedQueryResetsMaximum() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMaximumReset(() -> agent.createNamedQuery(NAMED_QUERY, AgentQueryRecord.class), false, false);
        }
    }

    @Test
    public void entityManagerPresetReferenceResetsMaximum() {
        TypedQueryReference<AgentQueryRecord> reference = emf.getNamedQueries(AgentQueryRecord.class).get(PRESET_QUERY);
        try (EntityManager em = emf.createEntityManager()) {
            assertMaximumReset(() -> em.createQuery(reference), false, true);
        }
    }

    @Test
    public void entityAgentPresetReferenceResetsMaximum() {
        TypedQueryReference<AgentQueryRecord> reference = emf.getNamedQueries(AgentQueryRecord.class).get(PRESET_QUERY);
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMaximumReset(() -> agent.createQuery(reference), false, true);
        }
    }

    @Test
    public void entityManagerNativeQueryResetsMaximum() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMaximumReset(() -> em.createNativeQuery(
                    "SELECT ID FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID"), true, false);
            assertMaximumReset(() -> em.createNativeQuery(
                    "SELECT * FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID", AgentQueryRecord.class), true, false);
            assertNativeConversionRetainsMaximumReset(() -> em.createNativeQuery(
                    "SELECT * FROM EA_QUERY_RECORD ORDER BY ID"));
        }
    }

    @Test
    public void entityAgentNativeQueryResetsMaximum() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMaximumReset(() -> agent.createNativeQuery(
                    "SELECT ID FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID"), true, false);
            assertMaximumReset(() -> agent.createNativeQuery(
                    "SELECT * FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID", AgentQueryRecord.class), true, false);
            assertNativeConversionRetainsMaximumReset(() -> agent.createNativeQuery(
                    "SELECT * FROM EA_QUERY_RECORD ORDER BY ID"));
        }
    }

    @Test
    public void entityManagerZeroLimitPreservesValidation() {
        TypedQuery<AgentQueryRecord> closed;
        try (EntityManager em = emf.createEntityManager()) {
            assertThrows(IllegalStateException.class,
                    () -> em.createQuery(QUERY, AgentQueryRecord.class).setMaxResults(0).getResultList());
            assertThrows(IllegalStateException.class,
                    () -> em.createQuery("update AgentQueryRecord r set r.title = 'Zero'")
                            .setMaxResults(0).getResultList());
            TypedQuery<AgentQueryRecord> locked = configuredQuery(() -> em.createQuery(QUERY, AgentQueryRecord.class), true);
            locked.setMaxResults(0).setLockMode(LockModeType.PESSIMISTIC_WRITE);
            assertThrows(TransactionRequiredException.class, locked::getResultList);
            closed = configuredQuery(() -> em.createQuery(QUERY, AgentQueryRecord.class), true).setMaxResults(0);
        }
        assertThrows(IllegalStateException.class, closed::getResultList);
    }

    @Test
    public void entityAgentZeroLimitPreservesValidation() {
        TypedQuery<AgentQueryRecord> closed;
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertThrows(IllegalStateException.class,
                    () -> agent.createQuery(QUERY, AgentQueryRecord.class).setMaxResults(0).getResultList());
            assertThrows(IllegalStateException.class,
                    () -> agent.createQuery("update AgentQueryRecord r set r.title = 'Zero'")
                            .setMaxResults(0).getResultList());
            TypedQuery<AgentQueryRecord> locked = configuredQuery(() -> agent.createQuery(QUERY, AgentQueryRecord.class), true);
            locked.setMaxResults(0).setLockMode(LockModeType.PESSIMISTIC_WRITE);
            assertThrows(TransactionRequiredException.class, locked::getResultList);
            closed = configuredQuery(() -> agent.createQuery(QUERY, AgentQueryRecord.class), true).setMaxResults(0);
        }
        assertThrows(IllegalStateException.class, closed::getResultList);
    }

    @Test
    public void entityManagerNativeQueryZeroLimitReturnsNoRows() {
        try (EntityManager em = emf.createEntityManager()) {
            Query query = em.createNativeQuery("SELECT ID FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID");
            query.setParameter(1, 1).setMaxResults(0);
            assertNativeZeroRestoresAfterFailure(query,
                    emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getEventManager());
        }
    }

    @Test
    public void entityAgentNativeQueryZeroLimitReturnsNoRows() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            Query query = agent.createNativeQuery("SELECT ID FROM EA_QUERY_RECORD WHERE ID >= ?1 ORDER BY ID");
            query.setParameter(1, 1).setMaxResults(0);
            assertNativeZeroRestoresAfterFailure(query, agent.unwrap(Session.class).getEventManager());
        }
    }

    @Test
    public void entityManagerNativeZeroLimitClosesCursor() throws SQLException {
        try (EntityManager em = emf.createEntityManager()) {
            Query query = em.createNativeQuery("SELECT ID FROM EA_QUERY_RECORD ORDER BY ID");
            assertNativeZeroClosesDiscardedCursor(query,
                    em.unwrap(JpaEntityManager.class).getActiveSession().getEventManager());
        }
    }

    @Test
    public void entityAgentNativeZeroLimitClosesCursor() throws SQLException {
        try (EntityAgent agent = emf.createEntityAgent()) {
            Query query = agent.createNativeQuery("SELECT ID FROM EA_QUERY_RECORD ORDER BY ID");
            assertNativeZeroClosesDiscardedCursor(query, agent.unwrap(Session.class).getEventManager());
        }
    }

    @Test
    public void entityManagerNativeZeroLimitPreservesMutation() {
        boolean sqlServer = requireNativeReturning();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                assertNativeMutation(sqlServer, List.of(em::createNativeQuery,
                        sql -> em.createNativeQuery(sql, AgentQueryRecord.class),
                        sql -> em.createNativeQuery(sql, Integer.class),
                        sql -> em.createNativeQuery(sql, "EAQuery.recordMapping")),
                        () -> em.createNativeQuery("SELECT COUNT(*) FROM EA_QUERY_RECORD WHERE TITLE = ?1"),
                        () -> em.createNamedQuery(PRESET_QUERY, AgentQueryRecord.class));
            } finally {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void entityAgentNativeZeroLimitPreservesMutation() {
        boolean sqlServer = requireNativeReturning();
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            try {
                assertNativeMutation(sqlServer, List.of(agent::createNativeQuery,
                        sql -> agent.createNativeQuery(sql, AgentQueryRecord.class),
                        sql -> agent.createNativeQuery(sql, Integer.class),
                        sql -> agent.createNativeQuery(sql, "EAQuery.recordMapping")),
                        () -> agent.createNativeQuery("SELECT COUNT(*) FROM EA_QUERY_RECORD WHERE TITLE = ?1"),
                        () -> agent.createNamedQuery(PRESET_QUERY, AgentQueryRecord.class));
            } finally {
                agent.getTransaction().rollback();
            }
        }
    }

    @Test
    public void entityManagerZeroLimitStillFlushesPendingChanges() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                AgentQueryGroup group = em.find(AgentQueryGroup.class, 1);
                em.persist(new AgentQueryRecord(4, "Delta", group));
                TypedQuery<AgentQueryRecord> query = configuredQuery(() -> em.createQuery(QUERY, AgentQueryRecord.class), true);
                query.setMaxResults(0);
                assertEquals(List.of(), query.getResultList());
                // COMMIT prevents this control query from performing the AUTO flush being tested.
                Number count = (Number) em.createNativeQuery("SELECT COUNT(*) FROM EA_QUERY_RECORD")
                        .setFlushMode(FlushModeType.COMMIT).getSingleResult();
                assertEquals(4L, count.longValue());
            } finally {
                em.getTransaction().rollback();
            }
        }
    }

    private void assertMaximumReset(Supplier<? extends Query> createQuery, boolean nativeQuery, boolean preset) {
        for (boolean bind : List.of(true, false)) {
            if (nativeQuery) {
                assertNativeMaximumReset(createQuery, bind);
                continue;
            }
            if (preset) {
                // Reset before another operation can initialize the inherited maximum.
                Query unexecuted = createQuery.get().setMaxResults(Integer.MAX_VALUE);
                configureResetQuery(unexecuted, bind, nativeQuery);
                assertEquals(1, unexecuted.getFirstResult());
                assertEquals(Integer.MAX_VALUE, unexecuted.getMaxResults());
                assertResetIds(unexecuted, List.of(2, 3));
            }

            Query query = configureResetQuery(createQuery.get(), bind, nativeQuery);
            Query independent = configureResetQuery(createQuery.get(), bind, nativeQuery);
            assertResetPage(query, 0, 1, List.of(1));
            assertResetPage(query, 0, Integer.MAX_VALUE, List.of(1, 2, 3));
            assertResetPage(query, 0, Integer.MAX_VALUE, List.of(1, 2, 3));
            assertResetPage(query, 1, 1, List.of(2));
            query.setMaxResults(Integer.MAX_VALUE);
            assertEquals(1, query.getFirstResult());
            assertEquals(Integer.MAX_VALUE, query.getMaxResults());
            assertResetIds(query, List.of(2, 3));
            query.setMaxResults(Integer.MAX_VALUE);
            assertResetIds(query, List.of(2, 3));
            query.setFirstResult(0);
            assertResetIds(query, List.of(1, 2, 3));
            assertResetPage(query, 0, 2, List.of(1, 2));
            assertResetPage(query, 0, 0, List.of());
            assertResetPage(query, 0, Integer.MAX_VALUE, List.of(1, 2, 3));
            assertResetPage(query, 1, 1, List.of(2));
            assertResetPage(query, 1, Integer.MAX_VALUE, List.of(2, 3));

            assertResetDefaults(independent, preset);
            assertResetDefaults(configureResetQuery(createQuery.get(), bind, nativeQuery), preset);
            if (!preset) {
                Query hinted = configureResetQuery(createQuery.get(), bind, nativeQuery)
                        .setHint(QueryHints.JDBC_MAX_ROWS, 1);
                assertResetIds(hinted, List.of(1));
                query.setMaxResults(Integer.MAX_VALUE);
                assertResetIds(query, List.of(2, 3));
                // An untouched wrapper maximum must not erase its native maximum hint.
                assertResetIds(hinted, List.of(1));
                hinted.setMaxResults(Integer.MAX_VALUE);
                assertResetIds(hinted, List.of(1, 2, 3));
                assertResetDefaults(configureResetQuery(createQuery.get(), bind, nativeQuery), false);
            }
        }
    }

    private void assertNativeMaximumReset(Supplier<? extends Query> createQuery, boolean bind) {
        Query independent = configureResetQuery(createQuery.get(), bind, true);
        for (int first : List.of(0, 1)) {
            // Exercise maximum changes at each offset without changing a prepared native call's offset.
            Query query = configureResetQuery(createQuery.get(), bind, true).setFirstResult(first);
            List<Integer> firstRow = first == 0 ? List.of(1) : List.of(2);
            List<Integer> firstTwo = first == 0 ? List.of(1, 2) : List.of(2, 3);
            List<Integer> remaining = first == 0 ? List.of(1, 2, 3) : List.of(2, 3);
            assertResetPage(query, first, 1, firstRow);
            assertResetPage(query, first, Integer.MAX_VALUE, remaining);
            assertResetPage(query, first, Integer.MAX_VALUE, remaining);
            assertResetPage(query, first, 2, firstTwo);
            assertResetPage(query, first, 0, List.of());
            assertResetPage(query, first, Integer.MAX_VALUE, remaining);
            assertResetPage(query, first, 1, firstRow);
            assertResetPage(query, first, Integer.MAX_VALUE, remaining);
            assertResetDefaults(independent, false);
            assertResetDefaults(configureResetQuery(createQuery.get(), bind, true), false);
        }

        Query hinted = configureResetQuery(createQuery.get(), bind, true).setHint(QueryHints.JDBC_MAX_ROWS, 1);
        assertResetIds(hinted, List.of(1));
        Query other = configureResetQuery(createQuery.get(), bind, true);
        assertResetPage(other, 1, 1, List.of(2));
        assertResetPage(other, 1, Integer.MAX_VALUE, List.of(2, 3));
        assertResetIds(hinted, List.of(1));
        hinted.setMaxResults(Integer.MAX_VALUE);
        assertResetIds(hinted, List.of(1, 2, 3));
        assertResetDefaults(configureResetQuery(createQuery.get(), bind, true), false);
    }

    private void assertNativeConversionRetainsMaximumReset(Supplier<StatementOrTypedQuery> createQuery) {
        for (boolean mapped : List.of(false, true)) {
            for (boolean hinted : List.of(false, true)) {
                StatementOrTypedQuery query = createQuery.get();
                if (hinted) {
                    query.setHint(QueryHints.JDBC_MAX_ROWS, 1);
                }
                assertEquals(hinted ? 1 : 3, query.getResultList().size());
                query.setMaxResults(Integer.MAX_VALUE);
                assertEquals(3, query.getResultList().size());
                Query converted = mapped ? query.withResultSetMapping(
                        emf.getResultSetMappings(AgentQueryRecord.class).get("EAQuery.recordMapping"))
                        : query.ofType(AgentQueryRecord.class);
                assertEquals(Integer.MAX_VALUE, converted.getMaxResults());
                assertResetIds(converted, List.of(1, 2, 3));
            }
        }
    }

    private Query configureResetQuery(Query query, boolean bind, boolean nativeQuery) {
        query.setHint(QueryHints.BIND_PARAMETERS, bind);
        return nativeQuery ? query.setParameter(1, 1) : query.setParameter("minimum", 1);
    }

    private void assertResetDefaults(Query query, boolean preset) {
        assertEquals(preset ? 1 : 0, query.getFirstResult());
        assertEquals(preset ? 1 : Integer.MAX_VALUE, query.getMaxResults());
        assertResetIds(query, preset ? List.of(2) : List.of(1, 2, 3));
    }

    private void assertResetPage(Query query, int first, int maximum, List<Integer> expected) {
        query.setFirstResult(first).setMaxResults(maximum);
        assertEquals(first, query.getFirstResult());
        assertEquals(maximum, query.getMaxResults());
        assertResetIds(query, expected);
    }

    private void assertResetIds(Query query, List<Integer> expected) {
        List<?> rows = query.getResultList();
        assertEquals("first=" + query.getFirstResult() + ", maximum=" + query.getMaxResults(), expected,
                rows.stream().map(row -> row instanceof AgentQueryRecord record
                        ? record.id : ((Number) row).intValue()).toList());
    }

    private boolean requireNativeReturning() {
        var platform = emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getPlatform();
        assumeTrue("This native mutation fixture requires SQL Server OUTPUT or PostgreSQL RETURNING",
                platform.isSQLServer() || platform.isPostgreSQL());
        return platform.isSQLServer();
    }

    private void assertNativeMutation(boolean sqlServer, List<Function<String, Query>> factories,
                                      Supplier<Query> countTitle, Supplier<TypedQuery<AgentQueryRecord>> freshNamed) {
        for (boolean bind : List.of(true, false)) {
            for (int index = 0; index < factories.size(); index++) {
                String projection = index == 1 || index == 3 ? "*" : "ID";
                String sql = sqlServer
                        ? "UPDATE EA_QUERY_RECORD SET TITLE = ?1 OUTPUT INSERTED." + projection + " WHERE ID >= ?2"
                        : "UPDATE EA_QUERY_RECORD SET TITLE = ?1 WHERE ID >= ?2 RETURNING " + projection;
                String title = "NativeZero" + index + bind;
                Query query = factories.get(index).apply(sql).setHint("eclipselink.jdbc.bind-parameters", bind)
                        .setParameter(1, title).setParameter(2, 1).setFirstResult(1).setMaxResults(0);
                List<?> results = query.getResultList();
                // Check the database effect first: an empty result must not hide a skipped SQL statement.
                Number changed = (Number) countTitle.get().setParameter(1, title)
                        .setFlushMode(FlushModeType.COMMIT).getSingleResult();
                assertEquals(3L, changed.longValue());
                assertEquals(List.of(), results);
                assertEquals(1, query.getFirstResult());
                assertEquals(0, query.getMaxResults());

                TypedQuery<AgentQueryRecord> fresh = configuredQuery(freshNamed, bind);
                assertEquals(1, fresh.getFirstResult());
                assertEquals(1, fresh.getMaxResults());
                assertIds(fresh, List.of(2));

                query.setParameter(1, title + "Again").setParameter(2, 3).setFirstResult(0).setMaxResults(1);
                List<?> positive = query.getResultList();
                Number changedAgain = (Number) countTitle.get().setParameter(1, title + "Again")
                        .setFlushMode(FlushModeType.COMMIT).getSingleResult();
                assertEquals(1L, changedAgain.longValue());
                assertEquals(1, positive.size());
            }
        }
    }

    private void assertNativeZeroRestoresAfterFailure(Query query, SessionEventManager events) {
        IllegalStateException failure = new IllegalStateException("native zero-limit execution failure");
        SessionEventAdapter listener = new SessionEventAdapter() {
            private boolean armed = true;

            @Override
            public void preExecuteQuery(SessionEvent event) {
                if (armed && event.getQuery().isUserDefined()) {
                    armed = false;
                    throw failure;
                }
            }
        };
        events.addListener(listener);
        try {
            assertSame(failure, assertThrows(IllegalStateException.class, query::getResultList));
            assertEquals(0, query.getFirstResult());
            assertEquals(0, query.getMaxResults());
            assertEmptyResults(query);
            assertEquals(1, query.setMaxResults(1).getResultList().size());
        } finally {
            events.removeListener(listener);
        }
    }

    private void assertNativeZeroClosesDiscardedCursor(Query query, SessionEventManager events) throws SQLException {
        List<Cursor> cursors = new ArrayList<>();
        List<ResultSet> resultSets = new ArrayList<>();
        SessionEventAdapter listener = new SessionEventAdapter() {
            @Override
            public void postExecuteQuery(SessionEvent event) {
                if (event.getQuery().isUserDefined() && event.getResult() instanceof Cursor cursor
                        && !cursors.contains(cursor)) {
                    cursors.add(cursor);
                    resultSets.add(cursor.getResultSet());
                    assertFalse(cursor.isClosed());
                }
            }
        };
        query.setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 0).setMaxResults(0);
        events.addListener(listener);
        try {
            assertNull(query.getSingleResultOrNull());
            assertEquals(1, cursors.size());
            assertCursorClosed(cursors.get(0), resultSets.get(0));

            assertThrows(NoResultException.class, query::getSingleResult);
            assertEquals(2, cursors.size());
            assertCursorClosed(cursors.get(1), resultSets.get(1));
            assertEquals(0, query.getFirstResult());
            assertEquals(0, query.getMaxResults());

            Cursor positive = (Cursor) query.setMaxResults(1).getSingleResult();
            try {
                assertEquals(3, cursors.size());
                assertSame(cursors.get(2), positive);
                assertFalse(positive.isClosed());
                assertNotNull(resultSets.get(2));
                assertFalse(resultSets.get(2).isClosed());
                assertTrue(positive.hasNext());
            } finally {
                positive.close();
            }
            assertCursorClosed(positive, resultSets.get(2));
        } finally {
            events.removeListener(listener);
            // Preserve ownership even when the unfixed provider fails an assertion above.
            for (Cursor cursor : cursors) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }

    private void assertCursorClosed(Cursor cursor, ResultSet resultSet) throws SQLException {
        assertTrue(cursor.isClosed());
        assertNotNull(resultSet);
        assertTrue(resultSet.isClosed());
    }

    private void assertPresetReference(Supplier<TypedQuery<AgentQueryRecord>> createQuery) {
        for (boolean bind : List.of(true, false)) {
            TypedQuery<AgentQueryRecord> query = configuredQuery(createQuery, bind);
            assertEquals(1, query.getFirstResult());
            assertEquals(1, query.getMaxResults());
            assertIds(query, List.of(2));
            assertPage(query, 0, 2, List.of(1, 2));
            assertPage(query, 0, 2, List.of(1, 2));
            TypedQuery<AgentQueryRecord> fresh = configuredQuery(createQuery, bind);
            assertEquals(1, fresh.getFirstResult());
            assertEquals(1, fresh.getMaxResults());
            assertIds(fresh, List.of(2));
            assertIds(query, List.of(1, 2));
        }
    }

    private void assertEmptyResults(Query query) {
        assertEquals(List.of(), query.getResultList());
        assertNull(query.getSingleResultOrNull());
        assertThrows(NoResultException.class, query::getSingleResult);
        try (var results = query.getResultStream()) {
            assertEquals(0L, results.count());
        }
    }

    private void assertFinitePages(Supplier<TypedQuery<AgentQueryRecord>> createQuery) {
        for (boolean bind : List.of(true, false)) {
            TypedQuery<AgentQueryRecord> query = configuredQuery(createQuery, bind);
            TypedQuery<AgentQueryRecord> independent = configuredQuery(createQuery, bind);

            assertPage(query, 1, 1, List.of(2));
            // The sum is still two: changing only the native maximum row cannot repair this transition.
            assertPage(query, 0, 2, List.of(1, 2));
            query.setMaxResults(0);
            assertEmptyResults(query);
            assertEquals(3L, query.getResultCount());
            assertPage(query, 0, 2, List.of(1, 2));
            assertPage(query, 1, 2, List.of(2, 3));
            assertPage(query, 2, 2, List.of(3));
            assertPage(query, 1, 1, List.of(2));
            assertPage(query, 1, 3, List.of(2, 3));
            assertPage(query, 0, 3, List.of(1, 2, 3));
            assertPage(query, 2, 1, List.of(3));
            assertPage(query, 2, 1, List.of(3));

            assertIds(independent, List.of(1, 2, 3));
            assertEquals(0, independent.getFirstResult());
            assertEquals(Integer.MAX_VALUE, independent.getMaxResults());
            assertPage(independent, 0, 1, List.of(1));
            assertIds(query, List.of(3));
            assertIds(configuredQuery(createQuery, bind), List.of(1, 2, 3));
        }
    }

    private void assertPaginationShapes(Supplier<TypedQuery<AgentQueryRecord>> createQuery) {
        for (boolean bind : List.of(true, false)) {
            TypedQuery<AgentQueryRecord> query = configuredQuery(createQuery, bind);
            assertIds(query, List.of(1, 2, 3));
            query.setMaxResults(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, query.getMaxResults());
            assertIds(query, List.of(1, 2, 3));
            query.setFirstResult(1);
            assertIds(query, List.of(2, 3));
            query.setFirstResult(2);
            assertIds(query, List.of(3));
            query.setFirstResult(0);
            assertIds(query, List.of(1, 2, 3));
            query.setFirstResult(1);
            assertIds(query, List.of(2, 3));
            assertPage(query, 1, 1, List.of(2));
            assertPage(query, 0, 2, List.of(1, 2));

            TypedQuery<AgentQueryRecord> zero = configuredQuery(createQuery, bind);
            zero.setMaxResults(0);
            assertEquals(0, zero.getMaxResults());
            assertEmptyResults(zero);
            assertEquals(3L, zero.getResultCount());
            assertPage(zero, 0, 1, List.of(1));
            assertPage(zero, 1, 1, List.of(2));
            zero.setMaxResults(0);
            assertEmptyResults(zero);
            assertPage(zero, 1, 1, List.of(2));
            assertIds(configuredQuery(createQuery, bind), List.of(1, 2, 3));
        }
    }

    private TypedQuery<AgentQueryRecord> configuredQuery(Supplier<TypedQuery<AgentQueryRecord>> createQuery,
                                                        boolean bind) {
        return createQuery.get().setHint("eclipselink.jdbc.bind-parameters", bind).setParameter("minimum", 1);
    }

    private void assertPage(TypedQuery<AgentQueryRecord> query, int first, int maximum, List<Integer> expected) {
        query.setFirstResult(first).setMaxResults(maximum);
        assertEquals(first, query.getFirstResult());
        assertEquals(maximum, query.getMaxResults());
        assertIds(query, expected);
    }

    private void assertIds(TypedQuery<AgentQueryRecord> query, List<Integer> expected) {
        assertEquals("first=" + query.getFirstResult() + ", maximum=" + query.getMaxResults(),
                expected, query.getResultList().stream().map(record -> record.id).toList());
    }
}
