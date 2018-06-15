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
public final class NumericLiteralTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 45000";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric(45000)))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 45000.45";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric(45000.45)))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 4E5";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("4E5")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e.salary = +123";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("+123")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE e.salary = -8.932E5";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("-8.932E5")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 0.123e-1";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("0.123e-1")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 5.3f";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("5.3f")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 5.3F";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("5.3F")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 5L";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("5L")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 5.3d";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("5.3d")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 5.3D";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("5.3D")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 0xFF";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric("0xFF")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String query = "SELECT e FROM Employee e WHERE e.salary = .2F";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.salary").equal(numeric(".2F")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_16() {

        String query = "SELECT e FROM Employee e WHERE e.salary = 2.2e-2d AND e.name = 'JPQL'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(
                    path("e.salary").equal(numeric("2.2e-2d"))
                .and(
                    path("e.name").equal(string("'JPQL'"))
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
