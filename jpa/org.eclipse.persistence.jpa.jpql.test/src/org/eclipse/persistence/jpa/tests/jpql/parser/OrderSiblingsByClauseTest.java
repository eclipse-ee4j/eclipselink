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
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * The unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause OrderSiblingsByClause}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderSiblingsByClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                orderSiblingsBy(nullExpression())
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY ";

        OrderSiblingsByClauseTester orderSiblingsBy = orderSiblingsBy(nullExpression());
        orderSiblingsBy.hasSpaceAfterIdentifier = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                orderSiblingsBy
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.employee")),
                orderSiblingsBy(orderByItem("e.name"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.employee")),
                orderSiblingsBy(orderByItemAsc("e.name"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name DESC";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.employee")),
                orderSiblingsBy(orderByItemDesc("e.name"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.firstName, e.lastName";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.employee")),
                orderSiblingsBy(orderByItem("e.firstName"), orderByItem("e.lastName"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY";

        OrderSiblingsByClauseTester orderSiblingsBy = orderSiblingsBy(nullExpression());
        orderSiblingsBy.hasSpaceAfterIdentifier = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                connectBy(collectionPath("e.employee")),
                orderSiblingsBy
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY e.name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee", "e",
                orderSiblingsBy(orderByItem("e.name"))
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
