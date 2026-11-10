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

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.entityagent.model.AgentLazyNode;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeferredOwner;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeferredTarget;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeferredLeaf;
import org.eclipse.persistence.jpa.entityagent.model.AgentRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentSpecialRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentTarget;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(EmfRunner.class)
public class TestEntityAgentDetachedGraphs {
    @Emf(name = "agent-bounded-detached-graphs", createTables = DDLGen.DROP_CREATE,
            classes = {AgentLazyNode.class, AgentRecord.class, AgentSpecialRecord.class, AgentTarget.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Emf(name = "agent-deferred-custom-reference", createTables = DDLGen.DROP_CREATE,
            classes = {AgentDeferredOwner.class, AgentDeferredTarget.class, AgentDeferredLeaf.class},
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.session.customizer", value = "org.eclipse.persistence.jpa.entityagent.TestEntityAgentDetachedGraphs$CustomSelection")})
    private EntityManagerFactory custom;

    public static class CustomSelection implements org.eclipse.persistence.sessions.SessionCustomizer {
        @Override
        public void customize(org.eclipse.persistence.sessions.Session session) {
            var mapping = (org.eclipse.persistence.mappings.OneToOneMapping) session.getDescriptor(AgentDeferredOwner.class)
                    .getMappingForAttributeName("favorite");
            mapping.setSelectionSQLString("SELECT ID, LABEL, OWNER_ID, DTYPE, DETAIL FROM EA_DEFER_TARGET WHERE ID = #EA_DEFER_OWNER.FAVORITE_ID");
        }
    }

    @Test
    public void inverseAndCustomPolymorphicReferencesResolveIdentityWithoutPostLoad() {
        try (EntityAgent setup = custom.createEntityAgent()) {
            AgentDeferredOwner owner = new AgentDeferredOwner(); owner.id = 1; owner.name = "owner";
            AgentDeferredOwner empty = new AgentDeferredOwner(); empty.id = 2; empty.name = "empty";
            setup.insert(owner); setup.insert(empty);
            AgentDeferredLeaf target = new AgentDeferredLeaf(); target.id = 11; target.label = "target"; target.detail = "leaf"; target.owner = owner;
            setup.insert(target); owner.setFavorite(target); setup.update(owner);
            setup.runWithConnection((Connection connection) -> {
                try (var sql = connection.createStatement(); var row = sql.executeQuery("SELECT FAVORITE_ID FROM EA_DEFER_OWNER WHERE ID=1")) {
                    assertTrue(row.next()); assertEquals(11L, row.getLong(1));
                }
            });
        }
        try {
            AgentDeferredOwner detached;
            try (EntityAgent first = custom.createEntityAgent()) {
                detached = first.get(AgentDeferredOwner.class, 1L);
                assertTrue("Custom reference type: " + (detached.getFavorite() == null ? "null" : detached.getFavorite().getClass().getName()), detached.getFavorite() instanceof AgentDeferredLeaf);
                assertTrue(detached.getInverse() instanceof AgentDeferredLeaf);
                assertFalse(custom.getPersistenceUnitUtil().isLoaded(detached.getFavorite(), "label"));
                assertFalse(custom.getPersistenceUnitUtil().isLoaded(detached.getInverse(), "label"));
                assertEquals(0, detached.getFavorite().getLoads());
                assertEquals(0, detached.getInverse().getLoads());
                assertNull(((FetchGroupTracker) detached.getFavorite())._persistence_getSession());
                AgentDeferredOwner empty = first.get(AgentDeferredOwner.class, 2L);
                assertNull(empty.getFavorite()); assertNull(empty.getInverse());
            }
            try (EntityAgent second = custom.createEntityAgent()) {
                second.getTransaction().begin();
                AgentDeferredLeaf current = second.get(AgentDeferredLeaf.class, 11L);
                current.label = "transaction target"; second.update(current);
                second.fetch(detached.getFavorite()); second.fetch(detached.getInverse());
                assertEquals("transaction target", detached.getFavorite().getLabel());
                assertEquals("leaf", ((AgentDeferredLeaf) detached.getFavorite()).getDetail());
                assertEquals(1, detached.getFavorite().getLoads());
                assertEquals(1, detached.getInverse().getLoads());
                second.getTransaction().rollback();
            }
            assertEquals("transaction target", detached.getInverse().getLabel());
        } finally {
            try (EntityAgent cleanup = custom.createEntityAgent()) {
                cleanup.runWithConnection((Connection connection) -> {
                    try (var sql = connection.createStatement()) {
                        sql.executeUpdate("UPDATE EA_DEFER_OWNER SET FAVORITE_ID = NULL");
                        sql.executeUpdate("DELETE FROM EA_DEFER_TARGET");
                        sql.executeUpdate("DELETE FROM EA_DEFER_OWNER");
                    }
                });
            }
        }
    }

    @After
    public void clearRows() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var sql = connection.createStatement()) {
                    sql.executeUpdate("UPDATE EA_GRAPH_LAZY SET NEXT_ID = NULL");
                    for (String table : List.of("EA_GRAPH_LAZY", "EA_LINK", "EA_TAG", "EA_SPECIAL", "EA_DETAILS", "EA_RECORD", "EA_TARGET")) {
                        sql.executeUpdate("DELETE FROM " + table);
                    }
                }
            });
        }
    }

    private void chain(int count) {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.runWithConnection((Connection connection) -> {
                try (var sql = connection.prepareStatement("INSERT INTO EA_GRAPH_LAZY (ID, NAME, DETAIL) VALUES (?, ?, ?)")) {
                    for (int i = 1; i <= count; i++) {
                        sql.setLong(1, i); sql.setString(2, "node " + i); sql.setString(3, "detail " + i); sql.executeUpdate();
                    }
                }
                try (var sql = connection.prepareStatement("UPDATE EA_GRAPH_LAZY SET NEXT_ID = ? WHERE ID = ?")) {
                    for (int i = 1; i < count; i++) {
                        sql.setLong(1, i + 1); sql.setLong(2, i); sql.executeUpdate();
                    }
                }
            });
        }
    }

    private void record(int breadth) {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord record = new AgentRecord(4001, "owner");
            record.lazyNote = "basic state";
            record.tags.add("tag");
            for (int i = 1; i <= breadth; i++) {
                AgentTarget target = new AgentTarget(i, "target " + i);
                agent.insert(target);
                record.targets.add(target);
                if (i == 1) record.favorite = target;
            }
            agent.insert(record);
        }
    }

    private final class Calls implements AutoCloseable {
        private final AtomicInteger count = new AtomicInteger();
        private final SessionEventAdapter listener = new SessionEventAdapter() {
            @Override public void preExecuteCall(SessionEvent event) { count.incrementAndGet(); }
        };
        private Calls() { emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getEventManager().addListener(listener); }
        private int count() { return count.get(); }
        @Override public void close() { emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getEventManager().removeListener(listener); }
    }

    private int readUnrequestedChain() {
        try (Calls calls = new Calls(); EntityAgent agent = emf.createEntityAgent()) {
            AgentLazyNode first = agent.get(AgentLazyNode.class, 1L);
            assertEquals("detail 1", first.getDetail());
            assertEquals(1, first.getLoads());
            AgentLazyNode next = first.getNext();
            assertNotNull(next);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(next, "name"));
            assertNull(((FetchGroupTracker) next)._persistence_getSession());
            assertEquals(0, next.getLoads());
            assertThrows(IllegalStateException.class, next::getDetail);
            return calls.count();
        }
    }

    @Test
    public void unrequestedDepthDoesNotIncreaseReadsOrMaterializedState() {
        assertTrue(new AgentLazyNode() instanceof PersistenceWeaved);
        chain(2);
        int shallow = readUnrequestedChain();
        clearRows();
        chain(80);
        assertEquals(shallow, readUnrequestedChain());
        assertEquals(1, shallow);
    }

    private int readUnrequestedBreadth() {
        try (Calls calls = new Calls(); EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord result = agent.createQuery("select r from AgentRecord r where r.id = 4001", AgentRecord.class).getSingleResult();
            assertEquals("basic state", result.getLazyNote());
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(result, "tags"));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(result, "targets"));
            DatabaseValueHolder<?> tags = (DatabaseValueHolder<?>) ((IndirectContainer) result.tags).getValueHolder();
            DatabaseValueHolder<?> targets = (DatabaseValueHolder<?>) ((IndirectContainer) result.targets).getValueHolder();
            assertNull(tags.getSession()); assertNull(targets.getSession());
            assertNotSame(tags, targets); assertNotSame(tags.getRow(), targets.getRow());
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(result.getFavorite(), "label"));
            return calls.count();
        }
    }

    @Test
    public void unrequestedBreadthDoesNotIncreaseReadsOrInstantiateCollections() {
        record(1);
        int narrow = readUnrequestedBreadth();
        clearRows();
        record(60);
        assertEquals(narrow, readUnrequestedBreadth());
    }

    @Test
    public void anotherAgentFetchesDetachedCollectionsAndReferenceOnItsOwnTransaction() {
        record(1);
        AgentRecord detached;
        try (EntityAgent first = emf.createEntityAgent()) { detached = first.get(AgentRecord.class, 4001L); }
        assertThrows(IllegalStateException.class, detached.tags::size);
        AgentTarget favorite = detached.getFavorite();
        assertFalse(emf.getPersistenceUnitUtil().isLoaded(favorite, "label"));
        try (EntityAgent second = emf.createEntityAgent()) {
            second.getTransaction().begin();
            AgentTarget changed = second.get(AgentTarget.class, 1L);
            changed.label = "uncommitted target";
            second.update(changed);
            second.runWithConnection((Connection connection) -> {
                try (var sql = connection.createStatement()) { sql.executeUpdate("UPDATE EA_TAG SET TAG_VALUE='uncommitted tag' WHERE RECORD_ID=4001"); }
            });
            assertSame(detached.tags, second.fetch(detached.tags));
            assertSame(favorite, second.fetch(favorite));
            second.fetch(detached.targets);
            assertEquals(List.of("uncommitted tag"), detached.tags);
            assertEquals("uncommitted target", favorite.label);
            assertEquals("uncommitted target", detached.targets.get(0).label);
            second.getTransaction().rollback();
        }
        try (Calls calls = new Calls(); EntityAgent observer = emf.createEntityAgent()) {
            assertEquals("uncommitted target", favorite.label);
            assertEquals("uncommitted tag", detached.tags.get(0));
            assertEquals(0, calls.count());
            assertEquals("target 1", observer.get(AgentTarget.class, 1L).label);
        }
    }

    @Test
    public void requestedNestedGraphHasProportionalWorkAndClosedUsableState() {
        chain(6);
        AgentLazyNode first;
        int reads;
        try (Calls calls = new Calls(); EntityAgent agent = emf.createEntityAgent()) {
            var graph = agent.createEntityGraph(AgentLazyNode.class);
            graph.addSubgraph("next").addAttributeNodes("next");
            first = agent.get(graph, 1L);
            assertEquals("detail 2", first.getNext().getDetail());
            assertEquals("detail 3", first.getNext().getNext().getDetail());
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(first.getNext().getNext().getNext(), "name"));
            assertEquals(1, first.getLoads());
            assertEquals(1, first.getNext().getLoads());
            assertEquals(1, first.getNext().getNext().getLoads());
            reads = calls.count();
        }
        assertEquals(3, reads);
        try (Calls calls = new Calls()) {
            assertEquals("detail 3", first.getNext().getNext().getDetail());
            assertEquals(0, calls.count());
        }
    }

    @Test
    public void independentResultsDoNotShareDeferredHoldersOrRows() {
        record(1);
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord first = agent.get(AgentRecord.class, 4001L);
            AgentRecord second = agent.get(AgentRecord.class, 4001L);
            var one = (DatabaseValueHolder<?>) ((IndirectContainer) first.tags).getValueHolder();
            var two = (DatabaseValueHolder<?>) ((IndirectContainer) second.tags).getValueHolder();
            assertNotSame(first.tags, second.tags); assertNotSame(one, two); assertNotSame(one.getRow(), two.getRow());
            agent.fetch(first.tags);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(second, "tags"));
            first.tags.add("caller only");
            agent.fetch(second.tags);
            assertEquals(List.of("tag"), second.tags);
        }
    }

    @Test
    public void updatingScalarStateDoesNotInitializeOrOverwriteUnfetchedCollections() {
        record(1);
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentRecord detached = agent.get(AgentRecord.class, 4001L);
            Integer version = detached.version;
            agent.update(detached);
            assertEquals(version, detached.version);
            detached.note = "excluded secondary";
            agent.update(detached);
            assertEquals(version, detached.version);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(detached, "tags"));
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(detached, "targets"));
            detached.name = "updated owner";
            agent.update(detached);
            assertTrue(detached.version > version);
            agent.fetch(detached.tags); agent.fetch(detached.targets);
            assertEquals(List.of("tag"), detached.tags);
            assertEquals(1, detached.targets.size());
        }
    }

    @Test
    public void ordinaryCopyGroupStillCopiesItsRequestedCascade() {
        record(1);
        try (var manager = emf.createEntityManager()) {
            AgentRecord managed = manager.find(AgentRecord.class, 4001L);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(managed, "tags"));
            CopyGroup copy = new CopyGroup();
            copy.cascadeAllParts();
            AgentRecord result = (AgentRecord) emf.unwrap(JpaEntityManagerFactory.class).getServerSession().copy(managed, copy);
            assertEquals(List.of("tag"), result.tags);
            assertEquals("target 1", result.targets.get(0).label);
            assertNotSame(managed, result); assertNotSame(managed.tags, result.tags);
            assertTrue(manager.contains(managed));
        }
    }
}
