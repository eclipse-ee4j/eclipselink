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
public final class SelectClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT AVG(mag.price) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(avg("mag.price")),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e.firstName, e.lastName, e.address FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(path("e.firstName"), path("e.lastName"), path("e.address"))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT OBJECT(e), COUNT(DISTINCT e) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(object("e"), countDistinct(variable("e")))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT COUNT(DISTINCT mag.price) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(countDistinct(path("mag.price"))),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT COUNT  ( DISTINCT mag.price  ) FROM Magazine mag";

		SelectStatementTester selectStatement = selectStatement(
			select(countDistinct(path("mag.price"))),
			from("Magazine", "mag")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT DISTINCT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT AVG(e.age), e, COUNT(e.name), NEW test(e), OBJECT(e), SUM(e.age) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(collection(
				avg("e.age"),
				variable("e"),
				count("e.name"),
				new_("test", variable("e")),
				object("e"),
				sum("e.age")
			)),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT OBJECT(e) FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(object("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT DISTINCT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			selectDistinct(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}
}