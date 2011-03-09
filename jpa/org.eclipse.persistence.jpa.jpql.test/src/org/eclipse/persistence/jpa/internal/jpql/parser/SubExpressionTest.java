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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SubExpressionTest extends AbstractJPQLTest {
	@Test/*(timeout=10000)*/
	public void testBuildExpression_01() {
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (x > 2) AND (AVG (e.name) <> TRIM (e.name))";

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

		// SubExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// ComparisonExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof ComparisonExpression);

		// SubExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		// ComparisonExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		// AvgFunction
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);

		// TrimExpression
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof TrimExpression);
	}

	@Test/*(timeout=10000)*/
	public void testBuildExpression_02() {
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (x > 2) AND ((AVG (e.name) <> TRIM (e.name))";

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

		// SubExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// ComparisonExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof ComparisonExpression);

		// SubExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		assertFalse(subExpression.hasRightParenthesis());

		// SubExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		assertTrue(subExpression.hasRightParenthesis());

		// ComparisonExpression
		expression = subExpression.getExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		// AvgFunction
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);

		// TrimExpression
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof TrimExpression);
	}

	@Test/*(timeout=10000)*/
	public void testBuildExpression_03() {
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE (((e.name LIKE 'Pascal')) AND ((CURRENT_DATE) > 2))";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause — WHERE (((e.name LIKE 'Pascal')) AND ((CURRENT_DATE) > 2))
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubExpression — (((e.name LIKE 'Pascal')) AND ((CURRENT_DATE) > 2))
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		// AndExpression — ((e.name LIKE 'Pascal')) AND ((CURRENT_DATE) > 2)
		expression = subExpression.getExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		// SubExpression — ((e.name LIKE 'Pascal'))
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		// SubExpression — (e.name LIKE 'Pascal')
		expression = subExpression.getExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		// SubExpression — ((CURRENT_DATE) > 2)
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof SubExpression);
		subExpression = (SubExpression) expression;

		// ComparisonExpression — (CURRENT_DATE) > 2
		expression = subExpression.getExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		// SubExpression — (CURRENT_DATE)
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof SubExpression);
	}
}