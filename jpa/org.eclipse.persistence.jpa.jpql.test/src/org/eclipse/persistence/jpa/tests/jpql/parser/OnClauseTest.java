/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
public final class OnClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e JOIN e.projects p ON p.budget > 10000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e",
				join("e.projects", "p",
					on(
							path("p.budget")
						.greaterThan(
							numeric("10000")
						)
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e JOIN e.projects p ON";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join("e.projects", "p", on(nullExpression())))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e JOIN e.projects p ON ";

		OnClauseTester onClause = on(nullExpression());
		onClause.hasSpaceAfterIdentifier = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join("e.projects", "p", onClause))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e JOIN e.projects p ON p.budget > 10000 JOIN FETCH p.managers";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e",
				join("e.projects", "p",
					on(
							path("p.budget")
						.greaterThan(
							numeric("10000")
						)
					)
				),
				joinFetch("p.managers")
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e " +
		               "FROM Employee e JOIN e.projects p ON p.budget > 10000 " +
		               "                JOIN FETCH p.managers ON " +
		               "WHERE e.id > -1";

		OnClauseTester onClause = on(nullExpression());
		onClause.hasSpaceAfterIdentifier = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e",
				join("e.projects", "p",
					on(
							path("p.budget")
						.greaterThan(
							numeric("10000")
						)
					)
				),
				joinFetch(collectionPath("p.managers"), nullExpression(),
					onClause
				)
			),
			where(path("e.id").greaterThan(numeric(-1)))
		);

		selectStatement.hasSpaceAfterFrom = false;
		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e " +
		               "FROM Employee e JOIN e.projects p ON p.budget > 10000 " +
		               "                JOIN FETCH p.managers ON SQRT(e.age) = 2 " +
		               "WHERE e.id > -1";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e",
				join("e.projects", "p",
					on(
							path("p.budget")
						.greaterThan(
							numeric("10000")
						)
					)
				),
				joinFetch(collectionPath("p.managers"), nullExpression(),
					on(sqrt(path("e.age")).equal(numeric(2)))
				)
			),
			where(path("e.id").greaterThan(numeric(-1)))
		);

		testQuery(query, selectStatement);
	}
}