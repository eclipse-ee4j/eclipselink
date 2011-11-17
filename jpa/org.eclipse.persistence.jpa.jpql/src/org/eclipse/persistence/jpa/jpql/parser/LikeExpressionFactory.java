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
 * This {@link LikeExpressionFactory} creates a new {@link LikeExpression} when the portion of the
 * query to parse starts with <b>LIKE</b> or <b>NOT LIKE</b>.
 *
 * @see LikeExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class LikeExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link LikeExpressionFactory}.
	 */
	public static final String ID = Expression.LIKE;

	/**
	 * Creates a new <code>LikeExpressionFactory</code>.
	 */
	public LikeExpressionFactory() {
		super(ID, Expression.LIKE,
		          Expression.NOT_LIKE);
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

		expression = new LikeExpression(parent, expression);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}