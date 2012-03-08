/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This visitor is responsible to populate a {@link ReadAllQuery} by traversing a {@link
 * org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression} representing a
 * <b>SELECT</b> query.
 *
 * @see ReadAllQueryVisitor
 * @see ReportQueryVisitor
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractReadAllQueryVisitor extends AbstractEclipseLinkExpressionVisitor {

	/**
	 * This visitor is responsible to determine if the select expression is the <code>COUNT</code> function.
	 */
	private CountFunctionVisitor countFunctionVisitor;

	/**
	 * This visitor is responsible to add the non-fetch joined attributes to the query.
	 */
	private JoinExpressionVisitor joinVisitor;

	/**
	 * This visitor is responsible to determine if there is a one-to-one relationship selected.
	 */
	private OneToOneSelectedVisitor oneToOneSelectedVisitor;

	/**
	 * This visitor is responsible to add the order items to the query.
	 */
	private OrderByVisitor orderByVisitor;

	/**
	 * The {@link ObjectLevelReadQuery} to populate.
	 */
	ObjectLevelReadQuery query;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	final JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>ReadAllQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	AbstractReadAllQueryVisitor(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	private CountFunctionVisitor countFunctionVisitor() {
		if (countFunctionVisitor == null) {
			countFunctionVisitor = new CountFunctionVisitor();
		}
		return countFunctionVisitor;
	}

	private boolean hasNotCountFunction(AbstractSelectClause expression) {
		CountFunctionVisitor visitor = countFunctionVisitor();
		expression.accept(visitor);
		return visitor.hasCountFunction;
	}

	/**
	 * Determines whether there is a one-to-one relationship selected. This includes a chain of
	 * relationships.
	 * <p>
	 * <ul>
	 * <li>True: SELECT employee.address FROM ..... // Simple 1:1
	 * <li>True: SELECT a.b.c.d FROM ..... // where a->b, b->c and c->d are all 1:1.
	 * <li>False: SELECT OBJECT(employee) FROM ..... // simple SELECT
	 * <li>False: SELECT phoneNumber.areaCode FROM ..... // direct-to-field
	 * </ul>
	 *
	 * @param expression The select expression to introspect.
	 * @return <code>true</code> if the select expression represents a relationship; <code>false</code>
	 * otherwise
	 */
	private boolean hasOneToOneSelected(AbstractSelectClause expression) {
		OneToOneSelectedVisitor visitor = oneToOneSelectedVisitor();
		expression.accept(visitor);
		return visitor.oneToOneSelected;
	}

	private JoinExpressionVisitor joinVisitor() {
		if (joinVisitor == null) {
			joinVisitor = new JoinExpressionVisitor();
		}
		return joinVisitor;
	}

	private OneToOneSelectedVisitor oneToOneSelectedVisitor() {
		if (oneToOneSelectedVisitor == null) {
			oneToOneSelectedVisitor = new OneToOneSelectedVisitor();
		}
		return oneToOneSelectedVisitor;
	}

	private OrderByVisitor orderyByVisitor() {
		if (orderByVisitor == null) {
			orderByVisitor = new OrderByVisitor();
		}
		return orderByVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		visitAbstractFromClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {
		expression.accept(orderyByVisitor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		visitAbstractSelectClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {

		// SELECT FROM WHERE GROUP BY HAVING
		visitAbstractSelectStatement(expression);

		// ORDER BY
		if (expression.hasOrderByClause()) {
			expression.getOrderByClause().accept(this);
		}

		// Add join expressions to the query (but not the join fetch expressions)
		expression.getFromClause().accept(joinVisitor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		visitAbstractFromClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		visitAbstractSelectClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {

		// SELECT FROM WHERE GROUP BY HAVING
		visitAbstractSelectStatement(expression);

		// Add join expressions to the query (but not the join fetch expressions)
		expression.getFromClause().accept(joinVisitor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		query.setSelectionCriteria(queryContext.buildExpression(expression));
	}

	void visitAbstractFromClause(AbstractFromClause expression) {

		// Set the ExpressionBuilder
		Expression baseExpression = queryContext.getBaseExpression();
		ExpressionBuilder expressionBuilder = baseExpression.getBuilder();
		query.setExpressionBuilder(expressionBuilder);

		// Set the reference class if it's not set
		if (query.getReferenceClass() == null) {
			query.setReferenceClass(expressionBuilder.getQueryClass());
			query.changeDescriptor(queryContext.getSession());
		}
	}

	void visitAbstractSelectClause(AbstractSelectClause expression) {

		// DISTINCT
		if (expression.hasDistinct() && !hasNotCountFunction(expression)) {
			query.useDistinct();
		}

		// Indicate on the query if "return null if primary key null".
		// This means we want nulls returned if we expect an outer join
		boolean buildNullForNullPK = hasOneToOneSelected(expression);
		query.setShouldBuildNullForNullPk(buildNullForNullPK);
	}

	void visitAbstractSelectStatement(AbstractSelectStatement expression) {

		// First visit the FROM clause in order to retrieve the reference classes and
		// create an ExpressionBuilder for each abstract schema name
		expression.getFromClause().accept(this);
		expression.getSelectClause().accept(this);

		if (expression.hasWhereClause()) {
			expression.getWhereClause().accept(this);
		}

		if (expression.hasGroupByClause()) {
			expression.getGroupByClause().accept(this);
		}

		if (expression.hasHavingClause()) {
			expression.getHavingClause().accept(this);
		}
	}

	private class CountFunctionVisitor extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * Determines whether the single {@link org.eclipse.persistence.jpa.query.parser.Expression
		 * Expression} is the <b>COUNT</b> expression.
		 */
		boolean hasCountFunction;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression) {
			hasCountFunction = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			hasCountFunction = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression) {
			expression.getSelectExpression().accept(this);
		}
	}

	private class JoinExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

		private Expression addNonFetchJoinedAttribute(org.eclipse.persistence.jpa.jpql.parser.Expression expression,
		                                              IdentificationVariable identificationVariable) {

			String variableName = identificationVariable.getVariableName();

			// Always add the expression, as it may not be defined elsewhere,
			// unless it has already been defined as the builder.
			Expression queryExpression = queryContext.getQueryExpression(variableName);

			if (queryExpression == null) {
				queryExpression = queryContext.buildExpression(expression);
				queryContext.addQueryExpression(variableName, queryExpression);
			}

			ObjectLevelReadQuery query = (ObjectLevelReadQuery) queryContext.getDatabaseQuery();

			if (query.getExpressionBuilder() != queryExpression) {
				query.addNonFetchJoinedAttribute(queryExpression);
			}

			return queryExpression;
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
		public void visit(CollectionMemberDeclaration expression) {
			addNonFetchJoinedAttribute(
				expression,
				(IdentificationVariable) expression.getIdentificationVariable()
			);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {

			expression.getRangeVariableDeclaration().accept(this);

			if (expression.hasJoins()) {
				expression.getJoins().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			if (expression.hasIdentificationVariable()) {

				IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
				Expression queryExpression = addNonFetchJoinedAttribute(expression, identificationVariable);

				if (expression.hasOnClause()) {

					Expression onClause = queryContext.buildExpression(expression.getOnClause());

					if (expression.isLeftJoin()) {
						queryExpression = queryExpression.leftJoin(queryExpression, onClause);
					}
					else {
						queryExpression = queryExpression.join(queryExpression, onClause);
					}
				}

				if (expression.hasFetch()) {

					String variableName = identificationVariable.getVariableName();
					queryExpression = queryContext.getQueryExpression(variableName);

					if (queryExpression == null) {
						queryExpression = queryContext.buildExpression(expression);
						queryContext.addQueryExpression(variableName, queryExpression);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			addNonFetchJoinedAttribute(
				expression,
				(IdentificationVariable) expression.getIdentificationVariable()
			);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
		}
	}

	private class OneToOneSelectedVisitor extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * Determines whether the visited {@link org.eclipse.persistence.jpa.query.parser.Expression
		 * Expression} represents a relationship.
		 */
		boolean oneToOneSelected;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
				child.accept(this);
				if (oneToOneSelected) {
					break;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpression expression) {
			expression.getConstructorItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression) {
			oneToOneSelected = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			oneToOneSelected = !queryContext.isRangeIdentificationVariable(expression.getVariableName());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression) {
			oneToOneSelected = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MaxFunction expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ObjectExpression expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			oneToOneSelected = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			DatabaseMapping mapping = queryContext.resolveMapping(expression);
			oneToOneSelected = (mapping != null) && !mapping.isDirectToFieldMapping();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {
			oneToOneSelected = true;
		}
	}

	/**
	 * This visitor is responsible to create the ordering {@link Expression Expressions} by
	 * traversing the order by items.
	 */
	private class OrderByVisitor extends AbstractEclipseLinkExpressionVisitor {

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
		public void visit(OrderByClause expression) {
			expression.getOrderByItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression) {

			// Create the order by item expression
			Expression queryExpression = queryContext.buildExpression(expression.getExpression());

			// Create the ordering item
			if (expression.getOrdering() == Ordering.DESC) {
				query.addOrdering(queryExpression.descending());
			}
			else if (expression.getOrdering() == Ordering.ASC) {
				query.addOrdering(queryExpression.ascending());
			}
			else {
				query.addOrdering(queryExpression);
			}
		}
	}
}