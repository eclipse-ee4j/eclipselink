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
 * This {@link StringLiteralFactory} is responsible to parse a sub-query starting with a single quote.
 *
 * @see StringLiteral
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StringLiteralFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link StringLiteralFactory}.
	 */
	public static final String ID = "string-literal";

	/**
	 * Creates a new <code>StringLiteralFactory</code>.
	 */
	public StringLiteralFactory() {
		super(ID, Expression.QUOTE);
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

		expression = new StringLiteral(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}