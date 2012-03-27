/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>LIKE</b> condition is used to specify a search for a pattern.
 * <p>
 * The <code>string_expression</code> must have a string value. The <code>pattern_value</code> is a
 * string literal or a string-valued input parameter in which an underscore (_) stands for any
 * single character, a percent (%) character stands for any sequence of characters (including the
 * empty sequence), and all other characters stand for themselves. The optional <code>escape_character</code>
 * is a single-character string literal or a character-valued input parameter (i.e., char or
 * Character) and is used to escape the special meaning of the underscore and percent characters in
 * <code>pattern_value</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>like_expression ::= string_expression [NOT] LIKE pattern_value [ESCAPE escape_character]</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class LikeExpression extends AbstractExpression {

	/**
	 * The {@link Expression} representing the escape character, which is either a single character
	 * or an input parameter.
	 */
	private AbstractExpression escapeCharacter;

	/**
	 * The actual <b>escape</b> identifier found in the string representation of the JPQL query.
	 */
	private String escapeIdentifier;

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
	 * The actual <b>LIKE</b> identifier found in the string representation of the JPQL query.
	 */
	private String likeIdentifier;

	/**
	 * The actual <b>NOT</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

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
	 * @param stringExpression The first part of this expression, which is the string expression
	 */
	public LikeExpression(AbstractExpression parent, AbstractExpression stringExpression) {
		super(parent, LIKE);

		if (stringExpression != null) {
			this.stringExpression = stringExpression;
			this.stringExpression.setParent(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getStringExpression().accept(visitor);
		getPatternValue().accept(visitor);
		getEscapeCharacter().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getStringExpression());
		children.add(getPatternValue());
		children.add(getEscapeCharacter());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// String expression
		if (stringExpression != null) {
			children.add(stringExpression);
		}

		// 'NOT'
		if (hasNot) {
			children.add(buildStringExpression(SPACE));
			children.add(buildStringExpression(NOT));
		}

		children.add(buildStringExpression(SPACE));

		// 'LIKE'
		children.add(buildStringExpression(LIKE));

		if (hasSpaceAfterLike) {
			children.add(buildStringExpression(SPACE));
		}

		// Pattern value
		if (patternValue != null) {
			children.add(patternValue);
		}

		if (hasSpaceAfterPatternValue) {
			children.add(buildStringExpression(SPACE));
		}

		// 'ESCAPE'
		if (hasEscape) {
			children.add(buildStringExpression(ESCAPE));
		}

		if (hasSpaceAfterEscape) {
			children.add(buildStringExpression(SPACE));
		}

		// Escape character
		if (escapeCharacter != null) {
			children.add(escapeCharacter);
		}
	}

	/**
	 * Returns the actual <b>ESCAPE</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>ESCAPE</b> identifier that was actually parsed, or an empty string if it was
	 * not parsed
	 */
	public String getActualEscapeIdentifier() {
		return (escapeIdentifier != null) ? escapeIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual <b>LIKE</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>LIKE</b> identifier that was actually parsed
	 */
	public String getActualLikeIdentifier() {
		return likeIdentifier;
	}

	/**
	 * Returns the actual <b>NOT</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} that represents the escape character, which is either a single
	 * character or an input parameter.
	 *
	 * @return The expression that was parsed representing the escape character
	 */
	public Expression getEscapeCharacter() {
		if (escapeCharacter == null) {
			escapeCharacter = buildNullExpression();
		}
		return escapeCharacter;
	}

	/**
	 * Returns the enum constant that represents the identifier.
	 *
	 * @return Either <b>LIKE</b> or <b>NOT LIKE</b>
	 */
	public String getIdentifier() {
		return hasNot ? NOT_LIKE : LIKE;
	}

	/**
	 * Returns the {@link Expression} that represents the pattern value.
	 *
	 * @return The expression that was parsed representing the pattern value
	 */
	public Expression getPatternValue() {
		if (patternValue == null) {
			patternValue = buildNullExpression();
		}
		return patternValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(LikeExpressionBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the string expression.
	 *
	 * @return The expression that was parsed representing the string expression
	 */
	public Expression getStringExpression() {
		if (stringExpression == null) {
			stringExpression = buildNullExpression();
		}
		return stringExpression;
	}

	/**
	 * Determines whether the identifier <b>ESCAPE</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>ESCAPE</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasEscape() {
		return hasEscape;
	}

	/**
	 * Determines whether the escape character was parsed, which is either a single character or an
	 * input parameter.
	 *
	 * @return <code>true</code> if the escape character was parsed; <code>false</code> otherwise
	 */
	public boolean hasEscapeCharacter() {
		return escapeCharacter != null &&
		      !escapeCharacter.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return hasNot;
	}

	/**
	 * Determines whether the pattern value was parsed.
	 *
	 * @return <code>true</code> if the pattern value was parsed; <code>false</code> otherwise
	 */
	public boolean hasPatternValue() {
		return patternValue != null &&
		      !patternValue.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>ESCAPE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>ESCAPE</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterEscape() {
		return hasSpaceAfterEscape;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>LIKE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>LIKE</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterLike() {
		return hasSpaceAfterLike;
	}

	/**
	 * Determines whether a whitespace was parsed after the pattern value.
	 *
	 * @return <code>true</code> if there was a whitespace after the pattern value; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterPatternValue() {
		return hasSpaceAfterPatternValue;
	}

	/**
	 * Determines whether a whitespace was parsed after the string expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the string expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStringExpression() {
		return hasStringExpression();
	}

	/**
	 * Determines whether the string expression was parsed.
	 *
	 * @return <code>true</code> if the string expression was parsed; <code>false</code> otherwise
	 */
	public boolean hasStringExpression() {
		return stringExpression != null &&
		      !stringExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		char character = word.charAt(0);

		if (getQueryBNF(PatternValueBNF.ID).handleAggregate() &&
		    (character == '+' || character == '-' || character == '*' || character == '/')) {

			return false;
		}

		return super.isParsingComplete(wordParser, word, expression) ||
		       word.equalsIgnoreCase(ESCAPE) ||
		       word.equalsIgnoreCase(AND)    ||
		       word.equalsIgnoreCase(OR)     ||
		       expression != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NOT
		hasNot = wordParser.startsWithIgnoreCase('N');

		if (hasNot) {
			notIdentifier = wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'LIKE'
		likeIdentifier = wordParser.moveForward(LIKE);

		hasSpaceAfterLike = wordParser.skipLeadingWhitespace() > 0;

		// Parse the pattern value
		patternValue = parse(wordParser, PatternValueBNF.ID, tolerant);

		int count = wordParser.skipLeadingWhitespace();

		// Parse 'ESCAPE'
		hasEscape = wordParser.startsWithIdentifier(ESCAPE);

		if (hasEscape) {
			hasSpaceAfterPatternValue = (count > 0);
			count = 0;
			escapeIdentifier = wordParser.moveForward(ESCAPE);
			hasSpaceAfterEscape = wordParser.skipLeadingWhitespace() > 0;
		}
		else if (tolerant) {
			hasSpaceAfterPatternValue = (count > 0);
		}
		else {
			wordParser.moveBackward(count);
			return;
		}

		// Parse escape character
		char character = wordParser.character();

		// Single escape character
		if (character == SINGLE_QUOTE) {
			escapeCharacter = new StringLiteral(this, wordParser.word());
			escapeCharacter.parse(wordParser, tolerant);

			count = 0;
		}
		// Parse input parameter
		else if (character == ':' || character == '?') {
			escapeCharacter = new InputParameter(this, wordParser.word());
			escapeCharacter.parse(wordParser, tolerant);
			count = 0;
		}
		// Parse an invalid expression
		else if (tolerant) {
			escapeCharacter = parse(wordParser, PreLiteralExpressionBNF.ID, tolerant);

			if (!hasEscape && (escapeCharacter == null) && !wordParser.isTail()) {
				hasSpaceAfterPatternValue = false;
				wordParser.moveBackward(count);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// String expression
		if (stringExpression != null) {
			stringExpression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'NOT LIKE' or 'LIKE'
		if (actual) {
			if (hasNot) {
				writer.append(notIdentifier);
				writer.append(SPACE);
			}
			writer.append(likeIdentifier);
		}
		else {
			writer.append(hasNot ? NOT_LIKE : LIKE);
		}

		if (hasSpaceAfterLike) {
			writer.append(SPACE);
		}

		// Pattern value
		if (patternValue != null) {
			patternValue.toParsedText(writer, actual);
		}

		if (hasSpaceAfterPatternValue) {
			writer.append(SPACE);
		}

		// 'ESCAPE'
		if (hasEscape) {
			writer.append(actual ? escapeIdentifier : ESCAPE);
		}

		if (hasSpaceAfterEscape) {
			writer.append(SPACE);
		}

		// Escape character
		if (escapeCharacter != null) {
			escapeCharacter.toParsedText(writer, actual);
		}
	}
}