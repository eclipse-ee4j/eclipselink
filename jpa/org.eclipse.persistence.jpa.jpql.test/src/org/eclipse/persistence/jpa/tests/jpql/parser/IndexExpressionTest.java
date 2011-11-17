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
public final class IndexExpressionTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_03() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("INDEX(BETWEEN", "INDEX( BETWEEN");
			}
		};
	}

	@Test
	public void testBuildExpression_01() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX(c) BETWEEN 0 AND 9";

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index("c").between(numeric(0), numeric(9)))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX() BETWEEN 0 AND 9";

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index(nullExpression()).between(numeric(0), numeric(9)))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_03() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX( BETWEEN 0 AND 9";

		IndexExpressionTester index = index(nullExpression());
		index.hasLeftParenthesis  = true;
		index.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index.between(numeric(0), numeric(9)))
		);

		testInvalidQuery(query, selectStatement, buildQueryFormatter_03());
	}

	@Test
	public void testBuildExpression_04() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX BETWEEN 0 AND 9";

		IndexExpressionTester index = index(nullExpression());
		index.hasLeftParenthesis  = false;
		index.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index.between(numeric(0), numeric(9)))
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_05() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX) ";

		IndexExpressionTester index = index(nullExpression());
		index.hasLeftParenthesis  = false;
		index.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index)
		);

		selectStatement.hasSpaceAfterWhere = true;

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_06() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX e)";

		IndexExpressionTester index = index("e");
		index.hasLeftParenthesis  = false;
		index.hasRightParenthesis = true;

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX(e";

		IndexExpressionTester index = index("e");
		index.hasLeftParenthesis  = true;
		index.hasRightParenthesis = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index)
		);

		testInvalidQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_08() throws Exception {

		String query = "SELECT c FROM CreditCard c WHERE INDEX(e ";

		IndexExpressionTester index = index("e");
		index.hasLeftParenthesis  = true;
		index.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("c")),
			from("CreditCard", "c"),
			where(index)
		);

		selectStatement.hasSpaceAfterWhere = true;

		testInvalidQuery(query, selectStatement);
	}
}