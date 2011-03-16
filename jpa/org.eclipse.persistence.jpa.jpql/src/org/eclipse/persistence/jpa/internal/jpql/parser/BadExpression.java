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
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class BadExpression extends AbstractExpression {

	/**
	 *
	 */
	private AbstractExpression expression;

	/**
	 * Creates a new <code>BadExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	BadExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>BadExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression
	 */
	BadExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent);

		this.expression = expression;
		this.expression.setParent(this);
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
		children.add(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF findQueryBNF(AbstractExpression expression) {
		return getParent().findQueryBNF(expression);
	}

	public AbstractExpression getExpression() {
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
		return queryBNF(BadExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		char character = wordParser.character();

		return character == AbstractExpression.LEFT_PARENTHESIS  ||
		       character == AbstractExpression.RIGHT_PARENTHESIS ||
		       character == AbstractExpression.COMMA             ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {
		expression = parse(
			wordParser,
			getQueryBNF(),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {
		if (expression != null) {
			expression.toParsedText(writer);
		}
	}
}