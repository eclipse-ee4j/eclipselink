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
 * This {@link CollectionMemberExpressionFactory} creates a new {@link CollectionMemberExpression}
 * when the portion of the query to parse starts with <b>MEMBER</b>, <b>MEMBER OF</b>, <b>NOT
 * MEMBER</b> or <b>NOT MEMBER OF</b>.
 *
 * @see CollectionMemberExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class CollectionMemberExpressionFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link CollectionMemberExpression}.
	 */
	public static final String ID = Expression.MEMBER;

	/**
	 * Creates a new <code>CollectionMemberExpressionFactory</code>.
	 */
	public CollectionMemberExpressionFactory() {
		super(ID, Expression.MEMBER,
		          Expression.MEMBER_OF,
		          Expression.NOT_MEMBER,
		          Expression.NOT_MEMBER_OF);
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

		expression = new CollectionMemberExpression(parent, expression);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}