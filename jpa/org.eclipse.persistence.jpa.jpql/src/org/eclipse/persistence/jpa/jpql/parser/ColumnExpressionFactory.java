/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * This {@link ColumnExpressionFactory} creates a new {@link ColumnExpression} when the portion of the
 * query to parse starts with <b>COLUMN</b>.
 *
 * @see ColumnExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class ColumnExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier for this {@link SQLExpressionFactory}.
	 */
	public static final String ID = Expression.COLUMN;

	/**
	 * Creates a new <code>FuncExpressionFactory</code>.
	 */
	public ColumnExpressionFactory() {
		super(ID, Expression.COLUMN);
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

		expression = new ColumnExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}