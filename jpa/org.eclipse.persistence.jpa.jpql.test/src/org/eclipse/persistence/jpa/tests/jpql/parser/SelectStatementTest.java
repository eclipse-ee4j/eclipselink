/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
public final class SelectStatementTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE e.department.name = 'NA42' AND " +
		               "      e.address.state IN ('NY', 'CA')";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.department.name").equal(string("'NA42'"))
				.and(
					path("e.address.state").in(string("'NY'"), string("'CA'"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT Distinct e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT DISTINCT e from Employee e WHERE e.name = 'Pascal'";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e "        +
		               "FROM Employee e " +
		               "GROUP BY e "      +
		               "HAVING SUM(e) "   +
		               "ORDER BY e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			nullExpression(),
			groupBy(variable("e")),
			having(sum(variable("e"))),
			orderBy(orderByItem(variable("e")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			groupBy(variable("e")),
			having(sum(variable("e"))),
			nullExpression()
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e) ORDER BY e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			groupBy(variable("e")),
			having(sum(variable("e"))),
			orderBy(orderByItem(variable("e")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e ORDER BY e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			groupBy(variable("e")),
			nullExpression(),
			orderBy(orderByItem(variable("e")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' HAVING SUM(e) ORDER BY e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'"))),
			nullExpression(),
			having(sum(variable("e"))),
			orderBy(orderByItem(variable("e")))
		);

		testQuery(query, selectStatement);
	}
}