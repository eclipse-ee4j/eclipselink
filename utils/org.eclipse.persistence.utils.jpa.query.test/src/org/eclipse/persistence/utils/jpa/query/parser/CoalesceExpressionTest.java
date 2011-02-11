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
public final class CoalesceExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT COALESCE(e.age, e.name) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// CoalesceExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CoalesceExpression);
		CoalesceExpression coalesceExpression = (CoalesceExpression) expression;

		// CollectionExpression
		expression = coalesceExpression.getExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());
		assertTrue  (collectionExpression.getChild(0) instanceof StateFieldPathExpression);
		assertTrue  (collectionExpression.getChild(1) instanceof StateFieldPathExpression);
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) + 20";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ArithmeticExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ArithmeticExpression);
		ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;

		// CoalesceExpression
		expression = arithmeticExpression.getLeftExpression();
		assertTrue(expression instanceof CoalesceExpression);
		CoalesceExpression coalesceExpression = (CoalesceExpression) expression;

		// CollectionExpression
		expression = coalesceExpression.getExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());
		assertTrue  (collectionExpression.getChild(0) instanceof StateFieldPathExpression);
		assertTrue  (collectionExpression.getChild(1) instanceof NumericLiteral);
	}
}