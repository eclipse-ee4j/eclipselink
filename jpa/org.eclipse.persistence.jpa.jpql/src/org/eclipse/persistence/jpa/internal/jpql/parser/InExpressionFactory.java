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

import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * This {@link InExpressionFactory} creates a new {@link InExpression} when the portion of the query
 * to parse starts with <b>IN</b> or <b>NOT IN</b>.
 *
 * @see InExpression
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class InExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link InExpressionFactory}.
	 */
	static final String ID = Expression.IN;

	/**
	 * Creates a new <code>InExpressionFactory</code>.
	 */
	InExpressionFactory() {
		super(ID, Expression.IN,
		          Expression.NOT_IN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant) {

		expression = new InExpression(parent, expression);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}