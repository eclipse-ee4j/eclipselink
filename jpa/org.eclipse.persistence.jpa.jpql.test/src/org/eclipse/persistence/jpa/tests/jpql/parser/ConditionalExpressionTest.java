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
public final class ConditionalExpressionTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_09() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("+", " + ");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryFormatter_10() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("-", " - ");
			}
		};
	}

	@Test
	public void testBuildExpression_01() {

		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE 'Pascal'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(path("e.name").notLike(string("'Pascal'")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' AND e.name <> e.lastName";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
						path("e.name")
					.greaterThan(
						string("'Pascal'"))
				.and(
						path("e.name")
					.different(
						path("e.lastName"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
						path("e.name")
					.greaterThan(
						string("'Pascal'"))
				.or(
						path("e.name")
					.different(
						path("e.lastName"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName AND e.age = 26";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
						path("e.name")
					.greaterThan(
						string("'Pascal'"))
				.or(
						path("e.name").different(path("e.lastName"))
					.and(
						path("e.age").equal(numeric(26))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM Employee e WHERE " +
		               "e.name > 'Pascal' AND e.manager >= 'code' OR " +
		               "e.name <> e.lastName AND e.age = 26";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
						path("e.name").greaterThan(string("'Pascal'"))
				.and(
						path("e.manager").greaterThanOrEqual(string("'code'"))
				)
				.or(
						path("e.name").different(path("e.lastName"))
					.and(
						path("e.age").equal(numeric(26))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE     e.name > 'Pascal' " +
		               "      AND    e.manager >= 'code' " +
		               "          OR " +
		               "             e.age < 21 " +
		               "          OR " +
		               "             e.name <> e.lastName " +
		               "      AND " +
		               "          e.age = 26";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.name").greaterThan(string("'Pascal'"))
				.and(
						path("e.manager").greaterThanOrEqual(string("'code'"))
				)
				.or(
					path("e.age").lowerThan(numeric(21))
				)
				.or(
						path("e.name").different(path("e.lastName"))
					.and(
						path("e.age").equal(numeric(26))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age)/mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").division(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").multiplication(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age)+mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").add(path("mag.salary")))
		);

		testQuery(query, selectStatement, buildQueryFormatter_09());
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age)-mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").substract(path("mag.salary")))
		);

		testQuery(query, selectStatement, buildQueryFormatter_10());
	}

	@Test
	public void testBuildExpression_11() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age) * mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").multiplication(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String query = "SELECT e FROM Employee e WHERE AVG(e.age) / mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(avg("e.age").division(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String query = "SELECT e FROM Employee e WHERE -AVG(e.age) / mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(minus(avg("e.age")).division(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_14() {

		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(plus(avg("e.age")).division(path("mag.salary")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_15() {

		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / -mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(plus(avg("e.age")).division(minus(path("mag.salary"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_16() {

		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) - -mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(plus(avg("e.age")).substract(minus(path("mag.salary"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_17() {

		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) = 2 AND -mag.salary";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					plus(avg("e.age")).equal(numeric(2))
				.and(
					minus(path("mag.salary"))
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_18() {

		String query = "SELECT e FROM Employee e WHERE +(SQRT(e.age) + e.age) >= -21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					plus(
						sub(
								sqrt(path("e.age"))
							.add(
								path("e.age")
							)
						)
					)
				.greaterThanOrEqual(
					numeric(-21)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}