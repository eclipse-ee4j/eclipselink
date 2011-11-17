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
 * This {@link Expression} represents an identification variable that maps a {@link java.util.Map Map}
 * property, either the key, the value or a {@link java.util.Map.Entry Map.Entry}).
 * <p>
 * This is part of JPA 2.0.
 * <p>
 * <div nowrap><b>BNF:</b> <code>&lt;identifier&gt;(identification_variable)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class EncapsulatedIdentificationVariableExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>MapEntryIdentificationVariableExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected EncapsulatedIdentificationVariableExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String encapsulatedExpressionBNF() {
		return PreLiteralExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(GeneralIdentificationVariableBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression parse(WordParser wordParser, JPQLQueryBNF queryBNF, boolean tolerant) {

		if (tolerant) {
			return super.parse(wordParser, queryBNF, tolerant);
		}

		String word = wordParser.word();

		IdentificationVariable expression = new IdentificationVariable(this, word);
		expression.parse(wordParser, tolerant);
		return expression;
	}
}