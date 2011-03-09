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
public final class UpdateItemTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "UPDATE Employee e SET e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof UpdateItem);
		UpdateItem updateItem = (UpdateItem) expression;

		assertTrue(updateItem.hasEqualSign());
		assertEquals("e.name = 'Pascal'", updateItem.toParsedText());

		// StateFieldPathExpression
		expression = updateItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// StringLiteral
		expression = updateItem.getNewValue();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "UPDATE Employee e SET e.name = 'Pascal', e.manager.salary = 100000";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		// CollectionExpression
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// Item 1: UpdateItem
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof UpdateItem);
		UpdateItem updateItem = (UpdateItem) expression;

		assertTrue(updateItem.hasEqualSign());
		assertEquals("e.name = 'Pascal'", updateItem.toParsedText());

		// StateFieldPathExpression
		expression = updateItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.name", stateFieldPathExpression.toParsedText());

		// StringLiteral
		expression = updateItem.getNewValue();
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'Pascal'", stringLiteral.toParsedText());

		// Item 1: UpdateItem
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof UpdateItem);
		updateItem = (UpdateItem) expression;

		assertTrue(updateItem.hasEqualSign());
		assertEquals("e.manager.salary = 100000", updateItem.toParsedText());

		// StateFieldPathExpression
		expression = updateItem.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.manager.salary", stateFieldPathExpression.toParsedText());

		// NumericLiteral
		expression = updateItem.getNewValue();
		assertTrue(expression instanceof NumericLiteral);
		NumericLiteral numericLiteral = (NumericLiteral) expression;

		assertEquals("100000", numericLiteral.toParsedText());
	}
}