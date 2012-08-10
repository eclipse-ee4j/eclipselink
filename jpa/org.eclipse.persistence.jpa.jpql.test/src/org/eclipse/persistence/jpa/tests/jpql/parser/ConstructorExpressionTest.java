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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.junit.Test;

@SuppressWarnings("nls")
public final class ConstructorExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e) FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(new_(ConstructorExpressionTest.class.getName(), variable("e"))),
			from("Employee", "e")
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String jpqlQuery = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e, COUNT(DISTINCT e.name)) FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(
				new_(
					ConstructorExpressionTest.class.getName(),
					variable("e"),
					countDistinct(path("e.name"))
				)
			),
			from("Employee", "e")
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT NEW";

		ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = false;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT NEW ";

		ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
		constructor.hasSpaceAfterNew    = true;
		constructor.hasLeftParenthesis  = false;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT NEW From Employee e";

		ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
		constructor.hasSpaceAfterNew    = true;
		constructor.hasLeftParenthesis  = false;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT NEW(";

		ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT NEW(,";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			collection(
				new ExpressionTester[] { nullExpression(), nullExpression() },
				new Boolean[] { true, false },
				new Boolean[] { false, false }
			)
		);

		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String jpqlQuery = "SELECT NEW()";

		ConstructorExpressionTester constructor = new_(ExpressionTools.EMPTY_STRING, nullExpression());
		constructor.hasSpaceAfterNew = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String jpqlQuery = "SELECT NEW(,)";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			collection(
				new ExpressionTester[] { nullExpression(), nullExpression() },
				new Boolean[] { true, false },
				new Boolean[] { false, false }
			)
		);

		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String jpqlQuery = "SELECT NEW(e.name";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			path("e.name")
		);
		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String jpqlQuery = "SELECT NEW(e.name,";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			collection(
				new ExpressionTester[] { path("e.name"), nullExpression() },
				new Boolean[] { true, false },
				new Boolean[] { false, false }
			)
		);

		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String jpqlQuery = "SELECT NEW(e.name, ";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			collection(path("e.name"), nullExpression())
		);
		constructor.hasSpaceAfterNew    = false;
		constructor.hasLeftParenthesis  = true;
		constructor.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String jpqlQuery = "SELECT NEW(e.name,)";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			collection(
				new ExpressionTester[] { path("e.name"), nullExpression() },
				new Boolean[] { true, false },
				new Boolean[] { false, false }
			)
		);
		constructor.hasSpaceAfterNew = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_14() {

		String jpqlQuery = "SELECT NEW(e.name)";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			path("e.name")
		);
		constructor.hasSpaceAfterNew = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_15() {

		String jpqlQuery = "SELECT NEW(e.name) From Employee e";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			path("e.name")
		);
		constructor.hasSpaceAfterNew = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_16() {

		String jpqlQuery = "SELECT NEW(AVG(e.name)) From Employee e";

		ConstructorExpressionTester constructor = new_(
			ExpressionTools.EMPTY_STRING,
			avg("e.name")
		);
		constructor.hasSpaceAfterNew = false;

		ExpressionTester selectStatement = selectStatement(
			select(constructor),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_17() {

		String jpqlQuery = "SELECT NEW (e.name)";

		ExpressionTester selectStatement = selectStatement(
			select(new_(ExpressionTools.EMPTY_STRING, path("e.name")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}