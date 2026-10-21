/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 *
 * This file was generated with assistance from OpenAI Codex.
 */
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.ExtractOperator;
import org.eclipse.persistence.internal.expressions.LiteralExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.Assert;
import org.junit.Test;

public class H2TemporalPlatformTest {

    private final H2Platform platform = new H2Platform();

    @Test
    public void testExtractDateUsesCastInSelect() {
        Assert.assertEquals("SELECT CAST(STAMP AS DATE) FROM TEMPORAL_SAMPLE", select("DATE"));
    }

    @Test
    public void testExtractTimeRetainsFractionalPrecisionInSelect() {
        Assert.assertEquals("SELECT CAST(STAMP AS TIME(9)) FROM TEMPORAL_SAMPLE", select("TIME"));
    }

    @Test
    public void testExtractDuoAndCollectionAgree() {
        for (String part : List.of("DATE", "TIME", "date", "time")) {
            ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
            List<Expression> arguments = List.of(new LiteralExpression("STAMP", null), new LiteralExpression(part, null));
            StringWriter duo = new StringWriter();
            operator.printDuo(arguments.get(0), arguments.get(1), printer(duo));
            StringWriter collection = new StringWriter();
            operator.printCollection(arguments, printer(collection));
            Assert.assertEquals(duo.toString(), collection.toString());
            Assert.assertEquals(part.equalsIgnoreCase("DATE") ? "CAST(STAMP AS DATE)" : "CAST(STAMP AS TIME(9))", duo.toString());
        }
    }

    @Test
    public void testOtherExtractionFieldsRetainTheirSql() {
        for (String part : List.of("YEAR", "QUARTER", "MONTH", "WEEK", "DAY", "HOUR", "MINUTE", "SECOND")) {
            Assert.assertEquals("SELECT EXTRACT(" + part + " FROM STAMP) FROM TEMPORAL_SAMPLE", select(part));
        }
    }

    @Test
    public void testExtractJavaDuoAndCollectionAgree() {
        ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
        Expression first = new ExpressionBuilder().getField("STAMP");
        StringWriter operand = new StringWriter();
        first.printJava(new ExpressionJavaPrinter("builder", operand, platform));
        for (String part : List.of("DATE", "TIME", "date", "time")) {
            Expression second = new LiteralExpression(part, null);
            StringWriter duo = new StringWriter();
            operator.printJavaDuo(first, second, new ExpressionJavaPrinter("builder", duo, platform));
            StringWriter collection = new StringWriter();
            operator.printJavaCollection(List.of(first, second), new ExpressionJavaPrinter("builder", collection, platform));
            Assert.assertEquals(duo.toString(), collection.toString());
            Assert.assertEquals("CAST(" + operand + (part.equalsIgnoreCase("DATE") ? " AS DATE)" : " AS TIME(9))"), duo.toString());
        }
        for (String part : List.of("YEAR", "QUARTER", "MONTH", "WEEK", "DAY", "HOUR", "MINUTE", "SECOND")) {
            List<Expression> arguments = List.of(first, new LiteralExpression(part, null));
            StringWriter expected = new StringWriter();
            new ExtractOperator().printJavaCollection(arguments, new ExpressionJavaPrinter("builder", expected, platform));
            StringWriter actual = new StringWriter();
            operator.printJavaCollection(arguments, new ExpressionJavaPrinter("builder", actual, platform));
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void testInvalidExtractionPartRetainsInheritedError() {
        Expression first = new ExpressionBuilder().getField("STAMP");
        Expression second = new LiteralExpression("invalid", null);
        for (ExpressionOperator operator : List.of(new ExtractOperator(), platform.getOperator(ExpressionOperator.Extract))) {
            Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                    () -> operator.printDuo(first, second, printer(new StringWriter()))).getMessage());
            Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                    () -> operator.printCollection(List.of(first, second), printer(new StringWriter()))).getMessage());
            Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                    () -> operator.printJavaDuo(first, second, new ExpressionJavaPrinter("builder", new StringWriter(), platform))).getMessage());
            Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                    () -> operator.printJavaCollection(List.of(first, second), new ExpressionJavaPrinter("builder", new StringWriter(), platform))).getMessage());
        }
    }

    @Test
    public void testIndexedLocalTimePreservesNanoseconds() throws SQLException {
        List<Invocation> calls = new ArrayList<>();
        PreparedStatement statement = capture(PreparedStatement.class, calls);
        for (LocalTime value : List.of(LocalTime.MIDNIGHT, LocalTime.of(12, 32, 46, 123456789), LocalTime.MAX)) {
            calls.clear();
            platform.setParameterValueInDatabaseCall(value, statement, 2, null);
            assertInvocation(calls, "setObject", 2, value);
        }
    }

    @Test
    public void testNamedLocalTimePreservesNanoseconds() throws SQLException {
        List<Invocation> calls = new ArrayList<>();
        CallableStatement statement = capture(CallableStatement.class, calls);
        for (LocalTime value : List.of(LocalTime.MIDNIGHT, LocalTime.of(12, 32, 46, 123456789), LocalTime.MAX)) {
            calls.clear();
            platform.setParameterValueInDatabaseCall(value, statement, "time_value", null);
            assertInvocation(calls, "setObject", "time_value", value);
        }
    }

    @Test
    public void testIndexedFallbackMatchesBasePlatform() throws SQLException {
        DatabasePlatform base = new DatabasePlatform();
        List<Invocation> expected = new ArrayList<>();
        List<Invocation> actual = new ArrayList<>();
        PreparedStatement baseStatement = capture(PreparedStatement.class, expected);
        PreparedStatement h2Statement = capture(PreparedStatement.class, actual);
        for (Object value : new Object[] {null, "value", 42, true, LocalDate.of(2024, 1, 2), LocalDateTime.of(2024, 1, 2, 3, 4, 5, 123456789)}) {
            expected.clear();
            actual.clear();
            base.setParameterValueInDatabaseCall(value, baseStatement, 1, null);
            platform.setParameterValueInDatabaseCall(value, h2Statement, 1, null);
            assertSameInvocations(expected, actual);
        }
    }

    @Test
    public void testNamedFallbackMatchesBasePlatform() throws SQLException {
        DatabasePlatform base = new DatabasePlatform();
        List<Invocation> expected = new ArrayList<>();
        List<Invocation> actual = new ArrayList<>();
        CallableStatement baseStatement = capture(CallableStatement.class, expected);
        CallableStatement h2Statement = capture(CallableStatement.class, actual);
        for (Object value : new Object[] {null, "value", 42, true, LocalDate.of(2024, 1, 2), LocalDateTime.of(2024, 1, 2, 3, 4, 5, 123456789)}) {
            expected.clear();
            actual.clear();
            base.setParameterValueInDatabaseCall(value, baseStatement, "value", null);
            platform.setParameterValueInDatabaseCall(value, h2Statement, "value", null);
            assertSameInvocations(expected, actual);
        }
    }

    @Test
    public void testJdbcBindingFailurePropagates() {
        SQLException failure = new SQLException("binding failure");
        CallableStatement statement = (CallableStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {CallableStatement.class}, (proxy, method, arguments) -> { throw failure; });
        Assert.assertSame(failure, Assert.assertThrows(SQLException.class,
                () -> platform.setParameterValueInDatabaseCall(LocalTime.NOON, statement, 1, null)));
        Assert.assertSame(failure, Assert.assertThrows(SQLException.class,
                () -> platform.setParameterValueInDatabaseCall(LocalTime.NOON, statement, "value", null)));
    }

    private String select(String part) {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addField(builder.getField("STAMP").extract(part));
        statement.addTable(new DatabaseTable("TEMPORAL_SAMPLE"));
        statement.computeTablesFromTables();
        return statement.buildCall(session).getSQLString();
    }

    private ExpressionSQLPrinter printer(StringWriter writer) {
        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(
                new DatabaseSessionImpl(new DatabaseLogin(platform)), null, new SQLCall(), false, null);
        printer.setWriter(writer);
        return printer;
    }

    private <T> T capture(Class<T> type, List<Invocation> calls) {
        return type.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {type},
                (proxy, method, arguments) -> {
                    calls.add(new Invocation(method.getName(), arguments));
                    return null;
                }));
    }

    private void assertInvocation(List<Invocation> calls, String method, Object key, Object value) {
        Assert.assertEquals(1, calls.size());
        Assert.assertEquals(method, calls.get(0).method);
        Assert.assertArrayEquals(new Object[] {key, value}, calls.get(0).arguments);
        Assert.assertSame(value, calls.get(0).arguments[1]);
    }

    private void assertSameInvocations(List<Invocation> expected, List<Invocation> actual) {
        Assert.assertEquals(expected.size(), actual.size());
        for (int index = 0; index < expected.size(); index++) {
            Assert.assertEquals(expected.get(index).method, actual.get(index).method);
            Assert.assertArrayEquals(expected.get(index).arguments, actual.get(index).arguments);
        }
    }

    private static class Invocation {
        private final String method;
        private final Object[] arguments;

        private Invocation(String method, Object[] arguments) {
            this.method = method;
            this.arguments = arguments;
        }
    }
}
