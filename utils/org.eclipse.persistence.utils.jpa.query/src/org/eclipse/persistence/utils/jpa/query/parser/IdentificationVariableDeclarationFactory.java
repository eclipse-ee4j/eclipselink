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
 * This {@link IdentificationVariableDeclarationFactory} is meant to handle the
 * parsing of a portion of the query when it's expected to be an identification
 * variable declaration.
 *
 * @see IdentificationVariableDeclaration
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class IdentificationVariableDeclarationFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link IdentificationVariableDeclarationFactory}.
	 */
	static final String ID = "identification-variable-declaration";

	/**
	 * Creates a new <code>IdentificationVariableDeclarationFactory</code>.
	 */
	IdentificationVariableDeclarationFactory()
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
		if (word.equalsIgnoreCase(Expression.IN))
		{
			expression = new CollectionMemberDeclaration(parent);
		}
		else
		{
			expression = new IdentificationVariableDeclaration(parent);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}