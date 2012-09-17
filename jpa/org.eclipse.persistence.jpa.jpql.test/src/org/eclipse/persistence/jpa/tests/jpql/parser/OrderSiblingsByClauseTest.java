/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

/**
 * The unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause OrderSiblingsByClause}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderSiblingsByClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				orderSiblingsBy(nullExpression())
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY ";

		OrderSiblingsByClauseTester orderSiblingsBy = orderSiblingsBy(nullExpression());
		orderSiblingsBy.hasSpaceAfterIdentifier = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				orderSiblingsBy
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.employee")),
				orderSiblingsBy(orderByItem("e.name"))
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name ASC";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.employee")),
				orderSiblingsBy(orderByItemAsc("e.name"))
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.name DESC";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.employee")),
				orderSiblingsBy(orderByItemDesc("e.name"))
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY e.firstName, e.lastName";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.employee")),
				orderSiblingsBy(orderByItem("e.firstName"), orderByItem("e.lastName"))
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY e.employee ORDER SIBLINGS BY";

		OrderSiblingsByClauseTester orderSiblingsBy = orderSiblingsBy(nullExpression());
		orderSiblingsBy.hasSpaceAfterIdentifier = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.employee")),
				orderSiblingsBy
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String jpqlQuery = "SELECT e FROM Employee e ORDER SIBLINGS BY e.name";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				orderSiblingsBy(orderByItem("e.name"))
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}
}