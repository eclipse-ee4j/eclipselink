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
public final class InputParameterTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE e.name = ?1";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(inputParameter("?1")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e WHERE e.name = ?";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(inputParameter("?")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e WHERE e.name = :name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(inputParameter(":name")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e.name = :";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(inputParameter(":")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT c FROM Customer c WHERE c.firstName=?1 AND c.lastName=?2";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.firstName").equal(inputParameter("?1"))
                .and(
                    path("c.lastName").equal(inputParameter("?2"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT c FROM Customer c WHERE c.firstName=:first AND c.lastName=:last";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "c"),
            where(
                    path("c.firstName").equal(inputParameter(":first"))
                .and(
                    path("c.lastName").equal(inputParameter(":last"))
                )
            )
        );

        testQuery(query, selectStatement);
    }
}
