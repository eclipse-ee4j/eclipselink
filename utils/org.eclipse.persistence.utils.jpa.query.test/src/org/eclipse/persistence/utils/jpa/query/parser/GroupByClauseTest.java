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
public final class GroupByClauseTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "SELECT e FROM Employee e GROUP BY e.name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// GroupByClause
		assertTrue(selectStatement.hasGroupByClause());
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
		GroupByClause groupByClause = (GroupByClause) expression;

		// StateFieldPathExpression
		expression = groupByClause.getGroupByItems();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.name", stateFieldPathExpression.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e GROUP BY e.name, e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// GroupByClause
		assertTrue(selectStatement.hasGroupByClause());
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
		GroupByClause groupByClause = (GroupByClause) expression;

		// CollectionExpression
		expression = groupByClause.getGroupByItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// 1. StateFieldPathExpression
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals(2, stateFieldPathExpression.pathSize());
		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// 2. IdentificationVariable
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.getText());
	}
}