/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
public final class CollectionMemberDeclarationTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_09() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("IN(AS", "IN( AS");
			}
		};
	}

	@Test
	public void testBuildExpression_01() {

		String query = " SELECT e  FROM  IN ( e.address.street  )  AS emp ";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(fromInAs("e.address.street", "emp"))
		);

		selectStatement.hasSpaceAfterFrom = true;

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "SELECT e FROM IN(e.address.street) emp";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(fromIn("e.address.street", "emp"))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "SELECT e FROM IN(e.address.street) emp, IN(e.name) AS name";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				fromIn("e.address.street", "emp"),
				fromInAs("e.name", "name")
			)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "SELECT e FROM IN";

		CollectionMemberDeclarationTester in = fromIn(nullExpression(), nullExpression());
		in.hasLeftParenthesis = false;
		in.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "SELECT e FROM IN(";
		CollectionMemberDeclarationTester in = fromIn(nullExpression(), nullExpression());
		in.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "SELECT e FROM IN()";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(fromIn(nullExpression(), nullExpression()))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "SELECT e FROM IN() AS";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
		in.hasSpaceAfterAs = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "SELECT e FROM IN AS";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
		in.hasLeftParenthesis            = false;
		in.hasRightParenthesis           = false;
		in.hasSpaceAfterAs               = false;
		in.hasSpaceAfterIn               = true;
		in.hasSpaceAfterRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "SELECT e FROM IN( AS";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
		in.hasLeftParenthesis  = true;
		in.hasRightParenthesis = false;
		in.hasSpaceAfterAs     = false;
		in.hasSpaceAfterRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement, buildQueryFormatter_09());
	}

	@Test
	public void testBuildExpression_10() {

		String query = "SELECT e FROM IN) AS";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
		in.hasLeftParenthesis  = false;
		in.hasRightParenthesis = true;
		in.hasSpaceAfterAs     = false;
		in.hasSpaceAfterRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String query = "SELECT e FROM IN AS employees";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), variable("employees"));
		in.hasSpaceAfterIn               = true;
		in.hasLeftParenthesis            = false;
		in.hasRightParenthesis           = false;
		in.hasSpaceAfterAs               = true;
		in.hasSpaceAfterRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String query = "SELECT e FROM IN() AS ";

		CollectionMemberDeclarationTester in = fromInAs(nullExpression(), nullExpression());
		in.hasLeftParenthesis  = true;
		in.hasRightParenthesis = true;
		in.hasSpaceAfterAs     = true;
		in.hasSpaceAfterRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(in)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_14() {

		String query = "SELECT a FROM Address a WHERE EXISTS (SELECT e FROM Employee e, IN a.customerList)";

		ExpressionTester selectStatement = selectStatement(
			select(variable("a")),
			from("Address", "a"),
			where(
				exists(
					subquery(
						subSelect(variable("e")),
						subFrom(
							identificationVariableDeclaration("Employee", "e"),
							subFromIn("a.customerList")
						)
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}