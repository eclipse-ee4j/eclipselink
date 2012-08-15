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
public final class BetweenExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND 40";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").between(numeric(20), numeric(40)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {
		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN (SELECT e.age FROM Employee e) AND 40";

		ExpressionTester subquery = subquery(
			subSelect(path("e.age")),
			subFrom("Employee", "e")
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.age")
				.between(
					sub(subquery),
					numeric(40)
				)
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT e, m " +
		               "FROM Employee e, Manager m " +
		               "WHERE e.age BETWEEN (SELECT e.age FROM Employee e) AND (SELECT m.age FROM Manager m)";

		ExpressionTester subquery1 = subquery(
			subSelect(path("e.age")),
			subFrom("Employee", "e")
		);

		ExpressionTester subquery2 = subquery(
			subSelect(path("m.age")),
			subFrom("Manager", "m")
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e"), variable("m")),
			from("Employee", "e", "Manager", "m"),
			where(
					path("e.age")
				.between(
					sub(subquery1),
					sub(subquery2)
				)
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN";

		BetweenExpressionTester betweenExpression = path("e.age").between(nullExpression(), nullExpression());
		betweenExpression.hasAnd = false;
		betweenExpression.hasSpaceAfterAnd = false;
		betweenExpression.hasSpaceAfterBetween = false;
		betweenExpression.hasSpaceAfterLowerBound = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN AND";

		BetweenExpressionTester betweenExpression = path("e.age").between(nullExpression(), nullExpression());
		betweenExpression.hasSpaceAfterAnd = false;
		betweenExpression.hasSpaceAfterLowerBound = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE BETWEEN ";

		BetweenExpressionTester betweenExpression = between(
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
		betweenExpression.hasAnd = false;
		betweenExpression.hasSpaceAfterAnd = false;
		betweenExpression.hasSpaceAfterLowerBound = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE BETWEEN 10 AND";

		BetweenExpressionTester betweenExpression = between(nullExpression(), numeric(10), nullExpression());
		betweenExpression.hasSpaceAfterAnd = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE NOT BETWEEN 10 AND 20";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notBetween(nullExpression(), numeric(10), numeric(20)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age NOT BETWEEN AND 20";

		BetweenExpressionTester betweenExpression = notBetween(
			path("e.age"),
			nullExpression(),
			numeric(20)
		);
		betweenExpression.hasSpaceAfterLowerBound = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN ORDER BY e.name";

		BetweenExpressionTester betweenExpression = between(
			path("e.age"),
			nullExpression(),
			nullExpression()
		);
		betweenExpression.hasAnd = false;
		betweenExpression.hasSpaceAfterAnd = false;
		betweenExpression.hasSpaceAfterLowerBound = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression),
			orderBy(orderByItem("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 10 AND ORDER BY e.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").between(numeric(10), nullExpression())),
			orderBy(orderByItem("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age BETWEEN 10 ORDER BY e.name";

		BetweenExpressionTester betweenExpression = between(
			path("e.age"),
			numeric(10),
			nullExpression()
		);
		betweenExpression.hasAnd = false;
		betweenExpression.hasSpaceAfterAnd = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(betweenExpression),
			orderBy(orderByItem("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}