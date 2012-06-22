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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public final class NullComparisonExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE e.name IS NULL";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(isNull(path("e.name")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(isNotNull(path("e.name")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE IS NULL";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(isNull(nullExpression()))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE IS NULL HAVING e.adult";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			where(isNull(nullExpression())),
			nullExpression(),
			having(path("e.adult")),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}
}