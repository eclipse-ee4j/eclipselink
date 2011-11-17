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
 * This {@link IsExpressionFactory} creates a new expression when the portion of the query to parse
 * starts with <b>IS</b>.
 *
 * @see EmptyCollectionComparisonExpression
 * @see NullComparisonExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class IsExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link IsExpressionFactory}.
	 */
	public static final String ID = Expression.IS;

	/**
	 * Creates a new <code>IsExpressionFactory</code>.
	 */
	public IsExpressionFactory() {
		super(ID, Expression.IS);
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

		int index = wordParser.position() + 2;
		index += wordParser.whitespaceCount(index);

		// IS NOT EMPTY or IS NO NULL
		if (wordParser.startsWithIdentifier(Expression.NOT, index)) {
			index += 3;
			index += wordParser.whitespaceCount(index);

			// IS NOT EMPTY
			if (wordParser.startsWithIdentifier(Expression.EMPTY, index)) {
				expression = new EmptyCollectionComparisonExpression(parent, Expression.IS_NOT_EMPTY, expression);
			}
			// IS NOT NULL
			else if (wordParser.startsWithIdentifier(Expression.NULL, index)) {
				expression = new NullComparisonExpression(parent, Expression.IS_NOT_NULL, expression);
			}
			else {
				return null;
			}
		}
		// IS EMPTY
		else if (wordParser.startsWithIdentifier(Expression.EMPTY, index)) {
			expression = new EmptyCollectionComparisonExpression(parent, Expression.IS_EMPTY, expression);
		}
		// IS NULL
		else if (wordParser.startsWithIdentifier(Expression.NULL, index)) {
			expression = new NullComparisonExpression(parent, Expression.IS_NULL, expression);
		}
		else {
			return null;
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}