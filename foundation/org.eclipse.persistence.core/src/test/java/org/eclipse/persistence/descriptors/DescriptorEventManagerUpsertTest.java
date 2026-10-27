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
package org.eclipse.persistence.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescriptorEventManagerUpsertTest {
    static Stream<Arguments> physicalEvents() {
        return Stream.of(
                Arguments.of(DescriptorEventManager.PreInsertEvent, DescriptorEventManager.PreUpsertEvent),
                Arguments.of(DescriptorEventManager.PreUpdateWithChangesEvent, DescriptorEventManager.PreUpsertEvent),
                Arguments.of(DescriptorEventManager.PostInsertEvent, DescriptorEventManager.PostUpsertEvent),
                Arguments.of(DescriptorEventManager.PostUpdateEvent, DescriptorEventManager.PostUpsertEvent));
    }

    @ParameterizedTest
    @MethodSource("physicalEvents")
    void nativeEntityReceivesPhysicalEventAndJpaAndValidationReceiveUpsert(int physical, int logical) {
        List<String> calls = new ArrayList<>();
        RecordingListener entity = new RecordingListener("entity", calls);
        DescriptorEventManager manager = manager(entity.getClass(), calls);
        DescriptorEvent event = event(entity, physical, "UPSERT");

        manager.executeEvent(event);

        assertEquals(List.of("native:" + physical, "jpa:" + logical,
                "validation:" + logical, "entity:" + physical), calls);
        assertEquals(physical, event.getEventCode());
    }

    @ParameterizedTest
    @MethodSource("physicalEvents")
    void descriptorSelectorReceivesPhysicalEventAfterJpaTranslation(int physical, int logical) {
        List<String> calls = new ArrayList<>();
        SelectorEntity entity = new SelectorEntity(calls);
        DescriptorEventManager manager = manager(SelectorEntity.class, calls);
        manager.getEventSelectors().set(physical, "physicalEvent");
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent event = event(entity, physical, "UPSERT");

        manager.executeEvent(event);

        assertEquals(List.of("native:" + physical, "jpa:" + logical,
                "validation:" + logical, "selector:" + physical), calls);
        assertEquals(physical, event.getEventCode());
    }

    @Test
    void callbackFailureRestoresEventAndPreventsLaterCallbacks() {
        List<String> calls = new ArrayList<>();
        SelectorEntity entity = new SelectorEntity(calls);
        DescriptorEventManager manager = manager(SelectorEntity.class, calls);
        IllegalStateException failure = new IllegalStateException("callback failure");
        manager.setEntityEventListener(new DescriptorEventAdapter() {
            @Override
            public void preUpsert(DescriptorEvent event) {
                assertEquals(DescriptorEventManager.PreUpsertEvent, event.getEventCode());
                throw failure;
            }
        });
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent event = event(entity, DescriptorEventManager.PreInsertEvent, "UPSERT");

        assertSame(failure, assertThrows(IllegalStateException.class, () -> manager.executeEvent(event)));
        assertEquals(List.of("native:" + DescriptorEventManager.PreInsertEvent), calls);
        assertEquals(DescriptorEventManager.PreInsertEvent, event.getEventCode());
    }

    @Test
    void nestedOrdinaryEventDoesNotInheritTheOuterTranslatedCode() {
        List<String> calls = new ArrayList<>();
        RecordingListener entity = new RecordingListener("entity", calls);
        DescriptorEventManager manager = manager(entity.getClass(), calls);
        DescriptorEvent nested = event(entity, DescriptorEventManager.PreInsertEvent, "INSERT");
        manager.setEntityEventListener(new RecordingListener("jpa", calls) {
            @Override
            public void preUpsert(DescriptorEvent event) {
                super.preUpsert(event);
                manager.executeEvent(nested);
                assertEquals(DescriptorEventManager.PreUpsertEvent, event.getEventCode());
                assertEquals(DescriptorEventManager.PreInsertEvent, nested.getEventCode());
            }
        });
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent outer = event(entity, DescriptorEventManager.PreInsertEvent, "UPSERT");

        manager.executeEvent(outer);

        int insert = DescriptorEventManager.PreInsertEvent;
        int upsert = DescriptorEventManager.PreUpsertEvent;
        assertEquals(List.of("native:" + insert, "jpa:" + upsert,
                "native:" + insert, "jpa:" + insert, "validation:" + insert, "entity:" + insert,
                "validation:" + upsert, "entity:" + insert), calls);
        assertEquals(insert, outer.getEventCode());
    }

    @Test
    void lockWriteRetainsNativeEventsAndSuppressesJpaAndValidation() {
        List<String> calls = new ArrayList<>();
        RecordingListener entity = new RecordingListener("entity", calls);
        DescriptorEventManager manager = manager(entity.getClass(), calls);
        DescriptorEvent event = event(entity, DescriptorEventManager.PreUpdateWithChangesEvent, "LOCK");

        manager.executeEvent(event);

        int physical = DescriptorEventManager.PreUpdateWithChangesEvent;
        assertEquals(List.of("native:" + physical, "entity:" + physical), calls);
        assertEquals(physical, event.getEventCode());
    }

    private static DescriptorEventManager manager(Class<?> entityType, List<String> calls) {
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(entityType);
        DescriptorEventManager manager = descriptor.getEventManager();
        manager.addListener(new RecordingListener("native", calls));
        manager.setEntityEventListener(new RecordingListener("jpa", calls));
        manager.addInternalListener(new RecordingListener("validation", calls));
        manager.initialize(new DatabaseSessionImpl());
        return manager;
    }

    private static DescriptorEvent event(Object entity, int code, String operation) {
        InsertObjectQuery query = new InsertObjectQuery(entity);
        query.setProperty(EntityAgentProperties.OPERATION, operation);
        DescriptorEvent event = new DescriptorEvent(entity);
        event.setEventCode(code);
        event.setQuery(query);
        event.setSession(new DatabaseSessionImpl());
        return event;
    }

    public static class SelectorEntity {
        private final List<String> calls;

        SelectorEntity(List<String> calls) {
            this.calls = calls;
        }

        public void physicalEvent(DescriptorEvent event) {
            calls.add("selector:" + event.getEventCode());
        }
    }

    private static class RecordingListener extends DescriptorEventAdapter {
        private final String name;
        private final List<String> calls;

        RecordingListener(String name, List<String> calls) {
            this.name = name;
            this.calls = calls;
        }

        private void record(DescriptorEvent event) {
            calls.add(name + ":" + event.getEventCode());
        }

        @Override
        public void preInsert(DescriptorEvent event) { record(event); }
        @Override
        public void postInsert(DescriptorEvent event) { record(event); }
        @Override
        public void preUpdateWithChanges(DescriptorEvent event) { record(event); }
        @Override
        public void postUpdate(DescriptorEvent event) { record(event); }
        @Override
        public void preUpsert(DescriptorEvent event) { record(event); }
        @Override
        public void postUpsert(DescriptorEvent event) { record(event); }
    }
}
