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
 * This {@link RegexpExpressionFactory} creates a new {@link RegexpExpression} when the portion of the
 * query to parse starts with <b>REGEXP</b>.
 *
 * @see RegexpExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class RegexpExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link RegexpExpressionFactory}.
	 */
	public static final String ID = Expression.REGEXP;

	/**
	 * Creates a new <code>RegexpExpressionFactory</code>.
	 */
	public RegexpExpressionFactory() {
		super(ID, Expression.REGEXP);
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

		expression = new RegexpExpression(parent, expression);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}