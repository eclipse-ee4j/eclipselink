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
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.broker.SessionBroker;

/**
 * INTERNAL: Factory-lifetime programmatic listeners. Descriptors retain only an
 * activation flag; owned contexts provide the delegate-specific dispatcher.
 */
public final class EntityListenerRegistry {
    private static final Map<Class<? extends Annotation>, String> EVENTS = Map.ofEntries(
            Map.entry(PrePersist.class, EntityListener.PRE_PERSIST),
            Map.entry(PostPersist.class, EntityListener.POST_PERSIST),
            Map.entry(PreRemove.class, EntityListener.PRE_REMOVE),
            Map.entry(PostRemove.class, EntityListener.POST_REMOVE),
            Map.entry(PreMerge.class, EntityListener.PRE_MERGE),
            Map.entry(PostLoad.class, EntityListener.POST_LOAD),
            Map.entry(PreInsert.class, EntityListener.PRE_INSERT),
            Map.entry(PostInsert.class, EntityListener.POST_INSERT),
            Map.entry(PreUpdate.class, EntityListener.PRE_UPDATE_WITH_CHANGES),
            Map.entry(PostUpdate.class, EntityListener.POST_UPDATE),
            Map.entry(PreUpsert.class, EntityListener.PRE_UPSERT),
            Map.entry(PostUpsert.class, EntityListener.POST_UPSERT),
            Map.entry(PreDelete.class, EntityListener.PRE_DELETE),
            Map.entry(PostDelete.class, EntityListener.POST_DELETE));

    // Values must not retain their weak keys or a descriptor/session graph.
    private final Map<AbstractSession, Boolean> sessions = new WeakHashMap<>();
    private List<Registration<?>> registrations = List.of();
    private boolean closed;

    public synchronized <E> EntityListenerRegistration register(Class<E> entityType,
            Class<? extends Annotation> eventType, Consumer<? super E> consumer) {
        if (closed) {
            throw new IllegalStateException("Entity listener registry is closed");
        }
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(consumer, "consumer");
        String event = EVENTS.get(eventType);
        if (event == null) {
            throw new IllegalArgumentException("Unrecognized entity lifecycle event: " + eventType.getName());
        }
        Object identity = new Object();
        Registration<E> registration = new Registration<>(identity, entityType, event, consumer);
        List<Registration<?>> updated = new ArrayList<>(registrations);
        updated.add(registration);
        registrations = List.copyOf(updated);
        for (AbstractSession session : sessions.keySet()) {
            enableDescriptors(session, registration);
        }
        return new Cancellation(this, identity);
    }

    /** Track deployments and effective context descriptor clones without owning them. */
    public synchronized void trackSession(AbstractSession session) {
        Objects.requireNonNull(session, "session");
        if (closed) {
            return;
        }
        sessions.put(session, Boolean.TRUE);
        for (Registration<?> registration : registrations) {
            enableDescriptors(session, registration);
        }
        if (session instanceof SessionBroker broker) {
            for (AbstractSession member : broker.getSessionsByName().values()) {
                trackSession(member);
            }
        }
    }

    private static void enableDescriptors(AbstractSession session, Registration<?> registration) {
        session.enableProgrammaticEventListeners();
        for (ClassDescriptor descriptor : session.getDescriptors().values()) {
            Class<?> entityType = descriptor.getJavaClass();
            if (descriptor.isDescriptorTypeNormal() && entityType != null
                    && registration.entityType.isAssignableFrom(entityType)) {
                descriptor.getEventManager().enableProgrammaticEventListeners();
            }
        }
    }

    public DescriptorEventListener createDispatcher(BooleanSupplier delegateClosed) {
        return new Dispatcher(this, Objects.requireNonNull(delegateClosed, "delegateClosed"));
    }

    public synchronized void close() {
        closed = true;
        registrations = List.of();
        sessions.clear();
    }

    private synchronized List<Registration<?>> snapshot() {
        return registrations;
    }

    private synchronized void cancel(Object identity) {
        registrations = registrations.stream().filter(registration -> registration.identity != identity).toList();
    }

    private static final class Registration<E> {
        private final Object identity;
        private final Class<E> entityType;
        private final String event;
        private final Consumer<? super E> consumer;

        private Registration(Object identity, Class<E> entityType, String event, Consumer<? super E> consumer) {
            this.identity = identity;
            this.entityType = entityType;
            this.event = event;
            this.consumer = consumer;
        }

        private void notify(String event, Object entity) {
            if (this.event.equals(event) && entityType.isInstance(entity)) {
                consumer.accept(entityType.cast(entity));
            }
        }
    }

    /** A retained token owns neither its Consumer nor a cancelled registry. */
    private static final class Cancellation implements EntityListenerRegistration {
        private EntityListenerRegistry registry;
        private final Object identity;

        private Cancellation(EntityListenerRegistry registry, Object identity) {
            this.registry = registry;
            this.identity = identity;
        }

        @Override
        public synchronized void cancel() {
            if (registry != null) {
                registry.cancel(identity);
                registry = null;
            }
        }
    }

    /** Inherit the existing PostClone/PostRefresh internal-read and target predicates. */
    private static final class Dispatcher extends EntityListener<Object> {
        private final EntityListenerRegistry registry;
        private final BooleanSupplier delegateClosed;

        private Dispatcher(EntityListenerRegistry registry, BooleanSupplier delegateClosed) {
            super(Object.class);
            this.registry = registry;
            this.delegateClosed = delegateClosed;
        }

        @Override
        void invokeMethod(String event, DescriptorEvent descriptorEvent) {
            if (delegateClosed.getAsBoolean() || !descriptorEvent.getDescriptor().isDescriptorTypeNormal()) {
                return;
            }
            String callback = POST_CLONE.equals(event) || POST_REFRESH.equals(event) ? POST_LOAD : event;
            // The selected snapshot intentionally survives cancellation/close during delivery.
            // No registry lock is held while invoking application code.
            for (Registration<?> registration : registry.snapshot()) {
                registration.notify(callback, descriptorEvent.getSource());
            }
        }
    }
}
