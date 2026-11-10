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

import java.sql.Connection;

import org.eclipse.persistence.indirection.IndirectContainer;
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

@RunWith(EmfRunner.class)
public class TestEntityAgentNamedQueryOptions {
    @Emf(name = "agent-named-options", createTables = DDLGen.DROP_CREATE,
            classes = AgentNamedOptionsRecord.class, mappingFiles = "META-INF/entityagent-named-options.xml",
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    static void seed(EntityManagerFactory factory) {
        clear(factory);
        try (EntityAgent agent = factory.createEntityAgent()) {
            AgentNamedOptionsRecord row = new AgentNamedOptionsRecord();
            row.id = 1;
            row.name = "named";
            row.tags.add("tag");
            row.notes.add("note");
            agent.insert(row);
        }
    }

    static void clear(EntityManagerFactory factory) {
        try (EntityAgent agent = factory.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.createStatement()) {
                    for (String table : new String[] {"EA_NAMED_TAG", "EA_NAMED_NOTE", "EA_NAMED_OPTIONS"}) {
                        statement.executeUpdate("DELETE FROM " + table);
                    }
                }
            });
        }
    }

    @Before public void seed() { seed(emf); }
    @After public void clear() { clear(emf); }

    private void assertLoaded(AgentNamedOptionsRecord row, boolean tags, boolean notes) {
        assertEquals(tags, emf.getPersistenceUnitUtil().isLoaded(row, "tags"));
        assertEquals(notes, emf.getPersistenceUnitUtil().isLoaded(row, "notes"));
        // This is the actual collection holder at the return boundary, before navigation.
        assertHolder(row, "tags", row.tags, tags);
        assertHolder(row, "notes", row.notes, notes);
    }

    private void assertHolder(AgentNamedOptionsRecord row, String attribute, Object value, boolean loaded) {
        if (value instanceof IndirectContainer container) {
            assertEquals(loaded, container.getValueHolder().isInstantiated());
        } else {
            // A FETCH graph may omit the field entirely; its woven fetch-group
            // tracker, rather than a collection holder, represents that state.
            assertFalse(loaded);
            assertTrue(row instanceof org.eclipse.persistence.queries.FetchGroupTracker);
            var group = ((org.eclipse.persistence.queries.FetchGroupTracker) row)._persistence_getFetchGroup();
            assertNotNull(group);
            assertFalse(group.containsAttribute(attribute));
        }
    }

    @Test
    public void annotationAndXmlLoadGraphsApplyToManagedAndDetachedQueries() {
        try (EntityManager manager = emf.createEntityManager()) {
            AgentNamedOptionsRecord control = manager.createQuery("select r from AgentNamedOptionsRecord r", AgentNamedOptionsRecord.class).getSingleResult();
            assertLoaded(control, false, false);
        }
        for (String name : new String[] {"EANamed.graph", "EANamed.xmlGraph"}) {
            try (EntityManager manager = emf.createEntityManager()) {
                AgentNamedOptionsRecord row = manager.createNamedQuery(name, AgentNamedOptionsRecord.class).getSingleResult();
                assertTrue(manager.contains(row));
                assertLoaded(row, true, false);
                assertEquals("tag", row.tags.get(0));
            }
            AgentNamedOptionsRecord row;
            try (EntityAgent agent = emf.createEntityAgent()) {
                row = agent.createNamedQuery(name, AgentNamedOptionsRecord.class).getSingleResult();
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(row, "tags"));
                row.tags.set(0, "detached");
            }
            assertEquals("detached", row.tags.get(0));
            try (EntityManager manager = emf.createEntityManager()) {
                assertEquals("tag", manager.createNamedQuery(name, AgentNamedOptionsRecord.class).getSingleResult().tags.get(0));
            }
        }
    }

    @Test
    public void queryGraphOverrideDoesNotMutateTheNamedDeclaration() {
        for (String name : new String[] {"EANamed.graph", "EANamed.xmlGraph"}) {
            try (EntityManager manager = emf.createEntityManager()) {
                AgentNamedOptionsRecord row = manager.createNamedQuery(name)
                        .withEntityGraph(manager.getEntityGraph(AgentNamedOptionsRecord.class, "EANamed.notes")).getSingleResult();
                assertLoaded(row, false, true);
            }
            try (EntityManager manager = emf.createEntityManager()) {
                assertLoaded(manager.createNamedQuery(name, AgentNamedOptionsRecord.class).getSingleResult(), true, false);
            }
            try (EntityAgent agent = emf.createEntityAgent()) {
                AgentNamedOptionsRecord row = agent.createNamedQuery(name)
                        .withEntityGraph(agent.getEntityGraph(AgentNamedOptionsRecord.class, "EANamed.notes")).getSingleResult();
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(row, "notes"));
                assertEquals("note", row.notes.get(0));
                assertEquals("EANamed.tags", agent.createNamedQuery(name).getHints().get("jakarta.persistence.loadgraph"));
            }
        }
    }

    @Test
    public void explicitAnnotationAndXmlGraphHintsTakePrecedence() {
        for (String name : new String[] {"EANamed.graphHint", "EANamed.xmlGraphHint", "EANamed.fetchHint"}) {
            try (EntityManager manager = emf.createEntityManager()) {
                assertLoaded(manager.createNamedQuery(name, AgentNamedOptionsRecord.class).getSingleResult(), false, true);
            }
            try (EntityAgent agent = emf.createEntityAgent()) {
                var query = agent.createNamedQuery(name, AgentNamedOptionsRecord.class);
                assertFalse(query.getHints().containsValue("EANamed.tags"));
                assertEquals("note", query.getSingleResult().notes.get(0));
            }
        }
    }
}
