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
public final class ExistsExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_1() {

		String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(
				subquery(
					subSelect(variable("e")),
					subFrom("Employee", "e")
				)
			))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_2() {

		String query = "SELECT e FROM Employee e WHERE NOT EXISTS (SELECT e FROM Employee e)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notExists(
				subquery(
					subSelect(variable("e")),
					subFrom("Employee", "e")
				)
			))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_3() {

		String query = "SELECT e FROM Employee e WHERE EXISTS";

		ExistsExpressionTester exists = exists(nullExpression());
		exists.hasLeftParenthesis  = false;
		exists.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_4() {

		String query = "SELECT e FROM Employee e WHERE EXISTS(";

		ExistsExpressionTester exists = exists(nullExpression());
		exists.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_5() {

		String query = "SELECT e FROM Employee e WHERE EXISTS()";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists(nullExpression()))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_6() {

		String query = "SELECT e FROM Employee e WHERE EXISTS GROUP BY e.name";

		ExistsExpressionTester exists = exists(nullExpression());
		exists.hasLeftParenthesis  = false;
		exists.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists),
			groupBy(path("e.name"))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_7() {

		String query = "SELECT e FROM Employee e WHERE EXISTS( GROUP BY e.name";

		ExistsExpressionTester exists = exists(nullExpression());
		exists.hasLeftParenthesis  = true;
		exists.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists),
			groupBy(path("e.name"))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_8() {

		String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e.name HAVING e.age > 21)";

		ExistsExpressionTester exists = exists(subquery(
			subSelect(variable("e")),
			subFrom("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			groupBy(path("e.name")),
			having(path("e.age").greaterThan(numeric(21)))
		));

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(exists)
		);

		testQuery(query, selectStatement);
	}
}