/*******************************************************************************
 * Copyright (c) 2012, 2013, 2013 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for a <code><b>CONNECT BY</b></code> clause. This was added to the grammar with
 * EclipseLink 2.5.
 *
 * <div nowrap><b>BNF:</b> <code>CONNECT BY { single_valued_object_path_expression | collection_valued_path_expression }</code><p>
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
		registerExpressionFactory(ConnectByClauseFactory.ID);
	}
}