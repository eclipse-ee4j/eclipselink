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
public final class ConcatExpressionTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryStringFormatter_04() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_11() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_12() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("(AND", "( AND");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_13() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				query = query.replace("(AND", "( AND");
				query = query.replace("\')", "\' )");
				return query;
			}
		};
	}

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, e.lastName)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(path("e.firstName"), path("e.lastName")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(AVG(e.firstName), e.lastName)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(avg("e.firstName"), path("e.lastName")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName,)";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { path("e.firstName"), nullExpression() },
			new Boolean[] { true,  false },
			new Boolean[] { false, false }
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(collectionExpression))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, )";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { path("e.firstName"), nullExpression() },
			new Boolean[] { true, false },
			new Boolean[] { true, false }
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(collectionExpression))
		);

		testInvalidQuery(query, selectStatement, buildQueryStringFormatter_04());
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE CONCAT";

		ConcatExpressionTester concatExpression = concat(nullExpression());
		concatExpression.hasLeftParenthesis  = false;
		concatExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concatExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(";

		ConcatExpressionTester concatExpression = concat(nullExpression());
		concatExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concatExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE CONCAT()";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(nullExpression()))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(,)";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { nullExpression(), nullExpression() },
			new Boolean[] { true,  false },
			new Boolean[] { false, false }
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(collectionExpression))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name,";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { path("e.name"), nullExpression() },
			new Boolean[] { true,  false },
			new Boolean[] { false, false }
		);

		ConcatExpressionTester concatExpression = concat(collectionExpression);
		concatExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concatExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name, ";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { path("e.name"), nullExpression() },
			new Boolean[] { true, false },
			new Boolean[] { true, false }
		);

		ConcatExpressionTester concatExpression = concat(collectionExpression);
		concatExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concatExpression)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name, )";

		CollectionExpressionTester collectionExpression = collection(
			new ExpressionTester[] { path("e.name"), nullExpression() },
			new Boolean[] { true, false },
			new Boolean[] { true, false }
		);

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(concat(collectionExpression))
		);

		testInvalidQuery(query, selectStatement, buildQueryStringFormatter_11());
	}

	@Test
	public void testBuildExpression_12() {

		String query = "SELECT e FROM Employee e WHERE CONCAT( AND e.name = 'Pascal'";

		ConcatExpressionTester concatExpression = concat(nullExpression());
		concatExpression.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					concatExpression
				.and(
					path("e.name").equal(string("'Pascal'"))
				)
			)
		);

		testInvalidQuery(query, selectStatement, buildQueryStringFormatter_12());
	}

	@Test
	public void testBuildExpression_13() {

		String query = "SELECT e FROM Employee e WHERE CONCAT( AND e.name = 'Pascal' )";

		ConcatExpressionTester concatExpression = concat(nullExpression());
		concatExpression.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					concatExpression
				.and(
					path("e.name").equal(string("'Pascal'"))
				)
			)
		);

		selectStatement.hasSpaceAfterWhere = true;

		JPQLExpressionTester jpqlExpression = jpqlExpression(selectStatement, unknown(")"));
		testInvalidQuery(query, jpqlExpression, buildQueryStringFormatter_13());
	}
}