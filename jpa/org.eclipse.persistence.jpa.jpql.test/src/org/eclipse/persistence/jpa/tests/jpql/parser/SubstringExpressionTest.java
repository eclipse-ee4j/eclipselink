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
public final class SubstringExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, 1)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring(path("e.name"), numeric(0), numeric(1)))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(AVG(e.name), e.age, 2 + e.startDate)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring(avg("e.name"), path("e.age"), numeric(2).add(path("e.startDate"))))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = false;
        substring.hasLeftParenthesis       = false;
        substring.hasRightParenthesis      = false;
        substring.hasSecondComma           = false;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = false;
        substring.hasLeftParenthesis       = true;
        substring.hasRightParenthesis      = false;
        substring.hasSecondComma           = false;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING()";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = false;
        substring.hasLeftParenthesis       = true;
        substring.hasRightParenthesis      = true;
        substring.hasSecondComma           = false;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(,";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = true;
        substring.hasLeftParenthesis       = true;
        substring.hasRightParenthesis      = false;
        substring.hasSecondComma           = false;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = true;
        substring.hasLeftParenthesis       = true;
        substring.hasRightParenthesis      = false;
        substring.hasSecondComma           = true;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,)";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasFirstComma            = true;
        substring.hasLeftParenthesis       = true;
        substring.hasRightParenthesis      = true;
        substring.hasSecondComma           = true;
        substring.hasSpaceAfterFirstComma  = false;
        substring.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(substring)
        );

        testInvalidQuery(query, selectStatement);
    }
}
