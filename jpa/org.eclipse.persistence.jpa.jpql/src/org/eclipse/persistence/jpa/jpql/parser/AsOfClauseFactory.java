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
 * This <code>AsOfClauseFactory</code> creates a new {@link AsOfClause} when the portion of the JPQL
 * query to parse starts with <b>AS OF</b>.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class AsOfClauseFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link AsOfClauseFactory}.
	 */
	public static final String ID = Expression.AS_OF;

	/**
	 * Creates a new <code>AsOfClauseFactory</code>.
	 */
	public AsOfClauseFactory() {
		super(ID, Expression.AS_OF);
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

		expression = new AsOfClause(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}