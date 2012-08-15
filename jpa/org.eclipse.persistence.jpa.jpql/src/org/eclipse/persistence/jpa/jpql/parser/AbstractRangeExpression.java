/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * This <code>AbstractRangeExpression</code> parses an expression that represents a range.
 *
 * <div nowrap><b>BNF:</b> <code>range_expression ::= BETWEEN expression AND expression</code><p>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public abstract class AbstractRangeExpression extends AbstractExpression {

	/**
	 * The actual <b>AND</b> identifier found in the string representation of the JPQL query.
	 */
	private String andIdentifier;

	/**
	 * The actual <b>BETWEEN</b> found in the string representation of the JPQL query.
	 */
	private String betweenIdentifier;

	/**
	 * Determines whether a whitespace was found after <b>AND</b>.
	 */
	private boolean hasSpaceAfterAnd;

	/**
	 * Determines whether a whitespace was found after <b>BETWEEN</b>.
	 */
	private boolean hasSpaceAfterBetween;

	/**
	 * Determines whether a whitespace was found after the lower bound expression.
	 */
	private boolean hasSpaceAfterLowerBound;

	/**
	 * The {@link Expression} representing the lower bound expression.
	 */
	private AbstractExpression lowerBoundExpression;

	/**
	 * The {@link Expression} representing the upper bound expression.
	 */
	private AbstractExpression upperBoundExpression;

	/**
	 * Creates a new <code>AbstractRangeExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The JPQL identifier for this expression
	 */
	protected AbstractRangeExpression(AbstractExpression parent, String identifier) {
		super(parent, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getLowerBoundExpression().accept(visitor);
		getUpperBoundExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getLowerBoundExpression());
		children.add(getUpperBoundExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// Identifier
		children.add(buildStringExpression(getText()));

		if (hasSpaceAfterBetween) {
			children.add(buildStringExpression(SPACE));
		}

		// Lower bound expression
		if (lowerBoundExpression != null) {
			children.add(lowerBoundExpression);
		}

		if (hasSpaceAfterLowerBound) {
			children.add(buildStringExpression(SPACE));
		}

		// 'AND'
		if (andIdentifier != null) {
			children.add(buildStringExpression(AND));
		}

		if (hasSpaceAfterAnd) {
			children.add(buildStringExpression(SPACE));
		}

		// Upper bound expression
		if (upperBoundExpression != null) {
			children.add(upperBoundExpression);
		}
	}

	/**
	 * Returns the unique identifier of the BNF
	 */
	protected abstract String boundExpressionQueryBNFId();

	/**
	 * Returns the actual <b>AND</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>AND</b> identifier that was actually parsed, or an empty string if it was not parsed
	 */
	public final String getActualAndIdentifier() {
		return (andIdentifier != null) ? andIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual found in the string representation of the JPQL query, which has the actual
	 * case that was used.
	 *
	 * @return The identifier that was actually parsed
	 */
	public final String getActualBetweenIdentifier() {
		return betweenIdentifier;
	}

	/**
	 * Returns the {@link Expression} representing the lower bound expression.
	 *
	 * @return The expression that was parsed representing the lower bound expression
	 */
	public final Expression getLowerBoundExpression() {
		if (lowerBoundExpression == null) {
			lowerBoundExpression = buildNullExpression();
		}
		return lowerBoundExpression;
	}

	/**
	 * Returns the {@link Expression} representing the upper bound expression.
	 *
	 * @return The expression that was parsed representing the upper bound expression
	 */
	public Expression getUpperBoundExpression() {
		if (upperBoundExpression == null) {
			upperBoundExpression = buildNullExpression();
		}
		return upperBoundExpression;
	}

	/**
	 * Determines whether the identifier <b>AND</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>AND</b> was parsed; <code>false</code> otherwise
	 */
	public final boolean hasAnd() {
		return andIdentifier != null;
	}

	/**
	 * Determines whether the identifier <b>BETWEEN</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>BETWEEN</b> was parsed; <code>false</code> otherwise
	 */
	protected boolean hasBetween() {
		return betweenIdentifier != null;
	}

	/**
	 * Determines whether the lower bound expression was parsed.
	 *
	 * @return <code>true</code> if the query has the lower bound expression; <code>false</code>
	 * otherwise
	 */
	public final boolean hasLowerBoundExpression() {
		return lowerBoundExpression != null &&
		      !lowerBoundExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was found after <b>AND</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AND</b>; <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterAnd() {
		return hasSpaceAfterAnd;
	}

	/**
	 * Determines whether a whitespace was found after <b>BETWEEN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after the <b>BETWEEN</b>;
	 * <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterBetween() {
		return hasSpaceAfterBetween;
	}

	/**
	 * Determines whether a whitespace was found after the lower bound expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the lower bound expression;
	 * <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterLowerBound() {
		return hasSpaceAfterLowerBound;
	}

	/**
	 * Determines whether the upper bound expression was parsed.
	 *
	 * @return <code>true</code> if the query has the upper bound expression; <code>false</code> otherwise
	 */
	public final boolean hasUpperBoundExpression() {
		return upperBoundExpression != null &&
		      !upperBoundExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       word.equalsIgnoreCase(AND)                  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse BETWEEN
		if (wordParser.startsWithIdentifier(BETWEEN)) {
			betweenIdentifier = wordParser.moveForward(BETWEEN);
			hasSpaceAfterBetween = (wordParser.skipLeadingWhitespace() > 0);
		}

		// Parse 'x AND y'
		parseRange(wordParser, tolerant);
	}

	/**
	 * Parses the portion of the JPQL query that represents the range .
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 */
	protected void parseRange(WordParser wordParser, boolean tolerant) {

		// Parse lower bound expression
		lowerBoundExpression = parse(wordParser, boundExpressionQueryBNFId(), tolerant);

		if (hasLowerBoundExpression()) {
			hasSpaceAfterLowerBound = (wordParser.skipLeadingWhitespace() > 0);
		}

		// Parse 'AND'
		if (wordParser.startsWithIdentifier(AND)) {
			andIdentifier = wordParser.moveForward(AND);
			hasSpaceAfterAnd = (wordParser.skipLeadingWhitespace() > 0);
		}

		// Parse upper bound expression
		upperBoundExpression = parse(wordParser, boundExpressionQueryBNFId(), tolerant);
	}

	/**
	 * Generates a string representation of this the portion that represents the range.
	 *
	 * @param writer The buffer used to append this {@link Expression}'s string representation
	 * @param actual Determines whether the string representation should represent what was parsed,
	 * i.e. include any "virtual" whitespace (such as ending whitespace) and the actual case of the
	 * JPQL identifiers
	 */
	protected void toParsedRangeText(StringBuilder writer, boolean actual) {

		// Lower bound expression
		if (lowerBoundExpression != null) {
			lowerBoundExpression.toParsedText(writer, actual);
		}

		if (hasSpaceAfterLowerBound) {
			writer.append(SPACE);
		}

		// 'AND'
		if (andIdentifier != null) {
			writer.append(actual ? andIdentifier : AND);
		}

		if (hasSpaceAfterAnd) {
			writer.append(SPACE);
		}

		// Upper bound expression
		if (upperBoundExpression != null) {
			upperBoundExpression.toParsedText(writer, actual);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Identifier
		if (betweenIdentifier != null) {
			writer.append(actual ? betweenIdentifier : BETWEEN);
		}

		if (hasSpaceAfterBetween) {
			writer.append(SPACE);
		}

		// Range 'x AND y'
		toParsedRangeText(writer, actual);
	}
}