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
public abstract class FunctionTest extends AbstractJPQLTest
{
	abstract Class<? extends AggregateFunction> functionClass();

	abstract String identifier();

	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public final void testBuildExpression_01()
	{
		String query = String.format("SELECT %s(e) FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(functionClass().isAssignableFrom(expression.getClass()));
		AggregateFunction function = (AggregateFunction) expression;

		assertFalse (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertTrue  (function.hasRightParenthesis());
		assertEquals(identifier() + "(e)", function.toParsedText());
	}

	@Test
	public final void testBuildExpression_02()
	{
		String query = String.format("SELECT %s(DISTINCT e) FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertTrue  (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertTrue  (function.hasRightParenthesis());
		assertEquals(identifier() + "(DISTINCT e)", function.toParsedText());
	}

	@Test
	public final void testBuildExpression_03()
	{
		String query = String.format("SELECT %s FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertFalse (function.hasDistinct());
		assertFalse (function.hasLeftParenthesis());
		assertFalse (function.hasRightParenthesis());
		assertEquals(identifier(), function.toParsedText());
	}

	@Test
	public final void testBuildExpression_04()
	{
		String query = String.format("SELECT %s( FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertFalse (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertFalse (function.hasRightParenthesis());
		assertEquals(identifier() + "(", function.toParsedText());
	}

	@Test
	public final void testBuildExpression_05()
	{
		String query = String.format("SELECT %s() FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertFalse (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertTrue  (function.hasRightParenthesis());
		assertEquals(identifier() + "()", function.toParsedText());
	}

	@Test
	public final void testBuildExpression_06()
	{
		String query = String.format("SELECT %s (DISTINCT) FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertTrue  (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertTrue  (function.hasRightParenthesis());
		assertEquals(identifier() + "(DISTINCT)", function.toParsedText());
	}

	@Test
	public final void testBuildExpression_07()
	{
		String query = String.format("SELECT %s (DISTINCT FROM Employee e", identifier());
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AbstractFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AggregateFunction);
		AggregateFunction function = (AggregateFunction) expression;

		assertTrue  (function.hasDistinct());
		assertTrue  (function.hasLeftParenthesis());
		assertFalse (function.hasRightParenthesis());
		assertEquals(identifier() + "(DISTINCT ", function.toParsedText());
	}
}