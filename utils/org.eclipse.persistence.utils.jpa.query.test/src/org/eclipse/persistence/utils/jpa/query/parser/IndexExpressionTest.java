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
public final class IndexExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01() throws Exception
	{
		String query = "SELECT c FROM CreditCard c WHERE INDEX(c) BETWEEN 0 AND 9";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// BetweenExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof BetweenExpression);
		BetweenExpression betweenExpression = (BetweenExpression) expression;

		// IndexExpression
		expression = betweenExpression.getExpression();
		assertTrue(expression instanceof IndexExpression);
		IndexExpression indexExpression = (IndexExpression) expression;

		// IdentificationVariable
		expression = indexExpression.getExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("c", identificationVariable.getText());
	}
}