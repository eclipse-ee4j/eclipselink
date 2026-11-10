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
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Timeout;
import jakarta.persistence.Tuple;
import jakarta.persistence.EntityManagerFactory;

import java.sql.Connection;
import java.util.List;
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

import static jakarta.persistence.sql.ResultSetMapping.*;
import static org.junit.Assume.assumeTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentProcedureCallableClosure {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    private String alias;
    private boolean owned;

    @Before
    public void createOwnedAlias() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assumeTrue("This fixture uses an H2 Java alias", agent.callWithConnection(
                    (Connection connection) -> "H2".equals(connection.getMetaData().getDatabaseProductName())));
            alias = "EA_CALLABLE_ROWS_" + UUID.randomUUID().toString().replace("-", "");
            agent.runWithConnection((Connection connection) -> {
                try (var query = connection.prepareStatement(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_NAME = ?")) {
                    query.setString(1, alias.toUpperCase(java.util.Locale.ROOT));
                    try (var result = query.executeQuery()) {
                        assertTrue(result.next());
                        assertEquals(0, result.getInt(1));
                    }
                }
                try (var statement = connection.createStatement()) {
                    statement.execute("CREATE ALIAS " + alias + " FOR \""
                            + TestEntityAgentQueryProcedures.class.getName() + ".procedureRows\"");
                    owned = true;
                }
            });
        }
        emf.runInTransaction(em -> em.persist(new AgentQueryRecord(1, "Procedure", null)));
    }

    @After
    public void removeOwnedAlias() {
        if (owned) {
            try (EntityAgent agent = emf.createEntityAgent()) {
                agent.runWithConnection((Connection connection) -> {
                    try (var statement = connection.createStatement()) {
                        statement.execute("DROP ALIAS " + alias);
                    }
                });
            }
            emf.runInTransaction(em -> em.createStatement("delete from AgentQueryRecord").execute());
        }
    }

    @Test
    public void nullableAndListAdaptersApplyMappingsAndProcedureOptions() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            try (StoredProcedureQuery query = rows(agent, 1)) {
                assertSame(query, query.addOption(Timeout.seconds(10)));
                assertTrue(query.getOptions().contains(Timeout.seconds(10)));
                AgentQueryRecord result = query.getSingleResultOrNull(AgentQueryRecord.class);
                assertNotNull(result);
                assertEquals("Procedure", result.title);
                result.setTitle("Detached nullable procedure");
                assertEquals("Procedure", agent.get(AgentQueryRecord.class, 1).title);
            }
            try (StoredProcedureQuery query = rows(agent, 99)) {
                assertNull(query.getSingleResultOrNull(AgentQueryRecord.class));
            }
            var mapping = tuple(column("ID", Integer.class), column("TITLE", String.class));
            try (StoredProcedureQuery query = rows(agent, 1)) {
                Tuple result = query.getSingleResultOrNull(mapping);
                assertNotNull(result);
                assertEquals(1, result.get(0));
                assertEquals("Procedure", result.get(1));
            }
            try (StoredProcedureQuery query = rows(agent, 99)) {
                assertNull(query.getSingleResultOrNull(mapping));
            }
            try (StoredProcedureQuery query = rows(agent, 1)) {
                List<Tuple> results = query.getResultList(mapping);
                assertEquals(1, results.size());
                assertEquals(1, results.get(0).get(0));
                assertEquals("Procedure", results.get(0).get(1));
            }
            try (StoredProcedureQuery query = rows(agent, 99)) {
                assertTrue(query.getResultList(mapping).isEmpty());
            }
        }
    }

    private StoredProcedureQuery rows(EntityAgent agent, int id) {
        StoredProcedureQuery query = agent.createStoredProcedureQuery(alias);
        query.registerParameter(1, Integer.class, ParameterMode.IN);
        query.setParameter(1, id);
        return query;
    }
}
