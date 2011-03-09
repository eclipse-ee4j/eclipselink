/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql;

import org.eclipse.persistence.jpa.internal.jpql.parser.ContentAssistVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.jpql.ContentAssistItems;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * This provider is responsible to find the possible choices in order to continue the query where
 * the cursor is positioned.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class ContentAssistProvider {

	/**
	 * The position of the cursor in the query used to determine the possible choices.
	 */
	private final int position;

	/**
	 * The model object holding onto the query, which is <code>null</code> when this object is used
	 * by the unit-tests.
	 */
	private final IQuery query;

	/**
	 * Creates a new <code>ContentAssistProvider</code>.
	 *
	 * @param query The external representation of the Java Persistence query
	 * @param position The position of the cursor in the query
	 */
	public ContentAssistProvider(IQuery query, int position) {
		super();
		this.query    = query;
		this.position = position;
	}

	/**
	 * Retrieves the list of the possible choices which match the two criteria: the cursor
	 * position and how much of the query is written.
	 *
	 * @return The container of possible choices
	 */
	public ContentAssistItems items() {

		DefaultContentAssistItems items = new DefaultContentAssistItems();

		// Parse the JPQL query
		String jpqlQuery = query.getExpression();
		JPQLExpression jpqlExpression = parse(jpqlQuery);

		// Create a map of the positions within the parsed tree
		QueryPosition queryPosition = jpqlExpression.buildPosition(jpqlQuery, position);

		// Create a context that is used to cache the information so it is calculated only once
		JPQLQueryContext queryContext = new JPQLQueryContext();
		queryContext.setCurrentQuery(jpqlExpression);

		// Visit the expression, which will collect the possible choices
		ContentAssistVisitor visitor1 = new ContentAssistVisitor(query, jpqlExpression, queryContext, items, queryPosition);
		queryPosition.getExpression().accept(visitor1);

		// Visit the expression to collect choices related to path expressions
		PathExpressionContentAssistVisitor visitor2 = new PathExpressionContentAssistVisitor(query, queryPosition);
		queryPosition.getExpression().accept(visitor2);
		visitor2.buildItems(items);

		return items;
	}

	private JPQLExpression parse(String jpqlQuery) {
		return new JPQLExpression(jpqlQuery, query.getProvider().getVersion(), true);
	}
}