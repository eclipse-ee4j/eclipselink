/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for a conditional term expression.
 *
 * <div nowrap><b>BNF:</b> <code>connectby_clause ::= CONNECT BY { PRIOR parent_expr = child_expr | child_expr = PRIOR parent_expr }</code><p>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ConnectByClauseBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "connectby_clause";

	/**
	 * Creates a new <code>ConnectByClauseBNF</code>.
	 */
	public ConnectByClauseBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleCollection(true);
		registerExpressionFactory(ConnectByClauseFactory.ID);
	}
}