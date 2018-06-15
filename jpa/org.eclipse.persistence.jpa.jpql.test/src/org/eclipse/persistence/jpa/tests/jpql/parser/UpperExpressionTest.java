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
public final class UpperExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER(e.firstName)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper(path("e.firstName")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER(AVG(e.firstName))";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper(avg("e.firstName")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER";

        UpperExpressionTester upper = upper(nullExpression());
        upper.hasLeftParenthesis  = false;
        upper.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER(";

        UpperExpressionTester upper = upper(nullExpression());
        upper.hasLeftParenthesis  = true;
        upper.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER()";

        UpperExpressionTester upper = upper(nullExpression());
        upper.hasLeftParenthesis  = true;
        upper.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER GROUP BY e.name";

        UpperExpressionTester upper = upper(nullExpression());
        upper.hasLeftParenthesis  = false;
        upper.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER( GROUP BY e.name";

        UpperExpressionTester upper = upper(nullExpression());
        upper.hasLeftParenthesis  = true;
        upper.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(upper),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
