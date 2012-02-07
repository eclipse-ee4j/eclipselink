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

import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor is responsible to complete the initialization of the {@link ReportQuery} by visiting
 * the expression clauses.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ReportQueryVisitor extends AbstractReadAllQueryVisitor {

	/**
	 * The visitor is responsible to add the group items to the query.
	 */
	private GroupByVisitor groupByVisitor;

	/**
	 * The visitor is responsible to create the attributes by visiting the select items.
	 */
	private ReportItemBuilder selectItemsBuilder;

	/**
	 * This array is used to store the type of the select {@link Expression JPQL Expression} that is
	 * converted into an {@link Expression EclipseLink Expression}.
	 */
	Class<?>[] type;

	/**
	 * Creates a new <code>ReportQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ReportQueryVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
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
	private ReportItemBuilder selectItemsBuilder() {
		if (selectItemsBuilder == null) {
			selectItemsBuilder = new ReportItemBuilder(queryContext);
		}
		return selectItemsBuilder;
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
		((ReportQuery) query).setHavingExpression(queryContext.buildExpression(expression));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void visitAbstractSelectClause(AbstractSelectClause expression) {
		super.visitAbstractSelectClause(expression);
		visitSelectClause(expression);
	}

	private void visitSelectClause(AbstractSelectClause expression) {

		ReportItemBuilder builder = selectItemsBuilder();
		Class previousType = builder.type[0];
		ReportQuery previousQuery = builder.query;
		try {
			builder.type[0] = null;
			builder.query   = (ReportQuery) query;

			expression.accept(builder);

			type[0] = builder.type[0];
		}
		finally {
			builder.query   = previousQuery;
			builder.type[0] = previousType;
		}
	}

	/**
	 * This visitor is responsible to add the grouping {@link Expression Expressions} by traversing
	 * the group items.
	 */
	private class GroupByVisitor extends EclipseLinkAnonymousExpressionVisitor {

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
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			((ReportQuery) query).addGrouping(queryContext.buildExpression(expression));
		}
	}
}