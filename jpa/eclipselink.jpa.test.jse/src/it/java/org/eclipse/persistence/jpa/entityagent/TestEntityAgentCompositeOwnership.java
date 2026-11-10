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

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import org.eclipse.persistence.jpa.entityagent.model.AgentCompositeIdClassRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentCompositeKey;
import org.eclipse.persistence.jpa.entityagent.model.AgentCompositeRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentOwnedChild;
import org.eclipse.persistence.jpa.entityagent.model.AgentOwnedParent;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

@RunWith(EmfRunner.class)
public class TestEntityAgentCompositeOwnership {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentCompositeKey.class, AgentCompositeRecord.class, AgentCompositeIdClassRecord.class,
                    AgentOwnedParent.class, AgentOwnedChild.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createNativeStatement("update EA_OWNED_CHILD set OWNED_PARENT_ID = null, INVERSE_PARENT_ID = null").execute();
            em.createStatement("delete from AgentOwnedChild").execute();
            em.createStatement("delete from AgentOwnedParent").execute();
            em.createStatement("delete from AgentCompositeRecord").execute();
            em.createStatement("delete from AgentCompositeIdClassRecord").execute();
        });
    }

    @Test
    public void embeddedCompositeKeySupportsImmediateCrudAndUpsert() {
        AgentCompositeKey key = new AgentCompositeKey(1, "first");
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent observer = emf.createEntityAgent()) {
            AgentCompositeRecord record = new AgentCompositeRecord(key, "inserted");
            agent.insert(record);
            assertEquals("inserted", observer.get(AgentCompositeRecord.class, new AgentCompositeKey(1, "first")).label);
            Integer firstVersion = record.version;
            record.label = "updated";
            agent.update(record);
            assertEquals(Integer.valueOf(firstVersion + 1), record.version);
            assertEquals("updated", observer.find(AgentCompositeRecord.class, key).label);
            record.label = "upserted";
            agent.upsert(record);
            assertEquals("upserted", observer.get(AgentCompositeRecord.class, key).label);
            agent.delete(record);
            assertNull(observer.find(AgentCompositeRecord.class, key));
            AgentCompositeRecord missing = new AgentCompositeRecord(new AgentCompositeKey(1, "second"), "new upsert");
            agent.upsert(missing);
            assertEquals("new upsert", observer.get(AgentCompositeRecord.class, missing.id).label);
        }
    }

    @Test
    public void embeddedCompositeMultipleLookupsPreservePositionAndMissingRows() {
        AgentCompositeKey first = new AgentCompositeKey(2, "first");
        AgentCompositeKey second = new AgentCompositeKey(2, "second");
        AgentCompositeKey missing = new AgentCompositeKey(2, "missing");
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.insertMultiple(List.of(new AgentCompositeRecord(first, "first"), new AgentCompositeRecord(second, "second")));
            var found = agent.findMultiple(AgentCompositeRecord.class, List.of(second, missing, first, first));
            assertEquals(4, found.size());
            assertEquals("second", found.get(0).label);
            assertNull(found.get(1));
            assertEquals("first", found.get(2).label);
            assertEquals("first", found.get(3).label);
            assertEquals(List.of("second", "first", "first"), agent.getMultiple(AgentCompositeRecord.class,
                    List.of(second, first, first)).stream().map(record -> record.label).toList());
            assertThrows(EntityNotFoundException.class, () -> agent.getMultiple(AgentCompositeRecord.class,
                    List.of(first, missing)));
            assertThrows(IllegalArgumentException.class, () -> agent.find(AgentCompositeRecord.class, 2));
        }
    }

    @Test
    public void idClassCompositeKeysSupportCrudAndMultipleLookup() {
        AgentCompositeKey key = new AgentCompositeKey(3, "key");
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCompositeIdClassRecord record = new AgentCompositeIdClassRecord(3, "key", "inserted");
            agent.insert(record);
            assertEquals("inserted", agent.find(AgentCompositeIdClassRecord.class, key).label);
            record.label = "updated";
            agent.update(record);
            assertEquals("updated", agent.getMultiple(AgentCompositeIdClassRecord.class, List.of(key)).get(0).label);
            record.label = "upserted";
            agent.upsert(record);
            assertEquals("upserted", agent.get(AgentCompositeIdClassRecord.class, key).label);
            agent.delete(record);
            assertNull(agent.find(AgentCompositeIdClassRecord.class, key));
        }
    }

    @Test
    public void owningForeignKeyInsertLinksExistingChildrenWithoutTargetWrites() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentOwnedChild child = new AgentOwnedChild(101, "stored child");
            agent.insert(child);
            Integer childVersion = child.version;
            child.businessName = "must not cascade";
            AgentOwnedParent parent = new AgentOwnedParent(101, "parent");
            parent.children.add(child);
            agent.insert(parent);
            assertTarget(agent, 101, "stored child", childVersion, 101L);
            AgentOwnedParent stored = agent.get(AgentOwnedParent.class, 101L);
            agent.fetch(stored.children);
            assertEquals(1, stored.children.size());
        }
    }

    @Test
    public void owningForeignKeyUpdateAddsAndRemovesLinksWithoutChangingTargets() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentOwnedChild first = new AgentOwnedChild(201, "first");
            AgentOwnedChild second = new AgentOwnedChild(202, "second");
            agent.insertMultiple(List.of(first, second));
            Integer firstVersion = first.version;
            Integer secondVersion = second.version;
            AgentOwnedParent parent = new AgentOwnedParent(201, "parent");
            parent.children.add(first);
            agent.insert(parent);
            parent.children.clear();
            second.businessName = "must not cascade";
            parent.children.add(second);
            agent.update(parent);
            assertTarget(agent, 201, "first", firstVersion, null);
            assertTarget(agent, 202, "second", secondVersion, 201L);
        }
    }

    @Test
    public void owningForeignKeyDeleteUnlinksWithoutCascadeDelete() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentOwnedChild child = new AgentOwnedChild(301, "survives");
            agent.insert(child);
            Integer childVersion = child.version;
            AgentOwnedParent parent = new AgentOwnedParent(301, "parent");
            parent.children.add(child);
            agent.insert(parent);
            agent.delete(parent);
            assertNull(agent.find(AgentOwnedParent.class, 301L));
            assertTarget(agent, 301, "survives", childVersion, null);
        }
    }

    @Test
    public void inverseCollectionsDoNotChangeOwningChildForeignKeyOrBusinessState() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentOwnedParent first = new AgentOwnedParent(401, "first parent");
            agent.insert(first);
            AgentOwnedChild child = new AgentOwnedChild(401, "stored child");
            child.inverseParent = first;
            agent.insert(child);
            Integer childVersion = child.version;
            first.inverseChildren.clear();
            agent.update(first);
            child.businessName = "must not cascade";
            AgentOwnedParent second = new AgentOwnedParent(402, "second parent");
            second.inverseChildren.add(child);
            agent.insert(second);
            agent.update(second);
            agent.delete(second);
            AgentOwnedChild stored = agent.get(AgentOwnedChild.class, 401L);
            assertEquals("stored child", stored.businessName);
            assertEquals(childVersion, stored.version);
            assertNotNull(stored.inverseParent);
            assertEquals(Long.valueOf(401), stored.inverseParent.id);
            assertEquals(Long.valueOf(401), foreignKey(agent, 401, "INVERSE_PARENT_ID"));
        }
    }

    @Test
    public void ownedForeignKeyChangesRollbackWithTheParentUpdate() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent observer = emf.createEntityAgent()) {
            AgentOwnedChild first = new AgentOwnedChild(501, "first");
            AgentOwnedChild second = new AgentOwnedChild(502, "second");
            agent.insertMultiple(List.of(first, second));
            Integer firstVersion = first.version;
            Integer secondVersion = second.version;
            AgentOwnedParent parent = new AgentOwnedParent(501, "before");
            parent.children.add(first);
            agent.insert(parent);
            Integer parentVersion = parent.version;
            agent.getTransaction().begin();
            parent.name = "rolled back";
            parent.children.clear();
            parent.children.add(second);
            agent.update(parent);
            assertEquals(Long.valueOf(501), foreignKey(agent, 502, "OWNED_PARENT_ID"));
            assertNull(foreignKey(agent, 501, "OWNED_PARENT_ID"));
            agent.getTransaction().rollback();
            AgentOwnedParent stored = observer.get(AgentOwnedParent.class, 501L);
            assertEquals("before", stored.name);
            assertEquals(parentVersion, stored.version);
            assertTarget(observer, 501, "first", firstVersion, 501L);
            assertTarget(observer, 502, "second", secondVersion, null);
        }
    }

    private void assertTarget(EntityAgent agent, long id, String businessName, Integer version, Long owner) {
        AgentOwnedChild stored = agent.get(AgentOwnedChild.class, id);
        assertEquals(businessName, stored.businessName);
        assertEquals(version, stored.version);
        assertEquals(owner, foreignKey(agent, id, "OWNED_PARENT_ID"));
    }

    private Long foreignKey(EntityAgent agent, long id, String column) {
        if (!column.equals("OWNED_PARENT_ID") && !column.equals("INVERSE_PARENT_ID")) {
            throw new IllegalArgumentException("Unknown fixture foreign key");
        }
        Object value = agent.createNativeQuery("select " + column + " from EA_OWNED_CHILD where ID = ?1")
                .setParameter(1, id).getSingleResult();
        return value == null ? null : ((Number) value).longValue();
    }
}
