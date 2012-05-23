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
 * This {@link WhenClauseFactory} creates a new {@link WhenClause} when the portion of the query to
 * parse starts with <b>WHEN</b>.
 *
 * @see WhenClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhenClauseFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link WhenClauseFactory}.
	 */
	public static final String ID = Expression.WHEN;

	/**
	 * Creates a new <code>WhenClauseFactory</code>.
	 */
	public WhenClauseFactory() {
		super(ID, Expression.WHEN);
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

		expression = new WhenClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}