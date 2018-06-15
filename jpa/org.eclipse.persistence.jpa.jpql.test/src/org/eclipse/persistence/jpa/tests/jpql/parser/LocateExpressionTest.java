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
public final class LocateExpressionTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildFormatter_01() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace("LOCATE(AND", "LOCATE( AND");
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_02() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.substring(0, query.length() - 1);
            }
        };
    }

    private JPQLQueryStringFormatter buildFormatter_12() {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return query.replace(",,", ", ,");
            }
        };
    }

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate(path("e.name"), path("e.age")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(AVG(e.name), e.age, 2 + e.startDate)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate(avg("e.name"), path("e.age"), numeric(2).add(path("e.startDate"))))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE LOCATE";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = false;
        locate.hasLeftParenthesis = false;
        locate.hasRightParenthesis = false;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = false;
        locate.hasRightParenthesis = false;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE LOCATE()";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = false;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(,)";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = true;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(,";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasRightParenthesis = false;
        locate.hasFirstComma = true;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(,,";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = true;
        locate.hasSecondComma = true;
        locate.hasRightParenthesis = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(,,)";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = true;
        locate.hasSecondComma = true;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e WHERE LOCATE AND e.name = 'Pascal'";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = false;
        locate.hasLeftParenthesis = false;
        locate.hasRightParenthesis = false;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    locate
                .and(
                    path("e.name").equal(string("'Pascal'"))
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM Employee e WHERE LOCATE( AND e.name = 'Pascal'";

        LocateExpressionTester locate = locate(nullExpression(), nullExpression());
        locate.hasFirstComma = false;
        locate.hasRightParenthesis = false;
        locate.hasSecondComma = false;
        locate.hasSpaceAfterFirstComma = false;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    locate
                .and(
                    path("e.name").equal(string("'Pascal'"))
                )
            )
        );

        testInvalidQuery(query, selectStatement, buildFormatter_01());
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(, , 2 + e.startDate";

        LocateExpressionTester locate = locate(
            nullExpression(),
            nullExpression(),
            numeric(2).add(path("e.startDate"))
        );

        locate.hasFirstComma = true;
        locate.hasSecondComma = true;
        locate.hasRightParenthesis = false;
        locate.hasSpaceAfterFirstComma = true;
        locate.hasSpaceAfterSecondComma = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement, buildFormatter_12());
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age, ";

        LocateExpressionTester locate = locate(nullExpression(), path("e.age"));
        locate.hasFirstComma = true;
        locate.hasSecondComma = true;
        locate.hasLeftParenthesis = true;
        locate.hasRightParenthesis = false;
        locate.hasSpaceAfterFirstComma = true;
        locate.hasSpaceAfterSecondComma = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age ";

        LocateExpressionTester locate = locate(nullExpression(), path("e.age"));
        locate.hasFirstComma = true;
        locate.hasSecondComma = false;
        locate.hasLeftParenthesis = true;
        locate.hasRightParenthesis = false;
        locate.hasSpaceAfterFirstComma = true;
        locate.hasSpaceAfterSecondComma = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(locate)
        );

        testValidQuery(query, selectStatement, buildFormatter_02());
    }
}
