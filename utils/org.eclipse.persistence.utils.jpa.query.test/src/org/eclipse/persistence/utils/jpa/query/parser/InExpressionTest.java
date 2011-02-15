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
public final class InExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.name IN (e.address.street)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertTrue (inExpression.hasLeftParenthesis());
		assertTrue (inExpression.hasRightParenthesis());

		// StateFieldPathExpression
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// StateFieldPathExpression
		expression = inExpression.getInItems();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(3, stateFieldPathExpression.pathSize());
		assertEquals("e.address.street", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e NOT IN(e.address.street)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame(InExpression.IN, inExpression.getText());
		assertTrue(inExpression.hasNot());
		assertTrue(inExpression.hasLeftParenthesis());
		assertTrue(inExpression.hasRightParenthesis());

		// IdentificationVariable
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// StateFieldPathExpression
		expression = inExpression.getInItems();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(3, stateFieldPathExpression.pathSize());
		assertEquals("e.address.street", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e IN (e.address.street, e.name)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertTrue (inExpression.hasLeftParenthesis());
		assertTrue (inExpression.hasRightParenthesis());

		// IdentificationVariable
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// CollectionExpression
		expression = inExpression.getInItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// 1. StateFieldPathExpression
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(3, stateFieldPathExpression.pathSize());
		assertEquals("e.address.street", stateFieldPathExpression.toParsedText());

		// 2. StateFieldPathExpression
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e IN (SELECT m FROM Manager m)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertTrue (inExpression.hasLeftParenthesis());
		assertTrue (inExpression.hasRightParenthesis());

		// IdentificationVariable
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// SimpleSelectStatement
		expression = inExpression.getInItems();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		assertEquals("SELECT m FROM Manager m", simpleSelectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE e IN";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertFalse(inExpression.hasLeftParenthesis());
		assertFalse(inExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE e IN(";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertTrue (inExpression.hasLeftParenthesis());
		assertFalse(inExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE e IN()";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertSame (InExpression.IN, inExpression.getText());
		assertFalse(inExpression.hasNot());
		assertTrue (inExpression.hasLeftParenthesis());
		assertTrue (inExpression.hasRightParenthesis());
	}
}