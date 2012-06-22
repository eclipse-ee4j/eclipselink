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
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor is responsible to complete the initialization of the {@link ReportQuery} by visiting
 * the expression clauses.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ReportQueryVisitor extends AbstractObjectLevelReadQueryVisitor<ReportQuery> {

	/**
	 * The visitor is responsible to add the group items to the query.
	 */
	private GroupByVisitor groupByVisitor;

	/**
	 * The visitor is responsible to create the attributes by visiting the select items.
	 */
	private ReportItemExpressionBuilder reportItemBuilder;

	/**
	 * Creates a new <code>ReportQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	ReportQueryVisitor(DefaultJPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ReportQuery getDatabaseQuery() {
		return queryContext.getDatabaseQuery();
	}

	private GroupByVisitor groupByVisitor() {
		if (groupByVisitor == null) {
			groupByVisitor = new GroupByVisitor();
		}
		return groupByVisitor;
	}

	/**
	 * Returns the builder that is responsible to create the {@link Expression Expressions} for each
	 * of the select items.
	 *
	 * @return The builder used for the select items
	 */
	private ExpressionVisitor reportItemBuilder() {
		if (reportItemBuilder == null) {
			reportItemBuilder = new ReportItemExpressionBuilder(queryContext);
		}
		return reportItemBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {
		expression.accept(groupByVisitor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {
		Expression queryExpression = queryContext.buildQueryExpression(expression);
		getDatabaseQuery().setHavingExpression(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		super.visit(expression);
		expression.accept(reportItemBuilder());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		super.visit(expression);
		expression.accept(reportItemBuilder());
	}

	/**
	 * This visitor is responsible to add the grouping {@link Expression Expressions} by traversing
	 * the group items.
	 */
	private class GroupByVisitor extends AnonymousExpressionVisitor {

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
		public void visit(GroupByClause expression) {
			expression.getGroupByItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
			Expression queryExpression = queryContext.buildQueryExpression(expression);
			getDatabaseQuery().addGrouping(queryExpression);
		}
	}
}