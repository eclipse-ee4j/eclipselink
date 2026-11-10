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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.LiteralExpression;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.Oracle12Platform;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.Assert;
import org.junit.Test;

public class OracleTemporalPlatformTest {
    private static String clock(String source) {
        return "(TIMESTAMP '1970-01-01 00:00:00' + TO_DSINTERVAL(TO_CHAR(CAST(" + source
                + " AS TIMESTAMP(9)), '\"0 \"HH24:MI:SS.FF9')))";
    }

    @Test
    public void testTimePrintersPreserveOneSourceAndEpochPrecision() {
        for (OraclePlatform platform : List.of(new OraclePlatform(), new Oracle12Platform())) {
            assertPrinters(platform, "TIME", clock("STAMP"));
        }
    }

    @Test
    public void testTimeBindsOriginalParameterOnce() {
        for (OraclePlatform platform : List.of(new OraclePlatform(), new Oracle12Platform())) {
            DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
            ExpressionBuilder builder = new ExpressionBuilder();
            builder.setSession(session);
            Expression source = builder.getParameter("stamp", LocalDateTime.class);
            Expression field = new LiteralExpression("TIME", null);
            for (boolean collection : List.of(false, true)) {
                SQLCall call = new SQLCall();
                StringWriter sql = new StringWriter();
                ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, null, call, false, null);
                printer.setWriter(sql);
                ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
                if (collection) {
                    operator.printCollection(List.of(source, field), printer);
                } else {
                    operator.printDuo(source, field, printer);
                }
                Assert.assertEquals(clock("?"), sql.toString());
                Assert.assertEquals(List.of(source), call.getParameters());
                Assert.assertSame(builder, source.getBuilder());
            }
        }
    }

    @Test
    public void testOtherFieldsRetainTheirPrinters() {
        OraclePlatform platform = new OraclePlatform();
        assertPrinters(platform, "DATE", "TRUNC(STAMP)");
        assertPrinters(platform, "QUARTER", "TO_NUMBER(TO_CHAR(STAMP, 'Q'))");
        assertPrinters(platform, "WEEK", "TO_NUMBER(TO_CHAR(STAMP, 'IW'))");
        for (String part : List.of("YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND")) {
            assertPrinters(platform, part, "EXTRACT(" + part + " FROM STAMP)");
        }
    }

    @Test
    public void testInvalidFieldRetainsInheritedError() {
        OraclePlatform platform = new OraclePlatform();
        ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
        Expression source = new LiteralExpression("STAMP", null);
        Expression field = new LiteralExpression("invalid", null);
        Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                () -> operator.printDuo(source, field, printer(platform, new StringWriter()))).getMessage());
        Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                () -> operator.printCollection(List.of(source, field), printer(platform, new StringWriter()))).getMessage());
        Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                () -> operator.printJavaDuo(source, field, new ExpressionJavaPrinter("builder", new StringWriter(), platform))).getMessage());
        Assert.assertEquals("Unknown EXTRACT function datetime_field: INVALID", Assert.assertThrows(IllegalArgumentException.class,
                () -> operator.printJavaCollection(List.of(source, field), new ExpressionJavaPrinter("builder", new StringWriter(), platform))).getMessage());
    }

    private static void assertPrinters(OraclePlatform platform, String part, String expected) {
        Expression source = new LiteralExpression("STAMP", null) {
            @Override
            public void printJava(ExpressionJavaPrinter printer) {
                printer.printString("STAMP");
            }
        };
        ExpressionOperator operator = platform.getOperator(ExpressionOperator.Extract);
        for (String name : List.of(part, part.toLowerCase(Locale.ROOT))) {
            Expression field = new LiteralExpression(name, null) {
                @Override
                public void printJava(ExpressionJavaPrinter printer) {
                    printer.printString(name);
                }
            };
            String rendered = expected.startsWith("EXTRACT(") ? expected.replace("EXTRACT(" + part + " ", "EXTRACT(" + name + " ") : expected;
            StringWriter duo = new StringWriter();
            operator.printDuo(source, field, printer(platform, duo));
            Assert.assertEquals(rendered, duo.toString());
            StringWriter collection = new StringWriter();
            operator.printCollection(List.of(source, field), printer(platform, collection));
            Assert.assertEquals(rendered, collection.toString());
            StringWriter javaDuo = new StringWriter();
            operator.printJavaDuo(source, field, new ExpressionJavaPrinter("builder", javaDuo, platform));
            Assert.assertEquals(rendered, javaDuo.toString());
            StringWriter javaCollection = new StringWriter();
            operator.printJavaCollection(List.of(source, field), new ExpressionJavaPrinter("builder", javaCollection, platform));
            Assert.assertEquals(rendered, javaCollection.toString());
        }
    }

    private static ExpressionSQLPrinter printer(OraclePlatform platform, StringWriter writer) {
        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(new DatabaseSessionImpl(new DatabaseLogin(platform)),
                null, new SQLCall(), false, null);
        printer.setWriter(writer);
        return printer;
    }
}
