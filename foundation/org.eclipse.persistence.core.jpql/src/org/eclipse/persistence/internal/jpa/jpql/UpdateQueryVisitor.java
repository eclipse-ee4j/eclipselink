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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateItem;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * This builder/visitor is responsible to populate a {@link UpdateAllQuery} when the query is a
 * <b>UPDATE</b> query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class UpdateQueryVisitor extends AbstractModifyAllQueryBuilder<UpdateAllQuery> {

	/**
	 * Creates a new <code>UpdateQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	UpdateQueryVisitor(DefaultJPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	UpdateAllQuery getDatabaseQuery() {
		return super.getDatabaseQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {

		// Create the Expression for the state field path expression
		Expression leftExpression = queryContext.buildQueryExpression(expression.getStateFieldPathExpression());

		// Create the Expression for the new value
		Expression rightExpression = queryContext.buildQueryExpression(expression.getNewValue());

		// Add the expressions to the query
		getDatabaseQuery().addUpdate(leftExpression, rightExpression);
	}
}