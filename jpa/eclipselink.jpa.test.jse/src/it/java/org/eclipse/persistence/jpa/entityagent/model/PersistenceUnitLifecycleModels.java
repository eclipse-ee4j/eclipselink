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
package org.eclipse.persistence.jpa.entityagent.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityListener;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PreClose;
import jakarta.persistence.PreInsert;
import jakarta.persistence.Table;

/** Owned rows and a per-persistence-unit trace for public lifecycle assertions. */
public final class PersistenceUnitLifecycleModels {
    private PersistenceUnitLifecycleModels() {
    }

    @Entity(name = "PersistenceUnitLifecycleRecord")
    @Table(name = "JPA_PUL_RECORD")
    public static class Record {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Column(name = "ROW_VALUE", length = 80)
        public String value;

        public Record() {
        }

        public Record(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Entity(name = "PersistenceUnitLifecycleOther")
    @Table(name = "JPA_PUL_OTHER")
    public static class Other {
        @Id
        @Column(name = "ROW_ID")
        public int id;
    }

    /** These classes are mapped only by the isolated cache-overlay XML fixture. */
    public static class XmlOverlayEntity {
        public int id;
        public String value;
    }

    public static class XmlOverlayBase {
        public int id;
    }

    public static class XmlOverlayChild extends XmlOverlayBase {
        public String value;
    }

    /** No EntityListener annotation: registration must come from the XML entity. */
    public static class XmlOverlayListener {
        @PostCreate
        public void created(EntityManagerFactory factory) {
            Trace.forFactory(factory).add("factory-create", factory, factory.isOpen(), this);
        }

        @PreClose
        public void closing(EntityManagerFactory factory) {
            Trace.forFactory(factory).add("factory-close", factory, factory.isOpen(), this);
        }

        @PostCreate
        public void created(EntityManager manager) {
            Trace.forFactory(manager.getEntityManagerFactory()).add("manager-create", manager, manager.isOpen(), this);
        }

        @PreClose
        public void closing(EntityManager manager) {
            Trace.forFactory(manager.getEntityManagerFactory()).add("manager-close", manager, manager.isOpen(), this);
        }

        @PostCreate
        public void created(EntityAgent agent) {
            Trace.forFactory(agent.getEntityManagerFactory()).add("agent-create", agent, agent.isOpen(), this);
        }

        @PreClose
        public void closing(EntityAgent agent) {
            Trace.forFactory(agent.getEntityManagerFactory()).add("agent-close", agent, agent.isOpen(), this);
        }
    }

    public record Event(String kind, Object target, boolean open, Object listener) {
    }

    public static final class Trace implements AutoCloseable {
        private static final Map<String, Trace> ACTIVE = new ConcurrentHashMap<>();
        private final String name;
        public final List<Event> events = new CopyOnWriteArrayList<>();
        public volatile Consumer<Event> action = event -> { };

        public Trace(String name) {
            this.name = name;
            if (ACTIVE.putIfAbsent(name, this) != null) {
                throw new IllegalStateException("Lifecycle trace already exists: " + name);
            }
        }

        public List<Event> events(Object target) {
            return events.stream().filter(event -> event.target() == target).toList();
        }

        public List<String> kinds(Object target) {
            return events(target).stream().map(Event::kind).toList();
        }

        public static Trace forFactory(EntityManagerFactory factory) {
            return ACTIVE.get(factory.getName());
        }

        public void add(String kind, Object target, boolean open, Object listener) {
            Event event = new Event(kind, target, open, listener);
            events.add(event);
            action.accept(event);
        }

        @Override
        public void close() {
            ACTIVE.remove(name, this);
        }
    }

    @EntityListener
    public static class Listener {
        private Trace trace;

        @PostCreate
        public void created(EntityManagerFactory factory) {
            trace = Trace.forFactory(factory);
            factory.getMetamodel();
            record("factory-create", factory, factory.isOpen());
        }

        @PreClose
        protected void closing(EntityManagerFactory factory) {
            factory.getMetamodel();
            record("factory-close", factory, factory.isOpen());
        }

        @PostCreate
        void created(EntityManager manager) {
            trace(manager);
            manager.getCriteriaBuilder();
            record("manager-create", manager, manager.isOpen());
        }

        @PreClose
        private void closing(EntityManager manager) {
            manager.getCriteriaBuilder();
            record("manager-close", manager, manager.isOpen());
        }

        @PostCreate
        protected void created(EntityAgent agent) {
            trace(agent);
            agent.getProperties();
            record("agent-create", agent, agent.isOpen());
        }

        @PreClose
        public void closing(EntityAgent agent) {
            agent.getProperties();
            record("agent-close", agent, agent.isOpen());
        }

        private void trace(EntityHandler handler) {
            if (trace == null) {
                trace = Trace.forFactory(handler.getEntityManagerFactory());
            }
        }

        private void record(String kind, Object target, boolean open) {
            if (trace != null) {
                trace.add(kind, target, open, this);
            }
        }

        public void xmlCreated(EntityManagerFactory factory) {
            trace = Trace.forFactory(factory);
            record("xml-factory-create", factory, factory.isOpen());
        }

        public void xmlCreated(EntityManager manager) {
            trace(manager);
            record("xml-manager-create", manager, manager.isOpen());
        }

        public void xmlCreated(EntityAgent agent) {
            trace(agent);
            record("xml-agent-create", agent, agent.isOpen());
        }

        public void xmlClosing(EntityManagerFactory factory) {
            record("xml-factory-close", factory, factory.isOpen());
        }

        public void xmlClosing(EntityManager manager) {
            record("xml-manager-close", manager, manager.isOpen());
        }

        public void xmlClosing(EntityAgent agent) {
            record("xml-agent-close", agent, agent.isOpen());
        }
    }

    @EntityListener
    public static class InheritedListener extends Listener {
    }

    @EntityListener
    public static class OverridingListener extends Listener {
        @Override
        protected void created(EntityAgent agent) {
            // A Java override suppresses the inherited annotation.
        }
    }

    @EntityListener
    public static class MixedListener extends Listener {
        @PreInsert
        public void inserting(Record record) {
            record.value = "mixed:" + record.value;
        }
    }

    @EntityListener
    public static class PeerListener extends Listener {
    }

    @EntityListener
    public static class DualListener {
        @PostCreate
        @PreClose
        public void changed(EntityAgent agent) {
            Trace trace = Trace.forFactory(agent.getEntityManagerFactory());
            if (trace != null) {
                trace.add("agent-both", agent, agent.isOpen(), this);
            }
        }
    }

    public static class GenericBase<T> {
        public void accept(T target) {
        }
    }

    @EntityListener
    public static class BridgeListener extends GenericBase<EntityManager> {
        @Override
        @PostCreate
        public void accept(EntityManager manager) {
            Trace trace = Trace.forFactory(manager.getEntityManagerFactory());
            if (trace != null) {
                trace.add("bridge-manager-create", manager, manager.isOpen(), this);
            }
        }
    }

    /** XML alone declares this class; an unrelated overload must never be selected. */
    public static class XmlListener {
        private Trace trace;

        public void created(EntityManagerFactory factory) {
            trace = Trace.forFactory(factory);
            if (trace != null) {
                trace.add("xml-factory-create", factory, factory.isOpen(), this);
            }
        }

        public void created(EntityManager manager) {
            if (trace != null) {
                trace.add("xml-manager-create", manager, manager.isOpen(), this);
            }
        }

        public void created(EntityAgent agent) {
            if (trace != null) {
                trace.add("xml-agent-create", agent, agent.isOpen(), this);
            }
        }

        public void created(String unrelated) {
            throw new AssertionError("Unrelated XML overload was selected");
        }

        public void closing(EntityManagerFactory factory) {
            if (trace != null) {
                trace.add("xml-factory-close", factory, factory.isOpen(), this);
            }
        }

        public void closing(EntityManager manager) {
            if (trace != null) {
                trace.add("xml-manager-close", manager, manager.isOpen(), this);
            }
        }

        public void closing(EntityAgent agent) {
            if (trace != null) {
                trace.add("xml-agent-close", agent, agent.isOpen(), this);
            }
        }
    }

    // Invalid listeners have no discoverable annotation. Only isolated XML selects them.
    public static class InvalidZero {
        @PostCreate
        public void callback() {
        }
    }

    public static class InvalidTwo {
        @PostCreate
        public void callback(EntityManager first, EntityManager second) {
        }
    }

    public static class InvalidType {
        @PostCreate
        public void callback(EntityHandler handler) {
        }
    }

    public static class InvalidReturn {
        @PostCreate
        public int callback(EntityManager manager) {
            return 0;
        }
    }

    public static class InvalidStatic {
        @PostCreate
        public static void callback(EntityManager manager) {
        }
    }

    public static class InvalidFinal {
        @PostCreate
        public final void callback(EntityManager manager) {
        }
    }

    public static class InvalidDuplicate {
        @PostCreate
        public void first(EntityManager manager) {
        }

        @PostCreate
        public void second(EntityManager manager) {
        }
    }
}
