/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.junit.Test;

@SuppressWarnings("nls")
public abstract class AbstractFunctionExpressionTest extends JPQLParserTest {

	private int index;

	private JPQLQueryStringFormatter buildQueryStringFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("(,)", "(, )");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_2() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_3(final String jpqlQuery) {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return jpqlQuery;
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryStringFormatter_4(final String jpqlQuery) {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return jpqlQuery.replaceAll("=", " = ").replaceAll("\\s+", " ");
			}
		};
	}

	protected abstract String functionName(int index);

	protected abstract String identifier(int index);

	protected boolean isExpression_14_Bad() {
		return false;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		index++;
	}

	@Override
	protected void setUpClass() throws Exception {
		super.setUpClass();
		index = -1;
	}

	@Test
	public void testBuildExpression_01() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('FUNCTION_NAME') FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + ") " +
		                   "FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(function(identifier, functionName)),
			from("Employee", "e")
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String identifier = identifier(index);
		String variable = identifier.toLowerCase();
		String entityName = Character.toUpperCase(identifier.charAt(0)) + identifier.toLowerCase().substring(1);

		// SELECT FUNCTION FROM Employee FUNCTION
		String jpqlQuery = "SELECT " + variable + " FROM " + entityName + " " + variable;

		ExpressionTester selectStatement = selectStatement(
			select(variable(variable)),
			from(entityName, variable)
		);

		testQuery(jpqlQuery, selectStatement, buildQueryStringFormatter_3(jpqlQuery));
	}

	@Test
	public void testBuildExpression_03() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION( FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "( FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = false;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = false;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(, FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(, FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = false;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(,) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(,) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(, ) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(, ) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = true;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement, buildQueryStringFormatter_1());
	}

	@Test
	public void testBuildExpression_07() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(, e) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(, e) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName, variable("e"));
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = true;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(e) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(e) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName, variable("e"));
		function.hasComma = false;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String identifier = identifier(index);
		String functionName = ExpressionTools.EMPTY_STRING;

		// SELECT FUNCTION(e FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(e FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName, variable("e"));
		function.hasComma = false;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = false;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('functionName' FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + " FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = false;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = false;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('functionName', FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + ", FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = false;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('functionName',) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + ",) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = false;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('functionName', ) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + ", ) FROM Employee e";

		FunctionExpressionTester function = function(identifier, functionName);
		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = true;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement, buildQueryStringFormatter_2());
	}

	@Test
	public void testBuildExpression_14() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		// SELECT FUNCTION('functionName', LENGTH(e.name)) FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName + ", LENGTH(e.name)) FROM Employee e";

		FunctionExpressionTester function = function(
			identifier,
			functionName,
			isExpression_14_Bad() ? bad(length(path("e.name"))) : length(path("e.name"))
		);

		function.hasComma = true;
		function.hasLeftParenthesis = true;
		function.hasRightParenthesis = true;
		function.hasSpaceAfterComma = true;

		ExpressionTester selectStatement = selectStatement(
			select(function),
			from("Employee", "e")
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_15() {

		String identifier = identifier(index);
		String functionName1 = functionName(index++);
		String functionName2 = functionName(index);

		// SELECT FUNCTION('functionName', e.firstName, 'NoFirstName'),
		//        FUNCTION('functionName', e.lastName, 'NoLastName')
		// FROM Employee e
		String jpqlQuery = "SELECT " + identifier + "(" + functionName1 + ", e.firstName, 'NoFirstName')," +
		                   "       " + identifier + "(" + functionName2 + ", e.lastName,  'NoLastName' ) " +
		                   "FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(
				function(identifier, functionName1, path("e.firstName"), string("'NoFirstName'")),
				function(identifier, functionName2, path("e.lastName"),  string("'NoLastName'"))
			),
			from("Employee", "e")
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_16() {

		String identifier = identifier(index);
		String functionName = functionName(index);

		String jpqlQuery = "SELECT a " +
		                   "FROM Asset a, Geography selectedGeography " +
		                   "WHERE selectedGeography.id = :id AND " +
		                   "      a.id IN :id_list AND " +
		                   "      " + identifier + "(" + functionName + ", a.geometry, selectedGeography.geometry) = 'TRUE'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("a")),
			from("Asset", "a", "Geography", "selectedGeography"),
			where(
					path("selectedGeography.id").equal(inputParameter(":id"))
				.and(
					path("a.id").in(":id_list")
				).and(
						function(identifier, functionName, path("a.geometry"), path("selectedGeography.geometry"))
					.equal(
						string("'TRUE'"))
				)
			)
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void testBuildExpression_17() {

		String identifier = identifier(index);
		String functionName = functionName(index);
		String variable = identifier.toLowerCase();
		String entityName = Character.toUpperCase(identifier.charAt(0)) + identifier.toLowerCase().substring(1);

		// SELECT function FROM Function function
		// WHERE     function.function=FUNCTION('functionName')
		//       AND
		//           function=function
		// ORDER BY e.name
		String jpqlQuery = "SELECT " + variable + " " +
		                   "FROM " + entityName + " " + variable + " " +
		                   "WHERE " + variable + "." + variable + " = " + identifier + "(" + functionName + ") " +
		                   "      AND" +
		                   "      " + variable + "=" + variable + " " +
		                   "ORDER BY e.name";

		ExpressionTester selectStatement = selectStatement(
			select(variable(variable)),
			from(entityName, variable),
			where(
					path(variable + "." + variable).equal(function(identifier, functionName))
				.and(
					variable(variable).equal(variable(variable))
				)
			),
			orderBy(orderByItem("e.name"))
		);

		testQuery(jpqlQuery, selectStatement, buildQueryStringFormatter_4(jpqlQuery));
	}
}