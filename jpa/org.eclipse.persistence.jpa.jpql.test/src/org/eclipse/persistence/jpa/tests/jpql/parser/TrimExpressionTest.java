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
public final class TrimExpressionTest extends JPQLParserTest {
    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE TRIM(e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trim(path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimTrailing(path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE TRIM(BOTH e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimBoth(path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE TRIM(LEADING FROM e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimLeadingFrom(path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING FROM e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimTrailingFrom(path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE TRIM('J' FROM e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimFrom('J', path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e FROM Employee e WHERE TRIM(BOTH 'J' FROM e.name)";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(trimBothFrom('J', path("e.name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String query = "SELECT p FROM Person p where 'a' = trim(trim('a'))";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("p")),
            from("Person", "p"),
            where(
                    string("'a'")
                .equal(
                    trim(
                        trim(string("'a'"))
                    )
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
