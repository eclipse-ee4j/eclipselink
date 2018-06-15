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
public final class StringLiteralTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' > 'Pascal'";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(string("'Pascal''s code'").greaterThan(string("'Pascal'")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT c FROM Customer C WHERE c.firstName='Bill' AND c.lastName='Burkes'";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "C"),
            where(
                    path("c.firstName").equal(string("'Bill'"))
                .and(
                    path("c.lastName").equal(string("'Burkes'"))
                )
            )
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT c FROM Customer C WHERE c.firstName=\"Bill\"";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("c")),
            from("Customer", "C"),
            where(path("c.firstName").equal(string("\"Bill\"")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT e FROM Employee e WHERE e.name = '''  JPQL  From wHeRe '' '";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'''  JPQL  From wHeRe '' '")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("'JPQL")))
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT e FROM Employee e WHERE e.name = \"JPQL";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.name").equal(string("\"JPQL")))
        );

        testQuery(query, selectStatement);
    }
}
