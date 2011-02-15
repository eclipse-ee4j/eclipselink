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
public final class FuncExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName') FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// CollectionExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// FuncExpression - 1
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof FuncExpression);
		FuncExpression funcExpression = (FuncExpression) expression;

		assertTrue(funcExpression.hasLeftParenthesis());
		assertTrue(funcExpression.hasRightParenthesis());

		// CollectionExpression
		expression = funcExpression.getExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression functionItems = (CollectionExpression) expression;

		assertEquals(3, functionItems.childrenSize());

		assertEquals("'NVL'",         functionItems.getChild(0).toParsedText());
		assertEquals("e.firstName",   functionItems.getChild(1).toParsedText());
		assertEquals("'NoFirstName'", functionItems.getChild(2).toParsedText());

		// FuncExpression - 2
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof FuncExpression);
		funcExpression = (FuncExpression) expression;

		assertTrue(funcExpression.hasLeftParenthesis());
		assertTrue(funcExpression.hasRightParenthesis());

		// CollectionExpression
		expression = funcExpression.getExpression();
		assertTrue(expression instanceof CollectionExpression);
		functionItems = (CollectionExpression) expression;

		assertEquals(3, functionItems.childrenSize());

		assertEquals("'NVL'",        functionItems.getChild(0).toParsedText());
		assertEquals("e.lastName",   functionItems.getChild(1).toParsedText());
		assertEquals("'NoLastName'", functionItems.getChild(2).toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT a " +
		               "FROM Asset a, Geography selectedGeography " +
                     "WHERE selectedGeography.id = :id AND " +
                     "      a.id IN (:id_list) AND " +
                     "      FUNC('ST_Intersects', a.geometry, selectedGeography.geometry) = 'TRUE'";

		ExpressionTester selectStatement = selectStatement
		(
			select(variable("a")),
			from("Asset", "a", "Geography", "selectedGeography"),
			where(
						path("selectedGeography.id")
					.equal(
						inputParameter(":id"))
				.and(
						path("a.id").in(inputParameter(":id_list"))
					.and(
							func(string("'ST_Intersects'"),
							     path("a.geometry"),
							     path("selectedGeography.geometry"))
						.equal(
							string("'TRUE'"))
					)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}