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
 * This unit-tests test parsing the null ordering added to an ordering item.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class OrderByItemTest extends JPQLParserTest {

	@Test
	public void test_BuildExpression_01() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name asc nulls first";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(orderByItemAscNullsFirst("e.name"))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_02() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name asc nulls last";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(orderByItemAscNullsLast("e.name"))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_03() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name nulls first";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(orderByItemNullsFirst("e.name"))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_04() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name nulls last";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(orderByItemNullsLast("e.name"))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_05() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age desc nulls first";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(
				orderByItemNullsLast("e.name"),
				orderByItemDescNullsFirst("e.age")
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_06() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age nulls";

		OrderByItemTester orderByItem = orderByItem("e.age");
		orderByItem.nulls = "NULLS";
		orderByItem.hasSpaceAfterNulls = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(
				orderByItemNullsLast("e.name"),
				orderByItem
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_07() throws Exception {

		String jpqlQuery = "select e from Employee e order by e.name nulls last, e.age NULLS ";

		OrderByItemTester orderByItem = orderByItem("e.age");
		orderByItem.nulls = "NULLS";
		orderByItem.hasSpaceAfterNulls = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			orderBy(
				orderByItemNullsLast("e.name"),
				orderByItem
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_08() throws Exception {

		String jpqlQuery = "SELECT i FROM Item i WHERE i.category=:category ORDER BY i.id\"";

		ExpressionTester selectStatement = selectStatement(
			select(variable("i")),
			from("Item", "i"),
			where(path("i.category").equal(inputParameter(":category"))),
			orderBy(orderByItem(path("i.id\"")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}