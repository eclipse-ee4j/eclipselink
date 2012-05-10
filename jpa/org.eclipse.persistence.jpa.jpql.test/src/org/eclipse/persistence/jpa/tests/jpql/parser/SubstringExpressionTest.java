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
public final class SubstringExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, 1)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring(path("e.name"), numeric(0), numeric(1)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(AVG(e.name), e.age, 2 + e.startDate)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring(avg("e.name"), path("e.age"), numeric(2).add(path("e.startDate"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = false;
		substring.hasLeftParenthesis       = false;
		substring.hasRightParenthesis      = false;
		substring.hasSecondComma           = false;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = false;
		substring.hasLeftParenthesis       = true;
		substring.hasRightParenthesis      = false;
		substring.hasSecondComma           = false;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING()";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = false;
		substring.hasLeftParenthesis       = true;
		substring.hasRightParenthesis      = true;
		substring.hasSecondComma           = false;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = true;
		substring.hasLeftParenthesis       = true;
		substring.hasRightParenthesis      = false;
		substring.hasSecondComma           = false;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = true;
		substring.hasLeftParenthesis       = true;
		substring.hasRightParenthesis      = false;
		substring.hasSecondComma           = true;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,)";

		SubstringExpressionTester substring = substring(nullExpression(), nullExpression());
		substring.hasFirstComma            = true;
		substring.hasLeftParenthesis       = true;
		substring.hasRightParenthesis      = true;
		substring.hasSecondComma           = true;
		substring.hasSpaceAfterFirstComma  = false;
		substring.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(substring)
		);

		testInvalidQuery(query, selectStatement);
	}
}