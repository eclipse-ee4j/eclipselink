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

import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_4.*;

/**
 * The unit-tests for {@link org.eclipse.persistence.jpa.jpql.parser.CastExpression CastExpression}.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CastExpressionTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildStringFormatter_01() {
		return new JPQLQueryStringFormatter() {
			public String format(String jpqlQuery) {
				return jpqlQuery.replace("AS)", "AS )");
			}
		};
	}

	private JPQLQueryStringFormatter buildStringFormatter_02() {
		return new JPQLQueryStringFormatter() {
			public String format(String jpqlQuery) {
				return jpqlQuery.replace("TIMESTAMP)", "timestamp)");
			}
		};
	}

	@Test
	public void test_BuildExpression_01() throws Exception {

		// Select cast(e.firstName as char)
		// from Employee e
		// where cast(e.firstName as char) = 'Bob'

		ExpressionTester selectStatement = selectStatement(
			select(castAs("e.firstName", "char")),
			from("Employee", "e"),
			where(
					castAs(path("e.firstName"), "char")
				.equal(
					string("'Bob'")
				)
			)
		);

		testQuery(query_003(), selectStatement);
	}

	@Test
	public void test_BuildExpression_02() throws Exception {

		// Select cast(e.firstName as char(3))
		// from Employee e
		// where cast(e.firstName as char(3)) = 'Bob'

		ExpressionTester selectStatement = selectStatement(
			select(castAs("e.firstName", "char", 3)),
			from("Employee", "e"),
			where(
					castAs(path("e.firstName"), "char", 3)
				.equal(
					string("'Bob'")
				)
			)
		);

		testQuery(query_004(), selectStatement);
	}

	@Test
	public void test_BuildExpression_03() throws Exception {

		// Select cast(e.firstName NUMERIC(5, 4)) from Employee e

		ExpressionTester selectStatement = selectStatement(
			select(cast("e.firstName", "NUMERIC", 5, 4)),
			from("Employee", "e")
		);

		testQuery(query_005(), selectStatement);
	}

	@Test
	public void test_BuildExpression_04() throws Exception {

		// Select cast(e.firstName timestamp) from Employee e

		ExpressionTester selectStatement = selectStatement(
			select(cast("e.firstName", "timestamp")),
			from("Employee", "e")
		);

		testQuery(query_006(), selectStatement, buildStringFormatter_02());
	}

	@Test
	public void test_BuildExpression_05() throws Exception {

		// Select cast(e.firstName YEAR()) from Employee e

		DatabaseTypeTester databaseType = databaseType("YEAR");
		databaseType.hasLeftParenthesis = true;
		databaseType.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast("e.firstName", databaseType)),
			from("Employee", "e")
		);

		testQuery(query_007(), selectStatement);
	}

	@Test
	public void test_BuildExpression_06() throws Exception {

		// Select cast(e.firstName as TIMESTAMP()) from Employee e

		DatabaseTypeTester databaseType = databaseType("TIMESTAMP");
		databaseType.hasLeftParenthesis = true;
		databaseType.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(castAs("e.firstName", databaseType)),
			from("Employee", "e")
		);

		testQuery(query_008(), selectStatement);
	}

	@Test
	public void test_BuildExpression_07() throws Exception {

		String jpqlQuery = "Select cast(e.firstName from Employee e";

		CastExpressionTester cast = cast("e.firstName", nullExpression());
		cast.hasRightParenthesis     = false;
		cast.hasSpaceAfterExpression = true;

		SelectStatementTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		selectStatement.hasSpaceAfterSelect = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_08() throws Exception {

		String jpqlQuery = "Select cast";

		CastExpressionTester cast = cast(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(cast)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_09() throws Exception {

		String jpqlQuery = "Select cast(";

		CastExpressionTester cast = cast(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = true;
		cast.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(cast)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_10() throws Exception {

		String jpqlQuery = "Select cast()";

		CastExpressionTester cast = cast(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = true;
		cast.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_11() throws Exception {

		String jpqlQuery = "Select cast(AS";

		CastExpressionTester cast = castAs(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = true;
		cast.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(cast)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_12() throws Exception {

		String jpqlQuery = "Select cast(AS)";

		CastExpressionTester cast = castAs(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = true;
		cast.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_14() throws Exception {

		String jpqlQuery = "select cast from Employee e";

		CastExpressionTester cast = cast(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_15() throws Exception {

		String jpqlQuery = "select cast ) from Employee e";

		CastExpressionTester cast = cast(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_16() throws Exception {

		String jpqlQuery = "select cast AS ) from Employee e";

		CastExpressionTester cast = castAs(nullExpression(), nullExpression());
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = true;
		cast.hasSpaceAfterAs     = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement, buildStringFormatter_01());
	}

	@Test
	public void test_BuildExpression_17() throws Exception {

		String jpqlQuery = "select cast char) from Employee e";

		CastExpressionTester cast = cast(variable("char"), nullExpression());
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_BuildExpression_18() throws Exception {

		String jpqlQuery = "select cast as char) from Employee e";

		CastExpressionTester cast = castAs(nullExpression(), databaseType("char"));
		cast.hasLeftParenthesis  = false;
		cast.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(cast),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}