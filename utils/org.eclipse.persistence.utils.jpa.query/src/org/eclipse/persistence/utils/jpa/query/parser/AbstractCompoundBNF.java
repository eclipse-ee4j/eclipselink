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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This query BNF is used to register child BNFs in order to inherit their
 * properties as well.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
abstract class AbstractCompoundBNF extends JPQLQueryBNF
{
	/**
	 * The children BNF of this one.
	 */
	private List<String> children;

	/**
	 * Creates a new <code>AbstractCompoundBNF</code>.
	 *
	 * @param id The unique identifier of this BNF rule
	 */
	AbstractCompoundBNF(String id)
	{
		super(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addChildren(Set<JPQLQueryBNF> queryBNFs)
	{
		super.addChildren(queryBNFs);

		for (String id : children)
		{
			JPQLQueryBNF queryBNF = queryBNF(id);

			if (queryBNFs.add(queryBNF))
			{
				queryBNF.addChildren(queryBNFs);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void initialize()
	{
		children = new ArrayList<String>();
	}

	private JPQLQueryBNF queryBNF(String id)
	{
		return AbstractExpression.queryBNF(id);
	}

	/**
	 * Registers the unique identifier of the BNF rule as a child of this BNF
	 * rule.
	 *
	 * @param queryBNF The unique identifier of the BNF rule
	 */
	final void registerChild(String queryBNF)
	{
		children.add(queryBNF);
	}
}