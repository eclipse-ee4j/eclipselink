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
public final class SqrtExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(2)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt(numeric(2)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.age + 100)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt(path("e.age").add(numeric(100))))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.age + 100 - AVG(e.age))";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt(path("e.age").add(numeric(100).subtract(avg(path("e.age"))))))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT";

		SqrtExpressionTester sqrt = sqrt(nullExpression());
		sqrt.hasLeftParenthesis  = false;
		sqrt.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(";

		SqrtExpressionTester sqrt = sqrt(nullExpression());
		sqrt.hasLeftParenthesis  = true;
		sqrt.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT()";

		SqrtExpressionTester sqrt = sqrt(nullExpression());
		sqrt.hasLeftParenthesis  = true;
		sqrt.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT GROUP BY e.name";

		SqrtExpressionTester sqrt = sqrt(nullExpression());
		sqrt.hasLeftParenthesis  = false;
		sqrt.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT( GROUP BY e.name";

		SqrtExpressionTester sqrt = sqrt(nullExpression());
		sqrt.hasLeftParenthesis  = true;
		sqrt.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(sqrt),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}