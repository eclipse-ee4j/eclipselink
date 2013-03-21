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

import java.util.Collection;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.history.AsOfSCNClause;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
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
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor is responsible to populate an {@link ObjectLevelReadQuery} by traversing a {@link
 * org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression} representing a
 * <b>SELECT</b> query.
 *
 * @see ObjectLevelReadQueryVisitor
 * @see ReportQueryVisitor
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
abstract class AbstractObjectLevelReadQueryVisitor extends AbstractEclipseLinkExpressionVisitor {

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
	 * @param query The {@link ObjectLevelReadQuery} to populate by using this visitor to visit the
	 * parsed tree representation of the JPQL query
	 */
	AbstractObjectLevelReadQueryVisitor(JPQLQueryContext queryContext, ObjectLevelReadQuery query) {
		super();
		this.query = query;
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AsOfClause expression) {

		Expression queryExpression = queryContext.buildExpression(expression);
		org.eclipse.persistence.history.AsOfClause asOfClause;

		if (expression.hasScn()) {
			asOfClause = new AsOfSCNClause(queryExpression);
		}
		else {
			asOfClause = new org.eclipse.persistence.history.AsOfClause(queryExpression);
		}

		query.setAsOfClause(asOfClause);
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
	public void visit(FromClause expression) {
		visitAbstractFromClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		visitIdentificationVariable(expression);
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
	public void visit(OrderByClause expression) {
		expression.getOrderByItems().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {
		Expression queryExpression = queryContext.buildExpression(expression);
		query.addOrdering(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		// Select couple flags
		visitAbstractSelectClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {

		// Handle SELECT/FROM/WHERE clauses
		visitAbstractSelectStatement(expression);

		// ORDER BY clause
		if (expression.hasOrderByClause()) {
			expression.getOrderByClause().accept(this);
		}

		// UNION clauses
		if (expression.hasUnionClauses()) {
			expression.getUnionClauses().accept(this);
		}
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
		// Select couple flags
		visitAbstractSelectClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {
		// Handle SELECT/FROM/WHERE
		visitAbstractSelectStatement(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnionClause expression) {

		ReportQuery subquery = queryContext.buildSubquery((SimpleSelectStatement) expression.getQuery());
		Expression union = null;

		if (expression.isUnion()) {
			if (expression.hasAll()) {
				union = query.getExpressionBuilder().unionAll(subquery);
			}
			else {
				union = query.getExpressionBuilder().union(subquery);
			}
		}
		else if (expression.isIntersect()) {
			if (expression.hasAll()) {
				union = query.getExpressionBuilder().intersectAll(subquery);
			}
			else {
				union = query.getExpressionBuilder().intersect(subquery);
			}
		}
		else if (expression.isExcept()) {
			if (expression.hasAll()) {
				union = query.getExpressionBuilder().exceptAll(subquery);
			}
			else {
				union = query.getExpressionBuilder().except(subquery);
			}
		}

		query.addUnionExpression(union);
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

		// Add join expressions to the query (but not the join fetch expressions)
		JoinVisitor visitor = new JoinVisitor();
		expression.accept(visitor);

		// Visit the AS OF clause
		if (expression.hasAsOfClause()) {
			expression.getAsOfClause().accept(this);
		}

		// Visit the hierarchical clause
		if (expression.hasHierarchicalQueryClause()) {
			expression.getHierarchicalQueryClause().accept(this);
		}
	}

	void visitAbstractSelectClause(AbstractSelectClause expression) {

		// DISTINCT
		if (expression.hasDistinct()) {

			CountFunctionVisitor visitor = new CountFunctionVisitor();
			expression.accept(visitor);

			if (!visitor.hasCountFunction) {
				query.useDistinct();
			}
		}

		// Indicate on the query if "return null if primary key null".
		// This means we want nulls returned if we expect an outer join
		// True:  SELECT employee.address FROM ..... // Simple 1:1
		// True:  SELECT a.b.c.d FROM ..... // where a->b, b->c and c->d are all 1:1.
		// False: SELECT OBJECT(employee) FROM ..... // simple SELECT
		// False: SELECT phoneNumber.areaCode FROM ..... // direct-to-field
		OneToOneSelectedVisitor visitor = new OneToOneSelectedVisitor();
		expression.accept(visitor);
		query.setShouldBuildNullForNullPk(visitor.oneToOneSelected);

		// Now visit the select expression
		expression.getSelectExpression().accept(this);
	}

	void visitAbstractSelectStatement(AbstractSelectStatement expression) {

		// First visit the FROM clause in order to retrieve the reference classes and
		// create an ExpressionBuilder for each abstract schema name
		expression.getFromClause().accept(this);
		expression.getSelectClause().accept(this);

		if (expression.hasWhereClause()) {
			expression.getWhereClause().accept(this);
		}
	}

	void visitIdentificationVariable(IdentificationVariable expression) {

		String variableName = expression.getVariableName();

		// Retrieve the join fetches that were defined in the same identification variable
		// declaration, if the identification variable is mapped to a join, then there will
		// not be any join fetch associated with it
		Collection<Join> joinFetches = queryContext.getJoinFetches(variableName);

		if (joinFetches != null ) {

			for (Join joinFetch : joinFetches) {

				// Retrieve the join association path expression's identification variable
				String joinFetchVariableName = queryContext.literal(
					joinFetch,
					LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
				);

				// Both identification variables are the same.
				// Then add the join associated path expression as a joined attribute
				// Example: FROM Employee e JOIN FETCH e.employees
				if (variableName.equals(joinFetchVariableName)) {
					org.eclipse.persistence.expressions.Expression queryExpression = queryContext.buildExpression(joinFetch);
					query.addJoinedAttribute(queryExpression);
				}
			}
		}
	}

	private class CountFunctionVisitor extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * Determines whether the single {@link org.eclipse.persistence.jpa.jpql.parser.Expression
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

	private class JoinVisitor extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * Cache the Expression associated with the identification variable so it can be used in the
		 * visit(Join) in order to properly create the ON clause expression.
		 */
		private Expression baseExpression;

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

				// Add the ON clause to the expression
				if (expression.hasOnClause()) {
					Expression onClause = queryContext.buildExpression(expression.getOnClause());

					// Create the JOIN expression using the base Expression
					if (expression.isLeftJoin()) {
						queryExpression = baseExpression.leftJoin(queryExpression, onClause);
					}
					else {
						queryExpression = baseExpression.join(queryExpression, onClause);
					}
				}

				// Add the FETCH expression to the Expression
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
			baseExpression = addNonFetchJoinedAttribute(
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
		 * Determines whether the visited {@link org.eclipse.persistence.jpa.jpql.parser.Expression
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
}