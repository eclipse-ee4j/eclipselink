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

import org.eclipse.persistence.utils.jpa.query.parser.ContentAssistVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.QueryPosition;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * This provider is responsible to find the possible choices in order to
 * continue the query where the cursor is positioned.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class ContentAssistProvider
{
	/**
	 * Determines if the application is accessible. If turned on, then
	 * {@link PathExpressionContentAssistVisitor} will be used to add more choices; otherwise
	 * it will be skipped.
	 */
	private boolean applicationAccessible;

	/**
	 * The position of the cursor in the query used to determine the possible
	 * choices.
	 */
	private int position;

	/**
	 * The model object holding onto the query, which is <code>null</code> when
	 * this object is used by the unit-tests.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>ContentAssistProvider</code>.
	 *
	 * @param query The external representation of the Java Persistence query
	 * @param position The position of the cursor in the query
	 */
	public ContentAssistProvider(IQuery query, int position)
	{
		this(query, position, true);
	}

	/**
	 * Creates a new <code>ContentAssistProvider</code>.
	 *
	 * @param query The external representation of the Java Persistence query
	 * @param position The position of the cursor in the query
	 * @param applicationAccessible Determines if the application is accessible. If turned on, then
	 * {@link PathExpressionContentAssistVisitor} will be used to add more choices; otherwise it will
	 * be skipped
	 */
	ContentAssistProvider(IQuery query, int position, boolean applicationAccessible)
	{
		super();

		this.query = query;
		this.position = position;
		this.applicationAccessible = applicationAccessible;
	}

	private ContentAssistVisitor buildContentAssistVisitor(ContentAssistItems items,
	                                                       QueryPosition queryPosition)
	{
		return new ContentAssistVisitor(query, items, queryPosition);
	}

	private JPQLExpression buildExpression()
	{
		return new JPQLExpression
		(
			query.getExpression(),
			query.getProvider().getVersion(),
			true // Has to be tolerant to accept an incomplete query
		);
	}

	private PathExpressionContentAssistVisitor buildPathExpressionVisitor(QueryPosition queryPosition)
	{
		return new PathExpressionContentAssistVisitor(query, queryPosition);
	}

	/**
	 * Retrieves the list of the possible choices that match the criteria, which
	 * are the cursor position and how much of the query is written.
	 *
	 * @return The list of possible choices
	 */
	public ContentAssistItems items()
	{
		ContentAssistItems items = new ContentAssistItems();

		// Create a map of the positions within the parsed tree
		JPQLExpression jpqlExpression = buildExpression();
		QueryPosition queryPosition = jpqlExpression.buildPosition(query.getExpression(), position);

		// Visit the expression, which will collect the possible choices
		ContentAssistVisitor visitor1 = buildContentAssistVisitor(items, queryPosition);
		queryPosition.getExpression().accept(visitor1);

		if (applicationAccessible)
		{
			// Visit the expression to collect choices related to path expressions
			PathExpressionContentAssistVisitor visitor2 = buildPathExpressionVisitor(queryPosition);
			queryPosition.getExpression().accept(visitor2);
			visitor2.buildItems(items);
		}

		return items;
	}
}