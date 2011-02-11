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

import org.eclipse.persistence.utils.jpa.query.parser.JPQLTests.QueryStringFormatter;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class LocateExpressionTest extends AbstractJPQLTest
{
	private QueryStringFormatter buildFormatter_1()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.replace("LOCATE(AND", "LOCATE( AND");
			}
		};
	}

	private QueryStringFormatter buildFormatter_2()
	{
		return new QueryStringFormatter()
		{
			@Override
			public String format(String query)
			{
				return query.substring(0, query.length() - 1);
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
		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertTrue (locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());

		// 1. StateFieldPathExpression
		expression = locateExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// 2. StateFieldPathExpression
		expression = locateExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// 3. NullExpression
		expression = locateExpression.getThirdExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(AVG(e.name), e.age, 2 + e.startDate)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue(locateExpression.hasLeftParenthesis());
		assertTrue(locateExpression.hasRightParenthesis());
		assertTrue(locateExpression.hasFirstComma());
		assertTrue(locateExpression.hasSecondComma());

		// 1. AvgFunction
		expression = locateExpression.getFirstExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.name)", avgFunction.toParsedText());

		// 2. StateFieldPathExpression
		expression = locateExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// 3. ArithmeticExpression
		expression = locateExpression.getThirdExpression();
		assertTrue(expression instanceof ArithmeticExpression);
		ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;

		assertEquals("2 + e.startDate", arithmeticExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertFalse(locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertFalse(locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertFalse(locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertTrue (locateExpression.hasRightParenthesis());
		assertFalse(locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertTrue (locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(,";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(,,";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertTrue (locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(,,)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue(locateExpression.hasLeftParenthesis());
		assertTrue(locateExpression.hasRightParenthesis());
		assertTrue(locateExpression.hasFirstComma());
		assertTrue(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE AND e.name = 'Pascal'";
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

		// LocateExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertFalse(locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertFalse(locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE( AND e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildFormatter_1());

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

		// LocateExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertFalse(locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(, , 2 + e.startDate";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertTrue (locateExpression.hasSecondComma());
		assertTrue (locateExpression.hasSpaceAfterFirstComma());
		assertTrue (locateExpression.hasSpaceAfterSecondComma());

		// 1. NullExpression
		assertFalse(locateExpression.hasFirstExpression());

		// 2. NullExpression
		assertFalse(locateExpression.hasSecondExpression());

		// 3. SimpleArithmeticExpression
		expression = locateExpression.getThirdExpression();
		assertTrue(expression instanceof ArithmeticExpression);
		ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;

		assertEquals("2 + e.startDate", arithmeticExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age, ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertTrue (locateExpression.hasSecondComma());

		// 1. NullExpression
		expression = locateExpression.getFirstExpression();
		assertTrue(expression instanceof NullExpression);

		// 2. StateFieldPathExpression
		expression = locateExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// 3. NullExpression
		expression = locateExpression.getThirdExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, buildFormatter_2());

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LocateExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LocateExpression);
		LocateExpression locateExpression = (LocateExpression) expression;

		assertTrue (locateExpression.hasLeftParenthesis());
		assertFalse(locateExpression.hasRightParenthesis());
		assertTrue (locateExpression.hasFirstComma());
		assertFalse(locateExpression.hasSecondComma());

		// 1. NullExpression
		expression = locateExpression.getFirstExpression();
		assertTrue(expression instanceof NullExpression);

		// 2. StateFieldPathExpression
		expression = locateExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// 3. NullExpression
		expression = locateExpression.getThirdExpression();
		assertTrue(expression instanceof NullExpression);
	}
}