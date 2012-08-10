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
 * This <code>StartWithClauseFactory</code> creates a new {@link StartWithClause} when the portion
 * of the query to parse starts with <b>START WITH</b>.
 *
 * @see StartWithClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class StartWithClauseFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link StartWithClauseFactory}.
	 */
	public static final String ID = Expression.START_WITH;

	/**
	 * Creates a new <code>StartWithClauseFactory</code>.
	 */
	public StartWithClauseFactory() {
		super(ID, Expression.START_WITH);
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

		expression = new StartWithClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}