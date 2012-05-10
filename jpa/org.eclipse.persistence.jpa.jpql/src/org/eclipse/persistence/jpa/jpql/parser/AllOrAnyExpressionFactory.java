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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link AllOrAnyExpressionFactory} creates a new {@link AllOrAnyExpression} when the portion
 * of the query to parse starts with <b>ALL</b>, <b>ANY</b> or <b>SOME</b>.
 *
 * @see AllOrAnyExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class AllOrAnyExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link AllOrAnyExpressionFactory}.
	 */
	public static final String ID = "all-or-any";

	/**
	 * Creates a new <code>AndExpressionFactory</code>.
	 */
	public AllOrAnyExpressionFactory() {
		super(ID, Expression.ALL,
		          Expression.ANY,
		          Expression.SOME);
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

		expression = new AllOrAnyExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}