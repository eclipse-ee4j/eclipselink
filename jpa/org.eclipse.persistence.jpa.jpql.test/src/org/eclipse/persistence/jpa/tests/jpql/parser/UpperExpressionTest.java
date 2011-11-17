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

import org.junit.Test;

@SuppressWarnings("nls")
public final class UpperExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE UPPER(e.firstName)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper(path("e.firstName")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE UPPER(AVG(e.firstName))";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper(avg("e.firstName")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE UPPER";

		UpperExpressionTester upper = upper(nullExpression());
		upper.hasLeftParenthesis  = false;
		upper.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE UPPER(";

		UpperExpressionTester upper = upper(nullExpression());
		upper.hasLeftParenthesis  = true;
		upper.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE UPPER()";

		UpperExpressionTester upper = upper(nullExpression());
		upper.hasLeftParenthesis  = true;
		upper.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE UPPER GROUP BY e.name";

		UpperExpressionTester upper = upper(nullExpression());
		upper.hasLeftParenthesis  = false;
		upper.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper),
			groupBy(path("e.name")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE UPPER( GROUP BY e.name";

		UpperExpressionTester upper = upper(nullExpression());
		upper.hasLeftParenthesis  = true;
		upper.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(upper),
			groupBy(path("e.name")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}
}