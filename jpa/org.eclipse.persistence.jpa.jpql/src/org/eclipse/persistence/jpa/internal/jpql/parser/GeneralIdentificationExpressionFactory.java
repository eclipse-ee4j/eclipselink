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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;

/**
 * This {@link ValueExpressionFactory} creates a general identification variable, which is either
 * with the identifier <b>KEY</b> or <b>VALUE</b> and then checks the existence of a path expression.
 *
 * @see KeyExpressionFactory
 * @see ValueExpressionFactory
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class GeneralIdentificationExpressionFactory extends ExpressionFactory {

	/**
	 * Creates a new <code>GeneralIdentificationExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this <code>ExpressionFactory</code>
	 * @param identifier The JPQL identifier handled by this factory
	 */
	GeneralIdentificationExpressionFactory(String id, String identifier) {
		super(id, identifier);
	}

	/**
	 * Creates the actual expression this factory manages.
	 *
	 * @param parent The parent of this expression
	 * @return The {@link Expression} this factory manages
	 */
	abstract AbstractExpression buildExpression(AbstractExpression parent);

	/**
	 * {@inheritDoc}
	 */
	@Override
	final AbstractExpression buildExpression(AbstractExpression parent,
	                                         WordParser wordParser,
	                                         String word,
	                                         JPQLQueryBNF queryBNF,
	                                         AbstractExpression expression,
	                                         boolean tolerant) {

		expression = buildExpression(parent);
		expression.parse(wordParser, tolerant);

		if (wordParser.character() == AbstractExpression.DOT) {
			ExpressionFactory factory = AbstractExpression.expressionFactory(queryBNF.getFallbackExpressionFactoryId());
			expression = factory.buildExpression(parent, wordParser, wordParser.word(), queryBNF, expression, tolerant);
		}

		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final IJPAVersion getVersion() {
		return IJPAVersion.VERSION_2_0;
	}
}