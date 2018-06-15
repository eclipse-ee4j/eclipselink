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

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * JPA version 2.0.
 */
@SuppressWarnings("nls")
public final class TypeExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(e) IN :empTypes";

        InExpressionTester inExpression = type("e").in(inputParameter(":empTypes"));
        inExpression.hasLeftParenthesis  = false;
        inExpression.hasRightParenthesis = false;
        inExpression.hasSpaceAfterIn     = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(inExpression)
        );

        testQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE";

        TypeExpressionTester type = type(nullExpression());
        type.hasLeftParenthesis  = false;
        type.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(";

        TypeExpressionTester type = type(nullExpression());
        type.hasLeftParenthesis  = true;
        type.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type)
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE( GROUP BY e.name";

        TypeExpressionTester type = type(nullExpression());
        type.hasLeftParenthesis  = true;
        type.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(e GROUP BY e.name";

        TypeExpressionTester type = type("e");
        type.hasLeftParenthesis  = true;
        type.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE) GROUP BY e.name";

        TypeExpressionTester type = type(nullExpression());
        type.hasLeftParenthesis  = false;
        type.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE e) GROUP BY e.name";

        TypeExpressionTester type = type("e");
        type.hasLeftParenthesis  = false;
        type.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from("Employee", "e"),
            where(type),
            groupBy(path("e.name"))
        );

        testInvalidQuery(jpqlQuery, selectStatement);
    }
}
