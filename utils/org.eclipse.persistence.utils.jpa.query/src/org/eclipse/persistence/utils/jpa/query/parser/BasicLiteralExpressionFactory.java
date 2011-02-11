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
 * This {@link LiteralExpressionFactory} is responsible to return the right
 * literal expression.
 *
 * @see StringLiteral
 * @see InputParameter
 * @see NumericLiteral
 * @see KeywordExpression
 * @see StateFieldPathExpression
 * @see IdentificationVariable
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class BasicLiteralExpressionFactory extends AbstractLiteralExpressionFactory
{
	/**
	 * The unique identifier of this {@link BasicLiteralExpressionFactory}.
	 */
	static final String ID = "basic_literal";

	/**
	 * Creates a new <code>LiteralExpressionFactory</code>.
	 */
	BasicLiteralExpressionFactory()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   AbstractExpression expression,
	                                   boolean tolerant)
	{
		expression = new IdentificationVariable(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean shouldSkip(AbstractExpression expression)
	{
		return false;
	}
}