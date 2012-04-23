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
 * This {@link RangeVariableDeclaration} creates a new {@link RangeVariableDeclaration}.
 *
 * @see RangeVariableDeclaration
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RangeVariableDeclarationFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link RangeVariableDeclarationFactory}.
	 */
	public static final String ID = "range_variable_declaration";

	/**
	 * Creates a new <code>RangeVariableDeclarationFactory</code>.
	 */
	public RangeVariableDeclarationFactory() {
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

		// When the tolerant mode is turned on, parse the invalid portion of the query,
		// Order/Group are two exceptions to this rule
		// (expression != null) skip this check when parsing the first range variable declaration
		if (tolerant &&
		     expression != null             &&
		    !word.equalsIgnoreCase("order") &&
		    !word.equalsIgnoreCase("group") &&
		    registry.isIdentifier(word)) {

			ExpressionFactory factory = registry.expressionFactoryForIdentifier(word);

			if (factory == null) {
				return null;
			}

			expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);

			if (expression != null) {
				return new BadExpression(parent, expression);
			}
		}

		expression = new RangeVariableDeclaration(parent);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}