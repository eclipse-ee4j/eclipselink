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

/**
 * JPA version 2.0.
 */
@SuppressWarnings("nls")
public final class TypeExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(e) IN :empTypes";

		InExpressionTester inExpression = type("e").in(inputParameter(":empTypes"));
		inExpression.hasLeftParenthesis  = false;
		inExpression.hasRightParenthesis = false;
		inExpression.hasSpaceAfterIn     = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(inExpression)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE";

		TypeExpressionTester type = type(nullExpression());
		type.hasLeftParenthesis  = false;
		type.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(";

		TypeExpressionTester type = type(nullExpression());
		type.hasLeftParenthesis  = true;
		type.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE( GROUP BY e.name";

		TypeExpressionTester type = type(nullExpression());
		type.hasLeftParenthesis  = true;
		type.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(e GROUP BY e.name";

		TypeExpressionTester type = type("e");
		type.hasLeftParenthesis  = true;
		type.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE) GROUP BY e.name";

		TypeExpressionTester type = type(nullExpression());
		type.hasLeftParenthesis  = false;
		type.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE e) GROUP BY e.name";

		TypeExpressionTester type = type("e");
		type.hasLeftParenthesis  = false;
		type.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(type),
			groupBy(path("e.name"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}