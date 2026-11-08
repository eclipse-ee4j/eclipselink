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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryHint;
import jakarta.persistence.query.NativeQuery;
import jakarta.persistence.query.QueryOptions;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.QueryHintsHandler;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeQueryMetadataTest {
    private final ServerSession session = new ServerSession();
    private final MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(session), getClass().getClassLoader());
    private final MetadataProject project = new MetadataProject(new SEPersistenceUnitInfo(), session,
            false, false, false, false, false);

    static Stream<Arguments> resultTypes() {
        return Stream.of(
                Arguments.of("direct", Result.class.getName()),
                Arguments.of("list", Result.class.getName()),
                Arguments.of("stream", Result.class.getName()),
                Arguments.of("scalar", "java.lang.String"),
                Arguments.of("primitive", "int"),
                Arguments.of("row", "[Ljava.lang.Object;"),
                Arguments.of("array", "java.lang.String[]"),
                Arguments.of("primitiveArray", "[B"),
                Arguments.of("nestedGeneric", GenericOuter.Member.class.getName()),
                Arguments.of("genericParameters", Result.class.getName()));
    }

    @ParameterizedTest
    @MethodSource("resultTypes")
    void infersReturnShapeWithoutParameterOrOwnerGenericArguments(String method, String expected) {
        assertEquals(expected, query(Returns.class, method).getResultClass().getName());
    }

    @Test
    void explicitMappingDoesNotRequireCollectionInference() {
        NativeQueryMetadata query = query(Returns.class, "explicit");
        assertEquals(1, query.getConstructorResults().size());
        assertEquals(List.of("SECOND", "FIRST"), query.getConstructorResults().getFirst().getColumnResults()
                .stream().map(ColumnResultMetadata::getName).toList());
    }

    @Test
    void preservesRepeatedScalarMappingsInDeclarationOrder() {
        assertEquals(List.of("SECOND", "FIRST"), query(Returns.class, "columns").getColumnResults()
                .stream().map(ColumnResultMetadata::getName).toList());
    }

    @Test
    void canonicalNamesPreserveOverloadsAndArrayTypes() {
        TestAccessor accessor = accessor(Names.class);
        NativeQueryMetadata.process(accessor.type, accessor);
        String prefix = Names.class.getCanonicalName() + "#";
        assertEquals(Set.of(prefix + "lookup()", prefix + "lookup(int)",
                        prefix + "lookup(java.lang.String,byte[],java.lang.String[][])",
                        prefix + "member(" + Member$Type.class.getCanonicalName() + "[])"),
                project.getNamedQueries(accessor).stream().map(NamedQueryMetadata::getName).collect(Collectors.toSet()));
        assertEquals(Names.class.getName(), accessor.type.getName(), "Binary loading identity is unchanged");
    }

    @Test
    void literalDollarInTopLevelNameIsNotRewritten() {
        NativeQueryMetadata query = query(Native$QueryType.class, "lookup");
        assertEquals(Native$QueryType.class.getCanonicalName() + "#lookup()", query.getName());
    }

    @Test
    void syntheticCovariantBridgeDoesNotCreateConflictingQuery() {
        TestAccessor accessor = accessor(Covariant.class);
        NativeQueryMetadata.process(accessor.type, accessor);
        NativeQueryMetadata.process(accessor.type, accessor);
        assertEquals(1, project.getNamedQueries(accessor).size());
        assertEquals("java.lang.String", project.getNamedQueries(accessor).iterator().next().getResultClass().getName());
    }

    @Test
    void metadataDiscoveryDoesNotInitializeOrInvokeDeclaringClass() {
        assertFalse(Initialization.initialized);
        TestAccessor accessor = accessor(NoInitialization.class);
        NativeQueryMetadata.process(accessor.type, accessor);
        assertEquals(1, project.getNamedQueries(accessor).size());
        assertFalse(Initialization.initialized);
    }

    @Test
    void queryOptionsPresenceAppliesCacheDefaultsOnlyToThatDeclaration() {
        assertTrue(query(Options.class, "absent").getHints().isEmpty());
        assertEquals(Map.of(QueryHints.CACHE_STORE_MODE, "USE", QueryHints.CACHE_RETRIEVE_MODE, "USE"),
                hints(query(Options.class, "defaults")));
        assertEquals(Map.of(QueryHints.CACHE_STORE_MODE, "BYPASS", QueryHints.CACHE_RETRIEVE_MODE, "BYPASS",
                        QueryHints.QUERY_TIMEOUT, "2500", QueryHints.FLUSH, "false", "application.marker", "retained"),
                hints(query(Options.class, "explicit")));
        assertTrue(query(Options.class, "absent").getHints().isEmpty());
    }

    @Test
    void explicitHintsKeepExistingNamedQueryPrecedence() {
        Map<String, String> hints = hints(query(Options.class, "hintOverride"));
        assertEquals("BYPASS", hints.get(QueryHints.CACHE_STORE_MODE));
        assertEquals("true", hints.get(QueryHints.FLUSH));
    }

    static Stream<Arguments> cacheQueryTypes() {
        return Stream.of(
                Arguments.of((Supplier<DatabaseQuery>) ReadAllQuery::new),
                Arguments.of((Supplier<DatabaseQuery>) () -> {
                    ReadAllQuery query = new ReadAllQuery();
                    query.setSQLString("SELECT ID FROM CACHE_OPTION_SAMPLE");
                    query.setIsUserDefined(true);
                    return query;
                }),
                Arguments.of((Supplier<DatabaseQuery>) ResultSetMappingQuery::new),
                Arguments.of((Supplier<DatabaseQuery>) DataReadQuery::new),
                Arguments.of((Supplier<DatabaseQuery>) DataModifyQuery::new));
    }

    static Stream<Arguments> cacheHintDefaults() {
        return Stream.of(
                Arguments.of(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE),
                Arguments.of("jakarta.persistence.cacheRetrieveMode", CacheRetrieveMode.USE),
                Arguments.of(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE),
                Arguments.of("jakarta.persistence.cacheStoreMode", CacheStoreMode.USE));
    }

    @ParameterizedTest
    @MethodSource("cacheHintDefaults")
    void emptyCacheHintAppliesItsDeclaredUseDefault(String alias, Enum<?> expected) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        QueryHintsHandler.apply(alias, "", query, null, null);
        String canonical = expected instanceof CacheRetrieveMode ? QueryHints.CACHE_RETRIEVE_MODE : QueryHints.CACHE_STORE_MODE;
        assertEquals(expected, query.getProperty(canonical));
        assertEquals("", ((Map<?, ?>) query.getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY)).get(alias));
        assertFalse(query.shouldRetrieveBypassCache());
        assertFalse(query.shouldStoreBypassCache());
        assertFalse(query.shouldRefreshIdentityMapResult());
    }

    @ParameterizedTest
    @MethodSource("cacheHintDefaults")
    void nullCacheHintsRetainFailureAndPreviousSemanticState(String alias, Enum<?> defaultMode) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        Object previous = defaultMode instanceof CacheRetrieveMode ? CacheRetrieveMode.BYPASS : CacheStoreMode.BYPASS;
        QueryHintsHandler.apply(alias, previous, query, null, null);
        DatabaseQuery sibling = (DatabaseQuery) query.clone();
        String canonical = defaultMode instanceof CacheRetrieveMode ? QueryHints.CACHE_RETRIEVE_MODE : QueryHints.CACHE_STORE_MODE;
        Object previousCanonical = query.getProperty(canonical);
        boolean retrieveBypass = query.shouldRetrieveBypassCache();
        boolean storeBypass = query.shouldStoreBypassCache();
        assertThrows(NullPointerException.class, () -> QueryHintsHandler.apply(alias, null, query, null, null));
        assertEquals(previousCanonical, query.getProperty(canonical));
        assertEquals(retrieveBypass, query.shouldRetrieveBypassCache());
        assertEquals(storeBypass, query.shouldStoreBypassCache());
        Map<?, ?> raw = (Map<?, ?>) query.getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY);
        assertTrue(raw.containsKey(alias));
        assertEquals(null, raw.get(alias));
        assertEquals(previous, ((Map<?, ?>) sibling.getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY)).get(alias));
        QueryHintsHandler.apply(alias, defaultMode, query, null, null);
        assertFalse(query.shouldRetrieveBypassCache());
        assertFalse(query.shouldStoreBypassCache());
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void acceptedCacheInputsPreserveCanonicalEnumsAndExactRawValues(Supplier<DatabaseQuery> create) {
        for (Enum<?> mode : cacheModes()) {
            for (String alias : cacheAliases(mode)) {
                for (Object raw : List.of(mode, mode.name())) {
                    DatabaseQuery query = create.get();
                    QueryHintsHandler.apply(alias, raw, query, null, null);
                    assertCacheMode(query, mode, false);
                    assertSame(raw, rawCacheHints(query).get(alias));
                    assertEquals(1, rawCacheHints(query).size());
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void everyAcceptedCacheTransitionKeepsTheLastSemanticValue(Supplier<DatabaseQuery> create) {
        for (Enum<?> first : cacheModes()) {
            for (Enum<?> second : cacheModes()) {
                if (first.getDeclaringClass() != second.getDeclaringClass()) {
                    continue;
                }
                for (String firstAlias : cacheAliases(first)) {
                    for (String secondAlias : cacheAliases(second)) {
                        for (Object firstRaw : List.of(first, first.name())) {
                            for (Object secondRaw : List.of(second, second.name())) {
                                DatabaseQuery query = create.get();
                                QueryHintsHandler.apply(firstAlias, firstRaw, query, null, null);
                                assertCacheMode(query, first, false);
                                QueryHintsHandler.apply(secondAlias, secondRaw, query, null, null);
                                assertCacheMode(query, second, false);
                                assertSame(secondRaw, rawCacheHints(query).get(secondAlias));
                                if (!firstAlias.equals(secondAlias)) {
                                    assertSame(firstRaw, rawCacheHints(query).get(firstAlias));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void emptyCacheInputsRestoreUseAndIndependentRefreshAfterEveryMode(Supplier<DatabaseQuery> create) {
        for (Enum<?> previous : cacheModes()) {
            for (String previousAlias : cacheAliases(previous)) {
                for (String emptyAlias : cacheAliases(previous)) {
                    for (boolean independent : List.of(false, true)) {
                        DatabaseQuery query = create.get();
                        if (query instanceof ObjectBuildingQuery building) {
                            building.setShouldRefreshIdentityMapResult(independent);
                        }
                        QueryHintsHandler.apply(previousAlias, previous, query, null, null);
                        QueryHintsHandler.apply(emptyAlias, "", query, null, null);
                        assertCacheMode(query, cacheUse(previous), independent);
                        assertEquals("", rawCacheHints(query).get(emptyAlias));
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void ignoredAndNullCacheInputsCannotReplaceAcceptedSemanticState(Supplier<DatabaseQuery> create) {
        for (Enum<?> previous : cacheModes()) {
            List<Object> inputs = invalidCacheInputs(previous);
            inputs.add(null);
            for (String alias : cacheAliases(previous)) {
                for (Object input : inputs) {
                    for (boolean accepted : List.of(false, true)) {
                        DatabaseQuery query = create.get();
                        if (accepted) {
                            QueryHintsHandler.apply(cacheKey(previous), previous, query, null, null);
                        }
                        DatabaseQuery sibling = (DatabaseQuery) query.clone();
                        Object canonical = query.getProperty(cacheKey(previous));
                        boolean retrieve = query.shouldRetrieveBypassCache();
                        boolean store = query.shouldStoreBypassCache();
                        boolean refresh = query instanceof ObjectBuildingQuery building && building.shouldRefreshIdentityMapResult();
                        if (input == null) {
                            assertThrows(NullPointerException.class, () -> QueryHintsHandler.apply(alias, null, query, null, null));
                        } else {
                            QueryHintsHandler.apply(alias, input, query, null, null);
                        }
                        assertSame(canonical, query.getProperty(cacheKey(previous)));
                        assertEquals(retrieve, query.shouldRetrieveBypassCache());
                        assertEquals(store, query.shouldStoreBypassCache());
                        if (query instanceof ObjectBuildingQuery building) {
                            assertEquals(refresh, building.shouldRefreshIdentityMapResult());
                        }
                        assertTrue(rawCacheHints(query).containsKey(alias));
                        assertSame(input, rawCacheHints(query).get(alias));
                        assertSame(canonical, sibling.getProperty(cacheKey(previous)));
                        if (accepted) {
                            assertSame(previous, rawCacheHints(sibling).get(cacheKey(previous)));
                        } else {
                            assertNull(sibling.getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY));
                        }
                        QueryHintsHandler.apply(alias, cacheUse(previous), query, null, null);
                        assertCacheMode(query, cacheUse(previous), false);
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("entityCacheQueryTypes")
    void aliasAndEmptyTransitionsPreserveProviderRefreshAcrossClones(Supplier<ObjectBuildingQuery> create) {
        for (boolean initial : List.of(false, true)) {
            for (Boolean provider : java.util.Arrays.asList(null, Boolean.FALSE, Boolean.TRUE)) {
                for (String firstAlias : cacheAliases(CacheStoreMode.REFRESH)) {
                    for (String secondAlias : cacheAliases(CacheStoreMode.REFRESH)) {
                        for (Object replacement : List.of(CacheStoreMode.USE, "USE", CacheStoreMode.BYPASS, "BYPASS", "")) {
                            ObjectBuildingQuery query = create.get();
                            query.setShouldRefreshIdentityMapResult(initial);
                            QueryHintsHandler.apply(firstAlias, CacheStoreMode.REFRESH, query, null, null);
                            ObjectBuildingQuery sibling = (ObjectBuildingQuery) query.clone();
                            if (provider != null) {
                                QueryHintsHandler.apply(QueryHints.REFRESH, provider, query, null, null);
                            }
                            QueryHintsHandler.apply(secondAlias, "ignored-value", query, null, null);
                            assertTrue(query.shouldRefreshIdentityMapResult());
                            assertCacheMode(query, CacheStoreMode.REFRESH, initial);
                            QueryHintsHandler.apply(secondAlias, replacement, query, null, null);
                            CacheStoreMode expected = replacement.equals(CacheStoreMode.BYPASS) || replacement.equals("BYPASS")
                                    ? CacheStoreMode.BYPASS : CacheStoreMode.USE;
                            assertCacheMode(query, expected, provider == null ? initial : provider);
                            assertCacheMode(sibling, CacheStoreMode.REFRESH, initial);
                            QueryHintsHandler.apply(secondAlias, "", sibling, null, null);
                            assertCacheMode(sibling, CacheStoreMode.USE, initial);
                        }
                    }
                }
            }
        }
    }

    @Test
    void orderedBulkAliasesFollowApplicationOrderAndKeepRawHistory() {
        for (Enum<?> first : cacheModes()) {
            Enum<?> second = first.name().equals("USE") ? cacheBypass(first) : cacheUse(first);
            for (boolean reverse : List.of(false, true)) {
                List<String> aliases = cacheAliases(first);
                Map<String, Object> hints = new LinkedHashMap<>();
                hints.put(aliases.get(reverse ? 1 : 0), first);
                hints.put(aliases.get(reverse ? 0 : 1), second.name());
                DatabaseQuery query = new ResultSetMappingQuery();
                QueryHintsHandler.apply(hints, query, null, null);
                assertCacheMode(query, second, false);
                assertEquals(hints, rawCacheHints(query));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void cacheHintClonesKeepAcceptedAndRawStateIndependent(Supplier<DatabaseQuery> create) {
        for (Enum<?> initial : cacheModes()) {
            DatabaseQuery template = create.get();
            QueryHintsHandler.apply(cacheKey(initial), initial, template, null, null);
            DatabaseQuery changed = (DatabaseQuery) template.clone();
            String legacy = cacheAliases(initial).get(1);
            QueryHintsHandler.apply(legacy, cacheUse(initial).name(), changed, null, null);
            assertCacheMode(changed, cacheUse(initial), false);
            assertCacheMode(template, initial, false);
            assertFalse(rawCacheHints(template).containsKey(legacy));
            QueryHintsHandler.apply(legacy, "", changed, null, null);
            assertEquals("", rawCacheHints(changed).get(legacy));
            QueryHintsHandler.apply(legacy, "ignored-value", changed, null, null);
            assertCacheMode(changed, cacheUse(initial), false);
            assertThrows(NullPointerException.class, () -> QueryHintsHandler.apply(legacy, null, changed, null, null));
            assertCacheMode(changed, cacheUse(initial), false);
            assertCacheMode(template, initial, false);
            assertSame(initial, rawCacheHints(template).get(cacheKey(initial)));
        }
    }

    private static List<Enum<?>> cacheModes() {
        return List.of(CacheRetrieveMode.USE, CacheRetrieveMode.BYPASS,
                CacheStoreMode.USE, CacheStoreMode.BYPASS, CacheStoreMode.REFRESH);
    }

    private static String cacheKey(Enum<?> mode) {
        return mode instanceof CacheRetrieveMode ? QueryHints.CACHE_RETRIEVE_MODE : QueryHints.CACHE_STORE_MODE;
    }

    private static List<String> cacheAliases(Enum<?> mode) {
        return List.of(cacheKey(mode), mode instanceof CacheRetrieveMode
                ? "jakarta.persistence.cacheRetrieveMode" : "jakarta.persistence.cacheStoreMode");
    }

    private static Enum<?> cacheUse(Enum<?> mode) {
        return mode instanceof CacheRetrieveMode ? CacheRetrieveMode.USE : CacheStoreMode.USE;
    }

    private static Enum<?> cacheBypass(Enum<?> mode) {
        return mode instanceof CacheRetrieveMode ? CacheRetrieveMode.BYPASS : CacheStoreMode.BYPASS;
    }

    private static Map<?, ?> rawCacheHints(DatabaseQuery query) {
        return (Map<?, ?>) query.getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY);
    }

    private static List<Object> invalidCacheInputs(Enum<?> family) {
        List<Object> inputs = new ArrayList<>(List.of("unknown", "use", " USE ", "bypass", " BYPASS ",
                "refresh", " REFRESH ", 17, new Object() {
                    @Override
                    public String toString() {
                        return "USE";
                    }
                }));
        inputs.addAll(family instanceof CacheRetrieveMode ? List.of(CacheStoreMode.values()) : List.of(CacheRetrieveMode.values()));
        return inputs;
    }

    private static void assertCacheMode(DatabaseQuery query, Enum<?> expected, boolean independentRefresh) {
        assertSame(expected, query.getProperty(cacheKey(expected)), expected + " canonical enum");
        if (expected instanceof CacheRetrieveMode) {
            assertEquals(expected == CacheRetrieveMode.BYPASS, query.shouldRetrieveBypassCache());
            if (query instanceof ObjectBuildingQuery building) {
                assertEquals(independentRefresh, building.shouldRefreshIdentityMapResult());
            }
        } else {
            assertEquals(expected == CacheStoreMode.BYPASS, query.shouldStoreBypassCache());
            if (query instanceof ObjectBuildingQuery building) {
                assertEquals(expected == CacheStoreMode.REFRESH || independentRefresh,
                        building.shouldRefreshIdentityMapResult(), expected + " refresh flag");
            }
        }
    }

    @ParameterizedTest
    @MethodSource("cacheQueryTypes")
    void cacheHintsApplyToNativeAndObjectQueriesWithoutChangingSiblingDefaults(Supplier<DatabaseQuery> create) {
        DatabaseQuery query = create.get();
        QueryHintsHandler.apply(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS, query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, "BYPASS", query, null, null);
        assertTrue(query.shouldRetrieveBypassCache());
        assertTrue(query.shouldStoreBypassCache());
        QueryHintsHandler.apply(QueryHints.CACHE_RETRIEVE_MODE, "USE", query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE, query, null, null);
        assertFalse(query.shouldRetrieveBypassCache());
        assertFalse(query.shouldStoreBypassCache());
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
        assertFalse(query.shouldStoreBypassCache());
        if (query instanceof ObjectBuildingQuery building) {
            assertTrue(building.shouldRefreshIdentityMapResult());
        }
        DatabaseQuery sibling = create.get();
        assertFalse(sibling.shouldRetrieveBypassCache());
        assertFalse(sibling.shouldStoreBypassCache());
        if (sibling instanceof ObjectBuildingQuery building) {
            assertFalse(building.shouldRefreshIdentityMapResult());
        }
    }

    @Test
    void unrecognizedCacheHintValuesKeepExistingObjectQueryBehavior() {
        ReadAllQuery query = new ReadAllQuery();
        query.retrieveBypassCache();
        query.storeBypassCache();
        QueryHintsHandler.apply(QueryHints.CACHE_RETRIEVE_MODE, "unknown", query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, "unknown", query, null, null);
        assertTrue(query.shouldRetrieveBypassCache());
        assertTrue(query.shouldStoreBypassCache());
        assertFalse(query.shouldRefreshIdentityMapResult());
    }

    static Stream<Arguments> entityCacheQueryTypes() {
        return Stream.of(
                Arguments.of((Supplier<ObjectBuildingQuery>) ReadAllQuery::new),
                Arguments.of((Supplier<ObjectBuildingQuery>) ResultSetMappingQuery::new));
    }

    @ParameterizedTest
    @MethodSource("entityCacheQueryTypes")
    void reversingCacheRefreshRestoresIndependentRefreshState(Supplier<ObjectBuildingQuery> create) {
        for (CacheStoreMode replacement : List.of(CacheStoreMode.BYPASS, CacheStoreMode.USE)) {
            ObjectBuildingQuery query = create.get();
            QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
            ObjectBuildingQuery sibling = (ObjectBuildingQuery) query.clone();
            QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, replacement, query, null, null);
            assertFalse(query.shouldRefreshIdentityMapResult(), replacement.name());
            assertTrue(sibling.shouldRefreshIdentityMapResult());
            QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
            assertTrue(query.shouldRefreshIdentityMapResult());
            QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, replacement, query, null, null);
            assertFalse(query.shouldRefreshIdentityMapResult());
        }
    }

    @ParameterizedTest
    @MethodSource("entityCacheQueryTypes")
    void cacheModesDoNotEraseIndependentProviderRefresh(Supplier<ObjectBuildingQuery> create) {
        ObjectBuildingQuery query = create.get();
        query.refreshIdentityMapResult();
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE, query, null, null);
        assertTrue(query.shouldRefreshIdentityMapResult(), "Programmatic refresh remains enabled");
        QueryHintsHandler.apply(QueryHints.REFRESH, false, query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
        QueryHintsHandler.apply(QueryHints.REFRESH, true, query, null, null);
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS, query, null, null);
        assertTrue(query.shouldRefreshIdentityMapResult(), "Provider refresh set after cache REFRESH remains enabled");
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.REFRESH, query, null, null);
        QueryHintsHandler.apply(QueryHints.REFRESH, false, query, null, null);
        assertTrue(query.shouldRefreshIdentityMapResult(), "Cache REFRESH remains effective independently");
        QueryHintsHandler.apply(QueryHints.CACHE_STORE_MODE, CacheStoreMode.USE, query, null, null);
        assertFalse(query.shouldRefreshIdentityMapResult());
    }

    @Test
    void rawAndTypeVariableCollectionDeclarationsFailWithoutChangingOtherMetadata() {
        for (String name : List.of("raw", "variable", "wildcard")) {
            assertThrows(IllegalArgumentException.class, () -> query(UndefinedReturns.class, name));
        }
        assertEquals(Result.class.getName(), query(Returns.class, "list").getResultClass().getName());
    }

    @Test
    void queryCompanionsAreNotAttributeMappingsButRealMappingsRemainVisible() {
        MetadataClass type = factory.getMetadataClass(Property.class.getName());
        EntityAccessor accessor = new EntityAccessor(null, type, project);
        assertFalse(type.getMethods().get("declaration").hasDeclaredAnnotations(accessor));
        assertTrue(type.getMethods().get("getMapped").hasDeclaredAnnotations(accessor));
        assertTrue(type.getMethods().get("invalidMapping").hasDeclaredAnnotations(accessor));
        accessor.setMetadataComplete(true);
        assertFalse(type.getMethods().get("getMapped").hasDeclaredAnnotations(accessor));
        NativeQueryMetadata.process(type, accessor);
        assertTrue(project.getNamedQueries(accessor).isEmpty());
    }

    private static Map<String, String> hints(NativeQueryMetadata query) {
        return query.getHints().stream().collect(Collectors.toMap(QueryHintMetadata::getName, QueryHintMetadata::getValue));
    }

    private NativeQueryMetadata query(Class<?> type, String name) {
        TestAccessor accessor = accessor(type);
        MetadataMethod method = accessor.type.getMethods().get(name);
        assertNotNull(method);
        return new NativeQueryMetadata(method, accessor);
    }

    private TestAccessor accessor(Class<?> type) {
        return new TestAccessor(factory.getMetadataClass(type.getName()), project);
    }

    private static final class TestAccessor extends MetadataAccessor {
        private final MetadataClass type;

        TestAccessor(MetadataClass type, MetadataProject project) {
            super(null, type, new MetadataDescriptor(type), project);
            this.type = type;
        }

        @Override
        protected MetadataAnnotation getAnnotation(String name) {
            return type.getAnnotation(name);
        }

        @Override
        public boolean isAnnotationPresent(String name) {
            return type.isAnnotationPresent(name);
        }

        @Override
        public boolean isProcessed() {
            return false;
        }

        @Override
        public void process() {
            NativeQueryMetadata.process(type, this);
        }
    }

    record Result(Integer id, String title) { }

    static class GenericOuter<T> {
        class Member { }
    }

    interface Returns {
        @NativeQuery("SELECT ID, TITLE FROM BOOK") Result direct();
        @NativeQuery("SELECT ID, TITLE FROM BOOK") List<Result> list();
        @NativeQuery("SELECT ID, TITLE FROM BOOK") Stream<Result> stream();
        @NativeQuery("SELECT TITLE FROM BOOK") String scalar();
        @NativeQuery("SELECT ID FROM BOOK") int primitive();
        @NativeQuery("SELECT ID, TITLE FROM BOOK") Object[] row();
        @NativeQuery("SELECT TITLE FROM BOOK") List<String[]> array();
        @NativeQuery("SELECT DATA FROM BOOK") List<byte[]> primitiveArray();
        @NativeQuery("SELECT ID FROM BOOK") List<GenericOuter<String>.Member> nestedGeneric();
        // Parser control: generic parameter types must never be mistaken for return element types.
        @NativeQuery("SELECT ID, TITLE FROM BOOK") List<Result> genericParameters(List<Long> parameter);
        @NativeQuery("SELECT TITLE, ID FROM BOOK")
        @ConstructorResult(targetClass = Result.class, columns = {@ColumnResult(name = "SECOND"), @ColumnResult(name = "FIRST")})
        List<?> explicit();
        @NativeQuery("SELECT TITLE, ID FROM BOOK")
        @ColumnResult(name = "SECOND") @ColumnResult(name = "FIRST")
        List<Object[]> columns();
    }

    interface Names {
        @NativeQuery("SELECT ID FROM BOOK") Integer lookup();
        @NativeQuery("SELECT ID FROM BOOK WHERE ID = ?1") Integer lookup(int id);
        @NativeQuery("SELECT ID FROM BOOK") Integer lookup(String value, byte[] bytes, String[][] names);
        @NativeQuery("SELECT ID FROM BOOK") Integer member(Member$Type[] value);
    }

    static class Member$Type { }

    static class Parent<T> {
        public T value() { throw new AssertionError(); }
    }

    static class Covariant extends Parent<String> {
        @Override
        @NativeQuery("SELECT TITLE FROM BOOK")
        public String value() { throw new AssertionError(); }
    }

    static class Initialization {
        static boolean initialized;
    }

    static class NoInitialization {
        static { Initialization.initialized = true; }
        @NativeQuery("SELECT TITLE FROM BOOK")
        private static String value() { throw new AssertionError(); }
    }

    interface Options {
        @NativeQuery("SELECT TITLE FROM BOOK") String absent();
        @NativeQuery("SELECT TITLE FROM BOOK") @QueryOptions String defaults();
        @NativeQuery("SELECT TITLE FROM BOOK")
        @QueryOptions(cacheStoreMode = CacheStoreMode.BYPASS, cacheRetrieveMode = CacheRetrieveMode.BYPASS,
                timeout = 2500, flush = QueryFlushMode.NO_FLUSH, hints = @QueryHint(name = "application.marker", value = "retained"))
        String explicit();
        @NativeQuery("SELECT TITLE FROM BOOK")
        @QueryOptions(flush = QueryFlushMode.NO_FLUSH, hints = {
                @QueryHint(name = QueryHints.CACHE_STORE_MODE, value = "BYPASS"),
                @QueryHint(name = QueryHints.FLUSH, value = "true")})
        String hintOverride();
    }

    interface UndefinedReturns<T> {
        @NativeQuery("SELECT TITLE FROM BOOK") List raw();
        @NativeQuery("SELECT TITLE FROM BOOK") List<T> variable();
        @NativeQuery("SELECT TITLE FROM BOOK") List<?> wildcard();
    }

    static class Property {
        @NativeQuery("SELECT TITLE FROM BOOK") @ColumnResult(name = "TITLE") @QueryOptions
        String declaration(Integer id) { throw new AssertionError(); }
        @NativeQuery("SELECT TITLE FROM BOOK") @Column
        public String getMapped() { throw new AssertionError(); }
        public void setMapped(String value) { }
        @NativeQuery("SELECT TITLE FROM BOOK") @Column
        String invalidMapping(Integer id) { throw new AssertionError(); }
    }
}

class Native$QueryType {
    @NativeQuery("SELECT TITLE FROM BOOK")
    static String lookup() { throw new AssertionError(); }
}
