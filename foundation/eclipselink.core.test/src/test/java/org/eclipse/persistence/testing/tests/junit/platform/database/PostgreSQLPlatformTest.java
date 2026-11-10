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
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.PostgreSQL10Platform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Test;

public class PostgreSQLPlatformTest {

    @Test
    public void testBooleanLiteralsAreTypedAndOtherRepresentationsArePreserved() {
        Object[] values = {Boolean.TRUE, Boolean.FALSE, null, 7, 3, "Y", "N"};
        String[] expected = {"TRUE", "FALSE", "NULL", "7", "3", "'Y'", "'N'"};
        for (PostgreSQLPlatform platform : platforms()) {
            for (int index = 0; index < values.length; index++) {
                StringWriter writer = new StringWriter();
                platform.appendParameter(new SQLCall(), writer, values[index]);
                Assert.assertEquals(expected[index], writer.toString());
            }
        }
    }

    @Test
    public void testBooleanParametersRetainJdbcBooleanBinding() throws Exception {
        for (PostgreSQLPlatform platform : platforms()) {
            List<String> calls = new ArrayList<>();
            PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[] {PreparedStatement.class},
                    (proxy, method, args) -> {
                        calls.add(method.getName() + ":" + args[0] + ":" + args[1]);
                        return null;
                    });
            platform.setParameterValueInDatabaseCall(true, statement, 1, null);
            platform.setParameterValueInDatabaseCall(false, statement, 2, null);
            Assert.assertEquals(List.of("setBoolean:1:true", "setBoolean:2:false"), calls);
        }
    }

    @Test
    public void testLocateFromPreservesNullsAndOriginalOperands() {
        for (PostgreSQLPlatform platform : platforms()) {
            DatabaseSessionImpl session = session(platform);
            SQLSelectStatement statement = statement(session);
            Expression source = statement.getBuilder().getField("NAME");
            FunctionExpression locate = (FunctionExpression) source.locate("B", 2);
            List<Expression> children = new ArrayList<>(locate.getChildren());
            statement.addField(locate);
            for (int attempt = 0; attempt < 2; attempt++) {
                Assert.assertEquals("SELECT CASE STRPOS(SUBSTRING(NAME FROM 2), 'B') WHEN 0 THEN 0 "
                        + "ELSE STRPOS(SUBSTRING(NAME FROM 2), 'B') - 1 + 2 END FROM ITEM",
                        statement.buildCall(session, null).getSQLString());
                Assert.assertSame(source, locate.getBaseExpression());
                Assert.assertEquals(children.size(), locate.getChildren().size());
                for (int index = 0; index < children.size(); index++) {
                    Assert.assertSame(children.get(index), locate.getChildren().get(index));
                }
            }
            Assert.assertTrue(platform.getOperator(ExpressionOperator.Locate2).isBindingSupported());
        }
    }

    @Test
    public void testLocateFromEscapesLiteralOperands() {
        for (PostgreSQLPlatform platform : platforms()) {
            DatabaseSessionImpl session = session(platform);
            SQLSelectStatement statement = statement(session);
            statement.addField(statement.getBuilder().value("a'b").locate("'", 2));
            Assert.assertEquals("SELECT CASE STRPOS(SUBSTRING('a''b' FROM 2), '''') WHEN 0 THEN 0 "
                    + "ELSE STRPOS(SUBSTRING('a''b' FROM 2), '''') - 1 + 2 END FROM ITEM",
                    statement.buildCall(session, null).getSQLString());
        }
    }

    @Test
    public void testLocateFromPreservesRepeatedBindingsAndNulls() {
        for (PostgreSQLPlatform platform : platforms()) {
            for (boolean partial : List.of(false, true)) {
                DatabaseSessionImpl session = session(platform);
                platform.setShouldBindAllParameters(true);
                platform.setShouldBindPartialParameters(partial);
                SQLSelectStatement statement = statement(session);
                Expression source = statement.getBuilder().getParameter("source", String.class);
                Expression needle = statement.getBuilder().getParameter("needle", String.class);
                Expression start = statement.getBuilder().getParameter("start", Integer.class);
                statement.addField(source.locate(needle, start));
                for (int attempt = 0; attempt < 2; attempt++) {
                    DatabaseCall call = statement.buildCall(session, null);
                    Assert.assertEquals("SELECT CASE STRPOS(SUBSTRING(? FROM ?), ?) WHEN 0 THEN 0 "
                            + "ELSE STRPOS(SUBSTRING(? FROM ?), ?) - 1 + ? END FROM ITEM", call.getSQLString());
                    List<String> order = List.of("source", "start", "needle", "source", "start", "needle", "start");
                    Assert.assertEquals(order, call.getParameters().stream()
                            .map(parameter -> ((ParameterExpression) parameter).getField().getName()).toList());
                    for (int index = 0; index < order.size(); index++) {
                        Assert.assertSame(order.get(index).equals("source") ? source
                                : order.get(index).equals("needle") ? needle : start, call.getParameters().get(index));
                    }
                    call.setQuery(new ReportQuery());
                    call.prepare(session);
                    for (int nullMask : new int[]{0, 1, 2, 4, 7, 0}) {
                        DatabaseRecord values = new DatabaseRecord();
                        values.put("source", (nullMask & 1) == 0 ? "ABCABC" : null);
                        values.put("needle", (nullMask & 2) == 0 ? "B" : null);
                        values.put("start", (nullMask & 4) == 0 ? 3 : null);
                        DatabaseCall execution = (DatabaseCall) call.clone();
                        execution.translate(values, null, session);
                        Assert.assertEquals(order.size(), execution.getParameters().size());
                        for (int index = 0; index < order.size(); index++) {
                            String name = order.get(index);
                            Object expected = values.get(name);
                            if (expected == null) {
                                Assert.assertEquals(name, ((DatabaseField) execution.getParameters().get(index)).getName());
                            } else {
                                Assert.assertEquals(expected, execution.getParameters().get(index));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testLocateWithoutStartRetainsNativeMapping() {
        for (PostgreSQLPlatform platform : platforms()) {
            DatabaseSessionImpl session = session(platform);
            platform.setShouldBindAllParameters(true);
            SQLSelectStatement statement = statement(session);
            Expression source = statement.getBuilder().getParameter("source", String.class);
            Expression needle = statement.getBuilder().getParameter("needle", String.class);
            statement.addField(source.locate(needle));
            for (int attempt = 0; attempt < 2; attempt++) {
                DatabaseCall call = statement.buildCall(session, null);
                Assert.assertEquals("SELECT STRPOS(?, ?) FROM ITEM", call.getSQLString());
                Assert.assertEquals(List.of(source, needle), call.getParameters());
            }
            Assert.assertTrue(platform.getOperator(ExpressionOperator.Locate).isBindingSupported());
        }
    }

    private List<PostgreSQLPlatform> platforms() {
        return List.of(new PostgreSQLPlatform(), new PostgreSQL10Platform());
    }

    private DatabaseSessionImpl session(PostgreSQLPlatform platform) {
        platform.setShouldBindAllParameters(false);
        platform.setShouldBindLiterals(false);
        return new DatabaseSessionImpl(new DatabaseLogin(platform));
    }

    private SQLSelectStatement statement(DatabaseSessionImpl session) {
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addTable(new DatabaseTable("ITEM"));
        statement.computeTablesFromTables();
        return statement;
    }
}
