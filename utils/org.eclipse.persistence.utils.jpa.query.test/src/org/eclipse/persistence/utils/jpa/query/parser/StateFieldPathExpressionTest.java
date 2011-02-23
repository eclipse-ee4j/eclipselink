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
public final class StateFieldPathExpressionTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT SUM(e.manager.name) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// SumFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof SumFunction);
		SumFunction sumFunction = (SumFunction) expression;

		// StateFieldPathExpression
		expression = sumFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertFalse (stateFieldPathExpression.endsWithDot());
		assertEquals(3, stateFieldPathExpression.pathSize());
		assertEquals("e",       stateFieldPathExpression.getPath(0));
		assertEquals("manager", stateFieldPathExpression.getPath(1));
		assertEquals("name",    stateFieldPathExpression.getPath(2));

		assertEquals("e.manager.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT SUM(e.manager.) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// SumFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof SumFunction);
		SumFunction sumFunction = (SumFunction) expression;

		// StateFieldPathExpression
		expression = sumFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertTrue  (stateFieldPathExpression.endsWithDot());
		assertEquals(2,            stateFieldPathExpression.pathSize());
		assertEquals("e",          stateFieldPathExpression.getPath(0));
		assertEquals("manager",    stateFieldPathExpression.getPath(1));
		assertEquals("e.manager.", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT c.creditCard.creditCompany.address.city FROM Customer c";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// StateFieldPathExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertFalse (stateFieldPathExpression.endsWithDot());
		assertFalse (stateFieldPathExpression.startsWithDot());
		assertEquals(5,               stateFieldPathExpression.pathSize());
		assertEquals("c",             stateFieldPathExpression.getPath(0));
		assertEquals("creditCard",    stateFieldPathExpression.getPath(1));
		assertEquals("creditCompany", stateFieldPathExpression.getPath(2));
		assertEquals("address",       stateFieldPathExpression.getPath(3));
		assertEquals("city",          stateFieldPathExpression.getPath(4));
		assertEquals("c.creditCard.creditCompany.address.city", stateFieldPathExpression.toParsedText());
	}

//	@Test
//	public void testExpressionFactory_01()
//	{
//		String query = "SELECT e. FROM Employee e";
//		int position = "SELECT e.".length();
//
//		testHasExpressionFactory
//		(
//			query,
//			position,
//			buildExpressionFactoryHelper_Employee(),
//			StateFieldPathExpressionFactory.ID
//		);
//	}
//
//	@Test
//	public void testExpressionFactory_02()
//	{
//		String query = "SELECT m. FROM Manager m";
//		int position = "SELECT m.".length();
//
//		testDoesNotHaveExpressionFactory
//		(
//			query,
//			position,
//			buildExpressionFactoryHelper_Employee(),
//			StateFieldPathExpressionFactory.ID
//		);
//	}
//
//	@Test
//	public void testExpressionFactory_03()
//	{
//		String query = "SELECT m. FROM Manager m";
//		int position = "SELECT m.".length();
//
//		testHasExpressionFactory
//		(
//			query,
//			position,
//			buildExpressionFactoryHelper_Manager(),
//			StateFieldPathExpressionFactory.ID
//		);
//	}
//
//	@Test
//	public void testExpressionFactory_04()
//	{
//		String query = "SELECT  FROM Manager m";
//		int position = "SELECT ".length();
//
//		testDoesNotHaveExpressionFactory
//		(
//			query,
//			position,
//			buildExpressionFactoryHelper_All(),
//			StateFieldPathExpressionFactory.ID
//		);
//	}
}