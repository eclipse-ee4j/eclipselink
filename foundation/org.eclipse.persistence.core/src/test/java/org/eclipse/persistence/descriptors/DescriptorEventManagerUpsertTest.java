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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.ExpressionQueryMechanism;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl.RefreshOperation;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        event.getSession().setEntityListener(new RecordingListener("runtime", calls));

        manager.executeEvent(event);

        int physical = DescriptorEventManager.PreUpdateWithChangesEvent;
        assertEquals(List.of("native:" + physical, "entity:" + physical), calls);
        assertEquals(physical, event.getEventCode());
    }

    @Test
    void runtimeListenersFollowMetadataAndPrecedeEntityMethodsAndValidation() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        manager.addDefaultEventListener(new RecordingListener("default", calls));
        manager.addEntityListenerEventListener(new RecordingListener("metadata", calls));
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent event = event(new Object(), DescriptorEventManager.PreInsertEvent, "INSERT");
        event.getSession().setEntityListener(new RecordingListener("runtime", calls));

        manager.executeEvent(event);

        int code = DescriptorEventManager.PreInsertEvent;
        assertEquals(List.of("native:" + code, "default:" + code, "metadata:" + code,
                "runtime:" + code, "jpa:" + code, "validation:" + code), calls);
    }

    @ParameterizedTest
    @MethodSource("physicalEvents")
    void runtimeListenersReceiveOnlyLogicalUpsertEvents(int physical, int logical) {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        DescriptorEvent event = event(new Object(), physical, "UPSERT");
        event.getSession().setEntityListener(new RecordingListener("runtime", calls));

        manager.executeEvent(event);

        assertEquals(List.of("native:" + physical, "runtime:" + logical,
                "jpa:" + logical, "validation:" + logical), calls);
        assertEquals(physical, event.getEventCode());
    }

    @Test
    void runtimeErrorRestoresPhysicalEventAndAbortsEntityValidationAndSelector() {
        List<String> calls = new ArrayList<>();
        SelectorEntity entity = new SelectorEntity(calls);
        DescriptorEventManager manager = manager(SelectorEntity.class, calls);
        manager.getEventSelectors().set(DescriptorEventManager.PreInsertEvent, "physicalEvent");
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent event = event(entity, DescriptorEventManager.PreInsertEvent, "UPSERT");
        AssertionError failure = new AssertionError("callback error");
        event.getSession().setEntityListener(new DescriptorEventAdapter() {
            @Override
            public void preUpsert(DescriptorEvent translated) {
                assertEquals(DescriptorEventManager.PreUpsertEvent, translated.getEventCode());
                throw failure;
            }
        });

        assertSame(failure, assertThrows(AssertionError.class, () -> manager.executeEvent(event)));
        assertEquals(List.of("native:" + DescriptorEventManager.PreInsertEvent), calls);
        assertEquals(DescriptorEventManager.PreInsertEvent, event.getEventCode());
    }

    @Test
    void runtimeNestedEventRestoresBothLogicalAndPhysicalCodes() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        DescriptorEvent nested = event(new Object(), DescriptorEventManager.PreInsertEvent, "INSERT");
        DescriptorEvent outer = event(new Object(), DescriptorEventManager.PreInsertEvent, "UPSERT");
        outer.getSession().setEntityListener(new RecordingListener("runtime", calls) {
            @Override
            public void preUpsert(DescriptorEvent event) {
                super.preUpsert(event);
                manager.executeEvent(nested);
                assertEquals(DescriptorEventManager.PreUpsertEvent, event.getEventCode());
            }
        });
        nested.getSession().setEntityListener(new RecordingListener("nested", calls));

        manager.executeEvent(outer);

        int insert = DescriptorEventManager.PreInsertEvent;
        int upsert = DescriptorEventManager.PreUpsertEvent;
        assertEquals(List.of("native:" + insert, "runtime:" + upsert, "native:" + insert,
                "nested:" + insert, "jpa:" + insert, "validation:" + insert,
                "jpa:" + upsert, "validation:" + upsert), calls);
        assertEquals(insert, outer.getEventCode());
        assertEquals(insert, nested.getEventCode());
    }

    @Test
    void managedPostInsertAndDeleteAlsoDeliverDistinctManagedEvents() {
        List<Integer> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, new ArrayList<>());
        UnitOfWorkImpl context = new UnitOfWorkImpl();
        context.setEntityListener(new DescriptorEventAdapter() {
            @Override
            public void postInsert(DescriptorEvent event) { calls.add(event.getEventCode()); }
            @Override
            public void postPersist(DescriptorEvent event) { calls.add(event.getEventCode()); }
            @Override
            public void postDelete(DescriptorEvent event) { calls.add(event.getEventCode()); }
            @Override
            public void postRemove(DescriptorEvent event) { calls.add(event.getEventCode()); }
        });
        DescriptorEvent event = event(new Object(), DescriptorEventManager.PostInsertEvent, "INSERT");
        event.setSession(context);
        manager.executeEvent(event);
        event.setEventCode(DescriptorEventManager.PostDeleteEvent);
        manager.executeEvent(event);

        assertEquals(List.of(DescriptorEventManager.PostInsertEvent, DescriptorEventManager.PostPersistEvent,
                DescriptorEventManager.PostDeleteEvent, DescriptorEventManager.PostRemoveEvent), calls);
        assertEquals(DescriptorEventManager.PostDeleteEvent, event.getEventCode());
    }

    @Test
    void activationSurvivesCloneAndInitializationWithoutAddingNativeListeners() {
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(Object.class);
        DescriptorEventManager manager = descriptor.getEventManager();
        manager.initialize(new DatabaseSessionImpl());
        assertFalse(manager.hasAnyEventListeners());
        manager.enableProgrammaticEventListeners();
        DescriptorEventManager clone = (DescriptorEventManager) manager.clone();
        clone.initialize(new DatabaseSessionImpl());
        assertTrue(manager.hasAnyEventListeners());
        assertTrue(clone.hasAnyEventListeners());
        assertTrue(manager.getEventListeners().isEmpty());
        assertNull(manager.getEntityEventListener());
    }

    static Stream<Arguments> concreteQueryPreparations() {
        return Stream.of(false, true).flatMap(readAll -> Stream.of("cold", "warm", "unprepared")
                .map(branch -> Arguments.of(readAll, branch)));
    }

    @ParameterizedTest
    @MethodSource("concreteQueryPreparations")
    void concreteExecutionInheritsOnlyActiveSourceWithoutAuthorizingTemplatesOrCopies(boolean readAll, String branch) {
        RefreshFixture fixture = new RefreshFixture();
        InterfacePolicy policy = new InterfacePolicy();
        policy.setDescriptor(fixture.descriptor);
        ObjectLevelReadQuery query = readAll ? new ReadAllQuery(RefreshEntity.class) : new ReadObjectQuery(RefreshEntity.class);
        query.setDescriptor(fixture.descriptor);
        query.setSession(fixture.parent);
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(7));
        DatabaseRecord row = new DatabaseRecord();
        query.setTranslationRow(row);
        query.setShouldPrepare(!"unprepared".equals(branch));
        ObjectBuildingQuery template = null;
        if ("warm".equals(branch)) {
            ObjectLevelReadQuery ordinary = policy.prepareQuery(query);
            template = (ObjectBuildingQuery) query.getConcreteSubclassQueries().get(RefreshEntity.class);
            assertFalse(ordinary.isRefreshSource());
            assertNotSame(template, ordinary);
        } else {
            assertTrue(query.getConcreteSubclassQueries().isEmpty());
        }

        ObjectLevelReadQuery execution;
        try (ObjectBuildingQuery.RefreshSourceScope ignored = query.beginRefreshSource()) {
            execution = policy.prepareQuery(query);
            assertTrue(query.isRefreshSource());
            assertTrue(execution.isRefreshSource());
            assertNotSame(query, execution);
            assertSame(fixture.descriptor, execution.getDescriptor());
            assertSame(row, execution.getTranslationRow());
            assertTrue(execution.isExecutionClone());
            assertFalse(((ObjectBuildingQuery) execution.clone()).isRefreshSource());
            assertFalse(((ObjectBuildingQuery) query.deepClone()).isRefreshSource());
            if ("unprepared".equals(branch)) {
                assertTrue(query.getConcreteSubclassQueries().isEmpty());
            } else {
                ObjectBuildingQuery cached = (ObjectBuildingQuery) query.getConcreteSubclassQueries().get(RefreshEntity.class);
                assertNotSame(execution, cached);
                assertFalse(cached.isRefreshSource());
                if (template != null) {
                    assertSame(template, cached);
                }
                template = cached;
            }
        }
        assertFalse(query.isRefreshSource());
        assertFalse(execution.isRefreshSource());
        assertFalse(policy.prepareQuery(query).isRefreshSource());
        try (ObjectBuildingQuery.RefreshSourceScope ignored = query.beginRefreshSource()) {
            assertTrue(policy.prepareQuery(query).isRefreshSource());
            assertFalse(execution.isRefreshSource(), "A later scope cannot revive a retained execution query");
        }
        if (template != null) {
            assertSame(template, query.getConcreteSubclassQueries().get(RefreshEntity.class));
            assertFalse(template.isRefreshSource());
        }
    }

    static Stream<String> descriptorAdditionApis() {
        return Stream.of("descriptor", "collection", "project");
    }

    static Stream<Arguments> descriptorInstallations() {
        return descriptorAdditionApis().flatMap(api -> Stream.of(false, true).map(marked -> Arguments.of(api, marked)));
    }

    @ParameterizedTest
    @MethodSource("descriptorInstallations")
    void descriptorInstallationPreservesNativeBoundaryAndActivationThroughInitialization(String api, boolean marked) {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        assertFalse(session.hasProgrammaticEventListeners());
        if (marked) {
            session.enableProgrammaticEventListeners();
        }
        ClassDescriptor descriptor = installDescriptor();
        assertFalse(descriptor.getEventManager().hasAnyEventListeners());
        addThrough(session, api, descriptor);
        assertFalse(session.isConnected());
        assertSame(descriptor, session.getDescriptor(RefreshEntity.class));
        assertEquals(marked, descriptor.getEventManager().hasAnyEventListeners());
        session.initializeDescriptors();
        assertEquals(marked, descriptor.getEventManager().hasAnyEventListeners());
        assertTrue(descriptor.getEventManager().getEventListeners().isEmpty());
        assertNull(descriptor.getEventManager().getEntityEventListener());
        assertNull(session.getEntityListener(), "Activation must not create a factory owner on a native session");

        ClassDescriptor aggregate = new RelationalDescriptor();
        aggregate.setJavaClass(Object.class);
        aggregate.descriptorIsAggregate();
        addThrough(session, api, aggregate);
        assertSame(aggregate, session.getDescriptor(Object.class));
        assertFalse(aggregate.getEventManager().hasAnyEventListeners());
    }

    @ParameterizedTest
    @MethodSource("descriptorAdditionApis")
    void descriptorInstallationObservesActivationPublishedInsideProjectAddition(String api) {
        ActivatingProject project = new ActivatingProject();
        DatabaseSessionImpl session = new DatabaseSessionImpl(project);
        ClassDescriptor descriptor = installDescriptor();
        assertFalse(session.hasProgrammaticEventListeners());
        addThrough(session, api, descriptor);
        assertEquals(1, project.publications);
        assertSame(descriptor, session.getDescriptor(RefreshEntity.class));
        assertTrue(session.hasProgrammaticEventListeners());
        assertTrue(descriptor.getEventManager().hasAnyEventListeners(),
                "Checking the marker only before Project publication loses this registration ordering");
        assertNull(session.getEntityListener());
    }

    private static ClassDescriptor installDescriptor() {
        ClassDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(RefreshEntity.class);
        descriptor.addTableName("LISTENER_INSTALL_UNIT");
        descriptor.addPrimaryKeyFieldName("ID");
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("value", "ENTRY_VALUE");
        return descriptor;
    }

    private static void addThrough(DatabaseSessionImpl session, String api, ClassDescriptor descriptor) {
        switch (api) {
            case "descriptor" -> session.addDescriptor(descriptor);
            case "collection" -> session.addDescriptors(List.of(descriptor));
            case "project" -> {
                Project project = new Project(new DatabaseLogin());
                project.addDescriptor(descriptor);
                session.addDescriptors(project);
            }
            default -> throw new AssertionError(api);
        }
    }

    private static final class ActivatingProject extends Project {
        private int publications;

        ActivatingProject() {
            super(new DatabaseLogin());
        }

        private void publishRegistration(DatabaseSessionImpl session) {
            assertFalse(session.hasProgrammaticEventListeners());
            assertTrue(session.getDescriptors().isEmpty());
            session.enableProgrammaticEventListeners();
            publications++;
        }

        @Override
        public void addDescriptor(ClassDescriptor descriptor, DatabaseSessionImpl session) {
            publishRegistration(session);
            super.addDescriptor(descriptor, session);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void addDescriptors(Collection descriptors, DatabaseSessionImpl session) {
            publishRegistration(session);
            super.addDescriptors(descriptors, session);
        }
    }

    @Test
    void clientAndCompositeUnitOfWorkUseOnlyTheirOwnedContextListener() {
        ServerSession shared = new ServerSession(new DatabaseLogin());
        ClientSession member = new ClientSession(shared, shared.getDefaultConnectionPolicy());
        ClientSession peer = new ClientSession(shared, shared.getDefaultConnectionPolicy());
        SessionBroker broker = new SessionBroker();
        broker.getSessionsByName().put("member", member);
        DescriptorEventListener owner = new DescriptorEventAdapter();
        DescriptorEventListener other = new DescriptorEventAdapter();
        broker.setEntityListener(owner);
        member.setEntityListener(owner);
        peer.setEntityListener(other);

        UnitOfWorkImpl brokerContext = new UnitOfWorkImpl(broker, ReferenceMode.HARD);
        UnitOfWorkImpl memberContext = new UnitOfWorkImpl(member, ReferenceMode.HARD);
        UnitOfWorkImpl peerContext = new UnitOfWorkImpl(peer, ReferenceMode.HARD);
        assertSame(owner, brokerContext.getEntityListener());
        assertSame(owner, memberContext.getEntityListener());
        assertSame(other, peerContext.getEntityListener());
        assertNull(shared.getEntityListener());
        assertNull(new ClientSession(shared, shared.getDefaultConnectionPolicy()).getEntityListener());
        memberContext.setEntityListener(other);
        assertSame(other, memberContext.getEntityListener());
        assertSame(owner, member.getEntityListener());
        assertFalse(member.getProperties().containsValue(owner));
    }

    @Test
    void refreshSourceRetainsNativeSelectorAndDefersOrderedJpaPhaseOnTarget() {
        List<String> calls = new ArrayList<>();
        SelectorEntity source = new SelectorEntity(calls);
        SelectorEntity target = new SelectorEntity(calls);
        DescriptorEventManager manager = manager(SelectorEntity.class, calls);
        manager.getEventSelectors().set(DescriptorEventManager.PostRefreshEvent, "physicalEvent");
        manager.addEntityListenerEventListener(new RecordingListener("metadata", calls));
        manager.initialize(new DatabaseSessionImpl());
        DescriptorEvent sourceEvent = event(source, DescriptorEventManager.PostRefreshEvent, "REFRESH");
        sourceEvent.getSession().setEntityListener(new RecordingListener("runtime-source", calls));
        manager.executeRefreshSourceEvent(sourceEvent);
        int code = DescriptorEventManager.PostRefreshEvent;
        assertEquals(List.of("native:" + code, "selector:" + code), calls);
        AbstractSession context = new DatabaseSessionImpl();
        context.setEntityListener(new RecordingListener("runtime", calls) {
            @Override
            public void postRefresh(DescriptorEvent event) {
                assertSame(target, event.getSource());
                assertSame(source, event.getOriginalObject());
                assertSame(manager.getDescriptor(), event.getDescriptor());
                super.postRefresh(event);
            }
        });
        DescriptorEvent targetEvent = refreshEvent(target, source, new ReadObjectQuery(), context, manager.getDescriptor(), code);
        assertEquals(2, calls.size());
        manager.executeEntityRefreshEvent(targetEvent);
        assertEquals(List.of("native:" + code, "selector:" + code, "metadata:" + code,
                "runtime:" + code, "jpa:" + code, "validation:" + code), calls);
    }

    @Test
    void refreshSourceRetainsNativeEntityInterfaceAndRestoresProfilingOnFailure() {
        List<String> calls = new ArrayList<>();
        RecordingListener source = new RecordingListener("native-entity", calls);
        DescriptorEventManager manager = manager(source.getClass(), calls);
        DescriptorEvent event = event(source, DescriptorEventManager.PostRefreshEvent, "REFRESH");
        ProfileSession context = new ProfileSession();
        event.setSession(context);
        manager.executeRefreshSourceEvent(event);
        int code = DescriptorEventManager.PostRefreshEvent;
        assertEquals(List.of("native:" + code, "native-entity:" + code), calls);
        assertEquals(1, context.starts);
        assertEquals(1, context.ends);
        AssertionError failure = new AssertionError("native failure");
        manager.addListener(new DescriptorEventAdapter() {
            @Override
            public void postRefresh(DescriptorEvent notified) {
                notified.setEventCode(DescriptorEventManager.PostMergeEvent);
                throw failure;
            }
        });
        assertSame(failure, assertThrows(AssertionError.class, () -> manager.executeRefreshSourceEvent(event)));
        assertEquals(code, event.getEventCode());
        assertEquals(2, context.starts);
        assertEquals(2, context.ends);
    }

    @Test
    void refreshJpaFailureAbortsEntityValidationAndPreservesProfiling() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        ProfileSession context = new ProfileSession();
        AssertionError failure = new AssertionError("runtime refresh failure");
        context.setEntityListener(new DescriptorEventAdapter() {
            @Override
            public void postRefresh(DescriptorEvent event) {
                calls.add("runtime");
                throw failure;
            }
        });
        DescriptorEvent event = refreshEvent(new Object(), new Object(), new ReadObjectQuery(), context,
                manager.getDescriptor(), DescriptorEventManager.PostRefreshEvent);
        assertSame(failure, assertThrows(AssertionError.class, () -> manager.executeEntityRefreshEvent(event)));
        assertEquals(List.of("runtime"), calls);
        assertEquals(1, context.starts);
        assertEquals(1, context.ends);
    }

    @Test
    void refreshSourceAuthorizationIsExactQueryAndRestoresPreviousScopeAfterReentrancy() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        RefreshBuilder builder = new RefreshBuilder(manager.getDescriptor());
        ReadObjectQuery query = builder.query();
        Object previous = new Object();
        query.setProperty(RefreshBuilder.marker(), previous);
        builder.onBuild = current -> {
            assertTrue(current.isRefreshSource());
            assertSame(previous, current.getProperty(RefreshBuilder.marker()));
            ReadObjectQuery copied = (ReadObjectQuery) current.clone();
            copied.setSession(current.getSession());
            assertFalse(copied.isRefreshSource());
            builder.emit(copied);
            builder.emit(current);
        };
        builder.source(query);
        int code = DescriptorEventManager.PostRefreshEvent;
        assertEquals(List.of("native:" + code, "jpa:" + code, "validation:" + code, "native:" + code), calls);
        assertSame(previous, query.getProperty(RefreshBuilder.marker()));
        assertFalse(query.isRefreshSource());

        int[] depth = {0};
        builder.onBuild = current -> {
            if (depth[0]++ == 0) {
                builder.source(current);
                assertTrue(current.isRefreshSource());
                assertSame(previous, current.getProperty(RefreshBuilder.marker()));
            }
            builder.emit(current);
        };
        builder.source(query);
        assertSame(previous, query.getProperty(RefreshBuilder.marker()));
        assertFalse(query.isRefreshSource());
    }

    @Test
    void explicitRelationshipSourceAuthorizationExpiresWithoutAuthorizingCopies() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        RefreshBuilder builder = new RefreshBuilder(manager.getDescriptor());
        ReadObjectQuery root = builder.query();
        ReadObjectQuery child = builder.query();
        ReadObjectQuery retained = builder.query();
        root.setProperty(RefreshBuilder.marker(), root);
        assertFalse(root.isRefreshSource(), "A legacy property value grants no source authorization");
        builder.onBuild = current -> {
            child.inheritRefreshSource(current);
            assertTrue(child.isRefreshSource());
            ReadObjectQuery copied = (ReadObjectQuery) child.clone();
            copied.setSession(child.getSession());
            assertFalse(copied.isRefreshSource());
            builder.emit(child);
            builder.emit(copied);
            retained.inheritRefreshSource(child);
            assertTrue(retained.isRefreshSource());
        };
        builder.source(root);
        int code = DescriptorEventManager.PostRefreshEvent;
        assertEquals(List.of("native:" + code, "native:" + code, "jpa:" + code,
                "validation:" + code), calls);
        assertFalse(root.isRefreshSource());
        assertFalse(child.isRefreshSource());
        assertFalse(retained.isRefreshSource());
        assertSame(root, root.getProperty(RefreshBuilder.marker()));
        builder.emit(retained);
        assertEquals(List.of("native:" + code, "native:" + code, "jpa:" + code,
                "validation:" + code, "native:" + code, "jpa:" + code,
                "validation:" + code), calls);
    }

    @Test
    void nestedSourceConstructionRestoresOuterAuthorizationAndExpiresOnlyItsOwnChildren() {
        DescriptorEventManager manager = manager(Object.class, new ArrayList<>());
        RefreshBuilder builder = new RefreshBuilder(manager.getDescriptor());
        ReadObjectQuery root = builder.query();
        ReadObjectQuery outerChild = builder.query();
        ReadObjectQuery innerChild = builder.query();
        int[] depth = {0};
        builder.onBuild = current -> {
            if (depth[0]++ == 0) {
                outerChild.inheritRefreshSource(current);
                builder.source(current);
                assertTrue(current.isRefreshSource());
                assertTrue(outerChild.isRefreshSource());
                assertFalse(innerChild.isRefreshSource());
            } else {
                innerChild.inheritRefreshSource(current);
                assertTrue(innerChild.isRefreshSource());
                assertTrue(outerChild.isRefreshSource());
            }
        };
        builder.source(root);
        assertFalse(root.isRefreshSource());
        assertFalse(outerChild.isRefreshSource());
        assertFalse(innerChild.isRefreshSource());
    }

    @Test
    void refreshSourceFailureRestoresAbsentAndExistingMarkerStates() {
        DescriptorEventManager manager = manager(Object.class, new ArrayList<>());
        RefreshBuilder builder = new RefreshBuilder(manager.getDescriptor());
        IllegalStateException failure = new IllegalStateException("source callback failure");
        manager.addListener(new DescriptorEventAdapter() {
            @Override
            public void postRefresh(DescriptorEvent event) { throw failure; }
        });
        ReadObjectQuery query = builder.query();
        assertSame(failure, assertThrows(IllegalStateException.class, () -> builder.source(query)));
        assertFalse(query.isRefreshSource());
        assertFalse(query.getProperties().containsKey(RefreshBuilder.marker()));
        Object previous = new Object();
        query.setProperty(RefreshBuilder.marker(), previous);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> builder.source(query)));
        assertFalse(query.isRefreshSource());
        assertSame(previous, query.getProperty(RefreshBuilder.marker()));
        query.setProperty(RefreshBuilder.marker(), null);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> builder.source(query)));
        assertFalse(query.isRefreshSource());
        assertTrue(query.getProperties().containsKey(RefreshBuilder.marker()));
        assertNull(query.getProperty(RefreshBuilder.marker()));
    }

    @Test
    void readOnlyAndNonregisteringRefreshRetainSourceJpaCallbacks() {
        for (boolean readOnly : List.of(false, true)) {
            List<String> calls = new ArrayList<>();
            DescriptorEventManager manager = manager(Object.class, calls);
            ClassDescriptor descriptor = manager.getDescriptor();
            descriptor.useFullIdentityMap();
            descriptor.setIdentityMapSize(10);
            DatabaseSessionImpl parent = new DatabaseSessionImpl(new DatabaseLogin());
            parent.addDescriptor(descriptor);
            UnitOfWorkImpl context = new UnitOfWorkImpl(parent, ReferenceMode.HARD);
            if (readOnly) {
                context.addReadOnlyClass(Object.class);
            }
            RefreshBuilder builder = new RefreshBuilder(descriptor);
            ReadObjectQuery query = builder.query();
            query.setSession(context);
            query.setShouldRegisterResultsInUnitOfWork(readOnly);
            assertSame(builder.cachedSource, builder.normal(query, context));
            int code = DescriptorEventManager.PostRefreshEvent;
            assertEquals(List.of("native:" + code, "jpa:" + code, "validation:" + code), calls);
            assertFalse(query.getProperties().containsKey(RefreshBuilder.marker()));
            assertSame(context, query.getSession());
        }
    }

    @Test
    void refreshOperationDeliversOrderedJpaPhasesOnceAndDeduplicatesByIdentity() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        manager.addEntityListenerEventListener(new RecordingListener("metadata", calls));
        manager.initialize(new DatabaseSessionImpl());
        UnitOfWorkImpl context = refreshContext(manager.getDescriptor());
        context.setEntityListener(new RecordingListener("runtime", calls));
        ReadObjectQuery query = refreshQuery(manager.getDescriptor(), context);
        Object fresh = new Object();
        Object existing = new Object();
        Object original = new Object();
        RefreshOperation operation = context.beginRefreshOperation(query);
        DescriptorEvent cloneEvent = refreshEvent(fresh, original, query, context, manager.getDescriptor(),
                DescriptorEventManager.PostCloneEvent);
        operation.capturePostClone(cloneEvent);
        operation.capturePostClone(cloneEvent);
        manager.executeRefreshSourceEvent(cloneEvent);
        operation.recordRefresh(fresh, original, manager.getDescriptor());
        operation.recordRefresh(existing, original, manager.getDescriptor());
        operation.recordRefresh(existing, original, manager.getDescriptor());
        assertEquals(List.of("native:" + DescriptorEventManager.PostCloneEvent), calls);
        context.executeDeferredEvents();
        assertEquals(1, calls.size(), "The legacy queue cannot publish operation-owned JPA phases");

        context.completeRefreshOperation(operation, true);
        int clone = DescriptorEventManager.PostCloneEvent;
        int refresh = DescriptorEventManager.PostRefreshEvent;
        assertEquals(List.of("native:" + clone, "metadata:" + clone, "runtime:" + clone,
                "jpa:" + clone, "validation:" + clone),
                calls.stream().filter(call -> call.endsWith(":" + clone)).toList());
        assertEquals(List.of("metadata:" + refresh, "runtime:" + refresh,
                "jpa:" + refresh, "validation:" + refresh),
                calls.stream().filter(call -> call.endsWith(":" + refresh)).toList());
        context.completeRefreshOperation(operation, true);
        context.executeDeferredEvents();
        assertEquals(9, calls.size(), "Completed phases must not replay");
    }

    @Test
    void failedRefreshOperationDiscardsOnlyItsPhasesAndRestoresOuterOperation() {
        List<String> calls = new ArrayList<>();
        DescriptorEventManager manager = manager(Object.class, calls);
        UnitOfWorkImpl context = refreshContext(manager.getDescriptor());
        ReadObjectQuery query = refreshQuery(manager.getDescriptor(), context);
        Object outerTarget = new Object();
        Object innerTarget = new Object();
        Object unrelated = new Object();
        List<Object> notified = new ArrayList<>();
        context.setEntityListener(new DescriptorEventAdapter() {
            @Override
            public void postRefresh(DescriptorEvent event) { notified.add(event.getObject()); }
            @Override
            public void postClone(DescriptorEvent event) { notified.add(event.getObject()); }
        });
        context.deferEvent(refreshEvent(unrelated, null, null, context, manager.getDescriptor(),
                DescriptorEventManager.PostCloneEvent));
        RefreshOperation outer = context.beginRefreshOperation(query);
        outer.recordRefresh(outerTarget, new Object(), manager.getDescriptor());
        RefreshOperation inner = context.beginRefreshOperation(query);
        inner.recordRefresh(innerTarget, new Object(), manager.getDescriptor());
        context.completeRefreshOperation(inner, false);
        assertTrue(notified.isEmpty());
        context.completeRefreshOperation(outer, true);
        assertEquals(List.of(outerTarget), notified);
        context.executeDeferredEvents();
        assertEquals(List.of(outerTarget, unrelated), notified);
        context.completeRefreshOperation(inner, true);
        context.executeDeferredEvents();
        assertEquals(List.of(outerTarget, unrelated), notified);
    }

    @Test
    void refreshCallbackFailureConsumesPendingPhasesWithoutClearingUnrelatedEvents() {
        for (boolean error : List.of(false, true)) {
            List<String> calls = new ArrayList<>();
            DescriptorEventManager manager = manager(Object.class, calls);
            UnitOfWorkImpl context = refreshContext(manager.getDescriptor());
            ReadObjectQuery query = refreshQuery(manager.getDescriptor(), context);
            Object first = new Object();
            Object later = new Object();
            Object unrelated = new Object();
            List<Object> notified = new ArrayList<>();
            RuntimeException runtimeFailure = new IllegalStateException("refresh phase failure");
            AssertionError errorFailure = new AssertionError("refresh phase error");
            context.setEntityListener(new DescriptorEventAdapter() {
                @Override
                public void postRefresh(DescriptorEvent event) {
                    notified.add(event.getObject());
                    if (error) {
                        throw errorFailure;
                    }
                    throw runtimeFailure;
                }
                @Override
                public void postClone(DescriptorEvent event) { notified.add(event.getObject()); }
            });
            context.deferEvent(refreshEvent(unrelated, null, null, context, manager.getDescriptor(),
                    DescriptorEventManager.PostCloneEvent));
            RefreshOperation operation = context.beginRefreshOperation(query);
            operation.recordRefresh(first, new Object(), manager.getDescriptor());
            operation.recordRefresh(later, new Object(), manager.getDescriptor());
            if (error) {
                assertSame(errorFailure, assertThrows(AssertionError.class,
                        () -> context.completeRefreshOperation(operation, true)));
            } else {
                assertSame(runtimeFailure, assertThrows(IllegalStateException.class,
                        () -> context.completeRefreshOperation(operation, true)));
            }
            assertEquals(1, notified.size());
            Object delivered = notified.get(0);
            assertTrue(delivered == first || delivered == later);
            context.completeRefreshOperation(operation, true);
            context.executeDeferredEvents();
            context.executeDeferredEvents();
            assertEquals(List.of(delivered, unrelated), notified,
                    "Neither completed nor pending failed phases may replay");
        }
    }

    @Test
    void refreshCompletionRestoresOuterOperationBeforeReentrantCallback() {
        DescriptorEventManager manager = manager(Object.class, new ArrayList<>());
        UnitOfWorkImpl context = refreshContext(manager.getDescriptor());
        ReadObjectQuery query = refreshQuery(manager.getDescriptor(), context);
        Object outerTarget = new Object();
        Object innerTarget = new Object();
        Object reentrantTarget = new Object();
        List<Object> notified = new ArrayList<>();
        RefreshOperation outer = context.beginRefreshOperation(query);
        outer.recordRefresh(outerTarget, null, manager.getDescriptor());
        RefreshOperation inner = context.beginRefreshOperation(query);
        inner.recordRefresh(innerTarget, null, manager.getDescriptor());
        context.setEntityListener(new DescriptorEventAdapter() {
            @Override
            public void postRefresh(DescriptorEvent event) {
                notified.add(event.getObject());
                if (event.getObject() == innerTarget) {
                    RefreshOperation nested = context.beginRefreshOperation(query);
                    nested.recordRefresh(reentrantTarget, null, manager.getDescriptor());
                    context.completeRefreshOperation(nested, true);
                }
            }
        });
        context.completeRefreshOperation(inner, true);
        assertEquals(List.of(innerTarget, reentrantTarget), notified);
        context.completeRefreshOperation(outer, true);
        assertEquals(List.of(innerTarget, reentrantTarget, outerTarget), notified);
    }

    @Test
    void nativeReentrantOrdinaryQueryDoesNotJoinTheOuterRefreshClonePhase() {
        RefreshFixture fixture = new RefreshFixture();
        RefreshEntity source = fixture.source(71);
        RefreshEntity ordinarySource = fixture.source(72);
        List<Integer> nativeIds = new ArrayList<>();
        SourceBuilder ordinaryBuilder = new SourceBuilder(fixture.descriptor, ordinarySource);
        ReadObjectQuery ordinaryQuery = new ReadObjectQuery(RefreshEntity.class);
        ordinaryQuery.setDescriptor(fixture.descriptor);
        ordinaryQuery.setSession(fixture.context);
        fixture.descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            @Override
            public void postClone(DescriptorEvent event) {
                int id = ((RefreshEntity) event.getObject()).id;
                nativeIds.add(id);
                if (id == source.id) {
                    ordinaryBuilder.normal(ordinaryQuery, fixture.context);
                }
            }
        });
        ReadObjectQuery query = refreshQuery(fixture.descriptor, fixture.context);
        RefreshOperation operation = fixture.context.beginRefreshOperation(query);
        RefreshEntity target = fixture.clone(source, operation);
        assertEquals(List.of(71, 72), nativeIds);
        assertEquals(List.of(72), fixture.jpaIds,
                "An ordinary query reentered from a native callback retains its ordinary JPA phase");
        fixture.context.completeRefreshOperation(operation, true);
        assertEquals(List.of(72, 71), fixture.jpaIds);
        assertEquals(71, target.id);
        assertEquals(List.of(71, 72), nativeIds, "JPA completion must not repeat native callbacks");
    }

    @Test
    void rejectedCopiedQueryCloneCaptureRetainsItsOrdinaryJpaPhase() {
        RefreshFixture fixture = new RefreshFixture();
        RefreshEntity source = fixture.source(73);
        RefreshEntity unrelated = new RefreshEntity();
        unrelated.id = 74;
        ReadObjectQuery query = refreshQuery(fixture.descriptor, fixture.context);
        ReadObjectQuery copied = (ReadObjectQuery) query.clone();
        copied.setSession(fixture.context);
        RefreshOperation operation = fixture.context.beginRefreshOperation(query);
        fixture.context.afterPopulate = clone -> {
            DescriptorEvent rejected = refreshEvent(unrelated, null, copied, fixture.context,
                    fixture.descriptor, DescriptorEventManager.PostCloneEvent);
            assertFalse(operation.capturePostClone(rejected));
            fixture.context.deferPostCloneEvent(rejected);
        };
        fixture.clone(source, operation);
        assertEquals(List.of(74), fixture.jpaIds,
                "A rejected capture must not be converted to a native-only event");
        fixture.context.completeRefreshOperation(operation, true);
        assertEquals(List.of(74, 73), fixture.jpaIds);
    }

    @Test
    void onlyTheExactQueryRefreshMergeCollectsExistingTargets() {
        RefreshFixture fixture = new RefreshFixture();
        RefreshEntity source = fixture.source(75);
        RefreshEntity target = (RefreshEntity) fixture.context.registerExistingObject(source);
        fixture.jpaIds.clear();
        fixture.jpaValues.clear();
        ReadObjectQuery query = refreshQuery(fixture.descriptor, fixture.context);
        ReadObjectQuery copied = (ReadObjectQuery) query.clone();
        copied.setSession(fixture.context);
        RefreshOperation operation = fixture.context.beginRefreshOperation(query);
        fixture.context.mergeClone(source);
        fixture.context.deepMergeClone(source);
        fixture.context.shallowMergeClone(source);
        fixture.context.mergeCloneWithReferences(source);
        fixture.context.mergeClone(source, MergeManager.NO_CASCADE, true);
        fixture.context.revertObject(target);
        fixture.context.revertAndResume();
        fixture.context.mergeClone(source, MergeManager.NO_CASCADE, true, copied);
        assertTrue(fixture.jpaIds.isEmpty(), "Legacy merges, revert and copied queries cannot inherit authorization");
        source.value = "final state";
        fixture.context.mergeClone(source, MergeManager.NO_CASCADE, true, query);
        assertEquals("final state", target.value);
        assertTrue(fixture.jpaIds.isEmpty(), "Even an authorized merge waits for root completion");
        fixture.context.completeRefreshOperation(operation, true);
        assertEquals(List.of(75), fixture.jpaIds);
        assertEquals(List.of("final state"), fixture.jpaValues);
    }

    static Stream<Arguments> refreshCallerFailures() {
        return Stream.of("construction", "merge").flatMap(location -> Stream.of(false, true)
                .flatMap(error -> Stream.of("absent", "value", "null")
                        .flatMap(marker -> Stream.of("normal", "optimized", "report")
                                .map(route -> Arguments.of(location, error, marker, route)))));
    }

    @ParameterizedTest
    @MethodSource("refreshCallerFailures")
    void refreshCallerFailureRestoresNestedStateAndAllowsSameQueryRetry(
            String location, boolean error, String marker, String route) {
        RefreshFixture fixture = new RefreshFixture();
        RefreshEntity outerSource = fixture.source(81);
        RefreshEntity failedSource = fixture.source(82);
        RefreshEntity unrelated = new RefreshEntity();
        unrelated.id = 83;
        SourceBuilder outerBuilder = new SourceBuilder(fixture.descriptor, outerSource);
        SourceBuilder innerBuilder = new SourceBuilder(fixture.descriptor, failedSource);
        ObjectLevelReadQuery outerQuery = refreshExecutionQuery(outerBuilder, fixture.context, route);
        ObjectLevelReadQuery query = refreshExecutionQuery(innerBuilder, fixture.context, route);
        Object previousMarker = "value".equals(marker) ? new Object() : null;
        if (!"absent".equals(marker)) {
            query.setProperty(RefreshBuilder.marker(), previousMarker);
        }
        Throwable failure = error ? new AssertionError("refresh caller error")
                : new IllegalStateException("refresh caller failure");
        boolean[] failed = {false};
        boolean[] captured = {false};
        List<Integer> nativeClones = new ArrayList<>();
        List<Integer> nativeMerges = new ArrayList<>();
        fixture.descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            @Override
            public void postClone(DescriptorEvent event) {
                nativeClones.add(((RefreshEntity) event.getObject()).id);
            }

            @Override
            public void postMerge(DescriptorEvent event) {
                int id = ((RefreshEntity) event.getObject()).id;
                nativeMerges.add(id);
                if (id == failedSource.id && "merge".equals(location) && !failed[0]) {
                    assertTrue(captured[0], "The merge failure must follow real PostClone capture");
                    failed[0] = true;
                    throwRefreshFailure(failure);
                }
            }
        });
        fixture.context.deferEvent(refreshEvent(unrelated, null, null, fixture.context,
                fixture.descriptor, DescriptorEventManager.PostCloneEvent));
        fixture.context.afterPopulate = clone -> {
            RefreshEntity target = (RefreshEntity) clone;
            if (target.id == failedSource.id) {
                Object operation = privateRefreshField(fixture.context, "refreshOperation");
                DescriptorEvent pending = (DescriptorEvent) ((Map<?, ?>) privateRefreshField(operation, "events")).get(target);
                assertSame(target, pending.getObject());
                assertEquals(DescriptorEventManager.PostCloneEvent, pending.getEventCode());
                assertSame(query, pending.getQuery());
                captured[0] = true;
                if ("construction".equals(location)) {
                    failed[0] = true;
                    throwRefreshFailure(failure);
                }
                return;
            }
            if (target.id != outerSource.id) {
                return;
            }
            Object previousOperation = privateRefreshField(fixture.context, "refreshOperation");
            Object previousCloning = privateRefreshField(fixture.context, "refreshCloneOperation");
            assertSame(previousOperation, previousCloning,
                    "Run the failing caller inside a real outer clone's active handoff");
            if (error) {
                assertSame(failure, assertThrows(AssertionError.class,
                        query::executeDatabaseQuery));
            } else {
                assertSame(failure, assertThrows(IllegalStateException.class,
                        query::executeDatabaseQuery));
            }
            assertTrue(captured[0]);
            assertTrue(failed[0]);
            assertSame(previousOperation, privateRefreshField(fixture.context, "refreshOperation"));
            assertSame(previousCloning, privateRefreshField(fixture.context, "refreshCloneOperation"));
            assertSame(fixture.context, query.getSession());
            assertEquals(!"absent".equals(marker), query.getProperties().containsKey(RefreshBuilder.marker()));
            assertSame(previousMarker, query.getProperty(RefreshBuilder.marker()));
            assertFalse(query.isRefreshSource());
            assertEquals(fixture.context.profileStarts, fixture.context.profileEnds);
            assertFalse(fixture.context.getIdentityMapAccessorInstance().getCacheKeyForObject(failedSource.id,
                    RefreshEntity.class, fixture.descriptor, false).isAcquired(),
                    "The nested caller must release its cache-key lock");
            assertEquals(List.of(unrelated.id), fixture.jpaIds,
                    "Unrelated queued work survives, while neither pending refresh phase escapes");
            fixture.context.executeDeferredEvents();
            assertEquals(List.of(unrelated.id), fixture.jpaIds, "A later drain cannot replay a failed phase");

            failedSource.value = "retry state";
            RefreshEntity retried = (RefreshEntity) query.executeDatabaseQuery();
            assertEquals("retry state", retried.value);
            assertEquals(List.of(unrelated.id, failedSource.id), fixture.jpaIds);
            assertEquals("retry state", fixture.jpaValues.get(fixture.jpaValues.size() - 1));
            assertSame(previousOperation, privateRefreshField(fixture.context, "refreshOperation"));
            assertSame(previousCloning, privateRefreshField(fixture.context, "refreshCloneOperation"));
            assertSame(fixture.context, query.getSession());
            assertEquals(!"absent".equals(marker), query.getProperties().containsKey(RefreshBuilder.marker()));
            assertSame(previousMarker, query.getProperty(RefreshBuilder.marker()));
            assertFalse(query.isRefreshSource());
        };
        RefreshEntity outerTarget = (RefreshEntity) outerQuery.executeDatabaseQuery();
        assertEquals(outerSource.id, outerTarget.id);
        assertEquals(List.of(unrelated.id, failedSource.id, outerSource.id), fixture.jpaIds);
        fixture.context.executeDeferredEvents();
        assertEquals(List.of(unrelated.id, failedSource.id, outerSource.id), fixture.jpaIds);
        assertEquals(List.of(unrelated.id, outerSource.id, failedSource.id), nativeClones);
        assertEquals("merge".equals(location) ? List.of(failedSource.id, failedSource.id, outerSource.id)
                : List.of(failedSource.id, outerSource.id), nativeMerges);
        assertNull(privateRefreshField(fixture.context, "refreshOperation"));
        assertNull(privateRefreshField(fixture.context, "refreshCloneOperation"));
        assertSame(fixture.context, outerQuery.getSession());
        assertFalse(outerQuery.getProperties().containsKey(RefreshBuilder.marker()));
        assertFalse(outerQuery.isRefreshSource());
        assertEquals(fixture.context.profileStarts, fixture.context.profileEnds);
    }

    private static Object privateRefreshField(Object target, String name) {
        try {
            Class<?> type = target instanceof UnitOfWorkImpl ? UnitOfWorkImpl.class : RefreshOperation.class;
            java.lang.reflect.Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static void throwRefreshFailure(Throwable failure) {
        if (failure instanceof Error error) {
            throw error;
        }
        throw (RuntimeException) failure;
    }

    public static class RefreshEntity {
        public int id;
        public String value;
    }

    private static final class RefreshFixture {
        final ClassDescriptor descriptor = new RelationalDescriptor();
        final DatabaseSessionImpl parent = new DatabaseSessionImpl(new DatabaseLogin());
        final RefreshTestUnit context;
        final List<Integer> jpaIds = new ArrayList<>();
        final List<String> jpaValues = new ArrayList<>();

        RefreshFixture() {
            descriptor.setJavaClass(RefreshEntity.class);
            descriptor.addTableName("REFRESH_PHASE_UNIT");
            descriptor.addPrimaryKeyFieldName("ID");
            descriptor.addDirectMapping("id", "ID");
            descriptor.addDirectMapping("value", "ENTRY_VALUE");
            descriptor.useFullIdentityMap();
            parent.addDescriptor(descriptor);
            parent.initializeDescriptors();
            descriptor.getEventManager().enableProgrammaticEventListeners();
            context = new RefreshTestUnit(parent);
            context.setEntityListener(new DescriptorEventAdapter() {
                private void record(DescriptorEvent event) {
                    RefreshEntity entity = (RefreshEntity) event.getObject();
                    jpaIds.add(entity.id);
                    jpaValues.add(entity.value);
                }
                @Override
                public void postClone(DescriptorEvent event) { record(event); }
                @Override
                public void postRefresh(DescriptorEvent event) { record(event); }
            });
        }

        RefreshEntity source(int id) {
            RefreshEntity entity = new RefreshEntity();
            entity.id = id;
            entity.value = "before";
            parent.getIdentityMapAccessor().putInIdentityMap(entity);
            return entity;
        }

        RefreshEntity clone(RefreshEntity original, RefreshOperation operation) {
            CacheKey parentKey = parent.getIdentityMapAccessorInstance().getCacheKeyForObject(original);
            return (RefreshEntity) context.cloneAndRegisterObject(original, parentKey,
                    new CacheKey(parentKey.getKey()), descriptor, operation);
        }
    }

    private static final class RefreshTestUnit extends UnitOfWorkImpl {
        Consumer<Object> afterPopulate = clone -> { };
        final Map<String, Integer> profileStarts = new HashMap<>();
        final Map<String, Integer> profileEnds = new HashMap<>();

        RefreshTestUnit(AbstractSession parent) { super(parent, ReferenceMode.HARD); }

        @Override
        public void startOperationProfile(String operation) {
            profileStarts.merge(operation, 1, Integer::sum);
            super.startOperationProfile(operation);
        }

        @Override
        public void endOperationProfile(String operation) {
            profileEnds.merge(operation, 1, Integer::sum);
            super.endOperationProfile(operation);
        }

        @Override
        protected void populateAndRegisterObject(Object original, Object workingClone, CacheKey unitKey,
                CacheKey parentKey, ClassDescriptor descriptor) {
            super.populateAndRegisterObject(original, workingClone, unitKey, parentKey, descriptor);
            afterPopulate.accept(workingClone);
        }
    }

    private static final class SourceBuilder extends ObjectBuilder {
        private final RefreshEntity source;

        SourceBuilder(ClassDescriptor descriptor, RefreshEntity source) {
            super(descriptor);
            this.source = source;
        }

        Object normal(ObjectBuildingQuery query, UnitOfWorkImpl context) {
            return buildWorkingCopyCloneNormally(query, null, context, source.id, null, getDescriptor(), null);
        }

        @Override
        protected Object buildObject(boolean returnCacheKey, ObjectBuildingQuery query, AbstractRecord row,
                AbstractSession session, Object primaryKey, CacheKey key, ClassDescriptor descriptor,
                JoinedAttributeManager joinManager) {
            return session.getIdentityMapAccessorInstance().getCacheKeyForObject(source);
        }
    }

    private static ObjectLevelReadQuery refreshExecutionQuery(
            SourceBuilder builder, UnitOfWorkImpl context, String route) {
        return "report".equals(route) ? new RefreshReportExecutionQuery(builder, context)
                : new RefreshExecutionQuery(builder, context, "optimized".equals(route));
    }

    private static final class RefreshReportExecutionQuery extends ReportQuery {
        private final SourceBuilder builder;
        private final UnitOfWorkImpl context;

        RefreshReportExecutionQuery(SourceBuilder builder, UnitOfWorkImpl context) {
            setReferenceClass(RefreshEntity.class);
            this.builder = builder;
            this.context = context;
            setDescriptor(builder.getDescriptor());
            setSession(context);
            refreshIdentityMapResult();
            setQueryMechanism(new ExpressionQueryMechanism(this) {
                @Override
                public Vector<AbstractRecord> selectAllReportQueryRows() {
                    return new Vector<>();
                }
            });
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Object buildObjects(Vector rows) {
            return builder.normal(this, context);
        }
    }

    private static final class RefreshExecutionQuery extends ReadObjectQuery {
        private final SourceBuilder builder;
        private final UnitOfWorkImpl context;

        RefreshExecutionQuery(SourceBuilder builder, UnitOfWorkImpl context, boolean optimized) {
            super(RefreshEntity.class);
            this.builder = builder;
            this.context = context;
            setDescriptor(builder.getDescriptor());
            setSession(context);
            refreshIdentityMapResult();
            this.isResultSetOptimizedQuery = optimized;
        }

        @Override
        protected Object executeObjectLevelReadQuery() {
            return builder.normal(this, context);
        }

        @Override
        protected Object executeObjectLevelReadQueryFromResultSet() {
            return builder.normal(this, context);
        }
    }

    private static UnitOfWorkImpl refreshContext(ClassDescriptor descriptor) {
        DatabaseSessionImpl parent = new DatabaseSessionImpl(new DatabaseLogin());
        parent.addDescriptor(descriptor);
        return new UnitOfWorkImpl(parent, ReferenceMode.HARD);
    }

    private static ReadObjectQuery refreshQuery(ClassDescriptor descriptor, UnitOfWorkImpl context) {
        ReadObjectQuery query = new ReadObjectQuery(descriptor.getJavaClass());
        query.setDescriptor(descriptor);
        query.setSession(context);
        query.refreshIdentityMapResult();
        return query;
    }

    private static DescriptorEvent refreshEvent(Object target, Object original, ObjectBuildingQuery query,
            AbstractSession context, ClassDescriptor descriptor, int code) {
        DescriptorEvent event = new DescriptorEvent(target);
        event.setOriginalObject(original);
        event.setQuery(query);
        event.setSession(context);
        event.setDescriptor(descriptor);
        event.setEventCode(code);
        return event;
    }

    private static class ProfileSession extends DatabaseSessionImpl {
        int starts;
        int ends;

        @Override
        public void startOperationProfile(String operation) { starts++; }
        @Override
        public void endOperationProfile(String operation) { ends++; }
    }

    private static class RefreshBuilder extends ObjectBuilder {
        final Object cachedSource = new Object();
        Consumer<ObjectBuildingQuery> onBuild = this::emit;

        RefreshBuilder(ClassDescriptor descriptor) { super(descriptor); }

        static String marker() { return "eclipselink.internal.jpa-refresh-source-query"; }

        ReadObjectQuery query() {
            ReadObjectQuery query = new ReadObjectQuery(Object.class);
            query.setDescriptor(getDescriptor());
            query.setSession(new DatabaseSessionImpl(new DatabaseLogin()));
            query.refreshIdentityMapResult();
            return query;
        }

        void source(ObjectBuildingQuery query) {
            buildRefreshSource(query, null, query.getSession(), 1, null, getDescriptor(), null);
        }

        void emit(ObjectBuildingQuery query) {
            postBuildAttributesIntoObjectEvent(cachedSource, null, query, true);
        }

        Object normal(ObjectBuildingQuery query, UnitOfWorkImpl context) {
            return buildWorkingCopyCloneNormally(query, null, context, 1, null, getDescriptor(), null);
        }

        @Override
        protected Object buildObject(boolean returnCacheKey, ObjectBuildingQuery query, AbstractRecord row,
                AbstractSession session, Object primaryKey, CacheKey preFetchedCacheKey,
                ClassDescriptor descriptor, JoinedAttributeManager joinManager) {
            onBuild.accept(query);
            return new CacheKey(primaryKey, cachedSource, null);
        }
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
        @Override
        public void postRefresh(DescriptorEvent event) { record(event); }
        @Override
        public void postClone(DescriptorEvent event) { record(event); }
    }
}
