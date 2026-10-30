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

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PreClose;
import jakarta.persistence.PreInsert;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.ValidationMode;
import jakarta.transaction.TransactionManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.PersistenceUnitImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.config.RuntimeFactory;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.BridgeListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.DualListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.Event;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.InheritedListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.Listener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.MixedListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.Other;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.OverridingListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.PeerListener;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.Record;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.Trace;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.XmlOverlayBase;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.XmlOverlayChild;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.XmlOverlayEntity;
import org.eclipse.persistence.jpa.entityagent.model.PersistenceUnitLifecycleModels.XmlOverlayListener;
import org.eclipse.persistence.jpa.metadata.ProjectCache;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Portable public factory, manager and agent lifecycle contracts. */
@RunWith(EmfRunner.class)
public class TestPersistenceUnitLifecycle {
    @Emf(name = "persistence-unit-lifecycle-fixture", createTables = DDLGen.DROP_CREATE,
            classes = {Record.class, Other.class}, properties = {
            @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
            @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory fixture;

    @Before
    @After
    public void cleanOwnedRows() {
        fixture.runInTransaction(manager -> {
            manager.createStatement("delete from PersistenceUnitLifecycleRecord").execute();
            manager.createStatement("delete from PersistenceUnitLifecycleOther").execute();
        });
    }

    @Test
    public void factoryCallbacksReceiveTheUsablePublicFactoryBeforeReturnAndClose() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
            assertSame(factory, unit.trace.events(factory).getFirst().target());
            assertTrue(unit.trace.events(factory).getFirst().open());
            factory.close();
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
            assertTrue(unit.trace.events(factory).getLast().open());
            assertFalse(factory.isOpen());
        }
    }

    @Test
    public void managerCallbacksReceiveTheUsablePublicManagerBeforeReturnAndClose() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityManager manager = factory.createEntityManager();
            try {
                assertEquals(List.of("manager-create"), unit.trace.kinds(manager));
                assertSame(manager, unit.trace.events(manager).getFirst().target());
                assertTrue(unit.trace.events(manager).getFirst().open());
            } finally {
                manager.close();
            }
            assertEquals(List.of("manager-create", "manager-close"), unit.trace.kinds(manager));
            assertTrue(unit.trace.events(manager).getLast().open());
            assertFalse(manager.isOpen());
        }
    }

    @Test
    public void agentCallbacksReceiveTheUsablePublicAgentBeforeReturnAndClose() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityAgent agent = factory.createEntityAgent();
            try {
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
                assertSame(agent, unit.trace.events(agent).getFirst().target());
                assertTrue(unit.trace.events(agent).getFirst().open());
            } finally {
                agent.close();
            }
            assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            assertTrue(unit.trace.events(agent).getLast().open());
            assertFalse(agent.isOpen());
        }
    }

    @Test
    public void lifecycleOnlyListenerWorksWithZeroOneAndManyEntitiesWithoutMultiplication() {
        for (Class<?>[] classes : List.of(new Class<?>[] {Listener.class},
                new Class<?>[] {Listener.class, Record.class},
                new Class<?>[] {Listener.class, Record.class, Other.class})) {
            try (Unit unit = new Unit(classes)) {
                verifySixCallbacks(unit, unit.create(), "");
            }
        }
    }

    @Test
    public void inheritedNonpublicCallbacksRetainAllSixSignatures() {
        try (Unit unit = new Unit(InheritedListener.class)) {
            verifySixCallbacks(unit, unit.create(), "");
        }
    }

    @Test
    public void overridingMethodWithoutAnnotationSuppressesInheritedCallback() {
        try (Unit unit = new Unit(OverridingListener.class)) {
            EntityAgent agent = unit.create().createEntityAgent();
            assertTrue(unit.trace.events(agent).isEmpty());
            agent.close();
            assertEquals(List.of("agent-close"), unit.trace.kinds(agent));
        }
    }

    @Test
    public void oneMethodMayReceiveBothLifecycleEvents() {
        try (Unit unit = new Unit(DualListener.class)) {
            EntityAgent agent = unit.create().createEntityAgent();
            assertEquals(List.of("agent-both"), unit.trace.kinds(agent));
            agent.close();
            assertEquals(List.of("agent-both", "agent-both"), unit.trace.kinds(agent));
        }
    }

    @Test
    public void compilerGeneratedBridgeDoesNotCreateAnotherCallbackSignature() {
        assertTrue(java.util.Arrays.stream(BridgeListener.class.getDeclaredMethods()).anyMatch(java.lang.reflect.Method::isBridge));
        try (Unit unit = new Unit(BridgeListener.class)) {
            try (EntityManager manager = unit.create().createEntityManager()) {
                assertEquals(List.of("bridge-manager-create"), unit.trace.kinds(manager));
            }
        }
    }

    @Test
    public void factoryCallbackRegistersAUsableNamedQueryBeforeReturn() {
        try (EntityAgent seed = fixture.createEntityAgent()) {
            seed.insert(new Record(1, "visible to callback query"));
        }
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            unit.trace.action = event -> {
                if (event.kind().equals("factory-create")) {
                    EntityManagerFactory factory = (EntityManagerFactory) event.target();
                    try (EntityManager manager = factory.createEntityManager()) {
                        factory.addNamedQuery("JPA.PUL.records", manager.createQuery(
                                "select r from PersistenceUnitLifecycleRecord r", Record.class));
                    }
                }
            };
            EntityManagerFactory factory = unit.create();
            try (EntityManager manager = factory.createEntityManager()) {
                assertEquals("visible to callback query", manager.createNamedQuery("JPA.PUL.records", Record.class)
                        .getSingleResult().value);
            }
            assertEquals(2, unit.trace.events.stream().filter(event -> event.kind().equals("manager-create")).count());
        }
    }

    @Test
    public void managerCallbacksCommitObservableRowsBeforeReturnAndBeforeClose() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            unit.trace.action = event -> {
                if (event.target() instanceof EntityManager manager) {
                    manager.getTransaction().begin();
                    try {
                        if (event.kind().equals("manager-create")) {
                            manager.persist(new Record(2, "created by manager"));
                        } else {
                            manager.find(Record.class, 2).setValue("closed by manager");
                        }
                        manager.getTransaction().commit();
                    } finally {
                        if (manager.getTransaction().isActive()) {
                            manager.getTransaction().rollback();
                        }
                    }
                }
            };
            EntityManager manager = unit.create().createEntityManager();
            assertEquals("created by manager", value(2));
            manager.close();
            assertEquals("closed by manager", value(2));
        }
    }

    @Test
    public void agentCallbacksWriteObservableRowsBeforeReturnAndBeforeClose() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            unit.trace.action = event -> {
                if (event.target() instanceof EntityAgent agent) {
                    if (event.kind().equals("agent-create")) {
                        agent.insert(new Record(3, "created by agent"));
                    } else {
                        Record row = agent.get(Record.class, 3);
                        row.value = "closed by agent";
                        agent.update(row);
                    }
                }
            };
            EntityAgent agent = unit.create().createEntityAgent();
            assertEquals("created by agent", value(3));
            agent.close();
            assertEquals("closed by agent", value(3));
        }
    }

    @Test
    public void creationOverloadsAndOptionsDeliverExactlyOneEvent() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            for (EntityManager manager : List.of(factory.createEntityManager(),
                    factory.createEntityManager(Map.of()),
                    factory.createEntityManager(new EntityManager.CreationOption[0]),
                    factory.createEntityManager(CacheRetrieveMode.BYPASS))) {
                assertEquals(List.of("manager-create"), unit.trace.kinds(manager));
                manager.close();
                assertEquals(List.of("manager-create", "manager-close"), unit.trace.kinds(manager));
            }
            for (EntityAgent agent : List.of(factory.createEntityAgent(), factory.createEntityAgent(Map.of()),
                    factory.createEntityAgent(new EntityAgent.CreationOption[0]),
                    factory.createEntityAgent(CacheRetrieveMode.BYPASS))) {
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
                agent.close();
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            }
            int before = unit.trace.events.size();
            assertThrows(IllegalArgumentException.class,
                    () -> factory.createEntityAgent(new EntityAgent.CreationOption() { }));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.createEntityManager(new EntityManager.CreationOption() { }));
            assertEquals(before, unit.trace.events.size());
        }
    }

    @Test
    public void typedAndUntypedTransactionHelpersDeliverCallbacksAroundWork() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            List<EntityHandler> handlers = new ArrayList<>();
            factory.runInTransaction(manager -> verifyHelper(unit, manager, handlers));
            assertEquals("manager", factory.callInTransaction(manager -> {
                verifyHelper(unit, manager, handlers);
                return "manager";
            }));
            factory.runInTransaction(EntityManager.class, manager -> verifyHelper(unit, manager, handlers));
            factory.callInTransaction(EntityManager.class, manager -> {
                verifyHelper(unit, manager, handlers);
                return null;
            });
            factory.runInTransaction(EntityAgent.class, agent -> verifyHelper(unit, agent, handlers));
            factory.callInTransaction(EntityAgent.class, agent -> {
                verifyHelper(unit, agent, handlers);
                return null;
            });
            for (EntityHandler handler : handlers) {
                String type = handler instanceof EntityManager ? "manager" : "agent";
                assertEquals(List.of(type + "-create", type + "-close"), unit.trace.kinds(handler));
                assertFalse(handler.isOpen());
            }
        }
    }

    @Test
    public void callbacksMayCreateNestedChildrenAfterRegistryPublication() {
        try (Unit unit = new Unit(Listener.class)) {
            AtomicBoolean first = new AtomicBoolean(true);
            unit.trace.action = event -> {
                if (event.kind().equals("manager-create") && first.getAndSet(false)) {
                    try (EntityAgent nested = ((EntityManager) event.target()).getEntityManagerFactory().createEntityAgent()) {
                        assertEquals(List.of("agent-create"), unit.trace.kinds(nested));
                    }
                }
            };
            try (EntityManager manager = unit.create().createEntityManager()) {
                assertEquals(List.of("manager-create"), unit.trace.kinds(manager));
                assertEquals(1, unit.trace.events.stream().filter(event -> event.kind().equals("agent-close")).count());
            }
        }
    }

    @Test
    public void reentrantCloseNotifiesOnceAndOrdinaryRepeatedCloseStillFails() {
        try (Unit unit = new Unit(Listener.class)) {
            unit.trace.action = event -> {
                if (event.kind().endsWith("-close")) {
                    close(event.target());
                    assertTrue(event.open());
                }
            };
            EntityManagerFactory factory = unit.create();
            EntityManager manager = factory.createEntityManager();
            EntityAgent agent = factory.createEntityAgent();
            manager.close();
            agent.close();
            factory.close();
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
            assertEquals(List.of("manager-create", "manager-close"), unit.trace.kinds(manager));
            assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            assertThrows(IllegalStateException.class, manager::close);
            assertThrows(IllegalStateException.class, agent::close);
            assertThrows(IllegalStateException.class, factory::close);
        }
    }

    @Test(timeout = 30000)
    public void concurrentFactoryCloseWaitsForCallbackCompletion() throws Exception {
        verifyConcurrentFactoryClose(false);
    }

    @Test(timeout = 30000)
    public void concurrentNativeFactoryCloseWaitsWithoutDelegateLockInversion() throws Exception {
        verifyConcurrentFactoryClose(true);
    }

    @Test
    public void currentDelegateCloseUsesPublicFactoryAndPreservesLiveAgentCallbacks() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityManagerFactoryDelegate delegate = factory.unwrap(EntityManagerFactoryDelegate.class);
            assertSame(delegate, factory.unwrap(JpaEntityManagerFactory.class).unwrap());
            EntityAgent agent = factory.createEntityAgent();
            Object listener = unit.trace.events(factory).getFirst().listener();
            try {
                delegate.close();
                assertFalse(factory.isOpen());
                assertFalse(delegate.isOpen());
                assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
                Event closing = unit.trace.events(factory).getLast();
                assertSame(factory, closing.target());
                assertTrue(closing.open());
                assertTrue(unit.trace.events(delegate).isEmpty());
                assertTrue(agent.isOpen());
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
                agent.close();
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
                assertSame(listener, unit.trace.events(agent).getLast().listener());
                assertTrue(unit.trace.events(agent).getLast().open());
                assertThrows(IllegalStateException.class, factory::close);
                assertThrows(IllegalStateException.class, delegate::close);
                assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
            } finally {
                if (agent.isOpen()) {
                    agent.close();
                }
            }
        }
    }

    @Test(timeout = 30000)
    public void nativeCloseRacingWithRefreshDoesNotCloseTheReplacementFactory() throws Exception {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl) unit.create();
            EntityManagerFactoryDelegate retired = factory.unwrap();
            unit.retired.add(retired);
            FutureTask<Throwable> closing = new FutureTask<>(() -> closeFailure(retired));
            Thread thread = Thread.ofPlatform().daemon().name("pul-native-close-during-refresh").unstarted(closing);
            synchronized (factory) {
                thread.start();
                assertTrue("Native close must enter its public owner before delegate disposal",
                        awaitCompetingClose(thread, closing));
                factory.refreshMetadata(Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true",
                        PersistenceUnitProperties.DDL_GENERATION, "none"));
                assertNotSame(retired, factory.unwrap());
            }
            Throwable failure = closing.get(10, TimeUnit.SECONDS);
            if (failure != null) {
                throw new AssertionError("The retired native delegate must close successfully", failure);
            }
            assertTrue("A native close that became retired must leave the replacement factory open", factory.isOpen());
            assertFalse(retired.isOpen());
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
            try (EntityAgent agent = factory.createEntityAgent()) {
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
            }
            factory.close();
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
        }
    }

    @Test
    public void nativeCloseDisposesRequestedDelegateAfterCallbackAdoptsRefreshedMetadata() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityManagerFactoryImpl first = (EntityManagerFactoryImpl) unit.create();
            EntityManagerFactoryImpl second = (EntityManagerFactoryImpl) unit.create();
            EntityManagerFactoryDelegate originalFirst = first.unwrap();
            EntityManagerFactoryDelegate requested = second.unwrap();
            unit.retired.add(originalFirst);
            unit.retired.add(requested);
            assertSame(originalFirst.getSetupImpl(), requested.getSetupImpl());
            EntityAgent agent = second.createEntityAgent();
            Object listener = unit.trace.events(second).getFirst().listener();
            AtomicReference<EntityManager> callbackManager = new AtomicReference<>();
            first.refreshMetadata(Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true",
                    PersistenceUnitProperties.DDL_GENERATION, "none"));
            unit.trace.action = event -> {
                if (event.kind().equals("factory-close") && event.target() == second) {
                    try (EntityManager manager = second.createEntityManager()) {
                        callbackManager.set(manager);
                        assertNotSame(requested, second.unwrap());
                        assertEquals(0L, manager.createQuery("select count(r) from PersistenceUnitLifecycleRecord r", Long.class)
                                .getSingleResult().longValue());
                    }
                }
            };
            try {
                requested.close();
                assertFalse("The exact native delegate whose close was requested must be disposed", requested.isOpen());
                assertFalse(second.isOpen());
                assertTrue("An unrelated historical delegate must retain its own close ownership", originalFirst.isOpen());
                assertTrue(first.isOpen());
                assertNotNull(callbackManager.get());
                assertEquals(List.of("manager-create", "manager-close"), unit.trace.kinds(callbackManager.get()));
                assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(second));
                assertTrue(unit.trace.events(requested).isEmpty());
                assertSame(second, unit.trace.events(second).getLast().target());
                assertTrue(unit.trace.events(second).getLast().open());
                assertTrue(agent.isOpen());
                agent.close();
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
                assertSame(listener, unit.trace.events(agent).getLast().listener());
            } finally {
                if (agent.isOpen()) {
                    agent.close();
                }
            }
        }
    }

    @Test
    public void closeDisposesOriginalAndReplacementAfterReentrantRefresh() {
        for (boolean nativeClose : List.of(false, true)) {
            for (boolean closeRetiredInCallback : List.of(false, true)) {
                try (Unit unit = new Unit(Listener.class, Record.class)) {
                    EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl) unit.create();
                    EntityManagerFactoryDelegate original = factory.unwrap();
                    unit.retired.add(original);
                    AtomicReference<EntityManagerFactoryDelegate> replacement = new AtomicReference<>();
                    unit.trace.action = event -> {
                        if (event.kind().equals("factory-close")) {
                            factory.refreshMetadata(Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true",
                                    PersistenceUnitProperties.DDL_GENERATION, "none"));
                            replacement.set(factory.unwrap());
                            assertNotSame(original, replacement.get());
                            if (closeRetiredInCallback) {
                                original.close();
                            }
                            try (EntityManager manager = factory.createEntityManager()) {
                                assertEquals(0L, manager.createQuery("select count(r) from PersistenceUnitLifecycleRecord r", Long.class)
                                        .getSingleResult().longValue());
                            }
                        }
                    };
                    close(nativeClose ? original : factory);
                    assertFalse("The delegate current when close began must be disposed", original.isOpen());
                    assertNotNull(replacement.get());
                    assertFalse(replacement.get().isOpen());
                    assertFalse(factory.isOpen());
                    assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
                    assertTrue(unit.trace.events(original).isEmpty());
                    assertEquals(1, unit.trace.events.stream().filter(event -> event.kind().equals("manager-create")).count());
                    assertEquals(1, unit.trace.events.stream().filter(event -> event.kind().equals("manager-close")).count());
                    assertThrows(IllegalStateException.class, factory::close);
                }
            }
        }
    }

    @Test(timeout = 30000)
    public void automaticMetadataReplacementWaitsForActiveFactoryClose() throws Exception {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityManagerFactoryImpl first = (EntityManagerFactoryImpl) unit.create();
            EntityManagerFactoryImpl second = (EntityManagerFactoryImpl) unit.create();
            unit.retired.add(first.unwrap());
            EntityManagerFactoryDelegate requested = second.unwrap();
            unit.retired.add(requested);
            first.refreshMetadata(Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true",
                    PersistenceUnitProperties.DDL_GENERATION, "none"));
            CountDownLatch callbackEntered = new CountDownLatch(1);
            CountDownLatch releaseCallback = new CountDownLatch(1);
            unit.trace.action = event -> {
                if (event.kind().equals("factory-close") && event.target() == second) {
                    callbackEntered.countDown();
                    try {
                        assertTrue("Close callback was not released", releaseCallback.await(10, TimeUnit.SECONDS));
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError("Close callback interrupted", interrupted);
                    }
                    assertTrue(second.isOpen());
                }
            };
            FutureTask<Throwable> closing = new FutureTask<>(() -> closeFailure(requested));
            FutureTask<Throwable> creating = new FutureTask<>(() -> {
                try (EntityManager manager = second.createEntityManager()) {
                    manager.getCriteriaBuilder();
                    return null;
                } catch (Throwable failure) {
                    return failure;
                }
            });
            Thread closeThread = Thread.ofPlatform().daemon().name("pul-close-expired-factory").unstarted(closing);
            Thread createThread = Thread.ofPlatform().daemon().name("pul-adopt-refreshed-metadata").unstarted(creating);
            boolean waited;
            closeThread.start();
            try {
                assertTrue("Close did not enter its callback", callbackEntered.await(10, TimeUnit.SECONDS));
                createThread.start();
                waited = awaitFactoryOperation(createThread, creating, "createEntityManagerImpl");
            } finally {
                releaseCallback.countDown();
            }
            Throwable closeFailure = closing.get(10, TimeUnit.SECONDS);
            Throwable createFailure = creating.get(10, TimeUnit.SECONDS);
            assertTrue("Automatic metadata replacement must wait for the active close", waited);
            if (closeFailure != null) {
                throw new AssertionError("The native close must complete successfully", closeFailure);
            }
            assertTrue("A manager request waiting behind close must not resurrect the factory",
                    createFailure instanceof IllegalStateException);
            assertFalse(requested.isOpen());
            assertFalse(second.isOpen());
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(second));
            assertEquals(0, unit.trace.events.stream().filter(event -> event.kind().equals("manager-create")).count());
        }
    }

    @Test
    public void sharedSetupFactoriesHaveIndependentListenerInstancesAndEvents() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory first = unit.create();
            EntityManagerFactory second = unit.create();
            assertNotSame(first, second);
            assertSame(first.unwrap(JpaEntityManagerFactory.class).getServerSession(),
                    second.unwrap(JpaEntityManagerFactory.class).getServerSession());
            assertNotSame(unit.trace.events(first).getFirst().listener(), unit.trace.events(second).getFirst().listener());
            first.close();
            assertEquals(List.of("factory-create"), unit.trace.kinds(second));
            try (EntityAgent agent = second.createEntityAgent()) {
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
            }
        }
    }

    @Test
    public void independentPersistenceUnitsDoNotLeakEvents() {
        try (Unit first = new Unit(Listener.class); Unit second = new Unit(Listener.class)) {
            EntityManagerFactory one = first.create();
            EntityManagerFactory two = second.create();
            try (EntityAgent agent = one.createEntityAgent(); EntityManager manager = two.createEntityManager()) {
                assertTrue(first.trace.events(manager).isEmpty());
                assertTrue(second.trace.events(agent).isEmpty());
                assertEquals(List.of("agent-create"), first.trace.kinds(agent));
                assertEquals(List.of("manager-create"), second.trace.kinds(manager));
            }
        }
    }

    @Test
    public void concurrentChildCreationUsesOneCompleteRegistry() throws Exception {
        try (Unit unit = new Unit(Listener.class); var executor = Executors.newFixedThreadPool(4)) {
            EntityManagerFactory factory = unit.create();
            List<Callable<EntityHandler>> tasks = new ArrayList<>();
            for (int index = 0; index < 16; index++) {
                boolean manager = index % 2 == 0;
                tasks.add(() -> {
                    EntityHandler handler = manager ? factory.createEntityManager() : factory.createEntityAgent();
                    assertEquals(1, unit.trace.events(handler).size());
                    handler.close();
                    return handler;
                });
            }
            Object listener = unit.trace.events(factory).getFirst().listener();
            for (var result : executor.invokeAll(tasks)) {
                EntityHandler handler = result.get();
                assertEquals(2, unit.trace.events(handler).size());
                for (Event event : unit.trace.events(handler)) {
                    assertSame(listener, event.listener());
                }
            }
        }
    }

    @Test
    public void liveAgentReceivesPreCloseAfterItsFactoryCloses() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityAgent agent = factory.createEntityAgent();
            factory.close();
            assertTrue(agent.isOpen());
            agent.close();
            assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            assertTrue(unit.trace.events(agent).getLast().open());
            assertFalse(factory.isOpen());
        }
    }

    @Test
    public void agentCleanupRetryDoesNotRepeatPreClose() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityAgent agent = unit.create().createEntityAgent();
            ClientSession client = agent.unwrap(ClientSession.class);
            RuntimeException primary = new IllegalStateException("owned one-shot client release failure");
            AtomicBoolean once = new AtomicBoolean(true);
            client.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void preReleaseClientSession(SessionEvent event) {
                    if (event.getSession() == client && once.getAndSet(false)) {
                        throw primary;
                    }
                }
            });
            agent.getTransaction().begin();
            agent.insert(new Record(6, "rolled back by close"));
            assertCause(primary, assertThrows(RuntimeException.class, agent::close));
            assertFalse(agent.isOpen());
            assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            agent.close();
            assertFalse(client.isActive());
            assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            try (EntityAgent observer = fixture.createEntityAgent()) {
                assertEquals(0L, observer.createQuery(
                        "select count(r) from PersistenceUnitLifecycleRecord r where r.id = 6", Long.class)
                        .getSingleResult().longValue());
            }
        }
    }

    @Test
    public void jtaManagerOptionsAndDeferredAgentCloseDeliverOneLifecycle() throws Exception {
        TransactionManager transactions = (TransactionManager) Class.forName("com.arjuna.ats.jta.TransactionManager")
                .getMethod("transactionManager").invoke(null);
        assertEquals(null, transactions.getTransaction());
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            unit.info.setTransactionType(PersistenceUnitTransactionType.JTA);
            unit.properties.put(PersistenceUnitProperties.TARGET_SERVER, AgentJtaFixture.Platform.class.getName());
            DataSource source = new LaneDataSource(unit.properties);
            unit.info.setJtaDataSource(source);
            unit.info.setNonJtaDataSource(source);
            EntityManagerFactory factory = unit.create();
            transactions.begin();
            try {
                for (EntityManager manager : List.of(factory.createEntityManager(SynchronizationType.SYNCHRONIZED, Map.of()),
                        factory.createEntityManager(SynchronizationType.UNSYNCHRONIZED),
                        factory.createEntityManager(SynchronizationType.SYNCHRONIZED))) {
                    assertEquals(List.of("manager-create"), unit.trace.kinds(manager));
                    manager.close();
                    assertEquals(List.of("manager-create", "manager-close"), unit.trace.kinds(manager));
                }
                EntityAgent agent = factory.createEntityAgent();
                ClientSession client = agent.unwrap(ClientSession.class);
                // A real query associates the agent with the external transaction.
                assertEquals(0L, agent.createQuery("select count(r) from PersistenceUnitLifecycleRecord r", Long.class)
                        .getSingleResult().longValue());
                agent.close();
                assertFalse(agent.isOpen());
                assertTrue(client.isActive());
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
                transactions.commit();
                assertFalse(client.isActive());
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
            } finally {
                if (transactions.getTransaction() != null) {
                    transactions.rollback();
                }
            }
        }
    }

    @Test
    public void preCloseFailuresDoNotPreventOtherListenersOrCriticalCleanup() {
        try (Unit unit = new Unit(Listener.class, PeerListener.class)) {
            RuntimeException primary = new IllegalStateException("owned pre-close failure");
            unit.trace.action = event -> {
                if (event.kind().endsWith("-close") && event.listener().getClass() == Listener.class) {
                    throw primary;
                }
            };
            EntityManagerFactory factory = unit.create();
            EntityManager manager = factory.createEntityManager();
            EntityAgent agent = factory.createEntityAgent();
            for (Object target : List.of(manager, agent, factory)) {
                assertCause(primary, assertThrows(RuntimeException.class, () -> close(target)));
                assertEquals(2, unit.trace.events(target).stream().filter(event -> event.kind().endsWith("-close")).count());
                assertFalse(isOpen(target));
            }
        }
    }

    @Test
    public void postCreateFailureClosesEachUnreturnedObjectAndRetainsTheCause() {
        for (String targetKind : List.of("factory-create", "manager-create", "agent-create")) {
            try (Unit unit = new Unit(Listener.class)) {
                RuntimeException primary = new IllegalStateException("owned " + targetKind + " failure");
                AtomicReference<Object> unreturned = new AtomicReference<>();
                unit.trace.action = event -> {
                    if (event.kind().equals(targetKind)) {
                        unreturned.set(event.target());
                        throw primary;
                    }
                };
                RuntimeException failure;
                if (targetKind.equals("factory-create")) {
                    failure = assertThrows(RuntimeException.class, unit::create);
                } else {
                    EntityManagerFactory factory = unit.create();
                    failure = assertThrows(RuntimeException.class, () -> {
                        if (targetKind.equals("manager-create")) {
                            factory.createEntityManager();
                        } else {
                            factory.createEntityAgent();
                        }
                    });
                }
                assertCause(primary, failure);
                assertNotNull(unreturned.get());
                assertFalse(isOpen(unreturned.get()));
            }
        }
    }

    @Test
    public void callbackClosingThenThrowingDoesNotReplaceItsOriginalFailure() {
        for (String targetKind : List.of("factory-create", "manager-create", "agent-create")) {
            try (Unit unit = new Unit(Listener.class)) {
                RuntimeException primary = new IllegalStateException("owned close-then-throw");
                unit.trace.action = event -> {
                    if (event.kind().equals(targetKind)) {
                        close(event.target());
                        throw primary;
                    }
                };
                if (targetKind.equals("factory-create")) {
                    assertCause(primary, assertThrows(RuntimeException.class, unit::create));
                } else {
                    EntityManagerFactory factory = unit.create();
                    assertCause(primary, assertThrows(RuntimeException.class, () -> {
                        if (targetKind.equals("manager-create")) {
                            factory.createEntityManager();
                        } else {
                            factory.createEntityAgent();
                        }
                    }));
                }
            }
        }
    }

    @Test
    public void failedManagerCreationReleasesItsSessionWhenCallbackClosesTheFactory() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityManagerFactory factory = unit.create();
            RuntimeException primary = new IllegalStateException("owned manager callback closes factory");
            AtomicReference<ClientSession> acquired = new AtomicReference<>();
            unit.trace.action = event -> {
                if (event.kind().equals("manager-create")) {
                    EntityManager manager = (EntityManager) event.target();
                    acquired.set((ClientSession) manager.unwrap(JpaEntityManager.class).getUnitOfWork().getParent());
                    assertEquals(0L, manager.createQuery("select count(r) from PersistenceUnitLifecycleRecord r", Long.class)
                            .getSingleResult().longValue());
                    assertTrue(acquired.get().isActive());
                    factory.close();
                    throw primary;
                }
            };
            assertSame(primary, assertThrows(RuntimeException.class, factory::createEntityManager));
            assertNotNull(acquired.get());
            assertFalse(acquired.get().isActive());
            assertFalse(factory.isOpen());
        }
    }

    @Test
    public void mixedEntityEventsAndProgrammaticRegistrationsRemainOperational() {
        try (Unit unit = new Unit(MixedListener.class, Record.class)) {
            EntityManagerFactory factory = unit.create();
            List<Record> seen = new ArrayList<>();
            var registration = factory.addListener(Record.class, PreInsert.class, seen::add);
            try (EntityAgent agent = factory.createEntityAgent()) {
                Record first = new Record(4, "first");
                agent.insert(first);
                assertEquals(List.of(first), seen);
                assertEquals("mixed:first", value(4));
                registration.cancel();
                agent.insert(new Record(5, "second"));
                assertEquals(List.of(first), seen);
                assertEquals("mixed:second", value(5));
            }
            assertThrows(IllegalArgumentException.class,
                    () -> factory.addListener(Record.class, PostCreate.class, seen::add));
            assertThrows(IllegalArgumentException.class,
                    () -> factory.addListener(Record.class, PreClose.class, seen::add));
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
        }
    }

    @Test
    public void configurationAndContainerBootstrapCompleteThePublicWrapper() {
        try (Unit unit = new Unit(Listener.class)) {
            PersistenceConfiguration configuration = new PersistenceConfiguration(unit.name)
                    .provider(PersistenceProvider.class.getName())
                    .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                    .validationMode(ValidationMode.NONE).managedClass(Listener.class)
                    .properties(properties());
            EntityManagerFactory factory = configuration.createEntityManagerFactory();
            unit.factories.add(factory);
            verifySixCallbacks(unit, factory, "");
        }
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = new PersistenceProvider().createContainerEntityManagerFactory(unit.info, unit.properties);
            unit.factories.add(factory);
            verifySixCallbacks(unit, factory, "");
        }
    }

    @Test
    public void runtimeFactoryBootstrapCompletesThePublicWrapper() {
        try (Unit unit = new Unit(Listener.class)) {
            PersistenceUnitImpl configuration = new PersistenceUnitImpl(unit.name, getClass().getClassLoader());
            configuration.addMappings();
            configuration.setExcludeUnlistedClasses(true);
            configuration.setClass(Listener.class.getName());
            properties().forEach(configuration::setProperty);
            EntityManagerFactory factory = RuntimeFactory.getInstance().createEntityManagerFactory(configuration);
            unit.factories.add(factory);
            verifySixCallbacks(unit, factory, "");
        }
    }

    @Test
    public void publicSessionBackedFactoryUsesAvailableLifecycleDefinitions() {
        try (Unit unit = new Unit(Record.class)) {
            unit.mapping("defaults.xml");
            EntityManagerFactory original = unit.create();
            EntityManagerFactory factory = JpaHelper.createEntityManagerFactory(
                    original.unwrap(JpaEntityManagerFactory.class).getServerSession());
            unit.factories.add(factory);
            assertNotSame(original, factory);
            verifySixCallbacks(unit, factory, "xml-");
            assertEquals(List.of("xml-factory-create"), unit.trace.kinds(original));
            assertTrue(original.isOpen());
        }
    }

    @Test
    public void noListenerFactoryRemainsLazy() {
        try (Unit unit = new Unit()) {
            unit.properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "false");
            unit.properties.put(PersistenceUnitProperties.JDBC_DRIVER, "no.such.LifecycleDriver");
            EntityManagerFactory factory = unit.create();
            assertTrue(factory.isOpen());
            assertTrue(unit.trace.events.isEmpty());
        }
    }

    @Test
    public void schemaGenerationDoesNotManufacturePublicFactoryEvents() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            unit.properties.put("jakarta.persistence.schema-generation.database.action", "none");
            new PersistenceProvider().generateSchema(unit.info, unit.properties);
            assertTrue(unit.trace.events.isEmpty());
        }
    }

    @Test
    public void refreshPreservesFactoryHistoryAndListenerIdentityForExistingAndNewChildren() {
        try (Unit unit = new Unit(Listener.class, Record.class)) {
            EntityManagerFactory factory = unit.create();
            EntityAgent existing = factory.createEntityAgent();
            EntityManagerFactoryImpl wrapper = (EntityManagerFactoryImpl) factory;
            EntityManagerFactoryDelegate retired = wrapper.unwrap();
            unit.retired.add(retired);
            // Refresh changes ordinary metadata; the logical factory retains its initial PU definitions.
            unit.mapping("overrides.xml");
            wrapper.refreshMetadata(Map.of(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true",
                    PersistenceUnitProperties.DDL_GENERATION, "none"));
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
            assertNotSame(retired, wrapper.unwrap());
            retired.close();
            assertFalse(retired.isOpen());
            assertThrows(IllegalStateException.class, retired::close);
            assertTrue(factory.isOpen());
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
            EntityAgent next = factory.createEntityAgent();
            existing.close();
            next.close();
            Object listener = unit.trace.events(factory).getFirst().listener();
            for (EntityAgent agent : List.of(existing, next)) {
                assertEquals(List.of("agent-create", "agent-close"), unit.trace.kinds(agent));
                unit.trace.events(agent).forEach(event -> assertSame(listener, event.listener()));
            }
            factory.close();
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
        }
    }

    @Test
    public void failedRefreshPreservesFactoryHistoryAndUsableCallbacks() {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityManagerFactoryImpl wrapper = (EntityManagerFactoryImpl) factory;
            EntityManagerFactoryDelegate original = wrapper.unwrap();
            Map<String, Object> refresh = new HashMap<>();
            refresh.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
            refresh.put(PersistenceUnitProperties.DDL_GENERATION, "none");
            refresh.put(PersistenceUnitProperties.SESSION_CUSTOMIZER,
                    (SessionCustomizer) session -> { throw new IllegalStateException("owned failed refresh"); });
            assertThrows(RuntimeException.class, () -> wrapper.refreshMetadata(refresh));
            assertSame(original, wrapper.unwrap());
            assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
            try (EntityAgent agent = factory.createEntityAgent()) {
                assertEquals(List.of("agent-create"), unit.trace.kinds(agent));
            }
        }
    }

    @Test
    public void xmlOnlyDefaultListenerWorksWithoutEntitiesAndResolvesLegalOverloads() {
        try (Unit unit = new Unit()) {
            unit.mapping("defaults.xml");
            verifySixCallbacks(unit, unit.create(), "xml-");
        }
    }

    @Test
    public void persistenceXmlBootstrapCompletesThePublicWrapper() {
        String name = "persistence-unit-lifecycle-xml";
        try (Trace trace = new Trace(name)) {
            Map<String, Object> settings = properties();
            settings.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML,
                    "META-INF/persistence-unit-lifecycle/persistence.xml");
            try (EntityManagerFactory factory = Persistence.createEntityManagerFactory(name, settings)) {
                assertEquals(List.of("xml-factory-create"), trace.kinds(factory));
                try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
                    assertEquals(List.of("xml-manager-create"), trace.kinds(manager));
                    assertEquals(List.of("xml-agent-create"), trace.kinds(agent));
                }
            }
            assertEquals(1, trace.events.stream().filter(event -> event.kind().equals("xml-factory-close")).count());
        }
    }

    @Test
    public void xmlEventDeclarationsOverrideAnnotations() {
        try (Unit unit = new Unit(Listener.class)) {
            unit.mapping("overrides.xml");
            verifySixCallbacks(unit, unit.create(), "xml-");
        }
    }

    @Test
    public void globalMetadataCompleteSuppressesLifecycleAnnotations() {
        try (Unit unit = new Unit(Listener.class)) {
            unit.mapping("complete.xml");
            EntityManagerFactory factory = unit.create();
            try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
                assertTrue(unit.trace.events.isEmpty());
            }
            factory.close();
            assertTrue(unit.trace.events.isEmpty());
        }
    }

    @Test
    public void cachedProjectRetainsAnnotationAndXmlLifecycleDefinitions() {
        for (String mapping : List.of("", "defaults.xml")) {
            try (Unit unit = mapping.isEmpty() ? new Unit(Listener.class, Record.class) : new Unit(Record.class)) {
                if (!mapping.isEmpty()) {
                    unit.mapping(mapping);
                }
                MemoryCache cache = new MemoryCache();
                unit.properties.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
                EntityManagerFactory first = unit.create();
                first.unwrap(JpaEntityManagerFactory.class).getServerSession();
                assertNotNull(cache.project);
                first.close();
                EntityManagerFactory restored = unit.create();
                assertEquals(1, cache.hits);
                assertEquals(1, cache.stores);
                verifySixCallbacks(unit, restored, mapping.isEmpty() ? "" : "xml-");
            }
        }
    }

    @Test
    public void cachedProjectRetainsEffectiveXmlLifecycleCompleteness() throws Exception {
        List<AssertionError> failures = new ArrayList<>();
        for (boolean mappedSuperclass : List.of(false, true)) {
            for (String overlay : List.of("incomplete", "none", "complete", "inherited-complete")) {
                try {
                    verifyCachedXmlOverlay(mappedSuperclass, overlay);
                } catch (AssertionError failure) {
                    failures.add(new AssertionError("Mapped superclass=" + mappedSuperclass + ", overlay=" + overlay, failure));
                }
            }
        }
        if (!failures.isEmpty()) {
            AssertionError failure = failures.getFirst();
            failures.stream().skip(1).forEach(failure::addSuppressed);
            throw failure;
        }
    }

    private void verifyCachedXmlOverlay(boolean mappedSuperclass, String overlay) throws Exception {
        Class<?> entity = mappedSuperclass ? XmlOverlayChild.class : XmlOverlayEntity.class;
        String accessor = mappedSuperclass ? "mapped-superclass" : "entity";
        String accessorClass = mappedSuperclass ? XmlOverlayBase.class.getName() : entity.getName();
        String listeners = "<entity-listeners><entity-listener class=\"" + XmlOverlayListener.class.getName()
                + "\"/></entity-listeners>";
        boolean inheritedComplete = overlay.equals("inherited-complete");
        String base = "<" + accessor + " class=\"" + accessorClass + "\" access=\"FIELD\""
                + (inheritedComplete ? " metadata-complete=\"true\"" : "") + ">"
                + (mappedSuperclass ? "" : "<table name=\"JPA_PUL_XML_BASE\"/>")
                + listeners + "<attributes><id name=\"id\"/>"
                + (mappedSuperclass ? "" : "<basic name=\"value\"/>") + "</attributes></" + accessor + ">";
        if (mappedSuperclass) {
            base += "<entity class=\"" + entity.getName() + "\" access=\"FIELD\">"
                    + "<table name=\"JPA_PUL_XML_BASE\"/><attributes><basic name=\"value\"/></attributes></entity>";
        }
        String override = "<" + accessor + " class=\"" + accessorClass + "\" access=\"FIELD\""
                + (inheritedComplete ? "" : " metadata-complete=\"" + overlay.equals("complete") + "\"") + ">"
                + (mappedSuperclass ? "" : "<table name=\"JPA_PUL_XML_OVERRIDE\"/>")
                + (inheritedComplete ? listeners : "") + "</" + accessor + ">";
        if (mappedSuperclass) {
            override += "<entity class=\"" + entity.getName() + "\" access=\"FIELD\">"
                    + "<table name=\"JPA_PUL_XML_OVERRIDE\"/></entity>";
        }
        Path jar = Files.createTempFile("persistence-unit-lifecycle-overlay-", ".jar");
        try {
            try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
                writeXmlMapping(output, "META-INF/orm.xml", "https://jakarta.ee/xml/ns/persistence/orm",
                        "https://jakarta.ee/xml/ns/persistence/orm/orm_4_0.xsd", "4.0", base);
                if (!overlay.equals("none")) {
                    writeXmlMapping(output, "META-INF/eclipselink-orm.xml",
                            "http://www.eclipse.org/eclipselink/xsds/persistence/orm",
                            "http://www.eclipse.org/eclipselink/xsds/eclipselink_orm_5_0.xsd", "5.0", override);
                }
            }
            try (Unit unit = new Unit(entity)) {
                unit.info.setPersistenceUnitRootUrl(jar.toUri().toURL());
                MemoryCache cache = new MemoryCache();
                unit.properties.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
                int descriptorCount = 0;
                for (int attempt = 0; attempt < 2; attempt++) {
                    EntityManagerFactory factory = unit.create();
                    var session = factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
                    assertEquals(overlay.equals("none") ? "JPA_PUL_XML_BASE" : "JPA_PUL_XML_OVERRIDE",
                            session.getDescriptor(entity).getDefaultTable().getName());
                    assertEquals("id", factory.getMetamodel().entity(entity).getId(int.class).getName());
                    assertEquals(attempt, cache.hits);
                    assertEquals(1, cache.stores);
                    assertNotNull(cache.project);
                    if (attempt == 0) {
                        descriptorCount = cache.project.getDescriptors().size();
                    } else {
                        assertEquals(descriptorCount, cache.project.getDescriptors().size());
                    }
                    if (overlay.equals("complete") || inheritedComplete) {
                        try (EntityManager manager = factory.createEntityManager(); EntityAgent agent = factory.createEntityAgent()) {
                            assertTrue(manager.isOpen());
                            assertTrue(agent.isOpen());
                        }
                        factory.close();
                        assertTrue("Callbacks must be suppressed on " + (attempt == 0 ? "cache miss" : "cache hit"),
                                unit.trace.events.isEmpty());
                    } else {
                        verifySixCallbacks(unit, factory, "");
                    }
                }
            }
        } finally {
            Files.deleteIfExists(jar);
        }
    }

    private static void writeXmlMapping(JarOutputStream output, String name, String namespace,
            String schema, String version, String content) throws Exception {
        output.putNextEntry(new JarEntry(name));
        output.write(("<entity-mappings xmlns=\"" + namespace
                + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\""
                + namespace + " " + schema + "\" version=\"" + version + "\">" + content
                + "</entity-mappings>").getBytes(StandardCharsets.UTF_8));
        output.closeEntry();
    }

    @Test
    public void compositeMembersContributeOneDeduplicatedLifecycleWithoutMemberFactories() throws Exception {
        Path jar = Files.createTempFile("persistence-unit-lifecycle-members-", ".jar");
        String firstName = "pul-first-" + UUID.randomUUID();
        String secondName = "pul-second-" + UUID.randomUUID();
        String xml = """
                <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_4_0.xsd"
                             version="4.0">
                    <persistence-unit name="%s" transaction-type="RESOURCE_LOCAL">
                        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                        <class>%s</class>
                        <class>%s</class>
                        <exclude-unlisted-classes>true</exclude-unlisted-classes>
                        <validation-mode>NONE</validation-mode>
                        <properties><property name="eclipselink.composite-unit.member" value="true"/></properties>
                    </persistence-unit>
                    <persistence-unit name="%s" transaction-type="RESOURCE_LOCAL">
                        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                        <class>%s</class>
                        <class>%s</class>
                        <exclude-unlisted-classes>true</exclude-unlisted-classes>
                        <validation-mode>NONE</validation-mode>
                        <properties><property name="eclipselink.composite-unit.member" value="true"/></properties>
                    </persistence-unit>
                </persistence>
                """.formatted(firstName, Record.class.getName(), Listener.class.getName(),
                        secondName, Other.class.getName(), Listener.class.getName());
        try {
            try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
                output.putNextEntry(new JarEntry("META-INF/persistence.xml"));
                output.write(xml.getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
            try (Unit unit = new Unit(); Trace firstTrace = new Trace(firstName); Trace secondTrace = new Trace(secondName)) {
                unit.info.setJarFileUrls(List.of(jar.toUri().toURL()));
                unit.info.getProperties().put(PersistenceUnitProperties.COMPOSITE_UNIT, "true");
                unit.properties.put(PersistenceUnitProperties.COMPOSITE_UNIT, "true");
                MemoryCache firstCache = new MemoryCache();
                MemoryCache secondCache = new MemoryCache();
                Map<String, Object> firstProperties = properties();
                Map<String, Object> secondProperties = properties();
                firstProperties.put(PersistenceUnitProperties.PROJECT_CACHE, firstCache);
                secondProperties.put(PersistenceUnitProperties.PROJECT_CACHE, secondCache);
                unit.properties.put(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES,
                        Map.of(firstName, firstProperties, secondName, secondProperties));
                for (int attempt = 0; attempt < 2; attempt++) {
                    EntityManagerFactory factory = unit.create();
                    assertEquals(List.of("factory-create"), unit.trace.kinds(factory));
                    try (EntityManager manager = factory.createEntityManager()) {
                        assertEquals(List.of("manager-create"), unit.trace.kinds(manager));
                        assertEquals(0L, manager.createQuery("select count(r) from PersistenceUnitLifecycleRecord r", Long.class)
                                .getSingleResult().longValue());
                    }
                    factory.close();
                    assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
                    assertEquals(attempt, firstCache.hits);
                    assertEquals(attempt, secondCache.hits);
                }
                assertTrue(firstTrace.events.isEmpty());
                assertTrue(secondTrace.events.isEmpty());
                assertEquals(1, firstCache.stores);
                assertEquals(1, secondCache.stores);
                assertEquals(2, unit.trace.events.stream().filter(event -> event.kind().equals("factory-create")).count());
            }
        } finally {
            Files.deleteIfExists(jar);
        }
    }

    @Test
    public void invalidLifecycleSignaturesAndDuplicateEventsFailBootstrap() {
        for (String mapping : List.of("zero", "two", "type", "return", "static", "final", "duplicate", "missing")) {
            try (Unit unit = new Unit()) {
                unit.mapping("invalid-" + mapping + ".xml");
                RuntimeException failure = assertThrows("Invalid " + mapping + " callback must fail",
                        RuntimeException.class, () -> {
                            EntityManagerFactory factory = unit.create();
                            factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
                        });
                boolean validation = false;
                for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                    validation |= cause instanceof ValidationException;
                }
                assertTrue("Expected callback validation failure for " + mapping, validation);
                assertTrue(unit.trace.events.isEmpty());
            }
        }
    }

    private String value(int id) {
        try (EntityAgent observer = fixture.createEntityAgent()) {
            return observer.get(Record.class, id).value;
        }
    }

    private static void verifyConcurrentFactoryClose(boolean nativeContender) throws Exception {
        try (Unit unit = new Unit(Listener.class)) {
            EntityManagerFactory factory = unit.create();
            EntityManagerFactory competing = nativeContender
                    ? factory.unwrap(EntityManagerFactoryDelegate.class) : factory;
            CountDownLatch callbackEntered = new CountDownLatch(1);
            CountDownLatch releaseCallback = new CountDownLatch(1);
            unit.trace.action = event -> {
                if (event.kind().equals("factory-close")) {
                    assertSame(factory, event.target());
                    callbackEntered.countDown();
                    try {
                        assertTrue("First close callback was not released", releaseCallback.await(10, TimeUnit.SECONDS));
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError("Close callback interrupted", interrupted);
                    }
                    assertTrue("The callback argument must remain open until callback completion", factory.isOpen());
                }
            };
            FutureTask<Throwable> first = new FutureTask<>(() -> closeFailure(factory));
            FutureTask<Throwable> second = new FutureTask<>(() -> closeFailure(competing));
            Thread firstThread = Thread.ofPlatform().daemon().name("pul-first-close").unstarted(first);
            Thread secondThread = Thread.ofPlatform().daemon().name("pul-competing-close").unstarted(second);
            boolean waited;
            boolean remainedOpen;
            firstThread.start();
            try {
                assertTrue("First close did not enter its callback", callbackEntered.await(10, TimeUnit.SECONDS));
                assertTrue(factory.isOpen());
                secondThread.start();
                waited = awaitCompetingClose(secondThread, second);
                remainedOpen = factory.isOpen();
            } finally {
                releaseCallback.countDown();
            }
            Throwable firstFailure = first.get(10, TimeUnit.SECONDS);
            Throwable secondFailure = second.get(10, TimeUnit.SECONDS);
            assertTrue("A competing close must wait until the active close completes", waited);
            assertTrue("A competing close must not destroy the active callback argument", remainedOpen);
            if (firstFailure != null) {
                throw new AssertionError("The first close must complete successfully", firstFailure);
            }
            assertTrue("The competing close must report the ordinary repeated-close exception",
                    secondFailure instanceof IllegalStateException);
            assertFalse(factory.isOpen());
            assertEquals(List.of("factory-create", "factory-close"), unit.trace.kinds(factory));
            assertThrows(IllegalStateException.class, factory::close);
        }
    }

    /** Observe the competing call inside close, rather than relying on a scheduling delay. */
    private static boolean awaitCompetingClose(Thread thread, FutureTask<Throwable> result) {
        return awaitFactoryOperation(thread, result, "close");
    }

    private static boolean awaitFactoryOperation(Thread thread, FutureTask<Throwable> result, String method) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (!result.isDone()) {
            Thread.State state = thread.getState();
            if (state == Thread.State.WAITING || state == Thread.State.BLOCKED) {
                for (StackTraceElement frame : thread.getStackTrace()) {
                    if (frame.getClassName().equals(EntityManagerFactoryImpl.class.getName())
                            && frame.getMethodName().equals(method)) {
                        return true;
                    }
                }
            }
            assertTrue("Competing factory operation neither completed nor waited for the active close",
                    System.nanoTime() < deadline);
            Thread.onSpinWait();
        }
        return false;
    }

    private static Throwable closeFailure(EntityManagerFactory factory) {
        try {
            factory.close();
            return null;
        } catch (Throwable failure) {
            return failure;
        }
    }

    private static void verifySixCallbacks(Unit unit, EntityManagerFactory factory, String prefix) {
        assertEquals(List.of(prefix + "factory-create"), unit.trace.kinds(factory));
        EntityManager manager = factory.createEntityManager();
        EntityAgent agent = factory.createEntityAgent();
        assertEquals(List.of(prefix + "manager-create"), unit.trace.kinds(manager));
        assertEquals(List.of(prefix + "agent-create"), unit.trace.kinds(agent));
        manager.close();
        agent.close();
        factory.close();
        assertEquals(List.of(prefix + "factory-create", prefix + "factory-close"), unit.trace.kinds(factory));
        assertEquals(List.of(prefix + "manager-create", prefix + "manager-close"), unit.trace.kinds(manager));
        assertEquals(List.of(prefix + "agent-create", prefix + "agent-close"), unit.trace.kinds(agent));
        assertTrue(unit.trace.events.stream().allMatch(Event::open));
    }

    private static void verifyHelper(Unit unit, EntityHandler handler, List<EntityHandler> handlers) {
        String type = handler instanceof EntityManager ? "manager" : "agent";
        assertEquals(List.of(type + "-create"), unit.trace.kinds(handler));
        assertTrue(handler.getTransaction().isActive());
        handlers.add(handler);
    }

    private static boolean isOpen(Object target) {
        return target instanceof EntityManagerFactory factory ? factory.isOpen() : ((EntityHandler) target).isOpen();
    }

    private static void close(Object target) {
        if (target instanceof EntityManagerFactory factory) {
            factory.close();
        } else {
            ((EntityHandler) target).close();
        }
    }

    private static void assertCause(Throwable expected, Throwable actual) {
        for (Throwable cause = actual; cause != null; cause = cause.getCause()) {
            if (cause == expected) {
                return;
            }
        }
        throw new AssertionError("Original lifecycle callback failure was not retained", actual);
    }

    public static final class MemoryCache implements ProjectCache {
        private Project project;
        private int hits;
        private int stores;

        @Override
        public Project retrieveProject(Map<String, ?> properties, ClassLoader loader, SessionLog log) {
            if (project != null) {
                hits++;
            }
            return project;
        }

        @Override
        public void storeProject(Project project, Map<String, ?> properties, SessionLog log) {
            stores++;
            this.project = project;
        }
    }

    /** Read-only JTA association uses the same database and opaque JDBC bridge as the lane. */
    private static final class LaneDataSource implements DataSource {
        private final Driver driver;
        private final String url;
        private final Properties connectionProperties = new Properties();

        private LaneDataSource(Map<String, Object> settings) throws Exception {
            driver = (Driver) Class.forName((String) settings.get(PersistenceUnitProperties.JDBC_DRIVER))
                    .getConstructor().newInstance();
            url = (String) settings.get(PersistenceUnitProperties.JDBC_URL);
            connectionProperties.setProperty("user", (String) settings.getOrDefault(PersistenceUnitProperties.JDBC_USER, ""));
            connectionProperties.setProperty("password", (String) settings.getOrDefault(PersistenceUnitProperties.JDBC_PASSWORD, ""));
        }

        @Override
        public Connection getConnection() throws SQLException {
            return driver.connect(url, connectionProperties);
        }

        @Override
        public Connection getConnection(String user, String password) throws SQLException {
            Properties values = new Properties();
            values.setProperty("user", user);
            values.setProperty("password", password);
            return driver.connect(url, values);
        }

        @Override
        public PrintWriter getLogWriter() {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter writer) {
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public void setLoginTimeout(int seconds) {
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }

        @Override
        public <T> T unwrap(Class<T> type) throws SQLException {
            if (type.isInstance(this)) {
                return type.cast(this);
            }
            throw new SQLException("Unsupported fixture datasource unwrap");
        }

        @Override
        public boolean isWrapperFor(Class<?> type) {
            return type.isInstance(this);
        }
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "false");
        properties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return properties;
    }

    private static final class Unit implements AutoCloseable {
        final String name = "persistence-unit-lifecycle-" + UUID.randomUUID();
        final Trace trace = new Trace(name);
        final SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        final Map<String, Object> properties = properties();
        final List<EntityManagerFactory> factories = new ArrayList<>();
        final List<EntityManagerFactoryDelegate> retired = new ArrayList<>();

        Unit(Class<?>... classes) {
            info.setPersistenceUnitName(name);
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(TestPersistenceUnitLifecycle.class.getClassLoader());
            info.setNewTempClassLoader(TestPersistenceUnitLifecycle.class.getClassLoader());
            info.setPersistenceUnitRootUrl(TestPersistenceUnitLifecycle.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(java.util.Arrays.stream(classes).map(Class::getName).toList());
            info.setExcludeUnlistedClasses(true);
            info.setProperties(new Properties());
            properties.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
        }

        EntityManagerFactory create() {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(name, properties);
            factories.add(factory);
            return factory;
        }

        void mapping(String resource) {
            info.setMappingFileNames(List.of("META-INF/persistence-unit-lifecycle/" + resource));
        }

        @Override
        public void close() {
            RuntimeException failure = null;
            try {
                List<EntityManagerFactory> owned = new ArrayList<>(factories.reversed());
                owned.addAll(retired);
                for (EntityManagerFactory factory : owned) {
                    try {
                        if (factory.isOpen()) {
                            factory.close();
                        }
                    } catch (RuntimeException cleanup) {
                        if (failure == null) {
                            failure = cleanup;
                        } else {
                            failure.addSuppressed(cleanup);
                        }
                    }
                }
            } finally {
                trace.close();
            }
            if (failure != null) {
                throw failure;
            }
        }
    }
}
