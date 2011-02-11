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
abstract class AbstractLiteralExpressionFactory extends ExpressionFactory
{
	/**
	 * Creates a new <code>AbstractLiteralExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this {@link ExpressionFactory}
	 */
	AbstractLiteralExpressionFactory(String id)
	{
		super(id);
	}

	/**
	 * Creates the actual {@link Expression} this factory manages.
	 *
	 * @param parent The parent expression
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The current word to parse
	 * @param expression During the parsing, it is possible the first part of
	 * an expression was parsed which needs to be used as a sub-expression of the newly created
	 * expression
	 * @return A new {@link Expression} representing a portion or the totality of the given text
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
	                                   boolean tolerant)
	{
		// Empty word, simply return a null expression
		// Check to see if we need to parse something
		if ((word.length() == 0) || ((expression != null) && shouldSkip(expression)))
		{
			return null;
		}

		char character = word.charAt(0);

		// StringLiteral
		if (character == '\'')
		{
			expression = new StringLiteral(parent);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// InputParameter
		if (character == ':' ||
		    character == '?')
		{
			expression = new InputParameter(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// NumericLiteral
		if (wordParser.startsWithDigit() == Boolean.TRUE)
		{
			expression = new NumericLiteral(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		// StateFieldPathExpression
		if (word.indexOf(AbstractExpression.DOT) > -1)
		{
			expression = new StateFieldPathExpression(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		return buildExpression(parent, wordParser, word, expression, tolerant);
	}

	/**
	 * Determines whether the creation of an {@link Expression} should be skipped or not when the
	 * given expression is not <code>null</code>.
	 *
	 * @param expression
	 * @return <code>true</code> if the creation of the {@link Expression} should be skipped;
	 * <code>false</code> to create it
	 */
	abstract boolean shouldSkip(AbstractExpression expression);
}