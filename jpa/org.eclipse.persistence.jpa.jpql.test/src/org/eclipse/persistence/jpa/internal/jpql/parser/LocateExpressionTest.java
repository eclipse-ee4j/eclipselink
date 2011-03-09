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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public final class LocateExpressionTest extends AbstractJPQLTest {

	private JPQLQueryStringFormatter buildFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("LOCATE(AND", "LOCATE( AND");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_2() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.substring(0, query.length() - 1);
			}
		};
	}

	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate(path("e.name"), path("e.age")))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(AVG(e.name), e.age, 2 + e.startDate)";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate(avg("e.name"), path("e.age"), numeric(2).add(path("e.startDate"))))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE LOCATE";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = false;
		locate.hasLeftParenthesis = false;
		locate.hasRightParenthesis = false;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = false;
		locate.hasRightParenthesis = false;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE LOCATE()";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = false;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(,)";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = true;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(,";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasRightParenthesis = false;
		locate.hasFirstComma = true;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(,,";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = true;
		locate.hasSecondComma = true;
		locate.hasRightParenthesis = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(,,)";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = true;
		locate.hasSecondComma = true;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {
		String query = "SELECT e FROM Employee e WHERE LOCATE AND e.name = 'Pascal'";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = false;
		locate.hasLeftParenthesis = false;
		locate.hasRightParenthesis = false;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					locate
				.and(
					path("e.name").equal(string("'Pascal'"))
				)
			)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {
		String query = "SELECT e FROM Employee e WHERE LOCATE( AND e.name = 'Pascal'";

		LocateExpressionTester locate = locate(nullExpression(), nullExpression());
		locate.hasFirstComma = false;
		locate.hasRightParenthesis = false;
		locate.hasSecondComma = false;
		locate.hasSpaceAfterFirstComma = false;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					locate
				.and(
					path("e.name").equal(string("'Pascal'"))
				)
			)
		);

		testInvalidQuery(query, selectStatement, buildFormatter_1());
	}

	@Test
	public void testBuildExpression_12() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(, , 2 + e.startDate";

		LocateExpressionTester locate = locate(
			nullExpression(),
			nullExpression(),
			numeric(2).add(path("e.startDate"))
		);

		locate.hasFirstComma = true;
		locate.hasSecondComma = true;
		locate.hasRightParenthesis = false;
		locate.hasSpaceAfterFirstComma = true;
		locate.hasSpaceAfterSecondComma = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age, ";

		LocateExpressionTester locate = locate(nullExpression(), path("e.age"));
		locate.hasFirstComma = true;
		locate.hasSecondComma = true;
		locate.hasLeftParenthesis = true;
		locate.hasRightParenthesis = false;
		locate.hasSpaceAfterFirstComma = true;
		locate.hasSpaceAfterSecondComma = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_14() {
		String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age ";

		LocateExpressionTester locate = locate(nullExpression(), path("e.age"));
		locate.hasFirstComma = true;
		locate.hasSecondComma = false;
		locate.hasLeftParenthesis = true;
		locate.hasRightParenthesis = false;
		locate.hasSpaceAfterFirstComma = true;
		locate.hasSpaceAfterSecondComma = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(locate)
		);

		testValidQuery(query, selectStatement, buildFormatter_2());
	}
}