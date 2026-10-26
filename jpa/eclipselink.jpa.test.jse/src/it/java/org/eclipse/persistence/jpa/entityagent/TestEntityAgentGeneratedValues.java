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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import java.sql.Connection;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedIdentity;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedIdentitySecond;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedOwner;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedSequence;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedTable;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedTarget;
import org.eclipse.persistence.jpa.entityagent.model.AgentGeneratedUuid;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentGeneratedValues {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentGeneratedTable.class, AgentGeneratedSequence.class,
                    AgentGeneratedIdentity.class, AgentGeneratedIdentitySecond.class, AgentGeneratedUuid.class,
                    AgentGeneratedOwner.class, AgentGeneratedTarget.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Emf(name = "agent-generated-no-binding", createTables = DDLGen.DROP_CREATE,
            classes = {AgentTarget.class, AgentGeneratedIdentity.class, AgentGeneratedIdentitySecond.class},
            properties = {
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.jdbc.bind-parameters", value = "false"),
                    @Property(name = "eclipselink.jdbc.batch-writing", value = "JDBC")})
    private EntityManagerFactory batchFactory;


    @Test
    public void tableIdsAreReturnedForSingleAndMultipleInserts() {
        verifyGeneratedInserts(AgentGeneratedTable.class, AgentGeneratedTable::new);
    }

    @Test
    public void sequenceIdsAreReturnedForSingleAndMultipleInserts() {
        verifyGeneratedInserts(AgentGeneratedSequence.class, AgentGeneratedSequence::new);
    }

    @Test
    public void identityIdsAreReturnedForSingleAndMultipleInserts() {
        verifyGeneratedInserts(AgentGeneratedIdentity.class, AgentGeneratedIdentity::new);
    }

    @Test
    public void uuidIdsAreReturnedForSingleAndMultipleInserts() {
        verifyGeneratedInserts(AgentGeneratedUuid.class, AgentGeneratedUuid::new);
    }

    private <T extends AgentGeneratedRecord> void verifyGeneratedInserts(Class<T> type, Supplier<T> constructor) {
        try (EntityAgent agent = emf.createEntityAgent()) {
            T first = constructor.get();
            first.name = "single";
            assertNull(first.identifier());
            agent.insert(first);
            assertNotNull(first.identifier());
            assertEquals(first.identifier(), first.postInsertId);
            assertEquals(List.of("pre-insert", "post-insert"), first.events);
            assertNotNull(first.version);
            T second = constructor.get();
            T third = constructor.get();
            second.name = "second";
            third.name = "third";
            agent.insertMultiple(List.of(second, third));
            assertNotNull(second.identifier());
            assertNotNull(third.identifier());
            assertNotEquals(first.identifier(), second.identifier());
            assertNotEquals(second.identifier(), third.identifier());
            assertEquals(second.identifier(), second.postInsertId);
            assertEquals(third.identifier(), third.postInsertId);
            assertEquals("single", agent.get(type, first.identifier()).name);
            assertEquals("second", agent.get(type, second.identifier()).name);
            assertEquals("third", agent.get(type, third.identifier()).name);
            agent.deleteMultiple(List.of(first, second, third));
        }
    }

    @Test
    public void upsertPreservesAssignedIdsOnGeneratedMappings() {
        verifyAssignedUpsert(AgentGeneratedTable.class, AgentGeneratedTable::new, 900001L);
        verifyAssignedUpsert(AgentGeneratedSequence.class, AgentGeneratedSequence::new, 900002L);
        verifyAssignedUpsert(AgentGeneratedIdentity.class, AgentGeneratedIdentity::new, 900003L);
        verifyAssignedUpsert(AgentGeneratedUuid.class, AgentGeneratedUuid::new,
                UUID.fromString("5106f26e-53df-4d65-8e80-f9f170fd6e52"));
    }

    private <T extends AgentGeneratedRecord> void verifyAssignedUpsert(Class<T> type, Supplier<T> constructor, Object id) {
        try (EntityAgent agent = emf.createEntityAgent()) {
            T missingId = constructor.get();
            assertThrows(IllegalArgumentException.class, () -> agent.upsert(missingId));
            assertNull(missingId.identifier());
            assertTrue(missingId.events.isEmpty());
            T record = constructor.get();
            record.assignIdentifier(id);
            record.name = "imported";
            agent.upsert(record);
            assertEquals(id, record.identifier());
            assertEquals(List.of("pre-upsert", "post-upsert"), record.events);
            assertEquals("imported", agent.get(type, id).name);
            Integer initialVersion = record.version;
            assertNotNull(initialVersion);
            record.events.clear();
            record.name = "changed";
            agent.upsert(record);
            assertEquals(id, record.identifier());
            assertEquals(List.of("pre-upsert", "post-upsert"), record.events);
            assertTrue(record.version > initialVersion);
            T loaded = agent.get(type, id);
            assertEquals(record.version, loaded.version);
            assertEquals("changed", loaded.name);
            agent.delete(record);
        }
    }

    @Test
    public void twoIdentityTablesShareATransactionWithoutLeakingInsertSettings() {
        AgentGeneratedIdentity first = new AgentGeneratedIdentity();
        first.id = 910001L;
        first.name = "assigned first";
        AgentGeneratedIdentitySecond second = new AgentGeneratedIdentitySecond();
        second.id = 920001L;
        second.name = "assigned second";
        AgentGeneratedIdentity generatedFirst = new AgentGeneratedIdentity();
        generatedFirst.name = "generated first";
        AgentGeneratedIdentitySecond generatedSecond = new AgentGeneratedIdentitySecond();
        generatedSecond.name = "generated second";
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            Connection connection = agent.callWithConnection(value -> (Connection) value);
            agent.upsert(first);
            agent.upsert(second);
            agent.insert(generatedFirst);
            agent.insert(generatedSecond);
            assertSame(connection, agent.callWithConnection(value -> value));
            assertEquals(910001L, first.id.longValue());
            assertEquals(920001L, second.id.longValue());
            assertNotNull(generatedFirst.id);
            assertNotNull(generatedSecond.id);
            assertNotEquals(first.id, generatedFirst.id);
            assertNotEquals(second.id, generatedSecond.id);
            agent.getTransaction().commit();
            assertEquals("assigned first", agent.get(AgentGeneratedIdentity.class, first.id).name);
            assertEquals("assigned second", agent.get(AgentGeneratedIdentitySecond.class, second.id).name);
            agent.deleteMultiple(List.of(first, second, generatedFirst, generatedSecond));
        }
        AgentGeneratedIdentity ordinary = new AgentGeneratedIdentity();
        ordinary.name = "ordinary generated after upsert";
        try (var manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            manager.persist(ordinary);
            manager.getTransaction().commit();
        }
        assertNotNull(ordinary.id);
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEquals(ordinary.name, agent.get(AgentGeneratedIdentity.class, ordinary.id).name);
            agent.delete(ordinary);
        }
    }

    @Test
    public void batchConfigurationKeepsAgentSqlImmediateAndIdentityValuesBound() {
        try (EntityAgent agent = batchFactory.createEntityAgent()) {
            ClientSession client = agent.unwrap(ClientSession.class);
            int[] scopedIdentityCalls = {0};
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void postExecuteCall(SessionEvent event) {
                    if (event.getSession() == client && event.getCall() instanceof DatabaseCall call
                            && call.getIdentityInsertTable() != null) {
                        assertTrue("Identity scope must preserve JDBC parameter markers", call.getSQLString().contains("?"));
                        assertTrue("The driver must receive actual bind values", call.hasParameters());
                        assertTrue(call.usesBinding(client));
                        scopedIdentityCalls[0]++;
                    }
                }
            });
            agent.getTransaction().begin();
            AgentTarget target = new AgentTarget(950001L, "inserted immediately");
            agent.insert(target);
            assertEquals("inserted immediately", rawLabel(agent, target.id));
            target.label = "updated immediately";
            agent.update(target);
            assertEquals("updated immediately", rawLabel(agent, target.id));
            assertEquals(1, agent.createQuery("update AgentTarget t set t.label = :label where t.id = :id")
                    .setParameter("label", "bulk immediately").setParameter("id", target.id).executeUpdate());
            assertEquals("bulk immediately", rawLabel(agent, target.id));
            agent.delete(target);
            assertNull(rawLabel(agent, target.id));
            AgentGeneratedIdentity first = new AgentGeneratedIdentity();
            first.id = 960001L;
            first.name = "bound assigned first";
            AgentGeneratedIdentitySecond second = new AgentGeneratedIdentitySecond();
            second.id = 970001L;
            second.name = "bound assigned second";
            agent.upsert(first);
            agent.upsert(second);
            if (client.getPlatform().isSQLServer()) {
                assertEquals("Both actual identity inserts must use prepared scope", 2, scopedIdentityCalls[0]);
            }
            agent.<Connection>runWithConnection(connection -> {
                try (var statement = connection.createStatement()) {
                    try (var rows = statement.executeQuery("SELECT NAME FROM EA_GV_IDENTITY WHERE ID = 960001")) {
                        assertTrue(rows.next());
                        assertEquals(first.name, rows.getString(1));
                    }
                    try (var rows = statement.executeQuery("SELECT NAME FROM EA_GV_IDENTITY_SECOND WHERE ID = 970001")) {
                        assertTrue(rows.next());
                        assertEquals(second.name, rows.getString(1));
                    }
                }
            });
            agent.getTransaction().rollback();
            assertNull(agent.find(AgentTarget.class, target.id));
            assertNull(agent.find(AgentGeneratedIdentity.class, first.id));
            assertNull(agent.find(AgentGeneratedIdentitySecond.class, second.id));
        }
    }

    private String rawLabel(EntityAgent agent, long id) {
        return agent.<Connection, String>callWithConnection(connection -> {
            try (var statement = connection.prepareStatement("SELECT LABEL FROM EA_TARGET WHERE ID = ?")) {
                statement.setLong(1, id);
                try (var rows = statement.executeQuery()) {
                    return rows.next() ? rows.getString(1) : null;
                }
            }
        });
    }

    @Test
    public void failedAssignedIdentityInsertLeavesGeneratedWorkUsable() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentGeneratedIdentity invalid = new AgentGeneratedIdentity();
            invalid.id = 930001L;
            assertThrows(PersistenceException.class, () -> agent.upsert(invalid));
            assertEquals(930001L, invalid.id.longValue());
            assertNull(invalid.version);
            assertEquals(List.of("pre-upsert"), invalid.events);
            assertNull(agent.find(AgentGeneratedIdentity.class, invalid.id));
            AgentGeneratedIdentity valid = new AgentGeneratedIdentity();
            valid.name = "generated after failed assigned insert";
            agent.insert(valid);
            assertNotNull(valid.id);
            assertEquals(valid.name, agent.get(AgentGeneratedIdentity.class, valid.id).name);
            agent.delete(valid);
        }
    }

    @Test
    public void entityCascadesDoNotApplyToAgentOwnedRelationshipWrites() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentGeneratedTarget target = new AgentGeneratedTarget(901001L, "independent");
            agent.insert(target);
            AgentGeneratedOwner owner = new AgentGeneratedOwner();
            owner.id = 901002L;
            owner.name = "owner";
            owner.target = target;
            owner.targets.add(target);
            target.name = "must-not-cascade-on-insert";
            agent.insert(owner);
            assertEquals("independent", agent.get(AgentGeneratedTarget.class, target.id).name);
            var graph = agent.createEntityGraph(AgentGeneratedOwner.class);
            graph.addAttributeNodes("target", "targets");
            AgentGeneratedOwner loaded = agent.get(graph, owner.id);
            assertEquals(target.id, loaded.target.id);
            assertEquals(1, loaded.targets.size());
            loaded.target.name = "must-not-cascade-on-update";
            loaded.name = "updated-owner";
            loaded.targets.clear();
            agent.update(loaded);
            assertEquals("independent", agent.get(AgentGeneratedTarget.class, target.id).name);
            assertTrue(agent.get(graph, owner.id).targets.isEmpty());
            agent.delete(loaded);
            assertNotNull(agent.find(AgentGeneratedTarget.class, target.id));
            agent.delete(target);
        }
    }
}
