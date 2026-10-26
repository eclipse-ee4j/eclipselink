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
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.EntityManagerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
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

/**
 * PostgreSQL JDBC lacks named CallableStatement methods. The existing provider
 * option binds registered names by index; registration must follow declaration order.
 */
@RunWith(EmfRunner.class)
public class TestEntityAgentNamedProcedureCallableClosure {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jpa.naming_into_indexed", value = "true"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    private String procedure;
    private boolean owned;

    @Before
    public void createOwnedProcedure() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assumeTrue("This fixture uses PostgreSQL OUT and INOUT parameters", agent.callWithConnection(
                    (Connection connection) -> "PostgreSQL".equals(connection.getMetaData().getDatabaseProductName())));
            procedure = "ea_named_callable_" + UUID.randomUUID().toString().replace("-", "");
            agent.runWithConnection((Connection connection) -> {
                assertFalse(routineExists(connection, procedure));
                try (var statement = connection.createStatement()) {
                    statement.execute("CREATE PROCEDURE " + procedure
                            + "(IN input_id integer, INOUT value_text varchar, OUT doubled integer, OUT out_text varchar) "
                            + "LANGUAGE plpgsql AS $$ BEGIN value_text := value_text || input_id::text; "
                            + "doubled := input_id * 2; out_text := 'out-' || input_id::text; END $$");
                    owned = true;
                }
            });
        }
    }

    @After
    public void removeOwnedProcedure() {
        if (owned) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.runWithConnection((Connection connection) -> {
                    try (var statement = connection.createStatement()) {
                        statement.execute("DROP PROCEDURE " + procedure + "(integer, character varying)");
                    }
                    assertFalse(routineExists(connection, procedure));
                });
            }
        }
    }

    @Test
    public void namedRegistrationsBindInputsAndReadActualOutputs() {
        try (EntityAgent agent = emf.createEntityAgent();
             StoredProcedureQuery query = agent.createStoredProcedureQuery(procedure)) {
            Parameter<Integer> input = query.registerParameter("input_id", Integer.class, ParameterMode.IN);
            assertSame(query, query.registerStoredProcedureParameter("value_text", String.class, ParameterMode.INOUT));
            query.registerStoredProcedureParameter("doubled", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("out_text", String.class, ParameterMode.OUT);
            assertEquals("input_id", input.getName());
            assertNull(input.getPosition());
            query.setParameter(input, 7).setParameter("value_text", "named-");
            agent.getTransaction().begin();
            query.execute();
            assertEquals("named-7", query.getOutputParameterValue("value_text"));
            assertEquals(14, query.getOutputParameterValue("doubled"));
            assertEquals(14, query.getOutputParameterValue(3));
            assertEquals(Integer.valueOf(14), query.getOutputParameterValue(query.getParameter("doubled", Integer.class)));
            assertEquals("out-7", query.getOutputParameterValue("out_text"));
            agent.getTransaction().commit();
            agent.runWithConnection((Connection connection) -> {
                assertFalse(connection.isClosed());
                try (var statement = connection.createStatement(); var result = statement.executeQuery("SELECT 1")) {
                    assertTrue(result.next());
                    assertEquals(1, result.getInt(1));
                }
            });
        }
        try (var em = emf.createEntityManager(); StoredProcedureQuery query = em.createStoredProcedureQuery(procedure)) {
            Parameter<Integer> input = query.registerParameter("input_id", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("value_text", String.class, ParameterMode.INOUT);
            query.registerStoredProcedureParameter("doubled", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("out_text", String.class, ParameterMode.OUT);
            assertEquals("input_id", input.getName());
            assertNull(input.getPosition());
            query.setParameter(input, 8).setParameter("value_text", "manager-");
            em.getTransaction().begin();
            query.execute();
            assertEquals("manager-8", query.getOutputParameterValue("value_text"));
            assertEquals(16, query.getOutputParameterValue("doubled"));
            assertEquals(16, query.getOutputParameterValue(3));
            assertEquals("out-8", query.getOutputParameterValue("out_text"));
            em.getTransaction().rollback();
        }
    }

    private static boolean routineExists(Connection connection, String name) throws SQLException {
        try (var query = connection.prepareStatement("SELECT COUNT(*) FROM pg_catalog.pg_proc P "
                + "JOIN pg_catalog.pg_namespace N ON P.pronamespace=N.oid "
                + "WHERE N.nspname=current_schema() AND P.proname=?")) {
            query.setString(1, name);
            try (var result = query.executeQuery()) {
                assertTrue(result.next());
                return result.getInt(1) != 0;
            }
        }
    }
}
