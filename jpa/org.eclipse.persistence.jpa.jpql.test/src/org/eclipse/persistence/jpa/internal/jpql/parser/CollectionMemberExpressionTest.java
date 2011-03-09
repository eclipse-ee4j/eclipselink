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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class CollectionMemberExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e, f FROM Employee e, e.employees f WHERE e.name MEMBER f.offices";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertFalse(collectionMemberExpression.hasNot());
		assertFalse(collectionMemberExpression.hasOf());

		// SimpleSelectStatement
		expression = collectionMemberExpression.getEntityExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// CollectionValuedPathExpression
		expression = collectionMemberExpression.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("f.offices", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e, f FROM Employee e, e.employees f WHERE e.name MEMBER OF e.employees";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertFalse(collectionMemberExpression.hasNot());
		assertTrue (collectionMemberExpression.hasOf());

		// SimpleSelectStatement
		expression = collectionMemberExpression.getEntityExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// CollectionValuedPathExpression
		expression = collectionMemberExpression.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.employees", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e, f FROM Employee e, e.employees f WHERE e.name NOT MEMBER OF e.employees";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertTrue(collectionMemberExpression.hasNot());
		assertTrue(collectionMemberExpression.hasOf());

		// SimpleSelectStatement
		expression = collectionMemberExpression.getEntityExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// CollectionValuedPathExpression
		expression = collectionMemberExpression.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("e.employees", collectionValuedPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE MEMBER";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertFalse(collectionMemberExpression.hasNot());
		assertFalse(collectionMemberExpression.hasOf());
		assertTrue (collectionMemberExpression.getEntityExpression() instanceof NullExpression);
		assertTrue (collectionMemberExpression.getCollectionValuedPathExpression() instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE NOT MEMBER";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertTrue (collectionMemberExpression.hasNot());
		assertFalse(collectionMemberExpression.hasOf());
		assertTrue (collectionMemberExpression.getEntityExpression() instanceof NullExpression);
		assertTrue (collectionMemberExpression.getCollectionValuedPathExpression() instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE MEMBER OF";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertFalse(collectionMemberExpression.hasNot());
		assertTrue (collectionMemberExpression.hasOf());
		assertTrue (collectionMemberExpression.getEntityExpression() instanceof NullExpression);
		assertTrue (collectionMemberExpression.getCollectionValuedPathExpression() instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE NOT MEMBER OF";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// CollectionMemberExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof CollectionMemberExpression);
		CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;

		assertTrue(collectionMemberExpression.hasNot());
		assertTrue(collectionMemberExpression.hasOf());
		assertTrue(collectionMemberExpression.getEntityExpression() instanceof NullExpression);
		assertTrue(collectionMemberExpression.getCollectionValuedPathExpression() instanceof NullExpression);
	}
}