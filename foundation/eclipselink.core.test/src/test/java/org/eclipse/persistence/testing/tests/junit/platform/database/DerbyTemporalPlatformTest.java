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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.LiteralExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.JavaDBPlatform;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.Assert;
import org.junit.Test;

public class DerbyTemporalPlatformTest {
    @Test
    public void testScalarWrappedCaseReceiverRetainsBindingsAndSource() {
        for (DerbyPlatform platform : List.of(new DerbyPlatform(), new JavaDBPlatform())) {
            DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
            ExpressionBuilder builder = new ExpressionBuilder();
            builder.setSession(session);
            Expression selector = builder.getParameter("selector", Integer.class);
            Expression match = builder.getParameter("match", Integer.class);
            Expression result = builder.getParameter("result", String.class);
            Expression otherwise = builder.getParameter("otherwise", String.class);
            Expression fallback = builder.getParameter("fallback", String.class);
            FunctionExpression choice = (FunctionExpression) selector.caseStatement(Map.of(match, result), otherwise);
            List<Expression> originalChildren = List.copyOf(choice.getChildren());
            for (Expression wrapped : List.of(choice.cast("VARCHAR(32)"), scalarWrapper(choice))) {
                SQLCall call = new SQLCall();
                StringWriter sql = new StringWriter();
                ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, null, call, false, null);
                printer.setWriter(sql);
                platform.getOperator(ExpressionOperator.Coalesce).printCollection(List.of(wrapped, fallback), printer);
                Assert.assertTrue(sql.toString(), sql.toString().startsWith("COALESCE(CASE WHEN 1=0 THEN "));
                Assert.assertEquals(List.of(selector, match, result, otherwise, selector, match, result, otherwise, fallback),
                        call.getParameters());
                for (int i = 0; i < originalChildren.size(); i++) {
                    Assert.assertSame(originalChildren.get(i), choice.getChildren().get(i));
                }
                Assert.assertSame(choice, ((FunctionExpression) wrapped).getChildren().getFirst());
            }
        }
    }

    @Test
    public void testCaseReceiverTraversalRespectsEvaluationBoundaries() {
        DerbyPlatform platform = new DerbyPlatform();
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        Expression choice = builder.value(1).caseStatement(Map.of(1, "first"), "other");
        FunctionExpression unknown = scalarWrapper(choice);
        unknown.getOperator().setType(ExpressionOperator.ComparisonOperator);
        for (Expression receiver : List.of(choice.maximum(), unknown, builder.value("typed").cast("VARCHAR(32)"))) {
            StringWriter sql = new StringWriter();
            ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, null, new SQLCall(), false, null);
            printer.setWriter(sql);
            platform.getOperator(ExpressionOperator.Coalesce).printCollection(List.of(receiver, choice), printer);
            Assert.assertFalse(sql.toString(), sql.toString().contains("CASE WHEN 1=0 THEN"));
        }
        Expression nested = builder.coalesce(List.of(choice, builder.value("fallback")));
        StringWriter sql = new StringWriter();
        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, null, new SQLCall(), false, null);
        printer.setWriter(sql);
        platform.getOperator(ExpressionOperator.Coalesce).printCollection(List.of(nested, builder.value("outer")), printer);
        Assert.assertTrue(sql.toString(), sql.toString().startsWith("COALESCE(COALESCE(CASE WHEN 1=0 THEN"));
        Assert.assertEquals(1, sql.toString().split("CASE WHEN 1=0 THEN", -1).length - 1);
    }

    private static FunctionExpression scalarWrapper(Expression source) {
        ExpressionOperator operator = new ExpressionOperator();
        operator.setType(ExpressionOperator.FunctionOperator);
        operator.bePrefix();
        operator.printsAs(List.of("(", ")"));
        FunctionExpression expression = new FunctionExpression();
        expression.setOperator(operator);
        expression.setBaseExpression(source);
        expression.addChild(source);
        return expression;
    }

    @Test
    public void testCalendarWeekPrintersAndInheritedJavaDb() {
        for (DerbyPlatform platform : List.of(new DerbyPlatform(), new JavaDBPlatform())) {
            Expression value = new LiteralExpression("STAMP", null) {
                @Override
                public void printJava(ExpressionJavaPrinter printer) {
                    printer.printString("STAMP");
                }
            };
            ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
            for (String part : List.of("WEEK", "week")) {
                Expression field = new LiteralExpression(part, null);
                StringWriter sql = new StringWriter();
                ExpressionSQLPrinter printer = new ExpressionSQLPrinter(
                        new DatabaseSessionImpl(new DatabaseLogin(platform)), null, new SQLCall(), false, null);
                printer.setWriter(sql);
                operator.printDuo(value, field, printer);
                String expected = sql.toString();
                Assert.assertTrue(expected.contains("YEAR(STAMP)"));
                Assert.assertTrue(expected.contains("MONTH(STAMP)"));
                Assert.assertTrue(expected.contains("DAY(STAMP)"));
                Assert.assertFalse(expected.contains("TIMESTAMPDIFF"));
                Assert.assertFalse(expected.contains("TIMESTAMPADD"));
                Assert.assertFalse(expected.contains("1753"));
                sql.getBuffer().setLength(0);
                operator.printCollection(List.of(value, field), printer);
                Assert.assertEquals(expected, sql.toString());
                StringWriter java = new StringWriter();
                operator.printJavaDuo(value, field, new ExpressionJavaPrinter("builder", java, platform));
                Assert.assertEquals(expected, java.toString());
                java.getBuffer().setLength(0);
                operator.printJavaCollection(List.of(value, field), new ExpressionJavaPrinter("builder", java, platform));
                Assert.assertEquals(expected, java.toString());
            }
        }
    }

    @Test
    public void testEveryRepeatedDateOperandRetainsItsBinding() {
        DerbyPlatform platform = new DerbyPlatform();
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        Expression value = builder.getParameter("day", LocalDate.class);
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addField(value.extract("WEEK"));
        statement.addTable(new DatabaseTable("TEMPORAL_SAMPLE"));
        statement.computeTablesFromTables();
        DatabaseCall call = statement.buildCall(session);
        Assert.assertTrue(call.getParameters().size() > 1);
        Assert.assertEquals(call.getParameters().size(), call.getSQLString().chars().filter(c -> c == '?').count());
        for (Object parameter : call.getParameters()) {
            Assert.assertEquals(call.getParameters().get(0), parameter);
        }
        Assert.assertSame(builder, value.getBuilder());
    }
}
