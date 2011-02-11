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

/**
 * This {@link IndexExpressionFactory} creates a new {@link IndexExpression}
 * when the portion of the query to parse starts with <b>INDEX</b>.
 *
 * @see IndexExpression
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class IndexExpressionFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link IndexExpressionFactory}.
	 */
	static final String ID = Expression.INDEX;

	/**
	 * Creates a new <code>IndexExpressionFactory</code>.
	 */
	IndexExpressionFactory()
	{
		super(ID, Expression.INDEX);
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
		expression = new IndexExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IJPAVersion getVersion()
	{
		return IJPAVersion.VERSION_2_0;
	}
}