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
public final class JoinTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join(collectionPath("pub.magazines"), nullExpression())),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT pub FROM Publisher pub JOIN WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join(nullExpression(), nullExpression())),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT pub FROM Publisher pub JOIN AS HAVING pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs(nullExpression(), nullExpression())),
			nullExpression(),
			nullExpression(),
			having(path("pub.revenue").greaterThan(numeric(1000000))),
			nullExpression()
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT pub FROM Publisher pub JOIN AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs(nullExpression(), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     LEFT JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     INNER JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", innerJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_9() {

		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     LEFT OUTER JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftOuterJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(query, selectStatement);
	}
}