/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
public final class JPQLExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testGetExpression_1()
	{
		String actualQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(actualQuery);

		Expression expression = jpqlExpression.getExpression(actualQuery, 0);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(actualQuery, 2);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(actualQuery, 4);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectClause);

		expression = jpqlExpression.getExpression(actualQuery, 10);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectClause);

		expression = jpqlExpression.getExpression(actualQuery, 25);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectStatement);

		expression = jpqlExpression.getExpression(actualQuery, 30);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof FromClause);
	}

	@Test
	public void testGetExpression_2()
	{
		String query = "SELECT OBJECT(e), COUNT(DISTINCT e) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 0);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof JPQLExpression);

		expression = jpqlExpression.getExpression(query, 14);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof ObjectExpression);

		// In 'SELECT'
		for (int index = 1; index < 8; index++)
		{
			expression = jpqlExpression.getExpression(query, index);
			assertNotNull(expression);
			assertTrue("The expression was " + expression, expression instanceof SelectClause);
		}

		// In 'OBJECT('
		for (int index = 8; index < 15; index++)
		{
			expression = jpqlExpression.getExpression(query, index);
			assertNotNull(expression);
			assertTrue("The expression was " + expression, expression instanceof ObjectExpression);
		}

		// In 'e' of 'OBJECT(e)'
		expression = jpqlExpression.getExpression(query, 15);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof IdentificationVariable);

		expression = jpqlExpression.getExpression(query, 17);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof CollectionExpression);

		expression = jpqlExpression.getExpression(query, 18);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof CollectionExpression);

		expression = jpqlExpression.getExpression(query, 24);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof CountFunction);

		expression = jpqlExpression.getExpression(query, 35);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof CountFunction);

		expression = jpqlExpression.getExpression(query, 36);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectStatement);
	}

	@Test
	public void testGetExpression_3()
	{
		String query = "SELECT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 8);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof IdentificationVariable);

		expression = jpqlExpression.getExpression(query, 6);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectClause);
	}

	@Test
	public void testGetExpression_4()
	{
		String query = "S";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 1);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof UnknownExpression);
	}

	@Test
	public void testGetExpression_5()
	{
		String query = "SELECT ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		Expression expression = jpqlExpression.getExpression(query, 7);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectClause);
	}

	@Test
	public void testGetExpression_6()
	{
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "HAVING e.department.name LIKE 'Sales%' ";

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);
		int position = query.length();

		Expression expression = jpqlExpression.getExpression(query, position);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof SelectStatement);
	}

	@Test
	public void testGetExpression_7()
	{
		String query = "SELECT SUM(e.name) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);
		int position = "SELECT SUM(e.".length();

		Expression expression = jpqlExpression.getExpression(query, position);
		assertNotNull(expression);
		assertTrue("The expression was " + expression, expression instanceof StateFieldPathExpression);
	}

	@Test
	public void testRepositionCursor_1()
	{
		String actualQuery = "  SELECT  AVG ( mag )    FROM  Magazine   mag";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(actualQuery/*, buildFormatter()*/);

		int position = 0;
		int expectedPosition = 0;
		int newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 2;
		expectedPosition = 0;
		newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 4;
		expectedPosition = 2;
		newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 10;
		expectedPosition = 7;
		newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		position = 31;
		expectedPosition = 21;
		newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_2()
	{
		String actualQuery = "  SELECT  AVG ( mag ) , " + // 23, 16
		                     "          NEW oracle.toplink.test.MyEntity(e)  " + // 70, 59
		                     "FROM  Employee e "; // 87, 68

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(actualQuery/*, buildFormatter()*/);

		// 23 = SELECT AVG(mag)|, ...
		int position = 23;
		int expectedPosition = 16;
		int newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);

		// 78 = ... FROM EM|PLOYEE
		position = 78;
		expectedPosition = 59;
		newPosition = jpqlExpression.repositionCursor(actualQuery, position);
		assertEquals(expectedPosition, newPosition);
	}

	@Test
	public void testRepositionCursor_3()
	{
		String query = "SELECT  e ";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition, actualPosition);

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 3
		expectedPosition = query.length();
		actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}

	@Test
	public void testRepositionCursor_4()
	{
		String query = " SELECT  e   FROM";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);
		int extraWhitespacesCount = 1;

		// Test 1
		int expectedPosition = 0;
		int actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition, actualPosition);

		extraWhitespacesCount = 2;

		// Test 2
		expectedPosition = query.indexOf("e");
		actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		extraWhitespacesCount = 4;

		// Test 3
		expectedPosition = query.indexOf("FROM");
		actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);

		// Test 4
		expectedPosition = query.length();
		actualPosition   = jpqlExpression.repositionCursor(query, expectedPosition);
		assertEquals(expectedPosition - extraWhitespacesCount, actualPosition);
	}
}