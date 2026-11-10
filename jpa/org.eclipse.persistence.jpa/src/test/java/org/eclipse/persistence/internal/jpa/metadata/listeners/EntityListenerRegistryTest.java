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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListenerRegistration;
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

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityListenerRegistryTest {
    @Test
    void recognizesAllFourteenCallbacksWithoutNativeBuildOrGenericUpdateDelivery() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        List<Class<? extends Annotation>> observed = new ArrayList<>();
        EntityValue entity = new EntityValue();
        DescriptorEvent event = event(entity);
        event.setSession(new UnitOfWorkImpl(session(EntityValue.class), ReferenceMode.HARD));
        List<Class<? extends Annotation>> annotations = List.of(PrePersist.class, PostPersist.class,
                PreRemove.class, PostRemove.class, PreMerge.class, PostLoad.class, PreInsert.class,
                PostInsert.class, PreUpdate.class, PostUpdate.class, PreUpsert.class, PostUpsert.class,
                PreDelete.class, PostDelete.class);
        for (Class<? extends Annotation> annotation : annotations) {
            registry.register(EntityValue.class, annotation, value -> {
                assertSame(entity, value);
                observed.add(annotation);
            });
        }
        List<Consumer<DescriptorEvent>> callbacks = List.of(dispatcher::prePersist, dispatcher::postPersist,
                dispatcher::preRemove, dispatcher::postRemove, dispatcher::preMerge, dispatcher::postClone,
                dispatcher::preInsert, dispatcher::postInsert, dispatcher::preUpdateWithChanges,
                dispatcher::postUpdate, dispatcher::preUpsert, dispatcher::postUpsert,
                dispatcher::preDelete, dispatcher::postDelete);
        callbacks.forEach(callback -> callback.accept(event));
        dispatcher.postBuild(event);
        dispatcher.preUpdate(event);
        assertEquals(annotations, observed);
        dispatcher.postRefresh(event);
        assertEquals(PostLoad.class, observed.getLast());
        assertEquals(15, observed.size());
    }

    @Test
    void filtersUseActualAssignableEntityOnceAndDuplicateTokensAreIndependent() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        EntityValue entity = new EntityValue();
        List<String> calls = new ArrayList<>();
        registry.register(Object.class, PrePersist.class, value -> calls.add("object"));
        registry.register(BaseValue.class, PrePersist.class, value -> calls.add("base"));
        registry.register(RootType.class, PrePersist.class, value -> calls.add("diamond"));
        registry.register(String.class, PrePersist.class, value -> calls.add("unmatched"));
        Consumer<EntityValue> duplicate = value -> calls.add("duplicate");
        EntityListenerRegistration first = registry.register(EntityValue.class, PrePersist.class, duplicate);
        registry.register(EntityValue.class, PrePersist.class, duplicate);
        dispatcher.prePersist(event(entity));
        assertEquals(List.of("object", "base", "diamond", "duplicate", "duplicate"), calls);
        calls.clear();
        first.cancel();
        first.cancel();
        dispatcher.prePersist(event(entity));
        assertEquals(List.of("object", "base", "diamond", "duplicate"), calls);
    }

    @Test
    void invalidInputsPublishNothingAndClosedRegistryRejectsRegistrations() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        AtomicInteger calls = new AtomicInteger();
        Consumer<Object> consumer = value -> calls.incrementAndGet();
        assertThrows(NullPointerException.class, () -> registry.register(null, PrePersist.class, consumer));
        assertThrows(NullPointerException.class, () -> registry.register(Object.class, null, consumer));
        assertThrows(NullPointerException.class, () -> registry.register(Object.class, PrePersist.class, null));
        assertThrows(IllegalArgumentException.class, () -> registry.register(Object.class, Entity.class, consumer));
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        dispatcher.prePersist(event(new EntityValue()));
        assertEquals(0, calls.get());
        EntityListenerRegistration token = registry.register(Object.class, PrePersist.class, consumer);
        registry.close();
        token.cancel();
        token.cancel();
        dispatcher.prePersist(event(new EntityValue()));
        assertEquals(0, calls.get());
        assertThrows(IllegalStateException.class, () -> registry.register(Object.class, PrePersist.class, consumer));
    }

    @Test
    void lateAndNewDeploymentActivationIncludesCompositeMembersAndClonedDescriptors() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        DatabaseSessionImpl oldSession = session(EntityValue.class);
        SessionBroker broker = new SessionBroker();
        broker.getSessionsByName().put("old", oldSession);
        registry.trackSession(broker);
        assertFalse(oldSession.getDescriptor(EntityValue.class).getEventManager().hasAnyEventListeners());
        EntityListenerRegistration token = registry.register(RootType.class, PrePersist.class, value -> { });
        assertTrue(oldSession.getDescriptor(EntityValue.class).getEventManager().hasAnyEventListeners());
        DatabaseSessionImpl replacement = session(EntityValue.class);
        ClassDescriptor clone = (ClassDescriptor) replacement.getDescriptor(EntityValue.class).clone();
        replacement.getDescriptors().put(EntityValue.class, clone);
        registry.trackSession(replacement);
        assertTrue(clone.getEventManager().hasAnyEventListeners());
        DatabaseSessionImpl unmatched = session(String.class);
        registry.trackSession(unmatched);
        assertFalse(unmatched.getDescriptor(String.class).getEventManager().hasAnyEventListeners());
        token.cancel();
        assertTrue(clone.getEventManager().hasAnyEventListeners());
        assertTrue(clone.getEventManager().getEventListeners().isEmpty());
        registry.close();
        DatabaseSessionImpl afterClose = session(EntityValue.class);
        registry.trackSession(afterClose);
        assertFalse(afterClose.getDescriptor(EntityValue.class).getEventManager().hasAnyEventListeners());
    }

    @Test
    void postLoadInheritsInternalReadSuppressedSourceAndRefreshTargetPredicates() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        List<Object> calls = new ArrayList<>();
        registry.register(Object.class, PostLoad.class, calls::add);
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        EntityValue entity = new EntityValue();
        DescriptorEvent event = event(entity);
        Object original = new Object();
        event.setOriginalObject(original);
        event.getSession().setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
        dispatcher.postClone(event);
        event.getSession().removeProperty(EntityAgentProperties.INTERNAL_READ);
        ReadObjectQuery query = new ReadObjectQuery(EntityValue.class);
        query.setProperty(EntityAgentProperties.INTERNAL_READ, Boolean.TRUE);
        event.setQuery(query);
        dispatcher.postClone(event);
        query.removeProperty(EntityAgentProperties.INTERNAL_READ);
        event.getSession().setProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE, original);
        dispatcher.postClone(event);
        event.getSession().removeProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE);
        dispatcher.postRefresh(event);
        assertTrue(calls.isEmpty());
        dispatcher.postClone(event);
        query.refreshIdentityMapResult();
        dispatcher.postRefresh(event);
        assertEquals(List.of(entity, entity), calls);
    }

    @Test
    void aggregateDescriptorsNeitherActivateNorDeliverThroughAnExistingNativeEvent() {
        for (boolean collection : List.of(false, true)) {
            EntityListenerRegistry registry = new EntityListenerRegistry();
            DatabaseSessionImpl session = session(EntityValue.class);
            ClassDescriptor descriptor = session.getDescriptor(EntityValue.class);
            if (collection) {
                descriptor.descriptorIsAggregateCollection();
            } else {
                descriptor.descriptorIsAggregate();
            }
            registry.trackSession(session);
            AtomicInteger runtimeCalls = new AtomicInteger();
            AtomicInteger nativeCalls = new AtomicInteger();
            registry.register(Object.class, PostLoad.class, value -> runtimeCalls.incrementAndGet());
            assertFalse(descriptor.getEventManager().hasAnyEventListeners());
            descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
                @Override
                public void postClone(DescriptorEvent event) {
                    nativeCalls.incrementAndGet();
                }
            });
            assertTrue(descriptor.getEventManager().hasAnyEventListeners());
            session.setEntityListener(registry.createDispatcher(() -> false));
            DescriptorEvent event = event(new EntityValue());
            event.setSession(session);
            event.setEventCode(org.eclipse.persistence.descriptors.DescriptorEventManager.PostCloneEvent);
            descriptor.getEventManager().executeEvent(event);
            assertEquals(1, nativeCalls.get());
            assertEquals(0, runtimeCalls.get());
        }
    }

    @Test
    void mutationIsSharedAndRuntimeFailureStopsLaterConsumers() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        EntityValue entity = new EntityValue();
        IllegalArgumentException failure = new IllegalArgumentException("application callback");
        registry.register(EntityValue.class, PrePersist.class, value -> value.number = 7);
        registry.register(EntityValue.class, PrePersist.class, value -> {
            assertSame(entity, value);
            assertEquals(7, value.number);
            throw failure;
        });
        registry.register(EntityValue.class, PrePersist.class, value -> value.number = 99);
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        assertSame(failure, assertThrows(IllegalArgumentException.class, () -> dispatcher.prePersist(event(entity))));
        assertEquals(7, entity.number);
    }

    @Test
    void reentrantAddSelfCancelAndCancelOtherAffectOnlyLaterSnapshots() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        List<String> calls = new ArrayList<>();
        AtomicReference<EntityListenerRegistration> self = new AtomicReference<>();
        AtomicReference<EntityListenerRegistration> other = new AtomicReference<>();
        self.set(registry.register(Object.class, PrePersist.class, value -> {
            calls.add("first");
            self.get().cancel();
            other.get().cancel();
            registry.register(Object.class, PrePersist.class, next -> calls.add("added"));
        }));
        other.set(registry.register(Object.class, PrePersist.class, value -> calls.add("second")));
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        dispatcher.prePersist(event(new EntityValue()));
        assertEquals(List.of("first", "second"), calls);
        dispatcher.prePersist(event(new EntityValue()));
        assertEquals(List.of("first", "second", "added"), calls);
    }

    @Test
    void concurrentCancelAndRegisterDoNotBlockOnAnApplicationCallback() throws Exception {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        List<String> calls = new ArrayList<>();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean pause = new AtomicBoolean(true);
        registry.register(Object.class, PrePersist.class, value -> {
            calls.add("first");
            if (pause.getAndSet(false)) {
                entered.countDown();
                await(release);
            }
        });
        EntityListenerRegistration second = registry.register(Object.class, PrePersist.class, value -> calls.add("second"));
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            var dispatch = executor.submit(() -> dispatcher.prePersist(event(new EntityValue())));
            assertTrue(entered.await(10, TimeUnit.SECONDS));
            executor.submit(() -> {
                second.cancel();
                registry.register(Object.class, PrePersist.class, value -> calls.add("third"));
            }).get(10, TimeUnit.SECONDS);
            release.countDown();
            dispatch.get(10, TimeUnit.SECONDS);
            assertEquals(List.of("first", "second"), calls);
            dispatcher.prePersist(event(new EntityValue()));
            assertEquals(List.of("first", "second", "first", "third"), calls);
        } finally {
            release.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    @Test
    void closePreservesAnInFlightSnapshotButSuppressesSubsequentDelivery() throws Exception {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        registry.register(Object.class, PrePersist.class, value -> {
            entered.countDown();
            await(release);
            calls.incrementAndGet();
        });
        registry.register(Object.class, PrePersist.class, value -> calls.incrementAndGet());
        DescriptorEventListener dispatcher = registry.createDispatcher(() -> false);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            var dispatch = executor.submit(() -> dispatcher.prePersist(event(new EntityValue())));
            assertTrue(entered.await(10, TimeUnit.SECONDS));
            executor.submit(registry::close).get(10, TimeUnit.SECONDS);
            release.countDown();
            dispatch.get(10, TimeUnit.SECONDS);
            assertEquals(2, calls.get());
            dispatcher.prePersist(event(new EntityValue()));
            assertEquals(2, calls.get());
        } finally {
            release.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    @Test
    void registrationRacingCloseCannotSurviveClose() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            for (int attempt = 0; attempt < 32; attempt++) {
                EntityListenerRegistry registry = new EntityListenerRegistry();
                AtomicInteger calls = new AtomicInteger();
                CountDownLatch start = new CountDownLatch(1);
                var registration = executor.submit(() -> {
                    await(start);
                    try {
                        return registry.register(Object.class, PrePersist.class, value -> calls.incrementAndGet());
                    } catch (IllegalStateException closed) {
                        return null;
                    }
                });
                var close = executor.submit(() -> {
                    await(start);
                    registry.close();
                });
                start.countDown();
                EntityListenerRegistration token = registration.get(10, TimeUnit.SECONDS);
                close.get(10, TimeUnit.SECONDS);
                registry.createDispatcher(() -> false).prePersist(event(new EntityValue()));
                assertEquals(0, calls.get());
                if (token != null) {
                    token.cancel();
                }
                assertThrows(IllegalStateException.class,
                        () -> registry.register(Object.class, PrePersist.class, value -> calls.incrementAndGet()));
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    @Test
    void retiringOneDispatcherLeavesTheFactoryRegistryUsable() {
        EntityListenerRegistry registry = new EntityListenerRegistry();
        AtomicBoolean oldClosed = new AtomicBoolean();
        AtomicInteger calls = new AtomicInteger();
        DescriptorEventListener old = registry.createDispatcher(oldClosed::get);
        DescriptorEventListener current = registry.createDispatcher(() -> false);
        EntityListenerRegistration token = registry.register(Object.class, PrePersist.class, value -> calls.incrementAndGet());
        old.prePersist(event(new EntityValue()));
        oldClosed.set(true);
        old.prePersist(event(new EntityValue()));
        current.prePersist(event(new EntityValue()));
        assertEquals(2, calls.get());
        token.cancel();
        current.prePersist(event(new EntityValue()));
        assertEquals(2, calls.get());
    }

    private static DescriptorEvent event(Object entity) {
        DescriptorEvent event = new DescriptorEvent(entity);
        event.setSession(new DatabaseSessionImpl());
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(entity.getClass());
        event.setDescriptor(descriptor);
        return event;
    }

    private static DatabaseSessionImpl session(Class<?> type) {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(type);
        session.addDescriptor(descriptor);
        descriptor.getEventManager().initialize(session);
        return session;
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        }
    }

    private interface RootType { }
    private interface LeftType extends RootType { }
    private interface RightType extends RootType { }
    private static class BaseValue { }
    private static class EntityValue extends BaseValue implements LeftType, RightType {
        int number;
    }
}
