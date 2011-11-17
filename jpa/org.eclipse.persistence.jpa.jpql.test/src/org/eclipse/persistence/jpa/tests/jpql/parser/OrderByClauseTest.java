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
public final class OrderByClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT ORDERe FROM Ordering ORDERe ORDER BY ORDERe.name";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("ORDERe")),
			from("Ordering", "ORDERe"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItem("ORDERe.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_ASC() {

		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e"), variable("f"), variable("g")),
			from("Employee", "e", "Manager", "g"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemAsc("e.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_Default() {

		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e"), variable("f"), variable("g")),
			from("Employee", "e", "Manager", "g"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItem("e.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_DESC() {

		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name DESC";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e"), variable("f"), variable("g")),
			from("Employee", "e", "Manager", "g"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc("e.name"))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_MultipleValue() {

		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC, f.address DESC, g.phone";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e"), variable("f"), variable("g")),
			from("Employee", "e", "Manager", "g"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(
				orderByItemAsc("e.name"),
				orderByItemDesc("f.address"),
				orderByItem("g.phone")
			)
		);

		testQuery(query, selectStatement);
	}
}