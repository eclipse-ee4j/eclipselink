/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class DeleteClauseTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "DELETE FROM";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());
		assertFalse(deleteClause.hasSpaceAfterFrom());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "DELETE FROM ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "DELETE FROM WHERE ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue(deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "DELETE FROM Employee";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "DELETE FROM Employee ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue(deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "DELETE FROM Employee AS";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "DELETE FROM Employee AS e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "DELETE FROM Employee AS WHERE";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAs());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "DELETE FROM Employee AS e WHERE";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "DELETE FROM Employee AS WHERE ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAs());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "DELETE FROM Employee AS e WHERE e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue(deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "DELETE FROM Employee AS e WHERE e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue(deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_13()
	{
		String query = "DELETE FROM  WHERE";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_14()
	{
		String query = "DELETE FROM WHERE e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// DeleteStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof DeleteStatement);
		DeleteStatement deleteStatement = (DeleteStatement) expression;

		// DeleteClause
		expression = deleteStatement.getDeleteClause();
		assertTrue(expression instanceof DeleteClause);
		DeleteClause deleteClause = (DeleteClause) expression;

		assertTrue (deleteClause.hasSpaceAfterDelete());

		// RangeVariableDeclaration
		expression = deleteClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);
	}

//	@Test
//	public void testManualCreation_01()
//	{
//		testManualCreation_01(true);
//	}

//	private void testManualCreation_01(boolean hasAS)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		DeleteStatement deleteStatement = expression.addDeleteStatement();
//		assertNotNull("DeleteStatement should not be null", deleteStatement);
//
//		DeleteClause deleteClause = deleteStatement.getDeleteClause();
//		assertNotNull("DeleteClause should not be null", deleteStatement);
//
//		String abstractSchemaName = "Employee";
//		String identificationVariable = "e";
//
//		RangeVariableDeclaration declaration = deleteClause.addAbstractSchemaName
//		(
//			abstractSchemaName,
//			identificationVariable,
//			hasAS
//		);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			deleteClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(hasAS,                  declaration.hasAs());
//		assertEquals(abstractSchemaName,     declaration.getAbstractSchemaName().toParsedText());
//		assertEquals(identificationVariable, declaration.getIdentificationVariable().toParsedText());
//	}
//
//	@Test
//	public void testManualCreation_02()
//	{
//		testManualCreation_01(false);
//	}
//
//	private void testManualCreation_02(String schemaNameDeclaration)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		DeleteStatement deleteStatement = expression.addDeleteStatement();
//		assertNotNull("DeleteStatement should not be null", deleteStatement);
//
//		DeleteClause deleteClause = deleteStatement.getDeleteClause();
//		assertNotNull("DeleteClause should not be null", deleteStatement);
//
//		RangeVariableDeclaration declaration = deleteClause.addAbstractSchemaName(schemaNameDeclaration);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			deleteClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(schemaNameDeclaration, declaration.toParsedText());
//	}
//
//	@Test
//	public void testManualCreation_03()
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		DeleteStatement deleteStatement = expression.addDeleteStatement();
//		assertNotNull("DeleteStatement should not be null", deleteStatement);
//
//		DeleteClause deleteClause = deleteStatement.getDeleteClause();
//		assertNotNull("DeleteClause should not be null", deleteStatement);
//
//		String schemaNameDeclaration = "Employee AS e";
//
//		RangeVariableDeclaration declaration = deleteClause.addAbstractSchemaName
//		(
//			schemaNameDeclaration
//		);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			deleteClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(schemaNameDeclaration, declaration.toParsedText());
//	}
//
//	@Test
//	public void testManualCreation_04()
//	{
//		testManualCreation_02("Employee AS e");
//	}
//
//	@Test
//	public void testManualCreation_05()
//	{
//		testManualCreation_02("Employee e");
//	}
}