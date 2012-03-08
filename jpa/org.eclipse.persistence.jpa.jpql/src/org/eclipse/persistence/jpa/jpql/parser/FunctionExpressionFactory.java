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
 * This {@link FunctionExpressionFactory} creates a new {@link FunctionExpression} when the portion
 * of the query to parse starts with an identifier related to a SQL function.
 *
 * @see FunctionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class FunctionExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier for this {@link FunctionExpressionFactory}.
	 */
	public static final String ID = Expression.FUNCTION;

	/**
	 * Creates a new <code>FunctionExpressionFactory</code>.
	 */
	public FunctionExpressionFactory() {
		super(ID);
	}

	/**
	 * Creates a new <code>FunctionExpressionFactory</code>.
	 *
	 * @param identifiers The JPQL identifiers handled by this factory
	 */
	public FunctionExpressionFactory(String... identifiers) {
		super(ID, identifiers);
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

		// Search for the constant that is registered with this factory
		String identifier = null;

		for (String possibleIdentifier : identifiers()) {
			if (possibleIdentifier.equalsIgnoreCase(word)) {
				identifier = possibleIdentifier;
				break;
			}
		}

		// No constant was found
		if (identifier == null) {
			return null;
		}

		expression = new FunctionExpression(parent, identifier);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}