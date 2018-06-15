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
public final class LengthExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE LENGTH(e.firstName)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length(path("e.firstName")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE LENGTH(AVG(e.firstName))";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length(avg("e.firstName")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE LENGTH";

        LengthExpressionTester length = length(nullExpression());
        length.hasLeftParenthesis  = false;
        length.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE LENGTH(";

        LengthExpressionTester length = length(nullExpression());
        length.hasLeftParenthesis  = true;
        length.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE LENGTH()";

        LengthExpressionTester length = length(nullExpression());
        length.hasLeftParenthesis  = true;
        length.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE LENGTH GROUP BY e.name";

        LengthExpressionTester length = length(nullExpression());
        length.hasLeftParenthesis  = false;
        length.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE LENGTH( GROUP BY e.name";

        LengthExpressionTester length = length(nullExpression());
        length.hasLeftParenthesis  = true;
        length.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(length),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }
}
