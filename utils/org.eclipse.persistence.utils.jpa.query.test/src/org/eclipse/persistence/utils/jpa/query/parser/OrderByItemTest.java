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
public final class OrderByItemTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_1()
	{
		String query = "SELECT e FROM Employee e ORDER BY DESC";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("e")),
			from("Employee", "e"),
			nullExpression(),
			nullExpression(),
			nullExpression(),
			orderBy(orderByItemDesc(nullExpression()))
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_ASC()
	{
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name ASC", orderByClause.toParsedText());

		// OrderByItem
		expression = orderByClause.getOrderByItems();
		assertTrue(expression instanceof OrderByItem);
		OrderByItem orderByItem = (OrderByItem) expression;
		assertSame(OrderByItem.Ordering.ASC, orderByItem.getOrdering());

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		assertEquals("e.name", expression.toParsedText());
	}

	@Test
	public void testBuildExpression_Default()
	{
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name", orderByClause.toParsedText());

		// OrderByItem
		expression = orderByClause.getOrderByItems();
		assertTrue(expression instanceof OrderByItem);
		OrderByItem orderByItem = (OrderByItem) expression;
		assertSame(OrderByItem.Ordering.DEFAULT, orderByItem.getOrdering());

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		assertEquals("e.name", expression.toParsedText());
	}

	@Test
	public void testBuildExpression_DESC()
	{
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name DESC";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name DESC", orderByClause.toParsedText());

		// OrderByItem
		expression = orderByClause.getOrderByItems();
		assertTrue(expression instanceof OrderByItem);
		OrderByItem orderByItem = (OrderByItem) expression;
		assertSame(OrderByItem.Ordering.DESC, orderByItem.getOrdering());

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		assertEquals("e.name", expression.toParsedText());
	}

	@Test
	public void testBuildExpression_MultipleValue()
	{
		String query = "SELECT e, f, g FROM Employee e, Manager g ORDER BY e.name ASC, f.address DESC, g.phone";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// OrderByClause
		expression = selectStatement.getOrderByClause();
		assertTrue(expression instanceof OrderByClause);
		OrderByClause orderByClause = (OrderByClause) expression;
		assertEquals("ORDER BY e.name ASC, f.address DESC, g.phone", orderByClause.toParsedText());

		// Collection of order by items
		expression = orderByClause.getOrderByItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;
		assertEquals(3, collectionExpression.childrenSize());

		// OrderByItem 1
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof OrderByItem);
		OrderByItem orderByItem = (OrderByItem) expression;

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		assertEquals("e.name", expression.toParsedText());
		assertSame(OrderByItem.Ordering.ASC, orderByItem.getOrdering());

		// OrderByItem 2
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof OrderByItem);
		orderByItem = (OrderByItem) expression;

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		assertEquals("f.address", expression.toParsedText());
		assertSame(OrderByItem.Ordering.DESC, orderByItem.getOrdering());

		// OrderByItem 3
		expression = collectionExpression.getChild(2);
		assertTrue(expression instanceof OrderByItem);
		orderByItem = (OrderByItem) expression;

		// StateFieldPathExpression
		expression = orderByItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		assertEquals("g.phone", expression.toParsedText());
		assertSame(OrderByItem.Ordering.DEFAULT, orderByItem.getOrdering());
	}
}