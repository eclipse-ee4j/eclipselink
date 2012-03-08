/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.junit.Test;

@SuppressWarnings("nls")
public abstract class AggregateFunctionTest extends JPQLParserTest {

	protected abstract AggregateFunctionTester aggregateFunctionTester(ExpressionTester expression);

	protected abstract Class<? extends AggregateFunction> functionClass();

	protected abstract String identifier();

	@Test
	public final void testBuildExpression_01() {
		String query = "SELECT " + identifier() + "(e) FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(aggregateFunctionTester(variable("e"))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public final void testBuildExpression_02() {
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
	public final void testBuildExpression_03() {
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
	public final void testBuildExpression_04() {
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
	public final void testBuildExpression_05() {
		String query = "SELECT " + identifier() + "() FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(aggregateFunctionTester(nullExpression())),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public final void testBuildExpression_06() {
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
	public final void testBuildExpression_07() {
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