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
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ExtractExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() throws Exception {

        String jpqlQuery = "Select extract(year from e.hireDate) from Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(extractFrom("year", path("e.hireDate"))),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() throws Exception {

        String jpqlQuery = "Select extract(year e.hireDate) from Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(extract("year", path("e.hireDate"))),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() throws Exception {

        String jpqlQuery = "Select extract from Employee e";

        ExtractExpressionTester extract = extract(null, nullExpression());
        extract.hasLeftParenthesis  = false;
        extract.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() throws Exception {

        String jpqlQuery = "Select extract( from Employee e";

        ExtractExpressionTester extract = extractFrom(null, variable("Employee"));
        extract.hasSpaceAfterPart   = true;
        extract.hasLeftParenthesis  = true;
        extract.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable(extract, "e"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() throws Exception {

        String jpqlQuery = "Select extract() from Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(extract(null, nullExpression())),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() throws Exception {

        String jpqlQuery = "Select extract(from) from Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(extractFrom(null, nullExpression())),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() throws Exception {

        String jpqlQuery = "Select extract( from Employee e";

        ExtractExpressionTester extract = extractFrom(null, variable("Employee"));
        extract.hasSpaceAfterPart   = true;
        extract.hasLeftParenthesis  = true;
        extract.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable(extract, "e"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_08() throws Exception {

        String jpqlQuery = "Select extract(MICROSECOND from Employee e";

        ExtractExpressionTester extract = extractFrom("MICROSECOND", variable("Employee"));
        extract.hasLeftParenthesis  = true;
        extract.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(resultVariable(extract, "e"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_09() throws Exception {

        String jpqlQuery = "Select extract(WEEK) from Employee e";

        ExtractExpressionTester extract = extract("WEEK", nullExpression());

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_10() throws Exception {

        String jpqlQuery = "Select extract(DAY_HOUR from from Employee e";

        ExtractExpressionTester extract = extractFrom("DAY_HOUR", nullExpression());
        extract.hasSpaceAfterFrom   = true;
        extract.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        selectStatement.hasSpaceAfterSelect = false;

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_11() throws Exception {

        String jpqlQuery = "Select extract(DAY_MINUTE from) from Employee e";

        ExtractExpressionTester extract = extractFrom("DAY_MINUTE", nullExpression());

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_12() throws Exception {

        String jpqlQuery = "Select extract(MONTH (select a.description from Alias a)) " +
                           "from Employee e";

        ExtractExpressionTester extract = extract(
            "MONTH",
            sub(
                subquery(
                    subSelect(path("a.description")),
                    subFrom("Alias", "a")
                )
            )
        );

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_13() throws Exception {

        String jpqlQuery = "Select extract(MONTH e.name + 2 " +
                           "from Employee e";

        ExtractExpressionTester extract = extract(
            "MONTH",
            path("e.name").add(numeric(2))
        );

        extract.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_14() throws Exception {

        String jpqlQuery = "Select extract MONTH e.name + 2) " +
                           "from Employee e";

        ExtractExpressionTester extract = extract(
            "MONTH",
            path("e.name").add(numeric(2))
        );

        extract.hasLeftParenthesis  = false;
        extract.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_15() throws Exception {

        String jpqlQuery = "Select extract MONTH e.name + 2) " +
                           "from Employee e";

        ExtractExpressionTester extract = extract(
            "MONTH",
            path("e.name").add(numeric(2))
        );

        extract.hasLeftParenthesis  = false;
        extract.hasRightParenthesis = true;

        ExpressionTester selectStatement = selectStatement(
            select(extract),
            from("Employee", "e")
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
