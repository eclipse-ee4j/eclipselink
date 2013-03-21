/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.queries.ReadAllQuery;

/**
 * This visitor is responsible to populate a {@link ReadAllQuery}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
class ReadAllQueryVisitor extends AbstractObjectLevelReadQueryVisitor {

	/**
	 * Creates a new <code>ReadAllQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param query The {@link ReportQuery} to populate by using this visitor to visit the parsed
	 * tree representation of the JPQL query
	 */
	ReadAllQueryVisitor(JPQLQueryContext queryContext, ReadAllQuery query) {
		super(queryContext, query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HierarchicalQueryClause expression) {

		// START WITH clause
		Expression startWithClause = null;

		if (expression.hasStartWithClause()) {
			startWithClause = queryContext.buildExpression(expression.getStartWithClause());
		}

		// CONNECT BY clause
		Expression connectByClause = queryContext.buildExpression(expression.getConnectByClause());

		// ORDER SIBLINGS BY clause
		List<Expression> orderItems = Collections.<Expression>emptyList();

		if (expression.hasOrderSiblingsByClause()) {
			OrderSiblingsByClauseVisitor visitor = new OrderSiblingsByClauseVisitor();
			expression.getOrderSiblingsByClause().accept(visitor);
			orderItems = visitor.orderByItems;
		}

		// Now add the hierarchical query clause
		ReadAllQuery query = (ReadAllQuery) this.query;
		query.setHierarchicalQueryClause(startWithClause, connectByClause, orderItems);
	}

	private class OrderSiblingsByClauseVisitor extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * The ordered list of items.
		 */
		List<Expression> orderByItems;

		/**
		 * Creates a new <code>OrderSiblingsByClauseVisitor</code>.
		 */
		OrderSiblingsByClauseVisitor() {
			super();
			orderByItems = new ArrayList<Expression>();
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
		public void visit(OrderSiblingsByClause expression) {
			expression.getOrderByItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			orderByItems.add(queryContext.buildExpression(expression));
		}
	}
}