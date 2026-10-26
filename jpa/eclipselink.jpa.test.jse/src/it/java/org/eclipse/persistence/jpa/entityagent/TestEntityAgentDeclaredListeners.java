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

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels.Alpha;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels.Beta;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels.CallbackRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels.XmlAlpha;
import org.eclipse.persistence.jpa.entityagent.model.AgentDeclaredListenerModels.XmlBeta;
import org.eclipse.persistence.jpa.metadata.FileBasedProjectCache;
import org.eclipse.persistence.jpa.metadata.ProjectCache;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Owned H2 bootstrap fixtures keep invalid plain entities out of static weaving. */
@RunWith(EmfRunner.class)
public class TestEntityAgentDeclaredListeners {
    @Test
    public void sharedAnnotationListenerSelectsEachUnrelatedAgentEntity() throws Exception {
        verifyRoute("annotation", false);
    }

    @Test
    public void sharedAnnotationListenerPreservesOrdinaryManagerCallbacks() throws Exception {
        verifyRoute("annotation", true);
    }

    @Test
    public void xmlDeclaredListenerSelectsCompatibleAnnotationCallbacks() throws Exception {
        verifyRoute("xml", false);
    }

    @Test
    public void xmlDeclaredListenerPreservesOrdinaryManagerCallbacks() throws Exception {
        verifyRoute("xml", true);
    }

    @Test
    public void defaultListenerSelectsCompatibleAnnotationCallbacks() throws Exception {
        verifyRoute("default", false);
    }

    @Test
    public void defaultListenerPreservesOrdinaryManagerCallbacks() throws Exception {
        verifyRoute("default", true);
    }

    @Test
    public void serializedAnnotationListenerRestoresBothTypedOverloads() throws Exception {
        verifyCachedRoute("annotation");
    }

    @Test
    public void serializedXmlListenerRestoresBothTypedOverloads() throws Exception {
        verifyCachedRoute("xml");
    }

    @Test
    public void serializedDefaultListenerRestoresBothTypedOverloads() throws Exception {
        verifyCachedRoute("default");
    }

    @Test
    public void zeroArgumentListenerCallbackIsRejected() throws Exception {
        assertInvalid("zero", AgentDeclaredListenerModels.InvalidZero.class,
                ValidationException.INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS);
    }

    @Test
    public void twoArgumentListenerCallbackIsRejected() throws Exception {
        assertInvalid("two", AgentDeclaredListenerModels.InvalidTwo.class,
                ValidationException.INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS);
    }

    @Test
    public void incompatibleStaticCallbackIsRejected() throws Exception {
        assertInvalid("static", AgentDeclaredListenerModels.InvalidStatic.class,
                ValidationException.INVALID_CALLBACK_METHOD_MODIFIER);
    }

    @Test
    public void incompatibleFinalCallbackIsRejected() throws Exception {
        assertInvalid("final", AgentDeclaredListenerModels.InvalidFinal.class,
                ValidationException.INVALID_CALLBACK_METHOD_MODIFIER);
    }

    @Test
    public void incompatibleNonVoidCallbackIsRejected() throws Exception {
        assertInvalid("return", AgentDeclaredListenerModels.InvalidReturn.class,
                ValidationException.INVALID_CALLBACK_METHOD_MODIFIER);
    }

    @Test
    public void primitiveParameterCallbackIsRejected() throws Exception {
        assertInvalid("primitive", AgentDeclaredListenerModels.InvalidPrimitive.class,
                ValidationException.INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS);
    }

    @Test
    public void duplicateEventAndParameterCallbacksAreRejected() throws Exception {
        assertInvalid("duplicate", AgentDeclaredListenerModels.InvalidDuplicate.class,
                ValidationException.MULTIPLE_CALLBACK_METHODS_DEFINED);
    }

    @Test
    public void entityCallbackWithAnIncompatibleParameterIsRejected() throws Exception {
        assertInvalid("entity-arity", AgentDeclaredListenerModels.InvalidEntityArity.class,
                ValidationException.INVALID_ENTITY_CALLBACK_METHOD_ARGUMENTS);
    }

    @Test
    public void duplicateCallbacksForAnUnrelatedParameterAreRejected() throws Exception {
        assertInvalid("unrelated-duplicate", AgentDeclaredListenerModels.InvalidUnrelatedDuplicate.class,
                ValidationException.MULTIPLE_CALLBACK_METHODS_DEFINED);
    }

    private static void verifyRoute(String route, boolean managed) throws Exception {
        try (ListenerUnit unit = new ListenerUnit(route);
                EntityManagerFactory factory = unit.factory(null)) {
            writeAndVerify(factory, route, managed);
        }
    }

    private static void verifyCachedRoute(String route) throws Exception {
        try (ListenerUnit unit = new ListenerUnit(route)) {
            try (EntityManagerFactory original = unit.factory("java-serialization")) {
                writeAndVerify(original, route, false);
                assertTrue(Files.size(unit.cacheFile) > 0);
            }
            RecordingCache cache = new RecordingCache();
            try (EntityManagerFactory restored = unit.factory(cache)) {
                assertEquals(1, cache.hits);
                assertEquals(0, cache.stores);
                assertSame(cache.retrieved, restored.unwrap(JpaEntityManagerFactory.class)
                        .getServerSession().getProject());
                writeAndVerify(restored, route, false);
                writeAndVerify(restored, route, true);
            }
        }
    }

    private static void writeAndVerify(EntityManagerFactory factory, String route, boolean managed) throws Exception {
        List<Class<? extends CallbackRecord>> types = route.equals("annotation")
                ? List.of(Alpha.class, Beta.class) : List.of(XmlAlpha.class, XmlBeta.class);
        for (Class<? extends CallbackRecord> type : types) {
            CallbackRecord record = type.getConstructor().newInstance();
            type.getField("id").setInt(record, 1);
            if (managed) {
                try (EntityManager manager = factory.createEntityManager()) {
                    manager.getTransaction().begin();
                    try {
                        manager.persist(record);
                        assertEquals(List.of("persist-" + type.getSimpleName()), record.events());
                        manager.flush();
                        assertTrue(manager.contains(record));
                        assertSame(record, manager.find(type, 1));
                        assertEquals(List.of("persist-" + type.getSimpleName(),
                                "insert-" + type.getSimpleName(), "post-any"), record.events());
                        manager.getTransaction().commit();
                    } finally {
                        if (manager.getTransaction().isActive()) {
                            manager.getTransaction().rollback();
                        }
                    }
                }
            } else {
                try (EntityAgent agent = factory.createEntityAgent()) {
                    agent.insert(record);
                    assertEquals(List.of("insert-" + type.getSimpleName(), "post-any"), record.events());
                }
            }
            try (EntityAgent observer = factory.createEntityAgent()) {
                CallbackRecord stored = observer.get(type, 1);
                assertEquals(type.getSimpleName() + "-written", stored.name());
                observer.delete(stored);
            }
        }
    }

    private static void assertInvalid(String resource, Class<?> type, int expectedCode) throws Exception {
        try (ListenerUnit unit = new ListenerUnit("invalid-" + resource, List.of(type))) {
            RuntimeException failure = assertThrows(RuntimeException.class, () -> unit.factory(null));
            ValidationException validation = null;
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                if (cause instanceof ValidationException candidate && candidate.getErrorCode() == expectedCode) {
                    validation = candidate;
                    break;
                }
            }
            assertNotNull("Expected callback validation error " + expectedCode + " in the bootstrap cause chain",
                    validation);
        }
    }

    public static final class RecordingCache implements ProjectCache {
        private final FileBasedProjectCache delegate = new FileBasedProjectCache();
        private Project retrieved;
        private int hits;
        private int stores;

        @Override
        public Project retrieveProject(Map<String, ?> properties, ClassLoader loader, SessionLog log) {
            retrieved = delegate.retrieveProject(properties, loader, log);
            if (retrieved != null) {
                hits++;
            }
            return retrieved;
        }

        @Override
        public void storeProject(Project project, Map<String, ?> properties, SessionLog log) {
            stores++;
            delegate.storeProject(project, properties, log);
        }
    }

    private static final class ListenerUnit implements AutoCloseable {
        private final String route;
        private final List<Class<?>> classes;
        private final DataSource dataSource;
        private final Connection keeper;
        private final String url = "jdbc:h2:mem:declared-listeners-" + UUID.randomUUID();
        private final Path directory = Files.createTempDirectory("entityagent-declared-listeners-");
        private final Path cacheFile = directory.resolve("project.bin");

        private ListenerUnit(String route) throws Exception {
            this(route, route.equals("annotation") ? List.of(Alpha.class, Beta.class)
                    : List.of(XmlAlpha.class, XmlBeta.class));
        }

        private ListenerUnit(String route, List<Class<?>> classes) throws Exception {
            this.route = route;
            this.classes = classes;
            Class<?> type = Class.forName("org.h2.jdbcx.JdbcDataSource");
            Object source = type.getConstructor().newInstance();
            type.getMethod("setURL", String.class).invoke(source, url);
            type.getMethod("setUser", String.class).invoke(source, "sa");
            type.getMethod("setPassword", String.class).invoke(source, "");
            dataSource = (DataSource) source;
            keeper = dataSource.getConnection();
            try (java.sql.Statement statement = keeper.createStatement()) {
                statement.execute("CREATE TABLE EA_DECLARED_ALPHA (ID INTEGER PRIMARY KEY, NAME VARCHAR(255))");
                statement.execute("CREATE TABLE EA_DECLARED_BETA (ID INTEGER PRIMARY KEY, NAME VARCHAR(255))");
            }
        }

        private EntityManagerFactory factory(Object cache) {
            SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
            info.setPersistenceUnitName("declared-listeners-" + UUID.randomUUID());
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(TestEntityAgentDeclaredListeners.class.getClassLoader());
            info.setNewTempClassLoader(TestEntityAgentDeclaredListeners.class.getClassLoader());
            info.setPersistenceUnitRootUrl(TestEntityAgentDeclaredListeners.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(classes.stream().map(Class::getName).toList());
            if (!route.equals("annotation")) {
                String mapping = route.startsWith("invalid-") ? "entityagent-listener-" + route + ".xml"
                        : route.equals("xml") ? "entityagent-declared-listeners.xml" : "entityagent-default-listeners.xml";
                info.setMappingFileNames(List.of("META-INF/" + mapping));
            }
            info.setExcludeUnlistedClasses(true);
            info.setNonJtaDataSource(dataSource);
            Properties properties = new Properties();
            properties.setProperty("jakarta.persistence.jdbc.url", url);
            properties.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
            properties.setProperty("jakarta.persistence.jdbc.user", "sa");
            properties.setProperty("jakarta.persistence.jdbc.password", "");
            properties.setProperty("jakarta.persistence.validation.mode", "NONE");
            properties.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform");
            properties.setProperty("eclipselink.weaving", "false");
            properties.setProperty("eclipselink.logging.level", "OFF");
            properties.setProperty("eclipselink.cache.shared.default", "false");
            info.setProperties(properties);
            Map<String, Object> overrides = new HashMap<>();
            overrides.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            if (cache != null) {
                overrides.put(PersistenceUnitProperties.PROJECT_CACHE, cache);
                overrides.put(PersistenceUnitProperties.PROJECT_CACHE_FILE, cacheFile.toString());
            }
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), overrides);
            try {
                factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
                return factory;
            } catch (RuntimeException | Error failure) {
                factory.close();
                throw failure;
            }
        }

        @Override
        public void close() throws Exception {
            try {
                keeper.close();
            } finally {
                Files.deleteIfExists(cacheFile);
                Files.deleteIfExists(directory);
            }
        }
    }
}
