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
 * This {@link InternalOrderByItemFactory} creates either a {@link StateFieldPathExpression} or
 * an {@link IdentificationVariable}.
 *
 * @see OrderByItem
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InternalOrderByItemFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this <code>InternalOrderByItemFactory</code>.
	 */
	static final String ID = "internal_orderby_item";

	/**
	 * Creates a new <code>InternalOrderByItemFactory</code>.
	 */
	InternalOrderByItemFactory()
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
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant)
	{
		if (tolerant && AbstractExpression.isIdentifier(word))
		{
			return null;
		}

		if (word.indexOf(AbstractExpression.DOT) > -1)
		{
			expression = new StateFieldPathExpression(parent, word);
			expression.parse(wordParser, tolerant);
		}
		else
		{
			ExpressionFactory factory = AbstractExpression.expressionFactory(PreLiteralExpressionFactory.ID);
			expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
		}

		return expression;
	}
}