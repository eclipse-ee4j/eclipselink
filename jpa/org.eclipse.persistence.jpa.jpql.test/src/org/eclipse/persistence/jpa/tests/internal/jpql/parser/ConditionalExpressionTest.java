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

import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class ConditionalExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE e.name NOT LIKE 'Pascal'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' AND e.name <> e.lastName";
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
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE e.name > 'Pascal' OR e.name <> e.lastName AND e.age = 26";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE " +
		               "e.name > 'Pascal' AND e.manager >= 'code' OR " +
		               "e.name <> e.lastName AND e.age = 26";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_06() {
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE     e.name > 'Pascal' " +
		               "      AND    e.manager >= 'code' " +
		               "          OR " +
		               "             e.age < 21 " +
		               "          OR " +
		               "             e.name <> e.lastName " +
		               "      AND " +
		               "          e.age = 26";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					path("e.name").greaterThan(string("'Pascal'"))
				.and(
						path("e.manager").greaterThanOrEqual(string("'code'"))
				)
				.or(
					path("e.age").lowerThan(numeric(21))
				)
				.or(
						path("e.name").different(path("e.lastName"))
					.and(
						path("e.age").equal(numeric(26))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)/mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_08() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_09() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)+mag.salary";
		query = JPQLQueryBuilder.formatPlusSign(query);
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_10() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)-mag.salary";
		query = JPQLQueryBuilder.formatMinusSign(query);
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubtractionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubtractionExpression);
		SubtractionExpression substractionExpression = (SubtractionExpression) expression;

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
	public void testBuildExpression_11() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age)*mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_12() {
		String query = "SELECT e FROM Employee e WHERE AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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
	public void testBuildExpression_13() {
		String query = "SELECT e FROM Employee e WHERE -AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

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
	public void testBuildExpression_14() {
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

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
	public void testBuildExpression_15() {
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) / -mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// ArithmeticFactor
		expression = divisionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// ArithmeticFactor
		expression = divisionExpression.getRightExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		arithmeticFactor = (ArithmeticFactor) expression;

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_16() {
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) - -mag.salary";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// SubtractionExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof SubtractionExpression);
		SubtractionExpression substractionExpression = (SubtractionExpression) expression;

		// ArithmeticFactor
		expression = substractionExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

		// AvgFuntion
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// ArithmeticFactor
		expression = substractionExpression.getRightExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		arithmeticFactor = (ArithmeticFactor) expression;

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_17() {
		String query = "SELECT e FROM Employee e WHERE +AVG(e.age) = 2 AND -mag.salary";
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

		// LogicalExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		assertSame(ComparisonExpression.EQUAL, comparisonExpression.getComparisonOperator());

		// ArithmeticFactor
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof ArithmeticFactor);
		ArithmeticFactor arithmeticFactor = (ArithmeticFactor) expression;

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

		// StateFieldPathExpression
		expression = arithmeticFactor.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("mag.salary", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_18() {

		String query = "SELECT e FROM Employee e WHERE +(SQRT(e.age) + e.age) >= -21";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					plus(
						sub(
								sqrt(path("e.age"))
							.add(
								path("e.age")
							)
						)
					)
				.greaterThanOrEqual(
					numeric(-21)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}