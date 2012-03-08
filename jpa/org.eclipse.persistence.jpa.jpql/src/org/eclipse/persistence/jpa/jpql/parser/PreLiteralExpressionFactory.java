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
 * This {@link PreLiteralExpressionFactory} is used to verify the next word in the query is not an
 * identifier before delegating the creation to {@link LiteralExpressionFactory}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class PreLiteralExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link PreLiteralExpressionFactory}.
	 */
	public static final String ID = "pre-literal";

	/**
	 * Creates a new <code>PreLiteralExpressionFactory</code>.
	 */
	public PreLiteralExpressionFactory() {
		super(ID);
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

		ExpressionRegistry registry = getExpressionRegistry();

		// When the tolerant mode is turned on, parse the invalid portion of the query
		if (tolerant && registry.isIdentifier(word)) {
			ExpressionFactory factory = registry.expressionFactoryForIdentifier(word);

			if (factory == null) {
				return null;
			}

			expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);

			if (expression != null) {
				return new BadExpression(parent, expression);
			}
		}

		ExpressionFactory factory = registry.getExpressionFactory(LiteralExpressionFactory.ID);
		return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
	}
}