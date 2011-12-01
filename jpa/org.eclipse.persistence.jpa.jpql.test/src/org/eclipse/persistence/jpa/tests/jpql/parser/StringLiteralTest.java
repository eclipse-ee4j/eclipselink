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
public final class StringLiteralTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' > 'Pascal'";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(string("'Pascal''s code'").greaterThan(string("'Pascal'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT c FROM Customer C WHERE c.firstName='Bill' AND c.lastName='Burkes'";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "C"),
			where(
					path("c.firstName").equal(string("'Bill'"))
				.and(
					path("c.lastName").equal(string("'Burkes'"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT c FROM Customer C WHERE c.firstName=\"Bill\"";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("Customer", "C"),
			where(path("c.firstName").equal(string("\"Bill\"")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE e.name = '''  JPQL  From wHeRe '' '";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'''  JPQL  From wHeRe '' '")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("'JPQL")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE e.name = \"JPQL";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").equal(string("\"JPQL")))
		);

		testQuery(query, selectStatement);
	}
}