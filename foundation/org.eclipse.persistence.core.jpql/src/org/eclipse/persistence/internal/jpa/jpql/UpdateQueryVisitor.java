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
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * This builder is responsible to populate a {@link UpdateAllQuery}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class UpdateQueryVisitor extends AbstractModifyAllQueryBuilder {

	/**
	 * Creates a new <code>UpdateQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	UpdateQueryVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		expression.acceptChildren(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		expression.getRangeVariableDeclaration().accept(this);
		expression.getUpdateItems().accept(this);
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
		((UpdateAllQuery) query).addUpdate(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression) {

		expression.getUpdateClause().accept(this);

		if (expression.hasWhereClause()) {
			expression.getWhereClause().accept(this);
		}
	}
}