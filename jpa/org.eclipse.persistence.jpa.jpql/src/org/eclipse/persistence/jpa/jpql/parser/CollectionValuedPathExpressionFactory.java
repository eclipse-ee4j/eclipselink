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
 * This {@link CollectionValuedPathExpressionFactory} creates a new {@link CollectionValuedPathExpression}.
 *
 * @see CollectionValuedPathExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CollectionValuedPathExpressionFactory extends AbstractCollectionValuedPathExpressionFactory {

	/**
	 * The unique identifier of this {@link CollectionValuedPathExpressionFactory}.
	 */
	public static final String ID = "collection_valued_path";

	/**
	 * Creates a new <code>CollectionValuedPathExpressionFactory</code>.
	 */
	public CollectionValuedPathExpressionFactory() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildFallbackExpression(AbstractExpression parent,
	                                                     WordParser wordParser,
	                                                     String word,
	                                                     JPQLQueryBNF queryBNF,
	                                                     AbstractExpression expression,
	                                                     boolean tolerant) {

		ExpressionFactory factory = getExpressionRegistry().getExpressionFactory(PreLiteralExpressionFactory.ID);
		return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
	}
}