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
 * The query BNF for a select item expression.
 *
 * <div nowrap><b>BNF:</b> <code>select_item ::= select_expression [[AS] result_variable]</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SelectItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "select_item";

	/**
	 * Creates a new <code>SelectItemBNF</code>.
	 */
	SelectItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId() {
		// Because none of the child BNFs can identify how to parse the select
		// expression, it will fall back into itself and will use
		// LiteralExpressionFactory. It handles most of the text that does not
		// start with an identifier
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackExpressionFactoryId() {
		return ResultVariableFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleCollection() {
		// Technically, this BNF does not support collection but it's parent
		// select_clause does. But this BNF is used by SelectClause directly
		// to parse the query so the flag has to be turned on here
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize() {
		super.initialize();

		registerChild(ResultVariableBNF.ID);
		registerChild(SelectExpressionBNF.ID);
	}
}