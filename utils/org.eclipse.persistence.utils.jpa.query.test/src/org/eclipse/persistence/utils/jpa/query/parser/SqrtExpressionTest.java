/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SqrtExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE SQRT(2)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		// NumericLiteral
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("2", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE SQRT(e.age + 100)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		// AdditionExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof AdditionExpression);
		AdditionExpression additionExpression = (AdditionExpression) expression;

		assertEquals("e.age + 100", additionExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE SQRT(e.age + 100 - AVG(e.age))";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		// AdditionExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof AdditionExpression);
		AdditionExpression additionExpression = (AdditionExpression) expression;

		assertEquals("e.age + 100 - AVG(e.age)", additionExpression.toParsedText());

		// Left expression: StateFieldPathExpression
		expression = additionExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// Right expression: SubtractionExpression
		expression = additionExpression.getRightExpression();
		assertTrue(expression instanceof SubtractionExpression);
		SubtractionExpression substractionExpression = (SubtractionExpression) expression;

		assertEquals("100 - AVG(e.age)", substractionExpression.toParsedText());

		// Left expression: NumericLiteral
		expression = substractionExpression.getLeftExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("100", numericLiteral.toParsedText());

		// Right expression: AvgFunction
		expression = substractionExpression.getRightExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE SQRT";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		assertSame (SqrtExpression.SQRT, sqrtExpression.getText());
		assertFalse(sqrtExpression.hasLeftParenthesis());
		assertFalse(sqrtExpression.hasRightParenthesis());

		// NullExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE SQRT(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		assertSame (SqrtExpression.SQRT, sqrtExpression.getText());
		assertTrue (sqrtExpression.hasLeftParenthesis());
		assertFalse(sqrtExpression.hasRightParenthesis());

		// NullExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE SQRT()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		assertSame(SqrtExpression.SQRT, sqrtExpression.getText());
		assertTrue(sqrtExpression.hasLeftParenthesis());
		assertTrue(sqrtExpression.hasRightParenthesis());

		// NullExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE SQRT GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		assertSame (SqrtExpression.SQRT, sqrtExpression.getText());
		assertFalse(sqrtExpression.hasLeftParenthesis());
		assertFalse(sqrtExpression.hasRightParenthesis());

		// NullExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT e FROM Employee e WHERE SQRT( GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertTrue(selectStatement.hasGroupByClause());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SqrtExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SqrtExpression);
		SqrtExpression sqrtExpression = (SqrtExpression) expression;

		assertSame (SqrtExpression.SQRT, sqrtExpression.getText());
		assertTrue (sqrtExpression.hasLeftParenthesis());
		assertFalse(sqrtExpression.hasRightParenthesis());

		// NullExpression
		expression = sqrtExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}
}