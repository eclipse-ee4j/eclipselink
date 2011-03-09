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

/**
 * This {@link SqrtExpressionFactory} creates a new {@link SqrtExpression} when
 * the portion of the query to parse starts with <b>SQRT</b>.
 *
 * @see SqrtExpression
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class SqrtExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link SqrtExpressionFactory}.
	 */
	static final String ID = Expression.SQRT;

	/**
	 * Creates a new <code>SqrtExpressionFactory</code>.
	 */
	SqrtExpressionFactory() {
		super(ID, Expression.SQRT);
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
		expression = new SqrtExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}