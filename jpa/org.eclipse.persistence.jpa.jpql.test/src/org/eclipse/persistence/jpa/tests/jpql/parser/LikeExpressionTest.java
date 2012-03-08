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
public final class LikeExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(string("'Pascal''s code'").like(string("'Pascal'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(string("'Pascal''s code'").notLike(string("'Pascal'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE 'p'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(string("'Pascal''s code'").like(string("'Pascal'"), string('p')))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE 'p'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(string("'Pascal''s code'").notLike(string("'Pascal'"), string('p')))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE";

		LikeExpressionTester notLikeExpression = notLike(
			string("'Pascal''s code'"),
			string("'Pascal'")
		);
		notLikeExpression.hasEscape = true;
		notLikeExpression.hasSpaceAfterEscape = false;
		notLikeExpression.hasSpaceAfterPatternValue = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notLikeExpression)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' 'p'";

		LikeExpressionTester notLikeExpression = notLike(
			string("'Pascal''s code'"),
			string("'Pascal'"),
			string('p')
		);
		notLikeExpression.hasEscape = false;
		notLikeExpression.hasSpaceAfterEscape = false;
		notLikeExpression.hasSpaceAfterPatternValue = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(notLikeExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE TRUE GROUP BY e";

		LikeExpressionTester likeExpression = like(
			string("'Pascal''s code'"),
			string("'Pascal'"),
			bad(TRUE())
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(likeExpression),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE TRUE AND e.name = 'Pascal' GROUP BY e";

		LikeExpressionTester likeExpression = like(
			string("'Pascal''s code'"),
			string("'Pascal'"),
			bad(TRUE())
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					likeExpression
				.and(
					path("e.name").equal(string("'Pascal'"))
				)
			),
			groupBy(variable("e")),
			nullExpression(),
			nullExpression()
		);

		testInvalidQuery(query, selectStatement);
	}
}