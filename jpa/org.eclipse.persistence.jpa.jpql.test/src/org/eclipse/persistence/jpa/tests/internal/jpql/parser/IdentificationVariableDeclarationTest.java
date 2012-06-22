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

import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class IdentificationVariableDeclarationTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e, Address AS addr";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionExpression
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// 1. IdentificationVariableDeclaration
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertFalse(rangeVariableDeclaration.hasAs());
		assertEquals("Employee e", rangeVariableDeclaration.toParsedText());

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Employee", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.getText());

		// 2. IdentificationVariableDeclaration
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertTrue(rangeVariableDeclaration.hasAs());
		assertEquals("Address AS addr", rangeVariableDeclaration.toParsedText());

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Address", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("addr", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e JOIN e.magazines AS mags";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// Join
		assertTrue(identificationVariableDeclaration.hasJoins());
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertEquals("JOIN e.magazines AS mags", join.toParsedText());
		assertTrue(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.magazines", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;
		assertEquals("mags", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e JOIN e.magazines mags";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// Join
		assertTrue(identificationVariableDeclaration.hasJoins());
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertEquals("JOIN e.magazines mags", join.toParsedText());
		assertFalse(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.magazines", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;
		assertEquals("mags", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e LEFT JOIN e.magazines mags";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// Join
		assertTrue(identificationVariableDeclaration.hasJoins());
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertEquals("LEFT JOIN e.magazines mags", join.toParsedText());
		assertFalse(join.hasAs());
		assertSame(Expression.LEFT_JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.magazines", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;
		assertEquals("mags", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e JOIN FETCH e.magazines";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// FetchJoin
		assertTrue(identificationVariableDeclaration.hasJoins());
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof JoinFetch);
		JoinFetch fetchJoin = (JoinFetch) expression;

		assertEquals("JOIN FETCH e.magazines", fetchJoin.toParsedText());
		assertSame(Expression.JOIN_FETCH, fetchJoin.getIdentifier());

		// CollectionValuedPathExpression
		expression = fetchJoin.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.magazines", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e JOIN e.magazines mags INNER JOIN e.name AS name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

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

		// CollectionExpression
		assertTrue(identificationVariableDeclaration.hasJoins());
		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof CollectionExpression);

		CollectionExpression collectionExpression = (CollectionExpression) expression;
		assertEquals(2, collectionExpression.childrenSize());

		// Join 1
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertEquals("JOIN e.magazines mags", join.toParsedText());
		assertFalse(join.hasAs());
		assertSame(Expression.JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.magazines", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;
		assertEquals("mags", identificationVariable.getText());

		// Join 2
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof Join);
		join = (Join) expression;

		assertEquals("INNER JOIN e.name AS name", join.toParsedText());
		assertTrue(join.hasAs());
		assertSame(Expression.INNER_JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.name", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;
		assertEquals("name", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "     LEFT OUTER JOIN e.name name, " +
		               "     Address AS addr JOIN FETCH addr.street " +
		               "     INNER JOIN addr.zip AS zip, " +
		               "     Manager m";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionExpression
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(3, collectionExpression.childrenSize());

		// 1. IdentificationVariableDeclaration
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertFalse(rangeVariableDeclaration.hasAs());
		assertEquals("Employee e", rangeVariableDeclaration.toParsedText());

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Employee", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.getText());

		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof Join);
		Join join = (Join) expression;

		assertFalse(join.hasAs());
		assertSame(Expression.LEFT_OUTER_JOIN, join.getIdentifier());

		// CollectionValuedPathExpression
		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.name", collectionValuedPathExpression.toParsedText());

		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("name", identificationVariable.getText());

		// 2. IdentificationVariableDeclaration
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertTrue(rangeVariableDeclaration.hasAs());
		assertEquals("Address AS addr", rangeVariableDeclaration.toParsedText());

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Address", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("addr", identificationVariable.getText());

		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression2 = (CollectionExpression) expression;

		assertEquals(2, collectionExpression2.childrenSize());

		expression = collectionExpression2.getChild(0);
		assertTrue(expression instanceof JoinFetch);
		JoinFetch fetchJoin = (JoinFetch) expression;

		assertSame(Expression.JOIN_FETCH, fetchJoin.getIdentifier());

		expression = fetchJoin.getJoinAssociationPath();
		assertEquals("addr.street", expression.toParsedText());

		expression = collectionExpression2.getChild(1);
		assertTrue(expression instanceof Join);
		join = (Join) expression;
		assertEquals("INNER JOIN addr.zip AS zip", expression.toParsedText());

		assertTrue(join.hasAs());
		assertSame(Expression.INNER_JOIN, join.getIdentifier());

		expression = join.getJoinAssociationPath();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		collectionValuedPathExpression = (CollectionValuedPathExpression) expression;
		assertEquals(2, collectionValuedPathExpression.pathSize());

		expression = join.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;
		assertEquals("zip", identificationVariable.getText());

		// 3. IdentificationVariableDeclaration
		expression = collectionExpression.getChild(2);
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertFalse(rangeVariableDeclaration.hasAs());
		assertEquals("Manager m", rangeVariableDeclaration.toParsedText());

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Manager", abstractSchemaName.getText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("m", identificationVariable.getText());

		expression = identificationVariableDeclaration.getJoins();
		assertTrue(expression instanceof NullExpression);
	}
}