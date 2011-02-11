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
 * This {@link DeleteClauseFactory} creates a new {@link DeleteClause}
 * when the portion of the query to parse starts with <b>DELETE FROM</b>.
 *
 * @see DeleteClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class DeleteClauseFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link DeleteClause}.
	 */
	static final String ID = Expression.DELETE;

	/**
	 * Creates a new <code>DeleteClauseFactory</code>.
	 */
	DeleteClauseFactory()
	{
		super(ID, Expression.DELETE);
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
		expression = new DeleteClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}