/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link IsExpressionFactory} creates a new expression when the portion of the query to parse
 * starts with <b>IS</b>.
 *
 * @see EmptyCollectionComparisonExpression
 * @see NullComparisonExpression
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class IsExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link IsExpressionFactory}.
	 */
	public static final String ID = IS;

	/**
	 * Creates a new <code>IsExpressionFactory</code>.
	 */
	public IsExpressionFactory() {
		super(ID, IS);
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

		int position = wordParser.position();
		int index = position + 2;
		int count = wordParser.whitespaceCount(index);
		index += count;

		// IS NOT EMPTY or IS NOT NULL
		if (wordParser.startsWithIdentifier(NOT, index)) {
			index += 3;
			count = wordParser.whitespaceCount(index);
			index += count;

			// IS NOT EMPTY
			if (wordParser.startsWithIdentifier(EMPTY, index)) {
				expression = new EmptyCollectionComparisonExpression(parent, IS_NOT_EMPTY, expression);
			}
			// IS NOT NULL
			else if (wordParser.startsWithIdentifier(NULL, index)) {
				expression = new NullComparisonExpression(parent, IS_NOT_NULL, expression);
			}
			// IS NOT
			else {
				word = wordParser.substring(position, index - count);
				expression = new UnknownExpression(parent, word);
			}
		}
		// IS EMPTY
		else if (wordParser.startsWithIdentifier(EMPTY, index)) {
			expression = new EmptyCollectionComparisonExpression(parent, IS_EMPTY, expression);
		}
		// IS NULL
		else if (wordParser.startsWithIdentifier(NULL, index)) {
			expression = new NullComparisonExpression(parent, IS_NULL, expression);
		}
		// IS
		else {
			word = wordParser.substring(position, index - count);
			expression = new UnknownExpression(parent, word);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}