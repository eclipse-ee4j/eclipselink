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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_5.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.AsOfClause AsOfClause}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AsOfClauseTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        // SELECT e
        // FROM Employee e
        // AS OF TIMESTAMP FUNC('TO_TIMESTAMP', '2003-04-04 09:30:00', 'YYYY-MM-DD HH:MI:SS')
        // WHERE e.name = 'JPQL'

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                "Employee",
                "e",
                asOfTimestamp(
                    function(FUNC, "'TO_TIMESTAMP'", string("'2003-04-04 09:30:00'"), string("'YYYY-MM-DD HH:MI:SS'"))
                )
            ),
            where(path("e.name").equal(string("'JPQL'")))
        );

        testQuery(query_004(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        // select e
        // from Employee e
        // as of scn 7920
        // where e.id = 222

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfScn(7920)),
            where(path("e.id").equal(numeric(222)))
        );

        testQuery(query_005(), selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF";

        AsOfClauseTester asOfClause = asOf(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = false;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF ";

        AsOfClauseTester asOfClause = asOf(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF SCN";

        AsOfClauseTester asOfClause = asOfScn(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF SCN ";

        AsOfClauseTester asOfClause = asOfScn(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;
        asOfClause.hasSpaceAfterCategory = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF TIMESTAMP";

        AsOfClauseTester asOfClause = asOfTimestamp(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF TIMESTAMP ";

        AsOfClauseTester asOfClause = asOfTimestamp(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;
        asOfClause.hasSpaceAfterCategory = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF WHERE e.name = 'JPQL'";

        AsOfClauseTester asOfClause = asOf(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause),
            where(path("e.name").equal(string("'JPQL'")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF SCN WHERE e.name = 'JPQL'";

        AsOfClauseTester asOfClause = asOfScn(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;
        asOfClause.hasSpaceAfterCategory = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause),
            where(path("e.name").equal(string("'JPQL'")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e AS OF TIMESTAMP WHERE e.name = 'JPQL'";

        AsOfClauseTester asOfClause = asOfTimestamp(nullExpression());
        asOfClause.hasSpaceAfterIdentifier = true;
        asOfClause.hasSpaceAfterCategory = true;

        ExpressionTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e", asOfClause),
            where(path("e.name").equal(string("'JPQL'")))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
