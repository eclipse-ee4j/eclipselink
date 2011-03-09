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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public abstract class FunctionTest extends AbstractJPQLTest {

	abstract Class<? extends AggregateFunction> functionClass();

	abstract String identifier();

	@Test
	public final void testBuildExpression_01() {
		String query = String.format("SELECT %s(e) FROM Employee e", identifier());

		ExpressionTester selectStatement = selectStatement(
			select(aggregateFunctionTester(variable("e"))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	abstract AggregateFunctionTester aggregateFunctionTester(ExpressionTester expression);

	@Test
	public final void testBuildExpression_02() {
		String query = String.format("SELECT %s(DISTINCT e) FROM Employee e", identifier());

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
		String query = String.format("SELECT %s FROM Employee e", identifier());

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
		String query = String.format("SELECT %s( FROM Employee e", identifier());

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
		String query = String.format("SELECT %s() FROM Employee e", identifier());

		ExpressionTester selectStatement = selectStatement(
			select(aggregateFunctionTester(nullExpression())),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public final void testBuildExpression_06() {
		String query = String.format("SELECT %s (DISTINCT) FROM Employee e", identifier());

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
		String query = String.format("SELECT %s (DISTINCT FROM Employee e", identifier());

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