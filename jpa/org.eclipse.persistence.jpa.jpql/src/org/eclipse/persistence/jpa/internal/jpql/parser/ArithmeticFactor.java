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
 * This expression simply adds a plus or minus sign to the arithmetic primary expression.
 *
 * <div nowrap><b>BNF:</b> <code>arithmetic_factor ::= [{+|-}] arithmetic_primary</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class ArithmeticFactor extends AbstractExpression {

	/**
	 * The {@link Expression} representing the arithmetic primary.
	 */
	private AbstractExpression expression;

	/**
	 * Creates a new <code>ArithmeticFactor</code>.
	 *
	 * @param parent The parent of this expression
	 * @param arithmeticFactor The arithmetic factor, which is either '+' or '-'
	 */
	ArithmeticFactor(AbstractExpression parent, String arithmeticFactor) {
		super(parent, arithmeticFactor);
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		children.add(buildStringExpression(getText()));

		if (expression != null) {
			children.add(expression);
		}
	}

	/**
	 * Returns the {@link Expression} representing the arithmetic primary.
	 *
	 * @return The expression representing the arithmetic primary
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(ArithmeticFactorBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleAggregate(JPQLQueryBNF queryBNF) {
		return false;
	}

	/**
	 * Determines whether the arithmetic primary was parsed.
	 *
	 * @return <code>true</code> the arithmetic primary was parsed; <code>false</code> if nothing was
	 * parsed
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines if the arithmetic primary is prepended with the minus sign.
	 *
	 * @return <code>true</code> if the expression is prepended with '-'; <code>false</code> otherwise
	 */
	public boolean hasMinusSign() {
		return getText().charAt(0) == '-';
	}

	/**
	 * Determines if the arithmetic primary is prepended with the plus sign.
	 *
	 * @return <code>true</code> if the expression is prepended with '+'; <code>false</code> otherwise
	 */
	public boolean hasPlusSign() {
		return getText().charAt(0) == '+';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		char character = wordParser.character();

		return wordParser.isArithmeticSymbol(character) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Remove the arithmetic operator
		wordParser.moveForward(1);

		wordParser.skipLeadingWhitespace();

		// Parse the expression
		expression = parse(wordParser, getQueryBNF(), tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		writer.append(getText());

		if (expression != null) {
			expression.toParsedText(writer);
		}
	}
}