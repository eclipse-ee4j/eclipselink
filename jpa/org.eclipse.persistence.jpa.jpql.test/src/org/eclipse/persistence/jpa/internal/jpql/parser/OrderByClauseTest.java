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
public final class OrderByClauseTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT ORDERe FROM Ordering ORDERe ORDER BY ORDERe.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY ORDERe.name", orderByClause.toParsedText());
	}

	@Test
	public void testBuildExpression_ASC() {
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name ASC", orderByClause.toParsedText());
	}

	@Test
	public void testBuildExpression_Default() {
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name", orderByClause.toParsedText());
	}

	@Test
	public void testBuildExpression_DESC() {
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name DESC";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name DESC", orderByClause.toParsedText());
	}

	@Test
	public void testBuildExpression_MultipleValue() {
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC, f.address DESC, g.phone";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name ASC, f.address DESC, g.phone", orderByClause.toParsedText());
	}
}