/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for a group by item expression.
 *
 * <div nowrap><b>BNF:</b> <code>groupby_item ::= single_valued_path_expression | identification_variable</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class GroupByItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "groupby_item";

	/**
	 * Creates a new <code>GroupByItemBNF</code>.
	 */
	public GroupByItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();

		// Technically, this BNF does not support collection but it's parent
		// groupby_clause does. But this BNF is used by GroupByClause directly
		// to parse the query so the flag has to be turned on here
		setHandleCollection(true);

		setHandleAggregate(true);
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(GroupByItemFactory.ID);
		registerChild(SingleValuedPathExpressionBNF.ID);
		registerChild(IdentificationVariableBNF.ID);
	}
}