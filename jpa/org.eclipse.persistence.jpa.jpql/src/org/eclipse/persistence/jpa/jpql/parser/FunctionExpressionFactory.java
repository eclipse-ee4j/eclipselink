/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * This {@link FunctionExpressionFactory} creates a new {@link FunctionExpression} when the portion of the
 * query to parse starts with <b>FUNCTION</b>.
 *
 * @see FunctionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class FunctionExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier for this {@link FunctionExpressionFactory}.
	 */
	public static final String ID = Expression.FUNCTION;

	/**
	 * Creates a new <code>FuncExpressionFactory</code>.
	 */
	public FunctionExpressionFactory() {
		super(ID, Expression.FUNCTION);
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

		expression = new FunctionExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}