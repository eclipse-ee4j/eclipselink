/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.fetchdefaults;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.jpa.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.fetchdefaults.model.DefaultFetchMappedSuperclass;
import org.eclipse.persistence.jpa.fetchdefaults.model.DefaultFetchOwner;
import org.eclipse.persistence.jpa.fetchdefaults.model.DefaultFetchPropertyOwner;
import org.eclipse.persistence.jpa.fetchdefaults.model.DefaultFetchTarget;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

/** Public bootstrap and woven runtime coverage for the persistence-unit to-one default. */
public class TestDefaultToOneFetch {
    private static final AtomicLong IDENTITIES = new AtomicLong();
    private static final String XML = "META-INF/default-to-one-fetch-persistence.xml";
    private static final String ORM = "META-INF/default-to-one-fetch-orm.xml";
    private static final String COMPLETE_ORM = "META-INF/default-to-one-fetch-complete-orm.xml";
    private static final List<String> TABLES = List.of("JPA_DTF_OWNER", "JPA_DTF_PROPERTY",
            "JPA_DTF_TARGET", "JPA_DTF_TAGS", "JPA_DTF_LINKS");
    private static final List<String> DEFAULT_ATTRIBUTES = List.of("omittedMany", "defaultMany",
            "omittedOne", "defaultOne");
    private static EntityManagerFactory fixture;
    private static SchemaManager schema;
    private static boolean ownsSchema;

    @BeforeClass
    public static void createFixture() throws Exception {
        if (Boolean.getBoolean("eclipselink.test.default-to-one-fetch.agent")) {
            assertTrue("The isolated agent execution must activate the real Java agent", JavaSECMPInitializer.usesAgent());
        }
        // Check the actual fields required for lazy to-one loading, not just a weaving marker.
        for (String attribute : List.of("omittedMany", "defaultMany", "omittedOne", "defaultOne",
                "lazyMany", "eagerMany", "lazyOne", "eagerOne", "xmlLazy", "xmlEager")) {
            assertHolderField(DefaultFetchOwner.class, attribute);
        }
        assertHolderField(DefaultFetchPropertyOwner.class, "many");
        assertHolderField(DefaultFetchPropertyOwner.class, "one");
        assertHolderField(DefaultFetchMappedSuperclass.class, "inheritedTarget");
        fixture = programmatic(configuration(identity("fixture"), null));
        schema = new SchemaManager(session(fixture));
        for (String table : TABLES) {
            assertFalse("Refusing to acquire a pre-existing fixture table: " + table, exists(table));
        }
        ownsSchema = true;
        schema.createDefaultTables(false);
        for (String table : TABLES) {
            assertTrue("Provider DDL must create " + table, exists(table));
        }
        try (EntityManager manager = fixture.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                List<DefaultFetchTarget> targets = new ArrayList<>();
                for (int id = 1; id <= 14; id++) {
                    DefaultFetchTarget target = new DefaultFetchTarget(id, "target-" + id);
                    manager.persist(target);
                    targets.add(target);
                }
                manager.persist(new DefaultFetchOwner(1, targets));
                manager.persist(new DefaultFetchPropertyOwner(1, targets.get(11), targets.get(12), targets.get(13)));
                manager.getTransaction().commit();
            } finally {
                if (manager.getTransaction().isActive()) {
                    manager.getTransaction().rollback();
                }
            }
        }
    }

    @AfterClass
    public static void removeFixture() {
        try {
            if (ownsSchema) {
                schema.dropDefaultTables();
                for (String table : TABLES) {
                    assertFalse("Owned fixture table must be removed: " + table, exists(table));
                }
            }
        } finally {
            if (fixture != null && fixture.isOpen()) {
                fixture.close();
            }
        }
    }

    @Test
    public void programmaticLazyDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("lazy"), FetchType.LAZY))) {
            assertOwnerPolicy(factory, FetchType.LAZY);
        }
    }

    @Test
    public void programmaticEagerDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("eager"), FetchType.EAGER))) {
            assertOwnerPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void omittedUnitDefaultRemainsEager() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("omitted"), null))) {
            assertOwnerPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void xmlLazyDefaults() throws Exception {
        try (EntityManagerFactory factory = xmlFactory("default-to-one-fetch-xml-lazy")) {
            assertOwnerPolicy(factory, FetchType.LAZY);
        }
    }

    @Test
    public void xmlEagerDefaults() throws Exception {
        try (EntityManagerFactory factory = xmlFactory("default-to-one-fetch-xml-eager")) {
            assertOwnerPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void xmlOmittedDefaultRemainsEager() throws Exception {
        try (EntityManagerFactory factory = xmlFactory("default-to-one-fetch-xml-omitted")) {
            assertOwnerPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void containerLazyDefault() throws Exception {
        assertContainerPolicy(FetchType.LAZY);
    }

    @Test
    public void containerEagerDefault() throws Exception {
        assertContainerPolicy(FetchType.EAGER);
    }

    @Test
    public void legacyContainerNullDefaultRemainsEager() throws Exception {
        assertContainerPolicy(null);
    }

    @Test
    public void propertyAndInheritedLazyDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("property-lazy"), FetchType.LAZY))) {
            assertPropertyPolicy(factory, FetchType.LAZY);
        }
    }

    @Test
    public void propertyAndInheritedEagerDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("property-eager"), FetchType.EAGER))) {
            assertPropertyPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void ormOverridesAndOmittedFetchWithLazyDefault() throws Exception {
        assertOrmPolicy(FetchType.LAZY);
    }

    @Test
    public void ormOverridesAndOmittedFetchWithEagerDefault() throws Exception {
        assertOrmPolicy(FetchType.EAGER);
    }

    @Test
    public void metadataCompleteLazyDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("complete-lazy"), FetchType.LAZY)
                .mappingFile(COMPLETE_ORM))) {
            assertPropertyPolicy(factory, FetchType.LAZY);
        }
    }

    @Test
    public void metadataCompleteEagerDefaults() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("complete-eager"), FetchType.EAGER)
                .mappingFile(COMPLETE_ORM))) {
            assertPropertyPolicy(factory, FetchType.EAGER);
        }
    }

    @Test
    public void sameNameLazyThenEagerFactoriesRetainTheirDefaults() throws Exception {
        assertFactoryIsolation(FetchType.LAZY, FetchType.EAGER);
    }

    @Test
    public void sameNameEagerThenLazyFactoriesRetainTheirDefaults() throws Exception {
        assertFactoryIsolation(FetchType.EAGER, FetchType.LAZY);
    }

    @Test
    public void sameNameSameDefaultReusesDeployment() throws Exception {
        String name = identity("same-default");
        try (EntityManagerFactory first = programmatic(configuration(name, FetchType.LAZY));
                EntityManagerFactory second = programmatic(configuration(name, FetchType.LAZY))) {
            assertSame(session(first), session(second));
            assertOwnerPolicy(first, FetchType.LAZY);
            assertOwnerPolicy(second, FetchType.LAZY);
        }
    }

    @Test
    public void explicitSessionNamePreservesReuseAndCollisionCheck() throws Exception {
        String name = identity("explicit-session");
        String sessionName = identity("session");
        PersistenceConfiguration lazy = configuration(name, FetchType.LAZY)
                .property(PersistenceUnitProperties.SESSION_NAME, sessionName);
        try (EntityManagerFactory first = programmatic(lazy)) {
            assertEquals(sessionName, session(first).getName());
            try (EntityManagerFactory second = programmatic(lazy)) {
                assertSame(session(first), session(second));
                assertOwnerPolicy(second, FetchType.LAZY);
            }
            PersistenceConfiguration eager = configuration(name, FetchType.EAGER)
                    .property(PersistenceUnitProperties.SESSION_NAME, sessionName);
            PersistenceUnitLoadingException exception = assertThrows(PersistenceUnitLoadingException.class,
                    () -> new PersistenceProvider().createEntityManagerFactory(eager));
            assertEquals(PersistenceUnitLoadingException.SESSION_NAME_ALREADY_IN_USE, exception.getErrorCode());
            assertOwnerPolicy(first, FetchType.LAZY);
        }
    }

    @Test
    public void xmlFactoriesRetainNameBasedReuseWithAgent() throws Exception {
        String name = "default-to-one-fetch-weaving";
        Map<String, Object> properties = properties();
        properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML,
                "META-INF/default-to-one-fetch-weaving/META-INF/persistence.xml");
        try (EntityManagerFactory first = Persistence.createEntityManagerFactory(name, properties);
                EntityManagerFactory second = Persistence.createEntityManagerFactory(name, properties)) {
            assertSame(session(first), session(second));
            if (JavaSECMPInitializer.usesAgent()) {
                assertEquals(name, first.unwrap(EntityManagerFactoryDelegate.class).getSetupImpl().getPersistenceUnitUniqueName());
            }
            assertOwnerPolicy(first, FetchType.EAGER);
            assertOwnerPolicy(second, FetchType.EAGER);
        }
    }

    @Test
    public void entityAgentFetchesDefaultLazyToOne() throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("agent-lazy"), FetchType.LAZY));
                EntityAgent agent = factory.createEntityAgent()) {
            DefaultFetchOwner owner = agent.get(DefaultFetchOwner.class, 1);
            assertNotNull(owner);
            assertTrue(((ForeignReferenceMapping) session(factory).getDescriptor(DefaultFetchOwner.class)
                    .getMappingForAttributeName("omittedMany")).isLazy());
            DefaultFetchTarget reference = owner.getOmittedMany();
            assertEquals(1, reference.getId());
            assertFalse("Detached reference must not have target state before fetch",
                    factory.getPersistenceUnitUtil().isLoaded(reference, "label"));
            assertSame(reference, agent.fetch(reference));
            assertTrue(factory.getPersistenceUnitUtil().isLoaded(reference, "label"));
            assertEquals("target-1", reference.getLabel());
        }
    }

    private static void assertFactoryIsolation(FetchType firstPolicy, FetchType secondPolicy) throws Exception {
        PersistenceConfiguration configuration = configuration(identity("opposite-defaults"), firstPolicy);
        try (EntityManagerFactory first = programmatic(configuration)) {
            assertOwnerPolicy(first, firstPolicy);
            // Mutating the caller's configuration must not mutate the first factory's metadata snapshot.
            configuration.defaultToOneFetchType(secondPolicy);
            try (EntityManagerFactory second = programmatic(configuration)) {
                assertNotSame(session(first), session(second));
                assertNotEquals(session(first).getName(), session(second).getName());
                assertOwnerPolicy(second, secondPolicy);
                assertOwnerPolicy(first, firstPolicy);
            }
            assertOwnerPolicy(first, firstPolicy);
            try (EntityManagerFactory reopened = programmatic(configuration)) {
                assertNotSame(session(first), session(reopened));
                assertOwnerPolicy(reopened, secondPolicy);
                assertOwnerPolicy(first, firstPolicy);
            }
        }
        configuration.defaultToOneFetchType(firstPolicy);
        try (EntityManagerFactory recreated = programmatic(configuration)) {
            assertOwnerPolicy(recreated, firstPolicy);
        }
    }

    private static void assertOrmPolicy(FetchType policy) throws Exception {
        try (EntityManagerFactory factory = programmatic(configuration(identity("orm"), policy).mappingFile(ORM))) {
            assertOwnerPolicy(factory, policy);
            try (EntityManager manager = factory.createEntityManager()) {
                DefaultFetchOwner owner = manager.find(DefaultFetchOwner.class, 1);
                assertAssociation(factory, owner, "xmlLazy", false);
                assertAssociation(factory, owner, "xmlEager", true);
                assertEquals("target-9", owner.getXmlLazy().getLabel());
                assertEquals("target-10", owner.getXmlEager().getLabel());
            }
        }
    }

    private static void assertContainerPolicy(FetchType policy) throws Exception {
        PersistenceConfiguration configuration = configuration(identity("container"), null);
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo(configuration,
                TestDefaultToOneFetch.class.getProtectionDomain().getCodeSource().getLocation()) {
            @Override
            public FetchType getDefaultToOneFetchType() {
                return policy;
            }
        };
        info.setNewTempClassLoader(TestDefaultToOneFetch.class.getClassLoader());
        boolean container = JavaSECMPInitializer.isInContainer();
        try (EntityManagerFactory factory = new PersistenceProvider().createContainerEntityManagerFactory(info, properties())) {
            assertOwnerPolicy(factory, policy == null ? FetchType.EAGER : policy);
        } finally {
            JavaSECMPInitializer.setIsInContainer(container);
        }
    }

    private static void assertOwnerPolicy(EntityManagerFactory factory, FetchType policy) throws Exception {
        try (EntityManager manager = factory.createEntityManager()) {
            DefaultFetchOwner owner = manager.find(DefaultFetchOwner.class, 1);
            assertNotNull(owner);
            for (String attribute : DEFAULT_ATTRIBUTES) {
                assertAssociation(factory, owner, attribute, policy == FetchType.EAGER);
            }
            assertAssociation(factory, owner, "lazyMany", false);
            assertAssociation(factory, owner, "lazyOne", false);
            assertAssociation(factory, owner, "eagerMany", true);
            assertAssociation(factory, owner, "eagerOne", true);
            assertTrue("Basic default remains eager", factory.getPersistenceUnitUtil().isLoaded(owner, "label"));
            assertFalse("Element collections retain their lazy default", factory.getPersistenceUnitUtil().isLoaded(owner, "tags"));
            assertFalse("To-many mappings retain their lazy default", factory.getPersistenceUnitUtil().isLoaded(owner, "targets"));
            // Inspect loaded state first; only then navigate through the ordinary woven accessors.
            List<String> attributes = List.of("omittedMany", "defaultMany", "omittedOne", "defaultOne",
                    "lazyMany", "eagerMany", "lazyOne", "eagerOne");
            for (int index = 0; index < attributes.size(); index++) {
                String attribute = attributes.get(index);
                String getter = "get" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
                DefaultFetchTarget target = (DefaultFetchTarget) DefaultFetchOwner.class.getMethod(getter).invoke(owner);
                assertEquals("target-" + (index + 1), target.getLabel());
                assertTrue("Navigation initializes " + attribute, factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
            }
            assertEquals("owner", owner.getLabel());
            assertEquals(List.of("tag"), owner.getTags());
            assertEquals("target-11", owner.getTargets().getFirst().getLabel());
        }
    }

    private static void assertPropertyPolicy(EntityManagerFactory factory, FetchType policy) throws Exception {
        try (EntityManager manager = factory.createEntityManager()) {
            DefaultFetchPropertyOwner owner = manager.find(DefaultFetchPropertyOwner.class, 1);
            assertNotNull(owner);
            for (String attribute : List.of("inheritedTarget", "many", "one")) {
                assertAssociation(factory, owner, attribute, policy == FetchType.EAGER);
            }
            assertEquals("target-12", owner.getInheritedTarget().getLabel());
            assertEquals("target-13", owner.getMany().getLabel());
            assertEquals("target-14", owner.getOne().getLabel());
            for (String attribute : List.of("inheritedTarget", "many", "one")) {
                assertTrue(factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
            }
        }
    }

    private static void assertAssociation(EntityManagerFactory factory, Object owner, String attribute,
            boolean loaded) throws Exception {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(factory)
                .getDescriptor(owner.getClass()).getMappingForAttributeName(attribute);
        assertEquals("Mapping policy for " + attribute, !loaded, mapping.isLazy());
        assertEquals("Loaded state for " + attribute, loaded,
                factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
        if (!loaded) {
            assertTrue("Lazy mapping must use indirection: " + attribute, mapping.usesIndirection());
            Field field = holderField(owner.getClass(), attribute);
            field.setAccessible(true);
            Object holder = field.get(owner);
            assertTrue("Actual field must contain a value holder: " + attribute, holder instanceof ValueHolderInterface);
            assertFalse("Value holder must remain uninstantiated: " + attribute,
                    ((ValueHolderInterface<?>) holder).isInstantiated());
        }
    }

    private static void assertHolderField(Class<?> type, String attribute) throws Exception {
        assertTrue("Static weaving must provide to-one indirection for " + type.getSimpleName() + "." + attribute,
                ValueHolderInterface.class.isAssignableFrom(holderField(type, attribute).getType()));
    }

    private static Field holderField(Class<?> type, String attribute) throws NoSuchFieldException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField("_persistence_" + attribute + "_vh");
            } catch (NoSuchFieldException ignored) {
                // Property-access mappings may be declared by a mapped superclass.
            }
        }
        throw new NoSuchFieldException(type.getName() + "." + attribute + " has no woven value-holder field");
    }

    private static EntityManagerFactory xmlFactory(String name) {
        assertNotNull(TestDefaultToOneFetch.class.getClassLoader().getResource(XML));
        Map<String, Object> properties = properties();
        properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, XML);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(name, properties);
        assertNotNull(factory);
        return factory;
    }

    private static PersistenceConfiguration configuration(String name, FetchType policy) {
        PersistenceConfiguration configuration = new PersistenceConfiguration(name)
                .provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .sharedCacheMode(SharedCacheMode.NONE)
                .managedClass(DefaultFetchOwner.class)
                .managedClass(DefaultFetchTarget.class)
                .managedClass(DefaultFetchPropertyOwner.class)
                .managedClass(DefaultFetchMappedSuperclass.class)
                .properties(properties());
        if (policy != null) {
            configuration.defaultToOneFetchType(policy);
        }
        return configuration;
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        // The ordinary test harness and the external database matrix supply these settings.
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

    private static EntityManagerFactory programmatic(PersistenceConfiguration configuration) {
        EntityManagerFactory factory = configuration.createEntityManagerFactory();
        assertNotNull(factory);
        return factory;
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static String identity(String kind) {
        return "default-to-one-fetch-" + kind + "-" + IDENTITIES.incrementAndGet();
    }

    private static boolean exists(String name) {
        TableDefinition table = new TableDefinition();
        table.setName(name);
        return schema.checkTableExists(table);
    }
}
