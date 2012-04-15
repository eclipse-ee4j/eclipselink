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
 * This {@link GroupByItemFactory} is responsible to return the right expression and to support
 * invalid expression as well.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class GroupByItemFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link GroupByItemFactory}.
	 */
	public static final String ID = "groupby_item";

	/**
	 * Creates a new <code>GroupByItemFactory</code>.
	 */
	public GroupByItemFactory() {
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

		// StateFieldPathExpression
		if (word.indexOf(AbstractExpression.DOT) > -1) {
			expression = new StateFieldPathExpression(parent, word);
			expression.parse(wordParser, tolerant);
			return expression;
		}

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

		// IdentificationVariable or any text
		expression = new IdentificationVariable(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}