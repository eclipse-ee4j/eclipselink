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
 * This {@link KeywordExpressionFactory} creates a new {@link KeywordExpression} when the portion of
 * the query to parse starts with <b>FALSE</b>, <b>TRUE</b> or <b>NULL</b>.
 *
 * @see KeywordExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class KeywordExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link KeywordExpressionFactory}.
	 */
	public static final String ID = "keyword";

	/**
	 * Creates a new <code>KeywordExpressionFactory</code>.
	 */
	public KeywordExpressionFactory() {
		super(ID, Expression.FALSE,
		          Expression.NULL,
		          Expression.TRUE);
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

		expression = new KeywordExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}