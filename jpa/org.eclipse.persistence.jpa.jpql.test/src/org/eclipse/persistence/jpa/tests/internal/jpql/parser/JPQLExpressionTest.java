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

import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UnknownExpression;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class JPQLExpressionTest extends AbstractJPQLTest {

	@Test
	public void testGetExpression_1() {

		String actualQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery);

		Expression expression = jpqlExpression.getExpression(actualQuery, 0);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(actualQuery, 2);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(actualQuery, 4);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectClause);

		expression = jpqlExpression.getExpression(actualQuery, 10);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectClause);

		expression = jpqlExpression.getExpression(actualQuery, 25);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectStatement);

		expression = jpqlExpression.getExpression(actualQuery, 30);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof FromClause);
	}

	@Test
	public void testGetExpression_2() {

		String query = "SELECT OBJECT(e), COUNT(DISTINCT e) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 0);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(query, 14);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof ObjectExpression);

		// In 'SELECT'
		for (int index = 1; index < 8; index++) {
			expression = jpqlExpression.getExpression(query, index);
			assertNotNull(expression);
			assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectClause);
		}

		// In 'OBJECT('
		for (int index = 8; index < 15; index++) {
			expression = jpqlExpression.getExpression(query, index);
			assertNotNull(expression);
			assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof ObjectExpression);
		}

		// In 'e' of 'OBJECT(e)'
		expression = jpqlExpression.getExpression(query, 15);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof IdentificationVariable);

		expression = jpqlExpression.getExpression(query, 17);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof CollectionExpression);

		expression = jpqlExpression.getExpression(query, 18);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof CollectionExpression);

		expression = jpqlExpression.getExpression(query, 24);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof CountFunction);

		expression = jpqlExpression.getExpression(query, 35);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof CountFunction);

		expression = jpqlExpression.getExpression(query, 36);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectStatement);
	}

	@Test
	public void testGetExpression_3() {

		String query = "SELECT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 8);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof IdentificationVariable);

		expression = jpqlExpression.getExpression(query, 6);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectClause);
	}

	@Test
	public void testGetExpression_4() {

		String query = "S";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 1);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof UnknownExpression);
	}

	@Test
	public void testGetExpression_5() {

		String query = "SELECT ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 7);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof SelectClause);
	}

	@Test
	public void testGetExpression_6() {

		String query = "SELECT e " +
		               "FROM Employee e " +
		               "HAVING e.department.name LIKE 'Sales%' ";

		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int position = query.length();

		Expression expression = jpqlExpression.getExpression(query, position);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof LikeExpression);
	}

	@Test
	public void testGetExpression_7() {

		String query = "SELECT SUM(e.name) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);
		int position = "SELECT SUM(e.".length();

		Expression expression = jpqlExpression.getExpression(query, position);
		assertNotNull(expression);
		assertTrue("The expression was " + expression.getClass().getSimpleName(), expression instanceof StateFieldPathExpression);
	}
}