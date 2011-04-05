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
 * This {@link NotExpressionFactory} creates a new {@link NotExpression} when
 * the portion of the query to parse starts with <b>NOT</b>. If the text has
 * another identifier that is negated, then the right expression will be created.
 *
 * @see NotExpression
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class NotExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link NotExpressionFactory}.
	 */
	static final String ID = Expression.NOT;

	/**
	 * Creates a new <code>NotExpressionFactory</code>.
	 */
	NotExpressionFactory() {
		super(ID, Expression.NOT);
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
		// Skip 'NOT '
		int index = wordParser.position() + 3;
		index += wordParser.whitespaceCount(index);

		// 'NOT IN'
		if (wordParser.startsWithIdentifier(Expression.IN, index)) {
			expression = new InExpression(parent, expression);
		}
		// 'NOT LIKE'
		else if (wordParser.startsWithIdentifier(Expression.LIKE, index)) {
			expression = new LikeExpression(parent, expression);
		}
		// 'NOT MEMBER'
		else if (wordParser.startsWithIdentifier(Expression.MEMBER, index)) {
			expression = new CollectionMemberExpression(parent, expression);
		}
		// 'NOT BETWEEN'
		else if (wordParser.startsWithIdentifier(Expression.BETWEEN, index)) {
			expression = new BetweenExpression(parent, expression);
		}
		// 'NOT EXISTS'
		else if (wordParser.startsWithIdentifier(Expression.EXISTS, index)) {
			expression = new ExistsExpression(parent);
		}
		// 'NOT'
		else {
			expression = new NotExpression(parent, queryBNF);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}