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
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.internal.jpql.LiteralType;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.MappingTypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This visitor is responsible to populate a {@link ObjectLevelReadQuery} by traversing a
 * {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression} representing a
 * <b>SELECT</b> query.
 *
 * @see ObjectLevelReadQueryVisitor
 * @see ReportQueryVisitor
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractObjectLevelReadQueryVisitor<T extends ObjectLevelReadQuery> extends AbstractExpressionVisitor {

	/**
	 * This visitor is responsible to determine if the select expression is the <code>COUNT</code> function.
	 */
	private CountFunctionVisitor countFunctionVisitor;

	/**
	 * This visitor is responsible to add the non-fetch joined attributes to the query.
	 */
	private JoinExpressionVisitor joinExpressionVisitor;

	/**
	 * This visitor is responsible to determine if there is a one-to-one relationship selected.
	 */
	private OneToOneSelectedVisitor oneToOneSelectedVisitor;

	/**
	 * This visitor is responsible to add the order items to the query.
	 */
	private OrderByVisitor orderByVisitor;

	/**
	 * The context used to query information about the application metadata.
	 */
	final DefaultJPQLQueryContext queryContext;

	/**
	 * Creates a new <code>AbstractObjectLevelReadQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	AbstractObjectLevelReadQueryVisitor(DefaultJPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	private CountFunctionVisitor countFunctionVisitor() {
		if (countFunctionVisitor == null) {
			countFunctionVisitor = new CountFunctionVisitor();
		}
		return countFunctionVisitor;
	}

	/**
	 * Returns the {@link ObjectLevelReadQuery}.
	 *
	 * @return The query being visitor
	 */
	@SuppressWarnings("unchecked")
	T getDatabaseQuery() {
		return (T) queryContext.getDatabaseQuery();
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
	 * @param expression The select expression to instrospect.
	 * @return <code>true</code> if the select expression represents a relationship; <code>false</code>
	 * otherwise
	 */
	private boolean hasOneToOneSelected(AbstractSelectClause expression) {
		OneToOneSelectedVisitor visitor = oneToOneSelectedVisitor();
		expression.accept(visitor);
		return visitor.oneToOneSelected;
	}

	private JoinExpressionVisitor joinExpressionVisitor() {
		if (joinExpressionVisitor == null) {
			joinExpressionVisitor = new JoinExpressionVisitor();
		}
		return joinExpressionVisitor;
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
		expression.getOrderByClause().accept(this);

		// Add join expressions to the query (but not the join fetch expressions)
		expression.getFromClause().accept(joinExpressionVisitor());
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
		expression.getFromClause().accept(joinExpressionVisitor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		Expression queryExpression = queryContext.buildQueryExpression(expression);
		getDatabaseQuery().setSelectionCriteria(queryExpression);
	}

	private void visitAbstractFromClause(AbstractFromClause expression) {

		ObjectLevelReadQuery query = queryContext.getDatabaseQuery();

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

	private void visitAbstractSelectClause(AbstractSelectClause expression) {

		ObjectLevelReadQuery query = queryContext.getDatabaseQuery();

		// DISTINCT
		if (expression.hasDistinct() && !hasNotCountFunction(expression)) {
			query.useDistinct();
		}

		// Indicate on the query if "return null if primary key null".
		// This means we want nulls returned if we expect an outer join
		boolean buildNullForNullPK = hasOneToOneSelected(expression);
		query.setShouldBuildNullForNullPk(buildNullForNullPK);
	}

	private void visitAbstractSelectStatement(AbstractSelectStatement expression) {

		// First visit the FROM clause in order to retrieve the reference classes and
		// create an ExpressionBuilder for each abstract schema name
		expression.getFromClause().accept(this);
		expression.getSelectClause().accept(this);
		expression.getWhereClause().accept(this);
		expression.getGroupByClause().accept(this);
		expression.getHavingClause().accept(this);
	}

	private class CountFunctionVisitor extends AnonymousExpressionVisitor {

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
		protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
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

	private class JoinExpressionVisitor extends AbstractExpressionVisitor {

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

			String variableName = queryContext.literal(
				expression.getIdentificationVariable(),
				LiteralType.IDENTIFICATION_VARIABLE
			);

			if (!queryContext.isIdentificationVariableUsed(variableName)) {
				ObjectLevelReadQuery query = queryContext.getDatabaseQuery();
				Expression queryExpression = queryContext.getQueryExpression(variableName);
				if (queryExpression == null) {
					queryExpression = queryContext.buildQueryExpression(expression.getCollectionValuedPathExpression());
				}
				query.addNonFetchJoinedAttribute(queryExpression);
			}
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
			expression.getJoins().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			String variableName = queryContext.literal(
				expression.getIdentificationVariable(),
				LiteralType.IDENTIFICATION_VARIABLE
			);

			if (!queryContext.isIdentificationVariableUsed(variableName)) {
				Expression queryExpression = queryContext.getQueryExpression(variableName);
				if (queryExpression == null) {
					queryExpression = queryContext.buildQueryExpression(expression);
					queryContext.addQueryExpression(variableName, queryExpression);
				}
				getDatabaseQuery().addNonFetchJoinedAttribute(queryExpression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
		}
	}

	private class OneToOneSelectedVisitor extends AnonymousExpressionVisitor {

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
			for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expression.getChildren()) {
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
			oneToOneSelected = !queryContext.isRangeIdentificationVariable(expression.getText());
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
		protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
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
			IMapping mapping = queryContext.getMapping(expression);
			oneToOneSelected = !MappingTypeHelper.isPropertyMapping(mapping);
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
	private class OrderByVisitor extends AbstractExpressionVisitor {

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

			Expression queryExpression = null;

			// If the order by item is an identification variable or a result variable, then
			// retrieve its variable name so we can check if an Expression was cached
			String variableName = queryContext.literal(
				expression.getExpression(),
				LiteralType.IDENTIFICATION_VARIABLE
			);

			// Retrieve the cached expression
			if (ExpressionTools.stringIsNotEmpty(variableName)) {
				queryExpression = queryContext.getQueryExpression(variableName);
			}

			// Create the Expression for the order by item if it was not cached
			if (queryExpression == null) {
				queryExpression = queryContext.buildQueryExpression(expression.getExpression());
			}

			// Create the ordering item
			if (expression.getOrdering() == Ordering.DESC) {
				getDatabaseQuery().addOrdering(queryExpression.descending());
			}
			else {
				getDatabaseQuery().addOrdering(queryExpression.ascending());
			}
		}
	}
}