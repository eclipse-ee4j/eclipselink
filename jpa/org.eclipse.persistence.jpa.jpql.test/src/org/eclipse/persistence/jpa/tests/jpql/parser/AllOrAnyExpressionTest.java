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

/**
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression AllOrAnyExpression}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AllOrAnyExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_All_1() {
        String query = "SELECT e FROM Employee e WHERE ALL (SELECT m FROM Manager m)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(all(
                subquery(
                    subSelect(variable("m")),
                    subFrom("Manager", "m")
                )
            ))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_All_2() {
        String query = "SELECT e FROM Employee e WHERE ALL";

        AllOrAnyExpressionTester allExpression = all(nullExpression());
        allExpression.hasLeftParenthesis  = false;
        allExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(allExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_All_3() {
        String query = "SELECT e FROM Employee e WHERE ALL(";

        AllOrAnyExpressionTester allExpression = all(nullExpression());
        allExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(allExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_All_4() {
        String query = "SELECT e FROM Employee e WHERE ALL()";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(all(nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_All_5() {
        String query = "SELECT e FROM Employee e WHERE ALL GROUP BY e.name";

        AllOrAnyExpressionTester allExpression = all(nullExpression());
        allExpression.hasLeftParenthesis  = false;
        allExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(allExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_All_6() {
        String query = "SELECT e FROM Employee e WHERE ALL( GROUP BY e.name";

        AllOrAnyExpressionTester allExpression = all(nullExpression());
        allExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(allExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_1() {
        String query = "SELECT e FROM Employee e WHERE ANY (SELECT m FROM Manager m)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(any(
                subquery(
                    subSelect(variable("m")),
                    subFrom("Manager", "m")
                )
            ))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_2() {
        String query = "SELECT e FROM Employee e WHERE ANY";

        AllOrAnyExpressionTester anyExpression = any(nullExpression());
        anyExpression.hasLeftParenthesis  = false;
        anyExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(anyExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_3() {
        String query = "SELECT e FROM Employee e WHERE ANY(";

        AllOrAnyExpressionTester anyExpression = any(nullExpression());
        anyExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(anyExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_4() {
        String query = "SELECT e FROM Employee e WHERE ANY()";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(any(nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_5() {
        String query = "SELECT e FROM Employee e WHERE ANY GROUP BY e.name";

        AllOrAnyExpressionTester anyExpression = any(nullExpression());
        anyExpression.hasLeftParenthesis  = false;
        anyExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(anyExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Any_6() {
        String query = "SELECT e FROM Employee e WHERE ANY( GROUP BY e.name";

        AllOrAnyExpressionTester anyExpression = any(nullExpression());
        anyExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(anyExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_1() {
        String query = "SELECT e FROM Employee e WHERE SOME (SELECT m FROM Manager m)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(some(
                subquery(
                    subSelect(variable("m")),
                    subFrom("Manager", "m")
                )
            ))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_2() {
        String query = "SELECT e FROM Employee e WHERE SOME";

        AllOrAnyExpressionTester someExpression = some(nullExpression());
        someExpression.hasLeftParenthesis  = false;
        someExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(someExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_3() {
        String query = "SELECT e FROM Employee e WHERE SOME(";

        AllOrAnyExpressionTester someExpression = some(nullExpression());
        someExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(someExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_4() {
        String query = "SELECT e FROM Employee e WHERE SOME()";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(some(nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_5() {
        String query = "SELECT e FROM Employee e WHERE SOME GROUP BY e.name";

        AllOrAnyExpressionTester someExpression = some(nullExpression());
        someExpression.hasLeftParenthesis  = false;
        someExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(someExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_Some_6() {
        String query = "SELECT e FROM Employee e WHERE SOME( GROUP BY e.name";

        AllOrAnyExpressionTester someExpression = some(nullExpression());
        someExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(someExpression),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }
}
