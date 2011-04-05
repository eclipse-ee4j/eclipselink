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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * Used in conditional expression to determine whether the result of an expression falls within an
 * inclusive range of values. Numeric, string and date expression can be evaluated in this way.
 *
 * <div nowrap><b>BNF:</b> <code>between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |<br>
 * string_expression [NOT] BETWEEN string_expression AND string_expression |<br>
 * datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression</code></div><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class BetweenExpression extends AbstractExpression {

	/**
	 * The {@link Expression} to be tested for an inclusive range of values.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether the identifier <b>AND</b> was parsed.
	 */
	private boolean hasAnd;

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 */
	private boolean hasNot;

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
	 * Creates a new <code>BetweenExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The {@link Expression} that is tested to be inclusive in a range of values
	 */
	BetweenExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent, BETWEEN);

		if (expression != null) {
			this.expression = expression;
			this.expression.setParent(this);
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
		getExpression().accept(visitor);
		getLowerBoundExpression().accept(visitor);
		getUpperBoundExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
		children.add(getLowerBoundExpression());
		children.add(getUpperBoundExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		// Expression
		if (expression != null) {
			children.add(expression);
		}

		if (hasExpression()) {
			children.add(buildStringExpression(SPACE));
		}

		// 'NOT'
		if (hasNot) {
			children.add(buildStringExpression(NOT));
			children.add(buildStringExpression(SPACE));
		}

		// 'BETWEEN'
		children.add(buildStringExpression(BETWEEN));

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
		if (hasAnd) {
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
	 * Returns the {@link Expression} representing the expression to be tested for a range of values.
	 *
	 * @return The expression that was parsed representing the expression to be tested
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * Returns the identifier for this expression that may include <b>NOT</b> if it was parsed.
	 *
	 * @return Either <b>BETWEEN</b> or <b>NOT BETWEEN</b>
	 */
	public String getIdentifier() {
		return hasNot ? NOT_BETWEEN : BETWEEN;
	}

	/**
	 * Returns the {@link Expression} representing the lower bound expression.
	 *
	 * @return The expression that was parsed representing the lower bound expression
	 */
	public Expression getLowerBoundExpression() {
		if (lowerBoundExpression == null) {
			lowerBoundExpression = buildNullExpression();
		}
		return lowerBoundExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(BetweenExpressionBNF.ID);
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
	public boolean hasAnd() {
		return hasAnd;
	}

	/**
	 * Determines whether the expression before the identifier was parsed.
	 *
	 * @return <code>true</code> if the query has the expression before <b>BETWEEN</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the lower bound expression was parsed.
	 *
	 * @return <code>true</code> if the query has the lower bound expression; <code>false</code>
	 * otherwise
	 */
	public boolean hasLowerBoundExpression() {
		return lowerBoundExpression != null &&
		      !lowerBoundExpression.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return hasNot;
	}

	/**
	 * Determines whether a whitespace was found after <b>AND</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AND</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAnd() {
		return hasSpaceAfterAnd;
	}

	/**
	 * Determines whether a whitespace was found after <b>BETWEEN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>BETWEEN</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterBetween() {
		return hasSpaceAfterBetween;
	}

	/**
	 * Determines whether a whitespace was found after the lower bound expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the lower bound expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterLowerBound() {
		return hasSpaceAfterLowerBound;
	}

	/**
	 * Determines whether the upper bound expression was parsed.
	 *
	 * @return <code>true</code> if the query has the upper bound expression; <code>false</code> otherwise
	 */
	public boolean hasUpperBoundExpression() {
		return upperBoundExpression != null &&
		      !upperBoundExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       word.equalsIgnoreCase(AND)                  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NOT'
		hasNot = wordParser.startsWithIgnoreCase('N');

		// Remove 'NOT'
		if (hasNot) {
			wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'BETWEEN'
		wordParser.moveForward(BETWEEN);

		hasSpaceAfterBetween = (wordParser.skipLeadingWhitespace() > 0);

		// Parse lower bound expression
		lowerBoundExpression = parse(
			wordParser,
			queryBNF(InternalBetweenExpressionBNF.ID),
			tolerant
		);

		if (hasLowerBoundExpression()) {
			hasSpaceAfterLowerBound = (wordParser.skipLeadingWhitespace() > 0);
		}

		// Check for 'AND'
		hasAnd = wordParser.startsWithIdentifier(AND);

		// Remove 'AND'
		if (hasAnd) {
			wordParser.moveForward(AND);
			hasSpaceAfterAnd = (wordParser.skipLeadingWhitespace() > 0);
		}

		// Parse upper bound expression
		upperBoundExpression = parse(
			wordParser,
			queryBNF(InternalBetweenExpressionBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer, boolean includeVirtual) {

		// Expression
		if (expression != null) {
			expression.toParsedText(writer, includeVirtual);
		}

		if (hasExpression()) {
			writer.append(SPACE);
		}

		// 'NOT'
		if (hasNot) {
			writer.append(NOT);
			writer.append(SPACE);
		}

		// 'BETWEEN'
		writer.append(BETWEEN);

		if (hasSpaceAfterBetween) {
			writer.append(SPACE);
		}

		// Lower bound expression
		if (lowerBoundExpression != null) {
			lowerBoundExpression.toParsedText(writer, includeVirtual);
		}

		if (hasSpaceAfterLowerBound) {
			writer.append(SPACE);
		}

		// 'AND'
		if (hasAnd) {
			writer.append(AND);
		}

		if (hasSpaceAfterAnd) {
			writer.append(SPACE);
		}

		// Upper bound expression
		if (upperBoundExpression != null) {
			upperBoundExpression.toParsedText(writer, includeVirtual);
		}
	}
}