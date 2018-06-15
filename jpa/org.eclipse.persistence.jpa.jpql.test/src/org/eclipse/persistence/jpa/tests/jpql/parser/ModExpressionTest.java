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
public final class ModExpressionTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryStringFormatter_11() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("MOD(AND", "MOD( AND");
            }
        };
    }

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE MOD(e.name, e.age)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(mod(path("e.name"), path("e.age")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE MOD(AVG(e.name), e.age)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(mod(avg("e.name"), path("e.age")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE MOD";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = false;
        modExpression.hasLeftParenthesis  = false;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE MOD(";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = false;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE MOD()";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = false;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = true;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE MOD(,)";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = true;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE MOD(,";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE MOD(, ";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE MOD(,)";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = true;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e WHERE MOD AND e.name = 'Pascal'";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = false;
        modExpression.hasLeftParenthesis  = false;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    modExpression
                .and(
                    path("e.name").equal(string("'Pascal'"))
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM Employee e WHERE MOD( AND e.name = 'Pascal'";

        ModExpressionTester modExpression = mod(nullExpression(), nullExpression());
        modExpression.hasComma            = false;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    modExpression
                .and(
                    path("e.name").equal(string("'Pascal'"))
                )
            )
        );

        testInvalidQuery(query, selectStatement, buildQueryStringFormatter_11());
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "SELECT e FROM Employee e WHERE MOD(, 2 + e.startDate";

        ModExpressionTester modExpression = mod(nullExpression(), numeric(2).add(path("e.startDate")));
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "SELECT e FROM Employee e WHERE MOD(, e.age";

        ModExpressionTester modExpression = mod(nullExpression(), path("e.age"));
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT e FROM Employee e WHERE MOD(, e.age ";

        ModExpressionTester modExpression = mod(nullExpression(), path("e.age"));
        modExpression.hasComma            = true;
        modExpression.hasLeftParenthesis  = true;
        modExpression.hasRightParenthesis = false;
        modExpression.hasSpaceAfterComma  = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(modExpression)
        );

        selectStatement.hasSpaceAfterWhere = true;

        testInvalidQuery(query, selectStatement);
    }
}
