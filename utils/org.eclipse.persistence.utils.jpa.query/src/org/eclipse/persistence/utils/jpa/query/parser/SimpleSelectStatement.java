/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
 * <div nowrap><b>BNFL</b> <code>subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class SimpleSelectStatement extends AbstractSelectStatement
{
	/**
	 * Creates a new <code>SimpleSelectStatement</code>.
	 *
	 * @param parent The parent of this expression
	 */
	SimpleSelectStatement(AbstractExpression parent)
	{
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	SimpleFromClause buildFromClause()
	{
		return new SimpleFromClause(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	SimpleSelectClause buildSelectClause()
	{
		return new SimpleSelectClause(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(SubQueryBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleSelectClause getSelectClause()
	{
		return (SimpleSelectClause) super.getSelectClause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean shouldManageSpaceAfterClause()
	{
		return false;
	}
}