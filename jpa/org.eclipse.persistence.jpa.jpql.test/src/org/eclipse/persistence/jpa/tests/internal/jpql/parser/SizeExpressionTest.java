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

import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SizeExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE SIZE(e.age)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		// CollectionValuedPathExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.age", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE SIZE";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		assertFalse(sizeExpression.hasLeftParenthesis());
		assertFalse(sizeExpression.hasRightParenthesis());

		// NullExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE SIZE(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		assertTrue (sizeExpression.hasLeftParenthesis());
		assertFalse(sizeExpression.hasRightParenthesis());

		// NullExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE SIZE()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		assertTrue(sizeExpression.hasLeftParenthesis());
		assertTrue(sizeExpression.hasRightParenthesis());

		// NullExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE SIZE GROUP BY e.name";
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

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		assertFalse(sizeExpression.hasLeftParenthesis());
		assertFalse(sizeExpression.hasRightParenthesis());

		// NullExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE SIZE( GROUP BY e.name";
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

		// SizeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SizeExpression);
		SizeExpression sizeExpression = (SizeExpression) expression;

		assertTrue (sizeExpression.hasLeftParenthesis());
		assertFalse(sizeExpression.hasRightParenthesis());

		// NullExpression
		expression = sizeExpression.getExpression();
		assertTrue(expression instanceof NullExpression);
	}
}