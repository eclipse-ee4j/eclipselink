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

import java.util.Collection;
import java.util.List;

/**
 * The <b>LIKE</b> condition is used to specify a search for a pattern.
 * <p>
 * The <code>string_expression</code> must have a string value. The <code>pattern_value</code>
 * is a string literal or a string-valued input parameter in which an underscore
 * (_) stands for any single character, a percent (%) character stands for any
 * sequence of characters (including the empty sequence), and all other
 * characters stand for themselves. The optional <code>escape_character</code>
 * is a single-character string literal or a character-valued input parameter
 * (i.e., char or Character) and is used to escape the special meaning of the
 * underscore and percent characters in <code>pattern_value</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>like_expression ::= string_expression [NOT] LIKE pattern_value [ESCAPE escape_character]</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class LikeExpression extends AbstractExpression
{
	/**
	 * The {@link Expression} representing the escape character, which is either
	 * a single character or an input parameter.
	 */
	private AbstractExpression escapeCharacter;

	/**
	 * Determines whether the identifier <b>ESCAPE</b> was parsed.
	 */
	private boolean hasEscape;

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 */
	private boolean hasNot;

	/**
	 * Determines whether a whitespace was parsed after <b>ESCAPE</b>.
	 */
	private boolean hasSpaceAfterEscape;

	/**
	 * Determines whether a whitespace was parsed after <b>LIKE</b>.
	 */
	private boolean hasSpaceAfterLike;

	/**
	 * Determines whether a whitespace was parsed after the pattern value.
	 */
	private boolean hasSpaceAfterPatternValue;

	/**
	 * The {@link Expression} representing the pattern value.
	 */
	private AbstractExpression patternValue;

	/**
	 * The {@link Expression} representing the string expression.
	 */
	private AbstractExpression stringExpression;

	/**
	 * Creates a new <code>LikeExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param stringExpression The first part of this expression, which is the
	 * string expression
	 */
	LikeExpression(AbstractExpression parent,
	               AbstractExpression stringExpression)
	{
		super(parent, LIKE);

		if (stringExpression != null)
		{
			this.stringExpression = stringExpression;
			this.stringExpression.setParent(this);
		}
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
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getStringExpression());
		children.add(getPatternValue());
		children.add(getEscapeCharacter());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// String expression
		if (stringExpression != null)
		{
			children.add(stringExpression);
		}

		// 'NOT'
		if (hasNot)
		{
			children.add(buildStringExpression(SPACE));
			children.add(buildStringExpression(NOT));
		}

		children.add(buildStringExpression(SPACE));

		// 'LIKE'
		children.add(buildStringExpression(LIKE));

		if (hasSpaceAfterLike)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Pattern value
		if (patternValue != null)
		{
			children.add(patternValue);
		}

		if (hasSpaceAfterPatternValue)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'ESCAPE'
		if (hasEscape)
		{
			children.add(buildStringExpression(ESCAPE));
		}

		if (hasSpaceAfterEscape)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Escape character
		if (escapeCharacter != null)
		{
			children.add(escapeCharacter);
		}
	}

	/**
	 * Returns the {@link Expression} that represents the escape character, which
	 * is either a single character or an input parameter.
	 *
	 * @return The expression that was parsed representing the escape character
	 */
	public Expression getEscapeCharacter()
	{
		if (escapeCharacter == null)
		{
			escapeCharacter = buildNullExpression();
		}

		return escapeCharacter;
	}

	/**
	 * Returns the enum constant that represents the identifier.
	 *
	 * @return Either <b>LIKE</b> or <b>NOT LIKE</b>
	 */
	public String getIdentifier()
	{
		return hasNot ? NOT_LIKE : LIKE;
	}

	/**
	 * Returns the {@link Expression} that represents the pattern value.
	 *
	 * @return The expression that was parsed representing the pattern value
	 */
	public Expression getPatternValue()
	{
		if (patternValue == null)
		{
			patternValue = buildNullExpression();
		}

		return patternValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(LikeExpressionBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the string expression.
	 *
	 * @return The expression that was parsed representing the string expression
	 */
	public Expression getStringExpression()
	{
		if (stringExpression == null)
		{
			stringExpression = buildNullExpression();
		}

		return stringExpression;
	}

	/**
	 * Determines whether the identifier <b>ESCAPE</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>ESCAPE</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasEscape()
	{
		return hasEscape;
	}

	/**
	 * Determines whether the escape character was parsed, which is either a
	 * single character or an input parameter.
	 *
	 * @return <code>true</code> if the escape character was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasEscapeCharacter()
	{
		return escapeCharacter != null &&
		      !escapeCharacter.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot()
	{
		return hasNot;
	}

	/**
	 * Determines whether the pattern value was parsed.
	 *
	 * @return <code>true</code> if the pattern value was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasPatternValue()
	{
		return patternValue != null &&
		      !patternValue.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>ESCAPE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>ESCAPE</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterEscape()
	{
		return hasSpaceAfterEscape;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>LIKE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>LIKE</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterLike()
	{
		return hasSpaceAfterLike;
	}

	/**
	 * Determines whether a whitespace was parsed after the pattern value.
	 *
	 * @return <code>true</code> if there was a whitespace after the pattern
	 * value; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterPatternValue()
	{
		return hasSpaceAfterPatternValue;
	}

	/**
	 * Determines whether a whitespace was parsed after the string expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the string expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStringExpression()
	{
		return hasStringExpression();
	}

	/**
	 * Determines whether the string expression was parsed.
	 *
	 * @return <code>true</code> if the string expression was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasStringExpression()
	{
		return stringExpression != null &&
		      !stringExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'NOT
		hasNot = wordParser.startsWithIgnoreCase('N');

		if (hasNot)
		{
			wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'LIKE'
		wordParser.moveForward(LIKE);

		hasSpaceAfterLike = wordParser.skipLeadingWhitespace() > 0;

		// Parse the pattern value
		patternValue = parse
		(
			wordParser,
			queryBNF(PatternValueBNF.ID),
			tolerant
		);

		int count = wordParser.skipLeadingWhitespace();
		hasSpaceAfterPatternValue = (count > 0);

		// Parse 'ESCAPE'
		hasEscape = wordParser.startsWithIdentifier(ESCAPE);

		if (hasEscape)
		{
			count = 0;
			wordParser.moveForward(ESCAPE);
			hasSpaceAfterEscape = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse escape character
		char character = wordParser.character();

		// Single escape character
		if (character == SINGLE_QUOTE)
		{
			escapeCharacter = new StringLiteral(this);
			escapeCharacter.parse(wordParser, tolerant);

			count = 0;
		}
		// Parse input parameter
		else if (character == ':' || character == '?')
		{
			if (tolerant)
			{
				escapeCharacter = parse
				(
					wordParser,
					queryBNF(InputParameterBNF.ID),
					tolerant
				);
			}
			else
			{
				escapeCharacter = new InputParameter(this, wordParser.word());
				escapeCharacter.parse(wordParser, tolerant);
			}

			count = 0;
		}

		if (count > 0)
		{
			hasSpaceAfterPatternValue = false;
			wordParser.moveBackward(count);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// String expression
		if (stringExpression != null)
		{
			stringExpression.toParsedText(writer);
			writer.append(SPACE);
		}

		// 'NOT LIKE' or 'LIKE'
		writer.append(hasNot ? NOT_LIKE : LIKE);

		if (hasSpaceAfterLike)
		{
			writer.append(SPACE);
		}

		// Pattern value
		if (patternValue != null)
		{
			patternValue.toParsedText(writer);
		}

		if (hasSpaceAfterPatternValue)
		{
			writer.append(SPACE);
		}

		// 'ESCAPE'
		if (hasEscape)
		{
			writer.append(ESCAPE);
		}

		if (hasSpaceAfterEscape)
		{
			writer.append(SPACE);
		}

		// Escape character
		if (escapeCharacter != null)
		{
			escapeCharacter.toParsedText(writer);
		}
	}
}