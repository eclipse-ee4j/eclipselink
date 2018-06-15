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
public final class SqrtExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(2)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt(numeric(2)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.age + 100)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt(path("e.age").add(numeric(100))))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.age + 100 - AVG(e.age))";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt(path("e.age").add(numeric(100).subtract(avg(path("e.age"))))))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT";

        SqrtExpressionTester sqrt = sqrt(nullExpression());
        sqrt.hasLeftParenthesis  = false;
        sqrt.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(";

        SqrtExpressionTester sqrt = sqrt(nullExpression());
        sqrt.hasLeftParenthesis  = true;
        sqrt.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt)
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT()";

        SqrtExpressionTester sqrt = sqrt(nullExpression());
        sqrt.hasLeftParenthesis  = true;
        sqrt.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT GROUP BY e.name";

        SqrtExpressionTester sqrt = sqrt(nullExpression());
        sqrt.hasLeftParenthesis  = false;
        sqrt.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT( GROUP BY e.name";

        SqrtExpressionTester sqrt = sqrt(nullExpression());
        sqrt.hasLeftParenthesis  = true;
        sqrt.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(sqrt),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
