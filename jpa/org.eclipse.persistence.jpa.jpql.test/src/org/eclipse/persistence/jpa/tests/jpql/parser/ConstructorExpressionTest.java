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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class ConstructorExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e) FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(new_(ConstructorExpressionTest.class.getName(), variable("e"))),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e, COUNT(DISTINCT e.name)) FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(
                new_(
                    ConstructorExpressionTest.class.getName(),
                    variable("e"),
                    countDistinct(path("e.name"))
                )
            ),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT NEW";

        ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = false;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT NEW ";

        ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
        constructor.hasSpaceAfterNew    = true;
        constructor.hasLeftParenthesis  = false;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT NEW From Employee e";

        ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
        constructor.hasSpaceAfterNew    = true;
        constructor.hasLeftParenthesis  = false;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT NEW(";

        ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT NEW(,";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            collection(
                new ExpressionTester[] { nullExpression(), nullExpression() },
                new Boolean[] { true, false },
                new Boolean[] { false, false }
            )
        );

        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "SELECT NEW()";

        ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
        constructor.hasSpaceAfterNew = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String jpqlQuery = "SELECT NEW(,)";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            collection(
                new ExpressionTester[] { nullExpression(), nullExpression() },
                new Boolean[] { true, false },
                new Boolean[] { false, false }
            )
        );

        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String jpqlQuery = "SELECT NEW(e.name";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            path("e.name")
        );
        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String jpqlQuery = "SELECT NEW(e.name,";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            collection(
                new ExpressionTester[] { path("e.name"), nullExpression() },
                new Boolean[] { true, false },
                new Boolean[] { false, false }
            )
        );

        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String jpqlQuery = "SELECT NEW(e.name, ";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            collection(path("e.name"), nullExpression())
        );
        constructor.hasSpaceAfterNew    = false;
        constructor.hasLeftParenthesis  = true;
        constructor.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String jpqlQuery = "SELECT NEW(e.name,)";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            collection(
                new ExpressionTester[] { path("e.name"), nullExpression() },
                new Boolean[] { true, false },
                new Boolean[] { false, false }
            )
        );
        constructor.hasSpaceAfterNew = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String jpqlQuery = "SELECT NEW(e.name)";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            path("e.name")
        );
        constructor.hasSpaceAfterNew = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String jpqlQuery = "SELECT NEW(e.name) From Employee e";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            path("e.name")
        );
        constructor.hasSpaceAfterNew = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_16() {

        String jpqlQuery = "SELECT NEW(AVG(e.name)) From Employee e";

        ConstructorExpressionTester constructor = new_(
            ExpressionTools.EMPTY_STRING,
            avg("e.name")
        );
        constructor.hasSpaceAfterNew = false;

        ExpressionTester selectStatement = selectStatement(
            select(constructor),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_17() {

        String jpqlQuery = "SELECT NEW (e.name)";

        ExpressionTester selectStatement = selectStatement(
            select(new_(ExpressionTools.EMPTY_STRING, path("e.name")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
