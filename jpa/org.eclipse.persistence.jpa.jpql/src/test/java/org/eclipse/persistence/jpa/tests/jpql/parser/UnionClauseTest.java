/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class UnionClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "select e from Employee e union";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "select e from Employee e union ";

        UnionClauseTester union = union(nullExpression());
        union.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "select e from Employee e union all";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            unionAll(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "select e from Employee e union all ";

        UnionClauseTester union = unionAll(nullExpression());
        union.hasSpaceAfterAll = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "select e from Employee e intersect";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            intersect(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "select e from Employee e intersect ";

        UnionClauseTester union = intersect(nullExpression());
        union.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "select e from Employee e intersect all";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            intersectAll(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() {

        String jpqlQuery = "select e from Employee e intersect all ";

        UnionClauseTester union = intersectAll(nullExpression());
        union.hasSpaceAfterAll = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() {

        String jpqlQuery = "select e from Employee e except";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            except(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() {

        String jpqlQuery = "select e from Employee e except ";

        UnionClauseTester union = except(nullExpression());
        union.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() {

        String jpqlQuery = "select e from Employee e except all";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            exceptAll(nullExpression())
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() {

        String jpqlQuery = "select e from Employee e except all ";

        UnionClauseTester union = exceptAll(nullExpression());
        union.hasSpaceAfterAll = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            union
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() {

        String jpqlQuery = "select e from Employee e except select a from Alias a";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            except(
                subSelect(variable("a")),
                subFrom("Alias", "a")
            )
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() {

        String jpqlQuery = "select e from Employee e " +
                           "except select a from Alias a " +
                           "union " +
                           "intersect all select p from Product p where p.id <> 2";

        UnionClauseTester union = union(nullExpression());
        union.hasSpaceAfterIdentifier = true;

        CollectionExpressionTester unionClauses = spacedCollection(
            except(
                subSelect(variable("a")),
                subFrom("Alias", "a")
            ),
            union,
            intersectAll(
                subSelect(variable("p")),
                subFrom("Product", "p"),
                where(path("p.id").different(numeric(2)))
            )
        );

        unionClauses.spaces[1] = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            unionClauses
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() {

        String jpqlQuery = "select e from Employee e " +
                           "except select a " +
                           "intersect all select p from Product p where p.id <> " +
                           "union select d from Department d";

        ComparisonExpressionTester comparison = path("p.id").different(nullExpression());
        comparison.hasSpaceAfterIdentifier = true;

        SimpleSelectStatementTester subquery = subquery(
            subSelect(variable("a")),
            nullExpression()
        );

        subquery.hasSpaceAfterFrom = true;

        CollectionExpressionTester unionClauses = spacedCollection(
            except(subquery),
            intersectAll(
                subSelect(variable("p")),
                subFrom("Product", "p"),
                where(comparison)
            ),
            union(
                subSelect(variable("d")),
                subFrom("Department", "d")
            )
        );

        unionClauses.spaces[0] = false;
        unionClauses.spaces[1] = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            unionClauses
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
