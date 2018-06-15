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
public final class IdentificationVariableDeclarationTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e, Address AS addr";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                identificationVariableDeclaration("Employee", "e"),
                identificationVariableDeclarationAs("Address", "addr")
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT e FROM Employee e JOIN e.magazines AS mags";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", joinAs("e.magazines", "mags"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT e FROM Employee e JOIN e.magazines mags";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join("e.magazines", "mags"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e LEFT JOIN e.magazines mags";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", leftJoin("e.magazines", "mags"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e JOIN FETCH e.magazines";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", joinFetch("e.magazines"))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e JOIN e.magazines mags " +
                       "                         INNER JOIN e.name AS name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                join("e.magazines", "mags"),
                innerJoinAs("e.name", "name")
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT e " +
                       "FROM Employee e LEFT OUTER JOIN e.magazines mags, " +
                       "     Address AS addr JOIN FETCH addr.street " +
                       "                     INNER JOIN addr.zip AS zip, " +
                       "     Manager m";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                identificationVariableDeclaration  ("Employee", "e",   leftOuterJoin("e.magazines", "mags")),
                identificationVariableDeclarationAs("Address", "addr", joinFetch("addr.street"), innerJoinAs("addr.zip", "zip")),
                identificationVariableDeclaration  ("Manager", "m")
            )
        );

        testQuery(query, selectStatement);
    }
}
