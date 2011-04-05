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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * This {@link CollectionValuedPathExpressionFactory} creates a new
 * {@link CollectionValuedPathExpression}.
 *
 * @see CollectionValuedPathExpression
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class CollectionValuedPathExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link CollectionValuedPathExpressionFactory}.
	 */
	static final String ID = "collection_valued_path";

	/**
	 * Creates a new <code>CollectionValuedPathExpressionFactory</code>.
	 */
	CollectionValuedPathExpressionFactory() {
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
		if (expression != null) {
			expression = new CollectionValuedPathExpression(parent, expression);
		}
		else if (tolerant && (word.indexOf(AbstractExpression.DOT) == -1)) {
			ExpressionFactory factory = AbstractExpression.expressionFactory(PreLiteralExpressionFactory.ID);
			return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
		}
		else {
			expression = new CollectionValuedPathExpression(parent, word);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}