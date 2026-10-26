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
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCode;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventManager;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/** Actual PostgreSQL procedure outputs and JDBC ownership; no cursor streaming assumption. */
@RunWith(EmfRunner.class)
public class TestEntityAgentPostgreSQLProcedures {
    @Emf(name = "agent-postgresql-procedures", createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jdbc.cache-statements", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;
    private boolean postgresql;
    private String scalarName;
    private String cursorName;
    private boolean scalarOwned;
    private boolean cursorOwned;

    @Before
    public void createOwnedProcedures() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            postgresql = agent.callWithConnection((Connection connection) ->
                    "PostgreSQL".equals(connection.getMetaData().getDatabaseProductName()));
            assumeTrue("This fixture exercises PostgreSQL procedure OUT/INOUT/refcursor parameters", postgresql);
            String token = UUID.randomUUID().toString().replace("-", "");
            scalarName = "ea_agent_scalar_" + token;
            cursorName = "ea_agent_cursor_" + token;
            agent.runWithConnection((Connection connection) -> {
                assertFalse(routineExists(connection, scalarName));
                assertFalse(routineExists(connection, cursorName));
                try (var statement = connection.createStatement()) {
                    statement.execute("CREATE PROCEDURE " + scalarName
                            + "(IN input_id integer, INOUT value_text varchar, OUT doubled integer, OUT out_text varchar) "
                            + "LANGUAGE plpgsql AS $$ BEGIN value_text := value_text || input_id::text; "
                            + "doubled := input_id * 2; out_text := 'out-' || input_id::text; END $$");
                    scalarOwned = true;
                    statement.execute("CREATE PROCEDURE " + cursorName
                            + "(IN input_id integer, OUT records refcursor, OUT base_records refcursor) LANGUAGE plpgsql AS $$ "
                            + "BEGIN OPEN records FOR SELECT * FROM EA_QUERY_RECORD WHERE ID = input_id; "
                            + "OPEN base_records FOR SELECT * FROM EA_QUERY_RECORD WHERE ID = 1; END $$");
                    cursorOwned = true;
                }
            });
        }
        emf.runInTransaction(em -> {
            em.createStatement("delete from AgentQueryRecord").execute();
            em.persist(new AgentQueryRecord(1, "ProcedureBase", null));
        });
    }

    @After
    public void removeOwnedProcedures() {
        if (!postgresql) {
            return;
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    if (cursorOwned) {
                        statement.execute("DROP PROCEDURE " + cursorName + "(integer)");
                        assertFalse(routineExists(connection, cursorName));
                    }
                    if (scalarOwned) {
                        statement.execute("DROP PROCEDURE " + scalarName + "(integer, character varying)");
                        assertFalse(routineExists(connection, scalarName));
                    }
                }
            });
        }
        emf.runInTransaction(em -> em.createStatement("delete from AgentQueryRecord").execute());
    }

    @Test
    public void positionalInInoutAndOutParametersUseTheAgentConnection() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent(); Calls calls = new Calls(agent)) {
            StoredProcedureQuery query = scalar(agent);
            agent.getTransaction().begin();
            Connection caller = agent.callWithConnection((Connection connection) -> connection);
            query.setParameter(1, 7).setParameter(2, "in-");
            query.execute();
            assertEquals("in-7", query.getOutputParameterValue(2));
            assertEquals(14, query.getOutputParameterValue(3));
            assertEquals("out-7", query.getOutputParameterValue(4));
            CallableStatement statement = calls.last();
            assertFalse(statement.isClosed());
            assertSame(caller, statement.getConnection());
            query.close();
            assertTrue(statement.isClosed());
            assertFalse(caller.isClosed());
            assertConnectionUsable(agent);
            agent.getTransaction().commit();
        }
        assertPeerUsable();
    }

    @Test
    public void convertedInoutAndOutValuesSupportTypedReadback() {
        try (EntityAgent agent = emf.createEntityAgent(); StoredProcedureQuery query = agent.createStoredProcedureQuery(scalarName)) {
            Parameter<Integer> input = query.registerParameter(1, Integer.class, ParameterMode.IN);
            Parameter<AgentQueryCode> inout = query.registerConvertedParameter(2, AgentQueryCodeConverter.class, ParameterMode.INOUT);
            Parameter<Integer> doubled = query.registerParameter(3, Integer.class, ParameterMode.OUT);
            Parameter<AgentQueryCode> output = query.registerConvertedParameter(4, AgentQueryCodeConverter.class, ParameterMode.OUT);
            AgentQueryCodeConverter.conversions = 0;
            agent.getTransaction().begin();
            query.setParameter(input, 8).setParameter(inout, new AgentQueryCode("converted-"));
            query.execute();
            assertEquals(new AgentQueryCode("converted-8"), query.getOutputParameterValue(inout));
            assertEquals(Integer.valueOf(16), query.getOutputParameterValue(doubled));
            assertEquals(new AgentQueryCode("out-8"), query.getOutputParameterValue(output));
            assertEquals(new AgentQueryCode("converted-8"), query.getOutputParameterValue(inout));
            assertEquals(1, AgentQueryCodeConverter.conversions);
            agent.getTransaction().rollback();
        }
        assertPeerUsable();
    }

    @Test
    public void refcursorMappedEntitiesSeeOwnUncommittedDataAndRemainDetached() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent();
             Calls calls = new Calls(agent)) {
            StoredProcedureQuery query = cursor(agent);
            agent.getTransaction().begin();
            agent.insert(new AgentQueryRecord(17, "Pending", null));
            Connection caller = agent.callWithConnection((Connection connection) -> connection);
            query.setParameter(1, 17);
            List<?> results = query.getResultList();
            assertEquals(1, results.size());
            AgentQueryRecord detached = (AgentQueryRecord) results.get(0);
            assertEquals("Pending", detached.title);
            assertEquals(1, detached.loadCallbacks);
            try (EntityManager em = emf.createEntityManager()) {
                assertFalse(em.contains(detached));
            }
            detached.title = "Detached cursor mutation";
            assertEquals("Pending", agent.get(AgentQueryRecord.class, 17).title);
            assertNull(peer.find(AgentQueryRecord.class, 17));
            CallableStatement statement = calls.last();
            assertSame(caller, statement.getConnection());
            query.close();
            assertTrue(statement.isClosed());
            assertFalse(caller.isClosed());
            assertConnectionUsable(agent);
            agent.getTransaction().rollback();
            assertNull(peer.find(AgentQueryRecord.class, 17));
            assertEquals("Detached cursor mutation", detached.title);
        }
    }

    @Test
    public void refcursorOutputParameterIsMappedAndCommitClosesItsStatement() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent(); Calls calls = new Calls(agent);
             StoredProcedureQuery query = cursor(agent)) {
            agent.getTransaction().begin();
            query.setParameter(1, 1);
            query.execute();
            Object output = query.getOutputParameterValue(2);
            assertTrue(output instanceof List<?>);
            AgentQueryRecord result = (AgentQueryRecord) ((List<?>) output).get(0);
            assertEquals("ProcedureBase", result.title);
            CallableStatement statement = calls.last();
            agent.getTransaction().commit();
            assertTrue(statement.isClosed());
            assertConnectionUsable(agent);
            result.title = "Detached output";
        }
        assertPeerUsable();
    }

    @Test
    public void commitAndRollbackCloseActualStatementsAndLeaveTheQueryReusable() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent(); Calls calls = new Calls(agent);
             StoredProcedureQuery query = scalar(agent)) {
            agent.getTransaction().begin();
            query.setParameter(1, 2).setParameter(2, "first-");
            query.execute();
            assertEquals("first-2", query.getOutputParameterValue(2));
            CallableStatement first = calls.last();
            agent.getTransaction().commit();
            assertTrue(first.isClosed());
            agent.getTransaction().begin();
            query.setParameter(1, 3).setParameter(2, "second-");
            query.execute();
            assertEquals("second-3", query.getOutputParameterValue(2));
            CallableStatement second = calls.last();
            assertNotSame(first, second);
            agent.getTransaction().rollback();
            assertTrue(second.isClosed());
            assertConnectionUsable(agent);
        }
        assertPeerUsable();
    }

    @Test
    public void ownerCloseReleasesRefcursorStatementAndRollsBackItsRows() throws Exception {
        EntityAgent agent = emf.createEntityAgent();
        try (Calls calls = new Calls(agent)) {
            agent.getTransaction().begin();
            agent.insert(new AgentQueryRecord(19, "Owned pending", null));
            StoredProcedureQuery query = cursor(agent).setParameter(1, 19);
            assertEquals(1, query.getResultList().size());
            CallableStatement statement = calls.last();
            agent.close();
            assertTrue(statement.isClosed());
            assertThrows(IllegalStateException.class, query::getParameters);
        } finally {
            if (agent.isOpen()) {
                agent.close();
            }
        }
        try (EntityAgent peer = emf.createEntityAgent()) {
            assertNull(peer.find(AgentQueryRecord.class, 19));
        }
        assertPeerUsable();
    }

    @Test
    public void twoRefcursorsPreserveResultOrderingAndCloseAfterPartialConsumption() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent(); Calls calls = new Calls(agent)) {
            agent.getTransaction().begin();
            agent.insert(new AgentQueryRecord(23, "First cursor", null));
            try (StoredProcedureQuery query = cursor(agent)) {
                query.setParameter(1, 23);
                assertTrue(query.execute());
                assertEquals("First cursor", ((AgentQueryRecord) query.getResultList().get(0)).title);
                assertTrue(query.hasMoreResults());
                assertEquals("ProcedureBase", ((AgentQueryRecord) query.getResultList().get(0)).title);
                assertFalse(query.hasMoreResults());
                assertEquals(-1, query.getUpdateCount());
            }
            assertTrue(calls.last().isClosed());
            StoredProcedureQuery partial = cursor(agent).setParameter(1, 23);
            assertEquals(1, partial.getResultList().size());
            CallableStatement statement = calls.last();
            partial.close();
            assertTrue(statement.isClosed());
            assertConnectionUsable(agent);
            agent.getTransaction().rollback();
        }
        assertPeerUsable();
    }

    @Test
    public void ordinaryEntityManagerRetainsManagedCursorAndScalarOutputBehavior() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try (StoredProcedureQuery query = em.createStoredProcedureQuery(scalarName)) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.registerParameter(2, String.class, ParameterMode.INOUT);
                Parameter<Integer> output = query.registerParameter(3, Integer.class, ParameterMode.OUT);
                query.registerParameter(4, String.class, ParameterMode.OUT);
                query.setParameter(1, 4).setParameter(2, "em-");
                query.execute();
                assertEquals("em-4", query.getOutputParameterValue(2));
                assertEquals(Integer.valueOf(8), query.getOutputParameterValue(output));
            }
            try (StoredProcedureQuery query = em.createStoredProcedureQuery(cursorName,
                    AgentQueryRecord.class, AgentQueryRecord.class)) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.registerParameter(2, void.class, ParameterMode.REF_CURSOR);
                query.registerParameter(3, void.class, ParameterMode.REF_CURSOR);
                query.setParameter(1, 1);
                AgentQueryRecord managed = (AgentQueryRecord) query.getResultList().get(0);
                assertTrue(em.contains(managed));
                managed.setTitle("Managed procedure result");
                em.flush();
                assertEquals("Managed procedure result", em.createQuery(
                        "select r.title from AgentQueryRecord r where r.id = 1", String.class).getSingleResult());
            }
            em.getTransaction().rollback();
        }
        assertPeerUsable();
    }

    private StoredProcedureQuery scalar(EntityAgent agent) {
        StoredProcedureQuery query = agent.createStoredProcedureQuery(scalarName);
        query.registerParameter(1, Integer.class, ParameterMode.IN);
        query.registerParameter(2, String.class, ParameterMode.INOUT);
        query.registerParameter(3, Integer.class, ParameterMode.OUT);
        query.registerParameter(4, String.class, ParameterMode.OUT);
        return query;
    }

    private StoredProcedureQuery cursor(EntityAgent agent) {
        StoredProcedureQuery query = agent.createStoredProcedureQuery(cursorName, AgentQueryRecord.class, AgentQueryRecord.class);
        query.registerParameter(1, Integer.class, ParameterMode.IN);
        query.registerParameter(2, void.class, ParameterMode.REF_CURSOR);
        query.registerParameter(3, void.class, ParameterMode.REF_CURSOR);
        return query;
    }

    private static boolean routineExists(Connection connection, String name) throws SQLException {
        try (var query = connection.prepareStatement("SELECT COUNT(*) FROM pg_catalog.pg_proc P "
                + "JOIN pg_catalog.pg_namespace N ON P.pronamespace=N.oid "
                + "WHERE N.nspname=current_schema() AND P.proname=?")) {
            query.setString(1, name);
            try (var result = query.executeQuery()) {
                result.next();
                return result.getInt(1) != 0;
            }
        }
    }

    private static void assertConnectionUsable(EntityAgent agent) {
        agent.runWithConnection((Connection connection) -> {
            assertFalse(connection.isClosed());
            try (var query = connection.createStatement(); var result = query.executeQuery("SELECT 1")) {
                assertTrue(result.next());
                assertEquals(1, result.getInt(1));
            }
        });
    }

    private void assertPeerUsable() {
        try (EntityAgent peer = emf.createEntityAgent()) {
            assertEquals("ProcedureBase", peer.get(AgentQueryRecord.class, 1).title);
            assertConnectionUsable(peer);
        }
    }

    private static final class Calls extends SessionEventAdapter implements AutoCloseable {
        private final SessionEventManager manager;
        private final List<CallableStatement> statements = new ArrayList<>();

        Calls(EntityAgent agent) {
            manager = agent.unwrap(ClientSession.class).getEventManager();
            manager.addListener(this);
        }

        @Override
        public void postExecuteCall(SessionEvent event) {
            if (event.getCall() instanceof DatabaseCall call && call.getStatement() instanceof CallableStatement statement) {
                statements.add(statement);
            }
        }

        CallableStatement last() {
            assertFalse("The actual callable statement must be observed", statements.isEmpty());
            return statements.get(statements.size() - 1);
        }

        @Override
        public void close() {
            manager.removeListener(this);
        }
    }
}
