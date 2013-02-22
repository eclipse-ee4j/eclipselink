/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class SubExpressionTest extends JPQLParserTest {

	@Test
	public void test_JPQLQuery_01() {

		String jpqlQuery = "SELECT e " +
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

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_02() {

		String jpqlQuery = "SELECT e " +
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

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_03() {

		String jpqlQuery = "SELECT e " +
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

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_04() {

		String jpqlQuery = "select (), ((), ()), (,), from Employee e";

		CollectionExpressionTester collection = collection(
			nullExpression(),
			nullExpression()
		);
		collection.spaces[0] = false;

		SelectStatementTester selectStatement = selectStatement(
			select(
				collection(
					sub(nullExpression()),
					sub(collection(
						sub(nullExpression()),
						sub(nullExpression())
					)),
					sub(collection),
					nullExpression()
				)
			),
			from("Employee", "e")
		);

		selectStatement.hasSpaceAfterSelect = false;
		testInvalidQuery(jpqlQuery, selectStatement);
	}
}