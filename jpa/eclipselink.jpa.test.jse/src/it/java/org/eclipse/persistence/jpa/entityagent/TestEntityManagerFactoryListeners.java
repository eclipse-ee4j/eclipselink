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

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.sql.DataSource;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityListenerRegistration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PostDelete;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostUpsert;
import jakarta.persistence.PreDelete;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PreMerge;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PreUpsert;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.persistence.annotations.CacheIsolationType;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Base;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Child;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Left;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Marker;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Ordered;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Plain;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.PolymorphicChild;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.PolymorphicFirst;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.PolymorphicParent;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.PolymorphicRecord;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.PolymorphicSecond;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Right;
import org.eclipse.persistence.jpa.entityagent.model.EntityManagerFactoryListenerModels.Root;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Public M7 listener contracts. Methods beginning h2 own a separate H2 lifecycle fixture. */
@RunWith(EmfRunner.class)
public class TestEntityManagerFactoryListeners {
    private static final List<Class<? extends Annotation>> EVENTS = List.of(
            PrePersist.class, PostPersist.class, PreRemove.class, PostRemove.class, PreMerge.class, PostLoad.class,
            PreInsert.class, PostInsert.class, PreUpdate.class, PostUpdate.class, PreUpsert.class, PostUpsert.class,
            PreDelete.class, PostDelete.class);

    @Emf(name = "emf-listener-fixture", createTables = DDLGen.DROP_CREATE,
            classes = {Plain.class, Root.class, Child.class, Ordered.class, PolymorphicChild.class,
                    PolymorphicFirst.class, PolymorphicSecond.class, PolymorphicParent.class}, properties = {
            @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
            @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory fixture;

    @Before
    public void cleanOwnedRows() {
        try (EntityManager manager = fixture.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                manager.createNativeQuery("UPDATE EA_EMFL_ORDERED SET RELATED_ID = NULL, SECONDARY_ID = NULL").executeUpdate();
                manager.createNativeQuery("UPDATE EA_EMFL_TPC_PARENT SET RELATED_ID = NULL").executeUpdate();
                for (String table : List.of("EA_EMFL_PLAIN", "EA_EMFL_TYPED", "EA_EMFL_ORDERED",
                        "EA_EMFL_TPC_PARENT", "EA_EMFL_TPC_FIRST", "EA_EMFL_TPC_SECOND")) {
                    manager.createNativeQuery("DELETE FROM " + table).executeUpdate();
                }
                manager.getTransaction().commit();
            } finally {
                rollback(manager);
            }
        }
        fixture.getCache().evictAll();
    }

    @Test
    public void managedPersistUpdateRemoveDeliverExactEventsAndRows() throws Exception {
        try (Unit unit = new Unit(); Recorder recorder = new Recorder(unit.factory);
                EntityManager manager = unit.factory.createEntityManager()) {
            Plain entity = new Plain(1, "inserted");
            manager.getTransaction().begin();
            manager.persist(entity);
            recorder.expect(entity, PrePersist.class);
            manager.flush();
            recorder.expect(entity, PreInsert.class, PostInsert.class, PostPersist.class);
            manager.getTransaction().commit();
            assertEquals("inserted", value(1));
            manager.getTransaction().begin();
            entity.setValue("updated");
            manager.flush();
            recorder.expect(entity, PreUpdate.class, PostUpdate.class);
            manager.getTransaction().commit();
            assertEquals("updated", value(1));
            manager.getTransaction().begin();
            manager.remove(entity);
            recorder.expect(entity, PreRemove.class);
            manager.flush();
            recorder.expect(entity, PreDelete.class, PostDelete.class, PostRemove.class);
            manager.getTransaction().commit();
            assertEquals(0, count(1));
        }
    }

    @Test
    public void agentInsertUpdateDeleteUseActualEntityWithoutManagedEvents() throws Exception {
        try (Unit unit = new Unit(); Recorder recorder = new Recorder(unit.factory);
                EntityAgent agent = unit.factory.createEntityAgent()) {
            Plain entity = new Plain(2, "inserted");
            agent.insert(entity);
            recorder.expect(entity, PreInsert.class, PostInsert.class);
            assertEquals("inserted", value(2));
            entity.value = "updated";
            agent.update(entity);
            recorder.expect(entity, PreUpdate.class, PostUpdate.class);
            assertEquals("updated", value(2));
            agent.delete(entity);
            recorder.expect(entity, PreDelete.class, PostDelete.class);
            assertEquals(0, count(2));
        }
    }

    @Test
    public void agentUpsertBothBranchesExposeOnlyLogicalEvents() throws Exception {
        try (Unit unit = new Unit(); Recorder recorder = new Recorder(unit.factory);
                EntityAgent agent = unit.factory.createEntityAgent()) {
            Plain entity = new Plain(3, "insert branch");
            agent.upsert(entity);
            recorder.expect(entity, PreUpsert.class, PostUpsert.class);
            assertEquals("insert branch", value(3));
            entity.value = "update branch";
            agent.upsert(entity);
            recorder.expect(entity, PreUpsert.class, PostUpsert.class);
            assertEquals("update branch", value(3));
        }
    }

    @Test
    public void preMergeReceivesSourceAndMutationReachesManagedTarget() throws Exception {
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            List<Object> sources = new ArrayList<>();
            List<Object> targets = new ArrayList<>();
            unit.factory.addListener(Plain.class, PreMerge.class, entity -> {
                sources.add(entity);
                entity.value = "merged:" + entity.value;
            });
            unit.factory.addListener(Plain.class, PrePersist.class, targets::add);
            Plain source = new Plain(4, "new");
            manager.getTransaction().begin();
            Plain target = manager.merge(source);
            assertNotSame(source, target);
            assertEquals(List.of(source), sources);
            assertEquals(List.of(target), targets);
            manager.getTransaction().commit();
            assertEquals("merged:new", value(4));
            source.version = target.version;
            manager.clear();
            sources.clear();
            targets.clear();
            source.value = "existing";
            manager.getTransaction().begin();
            target = manager.merge(source);
            assertNotSame(source, target);
            assertEquals(List.of(source), sources);
            assertTrue(targets.isEmpty());
            manager.getTransaction().commit();
            assertEquals("merged:existing", value(4));
        }
    }

    @Test
    public void postLoadReceivesFinalObjectsForMissHitQueryAndRefresh() throws Exception {
        seed(5, "original");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            List<Plain> loaded = new ArrayList<>();
            AtomicInteger calls = new AtomicInteger();
            unit.server().getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preExecuteCall(SessionEvent event) {
                    calls.incrementAndGet();
                }
            });
            unit.factory.addListener(Plain.class, PostLoad.class, loaded::add);
            unit.factory.getCache().evictAll();
            Plain first = manager.find(Plain.class, 5);
            assertTrue("Cache miss must execute SQL", calls.get() > 0);
            assertEquals(List.of(first), loaded);
            assertEquals("original", first.value);
            loaded.clear();
            manager.clear();
            calls.set(0);
            Plain cached = manager.find(Plain.class, 5);
            assertEquals("Shared-cache hit must avoid SQL", 0, calls.get());
            assertNotSame(first, cached);
            assertEquals(List.of(cached), loaded);
            loaded.clear();
            manager.clear();
            Plain queried = manager.createQuery("select p from EmfListenerPlain p where p.value = 'original'", Plain.class).getSingleResult();
            assertTrue("JPQL query must execute SQL", calls.get() > 0);
            assertEquals(List.of(queried), loaded);
            loaded.clear();
            changeValue(5, "refreshed");
            calls.set(0);
            manager.refresh(queried);
            assertTrue("Refresh must execute SQL", calls.get() > 0);
            assertEquals("refreshed", queried.value);
            assertEquals(List.of(queried), loaded);
        }
    }

    @Test
    public void agentPostLoadUsesReturnedAndRefreshTargetsOnly() throws Exception {
        seed(6, "original");
        try (Unit unit = new Unit(); Recorder recorder = new Recorder(unit.factory);
                EntityAgent agent = unit.factory.createEntityAgent()) {
            Plain entity = agent.get(Plain.class, 6);
            recorder.expect(entity, PostLoad.class);
            changeValue(6, "refreshed");
            agent.refresh(entity);
            recorder.expect(entity, PostLoad.class);
            assertEquals("refreshed", entity.value);
            entity.value = "updated";
            agent.update(entity);
            recorder.expect(entity, PreUpdate.class, PostUpdate.class);
            agent.upsert(entity);
            recorder.expect(entity, PreUpsert.class, PostUpsert.class);
            assertEquals("updated", value(6));
        }
    }

    @Test
    public void objectPostLoadFilterNeverReceivesEmbeddedValues() throws Exception {
        seed(34, "owner");
        try (Unit unit = new Unit()) {
            List<Object> loaded = new ArrayList<>();
            unit.factory.addListener(Object.class, PostLoad.class, loaded::add);
            try (EntityManager manager = unit.factory.createEntityManager()) {
                Plain entity = manager.find(Plain.class, 34);
                assertEquals("embedded detail", entity.details.note);
                assertEquals(List.of(entity), loaded);
                loaded.clear();
                manager.refresh(entity);
                assertEquals(List.of(entity), loaded);
            }
            loaded.clear();
            try (EntityAgent agent = unit.factory.createEntityAgent()) {
                Plain entity = agent.get(Plain.class, 34);
                assertEquals("embedded detail", entity.details.note);
                assertEquals(List.of(entity), loaded);
                loaded.clear();
                agent.refresh(entity);
                assertEquals(List.of(entity), loaded);
            }
        }
    }

    @Test
    public void internalForceIncrementAndBulkStatementsDoNotEmitWriteCallbacks() throws Exception {
        seed(7, "original");
        try (Unit unit = new Unit(); Recorder recorder = new Recorder(unit.factory);
                EntityManager manager = unit.factory.createEntityManager(); EntityAgent agent = unit.factory.createEntityAgent()) {
            Plain entity = agent.get(Plain.class, 7);
            recorder.expect(entity, PostLoad.class);
            int version = entity.version;
            // EntityAgent's internal LOCK write suppresses database callbacks.
            agent.getTransaction().begin();
            Plain locked = agent.get(Plain.class, 7, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            recorder.expect(locked, PostLoad.class);
            agent.getTransaction().commit();
            recorder.expect(locked);
            assertTrue(locked.version > version);
            try (EntityAgent observer = fixture.createEntityAgent()) {
                assertEquals(locked.version, observer.get(Plain.class, 7).version);
            }
            manager.getTransaction().begin();
            assertEquals(1, manager.createQuery("update EmfListenerPlain p set p.value = 'bulk' where p.id = 7").executeUpdate());
            manager.getTransaction().commit();
            recorder.expect(entity);
            assertEquals("bulk", value(7));
        }
    }

    @Test
    public void assignableFiltersMatchDiamondOnceAndExclusionsKeepRuntimeListeners() throws Exception {
        try (Unit unit = new Unit(); EntityAgent agent = unit.factory.createEntityAgent()) {
            List<String> seen = new ArrayList<>();
            Child entity = new Child();
            entity.id = 8;
            entity.value = "child";
            for (Class<?> filter : List.of(Child.class, Root.class, Base.class, Left.class, Right.class, Marker.class, Object.class)) {
                unit.factory.addListener(filter, PreInsert.class, value -> {
                    assertSame(entity, value);
                    seen.add(filter.getSimpleName());
                });
            }
            unit.factory.addListener(String.class, PreInsert.class, ignored -> seen.add("unmatched"));
            agent.insert(entity);
            assertEquals(List.of("Child", "Root", "Base", "Left", "Right", "Marker", "Object"), seen);
            assertTrue("Superclass metadata listener excluded, direct runtime filters retained", entity.trace.isEmpty());
            assertEquals(1, tableCount("EA_EMFL_TYPED", 8));
        }
    }

    @Test
    public void duplicateRegistrationsHaveIndependentCancellationIdentities() throws Exception {
        try (Unit unit = new Unit(); EntityAgent agent = unit.factory.createEntityAgent()) {
            List<Plain> seen = new ArrayList<>();
            Consumer<Plain> consumer = seen::add;
            EntityListenerRegistration first = unit.factory.addListener(Plain.class, PreInsert.class, consumer);
            EntityListenerRegistration second = unit.factory.addListener(Plain.class, PreInsert.class, consumer);
            assertNotSame(first, second);
            Plain one = new Plain(9, "one");
            agent.insert(one);
            assertEquals(List.of(one, one), seen);
            first.cancel();
            first.cancel();
            seen.clear();
            Plain two = new Plain(10, "two");
            agent.insert(two);
            assertEquals(List.of(two), seen);
            second.cancel();
            seen.clear();
            agent.insert(new Plain(11, "three"));
            assertTrue(seen.isEmpty());
        }
    }

    @Test
    public void metadataRuntimeEntityValidationOrderAndMutationReachSql() throws Exception {
        try (Unit unit = new Unit("CALLBACK"); EntityAgent agent = unit.factory.createEntityAgent()) {
            unit.factory.addListener(Ordered.class, PreInsert.class, entity -> {
                entity.trace.add("runtime-one");
                entity.value = "repaired";
            });
            unit.factory.addListener(Ordered.class, PreInsert.class, entity -> {
                assertEquals("repaired", entity.value);
                entity.trace.add("runtime-two");
            });
            Ordered entity = new Ordered();
            entity.id = 12;
            entity.value = "x";
            agent.insert(entity);
            assertEquals(List.of("metadata", "runtime-one", "runtime-two", "entity", "validation"), entity.trace);
            assertEquals("repaired", tableValue("EA_EMFL_ORDERED", 12));
        }
    }

    @Test
    public void runtimeMutationIsCheckedByValidationBeforeInsert() throws Exception {
        try (Unit unit = new Unit("CALLBACK"); EntityAgent agent = unit.factory.createEntityAgent()) {
            unit.factory.addListener(Ordered.class, PreInsert.class, entity -> {
                entity.trace.add("runtime");
                entity.value = "x";
            });
            Ordered entity = new Ordered();
            entity.id = 13;
            entity.value = "valid";
            assertThrows(ConstraintViolationException.class, () -> agent.insert(entity));
            assertEquals(List.of("metadata", "runtime", "entity", "validation"), entity.trace);
            assertEquals(0, tableCount("EA_EMFL_ORDERED", 13));
        }
    }

    @Test
    public void firstRegistrationReachesExistingCallbackFreeManagerAndAgentContexts() throws Exception {
        seed(14, "existing");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager();
                EntityAgent agent = unit.factory.createEntityAgent()) {
            assertFalse(unit.server().getDescriptor(Plain.class).getEventManager().hasAnyEventListeners());
            assertNotNull(manager.find(Plain.class, 14));
            assertNotNull(agent.get(Plain.class, 14));
            List<Plain> seen = new ArrayList<>();
            unit.factory.addListener(Plain.class, PreInsert.class, seen::add);
            Plain managed = new Plain(15, "manager");
            persist(manager, managed);
            Plain detached = new Plain(16, "agent");
            agent.insert(detached);
            assertEquals(List.of(managed, detached), seen);
            assertEquals("manager", value(15));
            assertEquals("agent", value(16));
        }
    }

    @Test
    public void clearPropertyReplacementAndReadOnlyRoutesRetainFactoryOwner() throws Exception {
        seed(17, "existing");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            EntityManagerImpl implementation = manager.unwrap(EntityManagerImpl.class);
            UnitOfWork nonSynchronized = (UnitOfWork) implementation.getSession();
            ClientSession readOnly = (ClientSession) implementation.getReadOnlySession();
            try {
                List<Plain> inserted = new ArrayList<>();
                List<Plain> loaded = new ArrayList<>();
                unit.factory.addListener(Plain.class, PreInsert.class, inserted::add);
                unit.factory.addListener(Plain.class, PostLoad.class, loaded::add);
                implementation.setProperties(new HashMap<>(Map.of("listener.test.marker", "replaced")));
                Plain nonSynchronizedRead = (Plain) nonSynchronized.readObject(Plain.class);
                assertEquals(17, nonSynchronizedRead.id);
                assertEquals(List.of(nonSynchronizedRead), loaded);
                loaded.clear();
                Plain direct = new Plain(35, "owned client");
                readOnly.writeObject(direct);
                assertEquals(List.of(direct), inserted);
                assertEquals("owned client", value(35));
                Plain first = new Plain(18, "first");
                persist(manager, first);
                manager.clear();
                Plain second = new Plain(19, "second");
                persist(manager, second);
                assertEquals(List.of(direct, first, second), inserted);
                Plain found = manager.find(Plain.class, 17);
                assertEquals(List.of(found), loaded);
                assertEquals("replaced", manager.getProperties().get("listener.test.marker"));
                assertFalse(manager.getProperties().values().stream().anyMatch(value -> value instanceof Consumer));
            } finally {
                nonSynchronized.release();
            }
        }
    }

    @Test
    public void wrapperManagerAgentAndUnwrapFactoryViewsShareOneRegistry() throws Exception {
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager();
                EntityAgent agent = unit.factory.createEntityAgent()) {
            List<String> seen = new ArrayList<>();
            unit.factory.addListener(Plain.class, PreInsert.class, entity -> seen.add("wrapper"));
            manager.getEntityManagerFactory().addListener(Plain.class, PreInsert.class, entity -> seen.add("manager"));
            agent.getEntityManagerFactory().addListener(Plain.class, PreInsert.class, entity -> seen.add("agent"));
            unit.wrapper().unwrap().addListener(Plain.class, PreInsert.class, entity -> seen.add("unwrap"));
            agent.insert(new Plain(20, "views"));
            assertEquals(List.of("wrapper", "manager", "agent", "unwrap"), seen);
            assertSame(manager.getEntityManagerFactory(), agent.getEntityManagerFactory());
            assertEquals("views", value(20));
        }
    }

    @Test
    public void registrationRejectsInvalidInputsWithoutPublishingAndAcceptsUnmatchedType() throws Exception {
        try (Unit unit = new Unit(); EntityAgent agent = unit.factory.createEntityAgent()) {
            AtomicInteger calls = new AtomicInteger();
            Consumer<Plain> consumer = entity -> calls.incrementAndGet();
            assertThrows(NullPointerException.class, () -> unit.factory.addListener(null, PreInsert.class, consumer));
            assertThrows(NullPointerException.class, () -> unit.factory.addListener(Plain.class, null, consumer));
            assertThrows(NullPointerException.class, () -> unit.factory.addListener(Plain.class, PreInsert.class, null));
            assertThrows(IllegalArgumentException.class, () -> unit.factory.addListener(Plain.class, Deprecated.class, consumer));
            assertThrows(IllegalArgumentException.class, () -> unit.factory.addListener(Plain.class, PostCreate.class, consumer));
            EntityListenerRegistration unmatched = unit.factory.addListener(String.class, PreInsert.class, value -> calls.incrementAndGet());
            agent.insert(new Plain(21, "unmatched"));
            assertEquals(0, calls.get());
            unmatched.cancel();
            unmatched.cancel();
        }
    }

    @Test
    public void reentrantSelfCancelCancelOtherAndAddUseStableSnapshots() throws Exception {
        try (Unit unit = new Unit(); EntityAgent agent = unit.factory.createEntityAgent()) {
            List<String> seen = new ArrayList<>();
            AtomicReference<EntityListenerRegistration> first = new AtomicReference<>();
            AtomicReference<EntityListenerRegistration> second = new AtomicReference<>();
            first.set(unit.factory.addListener(Plain.class, PreInsert.class, entity -> {
                seen.add("first");
                first.get().cancel();
                second.get().cancel();
                unit.factory.addListener(Plain.class, PreInsert.class, later -> seen.add("later"));
            }));
            second.set(unit.factory.addListener(Plain.class, PreInsert.class, entity -> seen.add("second")));
            agent.insert(new Plain(22, "first event"));
            assertEquals(List.of("first", "second"), seen);
            seen.clear();
            agent.insert(new Plain(23, "second event"));
            assertEquals(List.of("later"), seen);
        }
    }

    @Test
    public void managedRuntimeFailureStopsSiblingsMarksRollbackAndPreventsSql() throws Exception {
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            IllegalStateException marker = new IllegalStateException("programmatic pre-persist failure");
            List<String> seen = new ArrayList<>();
            unit.factory.addListener(Plain.class, PrePersist.class, entity -> {
                seen.add("first");
                throw marker;
            });
            unit.factory.addListener(Plain.class, PrePersist.class, entity -> seen.add("later"));
            manager.getTransaction().begin();
            try {
                RuntimeException actual = assertThrows(RuntimeException.class, () -> manager.persist(new Plain(24, "abort")));
                assertCause(marker, actual);
                assertTrue(manager.getTransaction().getRollbackOnly());
                assertEquals(List.of("first"), seen);
            } finally {
                rollback(manager);
            }
            assertEquals(0, count(24));
        }
    }

    @Test
    public void agentRuntimeFailureStopsEntityAndValidationAndPreventsSql() throws Exception {
        try (Unit unit = new Unit("CALLBACK"); EntityAgent agent = unit.factory.createEntityAgent()) {
            IllegalStateException marker = new IllegalStateException("programmatic pre-insert failure");
            unit.factory.addListener(Ordered.class, PreInsert.class, entity -> {
                entity.trace.add("runtime");
                throw marker;
            });
            unit.factory.addListener(Ordered.class, PreInsert.class, entity -> entity.trace.add("later"));
            Ordered entity = new Ordered();
            entity.id = 25;
            entity.value = "valid";
            RuntimeException actual = assertThrows(RuntimeException.class, () -> agent.insert(entity));
            assertCause(marker, actual);
            assertEquals(List.of("metadata", "runtime"), entity.trace);
            assertEquals(0, tableCount("EA_EMFL_ORDERED", 25));
        }
    }

    @Test
    public void errorAbortsLaterConsumersWithoutClaimingRollbackPolicy() throws Exception {
        try (Unit unit = new Unit(); EntityAgent agent = unit.factory.createEntityAgent()) {
            AssertionError marker = new AssertionError("programmatic callback error");
            AtomicInteger later = new AtomicInteger();
            unit.factory.addListener(Plain.class, PreInsert.class, entity -> { throw marker; });
            unit.factory.addListener(Plain.class, PreInsert.class, entity -> later.incrementAndGet());
            assertSame(marker, assertThrows(AssertionError.class, () -> agent.insert(new Plain(26, "abort"))));
            assertEquals(0, later.get());
            assertEquals(0, count(26));
        }
    }

    @Test
    public void sharedServerFactoriesKeepExistingContextsIsolatedAndNativeSessionsSilent() throws Exception {
        try (Unit unit = new Unit()) {
            EntityManagerFactory peer = unit.anotherFactory();
            assertNotSame(unit.factory, peer);
            assertSame(unit.server(), peer.unwrap(JpaEntityManagerFactory.class).getServerSession());
            EntityManager firstManager = unit.factory.createEntityManager();
            try (EntityManager peerManager = peer.createEntityManager();
                    EntityAgent firstAgent = unit.factory.createEntityAgent(); EntityAgent peerAgent = peer.createEntityAgent()) {
                firstManager.unwrap(JpaEntityManager.class).getActiveSession();
                peerManager.unwrap(JpaEntityManager.class).getActiveSession();
                List<Plain> first = new ArrayList<>();
                List<Plain> second = new ArrayList<>();
                EntityListenerRegistration token = unit.factory.addListener(Plain.class, PreInsert.class, first::add);
                peer.addListener(Plain.class, PreInsert.class, second::add);
                Plain a = new Plain(27, "first manager");
                Plain b = new Plain(28, "peer manager");
                persist(firstManager, a);
                persist(peerManager, b);
                Plain c = new Plain(29, "first agent");
                Plain d = new Plain(30, "peer agent");
                firstAgent.insert(c);
                peerAgent.insert(d);
                assertEquals(List.of(a, c), first);
                assertEquals(List.of(b, d), second);
                ClientSession nativeSession = unit.server().acquireClientSession();
                try {
                    nativeSession.writeObject(new Plain(31, "native"));
                } finally {
                    nativeSession.release();
                }
                assertEquals(List.of(a, c), first);
                assertEquals(List.of(b, d), second);
                assertEquals("native", value(31));
                token.cancel();
                firstAgent.insert(new Plain(32, "cancelled"));
                assertEquals(List.of(a, c), first);
                firstManager.close();
                unit.factory.close();
                Plain after = new Plain(33, "peer survives");
                peerAgent.insert(after);
                assertEquals(List.of(b, d, after), second);
                assertEquals("peer survives", value(33));
            } finally {
                if (firstManager.isOpen()) {
                    firstManager.close();
                }
            }
        }
    }

    @Test
    public void closedFactoryAndDelegateViewsRejectNewRegistrations() throws Exception {
        try (Unit unit = new Unit()) {
            EntityManagerFactoryDelegate view = unit.wrapper().unwrap();
            EntityListenerRegistration token = unit.factory.addListener(Plain.class, PreInsert.class, entity -> { });
            unit.factory.close();
            assertThrows(IllegalStateException.class, () -> unit.factory.addListener(Plain.class, PreInsert.class, entity -> { }));
            assertThrows(IllegalStateException.class, () -> view.addListener(Plain.class, PreInsert.class, entity -> { }));
            token.cancel();
            token.cancel();
        }
    }

    @Test
    public void sharedCacheRefreshMovesJpaPhasesToFinalTargetAndPreservesNativeEvents() throws Exception {
        seedOrdered(40, "before");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered target = manager.find(Ordered.class, 40);
            Object cached = unit.server().getIdentityMapAccessor().getFromIdentityMap(40, Ordered.class);
            assertNotNull(cached);
            assertNotSame(cached, target);
            target.clearLoadTrace();
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            try {
                changeValue("EA_EMFL_ORDERED", 40, "refreshed");
                manager.refresh(target);
                assertEquals(List.of(target), runtime);
                assertLoaded(target, "refreshed");
                assertEquals(List.of("refresh", "merge"), nativeEvents.stream().map(NativeEvent::kind).toList());
                assertSame(cached, nativeEvents.get(0).entity());
                assertSame(target, nativeEvents.get(1).entity());
                assertNoQueryMarker(nativeEvents.get(0).query());
                assertEquals("refreshed", tableValue("EA_EMFL_ORDERED", 40));
            } finally {
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    @Test
    public void transactionalRefreshDeliversJpaPhasesOnce() throws Exception {
        refreshOrdered(true, false);
    }

    @Test
    public void refreshCallbackMutationRemainsDirtyForSharedAndEarlyTransactionPaths() throws Exception {
        for (boolean earlyTransaction : new boolean[] {false, true}) {
            int id = earlyTransaction ? 51 : 50;
            seedOrdered(id, "before");
            try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
                Ordered target = manager.find(Ordered.class, id);
                target.clearLoadTrace();
                changeValue("EA_EMFL_ORDERED", id, "refreshed");
                List<Ordered> runtime = new ArrayList<>();
                unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
                if (earlyTransaction) {
                    manager.getTransaction().begin();
                    manager.unwrap(JpaEntityManager.class).getUnitOfWork().beginEarlyTransaction();
                }
                try {
                    manager.refresh(target);
                    assertEquals(List.of(target), runtime);
                    assertLoaded(target, "refreshed");
                    assertTrue(manager.contains(target));
                    if (!earlyTransaction) {
                        manager.getTransaction().begin();
                    }
                    manager.flush();
                    manager.getTransaction().commit();
                    assertEquals("runtime:refreshed", tableValue("EA_EMFL_ORDERED", id));
                    assertEquals(List.of(target), runtime);
                } finally {
                    rollback(manager);
                }
            }
        }
    }

    @Test
    public void protectedCacheRefreshDeliversJpaPhasesOnce() throws Exception {
        refreshOrdered(false, true);
    }

    @Test
    public void refreshHintedQueryWithExistingTargetDeliversOneLoadPhase() throws Exception {
        refreshQuery(false);
    }

    @Test
    public void refreshHintedQueryWithNewTargetDeliversOneLoadPhase() throws Exception {
        refreshQuery(true);
    }

    @Test
    public void duplicateRefreshRowsDeliverOnceForExistingTargetAndQueryReuse() throws Exception {
        duplicateRefreshRows(false);
    }

    @Test
    public void duplicateRefreshRowsDeliverOnceForNewTargetAndQueryReuse() throws Exception {
        duplicateRefreshRows(true);
    }

    @Test
    public void duplicateEntityProjectionRefreshDeliversOnceForExistingTarget() throws Exception {
        duplicateRefreshRows(false, ResultRoute.PROJECTION);
    }

    @Test
    public void duplicateEntityProjectionRefreshDeliversOnceForNewTarget() throws Exception {
        duplicateRefreshRows(true, ResultRoute.PROJECTION);
    }

    @Test
    public void standardResultStreamRefreshEagerlyDeliversOnceForExistingTarget() throws Exception {
        duplicateRefreshRows(false, ResultRoute.STREAM);
    }

    @Test
    public void standardResultStreamRefreshEagerlyDeliversOnceForNewTarget() throws Exception {
        duplicateRefreshRows(true, ResultRoute.STREAM);
    }

    @Test
    public void scalarRefreshProjectionDoesNotLoadEntities() throws Exception {
        seedOrdered(55, "scalar before");
        seed(56, "first scalar row");
        seed(57, "second scalar row");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered target = manager.find(Ordered.class, 55);
            Ordered cached = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(55, Ordered.class);
            target.clearLoadTrace();
            cached.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 55, "scalar refreshed");
            List<Object> runtime = new ArrayList<>();
            unit.factory.addListener(Object.class, PostLoad.class, runtime::add);
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            try {
                List<Object[]> results = manager.createQuery(
                        "select o.value, p.id from EmfListenerOrdered o, EmfListenerPlain p "
                        + "where o.id = 55 and p.id in (56, 57) order by p.id", Object[].class)
                        .setHint(QueryHints.REFRESH, true).getResultList();
                assertEquals(2, results.size());
                assertEquals(List.of("scalar refreshed", 56), List.of(results.get(0)));
                assertEquals(List.of("scalar refreshed", 57), List.of(results.get(1)));
                assertTrue(runtime.isEmpty());
                assertTrue(nativeEvents.isEmpty());
                assertNoLoadPhase(target);
                assertNoLoadPhase(cached);
                assertEquals("scalar before", target.value);
                assertEquals("scalar refreshed", tableValue("EA_EMFL_ORDERED", 55));
            } finally {
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    @Test
    public void eagerCascadeRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, true);
    }

    @Test
    public void eagerCascadeRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, true);
    }

    @Test
    public void metadataOnlyCascadeRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, false);
    }

    @Test
    public void metadataOnlyCascadeRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, false);
    }

    @Test
    public void joinedCascadeRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, true, CascadeRoute.JOIN);
    }

    @Test
    public void joinedCascadeRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, true, CascadeRoute.JOIN);
    }

    @Test
    public void metadataOnlyJoinedRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, false, CascadeRoute.JOIN);
    }

    @Test
    public void metadataOnlyJoinedRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, false, CascadeRoute.JOIN);
    }

    @Test
    public void batchCascadeRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, true, CascadeRoute.BATCH);
    }

    @Test
    public void batchCascadeRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, true, CascadeRoute.BATCH);
    }

    @Test
    public void metadataOnlyBatchRefreshKeepsExistingChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(false, false, CascadeRoute.BATCH);
    }

    @Test
    public void metadataOnlyBatchRefreshKeepsNewChildCacheSourcesCallbackFree() throws Exception {
        eagerCascadeSources(true, false, CascadeRoute.BATCH);
    }

    @Test
    public void cursoredRefreshInitialAndLaterRowsLeaveNoActiveOperation() throws Exception {
        nativeCursorRefresh(false, CursorRoute.COMPLETE);
    }

    @Test
    public void scrollableRefreshLaterRowsLeaveNoActiveOperation() throws Exception {
        nativeCursorRefresh(true, CursorRoute.COMPLETE);
    }

    @Test
    public void cursoredRefreshEarlyCloseLeavesNoActiveOperation() throws Exception {
        nativeCursorRefresh(false, CursorRoute.EARLY_CLOSE);
    }

    @Test
    public void scrollableRefreshEarlyCloseLeavesNoActiveOperation() throws Exception {
        nativeCursorRefresh(true, CursorRoute.EARLY_CLOSE);
    }

    @Test
    public void cursoredRefreshCallbackFailureAllowsSameQueryAndContextReuse() throws Exception {
        nativeCursorRefresh(false, CursorRoute.CALLBACK_FAILURE);
    }

    @Test
    public void scrollableRefreshCallbackFailureAllowsSameQueryAndContextReuse() throws Exception {
        nativeCursorRefresh(true, CursorRoute.CALLBACK_FAILURE);
    }

    @Test
    public void reentrantRefreshKeepsTargetsAndQueriesIsolated() throws Exception {
        seedOrdered(43, "outer before");
        seedOrdered(44, "inner before");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered outer = manager.find(Ordered.class, 43);
            Ordered inner = manager.find(Ordered.class, 44);
            outer.clearLoadTrace();
            inner.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 43, "outer refreshed");
            changeValue("EA_EMFL_ORDERED", 44, "inner refreshed");
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> {
                recordLoaded(entity, runtime);
                if (entity == outer) {
                    manager.refresh(inner);
                }
            });
            manager.refresh(outer);
            assertEquals(List.of(outer, inner), runtime);
            assertLoaded(outer, "outer refreshed");
            assertLoaded(inner, "inner refreshed");
        }
    }

    @Test
    public void callbackFailureDuringRefreshAbortsLaterPhasesAndAllowsFreshContextRetry() throws Exception {
        seedOrdered(45, "before");
        try (Unit unit = new Unit()) {
            try (EntityManager manager = unit.factory.createEntityManager()) {
                Ordered target = manager.find(Ordered.class, 45);
                target.clearLoadTrace();
                changeValue("EA_EMFL_ORDERED", 45, "refreshed");
                IllegalStateException marker = new IllegalStateException("owned refresh callback failure");
                EntityListenerRegistration throwing = unit.factory.addListener(Ordered.class, PostLoad.class, entity -> {
                    assertSame(target, entity);
                    entity.recordLoadPhase("runtime");
                    throw marker;
                });
                AtomicInteger later = new AtomicInteger();
                EntityListenerRegistration sibling = unit.factory.addListener(Ordered.class, PostLoad.class, entity -> later.incrementAndGet());
                manager.getTransaction().begin();
                try {
                    assertCause(marker, assertThrows(RuntimeException.class, () -> manager.refresh(target)));
                    assertTrue(manager.getTransaction().getRollbackOnly());
                    assertEquals("metadata>runtime>", target.loadTrace);
                    assertSame(target, target.metadataLoadTarget);
                    assertNull(target.entityLoadTarget);
                    assertEquals(0, later.get());
                } finally {
                    throwing.cancel();
                    sibling.cancel();
                    rollback(manager);
                }
            }
            try (EntityManager retry = unit.factory.createEntityManager()) {
                Ordered target = retry.find(Ordered.class, 45);
                target.clearLoadTrace();
                List<Ordered> runtime = new ArrayList<>();
                unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
                retry.refresh(target);
                assertEquals(List.of(target), runtime);
                assertLoaded(target, "refreshed");
            }
        }
    }

    @Test
    public void nativeRefreshFailureRestoresQueryMarkerBeforeRetry() throws Exception {
        seedOrdered(46, "before");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered target = manager.find(Ordered.class, 46);
            target.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 46, "refreshed");
            AtomicReference<DatabaseQuery> failedQuery = new AtomicReference<>();
            IllegalStateException marker = new IllegalStateException("owned native refresh failure");
            DescriptorEventAdapter failure = new DescriptorEventAdapter() {
                @Override
                public void postRefresh(DescriptorEvent event) {
                    failedQuery.set(event.getQuery());
                    throw marker;
                }
            };
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(failure);
            try {
                assertCause(marker, assertThrows(RuntimeException.class, () -> manager.refresh(target)));
            } finally {
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(failure);
            }
            assertNotNull(failedQuery.get());
            assertNoQueryMarker(failedQuery.get());
            assertEquals("", target.loadTrace);
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
            manager.refresh(target);
            assertEquals(List.of(target), runtime);
            assertLoaded(target, "refreshed");
        }
    }

    @Test
    public void repeatedCyclicCascadeRefreshDeliversOncePerManagedEntity() throws Exception {
        Ordered parent = ordered(47, "parent before");
        Ordered child = ordered(48, "child before");
        try (EntityAgent agent = fixture.createEntityAgent()) {
            agent.insert(child);
            parent.related = child;
            parent.secondary = child;
            agent.insert(parent);
            child.related = parent;
            agent.update(child);
        }
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered target = manager.find(Ordered.class, 47);
            Ordered related = target.related;
            assertSame(related, target.secondary);
            assertSame(target, related.related);
            target.clearLoadTrace();
            related.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 47, "parent refreshed");
            changeValue("EA_EMFL_ORDERED", 48, "child refreshed");
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
            manager.refresh(target);
            assertEquals("Callback IDs " + runtime.stream().map(entity -> entity.id).toList()
                    + "; parent trace " + target.loadTrace + "; child trace " + related.loadTrace
                    + "; parent value " + target.value + "; child value " + related.value, 2, runtime.size());
            assertEquals(1, runtime.stream().filter(entity -> entity == target).count());
            assertEquals(1, runtime.stream().filter(entity -> entity == related).count());
            assertLoaded(target, "parent refreshed");
            assertLoaded(related, "child refreshed");
            assertSame(related, target.secondary);
            assertSame(target, related.related);
            try {
                manager.getTransaction().begin();
                manager.flush();
                manager.getTransaction().commit();
                assertEquals("runtime:parent refreshed", tableValue("EA_EMFL_ORDERED", 47));
                assertEquals("runtime:child refreshed", tableValue("EA_EMFL_ORDERED", 48));
                assertEquals(2, runtime.size());
            } finally {
                rollback(manager);
            }
        }
    }

    @Test
    public void readOnlyRefreshPreservesMetadataAndNativeSourceCallbacks() throws Exception {
        seedOrdered(49, "before");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            manager.find(Ordered.class, 49);
            Ordered cached = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(49, Ordered.class);
            assertNotNull(cached);
            cached.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 49, "read only refreshed");
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            try {
                Ordered result = manager.createQuery("select o from EmfListenerOrdered o where o.id = 49", Ordered.class)
                        .setHint(QueryHints.REFRESH, true).setHint(QueryHints.READ_ONLY, true).getSingleResult();
                assertSame(cached, result);
                assertEquals("metadata>entity>", result.loadTrace);
                assertSame(result, result.metadataLoadTarget);
                assertSame(result, result.entityLoadTarget);
                assertEquals("read only refreshed", result.value);
                assertEquals(List.of("refresh"), nativeEvents.stream().map(NativeEvent::kind).toList());
                assertSame(result, nativeEvents.get(0).entity());
                assertNoQueryMarker(nativeEvents.get(0).query());
            } finally {
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    @Test
    public void h2ExplicitRefreshPreservesOldNewContextsAndCrossGenerationCancellation() throws Exception {
        try (LifecycleUnit lifecycle = new LifecycleUnit(); Unit unit = lifecycle.unit;
                EntityManager oldManager = unit.factory.createEntityManager(); EntityAgent oldAgent = unit.factory.createEntityAgent()) {
            EntityManagerFactoryDelegate old = unit.wrapper().unwrap();
            ServerSession oldSession = unit.server();
            List<Plain> seen = new ArrayList<>();
            EntityListenerRegistration initial = unit.factory.addListener(Plain.class, PreInsert.class, seen::add);
            unit.wrapper().refreshMetadata(refreshProperties());
            assertNotSame(old, unit.wrapper().unwrap());
            assertNotSame(oldSession, unit.server());
            try (EntityManager nextManager = unit.factory.createEntityManager(); EntityAgent nextAgent = unit.factory.createEntityAgent()) {
                AtomicInteger addedThroughOld = new AtomicInteger();
                EntityListenerRegistration late = old.addListener(Plain.class, PreInsert.class, entity -> addedThroughOld.incrementAndGet());
                Plain a = new Plain(101, "old em");
                Plain b = new Plain(102, "new em");
                Plain c = new Plain(103, "old agent");
                Plain d = new Plain(104, "new agent");
                persist(oldManager, a);
                persist(nextManager, b);
                oldAgent.insert(c);
                nextAgent.insert(d);
                assertEquals(List.of(a, b, c, d), seen);
                assertEquals(4, addedThroughOld.get());
                assertEquals(4, lifecycle.count());
                initial.cancel();
                oldAgent.insert(new Plain(105, "old cancelled"));
                nextAgent.insert(new Plain(106, "new cancelled"));
                assertEquals(4, seen.size());
                assertEquals(6, addedThroughOld.get());
                late.cancel();
                oldAgent.insert(new Plain(107, "old silent"));
                nextAgent.insert(new Plain(108, "new silent"));
                assertEquals(6, addedThroughOld.get());
            } finally {
                unit.retired.add(old);
            }
        }
    }

    @Test
    public void h2ImplicitExpiredMetadataReplacementPreservesRegistrations() throws Exception {
        try (LifecycleUnit lifecycle = new LifecycleUnit(); Unit unit = lifecycle.unit;
                EntityManager oldManager = unit.factory.createEntityManager(); EntityAgent oldAgent = unit.factory.createEntityAgent()) {
            EntityManagerFactoryDelegate old = unit.wrapper().unwrap();
            ServerSession oldSession = unit.server();
            List<Plain> seen = new ArrayList<>();
            unit.factory.addListener(Plain.class, PreInsert.class, seen::add);
            old.getSetupImpl().refreshMetadata(refreshProperties());
            assertTrue(old.getSetupImpl().isMetadataExpired());
            try (EntityManager next = unit.factory.createEntityManager()) {
                assertNotSame(old, unit.wrapper().unwrap());
                assertNotSame(oldSession, unit.server());
                Plain a = new Plain(111, "old");
                Plain b = new Plain(112, "new");
                oldAgent.insert(a);
                persist(next, b);
                assertEquals(List.of(a, b), seen);
                assertEquals(2, lifecycle.count());
            } finally {
                unit.retired.add(old);
            }
        }
    }

    @Test
    public void h2FailedRefreshRestoresOldDelegateAndRegistrations() throws Exception {
        try (LifecycleUnit lifecycle = new LifecycleUnit(); Unit unit = lifecycle.unit;
                EntityAgent oldAgent = unit.factory.createEntityAgent()) {
            EntityManagerFactoryDelegate old = unit.wrapper().unwrap();
            ServerSession oldSession = unit.server();
            List<Plain> seen = new ArrayList<>();
            unit.factory.addListener(Plain.class, PreInsert.class, seen::add);
            Map<String, Object> properties = new HashMap<>(refreshProperties());
            properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER,
                    (SessionCustomizer) session -> { throw new IllegalStateException("owned failed refresh"); });
            assertThrows(RuntimeException.class, () -> unit.wrapper().refreshMetadata(properties));
            assertSame(old, unit.wrapper().unwrap());
            assertSame(oldSession, unit.server());
            assertTrue(unit.factory.isOpen());
            Plain a = new Plain(121, "restored old");
            oldAgent.insert(a);
            try (EntityManager current = unit.factory.createEntityManager()) {
                Plain b = new Plain(122, "restored new context");
                persist(current, b);
                assertEquals(List.of(a, b), seen);
            }
            assertEquals(2, lifecycle.count());
        }
    }

    @Test
    public void h2OldDelegateCloseRetiresOnlyItsContexts() throws Exception {
        closeGeneration(false, false);
    }

    @Test
    public void h2CurrentDelegateCloseSilencesBothGenerations() throws Exception {
        closeGeneration(true, false);
    }

    @Test
    public void h2WrapperCloseSilencesBothGenerations() throws Exception {
        closeGeneration(true, true);
    }

    @Test
    public void h2DirectSessionFactoryAndStandaloneDelegateOwnRegistrations() throws Exception {
        try (LifecycleUnit lifecycle = new LifecycleUnit(); Unit unit = lifecycle.unit) {
            EntityManagerFactoryImpl direct = new EntityManagerFactoryImpl(unit.server());
            EntityManagerFactoryDelegate standalone = new EntityManagerFactoryDelegate(unit.server(), null);
            List<Plain> directSeen = new ArrayList<>();
            List<Plain> standaloneSeen = new ArrayList<>();
            try {
                direct.addListener(Plain.class, PreInsert.class, directSeen::add);
                EntityListenerRegistration token = standalone.addListener(Plain.class, PreInsert.class, standaloneSeen::add);
                try (EntityManager first = direct.createEntityManager(); EntityManager second = standalone.createEntityManager()) {
                    Plain a = new Plain(141, "direct");
                    Plain b = new Plain(142, "standalone");
                    persist(first, a);
                    persist(second, b);
                    assertEquals(List.of(a), directSeen);
                    assertEquals(List.of(b), standaloneSeen);
                    assertEquals(2, lifecycle.count());
                }
                standalone.close();
                token.cancel();
                assertThrows(IllegalStateException.class, () -> standalone.addListener(Plain.class, PreInsert.class, entity -> { }));
            } finally {
                if (standalone.isOpen()) {
                    standalone.close();
                }
                if (direct.isOpen()) {
                    direct.close();
                }
            }
        }
    }

    private static void closeGeneration(boolean current, boolean wrapper) throws Exception {
        try (LifecycleUnit lifecycle = new LifecycleUnit(); Unit unit = lifecycle.unit) {
            unit.anotherFactory(); // Retain an independent old-generation owner and its live server.
            EntityManagerFactoryDelegate old = unit.wrapper().unwrap();
            try (EntityAgent oldAgent = unit.factory.createEntityAgent()) {
                AtomicInteger seen = new AtomicInteger();
                unit.factory.addListener(Plain.class, PreInsert.class, entity -> seen.incrementAndGet());
                unit.wrapper().refreshMetadata(refreshProperties());
                unit.anotherFactory(); // Retain the new server while testing dispatcher retirement.
                EntityManagerFactoryDelegate replacement = unit.wrapper().unwrap();
                assertNotSame(old, replacement);
                try (EntityAgent nextAgent = unit.factory.createEntityAgent()) {
                    oldAgent.insert(new Plain(131, "old before close"));
                    nextAgent.insert(new Plain(132, "new before close"));
                    assertEquals(2, seen.get());
                    if (current) {
                        if (wrapper) {
                            unit.factory.close();
                        } else {
                            replacement.close();
                        }
                    } else {
                        old.close();
                    }
                    oldAgent.insert(new Plain(133, "old after close"));
                    nextAgent.insert(new Plain(134, "new after close"));
                    assertEquals(current ? 2 : 3, seen.get());
                    assertEquals(4, lifecycle.count());
                    assertThrows(IllegalStateException.class, () -> old.addListener(Plain.class, PreInsert.class, entity -> { }));
                } finally {
                    unit.retired.add(old);
                }
            }
        }
    }

    private void refreshOrdered(boolean transaction, boolean protectedCache) throws Exception {
        seedOrdered(41, "before");
        SessionCustomizer customizer = protectedCache
                ? session -> session.getClassDescriptor(Ordered.class).setCacheIsolation(CacheIsolationType.PROTECTED) : null;
        try (Unit unit = new Unit("NONE", null, customizer); EntityManager manager = unit.factory.createEntityManager()) {
            if (protectedCache) {
                assertEquals(CacheIsolationType.PROTECTED, unit.server().getDescriptor(Ordered.class).getCacheIsolation());
            }
            Ordered target = manager.find(Ordered.class, 41);
            target.clearLoadTrace();
            changeValue("EA_EMFL_ORDERED", 41, "refreshed");
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
            if (transaction) {
                manager.getTransaction().begin();
                manager.unwrap(JpaEntityManager.class).getUnitOfWork().beginEarlyTransaction();
            }
            try {
                manager.refresh(target);
                assertEquals(List.of(target), runtime);
                assertLoaded(target, "refreshed");
            } finally {
                rollback(manager);
            }
            assertEquals("refreshed", tableValue("EA_EMFL_ORDERED", 41));
        }
    }

    private void refreshQuery(boolean newTarget) throws Exception {
        seedOrdered(42, "before");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered previous = manager.find(Ordered.class, 42);
            Object cached = unit.server().getIdentityMapAccessor().getFromIdentityMap(42, Ordered.class);
            assertNotNull(cached);
            previous.clearLoadTrace();
            if (newTarget) {
                manager.clear();
            }
            changeValue("EA_EMFL_ORDERED", 42, "refreshed");
            List<Ordered> runtime = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            try {
                Ordered result = manager.createQuery("select o from EmfListenerOrdered o where o.id = 42", Ordered.class)
                        .setHint(QueryHints.REFRESH, true).getSingleResult();
                if (newTarget) {
                    assertNotSame(previous, result);
                } else {
                    assertSame(previous, result);
                }
                assertEquals(List.of(result), runtime);
                assertLoaded(result, "refreshed");
                assertEquals(newTarget ? List.of("refresh", "clone", "merge") : List.of("refresh", "merge"),
                        nativeEvents.stream().map(NativeEvent::kind).toList());
                assertSame(cached, nativeEvents.get(0).entity());
                for (NativeEvent event : nativeEvents.subList(1, nativeEvents.size())) {
                    assertSame(result, event.entity());
                    assertTrue("Native target phases precede JPA callbacks", event.loadTrace() == null || event.loadTrace().isEmpty());
                }
                assertNoQueryMarker(nativeEvents.get(0).query());
                manager.getTransaction().begin();
                manager.flush();
                manager.getTransaction().commit();
                assertEquals("runtime:refreshed", tableValue("EA_EMFL_ORDERED", 42));
                assertEquals(List.of(result), runtime);
            } finally {
                rollback(manager);
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    private void duplicateRefreshRows(boolean newTarget) throws Exception {
        duplicateRefreshRows(newTarget, ResultRoute.ENTITY);
    }

    private enum ResultRoute { ENTITY, PROJECTION, STREAM }

    private void duplicateRefreshRows(boolean newTarget, ResultRoute route) throws Exception {
        seedOrdered(52, "before");
        seed(53, "first cross row");
        seed(54, "second cross row");
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            Ordered previous = manager.find(Ordered.class, 52);
            if (newTarget) {
                manager.clear();
            }
            List<Ordered> runtime = new ArrayList<>();
            List<String> observedValues = new ArrayList<>();
            unit.factory.addListener(Ordered.class, PostLoad.class, entity -> {
                runtime.add(entity);
                observedValues.add(entity.value);
                entity.recordLoadPhase("runtime");
                entity.setValue("runtime:" + entity.value);
            });
            String from = " from EmfListenerOrdered o, EmfListenerPlain p where o.id = :id and p.id in (:a, :b) order by p.id";
            TypedQuery<?> query = route == ResultRoute.PROJECTION
                    ? manager.createQuery("select o, p.id" + from, Object[].class)
                    : manager.createQuery("select o" + from, Ordered.class);
            query.setParameter("id", 52).setParameter("a", 53).setParameter("b", 54).setHint(QueryHints.REFRESH, true);
            assertEquals(route == ResultRoute.PROJECTION, query.unwrap(JpaQuery.class).getDatabaseQuery().isReportQuery());
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            try {
                for (int execution = 1; execution <= 2; execution++) {
                    previous.clearLoadTrace();
                    Ordered cached = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(52, Ordered.class);
                    assertNotNull(cached);
                    cached.clearLoadTrace();
                    runtime.clear();
                    observedValues.clear();
                    nativeEvents.clear();
                    String databaseValue = "duplicate refreshed " + execution;
                    changeValue("EA_EMFL_ORDERED", 52, databaseValue);
                    try {
                        // No DISTINCT or fetch join: both SQL rows must survive result construction.
                        List<?> results;
                        if (route == ResultRoute.STREAM) {
                            try (Stream<?> stream = query.getResultStream()) {
                                assertEquals("Standard result stream must finish eager callback delivery before iteration", 1, runtime.size());
                                results = stream.toList();
                            }
                        } else {
                            results = query.getResultList();
                        }
                        assertEquals("Cartesian query must expose both result rows", 2, results.size());
                        Ordered target;
                        if (route == ResultRoute.PROJECTION) {
                            Object[] first = (Object[]) results.get(0);
                            Object[] second = (Object[]) results.get(1);
                            assertEquals(2, first.length);
                            assertEquals(2, second.length);
                            assertEquals(53, first[1]);
                            assertEquals(54, second[1]);
                            target = (Ordered) first[0];
                            assertSame(target, second[0]);
                        } else {
                            target = (Ordered) results.get(0);
                            assertSame(target, results.get(1));
                        }
                        assertTrue(manager.contains(target));
                        if (newTarget && execution == 1) {
                            assertNotSame(previous, target);
                        } else {
                            assertSame(previous, target);
                        }
                        assertEquals("One callback phase per identity and query invocation", List.of(target), runtime);
                        assertEquals(List.of(databaseValue), observedValues);
                        assertLoaded(target, databaseValue);
                        assertNativePhases(nativeEvents, cached, List.of("refresh"));
                        assertNativePhases(nativeEvents, target, newTarget && execution == 1
                                ? List.of("clone", "merge", "merge") : List.of("merge", "merge"));
                        assertEquals(newTarget && execution == 1 ? 4 : 3, nativeEvents.size());
                        for (NativeEvent event : nativeEvents) {
                            if (event.entity() == target) {
                                assertTrue("Every physical merge precedes the single JPA phase",
                                        event.loadTrace() == null || event.loadTrace().isEmpty());
                            }
                        }
                        manager.getTransaction().begin();
                        manager.flush();
                        manager.getTransaction().commit();
                        assertEquals("runtime:" + databaseValue, tableValue("EA_EMFL_ORDERED", 52));
                        assertEquals(List.of(target), runtime);
                        previous = target;
                    } finally {
                        rollback(manager);
                    }
                }
            } finally {
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    private void eagerCascadeSources(boolean newChild, boolean runtimeListener) throws Exception {
        eagerCascadeSources(newChild, runtimeListener, CascadeRoute.SELECTION);
    }

    private enum CascadeRoute { SELECTION, JOIN, BATCH }

    @Test
    public void lateDynamicTypeUsesExistingManagerObjectRegistration() throws Exception {
        lateDynamicType(false, Object.class);
    }

    @Test
    public void lateDynamicTypeUsesExistingManagerDynamicRegistration() throws Exception {
        lateDynamicType(false, DynamicEntity.class);
    }

    @Test
    public void lateDynamicTypeUsesExistingAgentObjectRegistration() throws Exception {
        lateDynamicType(true, Object.class);
    }

    @Test
    public void lateDynamicTypeUsesExistingAgentDynamicRegistration() throws Exception {
        lateDynamicType(true, DynamicEntity.class);
    }

    private <E> void lateDynamicType(boolean useAgent, Class<E> filter) throws Exception {
        try (Unit unit = new Unit()) {
            EntityManagerFactory peer = unit.anotherFactory();
            assertNotSame(unit.factory, peer);
            assertSame(unit.server(), peer.unwrap(JpaEntityManagerFactory.class).getServerSession());
            List<Object> ownerEvents = new ArrayList<>();
            List<Object> peerEvents = new ArrayList<>();
            EntityManager manager = unit.factory.createEntityManager();
            try (EntityManager peerManager = peer.createEntityManager();
                    EntityAgent agent = unit.factory.createEntityAgent();
                    EntityAgent peerAgent = peer.createEntityAgent()) {
                UnitOfWork ownerContext = manager.unwrap(JpaEntityManager.class).getUnitOfWork();
                UnitOfWork peerContext = peerManager.unwrap(JpaEntityManager.class).getUnitOfWork();
                EntityListenerRegistration registration = unit.factory.addListener(filter, PreInsert.class, entity -> {
                    ownerEvents.add(entity);
                    DynamicEntity value = (DynamicEntity) entity;
                    value.set("value", "owner:" + value.get("value"));
                });
                EntityListenerRegistration peerRegistration = peer.addListener(filter, PreInsert.class, entity -> {
                    peerEvents.add(entity);
                    DynamicEntity value = (DynamicEntity) entity;
                    value.set("value", "peer:" + value.get("value"));
                });
                JPADynamicHelper helper = new JPADynamicHelper(unit.factory);
                Class<?> dynamicClass = helper.getDynamicClassLoader().createDynamicClass(
                        "org.eclipse.persistence.jpa.entityagent.model.LateListener" + UUID.randomUUID().toString().replace("-", ""));
                JPADynamicTypeBuilder builder = new JPADynamicTypeBuilder(dynamicClass, null, "EA_EMFL_DYNAMIC");
                builder.setPrimaryKeyFields("ROW_ID");
                builder.addDirectMapping("id", int.class, "ROW_ID");
                builder.addDirectMapping("value", String.class, "ROW_VALUE");
                DynamicType type = builder.getType();
                assertFalse("No incidental metadata, native or validation listener may enable this fixture",
                        type.getDescriptor().getEventManager().hasAnyEventListeners());
                boolean installed = false;
                try {
                    helper.addTypes(true, false, type);
                    installed = true;
                    assertSame(ownerContext, manager.unwrap(JpaEntityManager.class).getUnitOfWork());
                    assertSame(peerContext, peerManager.unwrap(JpaEntityManager.class).getUnitOfWork());
                    DynamicEntity first = type.newDynamicEntity().set("id", 91).set("value", "first");
                    insertDynamic(manager, agent, first, useAgent);
                    assertEquals("Pre-existing registration must receive the actual dynamic entity", List.of(first), ownerEvents);
                    assertTrue(peerEvents.isEmpty());
                    assertEquals("owner:first", tableValue("EA_EMFL_DYNAMIC", 91));
                    registration.cancel();
                    DynamicEntity second = type.newDynamicEntity().set("id", 92).set("value", "second");
                    insertDynamic(manager, agent, second, useAgent);
                    assertEquals(List.of(first), ownerEvents);
                    assertEquals("second", tableValue("EA_EMFL_DYNAMIC", 92));
                    DynamicEntity third = type.newDynamicEntity().set("id", 93).set("value", "third");
                    insertDynamic(peerManager, peerAgent, third, useAgent);
                    assertEquals(List.of(first), ownerEvents);
                    assertEquals(List.of(third), peerEvents);
                    assertEquals("peer:third", tableValue("EA_EMFL_DYNAMIC", 93));
                    unit.factory.close();
                    DynamicEntity fourth = type.newDynamicEntity().set("id", 94).set("value", "fourth");
                    insertDynamic(peerManager, peerAgent, fourth, useAgent);
                    assertEquals(List.of(first), ownerEvents);
                    assertEquals(List.of(third, fourth), peerEvents);
                    assertEquals("peer:fourth", tableValue("EA_EMFL_DYNAMIC", 94));
                } finally {
                    registration.cancel();
                    peerRegistration.cancel();
                    rollback(manager);
                    rollback(peerManager);
                    if (installed) {
                        try (EntityManager cleanup = fixture.createEntityManager()) {
                            cleanup.getTransaction().begin();
                            try {
                                cleanup.createNativeQuery("DROP TABLE EA_EMFL_DYNAMIC").executeUpdate();
                                cleanup.getTransaction().commit();
                            } finally {
                                rollback(cleanup);
                            }
                        }
                    }
                }
            } finally {
                if (manager.isOpen()) {
                    manager.close();
                }
            }
        }
    }

    private static void insertDynamic(EntityManager manager, EntityAgent agent, DynamicEntity entity, boolean useAgent) {
        if (useAgent) {
            agent.insert(entity);
        } else {
            manager.getTransaction().begin();
            try {
                manager.persist(entity);
                manager.getTransaction().commit();
            } finally {
                rollback(manager);
            }
        }
    }

    @Test
    public void tablePerClassColdConcreteRefreshSuppressesMetadataSource() throws Exception {
        tablePerClassRefresh(false, false);
    }

    @Test
    public void tablePerClassWarmConcreteRefreshSuppressesMetadataSource() throws Exception {
        tablePerClassRefresh(true, false);
    }

    @Test
    public void tablePerClassColdConcreteRefreshDeliversRuntimeOnManagedTargets() throws Exception {
        tablePerClassRefresh(false, true);
    }

    @Test
    public void tablePerClassWarmConcreteRefreshDeliversRuntimeOnManagedTargets() throws Exception {
        tablePerClassRefresh(true, true);
    }

    private void tablePerClassRefresh(boolean warmQuery, boolean runtimeListener) throws Exception {
        PolymorphicParent parent = new PolymorphicParent();
        parent.id = 81;
        parent.value = "parent before";
        PolymorphicFirst child = new PolymorphicFirst();
        child.id = 82;
        child.value = "child before";
        PolymorphicFirst otherChild = new PolymorphicFirst();
        otherChild.id = 83;
        otherChild.value = "other child before";
        PolymorphicFirst lastChild = new PolymorphicFirst();
        lastChild.id = 84;
        lastChild.value = "last child before";
        try (EntityManager seed = fixture.createEntityManager()) {
            seed.getTransaction().begin();
            seed.persist(child);
            seed.persist(otherChild);
            seed.persist(lastChild);
            seed.persist(parent);
            seed.getTransaction().commit();
        }
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            // Concrete lookup warms the shared child without preparing the polymorphic relationship query.
            List<PolymorphicFirst> managedChildren = List.of(manager.find(PolymorphicFirst.class, 82),
                    manager.find(PolymorphicFirst.class, 83), manager.find(PolymorphicFirst.class, 84));
            for (PolymorphicFirst managed : managedChildren) {
                assertEquals("Concrete fixture declares both callbacks", "metadata>entity>", managed.loadTrace);
            }
            PolymorphicParent target = manager.find(PolymorphicParent.class, 81);
            PolymorphicParent cachedParent = (PolymorphicParent) unit.server().getIdentityMapAccessor()
                    .getFromIdentityMap(81, PolymorphicParent.class);
            List<PolymorphicFirst> cachedChildren = List.of(
                    (PolymorphicFirst) unit.server().getIdentityMapAccessor().getFromIdentityMap(82, PolymorphicFirst.class),
                    (PolymorphicFirst) unit.server().getIdentityMapAccessor().getFromIdentityMap(83, PolymorphicFirst.class),
                    (PolymorphicFirst) unit.server().getIdentityMapAccessor().getFromIdentityMap(84, PolymorphicFirst.class));
            assertNotSame(cachedParent, target);
            for (int index = 0; index < managedChildren.size(); index++) {
                assertNotSame(cachedChildren.get(index), managedChildren.get(index));
            }
            assertNull(target.related);
            try (EntityManager writer = fixture.createEntityManager()) {
                writer.getTransaction().begin();
                assertEquals(1, writer.createNativeQuery("UPDATE EA_EMFL_TPC_PARENT SET RELATED_ID = 82 WHERE ROW_ID = 81")
                        .executeUpdate());
                writer.getTransaction().commit();
            }
            List<ObjectLevelReadQuery> concreteExecutions = new ArrayList<>();
            SessionEventAdapter queryObserver = new SessionEventAdapter() {
                @Override
                public void postExecuteQuery(SessionEvent event) {
                    if (event.getQuery() instanceof ObjectLevelReadQuery query
                            && query.getReferenceClass() == PolymorphicFirst.class) {
                        concreteExecutions.add(query);
                    }
                }
            };
            List<PolymorphicRecord> runtime = new ArrayList<>();
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getEventManager().addListener(queryObserver);
            unit.server().getDescriptor(PolymorphicParent.class).getEventManager().addListener(nativeListener);
            unit.server().getDescriptor(PolymorphicFirst.class).getEventManager().addListener(nativeListener);
            try {
                ObjectLevelReadQuery preparedExecution = null;
                if (warmQuery) {
                    manager.refresh(target);
                    assertSame(managedChildren.get(0), target.related);
                    assertEquals("Warmup must execute the concrete relationship query", 1, concreteExecutions.size());
                    preparedExecution = concreteExecutions.get(0);
                    assertNotNull(preparedExecution.getSelectionCriteria());
                }
                if (runtimeListener) {
                    unit.factory.addListener(PolymorphicRecord.class, PostLoad.class, entity -> {
                        runtime.add(entity);
                        entity.recordLoadPhase("runtime");
                        entity.setValue("runtime:" + entity.value);
                    });
                }
                for (int execution = 0; execution < 2; execution++) {
                    // Reuse the concrete query with another cached identity: a template retains its
                    // original query id, which can suppress a repeat source refresh of the same identity.
                    int selected = execution + (warmQuery ? 1 : 0);
                    PolymorphicFirst managedChild = managedChildren.get(selected);
                    PolymorphicFirst cachedChild = cachedChildren.get(selected);
                    for (PolymorphicRecord entity : List.of(target, cachedParent, managedChildren.get(0),
                            managedChildren.get(1), managedChildren.get(2), cachedChildren.get(0),
                            cachedChildren.get(1), cachedChildren.get(2))) {
                        entity.clearLoadTrace();
                    }
                    nativeEvents.clear();
                    runtime.clear();
                    String prefix = runtimeListener ? "" : "metadata-only:";
                    String parentValue = prefix + "parent " + execution;
                    String childValue = prefix + "child " + execution;
                    changeValue("EA_EMFL_TPC_PARENT", 81, parentValue);
                    changeValue("EA_EMFL_TPC_FIRST", managedChild.id, childValue);
                    // A changed foreign key forces real child selection for both first and cached concrete queries.
                    try (EntityManager writer = fixture.createEntityManager()) {
                        writer.getTransaction().begin();
                        assertEquals(1, writer.createNativeQuery("UPDATE EA_EMFL_TPC_PARENT SET RELATED_ID = ? WHERE ROW_ID = 81")
                                .setParameter(1, managedChild.id).executeUpdate());
                        writer.getTransaction().commit();
                    }
                    int previousExecutions = concreteExecutions.size();
                    manager.refresh(target);
                    assertEquals(previousExecutions + 1, concreteExecutions.size());
                    ObjectLevelReadQuery executed = concreteExecutions.get(previousExecutions);
                    assertTrue(executed.isExecutionClone());
                    assertNotNull(executed.getSelectionCriteria());
                    if (preparedExecution == null) {
                        preparedExecution = executed;
                    } else {
                        assertNotSame("Each refresh uses a private concrete execution query", preparedExecution, executed);
                        assertSame("Concrete executions reuse the prepared selection criteria",
                                preparedExecution.getSelectionCriteria(), executed.getSelectionCriteria());
                        assertEquals(preparedExecution.getQueryId(), executed.getQueryId());
                    }
                    assertSame(managedChild, target.related);
                    assertSame(cachedChild, cachedParent.related);
                    assertSame(cachedParent, unit.server().getIdentityMapAccessor()
                            .getFromIdentityMap(81, PolymorphicParent.class));
                    assertSame(cachedChild, unit.server().getIdentityMapAccessor()
                            .getFromIdentityMap(managedChild.id, PolymorphicFirst.class));
                    for (PolymorphicRecord source : List.of(cachedParent, cachedChild)) {
                        assertEquals("Polymorphic cached source must not receive JPA callbacks", "", source.loadTrace);
                        assertNull(source.metadataLoadTarget);
                        assertNull(source.entityLoadTarget);
                    }
                    for (PolymorphicRecord managed : List.of(target, managedChild)) {
                        assertTrue(manager.contains(managed));
                        assertSame("Managed " + managed.id + " value=" + managed.value + " trace=" + managed.loadTrace
                                + " native=" + nativeEvents.stream().map(event -> event.kind() + ":"
                                + event.entity().getClass().getSimpleName() + ":" + ((PolymorphicRecord) event.entity()).id
                                + ":" + (event.query() == null ? "none" : event.query().getClass().getSimpleName())).toList(), managed, managed.metadataLoadTarget);
                        assertSame(managed, managed.entityLoadTarget);
                        assertEquals(runtimeListener ? "metadata>runtime>entity>" : "metadata>entity>", managed.loadTrace);
                        assertEquals(List.of("merge"), nativeEvents.stream().filter(e -> e.entity() == managed)
                                .map(NativeEvent::kind).toList());
                    }
                    for (PolymorphicRecord source : List.of(cachedParent, cachedChild)) {
                        assertEquals(List.of("refresh"), nativeEvents.stream().filter(e -> e.entity() == source)
                                .map(NativeEvent::kind).toList());
                    }
                    assertEquals(4, nativeEvents.size());
                    assertEquals(runtimeListener ? 2 : 0, runtime.size());
                    if (runtimeListener) {
                        assertEquals(1, runtime.stream().filter(e -> e == target).count());
                        assertEquals(1, runtime.stream().filter(e -> e == managedChild).count());
                    }
                    manager.getTransaction().begin();
                    manager.flush();
                    manager.getTransaction().commit();
                    String mutation = runtimeListener ? "runtime:" : "metadata:";
                    assertEquals(mutation + parentValue, tableValue("EA_EMFL_TPC_PARENT", 81));
                    assertEquals(mutation + childValue, tableValue("EA_EMFL_TPC_FIRST", managedChild.id));
                }
            } finally {
                rollback(manager);
                unit.server().getEventManager().removeListener(queryObserver);
                unit.server().getDescriptor(PolymorphicParent.class).getEventManager().removeListener(nativeListener);
                unit.server().getDescriptor(PolymorphicFirst.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    private void eagerCascadeSources(boolean newChild, boolean runtimeListener, CascadeRoute route) throws Exception {
        Ordered parent = ordered(61, "parent before");
        Ordered child = ordered(62, "child before");
        if (!newChild) {
            parent.related = child;
        }
        try (EntityAgent agent = fixture.createEntityAgent()) {
            agent.insert(child);
            agent.insert(parent);
        }
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            // Warm the shared child without registering it in the context under test.
            try (EntityManager warmer = unit.factory.createEntityManager()) {
                assertNotNull(warmer.find(Ordered.class, 62));
            }
            Ordered target = manager.find(Ordered.class, 61);
            Ordered previousChild = target.related;
            Ordered cachedParent = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(61, Ordered.class);
            Ordered cachedChild = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(62, Ordered.class);
            assertNotNull(cachedParent);
            assertNotNull(cachedChild);
            assertNotSame(cachedParent, target);
            assertFalse(manager.contains(cachedParent));
            assertFalse(manager.contains(cachedChild));
            if (newChild) {
                assertNull(previousChild);
                assertNull(cachedParent.related);
                try (EntityManager writer = fixture.createEntityManager()) {
                    writer.getTransaction().begin();
                    try {
                        assertEquals(1, writer.createNativeQuery("UPDATE EA_EMFL_ORDERED SET RELATED_ID = ? WHERE ROW_ID = ?")
                                .setParameter(1, 62).setParameter(2, 61).executeUpdate());
                        writer.getTransaction().commit();
                    } finally {
                        rollback(writer);
                    }
                }
            } else {
                assertNotNull(previousChild);
                assertNotSame(cachedChild, previousChild);
                assertSame(cachedChild, cachedParent.related);
                assertTrue(manager.contains(previousChild));
                previousChild.clearLoadTrace();
            }
            target.clearLoadTrace();
            cachedParent.clearLoadTrace();
            cachedChild.clearLoadTrace();
            String prefix = runtimeListener ? "" : "metadata-only:";
            String parentValue = prefix + "parent refreshed";
            String childValue = prefix + "child refreshed";
            changeValue("EA_EMFL_ORDERED", 61, parentValue);
            changeValue("EA_EMFL_ORDERED", 62, childValue);
            List<Ordered> runtime = new ArrayList<>();
            if (runtimeListener) {
                unit.factory.addListener(Ordered.class, PostLoad.class, entity -> {
                    runtime.add(entity);
                    entity.recordLoadPhase("runtime");
                    entity.setValue("runtime:" + entity.value);
                });
            }
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            AtomicInteger sqlCalls = new AtomicInteger();
            SessionEventAdapter sqlCounter = new SessionEventAdapter() {
                @Override
                public void preExecuteCall(SessionEvent event) {
                    sqlCalls.incrementAndGet();
                }
            };
            unit.server().getEventManager().addListener(sqlCounter);
            try {
                if (route == CascadeRoute.SELECTION) {
                    manager.refresh(target);
                } else {
                    TypedQuery<Ordered> query = manager.createQuery("select o from EmfListenerOrdered o where o.id = 61", Ordered.class)
                            .setHint(QueryHints.REFRESH, true).setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
                    if (route == CascadeRoute.JOIN) {
                        query.setHint(QueryHints.FETCH, "o.related");
                    } else {
                        query.setHint(QueryHints.BATCH, "o.related").setHint(QueryHints.BATCH_TYPE, "IN");
                    }
                    assertSame(target, query.getSingleResult());
                }
                if (route == CascadeRoute.JOIN) {
                    assertEquals("Joined source must use one SQL result", 1, sqlCalls.get());
                } else if (route == CascadeRoute.BATCH) {
                    assertEquals("Batch source must execute root and related SQL", 2, sqlCalls.get());
                }
                Ordered related = target.related;
                assertNotNull(related);
                assertEquals(62, related.id);
                assertNotSame(cachedChild, related);
                assertSame(cachedChild, cachedParent.related);
                assertTrue(manager.contains(related));
                if (!newChild) {
                    assertSame(previousChild, related);
                }
                assertNoLoadPhase(cachedParent);
                assertNoLoadPhase(cachedChild);
                if (runtimeListener) {
                    assertEquals(2, runtime.size());
                    assertEquals(1, runtime.stream().filter(entity -> entity == target).count());
                    assertEquals(1, runtime.stream().filter(entity -> entity == related).count());
                    assertLoaded(target, parentValue);
                    assertLoaded(related, childValue);
                } else {
                    assertTrue(runtime.isEmpty());
                    assertMetadataLoaded(target, parentValue);
                    assertMetadataLoaded(related, childValue);
                }
                assertNativePhases(nativeEvents, cachedParent, List.of("refresh"));
                assertNativePhases(nativeEvents, cachedChild, List.of("refresh"));
                assertNativePhases(nativeEvents, target, List.of("merge"));
                assertNativePhases(nativeEvents, related, newChild ? List.of("clone", "merge") : List.of("merge"));
                assertEquals("No extra native target refresh or intermediate event", newChild ? 5 : 4, nativeEvents.size());
                if (newChild) {
                    assertTrue(nativeIndex(nativeEvents, related, "clone") < nativeIndex(nativeEvents, related, "merge"));
                }
                assertTrue(nativeIndex(nativeEvents, cachedParent, "refresh") < nativeIndex(nativeEvents, target, "merge"));
                assertTrue(nativeIndex(nativeEvents, cachedChild, "refresh")
                        < nativeIndex(nativeEvents, related, newChild ? "clone" : "merge"));
                for (NativeEvent event : nativeEvents) {
                    if (event.entity() == cachedParent || event.entity() == cachedChild) {
                        assertNoQueryMarker(event.query());
                    } else {
                        assertTrue("Native target phases precede JPA callbacks", event.loadTrace() == null || event.loadTrace().isEmpty());
                    }
                }
                manager.getTransaction().begin();
                manager.flush();
                manager.getTransaction().commit();
                String mutation = runtimeListener ? "runtime:" : "metadata:";
                assertEquals(mutation + parentValue, tableValue("EA_EMFL_ORDERED", 61));
                assertEquals(mutation + childValue, tableValue("EA_EMFL_ORDERED", 62));
            } finally {
                rollback(manager);
                unit.server().getEventManager().removeListener(sqlCounter);
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    private enum CursorRoute { COMPLETE, EARLY_CLOSE, CALLBACK_FAILURE }

    private void nativeCursorRefresh(boolean scrollable, CursorRoute route) throws Exception {
        for (int id = 71; id <= 73; id++) {
            seedOrdered(id, "cursor before " + id);
        }
        try (Unit unit = new Unit(); EntityManager manager = unit.factory.createEntityManager()) {
            List<Ordered> targets = new ArrayList<>();
            List<Ordered> cached = new ArrayList<>();
            for (int id = 71; id <= 73; id++) {
                Ordered target = manager.find(Ordered.class, id);
                Ordered source = (Ordered) unit.server().getIdentityMapAccessor().getFromIdentityMap(id, Ordered.class);
                assertNotSame(source, target);
                target.clearLoadTrace();
                source.clearLoadTrace();
                targets.add(target);
                cached.add(source);
                changeValue("EA_EMFL_ORDERED", id, "cursor refreshed " + id);
            }
            List<Ordered> runtime = new ArrayList<>();
            IllegalStateException marker = new IllegalStateException("owned deferred refresh callback failure");
            EntityListenerRegistration registration = unit.factory.addListener(Ordered.class, PostLoad.class, entity -> {
                recordLoaded(entity, runtime);
                if (route == CursorRoute.CALLBACK_FAILURE && entity.id == 72) {
                    throw marker;
                }
            });
            TypedQuery<Ordered> query = manager.createQuery(
                    "select o from EmfListenerOrdered o where o.id between 71 and 73 order by o.id", Ordered.class)
                    .setHint(QueryHints.REFRESH, true);
            if (scrollable) {
                query.setHint(QueryHints.SCROLLABLE_CURSOR, true)
                        .setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
            } else {
                query.setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 1)
                        .setHint(QueryHints.CURSOR_PAGE_SIZE, 1);
            }
            List<NativeEvent> nativeEvents = new ArrayList<>();
            DescriptorEventAdapter nativeListener = nativeLoads(nativeEvents, true);
            unit.server().getDescriptor(Ordered.class).getEventManager().addListener(nativeListener);
            Cursor cursor = null;
            try {
                cursor = query.unwrap(JpaQuery.class).getResultCursor();
                assertEquals(scrollable, cursor instanceof ScrollableCursor);
                assertEquals(!scrollable, cursor instanceof CursoredStream);
                assertEquals("Only the configured initial page is materialized before return", scrollable ? 0 : 1, runtime.size());
                assertNoRefreshState(manager, cursor.getQuery());
                assertSame(targets.get(0), cursor.next());
                assertEquals(List.of(targets.get(0)), runtime);
                assertLoaded(targets.get(0), "cursor refreshed 71");
                assertNoRefreshState(manager, cursor.getQuery());
                if (route == CursorRoute.CALLBACK_FAILURE) {
                    Cursor failingCursor = cursor;
                    assertCause(marker, assertThrows(RuntimeException.class, failingCursor::next));
                    assertEquals(List.of(targets.get(0), targets.get(1)), runtime);
                    assertEquals("metadata>runtime>", targets.get(1).loadTrace);
                    assertNull(targets.get(1).entityLoadTarget);
                    assertNoRefreshState(manager, cursor.getQuery());
                    ((UnitOfWorkImpl) manager.unwrap(JpaEntityManager.class).getUnitOfWork()).executeDeferredEvents();
                    assertEquals("Failed cursor callback must not replay", 2, runtime.size());
                } else if (route == CursorRoute.COMPLETE) {
                    for (int index = 1; index < targets.size(); index++) {
                        assertSame(targets.get(index), cursor.next());
                        assertEquals(index + 1, runtime.size());
                        assertLoaded(targets.get(index), "cursor refreshed " + targets.get(index).id);
                        assertNoRefreshState(manager, cursor.getQuery());
                    }
                    assertFalse(cursor.hasNext());
                }
                cursor.close();
                assertTrue(cursor.isClosed());
                assertNoRefreshState(manager, cursor.getQuery());
                if (route != CursorRoute.COMPLETE) {
                    if (route == CursorRoute.EARLY_CLOSE) {
                        assertNoLoadPhase(targets.get(1));
                        assertNoLoadPhase(cached.get(1));
                    }
                    assertNoLoadPhase(targets.get(2));
                    assertNoLoadPhase(cached.get(2));
                    registration.cancel();
                    unit.factory.addListener(Ordered.class, PostLoad.class, entity -> recordLoaded(entity, runtime));
                    for (Ordered target : targets) {
                        target.clearLoadTrace();
                    }
                    for (Ordered source : cached) {
                        source.clearLoadTrace();
                    }
                    runtime.clear();
                    nativeEvents.clear();
                    // Reuse the same public query and persistence context after close/failure.
                    cursor = query.unwrap(JpaQuery.class).getResultCursor();
                    assertNoRefreshState(manager, cursor.getQuery());
                    for (Ordered target : targets) {
                        assertSame(target, cursor.next());
                        assertLoaded(target, "cursor refreshed " + target.id);
                        assertNoRefreshState(manager, cursor.getQuery());
                    }
                    assertFalse(cursor.hasNext());
                    cursor.close();
                    assertTrue(cursor.isClosed());
                    assertNoRefreshState(manager, cursor.getQuery());
                }
                assertEquals(targets, runtime);
                for (int index = 0; index < targets.size(); index++) {
                    assertNoLoadPhase(cached.get(index));
                    assertNativePhases(nativeEvents, cached.get(index), List.of("refresh"));
                    assertNativePhases(nativeEvents, targets.get(index), List.of("merge"));
                    assertTrue(nativeIndex(nativeEvents, cached.get(index), "refresh")
                            < nativeIndex(nativeEvents, targets.get(index), "merge"));
                }
                assertEquals(6, nativeEvents.size());
                manager.getTransaction().begin();
                manager.flush();
                manager.getTransaction().commit();
                for (Ordered target : targets) {
                    assertEquals("runtime:cursor refreshed " + target.id, tableValue("EA_EMFL_ORDERED", target.id));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                registration.cancel();
                rollback(manager);
                unit.server().getDescriptor(Ordered.class).getEventManager().removeListener(nativeListener);
            }
        }
    }

    private static void assertNoRefreshState(EntityManager manager, DatabaseQuery query) throws Exception {
        Object unitOfWork = manager.unwrap(JpaEntityManager.class).getUnitOfWork();
        for (String name : List.of("refreshOperation", "refreshCloneOperation")) {
            java.lang.reflect.Field field;
            try {
                field = UnitOfWorkImpl.class.getDeclaredField(name);
            } catch (NoSuchFieldException absentOnParent) {
                // Exact-parent comparison has no new state; candidate absence must fail closed.
                assertThrows(ClassNotFoundException.class, () -> Class.forName(
                        "org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerRegistry", false,
                        UnitOfWorkImpl.class.getClassLoader()));
                return;
            }
            field.setAccessible(true);
            assertNull("No operation may span returned cursor accesses: " + name, field.get(unitOfWork));
        }
        assertFalse((Boolean) ObjectBuildingQuery.class.getMethod("isRefreshSource").invoke(query));
        assertNoQueryMarker(query);
    }

    private static void assertNoLoadPhase(Ordered entity) {
        assertEquals("Cached source must not receive JPA callbacks: " + entity.id, "", entity.loadTrace);
        assertNull(entity.metadataLoadTarget);
        assertNull(entity.entityLoadTarget);
        assertNull(entity.metadataLoadValue);
        assertNull(entity.entityLoadValue);
    }

    private static void assertMetadataLoaded(Ordered entity, String databaseValue) {
        assertEquals("metadata>entity>", entity.loadTrace);
        assertSame(entity, entity.metadataLoadTarget);
        assertSame(entity, entity.entityLoadTarget);
        assertEquals(databaseValue, entity.metadataLoadValue);
        assertEquals("metadata:" + databaseValue, entity.entityLoadValue);
        assertEquals("metadata:" + databaseValue, entity.value);
    }

    private static void assertNativePhases(List<NativeEvent> events, Ordered entity, List<String> expected) {
        assertEquals("Native phases for entity " + entity.id, expected,
                events.stream().filter(event -> event.entity() == entity).map(NativeEvent::kind).toList());
    }

    private static int nativeIndex(List<NativeEvent> events, Ordered entity, String kind) {
        for (int index = 0; index < events.size(); index++) {
            NativeEvent event = events.get(index);
            if (event.entity() == entity && event.kind().equals(kind)) {
                return index;
            }
        }
        throw new AssertionError("Missing native " + kind + " for " + entity.id);
    }

    private static void recordLoaded(Ordered entity, List<Ordered> runtime) {
        assertSame(entity, entity.metadataLoadTarget);
        assertEquals("metadata>", entity.loadTrace);
        assertEquals(entity.value, entity.metadataLoadValue);
        runtime.add(entity);
        entity.recordLoadPhase("runtime");
        entity.setValue("runtime:" + entity.value);
    }

    private static void assertLoaded(Ordered entity, String databaseValue) {
        assertEquals("metadata>runtime>entity>", entity.loadTrace);
        assertSame(entity, entity.metadataLoadTarget);
        assertSame(entity, entity.entityLoadTarget);
        assertEquals(databaseValue, entity.metadataLoadValue);
        assertEquals("runtime:" + databaseValue, entity.entityLoadValue);
        assertEquals("runtime:" + databaseValue, entity.value);
    }

    private static DescriptorEventAdapter nativeLoads(List<NativeEvent> events) {
        return nativeLoads(events, false);
    }

    private static DescriptorEventAdapter nativeLoads(List<NativeEvent> events, boolean includeClone) {
        return new DescriptorEventAdapter() {
            @Override
            public void postClone(DescriptorEvent event) {
                if (includeClone) {
                    events.add(new NativeEvent("clone", event.getObject(), event.getQuery()));
                }
            }

            @Override
            public void postRefresh(DescriptorEvent event) {
                events.add(new NativeEvent("refresh", event.getObject(), event.getQuery()));
            }

            @Override
            public void postMerge(DescriptorEvent event) {
                events.add(new NativeEvent("merge", event.getObject(), event.getQuery()));
            }
        };
    }

    private static void assertNoQueryMarker(DatabaseQuery query) {
        assertNotNull(query);
        assertFalse("Scoped refresh marker must be restored", query.getProperties().values().stream().anyMatch(value -> value == query));
    }

    private static Ordered ordered(int id, String value) {
        Ordered entity = new Ordered();
        entity.id = id;
        entity.value = value;
        return entity;
    }

    private void seedOrdered(int id, String value) {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            agent.insert(ordered(id, value));
        }
    }

    private record NativeEvent(String kind, Object entity, DatabaseQuery query, String loadTrace) {
        private NativeEvent(String kind, Object entity, DatabaseQuery query) {
            this(kind, entity, query, entity instanceof Ordered ordered ? ordered.loadTrace : null);
        }
    }

    private static Map<String, Object> refreshProperties() {
        return Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true", PersistenceUnitProperties.DDL_GENERATION, "none");
    }

    private static void assertCause(Throwable expected, Throwable actual) {
        for (Throwable cause = actual; cause != null; cause = cause.getCause()) {
            if (cause == expected) {
                return;
            }
        }
        throw new AssertionError("Original callback failure was not preserved", actual);
    }

    private static void persist(EntityManager manager, Plain entity) {
        manager.getTransaction().begin();
        try {
            manager.persist(entity);
            manager.getTransaction().commit();
        } finally {
            rollback(manager);
        }
    }

    private static void rollback(EntityManager manager) {
        if (manager.getTransaction().isActive()) {
            manager.getTransaction().rollback();
        }
    }

    private void seed(int id, String value) {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            agent.insert(new Plain(id, value));
        }
    }

    private void changeValue(int id, String value) {
        changeValue("EA_EMFL_PLAIN", id, value);
    }

    private void changeValue(String table, int id, String value) {
        try (EntityManager manager = fixture.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                manager.createNativeQuery("UPDATE " + table + " SET ROW_VALUE = ? WHERE ROW_ID = ?")
                        .setParameter(1, value).setParameter(2, id).executeUpdate();
                manager.getTransaction().commit();
            } finally {
                rollback(manager);
            }
        }
    }

    private int count(int id) {
        return tableCount("EA_EMFL_PLAIN", id);
    }

    private int tableCount(String table, int id) {
        try (EntityManager manager = fixture.createEntityManager()) {
            return ((Number) manager.createNativeQuery("SELECT COUNT(*) FROM " + table + " WHERE ROW_ID = " + id).getSingleResult()).intValue();
        }
    }

    private String value(int id) {
        return tableValue("EA_EMFL_PLAIN", id);
    }

    private String tableValue(String table, int id) {
        try (EntityManager manager = fixture.createEntityManager()) {
            return (String) manager.createNativeQuery("SELECT ROW_VALUE FROM " + table + " WHERE ROW_ID = " + id).getSingleResult();
        }
    }

    private record Event(Class<? extends Annotation> kind, Object entity) {
    }

    private static final class Recorder implements AutoCloseable {
        private final List<EntityListenerRegistration> registrations = new ArrayList<>();
        private final List<Event> events = new ArrayList<>();

        private Recorder(EntityManagerFactory factory) {
            for (Class<? extends Annotation> event : EVENTS) {
                registrations.add(factory.addListener(Plain.class, event, entity -> events.add(new Event(event, entity))));
            }
        }

        @SafeVarargs
        private final void expect(Plain entity, Class<? extends Annotation>... kinds) {
            assertEquals(List.of(kinds), events.stream().map(Event::kind).toList());
            for (Event event : events) {
                assertSame(entity, event.entity());
            }
            events.clear();
        }

        @Override
        public void close() {
            registrations.forEach(EntityListenerRegistration::cancel);
        }
    }

    private static class Unit implements AutoCloseable {
        private final SEPersistenceUnitInfo info;
        private final Map<String, Object> properties;
        private final List<EntityManagerFactory> factories = new ArrayList<>();
        private final List<EntityManagerFactoryDelegate> retired = new ArrayList<>();
        private final EntityManagerFactory factory;

        private Unit() throws Exception {
            this("NONE");
        }

        private Unit(String validation) throws Exception {
            this(validation, null);
        }

        private Unit(String validation, DataSource dataSource) throws Exception {
            this(validation, dataSource, null);
        }

        private Unit(String validation, DataSource dataSource, SessionCustomizer customizer) throws Exception {
            String name = "emf-listeners-" + UUID.randomUUID();
            info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName(name);
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(Plain.class.getClassLoader());
            info.setNewTempClassLoader(Plain.class.getClassLoader());
            info.setPersistenceUnitRootUrl(Plain.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames((dataSource == null ? List.of(Plain.class, Root.class, Child.class, Ordered.class,
                    PolymorphicChild.class, PolymorphicFirst.class, PolymorphicSecond.class, PolymorphicParent.class)
                    : List.of(Plain.class)).stream().map(Class::getName).toList());
            info.setExcludeUnlistedClasses(true);
            info.setProperties(new Properties());
            properties = new HashMap<>();
            properties.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            properties.put(PersistenceUnitProperties.WEAVING, "false");
            properties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
            properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
            properties.put("jakarta.persistence.validation.mode", validation);
            if (customizer != null) {
                properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizer);
            }
            if (dataSource != null) {
                info.setNonJtaDataSource(dataSource);
                properties.put("jakarta.persistence.jdbc.user", "sa");
                properties.put("jakarta.persistence.jdbc.password", "");
                properties.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.H2Platform");
            }
            factory = anotherFactory();
        }

        private EntityManagerFactory anotherFactory() {
            EntityManagerFactory created = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), properties);
            factories.add(created);
            created.unwrap(JpaEntityManagerFactory.class).getServerSession();
            return created;
        }

        private EntityManagerFactoryImpl wrapper() {
            return (EntityManagerFactoryImpl) factory;
        }

        private ServerSession server() {
            return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
        }

        @Override
        public void close() {
            RuntimeException failure = null;
            List<EntityManagerFactory> owned = new ArrayList<>(factories);
            owned.addAll(retired);
            for (int index = owned.size() - 1; index >= 0; index--) {
                EntityManagerFactory value = owned.get(index);
                try {
                    if (value.isOpen()) {
                        value.close();
                    }
                } catch (RuntimeException exception) {
                    if (failure == null) {
                        failure = exception;
                    } else {
                        failure.addSuppressed(exception);
                    }
                }
            }
            if (failure != null) {
                throw failure;
            }
        }
    }

    private static final class LifecycleUnit implements AutoCloseable {
        private final Connection keeper;
        private final Unit unit;

        private LifecycleUnit() throws Exception {
            String name = "emf-listener-lifecycle-" + UUID.randomUUID();
            Class<?> type = Class.forName("org.h2.jdbcx.JdbcDataSource");
            Object source = type.getConstructor().newInstance();
            type.getMethod("setURL", String.class).invoke(source, "jdbc:h2:mem:" + name);
            type.getMethod("setUser", String.class).invoke(source, "sa");
            type.getMethod("setPassword", String.class).invoke(source, "");
            DataSource dataSource = (DataSource) source;
            keeper = dataSource.getConnection();
            try {
                try (java.sql.Statement statement = keeper.createStatement()) {
                    statement.execute("CREATE TABLE EA_EMFL_PLAIN (ROW_ID INTEGER PRIMARY KEY, ROW_VERSION INTEGER, ROW_VALUE VARCHAR(80), DETAIL_VALUE VARCHAR(80))");
                }
                unit = new Unit("NONE", dataSource);
            } catch (Exception | Error failure) {
                keeper.close();
                throw failure;
            }
        }

        private int count() throws Exception {
            try (java.sql.Statement statement = keeper.createStatement();
                    java.sql.ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM EA_EMFL_PLAIN")) {
                assertTrue(result.next());
                return result.getInt(1);
            }
        }

        @Override
        public void close() throws Exception {
            try {
                unit.close();
            } finally {
                keeper.close();
            }
        }
    }
}
