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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * This unit-tests test parsing the null ordering added to an ordering item.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderByItemTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name asc nulls first";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(orderByItemAscNullsFirst("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name asc nulls last";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(orderByItemAscNullsLast("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name nulls first";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(orderByItemNullsFirst("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name nulls last";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(orderByItemNullsLast("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age desc nulls first";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItemNullsLast("e.name"),
                orderByItemDescNullsFirst("e.age")
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age nulls";

        OrderByItemTester orderByItem = orderByItemNullsFirst("e.age");
        orderByItem.nulls = NULLS;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItemNullsLast("e.name"),
                orderByItem
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age NULLS ";

        OrderByItemTester orderByItem = orderByItemNullsFirst("e.age");
        orderByItem.nulls = "NULLS";
        orderByItem.hasSpaceAfterNulls = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            orderBy(
                orderByItemNullsLast("e.name"),
                orderByItem
            )
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() throws Exception {

        String jpqlQuery = "SELECT i FROM Item i WHERE i.category=:category ORDER BY i.id\"";

        ExpressionTester selectStatement = selectStatement(
            select(variable("i")),
            from("Item", "i"),
            where(path("i.category").equal(inputParameter(":category"))),
            orderBy(orderByItem(path("i.id\"")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
