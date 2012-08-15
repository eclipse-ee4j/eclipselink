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
 * Tests parsing <code><b>START WITH</b></code> clause.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.StartWithClause StartWithClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StartWithClauseTest extends JPQLParserTest {

	@Test
	public void test_BuildExpression_01() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", startWith(nullExpression()))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_02() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH ";

		StartWithClauseTester startWithClause = startWith(nullExpression());
		startWithClause.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", startWithClause)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_03() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH CONNECT BY NOCYCLE e.id = e.customer.id";

		StartWithClauseTester startWithClause = startWith(nullExpression());
		startWithClause.hasSpaceAfterIdentifier = true;

		HierarchicalQueryClauseTester clause = hierarchicalQueryClause(
			startWithClause,
			connectByNocycle(path("e.id").equal(path("e.customer.id")))
		);

		clause.hasSpaceAfterStartWithClause = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", clause)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_04() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH HAVING e.id = e.customer.id";

		StartWithClauseTester startWithClause = startWith(nullExpression());
		startWithClause.hasSpaceAfterIdentifier = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", startWithClause),
			having(path("e.id").equal(path("e.customer.id")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_05() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH e.id = 100";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", startWith(path("e.id").equal(numeric(100))))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_06() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH e.id = 100 AND e.name = 'JPQL'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				startWith(
						path("e.id").equal(numeric(100))
					.and(
						path("e.name").equal(string("'JPQL'"))
					)
				)
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_07() throws Exception {

		String jpqlQuery = "SELECT e " + "" +
		                   "FROM Employee e " +
		                   "START WITH     e.id = 100 " +
		                   "           AND" +
		                   "               e.name = 'JPQL' " +
		                   "CONNECT BY e.name = 'JPQL'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				startWith(
						path("e.id").equal(numeric(100))
					.and(
						path("e.name").equal(string("'JPQL'"))
					)
				),
				connectBy(path("e.name").equal(string("'JPQL'")))
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}