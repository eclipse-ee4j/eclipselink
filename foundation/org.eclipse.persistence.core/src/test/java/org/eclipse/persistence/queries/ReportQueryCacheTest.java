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
package org.eclipse.persistence.queries;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.identitymaps.IdentityMapManager;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ReportQueryCacheTest {
    private final DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
    private final IdentityMapManager cache = session.getIdentityMapAccessorInstance().getIdentityMapManager();

    private ReportQuery query(Class<?> resultClass) {
        ReportQuery query = new ReportQuery(new ExpressionBuilder());
        query.setName("shared-report");
        query.setResultClass(resultClass);
        query.returnSingleAttribute();
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
        return query;
    }

    @Test
    void namedBucketRetainsMultipleNoArgumentShapesAndBothClearPaths() {
        ReportQuery text = query(String.class);
        ReportQuery number = query(Integer.class);
        cache.putQueryResult(text, List.of(), "text");
        cache.putQueryResult(number, List.of(), 7);
        assertEquals("text", cache.getQueryResult(text, List.of(), true));
        assertEquals(7, cache.getQueryResult(number, List.of(), true));
        session.addQuery(text.getName(), text);
        session.getIdentityMapAccessor().clearQueryCache(text.getName());
        assertNull(cache.getQueryResult(text, List.of(), true));
        assertNull(cache.getQueryResult(number, List.of(), true));
        cache.putQueryResult(text, List.of(), "again");
        cache.clearQueryCache();
        assertNull(cache.getQueryResult(text, List.of(), true));
    }

    @Test
    void parameterValuesAndResolvedSignaturesBothParticipateInIdentity() {
        ReportQuery integer = constructorQuery(Integer.class);
        ReportQuery string = constructorQuery(String.class);
        List<Object> nullArgument = Arrays.asList((Object) null);
        cache.putQueryResult(integer, nullArgument, "integer constructor");
        cache.putQueryResult(string, nullArgument, "string constructor");
        cache.putQueryResult(integer, List.of(1), "one");
        cache.putQueryResult(integer, List.of(2), "two");
        assertEquals("integer constructor", cache.getQueryResult(integer, nullArgument, true));
        assertEquals("string constructor", cache.getQueryResult(string, nullArgument, true));
        assertEquals("one", cache.getQueryResult(integer, List.of(1), true));
        assertEquals("two", cache.getQueryResult(integer, List.of(2), true));
        assertEquals(integer.getResultCacheShape(), constructorQuery(Integer.class).getResultCacheShape());
        assertNotEquals(integer.getResultCacheShape(), string.getResultCacheShape());
        ReportQuery array = (ReportQuery) integer.clone();
        array.setReturnType(ReportQuery.ShouldReturnArray);
        assertNotEquals(integer.getResultCacheShape(), array.getResultCacheShape());
    }

    private ReportQuery constructorQuery(Class<?> argument) {
        ReportQuery query = query(Object.class);
        query.beginAddingConstructorArguments(Object.class, new Class<?>[]{argument});
        query.addAttribute("value", query.getExpressionBuilder().getParameter("value"));
        query.endAddingToConstructorItem();
        return query;
    }

    @Test
    void shapeSnapshotsOwnConstructorTypeArrays() {
        ReportQuery query = constructorQuery(Integer.class);
        Object shape = query.getResultCacheShape();
        ((ConstructorReportItem) query.getItems().getFirst()).getConstructorArgTypes()[0] = String.class;
        assertEquals(shape, constructorQuery(Integer.class).getResultCacheShape());
        assertNotEquals(shape, query.getResultCacheShape());
    }

    @Test
    void configuredCapacityExpiryNullPolicyAndClassInvalidationRemainEffective() {
        ReportQuery text = query(String.class);
        ReportQuery integer = query(Integer.class);
        ReportQuery number = query(Long.class);
        text.getQueryResultsCachePolicy().setMaximumCachedResults(2);
        cache.putQueryResult(text, List.of(), "text");
        cache.putQueryResult(integer, List.of(), 1);
        cache.putQueryResult(number, List.of(), 2L);
        assertNull(cache.getQueryResult(text, List.of(), true));
        assertEquals(1, cache.getQueryResult(integer, List.of(), true));
        assertEquals(2L, cache.getQueryResult(number, List.of(), true));
        cache.clearQueryCache();
        text.getQueryResultsCachePolicy().setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(-1));
        cache.putQueryResult(text, List.of(), "expired");
        assertEquals("expired", cache.getQueryResult(text, List.of(), false));
        assertNull(cache.getQueryResult(text, List.of(), true));
        cache.clearQueryCache();
        integer.getQueryResultsCachePolicy().setIsNullIgnored(true);
        cache.putQueryResult(integer, List.of(), null);
        assertNull(cache.getQueryResult(integer, List.of(), false));
        number.getQueryResultsCachePolicy().setInvalidateOnChange(true);
        number.getQueryResultsCachePolicy().setInvalidationClasses(Set.of(String.class));
        cache.putQueryResult(number, List.of(), 2L);
        cache.invalidateQueryCache(String.class);
        assertNull(cache.getQueryResult(number, List.of(), true));
    }

    @Test
    void nonReportCacheIdentityRemainsParameterBased() {
        DataReadQuery query = new DataReadQuery();
        query.setName("ordinary");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
        Object result = new Object();
        cache.putQueryResult(query, List.of(1), result);
        assertSame(result, cache.getQueryResult(query, List.of(1), true));
        assertNull(cache.getQueryResult(query, List.of(2), true));
    }

    @Test
    void entryComponentsUseEqualityAndDefensiveDeepArraySnapshots() {
        assertNotEquals(new ReportQueryResult.DistinctEntryKey("b_c", "a"),
                new ReportQueryResult.DistinctEntryKey("c", "a_b"));
        assertNotEquals(new ReportQueryResult.DistinctEntryKey("Aa", "same"),
                new ReportQueryResult.DistinctEntryKey("BB", "same"));
        byte[] bytes = {1, 2};
        Object[] nested = {new int[]{3}, new String[]{"value"}};
        var original = new ReportQueryResult.DistinctEntryKey(bytes, nested);
        var equal = new ReportQueryResult.DistinctEntryKey(new byte[]{1, 2},
                new Object[]{new int[]{3}, new String[]{"value"}});
        assertEquals(original, equal);
        assertEquals(original.hashCode(), equal.hashCode());
        bytes[0] = 9;
        ((int[]) nested[0])[0] = 9;
        assertEquals(original, equal);
        assertNotEquals(original, new ReportQueryResult.DistinctEntryKey(bytes, nested));
        assertEquals(new ReportQueryResult.DistinctEntryKey(null, null), new ReportQueryResult.DistinctEntryKey(null, null));
        ReportQueryResult legacy = new ReportQueryResult(List.of(), null);
        legacy.key = new StringBuffer("legacy_a_b_");
        assertEquals("legacy_a_b_", legacy.getResultKey());
        assertEquals(legacy.getResultKey(), legacy.getDistinctResultKey());
    }

    @Test
    void unmappedScalarMaterializationRetainsNullDefaultsAndAssignableValues() {
        Object previous = session.getPlatform().getConversionManager().getDefaultNullValue(String.class);
        try {
            for (String sentinel : Arrays.asList(null, "SENTINEL")) {
                session.getLogin().setDefaultNullValue(String.class, sentinel);
                for (Object value : Arrays.asList(null, "actual", 7)) {
                    ReportQuery query = new ReportQuery(new ExpressionBuilder());
                    query.setSession(session);
                    query.addItem("literal", query.getExpressionBuilder().value(value));
                    query.returnSingleAttribute();
                    query.setResultClass(String.class);
                    DatabaseRecord row = new DatabaseRecord();
                    row.put("literal", value);
                    assertNull(query.getItems().getFirst().getMapping());
                    assertNull(query.getItems().getFirst().getDescriptor());
                    assertEquals(value, new ReportQueryResult(query, row, new Vector<>()).getResults().getFirst());
                    assertEquals(value == null ? sentinel : value.toString(), query.buildObject(row));
                }
            }
            Object value = new Object();
            ReportQuery query = new ReportQuery(new ExpressionBuilder());
            query.setSession(session);
            query.addItem("literal", query.getExpressionBuilder().value(value));
            query.returnSingleAttribute();
            query.setResultClass(Object.class);
            DatabaseRecord row = new DatabaseRecord();
            row.put("literal", value);
            assertSame(value, query.buildObject(row));
        } finally {
            session.getLogin().setDefaultNullValue(String.class, previous);
        }
    }
}
