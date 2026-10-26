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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedIdentity;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static jakarta.persistence.sql.ResultSetMapping.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentQueryProcedures {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class, AgentGeneratedIdentity.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;
    private boolean h2;

    @Before
    public void createOwnedProcedures() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            h2 = agent.callWithConnection((Connection connection) -> "H2".equals(connection.getMetaData().getDatabaseProductName()));
            assumeTrue("This procedure fixture uses H2 Java aliases", h2);
            agent.runWithConnection((Connection connection) -> {
                try (java.sql.Statement statement = connection.createStatement()) {
                    statement.execute("CREATE ALIAS IF NOT EXISTS EA_QUERY_PROC_ROWS FOR \"" + getClass().getName() + ".procedureRows\"");
                    statement.execute("CREATE ALIAS IF NOT EXISTS EA_QUERY_PROC_TITLE FOR \"" + getClass().getName() + ".procedureTitle\"");
                }
            });
        }
        emf.runInTransaction(em -> {
            em.createStatement("delete from AgentQueryRecord").execute();
            em.persist(new AgentQueryRecord(1, "Procedure", null));
        });
    }

    @After
    public void removeOwnedProcedures() {
        if (!h2) {
            return;
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (java.sql.Statement statement = connection.createStatement()) {
                    statement.execute("DROP ALIAS IF EXISTS EA_QUERY_PROC_ROWS");
                    statement.execute("DROP ALIAS IF EXISTS EA_QUERY_PROC_TITLE");
                }
            });
        }
        emf.runInTransaction(em -> em.createStatement("delete from AgentQueryRecord").execute());
    }

    @Test
    public void procedureEntityAndNamedMappingsReturnDetachedResults() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            try (StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_ROWS", AgentQueryRecord.class)) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.setParameter(1, 1);
                AgentQueryRecord result = (AgentQueryRecord) query.getSingleResult();
                result.setTitle("Detached procedure");
            }
            try (StoredProcedureQuery query = agent.createNamedStoredProcedureQuery("EAQuery.rows")) {
                query.setParameter(1, 1);
                assertEquals("Procedure", ((AgentQueryRecord) query.getSingleResult()).title);
            }
            try (StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_ROWS", "EAQuery.recordMapping")) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.setParameter(1, 1);
                assertEquals("Procedure", ((AgentQueryRecord) query.getSingleResult()).title);
            }
        }
        assertEquals("Procedure", emf.callInTransaction(em -> em.find(AgentQueryRecord.class, 1).title));
    }

    @Test
    public void procedureTypedResultsAndProgrammaticMappingsWork() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            try (StoredProcedureQuery query = rows(agent)) {
                AgentQueryRecord entity = query.getSingleResult(AgentQueryRecord.class);
                assertEquals("Procedure", entity.title);
            }
            try (StoredProcedureQuery query = rows(agent)) {
                Tuple result = query.getSingleResult(tuple(column("ID", Integer.class), column("TITLE", String.class)));
                assertEquals(1, result.get(0));
                assertEquals("Procedure", result.get(1));
            }
            try (StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_TITLE", String.class)) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.setParameter(1, 1);
                assertEquals(List.of("Procedure"), query.getResultList());
            }
            try (StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_TITLE")) {
                query.registerParameter(1, Integer.class, ParameterMode.IN);
                query.setParameter(1, 1);
                assertEquals(new ProcedureTitle("Procedure"), query.getSingleResult(ProcedureTitle.class));
            }
        }
    }

    @Test
    public void procedureRemainsReusableAfterTransactionCleanupAndCloseIsTerminal() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent()) {
            StoredProcedureQuery query = rows(agent);
            agent.getTransaction().begin();
            assertEquals(1, query.getResultList(AgentQueryRecord.class).size());
            assertEquals(1, TestEntityAgentQueryOwnership.retainedResources(agent));
            agent.getTransaction().commit();
            assertEquals(0, TestEntityAgentQueryOwnership.retainedResources(agent));
            assertEquals(1, query.getResultList(AgentQueryRecord.class).size());
            assertEquals(1, TestEntityAgentQueryOwnership.retainedResources(agent));
            query.close();
            assertEquals(0, TestEntityAgentQueryOwnership.retainedResources(agent));
            assertThrows(IllegalStateException.class, query::getOptions);
            assertThrows(IllegalStateException.class, query::close);
        }
        EntityAgent agent = emf.createEntityAgent();
        StoredProcedureQuery acquired = rows(agent);
        agent.close();
        assertThrows(IllegalStateException.class, acquired::getParameters);
    }

    @Test
    public void failedProcedureCleanupRetainsOwnershipUntilSuccessfulRetry() throws Exception {
        try (EntityAgent agent = emf.createEntityAgent()) {
            StoredProcedureQuery query = rows(agent);
            assertEquals(1, query.getResultList(AgentQueryRecord.class).size());
            java.lang.reflect.Field field = query.getClass().getDeclaredField("executeStatement");
            field.setAccessible(true);
            java.sql.Statement original = (java.sql.Statement) field.get(query);
            java.util.concurrent.atomic.AtomicBoolean first = new java.util.concurrent.atomic.AtomicBoolean(true);
            SQLException primary = new SQLException("one-shot owned statement close failure");
            field.set(query, java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[] {java.sql.Statement.class}, (proxy, method, arguments) -> {
                        if (method.getName().equals("close") && first.getAndSet(false)) throw primary;
                        try {
                            return method.invoke(original, arguments);
                        } catch (java.lang.reflect.InvocationTargetException failure) {
                            throw failure.getCause();
                        }
                    }));
            PersistenceException failure = assertThrows(PersistenceException.class, query::close);
            assertSame(primary, failure.getCause());
            assertFalse(original.isClosed());
            assertEquals(1, TestEntityAgentQueryOwnership.retainedResources(agent));
            query.close();
            assertTrue(original.isClosed());
            assertEquals(0, TestEntityAgentQueryOwnership.retainedResources(agent));
            assertEquals("Procedure", agent.get(AgentQueryRecord.class, 1).title);
        }
    }

    @Test
    public void functionOutputRemainsAvailableAfterExecution() {
        try (EntityAgent agent = emf.createEntityAgent();
             StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_TITLE")) {
            Parameter<String> result = query.registerResultParameter(String.class);
            query.registerParameter(1, Integer.class, ParameterMode.IN);
            query.setParameter(1, 1);
            query.execute();
            assertEquals("Procedure", query.getOutputParameterValue(result));
            assertEquals("Procedure", query.getOutputParameterValue(result));
        }
        try (EntityManager em = emf.createEntityManager();
             StoredProcedureQuery query = em.createStoredProcedureQuery("EA_QUERY_PROC_TITLE")) {
            Parameter<String> result = query.registerResultParameter(String.class);
            query.registerParameter(1, Integer.class, ParameterMode.IN);
            query.setParameter(1, 1);
            query.execute();
            assertEquals("Procedure", query.getOutputParameterValue(result));
        }
    }

    @Test
    public void ordinaryEntityManagerIdentityUsesItsStatementGeneratedKey() {
        AgentGeneratedIdentity first = new AgentGeneratedIdentity();
        first.name = "ordinary identity one";
        AgentGeneratedIdentity second = new AgentGeneratedIdentity();
        second.name = "ordinary identity two";
        emf.runInTransaction(em -> {
            em.persist(first);
            em.persist(second);
        });
        try {
            assertNotNull(first.id);
            assertNotNull(second.id);
            assertNotEquals(first.id, second.id);
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.runWithConnection((Connection connection) -> {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT NAME FROM EA_GV_IDENTITY WHERE ID = ?")) {
                        statement.setLong(1, first.id);
                        try (ResultSet result = statement.executeQuery()) {
                            assertTrue(result.next());
                            assertEquals(first.name, result.getString(1));
                            assertFalse(result.next());
                        }
                        statement.setLong(1, second.id);
                        try (ResultSet result = statement.executeQuery()) {
                            assertTrue(result.next());
                            assertEquals(second.name, result.getString(1));
                        }
                    }
                });
            }
        } finally {
            emf.runInTransaction(em -> {
                em.createStatement("delete from AgentGeneratedIdentity e where e.id = :first or e.id = :second")
                        .setParameter("first", first.id).setParameter("second", second.id).execute();
            });
        }
    }

    @Test
    public void procedureFailureReleasesResourcesAndPreservesOwnerUse() {
        try (EntityAgent agent = emf.createEntityAgent();
             StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_ABSENT")) {
            assertThrows(PersistenceException.class, query::execute);
            assertEquals("Procedure", agent.get(AgentQueryRecord.class, 1).title);
        }
    }

    private StoredProcedureQuery rows(EntityAgent agent) {
        StoredProcedureQuery query = agent.createStoredProcedureQuery("EA_QUERY_PROC_ROWS");
        query.registerParameter(1, Integer.class, ParameterMode.IN);
        return query.setParameter(1, 1);
    }

    public static ResultSet procedureRows(Connection connection, int id) throws SQLException {
        CachedRowSet rows = RowSetProvider.newFactory().createCachedRowSet();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM EA_QUERY_RECORD WHERE ID = ?")) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                rows.populate(result);
            }
        }
        return rows;
    }

    public static String procedureTitle(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT TITLE FROM EA_QUERY_RECORD WHERE ID = ?")) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString(1) : null;
            }
        }
    }

    public record ProcedureTitle(String title) {
    }
}
