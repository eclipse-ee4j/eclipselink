/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.Test;

@SuppressWarnings("nls")
public final class AbsExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE ABS(2)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(abs(numeric(2)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE ABS(e.age + 100)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(abs(path("e.age").add(numeric("100"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE ABS(e.age + 100 - AVG(e.age))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					abs(path("e.age")
				.add(
						numeric("100")
					.subtract(
						avg("e.age"))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE ABS(LENGTH(e.name) + SIZE(e.age))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(abs(length(path("e.name")).add(size("e.age"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE ABS";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasLeftParenthesis = false;
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression)
		);

		testValidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE ABS(";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression)
		);

		testValidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE ABS()";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(abs(nullExpression()))
		);

		testValidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE ABS(x)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(abs(variable("x")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM Employee e WHERE ABS GROUP BY e.name";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasLeftParenthesis = false;
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(path("e.name")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e WHERE ABS( GROUP BY e.name";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(path("e.name")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String query = "SELECT e FROM Employee e WHERE ABS)";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasLeftParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String query = "SELECT e FROM Employee e WHERE ABS e.salary)";

		AbsExpressionTester absExpression = abs(path("e.salary"));
		absExpression.hasLeftParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String query = "SELECT e FROM Employee e WHERE ABS GROUP BY e";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasLeftParenthesis = false;
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_14() {

		String query = "SELECT e FROM Employee e WHERE ABS( GROUP BY e";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_15() {

		String query = "SELECT e FROM Employee e WHERE ABS) GROUP BY e";

		AbsExpressionTester absExpression = abs(nullExpression());
		absExpression.hasLeftParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_16() {

		String query = "SELECT e FROM Employee e WHERE ABS(e.age GROUP BY e";

		AbsExpressionTester absExpression = abs(path("e.age"));
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_17() {

		String query = "SELECT e FROM Employee e WHERE ABS(e.age + 2 GROUP BY e";

		AbsExpressionTester absExpression = abs(path("e.age").add(numeric(2)));
		absExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(absExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}
}