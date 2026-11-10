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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.entityagent.model.AgentCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentNativeCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentMergeCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentXmlCallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveryRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveryExcludedRecord;
import org.eclipse.persistence.jpa.entityagent.model.callbacks.AgentDiscoveredCallbackListener;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestEntityAgentCallbacks {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = AgentCallbackRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Emf(name = "entityagent-xml-callbacks", createTables = DDLGen.DROP_CREATE,
            classes = AgentXmlCallbackRecord.class,
            mappingFiles = "META-INF/entityagent-callbacks.xml",
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory xmlEmf;

    @Emf(name = "entityagent-listener-discovery", createTables = DDLGen.DROP_CREATE,
            classes = {AgentDiscoveryRecord.class, AgentDiscoveryExcludedRecord.class,
                    AgentDiscoveredCallbackListener.class},
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory discoveryEmf;

    @Emf(name = "entityagent-pre-merge", createTables = DDLGen.DROP_CREATE,
            classes = AgentMergeCallbackRecord.class,
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory mergeEmf;

    @Emf(name = "entityagent-native-callbacks", createTables = DDLGen.DROP_CREATE,
            classes = AgentNativeCallbackRecord.class, properties = {
            @Property(name = "jakarta.persistence.validation.mode", value = "CALLBACK"),
            @Property(name = "jakarta.persistence.validation.group.pre-insert", value =
                    "org.eclipse.persistence.jpa.entityagent.model.AgentNativeCallbackRecord$InsertChecks"),
            @Property(name = "jakarta.persistence.validation.group.pre-update", value =
                    "org.eclipse.persistence.jpa.entityagent.model.AgentNativeCallbackRecord$UpdateChecks"),
            @Property(name = "jakarta.persistence.validation.group.pre-upsert", value =
                    "org.eclipse.persistence.jpa.entityagent.model.AgentNativeCallbackRecord$UpsertChecks")})
    private EntityManagerFactory nativeEmf;

    @Before
    public void clearMergeCallbacks() {
        AgentMergeCallbackRecord.CALLBACKS.clear();
    }

    @Test
    public void preMergeMutatesNewSourceBeforeManagedPrePersist() {
        AgentMergeCallbackRecord source = new AgentMergeCallbackRecord(501, "new");
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentMergeCallbackRecord target = manager.merge(source);
                assertNotSame(source, target);
                assertEquals("merged:new", target.name);
                assertEquals(2, AgentMergeCallbackRecord.CALLBACKS.size());
                var preMerge = AgentMergeCallbackRecord.CALLBACKS.get(0);
                var prePersist = AgentMergeCallbackRecord.CALLBACKS.get(1);
                assertEquals("pre-merge", preMerge.event());
                assertSame(source, preMerge.entity());
                assertEquals("pre-persist", prePersist.event());
                assertSame(target, prePersist.entity());
                assertEquals("merged:new", prePersist.name());
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void preMergeUsesDetachedSourceBeforeCopyingExistingEntity() {
        AgentMergeCallbackRecord source = new AgentMergeCallbackRecord(502, "original");
        try (EntityAgent agent = mergeEmf.createEntityAgent()) {
            agent.insert(source);
        }
        source.name = "edited";
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentMergeCallbackRecord target = manager.merge(source);
            assertNotSame(source, target);
            assertEquals("merged:edited", target.name);
            assertEquals(1, AgentMergeCallbackRecord.CALLBACKS.size());
            assertSame(source, AgentMergeCallbackRecord.CALLBACKS.get(0).entity());
            manager.getTransaction().commit();
            assertEquals(1, AgentMergeCallbackRecord.CALLBACKS.size());
        }
        try (EntityAgent agent = mergeEmf.createEntityAgent()) {
            AgentMergeCallbackRecord stored = agent.get(AgentMergeCallbackRecord.class, 502);
            assertEquals("merged:edited", stored.name);
            agent.delete(stored);
        }
    }

    @Test
    public void cyclicRepeatedMergeCascadesInvokeEachSourceOnce() {
        AgentMergeCallbackRecord parent = new AgentMergeCallbackRecord(503, "parent");
        AgentMergeCallbackRecord child = new AgentMergeCallbackRecord(504, "child");
        parent.child = child;
        parent.repeatedChild = child;
        child.parent = parent;
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentMergeCallbackRecord target = manager.merge(parent);
                List<AgentMergeCallbackRecord> merged = AgentMergeCallbackRecord.CALLBACKS.stream()
                        .filter(callback -> callback.event().equals("pre-merge"))
                        .map(AgentMergeCallbackRecord.Callback::entity).toList();
                assertEquals(2, merged.size());
                assertTrue(merged.contains(parent));
                assertTrue(merged.contains(child));
                assertSame(target.child, target.repeatedChild);
                assertSame(target, target.child.parent);
                assertEquals("merged:parent", target.name);
                assertEquals("merged:child", target.child.name);
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void managedSourceSkipsPreMergeButStillCascades() {
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentMergeCallbackRecord managed = new AgentMergeCallbackRecord(505, "managed");
                manager.persist(managed);
                AgentMergeCallbackRecord child = new AgentMergeCallbackRecord(506, "child");
                managed.child = child;
                AgentMergeCallbackRecord.CALLBACKS.clear();
                assertSame(managed, manager.merge(managed));
                List<AgentMergeCallbackRecord> merged = AgentMergeCallbackRecord.CALLBACKS.stream()
                        .filter(callback -> callback.event().equals("pre-merge"))
                        .map(AgentMergeCallbackRecord.Callback::entity).toList();
                assertEquals(List.of(child), merged);
                assertEquals("managed", managed.name);
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void preMergeFailurePropagatesAndMarksTransactionForRollback() {
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                AgentMergeCallbackRecord source = new AgentMergeCallbackRecord(507, "failure");
                source.failPreMerge = true;
                assertThrows(IllegalStateException.class, () -> manager.merge(source));
                assertTrue(manager.getTransaction().getRollbackOnly());
                assertEquals(1, AgentMergeCallbackRecord.CALLBACKS.size());
                assertEquals("pre-merge", AgentMergeCallbackRecord.CALLBACKS.get(0).event());
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void refreshCommitAndAgentUpsertDoNotInvokePreMerge() {
        AgentMergeCallbackRecord source = new AgentMergeCallbackRecord(508, "upserted");
        try (EntityAgent agent = mergeEmf.createEntityAgent()) {
            agent.upsert(source);
            assertTrue(AgentMergeCallbackRecord.CALLBACKS.isEmpty());
            source.name = "discarded";
            agent.refresh(source);
            assertEquals("upserted", source.name);
            assertTrue(AgentMergeCallbackRecord.CALLBACKS.isEmpty());
        }
        try (EntityManager manager = mergeEmf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentMergeCallbackRecord target = manager.find(AgentMergeCallbackRecord.class, 508);
            target.name = "discarded";
            manager.refresh(target);
            assertEquals("upserted", target.name);
            target.name = "committed";
            manager.getTransaction().commit();
            assertTrue(AgentMergeCallbackRecord.CALLBACKS.isEmpty());
        }
        try (EntityAgent agent = mergeEmf.createEntityAgent()) {
            agent.delete(agent.get(AgentMergeCallbackRecord.class, 508));
        }
    }

    @Test
    public void annotationNamedStatementsUseSharedRegistry() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.insert(new AgentCallbackRecord(107, "original"));
            agent.getTransaction().begin();
            assertEquals(1, agent.createNamedStatement("agentCallbackRename")
                    .setParameter("name", "jpql").setParameter("id", 107).execute());
            assertEquals("jpql", agent.get(AgentCallbackRecord.class, 107).name);
            assertEquals(1, agent.createNamedStatement("agentCallbackNativeRename").execute());
            assertEquals("native", agent.get(AgentCallbackRecord.class, 107).name);
            assertEquals(1, agent.createNamedStatement("agentCallbackDelete").setParameter("id", 107).execute());
            assertEquals(0, agent.createNamedStatement("agentCallbackNativeDelete").execute());
            agent.getTransaction().commit();
        }
    }

    @Test
    public void xmlNamedStatementsAtUnitAndEntityLevel() {
        try (EntityAgent agent = xmlEmf.createEntityAgent()) {
            agent.insert(new AgentXmlCallbackRecord(202, "original"));
            agent.getTransaction().begin();
            assertEquals(1, agent.createNamedStatement("agentXmlRename")
                    .setParameter("name", "jpql").setParameter("id", 202).execute());
            assertEquals("jpql", agent.get(AgentXmlCallbackRecord.class, 202).name);
            assertEquals(1, agent.createNamedStatement("agentXmlNativeRename").execute());
            assertEquals("native", agent.get(AgentXmlCallbackRecord.class, 202).name);
            assertEquals(1, agent.createNamedStatement("agentXmlDelete").setParameter("id", 202).execute());
            assertEquals(0, agent.createNamedStatement("agentXmlNativeDelete").execute());
            agent.getTransaction().commit();
        }
    }

    @Test
    public void namedStatementsAreDiscoveredOnPackagesAndNonentityClasses() {
        try (EntityAgent agent = discoveryEmf.createEntityAgent()) {
            agent.insert(new AgentDiscoveryRecord(303));
            agent.insert(new AgentDiscoveryRecord(304));
            agent.getTransaction().begin();
            assertEquals(1, agent.createNamedStatement("agentPackageDelete").setParameter("id", 303).execute());
            assertEquals(1, agent.createNamedStatement("agentNonentityDelete").execute());
            agent.getTransaction().commit();
            assertNull(agent.find(AgentDiscoveryRecord.class, 303));
            assertNull(agent.find(AgentDiscoveryRecord.class, 304));
        }
    }

    @Test
    public void listenerDiscoveryOrderingAndOverloads() {
        try (EntityAgent agent = discoveryEmf.createEntityAgent()) {
            AgentDiscoveryRecord record = new AgentDiscoveryRecord(301);
            agent.insert(record);
            assertEquals(List.of("discovered-pre", "package-pre", "declared-pre", "entity-pre"),
                    record.events.subList(0, 4));
            assertEquals(6, record.events.size());
            assertTrue(record.events.contains("discovered-post-record"));
            assertTrue(record.events.contains("discovered-post-object"));
            agent.delete(record);
        }
    }

    @Test
    public void excludeDefaultsRetainsDiscoveredAndEntityListeners() {
        try (EntityAgent agent = discoveryEmf.createEntityAgent()) {
            AgentDiscoveryExcludedRecord record = new AgentDiscoveryExcludedRecord(302);
            agent.insert(record);
            assertEquals(List.of("discovered-pre", "declared-pre", "entity-pre"),
                    record.events.subList(0, 3));
            assertFalse(record.events.contains("package-pre"));
            agent.delete(record);
        }
    }

    @Test
    public void xmlDatabaseCallbacksAndVersionExclusion() {
        try (EntityAgent agent = xmlEmf.createEntityAgent()) {
            AgentXmlCallbackRecord record = new AgentXmlCallbackRecord(201, "xml");
            agent.insert(record);
            assertEquals(List.of("xml-pre-insert", "xml-post-insert"), record.events);
            Integer originalVersion = record.version;
            record.events.clear();
            record.note = "excluded";
            agent.update(record);
            assertEquals(originalVersion, record.version);
            assertEquals(List.of("xml-pre-update", "xml-post-update"), record.events);
            record.events.clear();
            record.name = "upserted";
            agent.upsert(record);
            assertEquals(List.of("xml-pre-upsert", "xml-post-upsert"), record.events);
            AgentXmlCallbackRecord loaded = agent.get(AgentXmlCallbackRecord.class, 201);
            assertEquals(List.of("xml-post-load:upserted"), loaded.events);
            loaded.events.clear();
            agent.delete(loaded);
            assertEquals(List.of("xml-pre-delete", "xml-post-delete"), loaded.events);
        }
    }

    @Test
    public void immediateDatabaseCallbacksAndDetachedLoad() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackRecord record = new AgentCallbackRecord(101, "change-before-insert");
            agent.insert(record);
            assertEquals(List.of("listener-pre-insert", "pre-insert", "post-insert"), record.events);
            assertEquals("changed-by-callback", record.name);
            AgentCallbackRecord loaded = agent.get(AgentCallbackRecord.class, 101);
            assertEquals(List.of("post-load:changed-by-callback"), loaded.events);
            loaded.events.clear();
            loaded.name = "updated";
            agent.update(loaded);
            assertEquals(List.of("pre-update", "post-update"), loaded.events);
            loaded.events.clear();
            agent.delete(loaded);
            assertEquals(List.of("pre-delete", "post-delete"), loaded.events);
        }
    }

    @Test
    public void upsertUsesOnlyUpsertCallbacks() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackRecord record = new AgentCallbackRecord(102, "new-upsert");
            agent.upsert(record);
            assertEquals(List.of("pre-upsert", "post-upsert"), record.events);
            record.events.clear();
            record.name = "existing-upsert";
            agent.upsert(record);
            assertEquals(List.of("pre-upsert", "post-upsert"), record.events);
            agent.delete(record);
        }
    }

    @Test
    public void callbackFailureStopsSubsequentCallbacksAndSql() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackRecord record = new AgentCallbackRecord(103, "fail-before-insert");
            assertThrows(IllegalStateException.class, () -> agent.insert(record));
            assertEquals(List.of("listener-pre-insert"), record.events);
            assertNull(agent.find(AgentCallbackRecord.class, 103));
        }
    }

    @Test
    public void refreshNotifiesSuppliedEntityAfterCopy() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackRecord record = new AgentCallbackRecord(104, "database");
            agent.insert(record);
            record.events.clear();
            record.name = "discarded";
            agent.refresh(record);
            assertEquals("database", record.name);
            assertEquals(List.of("post-load:database"), record.events);
            agent.delete(record);
        }
    }

    @Test
    public void excludedOnlyUpdatePreservesVersion() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentCallbackRecord record = new AgentCallbackRecord(105, "versioned");
            agent.insert(record);
            Integer initial = record.version;
            record.note = "excluded-change";
            agent.update(record);
            assertEquals(initial, record.version);
            assertEquals("excluded-change", agent.get(AgentCallbackRecord.class, 105).note);
            record.name = "included-change";
            agent.update(record);
            assertTrue(record.version > initial);
            agent.delete(record);
        }
    }

    @Test
    public void entityManagerPreservesManagedAndDatabaseCallbacks() {
        try (EntityManager manager = emf.createEntityManager()) {
            manager.getTransaction().begin();
            AgentCallbackRecord record = new AgentCallbackRecord(106, "managed");
            manager.persist(record);
            assertTrue(record.events.contains("pre-persist"));
            assertFalse(record.events.contains("post-persist"));
            manager.flush();
            assertTrue(record.events.contains("pre-insert"));
            assertTrue(record.events.contains("post-insert"));
            assertTrue(record.events.contains("post-persist"));
            record.events.clear();
            manager.remove(record);
            assertTrue(record.events.contains("pre-remove"));
            manager.flush();
            assertTrue(record.events.contains("pre-delete"));
            assertTrue(record.events.contains("post-delete"));
            assertTrue(record.events.contains("post-remove"));
            manager.getTransaction().commit();
        }
    }

    @Test
    public void nativeAndJpaCallbacksKeepTheirOwnUpsertSemantics() {
        DescriptorEventManager events = nativeEventManager();
        DescriptorEventAdapter listener = new NativeRecorder();
        events.addListener(listener);
        try (EntityAgent agent = nativeEmf.createEntityAgent()) {
            AgentNativeCallbackRecord record = new AgentNativeCallbackRecord(601, "new");
            agent.upsert(record);
            assertEquals(List.of("pre-upsert", "validate-upsert", "post-upsert"), record.callbacks);
            assertEquals(List.of("listener-pre-insert", "entity-pre-insert",
                    "listener-post-insert", "entity-post-insert"), record.nativeEvents);

            record.callbacks.clear();
            record.nativeEvents.clear();
            record.name = "updated";
            agent.upsert(record);
            assertEquals(List.of("pre-upsert", "validate-upsert", "post-upsert"), record.callbacks);
            assertEquals(List.of("listener-pre-update", "entity-pre-update",
                    "listener-pre-update-changes", "entity-pre-update-changes",
                    "listener-post-update", "entity-post-update"), record.nativeEvents);
            agent.delete(record);
        } finally {
            events.removeListener(listener);
        }
    }

    @Test
    public void ordinaryAgentAndManagedWritesRetainPhysicalAndJpaCallbacks() {
        DescriptorEventManager events = nativeEventManager();
        DescriptorEventAdapter listener = new NativeRecorder();
        events.addListener(listener);
        try (EntityAgent agent = nativeEmf.createEntityAgent()) {
            AgentNativeCallbackRecord record = new AgentNativeCallbackRecord(602, "insert");
            agent.insert(record);
            assertEquals(List.of("pre-insert", "validate-insert", "post-insert"), record.callbacks);
            assertEquals(List.of("listener-pre-insert", "entity-pre-insert",
                    "listener-post-insert", "entity-post-insert"), record.nativeEvents);
            record.callbacks.clear();
            record.nativeEvents.clear();
            record.name = "update";
            agent.update(record);
            assertEquals(List.of("pre-update", "validate-update", "post-update"), record.callbacks);
            assertEquals(List.of("listener-pre-update", "entity-pre-update",
                    "listener-pre-update-changes", "entity-pre-update-changes",
                    "listener-post-update", "entity-post-update"), record.nativeEvents);
            agent.delete(record);

            try (EntityManager manager = nativeEmf.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    AgentNativeCallbackRecord managed = new AgentNativeCallbackRecord(603, "managed");
                    manager.persist(managed);
                    assertEquals(List.of("pre-persist"), managed.callbacks);
                    manager.flush();
                    assertEquals(List.of("pre-persist", "pre-insert", "validate-insert",
                            "post-insert", "post-persist"), managed.callbacks);
                    assertEquals(List.of("listener-pre-insert", "entity-pre-insert",
                            "listener-post-insert", "entity-post-insert"), managed.nativeEvents);
                } finally {
                    manager.getTransaction().rollback();
                }
            }
        } finally {
            events.removeListener(listener);
        }
    }

    @Test
    public void failedUpsertCallbacksRestoreOperationAndStopRemainingDispatch() {
        DescriptorEventManager events = nativeEventManager();
        DescriptorEventAdapter listener = new NativeRecorder();
        events.addListener(listener);
        try (EntityAgent agent = nativeEmf.createEntityAgent()) {
            AgentNativeCallbackRecord record = new AgentNativeCallbackRecord(604, "failure");
            record.failUpsert = true;
            assertThrows(IllegalStateException.class, () -> agent.upsert(record));
            assertEquals(List.of("pre-upsert"), record.callbacks);
            assertEquals(List.of("listener-pre-insert"), record.nativeEvents);
            AbstractSession session = agent.unwrap(AbstractSession.class);
            assertNull(session.getProperty(EntityAgentProperties.OPERATION));
            assertNull(agent.find(AgentNativeCallbackRecord.class, record.id));

            record.failUpsert = false;
            record.valid = false;
            record.callbacks.clear();
            record.nativeEvents.clear();
            assertThrows(ConstraintViolationException.class, () -> agent.upsert(record));
            assertEquals(List.of("pre-upsert", "validate-upsert"), record.callbacks);
            assertEquals(List.of("listener-pre-insert"), record.nativeEvents);
            assertNull(session.getProperty(EntityAgentProperties.OPERATION));
            assertNull(agent.find(AgentNativeCallbackRecord.class, record.id));

            record.valid = true;
            record.callbacks.clear();
            record.nativeEvents.clear();
            agent.insert(record);
            assertEquals(List.of("pre-insert", "validate-insert", "post-insert"), record.callbacks);
            assertEquals(List.of("listener-pre-insert", "entity-pre-insert",
                    "listener-post-insert", "entity-post-insert"), record.nativeEvents);
            agent.delete(record);
        } finally {
            events.removeListener(listener);
        }
    }

    @Test
    public void nestedInsertRestoresEnclosingUpsertMarkerAndCallbacks() {
        try (EntityAgent agent = nativeEmf.createEntityAgent()) {
            AgentNativeCallbackRecord outer = new AgentNativeCallbackRecord(605, "outer");
            AgentNativeCallbackRecord nested = new AgentNativeCallbackRecord(606, "nested");
            AbstractSession session = agent.unwrap(AbstractSession.class);
            outer.nestedOperation = () -> {
                assertEquals("UPSERT", session.getProperty(EntityAgentProperties.OPERATION));
                agent.insert(nested);
                assertEquals("UPSERT", session.getProperty(EntityAgentProperties.OPERATION));
                assertEquals(Boolean.FALSE, session.getProperty(EntityAgentProperties.WRITE_SUCCEEDED));
            };
            agent.upsert(outer);
            assertEquals(List.of("pre-upsert", "validate-upsert", "post-upsert"), outer.callbacks);
            assertEquals(List.of("pre-insert", "validate-insert", "post-insert"), nested.callbacks);
            assertNull(session.getProperty(EntityAgentProperties.OPERATION));
            assertNull(session.getProperty(EntityAgentProperties.WRITE_SUCCEEDED));
            agent.delete(outer);
            agent.delete(nested);
        }
    }

    private DescriptorEventManager nativeEventManager() {
        return nativeEmf.unwrap(JpaEntityManagerFactory.class).getServerSession()
                .getDescriptor(AgentNativeCallbackRecord.class).getEventManager();
    }

    private static class NativeRecorder extends DescriptorEventAdapter {
        private void record(DescriptorEvent event, String name) {
            ((AgentNativeCallbackRecord) event.getSource()).nativeEvents.add("listener-" + name);
        }

        @Override
        public void preInsert(DescriptorEvent event) { record(event, "pre-insert"); }
        @Override
        public void postInsert(DescriptorEvent event) { record(event, "post-insert"); }
        @Override
        public void preUpdate(DescriptorEvent event) { record(event, "pre-update"); }
        @Override
        public void preUpdateWithChanges(DescriptorEvent event) { record(event, "pre-update-changes"); }
        @Override
        public void postUpdate(DescriptorEvent event) { record(event, "post-update"); }
    }

}
