/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Tests parsing <code><b>START WITH</b></code> clause.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.StartWithClause StartWithClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StartWithClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", startWith(nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH ";

        StartWithClauseTester startWithClause = startWith(nullExpression());
        startWithClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", startWithClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH CONNECT BY e.ids";

        StartWithClauseTester startWithClause = startWith(nullExpression());
        startWithClause.hasSpaceAfterIdentifier = true;

        HierarchicalQueryClauseTester clause = hierarchicalQueryClause(
            startWithClause,
            connectBy(collectionPath("e.ids"))
        );

        clause.hasSpaceAfterStartWithClause = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", clause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH HAVING e.id = e.customer.id";

        StartWithClauseTester startWithClause = startWith(nullExpression());
        startWithClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", startWithClause),
            having(path("e.id").equal(path("e.customer.id")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH e.id = 100";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", startWith(path("e.id").equal(numeric(100))))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH e.id = 100 AND e.name = 'JPQL'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                startWith(
                        path("e.id").equal(numeric(100))
                    .and(
                        path("e.name").equal(string("'JPQL'"))
                    )
                )
            )
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "SELECT e " + "" +
                           "FROM Employee e " +
                           "START WITH     e.id = 100 " +
                           "           AND" +
                           "               e.name = 'JPQL' " +
                           "CONNECT BY e.manager";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                startWith(
                        path("e.id").equal(numeric(100))
                    .and(
                        path("e.name").equal(string("'JPQL'"))
                    )
                ),
                connectBy(collectionPath("e.manager"))
            )
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
