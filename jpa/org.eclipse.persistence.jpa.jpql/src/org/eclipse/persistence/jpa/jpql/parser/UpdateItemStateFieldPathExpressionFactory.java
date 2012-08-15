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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link UpdateItemStateFieldPathExpressionFactory} is meant to handle the parsing of a
 * portion of the query when it's expected to be a state field path. By default a word without a dot
 * would be parsed as an identification variable but for the left side of the update item assignment,
 * a single word is a state field path expression.
 *
 * @see StateFieldPathExpression
 *
 * @version 2.4.1
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class UpdateItemStateFieldPathExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link StateFieldPathExpressionFactory}.
	 */
	public static final String ID = "update-item-state-field-path";

	/**
	 * Creates a new <code>UpdateItemStateFieldPathExpressionFactory</code>.
	 */
	public UpdateItemStateFieldPathExpressionFactory() {
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

			// Special case where the word is seen as an JPQL identifier but it's actually
			// an unqualified path expression. Examples:
			// UPDATE DateTime SET avg = 'JPQL'
			// UPDATE DateTime SET timestamp = 'JPQL'
			if (factory == null ||
			    registry.getIdentifierRole(word) == IdentifierRole.FUNCTION &&
			    !ExpressionTools.isFunctionExpression(wordParser, word)) {

				expression = new StateFieldPathExpression(parent, word);
				expression.parse(wordParser, tolerant);
				return expression;
			}

			factory = registry.getExpressionFactory(PreLiteralExpressionFactory.ID);

			// Pass on the fallback ExpressionFactory, this will allow PreLiteralExpressionFactory to
			// give to LiteralExpressionFactory the ExpressionFactory that will be used to create the
			// right object but will still create the right object when the query is invalid
			factory.setFallBackExpressionFactory(StateFieldPathExpressionFactory.ID);

			return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
		}
		else {
			expression = new StateFieldPathExpression(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}
	}
}