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
 * This factory creates an {@link EntityTypeLiteral}, which wraps an entity name.
 *
 * @see EntityTypeLiteral
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class EntityTypeLiteralFactory extends AbstractLiteralExpressionFactory
{
	/**
	 * The unique identifier for this {@link EntityTypeLiteralFactory}.
	 */
	static final String ID = "entity_type_literal";

	/**
	 * Creates a new <code>EntityTypeLiteralFactory</code>.
	 */
	EntityTypeLiteralFactory()
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
		expression = new EntityTypeLiteral(parent, word);
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