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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.DateConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.internal.jpql.CollectionValuedFieldResolver;
import org.eclipse.persistence.jpa.internal.jpql.EntityResolver;
import org.eclipse.persistence.jpa.internal.jpql.IdentificationVariableResolver;
import org.eclipse.persistence.jpa.internal.jpql.KeyResolver;
import org.eclipse.persistence.jpa.internal.jpql.LiteralType;
import org.eclipse.persistence.jpa.internal.jpql.Resolver;
import org.eclipse.persistence.jpa.internal.jpql.ResolverVisitor;
import org.eclipse.persistence.jpa.internal.jpql.SingleValuedObjectFieldResolver;
import org.eclipse.persistence.jpa.internal.jpql.StateFieldResolver;
import org.eclipse.persistence.jpa.internal.jpql.TreatResolver;
import org.eclipse.persistence.jpa.internal.jpql.ValueResolver;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.MappingTypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This {@link ExpressionVisitor} visits an {@link org.eclipse.jpa.query.parser.Expression JPQL
 * Expression} and creates an {@link org.eclipse.persistence.expressions.Expression Expression}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ExpressionBuilderVisitor implements ExpressionVisitor {

	/**
	 * This visitor creates a list by retrieving either the single child or the children of the
	 * {@link CollectionExpression}, which would be the child.
	 */
	private ChildrenExpressionVisitor childrenExpressionVisitor;

	/**
	 * This visitor is used to create a modified version of a {@link PathExpressionResolver} that
	 * does not wrap the last path, which is the collection-valued property name.
	 */
	private EmptyCollectionComparisonExpressionVisitor emptyCollectionComparisonExpressionVisitor;

	/**
	 * This visitor is responsible to create the right chain of {@link Expression Expressions} for
	 * a state field path expression and for a collection-valued path expression.
	 */
	private ExpressionResolverVisitor expressionResolverVisitor;

	/**
	 * This builder visits the {@link InExpression} in order to create the EclipseLink {@link
	 * Expression}.
	 */
	private InExpressionBuilder inExpressionBuilder;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final DefaultJPQLQueryContext queryContext;

	/**
	 * The EclipseLink {@link Expression} that represents a visited parsed
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression Expression}
	 */
	private Expression queryExpression;

	/**
	 * This will tell {@link #visit(IdentificationVariable)} to create a {@link ConstantExpression}
	 * if this is part of a comparison expression.
	 */
	private boolean typeExpression;

	/**
	 * The visitor responsible to create the {@link Expression Expressions} for the <b>WHEN</b> and
	 * <b>THEN</b> expressions.
	 */
	private WhenClauseExpressionVisitor whenClauseExpressionVisitor;

	/**
	 * Creates a new <code>ExpressionBuilderVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	ExpressionBuilderVisitor(DefaultJPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	private void appendJoinVariables(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression,
	                                 ReportQuery subquery) {

   	queryExpression = null;

   	// TODO: Move this behavior into the QueryBuilderContext where DeclarationExpressionResolver
   	//       will properly register identification variables
 		JoinVariableNameVisitor visitor2 = new JoinVariableNameVisitor();
		expression.accept(visitor2);

		for (String variableName : visitor2.outerVariablesNames()) {
			Expression innerExpression = queryContext.getQueryExpression(variableName);
			Expression outerExpression = queryContext.getParentQueryExpression(variableName);
			Expression equalExpression = innerExpression.equal(outerExpression);

			if (queryExpression == null) {
				queryExpression = equalExpression;
			}
			else {
				queryExpression = queryExpression.and(equalExpression);
			}
		}

		// Aggregate the WHERE clause with the joins expression
		if (queryExpression != null) {
			Expression whereClause = subquery.getSelectionCriteria();

		   if (whereClause != null) {
		   	whereClause = whereClause.and(queryExpression);
		   }
		   else {
		   	whereClause = queryExpression;
		   }

		   subquery.setSelectionCriteria(whereClause);
		}
	}

	private Expression buildExpression(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
		return resolve(queryContext.getResolver(expression));
	}

	private ReportQuery buildSubquery(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {

		// First create the subquery
      ReportQuery subquery = new ReportQuery();
      queryContext.newSubQueryContext(expression, subquery);

      try {
      	// Visit the subquery to populate it
      	expression.accept(queryContext.reportQueryVisitor());

        	// Add the joins between the subquery and the parent query
	      appendJoinVariables(expression, subquery);

	      return subquery;
      }
      finally {
      	queryContext.disposeSubqueryContext();
      }
	}

	private List<org.eclipse.persistence.jpa.internal.jpql.parser.Expression> children(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
		ChildrenExpressionVisitor visitor = childrenExpressionVisitor();
		try {
			expression.accept(visitor);
			return new ArrayList<org.eclipse.persistence.jpa.internal.jpql.parser.Expression>(visitor.expressions);
		}
		finally {
			visitor.expressions.clear();
		}
	}

	private ChildrenExpressionVisitor childrenExpressionVisitor() {
		if (childrenExpressionVisitor == null) {
			childrenExpressionVisitor = new ChildrenExpressionVisitor();
		}
		return childrenExpressionVisitor;
	}

	/**
	 * Disposes this visitor.
	 */
	void dispose() {
		queryExpression = null;
		typeExpression  = false;
	}

	private EmptyCollectionComparisonExpressionVisitor emptyCollectionComparisonExpressionVisitor() {
		if (emptyCollectionComparisonExpressionVisitor == null) {
			emptyCollectionComparisonExpressionVisitor = new EmptyCollectionComparisonExpressionVisitor();
		}
		return emptyCollectionComparisonExpressionVisitor;
	}

	private ExpressionResolverVisitor expressionResolverVisitor() {
		if (expressionResolverVisitor == null) {
			expressionResolverVisitor = new ExpressionResolverVisitor();
		}
		return expressionResolverVisitor;
	}

	/**
	 * Returns the EclipseLink {@link Expression} that represents a visited parsed
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression Expression}.
	 *
	 * @return The EclipseLink {@link Expression}
	 */
	Expression getQueryExpression() {
		return queryExpression;
	}

	private InExpressionBuilder inExpressionBuilder() {
		if (inExpressionBuilder == null) {
			inExpressionBuilder = new InExpressionBuilder();
		}
		return inExpressionBuilder;
	}

	private String literal(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression,
	                       LiteralType type) {

		return queryContext.literal(expression, type);
	}

	/**
	 * Resolves the {@link Expression} of the path expression represented by the given {@link Resolver}.
	 *
	 * @param resolver The {@link Resolver} used to properly navigate a path expression
	 * @return The {@link Expression} representing the path expression
	 */
	Expression resolve(Resolver resolver) {
		ExpressionResolverVisitor visitor = expressionResolverVisitor();
		try {
			resolver.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the ABS expression
		queryExpression = ExpressionMath.abs(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {

		// Create the left side of the addition expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the addition expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the addition expression
		queryExpression = ExpressionMath.add(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {

		// First create the subquery
      ReportQuery subquery = buildSubquery(expression.getExpression());

   	// Now create the ALL|SOME|ANY expression
      String identifier = expression.getIdentifier();
      queryExpression = queryContext.getBaseExpression();

      if (identifier == AllOrAnyExpression.ALL) {
      	queryExpression = queryExpression.all(subquery);
      }
      else if (identifier == AllOrAnyExpression.SOME) {
      	queryExpression = queryExpression.some(subquery);
      }
      else if (identifier == AllOrAnyExpression.ANY) {
      	queryExpression = queryExpression.any(subquery);
      }
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {

		// Create the left side of the logical expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the logical expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the AND expression
		queryExpression = leftExpression.and(rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {

		// First create the Expression that is prepended with the unary sign
		expression.getExpression().accept(this);
		Expression arithmeticFactor = queryExpression;

		// Create an expression for the constant 0 (zero)
		queryExpression = new ConstantExpression(0, new ExpressionBuilder());

		// "- <something>" is really "0 - <something>"
		queryExpression = ExpressionMath.subtract(queryExpression, arithmeticFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the AVG expression distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the AVG expression
		queryExpression = queryExpression.average();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {

		// First create the Expression for the result expression
		expression.getExpression().accept(this);
		Expression resultExpression = queryExpression;

		// Create the expression for the lower bound expression
		expression.getLowerBoundExpression().accept(this);
		Expression lowerBoundExpression = queryExpression;

		// Create the expression for the upper bound expression
		expression.getUpperBoundExpression().accept(this);
		Expression upperBoundExpression = queryExpression;

		// Create the BETWEEN expression
		if (expression.hasNot()) {
			queryExpression = resultExpression.notBetween(lowerBoundExpression, upperBoundExpression);
		}
		else {
			queryExpression = resultExpression.between(lowerBoundExpression, upperBoundExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {

		// Create the case operand expression
		expression.getCaseOperand().accept(this);
		Expression caseOperandExpression = queryExpression;

		WhenClauseExpressionVisitor visitor = whenClauseExpressionVisitor();

		try {
			// Create the WHEN clauses
			expression.getWhenClauses().accept(visitor);

			// Create the ELSE clauses
			expression.getElseExpression().accept(this);
			Expression elseExpression = queryExpression;

			// Create the CASE expression
			if (caseOperandExpression != null) {
				queryExpression = caseOperandExpression.caseStatement(visitor.whenClauses, elseExpression);
			}
			else {
				queryExpression = queryContext.getBaseExpression().caseStatement(visitor.whenClauses, elseExpression);
			}
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {

		List<Expression> expressions = new ArrayList<Expression>();

		for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expression.getExpression().getChildren()) {
			child.accept(this);
			expressions.add(queryExpression);
		}

		queryExpression = queryContext.getBaseExpression();
		queryExpression = queryExpression.coalesce(expressions);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {
		// Nothing to do, this should be handled by the owning expression
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {

		// Create the expression for the entity expression
		expression.getEntityExpression().accept(this);
		Expression entityExpression = queryExpression;

		if (expression.hasNot())  {

			// Retrieve the ExpressionBuilder from the collection-valued path expression's
			// identification variable and the variable name
			String variableName = literal(
				expression.getCollectionValuedPathExpression(),
				LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
			);

			Expression parentExpression = queryContext.getQueryExpression(variableName);

			// Now create the actual expression
			variableName = literal(
				expression.getCollectionValuedPathExpression(),
				LiteralType.PATH_EXPRESSION_LAST_PATH
			);

			queryExpression = new ExpressionBuilder().equal(entityExpression);
			queryExpression = parentExpression.noneOf(variableName, queryExpression);
		}
		else {
			// Create the expression for the collection-valued path expression
			expression.getCollectionValuedPathExpression().accept(this);

			// Create the MEMBER OF expression
			queryExpression = queryExpression.equal(entityExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {
		queryExpression = buildExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {

		// Create the left side of the comparison expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the comparison expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Make sure the flag is set to false
		typeExpression = false;

		// Now create the comparison expression
		String comparaison = expression.getComparisonOperator();

		// =
		if (comparaison == ComparisonExpression.EQUAL) {
			queryExpression = leftExpression.equal(rightExpression);
		}
		// <>
		else if (comparaison == ComparisonExpression.DIFFERENT) {
			queryExpression = leftExpression.notEqual(rightExpression);
		}
		// <
		else if (comparaison == ComparisonExpression.LOWER_THAN) {
			queryExpression = leftExpression.lessThan(rightExpression);
		}
		// <=
		else if (comparaison == ComparisonExpression.LOWER_THAN_OR_EQUAL) {
			queryExpression = leftExpression.lessThanEqual(rightExpression);
		}
		// >
		else if (comparaison == ComparisonExpression.GREATER_THAN) {
			queryExpression = leftExpression.greaterThan(rightExpression);
		}
		// <=
		else if (comparaison == ComparisonExpression.GREATER_THAN_OR_EQUAL) {
			queryExpression = leftExpression.greaterThanEqual(rightExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {

		List<org.eclipse.persistence.jpa.internal.jpql.parser.Expression> expressions = children(expression.getExpression());
		Expression newExpression = null;

		for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expressions) {
			child.accept(this);

			if (newExpression == null) {
				newExpression = queryExpression;
			}
			else {
				newExpression = newExpression.concat(queryExpression);
			}
		}

		queryExpression = newExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Create the COUNT expression
		queryExpression = queryExpression.count();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {

		if (expression.isJDBCDate()) {
			queryExpression = queryContext.getBaseExpression();
			queryExpression = new DateConstantExpression(expression.getText(), queryExpression);
		}
		else {
			queryExpression = queryContext.getBaseExpression();

			if (expression.isCurrentDate()) {
				queryExpression = queryExpression.currentDateDate();
			}
			else if (expression.isCurrentTime()) {
				queryExpression = queryExpression.currentTime();
			}
			else if (expression.isCurrentTimestamp()) {
				queryExpression = queryExpression.currentTimeStamp();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {

		// Create the left side of the division expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the division expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the division expression
		queryExpression = ExpressionMath.divide(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {

		// Retrieve the collection-valued field name
		String name = literal(
			expression.getExpression(),
			LiteralType.PATH_EXPRESSION_LAST_PATH
		);

		// Create the expression for the collection-valued path expression
		expression.getExpression().accept(emptyCollectionComparisonExpressionVisitor());

		// Create the IS EMPTY expression
		if (expression.hasNot()) {
			queryExpression = queryExpression.notEmpty(name);
		}
		else {
			queryExpression = queryExpression.isEmpty(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {
		ClassDescriptor descriptor = queryContext.getSession().getClassDescriptorForAlias(expression.getEntityTypeName());
		queryExpression = new ConstantExpression(descriptor.getJavaClass(), queryContext.getBaseExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {

		// Create the expression for the collection-valued path expression
		expression.getExpression().accept(this);

		// Now create the ENTRY expression
		MapEntryExpression entryExpression = new MapEntryExpression(queryExpression);
		entryExpression.returnMapEntry();
		queryExpression = entryExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {

		// First create the subquery
      ReportQuery subquery = buildSubquery(expression.getExpression());

		// Replace the SELECT clause of the exists subquery by SELECT 1 to avoid problems with
      // databases not supporting multiple columns in the subquery SELECT clause in SQL. The
      // original select clause expressions might include relationship navigations which should
      // result in FK joins in the generated SQL, e.g. ... EXISTS (SELECT o.customer FROM Order
      // o ...). Add the select clause expressions as non fetch join attributes to the ReportQuery
      // representing the subquery. This make sure the FK joins get generated
		for (ReportItem item : subquery.getItems()) {
			Expression expr = item.getAttributeExpression();
			subquery.addNonFetchJoinedAttribute(expr);
		}

		subquery.clearItems();

		Expression one = new ConstantExpression(1, new ExpressionBuilder());
		subquery.addItem("one", one);
		subquery.dontUseDistinct();

		// Now create the EXIST expression
		queryExpression = queryContext.getBaseExpression();

		if (expression.hasNot()) {
			queryExpression = queryExpression.notExists(subquery);
		}
		else {
			queryExpression = queryExpression.exists(subquery);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {

		List<org.eclipse.persistence.jpa.internal.jpql.parser.Expression> expressions = children(expression.getExpression());
		String functionName = expression.getUnquotedFunctionName();

		// No arguments
		if (expressions.isEmpty()) {
			queryExpression = queryContext.getBaseExpression();
			queryExpression = queryExpression.getFunction(functionName);
		}
		// One or more arguments
		else {
			// Create the Expressions for the rest
			Vector<Expression> queryExpressions = new Vector<Expression>(expressions.size());

			for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expressions) {
				child.accept(this);
				queryExpressions.add(queryExpression);
			}

			queryExpression = queryExpressions.remove(0);
			queryExpression = queryExpression.getFunctionWithArguments(functionName, queryExpressions);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariable expression) {

		// The identification variable is virtual, only do something if it falsely represents a state
		// field path expression
		if (expression.isVirtual()) {
			StateFieldPathExpression stateFieldPathExpression = expression.getStateFieldPathExpression();

			if (stateFieldPathExpression != null) {
				stateFieldPathExpression.accept(this);
			}
		}
		// Abstract schema name
		else if (typeExpression) {
			typeExpression = false;
			ClassDescriptor descriptor = queryContext.getSession().getClassDescriptorForAlias(expression.getText());
			queryExpression = new ConstantExpression(descriptor.getJavaClass(), queryContext.getBaseExpression());
		}
		// Identification variable
		else {
			queryExpression = buildExpression(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclaration expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {

		// Create the expression for the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the INDEX expression
		queryExpression = queryExpression.index();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {

		// Create the expression for the state field path expression
		expression.getExpression().accept(this);
		Expression stateFieldPathExpression = queryExpression;

		// Visit the IN expression
		visitInExpression(expression, stateFieldPathExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {

		String parameterName = expression.getParameter();

		// Calculate the input parameter type
		IType type = queryContext.getParameterType(expression);
		Class<?> javaType = queryContext.getJavaType(type);

		// Create the expression
		queryExpression = queryContext.getBaseExpression();
		queryExpression = queryExpression.getParameter(parameterName.substring(1), javaType);

		// Cache the input parameter type
		queryContext.addInputParameter(parameterName, javaType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {

		ExpressionResolverVisitor visitor = expressionResolverVisitor();
		Resolver resolver = queryContext.getResolver(expression.getJoinAssociationPath());
		boolean oldNullAllowed = resolver.isNullAllowed();

		try {
			resolver.setNullAllowed(expression.isLeftJoin());
			resolver.accept(visitor);
			queryExpression = visitor.expression;
		}
		finally {
			visitor.expression = null;
			resolver.setNullAllowed(oldNullAllowed);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinFetch expression) {

		ExpressionResolverVisitor visitor = expressionResolverVisitor();
		Resolver resolver = queryContext.getResolver(expression.getJoinAssociationPath());
		boolean oldNullAllowed = resolver.isNullAllowed();

		try {
			resolver.setNullAllowed(expression.isLeftJoinFetch());
			resolver.accept(visitor);
			queryExpression = visitor.expression;
		}
		finally {
			visitor.expression = null;
			resolver.setNullAllowed(oldNullAllowed);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {

		// First visit the parent Expression
		expression.getExpression().accept(this);

		// Now create the Expression of the KEY expression
		queryExpression = new MapEntryExpression(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {

		String keyword = expression.getText();
		Object value;

		if (keyword == KeywordExpression.NULL) {
			value = null;
		}
		else if (keyword == KeywordExpression.TRUE) {
			value = Boolean.TRUE;
		}
		else {
			value = Boolean.FALSE;
		}

		queryExpression = queryContext.getBaseExpression();
		queryExpression = new ConstantExpression(value, queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the LENGTH expression
		queryExpression = queryExpression.length();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {

		// Create the first expression
		expression.getStringExpression().accept(this);
		Expression firstExpression = queryExpression;

		// Create the expression for the pattern value
		expression.getPatternValue().accept(this);
		Expression patternValue = queryExpression;

		// Create the LIKE expression with the escape character
		if (expression.hasEscapeCharacter()) {
			expression.getEscapeCharacter().accept(this);
			queryExpression = firstExpression.like(patternValue, queryExpression);
		}
		// Create the LIKE expression with no escape character
		else {
			queryExpression = firstExpression.like(patternValue);
		}

		// Negate the expression
		if (expression.hasNot()) {
			queryExpression = queryExpression.not();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpression expression) {

		// Create the string to find in the find in expression
		expression.getFirstExpression().accept(this);
		Expression findExpression = queryExpression;

		// Create the find in string expression
		expression.getSecondExpression().accept(this);
		Expression findInExpression = queryExpression;

		// Create the expression for the start position
		expression.getThirdExpression().accept(this);
		Expression startPositionExpression = queryExpression;

		// Create the LOCATE expression
		if (startPositionExpression != null) {
			queryExpression = findInExpression.locate(findExpression, startPositionExpression);
		}
		else {
			queryExpression = findInExpression.locate(findExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the LOWER expression
		queryExpression = queryExpression.toLowerCase();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the MAX expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the MAX expression
		queryExpression = queryExpression.maximum();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the MIN expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the MIN expression
		queryExpression = queryExpression.minimum();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {

		// First create the Expression for the first expression
		expression.getFirstExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Now create the Expression for the second expression
		expression.getSecondExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the MOD expression
		queryExpression = ExpressionMath.mod(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {

		// Create the left side of the multiplication expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the multiplication expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the multiplication expression
		queryExpression = ExpressionMath.multiply(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {

		// Create the expression
		expression.getExpression().accept(this);

		// Negate the expression
		queryExpression = queryExpression.not();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {

		// Create the expression first
		expression.getExpression().accept(this);

		// Mark is as IS NULL
		queryExpression = queryExpression.isNull();

		// Negate the expression
		if (expression.hasNot()) {
			queryExpression = queryExpression.not();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
		queryExpression = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {

		// Create the first expression
		expression.getFirstExpression().accept(this);
		Expression firstExpression = queryExpression;

		// Create the second expression
		expression.getSecondExpression().accept(this);
		Expression secondExpression = queryExpression;

		// Now create the NULLIF expression
		queryExpression = firstExpression.nullIf(secondExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {

		// Instantiate a Number object with the value
		Class<?> type = queryContext.getJavaType(expression);
		Number number = queryContext.newInstance(type, String.class, expression.getText());

		// Now create the numeric expression
		queryExpression = new ConstantExpression(number, queryContext.getBaseExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {
		// Simply traverse the OBJECT's expression
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItem expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {

		// Create the left side of the logical expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the logical expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the OR expression
		queryExpression = leftExpression.or(rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclaration expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariable expression) {
		// Simply traverse the select expression
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {

		// First create the subquery
      ReportQuery subquery = buildSubquery(expression);

     	// Now wrap the subquery
     	queryExpression = queryContext.getBaseExpression();
     	queryExpression = queryExpression.subQuery(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {

		// Create the right chain of expressions
		expression.getExpression().accept(this);

		// Now create the SIZE expression
		String name = literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_LAST_PATH);
		queryExpression = queryExpression.size(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the SQRT expression
		queryExpression = ExpressionMath.sqrt(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {
		queryExpression = buildExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {
		queryExpression = queryContext.getBaseExpression();
		queryExpression = new ConstantExpression(expression.getUnquotedText(), queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpression expression) {

		// Create the first expression
		expression.getFirstExpression().accept(this);
		Expression firstExpression = queryExpression;

		// Create the second expression
		expression.getSecondExpression().accept(this);
		Expression secondExpression = queryExpression;

		// Create the third expression
		expression.getThirdExpression().accept(this);
		Expression thirdExpression = queryExpression;

		// Now create the SUBSTRING expression
		if (thirdExpression != null){
			queryExpression = firstExpression.substring(secondExpression, thirdExpression);
		}
		else {
			queryExpression = firstExpression.substring(secondExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {

		// Create the left side of the subtraction expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the subtraction expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the subtraction expression
		queryExpression = ExpressionMath.subtract(leftExpression, rightExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the SUM expression distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the SUM expression
		queryExpression = queryExpression.sum();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {

		// Create the TRIM character expression
		expression.getTrimCharacter().accept(this);
		Expression trimCharacter = queryExpression;

		// Create the string to trim
		expression.getExpression().accept(this);
		Expression stringExpression = queryExpression;

		switch (expression.getSpecification()) {

			case LEADING: {
				if (trimCharacter != null) {
					queryExpression = stringExpression.leftTrim(trimCharacter);
				}
				else {
					queryExpression = stringExpression.leftTrim();
				}
				break;
			}

			case TRAILING: {
				if (trimCharacter != null) {
					queryExpression = stringExpression.rightTrim(trimCharacter);
				}
				else {
					queryExpression = stringExpression.rightTrim();
				}
				break;
			}

			default: {
				if (trimCharacter != null) {
					queryExpression = stringExpression.trim(trimCharacter);
				}
				else {
					queryExpression = stringExpression.trim();
				}
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {

		// First create the Expression for the identification variable
		expression.getExpression().accept(this);

		// Create the TYPE expression
		queryExpression = queryExpression.type();

		// This will tell visit(IdentificationVariable) to create a ConstantExpression
		// if this is part of a comparison expression
		typeExpression = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {
		queryExpression = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItem expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the UPPER expression
		queryExpression = queryExpression.toUpperCase();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	private void visitInExpression(InExpression expression, Expression stateFieldPathExpression) {

		InExpressionBuilder visitor = inExpressionBuilder();

		try {
			visitor.stateFieldPathExpression = stateFieldPathExpression;
			visitor.singleInputParameter     = !expression.hasLeftParenthesis();
			visitor.hasNot                   = expression.hasNot();

			expression.getInItems().accept(visitor);
		}
		finally {
			visitor.singleInputParameter     = false;
			visitor.hasNot                   = false;
			visitor.stateFieldPathExpression = null;
		}
	}

	private WhenClauseExpressionVisitor whenClauseExpressionVisitor() {
		if (whenClauseExpressionVisitor == null) {
			whenClauseExpressionVisitor = new WhenClauseExpressionVisitor();
		}
		return whenClauseExpressionVisitor;
	}

	/**
	 * This visitor creates a list by retrieving either the single child or the children of the
	 * {@link CollectionExpression}, which would be the child.
	 */
	private class ChildrenExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The list of {@link org.eclipse.persistence.jpa.query.parser.Expression} that are the
		 * children of an expression.
		 */
		List<org.eclipse.persistence.jpa.internal.jpql.parser.Expression> expressions;

		/**
		 * Creates a new <code>ChildrenExpressionVisitor</code>.
		 */
		ChildrenExpressionVisitor() {
			super();
			this.expressions = new ArrayList<org.eclipse.persistence.jpa.internal.jpql.parser.Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expression.getChildren()) {
				expressions.add(child);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			// Can't be added to the list
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
			expressions.add(expression);
		}
	}

	private class EmptyCollectionComparisonExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			// Start at 1 since the first path is always an identification variable, and
			// its resolver was created by traversing it
			for (int index = 0, count = expression.pathSize(); index < count; index++) {

				// The first path is the identification variable, lets ExpressionBuilderVisitor
				// create the EclipseLink expression
				if (index == 0) {
					expression.getIdentificationVariable().accept(ExpressionBuilderVisitor.this);
				}
				// Any path between the identification variable and the last path (which is a
				// state field path expression) is always a single valued object field path
				else if (index + 1 < count) {
					queryExpression.get(expression.getPath(index));
				}
				// The last path is ignored
				else {
					break;
				}
			}
		}
	}

	private class ExpressionResolverVisitor implements ResolverVisitor {

		/**
		 * The EclipseLink {@link Expression} that resulted by visited the chain of {@link Resolver
		 * Resolvers}.
		 */
		Expression expression;

		private Enum<?> buildEnumConstant(Class<?> type, String constant) {

			Object[] constants = type.getEnumConstants();

			for (int index = constants.length; --index >= 0; ) {
				Enum<?> enumConstant = (Enum<?>) constants[index];
				if (constant.equals(enumConstant.name())) {
					return enumConstant;
				}
			}

			return null;
		}

		private void buildEnumExpression(Resolver resolver, IType type, String constant) {
			Class<?> javaType = queryContext.getJavaType(type);
			Enum<?> enumConstant = buildEnumConstant(javaType, constant);
			expression = new ConstantExpression(enumConstant, new ExpressionBuilder());
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(CollectionValuedFieldResolver resolver) {

			String path = resolver.getPath();
			IType type = resolver.getType();

			if (resolver.isEnumType()) {
				buildEnumExpression(resolver, resolver.getType(), path);
			}
			else {
				// First visit the parent Expression
				resolver.getParent().accept(this);

				// Now retrieve the Expression for the collection-valued field
				if (MappingTypeHelper.isCollectionMapping(resolver.getMapping())) {
					if (resolver.isNullAllowed()) {
						expression = expression.anyOfAllowingNone(path);
					}
					else {
						expression = expression.anyOf(path);
					}
				}
				else {
					if (resolver.isNullAllowed()) {
						expression = expression.getAllowingNull(path);
					}
					else {
						expression = expression.get(path);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(EntityResolver resolver) {
			String abstractSchemaName = resolver.getAbstractSchemaName();
			ClassDescriptor descriptor = queryContext.getDescriptor(abstractSchemaName);
			expression = new ExpressionBuilder(descriptor.getJavaClass());
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IdentificationVariableResolver resolver) {

			String variableName = resolver.getVariableName();
			expression = queryContext.getQueryExpression(variableName);

			if (expression == null) {

				// First visit the parent Expression
				resolver.getParent().accept(this);

				// Cache the identification variable information to prevent recreating the Expression
				queryContext.addUsedIdentificationVariable(variableName);
				queryContext.addQueryExpression(variableName, expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(KeyResolver resolver) {

			// First visit the parent Expression
			resolver.getParent().accept(this);

			// Now Create the MapEntry expression
			expression = new MapEntryExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(SingleValuedObjectFieldResolver resolver) {

			// First visit the parent Expression
			resolver.getParent().accept(this);

			// Now retrieve the Expression for the singled-valued object field
			expression = expression.get(resolver.getPath());
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(StateFieldResolver resolver) {

			String path = resolver.getPath();
			IType type = resolver.getType();

			if (resolver.isEnumType()) {
				buildEnumExpression(resolver, resolver.getType(), path);
			}
			else {
				// First visit the parent Expression
				resolver.getParent().accept(this);

				// Now retrieve the Expression for the state field
				if (MappingTypeHelper.isCollectionMapping(resolver.getMapping())) {
					if (resolver.isNullAllowed()) {
						expression = expression.anyOfAllowingNone(path);
					}
					else {
						expression = expression.anyOf(path);
					}
				}
				else {
					if ((resolver.isNullAllowed() || resolver.getParent().isNullAllowed()) &&
					    MappingTypeHelper.isRelationshipMapping(resolver.getMapping())) {

						expression = expression.getAllowingNull(path);
					}
					else {
						expression = expression.get(path);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatResolver resolver) {

			// First visit the parent Expression
			resolver.getParent().accept(this);

			// Now downcast the Expression
			ClassDescriptor entityType = queryContext.getDescriptor(resolver.getEntityTypeName());
			expression = expression.as(entityType.getJavaClass());
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(ValueResolver resolver) {
			resolver.getParent().accept(this);
		}
	}

	/**
	 * This visitor takes care of creating the <b>IN</b> expression.
	 */
	private class InExpressionBuilder extends AnonymousExpressionVisitor {

		private boolean hasNot;
		private boolean singleInputParameter;
		private Expression stateFieldPathExpression;
		private ExpressionVisitor visitor;

		/**
		 * Creates a new <code>InExpressionBuilder</code>.
		 */
		InExpressionBuilder() {
			super();
			visitor = new InItemExpressionVisitor();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {

			Collection<Expression> expressions = new ArrayList<Expression>();

			for (org.eclipse.persistence.jpa.internal.jpql.parser.Expression child : expression.getChildren()) {
				child.accept(visitor);
				expressions.add(queryExpression);
			}

			if (hasNot) {
				queryExpression = stateFieldPathExpression.notIn(expressions);
			}
			else {
				queryExpression = stateFieldPathExpression.in(expressions);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {

			if (singleInputParameter) {
				expression.accept(ExpressionBuilderVisitor.this);

				if (hasNot) {
					queryExpression = stateFieldPathExpression.notIn(queryExpression);
				}
				else {
					queryExpression = stateFieldPathExpression.in(queryExpression);
				}
			}
			else {
				visit((org.eclipse.persistence.jpa.internal.jpql.parser.Expression) expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {

			expression.accept(ExpressionBuilderVisitor.this);

			Collection<Expression> expressions = new ArrayList<Expression>();
			expressions.add(queryExpression);

			// Now create the IN expression
			if (hasNot) {
				queryExpression = stateFieldPathExpression.notIn(expressions);
			}
			else {
				queryExpression = stateFieldPathExpression.in(expressions);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {

			// First create the subquery
	      ReportQuery subquery = buildSubquery(expression);

      	// Now create the IN expression
			if (hasNot) {
				queryExpression = stateFieldPathExpression.notIn(subquery);
			}
			else {
				queryExpression = stateFieldPathExpression.in(subquery);
			}
		}

		private class InItemExpressionVisitor extends AnonymousExpressionVisitor {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void visit(IdentificationVariable expression) {
				ClassDescriptor descriptor = queryContext.getDescriptor(expression.getText());
				queryExpression = queryContext.getBaseExpression();
				queryExpression = new ConstantExpression(descriptor.getJavaClass(), queryExpression);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void visit(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
				expression.accept(ExpressionBuilderVisitor.this);
			}
		}
	}

	private class JoinVariableNameVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 *
		 */
		private boolean gatheringInnerVariable;

		/**
		 *
		 */
		private Set<String> innerVariables;

		/**
		 *
		 */
		private Set<String> outerVariableNames;

		/**
		 * Creates a new <code>JoinVariableNameVisitor</code>.
		 */
		JoinVariableNameVisitor() {
			super();
			this.innerVariables     = new HashSet<String>();
			this.outerVariableNames = new HashSet<String>();
		}

		/**
		 * Returns the identification variables that are used within the subquery and are defined in
		 * the top-level query.
		 *
		 * @return The outer identification variables
		 */
		Set<String> outerVariablesNames() {
			outerVariableNames.removeAll(innerVariables);
			return outerVariableNames;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			// Nothing to gather
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {

			expression.getCollectionValuedPathExpression().accept(this);

			gatheringInnerVariable = true;
			expression.getIdentificationVariable().accept(this);
			gatheringInnerVariable = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			expression.getIdentificationVariable().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			if (gatheringInnerVariable && !expression.isVirtual()) {
				innerVariables.add(expression.getText().toLowerCase());
			}
			else if (!gatheringInnerVariable) {
				outerVariableNames.add(expression.getText().toLowerCase());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			expression.getRangeVariableDeclaration().accept(this);
			expression.getJoins().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			expression.getJoinAssociationPath().accept(this);

			gatheringInnerVariable = true;
			expression.getIdentificationVariable().accept(this);
			gatheringInnerVariable = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {

			expression.getAbstractSchemaName().accept(this);

			gatheringInnerVariable = true;
			expression.getIdentificationVariable().accept(this);
			gatheringInnerVariable = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			expression.getFromClause().accept(this);
			expression.getWhereClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			expression.getIdentificationVariable().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {
			expression.getConditionalExpression().accept(this);
		}
	}

	/**
	 * This visitor is responsible to create the {@link Expression Expressions} for the <b>WHEN</b>
	 * and <b>THEN</b> expressions.
	 */
	private class WhenClauseExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The map of <b>WHEN</b> expressions mapped to their associated <b>THEN</b> expression.
		 */
		Map<Expression, Expression> whenClauses;

		/**
		 * Creates a new <code>WhenClauseExpressionVisitor</code>.
		 */
		WhenClauseExpressionVisitor() {
			super();
			whenClauses = new LinkedHashMap<Expression, Expression>();
		}

		/**
		 * Disposes this visitor.
		 */
		void dispose() {
			whenClauses.clear();
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
		public void visit(WhenClause expression) {

			// Create the WHEN expression
			expression.getWhenExpression().accept(ExpressionBuilderVisitor.this);
			Expression whenExpression = queryExpression;

			// Create the THEN expression
			expression.getThenExpression().accept(ExpressionBuilderVisitor.this);
			Expression thenExpression = queryExpression;

			whenClauses.put(whenExpression, thenExpression);
		}
	}
}