/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.queries.ModifyAllQuery;

/**
 * This builder is responsible to initialize a modify all query.
 *
 * @see DeleteQueryVisitor
 * @see UpdateQueryVisitor
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractModifyAllQueryBuilder<T extends ModifyAllQuery> extends AbstractTraverseChildrenVisitor {

	/**
	 * The context used to query information about the application metadata.
	 */
	final DefaultJPQLQueryContext queryContext;

	/**
	 * Creates a new <code>AbstractModifyAllQueryBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	AbstractModifyAllQueryBuilder(DefaultJPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	/**
	 * Returns the {@link ModifyAllQuery}.
	 *
	 * @return The query being visitor
	 */
	@SuppressWarnings("unchecked")
	T getDatabaseQuery() {
		return (T) queryContext.getDatabaseQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		T query = getDatabaseQuery();

		// Set the ExpressionBuilder
		Expression baseExpression = queryContext.getBaseExpression();
		ExpressionBuilder expressionBuilder = baseExpression.getBuilder();
		query.setExpressionBuilder(expressionBuilder);

		// Set the reference class if it's not set
		if (query.getReferenceClass() == null) {
			query.setReferenceClass(expressionBuilder.getQueryClass());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		Expression queryExpression = queryContext.buildQueryExpression(expression);
		getDatabaseQuery().setSelectionCriteria(queryExpression);
	}
}