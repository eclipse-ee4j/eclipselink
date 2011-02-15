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

import java.util.Arrays;
import org.eclipse.persistence.utils.jpa.query.QueryProblem;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * The default implementation of a {@link QueryProblem}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class DefaultQueryProblem implements QueryProblem
{
	private int endIndex;
	private Expression expression;
	private Object[] messageArguments;
	private String messageKey;
	private IQuery query;
	private int startIndex;

	/**
	 * Creates a new <code>DefaultQueryProblem</code>.
	 *
	 * @param query
	 * @param expression
	 * @param location
	 * @param messageKey
	 * @param messageArguments
	 */
	DefaultQueryProblem(IQuery query,
	                    Expression expression,
	                    int startIndex,
	                    int endIndex,
	                    String messageKey,
	                    Object... messageArguments)
	{
		super();

		this.query            = query;
		this.startIndex       = startIndex;
		this.endIndex         = endIndex;
		this.expression       = expression;
		this.messageKey       = messageKey;
		this.messageArguments = messageArguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEndPosition()
	{
		return endIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Expression getExpression()
	{
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getMessageArguments()
	{
		return messageArguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessageKey()
	{
		return messageKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery()
	{
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getStartPosition()
	{
		return startIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("messageKey=");
		sb.append(messageKey);
		sb.append(", messageArguments=");
		sb.append(Arrays.toString(messageArguments));
		sb.append(", position=[");
		sb.append(startIndex);
		sb.append(", ");
		sb.append(endIndex);
		sb.append("], query=");
		sb.append(query.getExpression());
		return sb.toString();
	}
}