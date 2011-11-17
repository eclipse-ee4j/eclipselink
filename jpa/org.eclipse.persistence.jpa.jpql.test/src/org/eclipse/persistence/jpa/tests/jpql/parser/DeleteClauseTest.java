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
public final class DeleteClauseTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() {

		String query = "DELETE FROM";

		DeleteClauseTester deleteClause = delete(nullExpression());
		deleteClause.hasSpaceAfterFrom = false;

		DeleteStatementTester deleteStatement = deleteStatement(deleteClause);
		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "DELETE FROM ";

		DeleteStatementTester deleteStatement = deleteStatement(delete(nullExpression()));
		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_03() {

		String query = "DELETE FROM WHERE ";

		WhereClauseTester whereClause = where(nullExpression());
		whereClause.hasSpaceAfterIdentifier = true;

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(nullExpression()),
			whereClause
		);

		deleteStatement.hasSpaceAfterDeleteClause = false;

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_04() {

		String query = "DELETE FROM Employee";

		RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
			abstractSchemaName("Employee"),
			virtualVariable("employee")
		);
		rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = false;

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(rangeVariableDeclaration)
		);

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_05() {

		String query = "DELETE FROM Employee ";

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(
				rangeVariableDeclaration(
					abstractSchemaName("Employee"),
					virtualVariable("employee")
				)
			)
		);

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_06() {

		String query = "DELETE FROM Employee AS";

		RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclarationAs(
			abstractSchemaName("Employee"),
			virtualVariable("employee")
		);
		rangeVariableDeclaration.hasSpaceAfterAs = false;

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(rangeVariableDeclaration)
		);

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_07() {

		String query = "DELETE FROM Employee AS e";

		DeleteStatementTester deleteStatement = deleteStatement(
			deleteAs("Employee", "e")
		);

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_08() {

		String query = "DELETE FROM Employee AS WHERE";

		DeleteStatementTester deleteStatement = deleteStatement(
			deleteAs(abstractSchemaName("Employee"), virtualVariable("employee")),
			where(nullExpression())
		);

		deleteStatement.hasSpaceAfterDeleteClause = false;

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_09() {

		String query = "DELETE FROM Employee AS e WHERE";

		DeleteStatementTester deleteStatement = deleteStatement(
			deleteAs("Employee", "e"),
			where(nullExpression())
		);

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_10() {

		String query = "DELETE FROM Employee AS WHERE ";

		WhereClauseTester whereClause = where(nullExpression());
		whereClause.hasSpaceAfterIdentifier = true;

		DeleteStatementTester deleteStatement = deleteStatement(
			deleteAs(abstractSchemaName("Employee"), virtualVariable("employee")),
			whereClause
		);

		deleteStatement.hasSpaceAfterDeleteClause = false;

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_11() {

		String query = "DELETE FROM Employee AS e WHERE e.name = 'Pascal'";

		DeleteStatementTester deleteStatement = deleteStatement(
			deleteAs("Employee", "e"),
			where(path("e.name").equal(string("'Pascal'")))
		);

		testQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_12() {

		String query = "DELETE FROM  WHERE";

		WhereClauseTester whereClause = where(nullExpression());
		whereClause.hasSpaceAfterIdentifier = false;

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(nullExpression()),
			whereClause
		);

		deleteStatement.hasSpaceAfterDeleteClause = false;

		testInvalidQuery(query, deleteStatement);
	}

	@Test
	public void testBuildExpression_13() {

		String query = "DELETE FROM WHERE e.name = 'Pascal'";

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(nullExpression()),
			where(path("e.name").equal(string("'Pascal'")))
		);

		deleteStatement.hasSpaceAfterDeleteClause = false;

		testInvalidQuery(query, deleteStatement);
	}
}