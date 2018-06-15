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
public final class NotExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE NOT e.adult";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(not(path("e.adult")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE NOT ((2 + e.age) NOT BETWEEN e.age AND 65)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                not(sub(
                        sub(numeric(2).add(path("e.age")))
                    .notBetween(
                        path("e.age"),
                        numeric(65))
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE NOT HAVING e.age = 2";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(not(nullExpression())),
            having(path("e.age").equal(numeric(2)))
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(not(sub(path("e.adult").greaterThan(numeric(17)))))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17) OR (e.name = 'JPQL')";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    not(sub(path("e.adult").greaterThan(numeric(17))))
                .or(
                    sub(path("e.name").equal(string("'JPQL'")))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17) AND (e.name = 'JPQL')";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    not(sub(path("e.adult").greaterThan(numeric(17))))
                .and(
                    sub(path("e.name").equal(string("'JPQL'")))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17) OR NOT (e.name = 'JPQL')";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    not(sub(path("e.adult").greaterThan(numeric(17))))
                .or(
                    not(sub(path("e.name").equal(string("'JPQL'"))))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17) AND NOT (e.name = 'JPQL')";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    not(sub(path("e.adult").greaterThan(numeric(17))))
                .and(
                    not(sub(path("e.name").equal(string("'JPQL'"))))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE NOT e.firstName = :firstName AND e.lastName = :lastName";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    not(path("e.firstName").equal(inputParameter(":firstName")))
                .and(
                    path("e.lastName").equal(inputParameter(":lastName"))
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
