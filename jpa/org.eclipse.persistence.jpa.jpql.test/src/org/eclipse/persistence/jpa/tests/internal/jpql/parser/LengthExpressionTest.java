/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class LengthExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE LENGTH(e.firstName)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		// StateFieldPathExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE LENGTH(AVG(e.firstName))";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		// AvgFunction
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.firstName)", avgFunction.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE LENGTH";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertFalse(lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE LENGTH(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertTrue (lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE LENGTH()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertTrue(lengthExpression.hasLeftParenthesis());
		assertTrue(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE LENGTH GROUP BY e.name";
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

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertFalse(lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE LENGTH( GROUP BY e.name";
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

		// LengthExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LengthExpression);
		LengthExpression lengthExpression = (LengthExpression) expression;

		assertTrue (lengthExpression.hasLeftParenthesis());
		assertFalse(lengthExpression.hasRightParenthesis());

		// NullExpression
		expression = lengthExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}
}