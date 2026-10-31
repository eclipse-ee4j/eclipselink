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
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Test;

public class MySQLPlatformTest {

    @Test
    public void testLengthCountsCharacters() {
        DatabaseSessionImpl session = session(new MySQLPlatform());
        SQLSelectStatement statement = statement(session);
        statement.addField(statement.getBuilder().getField("NAME").length());
        statement.addField(statement.getBuilder().value("\u00e9\uD83D\uDE00 ").length());
        assertRenderedTwice(session, statement,
                "SELECT ID, CHAR_LENGTH(NAME), CHAR_LENGTH('\u00e9\uD83D\uDE00 ') FROM ITEM", false);
    }

    @Test
    public void testDefaultNullsFirst() {
        assertNullOrdering("", true);
    }

    @Test
    public void testDefaultNullsLast() {
        assertNullOrdering("", false);
    }

    @Test
    public void testAscendingNullsFirst() {
        assertNullOrdering(" ASC", true);
    }

    @Test
    public void testAscendingNullsLast() {
        assertNullOrdering(" ASC", false);
    }

    @Test
    public void testDescendingNullsFirst() {
        assertNullOrdering(" DESC", true);
    }

    @Test
    public void testDescendingNullsLast() {
        assertNullOrdering(" DESC", false);
    }

    @Test
    public void testComposedAndMultipleOrderings() {
        DatabaseSessionImpl session = session(new MySQLPlatform());
        SQLSelectStatement statement = statement(session);
        ExpressionBuilder builder = statement.getBuilder();
        statement.getOrderByExpressions().add(builder.getField("NAME").toUpperCase().descending().nullsLast());
        statement.getOrderByExpressions().add(builder.getField("ID").ascending());
        statement.getOrderByExpressions().add(builder.getField("CODE").nullsFirst());
        assertRenderedTwice(session, statement,
                "SELECT ID FROM ITEM ORDER BY (CASE WHEN UPPER(NAME) IS NULL THEN 1 ELSE 0 END), UPPER(NAME) DESC, "
                        + "ID ASC, (CASE WHEN CODE IS NULL THEN 0 ELSE 1 END), CODE", false);
    }

    @Test
    public void testPaginationKeepsNullOrdering() {
        for (boolean useLimits : List.of(false, true)) {
            for (int first : List.of(0, 2)) {
                for (int max : List.of(0, 7)) {
                    DatabaseSessionImpl session = session(new MySQLPlatform());
                    session.getPlatform().setShouldUseRownumFiltering(useLimits);
                    SQLSelectStatement statement = statement(session);
                    statement.getOrderByExpressions().add(statement.getBuilder().getField("NAME").descending().nullsLast());
                    statement.setQuery(pagedQuery(first, max));
                    boolean paged = useLimits && max > 0;
                    String sql = "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN 1 ELSE 0 END), NAME DESC"
                            + (paged ? " LIMIT ? OFFSET ?" : "");
                    assertRenderedTwice(session, statement, sql, paged);
                }
            }
        }
    }

    @Test
    public void testPlainOrderingAndEmptyOrderingRemainUnchanged() {
        DatabaseSessionImpl session = session(new MySQLPlatform());
        SQLSelectStatement statement = statement(session);
        statement.setQuery(pagedQuery(2, 7));
        assertRenderedTwice(session, statement, "SELECT ID FROM ITEM LIMIT ? OFFSET ?", true);
        statement.getOrderByExpressions().add(statement.getBuilder().getField("ID").descending());
        assertRenderedTwice(session, statement, "SELECT ID FROM ITEM ORDER BY ID DESC LIMIT ? OFFSET ?", true);
    }

    @Test
    public void testDistinctProjectionThroughNormalQueryPreparation() {
        for (String selection : List.of("i.name", "UPPER(i.name)", "i.name, i.code", "i")) {
            for (String direction : List.of("", " ASC", " DESC")) {
                for (String precedence : List.of(" FIRST", " LAST")) {
                    for (boolean paged : List.of(false, true)) {
                        CapturingMySQLPlatform platform = new CapturingMySQLPlatform();
                        DatabaseSessionImpl session = mappedSession(platform);
                        String sort = selection.equals("UPPER(i.name)") ? selection : "i.name";
                        DatabaseQuery query = new HermesParser().buildQuery("SELECT DISTINCT " + selection
                                + " FROM Item i ORDER BY " + sort + direction + " NULLS" + precedence, session);
                        if (paged) {
                            ((ReadAllQuery) query).setFirstResult(1);
                            ((ReadAllQuery) query).setMaxRows(4);
                        }
                        query.prepareCall(session, new DatabaseRecord());
                        int columns = selection.equals("i") ? 3 : selection.contains(",") ? 2 : 1;
                        assertDistinctProjection(query.getCall(), 1, platform.originalFieldCount);
                        String sql = query.getCall().getSQLString();
                        Assert.assertEquals(paged, sql.contains(" LIMIT "));
                        for (int attempt = 0; attempt < 2; attempt++) {
                            DatabaseCall repeated = platform.statement.buildCall(session, query);
                            assertDistinctProjection(repeated, 1, platform.originalFieldCount);
                            Assert.assertEquals(withoutGeneratedAliases(sql), withoutGeneratedAliases(repeated.getSQLString()));
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

    @Test
    public void testDistinctMultipleOrderingsKeepParametersAndVisibleFields() {
        CapturingMySQLPlatform platform = new CapturingMySQLPlatform();
        DatabaseSessionImpl session = mappedSession(platform);
        platform.setShouldBindAllParameters(true);
        DatabaseQuery query = new HermesParser().buildQuery("SELECT DISTINCT i.name, i.code FROM Item i "
                + "WHERE i.id > :minimum AND i.code <> :excluded "
                + "ORDER BY i.name DESC NULLS LAST, i.code ASC NULLS FIRST", session);
        query.prepareCall(session, new DatabaseRecord());
        DatabaseCall prepared = query.getCall();
        assertDistinctProjection(prepared, 2, 2);
        for (int attempt = 0; attempt < 2; attempt++) {
            DatabaseCall repeated = platform.statement.buildCall(session, query);
            assertDistinctProjection(repeated, 2, 2);
            Assert.assertEquals(prepared.getSQLString(), repeated.getSQLString());
            Assert.assertEquals(2, repeated.getParameters().size());
            Assert.assertEquals("minimum", ((ParameterExpression) repeated.getParameters().get(0)).getField().getName());
            Assert.assertEquals("excluded", ((ParameterExpression) repeated.getParameters().get(1)).getField().getName());
        }
        Assert.assertEquals(2, ((ReportQuery) query).getItems().size());
    }

    @Test
    public void testOrderingExpressionKeepsEveryParameterOccurrence() {
        CapturingMySQLPlatform platform = new CapturingMySQLPlatform();
        DatabaseSessionImpl session = mappedSession(platform);
        platform.setShouldBindAllParameters(true);
        DatabaseQuery query = new HermesParser().buildQuery("SELECT DISTINCT COALESCE(i.name, :fallback) FROM Item i "
                + "WHERE i.id > :minimum ORDER BY COALESCE(i.name, :fallback) DESC NULLS LAST", session);
        query.prepareCall(session, new DatabaseRecord());
        DatabaseCall prepared = query.getCall();
        for (int attempt = 0; attempt < 2; attempt++) {
            DatabaseCall repeated = platform.statement.buildCall(session, query);
            assertDistinctProjection(repeated, 1, platform.originalFieldCount);
            Assert.assertEquals(withoutGeneratedAliases(prepared.getSQLString()), withoutGeneratedAliases(repeated.getSQLString()));
            List<String> parameters = repeated.getParameters().stream()
                    .map(parameter -> ((ParameterExpression) parameter).getField().getName()).toList();
            // Query normalization already includes the composed ordering value
            // in the DISTINCT projection; its existing binding is retained too.
            Assert.assertEquals(List.of("fallback", "fallback", "fallback", "minimum", "fallback", "fallback"), parameters);
            Assert.assertEquals(parameters.size(), repeated.getSQLString().chars().filter(value -> value == '?').count());
        }
        Assert.assertEquals(1, ((ReportQuery) query).getItems().size());
    }

    @Test
    public void testStatementRestoredAfterRenderingFailure() {
        for (boolean distinct : List.of(false, true)) {
            CapturingMySQLPlatform platform = new CapturingMySQLPlatform();
            DatabaseSessionImpl session = session(platform);
            SQLSelectStatement statement = statement(session);
            if (distinct) statement.useDistinct();
            IllegalStateException failure = new IllegalStateException("Rendering failed");
            Expression field = new LiteralExpression("NAME", statement.getBuilder()) {
                @Override
                public void printSQL(ExpressionSQLPrinter printer) {
                    throw failure;
                }
            };
            Expression sort = field.descending();
            Expression ordering = sort.nullsLast();
            statement.getOrderByExpressions().add(ordering);
            statement.setQuery(pagedQuery(2, 7));
            for (int attempt = 0; attempt < 2; attempt++) {
                try {
                    statement.buildCall(session, statement.getQuery());
                    Assert.fail("Expected rendering to fail");
                } catch (IllegalStateException actual) {
                    Assert.assertSame(failure, actual);
                }
                Assert.assertSame(sort, ((FunctionExpression) ordering).getBaseExpression());
                Assert.assertSame(field, ((FunctionExpression) sort).getBaseExpression());
                Assert.assertEquals(ExpressionOperator.NullsLast, ordering.getOperator().getSelector());
                Assert.assertEquals(ExpressionOperator.Descending, sort.getOperator().getSelector());
            }
        }
    }

    private void assertNullOrdering(String direction, boolean first) {
        DatabaseSessionImpl session = session(new MySQLPlatform());
        SQLSelectStatement statement = statement(session);
        Expression field = statement.getBuilder().getField("NAME");
        Expression sort = switch (direction) {
            case " ASC" -> field.ascending();
            case " DESC" -> field.descending();
            default -> field;
        };
        Expression ordering = first ? sort.nullsFirst() : sort.nullsLast();
        statement.getOrderByExpressions().add(ordering);
        assertRenderedTwice(session, statement, "SELECT ID FROM ITEM ORDER BY (CASE WHEN NAME IS NULL THEN "
                + (first ? "0 ELSE 1" : "1 ELSE 0") + " END), NAME" + direction, false);
        Assert.assertSame(sort, ((FunctionExpression) ordering).getBaseExpression());
    }

    private void assertRenderedTwice(DatabaseSessionImpl session, SQLSelectStatement statement,
                                     String expected, boolean paged) {
        List<Expression> ordering = statement.getOrderByExpressions();
        List<Expression> original = new ArrayList<>(ordering);
        List<?> fields = statement.getFields();
        List<?> originalFields = new ArrayList<>(fields);
        for (int attempt = 0; attempt < 2; attempt++) {
            DatabaseCall call = statement.buildCall(session, statement.getQuery());
            Assert.assertEquals(expected, withoutGeneratedAliases(call.getSQLString()));
            Assert.assertEquals(paged, call.shouldIgnoreFirstRowSetting());
            Assert.assertEquals(paged, call.shouldIgnoreMaxResultsSetting());
            Assert.assertSame(ordering, statement.getOrderByExpressions());
            Assert.assertSame(fields, statement.getFields());
            assertElementsUnchanged(ordering, original);
            assertElementsUnchanged(fields, originalFields);
        }
    }

    private void assertDistinctProjection(DatabaseCall call, int helpers, int visible) {
        String sql = call.getSQLString();
        String projection = sql.substring(0, sql.indexOf(" FROM "));
        Assert.assertEquals(sql, helpers, projection.split("CASE WHEN", -1).length - 1);
        Assert.assertFalse(sql, sql.contains(" NULLS "));
        Assert.assertEquals(sql, visible, call.getFields().size());
        Assert.assertEquals(sql, visible, call.getFieldsArray().length);
    }

    private static String withoutGeneratedAliases(String sql) {
        // Paginated statements allocate a fresh projection alias on each buildCall.
        return sql.replaceAll(" AS a\\d+\\b", "");
    }

    private static void assertElementsUnchanged(List<?> actual, List<?> expected) {
        Assert.assertEquals(expected.size(), actual.size());
        for (int index = 0; index < expected.size(); index++) {
            Assert.assertSame(expected.get(index), actual.get(index));
        }
    }

    private static class CapturingMySQLPlatform extends MySQLPlatform {
        private SQLSelectStatement statement;
        private int originalFieldCount;

        @Override
        public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
            this.statement = statement;
            List<?> fields = statement.getFields();
            originalFieldCount = fields.size();
            List<?> originalFields = new ArrayList<>(fields);
            List<Expression> ordering = statement.getOrderByExpressions();
            List<Expression> originalOrdering = new ArrayList<>(ordering);
            try {
                super.printSQLSelectStatement(call, printer, statement);
            } finally {
                Assert.assertSame(fields, statement.getFields());
                Assert.assertSame(ordering, statement.getOrderByExpressions());
                assertElementsUnchanged(fields, originalFields);
                assertElementsUnchanged(ordering, originalOrdering);
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

    private DatabaseSessionImpl session(MySQLPlatform platform) {
        platform.setShouldBindAllParameters(false);
        platform.setShouldBindLiterals(false);
        return new DatabaseSessionImpl(new DatabaseLogin(platform));
    }

    private DatabaseSessionImpl mappedSession(MySQLPlatform platform) {
        DatabaseSessionImpl session = session(platform);
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

    public static class Item {
        public Integer id;
        public String name;
        public String code;

        public Item() {
        }
    }
}
