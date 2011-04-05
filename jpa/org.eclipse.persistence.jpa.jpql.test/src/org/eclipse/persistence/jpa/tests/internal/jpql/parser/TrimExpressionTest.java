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

import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class TrimExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e WHERE TRIM(e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertFalse(trimExpression.hasFrom());
		assertFalse(trimExpression.hasTrimCharacter());
		assertFalse(trimExpression.hasSpecification());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertFalse(trimExpression.hasFrom());
		assertFalse(trimExpression.hasTrimCharacter());
		assertTrue (trimExpression.hasSpecification());
		assertSame (TrimExpression.Specification.TRAILING, trimExpression.getSpecification());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM Employee e WHERE TRIM(BOTH e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertFalse(trimExpression.hasFrom());
		assertFalse(trimExpression.hasTrimCharacter());
		assertTrue (trimExpression.hasSpecification());
		assertSame (TrimExpression.Specification.BOTH, trimExpression.getSpecification());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM Employee e WHERE TRIM(LEADING FROM e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertTrue (trimExpression.hasFrom());
		assertFalse(trimExpression.hasTrimCharacter());
		assertTrue (trimExpression.hasSpecification());
		assertSame (TrimExpression.Specification.LEADING, trimExpression.getSpecification());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM Employee e WHERE TRIM(TRAILING FROM e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertTrue (trimExpression.hasFrom());
		assertFalse(trimExpression.hasTrimCharacter());
		assertTrue (trimExpression.hasSpecification());
		assertSame (TrimExpression.Specification.TRAILING, trimExpression.getSpecification());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM Employee e WHERE TRIM('J' FROM e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertTrue  (trimExpression.hasFrom());
		assertTrue  (trimExpression.hasTrimCharacter());
		assertFalse (trimExpression.hasSpecification());
		assertEquals("'J'", trimExpression.getTrimCharacter().toParsedText());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM Employee e WHERE TRIM(BOTH 'J' FROM e.name)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// TrimExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof TrimExpression);
		TrimExpression trimExpression = (TrimExpression) expression;

		assertTrue(trimExpression.hasFrom());
		assertTrue(trimExpression.hasTrimCharacter());
		assertTrue(trimExpression.hasSpecification());
		assertSame(TrimExpression.Specification.BOTH, trimExpression.getSpecification());
		assertEquals("'J'", trimExpression.getTrimCharacter().toParsedText());

		// StateFieldPathExpression
		expression = trimExpression.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}
}