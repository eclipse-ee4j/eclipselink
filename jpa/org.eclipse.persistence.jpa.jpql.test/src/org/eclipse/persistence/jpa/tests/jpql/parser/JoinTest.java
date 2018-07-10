/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class JoinTest extends JPQLParserTest {

    private void test_Join(String identifier, JoinTester join) {

        String jpqlQuery = "SELECT e FROM Employee e " + identifier;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", join)
        );

        join.hasSpaceAfterJoin = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_01() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", join("pub.magazines", "mag")),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_02() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinAs("pub.magazines", "mag")),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_03() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", join(collectionPath("pub.magazines"), nullExpression())),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_04() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", join(nullExpression(), nullExpression())),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        selectStatement.hasSpaceAfterFrom = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_05() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN AS HAVING pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinAs(nullExpression(), nullExpression())),
            having(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        selectStatement.hasSpaceAfterFrom = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_06() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN AS mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinAs(nullExpression(), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_07() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT JOIN pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftJoin(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_08() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     INNER JOIN pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", innerJoin(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_09() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT OUTER JOIN pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftOuterJoin(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_Join_10() {

        String jpqlQuery = "Delete from Employee e join e.address a where e.id < 0";

        DeleteStatementTester deleteStatement = deleteStatement(
            delete(
                spacedCollection(
                    rangeVariableDeclaration("Employee", "e"),
                    bad(join("e.address", "a"))
                )
            ),
            where(path("e.id").lowerThan(numeric(0)))
        );

        testInvalidQuery(jpqlQuery, deleteStatement);
    }

    @Test
    public void test_Join_11() {
        JoinTester join = join(nullExpression(), nullExpression());
        test_Join(JOIN, join);
    }

    @Test
    public void test_Join_12() {
        JoinTester join = leftJoin(nullExpression(), nullExpression());
        test_Join(LEFT_JOIN, join);
    }

    @Test
    public void test_Join_13() {
        JoinTester join = leftOuterJoin(nullExpression(), nullExpression());
        test_Join(LEFT_OUTER_JOIN, join);
    }

    @Test
    public void test_Join_14() {
        JoinTester join = innerJoin(nullExpression(), nullExpression());
        test_Join(INNER_JOIN, join);
    }

    @Test
    public void test_Join_15() {
        JoinTester join = join("OUTER JOIN", nullExpression(), nullExpression());
        test_Join("OUTER JOIN", join);
    }

    @Test
    public void test_Join_16() {
        JoinTester join = joinFetch(nullExpression(), nullExpression());
        test_Join(JOIN_FETCH, join);
    }

    @Test
    public void test_Join_17() {
        JoinTester join = leftJoinFetch(nullExpression(), nullExpression());
        test_Join(LEFT_JOIN_FETCH, join);
    }

    @Test
    public void test_Join_18() {
        JoinTester join = leftOuterJoinFetch(nullExpression(), nullExpression());
        test_Join(LEFT_OUTER_JOIN_FETCH, join);
    }

    @Test
    public void test_Join_19() {
        JoinTester join = innerJoinFetch(nullExpression(), nullExpression());
        test_Join(INNER_JOIN_FETCH, join);
    }

    @Test
    public void test_Join_20() {
        JoinTester join = join("OUTER JOIN FETCH", nullExpression(), nullExpression());
        test_Join("OUTER JOIN FETCH", join);
    }

    @Test
    public void test_Join_21() {
        JoinTester join = join("LEFT INNER JOIN", nullExpression(), nullExpression());
        test_Join("LEFT INNER JOIN", join);
    }

    @Test
    public void test_Join_22() {
        JoinTester join = join("LEFT INNER JOIN FETCH", nullExpression(), nullExpression());
        test_Join("LEFT INNER JOIN FETCH", join);
    }

    @Test
    public void test_JoinFetch_01() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetch("pub.magazines", "mag")),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_02() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines AS mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetchAs("pub.magazines", "mag")),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_03() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetch(collectionPath("pub.magazines"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_04() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetch(nullExpression())),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        selectStatement.hasSpaceAfterFrom = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_05() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH AS HAVING pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetchAs(nullExpression(), nullExpression())),
            having(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        selectStatement.hasSpaceAfterFrom = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_06() {

        String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH AS mag WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetchAs(nullExpression(), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_07() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT JOIN FETCH pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_08() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     INNER JOIN FETCH pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";

        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", innerJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_09() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT OUTER JOIN FETCH pub.magazines mag " +
                           "WHERE pub.revenue > 1000000";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftOuterJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_10() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT OUTER JOIN FETCH pub.magazines " +
                           "WHERE pub.revenue > 1000000";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftOuterJoinFetch(collectionPath("pub.magazines"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_11() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     LEFT JOIN FETCH pub.magazines " +
                           "WHERE pub.revenue > 1000000";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", leftJoinFetch(collectionPath("pub.magazines"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JoinFetch_12() {

        String jpqlQuery = "SELECT pub " +
                           "FROM Publisher pub " +
                           "     JOIN FETCH pub.magazines " +
                           "WHERE pub.revenue > 1000000";


        SelectStatementTester selectStatement = selectStatement(
            select(variable("pub")),
            from("Publisher", "pub", joinFetch(collectionPath("pub.magazines"))),
            where(path("pub.revenue").greaterThan(numeric(1000000)))
        );

        testQuery(jpqlQuery, selectStatement);
    }
}
