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
public final class ComparisonExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name > 'Pascal'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").greaterThan(string("'Pascal'")))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age >= 21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").greaterThanOrEqual(numeric(21)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age = 21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").equal(numeric(21)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age < 21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").lowerThan(numeric(21)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <= 21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").lowerThanOrEqual(numeric(21)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <> 21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").different(numeric(21)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <>";

		ComparisonExpressionTester comparison = path("e.age").different(nullExpression());
		comparison.hasSpaceAfterIdentifier = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <> ";

		ComparisonExpressionTester comparison = path("e.age").different(nullExpression());
		comparison.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE <> e.age";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(nullExpression().different(path("e.age")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE <>";

		ComparisonExpressionTester comparison = nullExpression().different(nullExpression());
		comparison.hasSpaceAfterIdentifier = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE <> ";

		ComparisonExpressionTester comparison = nullExpression().different(nullExpression());
		comparison.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age <";

		ComparisonExpressionTester comparison = path("e.age").lowerThan(nullExpression());
		comparison.hasSpaceAfterIdentifier = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(comparison)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.age < ORDER BY e.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.age").lowerThan(nullExpression())),
			orderBy(orderByItem(path("e.name")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}