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

import org.eclipse.persistence.jpa.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.InternalSimpleSelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleResultVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class OrderByClauseTest extends JPQLParserTest {

    private JPQLQueryStringFormatter buildQueryFormatter_01(final String jpqlQuery) {
        return new JPQLQueryStringFormatter() {
            public String format(String query) {
                return jpqlQuery;
            }
        };
    }

    private boolean isScalarExpressionSupported() {
        JPQLQueryBNF queryBNF = getGrammar().getExpressionRegistry().getQueryBNF(InternalOrderByItemBNF.ID);
        return queryBNF.hasChild(ScalarExpressionBNF.ID);
    }

    private boolean isSubqueryResultVariableSupported() {
        JPQLQueryBNF queryBNF = getGrammar().getExpressionRegistry().getQueryBNF(InternalSimpleSelectExpressionBNF.ID);
        return queryBNF.getFallbackBNFId() == SimpleResultVariableBNF.ID;
    }

    private boolean isSubquerySupported() {
        JPQLQueryBNF queryBNF = getGrammar().getExpressionRegistry().getQueryBNF(InternalOrderByItemBNF.ID);
        return queryBNF.hasChild(SubqueryBNF.ID);
    }

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT ORDERe FROM Ordering ORDERe ORDER BY ORDERe.name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("ORDERe")),
            from("Ordering", "ORDERe"),
            orderBy(orderByItem("ORDERe.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate ORDER";

        CollectionExpressionTester items = spacedCollection(
            path("d.rate"),
            variable("ORDER")
        );

        SelectStatementTester selectStatement = selectStatement(
            select(variable("d")),
            from("DiscountCode", "d"),
            orderBy(orderByItem(items))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate GROUP";

        CollectionExpressionTester items = spacedCollection(
            path("d.rate"),
            variable("GROUP")
        );

        SelectStatementTester selectStatement = selectStatement(
            select(variable("d")),
            from("DiscountCode", "d"),
            orderBy(orderByItem(items))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate ORDER BY";

        OrderByItemTester orderBy = orderByItem("d.rate");
        orderBy.hasSpaceAfterExpression = true;

        JPQLExpressionTester jpqlExpression = jpqlExpression(
            selectStatement(
                select(variable("d")),
                from("DiscountCode", "d"),
                orderBy(orderBy)
            ),
            unknown(ORDER_BY)
        );

        testInvalidQuery(jpqlQuery, jpqlExpression);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate SELECT";

        SimpleSelectStatementTester select = subSelectStatement(
            subSelect(nullExpression()),
            nullExpression()
        );

        CollectionExpressionTester items = spacedCollection(
            path("d.rate"),
            isSubquerySupported() ? select : bad(select)
        );

        SelectStatementTester selectStatement = selectStatement(
            select(variable("d")),
            from("DiscountCode", "d"),
            orderBy(orderByItem(items))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate WHERE";

        OrderByItemTester orderBy = orderByItem("d.rate");
        orderBy.hasSpaceAfterExpression = true;

        JPQLExpressionTester jpqlExpression = jpqlExpression(
            selectStatement(
                select(variable("d")),
                from("DiscountCode", "d"),
                orderBy(orderBy)
            ),
            unknown(WHERE)
        );

        testInvalidQuery(jpqlQuery, jpqlExpression);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT d FROM DiscountCode d ORDER BY d.rate SUBSTRING";

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasLeftParenthesis  = false;
        substring.hasRightParenthesis = false;

        CollectionExpressionTester items = spacedCollection(
            path("d.rate"),
            isScalarExpressionSupported() ? substring : bad(substring)
        );

        SelectStatementTester selectStatement = selectStatement(
            select(variable("d")),
            from("DiscountCode", "d"),
            orderBy(orderByItem(items))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(orderByItem("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC, f.address DESC, g.phone";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(
                orderByItemAsc("e.name"),
                orderByItemDesc("f.address"),
                orderByItem("g.phone")
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC ORDER, f.address DESC, g.phone";

        OrderByItemTester item = orderByItemAsc("e.name");
        item.hasSpaceAfterOrdering = true;

        CollectionExpressionTester items = collection(
            item,
            orderByItem(variable("ORDER")),
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC NULLS ORDER, f.address DESC, g.phone";

        OrderByItemTester item = orderByItemAscNullsFirst("e.name");
        item.nulls = NULLS;
        item.hasSpaceAfterNulls = true;

        CollectionExpressionTester items = collection(
            item,
            orderByItem(variable("ORDER")),
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC NULLS SELECT, f.address DESC, g.phone";

        OrderByItemTester item = orderByItemAscNullsFirst("e.name");
        item.nulls = NULLS;
        item.hasSpaceAfterNulls = true;

        CollectionExpressionTester selectItems;

        if (isSubqueryResultVariableSupported()) {
            selectItems = collection(
                nullExpression(),
                resultVariable(path("f.address"), DESC),
                path("g.phone")
            );
        }
        else {
            selectItems = collection(
                nullExpression(),
                path("f.address"),
                variable(DESC),
                path("g.phone")
            );
            selectItems.commas[1] = false;
        }

        SimpleSelectClauseTester subSelect = subSelect(selectItems);
        subSelect.hasSpaceAfterSelect = false;

        SimpleSelectStatementTester select = subSelectStatement(
            subSelect,
            nullExpression()
        );

        CollectionExpressionTester items = collection(
            item,
            orderByItem(isSubquerySupported() ? select : bad(select))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC NULLS FIRST ORDER, f.address DESC, g.phone";

        OrderByItemTester item = orderByItemAscNullsFirst("e.name");
        item.hasSpaceAfterNulls = true;

        CollectionExpressionTester items = collection(
            item,
            orderByItem(variable("ORDER")),
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC NULLS ORDER FIRST, f.address DESC, g.phone";

        OrderByItemTester item1 = orderByItemAscNullsFirst("e.name");
        item1.hasSpaceAfterNulls = true;
        item1.nulls = NULLS;

        OrderByItemTester item2 = orderByItem(
            spacedCollection(
                variable("ORDER"),
                variable(FIRST)
            )
        );

        CollectionExpressionTester items = collection(
            item1,
            item2,
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC ORDER NULLS FIRST, f.address DESC, g.phone";

        OrderByItemTester item1 = orderByItemAsc("e.name");
        item1.hasSpaceAfterOrdering = true;

        OrderByItemTester item2 = orderByItemNullsFirst(variable("ORDER"));

        CollectionExpressionTester items = collection(
            item1,
            item2,
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_16() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC SUBSTRING NULLS FIRST, f.address DESC, g.phone";

        OrderByItemTester item = orderByItemAsc("e.name");
        item.hasSpaceAfterOrdering = true;

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasLeftParenthesis  = false;
        substring.hasRightParenthesis = false;

        CollectionExpressionTester items = collection(
            item,
            orderByItemNullsFirst(isScalarExpressionSupported() ? substring : bad(substring)),
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("f"), variable("g")),
            from("Employee", "e", "Manager", "g"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_17() {

        String jpqlQuery = "SELECT e, f, g " +
                           "FROM Employee e, Manager g " +
                           "ORDER BY e.name ASC NULLS SUBSTRING FIRST, f.address DESC, g.phone";

        OrderByItemTester item1 = orderByItemAscNullsFirst("e.name");
        item1.hasSpaceAfterOrdering = true;
        item1.hasSpaceAfterNulls = true;
        item1.nulls = NULLS;

        SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
        substring.hasLeftParenthesis  = false;
        substring.hasRightParenthesis = false;

        OrderByItemTester item2 = orderByItem(
            spacedCollection(
                isScalarExpressionSupported() ? substring : bad(substring),
                variable(FIRST)
            )
        );

        CollectionExpressionTester items = collection(
            item1,
            item2,
            orderByItemDesc("f.address"),
            orderByItem("g.phone")
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        JPQLExpressionTester jpqlExpression = jpqlExpression(
            selectStatement(
                select(variable("e"), variable("f"), variable("g")),
                from("Employee", "e", "Manager", "g"),
                orderBy(items)
            )
        );

        testInvalidQuery(jpqlQuery, jpqlExpression);
    }

    @Test
    public void test_JPQLQuery_18() {

        if (isJPA1_0()) {
            return;
        }

        String jpqlQuery = "SELECT order AS order FROM Order order ORDER BY order";

        SelectStatementTester selectStatement = selectStatement(
            select(resultVariableAs(variable("order"), "order")),
            from("Order", "order"),
            orderBy(orderByItem(variable("order")))
        );

        testQuery(jpqlQuery, selectStatement, buildQueryFormatter_01(jpqlQuery));
    }

    @Test
    public void test_JPQLQuery_19() {

        String query = "SELECT t,t.name FROM Table1 t ORDER BY NLSSORT(t.name, NLS_SORT=ENGLISH)";

        CollectionExpressionTester collection = collection(
            variable("NLSSORT"),
            sub(
                collection(
                    path("t.name"),
                    bad(
                        variable("NLS_SORT").equal(variable("ENGLISH"))
                    )
                )
            )
        );
        collection.commas[0] = false;
        collection.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(
                collection(
                    variable("t"),
                    path("t.name")
                )
            ),
            from("Table1", "t"),
            orderBy(
                orderByItem(collection)
            )
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_ASC_01() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name ASC";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(orderByItemAsc("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_ASC_02() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name ASC ORDER";

        OrderByItemTester item1 = orderByItemAsc("e.name");
        item1.hasSpaceAfterOrdering = true;

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(variable("ORDER"))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_ASC_03() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name ASC GROUP";

        OrderByItemTester item1 = orderByItemAsc("e.name");
        item1.hasSpaceAfterOrdering = true;

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(variable("GROUP"))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_ASC_04() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name ASC SELECT";

        OrderByItemTester item1 = orderByItemAsc("e.name");
        item1.hasSpaceAfterOrdering = true;

        SimpleSelectClauseTester subSelect = subSelect(nullExpression());
        subSelect.hasSpaceAfterSelect = false;

        SimpleSelectStatementTester select = subSelectStatement(
            subSelect,
            nullExpression()
        );

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(isSubquerySupported() ? select : bad(select))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_DESC_01() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name DESC";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(orderByItemDesc("e.name"))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_DESC_02() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name DESC ORDER";

        OrderByItemTester item1 = orderByItemDesc("e.name");
        item1.hasSpaceAfterOrdering = true;

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(variable("ORDER"))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_DESC_03() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name DESC GROUP";

        OrderByItemTester item1 = orderByItemDesc("e.name");
        item1.hasSpaceAfterOrdering = true;

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(variable("GROUP"))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_DESC_04() {

        String jpqlQuery = "SELECT e, m FROM Employee e, Manager m ORDER BY e.name DESC SELECT";

        OrderByItemTester item1 = orderByItemDesc("e.name");
        item1.hasSpaceAfterOrdering = true;

        SimpleSelectClauseTester subSelect = subSelect(nullExpression());
        subSelect.hasSpaceAfterSelect = false;

        SimpleSelectStatementTester select = subSelectStatement(
            subSelect,
            nullExpression()
        );

        CollectionExpressionTester items = collection(
            item1,
            orderByItem(isSubquerySupported() ? select : bad(select))
        );
        items.commas[0] = false;
        items.spaces[0] = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("m")),
            from("Employee", "e", "Manager", "m"),
            orderBy(items)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
