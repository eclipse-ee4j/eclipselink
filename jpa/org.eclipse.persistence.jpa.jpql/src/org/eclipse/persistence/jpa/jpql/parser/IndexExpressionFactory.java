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
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * This {@link IndexExpressionFactory} creates a new {@link IndexExpression} when the portion of the
 * query to parse starts with <b>INDEX</b>.
 *
 * @see IndexExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class IndexExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link IndexExpressionFactory}.
	 */
	public static final String ID = Expression.INDEX;

	/**
	 * Creates a new <code>IndexExpressionFactory</code>.
	 */
	public IndexExpressionFactory() {
		super(ID, Expression.INDEX);
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

		expression = new IndexExpression(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPAVersion getVersion() {
		return JPAVersion.VERSION_2_0;
	}
}