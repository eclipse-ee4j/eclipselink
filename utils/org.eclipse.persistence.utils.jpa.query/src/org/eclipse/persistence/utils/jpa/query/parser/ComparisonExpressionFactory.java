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

/**
 * This {@link ComparisonExpressionFactory} creates a new {@link ComparisonExpression}
 * when the portion of the query to parse starts with <b><</b>, <b>></b>, <b><></b>,
 * <b><=</b>, <b>>=</b> or <b>=</b>.
 *
 * @see ComparisonExpression
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ComparisonExpressionFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link ComparisonExpressionFactory}.
	 */
	static final String ID = "comparison";

	/**
	 * Creates a new <code>ComparisonExpressionFactory</code>.
	 */
	ComparisonExpressionFactory()
	{
		super(ID, Expression.DIFFERENT,
		          Expression.EQUAL,
		          Expression.GREATER_THAN,
		          Expression.GREATER_THAN_OR_EQUAL,
		          Expression.LOWER_THAN,
		          Expression.LOWER_THAN_OR_EQUAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant)
	{
		ComparisonExpression comparisonExpression = new ComparisonExpression(parent);
		comparisonExpression.parse(wordParser, tolerant);

		if (expression != null)
		{
			comparisonExpression.setLeftExpression(expression);
		}

		return comparisonExpression;
	}
}