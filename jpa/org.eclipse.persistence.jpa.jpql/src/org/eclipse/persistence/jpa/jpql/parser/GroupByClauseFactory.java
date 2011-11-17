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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link GroupByClauseFactory} creates a new {@link GroupByClause} when the portion of the
 * query to parse starts with <b>GROUP BY</b>.
 *
 * @see GroupByClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class GroupByClauseFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link GroupByClauseFactory}.
	 */
	public static final String ID = Expression.GROUP_BY;

	/**
	 * Creates a new <code>GroupByClauseFactory</code>.
	 */
	public GroupByClauseFactory() {
		super(ID, Expression.GROUP_BY);
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

		expression = new GroupByClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}