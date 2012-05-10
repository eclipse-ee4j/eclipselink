/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link ExtractExpressionFactory} creates a new {@link ExtractExpression} when the portion of
 * the query to parse starts with <b>EXTRACT</b>.
 *
 * @see CastExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class ExtractExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link ExtractExpressionFactory}.
	 */
	public static final String ID = Expression.EXTRACT;

	/**
	 * Creates a new <code>TrimExpressionFactory</code>.
	 */
	public ExtractExpressionFactory() {
		super(ID, Expression.EXTRACT);
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

		expression = new ExtractExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}