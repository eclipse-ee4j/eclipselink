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
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_5.*;

/**
 * Tests parsing <code><b>CONNECT BY</b></code> clause.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.ConnectByClause ConnectByClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ConnectByClauseTest extends JPQLParserTest {

	@Test
	public void test_BuildExpression_01() throws Exception {

		// SELECT e FROM Employee e CONNECT BY e.managers

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				"Employee", "e",
				connectBy(collectionPath("e.managers"))
			)
		);

		testQuery(query_001(), selectStatement);
	}

	@Test
	public void test_BuildExpression_02() throws Exception {

		// SELECT employee
		// FROM Employee employee
		// START WITH employee.id = 100
		// CONNECT BY employee.employees
		// ORDER BY employee.last_name

		SelectStatementTester selectStatement = selectStatement(
			select(variable("employee")),
			from(
				"Employee", "employee",
				startWith(path("employee.id").equal(numeric(100))),
				connectBy(collectionPath("employee.employees"))
			),
			orderBy(orderByItem("employee.last_name"))
		);

		testQuery(query_002(), selectStatement);
	}

	@Test
	public void test_BuildExpression_03() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", startWith(nullExpression()))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_04() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", connectBy(nullExpression()))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_05() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e CONNECT BY ORDER BY e.name";

		ConnectByClauseTester connectBy = connectBy(nullExpression());
		connectBy.hasSpaceAfterConnectBy = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", connectBy),
			orderBy("e.name")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}