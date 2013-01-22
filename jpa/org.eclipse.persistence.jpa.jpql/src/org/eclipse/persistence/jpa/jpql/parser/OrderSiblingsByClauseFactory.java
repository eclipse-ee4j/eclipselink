/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
 * This <code>OrderSiblingsByClauseFactory</code> creates a new {@link OrderSiblingsByClause} when
 * the portion of the query to parse starts with <b>ORDER SIBLINGS BY</b>.
 *
 * @see OrderByClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class OrderSiblingsByClauseFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link OrderSiblingsByClauseFactory}.
	 */
	public static final String ID = Expression.ORDER_SIBLINGS_BY;

	/**
	 * Creates a new <code>OrderSiblingsByClauseFactory</code>.
	 */
	public OrderSiblingsByClauseFactory() {
		super(ID, Expression.ORDER_SIBLINGS_BY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildExpression(AbstractExpression parent,
	                                             WordParser wordParser,
	                                             String word,
	                                             JPQLQueryBNF queryBNF,
	                                             AbstractExpression expression,
	                                             boolean tolerant) {

		expression = new OrderSiblingsByClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}