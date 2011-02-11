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

import java.util.HashMap;
import java.util.Map;

/**
 * This object contains the position of the cursor within the parsed tree. It
 * has the position for each {@link Expression expressions} up to the deepest
 * leaf.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class QueryPosition
{
	/**
	 * The child {@link Expression} where the position of the cursor is.
	 */
	private Expression expression;

	/**
	 * The position of the cursor in the query.
	 */
	private int position;

	/**
	 * The table containing the position of each of the {@link Expression} up to
	 * the deepest leaf.
	 */
	private Map<Expression, Integer> positions;

	/**
	 * Creates a new <code>QueryPosition</code>.
	 *
	 * @param position The position of the cursor in the query
	 */
	QueryPosition(int position)
	{
		super();

		this.position  = position;
		this.positions = new HashMap<Expression, Integer>();
	}

	/**
	 * Adds
	 *
	 * @param expression
	 * @param position
	 */
	void addPosition(Expression expression, int position)
	{
		positions.put(expression, position);
	}

	/**
	 * Returns the child {@link Expression} where the position of the cursor is.
	 *
	 * @return The deepest {@link Expression} child that was retrieving by
	 * traversing the parsed tree up to the position of the cursor.
	 */
	public Expression getExpression()
	{
		return expression;
	}

	/**
	 * Returns
	 *
	 * @param position
	 * @return
	 */
	public Expression getExpression(int position)
	{
		for (Map.Entry<Expression, Integer> entry : positions.entrySet())
		{
			if (entry.getValue() == position)
			{
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * Returns the position of the cursor in the query.
	 *
	 * @return The position of the cursor in the query
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Returns
	 *
	 * @param expression
	 * @return
	 */
	public int getPosition(Expression expression)
	{
		Integer position = positions.get(expression);
		return (position == null) ? -1 : position;
	}

	/**
	 * Removes
	 *
	 * @param expression
	 */
	void removePosition(Expression expression)
	{
		positions.remove(expression);
	}

	/**
	 * Sets
	 *
	 * @param expression
	 */
	void setExpression(Expression expression)
	{
		this.expression = expression;
	}
}