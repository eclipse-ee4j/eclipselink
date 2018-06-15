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

@SuppressWarnings("nls")
public final class ObjectExpressionTest extends JPQLParserTest {

    @Test
    public void test_JPQLQuery_01() {

        String query = "SELECT OBJECT(e) FROM Employee e";

        SelectStatementTester selectStatement = selectStatement(
            select(object("e")),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_02() {

        String query = "SELECT OBJECT FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = false;
        objectExpression.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(objectExpression),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_03() {

        String query = "SELECT OBJECT( FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = true;
        objectExpression.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(objectExpression),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_04() {

        String query = "SELECT OBJECT() FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = true;
        objectExpression.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(object(nullExpression())),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_05() {

        String query = "SELECT OBJECT, e FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = false;
        objectExpression.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(objectExpression, variable("e")),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_06() {

        String query = "SELECT OBJECT(, e FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = true;
        objectExpression.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(objectExpression, variable("e")),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public void test_JPQLQuery_07() {

        String query = "SELECT OBJECT), e FROM Employee e";

        ObjectExpressionTester objectExpression = object(nullExpression());
        objectExpression.hasLeftParenthesis  = false;
        objectExpression.hasRightParenthesis = true;

        SelectStatementTester selectStatement = selectStatement(
            select(objectExpression, variable("e")),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }
}
