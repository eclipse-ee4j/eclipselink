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
public final class ConditionalExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE 'Pascal'";
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

		assertTrue(likeExpression.hasNot());

		// StateFieldPathExpression
		expression = likeExpression.getStringExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// StringLiteral
		expression = likeExpression.getPatternValue();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' AND e.name <> e.lastName";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

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

		// 1. ComparisonExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name > 'Pascal'", comparisonExpression.toParsedText());

		// 2. ComparisonExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name <> e.lastName", comparisonExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03()
	{
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// OrExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof OrExpression);
		OrExpression orExpression = (OrExpression) expression;

		// 1. ComparisonExpression
		expression = orExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name > 'Pascal'", comparisonExpression.toParsedText());

		// 2. ComparisonExpression
		expression = orExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name <> e.lastName", comparisonExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_04()
	{
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName AND e.age = 26";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// OrExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof OrExpression);
		OrExpression orExpression = (OrExpression) expression;

		// 1. ComparisonExpression
		expression = orExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name > 'Pascal'", comparisonExpression.toParsedText());

		// 2. AndExpression
		expression = orExpression.getRightExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		// 2.1. ComparisonExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name <> e.lastName", comparisonExpression.toParsedText());

		// 2.2. ComparisonExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.age = 26", comparisonExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_05()
	{
		String query = "SELECT e FROM Employee e WHERE " +
		               "e.name > 'Pascal' AND e.manager >= 'code' OR " +
		               "e.name <> e.lastName AND e.age = 26";

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// OrExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof OrExpression);
		OrExpression orExpression = (OrExpression) expression;

		// 1. AndExpression
		expression = orExpression.getLeftExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		// 1.1 ComparisonExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name > 'Pascal'", comparisonExpression.toParsedText());

		// 1.2 ComparisonExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.manager >= 'code'", comparisonExpression.toParsedText());

		// 2. AndExpression
		expression = orExpression.getRightExpression();
		assertTrue(expression instanceof AndExpression);
		andExpression = (AndExpression) expression;

		// 2.1. ComparisonExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.name <> e.lastName", comparisonExpression.toParsedText());

		// 2.2. ComparisonExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ComparisonExpression);
		comparisonExpression = (ComparisonExpression) expression;

		assertEquals("e.age = 26", comparisonExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_06()
	{
		String query = "SELECT e FROM Employee e WHERE " +
		               "e.name > 'Pascal' AND e.manager >= 'code' OR " +
		               "e.age < 21 OR " +
		               "e.name <> e.lastName AND e.age = 26";

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// OrExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof OrExpression);
		OrExpression orExpression = (OrExpression) expression;

		// AndExpression - e.name > 'Pascal' AND e.manager >= 'code'
		expression = orExpression.getLeftExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

			// ComparisonExpression - e.name > 'Pascal'
			expression = andExpression.getLeftExpression();
			assertTrue(expression instanceof ComparisonExpression);
			ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

			assertEquals("e.name > 'Pascal'", comparisonExpression.toParsedText());

			// ComparisonExpression - e.manager >= 'code'
			expression = andExpression.getRightExpression();
			assertTrue(expression instanceof ComparisonExpression);
			comparisonExpression = (ComparisonExpression) expression;

			assertEquals("e.manager >= 'code'", comparisonExpression.toParsedText());

		// OrExpression - e.age < 21 OR e.name <> e.lastName AND e.age = 26
		expression = orExpression.getRightExpression();
		assertTrue(expression instanceof OrExpression);
		orExpression = (OrExpression) expression;

			// ComparisonExpression - e.age < 21
			expression = orExpression.getLeftExpression();
			assertTrue(expression instanceof ComparisonExpression);
			comparisonExpression = (ComparisonExpression) expression;

			assertEquals("e.age < 21", comparisonExpression.toParsedText());

			// AndExpression - e.name <> e.lastName AND e.age = 26
			expression = orExpression.getRightExpression();
			assertTrue(expression instanceof AndExpression);
			andExpression = (AndExpression) expression;

				// ComparisonExpression - e.name <> e.lastName
				expression = andExpression.getLeftExpression();
				assertTrue(expression instanceof ComparisonExpression);
				comparisonExpression = (ComparisonExpression) expression;

				assertEquals("e.name <> e.lastName", comparisonExpression.toParsedText());

				// ComparisonExpression - e.age = 26
				expression = andExpression.getRightExpression();
				assertTrue(expression instanceof ComparisonExpression);
				comparisonExpression = (ComparisonExpression) expression;

				assertEquals("e.age = 26", comparisonExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_07()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)/mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// DivisionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof DivisionExpression);
		DivisionExpression divisionExpression = (DivisionExpression) expression;

		assertSame(DivisionExpression.DIVISION, divisionExpression.getText());

		// AvgFuntion
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_08()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// MultiplicationExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof MultiplicationExpression);
		MultiplicationExpression multiplicationExpression = (MultiplicationExpression) expression;

		assertSame(MultiplicationExpression.MULTIPLICATION, multiplicationExpression.getText());

		// AvgFuntion
		expression = multiplicationExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = multiplicationExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_09()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)+mag.salary";
		query = JPQLQueries.formatPlusSign(query);
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// AdditionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AdditionExpression);
		AdditionExpression additionExpression = (AdditionExpression) expression;

		assertSame(AdditionExpression.PLUS, additionExpression.getText());

		// AvgFuntion
		expression = additionExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = additionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_10()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)-mag.salary";
		query = JPQLQueries.formatMinusSign(query);
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstractionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstractionExpression);
		SubstractionExpression substractionExpression = (SubstractionExpression) expression;

		assertSame(SubstractionExpression.MINUS, substractionExpression.getText());

		// AvgFuntion
		expression = substractionExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = substractionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_11()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// MultiplicationExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof MultiplicationExpression);
		MultiplicationExpression multiplicationExpression = (MultiplicationExpression) expression;

		assertSame(MultiplicationExpression.MULTIPLICATION, multiplicationExpression.getText());

		// AvgFuntion
		expression = multiplicationExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = multiplicationExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_12()
	{
		String query = "SELECT e FROM Employee e WHERE AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// DivisionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof DivisionExpression);
		DivisionExpression divisionExpression = (DivisionExpression) expression;

		assertSame(DivisionExpression.DIVISION, divisionExpression.getText());

		// AvgFuntion
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_13()
	{
		String query = "SELECT e FROM Employee e WHERE -AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// DivisionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof DivisionExpression);
		DivisionExpression divisionExpression = (DivisionExpression) expression;

		assertSame(DivisionExpression.DIVISION, divisionExpression.getText());

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(SubstractionExpression.MINUS, arithmeticFactor.getText());

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_14()
	{
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// DivisionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof DivisionExpression);
		DivisionExpression divisionExpression = (DivisionExpression) expression;

		assertSame(DivisionExpression.DIVISION, divisionExpression.getText());

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(AdditionExpression.PLUS, arithmeticFactor.getText());

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// StateFieldPathExpression
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_15()
	{
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / -mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// DivisionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof DivisionExpression);
		DivisionExpression divisionExpression = (DivisionExpression) expression;

		assertSame(DivisionExpression.DIVISION, divisionExpression.getText());

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(AdditionExpression.PLUS, arithmeticFactor.getText());

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// ArithmeticFactor
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(SubstractionExpression.MINUS, arithmeticFactor.getText());

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_16()
	{
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) - -mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubstractionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubstractionExpression);
		SubstractionExpression substractionExpression = (SubstractionExpression) expression;

		assertSame(SubstractionExpression.MINUS, substractionExpression.getText());

		// ArithmeticFactor
		expression = substractionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(AdditionExpression.PLUS, arithmeticFactor.getText());

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// ArithmeticFactor
		expression = substractionExpression.getRightExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(SubstractionExpression.MINUS, arithmeticFactor.getText());

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_17()
	{
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) = 2 AND -mag.salary";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

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

		// LogicalExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertSame(ComparisonExpression.EQUAL, comparisonExpression.getComparisonOperator());

		// ArithmeticFactor
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(AdditionExpression.PLUS, arithmeticFactor.getText());

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("2", numericLiteral.toParsedText());

		// ArithmeticFactor
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(SubstractionExpression.MINUS, arithmeticFactor.getText());

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_18()
	{
		String query = "SELECT e FROM Employee e WHERE +(SQRT(e.age) + e.age) >= -21";
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

		assertSame(ComparisonExpression.GREATER_THAN_OR_EQUAL, comparisonExpression.getComparisonOperator());

		// ArithmeticFactor
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		assertEquals(AdditionExpression.PLUS, arithmeticFactor.getText());

		// SubExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof SubExpression);
		SubExpression subExpression = (SubExpression) expression;

		assertEquals("(SQRT(e.age) + e.age)", subExpression.toParsedText());

		// NumericLiteral
		expression = comparisonExpression.getRightExpression();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("-21", numericLiteral.toParsedText());
	}
}