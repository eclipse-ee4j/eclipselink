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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementOrTypedQuery;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Timeout;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.internal.helper.DatabaseField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.IdentityHashMap;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.CursoredStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryExecutionContextTest {
    @Test
    void customReadQueriesKeepInvocationGraphsSeparateFromDescriptorState() {
        var object = new org.eclipse.persistence.queries.ReadObjectQuery() {
            @Override public void prepareCustomQuery(DatabaseQuery query) { super.prepareCustomQuery(query); }
        };
        object.setSelectionId(1);
        assertCustomGraphCopy(object, object::prepareCustomQuery);
        var all = new ReadAllQuery() {
            @Override public void prepareCustomQuery(DatabaseQuery query) { super.prepareCustomQuery(query); }
        };
        assertCustomGraphCopy(all, all::prepareCustomQuery);
    }

    private static void assertCustomGraphCopy(org.eclipse.persistence.queries.ObjectLevelReadQuery invocation,
            java.util.function.Consumer<DatabaseQuery> prepare) {
        var requested = new org.eclipse.persistence.queries.LoadGroup("invocation");
        requested.addAttribute("requested");
        invocation.setLoadGroup(requested);
        invocation.setShouldUseDefaultFetchGroup(false);
        var template = (org.eclipse.persistence.queries.ObjectLevelReadQuery) invocation.clone();
        template.setSQLString("SELECT ID FROM SAMPLE");
        var declared = new org.eclipse.persistence.queries.LoadGroup("descriptor");
        declared.addAttribute("declared");
        template.setLoadGroup(declared);
        template.setShouldUseDefaultFetchGroup(true);
        var descriptorFetch = new org.eclipse.persistence.queries.FetchGroup("descriptor-fetch");
        descriptorFetch.addAttribute("id");
        template.setFetchGroup(descriptorFetch);
        var execution = (org.eclipse.persistence.queries.ObjectLevelReadQuery) template.clone();
        var customCall = execution.getCall();
        prepare.accept(execution);
        assertSame(customCall, execution.getCall());
        assertNotSame(requested, execution.getLoadGroup());
        assertEquals(Set.of("requested"), execution.getLoadGroup().getAttributeNames());
        requested.addAttribute("later");
        assertFalse(execution.getLoadGroup().getAttributeNames().contains("later"));
        assertFalse(execution.shouldUseDefaultFetchGroup());
        assertEquals(Set.of("id"), execution.getFetchGroup().getAttributeNames());
        assertSame(declared, template.getLoadGroup());
        assertTrue(template.shouldUseDefaultFetchGroup());
        invocation.setLoadGroup(null);
        var reused = (org.eclipse.persistence.queries.ObjectLevelReadQuery) template.clone();
        var originalGraph = reused.getLoadGroup();
        prepare.accept(reused);
        assertSame(originalGraph, reused.getLoadGroup());
        assertEquals(Set.of("declared"), reused.getLoadGroup().getAttributeNames());
        assertSame(declared, template.getLoadGroup());
    }

    @Test
    void constructionDoesNotAcquireResourceOwnership() {
        TestContext context = new TestContext();
        new EJBQueryImpl<>(new DataReadQuery("select 1"), context);
        new StatementImpl(new DataModifyQuery("delete from sample"), context);
        procedure(context);
        assertTrue(context.resources.isEmpty());
    }

    @Test
    void cursorCleanupKeepsFailedOwnershipAndReleasesSuccessfulOwnership() {
        TestContext context = new TestContext();
        ReadAllQuery read = new ReadAllQuery();
        read.useCursoredStream();
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(read, context);
        RuntimeException failure = new IllegalStateException("one-shot cursor close");
        var accessor = new org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor() {
            boolean first = true;
            @Override public void closeCursor(java.sql.ResultSet result, AbstractSession session) {
                if (first) { first = false; throw failure; }
            }
            @Override public void closeStatement(java.sql.Statement statement, AbstractSession session,
                    org.eclipse.persistence.internal.databaseaccess.DatabaseCall call) { }
        };
        read.setAccessor(accessor);
        CursoredStream cursor = new CursoredStream() {
            {
                setSession(context.session);
                setResultSet((java.sql.ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class<?>[] {java.sql.ResultSet.class}, (proxy, method, arguments) -> null));
            }
        };
        cursor.query = read;
        context.result = cursor;
        assertSame(cursor, query.getResultCursor());
        assertEquals(Set.of(query), context.resources);
        assertNull(read.getProperty("eclipselink.cursor.resource-release"));
        assertSame(failure, assertThrows(RuntimeException.class, query::releaseResources));
        assertEquals(Set.of(query), context.resources);
        context.open = false;
        query.releaseResources();
        assertTrue(cursor.isClosed());
        assertTrue(context.resources.isEmpty());
        assertNull(cursor.getQuery().getProperty("eclipselink.cursor.resource-release"));
    }

    @Test
    void allQueryInterfacesRejectClosedOwner() {
        TestContext context = new TestContext();
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(new DataReadQuery("select 1"), context);
        Statement statement = new StatementImpl(new DataModifyQuery("delete from sample"), context);
        StoredProcedureQuery procedure = procedure(context);
        context.open = false;
        assertClosedMethods(query, TypedQuery.class);
        assertClosedMethods(query, StatementOrTypedQuery.class);
        assertClosedMethods(statement, Statement.class);
        assertClosedMethods(procedure, StoredProcedureQuery.class);
    }

    private static void assertClosedMethods(Object query, Class<?> contract) {
        for (Method method : contract.getMethods()) {
            Object[] arguments = new Object[method.getParameterCount()];
            for (int index = 0; index < arguments.length; index++) {
                if (method.getParameterTypes()[index] == int.class) {
                    arguments[index] = 1;
                }
            }
            InvocationTargetException failure = assertThrows(InvocationTargetException.class,
                    () -> method.invoke(query, arguments), method.toString());
            assertInstanceOf(IllegalStateException.class, failure.getCause(), method.toString());
        }
    }

    @Test
    void statementRequiresItsOwnerTransactionAndExecutesWithoutFlush() {
        TestContext context = new TestContext();
        Statement statement = new StatementImpl(new DataModifyQuery("delete from sample"), context);
        assertThrows(TransactionRequiredException.class, statement::execute);
        assertEquals(0, context.executions);
        context.active = true;
        assertEquals(1, statement.setQueryFlushMode(QueryFlushMode.FLUSH).execute());
        assertEquals(1, context.executions);
        assertEquals(0, context.flushes);
    }

    @Test
    void recoverableAgentFailureDoesNotUseEntityManagerRollbackPolicy() {
        TestContext context = new TestContext();
        context.active = true;
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(new DataReadQuery("select 1"), context);
        context.failure = new jakarta.persistence.OptimisticLockException("stale");
        assertThrows(jakarta.persistence.OptimisticLockException.class, query::getResultList);
        assertEquals(0, context.rollbackMarks);
        context.failure = null;
        assertEquals(List.of(), query.getResultList());
    }

    @Test
    void typedAndStatementOptionsReplaceByTypeAndReturnSnapshots() {
        TestContext context = new TestContext();
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(new DataReadQuery("select 1"), context);
        query.addOption(Timeout.milliseconds(250)).setTimeout(500).setQueryFlushMode(QueryFlushMode.NO_FLUSH);
        assertEquals(Set.of(Timeout.milliseconds(500), QueryFlushMode.NO_FLUSH), query.getOptions());
        query.getOptions().clear();
        assertEquals(2, query.getOptions().size());
        Statement statement = query.asStatement();
        statement.setTimeout(1000);
        assertTrue(statement.getOptions().contains(Timeout.milliseconds(1000)));
        statement.getOptions().clear();
        assertFalse(statement.getOptions().isEmpty());
        query.setTimeout((Timeout) null);
        assertNull(query.getTimeout());
        assertFalse(query.getOptions().stream().anyMatch(Timeout.class::isInstance));
        assertFalse(statement.getOptions().stream().anyMatch(Timeout.class::isInstance));
        statement.setTimeout(100).setTimeout((Integer) null);
        assertNull(statement.getTimeout());
    }

    @Test
    void convertedParameterIsConvertedOnceAtExecutionAndCanBeRebound() {
        TestContext context = new TestContext();
        DataReadQuery read = new DataReadQuery("select ?1");
        read.addArgument("1", String.class);
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(read, context);
        LabelConverter.conversions = 0;
        query.setConvertedParameter(1, new Label("first"), LabelConverter.class);
        assertEquals(0, LabelConverter.conversions);
        query.getResultList();
        assertEquals(List.of("first"), context.arguments);
        assertEquals(1, LabelConverter.conversions);
        query.setParameter(1, "second").getResultList();
        assertEquals(List.of("second"), context.arguments);
        assertEquals(1, LabelConverter.conversions);
    }

    @Test
    void positionalArgumentsMustExactlyMatchBeforeChangingBindings() {
        TestContext context = new TestContext();
        DataReadQuery read = new DataReadQuery("select ?1, ?2, ?1, '?'");
        read.addArgument("1", String.class);
        read.addArgument("2", String.class);
        EJBQueryImpl<Object> query = new EJBQueryImpl<>(read, context);
        assertThrows(IllegalArgumentException.class, () -> query.setParameters("short"));
        assertThrows(IllegalArgumentException.class, () -> query.setParameters("too", "many", "values"));
        query.setParameters("first", "second");
        assertEquals("first", query.getParameterValue(1));
        assertThrows(IllegalArgumentException.class, () -> query.setParameters("replacement"));
        assertEquals("first", query.getParameterValue(1));
    }

    @Test
    void procedureArgumentsStartAtOneAndCloseIsTerminal() {
        TestContext context = new TestContext();
        StoredProcedureQuery query = procedure(context);
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        assertThrows(IllegalArgumentException.class, () -> query.setParameters("first"));
        assertThrows(IllegalArgumentException.class, () -> query.setParameters("first", 2, 3));
        query.setParameters("first", 2);
        assertEquals("first", query.getParameterValue(1));
        assertEquals(2, query.getParameterValue(2));
        query.setTimeout(100);
        assertTrue(query.getOptions().contains(Timeout.milliseconds(100)));
        query.setTimeout((Timeout) null);
        assertNull(query.getTimeout());
        assertFalse(query.getOptions().stream().anyMatch(Timeout.class::isInstance));
        ((StoredProcedureQueryImpl) query).releaseResources();
        query.setParameter(1, "after resource cleanup");
        assertEquals("after resource cleanup", query.getParameterValue(1));
        query.close();
        assertThrows(IllegalStateException.class, query::close);
        assertThrows(IllegalStateException.class, query::getOptions);
    }

    @Test
    void convertedProcedureParametersUseRelationalJdbcTypesAndDomainOutputs() {
        TestContext context = new TestContext();
        StoredProcedureQueryImpl query = procedure(context);
        var parameter = query.registerConvertedParameter(1, LabelConverter.class, ParameterMode.INOUT);
        assertEquals(Label.class, parameter.getParameterType());
        query.setParameter(parameter, new Label("input"));
        assertEquals(List.of("input"), query.processParameters());
        query.setParameter(parameter, null);
        assertEquals(String.class, ((DatabaseField) query.processParameters().get(0)).getType());
        query.executeCall = new SQLCall();
        query.executeStatement = (CallableStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {CallableStatement.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("getObject")) {
                        return "output";
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        try {
            assertEquals(new Label("output"), query.getOutputParameterValue(parameter));
        } finally {
            query.executeCall = null;
            query.executeStatement = null;
        }
    }

    @Test
    void referencesKeepImmutableSnapshotsAndNullAbsentArguments() {
        Map<String, Object> hints = new HashMap<>();
        hints.put("hint", "old");
        TypedQueryReferenceImpl<Object> reference = new TypedQueryReferenceImpl<>("named", Object.class, hints);
        hints.put("hint", "new");
        assertEquals("old", reference.getHints().get("hint"));
        assertThrows(UnsupportedOperationException.class, () -> reference.getHints().clear());
        assertNull(reference.getArguments());
        assertNull(reference.getParameterTypes());
        assertNull(reference.getParameterNames());
        assertTrue(reference.getOptions().isEmpty());
    }

    private static StoredProcedureQueryImpl procedure(TestContext context) {
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("sample");
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setCall(call);
        return new StoredProcedureQueryImpl(query, context);
    }

    record Label(String value) {
    }

    public static class LabelConverter implements AttributeConverter<Label, String> {
        static int conversions;
        @Override
        public String convertToDatabaseColumn(Label value) {
            conversions++;
            return value == null ? null : value.value();
        }
        @Override
        public Label convertToEntityAttribute(String value) {
            return value == null ? null : new Label(value);
        }
    }

    static final class TestContext implements QueryExecutionContext {
        final AbstractSession session = new DatabaseSessionImpl(new DatabaseLogin());
        boolean open = true;
        boolean active;
        int executions;
        int flushes;
        int rollbackMarks;
        List<Object> arguments;
        RuntimeException failure;
        Object result;
        final Set<QueryImpl> resources = Collections.newSetFromMap(new IdentityHashMap<>());
        @Override public void verifyOpen() { if (!open) throw new IllegalStateException("closed"); }
        @Override public void verifyOpenWithSetRollbackOnly() { verifyOpen(); }
        @Override public AbstractSession getSession() { return session; }
        @Override public AbstractSession getExecutionSession(DatabaseQuery query) { return session; }
        @Override public boolean hasActiveTransaction() { return active; }
        @Override public void requireTransaction() { verifyOpen(); if (!active) throw new TransactionRequiredException(); }
        @Override public FlushModeType getFlushMode() { return FlushModeType.AUTO; }
        @Override public boolean shouldFlush() { return false; }
        @Override public void flush() { flushes++; }
        @Override public void setRollbackOnly() { rollbackMarks++; }
        @Override public Map<String, Object> getProperties() { return Map.of(); }
        @Override public void registerQuery(QueryImpl query) { resources.add(query); }
        @Override public void unregisterQuery(QueryImpl query) { resources.remove(query); }
        @Override public boolean isEntityAgent() { return true; }
        @Override public void lockResult(Object entity, LockModeType mode) { requireTransaction(); }
        @Override public Object executeQuery(DatabaseQuery query, List<Object> values, LockModeType lockMode) {
            verifyOpen();
            if (failure != null) throw failure;
            executions++;
            arguments = new ArrayList<>(values);
            if (result != null) return result;
            return query.isModifyQuery() ? 1 : List.of();
        }
    }
}
