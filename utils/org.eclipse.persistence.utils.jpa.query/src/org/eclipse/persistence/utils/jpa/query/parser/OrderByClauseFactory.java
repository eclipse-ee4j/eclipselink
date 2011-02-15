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
 * This {@link OrderByClauseFactory} creates a new {@link OrderByClause}
 * when the portion of the query to parse starts with <b>ORDER BY</b>.
 *
 * @see OrderByClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class OrderByClauseFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link OrderByClauseFactory}.
	 */
	static final String ID = Expression.ORDER_BY;

	/**
	 * Creates a new <code>OrderByClauseFactory</code>.
	 */
	OrderByClauseFactory()
	{
		super(ID, Expression.ORDER_BY);
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
		expression = new OrderByClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}