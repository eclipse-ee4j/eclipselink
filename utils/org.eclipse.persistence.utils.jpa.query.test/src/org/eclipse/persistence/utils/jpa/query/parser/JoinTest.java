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
public final class JoinTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines mag WHERE pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		testExpression(jpqlExpression, Expression.JOIN, false);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag WHERE pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		testExpression(jpqlExpression, Expression.JOIN, true);
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT pub FROM Publisher pub JOIN pub.magazines WHERE pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Publisher", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("pub", identificationVariable.getText());

		// Join
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertFalse(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("pub.magazines", collectionValuedPathExpression.getText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT pub FROM Publisher pub JOIN WHERE pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Publisher", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("pub", identificationVariable.getText());

		// Join
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertFalse(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// StateFieldPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT pub FROM Publisher pub JOIN AS HAVING pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Publisher", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("pub", identificationVariable.getText());

		// Join
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertTrue(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// StateFieldPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT pub FROM Publisher pub JOIN AS mag WHERE pub.revenue > 1000000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Publisher", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("pub", identificationVariable.getText());

		// Join
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertTrue(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// StateFieldPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("mag", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     LEFT JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		testExpression(jpqlExpression, Expression.LEFT_JOIN, false);
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     INNER JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		testExpression(jpqlExpression, Expression.INNER_JOIN, false);
	}

	@Test
	public void testBuildExpression_9() {
		String query = "SELECT pub " +
		               "FROM Publisher pub " +
		               "     LEFT OUTER JOIN pub.magazines mag " +
		               "WHERE pub.revenue > 1000000";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		testExpression(jpqlExpression, Expression.LEFT_OUTER_JOIN, false);
	}

	private void testExpression(JPQLExpression jpqlExpression,
	                            String joinType,
	                            boolean hasAs) {
		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Publisher", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("pub", identificationVariable.getText());

		// Join
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertEquals(hasAs, join.hasAs());
		assertSame(joinType, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("pub.magazines", collectionValuedPathExpression.getText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("mag", identificationVariable.getText());
	}
}