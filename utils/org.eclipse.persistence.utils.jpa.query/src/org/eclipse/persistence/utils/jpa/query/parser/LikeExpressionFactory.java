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

/**
 * This {@link LikeExpressionFactory} creates a new {@link LikeExpression}
 * when the portion of the query to parse starts with <b>LIKE</b> or <b>NOT LIKE</b>.
 *
 * @see LikeExpression
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class LikeExpressionFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link LikeExpressionFactory}.
	 */
	static final String ID = Expression.LIKE;

	/**
	 * Creates a new <code>LikeExpressionFactory</code>.
	 */
	LikeExpressionFactory()
	{
		super(ID, Expression.LIKE,
		          Expression.NOT_LIKE);
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
		expression = new LikeExpression(parent, expression);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}