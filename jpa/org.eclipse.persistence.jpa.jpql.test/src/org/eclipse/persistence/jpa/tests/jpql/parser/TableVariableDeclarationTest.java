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

/**
 * This unit-test tests parsing {@link org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration
 * TableVariableDeclaration}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class TableVariableDeclarationTest extends JPQLParserTest {

	@Test
	public final void buildBuildExpression_01() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE('DEPT') d WHERE e.dept = d";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration(table("'DEPT'"), variable("d"))
			),
			where(path("e.dept").equal(variable("d")))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_02() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE('DEPT') AS d WHERE e.dept = d";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclarationAs(table("'DEPT'"), variable("d"))
			),
			where(path("e.dept").equal(variable("d")))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_03() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE('DEPT') AS WHERE e.dept = d";

		TableVariableDeclarationTester tableVariableDeclaration = tableVariableDeclarationAs(
			table("'DEPT'"),
			nullExpression()
		);

		tableVariableDeclaration.hasSpaceAfterAs = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration
			),
			where(path("e.dept").equal(variable("d")))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_04() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE() WHERE e.dept = d";

		TableVariableDeclarationTester tableVariableDeclaration = tableVariableDeclaration(
			table(nullExpression()),
			nullExpression()
		);

		tableVariableDeclaration.hasSpaceAfterTableExpression = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration
			),
			where(path("e.dept").equal(variable("d")))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_05() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE('DEPT' WHERE e.dept = d";

		TableExpressionTester table = table("'DEPT'");
		table.hasRightParenthesis = false;

		TableVariableDeclarationTester tableVariableDeclaration = tableVariableDeclaration(
			table,
			nullExpression()
		);

		tableVariableDeclaration.hasSpaceAfterTableExpression = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration
			),
			where(path("e.dept").equal(variable("d")))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_06() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE('DEPT' WHERE e.dept = d";

		TableExpressionTester table = table("'DEPT'");
		table.hasRightParenthesis = false;

		TableVariableDeclarationTester tableVariableDeclaration = tableVariableDeclaration(
			table,
			nullExpression()
		);

		tableVariableDeclaration.hasSpaceAfterTableExpression = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration
			),
			where(path("e.dept").equal(variable("d")))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_07() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE d WHERE e.dept = d";

		TableExpressionTester table = table(nullExpression());
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration(table, "d")
			),
			where(path("e.dept").equal(variable("d")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_08() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE AS d WHERE e.dept = d";

		TableExpressionTester table = table(nullExpression());
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclarationAs(table, "d")
			),
			where(path("e.dept").equal(variable("d")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_09() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE 'DEPT') AS d WHERE e.dept = d";

		TableExpressionTester table = table("'DEPT'");
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclarationAs(table, "d")
			),
			where(path("e.dept").equal(variable("d")))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_10() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE WHERE e.dept = d";

		TableExpressionTester table = table(nullExpression());
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = false;

		TableVariableDeclarationTester tableVariableDeclaration = tableVariableDeclaration(
			table,
			nullExpression()
		);

		tableVariableDeclaration.hasSpaceAfterTableExpression = true;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration
			),
			where(path("e.dept").equal(variable("d")))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_11() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE, Address a";

		TableExpressionTester table = table(nullExpression());
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration(table, nullExpression()),
				identificationVariableDeclaration("Address", "a")
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_12() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e, TABLE";

		TableExpressionTester table = table(nullExpression());
		table.hasLeftParenthesis  = false;
		table.hasRightParenthesis = false;

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				identificationVariableDeclaration("Employee", "e"),
				tableVariableDeclaration(table, nullExpression())
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_13() throws Exception {

		String jpqlQuery = "SELECT e FROM TABLE('DEPT') d";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(tableVariableDeclaration("'DEPT'", "d"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_14() throws Exception {

		String jpqlQuery = "SELECT e FROM TABLE('DEPT') AS d";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(tableVariableDeclarationAs("'DEPT'", "d"))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public final void buildBuildExpression_15() throws Exception {

		String jpqlQuery = "SELECT e FROM TABLE('DEPT') AS d, Employee e";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from(
				tableVariableDeclarationAs("'DEPT'", "d"),
				identificationVariableDeclaration("Employee", "e")
			)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}