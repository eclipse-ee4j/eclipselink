/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_5.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Tests parsing <code><b>CONNECT BY</b></code> clause.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.ConnectByClause ConnectByClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ConnectByClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        // SELECT e FROM Employee e CONNECT BY e.managers

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.managers"))
            )
        );

        testQuery(query_001(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        // SELECT employee
        // FROM Employee employee
        // START WITH employee.id = 100
        // CONNECT BY employee.employees
        // ORDER BY employee.last_name

        SelectStatementTester selectStatement = selectStatement(
            select(variable("employee")),
            from(
                "Employee", "employee",
                startWith(path("employee.id").equal(numeric(100))),
                connectBy(collectionPath("employee.employees"))
            ),
            orderBy(orderByItem("employee.last_name"))
        );

        testQuery(query_002(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e START WITH";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", startWith(nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", connectBy(nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY ORDER BY e.name";

        ConnectByClauseTester connectBy = connectBy(nullExpression());
        connectBy.hasSpaceAfterConnectBy = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", connectBy),
            orderBy("e.name")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
