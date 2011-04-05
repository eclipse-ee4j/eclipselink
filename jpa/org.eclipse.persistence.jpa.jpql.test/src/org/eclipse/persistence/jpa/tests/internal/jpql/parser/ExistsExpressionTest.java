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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class ExistsExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_1() {
		String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		// SimpleSelectStatement
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		// SimpleSelectClause
		expression = simpleSelectStatement.getSelectClause();
		assertTrue(expression instanceof SimpleSelectClause);
		SimpleSelectClause simpleSelectClause = (SimpleSelectClause) expression;

		// IdentificationVariable
		expression = simpleSelectClause.getSelectExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// SimpleFromClause
		expression = simpleSelectStatement.getFromClause();
		assertTrue(expression instanceof SimpleFromClause);
		SimpleFromClause simpleFromClause = (SimpleFromClause) expression;

		// IdentificationVariableDeclaration
		expression = simpleFromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		assertEquals("Employee e", identificationVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_2() {
		String query = "SELECT e FROM Employee e WHERE NOT EXISTS (SELECT e FROM Employee e)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertTrue(existsExpression.hasNot());

		// SimpleSelectStatement
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		// SimpleSelectClause
		expression = simpleSelectStatement.getSelectClause();
		assertTrue(expression instanceof SimpleSelectClause);
		SimpleSelectClause simpleSelectClause = (SimpleSelectClause) expression;

		// IdentificationVariable
		expression = simpleSelectClause.getSelectExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// SimpleFromClause
		expression = simpleSelectStatement.getFromClause();
		assertTrue(expression instanceof SimpleFromClause);
		SimpleFromClause simpleFromClause = (SimpleFromClause) expression;

		// IdentificationVariableDeclaration
		expression = simpleFromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		assertEquals("Employee e", identificationVariableDeclaration.toParsedText());
	}

	@Test
	public void testBuildExpression_3() {
		String query = "SELECT e FROM Employee e WHERE EXISTS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertFalse(existsExpression.hasLeftParenthesis());
		assertFalse(existsExpression.hasRightParenthesis());

		// NullExpression
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_4() {
		String query = "SELECT e FROM Employee e WHERE EXISTS(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertTrue (existsExpression.hasLeftParenthesis());
		assertFalse(existsExpression.hasRightParenthesis());

		// NullExpression
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_5() {
		String query = "SELECT e FROM Employee e WHERE EXISTS()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertTrue(existsExpression.hasLeftParenthesis());
		assertTrue(existsExpression.hasRightParenthesis());

		// NullExpression
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_6() {
		String query = "SELECT e FROM Employee e WHERE EXISTS GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertFalse(existsExpression.hasLeftParenthesis());
		assertFalse(existsExpression.hasRightParenthesis());

		// NullExpression
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_7() {
		String query = "SELECT e FROM Employee e WHERE EXISTS( GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		assertTrue (existsExpression.hasLeftParenthesis());
		assertFalse(existsExpression.hasRightParenthesis());

		// NullExpression
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_8() {
		String query = "SELECT e FROM Employee e WHERE EXISTS (SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e.name HAVING e.age > 21)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		// SimpleSelectStatement
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals
		(
			"The simple select clause was not parsed correctly",
			"SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e.name HAVING e.age > 21",
			simpleSelectStatement.toParsedText()
		);
	}
}