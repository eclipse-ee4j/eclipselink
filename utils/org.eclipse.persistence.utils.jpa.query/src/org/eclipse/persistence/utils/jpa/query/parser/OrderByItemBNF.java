/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * The query BNF for the order by item expression.
 *
 * <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class OrderByItemBNF extends JPQLQueryBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "orderby_item";

	/**
	 * Creates a new <code>OrderByItemBNF</code>.
	 */
	OrderByItemBNF()
	{
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackBNFId()
	{
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String getFallbackExpressionFactoryId()
	{
		return OrderByItemFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleAggregate()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean handleCollection()
	{
		// Technically, this BNF does not support collection but it's parent
		// orderby_clause does. But this BNF is used by OrderByClause directly
		// to parse the query so the flag has to be turned on here
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
	}
}