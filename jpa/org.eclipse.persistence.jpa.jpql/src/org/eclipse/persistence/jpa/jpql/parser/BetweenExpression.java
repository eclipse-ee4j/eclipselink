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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Used in conditional expression to determine whether the result of an expression falls within an
 * inclusive range of values. Numeric, string and date expression can be evaluated in this way.
 *
 * <div nowrap><b>BNF:</b> <code>between_expression ::= arithmetic_expression [NOT] BETWEEN arithmetic_expression AND arithmetic_expression |<br>
 *                                                      string_expression [NOT] BETWEEN string_expression AND string_expression |<br>
 *                                                      datetime_expression [NOT] BETWEEN datetime_expression AND datetime_expression</code></div><p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class BetweenExpression extends AbstractRangeExpression {

	/**
	 * The {@link Expression} to be tested for an inclusive range of values.
	 */
	private AbstractExpression expression;

	/**
	 * The actual <b>NOT</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

	/**
	 * Creates a new <code>BetweenExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The {@link Expression} that is tested to be inclusive in a range of values
	 */
	public BetweenExpression(AbstractExpression parent, AbstractExpression expression) {
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
	@Override
	public void acceptChildren(ExpressionVisitor visitor) {
		getExpression().accept(visitor);
		super.acceptChildren(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
		super.addChildrenTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// Expression
		if (hasExpression()) {
			children.add(expression);
			children.add(buildStringExpression(SPACE));
		}

		// 'NOT'
		if (notIdentifier != null) {
			children.add(buildStringExpression(NOT));
			children.add(buildStringExpression(SPACE));
		}

		// BETWEEN x AND y
		super.addOrderedChildrenTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String boundExpressionQueryBNFId() {
		return InternalBetweenExpressionBNF.ID;
	}

	/**
	 * Returns the actual <b>NOT</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
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
		return (notIdentifier != null) ? NOT_BETWEEN : BETWEEN;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(BetweenExpressionBNF.ID);
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
	 * Determines whether the identifier <b>NOT</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return notIdentifier != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NOT'
		if (wordParser.startsWithIgnoreCase('N')) {
			notIdentifier = wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'BETWEEN x AND y'
		super.parse(wordParser, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Expression
		if (hasExpression()) {
			expression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'NOT'
		if (notIdentifier != null) {
			writer.append(actual ? notIdentifier : NOT);
			writer.append(SPACE);
		}

		super.toParsedText(writer, actual);
	}
}