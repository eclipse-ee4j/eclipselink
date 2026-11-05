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

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.sql.ConstructorMapping;
import jakarta.persistence.sql.EntityMapping;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaCache;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeBase;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeBook;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeDetail;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeInvalidProperty;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativePrivateQueries;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeProperty;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeQueries;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.EntityValue;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Ordinary;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Summary;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Text;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeTracker;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Portable public-API tests for method-declared native named queries. */
@RunWith(EmfRunner.class)
public class TestMethodNativeQuery {
    private static final List<Summary> SUMMARIES = List.of(new Summary(1, "Alpha"), new Summary(2, "Beta"));

    @Emf(name = "method-native-query", createTables = DDLGen.DROP_CREATE,
            classes = {MethodNativeBook.class, MethodNativeBase.class, MethodNativeDetail.class,
                    MethodNativeProperty.class, MethodNativeQueries.class, MethodNativePrivateQueries.class},
            properties = {@Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    @Property(name = "eclipselink.weaving", value = "false")})
    private EntityManagerFactory emf;

    @SQLCallListener(name = "method-native-query")
    private List<String> sql;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(manager -> {
            manager.persist(new MethodNativeBook(1, "Alpha", "first"));
            manager.persist(new MethodNativeBook(2, "Beta", "second"));
            manager.persist(new MethodNativeProperty(10, "Property"));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(manager -> {
            manager.createStatement("delete from MethodNativeBook").execute();
            manager.createStatement("delete from MethodNativeProperty").execute();
        });
        emf.getCache().evictAll();
    }

    private static String name(Class<?> declaringType, String method, Class<?>... parameters) {
        return declaringType.getCanonicalName() + "#" + method + "("
                + Arrays.stream(parameters).map(Class::getCanonicalName).collect(Collectors.joining(",")) + ")";
    }

    private static String name(String method, Class<?>... parameters) {
        return name(MethodNativeQueries.class, method, parameters);
    }

    private void withHandlers(Consumer<EntityHandler> assertions) {
        withHandlers(emf, assertions);
    }

    private static void withHandlers(EntityManagerFactory factory, Consumer<EntityHandler> assertions) {
        try (EntityManager manager = factory.createEntityManager()) {
            assertions.accept(manager);
        }
        try (EntityAgent agent = factory.createEntityAgent()) {
            assertions.accept(agent);
        }
    }

    @Test
    public void listReturnInfersRecordConstructor() {
        withHandlers(handler -> assertEquals(SUMMARIES, handler.createNamedQuery(name("summaries")).getResultList()));
    }

    @Test
    public void directReturnInfersRecordConstructor() {
        withHandlers(handler -> assertEquals(SUMMARIES.get(1),
                handler.createNamedQuery(name("summary", Integer.class)).setParameter(1, 2).getSingleResult()));
    }

    @Test
    public void streamReturnInfersRecordConstructor() {
        withHandlers(handler -> {
            try (var results = handler.createNamedQuery(name("summaryStream")).getResultStream()) {
                assertEquals(SUMMARIES, results.toList());
            }
        });
    }

    @Test
    public void ordinaryDtoConstructorReceivesOrderedValues() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("ordinaryResults")).getResultList();
            assertEquals(2, results.size());
            for (int i = 0; i < results.size(); i++) {
                Ordinary result = (Ordinary) results.get(i);
                assertEquals(SUMMARIES.get(i).id(), result.id);
                assertEquals(SUMMARIES.get(i).title(), result.title);
            }
        });
    }

    @Test
    public void overloadsUseRawParameterSignatures() {
        withHandlers(handler -> {
            assertEquals(SUMMARIES.get(0), handler.createNamedQuery(name("summary", Integer.class))
                    .setParameter(1, 1).getSingleResult());
            assertEquals(SUMMARIES.get(1), handler.createNamedQuery(name("summary", String.class))
                    .setParameter(1, "Beta").getSingleResult());
            assertEquals(SUMMARIES.get(0), handler.createNamedQuery(name("summary", int.class, String.class))
                    .setParameter(1, 1).setParameter(2, "Alpha").getSingleResult());
            assertThrows(IllegalArgumentException.class,
                    () -> handler.createNamedQuery(name("summary", Long.class)));
        });
    }

    @Test
    public void scalarAndPrimitiveScalarResultsAreNotStatements() {
        withHandlers(handler -> {
            assertEquals("Alpha", handler.createNamedQuery(name("title", Integer.class)).setParameter(1, 1).getSingleResult());
            assertEquals(List.of("Alpha", "Beta"), handler.createNamedQuery(name("titles")).getResultList());
            assertEquals(2, handler.createNamedQuery(name("count")).getSingleResult());
            assertEquals(2, handler.createNamedQuery(name("count")).ofType(Number.class).getSingleResult());
            JpaQuery<?> inferred = (JpaQuery<?>) handler.createNamedQuery(name("count"), int.class);
            assertEquals(2, inferred.ofType(Number.class).getSingleResult());
            assertThrows(IllegalArgumentException.class, () -> inferred.ofType(Double.class));
            JpaQuery<?> mapped = (JpaQuery<?>) handler.createNamedQuery(name("explicitPrimitive", Integer.class), int.class);
            mapped.setParameter(1, 1);
            assertEquals(1, mapped.ofType(Number.class).getSingleResult());
            assertEquals(1, (int) mapped.ofType(Integer.class).getSingleResult());
            assertEquals(1, (int) mapped.ofType(int.class).getSingleResult());
            assertThrows(IllegalArgumentException.class, () -> mapped.ofType(long.class));
        });
    }

    @Test
    public void arrayResultsPreserveTheSelectedColumns() {
        withHandlers(handler -> {
            Object[] one = (Object[]) handler.createNamedQuery(name("array", Integer.class)).setParameter(1, 2).getSingleResult();
            assertEquals(2, ((Number) one[0]).intValue());
            assertEquals("Beta", one[1]);
            List<?> results = handler.createNamedQuery(name("arrays")).getResultList();
            assertEquals(2, results.size());
            assertEquals("Alpha", ((Object[]) results.get(0))[1]);
            assertEquals("Beta", ((Object[]) results.get(1))[1]);
        });
    }

    @Test
    public void inferredEntitiesRetainManagedAndDetachedSemantics() {
        withHandlers(handler -> {
            MethodNativeBook book = (MethodNativeBook) handler.createNamedQuery(name("book", Integer.class))
                    .setParameter(1, 1).getSingleResult();
            assertEquals("Alpha", book.title);
            assertEquals("first", book.detail.note);
            if (handler instanceof EntityManager manager) {
                assertTrue(manager.contains(book));
            } else {
                book.title = "detached";
                assertEquals("Alpha", ((MethodNativeBook) handler.createNamedQuery(name("book", Integer.class))
                        .setParameter(1, 1).getSingleResult()).title);
            }
            assertEquals(2, handler.createNamedQuery(name("books")).getResultList().size());
        });
    }

    @Test
    public void explicitConstructorMappingOverridesInferenceAndColumnOrder() {
        withHandlers(handler -> assertEquals(SUMMARIES,
                handler.createNamedQuery(name("explicitConstructor")).getResultList()));
    }

    @Test
    public void typedFactoriesRetainExplicitMappingsAndNestedLocks() {
        withHandlers(handler -> {
            assertEquals(SUMMARIES, handler.createNamedQuery(name("explicitConstructor"), Summary.class).getResultList());
            assertEquals(SUMMARIES, handler.createNamedQuery(name("explicitConstructor"), Object.class).getResultList());
            assertEquals(SUMMARIES, handler.createNamedQuery("methodNative.mappedOrdinary", Summary.class).getResultList());
            MethodNativeBook aliased = handler.createNamedQuery(name("aliasedEntity", Integer.class), MethodNativeBook.class)
                    .setParameter(1, 1).getSingleResult();
            assertEquals("Alpha", aliased.title);
            assertEquals("first", aliased.detail.note);
            EntityValue nested = handler.createNamedQuery(name("nestedOne", Integer.class), EntityValue.class)
                    .setParameter(1, 1).getSingleResult();
            assertEquals("Alpha", nested.title());
            assertEquals(Integer.valueOf(1), nested.book().id);
            if (handler instanceof EntityManager manager) {
                assertTrue(manager.contains(nested.book()));
            }
            assertThrows(IllegalArgumentException.class,
                    () -> handler.createNamedQuery(name("explicitConstructor"), Ordinary.class));
            var locked = handler.createNamedQuery(name("nestedLocked", Integer.class), EntityValue.class).setParameter(1, 1);
            assertThrows(TransactionRequiredException.class, locked::getSingleResult);
            handler.getTransaction().begin();
            try {
                assertEquals("Alpha", locked.getSingleResult().book().title);
            } finally {
                handler.getTransaction().rollback();
            }
        });
    }

    @Test
    public void typedFactoriesRetainXmlMappings() {
        try (Unit unit = new Unit(MethodNativeBook.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/method-native-query/typed-mappings.xml"));
            withHandlers(unit.create(), handler -> {
                assertEquals(SUMMARIES, handler.createNamedQuery("methodNative.xmlMapped", Summary.class).getResultList());
                assertEquals(SUMMARIES, handler.createNamedQuery("methodNative.xmlMapped", Object.class).getResultList());
                assertThrows(IllegalArgumentException.class,
                        () -> handler.createNamedQuery("methodNative.xmlMapped", Ordinary.class));
            });
        }
    }

    @Test
    public void repeatedColumnMappingsRetainDeclaredOrderAndTypes() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("explicitColumns")).getResultList();
            for (int i = 0; i < results.size(); i++) {
                Object[] row = (Object[]) results.get(i);
                assertEquals(2, row.length);
                assertEquals(SUMMARIES.get(i).id(), row[0]);
                assertEquals(SUMMARIES.get(i).title(), row[1]);
            }
            assertEquals(2, results.size());
        });
    }

    @Test
    public void explicitScalarMappingConvertsTheJdbcValue() {
        withHandlers(handler -> assertEquals(1L,
                handler.createNamedQuery(name("explicitScalar", Integer.class)).setParameter(1, 1).getSingleResult()));
    }

    @Test
    public void explicitEntityMappingUsesTheExistingEntityMaterializer() {
        withHandlers(handler -> {
            MethodNativeBook row = (MethodNativeBook) handler.createNamedQuery(name("explicitEntity", Integer.class))
                    .setParameter(1, 2).getSingleResult();
            assertEquals(Integer.valueOf(2), row.id);
            assertEquals("Beta", row.title);
            assertEquals("second", row.detail.note);
        });
    }

    @Test
    public void mixedMappingsUseEntityConstructorScalarCategoryOrder() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("mixed")).getResultList();
            assertEquals(2, results.size());
            for (int i = 0; i < results.size(); i++) {
                Object[] row = (Object[]) results.get(i);
                assertEquals(3, row.length);
                assertEquals(SUMMARIES.get(i).id(), ((MethodNativeBook) row[0]).id);
                assertEquals(SUMMARIES.get(i), row[1]);
                assertEquals(SUMMARIES.get(i).title(), row[2]);
            }
        });
    }

    @Test
    public void repeatedConstructorsRetainDeclarationOrder() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("constructors")).getResultList();
            assertEquals(2, results.size());
            for (int i = 0; i < results.size(); i++) {
                Object[] row = (Object[]) results.get(i);
                assertEquals(2, row.length);
                assertEquals(SUMMARIES.get(i), row[0]);
                assertEquals(new Text(SUMMARIES.get(i).title()), row[1]);
            }
        });
    }

    @Test
    public void repeatedEntitiesRetainBothResults() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("entities")).getResultList();
            assertEquals(2, results.size());
            for (int i = 0; i < results.size(); i++) {
                Object[] row = (Object[]) results.get(i);
                assertEquals(2, row.length);
                assertEquals(SUMMARIES.get(i).id(), ((MethodNativeBook) row[0]).id);
                assertEquals(SUMMARIES.get(i).id(), ((MethodNativeBook) row[1]).id);
            }
        });
    }

    @Test
    public void constructorEntityArgumentsFollowColumnArguments() {
        withHandlers(handler -> {
            List<?> results = handler.createNamedQuery(name("nestedEntity")).getResultList();
            assertEquals(2, results.size());
            for (int i = 0; i < results.size(); i++) {
                EntityValue row = (EntityValue) results.get(i);
                assertEquals(SUMMARIES.get(i).title(), row.title());
                assertEquals(SUMMARIES.get(i).id(), row.book().id);
            }
        });
    }

    @Test
    public void nestedEntityResultsAreScopedToEachExecutingHandler() {
        List<MethodNativeBook> previous = new ArrayList<>();
        for (boolean agentMode : List.of(false, true, false, true, false)) {
            try (EntityHandler handler = agentMode ? emf.createEntityAgent() : emf.createEntityManager()) {
                assertFalse(handler.getTransaction().isActive());
                var query = handler.createNamedQuery(name("nestedOne", Integer.class)).setParameter(1, 1);
                EntityValue first = (EntityValue) query.getSingleResult();
                assertEquals("Alpha", first.title());
                assertEquals("Alpha", first.book().title);
                for (MethodNativeBook prior : previous) {
                    assertNotSame(prior, first.book());
                }
                previous.add(first.book());
                if (handler instanceof EntityManager manager) {
                    assertTrue(manager.contains(first.book()));
                    assertSame(first.book(), manager.find(MethodNativeBook.class, 1));
                    assertSame(first.book(), ((EntityValue) query.getSingleResult()).book());
                } else {
                    first.book().title = "caller change";
                    first.book().detail.note = "caller note";
                    EntityValue second = (EntityValue) query.getSingleResult();
                    assertNotSame(first.book(), second.book());
                    assertNotSame(first.book().detail, second.book().detail);
                    assertEquals("Alpha", second.book().title);
                    assertEquals("first", second.book().detail.note);
                }
                assertFalse(handler.getTransaction().isActive());
            }
        }
    }

    @Test
    public void nestedEntityParametersAndOptionsRemainQueryLocal() {
        var names = Set.copyOf(emf.getNamedQueries(Object.class).keySet());
        withHandlers(handler -> {
            Integer defaultTimeout = handler.createNamedQuery(name("nestedOne", Integer.class)).getTimeout();
            var first = handler.createNamedQuery(name("nestedOne", Integer.class))
                    .setParameter(1, 1).setTimeout(6000).setQueryFlushMode(QueryFlushMode.NO_FLUSH);
            var sibling = handler.createNamedQuery(name("nestedOne", Integer.class))
                    .setParameter(1, 2).setTimeout(9000);
            assertEquals(Integer.valueOf(1), ((EntityValue) first.getSingleResult()).book().id);
            assertEquals(Integer.valueOf(2), ((EntityValue) sibling.getSingleResult()).book().id);
            assertEquals(Integer.valueOf(2), ((EntityValue) first.setParameter(1, 2).getSingleResult()).book().id);
            assertEquals(Integer.valueOf(1), ((EntityValue) first.setParameter(1, 1).getSingleResult()).book().id);
            assertEquals(Integer.valueOf(6000), first.getTimeout());
            assertEquals(Integer.valueOf(9000), sibling.getTimeout());
            assertEquals(QueryFlushMode.NO_FLUSH, first.getQueryFlushMode());
            assertEquals(QueryFlushMode.DEFAULT, sibling.getQueryFlushMode());
            assertEquals(defaultTimeout, handler.createNamedQuery(name("nestedOne", Integer.class)).getTimeout());
        });
        assertEquals(names, emf.getNamedQueries(Object.class).keySet());
    }

    @Test
    public void nestedEntityLocksRequireAnActiveTransaction() {
        withHandlers(handler -> {
            var query = handler.createNamedQuery(name("nestedLocked", Integer.class)).setParameter(1, 1);
            assertThrows(TransactionRequiredException.class, query::getSingleResult);
            handler.getTransaction().begin();
            try {
                EntityValue value = (EntityValue) query.getSingleResult();
                assertEquals("Alpha", value.title());
                assertEquals(Integer.valueOf(1), value.book().id);
                if (handler instanceof EntityManager manager) {
                    assertTrue(manager.contains(value.book()));
                }
            } finally {
                if (handler.getTransaction().isActive()) {
                    handler.getTransaction().rollback();
                }
            }
        });
    }

    @Test
    public void namedNestedEntityMappingRoundTrips() {
        var mapping = emf.getResultSetMappings(EntityValue.class).get("methodNative.nestedMapping");
        assertNotNull(mapping);
        assertTrue(mapping instanceof ConstructorMapping<?>);
        ConstructorMapping<?> constructor = (ConstructorMapping<?>) mapping;
        assertEquals(EntityValue.class, constructor.targetClass());
        assertEquals(2, constructor.arguments().length);
        assertEquals(String.class, constructor.arguments()[0].getJavaType());
        assertTrue(constructor.arguments()[1] instanceof EntityMapping<?>);
        assertEquals(MethodNativeBook.class, constructor.arguments()[1].getJavaType());
        withHandlers(handler -> {
            var query = handler.createNativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
                    .withResultSetMapping(mapping).setParameter(1, 1);
            EntityValue value = query.getSingleResult();
            assertEquals("Alpha", value.title());
            assertEquals(Integer.valueOf(1), value.book().id);
            if (handler instanceof EntityManager manager) {
                assertTrue(manager.contains(value.book()));
            } else {
                value.book().title = "caller change";
                assertEquals("Alpha", query.getSingleResult().book().title);
            }
        });
    }

    @Test
    public void emptyAndNonUniqueResultsRetainCardinalityContracts() {
        withHandlers(handler -> {
            var empty = handler.createNamedQuery(name("summary", Integer.class)).setParameter(1, 999);
            assertTrue(empty.getResultList().isEmpty());
            assertNull(empty.getSingleResultOrNull());
            assertThrows(NoResultException.class, empty::getSingleResult);
            assertThrows(NonUniqueResultException.class,
                    () -> handler.createNamedQuery(name("summaries")).getSingleResult());
        });
    }

    @Test
    public void parameterValuesAndNullBindingsDoNotLeakAcrossQueries() {
        var declaration = emf.unwrap(JpaEntityManagerFactory.class).getServerSession()
                .getQuery(name("summary", String.class));
        var declaredArguments = List.copyOf(declaration.getArguments());
        var declaredTypes = new ArrayList<>(declaration.getArgumentTypes());
        withHandlers(handler -> {
            var query = handler.createNamedQuery(name("summary", String.class));
            assertEquals(SUMMARIES.get(0), query.setParameter(1, "Alpha").getSingleResult());
            assertEquals(SUMMARIES.get(1), query.setParameter(1, "Beta").getSingleResult());
            assertTrue(query.setParameter(1, null).getResultList().isEmpty());
            assertEquals(SUMMARIES.get(0), query.setParameter(1, "Alpha").getSingleResult());
            assertEquals(SUMMARIES.get(1), handler.createNamedQuery(name("summary", String.class))
                    .setParameter(1, "Beta").getSingleResult());
        });
        assertEquals(declaredArguments, declaration.getArguments());
        assertEquals(declaredTypes, declaration.getArgumentTypes());
    }

    @Test
    public void queryOptionsAndOverridesRemainIsolated() {
        withHandlers(handler -> {
            var query = handler.createNamedQuery(name("options", Integer.class)).setParameter(1, 1);
            assertEquals(CacheRetrieveMode.BYPASS, query.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.REFRESH, query.getCacheStoreMode());
            assertEquals(Integer.valueOf(3000), query.getTimeout());
            assertEquals(QueryFlushMode.NO_FLUSH, query.getQueryFlushMode());
            assertEquals("7", String.valueOf(query.getHints().get("eclipselink.jdbc.fetch-size")));
            assertEquals("Alpha", query.getSingleResult());
            query.setTimeout(6000).setQueryFlushMode(QueryFlushMode.FLUSH)
                    .setCacheRetrieveMode(CacheRetrieveMode.USE).setCacheStoreMode(CacheStoreMode.BYPASS);
            var fresh = handler.createNamedQuery(name("options", Integer.class));
            assertEquals(Integer.valueOf(3000), fresh.getTimeout());
            assertEquals(QueryFlushMode.NO_FLUSH, fresh.getQueryFlushMode());
            assertEquals(CacheRetrieveMode.BYPASS, fresh.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.REFRESH, fresh.getCacheStoreMode());
        });
    }

    @Test
    public void presentOptionsDefaultsOverrideHandlerCacheModes() {
        withHandlers(handler -> {
            handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
            handler.setCacheStoreMode(CacheStoreMode.REFRESH);
            var absent = handler.createNamedQuery(name("title", Integer.class));
            assertEquals(CacheRetrieveMode.BYPASS, absent.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.REFRESH, absent.getCacheStoreMode());
            var present = handler.createNamedQuery(name("defaultOptions", Integer.class));
            assertEquals(CacheRetrieveMode.USE, present.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.USE, present.getCacheStoreMode());
            assertEquals("Alpha", present.setParameter(1, 1).getSingleResult());
            handler.setCacheRetrieveMode(CacheRetrieveMode.USE);
            handler.setCacheStoreMode(CacheStoreMode.BYPASS);
            assertEquals(CacheRetrieveMode.USE, absent.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.BYPASS, absent.getCacheStoreMode());
            assertEquals(CacheStoreMode.USE, present.getCacheStoreMode());
            assertEquals("Alpha", absent.setParameter(1, 1).getSingleResult());
            assertFalse(absent.getHints().containsKey("jakarta.persistence.cache.storeMode"));
        });
    }

    @Test
    public void dynamicQueriesUseCurrentHandlerModesAndExplicitQueryOptions() {
        withHandlers(handler -> {
            var nativeQuery = handler.createNativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1", String.class);
            var jpql = handler.createQuery("SELECT b.title FROM MethodNativeBook b WHERE b.id = ?1", String.class);
            for (var query : List.of(nativeQuery, jpql)) {
                handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                handler.setCacheStoreMode(CacheStoreMode.BYPASS);
                assertEquals(CacheRetrieveMode.BYPASS, query.getCacheRetrieveMode());
                assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                assertEquals("Alpha", query.setParameter(1, 1).getSingleResult());
                handler.setCacheStoreMode(CacheStoreMode.REFRESH);
                assertEquals(CacheStoreMode.REFRESH, query.getCacheStoreMode());
                query.setCacheStoreMode(CacheStoreMode.USE);
                query.setCacheRetrieveMode(CacheRetrieveMode.USE);
                assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                assertEquals("Beta", query.setParameter(1, 2).getSingleResult());
                query.setTimeout(1500);
                assertEquals(Integer.valueOf(1500), query.getTimeout());
                query.setTimeout((Integer) null);
                assertNull(query.getTimeout());
            }
        });
    }

    @Test
    public void cacheModesControlEntityReadsAndCacheWrites() {
        for (boolean agentMode : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try {
                    try (EntityManager manager = factory.createEntityManager()) {
                        assertEquals("Alpha", manager.find(MethodNativeBook.class, 1, CacheStoreMode.REFRESH).title);
                    }
                    assertEquals("Alpha", cachedBook(factory).title);
                    writeTitleOutsideCache(factory, "database");
                    assertEquals("Alpha", cachedBook(factory).title);
                    assertEquals("database", readCachedBook(factory, agentMode, "cachedBookBypass").title);
                    assertEquals("Alpha", cachedBook(factory).title);
                    assertEquals("database", readCachedBook(factory, agentMode, "cachedNestedBookBypass").title);
                    assertEquals("Alpha", cachedBook(factory).title);
                    assertEquals("database", readCachedBook(factory, agentMode, "cachedBookRefresh").title);
                    assertEquals("database", cachedBook(factory).title);
                    factory.getCache().evict(MethodNativeBook.class, 1);
                    assertEquals("database", readCachedBook(factory, agentMode, "cachedBookBypass").title);
                    assertFalse(factory.getCache().contains(MethodNativeBook.class, 1));
                    assertEquals("database", readCachedBook(factory, agentMode, "cachedNestedBookBypass").title);
                    assertFalse(factory.getCache().contains(MethodNativeBook.class, 1));
                    MethodNativeBook defaultResult = readCachedBook(factory, agentMode, "cachedBookDefault");
                    assertEquals("database", defaultResult.title);
                    defaultResult.title = "caller change";
                    // USE permits cache storage; it does not require it.
                    if (factory.getCache().contains(MethodNativeBook.class, 1)) {
                        assertEquals("database", cachedBook(factory).title);
                    }
                    readCachedBook(factory, agentMode, "cachedBookRefresh");
                    writeTitleOutsideCache(factory, "newer");
                    assertEquals("database", cachedBook(factory).title);
                    assertEquals("newer", readCachedBook(factory, agentMode, "cachedMappedBookRefresh").title);
                    assertEquals("newer", cachedBook(factory).title);
                    writeTitleOutsideCache(factory, "nested");
                    assertEquals("newer", cachedBook(factory).title);
                    assertEquals("nested", readCachedBook(factory, agentMode, "cachedNestedBookRefresh").title);
                    assertEquals("nested", cachedBook(factory).title);
                    try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                        var inherited = handler.createNamedQuery(name("book", Integer.class)).setParameter(1, 1);
                        handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                        handler.setCacheStoreMode(CacheStoreMode.BYPASS);
                        writeTitleOutsideCache(factory, "live default");
                        assertEquals("live default", ((MethodNativeBook) inherited.getSingleResult()).title);
                        assertEquals("nested", cachedBook(factory).title);
                        handler.setCacheStoreMode(CacheStoreMode.REFRESH);
                        if (handler instanceof EntityManager manager) {
                            manager.clear();
                        }
                        assertEquals("live default", ((MethodNativeBook) inherited.getSingleResult()).title);
                        assertEquals("live default", cachedBook(factory).title);
                        writeTitleOutsideCache(factory, "stream default");
                        if (handler instanceof EntityManager manager) {
                            manager.clear();
                        }
                        try (var stream = inherited.getResultStream()) {
                            assertEquals("stream default", ((MethodNativeBook) stream.findFirst().orElseThrow()).title);
                        }
                        assertEquals("stream default", cachedBook(factory).title);
                    }
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    @Test
    public void refreshDeclarationsCanBeOverriddenWithoutChangingSiblings() {
        for (boolean agentMode : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try {
                    for (String method : List.of("cachedBookRefresh", "cachedMappedBookRefresh", "cachedNestedBookRefresh", "jpql")) {
                        writeTitleOutsideCache(factory, "Alpha");
                        factory.getCache().evictAll();
                        readCachedBook(factory, agentMode, "cachedBookRefresh");
                        try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                            jakarta.persistence.Query query = "jpql".equals(method)
                                    ? handler.createQuery("SELECT b FROM MethodNativeBook b WHERE b.id = ?1", MethodNativeBook.class)
                                            .setCacheStoreMode(CacheStoreMode.REFRESH)
                                    : handler.createNamedQuery(name(method, Integer.class));
                            query.setParameter(1, 1);
                            query.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                            for (int attempt = 0; attempt < 2; attempt++) {
                                String cached = cachedBook(factory).title;
                                String updated = method + attempt;
                                writeTitleOutsideCache(factory, updated);
                                query.setCacheStoreMode(CacheStoreMode.BYPASS);
                                if (handler instanceof EntityManager manager) {
                                    manager.clear();
                                }
                                Object value = query.getSingleResult();
                                MethodNativeBook book = value instanceof EntityValue nested ? nested.book() : (MethodNativeBook) value;
                                assertEquals(updated, book.title);
                                assertEquals(cached, cachedBook(factory).title);
                                assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                                query.setCacheStoreMode(CacheStoreMode.USE);
                                assertFalse(((ObjectBuildingQuery) query.unwrap(JpaQuery.class).getDatabaseQuery())
                                        .shouldRefreshIdentityMapResult());
                                query.setCacheStoreMode(CacheStoreMode.REFRESH);
                                if (handler instanceof EntityManager manager) {
                                    manager.clear();
                                }
                                query.getSingleResult();
                                assertEquals(updated, cachedBook(factory).title);
                            }
                            if (!"jpql".equals(method)) {
                                assertEquals(CacheStoreMode.REFRESH,
                                        handler.createNamedQuery(name(method, Integer.class)).getCacheStoreMode());
                            }
                        }
                    }
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    @Test
    public void legacyCacheHintsOverrideHandlerRefreshForMappedEntities() {
        for (boolean agentMode : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try {
                    for (String hint : List.of(QueryHints.CACHE_STORE_MODE, "jakarta.persistence.cacheStoreMode")) {
                        for (CacheStoreMode mode : List.of(CacheStoreMode.BYPASS, CacheStoreMode.USE)) {
                            writeTitleOutsideCache(factory, "Alpha");
                            factory.getCache().evictAll();
                            readCachedBook(factory, agentMode, "cachedBookRefresh");
                            writeTitleOutsideCache(factory, "database");
                            try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                                handler.setCacheStoreMode(CacheStoreMode.REFRESH);
                                var query = handler.createNamedQuery(name("explicitEntity", Integer.class))
                                        .setParameter(1, 1).setHint(hint, mode.name());
                                query.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                                assertEquals(mode, query.getCacheStoreMode());
                                assertEquals("database", ((MethodNativeBook) query.getSingleResult()).title);
                                if (mode == CacheStoreMode.BYPASS || agentMode) {
                                    // Agent USE retains its existing uncached-read policy without forcing refresh.
                                    assertEquals("Alpha", cachedBook(factory).title);
                                }
                            }
                        }
                    }
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    @Test
    public void cacheAliasesOverrideNamedModesWithoutRewritingRawHints() {
        String legacyStore = "jakarta.persistence.cacheStoreMode";
        String legacyRetrieve = "jakarta.persistence.cacheRetrieveMode";
        for (boolean agentMode : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try {
                    for (String method : List.of("cachedBookRefresh", "cachedMappedBookRefresh", "cachedNestedBookRefresh", "jpql")) {
                        writeTitleOutsideCache(factory, "Alpha");
                        factory.getCache().evictAll();
                        readCachedBook(factory, agentMode, "cachedBookRefresh");
                        try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                            jakarta.persistence.Query query = "jpql".equals(method)
                                    ? handler.createQuery("SELECT b FROM MethodNativeBook b WHERE b.id = ?1", MethodNativeBook.class)
                                            .setCacheStoreMode(CacheStoreMode.REFRESH)
                                    : handler.createNamedQuery(name(method, Integer.class));
                            query.setParameter(1, 1);
                            Object declaredStore = query.getHints().get(QueryHints.CACHE_STORE_MODE);
                            query.setHint(legacyStore, "BYPASS");
                            assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                            assertEquals(declaredStore, query.getHints().get(QueryHints.CACHE_STORE_MODE));
                            assertEquals("BYPASS", query.getHints().get(legacyStore));
                            query.setCacheRetrieveMode(CacheRetrieveMode.USE);
                            query.setHint(legacyRetrieve, "BYPASS");
                            assertEquals(CacheRetrieveMode.BYPASS, query.getCacheRetrieveMode());
                            assertEquals(CacheRetrieveMode.USE, query.getHints().get(QueryHints.CACHE_RETRIEVE_MODE));
                            assertEquals("BYPASS", query.getHints().get(legacyRetrieve));
                            query.getHints().put(legacyStore, CacheStoreMode.REFRESH);
                            assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                            for (int attempt = 0; attempt < 2; attempt++) {
                                String cached = cachedBook(factory).title;
                                String updated = method + attempt;
                                writeTitleOutsideCache(factory, updated);
                                query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
                                query.setHint(legacyStore, CacheStoreMode.BYPASS);
                                assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                                if (handler instanceof EntityManager manager) {
                                    manager.clear();
                                }
                                Object value = query.getSingleResult();
                                MethodNativeBook book = value instanceof EntityValue nested ? nested.book() : (MethodNativeBook) value;
                                assertEquals(updated, book.title);
                                assertEquals(cached, cachedBook(factory).title);
                                query.setHint(legacyStore, "REFRESH");
                                query.setHint(QueryHints.CACHE_STORE_MODE, "BYPASS");
                                assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                                query.setCacheStoreMode(CacheStoreMode.REFRESH);
                                if (handler instanceof EntityManager manager) {
                                    manager.clear();
                                }
                                query.getSingleResult();
                                assertEquals(updated, cachedBook(factory).title);
                            }
                            query.setHint(legacyStore, "");
                            query.setHint(legacyRetrieve, "");
                            assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                            assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                            assertEquals("", query.getHints().get(legacyStore));
                            assertEquals("", query.getHints().get(legacyRetrieve));
                            assertFalse(((ObjectBuildingQuery) query.unwrap(JpaQuery.class).getDatabaseQuery())
                                    .shouldRefreshIdentityMapResult());
                            handler.setCacheStoreMode(CacheStoreMode.REFRESH);
                            handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                            assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                            assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                            String cached = cachedBook(factory).title;
                            writeTitleOutsideCache(factory, "empty-default");
                            if (handler instanceof EntityManager manager) {
                                manager.clear();
                            }
                            Object value = query.getSingleResult();
                            if (agentMode) {
                                MethodNativeBook book = value instanceof EntityValue nested ? nested.book() : (MethodNativeBook) value;
                                assertEquals("empty-default", book.title);
                                assertEquals(cached, cachedBook(factory).title);
                            }
                            if (!"jpql".equals(method)) {
                                assertEquals(CacheStoreMode.REFRESH,
                                        handler.createNamedQuery(name(method, Integer.class)).getCacheStoreMode());
                            }
                        }
                    }
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    @Test
    public void ignoredCacheHintsDoNotReplaceAcceptedModesOrBlockLiveDefaults() {
        for (boolean agentMode : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try {
                    readCachedBook(factory, agentMode, "cachedBookRefresh");
                    try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                        var query = handler.createNamedQuery(name("explicitEntity", Integer.class)).setParameter(1, 1);
                        query.setHint(QueryHints.CACHE_STORE_MODE, "unknown");
                        query.setHint("jakarta.persistence.cacheRetrieveMode", "bypass");
                        handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                        handler.setCacheStoreMode(CacheStoreMode.BYPASS);
                        assertEquals(CacheRetrieveMode.BYPASS, query.getCacheRetrieveMode());
                        assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                        writeTitleOutsideCache(factory, "ignored-default");
                        assertEquals("ignored-default", ((MethodNativeBook) query.getSingleResult()).title);
                        assertEquals("Alpha", cachedBook(factory).title);
                        handler.setCacheStoreMode(CacheStoreMode.REFRESH);
                        if (handler instanceof EntityManager manager) {
                            manager.clear();
                        }
                        assertEquals(CacheStoreMode.REFRESH, query.getCacheStoreMode());
                        query.getSingleResult();
                        assertEquals("ignored-default", cachedBook(factory).title);
                        assertEquals("unknown", query.getHints().get(QueryHints.CACHE_STORE_MODE));
                        query.setCacheStoreMode(CacheStoreMode.BYPASS);
                        query.setHint("jakarta.persistence.cacheStoreMode", "unknown");
                        assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
                        writeTitleOutsideCache(factory, "ignored-explicit");
                        if (handler instanceof EntityManager manager) {
                            manager.clear();
                        }
                        assertEquals("ignored-explicit", ((MethodNativeBook) query.getSingleResult()).title);
                        assertEquals("ignored-default", cachedBook(factory).title);
                        query.setHint("jakarta.persistence.cacheStoreMode", "");
                        assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                        query.getSingleResult();
                    }
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    @Test
    public void nativeResultAdaptationsPreserveBindingsLimitsAndOptions() {
        withHandlers(handler -> {
            for (String adaptation : List.of("control", "ofType", "mapping", "named", "queryType")) {
                var source = adaptation.equals("control")
                        ? handler.createNativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID >= ?1 ORDER BY ID", MethodNativeBook.class)
                        : handler.createNativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID >= ?1 ORDER BY ID");
                source.setParameter(1, 1);
                source.setFirstResult(1);
                source.setMaxResults(1);
                source.setTimeout(2500);
                source.setQueryFlushMode(QueryFlushMode.NO_FLUSH);
                jakarta.persistence.Query query = switch (adaptation) {
                    case "control" -> source;
                    case "ofType" -> ((jakarta.persistence.StatementOrTypedQuery) source).ofType(MethodNativeBook.class);
                    case "mapping" -> ((jakarta.persistence.StatementOrTypedQuery) source).withResultSetMapping(jakarta.persistence.sql.ResultSetMapping.entity(MethodNativeBook.class));
                    case "named" -> {
                        emf.addNamedQuery("methodNative.dynamicSettings", source);
                        var named = handler.createNamedQuery("methodNative.dynamicSettings", MethodNativeBook.class);
                        named.setParameter(1, 1);
                        yield named;
                    }
                    case "queryType" -> source.setHint(QueryHints.QUERY_TYPE, NativeBookReadQuery.class.getName());
                    default -> throw new AssertionError(adaptation);
                };
                assertEquals(1, query.getFirstResult());
                assertEquals(1, query.getMaxResults());
                assertEquals(Integer.valueOf(2500), query.getTimeout());
                assertEquals(QueryFlushMode.NO_FLUSH, query.getQueryFlushMode());
                assertEquals("Beta", ((MethodNativeBook) query.getSingleResult()).title);
                assertEquals(1, query.getParameterValue(1));
                query.setParameter(1, 2);
                assertTrue(adaptation, query.getResultList().isEmpty());
                assertEquals(2, query.getParameterValue(1));
                query.setParameter(1, 1);
                assertEquals(adaptation, "Beta", ((MethodNativeBook) query.getSingleResult()).title);
            }
        });
    }

    @Test
    public void entityManagerOfTypeRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("ofType", false);
    }

    @Test
    public void entityAgentOfTypeRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("ofType", true);
    }

    @Test
    public void entityManagerResultMappingRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("mapping", false);
    }

    @Test
    public void entityAgentResultMappingRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("mapping", true);
    }

    @Test
    public void entityManagerTypedNamedFactoryRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("named", false);
    }

    @Test
    public void entityAgentTypedNamedFactoryRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("named", true);
    }

    @Test
    public void entityManagerCustomQueryTypeRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("queryType", false);
    }

    @Test
    public void entityAgentCustomQueryTypeRetainsCacheSemantics() {
        assertNativeAdaptationCacheSemantics("queryType", true);
    }

    @Test
    public void entityManagerQueryTypeRoundTripRetainsUncachedResults() {
        try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
            unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
            unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
            EntityManagerFactory factory = unit.create();
            for (String store : List.of("absent", "USE", "BYPASS")) {
                factory.getCache().evictAll();
                try (EntityManager manager = factory.createEntityManager()) {
                    var query = manager.createNativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1", MethodNativeBook.class);
                    var original = query.unwrap(JpaQuery.class).getDatabaseQuery();
                    original.setShouldMaintainCache(false);
                    if (!store.equals("absent")) {
                        query.setCacheStoreMode(CacheStoreMode.valueOf(store));
                    }
                    assertFalse(query.getHints().containsKey(QueryHints.MAINTAIN_CACHE));
                    query.setHint(QueryHints.QUERY_TYPE, QueryType.DataRead);
                    var intermediate = query.unwrap(JpaQuery.class).getDatabaseQuery();
                    assertFalse(intermediate.shouldMaintainCache());
                    query.setHint(QueryHints.QUERY_TYPE, NativeBookReadQuery.class.getName());
                    query.setParameter(1, 1);
                    assertEquals("Alpha", ((MethodNativeBook) query.getSingleResult()).title);
                    assertFalse(store, factory.getCache().contains(MethodNativeBook.class, 1));
                    assertFalse(query.unwrap(JpaQuery.class).getDatabaseQuery().shouldMaintainCache());
                    assertFalse(original.shouldMaintainCache());
                    assertFalse(intermediate.shouldMaintainCache());
                    assertFalse(query.getHints().containsKey(QueryHints.MAINTAIN_CACHE));
                }
            }
        }
    }

    private void assertNativeAdaptationCacheSemantics(String adaptation, boolean agentMode) {
        try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
            unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
            unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
            EntityManagerFactory factory = unit.create();
            try {
                readCachedBook(factory, agentMode, "cachedBookRefresh");
                try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
                    var source = handler.createNativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1");
                    if (adaptation.equals("queryType")) {
                        source.setHint(QueryHints.MAINTAIN_CACHE, true);
                    }
                    source.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                    source.setCacheStoreMode(CacheStoreMode.REFRESH);
                    jakarta.persistence.Query query = switch (adaptation) {
                        case "ofType" -> source.ofType(MethodNativeBook.class);
                        case "mapping" -> source.withResultSetMapping(jakarta.persistence.sql.ResultSetMapping.entity(MethodNativeBook.class));
                        case "named" -> {
                            factory.addNamedQuery("methodNative.dynamicCache", source);
                            yield handler.createNamedQuery("methodNative.dynamicCache", MethodNativeBook.class);
                        }
                        case "queryType" -> source.setHint(QueryHints.QUERY_TYPE, NativeBookReadQuery.class.getName());
                        default -> throw new AssertionError(adaptation);
                    };
                    query.setParameter(1, 1);
                    writeTitleOutsideCache(factory, "adapted-refresh");
                    assertEquals(CacheStoreMode.REFRESH, query.getCacheStoreMode());
                    assertEquals("adapted-refresh", ((MethodNativeBook) query.getSingleResult()).title);
                    assertEquals("adapted-refresh", cachedBook(factory).title);
                    assertTrue(((ObjectBuildingQuery) query.unwrap(JpaQuery.class).getDatabaseQuery())
                            .shouldRefreshIdentityMapResult());
                    for (Object replacement : List.of(CacheStoreMode.BYPASS, CacheStoreMode.USE, "")) {
                        String cached = cachedBook(factory).title;
                        query.setHint("jakarta.persistence.cacheStoreMode", replacement);
                        CacheStoreMode expected = replacement == CacheStoreMode.BYPASS ? CacheStoreMode.BYPASS : CacheStoreMode.USE;
                        assertEquals(expected, query.getCacheStoreMode());
                        writeTitleOutsideCache(factory, "adapted-" + expected);
                        if (handler instanceof EntityManager manager) {
                            manager.clear();
                        }
                        assertEquals("adapted-" + expected, ((MethodNativeBook) query.getSingleResult()).title);
                        if (expected == CacheStoreMode.BYPASS || agentMode) {
                            assertEquals(cached, cachedBook(factory).title);
                        }
                        assertFalse(((ObjectBuildingQuery) query.unwrap(JpaQuery.class).getDatabaseQuery())
                                .shouldRefreshIdentityMapResult());
                        query.setCacheStoreMode(CacheStoreMode.REFRESH);
                    }
                    if ("named".equals(adaptation)) {
                        assertEquals(CacheStoreMode.REFRESH,
                                handler.createNamedQuery("methodNative.dynamicCache").getCacheStoreMode());
                    }
                }
            } finally {
                writeTitleOutsideCache(factory, "Alpha");
                factory.getCache().evictAll();
            }
        }
    }

    /** A valid application-defined query type with the result identity supplied by its constructor. */
    public static class NativeBookReadQuery extends ReadAllQuery {
        public NativeBookReadQuery() {
            super(MethodNativeBook.class);
        }
    }

    @Test
    public void inheritedCacheModesRestoreRetainedQueryConformance() {
        for (boolean fail : List.of(false, true)) {
            try (EntityManager manager = emf.createEntityManager()) {
                manager.setCacheStoreMode(CacheStoreMode.BYPASS);
                var query = manager.createQuery("SELECT b FROM MethodNativeBook b WHERE b.title = ?1", MethodNativeBook.class)
                        .setHint(QueryHints.CACHE_USAGE, CacheUsage.ConformResultsInUnitOfWork).setParameter(1, "Alpha");
                ObjectLevelReadQuery retained = (ObjectLevelReadQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                manager.getTransaction().begin();
                try {
                    if (fail) {
                        retained.setRedirector((databaseQuery, arguments, session) -> {
                            throw new IllegalStateException("conformance restoration probe");
                        });
                        assertThrows(IllegalStateException.class, query::getResultList);
                        retained.setRedirector(null);
                    } else {
                        assertEquals(1, query.getResultList().size());
                    }
                    assertTrue(retained.shouldConformResultsInUnitOfWork());
                } finally {
                    manager.getTransaction().rollback();
                }
                manager.getTransaction().begin();
                try {
                    MethodNativeBook changed = manager.find(MethodNativeBook.class, 1);
                    changed.title = "unflushed";
                    query.setQueryFlushMode(QueryFlushMode.NO_FLUSH);
                    assertSame(changed, query.setParameter(1, "unflushed").getSingleResult());
                    assertTrue(retained.shouldConformResultsInUnitOfWork());
                } finally {
                    manager.getTransaction().rollback();
                }
            }
        }
    }

    @Test
    public void namedReportsReuseCachedResultsAcrossFreshWrappers() {
        assertNamedReportCache(false, "absent");
    }

    @Test
    public void namedReportsReuseCachedResultsOnSameWrapper() {
        assertNamedReportCache(true, "absent");
    }

    @Test
    public void freshNamedReportsReuseCacheWithCanonicalHandlerDefaults() {
        assertNamedReportCache(false, "canonical");
    }

    @Test
    public void sameNamedReportsReuseCacheWithCanonicalHandlerDefaults() {
        assertNamedReportCache(true, "canonical");
    }

    @Test
    public void freshNamedReportsReuseCacheWithLegacyHandlerDefaults() {
        assertNamedReportCache(false, "legacy");
    }

    @Test
    public void sameNamedReportsReuseCacheWithLegacyHandlerDefaults() {
        assertNamedReportCache(true, "legacy");
    }

    private void assertNamedReportCache(boolean reuseWrapper, String defaults) {
        // Keep scalar/DTO result caching separate from entity registration in the UnitOfWork.
        for (String name : List.of("methodNative.cachedTitles", "methodNative.cachedSummaries")) {
            ((JpaCache) emf.getCache()).clearQueryCache();
            try (EntityManager manager = emf.createEntityManager()) {
                setHandlerUseDefaults(manager, defaults);
                var query = manager.createNamedQuery(name).setParameter("id", 1);
                List<?> expected = name.endsWith("Titles") ? List.of("Alpha", "Beta") : SUMMARIES;
                Map<String, Object> hints = query.getHints();
                sql.clear();
                assertEquals(expected, query.getResultList());
                assertEquals(name + " must populate the cache with one database query", 1, sql.size());
                sql.clear();
                for (int attempt = 0; attempt < 5; attempt++) {
                    var next = reuseWrapper ? query : manager.createNamedQuery(name).setParameter("id", 1);
                    assertEquals(expected, next.getResultList());
                    assertEquals(hints, next.getHints());
                }
                assertEquals(name + " must reuse cached results after the first execution", 0, sql.size());
            }
        }
    }

    @Test
    public void dynamicReportsInitializeCacheWithInheritedUse() {
        for (String defaults : List.of("canonical", "legacy")) {
            for (boolean constructor : List.of(false, true)) {
                ((JpaCache) emf.getCache()).clearQueryCache();
                try (EntityManager manager = emf.createEntityManager()) {
                    setHandlerUseDefaults(manager, defaults);
                    String projection = constructor ? "NEW " + Summary.class.getName() + "(b.id, b.title)" : "b.title";
                    var query = manager.createQuery("SELECT " + projection
                            + " FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id")
                            .setHint(QueryHints.QUERY_RESULTS_CACHE, true)
                            .setHint(QueryHints.MAINTAIN_CACHE, false).setParameter("id", 1);
                    List<?> expected = constructor ? SUMMARIES : List.of("Alpha", "Beta");
                    Map<String, Object> hints = query.getHints();
                    sql.clear();
                    assertEquals(expected, query.getResultList());
                    assertEquals("An initially unprepared dynamic report must query the database", 1, sql.size());
                    sql.clear();
                    for (int attempt = 0; attempt < 5; attempt++) {
                        assertEquals(expected, query.getResultList());
                    }
                    assertEquals("Dynamic reports must reuse their first legitimately prepared execution", 0, sql.size());
                    assertEquals(hints, query.getHints());
                }
            }
        }
    }

    @Test
    public void reportCacheDefaultsRemainLiveWithoutChangingQueryHints() {
        for (String defaults : List.of("canonical", "legacy")) {
            for (String name : List.of("methodNative.cachedTitles", "methodNative.cachedSummaries")) {
                ((JpaCache) emf.getCache()).clearQueryCache();
                try (EntityManager manager = emf.createEntityManager()) {
                    var query = manager.createNamedQuery(name).setParameter("id", 1);
                    Map<String, Object> hints = query.getHints();
                    List<?> expected = name.endsWith("Titles") ? List.of("Alpha", "Beta") : SUMMARIES;
                    assertEquals(expected, query.getResultList());
                    setHandlerUseDefaults(manager, defaults);
                    assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                    assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                    sql.clear();
                    for (int attempt = 0; attempt < 5; attempt++) {
                        assertEquals(expected, query.getResultList());
                        assertEquals(hints, query.getHints());
                    }
                    assertEquals(name + " must remain cached when live defaults change to USE", 0, sql.size());
                    assertEquals(hints, manager.createNamedQuery(name).getHints());
                }
            }
        }
    }

    @Test
    public void explicitReportCacheModesOverrideLaterHandlerDefaults() {
        for (String name : List.of("methodNative.cachedTitles", "methodNative.cachedSummaries")) {
            ((JpaCache) emf.getCache()).clearQueryCache();
            try (EntityManager manager = emf.createEntityManager()) {
                var query = manager.createNamedQuery(name).setParameter("id", 1)
                        .setCacheRetrieveMode(CacheRetrieveMode.USE).setCacheStoreMode(CacheStoreMode.USE);
                List<?> expected = name.endsWith("Titles") ? List.of("Alpha", "Beta") : SUMMARIES;
                assertEquals(expected, query.getResultList());
                Map<String, Object> hints = query.getHints();
                manager.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
                manager.setCacheStoreMode(CacheStoreMode.REFRESH);
                sql.clear();
                assertEquals(expected, query.getResultList());
                assertEquals(0, sql.size());
                assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
                assertEquals(hints, query.getHints());
                var sibling = manager.createNamedQuery(name);
                assertEquals(CacheRetrieveMode.BYPASS, sibling.getCacheRetrieveMode());
                assertEquals(CacheStoreMode.REFRESH, sibling.getCacheStoreMode());
                assertFalse(sibling.getHints().containsKey(QueryHints.CACHE_STORE_MODE));
            }
        }
    }

    @Test
    public void dynamicReportCachesSeparateArgumentsLimitsAndResultShapes() {
        ((JpaCache) emf.getCache()).clearQueryCache();
        try (EntityManager manager = emf.createEntityManager()) {
            setHandlerUseDefaults(manager, "canonical");
            String jpql = "SELECT b.title FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id";
            var strings = manager.createQuery(jpql, String.class).setHint(QueryHints.QUERY_RESULTS_CACHE, true)
                    .setHint(QueryHints.MAINTAIN_CACHE, false).setParameter("id", 1);
            assertEquals(List.of("Alpha", "Beta"), strings.getResultList());
            var records = manager.createQuery(jpql, Text.class).setHint(QueryHints.QUERY_RESULTS_CACHE, true)
                    .setHint(QueryHints.MAINTAIN_CACHE, false).setParameter("id", 1);
            sql.clear();
            assertEquals(List.of(new Text("Alpha"), new Text("Beta")), records.getResultList());
            assertEquals("A different result shape needs its own cached result", 1, sql.size());
            sql.clear();
            assertEquals(List.of("Alpha", "Beta"), strings.getResultList());
            assertEquals(List.of(new Text("Alpha"), new Text("Beta")), records.getResultList());
            assertEquals(0, sql.size());
            assertEquals(List.of("Beta"), strings.setParameter("id", 2).getResultList());
            assertEquals("A different argument must not reuse the first result", 1, sql.size());
            sql.clear();
            assertEquals(List.of("Alpha", "Beta"), strings.setParameter("id", 1).getResultList());
            assertEquals(0, sql.size());
            assertEquals(List.of("Alpha"), strings.setMaxResults(1).getResultList());
            assertEquals(List.of("Alpha"), strings.getResultList());
            assertEquals(List.of("Alpha", "Beta"), strings.setMaxResults(2).getResultList());
            assertEquals(List.of(new Text("Alpha"), new Text("Beta")), records.getResultList());
        }
    }

    @Test
    public void dynamicReportCachesHonorHandlerBypassAndTimeout() {
        for (String defaults : List.of("canonical", "legacy")) {
            for (boolean constructor : List.of(false, true)) {
                for (CacheRetrieveMode retrieve : CacheRetrieveMode.values()) {
                    for (CacheStoreMode store : List.of(CacheStoreMode.USE, CacheStoreMode.BYPASS)) {
                        for (boolean timeout : List.of(false, true)) {
                            ((JpaCache) emf.getCache()).clearQueryCache();
                            try (EntityManager manager = emf.createEntityManager()) {
                                setHandlerCacheDefaults(manager, defaults, retrieve, store);
                                String projection = constructor ? "NEW " + MethodNativeBook.class.getName()
                                        + "(b.id, b.title, b.detail.note)" : "b.title";
                                var query = manager.createQuery("SELECT " + projection
                                        + " FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id")
                                        .setHint(QueryHints.QUERY_RESULTS_CACHE, true).setParameter("id", 1);
                                if (!constructor) {
                                    query.setHint(QueryHints.MAINTAIN_CACHE, false);
                                }
                                if (timeout) {
                                    query.setTimeout(5000);
                                }
                                Map<String, Object> hints = query.getHints();
                                sql.clear();
                                assertEquals(List.of("Alpha", "Beta"), reportTitles(query.getResultList(), constructor));
                                assertEquals(1, sql.size());
                                sql.clear();
                                assertEquals(List.of("Alpha", "Beta"), reportTitles(query.getResultList(), constructor));
                                assertEquals("Handler defaults must preserve base-supported report caching", 0, sql.size());
                                assertEquals(retrieve, query.getCacheRetrieveMode());
                                assertEquals(store, query.getCacheStoreMode());
                                assertEquals(hints, query.getHints());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void dynamicReportCacheDefaultsRemainLiveAndExplicitModesWin() {
        for (String defaults : List.of("canonical", "legacy")) {
            ((JpaCache) emf.getCache()).clearQueryCache();
            try (EntityManager manager = emf.createEntityManager()) {
                var query = manager.createQuery("SELECT b.title FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id")
                        .setHint(QueryHints.QUERY_RESULTS_CACHE, true)
                        .setHint(QueryHints.MAINTAIN_CACHE, false).setParameter("id", 1);
                Map<String, Object> hints = query.getHints();
                assertEquals(List.of("Alpha", "Beta"), query.getResultList());
                for (CacheStoreMode store : List.of(CacheStoreMode.BYPASS, CacheStoreMode.USE, CacheStoreMode.BYPASS)) {
                    CacheRetrieveMode retrieve = store == CacheStoreMode.USE ? CacheRetrieveMode.USE : CacheRetrieveMode.BYPASS;
                    setHandlerCacheDefaults(manager, defaults, retrieve, store);
                    assertEquals(retrieve, query.getCacheRetrieveMode());
                    assertEquals(store, query.getCacheStoreMode());
                    assertEquals(List.of("Alpha", "Beta"), query.getResultList());
                    sql.clear();
                    assertEquals(List.of("Alpha", "Beta"), query.getResultList());
                    assertEquals(0, sql.size());
                    assertEquals(hints, query.getHints());
                }
                query.setCacheRetrieveMode(CacheRetrieveMode.USE).setCacheStoreMode(CacheStoreMode.USE);
                setHandlerCacheDefaults(manager, defaults, CacheRetrieveMode.BYPASS, CacheStoreMode.REFRESH);
                assertEquals(List.of("Alpha", "Beta"), query.getResultList());
                sql.clear();
                assertEquals(List.of("Alpha", "Beta"), query.getResultList());
                assertEquals(0, sql.size());
                assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
            }
        }
    }

    private static List<String> reportTitles(List<?> results, boolean constructor) {
        return results.stream().map(value -> constructor ? ((MethodNativeBook) value).title : (String) value).toList();
    }

    @Test
    public void inheritedRefreshRefreshesPreparedEntityAndConstructorQueries() {
        for (boolean constructor : List.of(false, true)) {
            try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
                unit.info.setSharedCacheMode(SharedCacheMode.ALL.name());
                unit.settings.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true");
                EntityManagerFactory factory = unit.create();
                try (EntityManager manager = factory.createEntityManager()) {
                    String projection = constructor ? "NEW " + EntityValue.class.getName() + "(b.title, b)" : "b";
                    var query = manager.createQuery("SELECT " + projection + " FROM MethodNativeBook b WHERE b.id = :id")
                            .setHint(QueryHints.QUERY_RESULTS_CACHE, true).setParameter("id", 1);
                    Object initial = query.getSingleResult();
                    MethodNativeBook book = initial instanceof EntityValue value ? value.book() : (MethodNativeBook) initial;
                    assertEquals("Alpha", book.title);
                    writeTitleOutsideCache(factory, "refreshed");
                    manager.clear();
                    manager.setCacheStoreMode(CacheStoreMode.REFRESH);
                    Object result = query.getSingleResult();
                    MethodNativeBook refreshed = result instanceof EntityValue value ? value.book() : (MethodNativeBook) result;
                    assertEquals("refreshed", refreshed.title);
                    assertTrue(manager.contains(refreshed));
                    if (result instanceof EntityValue value) {
                        assertEquals("refreshed", value.title());
                    }
                    assertEquals("refreshed", cachedBook(factory).title);
                    assertFalse(query.getHints().containsKey(QueryHints.CACHE_STORE_MODE));
                } finally {
                    writeTitleOutsideCache(factory, "Alpha");
                    factory.getCache().evictAll();
                }
            }
        }
    }

    private static void setHandlerUseDefaults(EntityManager manager, String defaults) {
        setHandlerCacheDefaults(manager, defaults, CacheRetrieveMode.USE, CacheStoreMode.USE);
    }

    private static void setHandlerCacheDefaults(EntityManager manager, String defaults,
            CacheRetrieveMode retrieve, CacheStoreMode store) {
        if ("canonical".equals(defaults)) {
            manager.setCacheRetrieveMode(retrieve);
            manager.setCacheStoreMode(store);
        } else if ("legacy".equals(defaults)) {
            manager.setProperty("jakarta.persistence.cacheRetrieveMode", retrieve);
            manager.setProperty("jakarta.persistence.cacheStoreMode", store);
        }
    }

    @Test
    public void cacheOptionsAllowDtoAndStatementExecution() {
        withHandlers(handler -> {
            handler.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
            handler.setCacheStoreMode(CacheStoreMode.REFRESH);
            var dto = handler.createNamedQuery(name("summaryDefaults", Integer.class)).setParameter(1, 1);
            assertEquals(CacheRetrieveMode.USE, dto.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.USE, dto.getCacheStoreMode());
            assertEquals(SUMMARIES.get(0), dto.getSingleResult());
            handler.getTransaction().begin();
            try {
                var update = handler.createNamedQuery(name("updateInt", String.class, Integer.class))
                        .setParameter(1, "query update").setParameter(2, 1);
                assertEquals(Integer.valueOf(3000), update.getTimeout());
                assertEquals(QueryFlushMode.NO_FLUSH, update.getQueryFlushMode());
                assertEquals(1, update.executeUpdate());
                assertEquals(1, handler.createNamedStatement(name("updateInt", String.class, Integer.class))
                        .setParameter(1, "statement update").setParameter(2, 1).execute());
                assertEquals(new Summary(1, "statement update"), dto.getSingleResult());
            } finally {
                if (handler.getTransaction().isActive()) {
                    handler.getTransaction().rollback();
                }
            }
        });
    }

    private static MethodNativeBook readCachedBook(EntityManagerFactory factory, boolean agentMode, String method) {
        try (EntityHandler handler = agentMode ? factory.createEntityAgent() : factory.createEntityManager()) {
            var query = handler.createNamedQuery(name(method, Integer.class)).setParameter(1, 1);
            if ("cachedBookDefault".equals(method)) {
                assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
                assertEquals(CacheStoreMode.USE, query.getCacheStoreMode());
            }
            Object result = query.getSingleResult();
            return result instanceof EntityValue value ? value.book() : (MethodNativeBook) result;
        }
    }

    private static MethodNativeBook cachedBook(EntityManagerFactory factory) {
        MethodNativeBook cached = (MethodNativeBook) factory.unwrap(JpaEntityManagerFactory.class)
                .getServerSession().getIdentityMapAccessor().getFromIdentityMap(1, MethodNativeBook.class);
        assertNotNull("Expected a cached entity after explicit REFRESH", cached);
        return cached;
    }

    private static void writeTitleOutsideCache(EntityManagerFactory factory, String title) {
        try (EntityAgent agent = factory.createEntityAgent(CacheStoreMode.BYPASS)) {
            agent.runWithConnection((Connection connection) -> {
                try (var statement = connection.prepareStatement("UPDATE JPA_METHOD_NATIVE_BOOK SET TITLE = ? WHERE ID = ?")) {
                    statement.setString(1, title);
                    statement.setInt(2, 1);
                    assertEquals(1, statement.executeUpdate());
                }
            });
        }
    }

    @Test
    public void voidIntAndLongUpdateDeclarationsExecuteThroughBothApis() {
        withHandlers(handler -> {
            for (String method : List.of("updateVoid", "updateInt", "updateLong")) {
                for (boolean statement : List.of(false, true)) {
                    handler.getTransaction().begin();
                    try {
                        String queryName = name(method, String.class, Integer.class);
                        int count = statement ? handler.createNamedStatement(queryName)
                                .setParameter(1, "changed").setParameter(2, 1).execute()
                                : handler.createNamedQuery(queryName)
                                .setParameter(1, "changed").setParameter(2, 1).executeUpdate();
                        assertEquals(1, count);
                        assertEquals("changed", handler.createNamedQuery(name("title", Integer.class))
                                .setParameter(1, 1).getSingleResult());
                    } finally {
                        handler.getTransaction().rollback();
                    }
                }
            }
        });
    }

    @Test
    public void deleteStatementsHonorRollback() {
        withHandlers(handler -> {
            handler.getTransaction().begin();
            try {
                assertEquals(1, handler.createNamedStatement(name("delete", Integer.class)).setParameter(1, 2).execute());
                assertEquals(1, handler.createNamedQuery(name("count")).getSingleResult());
            } finally {
                handler.getTransaction().rollback();
            }
            assertEquals(2, handler.createNamedQuery(name("count")).getSingleResult());
        });
    }

    @Test
    public void managedTypesUseDeclaringTypeNames() {
        withHandlers(handler -> {
            for (Class<?> type : List.of(MethodNativeBase.class, MethodNativeDetail.class, MethodNativeBook.class)) {
                String method = type == MethodNativeBase.class ? "baseTitle"
                        : type == MethodNativeDetail.class ? "detailTitle" : "bookTitle";
                assertEquals("Alpha", handler.createNamedQuery(name(type, method, Integer.class))
                        .setParameter(1, 1).getSingleResult());
            }
            assertThrows(IllegalArgumentException.class,
                    () -> handler.createNamedQuery(name(MethodNativeBook.class, "baseTitle", Integer.class)));
        });
    }

    @Test
    public void propertyAccessKeepsRealMappingsAndIgnoresQueryOnlyMethods() {
        withHandlers(handler -> {
            assertEquals("Beta", handler.createNamedQuery(name(MethodNativeProperty.class, "lookupTitle", Integer.class))
                    .setParameter(1, 2).getSingleResult());
            assertEquals(10, handler.createNamedQuery(name(MethodNativeProperty.class, "getId")).getSingleResult());
            MethodNativeProperty row = handler.find(MethodNativeProperty.class, 10);
            assertEquals(Integer.valueOf(10), row.getId());
            assertEquals("Property", row.getTitle());
        });
    }

    @Test
    public void privateStaticDeclarationsDoNotInitializeOrInvokeTheirClass() {
        assertEquals(0, MethodNativeTracker.initializations);
        withHandlers(handler -> {
            for (String method : List.of("hidden", "visible")) {
                assertEquals("Alpha", handler.createNamedQuery(name(MethodNativePrivateQueries.class, method, Integer.class))
                        .setParameter(1, 1).getSingleResult());
            }
        });
        assertEquals(0, MethodNativeTracker.initializations);
    }

    @Test
    public void existingQueriesAndRegistrySizeRemainStable() {
        var before = Set.copyOf(emf.getNamedQueries(Object.class).keySet());
        withHandlers(handler -> {
            for (int attempt = 0; attempt < 3; attempt++) {
                assertEquals("Alpha", handler.createNamedQuery("methodNative.ordinary").getSingleResult());
                assertEquals(SUMMARIES, handler.createNamedQuery(name("summaries")).getResultList());
            }
            assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery("MethodNativeQueries.summaries"));
        });
        assertEquals(before, emf.getNamedQueries(Object.class).keySet());
    }

    @Test
    public void explicitQueryResultClassOverridesDoNotChangeTheDeclaration() {
        withHandlers(handler -> {
            var overridden = handler.createNamedQuery(name("summaries"), Ordinary.class).getResultList();
            assertEquals(2, overridden.size());
            assertEquals("Alpha", overridden.get(0).title);
            assertEquals(SUMMARIES, handler.createNamedQuery(name("summaries")).getResultList());
            assertThrows(IllegalArgumentException.class,
                    () -> handler.createNamedQuery(name("summaries")).ofType(Ordinary.class));
            var parameterized = handler.createNamedQuery(name("summary", Integer.class), Ordinary.class);
            assertEquals("Alpha", parameterized.setParameter(1, 1).getSingleResult().title);
            assertEquals("Beta", parameterized.setParameter(1, 2).getSingleResult().title);
            var limited = handler.createNamedQuery(name("limitedSummaries"), Ordinary.class);
            assertEquals(Integer.valueOf(3000), limited.getTimeout());
            assertEquals(CacheRetrieveMode.BYPASS, limited.getCacheRetrieveMode());
            assertEquals(1, limited.getResultList().size());
            assertEquals(new Text("Alpha"), handler.createNamedQuery("methodNative.ordinary", Text.class).getSingleResult());
            assertEquals("Alpha", handler.createNamedQuery("methodNative.ordinary").getSingleResult());
        });
    }

    @Test
    public void globalMetadataCompleteSuppressesMethodAnnotations() {
        try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/method-native-query/complete.xml"));
            withHandlers(unit.create(), handler -> {
                assertEquals("Alpha", handler.createNamedQuery("methodNative.xmlOnly").getSingleResult());
                assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery(name("summaries")));
                assertThrows(IllegalArgumentException.class,
                        () -> handler.createNamedQuery(name(MethodNativeBook.class, "bookTitle", Integer.class)));
            });
        }
    }

    @Test
    public void entityMetadataCompleteDoesNotSuppressUnrelatedQueryHolder() {
        try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/method-native-query/entity-complete.xml"));
            withHandlers(unit.create(), handler -> {
                assertEquals(SUMMARIES, handler.createNamedQuery(name("summaries")).getResultList());
                assertThrows(IllegalArgumentException.class,
                        () -> handler.createNamedQuery(name(MethodNativeBook.class, "bookTitle", Integer.class)));
            });
        }
    }

    @Test
    public void xmlQueryOverridesOnlyItsMatchingMethodDeclaration() {
        try (Unit unit = new Unit(MethodNativeBook.class, MethodNativeQueries.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/method-native-query/override.xml"));
            withHandlers(unit.create(), handler -> {
                assertEquals("first", handler.createNamedQuery(name("title", Integer.class)).setParameter(1, 1).getSingleResult());
                assertEquals(SUMMARIES, handler.createNamedQuery(name("summaries")).getResultList());
            });
        }
        withHandlers(handler -> assertEquals("Alpha",
                handler.createNamedQuery(name("title", Integer.class)).setParameter(1, 1).getSingleResult()));
    }

    @Test
    public void genuineInvalidMappingIsStillRejected() {
        try (Unit unit = new Unit(MethodNativeInvalidProperty.class)) {
            unit.info.setMappingFileNames(List.of("META-INF/method-native-query/invalid.xml"));
            RuntimeException failure = assertThrows(RuntimeException.class, () -> {
                unit.create().getMetamodel();
            });
            boolean found = false;
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                if (cause instanceof ValidationException && cause.getMessage().contains("invalidMappedMethod")) {
                    found = true;
                }
            }
            assertTrue("Expected validation of the real invalid property mapping", found);
        }
    }

    @Test
    public void unlistedDiscoveryAndExclusionRespectUnitMembership() throws Exception {
        try (Archive archive = new Archive(MethodNativeQueries.class)) {
            try (Unit discovered = new Unit(MethodNativeBook.class)) {
                discovered.info.setPersistenceUnitRootUrl(archive.url());
                discovered.info.setExcludeUnlistedClasses(false);
                withHandlers(discovered.create(), handler -> assertEquals(SUMMARIES,
                        handler.createNamedQuery(name("summaries")).getResultList()));
            }
            try (Unit excluded = new Unit(MethodNativeBook.class)) {
                excluded.info.setPersistenceUnitRootUrl(archive.url());
                withHandlers(excluded.create(), handler -> assertThrows(IllegalArgumentException.class,
                        () -> handler.createNamedQuery(name("summaries"))));
            }
        }
    }

    private static Map<String, Object> properties() {
        Map<String, Object> values = new HashMap<>();
        values.put(PersistenceUnitProperties.WEAVING, "false");
        values.put(PersistenceUnitProperties.DDL_GENERATION, "none");
        values.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        values.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        values.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String key : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(key);
            if (value != null) {
                values.put(key, value);
            }
        }
        return values;
    }

    private static final class Unit implements AutoCloseable {
        private final SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        private final Map<String, Object> settings = properties();
        private final List<EntityManagerFactory> factories = new ArrayList<>();

        private Unit(Class<?>... classes) {
            info.setPersistenceUnitName("method-native-query-" + UUID.randomUUID());
            info.setPersistenceProviderClassName(PersistenceProvider.class.getName());
            info.setPersistenceXMLSchemaVersion("4.0");
            info.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
            info.setClassLoader(TestMethodNativeQuery.class.getClassLoader());
            info.setNewTempClassLoader(TestMethodNativeQuery.class.getClassLoader());
            info.setPersistenceUnitRootUrl(TestMethodNativeQuery.class.getProtectionDomain().getCodeSource().getLocation());
            info.setManagedClassNames(Arrays.stream(classes).map(Class::getName).toList());
            info.setExcludeUnlistedClasses(true);
            info.setProperties(new Properties());
        }

        private EntityManagerFactory create() {
            Map<String, Object> overrides = new HashMap<>(settings);
            overrides.put(PersistenceUnitProperties.ECLIPSELINK_SE_PUINFO, info);
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(info.getPersistenceUnitName(), overrides);
            factories.add(factory);
            return factory;
        }

        @Override
        public void close() {
            for (EntityManagerFactory factory : factories.reversed()) {
                if (factory.isOpen()) {
                    factory.close();
                }
            }
        }
    }

    private static final class Archive implements AutoCloseable {
        private final Path file;

        private Archive(Class<?> type) throws Exception {
            file = Files.createTempFile("method-native-query-", ".jar");
            String resource = type.getName().replace('.', '/') + ".class";
            try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(file));
                    InputStream input = type.getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(resource, input);
                output.putNextEntry(new JarEntry(resource));
                input.transferTo(output);
                output.closeEntry();
            }
        }

        private URL url() throws Exception {
            return file.toUri().toURL();
        }

        @Override
        public void close() throws Exception {
            Files.deleteIfExists(file);
        }
    }
}
