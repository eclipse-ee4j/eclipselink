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

/**
 * A <b>WHEN</b> predicate is used to calculate a condition and when it's true, its <b>THEN</b> will
 * be executed.
 *
 * <div nowrap><b>BNF:</b> <code>when_clause ::= WHEN conditional_expression THEN scalar_expression</code><p>
 * or
 * <div nowrap><b>BNF:</b> <code>simple_when_clause ::= WHEN scalar_expression THEN scalar_expression</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhenClause extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>THEN</b>.
	 */
	private boolean hasSpaceAfterThen;

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>WHEN</b>.
	 */
	private boolean hasSpaceAfterWhen;

	/**
	 * Determines whether a whitespace was parsed after the conditional expression.
	 */
	private boolean hasSpaceAfterWhenExpression;

	/**
	 * Determines whether the identifier <b>THEN</b> was parsed.
	 */
	private boolean hasThen;

	/**
	 * The {@link Expression} representing the expression following the identifier
	 * <b>THEN</b>.
	 */
	private AbstractExpression thenExpression;

	/**
	 * The {@link Expression} representing the conditional predicate of the clause.
	 */
	private AbstractExpression whenExpression;

	/**
	 * Creates a new <code>WhenClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	WhenClause(AbstractExpression parent) {
		super(parent, WHEN);
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
		getWhenExpression().accept(visitor);
		getThenExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getWhenExpression());
		children.add(getThenExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		// WHEN
		children.add(buildStringExpression(WHEN));

		if (hasSpaceAfterWhen) {
			children.add(buildStringExpression(SPACE));
		}

		// WHEN Expression
		if (whenExpression != null) {
			children.add(whenExpression);
		}

		if (hasSpaceAfterWhenExpression) {
			children.add(buildStringExpression(SPACE));
		}

		// THEN
		if (hasThen) {
			children.add(buildStringExpression(THEN));
		}

		if (hasSpaceAfterThen) {
			children.add(buildStringExpression(SPACE));
		}

		// THEN expression
		if (thenExpression != null) {
			children.add(thenExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(WhenClauseBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the expression following the identifier <b>THEN</b>.
	 *
	 * @return The expression representing the expression executed if the predicate is true
	 */
	public AbstractExpression getThenExpression() {
		if (thenExpression == null) {
			thenExpression = buildNullExpression();
		}
		return thenExpression;
	}

	/**
	 * Returns the {@link Expression} representing the conditional predicate of the clause.
	 *
	 * @return The expression following the <b>WHEN</b> identifier
	 */
	public AbstractExpression getWhenExpression() {
		if (whenExpression == null) {
			whenExpression = buildNullExpression();
		}

		return whenExpression;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>THEN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>THEN</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterThen() {
		return hasSpaceAfterThen;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>WHEN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>WHEN</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterWhen() {
		return hasSpaceAfterWhen;
	}

	/**
	 * Determines whether a whitespace was parsed after the conditional expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the conditional expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterWhenExpression() {
		return hasSpaceAfterWhenExpression;
	}

	/**
	 * Determines whether the identifier <b>THEN</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>THEN</b> was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasThen() {
		return hasThen;
	}

	/**
	 * Determines whether the <b>THEN</b> expression of the query was parsed.
	 *
	 * @return <code>true</code> the <b>THEN</b> expression was parsed; <code>false</code> if nothing
	 * was parsed
	 */
	public boolean hasThenExpression() {
		return thenExpression != null &&
		      !thenExpression.isNull();
	}

	/**
	 * Determines whether the conditional expression of the query was parsed.
	 *
	 * @return <code>true</code> the conditional expression was parsed; <code>false</code> if nothing
	 * was parsed
	 */
	public boolean hasWhenExpression() {
		return whenExpression != null &&
		      !whenExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(WHEN) ||
		       word.equalsIgnoreCase(THEN) ||
		       word.equalsIgnoreCase(ELSE) ||
		       word.equalsIgnoreCase(END)  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'WHEN'
		wordParser.moveForward(WHEN);

		hasSpaceAfterWhen = wordParser.skipLeadingWhitespace() > 0;

		// Parse the expression
		whenExpression = parse(
			wordParser,
			queryBNF(InternalWhenClauseBNF.ID),
			tolerant
		);

		hasSpaceAfterWhenExpression = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'THEN'
		hasThen = wordParser.startsWithIdentifier(THEN);

		if (hasThen) {
			wordParser.moveForward(THEN);
		}

		hasSpaceAfterThen = wordParser.skipLeadingWhitespace() > 0;

		// Parse the then expression
		thenExpression = parse(
			wordParser,
			queryBNF(ScalarExpressionBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		// 'WHEN'
		writer.append(WHEN);

		if (hasSpaceAfterWhen) {
			writer.append(SPACE);
		}

		// Expression
		if (whenExpression != null) {
			whenExpression.toParsedText(writer);
		}

		if (hasSpaceAfterWhenExpression) {
			writer.append(SPACE);
		}

		// 'THEN'
		if (hasThen) {
			writer.append(THEN);
		}

		if (hasSpaceAfterThen) {
			writer.append(SPACE);
		}

		// Then expression
		if (thenExpression != null) {
			thenExpression.toParsedText(writer);
		}
	}
}