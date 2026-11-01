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
package org.eclipse.persistence.jpa.fetchoverrides;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.fetchoverrides.model.DuplicateUnqualifiedFetchBasic;
import org.eclipse.persistence.jpa.fetchoverrides.model.DuplicateUnqualifiedFetchOwner;
import org.eclipse.persistence.jpa.fetchoverrides.model.UnqualifiedFetchBase;
import org.eclipse.persistence.jpa.fetchoverrides.model.UnqualifiedFetchDefaultOwner;
import org.eclipse.persistence.jpa.fetchoverrides.model.UnqualifiedFetchOwner;
import org.eclipse.persistence.jpa.fetchoverrides.model.UnqualifiedFetchPropertyOwner;
import org.eclipse.persistence.jpa.fetchoverrides.model.UnqualifiedFetchTarget;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Unqualified Fetch.type overrides through real woven mappings and ordinary reads. */
public class TestUnqualifiedFetchOverrides {
    private static final AtomicLong IDENTITIES = new AtomicLong();
    private static final String RESOURCES = "META-INF/unqualified-fetch-overrides/";
    private static final List<String> TABLES = List.of("JPA_UF_OTM_EAGER", "JPA_UF_OTM_LAZY",
            "JPA_UF_MTM_EAGER", "JPA_UF_MTM_LAZY", "JPA_UF_TAG_EAGER", "JPA_UF_TAG_LAZY",
            "JPA_UF_OWNER", "JPA_UF_PROPERTY", "JPA_UF_TARGET");
    private static final Set<String> OWNER_TO_ONES = Set.of("eagerMany", "lazyMany", "eagerOne", "lazyOne",
            "defaultMany", "defaultOverrideMany", "defaultOverrideOne", "namedOnlyMany",
            "plainEagerMany", "repeatedMany");
    private static EntityManagerFactory fixture;
    private static SchemaManager schema;
    private static boolean ownsSchema;

    @BeforeClass
    public static void createFixture() throws Exception {
        assertTrue("Basic laziness requires actual fetch-group weaving",
                FetchGroupTracker.class.isAssignableFrom(UnqualifiedFetchOwner.class));
        for (String attribute : OWNER_TO_ONES) {
            assertHolder(UnqualifiedFetchOwner.class, attribute);
        }
        for (String attribute : List.of("eagerMany", "lazyMany", "eagerOne", "lazyOne")) {
            assertHolder(UnqualifiedFetchDefaultOwner.class, attribute);
        }
        assertHolder(UnqualifiedFetchPropertyOwner.class, "eagerTarget");
        assertHolder(UnqualifiedFetchPropertyOwner.class, "lazyTarget");
        assertHolder(UnqualifiedFetchBase.class, "inheritedTarget");
        try {
            fixture = configuration("fixture", FetchType.EAGER, false).createEntityManagerFactory();
            schema = new SchemaManager(session(fixture));
            for (String table : TABLES) {
                assertFalse("Refusing to acquire an existing fixture table: " + table, exists(table));
            }
            ownsSchema = true;
            schema.createDefaultTables(false);
            for (String table : TABLES) {
                assertTrue("Provider DDL must create " + table, exists(table));
            }
            try (EntityManager manager = fixture.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    List<UnqualifiedFetchTarget> targets = new ArrayList<>();
                    for (int id = 1; id <= 28; id++) {
                        UnqualifiedFetchTarget target = new UnqualifiedFetchTarget(id);
                        manager.persist(target);
                        targets.add(target);
                    }
                    manager.persist(new UnqualifiedFetchOwner(1, targets));
                    manager.persist(new UnqualifiedFetchPropertyOwner(1,
                            targets.get(25), targets.get(26), targets.get(27)));
                    manager.getTransaction().commit();
                } finally {
                    if (manager.getTransaction().isActive()) {
                        manager.getTransaction().rollback();
                    }
                }
            }
            fixture.getCache().evictAll();
        } catch (Exception | AssertionError failure) {
            try {
                removeFixture();
            } catch (Exception | AssertionError cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
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
            ownsSchema = false;
            if (fixture != null && fixture.isOpen()) {
                fixture.close();
            }
        }
    }

    @Test
    public void eagerFetchOverridesLazyManyToOne() throws Exception {
        assertRead("eagerMany", true, "target-1");
    }

    @Test
    public void lazyFetchOverridesEagerManyToOne() throws Exception {
        assertRead("lazyMany", false, "target-2");
    }

    @Test
    public void eagerFetchOverridesLazyOneToOne() throws Exception {
        assertRead("eagerOne", true, "target-3");
    }

    @Test
    public void lazyFetchOverridesEagerOneToOne() throws Exception {
        assertRead("lazyOne", false, "target-4");
    }

    @Test
    public void eagerFetchOverridesLazyBasic() throws Exception {
        assertRead("eagerText", true, "eager-text");
    }

    @Test
    public void lazyFetchOverridesEagerBasic() throws Exception {
        assertRead("lazyText", false, "lazy-text");
    }

    @Test
    public void eagerFetchOverridesLazyOneToMany() throws Exception {
        assertRead("eagerOneMany", true, List.of("target-20"));
    }

    @Test
    public void lazyFetchOverridesEagerOneToMany() throws Exception {
        assertRead("lazyOneMany", false, List.of("target-21"));
    }

    @Test
    public void eagerFetchOverridesLazyManyToMany() throws Exception {
        assertRead("eagerManyMany", true, List.of("target-22"));
    }

    @Test
    public void lazyFetchOverridesEagerManyToMany() throws Exception {
        assertRead("lazyManyMany", false, List.of("target-23"));
    }

    @Test
    public void eagerFetchOverridesLazyElementCollection() throws Exception {
        assertRead("eagerTags", true, List.of("eager-tag"));
    }

    @Test
    public void lazyFetchOverridesEagerElementCollection() throws Exception {
        assertRead("lazyTags", false, List.of("lazy-tag"));
    }

    @Test
    public void propertyAccessHonorsBothOverrides() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            UnqualifiedFetchPropertyOwner owner = manager.find(UnqualifiedFetchPropertyOwner.class, 1);
            assertState(fixture, owner, "eagerTarget", true);
            assertState(fixture, owner, "lazyTarget", false);
            assertEquals("target-27", owner.getEagerTarget().getLabel());
            assertEquals("target-28", owner.getLazyTarget().getLabel());
            assertState(fixture, owner, "lazyTarget", true);
        }
    }

    @Test
    public void mappedSuperclassPropertyFetchIsInherited() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            UnqualifiedFetchPropertyOwner owner = manager.find(UnqualifiedFetchPropertyOwner.class, 1);
            assertState(fixture, owner, "inheritedTarget", true);
            assertEquals("target-26", owner.getInheritedTarget().getLabel());
        }
    }

    @Test
    public void namedGraphOnlyFetchDoesNotChangePlainFind() throws Exception {
        assertRead("namedOnlyMany", false, "target-8");
    }

    @Test
    public void repeatedNamedAndUnqualifiedFetchRemainIndependent() throws Exception {
        assertRead("repeatedMany", true, "target-10");
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<UnqualifiedFetchOwner> graph =
                    manager.getEntityGraph(UnqualifiedFetchOwner.class, "UfNamedOnly");
            assertTrue(graph.getAttributeNode("namedOnlyMany").getOptions().contains(FetchType.EAGER));
            assertTrue(graph.getAttributeNode("repeatedMany").getOptions().contains(FetchType.LAZY));
        }
    }

    @Test
    public void absentFetchRetainsEagerUnitDefaultAndExplicitMappings() throws Exception {
        assertUnitDefaults(FetchType.EAGER);
    }

    @Test
    public void absentFetchRetainsLazyUnitDefaultAndExplicitMappings() throws Exception {
        assertUnitDefaults(FetchType.LAZY);
    }

    @Test
    public void fetchDefaultResolvesToEagerUnitPolicy() throws Exception {
        assertDefaultOverride(FetchType.EAGER);
    }

    @Test
    public void fetchDefaultResolvesToLazyUnitPolicy() throws Exception {
        assertDefaultOverride(FetchType.LAZY);
    }

    @Test
    public void explicitOverridesAreIndependentOfUnitDefault() throws Exception {
        for (FetchType policy : List.of(FetchType.EAGER, FetchType.LAZY)) {
            try (EntityManagerFactory factory = configuration("explicit", policy, false).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
                assertState(factory, owner, "eagerMany", true);
                assertState(factory, owner, "lazyMany", false);
                assertState(factory, owner, "eagerOne", true);
                assertState(factory, owner, "lazyOne", false);
            }
        }
    }

    @Test
    public void eagerFetchOverridesDefaultMappingsWithLazyUnitPolicy() throws Exception {
        assertOverridesOnDefaultMappings(FetchType.LAZY);
    }

    @Test
    public void lazyFetchOverridesDefaultMappingsWithEagerUnitPolicy() throws Exception {
        assertOverridesOnDefaultMappings(FetchType.EAGER);
    }

    @Test
    public void warmSharedCacheRetainsFetchPolicies() throws Exception {
        try (EntityManagerFactory factory = configuration("warm-cache", FetchType.EAGER, true).createEntityManagerFactory()) {
            factory.getCache().evictAll();
            for (int read = 0; read < 2; read++) {
                try (EntityManager manager = factory.createEntityManager()) {
                    UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
                    assertState(factory, owner, "eagerMany", true);
                    assertState(factory, owner, "lazyMany", false);
                    assertState(factory, owner, "eagerOne", true);
                    assertState(factory, owner, "lazyOne", false);
                    assertState(factory, owner, "eagerText", true);
                    assertState(factory, owner, "lazyText", false);
                }
                assertTrue("The second read must have a populated shared cache",
                        factory.getCache().contains(UnqualifiedFetchOwner.class, 1));
            }
        }
    }

    @Test
    public void clearingPersistenceContextRestoresUninitializedLazyAttributes() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            UnqualifiedFetchOwner first = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(fixture, first, "lazyMany", false);
            assertEquals("target-2", first.getLazyMany().getLabel());
            assertEquals("lazy-text", first.getLazyText());
            manager.clear();
            UnqualifiedFetchOwner second = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(fixture, second, "lazyMany", false);
            assertState(fixture, second, "lazyText", false);
        }
    }

    @Test
    public void jpqlEntityReadsHonorDefaultFetchOverrides() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.createQuery(
                    "SELECT o FROM UnqualifiedFetchOwner o WHERE o.id = :id", UnqualifiedFetchOwner.class)
                    .setParameter("id", 1).getSingleResult();
            assertState(fixture, owner, "eagerMany", true);
            assertState(fixture, owner, "lazyMany", false);
            assertState(fixture, owner, "eagerText", true);
            assertState(fixture, owner, "lazyText", false);
        }
    }

    @Test
    public void emptyLoadGraphRetainsUnqualifiedFetchDefaults() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<UnqualifiedFetchOwner> graph = manager.createEntityGraph(UnqualifiedFetchOwner.class);
            UnqualifiedFetchOwner owner = manager.find(graph, 1);
            assertState(fixture, owner, "eagerMany", true);
            assertState(fixture, owner, "lazyMany", false);
            assertState(fixture, owner, "eagerText", true);
            assertState(fixture, owner, "lazyText", false);
        }
    }

    @Test
    public void explicitXmlFetchOverridesFetchAnnotations() throws Exception {
        try (EntityManagerFactory factory = configuration("xml-explicit", FetchType.EAGER, false)
                .mappingFile(RESOURCES + "overrides-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(factory, owner, "eagerMany", false);
            assertState(factory, owner, "lazyMany", true);
            assertState(factory, owner, "eagerText", false);
            assertState(factory, owner, "lazyText", true);
            assertState(factory, owner, "eagerTags", false);
            assertState(factory, owner, "lazyTags", true);
            assertEquals("target-1", owner.getEagerMany().getLabel());
            assertEquals("eager-text", owner.getEagerText());
            assertEquals(List.of("eager-tag"), owner.getEagerTags());
        }
    }

    @Test
    public void omittedXmlFetchUsesUnitDefaultInsteadOfFetchAnnotation() throws Exception {
        for (FetchType policy : List.of(FetchType.LAZY, FetchType.EAGER)) {
            try (EntityManagerFactory factory = configuration("xml-omitted", policy, false)
                    .mappingFile(RESOURCES + "overrides-orm.xml").createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
                assertState(factory, owner, "repeatedMany", policy == FetchType.EAGER);
                assertEquals("target-10", owner.getRepeatedMany().getLabel());
            }
        }
    }

    @Test
    public void xmlEntityWithoutAttributeOverrideRetainsFetchAnnotations() throws Exception {
        try (EntityManagerFactory factory = configuration("xml-entity", FetchType.EAGER, false)
                .mappingFile(RESOURCES + "entity-only-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(factory, owner, "eagerMany", true);
            assertState(factory, owner, "lazyMany", false);
            assertState(factory, owner, "eagerText", true);
            assertState(factory, owner, "lazyText", false);
        }
    }

    @Test
    public void metadataCompleteDisablesPropertyAndInheritedFetchAnnotations() throws Exception {
        try (EntityManagerFactory factory = configuration("xml-complete", FetchType.LAZY, false)
                .mappingFile(RESOURCES + "complete-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchPropertyOwner owner = manager.find(UnqualifiedFetchPropertyOwner.class, 1);
            assertState(factory, owner, "inheritedTarget", false);
            assertState(factory, owner, "eagerTarget", false);
            assertState(factory, owner, "lazyTarget", false);
            assertEquals("target-26", owner.getInheritedTarget().getLabel());
            assertEquals("target-27", owner.getEagerTarget().getLabel());
            assertEquals("target-28", owner.getLazyTarget().getLabel());
        }
    }

    @Test
    public void duplicateUnqualifiedRelationshipFetchIsRejected() {
        assertDuplicateRejected(DuplicateUnqualifiedFetchOwner.class, "target", "duplicate-relationship-orm.xml");
    }

    @Test
    public void duplicateUnqualifiedBasicFetchIsRejected() {
        assertDuplicateRejected(DuplicateUnqualifiedFetchBasic.class, "label", "duplicate-basic-orm.xml");
    }

    private static void assertRead(String attribute, boolean loaded, Object expected) throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
            assertNotNull(owner);
            assertState(fixture, owner, attribute, loaded);
            Object actual = owner.getClass().getMethod("get" + Character.toUpperCase(attribute.charAt(0))
                    + attribute.substring(1)).invoke(owner);
            if (actual instanceof UnqualifiedFetchTarget target) {
                actual = target.getLabel();
            } else if (actual instanceof List<?> values) {
                actual = values.stream().map(value -> value instanceof UnqualifiedFetchTarget target
                        ? target.getLabel() : value).toList();
            }
            assertEquals(expected, actual);
            assertTrue("Navigation must load " + attribute, fixture.getPersistenceUnitUtil().isLoaded(owner, attribute));
        }
    }

    private static void assertUnitDefaults(FetchType policy) throws Exception {
        try (EntityManagerFactory factory = configuration("unit-default", policy, false).createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(factory, owner, "defaultMany", policy == FetchType.EAGER);
            assertState(factory, owner, "plainEagerMany", true);
            assertState(factory, owner, "namedOnlyMany", false);
        }
    }

    private static void assertDefaultOverride(FetchType policy) throws Exception {
        try (EntityManagerFactory factory = configuration("fetch-default", policy, false).createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchOwner owner = manager.find(UnqualifiedFetchOwner.class, 1);
            assertState(factory, owner, "defaultOverrideMany", policy == FetchType.EAGER);
            assertState(factory, owner, "defaultOverrideOne", policy == FetchType.EAGER);
            assertEquals("target-6", owner.getDefaultOverrideMany().getLabel());
            assertEquals("target-7", owner.getDefaultOverrideOne().getLabel());
        }
    }

    private static void assertOverridesOnDefaultMappings(FetchType policy) throws Exception {
        try (EntityManagerFactory factory = configuration("mapping-default", policy, false)
                .managedClass(UnqualifiedFetchDefaultOwner.class).createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            UnqualifiedFetchDefaultOwner owner = manager.find(UnqualifiedFetchDefaultOwner.class, 1);
            assertNotNull(owner);
            assertState(factory, owner, "eagerMany", true);
            assertState(factory, owner, "lazyMany", false);
            assertState(factory, owner, "eagerOne", true);
            assertState(factory, owner, "lazyOne", false);
            assertEquals("target-1", owner.getEagerMany().getLabel());
            assertEquals("target-2", owner.getLazyMany().getLabel());
            assertEquals("target-3", owner.getEagerOne().getLabel());
            assertEquals("target-4", owner.getLazyOne().getLabel());
            assertState(factory, owner, "lazyMany", true);
            assertState(factory, owner, "lazyOne", true);
        }
    }

    private static void assertState(EntityManagerFactory factory, Object owner, String attribute,
            boolean loaded) throws Exception {
        assertEquals("Loaded state for " + attribute, loaded,
                factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
        boolean toOne = owner instanceof UnqualifiedFetchOwner || owner instanceof UnqualifiedFetchDefaultOwner
                ? OWNER_TO_ONES.contains(attribute)
                : List.of("inheritedTarget", "eagerTarget", "lazyTarget").contains(attribute);
        if (toOne && !loaded) {
            ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(factory)
                    .getDescriptor(owner.getClass()).getMappingForAttributeName(attribute);
            assertTrue("Lazy mapping must use indirection: " + attribute, mapping.usesIndirection());
            Field field = holderField(owner.getClass(), attribute);
            field.setAccessible(true);
            ValueHolderInterface<?> holder = (ValueHolderInterface<?>) field.get(owner);
            assertNotNull("Lazy attribute must have an actual holder: " + attribute, holder);
            assertFalse("Lazy holder must not be initialized: " + attribute, holder.isInstantiated());
        }
        if ("lazyText".equals(attribute) && !loaded) {
            assertNotNull("Lazy basic field requires an active fetch group",
                    ((FetchGroupTracker) owner)._persistence_getFetchGroup());
            assertFalse(((FetchGroupTracker) owner)._persistence_getFetchGroup().containsAttribute(attribute));
        }
    }

    private static void assertDuplicateRejected(Class<?> invalid, String attribute, String mappingFile) {
        RuntimeException failure = assertThrows(RuntimeException.class, () -> {
            try (EntityManagerFactory factory = configuration("invalid", FetchType.EAGER, false)
                    .managedClass(invalid).mappingFile(RESOURCES + mappingFile)
                    .property(PersistenceUnitProperties.WEAVING, "false")
                    .createEntityManagerFactory()) {
                factory.getMetamodel();
            }
        });
        StringBuilder messages = new StringBuilder();
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            messages.append(cause.getMessage()).append('\n');
        }
        assertTrue("Diagnostic must identify the invalid declaring class: " + messages,
                messages.toString().contains(invalid.getSimpleName()));
        assertTrue("Diagnostic must identify the invalid attribute: " + messages,
                messages.toString().contains("Attribute [" + attribute + "]"));
        assertTrue("Diagnostic must identify duplicate unqualified Fetch declarations: " + messages,
                messages.toString().contains("more than one @Fetch annotation without a graph"));
        try (EntityManagerFactory factory = configuration("after-invalid", FetchType.EAGER, false)
                .createEntityManagerFactory()) {
            assertNotNull(factory.getMetamodel().entity(UnqualifiedFetchOwner.class));
        }
    }

    private static void assertHolder(Class<?> type, String attribute) throws Exception {
        assertTrue("Weaving must supply an actual to-one holder for " + type.getSimpleName() + "." + attribute,
                ValueHolderInterface.class.isAssignableFrom(holderField(type, attribute).getType()));
    }

    private static Field holderField(Class<?> type, String attribute) throws NoSuchFieldException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField("_persistence_" + attribute + "_vh");
            } catch (NoSuchFieldException ignored) {
                // The persistent accessor may be inherited from a mapped superclass.
            }
        }
        throw new NoSuchFieldException(type.getName() + "." + attribute + " has no woven value-holder field");
    }

    private static PersistenceConfiguration configuration(String kind, FetchType defaultFetch, boolean sharedCache) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, Boolean.toString(sharedCache));
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return new PersistenceConfiguration("unqualified-fetch-" + kind + "-" + IDENTITIES.incrementAndGet())
                .provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .sharedCacheMode(sharedCache ? SharedCacheMode.ALL : SharedCacheMode.NONE)
                .defaultToOneFetchType(defaultFetch)
                .managedClass(UnqualifiedFetchOwner.class)
                .managedClass(UnqualifiedFetchTarget.class)
                .managedClass(UnqualifiedFetchBase.class)
                .managedClass(UnqualifiedFetchPropertyOwner.class)
                .properties(properties);
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static boolean exists(String table) {
        TableDefinition definition = new TableDefinition();
        definition.setName(table);
        return schema.checkTableExists(definition);
    }
}
