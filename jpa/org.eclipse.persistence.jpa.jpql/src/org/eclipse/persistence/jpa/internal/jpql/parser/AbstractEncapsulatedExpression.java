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

import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * This expression handles parsing the identifier followed by an expression encapsulated within
 * parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p>
 *
 * @version 2.3
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
	 * Creates a new <code>AbstractEncapsulatedExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	AbstractEncapsulatedExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addOrderedChildrenTo(List<StringExpression> children) {

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
	abstract void addOrderedEncapsulatedExpressionTo(List<StringExpression> children);

	boolean areLogicalIdentifiersSupported() {
		return false;
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
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

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
	final void parse(WordParser wordParser, boolean tolerant) {

		// Parse the identifier
		String identifier = parseIdentifier(wordParser);
		wordParser.moveForward(identifier);
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
	abstract void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant);

	/**
	 * Parses the identifier of this expression.
	 *
	 * @param text The text to parse, which starts with the identifier
	 * @return The identifier for this expression
	 */
	abstract String parseIdentifier(WordParser wordParser);

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void toParsedText(StringBuilder writer, boolean includeVirtual) {

		// Identifier
		writer.append(getText());

		// '('
		if (hasLeftParenthesis) {
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterIdentifier) {
			writer.append(SPACE);
		}

		// Encapsulated expression
		toParsedTextEncapsulatedExpression(writer, includeVirtual);

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
	 * @param includeVirtual Determines whether to include any characters that are considered
	 * virtual, i.e. that was parsed when the query is incomplete and is needed for functionality
	 * like content assist
	 */
	abstract void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean includeVirtual);
}