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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link PreLiteralExpressionFactory} is used to verify the next word in the query is not an
 * identifier before delegating the creation to {@link LiteralExpressionFactory}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class PreLiteralExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link PreLiteralExpressionFactory}.
	 */
	static final String ID = "pre-literal";

	/**
	 * Creates a new <code>PreLiteralExpressionFactory</code>.
	 */
	PreLiteralExpressionFactory() {
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

		// When the tolerant mode is turned on, parse the invalid portion of the query
		if (tolerant && JPQLExpression.isIdentifier(word)) {
			ExpressionFactory factory = AbstractExpression.expressionFactoryForIdentifier(word);
			if (factory != null) {
				return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
			}
			return null;
		}

		ExpressionFactory factory = AbstractExpression.expressionFactory(LiteralExpressionFactory.ID);
		return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
	}
}