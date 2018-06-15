/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class NullIfExpressionTest extends JPQLParserTest {
    private JPQLQueryStringFormatter buildFormatter_9() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace(",)", ", )");
            }
        };
    }

    @Test
    public void test_JPQLQuery_1() {
        String query = "SELECT NULLIF('JPQL', 4 + e.age) FROM Employee e";

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf(string("'JPQL'"), numeric(4).add(path("e.age")))),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_2() {
        String query = "SELECT NULLIF() FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(nullExpression(), nullExpression());
        nullIf.hasComma = false;
        nullIf.hasSpaceAfterComma = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_3() {
        String query = "SELECT NULLIF(,) FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(nullExpression(), nullExpression());
        nullIf.hasComma = true;
        nullIf.hasSpaceAfterComma = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_4() {
        String query = "SELECT NULLIF FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(nullExpression(), nullExpression());
        nullIf.hasLeftParenthesis = false;
        nullIf.hasComma = false;
        nullIf.hasSpaceAfterComma = false;
        nullIf.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_5() {
        String query = "SELECT NULLIF( FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(nullExpression(), nullExpression());
        nullIf.hasLeftParenthesis = true;
        nullIf.hasComma = false;
        nullIf.hasSpaceAfterComma = false;
        nullIf.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_6() {
        String query = "SELECT NULLIF) FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(nullExpression(), nullExpression());
        nullIf.hasLeftParenthesis = false;
        nullIf.hasComma = false;
        nullIf.hasSpaceAfterComma = false;
        nullIf.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_7() {
        String query = "SELECT NULLIF(e.name) FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(path("e.name"), nullExpression());
        nullIf.hasComma = false;
        nullIf.hasSpaceAfterComma = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_8() {
        String query = "SELECT NULLIF(e.name,) FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(path("e.name"), nullExpression());
        nullIf.hasComma = true;
        nullIf.hasSpaceAfterComma = false;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_9() {
        String query = "SELECT NULLIF(e.name, ) FROM Employee e";

        NullIfExpressionTester nullIf = nullIf(path("e.name"), nullExpression());
        nullIf.hasComma = true;
        nullIf.hasSpaceAfterComma = true;

        ExpressionTester selectStatement = selectStatement
        (
            select(nullIf),
            from("Employee", "e")
        );

        testValidQuery(query, selectStatement, buildFormatter_9());
    }
}
