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
 * This expression represents an arithmetic expression, which means the first
 * and second expressions are aggregated with an arithmetic sign.
 *
 * @see AdditionExpression
 * @see DivisionExpression
 * @see MultiplicationExpression
 * @see SubstractionExpression
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class ArithmeticExpression extends CompoundExpression
{
	/**
	 * Creates a new <code>ArithmeticExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The arithmetic sign
	 */
	ArithmeticExpression(AbstractExpression parent, String identifier)
	{
		super(parent, identifier);
	}

	/**
	 * Returns the arithmetic sign this expression is actually representing.
	 *
	 * @return The single character value of the arithmetic sign
	 */
	public final String getArithmeticSign()
	{
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(ArithmeticTermBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final boolean isParsingComplete(WordParser wordParser, String word)
	{
		return word.equalsIgnoreCase(OR)  ||
		       word.equalsIgnoreCase(AND) ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final String parseIdentifier(WordParser wordParser)
	{
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final JPQLQueryBNF rightExpressionBNF()
	{
		return queryBNF(ArithmeticTermBNF.ID);
	}
}