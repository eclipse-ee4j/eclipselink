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

/**
 * The query BNF for a select item expression.
 *
 * <div nowrap><b>BNF:</b> <code>select_item ::= select_expression [[AS] result_variable]</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleSelectItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = SimpleSelectExpressionBNF.ID;

	/**
	 * Creates a new <code>SelectExpressionBNF2_0</code>.
	 */
	public SimpleSelectItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

		// Technically, this BNF does not support collection but it's parent
		// select_clause does. But this BNF is used by SelectClause directly
		// to parse the query so the flag has to be turned on here
		setHandleCollection(true);

		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(ResultVariableFactory.ID);
		registerChild(ResultVariableBNF.ID);
		registerChild(SimpleSelectExpressionBNF.ID);
	}
}