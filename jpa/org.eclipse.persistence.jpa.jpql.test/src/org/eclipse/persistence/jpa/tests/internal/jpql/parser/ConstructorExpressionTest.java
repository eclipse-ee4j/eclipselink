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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class ConstructorExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals(ConstructorExpressionTest.class.getName(), constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());

		// ConstructorItem
		expression = constructorExpression.getConstructorItems();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT NEW " + ConstructorExpressionTest.class.getName() + "(e, COUNT(DISTINCT e.name)) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals(ConstructorExpressionTest.class.getName(), constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());

		// ConstructorItem
		expression = constructorExpression.getConstructorItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());
		assertTrue(collectionExpression.getChild(0) instanceof IdentificationVariable);
		assertTrue(collectionExpression.getChild(1) instanceof CountFunction);

		IdentificationVariable identificationVariable = (IdentificationVariable) collectionExpression.getChild(0);
		assertEquals("e", identificationVariable.getText());

		CountFunction countFunction = (CountFunction) collectionExpression.getChild(1);
		assertTrue(countFunction.hasDistinct());

		expression = countFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;
		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT NEW";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertTrue  (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertFalse (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT NEW ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertTrue  (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertFalse (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT NEW From Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		assertEquals("FROM Employee e", expression.toParsedText());

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (constructorExpression.hasConstructorItems());
		assertFalse (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT NEW(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertTrue  (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT NEW(,";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT NEW()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertTrue  (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());
	}

	@Test
	public void testBuildExpression_09() {
		String query = "SELECT NEW(,)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());
		assertEquals(",", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_10() {
		String query = "SELECT NEW(e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
		assertEquals("e.name", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_11() {
		String query = "SELECT NEW(e.name,";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
		assertEquals("e.name,", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_12() {
		String query = "SELECT NEW(e.name, ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertFalse (constructorExpression.hasRightParenthesis());
		assertEquals("e.name, ", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_13() {
		String query = "SELECT NEW(e.name,)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());
		assertEquals("e.name,", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_14() {
		String query = "SELECT NEW(e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());
		assertEquals("e.name", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_15() {
		String query = "SELECT NEW(e.name) From Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());
		assertEquals("e.name", ((AbstractExpression) constructorExpression.getConstructorItems()).toParsedText());
	}

	@Test
	public void testBuildExpression_16() {
		String query = "SELECT NEW(AVG(e.name)) From Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ConstructorExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ConstructorExpression);
		ConstructorExpression constructorExpression = (ConstructorExpression) expression;

		assertEquals("", constructorExpression.getClassName());
		assertFalse (((AbstractExpression) constructorExpression.getConstructorItems()).isNull());
		assertTrue  (constructorExpression.hasLeftParenthesis());
		assertTrue  (constructorExpression.hasRightParenthesis());

		// AvgFunction
		expression = constructorExpression.getConstructorItems();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("e.name", avgFunction.getExpression().toParsedText());
	}
}