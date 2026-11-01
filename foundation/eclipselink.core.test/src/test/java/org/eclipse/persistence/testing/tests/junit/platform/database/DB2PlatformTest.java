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
 *
 * These regression tests were generated with assistance from OpenAI Codex.
 */
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.DB2MainframePlatform;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DB2ZPlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.JavaDBPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Test;

public class DB2PlatformTest {
    private static final int[] SELECTORS = {ExpressionOperator.Length, ExpressionOperator.Locate,
            ExpressionOperator.Locate2, ExpressionOperator.Substring, ExpressionOperator.SubstringSingleArg,
            ExpressionOperator.Left, ExpressionOperator.Right};
    private static final String SOURCE = "A'\u00e9\uD83D\uDE00B";
    private static final String SEARCH = "'\u00e9";

    @Test
    public void testLuwMappedCharacterOperators() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertSql(new DB2Platform(), partial, "mapped", false, new String[]{
                    "CHARACTER_LENGTH(NAME, CODEUNITS32)", "LOCATE('''\u00e9', NAME, CODEUNITS32)",
                    "LOCATE('''\u00e9', NAME, 2, CODEUNITS32)", "SUBSTRING(NAME, 2, 1, CODEUNITS32)",
                    "SUBSTRING(NAME, 2, CODEUNITS32)", clampedSql("LEFT", "NAME", "1"),
                    clampedSql("RIGHT", "NAME", "1")});
        }
    }

    @Test
    public void testLuwLiteralEscaping() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertSql(new DB2Platform(), partial, "literal", false, new String[]{
                    "CHARACTER_LENGTH('A''\u00e9\uD83D\uDE00B', CODEUNITS32)",
                    "LOCATE('''\u00e9', 'A''\u00e9\uD83D\uDE00B', CODEUNITS32)",
                    "LOCATE('''\u00e9', 'A''\u00e9\uD83D\uDE00B', 2, CODEUNITS32)",
                    "SUBSTRING('A''\u00e9\uD83D\uDE00B', 2, 1, CODEUNITS32)",
                    "SUBSTRING('A''\u00e9\uD83D\uDE00B', 2, CODEUNITS32)",
                    clampedSql("LEFT", "'A''\u00e9\uD83D\uDE00B'", "1"),
                    clampedSql("RIGHT", "'A''\u00e9\uD83D\uDE00B'", "1")});
        }
    }

    @Test
    public void testLuwParametersRetainBindingOrderAndRebinding() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertParameterSql(new DB2Platform(), partial, true);
        }
    }

    @Test
    public void testDerbyMappedOperatorsPreserveDialectAndGuardLocateNulls() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertSql(new DerbyPlatform(), partial, "mapped", false, new String[]{
                    "LENGTH(NAME)", derbyLocateSql("NAME", "'''\u00e9'"), derbyLocateSql("NAME", "'''\u00e9'", "2"),
                    "SUBSTR(NAME, 2, 1)", "SUBSTR(NAME, 2)", "SUBSTR(NAME, 1, 1)",
                    "SUBSTR(NAME, LENGTH(NAME) + 1 - 1, 1)"});
        }
    }

    @Test
    public void testDerbyParameterSqlPreservesBindingsWithLocateNullGuards() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertParameterSql(new DerbyPlatform(), partial, false);
        }
    }

    @Test
    public void testZosOperatorsAndBindingRemainUnchanged() throws SQLException {
        assertLegacyMapped(new DB2ZPlatform());
        for (boolean partial : List.of(false, true)) {
            assertParameterSql(new DB2ZPlatform(), partial, false);
        }
    }

    @Test
    public void testMainframeOperatorsAndBindingRemainUnchanged() throws SQLException {
        assertLegacyMapped(new DB2MainframePlatform());
        for (boolean partial : List.of(false, true)) {
            assertParameterSql(new DB2MainframePlatform(), partial, false);
        }
    }

    @Test
    public void testLuwOperatorFlagsAndOriginalChildrenArePreserved() throws SQLException {
        DB2Platform platform = new DB2Platform();
        for (int selector : SELECTORS) {
            Prepared prepared = prepare(platform, true, "parameter", true, selector);
            ExpressionOperator operator = prepared.expression.getPlatformOperator(platform);
            ExpressionOperator ordinary = ordinaryOperator(selector);
            Assert.assertEquals(ordinary.isBindingSupported(), operator.isBindingSupported());
            int arguments = prepared.expression.getChildren().size();
            if (selector == ExpressionOperator.Left || selector == ExpressionOperator.Right) {
                Assert.assertArrayEquals(new int[]{0, 1}, ordinary.getArgumentIndices(arguments));
                Assert.assertArrayEquals(new int[]{0, 1, 0, 0, 1}, operator.getArgumentIndices(arguments));
            } else {
                Assert.assertArrayEquals(ordinary.getArgumentIndices(arguments), operator.getArgumentIndices(arguments));
            }
            List<Expression> children = new ArrayList<>(prepared.expression.getChildren());
            for (int iteration = 0; iteration < 2; iteration++) {
                DatabaseCall execution = execute(prepared, iteration);
                Assert.assertTrue(execution.getSQLString().contains("CODEUNITS32"));
                Assert.assertEquals(children.size(), prepared.expression.getChildren().size());
                for (int index = 0; index < children.size(); index++) {
                    Assert.assertSame(children.get(index), prepared.expression.getChildren().get(index));
                }
            }
        }
    }

    @Test
    public void testInterleavedPlatformInitializationDoesNotChangeOtherInstances() throws SQLException {
        List<String[]> globalSql = new ArrayList<>();
        for (int selector : SELECTORS) {
            ExpressionOperator operator = ExpressionOperator.getInternalOperator(selector);
            globalSql.add(operator == null ? null : operator.getDatabaseStrings().clone());
        }
        List<DB2Platform> platforms = List.of(new DB2Platform(), new DerbyPlatform(), new DB2ZPlatform(),
                new DB2MainframePlatform(), new DB2Platform());
        List<List<String>> first = new ArrayList<>();
        for (DB2Platform platform : platforms) {
            List<String> sql = new ArrayList<>();
            for (int selector : SELECTORS) {
                sql.add(execute(prepare(platform, true, "parameter", true, selector), 0).getSQLString());
            }
            first.add(sql);
        }
        Assert.assertEquals(first.get(0), first.get(4));
        for (int p = platforms.size() - 1; p >= 0; p--) {
            for (int index = 0; index < SELECTORS.length; index++) {
                Assert.assertEquals(first.get(p).get(index),
                        execute(prepare(platforms.get(p), true, "parameter", true, SELECTORS[index]), 0).getSQLString());
                ExpressionOperator operator = ExpressionOperator.getInternalOperator(SELECTORS[index]);
                if (globalSql.get(index) == null) {
                    Assert.assertNull(operator);
                } else {
                    Assert.assertArrayEquals(globalSql.get(index), operator.getDatabaseStrings());
                }
            }
        }
        Assert.assertNotSame(platforms.get(0).getOperator(ExpressionOperator.Length),
                platforms.get(4).getOperator(ExpressionOperator.Length));
    }

    @Test
    public void testLuwLeftRightBoundaryBindingAndOperandReuse() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            for (int selector : new int[]{ExpressionOperator.Left, ExpressionOperator.Right}) {
                Prepared prepared = prepare(new DB2Platform(), partial, "parameter", true, selector);
                List<Expression> children = new ArrayList<>(prepared.expression.getChildren());
                Assert.assertEquals(2, children.size());
                String expectedSql = "SELECT " + clampedSql(selector == ExpressionOperator.Left ? "LEFT" : "RIGHT", "?", "?")
                        + " FROM ITEM";
                for (String source : new String[]{"", "A\u00e9B", "A\uD83D\uDE00B", "a  ", null, "A\uD83D\uDE00B"}) {
                    for (Integer length : new Integer[]{1, 8, 0, 2, 3, null, -1, 1}) {
                        DatabaseRecord values = new DatabaseRecord();
                        values.put("source", source);
                        values.put("length", length);
                        DatabaseCall execution = (DatabaseCall) prepared.call.clone();
                        execution.translate(values, null, prepared.session);
                        Assert.assertEquals(expectedSql, execution.getSQLString());
                        Assert.assertEquals(5, execution.getParameters().size());
                        int[] indices = {0, 1, 0, 0, 1};
                        for (int index = 0; index < indices.length; index++) {
                            Object expected = indices[index] == 0 ? source : length;
                            Object actual = execution.getParameters().get(index);
                            if (expected == null) {
                                Assert.assertEquals(indices[index] == 0 ? "source" : "length", ((DatabaseField) actual).getName());
                            } else {
                                Assert.assertEquals(expected, actual);
                            }
                        }
                        Assert.assertEquals(2, prepared.expression.getChildren().size());
                        Assert.assertSame(children.get(0), prepared.expression.getChildren().get(0));
                        Assert.assertSame(children.get(1), prepared.expression.getChildren().get(1));
                    }
                }
            }
        }
    }

    @Test
    public void testDerbyLocateRetainsNullBindingsAndOriginalOperands() throws SQLException {
        for (boolean partial : List.of(false, true)) {
            for (boolean coalesce : List.of(false, true)) {
                for (int selector : new int[]{ExpressionOperator.Locate, ExpressionOperator.Locate2}) {
                    Prepared prepared = prepareMappedLocate(partial, coalesce, selector);
                    List<Expression> children = new ArrayList<>(prepared.expression.getChildren());
                    String sourceSql = coalesce ? "COALESCE(NAME, ?)" : "NAME";
                    String expectedSql = "SELECT " + (selector == ExpressionOperator.Locate
                            ? derbyLocateSql(sourceSql, "?") : derbyLocateSql(sourceSql, "?", "?")) + " FROM ITEM";
                    Object[][] values = {{"ababa", "ba", 2}, {null, "ba", 2}, {"ababa", null, 2},
                            {"ababa", "ba", null}, {null, null, null}, {"ababa", "missing", 2},
                            {"ababa", "ba", 4}, {"ababa", "ba", -1}, {"ababa", "ba", 2}};
                    for (Object[] row : values) {
                        DatabaseRecord parameters = new DatabaseRecord();
                        parameters.put("source", row[0]);
                        parameters.put("search", row[1]);
                        parameters.put("start", row[2]);
                        DatabaseCall call = (DatabaseCall) prepared.call.clone();
                        call.translate(parameters, null, prepared.session);
                        Assert.assertEquals(expectedSql, call.getSQLString());
                        Assert.assertTrue(call.usesBinding(prepared.session.getPlatform()));
                        List<String> names = new ArrayList<>();
                        if (selector == ExpressionOperator.Locate2) {
                            names.add("start");
                        }
                        names.add("search");
                        if (coalesce) {
                            names.add("source");
                        }
                        if (selector == ExpressionOperator.Locate2) {
                            names.add("start");
                        }
                        Assert.assertEquals(names.size(), call.getParameters().size());
                        for (int index = 0; index < names.size(); index++) {
                            Object expected = parameters.get(names.get(index));
                            Object actual = call.getParameters().get(index);
                            if (expected == null) {
                                Assert.assertEquals(names.get(index), ((DatabaseField) actual).getName());
                            } else {
                                Assert.assertEquals(expected, actual);
                            }
                        }
                        Assert.assertEquals(children.size(), prepared.expression.getChildren().size());
                        for (int index = 0; index < children.size(); index++) {
                            Assert.assertSame(children.get(index), prepared.expression.getChildren().get(index));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testDerbyLocateBindingOverridesStayLocal() {
        for (DB2Platform platform : List.of(new DerbyPlatform(), new JavaDBPlatform())) {
            for (int selector : new int[]{ExpressionOperator.Locate, ExpressionOperator.Locate2}) {
                ExpressionOperator operator = platform.getOperator(selector);
                Assert.assertEquals(Boolean.TRUE, operator.isBindingSupported());
                Assert.assertArrayEquals(selector == ExpressionOperator.Locate ? new int[]{1, 0} : new int[]{2, 1, 0, 2},
                        operator.getArgumentIndices(selector == ExpressionOperator.Locate ? 2 : 3));
                Assert.assertNotSame(operator, new DerbyPlatform().getOperator(selector));
                for (DB2Platform other : List.of(new DB2Platform(), new DB2ZPlatform(), new DB2MainframePlatform())) {
                    Assert.assertEquals(Boolean.FALSE, other.getOperator(selector).isBindingSupported());
                }
            }
        }
        Assert.assertEquals(Boolean.FALSE, ExpressionOperator.locate().isBindingSupported());
        Assert.assertEquals(Boolean.FALSE, ExpressionOperator.locate2().isBindingSupported());
    }

    @Test
    public void testCoalescePreservesExplicitInlineValuesAndRemainingBindings() throws SQLException {
        for (DB2Platform platform : List.of(new DB2Platform(), new DerbyPlatform(),
                new DB2ZPlatform(), new DB2MainframePlatform())) {
            platform.setShouldBindAllParameters(true);
            platform.setShouldBindLiterals(true);
            platform.setShouldBindPartialParameters(true);
            for (boolean parameter : List.of(false, true)) {
                for (String placement : List.of("first", "last", "mapped")) {
                    DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
                    ExpressionBuilder builder = new ExpressionBuilder();
                    builder.setSession(session);
                    Expression inline = inlineCoalesceValue(builder, parameter);
                    Expression bound = builder.getParameter("bound", Integer.class);
                    List<Expression> arguments = switch (placement) {
                        case "first" -> List.of(inline, bound);
                        case "last" -> List.of(bound, inline);
                        default -> List.of(builder.getField("FLAG"), inline, bound);
                    };
                    Prepared prepared = prepare(session, builder, builder.coalesce(arguments));
                    DatabaseRecord values = new DatabaseRecord();
                    values.put("inline", 0);
                    values.put("bound", 7);
                    DatabaseCall execution = (DatabaseCall) prepared.call.clone();
                    execution.translate(values, null, session);
                    String expected = switch (placement) {
                        case "first" -> "COALESCE(0, ?)";
                        case "last" -> "COALESCE(?, 0)";
                        default -> "COALESCE(FLAG, 0, ?)";
                    };
                    Assert.assertEquals("SELECT " + expected + " FROM ITEM", execution.getSQLString());
                    Assert.assertEquals(List.of(7), execution.getParameters());
                    Assert.assertEquals(Boolean.FALSE, parameter ? ((ParameterExpression) inline).canBind()
                            : ((ConstantExpression) inline).canBind());
                }
            }
        }
    }

    @Test
    public void testCoalesceJavaPrintingPreservesExplicitInlineValues() {
        for (DB2Platform platform : List.of(new DB2Platform(), new DerbyPlatform(),
                new DB2ZPlatform(), new DB2MainframePlatform())) {
            platform.setShouldBindLiterals(true);
            platform.setShouldBindPartialParameters(true);
            ExpressionOperator operator = platform.getOperator(ExpressionOperator.Coalesce);
            // Supply Java separators, since the standard COALESCE operator only
            // defines SQL syntax. Exercise the platform's Java printing path.
            operator.printsJavaAs(List.of(", "));
            for (boolean parameter : List.of(false, true)) {
                ExpressionBuilder builder = new ExpressionBuilder();
                Expression inline = inlineCoalesceValue(builder, parameter);
                ParameterExpression bound = (ParameterExpression) builder.getParameter("bound", Integer.class);
                Expression field = builder.getField("FLAG");
                bound.setLocalBase(field);
                if (parameter) {
                    inline.setLocalBase(field);
                }
                StringWriter writer = new StringWriter();
                operator.printJavaCollection(List.of(inline, bound), new ExpressionJavaPrinter("builder", writer, platform));
                Assert.assertFalse(writer.toString().isEmpty());
                Assert.assertEquals(Boolean.FALSE, parameter ? ((ParameterExpression) inline).canBind()
                        : ((ConstantExpression) inline).canBind());
                Assert.assertEquals(Boolean.TRUE, bound.canBind());
            }
        }
    }

    private static org.eclipse.persistence.internal.sessions.AbstractRecord argumentRow(String name, Object value) {
        org.eclipse.persistence.queries.DataReadQuery query = new org.eclipse.persistence.queries.DataReadQuery();
        query.addArgument(name, Object.class);
        return query.rowFromArguments(java.util.Collections.singletonList(value), null);
    }

    @Test
    public void testSelectedNullTypesAndRebindingAcrossBindingModes() throws SQLException {
        for (DB2Platform platform : List.of(new DB2Platform(), new DerbyPlatform(), new DB2ZPlatform(), new DB2MainframePlatform())) {
            for (boolean binding : List.of(false, true)) {
                for (boolean partial : List.of(false, true)) {
                    platform.setShouldBindAllParameters(binding);
                    platform.setShouldBindPartialParameters(partial);
                    DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
                    ExpressionBuilder builder = new ExpressionBuilder();
                    builder.setSession(session);
                    ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Object.class);
                    DatabaseCall prepared = prepareCall(session, builder, parameter);
                    Object[] values = {null, new ParameterExpression.TypedNull(Integer.class), 42,
                            new ParameterExpression.TypedNull(String.class), "text", null};
                    String[] sqlValues = {"CAST(NULL AS VARCHAR(1))", "CAST(NULL AS INTEGER)", "42",
                            "CAST(NULL AS VARCHAR(1))", "'text'", "CAST(NULL AS VARCHAR(1))"};
                    for (int index = 0; index < values.length; index++) {
                        var row = argumentRow("value", values[index]);
                        DatabaseCall execution = (DatabaseCall) prepared.clone();
                        execution.translate(row, null, session);
                        Assert.assertEquals("SELECT " + sqlValues[index] + " FROM ITEM", execution.getSQLString());
                        Assert.assertTrue(execution.getParameters().isEmpty());
                        Assert.assertEquals(Object.class, parameter.getType());
                    }
                }
            }
        }
    }

    @Test
    public void testNullTranslationRetainsPromotedAndRelationalFieldTypes() throws SQLException {
        for (DB2Platform platform : List.of(new DB2Platform(), new DerbyPlatform())) {
            for (boolean partial : List.of(false, true)) {
                platform.setShouldBindAllParameters(true);
                platform.setShouldBindPartialParameters(partial);
                DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
                ExpressionBuilder builder = new ExpressionBuilder();
                builder.setSession(session);
                ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Object.class);
                for (boolean specificFirst : List.of(false, true)) {
                    org.eclipse.persistence.queries.DataReadQuery arguments = new org.eclipse.persistence.queries.DataReadQuery();
                    arguments.addArgument("value", specificFirst ? Long.class : Object.class);
                    arguments.addArgument("value", specificFirst ? Object.class : Long.class);
                    var promotedRow = arguments.rowFromArguments(java.util.Arrays.asList(null, null), session);
                    DatabaseCall promotedCall = prepareCall(session, builder, parameter);
                    promotedCall.translate(promotedRow, null, session);
                    Assert.assertEquals("SELECT CAST(NULL AS BIGINT) FROM ITEM", promotedCall.getSQLString());
                }

                DatabaseField relational = new DatabaseField("TEXT_VALUE");
                relational.setType(String.class);
                parameter = (ParameterExpression) builder.getParameter("relational", Integer.class);
                parameter.setLocalBase(builder.getField(relational));
                var row = argumentRow("relational", new ParameterExpression.TypedNull(Integer.class));
                DatabaseCall call = prepareCall(session, builder, parameter);
                call.translate(row, null, session);
                Assert.assertEquals("SELECT CAST(NULL AS VARCHAR(1)) FROM ITEM", call.getSQLString());
            }
        }
    }

    @Test
    public void testBoundSelectedNullTypesUseActualFullAndPartialTranslation() throws SQLException {
        for (var platform : List.of(new org.eclipse.persistence.platform.database.DatabasePlatform(), new PostgreSQLPlatform())) {
            for (boolean partial : List.of(false, true)) {
                platform.setShouldBindAllParameters(true);
                platform.setShouldBindPartialParameters(partial);
                DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
                ExpressionBuilder builder = new ExpressionBuilder();
                builder.setSession(session);
                ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Object.class);
                DatabaseCall prepared = prepareCall(session, builder, parameter, builder.getParameter("tail", Integer.class));
                DatabaseField relational = new DatabaseField("relational");
                relational.setType(String.class);
                Object[] values = {new ParameterExpression.TypedNull(Integer.class), 42,
                        new ParameterExpression.TypedNull(String.class), "text", null,
                        new ParameterExpression.ConvertedValue(relational)};
                int[] types = {Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
                for (int index = 0; index < values.length; index++) {
                    var row = argumentRow("value", values[index]);
                    row.put("tail", 77);
                    DatabaseCall execution = (DatabaseCall) prepared.clone();
                    execution.translate(row, null, session);
                    Assert.assertEquals("SELECT ?, ? FROM ITEM", execution.getSQLString());
                    Assert.assertEquals(2, execution.getParameters().size());
                    Assert.assertEquals(77, execution.getParameters().get(1));
                    Object bound = execution.getParameters().get(0);
                    if (values[index] instanceof Integer || values[index] instanceof String) {
                        Assert.assertEquals(values[index], bound);
                    } else {
                        Assert.assertTrue(bound instanceof DatabaseField);
                        List<Integer> jdbc = new ArrayList<>();
                        PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                                new Class<?>[]{PreparedStatement.class}, (proxy, method, args) -> {
                                    if (method.getName().equals("setNull")) {
                                        jdbc.add((Integer) args[0]);
                                        jdbc.add((Integer) args[1]);
                                        return null;
                                    }
                                    throw new UnsupportedOperationException(method.getName());
                                });
                        platform.setParameterValueInDatabaseCall(bound, statement, 1, session);
                        Assert.assertEquals(List.of(1, types[index]), jdbc);
                    }
                    Assert.assertEquals(Object.class, parameter.getType());
                }
                for (boolean specificFirst : List.of(false, true)) {
                    org.eclipse.persistence.queries.DataReadQuery arguments = new org.eclipse.persistence.queries.DataReadQuery();
                    arguments.addArgument("value", specificFirst ? Long.class : Object.class);
                    arguments.addArgument("value", specificFirst ? Object.class : Long.class);
                    arguments.addArgument("tail", Integer.class);
                    var row = arguments.rowFromArguments(java.util.Arrays.asList(null, null, 77), session);
                    DatabaseCall execution = (DatabaseCall) prepared.clone();
                    execution.translate(row, null, session);
                    Assert.assertEquals(Types.BIGINT, platform.getJDBCTypeForSetNull((DatabaseField) execution.getParameters().get(0)));
                    Assert.assertEquals(77, execution.getParameters().get(1));
                }
                parameter = (ParameterExpression) builder.getParameter("value", Integer.class);
                parameter.setLocalBase(builder.getField(relational));
                var row = argumentRow("value", new ParameterExpression.TypedNull(Integer.class));
                DatabaseCall mapped = prepareCall(session, builder, parameter);
                mapped.translate(row, null, session);
                Assert.assertEquals(Types.VARCHAR, platform.getJDBCTypeForSetNull((DatabaseField) mapped.getParameters().get(0)));
            }
        }
    }

    @Test
    public void testFullBindingKeepsOptionalAndCollectionParameterTranslation() throws SQLException {
        PostgreSQLPlatform platform = new PostgreSQLPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindPartialParameters(false);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        DatabaseCall optional = prepareCall(session, builder, builder.getParameter("optional", Object.class),
                builder.getParameter("tail", Integer.class));
        optional.getQuery().getNullableArguments().add(new DatabaseField("optional"));
        org.eclipse.persistence.internal.sessions.AbstractRecord row = argumentRow("optional", new ParameterExpression.TypedNull(Integer.class));
        row.put("tail", 77);
        optional.translate(row, null, session);
        Assert.assertEquals(List.of(77), optional.getParameters());

        DatabaseField field = new DatabaseField("value");
        field.setType(Long.class);
        DatabaseCall nativeParameter = prepareCall(session, builder, builder.getParameter("value", Object.class));
        nativeParameter.getParameters().set(0, field);
        row = new DatabaseRecord();
        row.put("value", null);
        nativeParameter.translate(row, new DatabaseRecord(), session);
        Assert.assertEquals(Types.BIGINT, platform.getJDBCTypeForSetNull((DatabaseField) nativeParameter.getParameters().get(0)));

        SQLSelectStatement membershipStatement = new SQLSelectStatement();
        membershipStatement.setBuilder(builder);
        membershipStatement.addField(builder.getField("ID"));
        membershipStatement.addTable(new DatabaseTable("ITEM"));
        membershipStatement.setWhereClause(builder.getField("ID").in(builder.getParameter("ids", java.util.Collection.class)));
        membershipStatement.computeTablesFromTables();
        DatabaseCall membership = membershipStatement.buildCall(session, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(session);
        row = new DatabaseRecord();
        row.put("ids", List.of(1, 2));
        DatabaseCall values = (DatabaseCall) membership.clone();
        values.translate(row, null, session);
        Assert.assertEquals(values.getSQLString(), List.of(1, 2), values.getParameters());
        Assert.assertTrue(values.getSQLString(), values.getSQLString().contains("IN (?,?)"));
        row.put("ids", null);
        DatabaseCall nullValue = (DatabaseCall) membership.clone();
        nullValue.translate(row, null, session);
        row = argumentRow("ids", new ParameterExpression.TypedNull(java.util.Collection.class));
        DatabaseCall typedNull = (DatabaseCall) membership.clone();
        typedNull.translate(row, null, session);
        Assert.assertEquals(nullValue.getSQLString(), typedNull.getSQLString());
        Assert.assertEquals(nullValue.getParameters(), typedNull.getParameters());
    }

    @Test
    public void testTypedNullSentinelIsNeverPrintedAsAFieldName() {
        DatabaseField field = new DatabaseField("parameter_name");
        field.setType(Integer.class);
        StringWriter writer = new StringWriter();
        new org.eclipse.persistence.internal.databaseaccess.DatabasePlatform()
                .appendParameterInternal(new org.eclipse.persistence.queries.SQLCall(), writer, field);
        Assert.assertEquals("NULL", writer.toString());
        for (DB2Platform platform : List.of(new DB2Platform(), new DerbyPlatform())) {
            writer = new StringWriter();
            platform.appendParameterInternal(new org.eclipse.persistence.queries.SQLCall(), writer, field);
            Assert.assertEquals("CAST(NULL AS INTEGER)", writer.toString());
        }
    }

    @Test
    public void testSelectedParameterUsesCurrentJdbcTypeWithoutChangingMappedFieldCaching() throws SQLException {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(new DB2Platform()));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        DatabaseField selected = prepareCall(session, builder, builder.getParameter("value", Object.class)).getFields().get(0);
        DatabaseAccessor accessor = new DatabaseAccessor();
        int[] type = {Types.VARCHAR};
        Object[] value = {null};
        AtomicInteger reads = new AtomicInteger();
        ResultSetMetaData metadata = metadata(type, reads);
        ResultSet results = results(value);
        DatabaseField restored = (DatabaseField) org.eclipse.persistence.sessions.serializers.JavaSerializer.instance.deserialize(
                org.eclipse.persistence.sessions.serializers.JavaSerializer.instance.serialize(selected, session), session);
        Assert.assertFalse(restored.shouldCacheSqlType());
        for (DatabaseField field : List.of(selected, selected.clone(), restored)) {
            type[0] = Types.VARCHAR;
            value[0] = null;
            Assert.assertNull(accessor.getObject(results, field, metadata, 1, session.getPlatform(), true, session));
            type[0] = Types.INTEGER;
            value[0] = 17;
            Assert.assertEquals(17, accessor.getObject(results, field, metadata, 1, session.getPlatform(), true, session));
            type[0] = Types.VARCHAR;
            value[0] = "later";
            Assert.assertEquals("later", accessor.getObject(results, field, metadata, 1, session.getPlatform(), true, session));
        }
        Assert.assertEquals(9, reads.get());
        DatabaseField mapped = new DatabaseField("MAPPED");
        mapped = (DatabaseField) org.eclipse.persistence.sessions.serializers.JavaSerializer.instance.deserialize(
                org.eclipse.persistence.sessions.serializers.JavaSerializer.instance.serialize(mapped, session), session);
        Assert.assertTrue(mapped.shouldCacheSqlType());
        Assert.assertEquals("later", accessor.getObject(results, mapped, metadata, 1, session.getPlatform(), true, session));
        Assert.assertEquals("later", accessor.getObject(results, mapped, metadata, 1, session.getPlatform(), true, session));
        Assert.assertEquals(10, reads.get());
    }

    @Test
    public void testSelectedParameterRetainsTemporalLobAndXmlJdbcDispatch() throws SQLException {
        List<Integer> dispatched = new ArrayList<>();
        List<Object> freed = new ArrayList<>();
        org.eclipse.persistence.platform.database.DatabasePlatform platform = new org.eclipse.persistence.platform.database.DatabasePlatform() {
            @Override
            public Object getObjectFromResultSet(ResultSet results, int column, int type,
                    org.eclipse.persistence.internal.sessions.AbstractSession session) throws SQLException {
                dispatched.add(type);
                return super.getObjectFromResultSet(results, column, type, session);
            }

            @Override
            public void freeTemporaryObject(Object value) {
                freed.add(value);
            }
        };
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        DatabaseField field = prepareCall(session, builder, builder.getParameter("value", Object.class)).getFields().get(0);
        DatabaseAccessor accessor = new DatabaseAccessor();
        int[] type = {Types.TIMESTAMP};
        Object[] value = {java.sql.Timestamp.valueOf("2026-01-02 03:04:05")};
        ResultSet results = results(value);
        ResultSetMetaData metadata = metadata(type, new AtomicInteger());
        Assert.assertSame(value[0], accessor.getObject(results, field, metadata, 1, platform, true, session));
        type[0] = Types.BLOB;
        value[0] = new javax.sql.rowset.serial.SerialBlob(new byte[]{1, 2, 3});
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) accessor.getObject(results, field, metadata, 1, platform, true, session));
        Assert.assertSame(value[0], freed.get(0));
        type[0] = Types.CLOB;
        value[0] = new javax.sql.rowset.serial.SerialClob("lob".toCharArray());
        Assert.assertEquals("lob", accessor.getObject(results, field, metadata, 1, platform, true, session));
        Assert.assertSame(value[0], freed.get(1));
        AtomicInteger xmlFreed = new AtomicInteger();
        type[0] = Types.SQLXML;
        value[0] = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{SQLXML.class}, (proxy, method, args) -> {
            if (method.getName().equals("getString")) {
                return "<value/>";
            }
            if (method.getName().equals("free")) {
                xmlFreed.incrementAndGet();
                return null;
            }
            throw new UnsupportedOperationException(method.getName());
        });
        Assert.assertEquals("<value/>", accessor.getObject(results, field, metadata, 1, platform, true, session));
        Assert.assertEquals(1, xmlFreed.get());
        Assert.assertEquals(List.of(Types.TIMESTAMP, Types.BLOB, Types.CLOB, Types.SQLXML), dispatched);
    }

    private ResultSetMetaData metadata(int[] type, AtomicInteger reads) {
        return (ResultSetMetaData) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSetMetaData.class}, (proxy, method, args) -> {
            if (method.getName().equals("getColumnType")) {
                reads.incrementAndGet();
                return type[0];
            }
            throw new UnsupportedOperationException(method.getName());
        });
    }

    private ResultSet results(Object[] value) {
        return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class}, (proxy, method, args) -> switch (method.getName()) {
            case "getObject" -> value[0];
            case "getString" -> value[0] == null ? null : value[0].toString();
            case "wasNull" -> value[0] == null;
            default -> throw new UnsupportedOperationException(method.getName());
        });
    }

    private Expression inlineCoalesceValue(ExpressionBuilder builder, boolean parameter) {
        if (parameter) {
            ParameterExpression value = (ParameterExpression) builder.getParameter("inline", Integer.class);
            value.setCanBind(false);
            return value;
        }
        ConstantExpression value = (ConstantExpression) builder.value(0);
        value.setCanBind(false);
        return value;
    }

    private String derbyLocateSql(String source, String search) {
        return "CASE WHEN 1 = 1 THEN LOCATE(" + search + ", " + source + ") ELSE CAST(NULL AS INTEGER) END";
    }

    private String derbyLocateSql(String source, String search, String start) {
        return "CASE WHEN CAST(" + start + " AS INTEGER) IS NULL THEN CAST(NULL AS INTEGER) ELSE LOCATE("
                + search + ", " + source + ", " + start + ") END";
    }

    private Prepared prepareMappedLocate(boolean partial, boolean coalesce, int selector) throws SQLException {
        DerbyPlatform platform = new DerbyPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindLiterals(true);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        Expression source = builder.getField("NAME");
        if (coalesce) {
            source = builder.coalesce(List.of(source, builder.getParameter("source", String.class)));
        }
        Expression search = builder.getParameter("search", String.class);
        Expression start = builder.getParameter("start", Integer.class);
        FunctionExpression expression = (FunctionExpression) (selector == ExpressionOperator.Locate
                ? source.locate(search) : source.locate(search, start));
        return prepare(session, builder, expression);
    }

    private String clampedSql(String function, String source, String length) {
        return function + "(" + source + ", CASE WHEN " + length + " > CHARACTER_LENGTH(" + source
                + ", CODEUNITS32) THEN CHARACTER_LENGTH(" + source + ", CODEUNITS32) ELSE " + length
                + " END, CODEUNITS32)";
    }

    private void assertLegacyMapped(DB2Platform platform) throws SQLException {
        for (boolean partial : List.of(false, true)) {
            assertSql(platform, partial, "mapped", false, new String[]{"LENGTH(NAME)",
                    "LOCATE('''\u00e9', NAME)", "LOCATE('''\u00e9', NAME, 2)", "SUBSTR(NAME, 2, 1)",
                    "SUBSTR(NAME, 2)", "LEFT(NAME, 1)", "RIGHT(NAME, 1)"});
        }
    }

    private void assertSql(DB2Platform platform, boolean partial, String kind, boolean binding, String[] expected)
            throws SQLException {
        for (int index = 0; index < SELECTORS.length; index++) {
            Prepared prepared = prepare(platform, partial, kind, binding, SELECTORS[index]);
            for (int iteration = 0; iteration < 2; iteration++) {
                DatabaseCall execution = execute(prepared, iteration);
                Assert.assertEquals("SELECT " + expected[index] + " FROM ITEM", execution.getSQLString());
                Assert.assertTrue(execution.getParameters().isEmpty());
            }
        }
    }

    private void assertParameterSql(DB2Platform platform, boolean partial, boolean unicode) throws SQLException {
        for (int index = 0; index < SELECTORS.length; index++) {
            int selector = SELECTORS[index];
            Prepared prepared = prepare(platform, partial, "parameter", true, selector);
            for (int iteration = 0; iteration < 2; iteration++) {
                String source = iteration == 0 ? SOURCE : "Other";
                String search = iteration == 0 ? SEARCH : "th";
                int start = iteration == 0 ? 2 : 1;
                int length = iteration == 0 ? 1 : 2;
                boolean derby = platform instanceof DerbyPlatform;
                boolean inline = selector == ExpressionOperator.Length || derby && !partial
                        || platform instanceof DB2ZPlatform && (selector == ExpressionOperator.Locate || selector == ExpressionOperator.Locate2);
                String s = inline ? quote(source) : "?";
                String pattern = inline ? quote(search) : "?";
                String from = inline ? Integer.toString(start) : "?";
                String size = inline ? Integer.toString(length) : "?";
                String[] fragments = unicode ? new String[]{"CHARACTER_LENGTH(" + s + ", CODEUNITS32)",
                        "LOCATE(" + pattern + ", " + s + ", CODEUNITS32)",
                        "LOCATE(" + pattern + ", " + s + ", " + from + ", CODEUNITS32)",
                        "SUBSTRING(" + s + ", " + from + ", " + size + ", CODEUNITS32)",
                        "SUBSTRING(" + s + ", " + from + ", CODEUNITS32)",
                        clampedSql("LEFT", s, size), clampedSql("RIGHT", s, size)}
                        : new String[]{"LENGTH(" + s + ")", derby ? derbyLocateSql(s, pattern) : "LOCATE(" + pattern + ", " + s + ")",
                        derby ? derbyLocateSql(s, pattern, from) : "LOCATE(" + pattern + ", " + s + ", " + from + ")",
                        "SUBSTR(" + s + ", " + from + ", " + size + ")",
                        "SUBSTR(" + s + ", " + from + ")", derby ? "SUBSTR(" + s + ", 1, " + size + ")" : "LEFT(" + s + ", " + size + ")",
                        derby ? "SUBSTR(" + s + ", LENGTH(" + s + ") + 1 - " + size + ", " + size + ")" : "RIGHT(" + s + ", " + size + ")"};
                List<?> parameters = inline ? List.of() : switch (selector) {
                    case ExpressionOperator.Locate -> List.of(search, source);
                    case ExpressionOperator.Locate2 -> derby ? List.of(start, search, source, start) : List.of(search, source, start);
                    case ExpressionOperator.Substring -> List.of(source, start, length);
                    case ExpressionOperator.SubstringSingleArg -> List.of(source, start);
                    case ExpressionOperator.Left -> unicode ? List.of(source, length, source, source, length) : List.of(source, length);
                    case ExpressionOperator.Right -> unicode ? List.of(source, length, source, source, length)
                            : derby ? List.of(source, source, length, length) : List.of(source, length);
                    default -> List.of(source, length);
                };
                DatabaseCall execution = execute(prepared, iteration);
                Assert.assertEquals("SELECT " + fragments[index] + " FROM ITEM", execution.getSQLString());
                Assert.assertEquals(parameters, execution.getParameters());
                Assert.assertEquals(!derby || partial, execution.usesBinding(platform));
            }
        }
    }

    private String quote(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    private ExpressionOperator ordinaryOperator(int selector) {
        return switch (selector) {
            case ExpressionOperator.Length -> ExpressionOperator.length();
            case ExpressionOperator.Locate -> ExpressionOperator.locate();
            case ExpressionOperator.Locate2 -> ExpressionOperator.locate2();
            case ExpressionOperator.Substring -> ExpressionOperator.substring();
            case ExpressionOperator.SubstringSingleArg -> ExpressionOperator.substringSingleArg();
            case ExpressionOperator.Left -> ExpressionOperator.left();
            default -> ExpressionOperator.right();
        };
    }

    private Prepared prepare(DB2Platform platform, boolean partial, String kind, boolean binding, int selector)
            throws SQLException {
        platform.setShouldBindAllParameters(binding);
        platform.setShouldBindLiterals(binding);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        Expression source = kind.equals("mapped") ? builder.getField("NAME") : kind.equals("literal")
                ? builder.value(SOURCE) : builder.getParameter("source", String.class);
        Object search = kind.equals("parameter") ? builder.getParameter("search", String.class) : SEARCH;
        Object start = kind.equals("parameter") ? builder.getParameter("start", Integer.class) : 2;
        Object length = kind.equals("parameter") ? builder.getParameter("length", Integer.class) : 1;
        FunctionExpression expression = (FunctionExpression) switch (selector) {
            case ExpressionOperator.Length -> source.length();
            case ExpressionOperator.Locate -> source.locate(search);
            case ExpressionOperator.Locate2 -> source.locate(search, start);
            case ExpressionOperator.Substring -> source.substring(start, length);
            case ExpressionOperator.SubstringSingleArg -> source.substring(start);
            case ExpressionOperator.Left -> source.left(length);
            default -> source.right(length);
        };
        return prepare(session, builder, expression);
    }

    private Prepared prepare(DatabaseSessionImpl session, ExpressionBuilder builder, FunctionExpression expression)
            throws SQLException {
        return new Prepared(session, prepareCall(session, builder, expression), expression);
    }

    private DatabaseCall prepareCall(DatabaseSessionImpl session, ExpressionBuilder builder, Expression... expressions)
            throws SQLException {
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        for (Expression expression : expressions) {
            statement.addField(expression);
        }
        statement.addTable(new DatabaseTable("ITEM"));
        statement.computeTablesFromTables();
        DatabaseCall call = statement.buildCall(session, null);
        call.setQuery(new ReportQuery());
        call.prepare(session);
        return call;
    }

    private DatabaseCall execute(Prepared prepared, int iteration) throws SQLException {
        DatabaseRecord values = new DatabaseRecord();
        values.put("source", iteration == 0 ? SOURCE : "Other");
        values.put("search", iteration == 0 ? SEARCH : "th");
        values.put("start", iteration == 0 ? 2 : 1);
        values.put("length", iteration == 0 ? 1 : 2);
        DatabaseCall execution = (DatabaseCall) prepared.call.clone();
        execution.translate(values, null, prepared.session);
        return execution;
    }

    private record Prepared(DatabaseSessionImpl session, DatabaseCall call, FunctionExpression expression) { }
}
