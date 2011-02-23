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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link KeywordExpressionFactory} creates a new {@link KeywordExpression} when the portion of
 * the query to parse starts with <b>FALSE</b>, <b>TRUE</b> or <b>NULL</b>.
 *
 * @see KeywordExpression
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class KeywordExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link KeywordExpressionFactory}.
	 */
	static final String ID = "keyword";

	/**
	 * Creates a new <code>KeywordExpressionFactory</code>.
	 */
	KeywordExpressionFactory() {
		super(ID, Expression.FALSE,
		          Expression.NULL,
		          Expression.TRUE);
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

		expression = new KeywordExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}