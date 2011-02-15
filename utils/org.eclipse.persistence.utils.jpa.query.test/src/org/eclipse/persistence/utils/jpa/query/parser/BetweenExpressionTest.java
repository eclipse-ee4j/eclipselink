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
public final class BetweenExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND 40";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertTrue (betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// StateFieldPathExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// Lower Bound: NumericLiteral
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("20", numericLiteral.toParsedText());

		// Upper Bound: NumericLiteral
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NumericLiteral);
		numericLiteral = (NumericLiteral) expression;

		assertEquals("40", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN (SELECT e.age FROM e) AND 40";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertTrue (betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// StateFieldPathExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// Lower Bound: SubExpression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// SimpleSelectStatement
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT e.age FROM e", simpleSelectStatement.toParsedText());

		// Upper Bound: NumericLiteral
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("40", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e, m FROM Employee e, Manager m WHERE e.age BETWEEN (SELECT e.age FROM e) AND (SELECT m.age FROM m)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertTrue (betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// StateFieldPathExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// Lower Bound: SubExpression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// SimpleSelectStatement
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT e.age FROM e", simpleSelectStatement.toParsedText());

		// Upper Bound: IdentificationVariable
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		// IdentificationVariable
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT m.age FROM m", simpleSelectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertFalse(betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertFalse(betweenExpression.hasSpaceAfterBetween());
		assertFalse(betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// StateFieldPathExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NullExpression);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN AND";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertFalse(betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// StateFieldPathExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NullExpression);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE BETWEEN ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertFalse(betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertFalse(betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof NullExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NullExpression);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE BETWEEN 10 AND";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof NullExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NumericLiteral);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE NOT BETWEEN 10 AND 20";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertTrue(betweenExpression.hasNot());
		assertTrue(betweenExpression.hasAnd());
		assertTrue(betweenExpression.hasSpaceAfterAnd());
		assertTrue(betweenExpression.hasSpaceAfterBetween());
		assertTrue(betweenExpression.hasSpaceAfterLowerBound());
		assertSame(BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof NullExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NumericLiteral);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NumericLiteral);
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE e.age NOT BETWEEN AND 20";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertTrue (betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertTrue (betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertFalse(betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NullExpression);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NumericLiteral);
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN ORDER BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		assertTrue(selectStatement.hasOrderByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertFalse(betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertFalse(betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NullExpression);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 10 AND ORDER BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		assertTrue(selectStatement.hasOrderByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertTrue (betweenExpression.hasAnd());
		assertTrue (betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NumericLiteral);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 10 ORDER BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		assertTrue(selectStatement.hasOrderByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertFalse(betweenExpression.hasNot());
		assertFalse(betweenExpression.hasAnd());
		assertFalse(betweenExpression.hasSpaceAfterAnd());
		assertTrue (betweenExpression.hasSpaceAfterBetween());
		assertTrue (betweenExpression.hasSpaceAfterLowerBound());
		assertSame (BetweenExpression.BETWEEN, betweenExpression.getText());

		// Expression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Lower bound expression
		expression = betweenExpression.getLowerBoundExpression();
		assertTrue(expression instanceof NumericLiteral);

		// Upper bound expression
		expression = betweenExpression.getUpperBoundExpression();
		assertTrue(expression instanceof NullExpression);
	}
}