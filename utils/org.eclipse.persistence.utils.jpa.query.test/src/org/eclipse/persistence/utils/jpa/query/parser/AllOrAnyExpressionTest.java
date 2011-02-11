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
public final class AllOrAnyExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_All_1()
	{
		String query = "SELECT e FROM Employee e WHERE ALL (SELECT m FROM Manager m)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// SimpleSelectStatement
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT m FROM Manager m", simpleSelectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_All_2()
	{
		String query = "SELECT e FROM Employee e WHERE ALL";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_All_3()
	{
		String query = "SELECT e FROM Employee e WHERE ALL(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_All_4()
	{
		String query = "SELECT e FROM Employee e WHERE ALL()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_All_5()
	{
		String query = "SELECT e FROM Employee e WHERE ALL GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_All_6()
	{
		String query = "SELECT e FROM Employee e WHERE ALL( GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ALL, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Any_1()
	{
		String query = "SELECT e FROM Employee e WHERE ANY (SELECT m FROM Manager m)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// SimpleSelectStatement
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT m FROM Manager m", simpleSelectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_Any_2()
	{
		String query = "SELECT e FROM Employee e WHERE ANY";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Any_3()
	{
		String query = "SELECT e FROM Employee e WHERE ANY(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Any_4()
	{
		String query = "SELECT e FROM Employee e WHERE ANY()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Any_5()
	{
		String query = "SELECT e FROM Employee e WHERE ANY GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Any_6()
	{
		String query = "SELECT e FROM Employee e WHERE ANY( GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.ANY, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Some_1()
	{
		String query = "SELECT e FROM Employee e WHERE SOME (SELECT m FROM Manager m)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// SimpleSelectStatement
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT m FROM Manager m", simpleSelectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_Some_2()
	{
		String query = "SELECT e FROM Employee e WHERE SOME";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Some_3()
	{
		String query = "SELECT e FROM Employee e WHERE SOME(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Some_4()
	{
		String query = "SELECT e FROM Employee e WHERE SOME()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame(AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertTrue(allOrAnyExpression.hasLeftParenthesis());
		assertTrue(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Some_5()
	{
		String query = "SELECT e FROM Employee e WHERE SOME GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertFalse(allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_Some_6()
	{
		String query = "SELECT e FROM Employee e WHERE SOME( GROUP BY e.name";
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

		// AllOrAnyExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AllOrAnyExpression);
		AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;

		assertSame (AllOrAnyExpression.SOME, allOrAnyExpression.getIdentifier());
		assertTrue (allOrAnyExpression.hasLeftParenthesis());
		assertFalse(allOrAnyExpression.hasRightParenthesis());

		// NullExpression
		expression = allOrAnyExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}
}