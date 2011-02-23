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
public final class StringLiteralTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' > 'Pascal'";
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

		// StringLiteral
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertTrue(stringLiteral.hasCloseQuote());
		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertTrue(stringLiteral.hasCloseQuote());
		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' > 'Pascal";
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

		// StringLiteral
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertTrue(stringLiteral.hasCloseQuote());
		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertFalse(stringLiteral.hasCloseQuote());
		assertEquals("'Pascal", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT c FROM Customer C WHERE c.firstName='Bill' AND c.lastName='Burkes'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// ComparisonExpression (left)
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertTrue  (stringLiteral.hasCloseQuote());
		assertEquals("'Bill'", stringLiteral.toParsedText());

		// ComparisonExpression (right)
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertTrue  (stringLiteral.hasCloseQuote());
		assertEquals("'Burkes'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT c FROM Customer C WHERE c.firstName='Bill";
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

		// StringLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertFalse (stringLiteral.hasCloseQuote());
		assertEquals("'Bill", stringLiteral.toParsedText());
	}
}