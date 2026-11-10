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
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.persistence.jpa.entityagent.model.AgentNamedOptionsRecord;
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

@RunWith(EmfRunner.class)
public class TestEntityAgentNamedQueryOptionLocks {
    @Emf(name = "agent-named-option-locks", createTables = DDLGen.DROP_CREATE,
            classes = AgentNamedOptionsRecord.class, mappingFiles = "META-INF/entityagent-named-options.xml",
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;
    private boolean supported;
    private boolean postgresql;

    @Before
    public void seed() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            String product = agent.callWithConnection((Connection connection) -> connection.getMetaData().getDatabaseProductName());
            postgresql = "PostgreSQL".equals(product);
            supported = postgresql || "H2".equals(product);
            assumeTrue("Requires the owned H2/PostgreSQL lock-timeout fixture", supported);
        }
        TestEntityAgentNamedQueryOptions.seed(emf);
    }

    @After public void cleanup() { if (supported) TestEntityAgentNamedQueryOptions.clear(emf); }

    private void contend(boolean locked) {
        try (EntityAgent contender = emf.createEntityAgent()) {
            contender.getTransaction().begin();
            contender.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    statement.execute(postgresql ? "SET LOCAL lock_timeout='200ms'" : "SET LOCK_TIMEOUT 200");
                }
            });
            Runnable change = () -> contender.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    assertEquals(1, statement.executeUpdate("UPDATE EA_NAMED_TAG SET TAG_VALUE='changed' WHERE OWNER_ID=1"));
                }
            });
            if (locked) {
                Throwable failure = assertThrows(PersistenceException.class, change::run);
                while (failure != null && !(failure instanceof SQLException)) failure = failure.getCause();
                assertNotNull("Actual JDBC lock timeout required", failure);
                SQLException database = (SQLException) failure;
                assertTrue(postgresql ? "55P03".equals(database.getSQLState()) : database.getErrorCode() == 50200);
            } else {
                change.run();
            }
            contender.getTransaction().rollback();
        }
    }

    private void execute(EntityHandler owner, String name, boolean override) {
        var query = owner.createNamedQuery(name, AgentNamedOptionsRecord.class);
        if (override) query.setLockScope(PessimisticLockScope.NORMAL);
        assertEquals(override || name.endsWith("Hint") ? PessimisticLockScope.NORMAL : PessimisticLockScope.EXTENDED,
                query.getLockScope());
        query.getSingleResult();
        contend(!override && !name.endsWith("Hint"));
    }

    @Test
    public void agentDeclarationLocksOwnedRowsAndExplicitOverrideIsIndependent() {
        for (String name : new String[] {"EANamed.extended", "EANamed.xmlExtended"}) {
            try (EntityAgent owner = emf.createEntityAgent()) {
                owner.getTransaction().begin();
                execute(owner, name, false);
                owner.getTransaction().rollback();
                contend(false);
                owner.getTransaction().begin();
                execute(owner, name, true);
                owner.getTransaction().rollback();
                owner.getTransaction().begin();
                execute(owner, name, false);
                owner.getTransaction().rollback();
            }
        }
    }

    @Test
    public void managerDeclarationLocksOwnedRowsAndExplicitOverrideIsIndependent() {
        for (String name : new String[] {"EANamed.extended", "EANamed.xmlExtended"}) {
            try (EntityManager owner = emf.createEntityManager()) {
                try {
                    owner.getTransaction().begin();
                    execute(owner, name, false);
                    owner.getTransaction().rollback();
                    contend(false);
                    owner.clear();
                    owner.getTransaction().begin();
                    execute(owner, name, true);
                    owner.getTransaction().rollback();
                } finally {
                    if (owner.getTransaction().isActive()) owner.getTransaction().rollback();
                }
            }
        }
    }

    @Test
    public void explicitDeclaredScopeHintsOverrideAnnotationAndXmlDefaults() {
        for (String name : new String[] {"EANamed.scopeHint", "EANamed.xmlScopeHint"}) {
            try (EntityAgent owner = emf.createEntityAgent()) {
                owner.getTransaction().begin();
                execute(owner, name, false);
                owner.getTransaction().rollback();
            }
            try (EntityManager owner = emf.createEntityManager()) {
                try {
                    owner.getTransaction().begin();
                    execute(owner, name, false);
                } finally {
                    if (owner.getTransaction().isActive()) owner.getTransaction().rollback();
                }
            }
        }
    }
}
