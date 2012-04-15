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

/**
 * The query BNF for the items of an <b>IN</b> expression.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF:</b> <code>in_item ::= ( actual_in_item {, actual_in_item}* | subquery)</code><p>
 * <div nowrap><b>BNF:</b> <code>actual_in_item ::= literal | input_parameter
 * <p>
 * JPA 2.0:
 * <div nowrap><b>BNF:</b> <code>in_item ::= ( actual_in_item {, actual_in_item}* ) | (subquery) | collection_valued_input_parameter</code><p>
 * <div nowrap><b>BNF:</b> <code>actual_in_item ::= literal | input_parameter
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InExpressionItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "in_expression_item";

	/**
	 * Creates a new <code>InExpressionItemBNF</code>.
	 */
	public InExpressionItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleAggregate(true); // To support invalid queries
		setHandleCollection(true);
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(PreLiteralExpressionFactory.ID);
		registerChild(LiteralBNF.ID);
		registerChild(InputParameterBNF.ID);
		registerChild(SubqueryBNF.ID);
	}
}