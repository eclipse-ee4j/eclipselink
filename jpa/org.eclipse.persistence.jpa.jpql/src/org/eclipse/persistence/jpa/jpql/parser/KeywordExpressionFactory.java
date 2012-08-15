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
 * This <code>KeywordExpressionFactory</code> creates a new {@link KeywordExpression} when the
 * portion of the JPQL query to parse is <code><b>FALSE</b></code>, <code><b>TRUE</b></code> or
 * <code><b>NULL</b></code>.
 * <p>
 * EclipseLink 2.5 added two new identifiers: <code><b>MINVALUE</b></code> and <code><b>MAXVALUE</b></code>.
 *
 * @see KeywordExpression
 *
 * @version 2.5
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

		for (String identifier : identifiers()) {
			if (identifier.equalsIgnoreCase(word)) {
				expression = new KeywordExpression(parent, identifier);
				expression.parse(wordParser, tolerant);
				return expression;
			}
		}

		return null;
	}
}