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
 * This factory is used by EclipseLink 2.4 to add support for parsing an abstract schema name when
 * the expression is not a join association path expression.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinCollectionValuedPathExpressionFactory extends AbstractCollectionValuedPathExpressionFactory {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "join_association_path_expression*";

	/**
	 * Creates a new <code>JoinCollectionValuedPathExpressionFactory</code>.
	 */
	public JoinCollectionValuedPathExpressionFactory() {
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

		// Pass on the fallback ExpressionFactory, this will allow PreLiteralExpressionFactory to
		// give to LiteralExpressionFactory the ExpressionFactory that will be used to create the
		// right object but will still create the right object when the query is invalid
		factory.setFallBackExpressionFactory(AbstractSchemaNameFactory.ID);

		return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
	}
}