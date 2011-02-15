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
package org.eclipse.persistence.utils.jpa.query;

import java.util.Iterator;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;

/**
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class QueryBuilder extends AbstractExpressionVisitor
{
	/**
	 *
	 */
	private final DatabaseQuery query;

	/**
	 * Creates a new <code>QueryBuilder</code>.
	 *
	 * @param query
	 */
	QueryBuilder(DatabaseQuery query)
	{
		super();
		this.query = query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		for (Iterator<Expression> iter = expression.children(); iter.hasNext(); )
		{
			iter.next().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
//		DeleteAllQuery deleteQuery = (DeleteAllQuery) query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		expression.getDeleteClause().accept(this);
		expression.getWhereClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		ObjectLevelReadQuery objectQuery = (ObjectLevelReadQuery) query;

		if (expression.hasDistinct())
		{
			objectQuery.useDistinct();
		}
		else
		{
			objectQuery.dontUseDistinct();
		}

		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
//		UpdateAllQuery updateQuery = (UpdateAllQuery) query;
		// updateQuery.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		expression.getUpdateClause().accept(this);
		expression.getWhereClause().accept(this);
	}
}