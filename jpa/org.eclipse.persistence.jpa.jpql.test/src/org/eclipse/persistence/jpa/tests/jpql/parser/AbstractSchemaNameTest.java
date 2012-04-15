/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
public final class AbstractSchemaNameTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() throws Exception {

		String query = "SELECT e FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Employee"), variable("e")))
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() throws Exception {

		String query = "SELECT e FROM Employee e, Address a";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Employee"), variable("e"))),
				identificationVariableDeclaration(rangeVariableDeclaration(abstractSchemaName("Address"), variable("a")))
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() throws Exception {

		String query = "select o from Order o";

		ExpressionTester selectStatement = selectStatement(
			select(variable("o")),
			from("Order", "o")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() throws Exception {

		String query = "select o from Order o order by o.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("o")),
			from("Order", "o"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItem("o.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() throws Exception {

		String query = "select o from Order o where o.age > 18";

		ExpressionTester selectStatement = selectStatement(
			select(variable("o")),
			from("Order", "o"),
			where(path("o.age").greaterThan(numeric(18))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() throws Exception {

		String query = "select g from Group g";

		ExpressionTester selectStatement = selectStatement(
			select(variable("g")),
			from("Group", "g")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() throws Exception {

		String query = "select g from Group g where g.age > 18";

		ExpressionTester selectStatement = selectStatement(
			select(variable("g")),
			from("Group", "g"),
			where(path("g.age").greaterThan(numeric(18))),
			nullExpression(),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}
}