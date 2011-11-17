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
 * This {@link StateFieldPathExpressionFactory} is meant to handle the parsing of a portion of the
 * query when it's expected to be a state field path.
 *
 * @see StateFieldPathExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StateFieldPathExpressionFactory extends AbstractLiteralExpressionFactory {

	/**
	 * The unique identifier of this {@link StateFieldPathExpressionFactory}.
	 */
	public static final String ID = "state-field-path";

	/**
	 * Creates a new <code>StateFieldPathExpressionFactory</code>.
	 */
	public StateFieldPathExpressionFactory() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildExpression(AbstractExpression parent,
	                                             WordParser wordParser,
	                                             String word,
	                                             AbstractExpression expression,
	                                             boolean tolerant) {

		if (tolerant && getExpressionRegistry().isIdentifier(word)) {
			return null;
		}

		expression = new IdentificationVariable(parent, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}