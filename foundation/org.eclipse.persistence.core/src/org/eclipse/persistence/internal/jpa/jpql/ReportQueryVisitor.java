/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor is responsible to populate a {@link ReportQuery}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ReportQueryVisitor extends ReadAllQueryVisitor {

	/**
	 * This array is used to store the type of the select {@link org.eclipse.persistence.jpa.jpql
	 * parser.Expression JPQL Expression} that is converted into an {@link org.eclipse.persistence.
	 * expressions.Expression EclipseLink Expression}.
	 */
	Class<?> type;

	/**
	 * Creates a new <code>ReportQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param query The {@link ReportQuery} to populate by using this visitor to visit the parsed
	 * tree representation of the JPQL query
	 */
	ReportQueryVisitor(JPQLQueryContext queryContext, ReportQuery query) {
		super(queryContext, query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {
		GroupByVisitor visitor = new GroupByVisitor();
		expression.accept(visitor);
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

		// First set couple flags on the query
		super.visitAbstractSelectClause(expression);

		// Now visit the select expressions and create the corresponding EclipseLink Expressions
		ReportItemBuilder builder = new ReportItemBuilder(queryContext, (ReportQuery) query);
		expression.accept(builder);
		type = builder.type[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void visitAbstractSelectStatement(AbstractSelectStatement expression) {

		super.visitAbstractSelectStatement(expression);

		if (expression.hasHavingClause()) {
			expression.getHavingClause().accept(this);
		}

		if (expression.hasGroupByClause()) {
			expression.getGroupByClause().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void visitIdentificationVariable(IdentificationVariable expression) {
		// ReportQuery does not require it
	}

	/**
	 * This visitor is responsible to add the {@link org.eclipse.persistence.jpa.jpql.parser.Expression
	 * group items} by traversing the group items.
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