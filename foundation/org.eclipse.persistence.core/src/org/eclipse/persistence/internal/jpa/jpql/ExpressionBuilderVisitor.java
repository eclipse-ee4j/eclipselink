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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.DateConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OnClause;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This {@link ExpressionVisitor} visits an {@link org.eclipse.persistence.jpa.jpql.parser.Expression
 * JPQL Expression} and creates the corresponding {@link org.eclipse.persistence.expressions.
 * Expression EclipseLink Expression}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ExpressionBuilderVisitor implements EclipseLinkExpressionVisitor {

	/**
	 * This visitor creates a list by retrieving either the single child or the children of the
	 * {@link CollectionExpression}, which would be the child.
	 */
	private ChildrenExpressionVisitor childrenExpressionVisitor;

	/**
	 * Determines whether the target relationship is allowed to be <code>null</code>.
	 */
	private boolean nullAllowed;

	/**
	 * This {@link Comparator} compares two {@link Class} values and returned the appropriate numeric
	 * type that takes precedence.
	 */
	private Comparator<Class<?>> numericTypeComparator;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * The EclipseLink {@link Expression} that represents a visited parsed {@link org.eclipse
	 * persistence.jpa.query.parser.Expression Expression}
	 */
	private Expression queryExpression;

	/**
	 * Keeps track of the type of an expression while traversing it.
	 */
	private final Class<?>[] type;

	/**
	 * The visitor responsible to create the {@link Expression Expressions} for the <b>WHEN</b> and
	 * <b>THEN</b> expressions.
	 */
	private WhenClauseExpressionVisitor whenClauseExpressionVisitor;

	/**
	 * Creates a new <code>ExpressionBuilderVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ExpressionBuilderVisitor(JPQLQueryContext queryContext) {
		super();
		this.type = new Class<?>[1];
		this.queryContext = queryContext;
	}

	private void appendJoinVariables(org.eclipse.persistence.jpa.jpql.parser.Expression expression,
	                                 ReportQuery subquery) {

		queryExpression = null;

		for (String variableName : collectOuterIdentificationVariables()) {
			Expression innerExpression = queryContext.getQueryExpression(variableName);
			Expression outerExpression = queryContext.getParent().getQueryExpressionImp(variableName);
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

	/**
	 * Creates a new EclipseLink {@link Expression} by visiting the given JPQL {@link org.eclipse.
	 * persistence.jpa.jpql.parser.Expression Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * convert into an EclipseLink {@link Expression}
	 * @param type The type of the expression
	 * @return The EclipseLink {@link Expression} of the JPQL fragment
	 */
	Expression buildExpression(org.eclipse.persistence.jpa.jpql.parser.Expression expression,
	                           Class<?>[] type) {

		Class<?> oldType = this.type[0];
		Expression oldQueryExpression = queryExpression;

		try {
			this.type[0]         = null;
			this.queryExpression = null;

			expression.accept(this);

			type[0] = this.type[0];
			return queryExpression;
		}
		finally {
			this.type[0]         = oldType;
			this.queryExpression = oldQueryExpression;
		}
	}

	/**
	 * Creates a new EclipseLink {@link Expression} by visiting the given JPQL {@link
	 * CollectionValuedPathExpression} that is used in the <code><b>GROUP BY</b></code> clause.
	 *
	 * @param expression The {@link CollectionValuedPathExpression} to convert into an EclipseLink
	 * {@link Expression}
	 * @return The EclipseLink {@link Expression} representation of that path expression
	 */
	Expression buildGroupByExpression(CollectionValuedPathExpression expression) {


		try {
			PathResolver resolver     = new PathResolver();
			resolver.length           = expression.pathSize() - 1;
			resolver.nullAllowed      = false;
			resolver.checkMappingType = false;

			expression.accept(resolver);

			return resolver.localExpression;
		}
		finally {
			this.type[0]         = null;
			this.queryExpression = null;
		}
	}

	/**
	 * Creates a new EclipseLink {@link Expression} by visiting the given JPQL {@link
	 * StateFieldPathExpression}. This method temporarily changes the null allowed flag if the state
	 * field is a foreign reference mapping
	 *
	 * @param expression The {@link StateFieldPathExpression} to convert into an EclipseLink {@link
	 * Expression}
	 * @return The EclipseLink {@link Expression} representation of that path expression
	 */
	Expression buildModifiedPathExpression(StateFieldPathExpression expression) {


		try {
			PathResolver resolver     = new PathResolver();
			resolver.length           = expression.pathSize();
			resolver.checkMappingType = true;

			expression.accept(resolver);

			return resolver.localExpression;
		}
		finally {
			this.type[0]         = null;
			this.queryExpression = null;
		}
	}

	/**
	 * Creates a new {@link ReportQuery} by visiting the given {@link SimpleSelectStatement}.
	 *
	 * @param expression The {@link SimpleSelectStatement} to convert into a {@link ReportQuery}
	 * @return A fully initialized {@link ReportQuery}
	 */
	ReportQuery buildSubquery(SimpleSelectStatement expression) {

		// First create the subquery
		ReportQuery subquery = new ReportQuery();
		queryContext.newSubQueryContext(expression, subquery);

		try {
			// Visit the subquery to populate it
			ReportQueryVisitor visitor = new ReportQueryVisitor(queryContext, subquery);
			expression.accept(visitor);
			type[0] = visitor.type;

			// Add the joins between the subquery and the parent query
			appendJoinVariables(expression, subquery);

			return subquery;
		}
		finally {
			queryContext.disposeSubqueryContext();
		}
	}

	private List<org.eclipse.persistence.jpa.jpql.parser.Expression> children(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		ChildrenExpressionVisitor visitor = childrenExpressionVisitor();
		try {
			expression.accept(visitor);
			return new LinkedList<org.eclipse.persistence.jpa.jpql.parser.Expression>(visitor.expressions);
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

	private Set<String> collectOuterIdentificationVariables() {

		// Retrieve the identification variables used in the current query
		Set<String> variableNames = new HashSet<String>(queryContext.getUsedIdentificationVariables());

		// Now remove the local identification variables that are defined in JOIN expressions and
		// in collection member declarations
		for (Declaration declaration : queryContext.getDeclarations()) {
			variableNames.remove(declaration.getVariableName());
		}

		return variableNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the ABS expression
		queryExpression = ExpressionMath.abs(queryExpression);

		// Note: The type will be calculated when traversing the ABS's expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		ClassDescriptor descriptor = queryContext.getDescriptor(expression.getText());
		type[0] = descriptor.getJavaClass();
		queryExpression = new ExpressionBuilder(type[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {

		List<Class<?>> types = new ArrayList<Class<?>>(2);

		// Create the left side of the addition expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;
		types.add(type[0]);

		// Create the right side of the addition expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;
		types.add(type[0]);

		// Now create the addition expression
		queryExpression = ExpressionMath.add(leftExpression, rightExpression);

		// Set the expression type
		Collections.sort(types, NumericTypeComparator.instance());
		type[0] = types.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression) {

		// First create the subquery
		ReportQuery subquery = buildSubquery((SimpleSelectStatement) expression.getExpression());

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

		// Note: The type will be calculated when traversing the ABS's expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression) {

		// Create the left side of the logical expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the logical expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the AND expression
		queryExpression = leftExpression.and(rightExpression);

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {

		// First create the Expression that is prepended with the unary sign
		expression.getExpression().accept(this);
		Expression arithmeticFactor = queryExpression;

		// Create an expression for the constant 0 (zero)
		queryExpression = new ConstantExpression(0, new ExpressionBuilder());

		// "- <something>" is really "0 - <something>"
		queryExpression = ExpressionMath.subtract(queryExpression, arithmeticFactor);

		// Note: The type will be calculated when traversing the sub-expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AsOfClause expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the AVG expression distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the AVG expression
		queryExpression = queryExpression.average();

		// Set the expression type
		type[0] = Double.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {

		Expression caseOperandExpression = null;

		// Create the case operand expression
		if (expression.hasCaseOperand()) {
			expression.getCaseOperand().accept(this);
			caseOperandExpression = queryExpression;
		}

		WhenClauseExpressionVisitor visitor = whenClauseExpressionVisitor();

		try {
			// Create the WHEN clauses
			expression.getWhenClauses().accept(visitor);

			// Create the ELSE clause
			expression.getElseExpression().accept(this);
			Expression elseExpression = queryExpression;
			visitor.types.add(type[0]);

			// Create the CASE expression
			if (caseOperandExpression != null) {
				queryExpression = caseOperandExpression.caseStatement(visitor.whenClauses, elseExpression);
			}
			else {
				queryExpression = queryContext.getBaseExpression();
				queryExpression = queryExpression.caseStatement(visitor.whenClauses, elseExpression);
			}

			// Set the expression type
			type[0] = queryContext.typeResolver().compareCollectionEquivalentTypes(visitor.types);
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CastExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the CAST expression
		org.eclipse.persistence.jpa.jpql.parser.Expression databaseType = expression.getDatabaseType();
		queryExpression = queryExpression.cast(databaseType.toParsedText());

		// Set the expression type
		type[0] = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {

		List<Expression> expressions = new ArrayList<Expression>();
		List<Class<?>> types = new LinkedList<Class<?>>();

		// Create the Expression for each scalar expression
		for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.getExpression().children()) {
			child.accept(this);
			expressions.add(queryExpression);
			types.add(type[0]);
		}

		// Create the COALESCE expression
		queryExpression = queryContext.getBaseExpression();
		queryExpression = queryExpression.coalesce(expressions);

		// Set the expression type
		type[0] = queryContext.typeResolver().compareCollectionEquivalentTypes(types);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		// Nothing to do, this should be handled by the owning expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {
		expression.getCollectionValuedPathExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression) {

		// Create the expression for the entity expression
		expression.getEntityExpression().accept(this);
		Expression entityExpression = queryExpression;

		if (expression.hasNot()) {

			CollectionValuedPathExpression pathExpression = (CollectionValuedPathExpression) expression.getCollectionValuedPathExpression();

			// Retrieve the ExpressionBuilder from the collection-valued path expression's
			// identification variable and the variable name
			pathExpression.getIdentificationVariable().accept(this);
			Expression parentExpression = queryExpression;

			// Now create the actual expression
			Expression newBuilder = new ExpressionBuilder();
			Expression collectionBase = newBuilder;
            for (int i = 1; i < pathExpression.pathSize() - 1; i++) {
                // nested paths must be single valued.
                collectionBase = collectionBase.get(pathExpression.getPath(i));
            }
            String lastPath = pathExpression.getPath(pathExpression.pathSize() - 1);
            // The following code is copied from Expression.noneOf and altered a bit
            Expression criteria = newBuilder.equal(parentExpression).and(collectionBase.anyOf(lastPath).equal(entityExpression));
            ReportQuery subQuery = new ReportQuery();
            subQuery.setShouldRetrieveFirstPrimaryKey(true);
            subQuery.setSelectionCriteria(criteria);
            // subQuery has the same reference class as parentExpression (which is an ExpressionBuilder).
            subQuery.setReferenceClass(((ExpressionBuilder)parentExpression).getQueryClass());
            queryExpression = parentExpression.notExists(subQuery);
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
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		visitPathExpression(expression, nullAllowed, expression.pathSize());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {

		ComparisonExpressionVisitor visitor = new ComparisonExpressionVisitor();

		// Create the left side of the comparison expression
		expression.getLeftExpression().accept(visitor);
		Expression leftExpression = queryExpression;

		// Create the right side of the comparison expression
		expression.getRightExpression().accept(visitor);
		Expression rightExpression = queryExpression;

		// Now create the comparison expression
		String comparaison = expression.getComparisonOperator();

		// =
		if (comparaison == ComparisonExpression.EQUAL) {
			queryExpression = leftExpression.equal(rightExpression);
		}
		// <>, !=
		else if (comparaison == ComparisonExpression.DIFFERENT ||
		         comparaison == ComparisonExpression.NOT_EQUAL) {

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
		else {
			throw new IllegalArgumentException("The comparison operator is unknown: " + comparaison);
		}

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {

		List<org.eclipse.persistence.jpa.jpql.parser.Expression> expressions = children(expression.getExpression());
		Expression newExpression = null;

		for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expressions) {
			child.accept(this);
			if (newExpression == null) {
				newExpression = queryExpression;
			}
			else {
				newExpression = newExpression.concat(queryExpression);
			}
		}

		queryExpression = newExpression;

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConnectByClause expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Create the COUNT expression
		queryExpression = queryExpression.count();

		// Set the expression type
		type[0] = Long.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DatabaseType expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {

		if (expression.isJDBCDate()) {
			queryExpression = queryContext.getBaseExpression();
			queryExpression = new DateConstantExpression(expression.getText(), queryExpression);
			String text = expression.getText();

			if (text.startsWith("{d")) {
				type[0] = Date.class;
			}
			else if (text.startsWith("{ts")) {
				type[0] = Timestamp.class;
			}
			else if (text.startsWith("{t")) {
				type[0] = Time.class;
			}
			else {
				type[0] = Object.class;
			}
		}
		else {
			queryExpression = queryContext.getBaseExpression();

			if (expression.isCurrentDate()) {
				queryExpression = queryExpression.currentDateDate();
				type[0] = Date.class;
			}
			else if (expression.isCurrentTime()) {
				queryExpression = queryExpression.currentTime();
				type[0] = Time.class;
			}
			else if (expression.isCurrentTimestamp()) {
				queryExpression = queryExpression.currentTimeStamp();
				type[0] = Timestamp.class;
			}
			else {
				throw new IllegalArgumentException("The DateTime is unknown: " + expression);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {

		List<Class<?>> types = new ArrayList<Class<?>>(2);

		// Create the left side of the division expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;
		types.add(type[0]);

		// Create the right side of the division expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;
		types.add(type[0]);

		// Now create the division expression
		queryExpression = ExpressionMath.divide(leftExpression, rightExpression);

		// Set the expression type
		Collections.sort(types, NumericTypeComparator.instance());
		type[0] = types.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression) {

		CollectionValuedPathExpression collectionPath = (CollectionValuedPathExpression) expression.getExpression();
		int lastPathIndex = collectionPath.pathSize() - 1;
		String name = collectionPath.getPath(lastPathIndex);

		// Create the expression for the collection-valued path expression (except the last path)
		visitPathExpression(collectionPath, false, lastPathIndex);

		// Create the IS EMPTY expression
		if (expression.hasNot()) {
			queryExpression = queryExpression.notEmpty(name);
		}
		else {
			queryExpression = queryExpression.isEmpty(name);
		}

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {
		ClassDescriptor descriptor = queryContext.getDescriptor(expression.getEntityTypeName());
		type[0] = descriptor.getJavaClass();
		queryExpression = new ConstantExpression(type[0], queryContext.getBaseExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {

		// Create the expression for the collection-valued path expression
		expression.getExpression().accept(this);

		// Now create the ENTRY expression
		MapEntryExpression entryExpression = new MapEntryExpression(queryExpression);
		entryExpression.returnMapEntry();
		queryExpression = entryExpression;

		// Set the expression type
		type[0] = Map.Entry.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression) {

		// First create the subquery
		ReportQuery subquery = buildSubquery((SimpleSelectStatement) expression.getExpression());

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

		// Now create the EXISTS expression
		queryExpression = queryContext.getBaseExpression();

		if (expression.hasNot()) {
			queryExpression = queryExpression.notExists(subquery);
		}
		else {
			queryExpression = queryExpression.exists(subquery);
		}

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExtractExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the EXTRACT expression
		queryExpression = queryExpression.extract(expression.getDatePart());

		// Set the expression type
		type[0] = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FunctionExpression expression) {

		String identifier   = expression.getIdentifier();
		String functionName = expression.getUnquotedFunctionName();

		// COLUMN
		if (identifier == org.eclipse.persistence.jpa.jpql.parser.Expression.COLUMN) {

			// Create the expression for the single child
			expression.getExpression().accept(this);

			// Create the expression representing a field in a data-level query
			queryExpression = queryExpression.getField(functionName);
		}
		else {

			List<org.eclipse.persistence.jpa.jpql.parser.Expression> expressions = children(expression.getExpression());

			// No arguments
			if (expressions.isEmpty()) {
				queryExpression = queryContext.getBaseExpression();

				// OPERATOR
				if (identifier == org.eclipse.persistence.jpa.jpql.parser.Expression.SQL) {
					queryExpression = queryExpression.literal(functionName);
				}
				// FUNC/FUNCTION
				else {
					queryExpression = queryExpression.getFunction(functionName);
				}
			}
			// One or more arguments
			else {

				// Create the Expressions for the rest
				List<Expression> queryExpressions = new ArrayList<Expression>(expressions.size());

				for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expressions) {
					child.accept(this);
					queryExpressions.add(queryExpression);
				}

				queryExpression = queryExpressions.remove(0);

				// SQL
				if (identifier == org.eclipse.persistence.jpa.jpql.parser.Expression.SQL) {
					queryExpression = queryExpression.sql(functionName, queryExpressions);
				}
				// OPERATOR
				else if (identifier == org.eclipse.persistence.jpa.jpql.parser.Expression.OPERATOR) {
					queryExpression = queryExpression.operator(functionName, queryExpressions);
				}
				// FUNC/FUNCTION
				else {
					queryExpression = queryExpression.getFunctionWithArguments(functionName, queryExpressions);
				}
			}
		}

		// Set the expression type
		type[0] = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HierarchicalQueryClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		// The identification variable is virtual, only do something
		// if it falsely represents a state field path expression
		if (expression.isVirtual()) {
			StateFieldPathExpression stateFieldPathExpression = expression.getStateFieldPathExpression();

			if (stateFieldPathExpression != null) {
				stateFieldPathExpression.accept(this);
				return;
			}
		}

		String variableName = expression.getVariableName();

		// Identification variable, it's important to use findQueryExpression() and not
		// getQueryExpression(). If the identification variable is defined by the parent
		// query, then the ExpressionBuilder have most likely been created already
		queryExpression = queryContext.findQueryExpression(variableName);

		// Retrieve the Declaration mapped to the variable name
		Declaration declaration = queryContext.findDeclaration(variableName);

		// A null Declaration would most likely mean it's coming from a
		// state field path expression that represents an enum constant
		if (declaration != null) {

			// The Expression was not created yet, which can happen if the identification
			// variable is declared in a parent query. If that is the case, create the
			// ExpressionBuilder and cache it for the current query
			if (queryExpression == null) {
				declaration.getBaseExpression().accept(this);
				queryContext.addQueryExpression(variableName, queryExpression);
			}

			// Retrieve the Entity type
			if (declaration.getType() == Type.RANGE) {
				type[0] = declaration.getDescriptor().getJavaClass();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {

		// Create the expression for the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the INDEX expression
		queryExpression = queryExpression.index();

		// Set the expression type
		type[0] = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {

		// Visit the left expression
		InExpressionExpressionBuilder visitor1 = new InExpressionExpressionBuilder();
		expression.getExpression().accept(visitor1);

		// Visit the IN items
		InExpressionBuilder visitor2 = new InExpressionBuilder();
		visitor2.hasNot               = expression.hasNot();
		visitor2.singleInputParameter = expression.isSingleInputParameter();
		visitor2.leftExpression       = queryExpression;
		expression.getInItems().accept(visitor2);

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {

		String parameterName = expression.getParameter();

		// Calculate the input parameter type
		Class<?> type = queryContext.getParameterType(expression);

		// Create the expression
		queryExpression = queryContext.getBaseExpression();
		queryExpression = queryExpression.getParameter(parameterName.substring(1), type);

		// Cache the input parameter type
		queryContext.addInputParameter(expression, queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		try {
			nullAllowed = expression.isLeftJoin();
			expression.getJoinAssociationPath().accept(this);
		}
		finally {
			nullAllowed = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {

		// First visit the parent Expression
		expression.getExpression().accept(this);

		// Now create the Expression of the KEY expression
		queryExpression = new MapEntryExpression(queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {

		String keyword = expression.getText();
		Object value;

		if (keyword == KeywordExpression.NULL) {
			value   = null;
			type[0] = Object.class;
		}
		else if (keyword == KeywordExpression.TRUE) {
			value   = Boolean.TRUE;
			type[0] = Boolean.class;
		}
		else {
			value   = Boolean.FALSE;
			type[0] = Boolean.class;
		}

		queryExpression = queryContext.getBaseExpression();
		queryExpression = new ConstantExpression(value, queryExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the LENGTH expression
		queryExpression = queryExpression.length();

		// Set the expression type
		type[0] = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

		// Set the expression type
		type[0] = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the LOWER expression
		queryExpression = queryExpression.toLowerCase();

		// Set the expression type
		type[0] = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the MAX expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the MAX expression
		queryExpression = queryExpression.maximum();

		// Note: The type will be calculated when traversing the sub-expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {

		// Create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the MIN expression has distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the MIN expression
		queryExpression = queryExpression.minimum();

		// Note: The type will be calculated when traversing the sub-expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {

		// First create the Expression for the first expression
		expression.getFirstExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Now create the Expression for the second expression
		expression.getSecondExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the MOD expression
		queryExpression = ExpressionMath.mod(leftExpression, rightExpression);

		// Set the expression type
		type[0] = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {

		List<Class<?>> types = new ArrayList<Class<?>>(2);

		// Create the left side of the multiplication expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;
		types.add(type[0]);

		// Create the right side of the multiplication expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;
		types.add(type[0]);

		// Now create the multiplication expression
		queryExpression = ExpressionMath.multiply(leftExpression, rightExpression);

		// Set the expression type
		Collections.sort(types, NumericTypeComparator.instance());
		type[0] = types.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {

		// Create the expression
		expression.getExpression().accept(this);

		// Negate the expression
		queryExpression = queryExpression.not();

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Create the expression first
		expression.getExpression().accept(this);

		// Mark it as NOT NULL
		if (expression.hasNot()) {
			queryExpression = queryExpression.notNull();
		}
		// Mark it as IS NULL
		else {
			queryExpression = queryExpression.isNull();
		}

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression) {
		queryExpression = null;
		type[0] = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {

		// Create the first expression
		expression.getFirstExpression().accept(this);
		Expression firstExpression = queryExpression;
		Class<?> actualType = type[0];

		// Create the second expression
		expression.getSecondExpression().accept(this);
		Expression secondExpression = queryExpression;

		// Now create the NULLIF expression
		queryExpression = firstExpression.nullIf(secondExpression);

		// Set the expression type
		type[0] = actualType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {

		// Instantiate a Number object with the value
		type[0] = queryContext.getType(expression);
		Number number = queryContext.newInstance(type[0], String.class, expression.getText());

		// Now create the numeric expression
		queryExpression = new ConstantExpression(number, queryContext.getBaseExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		// Simply traverse the OBJECT's expression
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OnClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {

		// Create the item
		expression.getExpression().accept(this);

		// Create the ordering item
		switch (expression.getOrdering()) {
			case ASC:  queryExpression = queryExpression.ascending();  break;
			case DESC: queryExpression = queryExpression.descending(); break;
		}

		// Create the null ordering item
		switch (expression.getNullOrdering()) {
			case NULLS_FIRST: queryExpression = queryExpression.nullsFirst(); break;
			case NULLS_LAST:  queryExpression = queryExpression.nullsLast();  break;
		}
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
	public void visit(OrExpression expression) {

		// Create the left side of the logical expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;

		// Create the right side of the logical expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;

		// Now create the OR expression
		queryExpression = leftExpression.or(rightExpression);

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		IdentificationVariable variable = (IdentificationVariable) expression.getIdentificationVariable();
		Declaration declaration = queryContext.getDeclaration(variable.getVariableName());

		switch (declaration.getType()) {

			// If the Declaration is RangeDeclaration, then retrieve its Descriptor directly,
			// this will support two cases automatically, the "root" object is
			// 1) An abstract schema name (entity name) -> parsed as AbstractSchemaName
			// 2) A fully qualified class name -> parsed as a CollectionValuedPathExpression
			//    that cannot be visited
			case RANGE:
			case CLASS_NAME: {
				type[0] = declaration.getDescriptor().getJavaClass();
				queryExpression = new ExpressionBuilder(type[0]);
				break;
			}

			// The FROM subquery needs to be created differently than a regular subquery
			case SUBQUERY: {
				type[0] = null;
				queryExpression = declaration.getQueryExpression();
				break;
			}

			// This should be a derived path (CollectionValuedPathExpression) or a subquery
			default: {
				expression.getRootObject().accept(this);
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RegexpExpression expression) {

		// Create the first expression
		expression.getStringExpression().accept(this);
		Expression firstExpression = queryExpression;

		// Create the expression for the pattern value
		expression.getPatternValue().accept(this);
		Expression patternValue = queryExpression;

		// Create the REGEXP expression
		queryExpression = firstExpression.regexp(patternValue);

		// Set the expression type
		type[0] = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {

		expression.getSelectExpression().accept(this);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression.getResultVariable();
		String variableName = identificationVariable.getVariableName();
		queryContext.addQueryExpression(variableName, queryExpression);

		// Note: The type will be calculated when traversing the select expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@Override
	public void visit(SizeExpression expression) {

		CollectionValuedPathExpression pathExpression = (CollectionValuedPathExpression) expression.getExpression();
		int lastIndex = pathExpression.pathSize() - 1;

		// Create the right chain of expressions
		visitPathExpression(pathExpression, false, lastIndex - 1);

		// Now create the SIZE expression
		String name = pathExpression.getPath(lastIndex);
		queryExpression = queryExpression.size(name);

		// Set the expression type
		type[0] = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the SQRT expression
		queryExpression = ExpressionMath.sqrt(queryExpression);

		// Set the expression type
		type[0] = Double.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StartWithClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		visitPathExpression(expression, false, expression.pathSize());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {

		// Create the expression
		queryExpression = queryContext.getBaseExpression();
		queryExpression = new ConstantExpression(expression.getUnquotedText(), queryExpression);

		// Set the expression type
		type[0] = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
		if (thirdExpression != null) {
			queryExpression = firstExpression.substring(secondExpression, thirdExpression);
		}
		else {
			queryExpression = firstExpression.substring(secondExpression);
		}

		// Set the expression type
		type[0] = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {

		List<Class<?>> types = new ArrayList<Class<?>>(2);

		// Create the left side of the subtraction expression
		expression.getLeftExpression().accept(this);
		Expression leftExpression = queryExpression;
		types.add(type[0]);

		// Create the right side of the subtraction expression
		expression.getRightExpression().accept(this);
		Expression rightExpression = queryExpression;
		types.add(type[0]);

		// Now create the subtraction expression
		queryExpression = ExpressionMath.subtract(leftExpression, rightExpression);

		// Set the expression type
		Collections.sort(types, NumericTypeComparator.instance());
		type[0] = types.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Mark the SUM expression distinct
		if (expression.hasDistinct()) {
			queryExpression = queryExpression.distinct();
		}

		// Now create the SUM expression
		queryExpression = queryExpression.sum();

		// Set the expression type
		type[0] = queryContext.typeResolver().convertSumFunctionType(type[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TableExpression expression) {
		String tableName = queryContext.literal(expression.getExpression(), LiteralType.STRING_LITERAL);
		tableName = ExpressionTools.unquote(tableName);
		queryExpression = queryContext.getBaseExpression().getTable(tableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TableVariableDeclaration expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {

		// First visit the parent Expression
		expression.getCollectionValuedPathExpression().accept(this);

		// Now downcast the Expression
		EntityTypeLiteral entityTypeLiteral = (EntityTypeLiteral) expression.getEntityType();
		ClassDescriptor entityType = queryContext.getDescriptor(entityTypeLiteral.getEntityTypeName());
		queryExpression = queryExpression.treat(entityType.getJavaClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

		// Set the expression type
		type[0] = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {

		// First create the Expression for the identification variable
		expression.getExpression().accept(this);

		// Create the TYPE expression
		queryExpression = queryExpression.type();

		// Note: The type will be calculated when traversing the select expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnionClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression) {
		queryExpression = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {

		// First create the expression from the encapsulated expression
		expression.getExpression().accept(this);

		// Now create the UPPER expression
		queryExpression = queryExpression.toUpperCase();

		// Set the expression type
		type[0] = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	private void visitPathExpression(AbstractPathExpression expression,
	                                 boolean nullAllowed,
	                                 int lastIndex) {


		PathResolver resolver = new PathResolver();

		resolver.length           = lastIndex;
		resolver.nullAllowed      = nullAllowed;
		resolver.checkMappingType = false;
		resolver.localExpression  = null;
		resolver.descriptor       = null;

		expression.accept(resolver);

		queryExpression = resolver.localExpression;
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
		 * The list of {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} that are
		 * the children of an expression.
		 */
		List<org.eclipse.persistence.jpa.jpql.parser.Expression> expressions;

		/**
		 * Creates a new <code>ChildrenExpressionVisitor</code>.
		 */
		ChildrenExpressionVisitor() {
			super();
			expressions = new ArrayList<org.eclipse.persistence.jpa.jpql.parser.Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
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
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			expressions.add(expression);
		}
	}

	/**
	 * This visitor makes sure to properly handle an entity type literal being parsed as an
	 * identification variable.
	 */
	private class ComparisonExpressionVisitor extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			boolean found = false;

			if (!expression.isVirtual()) {

				ClassDescriptor descriptor = queryContext.getDescriptor(expression.getText());

				// Entity type name
				if (descriptor != null) {
					type[0] = descriptor.getJavaClass();
					queryExpression = new ConstantExpression(type[0], queryContext.getBaseExpression());
					found = true;
				}
			}

			if (!found) {
				expression.accept(ExpressionBuilderVisitor.this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			expression.accept(ExpressionBuilderVisitor.this);
		}
	}

	/**
	 * This visitor takes care of creating the left expression of an <code><b>IN</b></code> expression
	 * and make sure to support nested arrays.
	 */
	private class InExpressionExpressionBuilder extends EclipseLinkAnonymousExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {

			// Assume this is a nested array
			List<Expression> children = new LinkedList<Expression>();

			for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
				child.accept(this);
				children.add(queryExpression);
			}

			queryExpression = new ConstantExpression(children, queryContext.getBaseExpression());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
			expression.accept(ExpressionBuilderVisitor.this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			expression.getExpression().accept(this);
		}
	}

	/**
	 * This visitor takes care of creating the <code><b>IN</b></code> expression by visiting the items.
	 */
	private class InExpressionBuilder extends EclipseLinkAnonymousExpressionVisitor {

		private boolean hasNot;
		private Expression leftExpression;
		private boolean singleInputParameter;

		/**
		 * Creates a new <code>InExpressionBuilder</code>.
		 */
		InExpressionBuilder() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {

			Collection<Expression> expressions = new ArrayList<Expression>();
			InItemExpressionVisitor visitor = new InItemExpressionVisitor();

			for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
				child.accept(visitor);
				expressions.add(queryExpression);
			}

			if (hasNot) {
				queryExpression = leftExpression.notIn(expressions);
			}
			else {
				queryExpression = leftExpression.in(expressions);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			boolean found = false;

			if (!expression.isVirtual()) {

				ClassDescriptor descriptor = queryContext.getDescriptor(expression.getText());

				// Entity type name
				if (descriptor != null) {
					type[0] = descriptor.getJavaClass();
					queryExpression = new ConstantExpression(type[0], queryContext.getBaseExpression());
					found = true;
				}
			}

			if (!found) {
				expression.accept(ExpressionBuilderVisitor.this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {

			if (singleInputParameter) {
				String parameterName = expression.getParameter();

				// Create the expression with Collection as the default type
				queryExpression = queryContext.getBaseExpression();
				queryExpression = queryExpression.getParameter(parameterName.substring(1), Collection.class);

				// Cache the input parameter type, which is by default Collection
				queryContext.addInputParameter(expression, queryExpression);

				if (hasNot) {
					queryExpression = leftExpression.notIn(queryExpression);
				}
				else {
					queryExpression = leftExpression.in(queryExpression);
				}
			}
			else {
				visit((org.eclipse.persistence.jpa.jpql.parser.Expression) expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {

			expression.accept(ExpressionBuilderVisitor.this);

			Collection<Expression> expressions = new ArrayList<Expression>();
			expressions.add(queryExpression);

			// Now create the IN expression
			if (hasNot) {
				queryExpression = leftExpression.notIn(expressions);
			}
			else {
				queryExpression = leftExpression.in(expressions);
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
				queryExpression = leftExpression.notIn(subquery);
			}
			else {
				queryExpression = leftExpression.in(subquery);
			}
		}

		private class InItemExpressionVisitor extends AnonymousExpressionVisitor {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void visit(IdentificationVariable expression) {
				ClassDescriptor descriptor = queryContext.getDescriptor(expression.getVariableName());
				queryExpression = queryContext.getBaseExpression();
				queryExpression = new ConstantExpression(descriptor.getJavaClass(), queryExpression);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void visit(CollectionExpression expression) {

				// Assume this is a nested array
				List<Expression> children = new LinkedList<Expression>();

				for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
					child.accept(this);
					children.add(queryExpression);
				}

				queryExpression = new ConstantExpression(children, queryContext.getBaseExpression());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void visit(SubExpression expression) {
				expression.getExpression().accept(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
				expression.accept(ExpressionBuilderVisitor.this);
			}
		}
	}

	private class PathResolver extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * Determines whether
		 */
		boolean checkMappingType;

		/**
		 * Keeps track of the {@link Declaration} when the identification variable maps to a "root"
		 * object defined in the <code><b>FROM</b></code> clause.
		 */
		private Declaration declaration;

		/**
		 * Keeps track of the descriptor while traversing the path expression.
		 */
		private ClassDescriptor descriptor;

		/**
		 * The actual number of paths within the path expression that will be traversed in order to
		 * create the EclipseLink {@link Expression}.
		 */
		int length;

		/**
		 * The EclipseLink {@link Expression} that was retrieved or created while traversing the path
		 * expression.
		 */
		Expression localExpression;

		/**
		 * Determines whether the target relationship is allowed to be <code>null</code>.
		 */
		boolean nullAllowed;

		/**
		 * Resolves a database column.
		 *
		 * @param expression The path expression representing an identification variable mapping to a
		 * database table followed by the column name
		 */
		private void resolveColumn(AbstractPathExpression expression) {
			String path = expression.getPath(1);
			localExpression = localExpression.getField(path);
		}

		/**
		 * Attempts to resolve the path expression as a fully qualified enum constant.
		 *
		 * @param expression The {@link AbstractPathExpression} that might represent an enum constant
		 * @return <code>true</code> if the path was a fully qualified enum constant; <code>false</code>
		 * if it's an actual path expression
		 */
		protected boolean resolveEnumConstant(AbstractPathExpression expression) {

			String fullPath = expression.toParsedText();
			Class<?> enumType = queryContext.getEnumType(fullPath);

			if (enumType != null) {

				// Make sure we keep track of the enum type
				type[0] = enumType;

				// Retrieve the enum constant
				String path = expression.getPath(expression.pathSize() - 1);
				Enum<?> enumConstant = retrieveEnumConstant(enumType, path);

				// Create the Expression
				localExpression = new ConstantExpression(enumConstant, new ExpressionBuilder());

				return true;
			}

			return false;
		}

		private void resolvePath(AbstractPathExpression expression) {

			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = length; index < count; index++) {

				String path = expression.getPath(index);
				DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(path);
				boolean last = (index + 1 == count);
				boolean collectionMapping = false;

				// The path is a mapping
				if (mapping != null) {

					// Make sure we keep track of its type
					if (type != null) {
						type[0] = queryContext.calculateMappingType(mapping);
					}

					// This will tell us how to create the Expression
					collectionMapping = mapping.isCollectionMapping();

					// Retrieve the reference descriptor so we can continue traversing the path
					if (!last) {
						descriptor = mapping.getReferenceDescriptor();
					}
					// Flag that needs to be modified for a special case
					else if (checkMappingType) {
						nullAllowed = mapping.isForeignReferenceMapping();
					}
				}
				// No mapping is defined for the single path, check for a query key
				else {
					QueryKey queryKey = descriptor.getQueryKeyNamed(path);

					if (queryKey != null) {
						// Make sure we keep track of its type
						if (type != null) {
							type[0] = queryContext.calculateQueryKeyType(queryKey);
						}

						// This will tell us how to create the Expression
						collectionMapping = queryKey.isCollectionQueryKey();

						// Retrieve the reference descriptor so we can continue traversing the path
						if (!last && queryKey.isForeignReferenceQueryKey()) {
							ForeignReferenceQueryKey referenceQueryKey = (ForeignReferenceQueryKey) queryKey;
							descriptor = queryContext.getDescriptor(referenceQueryKey.getReferenceClass());
						}
					}
					// Nothing was found
					else {
						break;
					}
				}

				// Now create the Expression
				if (collectionMapping) {
					if (last && nullAllowed) {
						localExpression = localExpression.anyOfAllowingNone(path);
					}
					else {
						localExpression = localExpression.anyOf(path);
					}
				}
				else {
					if (last && nullAllowed) {
						localExpression = localExpression.getAllowingNull(path);
					}
					else {
						localExpression = localExpression.get(path);
					}
				}
			}
		}

		private void resolveVirtualPath(AbstractPathExpression expression) {
			String path = expression.getPath(1);
			localExpression = localExpression.get(path);
		}

		/**
		 * Retrieves the actual {@link Enum} constant with the given name.
		 *
		 * @param type The {@link Enum} class used to retrieve the given name
		 * @param name The name of the constant to retrieve
		 * @return The {@link Enum} constant
		 */
		private Enum<?> retrieveEnumConstant(Class<?> type, String name) {

			for (Enum<?> enumConstant : (Enum<?>[]) type.getEnumConstants()) {
				if (name.equals(enumConstant.name())) {
					return enumConstant;
				}
			}

			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			visitPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {

			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
			String variableName = identificationVariable.getVariableName();

			// Retrieve the Expression for the identification variable
			declaration = queryContext.findDeclaration(variableName);
			declaration.getBaseExpression().accept(ExpressionBuilderVisitor.this);
			localExpression = queryExpression;

			// Create the Map.Entry expression
			MapEntryExpression entryExpression = new MapEntryExpression(localExpression);
			entryExpression.returnMapEntry();
			localExpression = entryExpression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			expression.accept(ExpressionBuilderVisitor.this);
			localExpression = queryExpression;

			// It is possible the Expression is null, it happens when the path expression is an enum
			// constant. If so, then no need to retrieve the descriptor
			if (localExpression != null) {
				declaration = queryContext.findDeclaration(expression.getVariableName());
				descriptor = declaration.getDescriptor();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression) {

			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();

			// Create the Expression for the identification variable
			identificationVariable.accept(ExpressionBuilderVisitor.this);
			localExpression = new MapEntryExpression(queryExpression);

			// Retrieve the mapping's key mapping's descriptor
			descriptor = queryContext.resolveDescriptor(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			visitPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TreatExpression expression) {

			expression.accept(ExpressionBuilderVisitor.this);
			localExpression = queryExpression;

			// Retrieve the mapping's key mapping's descriptor
			descriptor = queryContext.resolveDescriptor(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {

			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();

			// Create the Expression for the identification variable
			identificationVariable.accept(ExpressionBuilderVisitor.this);
			localExpression = queryExpression;

			// Retrieve the mapping's reference descriptor
			declaration = queryContext.findDeclaration(identificationVariable.getVariableName());
			descriptor = declaration.getDescriptor();
		}

		private void visitPathExpression(AbstractPathExpression expression) {

			// First resolve the identification variable
			expression.getIdentificationVariable().accept(this);

			// The path expression is composed with an identification
			// variable that is mapped to a subquery as the "root" object
			if ((declaration != null) && (declaration.getType() == Type.SUBQUERY)) {
				resolveVirtualPath(expression);
				return;
			}

			// A null value would most likely mean it's coming from a
			// state field path expression that represents an enum constant
			if (localExpression == null) {
				boolean result = resolveEnumConstant(expression);
				if (result) {
					return;
				}
			}

			// The path expression is mapping to a database table
			if ((declaration != null) && (declaration.getType() == Type.TABLE)) {
				resolveColumn(expression);
				return;
			}

			// Traverse the rest of the path expression
			resolvePath(expression);
		}
	}

	/**
	 * This visitor is responsible to create the {@link Expression Expressions} for the <b>WHEN</b>
	 * and <b>THEN</b> expressions.
	 */
	private class WhenClauseExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * Keeps tracks of the type of each <code><b>WHEN</b></code> clauses.
		 */
		final List<Class<?>> types;

		/**
		 * The map of <b>WHEN</b> expressions mapped to their associated <b>THEN</b> expression.
		 */
		Map<Expression, Expression> whenClauses;

		/**
		 * Creates a new <code>WhenClauseExpressionVisitor</code>.
		 */
		WhenClauseExpressionVisitor() {
			super();
			types = new LinkedList<Class<?>>();
			whenClauses = new LinkedHashMap<Expression, Expression>();
		}

		/**
		 * Disposes this visitor.
		 */
		void dispose() {
			types.clear();
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
			types.add(type[0]);

			// Create the THEN expression
			expression.getThenExpression().accept(ExpressionBuilderVisitor.this);
			Expression thenExpression = queryExpression;

			whenClauses.put(whenExpression, thenExpression);
		}
	}
}