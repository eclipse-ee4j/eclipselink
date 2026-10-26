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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PostInsert;
import jakarta.persistence.PreInsert;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/** Independent types sharing one listener; invalid plain types are mapped only by explicit test XML. */
public final class AgentDeclaredListenerModels {
    private AgentDeclaredListenerModels() {
    }

    public interface CallbackRecord {
        List<String> events();
        String name();
    }

    @Entity(name = "AgentDeclaredAlpha")
    @Table(name = "EA_DECLARED_ALPHA")
    @EntityListeners(SharedListener.class)
    public static class Alpha implements CallbackRecord {
        @Id
        public int id;
        public String name;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Entity(name = "AgentDeclaredBeta")
    @Table(name = "EA_DECLARED_BETA")
    @EntityListeners(SharedListener.class)
    public static class Beta implements CallbackRecord {
        @Id
        public int id;
        public String name;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Entity(name = "AgentDeclaredXmlAlpha")
    @Table(name = "EA_DECLARED_ALPHA")
    public static class XmlAlpha implements CallbackRecord {
        @Id
        public int id;
        public String name;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Entity(name = "AgentDeclaredXmlBeta")
    @Table(name = "EA_DECLARED_BETA")
    public static class XmlBeta implements CallbackRecord {
        @Id
        public int id;
        public String name;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }

        @Override
        public String name() {
            return name;
        }
    }

    public static class SharedListener {
        @PreInsert
        public void before(Alpha entity) {
            entity.events.add("insert-Alpha");
            entity.name = "Alpha-written";
        }

        @PrePersist
        public void persist(Alpha entity) {
            entity.events.add("persist-Alpha");
        }

        @PreInsert
        public void before(Beta entity) {
            entity.events.add("insert-Beta");
            entity.name = "Beta-written";
        }

        @PrePersist
        public void persist(Beta entity) {
            entity.events.add("persist-Beta");
        }

        @PreInsert
        public void before(XmlAlpha entity) {
            entity.events.add("insert-XmlAlpha");
            entity.name = "XmlAlpha-written";
        }

        @PrePersist
        public void persist(XmlAlpha entity) {
            entity.events.add("persist-XmlAlpha");
        }

        @PreInsert
        public void before(XmlBeta entity) {
            entity.events.add("insert-XmlBeta");
            entity.name = "XmlBeta-written";
        }

        @PrePersist
        public void persist(XmlBeta entity) {
            entity.events.add("persist-XmlBeta");
        }

        @PostInsert
        public void after(Object entity) {
            ((CallbackRecord) entity).events().add("post-any");
        }
    }

    @EntityListeners(ZeroListener.class)
    public static class InvalidZero {
        public int id;
    }

    public static class ZeroListener {
        @PreInsert
        public void before() { }
    }

    @EntityListeners(TwoListener.class)
    public static class InvalidTwo {
        public int id;
    }

    public static class TwoListener {
        @PreInsert
        public void before(InvalidTwo entity, Object other) { }
    }

    @EntityListeners(StaticListener.class)
    public static class InvalidStatic {
        public int id;
    }

    public static class StaticListener {
        @PreInsert
        public static void before(XmlBeta unrelated) { }
    }

    @EntityListeners(FinalListener.class)
    public static class InvalidFinal {
        public int id;
    }

    public static class FinalListener {
        @PreInsert
        public final void before(XmlBeta unrelated) { }
    }

    @EntityListeners(ReturnListener.class)
    public static class InvalidReturn {
        public int id;
    }

    public static class ReturnListener {
        @PreInsert
        public int before(XmlBeta unrelated) { return 1; }
    }

    @EntityListeners(PrimitiveListener.class)
    public static class InvalidPrimitive {
        public int id;
    }

    public static class PrimitiveListener {
        @PreInsert
        public void before(int invalid) { }
    }

    @EntityListeners(DuplicateListener.class)
    public static class InvalidDuplicate {
        public int id;
    }

    public static class DuplicateListener {
        @PreInsert
        public void before(InvalidDuplicate entity) { }

        @PreInsert
        public void second(InvalidDuplicate entity) { }
    }

    public static class InvalidEntityArity {
        public int id;

        @PreInsert
        public void before(XmlBeta unrelated) {
        }
    }
    @EntityListeners(UnrelatedDuplicateListener.class)
    public static class InvalidUnrelatedDuplicate {
        public int id;
    }

    public static class UnrelatedDuplicateListener {
        @PreInsert
        public void before(XmlBeta unrelated) {
        }

        @PreInsert
        public void second(XmlBeta unrelated) {
        }
    }

}
