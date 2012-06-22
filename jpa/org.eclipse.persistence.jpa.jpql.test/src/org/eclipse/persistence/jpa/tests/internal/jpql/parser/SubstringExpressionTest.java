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

import org.eclipse.persistence.jpa.internal.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SubstringExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, 1)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue(substringExpression.hasLeftParenthesis());
		assertTrue(substringExpression.hasRightParenthesis());
		assertTrue(substringExpression.hasFirstComma());
		assertTrue(substringExpression.hasSecondComma());

		// 1. StateFieldPathExpression
		expression = substringExpression.getFirstExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// 2. NumericLiteral
		expression = substringExpression.getSecondExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("0", numericLiteral.toParsedText());

		// 3. NumericLiteral
		expression = substringExpression.getThirdExpression();
		assertTrue(expression instanceof NumericLiteral);
		numericLiteral = (NumericLiteral) expression;

		assertEquals("1", numericLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(AVG(e.name), e.age, 2 + e.startDate)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		// 1. AvgFunction
		expression = substringExpression.getFirstExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.name)", avgFunction.toParsedText());

		// 2. StateFieldPathExpression
		expression = substringExpression.getSecondExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.age", stateFieldPathExpression.toParsedText());

		// 3. ArithmeticExpression
		expression = substringExpression.getThirdExpression();
		assertTrue(expression instanceof ArithmeticExpression);
		ArithmeticExpression arithmeticExpression = (ArithmeticExpression) expression;

		assertEquals("2 + e.startDate", arithmeticExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertFalse(substringExpression.hasLeftParenthesis());
		assertFalse(substringExpression.hasRightParenthesis());
		assertFalse(substringExpression.hasFirstComma());
		assertFalse(substringExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue (substringExpression.hasLeftParenthesis());
		assertFalse(substringExpression.hasRightParenthesis());
		assertFalse(substringExpression.hasFirstComma());
		assertFalse(substringExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue (substringExpression.hasLeftParenthesis());
		assertTrue (substringExpression.hasRightParenthesis());
		assertFalse(substringExpression.hasFirstComma());
		assertFalse(substringExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue (substringExpression.hasLeftParenthesis());
		assertFalse(substringExpression.hasRightParenthesis());
		assertTrue (substringExpression.hasFirstComma());
		assertFalse(substringExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue (substringExpression.hasLeftParenthesis());
		assertFalse(substringExpression.hasRightParenthesis());
		assertTrue (substringExpression.hasFirstComma());
		assertTrue (substringExpression.hasSecondComma());
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT e FROM Employee e WHERE SUBSTRING(,,)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstringExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstringExpression);
		SubstringExpression substringExpression = (SubstringExpression) expression;

		assertTrue(substringExpression.hasLeftParenthesis());
		assertTrue(substringExpression.hasRightParenthesis());
		assertTrue(substringExpression.hasFirstComma());
		assertTrue(substringExpression.hasSecondComma());
	}
}