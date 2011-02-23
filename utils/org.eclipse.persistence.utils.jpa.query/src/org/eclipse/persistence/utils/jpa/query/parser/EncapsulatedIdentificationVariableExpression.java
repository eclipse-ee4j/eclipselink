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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link Expression} represents an identification variable that maps a {@link java.util.Map Map}
 * property, either the key, the value or a {@link java.util.Map.Entry Map.Entry}).
 * <p>
 * This is part of JPA 2.0.
 * <p>
 * <div nowrap><b>BNF:</b> <code>&lt;identifier&gt;(identification_variable)</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class EncapsulatedIdentificationVariableExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>MapEntryIdentificationVariableExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	EncapsulatedIdentificationVariableExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final JPQLQueryBNF encapsulatedExpressionBNF() {
		return queryBNF(PreLiteralExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final JPQLQueryBNF getQueryBNF() {
		return queryBNF(GeneralIdentificationVariableBNF.ID);
	}
}