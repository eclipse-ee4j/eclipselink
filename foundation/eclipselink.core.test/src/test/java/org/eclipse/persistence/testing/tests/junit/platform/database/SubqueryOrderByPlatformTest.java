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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.DB2MainframePlatform;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DB2ZPlatform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.JavaDBPlatform;
import org.eclipse.persistence.platform.database.MariaDBPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.platform.database.PostgreSQL10Platform;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Test;

/** SQL-only sort projections must not change the public report selection. */
public class SubqueryOrderByPlatformTest {
    @Test
    public void testCapabilityPreservesUnmeasuredRelatives() {
        Assert.assertFalse(new DB2Platform().supportsSubSelectInOrderBy(false));
        Assert.assertFalse(new DB2Platform().supportsSubSelectInOrderBy(true));
        for (DatabasePlatform platform : distinctOnlyPlatforms()) {
            Assert.assertTrue(platform.supportsSubSelectInOrderBy(false));
            Assert.assertFalse(platform.supportsSubSelectInOrderBy(true));
        }
        Assert.assertTrue(new MariaDBPlatform().supportsSubSelectInOrderBy(true));
        Assert.assertTrue(new DB2MainframePlatform().supportsSubSelectInOrderBy(true));
        Assert.assertTrue(new DB2ZPlatform().supportsSubSelectInOrderBy(true));
        for (DatabasePlatform platform : unchangedPlatforms()) {
            Assert.assertTrue(platform.getClass().getSimpleName(), platform.supportsSubSelectInOrderBy(false));
        }
    }

    @Test
    public void testSelectedCountUsesItsExistingSqlColumn() {
        Fixture fixture = fixture(new DB2Platform());
        fixture.query.addAttribute("id", fixture.owner.get("id"));
        fixture.query.addAttribute("size", fixture.count, Integer.class);
        fixture.query.addOrdering(fixture.count.descending());
        DatabaseCall call = prepare(fixture);
        Assert.assertTrue(call.getSQLString(), call.getSQLString().endsWith("ORDER BY 2 DESC"));
        Assert.assertEquals(1, occurrences(call.getSQLString(), "COUNT("));
        Assert.assertEquals(2, call.getFields().size());
        Assert.assertEquals(2, fixture.query.getItems().size());
        Assert.assertTrue(call.getParameters().isEmpty());
    }

    @Test
    public void testUnselectedCountAddsOnlySqlFieldAndPreservesNullOrdering() {
        Fixture fixture = fixture(new DB2Platform());
        fixture.query.addAttribute("id", fixture.owner.get("id"));
        fixture.query.addOrdering(fixture.count.ascending().nullsFirst());
        DatabaseCall call = prepare(fixture);
        Assert.assertTrue(call.getSQLString(), call.getSQLString().endsWith("ORDER BY 2 ASC NULLS FIRST"));
        Assert.assertEquals(1, occurrences(call.getSQLString(), "COUNT("));
        Assert.assertEquals(2, call.getFields().size());
        Assert.assertEquals(1, fixture.query.getItems().size());
        Assert.assertEquals("id", fixture.query.getItems().getFirst().getName());
        Assert.assertTrue(call.getParameters().isEmpty());
    }

    @Test
    public void testEntitySelectionUsesPhysicalColumnPosition() {
        Fixture fixture = fixture(new DB2Platform());
        fixture.query.addItem("owner", fixture.owner);
        fixture.query.addAttribute("size", fixture.count, Integer.class);
        fixture.query.addOrdering(fixture.count.descending().nullsLast());
        DatabaseCall call = prepare(fixture);
        Assert.assertEquals(3, fixture.session.getDescriptor(Owner.class).getFields().size());
        Assert.assertEquals(4, call.getFields().size());
        Assert.assertEquals(2, fixture.query.getItems().size());
        Assert.assertTrue(call.getSQLString(), call.getSQLString().endsWith("ORDER BY 4 DESC NULLS LAST"));
    }

    @Test
    public void testCompleteArithmeticKeyBindsOnceAndRebindsWithoutOrdinalParameter() throws SQLException {
        Fixture fixture = fixture(new DB2Platform());
        Expression parameter = fixture.owner.getParameter("delta", Integer.class);
        Expression key = ExpressionMath.add(fixture.count, parameter);
        Expression ordering = key.ascending();
        fixture.query.addArgument("delta", Integer.class);
        fixture.query.addAttribute("id", fixture.owner.get("id"));
        fixture.query.addOrdering(ordering);
        DatabaseCall call = prepare(fixture);
        Assert.assertTrue(call.getSQLString(), call.getSQLString().endsWith("ORDER BY 2 ASC"));
        Assert.assertEquals(1, occurrences(call.getSQLString(), "COUNT("));
        Assert.assertEquals(1, call.getParameters().size());
        Assert.assertEquals(1, fixture.query.getItems().size());
        Assert.assertSame(key, ((FunctionExpression) ordering).getBaseExpression());
        for (Integer value : Arrays.asList(null, 2, 7, null)) {
            DatabaseRecord arguments = new DatabaseRecord();
            arguments.put("delta", value);
            DatabaseCall execution = (DatabaseCall) call.clone();
            execution.translate(arguments, null, fixture.session);
            Assert.assertEquals(call.getSQLString(), execution.getSQLString());
            Assert.assertEquals(1, execution.getParameters().size());
            Object actual = execution.getParameters().getFirst();
            if (value == null) {
                Assert.assertEquals("delta", ((DatabaseField) actual).getName());
            } else {
                Assert.assertEquals(value, actual);
            }
        }
        Assert.assertSame(fixture.owner, parameter.getBuilder());
    }

    @Test
    public void testOrdinaryOrderingDoesNotAddSqlProjection() {
        Fixture fixture = fixture(new DB2Platform());
        fixture.query.addAttribute("id", fixture.owner.get("id"));
        fixture.query.addOrdering(fixture.owner.get("name").toUpperCase().ascending());
        DatabaseCall call = prepare(fixture);
        Assert.assertTrue(call.getSQLString(), orderClause(call).contains("UCASE("));
        Assert.assertFalse(call.getSQLString(), orderClause(call).contains("COUNT("));
        Assert.assertEquals(1, call.getFields().size());
        Assert.assertEquals(1, fixture.query.getItems().size());
    }

    @Test
    public void testOtherPlatformsKeepSubqueryOrdering() {
        for (DatabasePlatform platform : unchangedPlatforms()) {
            Fixture fixture = fixture(platform);
            fixture.query.addAttribute("id", fixture.owner.get("id"));
            fixture.query.addOrdering(fixture.count.ascending());
            DatabaseCall call = prepare(fixture);
            Assert.assertTrue(platform.getClass().getSimpleName() + ": " + call.getSQLString(),
                    orderClause(call).contains("SELECT COUNT("));
            Assert.assertEquals(1, call.getFields().size());
            Assert.assertEquals(1, fixture.query.getItems().size());
        }
        for (DatabasePlatform platform : distinctOnlyPlatforms()) {
            Fixture fixture = fixture(platform);
            fixture.query.addAttribute("size", fixture.count, Integer.class);
            fixture.query.addOrdering(fixture.count.ascending());
            String ordinary = prepare(fixture).getSQLString();
            Assert.assertTrue(ordinary, orderClause(fixture.query.getCall()).contains("SELECT COUNT("));
            fixture.query.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
            fixture.query.setIsPrepared(false);
            DatabaseCall distinct = prepare(fixture);
            Assert.assertTrue(distinct.getSQLString(), distinct.getSQLString().endsWith("ORDER BY 1 ASC"));
            Assert.assertEquals(1, fixture.query.getItems().size());
            fixture.query.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
            fixture.query.setIsPrepared(false);
            Assert.assertEquals(ordinary, prepare(fixture).getSQLString());
        }
    }

    @Test
    public void testCloneRepeatedPreparationAndPaginationRetainPositions() {
        DB2Platform platform = new DB2Platform();
        platform.setShouldUseRownumFiltering(true);
        Fixture fixture = fixture(platform);
        fixture.query.addItem("owner", fixture.owner);
        fixture.query.addOrdering(fixture.count.descending());
        DatabaseCall first = prepare(fixture);
        String expected = first.getSQLString();
        Assert.assertTrue(expected, expected.endsWith("ORDER BY 4 DESC"));
        fixture.query.setIsPrepared(false);
        DatabaseCall second = prepare(fixture);
        Assert.assertEquals(expected, second.getSQLString());
        Assert.assertEquals(4, second.getFields().size());
        Assert.assertEquals(1, fixture.query.getItems().size());
        ReportQuery copy = (ReportQuery) fixture.query.clone();
        copy.setIsPrepared(false);
        copy.prepareCall(fixture.session, new DatabaseRecord());
        Assert.assertEquals(expected, copy.getCall().getSQLString());
        Assert.assertEquals(1, copy.getItems().size());
        copy.setFirstResult(1);
        copy.setMaxRows(3);
        copy.setIsPrepared(false);
        copy.prepareCall(fixture.session, new DatabaseRecord());
        String paged = copy.getCall().getSQLString();
        Assert.assertTrue(paged, paged.contains("ROWNUMBER() OVER()"));
        Assert.assertTrue(paged, paged.contains("ORDER BY 4 DESC"));
        Assert.assertEquals(4, copy.getCall().getFields().size());
        Assert.assertEquals(1, copy.getItems().size());
        Assert.assertEquals(expected, fixture.query.getCall().getSQLString());
    }

    @Test
    public void testSqlServerDistinctNullRankingPaginationAndReuse() throws SQLException {
        for (boolean selected : List.of(false, true)) {
            for (boolean nullsFirst : List.of(false, true)) {
                CapturingSQLServerPlatform platform = new CapturingSQLServerPlatform();
                platform.includeOrderedValue = selected;
                initializeSqlServer11(platform);
                platform.setShouldUseRownumFiltering(true);
                Fixture fixture = fixture(platform);
                Expression parameter = fixture.owner.getParameter("emptySize", Integer.class);
                FunctionExpression key = (FunctionExpression) fixture.count.nullIf(parameter);
                key.setResultType(Integer.class);
                Expression sort = nullsFirst ? key.descending() : key.ascending();
                Expression ordering = nullsFirst ? sort.nullsFirst() : sort.nullsLast();
                fixture.query.addArgument("emptySize", Integer.class);
                fixture.query.addItem("owner", fixture.owner);
                if (selected) {
                    fixture.query.addAttribute("size", key, Integer.class);
                }
                fixture.query.addOrdering(ordering);
                fixture.query.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
                fixture.query.setFirstResult(1);
                fixture.query.setMaxRows(4);
                List<Expression> originalOrder = fixture.query.getOrderByExpressions();
                DatabaseCall call = prepare(fixture);
                String sql = call.getSQLString();
                // Existing DISTINCT normalization appends a selected function key again;
                // preserve those original physical fields independently of public ReportItems.
                String ordinalOrder = " ORDER BY " + (selected ? "6, 4" : "4, 5")
                        + (nullsFirst ? " DESC" : " ASC");
                Assert.assertTrue(sql, sql.endsWith(ordinalOrder + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"));
                Assert.assertTrue(sql, sql.contains(nullsFirst ? "THEN 0 ELSE 1 END" : "THEN 1 ELSE 0 END"));
                Assert.assertEquals(selected ? 3 : 2, occurrences(sql, "COUNT("));
                Assert.assertEquals(1, occurrences(sql, " OFFSET "));
                Assert.assertEquals(selected ? 5 : 3, call.getFields().size());
                Assert.assertEquals(selected ? 2 : 1, fixture.query.getItems().size());
                Assert.assertEquals("owner", fixture.query.getItems().getFirst().getName());
                Assert.assertEquals(List.of("emptySize"), fixture.query.getArguments());
                Assert.assertSame(originalOrder, fixture.query.getOrderByExpressions());
                Assert.assertEquals(List.of(ordering), originalOrder);
                Assert.assertSame(sort, ((FunctionExpression) ordering).getBaseExpression());
                Assert.assertSame(key, ((FunctionExpression) sort).getBaseExpression());
                Assert.assertSame(fixture.owner, parameter.getBuilder());
                for (Integer value : Arrays.asList(null, 0, 2, null)) {
                    assertSqlServerBindings(fixture, call, value, 1, 3, selected ? 3 : 2);
                }

                fixture.query.setIsPrepared(false);
                Assert.assertEquals(sql, prepare(fixture).getSQLString());
                Assert.assertEquals(selected ? 5 : 3, fixture.query.getCall().getFields().size());
                ReportQuery copy = (ReportQuery) fixture.query.clone();
                copy.setIsPrepared(false);
                copy.prepareCall(fixture.session, new DatabaseRecord());
                Assert.assertEquals(sql, copy.getCall().getSQLString());
                Assert.assertEquals(selected ? 5 : 3, copy.getCall().getFields().size());
                Assert.assertEquals(selected ? 2 : 1, copy.getItems().size());

                fixture.query.setMaxRows(0);
                fixture.query.setFirstResult(2);
                fixture.query.setIsPrepared(false);
                DatabaseCall offsetOnly = prepare(fixture);
                Assert.assertTrue(offsetOnly.getSQLString(), offsetOnly.getSQLString().endsWith(ordinalOrder + " OFFSET ? ROWS"));
                assertSqlServerBindings(fixture, offsetOnly, 0, 2, 0, selected ? 3 : 2);
                fixture.query.setFirstResult(0);
                fixture.query.setIsPrepared(false);
                DatabaseCall unlimited = prepare(fixture);
                Assert.assertTrue(unlimited.getSQLString(), unlimited.getSQLString().endsWith(ordinalOrder));
                Assert.assertFalse(unlimited.shouldIgnoreFirstRowSetting());
                Assert.assertFalse(unlimited.shouldIgnoreMaxResultsSetting());
                Assert.assertEquals(selected ? 5 : 3, unlimited.getFields().size());
                Assert.assertSame(originalOrder, fixture.query.getOrderByExpressions());
                Assert.assertEquals(List.of(ordering), originalOrder);
                Assert.assertEquals(sql, copy.getCall().getSQLString());
            }
        }
    }

    @Test
    public void testMySqlDistinctNullRankAndMariaDbPreservation() {
        for (DatabasePlatform platform : List.of(new MySQLPlatform(), new MariaDBPlatform())) {
            platform.setShouldUseRownumFiltering(true);
            Fixture fixture = fixture(platform);
            Expression parameter = fixture.owner.getParameter("emptySize", Integer.class);
            FunctionExpression key = (FunctionExpression) fixture.count.nullIf(parameter);
            key.setResultType(Integer.class);
            Expression ordering = key.descending().nullsLast();
            fixture.query.addArgument("emptySize", Integer.class);
            fixture.query.addAttribute("size", key, Integer.class);
            fixture.query.addOrdering(ordering);
            fixture.query.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
            fixture.query.setFirstResult(1);
            fixture.query.setMaxRows(3);
            DatabaseCall call = prepare(fixture);
            String sql = call.getSQLString();
            if (platform instanceof MariaDBPlatform) {
                Assert.assertTrue(sql, orderClause(call).contains("CASE WHEN NULLIF("));
                Assert.assertTrue(sql, orderClause(call).contains("SELECT COUNT("));
            } else {
                Assert.assertTrue(sql, orderClause(call).startsWith(" ORDER BY 3, 1 DESC LIMIT "));
                Assert.assertEquals(3, occurrences(sql, "COUNT("));
            }
            Assert.assertEquals(1, occurrences(sql, " LIMIT "));
            Assert.assertEquals(2, call.getFields().size());
            Assert.assertEquals(1, fixture.query.getItems().size());
            Assert.assertEquals(List.of(ordering), fixture.query.getOrderByExpressions());
            Assert.assertSame(fixture.owner, parameter.getBuilder());
            fixture.query.setIsPrepared(false);
            Assert.assertEquals(sql, prepare(fixture).getSQLString());
            Assert.assertEquals(2, fixture.query.getCall().getFields().size());
        }
    }

    @Test
    public void testDerbyDistinctNullRankingPreservesOrdinaryOrderingAndReuse() {
        for (DatabasePlatform platform : List.of(new DerbyPlatform(), new JavaDBPlatform())) {
            for (boolean nullsFirst : List.of(false, true)) {
                Fixture fixture = fixture(platform);
                FunctionExpression key = (FunctionExpression) fixture.count.nullIf(fixture.owner.getParameter("emptySize", Integer.class));
                key.setResultType(Integer.class);
                Expression sort = nullsFirst ? key.ascending() : key.descending();
                Expression ordering = nullsFirst ? sort.nullsFirst() : sort.nullsLast();
                fixture.query.addArgument("emptySize", Integer.class);
                fixture.query.addItem("owner", fixture.owner);
                fixture.query.addAttribute("size", key, Integer.class);
                fixture.query.addOrdering(ordering);
                String ordinary = prepare(fixture).getSQLString();
                Assert.assertTrue(ordinary, ordinary.endsWith(nullsFirst ? " ASC NULLS FIRST" : " DESC NULLS LAST"));
                fixture.query.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
                fixture.query.setIsPrepared(false);
                DatabaseCall call = prepare(fixture);
                String sql = call.getSQLString();
                Assert.assertTrue(sql, sql.endsWith(nullsFirst ? "ORDER BY 6, 4 ASC" : "ORDER BY 6, 4 DESC"));
                Assert.assertFalse(sql, sql.contains("NULLS FIRST") || sql.contains("NULLS LAST"));
                Assert.assertTrue(sql, sql.contains(nullsFirst ? "THEN 0 ELSE 1 END" : "THEN 1 ELSE 0 END"));
                Assert.assertEquals(5, call.getFields().size());
                Assert.assertEquals(2, fixture.query.getItems().size());
                Assert.assertEquals(List.of(ordering), fixture.query.getOrderByExpressions());
                fixture.query.setIsPrepared(false);
                Assert.assertEquals(sql, prepare(fixture).getSQLString());
                fixture.query.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
                fixture.query.setIsPrepared(false);
                Assert.assertEquals(ordinary, prepare(fixture).getSQLString());
            }
        }
    }

    private static void assertSqlServerBindings(Fixture fixture, DatabaseCall prepared, Integer value,
                                                int offset, int limit, int valueOccurrences) {
        ReportQuery execution = (ReportQuery) fixture.query.clone();
        execution.setSession(fixture.session);
        DatabaseRecord arguments = new DatabaseRecord();
        arguments.put("emptySize", value);
        execution.setTranslationRow(arguments);
        execution.prepareForExecution();
        DatabaseCall call = (DatabaseCall) prepared.clone();
        call.translate(execution.getTranslationRow(), null, fixture.session);
        Assert.assertEquals(prepared.getSQLString(), call.getSQLString());
        Assert.assertEquals(valueOccurrences + (limit == 0 ? 1 : 2), call.getParameters().size());
        for (int index = 0; index < valueOccurrences; index++) {
            Object actual = call.getParameters().get(index);
            if (value == null) {
                Assert.assertEquals("emptySize", ((DatabaseField) actual).getName());
            } else {
                Assert.assertEquals(value, actual);
            }
        }
        Assert.assertEquals(offset, call.getParameters().get(valueOccurrences));
        if (limit > 0) {
            Assert.assertEquals(limit, call.getParameters().get(valueOccurrences + 1));
        }
    }

    private static void initializeSqlServer11(SQLServerPlatform platform) throws SQLException {
        DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(DatabaseMetaData.class.getClassLoader(),
                new Class<?>[] {DatabaseMetaData.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getDriverName" -> "Microsoft JDBC Driver";
                    case "getDriverVersion" -> "12.0.0";
                    case "getDatabaseMajorVersion" -> 11;
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
    }

    private static class CapturingSQLServerPlatform extends SQLServerPlatform {
        private boolean includeOrderedValue;

        @Override
        public boolean shouldSelectDistinctIncludeOrderBy() {
            // The selected branch retains the default; the other proves late scalar projection.
            return includeOrderedValue && super.shouldSelectDistinctIncludeOrderBy();
        }

        @Override
        public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
            List<?> fields = statement.getFields();
            List<?> originalFields = new ArrayList<>(fields);
            List<Expression> ordering = statement.getOrderByExpressions();
            List<Expression> originalOrdering = new ArrayList<>(ordering);
            try {
                super.printSQLSelectStatement(call, printer, statement);
            } finally {
                Assert.assertSame(fields, statement.getFields());
                Assert.assertSame(ordering, statement.getOrderByExpressions());
                assertSameElements(originalFields, fields);
                assertSameElements(originalOrdering, ordering);
            }
        }

        private static void assertSameElements(List<?> expected, List<?> actual) {
            Assert.assertEquals(expected.size(), actual.size());
            for (int index = 0; index < expected.size(); index++) {
                Assert.assertSame(expected.get(index), actual.get(index));
            }
        }
    }

    private static List<DatabasePlatform> unchangedPlatforms() {
        return List.of(new DatabasePlatform(), new H2Platform(), new PostgreSQLPlatform(),
                new DerbyPlatform(), new JavaDBPlatform(), new DB2MainframePlatform(), new DB2ZPlatform(),
                new OraclePlatform(), new SQLServerPlatform(), new MySQLPlatform(), new MariaDBPlatform());
    }

    private static List<DatabasePlatform> distinctOnlyPlatforms() {
        return List.of(new DerbyPlatform(), new JavaDBPlatform(), new OraclePlatform(),
                new SQLServerPlatform(), new MySQLPlatform(), new PostgreSQLPlatform(), new PostgreSQL10Platform());
    }

    private static String orderClause(DatabaseCall call) {
        String sql = call.getSQLString();
        return sql.substring(sql.lastIndexOf(" ORDER BY "));
    }

    private static int occurrences(String value, String part) {
        return (value.length() - value.replace(part, "").length()) / part.length();
    }

    private static DatabaseCall prepare(Fixture fixture) {
        fixture.query.prepareCall(fixture.session, new DatabaseRecord());
        return fixture.query.getCall();
    }

    private static Fixture fixture(DatabasePlatform platform) {
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindLiterals(false);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        RelationalDescriptor owner = new RelationalDescriptor();
        owner.setJavaClass(Owner.class);
        owner.setAlias("Owner");
        owner.addTableName("SORT_OWNER");
        owner.addPrimaryKeyFieldName("SORT_OWNER.ID");
        owner.addDirectMapping("id", "SORT_OWNER.ID");
        owner.addDirectMapping("name", "SORT_OWNER.NAME");
        owner.addDirectMapping("code", "SORT_OWNER.CODE");
        session.addDescriptor(owner);
        RelationalDescriptor member = new RelationalDescriptor();
        member.setJavaClass(Member.class);
        member.setAlias("Member");
        member.addTableName("SORT_MEMBER");
        member.addPrimaryKeyFieldName("SORT_MEMBER.ID");
        member.addDirectMapping("id", "SORT_MEMBER.ID");
        member.addDirectMapping("ownerId", "SORT_MEMBER.OWNER_ID");
        session.addDescriptor(member);
        session.initializeDescriptors();
        ExpressionBuilder outer = new ExpressionBuilder(Owner.class);
        ExpressionBuilder inner = new ExpressionBuilder(Member.class);
        ReportQuery subquery = new ReportQuery(Member.class, inner);
        ConstantExpression one = new ConstantExpression(1, inner);
        one.setCanBind(false);
        subquery.addCount("rowCount", one, Integer.class);
        subquery.setSelectionCriteria(inner.get("ownerId").equal(outer.get("id")));
        FunctionExpression count = (FunctionExpression) outer.subQuery(subquery).getFunction("");
        count.setResultType(Integer.class);
        return new Fixture(session, outer, new ReportQuery(Owner.class, outer), count);
    }

    private record Fixture(DatabaseSessionImpl session, ExpressionBuilder owner, ReportQuery query, Expression count) {
    }

    public static class Owner {
        public Integer id;
        public String name;
        public String code;
    }

    public static class Member {
        public Integer id;
        public Integer ownerId;
    }
}
