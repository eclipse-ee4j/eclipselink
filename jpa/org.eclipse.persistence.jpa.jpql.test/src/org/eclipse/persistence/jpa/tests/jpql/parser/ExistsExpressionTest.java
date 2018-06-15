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
public final class ExistsExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_1() {

        String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists(
                subquery(
                    subSelect(variable("e")),
                    subFrom("Employee", "e")
                )
            ))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_2() {

        String query = "SELECT e FROM Employee e WHERE NOT EXISTS (SELECT e FROM Employee e)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(notExists(
                subquery(
                    subSelect(variable("e")),
                    subFrom("Employee", "e")
                )
            ))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_3() {

        String query = "SELECT e FROM Employee e WHERE EXISTS";

        ExistsExpressionTester exists = exists(nullExpression());
        exists.hasLeftParenthesis  = false;
        exists.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_4() {

        String query = "SELECT e FROM Employee e WHERE EXISTS(";

        ExistsExpressionTester exists = exists(nullExpression());
        exists.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists)
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_5() {

        String query = "SELECT e FROM Employee e WHERE EXISTS()";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists(nullExpression()))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_6() {

        String query = "SELECT e FROM Employee e WHERE EXISTS GROUP BY e.name";

        ExistsExpressionTester exists = exists(nullExpression());
        exists.hasLeftParenthesis  = false;
        exists.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_7() {

        String query = "SELECT e FROM Employee e WHERE EXISTS( GROUP BY e.name";

        ExistsExpressionTester exists = exists(nullExpression());
        exists.hasLeftParenthesis  = true;
        exists.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists),
            groupBy(path("e.name"))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_8() {

        String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e.name HAVING e.age > 21)";

        ExistsExpressionTester exists = exists(subquery(
            subSelect(variable("e")),
            subFrom("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            groupBy(path("e.name")),
            having(path("e.age").greaterThan(numeric(21)))
        ));

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(exists)
        );

        testQuery(query, selectStatement);
    }
}
