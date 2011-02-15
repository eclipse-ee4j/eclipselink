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
public final class DateTimeTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.startDate < CURRENT_DATE";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.startDate").lowerThan(CURRENT_DATE()))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE CURRENT_TIME > e.startDate";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(CURRENT_TIME().greaterThan(path("e.startDate")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.startDate + 45 = CURRENT_TIMESTAMP";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.startDate").add(numeric(45)).equal(CURRENT_TIMESTAMP()))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE e = p.employee AND " +
		               "      CURRENT_TIMESTAMP = e.startDate";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(
					variable("e").equal(path("p.employee"))
				.and(
					CURRENT_TIMESTAMP().equal(path("e.startDate"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT o " +
		               "FROM Customer c JOIN c.orders o " +
		               "WHERE c.name = 'Smith' AND " +
		               "      o.submissionDate < {d '2008-12-31'}";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("o")),
			from("Customer", "c", join("c.orders", "o")),
			where(
					path("c.name").equal(string("'Smith'"))
				.and(
					path("o.submissionDate").lowerThan(dateTime("{d '2008-12-31'}"))
				)
			)
		);

		testQuery(query, selectStatement);
	}
}