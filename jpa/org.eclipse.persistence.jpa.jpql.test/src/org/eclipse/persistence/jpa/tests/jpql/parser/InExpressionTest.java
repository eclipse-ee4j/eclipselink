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
public final class InExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE e.name IN (e.address.street)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").in(path("e.address.street")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE e NOT IN(e.address.street)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(variable("e").notIn(path("e.address.street")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE e IN (e.address.street, e.name)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    variable("e")
                .in(
                    path("e.address.street"),
                    path("e.name")
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e IN (SELECT m FROM Manager m)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    variable("e")
                .in(
                    subquery(
                        subSelect(variable("m")),
                        subFrom("Manager", "m")
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE e IN";

        InExpressionTester inExpression = in(variable("e"), nullExpression());
        inExpression.hasSpaceAfterIn     = false;
        inExpression.hasLeftParenthesis  = false;
        inExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(inExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE e IN(";

        InExpressionTester inExpression = in(variable("e"), nullExpression());
        inExpression.hasLeftParenthesis  = true;
        inExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(inExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE e IN()";

        InExpressionTester inExpression = in(variable("e"), nullExpression());
        inExpression.hasLeftParenthesis  = true;
        inExpression.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(inExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE e NOT IN()";

        InExpressionTester notInExpression = notIn(variable("e"), nullExpression());
        notInExpression.hasLeftParenthesis  = true;
        notInExpression.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notInExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE e NOT IN(";

        InExpressionTester notInExpression = notIn(variable("e"), nullExpression());
        notInExpression.hasLeftParenthesis  = true;
        notInExpression.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notInExpression)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e " +
                       "WHERE e IN ((SELECT e2 FROM Employee e2 WHERE e2 = e), " +
                       "            (SELECT e3 FROM Employee e3 WHERE e3 = e))";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    variable("e")
                .in(
                    sub(subquery(
                        subSelect(variable("e2")),
                        subFrom("Employee", "e2"),
                        where(variable("e2").equal(variable("e")))
                    )),
                    sub(subquery(
                        subSelect(variable("e3")),
                        subFrom("Employee", "e3"),
                        where(variable("e3").equal(variable("e")))
                    ))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM Employee e WHERE NOT IN('JPQL', 'JPA')";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    nullExpression()
                .notIn(
                    string("'JPQL'"),
                    string("'JPA'")
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String query = "SELECT e FROM Employee e WHERE IN('JPQL', 'JPA')";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    nullExpression()
                .in(
                    string("'JPQL'"),
                    string("'JPA'")
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String query = "SELECT e FROM Employee e WHERE e.roomNumber IN(2, 3, 5)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    path("e.roomNumber")
                .in(
                    numeric(2),
                    numeric(3),
                    numeric(5)
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT prod FROM Product prod WHERE prod.project IN(LargeProject, SmallProject)";

        ExpressionTester selectStatement = selectStatement(
            select(variable("prod")),
            from("Product", "prod"),
            where(
                    path("prod.project")
                .in(
                    isJPA1_0() ? variable("LargeProject") : entity("LargeProject"),
                    isJPA1_0() ? variable("SmallProject") : entity("SmallProject")
                )
            )
        );

        testInvalidQuery(query, selectStatement);
    }
}
