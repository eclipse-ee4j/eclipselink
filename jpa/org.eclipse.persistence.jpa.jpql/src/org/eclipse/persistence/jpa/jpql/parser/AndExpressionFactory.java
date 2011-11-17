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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link AndExpression} creates a new {@link AdditionExpression} when the portion of the query
 * to parse starts with <b>AND</b>.
 *
 * @see AndExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class AndExpressionFactory extends ExpressionFactory {

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression}
	 * passed to this factory is an {@link OrExpression}.
	 */
	private OrExpressionVisitor visitor;

	/**
	 * The unique identifier of this {@link AndExpression}.
	 */
	public static final String ID = Expression.AND;

	/**
	 * Creates a new <code>AndExpressionFactory</code>.
	 */
	public AndExpressionFactory() {
		super(ID, Expression.AND);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("null")
	protected AbstractExpression buildExpression(AbstractExpression parent,
	                                             WordParser wordParser,
	                                             String word,
	                                             JPQLQueryBNF queryBNF,
	                                             AbstractExpression expression,
	                                             boolean tolerant) {

		if (expression != null) {
			expression.accept(visitor());
		}

		if ((visitor != null) && visitor.found) {
			visitor.found = false;
			OrExpression orExpression = (OrExpression) expression;

			AndExpression andExpression = new AndExpression(parent);
			andExpression.setLeftExpression((AbstractExpression) orExpression.getRightExpression());
			andExpression.parse(wordParser, tolerant);
			orExpression.setRightExpression(andExpression);

			return orExpression;
		}
		else {
			AndExpression andExpression = new AndExpression(parent);

			if (expression != null) {
				andExpression.setLeftExpression(expression);
			}

			andExpression.parse(wordParser, tolerant);
			return andExpression;
		}
	}

	private OrExpressionVisitor visitor() {
		if (visitor == null) {
			visitor = new OrExpressionVisitor();
		}
		return visitor;
	}

	/**
	 * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
	 * factory is an {@link OrExpression}.
	 */
	private class OrExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * This flag is turned on if the {@link Expression} visited is {@link OrExpression}.
		 */
		boolean found;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {
			found = true;
		}
	}
}