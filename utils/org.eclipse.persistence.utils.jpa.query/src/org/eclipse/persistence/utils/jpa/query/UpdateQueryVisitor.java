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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateItem;

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
	UpdateQueryVisitor(QueryBuilderContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	UpdateAllQuery query() {
		return super.query();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {

		// Create the Expression for the state field path expression
		Expression leftExpression = queryContext.buildExpression(expression.getStateFieldPathExpression());

		// Create the Expression for the new value
		Expression rightExpression = queryContext.buildExpression(expression.getNewValue());

		// Add the expressions to the query
		query().addUpdate(leftExpression, rightExpression);
	}
}