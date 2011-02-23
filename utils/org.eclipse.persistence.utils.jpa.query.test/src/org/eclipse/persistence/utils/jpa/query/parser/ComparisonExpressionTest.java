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
public final class ComparisonExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE e.age >= 21";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		assertSame(ComparisonExpression.GREATER_THAN_OR_EQUAL, comparisonExpression.getComparisonOperator());

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("21", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE e.age = 21";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		assertSame(ComparisonExpression.EQUAL, comparisonExpression.getComparisonOperator());

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("21", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE e.age < 21";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		assertSame(ComparisonExpression.LOWER_THAN, comparisonExpression.getComparisonOperator());

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("21", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE e.age <= 21";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		assertSame(ComparisonExpression.LOWER_THAN_OR_EQUAL, comparisonExpression.getComparisonOperator());

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("21", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE e.age <> 21";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		assertSame(ComparisonExpression.DIFFERENT, comparisonExpression.getComparisonOperator());

		// SingleValuedPathExpressionFactory
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("21", numericLiteral.toParsedText());
	}
}