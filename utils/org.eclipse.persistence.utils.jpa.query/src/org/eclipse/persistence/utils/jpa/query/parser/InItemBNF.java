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
 * The query BNF for the items of an <b>IN</b> expression.
 *
 * <div nowrap><b>BNF:</b> <code>in_item ::= ( in_item {, in_item}* ) | (subquery) | collection_valued_input_parameter</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class InItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "in_item";

	/**
	 * Creates a new <code>InItemBNF</code>.
	 */
	InItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackExpressionFactoryId() {
		return EntityTypeLiteralFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleAggregate() {
		// To support invalid queries
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleCollection() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerExpressionFactory(EntityTypeLiteralFactory.ID);

		registerChild(LiteralBNF.ID);
		registerChild(InputParameterBNF.ID);
		registerChild(SubQueryBNF.ID);

		// EclipseLink extension of JPQL grammar
		registerChild(ScalarExpressionBNF.ID);
	}
}