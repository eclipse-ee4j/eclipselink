/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public final class NumericLiteralTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 45000";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric(45000)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 45000.45";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric(45000.45)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 4E5";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("4E5")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = +123";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("+123")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = -8.932E5";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("-8.932E5")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 0.123e-1";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("0.123e-1")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 5.3f";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("5.3f")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 5.3F";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("5.3F")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 5L";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("5L")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 5.3d";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("5.3d")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.salary = 5.3D";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.salary").equal(numeric("5.3D")))
		);

		testQuery(query, selectStatement);
	}
}