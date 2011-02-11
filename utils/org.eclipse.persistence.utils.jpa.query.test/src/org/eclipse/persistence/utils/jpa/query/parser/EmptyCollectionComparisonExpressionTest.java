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
public final class EmptyCollectionComparisonExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS EMPTY";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// EmptyCollectionComparisonExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof EmptyCollectionComparisonExpression);
		EmptyCollectionComparisonExpression comparisonExpression = (EmptyCollectionComparisonExpression) expression;

		assertFalse(comparisonExpression.hasNot());

		// CollectionValuedPathExpression
		expression = comparisonExpression.getExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.name", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IS NOT EMPTY";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// EmptyCollectionComparisonExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof EmptyCollectionComparisonExpression);
		EmptyCollectionComparisonExpression comparisonExpression = (EmptyCollectionComparisonExpression) expression;

		assertTrue(comparisonExpression.hasNot());

		// CollectionValuedPathExpression
		expression = comparisonExpression.getExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.name", collectionValuedPathExpression.toParsedText());
	}
}