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
 * This {@link GroupByItemFactory} is responsible to return the right expression
 * and to support invalid expression as well.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class GroupByItemFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link GroupByItemFactory}.
	 */
	static final String ID = "groupby_item";

	/**
	 * Creates a new <code>GroupByItemFactory</code>.
	 */
	GroupByItemFactory()
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
		// StateFieldPathExpression
		if (word.indexOf(AbstractExpression.DOT) > -1)
		{
			expression = new StateFieldPathExpression(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// IdentificationVariable or any text
		expression = new IdentificationVariable(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}