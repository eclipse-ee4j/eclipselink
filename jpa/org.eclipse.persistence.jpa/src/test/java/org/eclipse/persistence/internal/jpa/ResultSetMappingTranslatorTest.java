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
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Tuple;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.sql.ConstructorMapping;
import jakarta.persistence.sql.EntityMapping;
import jakarta.persistence.sql.ResultSetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.QueryType;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.ColumnResult;
import org.eclipse.persistence.queries.ConstructorResult;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.EntityResult;
import org.eclipse.persistence.queries.FieldResult;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static jakarta.persistence.sql.ResultSetMapping.*;

class ResultSetMappingTranslatorTest {
    @Test
    void laterRetrieveAliasAgreesWithTheAppliedFlags() {
        var query = new EJBQueryImpl<>(new ResultSetMappingQuery(), new QueryExecutionContextTest.TestContext());
        query.setHint(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
        query.setHint("jakarta.persistence.cacheRetrieveMode", "USE");
        assertFalse(query.getDatabaseQueryInternal().shouldRetrieveBypassCache());
        assertEquals(CacheRetrieveMode.USE, query.getCacheRetrieveMode());
    }

    @Test
    void laterStoreAliasAgreesWithTheAppliedFlags() {
        var query = new EJBQueryImpl<>(new ResultSetMappingQuery(), new QueryExecutionContextTest.TestContext());
        query.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        query.setHint("jakarta.persistence.cacheStoreMode", "BYPASS");
        assertTrue(query.getDatabaseQueryInternal().shouldStoreBypassCache());
        assertFalse(((ResultSetMappingQuery) query.getDatabaseQueryInternal()).shouldRefreshIdentityMapResult());
        assertEquals(CacheStoreMode.BYPASS, query.getCacheStoreMode());
    }

    @Test
    void publicCacheGettersFollowAllAliasOrdersAndTypedSetterOverrides() {
        for (Enum<?> first : queryCacheModes()) {
            for (Enum<?> second : queryCacheModes()) {
                if (first.getDeclaringClass() != second.getDeclaringClass()) {
                    continue;
                }
                for (String firstAlias : queryCacheAliases(first)) {
                    for (String secondAlias : queryCacheAliases(second)) {
                        for (Object firstRaw : List.of(first, first.name())) {
                            for (Object secondRaw : List.of(second, second.name())) {
                                var query = new EJBQueryImpl<>(new ResultSetMappingQuery(), new QueryExecutionContextTest.TestContext());
                                query.setHint(firstAlias, firstRaw);
                                query.setHint(secondAlias, secondRaw);
                                assertEffectiveCacheMode(query, second);
                                assertSame(secondRaw, query.getHints().get(secondAlias));
                                if (!firstAlias.equals(secondAlias)) {
                                    assertSame(firstRaw, query.getHints().get(firstAlias));
                                }
                                setTypedCacheMode(query, first);
                                assertEffectiveCacheMode(query, first);
                                query.setHint(queryCacheAliases(second).get(1), second.name());
                                assertEffectiveCacheMode(query, second);
                                setTypedCacheMode(query, second);
                                assertEffectiveCacheMode(query, second);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void ignoredPublicCacheInputsAndNullCannotPoisonGettersOrRawCopies() {
        for (Enum<?> previous : queryCacheModes()) {
            Object wrongFamily = previous instanceof CacheRetrieveMode ? CacheStoreMode.USE : CacheRetrieveMode.USE;
            List<Object> inputs = new ArrayList<>(List.of("unknown", "use", " USE ", wrongFamily, new Object() {
                @Override
                public String toString() {
                    return "USE";
                }
            }));
            inputs.add(null);
            for (String alias : queryCacheAliases(previous)) {
                for (Object input : inputs) {
                    var query = new EJBQueryImpl<>(new ResultSetMappingQuery(), new QueryExecutionContextTest.TestContext());
                    setTypedCacheMode(query, previous);
                    if (input == null) {
                        assertThrows(NullPointerException.class, () -> query.setHint(alias, null));
                    } else {
                        query.setHint(alias, input);
                    }
                    assertEffectiveCacheMode(query, previous);
                    Map<String, Object> copy = query.getHints();
                    assertTrue(copy.containsKey(alias));
                    assertSame(input, copy.get(alias));
                    copy.clear();
                    assertTrue(query.getHints().containsKey(alias));
                    assertSame(input, query.getHints().get(alias));
                    query.setHint(alias, "");
                    assertEffectiveCacheMode(query, previous instanceof CacheRetrieveMode ? CacheRetrieveMode.USE : CacheStoreMode.USE);
                    assertEquals("", query.getHints().get(alias));
                }
            }
        }
    }

    @Test
    void acceptedCacheStateSurvivesTypedNamedAdaptationAndReadToModifyConversion() {
        for (CacheRetrieveMode retrieve : CacheRetrieveMode.values()) {
            for (CacheStoreMode store : CacheStoreMode.values()) {
                for (boolean explicitMapping : List.of(false, true)) {
                    QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
                    context.active = true;
                    ResultSetMappingQuery named;
                    Class<?> requested;
                    if (explicitMapping) {
                        named = register(context, reorderedMapping(), false);
                        requested = Reordered.class;
                    } else {
                        named = new ResultSetMappingQuery();
                        named.setSQLString("UPDATE SAMPLE SET NUMBER = 7");
                        named.setIsUserDefined(true);
                        named.setResultClass(Inner.class);
                        context.session.addQuery("cache-inferred-native", named);
                        requested = Integer.class;
                    }
                    named.refreshIdentityMapResult();
                    QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, named, null, null);
                    QueryHintsHandler.apply("jakarta.persistence.cacheStoreMode", store.name(), named, null, null);
                    QueryHintsHandler.apply(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS, named, null, null);
                    QueryHintsHandler.apply("jakarta.persistence.cacheRetrieveMode", retrieve.name(), named, null, null);
                    named.setIsPrepared(true);
                    Map<String, Object> declaredHints = Map.copyOf(QueryHintsHandler.get(named));
                    EJBQueryImpl<?> query = new EJBQueryImpl<>(named.getName(), context, true, requested);
                    assertEffectiveCacheMode(query, retrieve);
                    assertEffectiveCacheMode(query, store);
                    assertEquals(declaredHints, query.getHints());
                    assertTrue(((ResultSetMappingQuery) query.getDatabaseQueryInternal()).shouldRefreshIdentityMapResult());
                    query.setHint("jakarta.persistence.cacheStoreMode", "");
                    assertEffectiveCacheMode(query, CacheStoreMode.USE);
                    assertTrue(((ResultSetMappingQuery) query.getDatabaseQueryInternal()).shouldRefreshIdentityMapResult());
                    assertEquals(1, query.executeUpdate());
                    DatabaseQuery modified = query.getDatabaseQueryInternal();
                    assertTrue(modified.isDataModifyQuery());
                    assertEffectiveCacheMode(query, retrieve);
                    assertEffectiveCacheMode(query, CacheStoreMode.USE);
                    assertEquals("", query.getHints().get("jakarta.persistence.cacheStoreMode"));
                    assertEquals(declaredHints, QueryHintsHandler.get(named));
                    assertSame(store, named.getProperty(QueryHints.CACHE_STORE_MODE));
                    assertTrue(named.shouldRefreshIdentityMapResult());
                    EJBQueryImpl<?> sibling = new EJBQueryImpl<>(named.getName(), context, true, requested);
                    assertEffectiveCacheMode(sibling, store);
                    assertEffectiveCacheMode(sibling, retrieve);
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ofType", "withResultSetMapping", "typedNamed", "queryType"})
    void nativeEntityAdaptersMaterializeAndReverseRefresh(String adapter) {
        for (Object replacement : List.of(CacheStoreMode.USE, CacheStoreMode.BYPASS, "")) {
            QueryExecutionContextTest.TestContext context = cacheEntityContext();
            EJBQueryImpl<?> source = untypedCacheQuery(context);
            if (adapter.equals("queryType")) {
                source.setHint(QueryHints.MAINTAIN_CACHE, true);
            }
            source.setCacheStoreMode(CacheStoreMode.REFRESH);
            DatabaseQuery original = source.getDatabaseQueryInternal();
            Map<String, Object> raw = new HashMap<>(source.getHints());
            EJBQueryImpl<?> sibling = new EJBQueryImpl<>((DatabaseQuery) original.clone(), context);
            EJBQueryImpl<?> adapted = adaptCacheQuery(adapter, source, context);
            assertConfiguredCacheEntity(adapted, context);
            assertEffectiveCacheMode(adapted, CacheStoreMode.REFRESH);
            ObjectBuildingQuery target = (ObjectBuildingQuery) adapted.getDatabaseQueryInternal();
            assertTrue(target.shouldRefreshIdentityMapResult(), adapter + " must materialize REFRESH");
            assertRawHintsRetained(raw, adapted);
            adapted.setHint(QueryHints.CACHE_STORE_MODE, replacement);
            assertEffectiveCacheMode(adapted, replacement == CacheStoreMode.BYPASS
                    ? CacheStoreMode.BYPASS : CacheStoreMode.USE);
            assertFalse(target.shouldRefreshIdentityMapResult(), adapter + " must restore independent refresh=false");
            assertSame(CacheStoreMode.REFRESH, original.getProperty(QueryHints.CACHE_STORE_MODE));
            assertEquals(raw, cacheAdapterSourceHints(original));
            assertEffectiveCacheMode(sibling, CacheStoreMode.REFRESH);
            assertEquals(raw, sibling.getHints());
        }
    }

    static Stream<Arguments> nativeCacheHistories() {
        return Stream.of("ofType", "withResultSetMapping", "typedNamed", "queryType")
                .flatMap(adapter -> Stream.of("ignored", "null", "standardThenLegacy", "legacyThenStandard",
                        "typedThenLegacy", "emptyThenIgnored")
                        .map(history -> Arguments.of(adapter, history)));
    }

    @ParameterizedTest
    @MethodSource("nativeCacheHistories")
    void nativeEntityAdaptersPreserveAcceptedStateBehindRawHistory(String adapter, String history) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> source = untypedCacheQuery(context);
        CacheStoreMode accepted = CacheStoreMode.REFRESH;
        switch (history) {
            case "ignored" -> {
                source.setCacheStoreMode(CacheStoreMode.REFRESH);
                source.setHint(QueryHints.CACHE_STORE_MODE, "ignored");
            }
            case "null" -> {
                source.setCacheStoreMode(CacheStoreMode.REFRESH);
                assertThrows(NullPointerException.class, () -> source.setHint(QueryHints.CACHE_STORE_MODE, null));
            }
            case "standardThenLegacy" -> {
                source.setHint(QueryHints.CACHE_STORE_MODE, "BYPASS");
                source.setHint("jakarta.persistence.cacheStoreMode", CacheStoreMode.REFRESH);
            }
            case "legacyThenStandard" -> {
                source.setHint("jakarta.persistence.cacheStoreMode", "BYPASS");
                source.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
            }
            case "typedThenLegacy" -> {
                source.setCacheStoreMode(CacheStoreMode.REFRESH);
                source.setHint("jakarta.persistence.cacheStoreMode", "USE");
                accepted = CacheStoreMode.USE;
            }
            case "emptyThenIgnored" -> {
                source.setCacheStoreMode(CacheStoreMode.REFRESH);
                source.setHint("jakarta.persistence.cacheStoreMode", "");
                source.setHint(QueryHints.CACHE_STORE_MODE, "ignored");
                accepted = CacheStoreMode.USE;
            }
            default -> throw new AssertionError(history);
        }
        if (adapter.equals("queryType") && accepted == CacheStoreMode.REFRESH) {
            source.setHint(QueryHints.MAINTAIN_CACHE, true);
        }
        source.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
        source.setHint("jakarta.persistence.cacheRetrieveMode", "ignored");
        DatabaseQuery original = source.getDatabaseQueryInternal();
        Map<String, Object> raw = new HashMap<>(source.getHints());
        EJBQueryImpl<?> adapted = assertDoesNotThrow(() -> adaptCacheQuery(adapter, source, context));
        assertConfiguredCacheEntity(adapted, context);
        assertEffectiveCacheMode(adapted, accepted);
        assertEffectiveCacheMode(adapted, CacheRetrieveMode.BYPASS);
        ObjectBuildingQuery target = (ObjectBuildingQuery) adapted.getDatabaseQueryInternal();
        assertEquals(accepted == CacheStoreMode.REFRESH, target.shouldRefreshIdentityMapResult());
        assertRawHintsRetained(raw, adapted);
        adapted.setCacheStoreMode(CacheStoreMode.USE);
        assertFalse(target.shouldRefreshIdentityMapResult());
        adapted.setCacheRetrieveMode(CacheRetrieveMode.USE);
        assertSame(accepted, original.getProperty(QueryHints.CACHE_STORE_MODE));
        assertSame(CacheRetrieveMode.BYPASS, original.getProperty(QueryHints.CACHE_RETRIEVE_MODE));
        assertEquals(raw, cacheAdapterSourceHints(original));
    }

    static Stream<Arguments> cacheShapeTransfers() {
        return Stream.of("data", "modify", "entity", "mapped").flatMap(source ->
                Stream.of("data", "modify", "entity", "mapped", "refreshingEntity").flatMap(target ->
                        (source.equals("entity") || source.equals("mapped") ? Stream.of(false, true) : Stream.of(false))
                                .flatMap(independent -> Stream.of("absent", "USE", "BYPASS").flatMap(retrieve ->
                                        Stream.of("absent", "USE", "BYPASS", "REFRESH")
                                                .map(store -> Arguments.of(source, target, independent, retrieve, store))))));
    }

    @ParameterizedTest
    @MethodSource("cacheShapeTransfers")
    void queryTypeShapeTransfersPreserveSemanticStateAndIndependentRefresh(String sourceShape,
            String targetShape, boolean independent, String retrieveName, String storeName) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> source = cacheShapeQuery(context, sourceShape, independent);
        CacheRetrieveMode retrieve = retrieveName.equals("absent") ? null : CacheRetrieveMode.valueOf(retrieveName);
        CacheStoreMode store = storeName.equals("absent") ? null : CacheStoreMode.valueOf(storeName);
        if (sourceShape.equals("data") && (store == CacheStoreMode.REFRESH || targetShape.equals("refreshingEntity"))) {
            source.setHint(QueryHints.MAINTAIN_CACHE, true);
        }
        if (retrieve != null) {
            source.setCacheRetrieveMode(retrieve);
        }
        if (store != null) {
            source.setCacheStoreMode(store);
        }
        DatabaseQuery original = source.getDatabaseQueryInternal();
        Map<String, Object> raw = new HashMap<>(source.getHints());
        EJBQueryImpl<?> sibling = new EJBQueryImpl<>((DatabaseQuery) original.clone(), context);
        source.setHint(QueryHints.QUERY_TYPE, cacheShapeHint(targetShape));
        DatabaseQuery target = source.getDatabaseQueryInternal();
        assertEquals(original.shouldMaintainCache(), target.shouldMaintainCache());
        boolean targetIndependent = original instanceof ObjectBuildingQuery ? independent : targetShape.equals("refreshingEntity");
        assertTransferredCacheState(target, retrieve, store, targetIndependent);
        assertRawHintsRetained(raw, source);
        if (target instanceof ObjectBuildingQuery) {
            assertConfiguredCacheEntity(source, context);
        }
        // Entity-free shapes carry independent ownership back to an entity-capable shape.
        source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
        assertConfiguredCacheEntity(source, context);
        assertEquals(original.shouldMaintainCache(), source.getDatabaseQueryInternal().shouldMaintainCache());
        assertTransferredCacheState(source.getDatabaseQueryInternal(), retrieve, store, targetIndependent);
        source.setCacheStoreMode(CacheStoreMode.USE);
        source.setCacheRetrieveMode(CacheRetrieveMode.USE);
        assertTransferredCacheState(source.getDatabaseQueryInternal(), CacheRetrieveMode.USE,
                CacheStoreMode.USE, targetIndependent);
        assertTransferredCacheState(original, retrieve, store, independent);
        assertTransferredCacheState(sibling.getDatabaseQueryInternal(), retrieve, store, independent);
        assertEquals(raw, cacheAdapterSourceHints(original));
        assertEquals(raw, sibling.getHints());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ofType", "withResultSetMapping", "typedNamed", "queryType"})
    void nativeEntityAdaptersDoNotInventModesFromAbsentOrIgnoredHistory(String adapter) {
        for (String history : List.of("absent", "ignored", "null")) {
            QueryExecutionContextTest.TestContext context = cacheEntityContext();
            EJBQueryImpl<?> source = untypedCacheQuery(context);
            for (String alias : List.of(QueryHints.CACHE_RETRIEVE_MODE, "jakarta.persistence.cacheRetrieveMode",
                    QueryHints.CACHE_STORE_MODE, "jakarta.persistence.cacheStoreMode")) {
                if (history.equals("ignored")) {
                    source.setHint(alias, "ignored");
                } else if (history.equals("null")) {
                    assertThrows(NullPointerException.class, () -> source.setHint(alias, null));
                }
            }
            DatabaseQuery original = source.getDatabaseQueryInternal();
            Map<String, Object> raw = new HashMap<>(source.getHints());
            EJBQueryImpl<?> target = assertDoesNotThrow(() -> adaptCacheQuery(adapter, source, context));
            assertConfiguredCacheEntity(target, context);
            assertTransferredCacheState(target.getDatabaseQueryInternal(), null, null, false);
            assertEquals(CacheRetrieveMode.USE, target.getCacheRetrieveMode());
            assertEquals(CacheStoreMode.USE, target.getCacheStoreMode());
            assertRawHintsRetained(raw, target);
            target.setCacheStoreMode(CacheStoreMode.REFRESH);
            assertTrue(((ObjectBuildingQuery) target.getDatabaseQueryInternal()).shouldRefreshIdentityMapResult());
            target.setHint("jakarta.persistence.cacheStoreMode", "");
            assertTransferredCacheState(target.getDatabaseQueryInternal(), null, CacheStoreMode.USE, false);
            assertNull(original.getProperty(QueryHints.CACHE_RETRIEVE_MODE));
            assertNull(original.getProperty(QueryHints.CACHE_STORE_MODE));
            assertEquals(raw, cacheAdapterSourceHints(original));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"entity", "mapped"})
    void independentProviderChangesSurviveRepeatedNativeMappingAdaptation(String shape) {
        for (boolean before : List.of(false, true)) {
            for (boolean during : List.of(false, true)) {
                for (Object replacement : List.of(CacheStoreMode.USE, CacheStoreMode.BYPASS, "")) {
                    QueryExecutionContextTest.TestContext context = cacheEntityContext();
                    EJBQueryImpl<?> source = cacheShapeQuery(context, shape, before);
                    source.setCacheStoreMode(CacheStoreMode.REFRESH);
                    source.setHint(QueryHints.REFRESH, during);
                    DatabaseQuery original = source.getDatabaseQueryInternal();
                    Map<String, Object> raw = new HashMap<>(source.getHints());
                    for (int repetition = 0; repetition < 2; repetition++) {
                        source.withResultSetMapping(entity(CacheEntity.class));
                        assertConfiguredCacheEntity(source, context);
                        assertTransferredCacheState(source.getDatabaseQueryInternal(), null, CacheStoreMode.REFRESH, during);
                        assertRawHintsRetained(raw, source);
                    }
                    source.setHint("jakarta.persistence.cacheStoreMode", replacement);
                    CacheStoreMode accepted = replacement == CacheStoreMode.BYPASS ? CacheStoreMode.BYPASS : CacheStoreMode.USE;
                    assertTransferredCacheState(source.getDatabaseQueryInternal(), null, accepted, during);
                    source.setCacheStoreMode(CacheStoreMode.REFRESH);
                    source.setHint(QueryHints.REFRESH, !during);
                    source.withResultSetMapping(entity(CacheEntity.class));
                    source.setCacheStoreMode(CacheStoreMode.USE);
                    assertTransferredCacheState(source.getDatabaseQueryInternal(), null, CacheStoreMode.USE, !during);
                    assertEquals(raw, QueryHintsHandler.get(original));
                    assertTransferredCacheState(original, null, CacheStoreMode.REFRESH, during);
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ofType", "withResultSetMapping", "typedNamed", "queryType"})
    void nativeEntityAdaptersRetainReadSettingsArgumentsAndIndependentCopies(String adapter) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        DataReadQuery configured = new DataReadQuery();
        String sql = "SELECT ID, LABEL FROM CACHE_ADAPTER_ENTITY WHERE ID = ?";
        configured.setSQLString(sql);
        configured.setIsUserDefined(true);
        configured.addArgument("1", Integer.class, true);
        configured.setArgumentValues(new ArrayList<>(List.of(17)));
        EJBQueryImpl<?> source = new EJBQueryImpl<>(configured, context);
        source.setParameter(1, 17);
        source.setFirstResult(2);
        source.setMaxResults(3);
        source.setFlushMode(FlushModeType.COMMIT);
        source.setHint(QueryHints.QUERY_TIMEOUT_UNIT, TimeUnit.SECONDS);
        source.setHint(QueryHints.QUERY_TIMEOUT, 7);
        source.setHint(QueryHints.JDBC_FETCH_SIZE, 11);
        source.setHint(QueryHints.BIND_PARAMETERS, false);
        if (adapter.equals("queryType")) {
            source.setHint(QueryHints.MAINTAIN_CACHE, true);
        }
        source.setCacheRetrieveMode(CacheRetrieveMode.BYPASS);
        source.setCacheStoreMode(CacheStoreMode.REFRESH);
        source.propagateResultProperties();
        DatabaseQuery original = source.getDatabaseQueryInternal();
        assertEquals(List.of(Integer.class), original.getArgumentTypes());
        Map<String, Object> raw = new HashMap<>(source.getHints());
        EJBQueryImpl<?> sibling = new EJBQueryImpl<>((DatabaseQuery) original.clone(), context);
        sibling.setParameter(1, 17);
        EJBQueryImpl<?> target = adaptCacheQuery(adapter, source, context);
        if (adapter.equals("typedNamed")) {
            assertThrows(IllegalStateException.class, () -> target.getParameterValue(1));
            target.setParameter(1, 17);
        }
        DatabaseQuery adapted = target.getDatabaseQueryInternal();
        assertEquals(sql, adapted.getSQLString());
        assertEquals(2, target.getFirstResult());
        assertEquals(3, target.getMaxResults());
        assertEquals(17, target.getParameterValue(1));
        assertEquals(FlushModeType.COMMIT, target.getFlushMode());
        assertEquals(7, adapted.getQueryTimeout());
        assertEquals(TimeUnit.SECONDS, adapted.getQueryTimeoutUnit());
        assertEquals(11, ((ReadQuery) adapted).getFetchSize());
        assertFalse(adapted.shouldBindAllParameters());
        assertEquals(List.of("1"), adapted.getArguments());
        assertEquals(List.of(Integer.class), adapted.getArgumentTypes());
        assertEquals(List.of(17), adapted.getArgumentValues());
        assertEquals(original.getNullableArguments(), adapted.getNullableArguments());
        assertTransferredCacheState(adapted, CacheRetrieveMode.BYPASS, CacheStoreMode.REFRESH, false);
        assertRawHintsRetained(raw, target);
        assertConfiguredCacheEntity(target, context);
        target.setParameter(1, 23);
        target.setFirstResult(0);
        target.setMaxResults(1);
        target.setHint(QueryHints.QUERY_TIMEOUT, 1);
        target.setCacheStoreMode(CacheStoreMode.USE);
        assertEquals(23, target.getParameterValue(1));
        assertEquals(17, sibling.getParameterValue(1));
        assertEquals(2, ((ReadQuery) original).getFirstResult());
        assertEquals(5, ((ReadQuery) original).getMaxRows());
        assertEquals(7, original.getQueryTimeout());
        assertEquals(raw, cacheAdapterSourceHints(original));
        assertEquals(raw, sibling.getHints());
        // Native rebuilders detach mutable argument metadata; QueryTypeHint retains the core copy contract.
        if (!adapter.equals("queryType")) {
            adapted.getArguments().add("extra");
            adapted.getArgumentValues().set(0, 29);
            adapted.getArgumentTypeNames().add(String.class.getName());
            adapted.getNullableArguments().getFirst().setName("target_only");
            assertEquals(List.of("1"), original.getArguments());
            assertEquals(List.of(17), original.getArgumentValues());
            assertEquals(List.of(Integer.class), original.getArgumentTypes());
            assertEquals("1", original.getNullableArguments().getFirst().getName());
        }
        Map<String, Object> hints = target.getHints();
        hints.clear();
        assertFalse(target.getHints().isEmpty());
        assertTransferredCacheState(original, CacheRetrieveMode.BYPASS, CacheStoreMode.REFRESH, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ofType", "withResultSetMapping", "typedNamed", "queryType"})
    void nativeEntityAdaptersPreserveExplicitMaintainCacheOptions(String adapter) {
        for (Object input : List.of(false, true, "")) {
            boolean maintain = Boolean.TRUE.equals(input);
            List<CacheStoreMode> modes = maintain ? List.of(CacheStoreMode.values())
                    : List.of(CacheStoreMode.USE, CacheStoreMode.BYPASS);
            for (CacheStoreMode store : modes) {
                QueryExecutionContextTest.TestContext context = cacheEntityContext();
                EJBQueryImpl<?> source = untypedCacheQuery(context);
                source.setHint(QueryHints.MAINTAIN_CACHE, input);
                source.setCacheStoreMode(store);
                DatabaseQuery original = source.getDatabaseQueryInternal();
                Map<String, Object> raw = new HashMap<>(source.getHints());
                EJBQueryImpl<?> target = adaptCacheQuery(adapter, source, context);
                assertEquals(maintain, target.getDatabaseQueryInternal().shouldMaintainCache());
                assertTransferredCacheState(target.getDatabaseQueryInternal(), null, store, false);
                assertConfiguredCacheEntity(target, context);
                assertRawHintsRetained(raw, target);
                target.setHint(QueryHints.MAINTAIN_CACHE, !maintain);
                assertEquals(maintain, original.shouldMaintainCache());
                assertEquals(raw, cacheAdapterSourceHints(original));
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"data", "modify", "entity", "mapped"})
    void queryTypePreservesSourceMaintainCacheOverCustomTargetDefault(String shape) {
        for (boolean initial : List.of(false, true)) {
            for (String explicit : List.of("absent", "true", "false", "")) {
                QueryExecutionContextTest.TestContext context = cacheEntityContext();
                EJBQueryImpl<?> source = cacheShapeQuery(context, shape, false);
                source.getDatabaseQueryInternal().setShouldMaintainCache(initial);
                if (!explicit.equals("absent")) {
                    source.setHint(QueryHints.MAINTAIN_CACHE, explicit.isEmpty() ? "" : Boolean.valueOf(explicit));
                }
                source.setCacheStoreMode(CacheStoreMode.USE);
                DatabaseQuery original = source.getDatabaseQueryInternal();
                boolean sourceMaintain = original.shouldMaintainCache();
                source.setHint(QueryHints.QUERY_TYPE, NonCachingCacheEntityQuery.class.getName());
                // A programmatic true value is non-default for DataReadQuery and remains meaningful.
                boolean expected = explicit.equals("absent") ? initial : Boolean.parseBoolean(explicit);
                assertEquals(expected, source.getDatabaseQueryInternal().shouldMaintainCache());
                assertTransferredCacheState(source.getDatabaseQueryInternal(), null, CacheStoreMode.USE, false);
                assertConfiguredCacheEntity(source, context);
                source.setHint(QueryHints.MAINTAIN_CACHE, !expected);
                assertEquals(sourceMaintain, original.shouldMaintainCache());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"entity", "mapped"})
    void nativeMappingRebuildPreservesObjectSourceMaintainCacheFalse(String shape) {
        for (CacheStoreMode store : List.of(CacheStoreMode.USE, CacheStoreMode.BYPASS)) {
            QueryExecutionContextTest.TestContext context = cacheEntityContext();
            EJBQueryImpl<?> source = cacheShapeQuery(context, shape, false);
            source.getDatabaseQueryInternal().setShouldMaintainCache(false);
            source.setCacheStoreMode(store);
            DatabaseQuery original = source.getDatabaseQueryInternal();
            source.withResultSetMapping(entity(CacheEntity.class));
            assertFalse(source.getDatabaseQueryInternal().shouldMaintainCache());
            assertTransferredCacheState(source.getDatabaseQueryInternal(), null, store, false);
            assertConfiguredCacheEntity(source, context);
            source.setHint(QueryHints.MAINTAIN_CACHE, true);
            assertFalse(original.shouldMaintainCache());
        }
    }

    @Test
    void queryTypeRoundTripPreservesExplicitMaintainCacheAndModifyOverrides() {
        for (Object option : List.of(false, true, "")) {
            QueryExecutionContextTest.TestContext context = cacheEntityContext();
            EJBQueryImpl<?> source = untypedCacheQuery(context);
            source.setHint(QueryHints.MAINTAIN_CACHE, option);
            boolean expected = source.getDatabaseQueryInternal().shouldMaintainCache();
            source.setCacheStoreMode(CacheStoreMode.USE);
            source.setHint(QueryHints.QUERY_TYPE, QueryType.DataModify);
            assertEquals(expected, source.getDatabaseQueryInternal().shouldMaintainCache());
            source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
            assertEquals(expected, source.getDatabaseQueryInternal().shouldMaintainCache());
            assertConfiguredCacheEntity(source, context);
        }
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> modify = cacheShapeQuery(context, "modify", false);
        modify.getDatabaseQueryInternal().setShouldMaintainCache(false);
        modify.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
        assertFalse(modify.getDatabaseQueryInternal().shouldMaintainCache());
        assertConfiguredCacheEntity(modify, context);
    }

    static Stream<Arguments> unhintedNonCachingRoundTrips() {
        return Stream.of("entity", "custom", "modify").flatMap(shape ->
                Stream.of("absent", "USE", "BYPASS").map(store -> Arguments.of(shape, store)));
    }

    @ParameterizedTest
    @MethodSource("unhintedNonCachingRoundTrips")
    void queryTypeRoundTripPreservesUnhintedNonCachingState(String shape, String storeName) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> source = shape.equals("custom")
                ? new EJBQueryImpl<>(new NonCachingCacheEntityQuery(), context)
                : cacheShapeQuery(context, shape, false);
        source.getDatabaseQueryInternal().setSQLString("SELECT ID, LABEL FROM CACHE_ADAPTER_ENTITY");
        if (!shape.equals("custom")) {
            source.getDatabaseQueryInternal().setShouldMaintainCache(false);
        }
        CacheStoreMode store = storeName.equals("absent") ? null : CacheStoreMode.valueOf(storeName);
        if (store != null) {
            source.setCacheStoreMode(store);
        }
        DatabaseQuery original = source.getDatabaseQueryInternal();
        Map<String, Object> raw = new HashMap<>(source.getHints());
        EJBQueryImpl<?> sibling = new EJBQueryImpl<>((DatabaseQuery) original.clone(), context);
        assertFalse(original.shouldMaintainCache());
        assertFalse(raw.containsKey(QueryHints.MAINTAIN_CACHE));
        source.setHint(QueryHints.QUERY_TYPE, QueryType.DataRead);
        DatabaseQuery intermediate = source.getDatabaseQueryInternal();
        assertInstanceOf(DataReadQuery.class, intermediate);
        assertFalse(intermediate.shouldMaintainCache());
        source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
        assertFalse(source.getDatabaseQueryInternal().shouldMaintainCache());
        assertConfiguredCacheEntity(source, context);
        assertTransferredCacheState(source.getDatabaseQueryInternal(), null, store, false);
        assertRawHintsRetained(raw, source);
        assertFalse(source.getHints().containsKey(QueryHints.MAINTAIN_CACHE));
        source.getDatabaseQueryInternal().setShouldMaintainCache(true);
        source.setCacheStoreMode(CacheStoreMode.REFRESH);
        assertFalse(original.shouldMaintainCache());
        assertFalse(intermediate.shouldMaintainCache());
        assertFalse(sibling.getDatabaseQueryInternal().shouldMaintainCache());
        assertTransferredCacheState(original, null, store, false);
        assertEquals(raw, cacheAdapterSourceHints(original));
        assertEquals(raw, sibling.getHints());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void batchWriteConversionPreservesCacheStateForQueryTypeAdaptation(boolean batch) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> source = untypedCacheQuery(context);
        source.setHint(QueryHints.MAINTAIN_CACHE, true);
        source.setCacheStoreMode(CacheStoreMode.REFRESH);
        source.setHint(QueryHints.BATCH_WRITING, batch);
        assertInstanceOf(DataModifyQuery.class, source.getDatabaseQueryInternal());
        source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
        assertTransferredCacheState(source.getDatabaseQueryInternal(), null, CacheStoreMode.REFRESH, false);
        assertConfiguredCacheEntity(source, context);
        source.setCacheStoreMode(CacheStoreMode.BYPASS);
        assertTransferredCacheState(source.getDatabaseQueryInternal(), null, CacheStoreMode.BYPASS, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"direct", "modify", "batchTrue", "batchFalse"})
    void genericCopiesPreserveFreshScalarMaintainCacheFalse(String route) {
        for (String storeName : List.of("absent", "USE", "BYPASS")) {
            QueryExecutionContextTest.TestContext context = cacheEntityContext();
            EJBQueryImpl<?> source = untypedCacheQuery(context);
            CacheStoreMode store = storeName.equals("absent") ? null : CacheStoreMode.valueOf(storeName);
            if (store != null) {
                source.setCacheStoreMode(store);
            }
            DatabaseQuery original = source.getDatabaseQueryInternal();
            assertFalse(original.shouldMaintainCache());
            if (route.equals("modify")) {
                source.setHint(QueryHints.QUERY_TYPE, QueryType.DataModify);
            } else if (route.startsWith("batch")) {
                source.setHint(QueryHints.BATCH_WRITING, route.equals("batchTrue"));
            }
            assertFalse(source.getDatabaseQueryInternal().shouldMaintainCache());
            source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
            assertFalse(source.getDatabaseQueryInternal().shouldMaintainCache());
            assertConfiguredCacheEntity(source, context);
            assertTransferredCacheState(source.getDatabaseQueryInternal(), null, store, false);
            assertFalse(source.getHints().containsKey(QueryHints.MAINTAIN_CACHE));
            assertFalse(original.shouldMaintainCache());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void genericNonCachingEntityRetainsRefreshValidation(boolean independent) {
        QueryExecutionContextTest.TestContext context = cacheEntityContext();
        EJBQueryImpl<?> source = untypedCacheQuery(context);
        if (!independent) {
            source.setCacheStoreMode(CacheStoreMode.REFRESH);
        }
        source.setHint(QueryHints.QUERY_TYPE, independent
                ? RefreshingCacheEntityQuery.class.getName() : ConfiguredCacheEntityQuery.class.getName());
        DatabaseQuery target = source.getDatabaseQueryInternal();
        assertFalse(target.shouldMaintainCache());
        assertTrue(((ObjectBuildingQuery) target).shouldRefreshIdentityMapResult());
        QueryException failure = assertThrows(QueryException.class,
                () -> target.checkPrepare(context.session, new DatabaseRecord()));
        assertEquals(QueryException.REFRESH_NOT_POSSIBLE_WITHOUT_CACHE, failure.getErrorCode());
        assertFalse(source.getHints().containsKey(QueryHints.MAINTAIN_CACHE));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preparedReportOverlaysKeepPreparationAndOwnedItems(boolean executionClone) {
        var session = new CacheOverlaySession();
        var context = cacheEntityContext(new QueryExecutionContextTest.TestContext(session, false));
        CacheReportQuery source = new CacheReportQuery();
        source.setName("prepared-report-overlay");
        ConstructorReportItem inner = new ConstructorReportItem("inner");
        inner.setResultType(Inner.class);
        inner.setConstructorArgTypes(new Class<?>[] {int.class});
        inner.addAttribute(source.getExpressionBuilder().get("id"));
        ConstructorReportItem outer = new ConstructorReportItem("outer");
        outer.setResultType(Outer.class);
        outer.setConstructorArgTypes(new Class<?>[] {Inner.class, String.class});
        outer.addItem(inner);
        outer.addAttribute(source.getExpressionBuilder().get("label"));
        source.addConstructorReportItem(outer);
        source.cacheQueryResults();
        source.checkPrepare(session, new DatabaseRecord());
        assertTrue(source.isPrepared());
        // Seed a valid preparation decision which unpreparing the clone clears.
        source.recordNoCustomQueryUsed();
        assertEquals(Boolean.FALSE, source.isCustomQueryUsed());
        ReportQuery unpreparedCopy = (ReportQuery) source.clone();
        assertFalse(unpreparedCopy.isPrepared());
        assertNull(unpreparedCopy.isCustomQueryUsed());
        inner.getReportItems().getFirst().getJoinedAttributeManager().setBaseQuery(source);
        source.setIsExecutionClone(executionClone);
        String preparedSql = source.getSQLString();
        assertNotNull(preparedSql);
        context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE);
        var wrapper = new EJBQueryImpl<>(source, context);

        wrapper.getResultList();

        ReportQuery overlay = (ReportQuery) session.executed;
        assertNotSame(source, overlay);
        assertSame(source, wrapper.getDatabaseQueryInternal());
        assertTrue(overlay.isPrepared());
        assertFalse(overlay.isExecutionClone());
        assertEquals(executionClone, source.isExecutionClone());
        assertEquals(preparedSql, overlay.getSQLString());
        assertEquals(Boolean.FALSE, overlay.isCustomQueryUsed());
        assertNotSame(source.getQueryMechanism(), overlay.getQueryMechanism());
        assertSame(overlay, overlay.getQueryMechanism().getQuery());
        assertNotSame(source.getItems(), overlay.getItems());
        ConstructorReportItem copiedOuter = (ConstructorReportItem) overlay.getItems().getFirst();
        ConstructorReportItem copiedInner = (ConstructorReportItem) copiedOuter.getReportItems().getFirst();
        ReportItem copiedValue = copiedInner.getReportItems().getFirst();
        assertNotSame(outer, copiedOuter);
        assertNotSame(outer.getReportItems(), copiedOuter.getReportItems());
        assertNotSame(outer.getConstructorArgTypes(), copiedOuter.getConstructorArgTypes());
        assertNotSame(inner.getConstructorArgTypes(), copiedInner.getConstructorArgTypes());
        assertNotSame(inner.getReportItems().getFirst().getJoinedAttributeManager(), copiedValue.getJoinedAttributeManager());
        assertSame(overlay, copiedValue.getJoinedAttributeManager().getBaseQuery());
        copiedInner.getConstructorArgTypes()[0] = String.class;
        copiedOuter.getReportItems().clear();
        assertEquals(int.class, inner.getConstructorArgTypes()[0]);
        assertEquals(2, outer.getReportItems().size());
        assertNull(source.getProperty(QueryHints.CACHE_STORE_MODE));
        assertFalse(wrapper.getHints().containsKey(QueryHints.CACHE_STORE_MODE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"disabled", "nullable"})
    void inheritedModesDoNotForceDynamicReportPreparation(String condition) {
        var session = new CacheOverlaySession();
        var context = cacheEntityContext(new QueryExecutionContextTest.TestContext(session, false));
        ReportQuery source = cachedLabelReport();
        if (condition.equals("disabled")) {
            source.setShouldPrepare(false);
        } else {
            source.addArgument("id", Integer.class, true);
            source.setSelectionCriteria(source.getExpressionBuilder().get("id").equal(source.getExpressionBuilder().getParameter("id")));
        }
        context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE);
        var wrapper = new EJBQueryImpl<>(source, context);
        if (condition.equals("nullable")) {
            wrapper.setParameter("id", null);
        }
        wrapper.getResultList();
        assertFalse(source.isPrepared());
        assertFalse(session.executed.isPrepared());
        assertSame(source, wrapper.getDatabaseQueryInternal());
        if (condition.equals("nullable")) {
            wrapper.setParameter("id", 1).getResultList();
            assertTrue(source.isPrepared());
            assertTrue(session.executed.isPrepared());
        } else {
            assertFalse(source.shouldPrepare());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void inheritedRefreshUsesNormalPreparationValidation(boolean report) {
        for (boolean maintainCache : List.of(false, true)) {
            var session = new CacheOverlaySession();
            var context = cacheEntityContext(new QueryExecutionContextTest.TestContext(session, false));
            ObjectLevelReadQuery source = report ? cachedLabelReport() : new ReadAllQuery(CacheEntity.class);
            source.setName("refresh-validation");
            source.setShouldMaintainCache(maintainCache);
            source.checkPrepare(session, new DatabaseRecord());
            assertTrue(source.isPrepared());
            context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
            var wrapper = new EJBQueryImpl<>(source, context);

            wrapper.getResultList();

            ObjectLevelReadQuery overlay = (ObjectLevelReadQuery) session.executed;
            assertFalse(overlay.isPrepared());
            assertTrue(overlay.shouldRefreshIdentityMapResult());
            assertFalse(source.shouldRefreshIdentityMapResult());
            assertTrue(source.isPrepared());
            assertSame(source, wrapper.getDatabaseQueryInternal());
            if (maintainCache) {
                assertDoesNotThrow(() -> overlay.checkPrepare(session, new DatabaseRecord()));
                assertTrue(overlay.isPrepared());
            } else {
                QueryException failure = assertThrows(QueryException.class,
                        () -> overlay.checkPrepare(session, new DatabaseRecord()));
                assertEquals(QueryException.REFRESH_NOT_POSSIBLE_WITHOUT_CACHE, failure.getErrorCode());
            }
        }
    }

    private static ReportQuery cachedLabelReport() {
        ReportQuery query = new CacheReportQuery();
        query.addAttribute("label", query.getExpressionBuilder().get("label"));
        query.cacheQueryResults();
        return query;
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void anonymousReportExecutionKeepsPrivateIdentityAndRestoresFlags(boolean shared) {
        for (boolean executionClone : List.of(false, true)) {
            for (boolean fail : List.of(false, true)) {
                var session = new CacheOverlaySession();
                var context = cacheEntityContext(new QueryExecutionContextTest.TestContext(session, false));
                ReportQuery source = cachedLabelReport();
                source.checkPrepare(session, new DatabaseRecord());
                source.setIsExecutionClone(executionClone);
                var wrapper = new EJBQueryImpl<>(source, context);
                if (!shared) {
                    wrapper.cloneSharedQuery();
                }
                DatabaseQuery previous = wrapper.getDatabaseQueryInternal();
                Map<String, Object> hints = wrapper.getHints();
                context.properties.put(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS);
                context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
                session.duringExecution = query -> {
                    assertTrue(query.shouldRetrieveBypassCache());
                    assertTrue(query.shouldStoreBypassCache());
                    assertFalse(((ReportQuery) query).shouldRefreshIdentityMapResult());
                    assertFalse(query.isExecutionClone());
                    assertNull(query.getProperty(QueryHints.CACHE_RETRIEVE_MODE));
                    assertNull(query.getProperty(QueryHints.CACHE_STORE_MODE));
                    if (fail) {
                        throw new IllegalStateException("execution failure");
                    }
                };
                if (fail) {
                    assertThrows(IllegalStateException.class, wrapper::getResultList);
                } else {
                    wrapper.getResultList();
                }
                DatabaseQuery owned = wrapper.getDatabaseQueryInternal();
                assertSame(owned, session.executed);
                if (shared) {
                    assertNotSame(previous, owned);
                } else {
                    assertSame(previous, owned);
                }
                assertFalse(owned.shouldRetrieveBypassCache());
                assertFalse(owned.shouldStoreBypassCache());
                assertFalse(((ReportQuery) owned).shouldRefreshIdentityMapResult());
                assertEquals(executionClone, owned.isExecutionClone());
                assertEquals(hints, wrapper.getHints());
                assertFalse(source.shouldRetrieveBypassCache());
                assertFalse(source.shouldStoreBypassCache());
                assertEquals(executionClone, source.isExecutionClone());
                session.duringExecution = query -> {
                    assertTrue(query.shouldRetrieveBypassCache());
                    assertTrue(query.shouldStoreBypassCache());
                    assertFalse(query.isExecutionClone());
                };
                wrapper.getResultList();
                assertSame(owned, session.executed);
                assertSame(owned, wrapper.getDatabaseQueryInternal());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void anonymousRefreshRestoresFlagsAndInvalidatesPreparation(boolean maintainCache) {
        var session = new CacheOverlaySession();
        var context = cacheEntityContext(new QueryExecutionContextTest.TestContext(session, false));
        ReportQuery source = cachedLabelReport();
        source.setShouldMaintainCache(maintainCache);
        source.checkPrepare(session, new DatabaseRecord());
        var wrapper = new EJBQueryImpl<>(source, context);
        context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH);
        session.duringExecution = query -> {
            assertTrue(((ReportQuery) query).shouldRefreshIdentityMapResult());
            assertFalse(query.isPrepared());
            assertFalse(query.isExecutionClone());
            query.checkPrepare(session, new DatabaseRecord());
        };
        if (maintainCache) {
            wrapper.getResultList();
        } else {
            jakarta.persistence.PersistenceException failure = assertThrows(jakarta.persistence.PersistenceException.class,
                    wrapper::getResultList);
            assertEquals(QueryException.REFRESH_NOT_POSSIBLE_WITHOUT_CACHE,
                    assertInstanceOf(QueryException.class, failure.getCause()).getErrorCode());
        }
        ReportQuery owned = (ReportQuery) wrapper.getDatabaseQueryInternal();
        assertNotSame(source, owned);
        assertFalse(owned.shouldRefreshIdentityMapResult());
        assertFalse(owned.isExecutionClone());
        assertFalse(owned.isPrepared());
        assertNull(owned.getProperty(QueryHints.CACHE_STORE_MODE));
        assertFalse(wrapper.getHints().containsKey(QueryHints.CACHE_STORE_MODE));
        assertTrue(source.isPrepared());
        assertFalse(source.shouldRefreshIdentityMapResult());
        context.properties.put(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE);
        session.duringExecution = query -> {
            assertFalse(((ReportQuery) query).shouldRefreshIdentityMapResult());
            query.checkPrepare(session, new DatabaseRecord());
        };
        wrapper.getResultList();
        assertSame(owned, session.executed);
        assertTrue(owned.isPrepared());
    }

    private static final class CacheReportQuery extends ReportQuery {
        private CacheReportQuery() {
            super(CacheEntity.class, new ExpressionBuilder(CacheEntity.class));
        }

        private void recordNoCustomQueryUsed() {
            setIsCustomQueryUsed(false);
        }
    }

    private static final class CacheOverlaySession extends DatabaseSessionImpl {
        private DatabaseQuery executed;
        private Consumer<DatabaseQuery> duringExecution;

        private CacheOverlaySession() {
            super(new DatabaseLogin());
        }

        @Override
        public Object executeQuery(DatabaseQuery query, List arguments) {
            executed = query;
            if (duringExecution != null) {
                duringExecution.accept(query);
            }
            return List.of();
        }
    }

    private static EJBQueryImpl<?> cacheShapeQuery(QueryExecutionContextTest.TestContext context,
            String shape, boolean independent) {
        DatabaseQuery query = switch (shape) {
            case "data" -> new DataReadQuery();
            case "modify" -> new DataModifyQuery();
            case "entity" -> new ConfiguredCacheEntityQuery();
            case "mapped" -> new ConfiguredCacheEntityMappingQuery();
            default -> throw new AssertionError(shape);
        };
        query.setSQLString("SELECT ID, LABEL FROM CACHE_ADAPTER_ENTITY");
        query.setIsUserDefined(true);
        if (query instanceof ObjectBuildingQuery building) {
            building.setShouldRefreshIdentityMapResult(independent);
        }
        return new EJBQueryImpl<>(query, context);
    }

    private static String cacheShapeHint(String shape) {
        return switch (shape) {
            case "data" -> QueryType.DataRead;
            case "modify" -> QueryType.DataModify;
            case "entity" -> ConfiguredCacheEntityQuery.class.getName();
            case "mapped" -> ConfiguredCacheEntityMappingQuery.class.getName();
            case "refreshingEntity" -> RefreshingCacheEntityQuery.class.getName();
            default -> throw new AssertionError(shape);
        };
    }

    private static void assertTransferredCacheState(DatabaseQuery query, CacheRetrieveMode retrieve,
            CacheStoreMode store, boolean independent) {
        assertSame(retrieve, query.getProperty(QueryHints.CACHE_RETRIEVE_MODE));
        assertSame(store, query.getProperty(QueryHints.CACHE_STORE_MODE));
        assertEquals(retrieve == CacheRetrieveMode.BYPASS, query.shouldRetrieveBypassCache());
        assertEquals(store == CacheStoreMode.BYPASS, query.shouldStoreBypassCache());
        if (query instanceof ObjectBuildingQuery building) {
            assertEquals(independent || store == CacheStoreMode.REFRESH, building.shouldRefreshIdentityMapResult());
        }
    }

    private static QueryExecutionContextTest.TestContext cacheEntityContext() {
        return cacheEntityContext(new QueryExecutionContextTest.TestContext());
    }

    private static QueryExecutionContextTest.TestContext cacheEntityContext(QueryExecutionContextTest.TestContext context) {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(CacheEntity.class);
        descriptor.setAlias("CacheEntity");
        descriptor.addTableName("CACHE_ADAPTER_ENTITY");
        descriptor.addPrimaryKeyFieldName("CACHE_ADAPTER_ENTITY.ID");
        descriptor.addDirectMapping("id", "CACHE_ADAPTER_ENTITY.ID");
        descriptor.addDirectMapping("label", "CACHE_ADAPTER_ENTITY.LABEL");
        DatabaseSessionImpl session = (DatabaseSessionImpl) context.session;
        session.addDescriptor(descriptor);
        session.initializeDescriptors();
        return context;
    }

    private static EJBQueryImpl<?> untypedCacheQuery(QueryExecutionContextTest.TestContext context) {
        DataReadQuery databaseQuery = new DataReadQuery();
        databaseQuery.setSQLString("SELECT ID, LABEL FROM CACHE_ADAPTER_ENTITY");
        databaseQuery.setIsUserDefined(true);
        return new EJBQueryImpl<>(databaseQuery, context);
    }

    private static EJBQueryImpl<?> adaptCacheQuery(String adapter, EJBQueryImpl<?> source,
            QueryExecutionContextTest.TestContext context) {
        return switch (adapter) {
            case "ofType" -> (EJBQueryImpl<?>) source.ofType(CacheEntity.class);
            case "withResultSetMapping" -> (EJBQueryImpl<?>) source.withResultSetMapping(entity(CacheEntity.class));
            case "typedNamed" -> {
                DatabaseQuery template = (DatabaseQuery) source.getDatabaseQueryInternal().clone();
                // Match addNamedQuery's registration of wrapper-owned read limits.
                ReadQuery read = (ReadQuery) template;
                read.setInternalMax(source.getMaxResultsInternal());
                read.setFirstResult(source.getFirstResult());
                context.session.addQuery("cache-adapter-native", template);
                yield new EJBQueryImpl<>("cache-adapter-native", context, true, CacheEntity.class);
            }
            case "queryType" -> {
                source.setHint(QueryHints.QUERY_TYPE, ConfiguredCacheEntityQuery.class.getName());
                yield source;
            }
            default -> throw new AssertionError(adapter);
        };
    }

    private static void assertConfiguredCacheEntity(EJBQueryImpl<?> query,
            QueryExecutionContextTest.TestContext context) {
        DatabaseQuery databaseQuery = query.getDatabaseQueryInternal();
        assertInstanceOf(ObjectBuildingQuery.class, databaseQuery);
        assertDoesNotThrow(() -> databaseQuery.checkPrepare(context.session, new DatabaseRecord()));
        if (databaseQuery instanceof ReadAllQuery read) {
            assertSame(CacheEntity.class, read.getReferenceClass());
            assertSame(context.session.getDescriptor(CacheEntity.class), read.getDescriptor());
        } else {
            assertInstanceOf(ResultSetMappingQuery.class, databaseQuery);
        }
    }

    private static Map<String, Object> cacheAdapterSourceHints(DatabaseQuery source) {
        Map<String, Object> supplied = QueryHintsHandler.get(source);
        Map<String, Object> hints = supplied == null ? new HashMap<>() : new HashMap<>(supplied);
        // Applying QueryTypeHint records that non-cache hint before changing the query shape.
        hints.remove(QueryHints.QUERY_TYPE);
        return hints;
    }

    private static void assertRawHintsRetained(Map<String, Object> original, EJBQueryImpl<?> adapted) {
        Map<String, Object> actual = adapted.getHints();
        original.forEach((key, value) -> {
            assertTrue(actual.containsKey(key));
            assertSame(value, actual.get(key));
        });
    }

    private static List<Enum<?>> queryCacheModes() {
        return List.of(CacheRetrieveMode.USE, CacheRetrieveMode.BYPASS,
                CacheStoreMode.USE, CacheStoreMode.BYPASS, CacheStoreMode.REFRESH);
    }

    private static List<String> queryCacheAliases(Enum<?> mode) {
        return mode instanceof CacheRetrieveMode
                ? List.of(QueryHints.CACHE_RETRIEVE_MODE, "jakarta.persistence.cacheRetrieveMode")
                : List.of(QueryHints.CACHE_STORE_MODE, "jakarta.persistence.cacheStoreMode");
    }

    private static void setTypedCacheMode(EJBQueryImpl<?> query, Enum<?> mode) {
        if (mode instanceof CacheRetrieveMode retrieve) {
            query.setCacheRetrieveMode(retrieve);
        } else {
            query.setCacheStoreMode((CacheStoreMode) mode);
        }
    }

    private static void assertEffectiveCacheMode(EJBQueryImpl<?> query, Enum<?> expected) {
        if (expected instanceof CacheRetrieveMode retrieve) {
            assertEquals(retrieve, query.getCacheRetrieveMode());
            assertSame(retrieve, query.getDatabaseQueryInternal().getProperty(QueryHints.CACHE_RETRIEVE_MODE));
            assertEquals(retrieve == CacheRetrieveMode.BYPASS, query.getDatabaseQueryInternal().shouldRetrieveBypassCache());
        } else {
            CacheStoreMode store = (CacheStoreMode) expected;
            assertEquals(store, query.getCacheStoreMode());
            assertSame(store, query.getDatabaseQueryInternal().getProperty(QueryHints.CACHE_STORE_MODE));
            assertEquals(store == CacheStoreMode.BYPASS, query.getDatabaseQueryInternal().shouldStoreBypassCache());
        }
    }


    @Test
    void scalarMappingConvertsJdbcNumericValue() {
        assertEquals(7, result(column("TOTAL", Integer.class)));
    }

    @Test
    void compoundPreservesArrayShapeForSingleElement() {
        assertArrayEquals(new Object[] {7}, (Object[]) result(compound(column("TOTAL", Integer.class))));
    }

    @Test
    void tupleSupportsIndexAliasAndElementWithoutLeakingItsArray() {
        var total = column("TOTAL", Integer.class).withAlias("total");
        var name = column("NAME", String.class);
        Tuple tuple = (Tuple) result(tuple(total, name));
        assertEquals(7, tuple.get(0));
        assertEquals(7, tuple.get("total", Integer.class));
        assertEquals(7, tuple.get(total));
        assertEquals("entry", tuple.get(name));
        assertThrows(IllegalArgumentException.class, () -> tuple.get("unknown"));
        assertThrows(IllegalArgumentException.class, () -> tuple.get(-1));
        assertThrows(IllegalArgumentException.class, () -> tuple.get(0, String.class));
        Object[] copy = tuple.toArray();
        copy[0] = 99;
        assertEquals(7, tuple.get(0));
        tuple.getElements().clear();
        assertEquals(2, tuple.getElements().size());
    }

    @Test
    void nestedConstructorsReceiveTypedArguments() {
        var mapping = constructor(Outer.class,
                constructor(Inner.class, column("TOTAL", int.class)), column("NAME", String.class));
        assertEquals(new Outer(new Inner(7), "entry"), result(mapping));
    }

    @Test
    void badConstructorIsReportedAsArgumentError() {
        assertThrows(IllegalArgumentException.class,
                () -> result(constructor(Inner.class, column("NAME", String.class))));
    }

    @Test
    void resultClassMappingConvertsScalarAndConstructsRows() {
        DatabaseRecord row = new DatabaseRecord();
        row.put("TOTAL", BigDecimal.valueOf(7));
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        query.setSession(context.getSession());
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Integer.class, context)));
        assertEquals(7, query.buildObjectsFromRecords(List.of(row), 0).get(0));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Inner.class, context)));
        assertEquals(new Inner(7), query.buildObjectsFromRecords(List.of(row), 0).get(0));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Object[].class, context)));
        assertArrayEquals(new Object[] {BigDecimal.valueOf(7)}, (Object[]) query.buildObjectsFromRecords(List.of(row), 0).get(0));
    }

    @Test
    void namedConstructorMappingPreservesColumnsThenEntitiesAndLocks() {
        ConstructorResult constructor = new ConstructorResult(Outer.class);
        constructor.addColumnResult(new ColumnResult("TOTAL"));
        constructor.addEntityResult(new EntityResult(Inner.class));
        EntityResult locked = new EntityResult(String.class);
        locked.setLockMode("PESSIMISTIC_READ");
        constructor.addEntityResult(locked);
        SQLResultSetMapping named = new SQLResultSetMapping("nested-entities");
        named.addResult(constructor);
        var converted = (ConstructorMapping<?>) ResultSetMappingTranslator.toJakartaMapping(named,
                new DatabaseSessionImpl(new DatabaseLogin()));
        assertEquals(3, converted.arguments().length);
        assertEquals(column("TOTAL", Object.class), converted.arguments()[0]);
        assertEquals(Inner.class, ((EntityMapping<?>) converted.arguments()[1]).entityClass());
        assertEquals(String.class, ((EntityMapping<?>) converted.arguments()[2]).entityClass());
        assertEquals(LockModeType.PESSIMISTIC_READ, ((EntityMapping<?>) converted.arguments()[2]).lockMode());
        assertTrue(ResultSetMappingTranslator.mappingRequiresTransaction(named));
        assertTrue(ResultSetMappingTranslator.mappingRequiresTransaction(converted));
    }

    @Test
    void unlockedEntityArgumentsRequirePreparationWithoutRequiringATransaction() {
        ConstructorResult constructor = new ConstructorResult(Outer.class);
        constructor.addEntityResult(new EntityResult(Inner.class));
        SQLResultSetMapping named = new SQLResultSetMapping("unlocked-entities");
        named.addResult(constructor);
        assertTrue(ResultSetMappingTranslator.mappingRequiresPreparation(named));
        assertFalse(ResultSetMappingTranslator.mappingRequiresTransaction(named));
        ResultSetMapping<?> converted = ResultSetMappingTranslator.toJakartaMapping(named,
                new DatabaseSessionImpl(new DatabaseLogin()));
        assertFalse(ResultSetMappingTranslator.mappingRequiresTransaction(converted));
    }

    @Test
    void legacyColumnOnlyMappingDoesNotRequireEntityPreparation() {
        ConstructorResult constructor = new ConstructorResult(Inner.class);
        constructor.addColumnResult(new ColumnResult("TOTAL"));
        SQLResultSetMapping named = new SQLResultSetMapping("column-constructor");
        named.addResult(constructor);
        assertFalse(ResultSetMappingTranslator.mappingRequiresPreparation(named));
        assertFalse(ResultSetMappingTranslator.mappingRequiresTransaction(named));
        assertFalse(ResultSetMappingTranslator.mappingRequiresPreparation(null));
    }

    @Test
    void inlineConstructorEntityLocksRequireTransactionBeforeExecution() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        ResultSetMappingQuery read = new ResultSetMappingQuery();
        read.addSQLResultSetMapping(lockedConstructorMapping("inline-constructor"));
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(read, context);
        assertThrows(TransactionRequiredException.class, query::getResultList);
        assertEquals(0, context.executions);
        assertNull(read.getSession());
    }

    @Test
    void namedConstructorEntityLocksRequireTransactionBeforeExecution() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        SQLResultSetMapping mapping = lockedConstructorMapping("named-constructor");
        context.session.getProject().addSQLResultSetMapping(mapping);
        ResultSetMappingQuery read = new ResultSetMappingQuery();
        read.addSQLResultSetMappingName(mapping.getName());
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(read, context);
        assertThrows(TransactionRequiredException.class, query::getResultList);
        assertEquals(0, context.executions);
        assertNull(read.getSession());
    }

    @Test
    void directMappingsTakePrecedenceOverAllRecordedNamesWithoutChangingTheSession() {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        SQLResultSetMapping local = lockedConstructorMapping("shared-name");
        SQLResultSetMapping registered = new SQLResultSetMapping("shared-name");
        session.getProject().addSQLResultSetMapping(registered);
        ResultSetMappingQuery read = new ResultSetMappingQuery();
        read.addSQLResultSetMapping(local);
        read.addSQLResultSetMappingName("missing-name");
        assertEquals(List.of(local), read.getSQLResultSetMappings(session));
        assertEquals(List.of(local), read.getSQLResultSetMappings());
        assertNull(read.getSession());
        read.setSQLResultSetMappings(List.of());
        List<SQLResultSetMapping> resolved = read.getSQLResultSetMappings(session);
        assertEquals(2, resolved.size());
        assertSame(registered, resolved.get(0));
        assertNull(resolved.get(1));
        assertNull(read.getSession());
    }

    @Test
    void typedNamedFactoryPreservesExplicitConstructorOrderForInlineAndNamedMappings() {
        for (boolean byName : List.of(false, true)) {
            for (Class<?> requested : List.of(Reordered.class, Object.class)) {
                QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
                SQLResultSetMapping mapping = reorderedMapping();
                ResultSetMappingQuery named = register(context, mapping, byName);
                EJBQueryImpl<?> typed = new EJBQueryImpl<>(named.getName(), context, true, requested);
                ResultSetMappingQuery adapted = (ResultSetMappingQuery) typed.getDatabaseQueryInternal();
                adapted.setSession(context.session);
                DatabaseRecord row = new DatabaseRecord();
                row.put("LABEL", "entry");
                row.put("NUMBER", BigDecimal.valueOf(7));
                assertEquals(new Reordered(7, "entry"), adapted.buildObjectsFromRecords(List.of(row), 0).getFirst());
                assertSame(mapping, adapted.getSQLResultSetMappings().getFirst());
                assertNull(named.getSession());
                assertNull(named.getResultClass());
                assertThrows(IllegalArgumentException.class, () -> typed.ofType(Inner.class));
            }
        }
    }

    @Test
    void typedNamedFactoryRejectsIncompatibleExplicitConstructorType() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        ResultSetMappingQuery named = register(context, reorderedMapping(), false);
        assertThrows(IllegalArgumentException.class,
                () -> new EJBQueryImpl<>(named.getName(), context, true, Inner.class));
        assertNull(named.getResultClass());
    }

    @Test
    void typedNamedFactoryRetainsNestedEntityLockObligations() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        ResultSetMappingQuery named = register(context, lockedConstructorMapping("typed-lock"), false);
        EJBQueryImpl<Outer> typed = new EJBQueryImpl<>(named.getName(), context, true, Outer.class);
        assertThrows(TransactionRequiredException.class, typed::getResultList);
        assertEquals(0, context.executions);
    }

    @Test
    void typedNamedFactoryPreservesMixedAndTypedScalarMappingShapes() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        SQLResultSetMapping scalar = new SQLResultSetMapping("typed-scalar");
        scalar.addResult(typedColumn("NUMBER", Integer.class));
        ResultSetMappingQuery named = register(context, scalar, false);
        EJBQueryImpl<?> typed = new EJBQueryImpl<>(named.getName(), context, true, Number.class);
        assertSame(scalar, ((ResultSetMappingQuery) typed.getDatabaseQueryInternal())
                .getSQLResultSetMappings(context.session).getFirst());
        String scalarName = named.getName();
        assertThrows(IllegalArgumentException.class,
                () -> new EJBQueryImpl<>(scalarName, context, true, String.class));

        SQLResultSetMapping mixed = reorderedMapping();
        mixed.addResult(typedColumn("LABEL", String.class));
        named = register(context, mixed, false);
        EJBQueryImpl<?> array = new EJBQueryImpl<>(named.getName(), context, true, Object[].class);
        assertSame(mixed, ((ResultSetMappingQuery) array.getDatabaseQueryInternal())
                .getSQLResultSetMappings(context.session).getFirst());
        String mixedName = named.getName();
        assertThrows(IllegalArgumentException.class,
                () -> new EJBQueryImpl<>(mixedName, context, true, Reordered.class));
    }

    @Test
    void inferredNativeResultClassCanStillBeRepackagedByTheTypedFactory() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        ResultSetMappingQuery named = new ResultSetMappingQuery();
        named.setResultClass(Inner.class);
        named.setSQLString("SELECT NUMBER FROM SAMPLE");
        named.setIsUserDefined(true);
        named.setIsPrepared(true);
        context.session.addQuery("inferred-native", named);
        EJBQueryImpl<?> typed = new EJBQueryImpl<>("inferred-native", context, true, Integer.class);
        assertEquals(Integer.class, ((ResultSetMappingQuery) typed.getDatabaseQueryInternal()).getResultClass());
        assertEquals(Inner.class, named.getResultClass());
        assertThrows(IllegalArgumentException.class,
                () -> new EJBQueryImpl<>("inferred-native", context, true).ofType(Integer.class));
    }

    @Test
    void typedNamedFactoryPreservesEntityAliasesAndProgrammaticMappings() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        EntityResult entity = new EntityResult(Inner.class);
        entity.addFieldResult(new FieldResult("total", "NUMBER_ALIAS"));
        SQLResultSetMapping mapping = new SQLResultSetMapping("entity-alias");
        mapping.addResult(entity);
        ResultSetMappingQuery named = register(context, mapping, false);
        EJBQueryImpl<?> typed = new EJBQueryImpl<>(named.getName(), context, true, Inner.class);
        assertSame(entity, ((ResultSetMappingQuery) typed.getDatabaseQueryInternal())
                .getSQLResultSetMappings(context.session).getFirst().getResults().getFirst());

        SQLResultSetMapping programmatic = ResultSetMappingTranslator.toEclipseLinkMapping(
                constructor(Reordered.class, column("NUMBER", Integer.class), column("LABEL", String.class)));
        named = register(context, programmatic, false);
        typed = new EJBQueryImpl<>(named.getName(), context, true, Reordered.class);
        ResultSetMappingQuery adapted = (ResultSetMappingQuery) typed.getDatabaseQueryInternal();
        adapted.setSession(context.session);
        DatabaseRecord row = new DatabaseRecord();
        row.put("LABEL", "entry");
        row.put("NUMBER", BigDecimal.valueOf(7));
        assertEquals(new Reordered(7, "entry"), adapted.buildObjectsFromRecords(List.of(row), 0).getFirst());
        assertSame(programmatic, adapted.getSQLResultSetMappings().getFirst());
    }

    @Test
    void typedNativeFactoryAcceptsPrimitiveAndBoxedScalarCompatibility() {
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        SQLResultSetMapping scalar = new SQLResultSetMapping("primitive-scalar");
        scalar.addResult(typedColumn("NUMBER", int.class));
        ResultSetMappingQuery named = register(context, scalar, false);
        for (Class<?> requested : List.of(Integer.class, Number.class, int.class)) {
            EJBQueryImpl<?> typed = new EJBQueryImpl<>(named.getName(), context, true, requested);
            assertSame(scalar, ((ResultSetMappingQuery) typed.getDatabaseQueryInternal())
                    .getSQLResultSetMappings(context.session).getFirst());
            assertSame(typed, typed.ofType(Number.class));
            assertSame(typed, typed.ofType(Object.class));
            assertSame(typed, typed.ofType(Integer.class));
            assertSame(typed, typed.ofType(int.class));
            assertThrows(IllegalArgumentException.class, () -> typed.ofType(Double.class));
            assertThrows(IllegalArgumentException.class, () -> typed.ofType(long.class));
        }
    }

    private static SQLResultSetMapping reorderedMapping() {
        ConstructorResult constructor = new ConstructorResult(Reordered.class);
        constructor.addColumnResult(typedColumn("NUMBER", Integer.class));
        constructor.addColumnResult(typedColumn("LABEL", String.class));
        SQLResultSetMapping mapping = new SQLResultSetMapping("reordered-constructor");
        mapping.addResult(constructor);
        return mapping;
    }

    private static ColumnResult typedColumn(String name, Class<?> type) {
        DatabaseField field = new DatabaseField(name);
        field.setType(type);
        return new ColumnResult(field);
    }

    private static ResultSetMappingQuery register(QueryExecutionContextTest.TestContext context,
            SQLResultSetMapping mapping, boolean byName) {
        ResultSetMappingQuery named = new ResultSetMappingQuery();
        named.setSQLString("SELECT LABEL, NUMBER FROM SAMPLE");
        named.setIsUserDefined(true);
        named.setIsPrepared(true);
        if (byName) {
            context.session.getProject().addSQLResultSetMapping(mapping);
            named.addSQLResultSetMappingName(mapping.getName());
        } else {
            named.addSQLResultSetMapping(mapping);
        }
        context.session.addQuery(mapping.getName(), named);
        return named;
    }

    private static SQLResultSetMapping lockedConstructorMapping(String name) {
        EntityResult entity = new EntityResult(Inner.class);
        entity.setLockMode("PESSIMISTIC_WRITE");
        ConstructorResult constructor = new ConstructorResult(Outer.class);
        constructor.addEntityResult(entity);
        SQLResultSetMapping mapping = new SQLResultSetMapping(name);
        mapping.addResult(constructor);
        return mapping;
    }

    private static Object result(ResultSetMapping<?> mapping) {
        DatabaseRecord row = new DatabaseRecord();
        row.put("TOTAL", BigDecimal.valueOf(7));
        row.put("NAME", "entry");
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSession(new DatabaseSessionImpl(new DatabaseLogin()));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.toEclipseLinkMapping(mapping)));
        return query.buildObjectsFromRecords(List.of(row), 0).get(0);
    }

    public static class CacheEntity {
        public Integer id;
        public String label;

        public CacheEntity() {
        }
    }

    public static class ConfiguredCacheEntityQuery extends ReadAllQuery {
        public ConfiguredCacheEntityQuery() {
            super(CacheEntity.class);
        }
    }

    public static class RefreshingCacheEntityQuery extends ReadAllQuery {
        public RefreshingCacheEntityQuery() {
            super(CacheEntity.class);
            refreshIdentityMapResult();
        }
    }

    public static class NonCachingCacheEntityQuery extends ReadAllQuery {
        public NonCachingCacheEntityQuery() {
            super(CacheEntity.class);
            setShouldMaintainCache(false);
        }
    }

    public static class ConfiguredCacheEntityMappingQuery extends ResultSetMappingQuery {
        public ConfiguredCacheEntityMappingQuery() {
            setReferenceClass(CacheEntity.class);
            SQLResultSetMapping mapping = new SQLResultSetMapping("cache-shape-entity");
            mapping.addResult(new EntityResult(CacheEntity.class));
            addSQLResultSetMapping(mapping);
        }
    }

    public record Inner(int total) {
    }

    public record Outer(Inner inner, String name) {
    }

    public record Reordered(Integer number, String label) {
    }
}
