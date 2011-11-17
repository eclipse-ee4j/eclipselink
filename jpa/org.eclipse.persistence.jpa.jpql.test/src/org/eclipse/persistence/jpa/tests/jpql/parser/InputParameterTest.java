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
public final class InputParameterTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE e.name = ?1";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(inputParameter("?1")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE e.name = ?";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(inputParameter("?")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE e.name = :name";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(inputParameter(":name")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE e.name = :";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(inputParameter(":")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT c FROM Customer c WHERE c.firstName=?1 AND c.lastName=?2";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.firstName").equal(inputParameter("?1"))
				.and(
					path("c.lastName").equal(inputParameter("?2"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT c FROM Customer c WHERE c.firstName=:first AND c.lastName=:last";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "c"),
			where(
					path("c.firstName").equal(inputParameter(":first"))
				.and(
					path("c.lastName").equal(inputParameter(":last"))
				)
			)
		);

		testQuery(query, selectStatement);
	}
}