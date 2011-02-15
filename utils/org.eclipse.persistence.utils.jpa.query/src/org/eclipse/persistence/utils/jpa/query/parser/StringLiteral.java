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

import java.util.List;

/**
 * A string literal is enclosed in single quotes. For example: 'literal'. A
 * string literal that includes a single quote is represented by two single
 * quotes. For example: 'literal''s'. String literals in queries, like Java
 * String literals, use unicode character encoding.Approximate literals support
 * the use Java floating point literal syntax as well as SQL approximate numeric
 * literal syntax. Enum literals support the use of Java enum literal syntax.
 * The enum class name must be specified. Appropriate suffixes may be used to
 * indicate the specific type of a numeric literal in accordance with the Java
 * Language Specification. The boolean literals are <code>TRUE</code> and
 * <code>FALSE</code>. Although predefined reserved literals appear in upper
 * case, they are case insensitive.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class StringLiteral extends AbstractExpression
{
	/**
	 * Creates a new <code>StringLiteral</code>.
	 *
	 * @param parent The parent of this expression
	 */
	StringLiteral(AbstractExpression parent)
	{
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(StringLiteralBNF.ID);
	}

	/**
	 * Determines whether the closing quote was present or not.
	 *
	 * @return <code>true</code> if the literal is ended by a single quote;
	 * <code>false</code> otherwise
	 */
	public boolean hasCloseQuote()
	{
		String text = getText();
		int length = text.length();
		return (length > 1) && text.charAt(length - 1) == SINGLE_QUOTE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse the literal
		String literal = parseLiteral(wordParser);
		wordParser.moveForward(literal);
		setText(literal);
	}

	/**
	 * Retrieves the first word from the given text starting at the specified
	 * position.
	 *
	 * @param text The text from which the first word will be retrieved
	 * @param position The position of the cursor where to start retreiving the
	 * word
	 * @return The first word contained in the text, if none could be found, then
	 * an empty string is returned
	 */
	private String parseLiteral(WordParser wordParser)
	{
		int endIndex = wordParser.position() + 1;

		for (int length = wordParser.length(); endIndex < length; endIndex++)
		{
			char character = wordParser.character(endIndex);

			if (character == SINGLE_QUOTE)
			{
				endIndex++;

				// The single quote is escaped by '
				if (endIndex < length &&
				    wordParser.character(endIndex) == SINGLE_QUOTE)
				{
					continue;
				}

				// Reached the end of the string literal
				break;
			}
		}

		return wordParser.substring(wordParser.position(), endIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		writer.append(getText());
	}
}