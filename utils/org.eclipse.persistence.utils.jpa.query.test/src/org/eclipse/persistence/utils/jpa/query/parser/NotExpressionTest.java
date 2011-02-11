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
public final class NotExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE NOT e.adult";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// NotExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof NotExpression);
		NotExpression notExpression = (NotExpression) expression;

		assertEquals("NOT e.adult", notExpression.toParsedText());

		// StateFieldPathExpression
		expression = notExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.adult", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE NOT ((2 + e.age) NOT BETWEEN e.age)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// NotExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof NotExpression);
		NotExpression notExpression = (NotExpression) expression;

		assertEquals("NOT ((2 + e.age) NOT BETWEEN e.age)", notExpression.toParsedText());

		// SubExpression
		expression = notExpression.getExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		assertEquals("((2 + e.age) NOT BETWEEN e.age)", subExpression.toParsedText());

		// BetweenExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		assertEquals("(2 + e.age) NOT BETWEEN e.age", betweenExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE NOT HAVING e.age = 2";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// NotExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof NotExpression);
		NotExpression notExpression = (NotExpression) expression;

		// NullExpression
		expression = notExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE NOT (e.adult > 17)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// NotExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof NotExpression);
		NotExpression notExpression = (NotExpression) expression;

		// SubExpression
		expression = notExpression.getExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		assertEquals("(e.adult > 17)", subExpression.toParsedText());
	}
}