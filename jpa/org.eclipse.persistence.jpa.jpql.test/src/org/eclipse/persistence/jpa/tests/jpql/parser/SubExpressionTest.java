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
public final class SubExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (x > 2) AND (AVG (e.name) <> TRIM (e.name))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					sub(variable("x").greaterThan(numeric(2)))
				.and(
					sub(avg("e.name").different(trim(path("e.name"))))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (x > 2) AND ((AVG (e.name) <> TRIM (e.name)))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					sub(variable("x").greaterThan(numeric(2)))
				.and(
					sub(sub(
						avg("e.name").different(trim(path("e.name")))
					))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (((e.name LIKE 'Pascal')) AND ((CURRENT_DATE) > 2))";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
				sub(
						sub(sub(path("e.name").like(string("'Pascal'"))))
					.and(
						sub(sub(CURRENT_DATE()).greaterThan(numeric(2)))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}