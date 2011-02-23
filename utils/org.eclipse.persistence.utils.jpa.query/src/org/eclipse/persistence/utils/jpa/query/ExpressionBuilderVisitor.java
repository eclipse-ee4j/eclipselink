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
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AllOrAnyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticFactor;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.BadExpression;
import org.eclipse.persistence.utils.jpa.query.parser.BetweenExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CaseExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CoalesceExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConcatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConstructorExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CountFunction;
import org.eclipse.persistence.utils.jpa.query.parser.DateTime;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.DivisionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntityTypeLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.EntryExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ExistsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.FuncExpression;
import org.eclipse.persistence.utils.jpa.query.parser.GroupByClause;
import org.eclipse.persistence.utils.jpa.query.parser.HavingClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.InExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IndexExpression;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.KeywordExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LengthExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LikeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LocateExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LowerExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MaxFunction;
import org.eclipse.persistence.utils.jpa.query.parser.MinFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ModExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MultiplicationExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NotExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullIfExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NumericLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.ObjectExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByClause;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.ResultVariable;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SizeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SqrtExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StringLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.SubExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubtractionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.TreatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TrimExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TypeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UnknownExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateItem;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpperExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.parser.WhenClause;
import org.eclipse.persistence.utils.jpa.query.parser.WhereClause;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;

/**
 * This {@link ExpressionVisitor} visits an {@link org.eclipse.jpa.query.parser.Expression JPQL
 * Expression} and creates an {@link org.eclipse.persistence.expressions.Expression Expression}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ExpressionBuilderVisitor implements ExpressionVisitor {

	/**
	 *
	 */
	private ChildrenExpressionVisitor childrenExpressionVisitor;

	/**
	 *
	 */
	private CollectionMemberExpressionVisitor collectionMemberExpressionVisitor;

	/**
	 * This visitor is used to create a modified version of a {@link PathExpressionResolver} that
	 * does not wrap the last path, which is the collection-valued property name.
	 */
	private EmptyCollectionComparisonExpressionVisitor emptyCollectionComparisonExpressionVisitor;

	/**
	 *
	 */
	private GeneralIdentificationVariableExpressionVisitor identificationVariableVisitor;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final QueryBuilderContext queryContext;

	/**
	 * The EclipseLink {@link Expression} that represents a visited parsed
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression Expression}
	 */
	private Expression queryExpression;

	/**
	 *
	 */
	private PathExpressionResolver resolver;

	/**
	 *
	 */
	private boolean resolverOnly;

	/**
	 * This will tell visit(IdentificationVariable) to create a <code>ConstantExpression</code> if
	 * this is part of a comparison expression.
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
	ExpressionBuilderVisitor(QueryBuilderContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	private void appendJoinVariables(org.eclipse.persistence.utils.jpa.query.parser.Expression expression,
	                                 ReportQuery subquery) {

   	queryExpression = null;

   	// TODO: Move this behavior into the QueryBuilderContext where DeclarationExpressionResolver
   	//       will properly register identification variables
 		JoinVariableNameVisitor visitor2 = new JoinVariableNameVisitor();
		expression.accept(visitor2);

		for (String variableName : visitor2.outerVariablesNames()) {
			Expression innerExpression = queryContext.getExpression(variableName);
			Expression outerExpression = queryContext.getParentExpression(variableName);
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

	private Expression buildIdentificationVariableExpression(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
		PathExpressionResolver resolver = identificationVariablePathExpressionResolver(expression);
		return resolver.getExpression();
	}

	private ReportQuery buildSubquery(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {

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
      	queryContext.disposeSubQueryContext();
      }
	}

	private List<org.eclipse.persistence.utils.jpa.query.parser.Expression> children(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
		ChildrenExpressionVisitor visitor = childrenExpressionVisitor();
		try {
			expression.accept(visitor);
			return new ArrayList<org.eclipse.persistence.utils.jpa.query.parser.Expression>(visitor.expressions);
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

	private CollectionMemberExpressionVisitor collectionMemberExpressionVisitor() {
		if (collectionMemberExpressionVisitor == null) {
			collectionMemberExpressionVisitor = new CollectionMemberExpressionVisitor();
		}
		return collectionMemberExpressionVisitor;
	}

	/**
	 * Disposes this visitor.
	 */
	void dispose() {
		this.resolver        = null;
		this.queryExpression = null;
		this.typeExpression  = false;
		this.resolverOnly    = false;
	}

	private EmptyCollectionComparisonExpressionVisitor emptyCollectionComparisonExpressionVisitor() {
		if (emptyCollectionComparisonExpressionVisitor == null) {
			emptyCollectionComparisonExpressionVisitor = new EmptyCollectionComparisonExpressionVisitor();
		}
		return emptyCollectionComparisonExpressionVisitor;
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

	PathExpressionResolver getResolver() {
		return resolver;
	}

	private PathExpressionResolver identificationVariablePathExpressionResolver(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
		GeneralIdentificationVariableExpressionVisitor visitor = identificationVariableVisitor();
		try {
			expression.accept(visitor);
			return visitor.resolver;
		}
		finally {
			visitor.resolver = null;
		}
	}

	private GeneralIdentificationVariableExpressionVisitor identificationVariableVisitor() {
		if (identificationVariableVisitor == null) {
			identificationVariableVisitor = new GeneralIdentificationVariableExpressionVisitor();
		}
		return identificationVariableVisitor;
	}

	private boolean isLeftJoin(Join expression) {
		String identifier = expression.getIdentifier();
		return identifier == Join.LEFT_JOIN ||
		       identifier == Join.LEFT_OUTER_JOIN;
	}

	private boolean isLeftJoinFetch(JoinFetch expression) {
		String identifier = expression.getIdentifier();
		return identifier == JoinFetch.LEFT_JOIN_FETCH ||
		       identifier == JoinFetch.LEFT_OUTER_JOIN_FETCH;
	}

	void setResolverOnly(boolean resolverOnly) {
		this.resolverOnly = resolverOnly;
	}

	private String variableName(org.eclipse.persistence.utils.jpa.query.parser.Expression expression, Type type) {
		return queryContext.variableName(expression, type);
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

		// Now create the logical expression
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

		for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expression.getExpression().getChildren()) {
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

			CollectionMemberExpressionVisitor visitor = collectionMemberExpressionVisitor();

			try {
				// Retrieve the ExpressionBuilder from the collection-valued path expression's
				// identification variable and the variable name
				expression.getCollectionValuedPathExpression().accept(visitor);
				Expression parentExpression = queryExpression;

				// Create the EclipseLink expression
				queryExpression = new ExpressionBuilder().equal(entityExpression);
				queryExpression = parentExpression.noneOf(visitor.variableName, queryExpression);
			}
			finally {
				visitor.variableName = null;
			}
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

		resolver = null;
		IMapping mapping = queryContext.getMapping(expression);

		// Start at 1 since the first path is always an identification variable, and
		// its resolver was created by traversing it
		for (int index = 0, count = expression.pathSize(); index < count; index++) {
			if (index == 0) {
				resolver = identificationVariablePathExpressionResolver(expression.getIdentificationVariable());

				// Reposition the index to 0 because the identification variable is
				// virtual and it is not part of the path
				if (expression.hasVirtualIdentificationVariable()) {
					index--;
				}
			}
			// Any path between the identification variable and the last path (which is a
			// state field path expression) is always a single valued object field path
			else if (index + 1 < count) {
				resolver = new SingleValuedObjectFieldExpressionResolver(resolver, expression.getPath(index));
			}
			// The last path is a collection-valued field expression
			else {
				resolver = new CollectionValuedPathExpressionResolver(resolver, mapping, expression.getPath(index));
			}
		}

		// Now retrieve the Expression
		if (!resolverOnly) {
			queryExpression = resolver.getExpression();
		}
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

		// Now create the comparaison expression
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

		List<org.eclipse.persistence.utils.jpa.query.parser.Expression> expressions = children(expression.getExpression());
		Expression newExpression = null;

		for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expressions) {
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
		String name = variableName(expression.getExpression(), Type.PATH_EXPRESSION_LAST_PATH);

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
		queryExpression = buildIdentificationVariableExpression(expression);
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

		List<org.eclipse.persistence.utils.jpa.query.parser.Expression> expressions = children(expression.getExpression());
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

			for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expressions) {
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

		// The identification variable is virtual, only do something if it fasly represents a state
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
			queryExpression = buildIdentificationVariableExpression(expression);
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

		// Create the IN expression
		InExpressionBuilder builder = new InExpressionBuilder(
			stateFieldPathExpression,
			expression.hasNot(),
			!expression.hasLeftParenthesis()
		);

		expression.getInItems().accept(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {

		String parameterName = expression.getParameter();

		// Calculate the input parameter type
		Class<?> type = queryContext.getParameterType(expression.getRoot(), parameterName);

		// Create the expression
		queryExpression = queryContext.getBaseExpression();
		queryExpression = queryExpression.getParameter(parameterName.substring(1), type);

		// Cache the input parameter type
		queryContext.addInputParameter(parameterName, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {
		resolverOnly = true;
		try {
			expression.getJoinAssociationPath().accept(this);
			resolver.setNullAllowed(isLeftJoin(expression));
			queryExpression = resolver.getExpression();
		}
		finally {
			resolver = null;
			resolverOnly = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinFetch expression) {
		resolverOnly = true;
		try {
			expression.getJoinAssociationPath().accept(this);
			resolver.setNullAllowed(isLeftJoinFetch(expression));
			queryExpression = resolver.getExpression();
		}
		finally {
			resolver = null;
			resolverOnly = false;
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
		queryExpression = buildIdentificationVariableExpression(expression);
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

		// Create the expression from the encapsualted expression
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

		// Add NOT
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
		Class<?> type = queryContext.getType(expression);
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

		// Now create the logical expression
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
		String name = variableName(expression.getExpression(), Type.PATH_EXPRESSION_LAST_PATH);
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

		resolver = null;
		IMapping mapping = queryContext.getMapping(expression);

		// Create a chain of PathExpressionResolver
		for (int index = 0, count = expression.pathSize(); index < count; index++) {

			// The first path is always the general identification variable
			if ((index == 0) && (resolver == null)) {
				resolver = identificationVariablePathExpressionResolver(expression.getIdentificationVariable());

				// Reposition the index to 0 because the identification variable is
				// virtual and it is not part of the path
				if (expression.hasVirtualIdentificationVariable()) {
					index--;
				}
			}
			// Any path between the identification variable and the last path (which is a
			// state field path expression) is always a single valued object field path
			else if (index + 1 < count) {
				resolver = new SingleValuedObjectFieldExpressionResolver(resolver, expression.getPath(index));
			}
			// The last path is a state field path expression
			else {
				resolver = new StateFieldPathExpressionResolver(resolver, mapping, expression.getPath(index));
			}
		}

		// Wrap the PathExpressionResolver so it can check for an enum type
		resolver = new EnumPathExpressionResolver(resolver, queryContext, expression.toString());

		if (!resolverOnly) {
			queryExpression = resolver.getExpression();
		}
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
		queryExpression = buildIdentificationVariableExpression(expression);
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

	private WhenClauseExpressionVisitor whenClauseExpressionVisitor() {
		if (whenClauseExpressionVisitor == null) {
			whenClauseExpressionVisitor = new WhenClauseExpressionVisitor();
		}
		return whenClauseExpressionVisitor;
	}

	private class ChildrenExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The list of {@link org.eclipse.persistence.jpa.query.parser.Expression} that are the
		 * children of an expression.
		 */
		List<org.eclipse.persistence.utils.jpa.query.parser.Expression> expressions;

		/**
		 * Creates a new <code>ChildrenExpressionVisitor</code>.
		 */
		ChildrenExpressionVisitor() {
			super();
			this.expressions = new ArrayList<org.eclipse.persistence.utils.jpa.query.parser.Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expression.getChildren()) {
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
		protected void visit(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
			expressions.add(expression);
		}
	}

	private class CollectionMemberExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The last path in the collection-valued path expression, which is the name of the
		 * relationship mapping.
		 */
		String variableName;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			expression.getIdentificationVariable().accept(ExpressionBuilderVisitor.this);
			variableName = expression.getPath(expression.pathSize() - 1);
		}
	}

	private class EmptyCollectionComparisonExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("null")
		public void visit(CollectionValuedPathExpression expression) {

			PathExpressionResolver resolver = null;

			// Start at 1 since the first path is always an identification variable, and
			// its resolver was created by traversing it
			for (int index = 0, count = expression.pathSize(); index < count; index++) {
				if (index == 0) {
					resolver = identificationVariablePathExpressionResolver(expression.getIdentificationVariable());
				}
				// Any path between the identification variable and the last path (which is a
				// state field path expression) is always a single valued object field path
				else if (index + 1 < count) {
					resolver = new SingleValuedObjectFieldExpressionResolver(resolver, expression.getPath(index));
				}
				// The last path is a collection-valued field expression
				else {
					// Ignore the collection-valued field
				}
			}

			// Now retrieve the Expression
			queryExpression = resolver.getExpression();
		}
	}

	private class GeneralIdentificationVariableExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link PathExpressionResolver} for the visited general identification variable.
		 */
		PathExpressionResolver resolver;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {
			expression.getExpression().accept(this);
			resolver = new EntryPathExpressionResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			resolver = new IdentificationVariableExpressionResolver(
				queryContext.getDeclarationPathExpressionResolver(),
				expression.getText(),
				queryContext
			);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression) {
			expression.getExpression().accept(this);
			resolver = new KeyPathExpressionResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {
			expression.getExpression().accept(this);
			resolver = new ValuePathExpressionResolver(resolver);
		}
	}

	private class InExpressionBuilder extends AnonymousExpressionVisitor {

		private boolean hasNot;
		private boolean singleInputParameter;
		private Expression stateFieldPathExpression;
		private ExpressionVisitor visitor;

		InExpressionBuilder(Expression stateFieldPathExpression,
		                    boolean hasNot,
		                    boolean singleInputParameter) {
			super();

			this.hasNot  = hasNot;
			this.singleInputParameter = singleInputParameter;
			this.visitor = new InItemExpressionVisitor();
			this.stateFieldPathExpression = stateFieldPathExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {

			Collection<Expression> expressions = new ArrayList<Expression>();

			for (org.eclipse.persistence.utils.jpa.query.parser.Expression child : expression.getChildren()) {
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
				visit((org.eclipse.persistence.utils.jpa.query.parser.Expression) expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {

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
			protected void visit(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
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