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
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.ConstructorExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CountFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MaxFunction;
import org.eclipse.persistence.utils.jpa.query.parser.MinFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ObjectExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByClause;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem.Ordering;
import org.eclipse.persistence.utils.jpa.query.parser.ResultVariable;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.parser.WhereClause;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;

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
	final QueryBuilderContext queryContext;

	/**
	 * Creates a new <code>AbstractObjectLevelReadQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	AbstractObjectLevelReadQueryVisitor(QueryBuilderContext queryContext) {
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
	 * Returns the {@link ObjectLevelReadQuery}.
	 *
	 * @return The query being visitor
	 */
	@SuppressWarnings("unchecked")
	T query() {
		return (T) queryContext.getQuery();
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
		Expression queryExpression = queryContext.buildExpression(expression);
		query().setSelectionCriteria(queryExpression);
	}

	private void visitAbstractFromClause(AbstractFromClause expression) {

		ObjectLevelReadQuery query = queryContext.getQuery();

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

		ObjectLevelReadQuery query = queryContext.getQuery();

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

		// SELECT
		expression.getSelectClause().accept(this);

		// WHERE
		if (expression.hasWhereClause()) {
			expression.getWhereClause().accept(this);
		}

		// GROUP BY
		if (expression.hasGroupByClause()) {
			expression.getGroupByClause().accept(this);
		}

		// HAVING
		if (expression.hasHavingClause()) {
			expression.getHavingClause().accept(this);
		}
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
		protected void visit(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
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

			String variableName = queryContext.variableName(expression.getIdentificationVariable(), Type.IDENTIFICATION_VARIABLE);

			if (!queryContext.isIdentificationVariableUsed(variableName)) {
				ObjectLevelReadQuery query = queryContext.getQuery();
				Expression queryExpression = queryContext.getDeclarationPathExpressionResolver().getExpression(variableName);
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
			if (expression.hasJoins()) {
				expression.getJoins().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			String variableName = queryContext.variableName(expression.getIdentificationVariable(), Type.IDENTIFICATION_VARIABLE);

			if (!queryContext.isIdentificationVariableUsed(variableName)) {
				ObjectLevelReadQuery query = queryContext.getQuery();
				Expression queryExpression = queryContext.getDeclarationPathExpressionResolver().getExpression(variableName);
				query.addNonFetchJoinedAttribute(queryExpression);
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
		 * Determines whether the given {@link IMapping} is a direct to field mapping.
		 *
		 * @param mapping The {@link IMapping} to verify if it's a direct to field mapping
		 * @return <code>true</code> if the given {@link IMapping} is a basic, Id or version mapping;
		 * <code>false</code> otherwise
		 */
		private boolean isDirectToField(IMapping mapping) {
			switch (mapping.getMappingType()) {
				case BASIC:
				case ID:
				case VERSION: return true;
				default:      return false;
			}
		}

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
			for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expression.getChildren()) {
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
		protected void visit(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
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
			oneToOneSelected = (mapping != null) && !isDirectToField(mapping);
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

			org.eclipse.persistence.utils.jpa.query.parser.Expression orderByItem = expression.getExpression();
			Expression queryExpression = null;

			// If the order by item is an identification variable or a result variable, then
			// retrieve its variable name so we can check if an Expression was cached
			String variableName = queryContext.variableName(expression, Type.IDENTIFICATION_VARIABLE);

			// Retrieve the cached expression
			if (ExpressionTools.stringIsNotEmpty(variableName)) {
				queryExpression = queryContext.getExpression(variableName);
			}

			// Create the Expression for the order by item if it was not cached
			if (queryExpression == null) {
				queryExpression = queryContext.buildExpression(orderByItem);
			}

			// Create the ordering item
			if (expression.getOrdering() == Ordering.DESC) {
				query().addOrdering(queryExpression.descending());
			}
			else {
				query().addOrdering(queryExpression.ascending());
			}
		}
	}
}