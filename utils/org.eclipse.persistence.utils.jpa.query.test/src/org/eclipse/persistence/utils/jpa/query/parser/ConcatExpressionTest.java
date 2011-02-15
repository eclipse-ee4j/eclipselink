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

import org.eclipse.persistence.utils.jpa.query.parser.JPQLTests.QueryStringFormatter;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class ConcatExpressionTest extends AbstractJPQLTest
{
	private QueryStringFormatter buildQueryStringFormatter_11()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace(",)", ", )");
			}
		};
	}

	private QueryStringFormatter buildQueryStringFormatter_12()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace("(AND", "( AND");
			}
		};
	}

	private QueryStringFormatter buildQueryStringFormatter_13()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				query = query.replace("(AND", "( AND");
				query = query.replace("')", "' )");
				return query;
			}
		};
	}

	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, e.lastName)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue(concatExpression.hasLeftParenthesis());
		assertTrue(concatExpression.hasRightParenthesis());
		assertTrue(concatExpression.hasFirstExpression());
		assertTrue(concatExpression.hasSecondExpression());
		assertTrue(concatExpression.hasComma());

		// First expression: StateFieldPathExpression
		expression = concatExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.toParsedText());

		// Second expression: StateFieldPathExpression
		expression = concatExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.lastName", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(AVG(e.firstName), e.lastName)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue(concatExpression.hasLeftParenthesis());
		assertTrue(concatExpression.hasRightParenthesis());
		assertTrue(concatExpression.hasFirstExpression());
		assertTrue(concatExpression.hasSecondExpression());
		assertTrue(concatExpression.hasComma());

		// First expression: AvgFunction
		expression = concatExpression.getFirstExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.firstName)", avgFunction.toParsedText());

		// Second expression: StateFieldPathExpression
		expression = concatExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.lastName", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertTrue (concatExpression.hasRightParenthesis());
		assertTrue (concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());

		// First expression: StateFieldPathExpression
		expression = concatExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.toParsedText());

		// Second expression: NullExpression
		expression = concatExpression.getSecondExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertTrue (concatExpression.hasRightParenthesis());
		assertTrue (concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());

		// First expression: StateFieldPathExpression
		expression = concatExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.toParsedText());

		// Second expression: NullExpression
		expression = concatExpression.getSecondExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertFalse(concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertFalse(concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertFalse(concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertTrue (concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertFalse(concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertTrue (concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name,";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertTrue (concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name, ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertTrue (concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT(e.name, )";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildQueryStringFormatter_11());

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ConcatExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertTrue (concatExpression.hasRightParenthesis());
		assertTrue (concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertTrue (concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT( AND e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildQueryStringFormatter_12());

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AndExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		// ConcatExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertFalse(concatExpression.hasComma());
	}

	@Test
	public void testBuildExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE CONCAT( AND e.name = 'Pascal' )";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildQueryStringFormatter_13());

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AndExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		// ConcatExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ConcatExpression);
		ConcatExpression concatExpression = (ConcatExpression) expression;

		assertTrue (concatExpression.hasLeftParenthesis());
		assertFalse(concatExpression.hasRightParenthesis());
		assertFalse(concatExpression.hasFirstExpression());
		assertFalse(concatExpression.hasSecondExpression());
		assertFalse(concatExpression.hasComma());
	}
}