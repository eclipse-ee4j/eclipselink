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
 * This {@link UnknownExpressionFactory} creates a new {@link UnknownExpression} when the portion of
 * the query to parse is unknown.
 *
 * @see UnknownExpression
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class UnknownExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link UnknownExpressionFactory}.
	 */
	static final String ID = "unknown";

	/**
	 * Creates a new <code>UnknownExpressionFactory</code>.
	 */
	UnknownExpressionFactory() {
		super(ID);
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

		expression = new UnknownExpression(parent, wordParser.substring());
		expression.parse(wordParser, tolerant);
		return expression;
	}
}