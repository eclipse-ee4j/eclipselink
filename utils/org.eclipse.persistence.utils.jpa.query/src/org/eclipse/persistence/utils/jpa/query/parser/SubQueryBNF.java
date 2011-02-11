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
 * The query BNF for a subquery expression.
 *
 * <div nowrap><b>BNF:</b> <code>subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]</code><p>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SubQueryBNF extends AbstractCompoundBNF
{
	/**
	 * The unique identifier of this BNF rule.
	 */
	static final String ID = "subquery";

	/**
	 * Creates a new <code>SubQueryBNF</code>.
	 */
	SubQueryBNF()
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
		return BadExpressionFactory.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		super.initialize();

		registerChild(SimpleSelectClauseBNF.ID);
	}
}