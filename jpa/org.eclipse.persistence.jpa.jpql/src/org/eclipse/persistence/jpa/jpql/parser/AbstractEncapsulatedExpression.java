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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression handles parsing the identifier followed by an expression encapsulated within
 * parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractEncapsulatedExpression extends AbstractExpression {

	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasLeftParenthesis;

	/**
	 * Flag used to determine if the opening parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * Special flag used to separate the identifier with the encapsulated expression when the left
	 * parenthesis is missing.
	 */
	private boolean hasSpaceAfterIdentifier;

	/**
	 * The actual identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * Creates a new <code>AbstractEncapsulatedExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected AbstractEncapsulatedExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void addOrderedChildrenTo(List<StringExpression> children) {

		// Identifier
		children.add(buildStringExpression(getText()));

		// '('
		if (hasLeftParenthesis) {
			children.add(buildStringExpression(LEFT_PARENTHESIS));
		}
		else if (hasSpaceAfterIdentifier) {
			children.add(buildStringExpression(SPACE));
		}

		addOrderedEncapsulatedExpressionTo(children);

		// ')'
		if (hasRightParenthesis) {
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * Adds the {@link StringExpression StringExpressions} representing the encapsulated {@link
	 * Expression}.
	 *
	 * @param children The list used to store the string representation of the encapsulated {@link
	 * Expression}
	 */
	protected abstract void addOrderedEncapsulatedExpressionTo(List<StringExpression> children);

	protected boolean areLogicalIdentifiersSupported() {
		return false;
	}

	/**
	 * Returns the actual identifier found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The JPQL identifier that was actually parsed
	 */
	public final String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the JPQL identifier of this expression.
	 *
	 * @return The JPQL identifier
	 */
	public final String getIdentifier() {
		return getText();
	}

	/**
	 * Determines whether something was parsed after the left parenthesis.
	 *
	 * @return <code>true</code> if something was parsed; <code>false</code> otherwise
	 */
	public abstract boolean hasEncapsulatedExpression();

	/**
	 * Determines whether the open parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the open parenthesis was present in the string version of the
	 * query; <code>false</code> otherwise
	 */
	public final boolean hasLeftParenthesis() {
		return hasLeftParenthesis;
	}

	/**
	 * Determines whether the close parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the close parenthesis was present in the string version of the
	 * query; <code>false</code> otherwise
	 */
	public final boolean hasRightParenthesis() {
		return hasRightParenthesis;
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier rather than the left
	 * parenthesis. This can happen in incomplete query of this form: <b>ABS 4 + 5)</b>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the identifier; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterIdentifier() {
		return hasSpaceAfterIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		if (wordParser.startsWith(RIGHT_PARENTHESIS) ||
		    word.equalsIgnoreCase(WHEN)              ||
		    word.equalsIgnoreCase(SET)               ||
		    word.equalsIgnoreCase(AS)                ||
		    super.isParsingComplete(wordParser, word, expression)) {

			return true;
		}

		if (areLogicalIdentifiersSupported()) {
			return false;
		}

		// This check for compound functions, such as AND, OR, <, <=, =, >=, >, BETWEEN
		return word.equalsIgnoreCase(AND)          ||
		       word.equalsIgnoreCase(OR)           ||
		       word.equalsIgnoreCase(BETWEEN)      ||
		       word.equalsIgnoreCase(NOT_BETWEEN)  ||
		       wordParser.startsWith(LOWER_THAN)   ||
		       wordParser.startsWith(GREATER_THAN) ||
		       wordParser.startsWith(EQUAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void parse(WordParser wordParser, boolean tolerant) {

		// Parse the identifier
		String identifier = parseIdentifier(wordParser);
		this.identifier = wordParser.moveForward(identifier);
		setText(identifier);

		int count = wordParser.skipLeadingWhitespace();

		// Parse '('
		hasLeftParenthesis = wordParser.startsWith(LEFT_PARENTHESIS);

		if (hasLeftParenthesis) {
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse the expression
		parseEncapsulatedExpression(wordParser, tolerant);

		if (hasEncapsulatedExpression()) {
			// When having incomplete query of this form: ABS 4 + 5),
			// a whitespace is required
			hasSpaceAfterIdentifier = !hasLeftParenthesis && (count > 0);
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse ')'
		hasRightParenthesis = wordParser.startsWith(RIGHT_PARENTHESIS);

		if (hasRightParenthesis) {
			wordParser.moveForward(1);
		}
		else {
			wordParser.moveBackward(count);
		}
	}

	/**
	 * Parses the encapsulated expression by starting at the current position, which is part of the
	 * given {@link WordParser}.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 */
	protected abstract void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant);

	/**
	 * Parses the identifier of this expression.
	 *
	 * @param text The text to parse, which starts with the identifier
	 * @return The identifier for this expression
	 */
	protected abstract String parseIdentifier(WordParser wordParser);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void toParsedText(StringBuilder writer, boolean actual) {

		// Identifier
		writer.append(actual ? identifier : getText());

		// '('
		if (hasLeftParenthesis) {
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterIdentifier) {
			writer.append(SPACE);
		}

		// Encapsulated expression
		toParsedTextEncapsulatedExpression(writer, actual);

		// ')'
		if (hasRightParenthesis) {
			writer.append(RIGHT_PARENTHESIS);
		}
	}

	/**
	 * Generates a string representation of the encapsulated {@link Expression}.
	 *
	 * @param writer The buffer used to append the encapsulated {@link Expression}'s string
	 * representation
	 * @param actual Determines whether to include any characters that are considered
	 * virtual, i.e. that was parsed when the query is incomplete and is needed for functionality
	 * like content assist
	 */
	protected abstract void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual);
}