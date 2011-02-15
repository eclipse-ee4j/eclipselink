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
public final class SimpleSelectStatementTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE (SELECT e.age FROM Employee e) > 10";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ComparisonExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertSame(ComparisonExpression.GREATER_THAN, comparisonExpression.getComparisonOperator());

		// Left expression: SubExpression
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// SimpleSelectStatement
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT e.age FROM Employee e", simpleSelectStatement.toParsedText());

		// SimpleSelectClause
		expression = simpleSelectStatement.getSelectClause();
		assertTrue(expression instanceof SimpleSelectClause);
		SimpleSelectClause simpleSelectClause = (SimpleSelectClause) expression;

		assertEquals("SELECT e.age", simpleSelectClause.toParsedText());

		// SimpleFromClause
		expression = simpleSelectStatement.getFromClause();
		assertTrue(expression instanceof SimpleFromClause);
		SimpleFromClause simpleFromClause = (SimpleFromClause) expression;

		assertEquals("FROM Employee e", simpleFromClause.toParsedText());

		// IdentificationVariable
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("10", numericLiteral.toParsedText());
	}
}