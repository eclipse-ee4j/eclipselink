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

import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JPA version 2.0.
 */
@SuppressWarnings("nls")
public final class TypeExpressionTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e WHERE TYPE(e) IN :empTypes";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query, IJPAVersion.VERSION_2_0);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// InExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		// TypeExpression
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof TypeExpression);
		TypeExpression entityTypeExpression = (TypeExpression) expression;

		// IdentificationVariable
		expression = entityTypeExpression.getExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals
		(
			"The identification variable should have been parsed",
			"e",
			identificationVariable.getText()
		);
	}
}