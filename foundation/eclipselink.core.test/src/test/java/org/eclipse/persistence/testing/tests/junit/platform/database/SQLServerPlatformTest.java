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
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Test;

public class SQLServerPlatformTest {

    @Test
    public void testNullOffsetTemporalUsesTimestamp() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(false);

        Assert.assertEquals(Types.TIMESTAMP, platform.getJDBCTypeForSetNull(fieldWithType(OffsetDateTime.class)));
        Assert.assertEquals(Types.TIMESTAMP, platform.getJDBCTypeForSetNull(fieldWithType(OffsetTime.class)));
    }

    @Test
    public void testNullOffsetTemporalUsesTimezone() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(true);

        Assert.assertEquals(Types.TIMESTAMP_WITH_TIMEZONE,
                platform.getJDBCTypeForSetNull(fieldWithType(OffsetDateTime.class)));
        Assert.assertEquals(Types.TIME_WITH_TIMEZONE,
                platform.getJDBCTypeForSetNull(fieldWithType(OffsetTime.class)));
    }

    @Test
    public void testExplicitNullSqlTypeIsPreserved() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(false);

        DatabaseField field = fieldWithType(OffsetDateTime.class);
        field.setSqlType(Types.OTHER);

        Assert.assertEquals(Types.OTHER, platform.getJDBCTypeForSetNull(field));
    }

    @Test
    public void testOrderByValuesAndDirections() {
        for (boolean bind : new boolean[] {true, false}) {
            SQLServerPlatform platform = new SQLServerPlatform();
            platform.setShouldBindLiterals(bind);
            DatabaseSessionImpl session = session(platform);
            ExpressionBuilder builder = builder(session);
            for (Object value : Arrays.asList(1, 2, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1.5, null)) {
                Expression literal = builder.value(value);
                List<Expression> orders = List.of(literal, literal.ascending(), literal.descending());
                for (int i = 0; i < orders.size(); i++) {
                    DatabaseCall call = statement(builder, orders.get(i)).buildCall(session, new DataReadQuery());
                    String direction = i == 0 ? "" : i == 1 ? " ASC" : " DESC";
                    Assert.assertEquals("SELECT VALUE FROM TEST_TABLE ORDER BY (SELECT "
                            + (bind ? "?" : value == null ? "NULL" : value) + ")" + direction, call.getSQLString());
                    Assert.assertEquals(bind ? Arrays.asList(value) : List.of(), call.getParameters());
                    Assert.assertEquals(bind ? List.of(ParameterType.LITERAL) : List.of(), call.getParameterTypes());
                }
            }
        }
    }

    @Test
    public void testOrderByParameterTranslationAndReuse() {
        DatabaseSessionImpl session = session(new SQLServerPlatform());
        ExpressionBuilder builder = builder(session);
        ParameterExpression parameter = (ParameterExpression) builder.getParameter("sort", Integer.class);
        parameter.getField().setType(Integer.class);
        Expression original = parameter.descending();
        SQLSelectStatement statement = statement(builder, original);
        statement.setWhereClause(builder.getField("VALUE").greaterThan(parameter));
        String expected = "SELECT VALUE FROM TEST_TABLE WHERE (VALUE > ?) ORDER BY (SELECT ?) DESC";
        DatabaseCall prepared = statement.buildCall(session, new DataReadQuery());
        Assert.assertEquals(expected, prepared.getSQLString());
        Assert.assertEquals(List.of(ParameterType.TRANSLATION, ParameterType.TRANSLATION), prepared.getParameterTypes());
        Assert.assertSame(parameter, prepared.getParameters().get(0));
        Assert.assertSame(parameter, prepared.getParameters().get(1));
        Assert.assertEquals(Arrays.asList(null, null), prepared.getParameterBindings());
        Assert.assertSame(parameter, ((FunctionExpression) original).getBaseExpression());
        Assert.assertEquals(expected, statement.buildCall(session, new DataReadQuery()).getSQLString());
        Assert.assertEquals("SELECT VALUE FROM TEST_TABLE ORDER BY (SELECT ?) DESC",
                statement(builder, original).buildCall(session, new DataReadQuery()).getSQLString());

        prepared.prepare(session);
        for (Integer value : Arrays.asList(1, 2, null, -1)) {
            DatabaseRecord row = new DatabaseRecord();
            row.put(parameter.getField(), value);
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, new DatabaseRecord(), session);
            Assert.assertEquals(expected, execution.getSQLString());
            if (value == null) {
                Assert.assertEquals(2, execution.getParameters().size());
                for (Object bound : execution.getParameters()) {
                    Assert.assertTrue(bound instanceof DatabaseField);
                    Assert.assertEquals(Integer.class, ((DatabaseField) bound).getType());
                }
            } else {
                Assert.assertEquals(List.of(value, value), execution.getParameters());
            }
            Assert.assertSame(parameter, prepared.getParameters().get(1));
        }
    }

    @Test
    public void testOrderByNullPrecedence() {
        DatabaseSessionImpl session = session(new SQLServerPlatform());
        ExpressionBuilder builder = builder(session);
        Expression parameter = builder.getParameter("sort", Integer.class);
        List<Expression> orders = List.of(parameter, parameter.ascending(), parameter.descending());
        for (boolean first : new boolean[] {true, false}) {
            for (int i = 0; i < orders.size(); i++) {
                Expression order = first ? orders.get(i).nullsFirst() : orders.get(i).nullsLast();
                SQLSelectStatement statement = statement(builder, order);
                String expected = "SELECT VALUE FROM TEST_TABLE ORDER BY (CASE WHEN (SELECT ?) IS NULL THEN "
                        + (first ? "0 ELSE 1" : "1 ELSE 0") + " END), (SELECT ?)"
                        + (i == 0 ? "" : i == 1 ? " ASC" : " DESC");
                DatabaseCall call = statement.buildCall(session, new DataReadQuery());
                Assert.assertEquals(expected, call.getSQLString());
                Assert.assertEquals(List.of(parameter, parameter), call.getParameters());
                Assert.assertEquals(expected, statement.buildCall(session, new DataReadQuery()).getSQLString());
                Assert.assertEquals(expected, statement(builder, order).buildCall(session, new DataReadQuery()).getSQLString());
            }
        }
    }

    @Test
    public void testOrderByPagination() throws Exception {
        SQLServerPlatform platform = new SQLServerPlatform();
        DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {DatabaseMetaData.class}, (proxy, method, arguments) -> switch (method.getName()) {
                    case "getDriverName" -> "Microsoft JDBC Driver";
                    case "getDatabaseMajorVersion" -> 16;
                    case "getDriverVersion" -> "13.6.2";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        Connection connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {Connection.class}, (proxy, method, arguments) -> {
                    if ("getMetaData".equals(method.getName())) {
                        return metadata;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        platform.initializeConnectionData(connection);
        DatabaseSessionImpl session = session(platform);
        ExpressionBuilder builder = builder(session);
        Expression parameter = builder.getParameter("sort", Integer.class);
        for (int max : new int[] {0, 3}) {
            SQLSelectStatement statement = statement(builder, parameter.ascending());
            statement.getOrderByExpressions().add(builder.getField("VALUE").descending());
            DataReadQuery query = new DataReadQuery();
            query.setFirstResult(1);
            query.setMaxRows(max);
            statement.setQuery(query);
            DatabaseCall call = statement.buildCall(session, query);
            Assert.assertEquals("SELECT VALUE FROM TEST_TABLE ORDER BY (SELECT ?) ASC, VALUE DESC OFFSET ? ROWS"
                    + (max == 0 ? "" : " FETCH NEXT ? ROWS ONLY"), call.getSQLString());
            Assert.assertEquals(max == 0 ? List.of(parameter, 1) : List.of(parameter, 1, 2), call.getParameters());
            Assert.assertTrue(call.shouldIgnoreFirstRowSetting());
            Assert.assertTrue(call.shouldIgnoreMaxResultsSetting());
        }
        SQLSelectStatement unordered = statement(builder, parameter);
        unordered.getOrderByExpressions().clear();
        DataReadQuery query = new DataReadQuery();
        query.setFirstResult(1);
        query.setMaxRows(3);
        unordered.setQuery(query);
        DatabaseCall call = unordered.buildCall(session, query);
        Assert.assertEquals("SELECT VALUE FROM TEST_TABLE ORDER BY ROW_NUMBER() OVER (ORDER BY (SELECT null))"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", call.getSQLString());
        Assert.assertEquals(List.of(1, 2), call.getParameters());
        Assert.assertTrue(call.shouldIgnoreFirstRowSetting());
        Assert.assertTrue(call.shouldIgnoreMaxResultsSetting());
    }

    @Test
    public void testOrderByExcludedExpressionsAndStatements() {
        SQLServerPlatform platform = new SQLServerPlatform();
        DatabaseSessionImpl session = session(platform);
        ExpressionBuilder builder = builder(session);
        Expression parameter = builder.getParameter("sort", Integer.class);
        Expression field = builder.getField("VALUE");
        List<Expression> orders = List.of(field, ExpressionMath.abs(parameter),
                ExpressionMath.add(parameter, field), ExpressionMath.add(field, parameter),
                ExpressionMath.add(parameter, parameter), builder.literal("1"), parameter.sql("ABS(?)", List.of()));
        List<String> expected = List.of("VALUE", "ABS(?)", "(? + VALUE)", "(VALUE + ?)", "(? + ?)", "1", "ABS(?)");
        for (int i = 0; i < orders.size(); i++) {
            SQLSelectStatement statement = statement(builder, orders.get(i));
            Assert.assertEquals("SELECT VALUE FROM TEST_TABLE ORDER BY " + expected.get(i),
                    statement.buildCall(session, new DataReadQuery()).getSQLString());
            Assert.assertSame(orders.get(i), statement.getOrderByExpressions().get(0));
        }
        SQLSelectStatement distinct = statement(builder, parameter.ascending());
        distinct.useDistinct();
        Assert.assertEquals("SELECT DISTINCT VALUE FROM TEST_TABLE ORDER BY ? ASC",
                distinct.buildCall(session, new DataReadQuery()).getSQLString());
        for (String operation : List.of("UNION", "INTERSECT", "EXCEPT")) {
            SQLSelectStatement union = statement(builder, parameter.descending());
            union.setUnionExpressions(List.of(builder.literal(operation + " SELECT VALUE FROM TEST_TABLE")));
            Assert.assertEquals("(SELECT VALUE FROM TEST_TABLE ORDER BY ? DESC " + operation + " SELECT VALUE FROM TEST_TABLE)",
                    union.buildCall(session, new DataReadQuery()).getSQLString());
        }
        platform.setShouldBindLiterals(false);
        SQLSelectStatement literalDistinct = statement(builder, builder.value(1));
        literalDistinct.useDistinct();
        literalDistinct.addField(builder.value(1));
        Assert.assertEquals("SELECT DISTINCT VALUE, 1 FROM TEST_TABLE ORDER BY 1",
                literalDistinct.buildCall(session, new DataReadQuery()).getSQLString());
    }

    private DatabaseSessionImpl session(SQLServerPlatform platform) {
        platform.setShouldBindAllParameters(true);
        DatabaseLogin login = new DatabaseLogin();
        login.setPlatform(platform);
        return new DatabaseSessionImpl(login);
    }

    private ExpressionBuilder builder(DatabaseSessionImpl session) {
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        return builder;
    }

    private SQLSelectStatement statement(ExpressionBuilder builder, Expression order) {
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addField(new DatabaseField("VALUE"));
        statement.addTable(new DatabaseTable("TEST_TABLE"));
        statement.computeTablesFromTables();
        statement.getOrderByExpressions().add(order);
        return statement;
    }

    private DatabaseField fieldWithType(Class<?> type) {
        DatabaseField field = new DatabaseField("OFFSET_DATE_TIME");
        field.setType(type);
        return field;
    }
}
