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
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This wraps another {@link Expression} that was correctly parsed by it is located in an invalid
 * location within the JPQL query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class BadExpression extends AbstractExpression {

	/**
	 * The {@link Expression} that was parsed in a location that was not supposed to contain that
	 * expression.
	 */
	private AbstractExpression expression;

	/**
	 * Creates a new <code>BadExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public BadExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>BadExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The {@link Expression} that was parsed in a location that was not supposed
	 * to contain that expression
	 */
	public BadExpression(AbstractExpression parent, AbstractExpression expression) {
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
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {
		children.add(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF findQueryBNF(AbstractExpression expression) {
		return getParent().findQueryBNF(expression);
	}

	/**
	 * Returns the {@link Expression} that was parsed but grammatically, it is not a valid location.
	 *
	 * @return The invalid portion of the JPQL query that was parsed
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
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(BadExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

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
	protected boolean isUnknown() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {
		expression = parse(wordParser, BadExpressionBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}
	}
}