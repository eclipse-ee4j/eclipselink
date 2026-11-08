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
package org.eclipse.persistence.jpa.test.query;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.methodnative.discovery.MethodNativeDiscoveryBase;
import org.eclipse.persistence.jpa.test.query.methodnative.discovery.MethodNativeDiscoveryDetail;
import org.eclipse.persistence.jpa.test.query.methodnative.discovery.MethodNativeDiscoveryOtherDetail;
import org.eclipse.persistence.jpa.test.query.methodnative.discovery.MethodNativeDiscoveryOwner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/** Method-native declarations reached only through embeddable mapped-superclass processing. */
@RunWith(EmfRunner.class)
public class TestMethodNativeManagedDiscovery {
    private static final String BASE_QUERY = name(MethodNativeDiscoveryBase.class, "inheritedText");
    private static final String DETAIL_QUERY = name(MethodNativeDiscoveryDetail.class, "detailText");
    private static final String OTHER_QUERY = name(MethodNativeDiscoveryOtherDetail.class, "otherText");

    @Emf(name = "method-native-managed-discovery", createTables = DDLGen.DROP_CREATE,
            classes = {MethodNativeDiscoveryOwner.class},
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(manager -> {
            manager.persist(new MethodNativeDiscoveryOwner(1, "First", "Other first"));
            manager.persist(new MethodNativeDiscoveryOwner(2, "Second", "Other second"));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(manager -> manager.createStatement("delete from MethodNativeDiscoveryOwner").execute());
        emf.getCache().evictAll();
    }

    @Test
    public void implicitEmbeddableSuperclassesRegisterEachDeclarationOnce() {
        // Neither embeddable nor their superclass is a listed PU class. The owner does not inherit the declaration.
        assertEquals(Object.class, MethodNativeDiscoveryOwner.class.getSuperclass());
        try (EntityManagerFactory factory = createFactory()) {
            Set<String> expected = Set.of(BASE_QUERY, DETAIL_QUERY, OTHER_QUERY);
            assertEquals(expected, factory.getNamedQueries(Object.class).keySet());
            assertEquals(1, factory.unwrap(JpaEntityManagerFactory.class).getServerSession()
                    .getQueries().get(BASE_QUERY).size());
            withHandlers(factory, handler -> {
                var inherited = handler.createNamedQuery(BASE_QUERY);
                assertEquals("First", inherited.setParameter(1, 1).getSingleResult());
                assertEquals("Second", inherited.setParameter(1, 2).getSingleResult());
                assertEquals("First", handler.createNamedQuery(DETAIL_QUERY).setParameter(1, 1).getSingleResult());
                assertEquals("Other first", handler.createNamedQuery(OTHER_QUERY).setParameter(1, 1).getSingleResult());
                assertNoInheritedAliases(handler);
                assertEmbeddedState(handler);
            });
            assertEquals(expected, factory.getNamedQueries(Object.class).keySet());
            assertEquals(1, factory.unwrap(JpaEntityManagerFactory.class).getServerSession()
                    .getQueries().get(BASE_QUERY).size());
        }
    }

    @Test
    public void embeddableMetadataCompleteKeepsSuperclassDeclarations() {
        try (EntityManagerFactory factory = createFactory("embeddable-complete.xml")) {
            assertEquals(Set.of(BASE_QUERY, OTHER_QUERY), factory.getNamedQueries(Object.class).keySet());
            withHandlers(factory, handler -> {
                assertEquals("First", handler.createNamedQuery(BASE_QUERY).setParameter(1, 1).getSingleResult());
                assertEquals("Other first", handler.createNamedQuery(OTHER_QUERY).setParameter(1, 1).getSingleResult());
                assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery(DETAIL_QUERY));
                assertNoInheritedAliases(handler);
                assertEmbeddedState(handler);
            });
        }
    }

    @Test
    public void mappedSuperclassMetadataCompleteSuppressesOnlyItsDeclarations() {
        try (EntityManagerFactory factory = createFactory("superclass-complete.xml")) {
            assertEquals(Set.of(DETAIL_QUERY, OTHER_QUERY), factory.getNamedQueries(Object.class).keySet());
            withHandlers(factory, handler -> {
                assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery(BASE_QUERY));
                assertEquals("First", handler.createNamedQuery(DETAIL_QUERY).setParameter(1, 1).getSingleResult());
                assertEquals("Other first", handler.createNamedQuery(OTHER_QUERY).setParameter(1, 1).getSingleResult());
                assertNoInheritedAliases(handler);
                assertEmbeddedState(handler);
            });
        }
    }

    @Test
    public void globalMetadataCompleteSuppressesImplicitSuperclassAndEmbeddableDeclarations() {
        try (EntityManagerFactory factory = createFactory("complete.xml")) {
            assertEquals(Set.of("methodNativeManaged.xmlOnly"), factory.getNamedQueries(Object.class).keySet());
            withHandlers(factory, handler -> {
                assertEquals("Second", handler.createNamedQuery("methodNativeManaged.xmlOnly")
                        .setParameter(1, 2).getSingleResult());
                for (String query : List.of(BASE_QUERY, DETAIL_QUERY, OTHER_QUERY)) {
                    assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery(query));
                }
                assertNoInheritedAliases(handler);
                assertEmbeddedState(handler);
            });
        }
    }

    private static String name(Class<?> declaringType, String method) {
        return declaringType.getCanonicalName() + "#" + method + "(java.lang.Integer)";
    }

    private static void assertNoInheritedAliases(EntityHandler handler) {
        for (Class<?> type : List.of(MethodNativeDiscoveryOwner.class, MethodNativeDiscoveryDetail.class,
                MethodNativeDiscoveryOtherDetail.class)) {
            assertThrows(IllegalArgumentException.class,
                    () -> handler.createNamedQuery(name(type, "inheritedText")));
        }
    }

    private static void assertEmbeddedState(EntityHandler handler) {
        MethodNativeDiscoveryOwner owner = handler.find(MethodNativeDiscoveryOwner.class, 1);
        assertEquals("First", owner.detail.text);
        assertEquals("Other first", owner.otherDetail.text);
    }

    private static void withHandlers(EntityManagerFactory factory, Consumer<EntityHandler> assertions) {
        try (EntityManager manager = factory.createEntityManager()) {
            assertions.accept(manager);
        }
        try (EntityAgent agent = factory.createEntityAgent()) {
            assertions.accept(agent);
        }
    }

    private static EntityManagerFactory createFactory(String... mappingFiles) {
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setPersistenceUnitName("method-native-managed-discovery-" + UUID.randomUUID());
        info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
        info.setPersistenceXMLSchemaVersion("4.0");
        info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        info.setClassLoader(TestMethodNativeManagedDiscovery.class.getClassLoader());
        info.setNewTempClassLoader(TestMethodNativeManagedDiscovery.class.getClassLoader());
        info.setPersistenceUnitRootUrl(TestMethodNativeManagedDiscovery.class.getProtectionDomain().getCodeSource().getLocation());
        info.setManagedClassNames(List.of(MethodNativeDiscoveryOwner.class.getName()));
        info.setExcludeUnlistedClasses(true);
        info.setMappingFileNames(java.util.Arrays.stream(mappingFiles)
                .map(file -> "META-INF/method-native-managed-discovery/" + file).toList());
        info.setProperties(new Properties());
        Map<String, Object> settings = new HashMap<>();
        settings.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
        settings.put(PersistenceUnitProperties.WEAVING, "false");
        settings.put(PersistenceUnitProperties.DDL_GENERATION, "none");
        settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        settings.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        settings.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String key : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(key);
            if (value != null) {
                settings.put(key, value);
            }
        }
        return Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), settings);
    }
}
