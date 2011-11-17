/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression represents an arithmetic expression, which means the first and second expressions
 * are aggregated with an arithmetic sign.
 *
 * @see AdditionExpression
 * @see DivisionExpression
 * @see MultiplicationExpression
 * @see SubtractionExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class ArithmeticExpression extends CompoundExpression {

	/**
	 * Creates a new <code>ArithmeticExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The arithmetic sign
	 */
	protected ArithmeticExpression(AbstractExpression parent, String identifier) {
		super(parent, identifier);
	}

	/**
	 * Returns the arithmetic sign this expression is actually representing.
	 *
	 * @return The single character value of the arithmetic sign
	 */
	public final String getArithmeticSign() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ArithmeticTermBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		char character = word.charAt(0);

		// A comparison symbol needs to continue the parsing
		if (character == '=' || character == '<' || character == '>') {
			return true;
		}

		if (character == '+' || character == '-') {
			return false;
		}

		return (expression != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final String parseIdentifier(WordParser wordParser) {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final JPQLQueryBNF rightExpressionBNF() {
		return getQueryBNF(ArithmeticTermBNF.ID);
	}
}