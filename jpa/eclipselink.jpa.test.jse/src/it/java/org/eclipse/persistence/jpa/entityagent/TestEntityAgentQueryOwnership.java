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
import jakarta.persistence.Statement;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.Field;
import java.util.Set;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.entityagent.model.AgentRaceRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.Cursor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/** Resource ownership is measured deterministically, without GC timing. */
@RunWith(EmfRunner.class)
public class TestEntityAgentQueryOwnership {
    @Emf(name = "agent-query-ownership", createTables = DDLGen.DROP_CREATE,
            classes = AgentRaceRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        try (EntityAgent agent = emf.createEntityAgent()) {
            for (int id = 1; id <= 4; id++) agent.insert(new AgentRaceRecord(id, "row " + id));
        }
    }

    @After
    public void cleanup() {
        emf.runInTransaction(manager -> manager.createStatement("delete from AgentRaceRecord").execute());
    }

    static int retainedResources(EntityAgent agent) throws ReflectiveOperationException {
        Field field = agent.getClass().getDeclaredField("queries");
        field.setAccessible(true);
        return ((Set<?>) field.get(agent)).size();
    }

    @Test
    public void completedQueriesAndStatementsDoNotAccumulateAcrossTransactions() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent()) {
            for (int cycle = 0; cycle < 4; cycle++) {
                agent.getTransaction().begin();
                for (int index = 0; index < 16; index++) {
                    String bound = new String(new char[128]).replace('\0', 'x');
                    assertTrue(agent.createQuery("select r from AgentRaceRecord r where r.name=:name", AgentRaceRecord.class)
                            .setParameter("name", bound).getResultList().isEmpty());
                    assertEquals(0, agent.createStatement("update AgentRaceRecord r set r.name=:name where r.id=-1")
                            .setParameter("name", bound).execute());
                    assertEquals("Completed results must not retain their bindings", 0, retainedResources(agent));
                }
                if (cycle % 2 == 0) agent.getTransaction().commit(); else agent.getTransaction().rollback();
                assertEquals(0, retainedResources(agent));
            }
        }
    }

    @Test
    public void failedConstructionHasPublicExceptionAndDoesNotAcquireOwnership() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            for (int attempt = 0; attempt < 8; attempt++) {
                IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                        () -> agent.createQuery("this is invalid JPQL", AgentRaceRecord.class));
                assertNotNull("The compiler failure is preserved", failure.getCause());
                assertThrows(IllegalArgumentException.class, () -> agent.createStatement("this is invalid JPQL"));
                assertEquals(0, retainedResources(agent));
                assertFalse(agent.getTransaction().getRollbackOnly());
            }
            agent.insert(new AgentRaceRecord(5, "after invalid construction"));
            agent.getTransaction().commit();
            assertEquals("after invalid construction", agent.get(AgentRaceRecord.class, 5L).name);
        }
    }

    private Cursor cursor(EntityAgent agent) {
        return agent.createQuery("select r from AgentRaceRecord r order by r.id", AgentRaceRecord.class)
                .setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 0)
                .setHint(QueryHints.CURSOR_PAGE_SIZE, 1).unwrap(JpaQuery.class).getResultCursor();
    }

    @Test
    public void actualCursorsRemainOwnedUntilCompletionAndOwnerDisposal() throws Exception {
        EntityAgent agent = emf.createEntityAgent();
        try {
            for (boolean commit : new boolean[] {true, false}) {
                agent.getTransaction().begin();
                Cursor cursor = cursor(agent);
                assertFalse(cursor.isClosed());
                assertEquals(1, retainedResources(agent));
                if (commit) agent.getTransaction().commit(); else agent.getTransaction().rollback();
                assertTrue(cursor.isClosed());
                assertNull(cursor.getQuery().getProperty("eclipselink.cursor.resource-release"));
                assertEquals(0, retainedResources(agent));
            }
            Cursor cursor = cursor(agent);
            assertEquals(1, retainedResources(agent));
            agent.close();
            assertTrue(cursor.isClosed());
            assertNull(cursor.getQuery().getProperty("eclipselink.cursor.resource-release"));
            assertEquals(0, retainedResources(agent));
        } finally {
            if (agent.isOpen()) agent.close();
        }
    }

    @Test
    public void explicitMultipleCursorCloseReleasesOnlyTheLastOutstandingResource() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var query = agent.createQuery("select r from AgentRaceRecord r order by r.id", AgentRaceRecord.class)
                    .setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .setHint(QueryHints.CURSOR_PAGE_SIZE, 1).unwrap(JpaQuery.class);
            Cursor first = query.getResultCursor();
            Cursor second = query.getResultCursor();
            assertNotSame(first.getQuery(), second.getQuery());
            assertNotSame(first.getQuery(), query.getDatabaseQuery());
            assertNull(query.getDatabaseQuery().getProperty("eclipselink.cursor.resource-release"));
            assertEquals(1, retainedResources(agent));
            first.close();
            assertNull(first.getQuery().getProperty("eclipselink.cursor.resource-release"));
            assertEquals(1, retainedResources(agent));
            assertFalse(second.isClosed());
            first.close();
            second.close();
            assertEquals(0, retainedResources(agent));
            assertNull(second.getQuery().getProperty("eclipselink.cursor.resource-release"));
            second.close();
            try (var stream = agent.createQuery("select r from AgentRaceRecord r", AgentRaceRecord.class).getResultStream()) {
                assertEquals(4, stream.count());
            }
            assertEquals(0, retainedResources(agent));
        }
    }

    @Test
    public void ordinaryManagerCursorClosePreservesManagedResultsAndReusableQuery() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            var query = manager.createQuery("select r from AgentRaceRecord r order by r.id", AgentRaceRecord.class)
                    .setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 0)
                    .setHint(QueryHints.CURSOR_PAGE_SIZE, 1).unwrap(JpaQuery.class);
            Cursor first = query.getResultCursor();
            Object row = first.next();
            assertTrue(manager.contains(row));
            first.close();
            assertTrue(first.isClosed());
            assertNull(first.getQuery().getProperty("eclipselink.cursor.resource-release"));
            assertNull(query.getDatabaseQuery().getProperty("eclipselink.cursor.resource-release"));
            first.close();
            Cursor second = query.getResultCursor();
            assertSame(row, second.next());
            manager.getTransaction().commit();
            assertTrue(second.isClosed());
            assertNull(second.getQuery().getProperty("eclipselink.cursor.resource-release"));
        }
    }

    @Test
    public void unretainedEscapedQueriesStillRejectClosedOwner() throws Exception {
        EntityAgent agent = emf.createEntityAgent();
        TypedQuery<AgentRaceRecord> query = agent.createQuery("select r from AgentRaceRecord r", AgentRaceRecord.class);
        Statement statement = agent.createStatement("delete from AgentRaceRecord where id=-1");
        assertEquals(0, retainedResources(agent));
        agent.close();
        assertThrows(IllegalStateException.class, query::getParameters);
        assertThrows(IllegalStateException.class, query::getResultList);
        assertThrows(IllegalStateException.class, statement::getOptions);
        assertThrows(IllegalStateException.class, statement::execute);
    }
}
