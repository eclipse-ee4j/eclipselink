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
 * This expression represents a logical expression, which means the first and
 * second expressions are aggregated with either <b>AND</b> or <b>OR</b>.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class LogicalExpression extends CompoundExpression
{
	/**
	 * Creates a new <code>LogicalExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The identifier of this expression
	 */
	LogicalExpression(AbstractExpression parent,
	                  String identifier)
	{
		super(parent, identifier);
	}

	/**
	 * Returns the identifier that is aggregating the two expressions.
	 *
	 * @return Either <b>AND</b> or <b>OR</b>
	 */
	public final String getIdentifier()
	{
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(ConditionalExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word)
	{
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser)
	{
		return getText();
	}
}