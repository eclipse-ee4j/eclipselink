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
public final class SelectStatementTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e " +
                       "FROM Employee e " +
                       "WHERE e.department.name = 'NA42' AND " +
                       "      e.address.state IN ('NY', 'CA')";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    path("e.department.name").equal(string("'NA42'"))
                .and(
                    path("e.address.state").in(string("'NY'"), string("'CA'"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT Distinct e FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            selectDistinct(variable("e")),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT DISTINCT e from Employee e WHERE e.name = 'Pascal'";

        SelectStatementTester selectStatement = selectStatement(
            selectDistinct(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e "        +
                       "FROM Employee e " +
                       "GROUP BY e "      +
                       "HAVING SUM(e) "   +
                       "ORDER BY e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            groupBy(variable("e")),
            having(sum(variable("e"))),
            orderBy(orderByItem(variable("e")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            groupBy(variable("e"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            groupBy(variable("e")),
            having(sum(variable("e")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e) ORDER BY e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            groupBy(variable("e")),
            having(sum(variable("e"))),
            orderBy(orderByItem(variable("e")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e ORDER BY e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            groupBy(variable("e")),
            orderBy(orderByItem(variable("e")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' HAVING SUM(e) ORDER BY e";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'Pascal'"))),
            having(sum(variable("e"))),
            orderBy(orderByItem(variable("e")))
        );

        testQuery(query, selectStatement);
    }
}
