/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
public final class LengthExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH(e.firstName)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		// StateFieldPathExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH(AVG(e.firstName))";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		// AvgFunction
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.firstName)", avgFunction.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertSame (LengthExpression.LENGTH, lengthExpression.getText());
		assertFalse(lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertSame (LengthExpression.LENGTH, lengthExpression.getText());
		assertTrue (lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertSame(LengthExpression.LENGTH, lengthExpression.getText());
		assertTrue(lengthExpression.hasLeftParenthesis());
		assertTrue(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertSame (LengthExpression.LENGTH, lengthExpression.getText());
		assertFalse(lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE LENGTH( GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertSame (LengthExpression.LENGTH, lengthExpression.getText());
		assertTrue (lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}
}