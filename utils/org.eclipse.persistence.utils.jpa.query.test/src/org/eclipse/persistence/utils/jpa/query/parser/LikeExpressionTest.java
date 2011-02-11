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
public final class LikeExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertFalse(likeExpression.hasEscape());
		assertFalse(likeExpression.hasEscapeCharacter());
		assertFalse(likeExpression.hasNot());

		// Escape character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof NullExpression);

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertFalse(likeExpression.hasEscape());
		assertFalse(likeExpression.hasEscapeCharacter());
		assertTrue (likeExpression.hasNot());

		// Escape Character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof NullExpression);

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' LIKE 'Pascal' ESCAPE 'p'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertTrue (likeExpression.hasEscape());
		assertTrue (likeExpression.hasEscapeCharacter());
		assertFalse(likeExpression.hasNot());

		// Escape Character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'p'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE 'p'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertTrue (likeExpression.hasEscape());
		assertTrue(likeExpression.hasEscapeCharacter());
		assertTrue (likeExpression.hasNot());

		// Escape Character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'p'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' ESCAPE";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertTrue (likeExpression.hasEscape());
		assertFalse(likeExpression.hasEscapeCharacter());
		assertTrue (likeExpression.hasNot());

		// Escape Character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof NullExpression);

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE 'Pascal''s code' NOT LIKE 'Pascal' 'p'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// LikeExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof LikeExpression);
		LikeExpression likeExpression = (LikeExpression) expression;

		assertFalse(likeExpression.hasEscape());
		assertTrue (likeExpression.hasEscapeCharacter());
		assertTrue (likeExpression.hasNot());

		// Escape Character
		expression = likeExpression.getEscapeCharacter();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'p'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal''s code'", stringLiteral.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}
}