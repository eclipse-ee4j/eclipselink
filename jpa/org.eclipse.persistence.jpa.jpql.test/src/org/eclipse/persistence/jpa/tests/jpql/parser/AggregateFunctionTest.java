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

import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * Unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.AggregateFunction AggregateFunction}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AggregateFunctionTest extends JPQLParserTest {

    protected abstract AggregateFunctionTester aggregateFunctionTester(ExpressionTester expression);

    protected abstract Class<? extends AggregateFunction> functionClass();

    protected abstract String identifier();

    @Test
    public final void test_JPQLQuery_01() {
        String query = "SELECT " + identifier() + "(e) FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester(variable("e"))),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_02() {
        String query = "SELECT " + identifier() + "(DISTINCT e) FROM Employee e";

        AggregateFunctionTester aggregateFunctionTester = aggregateFunctionTester(variable("e"));
        aggregateFunctionTester.hasDistinct = true;
        aggregateFunctionTester.hasSpaceAfterDistinct = true;

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_03() {
        String query = "SELECT " + identifier() + " FROM Employee e";

        AggregateFunctionTester aggregateFunctionTester = aggregateFunctionTester(nullExpression());
        aggregateFunctionTester.hasLeftParenthesis  = false;
        aggregateFunctionTester.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_04() {
        String query = "SELECT " + identifier() + "( FROM Employee e";

        AggregateFunctionTester aggregateFunctionTester = aggregateFunctionTester(nullExpression());
        aggregateFunctionTester.hasRightParenthesis = false;

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester),
            from("Employee", "e")
        );

        testInvalidQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_05() {
        String query = "SELECT " + identifier() + "() FROM Employee e";

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester(nullExpression())),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_06() {
        String query = "SELECT " + identifier() + "(DISTINCT) FROM Employee e";

        AggregateFunctionTester aggregateFunctionTester = aggregateFunctionTester(nullExpression());
        aggregateFunctionTester.hasDistinct = true;

        ExpressionTester selectStatement = selectStatement(
            select(aggregateFunctionTester),
            from("Employee", "e")
        );

        testQuery(query, selectStatement);
    }

    @Test
    public final void test_JPQLQuery_07() {
        String query = "SELECT " + identifier() + "(DISTINCT FROM Employee e";

        AggregateFunctionTester aggregateFunctionTester = aggregateFunctionTester(nullExpression());
        aggregateFunctionTester.hasDistinct = true;
        aggregateFunctionTester.hasSpaceAfterDistinct = true;
        aggregateFunctionTester.hasRightParenthesis = false;

        SelectStatementTester selectStatement = selectStatement(
            select(aggregateFunctionTester),
            from("Employee", "e")
        );

        selectStatement.hasSpaceAfterSelect = false;
        testInvalidQuery(query, selectStatement);
    }
}
