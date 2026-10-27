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
 * The null-ordering regression tests were generated with assistance from OpenAI Codex.
 * Existing tests and licensing are retained.
 */
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LiteralExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.jpql.HermesParser;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
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
    public void testDefaultNullsFirst() throws SQLException {
        assertNullOrdering("", true);
    }

    @Test
    public void testDefaultNullsLast() throws SQLException {
        assertNullOrdering("", false);
    }

    @Test
    public void testAscendingNullsFirst() throws SQLException {
        assertNullOrdering(" ASC", true);
    }

    @Test
    public void testAscendingNullsLast() throws SQLException {
        assertNullOrdering(" ASC", false);
    }

    @Test
    public void testDescendingNullsFirst() throws SQLException {
        assertNullOrdering(" DESC", true);
    }

    @Test
    public void testDescendingNullsLast() throws SQLException {
        assertNullOrdering(" DESC", false);
    }

    @Test
    public void testNullOrderingWithComposedExpression() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").toUpperCase().descending().nullsLast());

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN UPPER(NAME) IS NULL THEN 1 ELSE 0 END), UPPER(NAME) DESC", false);
    }

    @Test
    public void testMultipleOrderTermsKeepDirectionAndPrecedence() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        ExpressionBuilder builder = statement.getBuilder();
        statement.getOrderByExpressions().add(builder.getField("NAME").ascending().nullsLast());
        statement.getOrderByExpressions().add(builder.getField("ID").descending());
        statement.getOrderByExpressions().add(builder.getField("CODE").descending().nullsFirst());

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 1 ELSE 0 END), NAME ASC, ID DESC, "
                        + "(CASE WHEN CODE IS NULL THEN 0 ELSE 1 END), CODE DESC", false);
    }

    @Test
    public void testPagedNullOrderingDoesNotMutateStatement() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").descending().nullsLast());
        statement.setQuery(pagedQuery(2, 7));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 1 ELSE 0 END), NAME DESC "
                        + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", true);
    }

    @Test
    public void testPagedEmptyOrderingDoesNotMutateStatement() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        statement.setQuery(pagedQuery(2, 7));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY ROW_NUMBER() OVER (ORDER BY (SELECT null)) "
                        + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", true);
    }

    @Test
    public void testOffsetWithoutLimitDoesNotMutateStatement() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").ascending().nullsFirst());
        statement.setQuery(pagedQuery(2, 0));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 0 ELSE 1 END), NAME ASC OFFSET ? ROWS", true);
    }

    @Test
    public void testVersion10FallsBackWithoutPagination() throws SQLException {
        DatabaseSessionImpl session = session(10);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").descending().nullsFirst());
        statement.setQuery(pagedQuery(2, 7));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 0 ELSE 1 END), NAME DESC", false);
    }

    @Test
    public void testUnpagedQueryDoesNotMutateStatement() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").ascending().nullsLast());
        statement.setQuery(pagedQuery(0, 0));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 1 ELSE 0 END), NAME ASC", false);
    }

    @Test
    public void testDisabledRownumFilteringKeepsNullOrdering() throws SQLException {
        DatabaseSessionImpl session = session(11);
        session.getPlatform().setShouldUseRownumFiltering(false);
        SQLSelectStatement statement = statement(session);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").nullsLast());
        statement.setQuery(pagedQuery(2, 7));

        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 1 ELSE 0 END), NAME", false);
    }

    @Test
    public void testOrderByRestoredAfterRenderingFailure() throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        IllegalStateException failure = new IllegalStateException("Rendering failed");
        Expression field = new LiteralExpression("NAME", statement.getBuilder()) {
            @Override
            public void printSQL(ExpressionSQLPrinter printer) {
                throw failure;
            }
        };
        Expression sort = field.descending();
        Expression ordering = sort.nullsLast();
        List<Expression> originalOrderBy = statement.getOrderByExpressions();
        originalOrderBy.add(ordering);
        statement.setQuery(pagedQuery(2, 7));

        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                statement.buildCall(session, statement.getQuery());
                Assert.fail("Expected rendering to fail");
            } catch (IllegalStateException actual) {
                Assert.assertSame(failure, actual);
            }
            Assert.assertSame(originalOrderBy, statement.getOrderByExpressions());
            Assert.assertEquals(1, originalOrderBy.size());
            Assert.assertSame(ordering, originalOrderBy.get(0));
            Assert.assertSame(sort, ((FunctionExpression) ordering).getBaseExpression());
            Assert.assertSame(field, ((FunctionExpression) sort).getBaseExpression());
            Assert.assertEquals(ExpressionOperator.NullsLast, ordering.getOperator().getSelector());
            Assert.assertEquals(ExpressionOperator.Descending, sort.getOperator().getSelector());
        }
    }

    @Test
    public void testPreparedPaginationUsesCurrentExecutionValues() throws SQLException {
        for (boolean binding : List.of(false, true)) {
            DatabaseSessionImpl session = mappedSession(11, new SQLServerPlatform());
            session.getPlatform().setShouldBindAllParameters(binding);
            ReadAllQuery query = (ReadAllQuery) new HermesParser().buildQuery(
                    "SELECT i FROM Item i ORDER BY i.id", session);
            query.setFirstResult(1);
            query.setMaxRows(2);
            query.prepareCall(session, new DatabaseRecord());
            DatabaseCall prepared = query.getCall();
            assertPaginationTranslation(session, query, binding, 1, 1);

            query.setFirstResult(0);
            query.setMaxRows(2);
            Assert.assertTrue(query.isPrepared());
            Assert.assertSame(prepared, query.getCall());
            assertPaginationTranslation(session, query, binding, 0, 2);

            query.setFirstResult(2);
            query.setMaxRows(5);
            assertPaginationTranslation(session, query, binding, 2, 3);
            query.setMaxRows(3);
            assertPaginationTranslation(session, query, binding, 2, 1);
            assertPaginationTranslation(session, query, binding, 2, 1);
        }
    }

    @Test
    public void testPreparedOffsetOnlyUsesCurrentExecutionValue() throws SQLException {
        for (boolean binding : List.of(false, true)) {
            DatabaseSessionImpl session = mappedSession(11, new SQLServerPlatform());
            session.getPlatform().setShouldBindAllParameters(binding);
            ReadAllQuery query = (ReadAllQuery) new HermesParser().buildQuery(
                    "SELECT i FROM Item i ORDER BY i.id", session);
            query.setFirstResult(2);
            query.prepareCall(session, new DatabaseRecord());
            DatabaseCall prepared = query.getCall();
            assertPaginationTranslation(session, query, binding, 2, 0);
            query.setFirstResult(0);
            Assert.assertTrue(query.isPrepared());
            Assert.assertSame(prepared, query.getCall());
            assertPaginationTranslation(session, query, binding, 0, 0);
            query.setMaxRows(2);
            Assert.assertFalse(query.isPrepared());
            query.prepareCall(session, new DatabaseRecord());
            assertPaginationTranslation(session, query, binding, 0, 2);
        }
    }

    private void assertPaginationTranslation(DatabaseSessionImpl session, ReadAllQuery query,
                                             boolean binding, int offset, int limit) {
        ReadAllQuery execution = (ReadAllQuery) query.clone();
        execution.setSession(session);
        execution.setTranslationRow(new DatabaseRecord());
        execution.prepareForExecution();
        DatabaseCall call = (DatabaseCall) query.getCall().clone();
        call.translate(execution.getTranslationRow(), null, session);
        if (binding) {
            Assert.assertEquals(limit == 0 ? List.of(offset) : List.of(offset, limit), call.getParameters());
        } else {
            String pagination = " OFFSET " + offset + " ROWS";
            if (limit > 0) {
                pagination += " FETCH NEXT " + limit + " ROWS ONLY";
            }
            Assert.assertTrue(call.getSQLString(), call.getSQLString().endsWith(pagination));
        }
    }

    @Test
    public void testDistinctProjectionThroughNormalQueryPreparation() throws SQLException {
        for (int version : List.of(10, 11)) {
            for (String selection : List.of("i.name", "i.name, i.code", "i")) {
                for (String direction : List.of("", " ASC", " DESC")) {
                    for (String precedence : List.of(" FIRST", " LAST")) {
                        for (boolean paged : List.of(false, true)) {
                            CapturingSQLServerPlatform platform = new CapturingSQLServerPlatform();
                            DatabaseSessionImpl session = mappedSession(version, platform);
                            DatabaseQuery query = new HermesParser().buildQuery("SELECT DISTINCT " + selection
                                    + " FROM Item i ORDER BY i.name" + direction + " NULLS" + precedence, session);
                            if (paged) {
                                ((ReadAllQuery) query).setFirstResult(1);
                                ((ReadAllQuery) query).setMaxRows(4);
                            }
                            query.prepareCall(session, new DatabaseRecord());
                            int columns = selection.equals("i") ? 3 : selection.contains(",") ? 2 : 1;
                            assertDistinctProjection(query.getCall(), 1, columns);
                            SQLSelectStatement statement = platform.statement;
                            for (int attempt = 0; attempt < 2; attempt++) {
                                assertDistinctProjection(statement.buildCall(session, query), 1, columns);
                            }
                            if (query instanceof ReportQuery report) {
                                Assert.assertEquals(columns, report.getItems().size());
                                for (int index = 0; index < columns; index++) {
                                    Assert.assertEquals(index, report.getItems().get(index).getResultIndex());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testDistinctMultipleOrderingsKeepVisibleResultFields() throws SQLException {
        CapturingSQLServerPlatform platform = new CapturingSQLServerPlatform();
        DatabaseSessionImpl session = mappedSession(11, platform);
        platform.setShouldBindAllParameters(true);
        DatabaseQuery query = new HermesParser().buildQuery("SELECT DISTINCT UPPER(i.name), i.code FROM Item i "
                + "WHERE i.id > :minimum AND i.code <> :excluded "
                + "ORDER BY UPPER(i.name) DESC NULLS LAST, i.code ASC NULLS FIRST", session);
        query.prepareCall(session, new DatabaseRecord());
        DatabaseCall prepared = query.getCall();
        assertDistinctProjection(prepared, 2, 3);
        for (int attempt = 0; attempt < 2; attempt++) {
            DatabaseCall repeated = platform.statement.buildCall(session, query);
            assertDistinctProjection(repeated, 2, 3);
            Assert.assertEquals(prepared.getSQLString(), repeated.getSQLString());
            Assert.assertEquals(2, repeated.getParameters().size());
            Assert.assertEquals("minimum", ((ParameterExpression) repeated.getParameters().get(0)).getField().getName());
            Assert.assertEquals("excluded", ((ParameterExpression) repeated.getParameters().get(1)).getField().getName());
        }
        Assert.assertEquals(2, ((ReportQuery) query).getItems().size());
    }

    @Test
    public void testDistinctFieldsRestoredAfterRenderingFailure() throws SQLException {
        CapturingSQLServerPlatform platform = new CapturingSQLServerPlatform();
        DatabaseSessionImpl session = session(11, platform);
        SQLSelectStatement statement = statement(session);
        statement.useDistinct();
        IllegalStateException failure = new IllegalStateException("Rendering failed");
        Expression field = new LiteralExpression("NAME", statement.getBuilder()) {
            @Override
            public void printSQL(ExpressionSQLPrinter printer) {
                throw failure;
            }
        };
        statement.getOrderByExpressions().add(field.descending().nullsLast());
        statement.setQuery(pagedQuery(2, 7));
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                statement.buildCall(session, statement.getQuery());
                Assert.fail("Expected rendering to fail");
            } catch (IllegalStateException actual) {
                Assert.assertSame(failure, actual);
            }
        }
    }

    private void assertDistinctProjection(DatabaseCall call, int helpers, int visibleColumns) {
        String sql = call.getSQLString();
        String projection = sql.substring(0, sql.indexOf(" FROM "));
        Assert.assertEquals(sql, helpers, projection.split("CASE WHEN", -1).length - 1);
        Assert.assertEquals(sql, visibleColumns, call.getFields().size());
        Assert.assertEquals(sql, visibleColumns, call.getFieldsArray().length);
    }

    private DatabaseSessionImpl mappedSession(int version, SQLServerPlatform platform) throws SQLException {
        DatabaseSessionImpl session = session(version, platform);
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Item.class);
        descriptor.setAlias("Item");
        descriptor.addTableName("ITEM");
        descriptor.addPrimaryKeyFieldName("ITEM.ID");
        descriptor.addDirectMapping("id", "ITEM.ID");
        descriptor.addDirectMapping("name", "ITEM.NAME");
        descriptor.addDirectMapping("code", "ITEM.CODE");
        session.addDescriptor(descriptor);
        session.initializeDescriptors();
        return session;
    }

    private static class CapturingSQLServerPlatform extends SQLServerPlatform {
        private SQLSelectStatement statement;

        @Override
        public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
            this.statement = statement;
            List<?> fields = statement.getFields();
            List<?> fieldSnapshot = new ArrayList<>(fields);
            List<Expression> ordering = statement.getOrderByExpressions();
            List<Expression> orderSnapshot = new ArrayList<>(ordering);
            try {
                super.printSQLSelectStatement(call, printer, statement);
            } finally {
                Assert.assertSame(fields, statement.getFields());
                Assert.assertSame(ordering, statement.getOrderByExpressions());
                assertElementsUnchanged(fields, fieldSnapshot);
                assertElementsUnchanged(ordering, orderSnapshot);
            }
        }

        private void assertElementsUnchanged(List<?> actual, List<?> expected) {
            Assert.assertEquals(expected.size(), actual.size());
            for (int index = 0; index < expected.size(); index++) {
                Assert.assertSame(expected.get(index), actual.get(index));
            }
        }
    }

    public static class Item {
        public Integer id;
        public String name;
        public String code;

        public Item() {
        }
    }

    private void assertNullOrdering(String direction, boolean nullsFirst) throws SQLException {
        DatabaseSessionImpl session = session(11);
        SQLSelectStatement statement = statement(session);
        Expression field = statement.getBuilder().getField("NAME");
        Expression sort = switch (direction) {
            case " ASC" -> field.ascending();
            case " DESC" -> field.descending();
            default -> field;
        };
        Expression ordering = nullsFirst ? sort.nullsFirst() : sort.nullsLast();
        statement.getOrderByExpressions().add(ordering);

        String values = nullsFirst ? "0 ELSE 1" : "1 ELSE 0";
        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN " + values + " END), NAME" + direction, false);
        Assert.assertSame(sort, ((FunctionExpression) ordering).getBaseExpression());
        Assert.assertEquals(nullsFirst ? ExpressionOperator.NullsFirst : ExpressionOperator.NullsLast,
                ordering.getOperator().getSelector());
        if (!direction.isEmpty()) {
            Assert.assertSame(field, ((FunctionExpression) sort).getBaseExpression());
            Assert.assertEquals(direction.equals(" ASC") ? ExpressionOperator.Ascending : ExpressionOperator.Descending,
                    sort.getOperator().getSelector());
        }
    }

    private void assertRenderedTwice(DatabaseSessionImpl session, SQLSelectStatement statement, String expectedSql,
                                     boolean pagedInSql) {
        List<Expression> originalOrderBy = statement.getOrderByExpressions();
        List<Expression> originalExpressions = new ArrayList<>(originalOrderBy);
        for (int attempt = 0; attempt < 2; attempt++) {
            DatabaseCall call = statement.buildCall(session, statement.getQuery());
            Assert.assertEquals(expectedSql, call.getSQLString());
            Assert.assertEquals(pagedInSql, call.shouldIgnoreFirstRowSetting());
            Assert.assertEquals(pagedInSql, call.shouldIgnoreMaxResultsSetting());
            Assert.assertSame(originalOrderBy, statement.getOrderByExpressions());
            Assert.assertEquals(originalExpressions.size(), originalOrderBy.size());
            for (int index = 0; index < originalExpressions.size(); index++) {
                Assert.assertSame(originalExpressions.get(index), originalOrderBy.get(index));
            }
        }
    }

    private SQLSelectStatement statement(DatabaseSessionImpl session) {
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addField(new DatabaseField("ID"));
        statement.addTable(new DatabaseTable("ITEM"));
        statement.computeTablesFromTables();
        return statement;
    }

    private ReadAllQuery pagedQuery(int first, int max) {
        ReadAllQuery query = new ReadAllQuery();
        query.setFirstResult(first);
        query.setMaxRows(max);
        return query;
    }

    private DatabaseSessionImpl session(int databaseMajorVersion) throws SQLException {
        return session(databaseMajorVersion, new SQLServerPlatform());
    }

    private DatabaseSessionImpl session(int databaseMajorVersion, SQLServerPlatform platform) throws SQLException {
        DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(DatabaseMetaData.class.getClassLoader(),
                new Class<?>[] {DatabaseMetaData.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getDriverName" -> "Microsoft JDBC Driver";
                    case "getDriverVersion" -> "12.0.0";
                    case "getDatabaseMajorVersion" -> databaseMajorVersion;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        Connection connection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[] {Connection.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getMetaData")) {
                        return metadata;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        platform.initializeConnectionData(connection);
        platform.setShouldBindAllParameters(false);
        platform.setShouldBindLiterals(false);
        return new DatabaseSessionImpl(new DatabaseLogin(platform));
    }

    private DatabaseField fieldWithType(Class<?> type) {
        DatabaseField field = new DatabaseField("OFFSET_DATE_TIME");
        field.setType(type);
        return field;
    }
}
