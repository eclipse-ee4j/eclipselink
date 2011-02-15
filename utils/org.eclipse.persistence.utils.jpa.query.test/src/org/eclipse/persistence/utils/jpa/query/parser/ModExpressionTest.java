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
public final class ModExpressionTest extends AbstractJPQLTest
{
	private QueryStringFormatter buildQueryStringFormatter_11()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace("MOD(AND", "MOD( AND");
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
		String query = "SELECT e FROM Employee e WHERE MOD(e.name, e.age)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue(modExpression.hasLeftParenthesis());
		assertTrue(modExpression.hasRightParenthesis());
		assertTrue(modExpression.hasComma());

		// 1. StateFieldPathExpression
		expression = modExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// 2. StateFieldPathExpression
		expression = modExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(AVG(e.name), e.age)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue(modExpression.hasLeftParenthesis());
		assertTrue(modExpression.hasRightParenthesis());
		assertTrue(modExpression.hasComma());

		// 1. AggregateExpression
		expression = modExpression.getFirstExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.name)", avgFunction.toParsedText());

		// 2. StateFieldPathExpression
		expression = modExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE MOD";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertFalse(modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertFalse(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertFalse(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE MOD()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertTrue (modExpression.hasRightParenthesis());
		assertFalse(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertTrue (modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(,";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(,";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue(modExpression.hasLeftParenthesis());
		assertTrue(modExpression.hasRightParenthesis());
		assertTrue(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE MOD AND e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

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

		// ModExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertFalse(modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertFalse(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE MOD( AND e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildQueryStringFormatter_11());

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

		// ModExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertFalse(modExpression.hasComma());
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(, 2 + e.startDate";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());

		// 1. NullExpression
		expression = modExpression.getFirstExpression();
		assertTrue(expression instanceof NullExpression);

		// 2. ArithmeticExpression
		expression = modExpression.getSecondExpression();
		assertTrue(expression instanceof ArithmeticExpression);
		ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;

		assertEquals("2 + e.startDate", arithmeticExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(, e.age ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());

		// 1. NullExpression
		expression = modExpression.getFirstExpression();
		assertTrue(expression instanceof NullExpression);

		// 2. StateFieldPathExpression
		expression = modExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE MOD(, e.age ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ModExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ModExpression);
		ModExpression modExpression = (ModExpression) expression;

		assertTrue (modExpression.hasLeftParenthesis());
		assertFalse(modExpression.hasRightParenthesis());
		assertTrue (modExpression.hasComma());

		// 1. NullExpression
		expression = modExpression.getFirstExpression();
		assertTrue(expression instanceof NullExpression);

		// 2. StateFieldPathExpression
		expression = modExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());
	}
}