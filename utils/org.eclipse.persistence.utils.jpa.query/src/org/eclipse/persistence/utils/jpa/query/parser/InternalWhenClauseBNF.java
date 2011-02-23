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
 * The query BNF for the expression following the <b>WHEN</b> identifier.
 *
 * <div nowrap><b>BNF:</b> <code>when_clause ::= WHEN conditional_expression THEN scalar_expression</code><p>
 * or
 * <div nowrap><b>BNF:</b> <code>simple_when_clause ::= WHEN scalar_expression THEN scalar_expression</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InternalWhenClauseBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "internal_when_clause*";

	/**
	 * Creates a new <code>InternalWhenClauseBNF</code>.
	 */
	InternalWhenClauseBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId() {
		return EntityTypeExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(ConditionalExpressionBNF.ID);
		registerChild(ScalarExpressionBNF.ID);
	}
}