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
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PreClose;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.QueryImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.cdi.InjectionManager;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceUnitListenerRegistryTest {
    @Test
    void managerOwnershipFailureStillReleasesAllResources() throws Exception {
        InjectionSession session = new InjectionSession();
        ControlledFactory factory = completedFactory(session);
        PersistenceUnitListenerRegistry registry = factoryRegistry(factory);
        CloseRaceManager manager = new CloseRaceManager(factory);
        Field listeners = EntityManagerImpl.class.getDeclaredField("persistenceUnitListeners");
        listeners.setAccessible(true);
        listeners.set(manager, registry);
        TrackedClientSession parent = session.acquireClientSession(Map.of());
        TrackedClientSession readOnly = session.acquireClientSession(Map.of());
        TrackedContext context = new TrackedContext(parent);
        TrackedQuery query = new TrackedQuery(manager);
        manager.installResources(context, readOnly, query);
        manager.closeFactoryAfterOpenCheck = true;
        IllegalStateException failure = assertThrows(IllegalStateException.class, manager::close);
        try {
            assertEquals("Persistence unit lifecycle listeners are closed", failure.getMessage());
            assertEquals(1, query.releases.get());
            assertEquals(1, context.releases.get());
            assertEquals(1, parent.releases.get());
            assertEquals(1, readOnly.releases.get());
            assertTrue(suppressed(failure).contains(query.failure));
            assertTrue(manager.resourcesCleared());
            assertFalse(manager.isOpen());
            ManagedInjection injection = session.managers.getFirst();
            assertEquals(1, injection.factoryPreClose.get());
            assertEquals(0, injection.managerPreClose.get());
            assertEquals(1, injection.cleanups.get());
            assertTrue(injection.destroyed.get());
            // An erroneous release after a failed retain would make the count
            // negative and incorrectly allow another ownership acquisition.
            assertThrows(IllegalStateException.class, registry::retainAgent);
        } finally {
            context.release();
            parent.release();
            readOnly.release();
            registry.releaseFactory();
        }
    }

    @Test
    void delegateReplacementDuringCloseRetainsListenerUntilLiveAgentCloses() throws Exception {
        for (boolean closeOriginalInCallback : new boolean[] {false, true}) {
            InjectionSession session = new InjectionSession();
            ControlledFactory factory = completedFactory(session);
            PersistenceUnitListenerRegistry registry = factoryRegistry(factory);
            EntityManagerFactoryDelegate original = factory.unwrap();
            EntityAgent agent = factory.createEntityAgent();
            ManagedInjection injection = session.managers.getFirst();
            injection.onFactoryPreClose = () -> {
                assertSame(original, factory.replaceDelegate(session));
                if (closeOriginalInCallback) {
                    original.close();
                }
                assertFalse(injection.destroyed.get());
            };
            try {
                original.close();
                assertFalse(original.isOpen());
                assertFalse(factory.unwrap().isOpen());
                assertFalse(factory.isOpen());
                assertSame(factory, injection.factoryTarget);
                assertEquals(1, injection.factoryPreClose.get());
                assertEquals(0, injection.cleanups.get());
                assertTrue(agent.isOpen());
                agent.close();
                assertEquals(1, injection.preClose.get());
                assertEquals(1, injection.cleanups.get());
                assertTrue(injection.destroyed.get());
            } finally {
                if (agent.isOpen()) {
                    agent.close();
                }
                if (factory.isOpen()) {
                    factory.close();
                }
                if (original.isOpen()) {
                    original.close();
                }
                registry.releaseFactory();
            }
        }
    }

    @Test
    void currentDelegateCloseRetainsInjectedListenerUntilLiveAgentCloses() throws Exception {
        InjectionSession session = new InjectionSession();
        ControlledFactory factory = completedFactory(session);
        PersistenceUnitListenerRegistry registry = factoryRegistry(factory);
        EntityAgent agent = factory.createEntityAgent();
        ManagedInjection injection = session.managers.getFirst();
        try {
            factory.unwrap(EntityManagerFactoryDelegate.class).close();
            assertFalse(factory.isOpen());
            assertEquals(1, injection.factoryPreClose.get());
            assertSame(factory, injection.factoryTarget);
            assertEquals(0, injection.cleanups.get());
            assertTrue(agent.isOpen());
            agent.close();
            assertEquals(1, injection.preClose.get());
            assertEquals(1, injection.cleanups.get());
            assertTrue(injection.destroyed.get());
            assertThrows(IllegalStateException.class, factory::close);
        } finally {
            if (agent.isOpen()) {
                agent.close();
            }
            if (factory.isOpen()) {
                factory.close();
            }
            registry.releaseFactory();
        }
    }

    @Test
    void retiredDelegateCloseDoesNotReleaseCurrentFactoryListener() throws Exception {
        InjectionSession session = new InjectionSession();
        ControlledFactory factory = completedFactory(session);
        PersistenceUnitListenerRegistry registry = factoryRegistry(factory);
        EntityManagerFactoryDelegate retired = factory.replaceDelegate(session);
        EntityAgent agent = factory.createEntityAgent();
        ManagedInjection injection = session.managers.getFirst();
        try {
            retired.close();
            assertTrue(factory.isOpen());
            assertEquals(0, injection.factoryPreClose.get());
            assertEquals(0, injection.cleanups.get());
            agent.close();
            assertEquals(1, injection.preClose.get());
            assertEquals(0, injection.cleanups.get());
            factory.close();
            assertSame(factory, injection.factoryTarget);
            assertEquals(1, injection.factoryPreClose.get());
            assertEquals(1, injection.cleanups.get());
        } finally {
            if (agent.isOpen()) {
                agent.close();
            }
            if (factory.isOpen()) {
                factory.close();
            }
            registry.releaseFactory();
        }
    }

    @Test
    void closingFactoryInsideCallbackKeepsAllDispatchListenersAlive() {
        InjectionSession session = new InjectionSession();
        PersistenceUnitListenerRegistry registry = registry(session, ManagedListener.class, SecondListener.class);
        session.managers.getFirst().onPostCreate = registry::releaseFactory;
        registry.postCreate(EntityAgent.class, agent());
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.postCreate.get());
            assertEquals(1, manager.cleanups.get());
            assertTrue(manager.destroyed.get());
        }
    }

    @Test
    void injectedListenerRemainsAliveAfterFactoryCloseUntilLastAgentReleases() {
        InjectionSession session = new InjectionSession();
        PersistenceUnitListenerRegistry registry = registry(session, ManagedListener.class);
        ManagedInjection manager = session.managers.getFirst();
        EntityAgent agent = agent();
        registry.retainAgent();
        registry.retainAgent();
        registry.postCreate(EntityAgent.class, agent);
        assertEquals(1, manager.created.get());
        assertEquals(1, manager.postCreate.get());
        registry.releaseFactory();
        registry.releaseFactory();
        assertEquals(0, manager.cleanups.get());
        assertNull(registry.preClose(EntityAgent.class, agent));
        registry.releaseAgent();
        assertEquals(0, manager.cleanups.get());
        assertNull(registry.preClose(EntityAgent.class, agent));
        registry.releaseAgent();
        assertEquals(2, manager.preClose.get());
        assertEquals(1, manager.cleanups.get());
        assertTrue(manager.destroyed.get());
        assertThrows(IllegalStateException.class, registry::retainAgent);
    }

    @Test
    void registriesSharingSessionOwnDistinctInjectionManagers() {
        InjectionSession session = new InjectionSession();
        InjectionManager<?> sessionOwned = session.getInjectionManager();
        PersistenceUnitListenerRegistry first = registry(session, ManagedListener.class);
        PersistenceUnitListenerRegistry second = registry(session, ManagedListener.class);
        assertEquals(3, session.managers.size());
        assertSame(session.managers.getFirst(), sessionOwned);
        first.releaseFactory();
        second.postCreate(EntityAgent.class, agent());
        assertEquals(0, session.managers.get(0).cleanups.get());
        assertEquals(1, session.managers.get(1).cleanups.get());
        assertEquals(0, session.managers.get(2).cleanups.get());
        assertEquals(1, session.managers.get(2).postCreate.get());
        second.releaseFactory();
        assertEquals(1, session.managers.get(2).cleanups.get());
        sessionOwned.cleanUp();
    }

    @Test
    void preCloseAttemptsWholeChainAndRetainsPrimaryAndSuppressedFailures() {
        InjectionSession session = new InjectionSession();
        PersistenceUnitListenerRegistry registry = registry(session,
                FirstCloseFailure.class, SecondCloseFailure.class, ManagedListener.class);
        Throwable failure = registry.preClose(EntityAgent.class, agent());
        assertSame(session.managers.get(0).callbackFailure, failure);
        assertEquals(1, failure.getSuppressed().length);
        assertSame(session.managers.get(1).callbackFailure, failure.getSuppressed()[0]);
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.preClose.get());
        }
        registry.releaseFactory();
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.cleanups.get());
        }
    }

    @Test
    void postCreateStopsDispatchAndCleanupPreservesOriginalFailure() {
        InjectionSession session = new InjectionSession();
        session.failCleanup = true;
        PersistenceUnitListenerRegistry registry = registry(session, CreateFailure.class, ManagedListener.class);
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> registry.postCreate(EntityAgent.class, agent()));
        assertSame(session.managers.get(0).callbackFailure, failure);
        assertEquals(1, session.managers.get(0).postCreate.get());
        assertEquals(0, session.managers.get(1).postCreate.get());
        assertSame(failure, PersistenceUnitListenerRegistry.cleanup(failure, registry::releaseFactory));
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.cleanups.get());
            assertTrue(suppressed(failure).contains(manager.cleanupFailure));
        }
    }

    @Test
    void failedListenerConstructionDisposesEveryAlreadyAllocatedManager() {
        InjectionSession session = new InjectionSession();
        session.failCleanup = true;
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> registry(session, ManagedListener.class, BrokenListener.class));
        assertSame(BrokenListener.FAILURE, failure);
        assertEquals(2, session.managers.size());
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.cleanups.get());
            assertTrue(manager.destroyed.get());
            assertTrue(suppressed(failure).contains(manager.cleanupFailure));
        }
    }

    @Test
    void failingDestroyDoesNotPreventOtherListenerCleanupOrRepeatDestruction() {
        InjectionSession session = new InjectionSession();
        session.failCleanup = true;
        PersistenceUnitListenerRegistry registry = registry(session, ManagedListener.class, SecondListener.class);
        IllegalStateException failure = assertThrows(IllegalStateException.class, registry::releaseFactory);
        assertSame(session.managers.get(0).cleanupFailure, failure);
        assertEquals(1, failure.getSuppressed().length);
        assertSame(session.managers.get(1).cleanupFailure, failure.getSuppressed()[0]);
        registry.releaseFactory();
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.cleanups.get());
        }
    }

    @Test
    void concurrentAgentOwnershipAndDispatchDoNotCrossFactories() throws Exception {
        InjectionSession session = new InjectionSession();
        PersistenceUnitListenerRegistry first = registry(session, ManagedListener.class);
        PersistenceUnitListenerRegistry second = registry(session, ManagedListener.class);
        first.retainAgent();
        second.retainAgent();
        first.releaseFactory();
        second.releaseFactory();
        CountDownLatch start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(8);
        try {
            List<java.util.concurrent.Future<?>> calls = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                PersistenceUnitListenerRegistry registry = (i & 1) == 0 ? first : second;
                calls.add(executor.submit(() -> {
                    await(start);
                    registry.retainAgent();
                    try {
                        EntityAgent target = agent();
                        registry.postCreate(EntityAgent.class, target);
                        assertNull(registry.preClose(EntityAgent.class, target));
                    } finally {
                        registry.releaseAgent();
                    }
                }));
            }
            start.countDown();
            for (var call : calls) {
                call.get(10, TimeUnit.SECONDS);
            }
            for (ManagedInjection manager : session.managers) {
                assertEquals(32, manager.postCreate.get());
                assertEquals(32, manager.preClose.get());
                assertEquals(0, manager.cleanups.get());
            }
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
            first.releaseAgent();
            second.releaseAgent();
        }
        for (ManagedInjection manager : session.managers) {
            assertEquals(1, manager.cleanups.get());
        }
    }

    private static ControlledFactory completedFactory(InjectionSession session) {
        session.setProperty(PersistenceUnitListener.DEFINITIONS_PROPERTY,
                List.of(new PersistenceUnitListener(CloseOwnershipListener.class.getName(), true, null, null)));
        ControlledFactory factory = new ControlledFactory(session);
        factory.completeCreation();
        return factory;
    }

    private static PersistenceUnitListenerRegistry factoryRegistry(EntityManagerFactoryImpl factory) throws Exception {
        Field listeners = EntityManagerFactoryImpl.class.getDeclaredField("persistenceUnitListeners");
        listeners.setAccessible(true);
        return (PersistenceUnitListenerRegistry) listeners.get(factory);
    }

    private static PersistenceUnitListenerRegistry registry(InjectionSession session, Class<?>... types) {
        List<PersistenceUnitListener> definitions = new ArrayList<>();
        for (Class<?> type : types) {
            definitions.add(new PersistenceUnitListener(type.getName(), true, null, null));
        }
        return new PersistenceUnitListenerRegistry(definitions, session);
    }

    private static List<Throwable> suppressed(Throwable failure) {
        List<Throwable> result = new ArrayList<>();
        for (Throwable secondary : failure.getSuppressed()) {
            result.add(secondary);
            result.addAll(suppressed(secondary));
        }
        return result;
    }

    private static EntityAgent agent() {
        return (EntityAgent) Proxy.newProxyInstance(EntityAgent.class.getClassLoader(), new Class<?>[] {EntityAgent.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("isOpen")) {
                        return true;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        }
    }

    private static final class ControlledFactory extends EntityManagerFactoryImpl {
        private ControlledFactory(InjectionSession session) {
            super(session);
        }

        private EntityManagerFactoryDelegate replaceDelegate(InjectionSession session) {
            EntityManagerFactoryDelegate previous = delegate;
            delegate = new EntityManagerFactoryDelegate(session, this);
            return previous;
        }
    }

    private static final class CloseRaceManager extends EntityManagerImpl {
        private final ControlledFactory owner;
        private boolean closeFactoryAfterOpenCheck;

        private CloseRaceManager(ControlledFactory factory) {
            super(factory.unwrap(), null, null);
            owner = factory;
        }

        @Override
        public void verifyOpen() {
            super.verifyOpen();
            if (closeFactoryAfterOpenCheck) {
                closeFactoryAfterOpenCheck = false;
                owner.close();
            }
        }

        private void installResources(TrackedContext context, TrackedClientSession readOnly, TrackedQuery query) {
            extendedPersistenceContext = context;
            readOnlySession = readOnly;
            addOpenQuery(query);
        }

        private boolean resourcesCleared() {
            return extendedPersistenceContext == null && readOnlySession == null
                    && databaseSession == null && factory == null;
        }
    }

    private static final class TrackedQuery extends QueryImpl {
        private final AtomicInteger releases = new AtomicInteger();
        private final IllegalStateException failure = new IllegalStateException("query release");

        private TrackedQuery(EntityManagerImpl manager) {
            super(new DataReadQuery(), manager);
        }

        @Override
        public void releaseResources() {
            releases.incrementAndGet();
            super.releaseResources();
            throw failure;
        }
    }

    private static final class TrackedContext extends RepeatableWriteUnitOfWork {
        private final AtomicInteger releases = new AtomicInteger();

        private TrackedContext(ClientSession parent) {
            super(parent, ReferenceMode.HARD);
        }

        @Override
        public void release() {
            releases.incrementAndGet();
            super.release();
        }
    }

    private static final class TrackedClientSession extends ClientSession {
        private final AtomicInteger releases = new AtomicInteger();

        private TrackedClientSession(ServerSession parent) {
            super(parent, parent.getDefaultConnectionPolicy());
        }

        @Override
        public void release() {
            releases.incrementAndGet();
            super.release();
        }
    }

    private static final class InjectionSession extends ServerSession {
        private final List<ManagedInjection> managers = new ArrayList<>();
        private boolean failCleanup;

        private InjectionSession() {
            super(new DatabaseLogin());
        }

        @Override
        @SuppressWarnings("rawtypes")
        public TrackedClientSession acquireClientSession(Map properties) {
            return new TrackedClientSession(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> InjectionManager<T> createInjectionManager(Object beanManager) {
            ManagedInjection manager = new ManagedInjection(failCleanup);
            managers.add(manager);
            return (InjectionManager<T>) manager;
        }
    }

    private static final class ManagedInjection implements InjectionManager<Object> {
        private final AtomicInteger created = new AtomicInteger();
        private final AtomicInteger postCreate = new AtomicInteger();
        private final AtomicInteger preClose = new AtomicInteger();
        private final AtomicInteger cleanups = new AtomicInteger();
        private final AtomicInteger factoryPreClose = new AtomicInteger();
        private final AtomicInteger managerPreClose = new AtomicInteger();
        private EntityManagerFactory factoryTarget;
        private final AtomicBoolean destroyed = new AtomicBoolean();
        private final IllegalStateException callbackFailure = new IllegalStateException("application callback");
        private final IllegalStateException cleanupFailure = new IllegalStateException("listener destroy");
        private final boolean failCleanup;
        private Runnable onPostCreate = () -> { };
        private Runnable onFactoryPreClose = () -> { };

        private ManagedInjection(boolean failCleanup) {
            this.failCleanup = failCleanup;
        }

        @Override
        public Object createManagedBeanAndInjectDependencies(Class<Object> type) {
            if (type == (Class<?>) BrokenListener.class) {
                return null;
            }
            try {
                ManagedListener listener = (ManagedListener) type.getConstructor().newInstance();
                listener.owner = this;
                created.incrementAndGet();
                return listener;
            } catch (ReflectiveOperationException failure) {
                throw new AssertionError(failure);
            }
        }

        @Override
        public void cleanUp() {
            assertEquals(1, cleanups.incrementAndGet());
            destroyed.set(true);
            if (failCleanup) {
                throw cleanupFailure;
            }
        }
    }

    public static class ManagedListener {
        ManagedInjection owner;

        @PostCreate
        public void created(EntityAgent target) {
            assertFalse(owner.destroyed.get());
            assertTrue(target.isOpen());
            owner.postCreate.incrementAndGet();
            owner.onPostCreate.run();
        }

        @PreClose
        public void closing(EntityAgent target) {
            assertFalse(owner.destroyed.get());
            assertTrue(target.isOpen());
            owner.preClose.incrementAndGet();
        }
    }

    public static class CloseOwnershipListener extends ManagedListener {
        @PreClose
        public void closing(EntityManagerFactory target) {
            assertFalse(owner.destroyed.get());
            assertTrue(target.isOpen());
            owner.factoryPreClose.incrementAndGet();
            owner.factoryTarget = target;
            owner.onFactoryPreClose.run();
        }

        @PreClose
        public void closing(EntityManager target) {
            assertFalse(owner.destroyed.get());
            assertTrue(target.isOpen());
            owner.managerPreClose.incrementAndGet();
        }
    }

    public static class SecondListener extends ManagedListener { }

    public static class FirstCloseFailure extends ManagedListener {
        @Override
        @PreClose
        public void closing(EntityAgent target) {
            super.closing(target);
            throw owner.callbackFailure;
        }
    }

    public static class SecondCloseFailure extends FirstCloseFailure { }

    public static class CreateFailure extends ManagedListener {
        @Override
        @PostCreate
        public void created(EntityAgent target) {
            super.created(target);
            throw owner.callbackFailure;
        }
    }

    public static class BrokenListener {
        static final IllegalStateException FAILURE = new IllegalStateException("listener construction");

        public BrokenListener() {
            throw FAILURE;
        }

        @PostCreate
        public void created(EntityAgent target) { }
    }
}
