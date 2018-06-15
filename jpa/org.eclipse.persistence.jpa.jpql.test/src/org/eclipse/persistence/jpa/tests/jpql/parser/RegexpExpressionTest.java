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
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_4.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RegexpExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        // Select e from Employee e where e.firstName regexp '^B.*'

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(path("e.firstName").regexp(string("'^B.*'")))
        );

        testQuery(query_014(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        String jpqlQuery = "Select e from Employee e where regexp";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp(nullExpression(), nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "Select e from Employee e where regexp 'value'";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp(nullExpression(), string("'value'")))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "Select e from Employee e where e.firstName regexp";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp(path("e.firstName"), nullExpression()))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "Select e from Employee e where e.firstName regexp ";

        RegexpExpressionTester regexp = regexp(path("e.firstName"), nullExpression());
        regexp.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "Select e from Employee e where e.firstName regexp order by e.name";

        RegexpExpressionTester regexp = regexp(path("e.firstName"), nullExpression());
        regexp.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp),
            orderBy(orderByItem(path("e.name")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "Select e from Employee e where e.firstName regexp 2 + 2";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp(path("e.firstName"), numeric(2).add(numeric(2))))
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() throws Exception {

        String jpqlQuery = "Select e from Employee e where e.firstName regexp 2 + 2 order by e.name";

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(regexp(path("e.firstName"), numeric(2).add(numeric(2)))),
            orderBy(orderByItem(path("e.name")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
