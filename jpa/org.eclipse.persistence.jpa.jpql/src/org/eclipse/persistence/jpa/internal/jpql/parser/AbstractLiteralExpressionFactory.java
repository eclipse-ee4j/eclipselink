/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This factory is responsible to return the right literal expression.
 *
 * @see StringLiteral
 * @see InputParameter
 * @see NumericLiteral
 * @see KeywordExpression
 * @see StateFieldPathExpression
 * @see IdentificationVariable
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class AbstractLiteralExpressionFactory extends ExpressionFactory {

	/**
	 * Creates a new <code>AbstractLiteralExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this {@link ExpressionFactory}
	 */
	AbstractLiteralExpressionFactory(String id) {
		super(id);
	}

	/**
	 * Creates the actual {@link AbstractExpression} this factory manages.
	 *
	 * @param parent The parent expression
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The current word to parse
	 * @param expression During the parsing, it is possible the first part of
	 * an expression was parsed which needs to be used as a sub-expression of the newly created
	 * expression
	 * @return A new {@link AbstractExpression} representing the given word
	 */
	abstract AbstractExpression buildExpression(AbstractExpression parent,
	                                            WordParser wordParser,
	                                            String word,
	                                            AbstractExpression expression,
	                                            boolean tolerant);

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant) {

		char character = word.charAt(0);

		// StringLiteral
		if (ExpressionTools.isQuote(character)) {
			expression = new StringLiteral(parent);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// InputParameter
		if (ExpressionTools.isParameter(character)) {
			expression = new InputParameter(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// NumericLiteral
		if (wordParser.startsWithDigit() == Boolean.TRUE) {
			expression = new NumericLiteral(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// StateFieldPathExpression
		if (word.indexOf(AbstractExpression.DOT) > -1) {

			if ((expression != null) && (character == AbstractExpression.DOT)) {
				expression = new StateFieldPathExpression(parent, expression);
			}
			else {
				expression = new StateFieldPathExpression(parent, word);
			}

			expression.parse(wordParser, tolerant);
			return expression;
		}

		return buildExpression(parent, wordParser, word, expression, tolerant);
	}
}