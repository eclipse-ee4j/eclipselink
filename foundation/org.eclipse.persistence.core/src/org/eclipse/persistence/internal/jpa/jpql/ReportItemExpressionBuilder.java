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
import java.util.List;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.internal.jpql.LiteralType;
import org.eclipse.persistence.jpa.internal.jpql.Resolver;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.internal.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.MappingTypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This visitor is responsible to create the EclipseLink {@link Expression Expressions} for the
 * select items and add them as attributes.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ReportItemExpressionBuilder extends AnonymousExpressionVisitor {

	/**
	 * The visitor responsible to visit the constructor items.
	 */
	private ConstructorExpressionVisitor constructorExpressionVisitor;

	/**
	 * Determines whether the <b>SELECT</b> clause has more than one select item.
	 */
	private boolean multipleSelects;

	/**
	 * The context used to query information about the application metadata.
	 */
	private final DefaultJPQLQueryContext queryContext;

	/**
	 * The visitor responsible to visit the <b>SIZE</b>'s expression and to create a modify version
	 * of the {@link PathExpressionResolver} that will create the {@link org.eclipse.persistence.expressions.Expression
	 * Expression}.
	 */
	private SizeGroupByExpressionVisitor sizeGroupByExpressionVisitor;

	/**
	 * Creates a new <code>ReportItemExpressionBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	ReportItemExpressionBuilder(DefaultJPQLQueryContext queryContext) {

		super();
		this.queryContext = queryContext;
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression}. No name will be
	 * associated with the attribute and no type will be calculated.
	 *
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 */
	private void addAttribute(Expression expression) {
		addAttribute(ExpressionTools.EMPTY_STRING, expression);
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression}. No name will be
	 * associated with the attribute.
	 *
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 * @param type The attribute's type, which can be <code>null</code>
	 */
	private void addAttribute(Expression expression, Class<?> type) {
		addAttribute(ExpressionTools.EMPTY_STRING, expression, type);
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression}. No type will be
	 * calculated.
	 *
	 * @param name The name of the attribute
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 */
	private void addAttribute(String name, Expression expression) {
		addAttribute(name, expression, null);
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression}. No type will be
	 * calculated.
	 *
	 * @param name The name of the attribute
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 * @param type The attribute's type, which can be <code>null</code>
	 */
	private void addAttribute(String name, Expression expression, Class<?> type) {
		addAttribute(name, queryContext.buildQueryExpression(expression), type);
	}

	/**
	 * Adds an attribute to the report query for the given {@link org.eclipse.persistence.expressions.Expression
	 * Expression}. No type will be calculated.
	 *
	 * @param name The name of the attribute
	 * @param expression The {@link org.eclipse.persistence.expressions.Expression Expression} to
	 * add as an attribute
	 * org.eclipse.persistence.expressions.Expression Expression}
	 */
	private void addAttribute(String name, org.eclipse.persistence.expressions.Expression expression) {
		addAttribute(name, expression, null);
	}

	/**
	 * Adds an attribute to the report query for the given {@link org.eclipse.persistence.expressions.Expression
	 * Expression}. No type will be calculated.
	 *
	 * @param name The name of the attribute
	 * @param expression The {@link org.eclipse.persistence.expressions.Expression Expression} to
	 * add as an attribute
	 * org.eclipse.persistence.expressions.Expression Expression}
	 * @param type The attribute's type, which can be <code>null</code>
	 */
	private void addAttribute(String name,
	                          org.eclipse.persistence.expressions.Expression expression,
	                          Class<?> type) {

		getDatabaseQuery().addAttribute(name, expression, type);
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression}. No name will be
	 * associated with the attribute and a type will be calculated.
	 *
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 */
	private void addAttributeWithType(Expression expression) {
		addAttributeWithType(ExpressionTools.EMPTY_STRING, expression);
	}

	/**
	 * Adds an attribute to the report query for the given {@link Expression} and a type will be
	 * calculated.
	 *
	 * @param name The name of the attribute
	 * @param expression The {@link Expression JPQL Expression} to convert into an {@link
	 * org.eclipse.persistence.expressions.Expression Expression}
	 */
	private void addAttributeWithType(String name, Expression expression) {
		addAttribute(name, expression, queryContext.getJavaType(expression));
	}

	private ConstructorExpressionVisitor constructorExpressionVisitor() {
		if (constructorExpressionVisitor == null) {
			constructorExpressionVisitor = new ConstructorExpressionVisitor();
		}
		return constructorExpressionVisitor;
	}

	private ReportQuery getDatabaseQuery() {
		return queryContext.getDatabaseQuery();
	}

	private String literal(Expression expression, LiteralType type) {
		return queryContext.literal(expression, type);
	}

	private SizeGroupByExpressionVisitor sizeGroupByExpressionVisitor() {
		if (sizeGroupByExpressionVisitor == null) {
			sizeGroupByExpressionVisitor = new SizeGroupByExpressionVisitor();
		}
		return sizeGroupByExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {
		addAttributeWithType(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {

		// Calculate the type
		Class<?> type = queryContext.getJavaType(expression);

		if (type == Object.class) {
			type = null;
		}

		// Add the attribute
		addAttribute("plus", expression, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		String name = literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_LAST_PATH);
		addAttribute(name, expression, Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {
		addAttributeWithType("Case", expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {
		addAttribute("Coalesce", expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		multipleSelects = true;
		expression.acceptChildren(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {
		addAttribute(expression, String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {

		ReportQuery query = getDatabaseQuery();
		Class<?> type = queryContext.getJavaType(expression.getClassName());

		query.beginAddingConstructorArguments(type);
		expression.accept(constructorExpressionVisitor());
		query.endAddingToConstructorItem();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {

		// Retrieve the attribute name
		String name = literal(
			expression.getExpression(),
			LiteralType.PATH_EXPRESSION_LAST_PATH
		);

		if (ExpressionTools.stringIsEmpty(name)) {
			name = CountFunction.COUNT;
		}

		// Add the attribute
		addAttribute(name, expression, Long.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {
		if (expression.isJDBCDate()) {
			addAttribute("CONSTANT", expression);
		}
		else {
			addAttributeWithType("date", expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {

		// Calculate the type
		Class<?> type = queryContext.getJavaType(expression);

		if (type == Object.class) {
			type = null;
		}

		// Add the attribute
		addAttribute("divide", expression, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {
		addAttribute(" MapEntry", expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression) {
		addAttribute(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		String variableName = expression.getText();

		// Retrieve the FETCH JOIN expressions using the identification variable
		List<org.eclipse.persistence.expressions.Expression> joinFetches = new ArrayList<org.eclipse.persistence.expressions.Expression>();

		// Retrieve the join fetches that were defined in the same identification variable
		// declaration, if the identification variable is mapped to a join, then there will
		// not be any join fetch associated with it
		for (JoinFetch joinFetch : queryContext.getJoinFetches(variableName)) {

			// Retrieve the join association path expression's identification variable
			String joinFetchVariableName = literal(joinFetch, LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE);

			if (variableName.equalsIgnoreCase(joinFetchVariableName)) {
				joinFetches.add(queryContext.buildQueryExpression(joinFetch));
			}
		}

		// Add the attribute
		if (joinFetches.isEmpty()) {
			addAttribute(variableName, queryContext.buildQueryExpression(expression));
		}
		else {
			getDatabaseQuery().addItem(variableName, queryContext.buildQueryExpression(expression), joinFetches);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {
		addAttribute("Index", expression, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {
		addAttribute("MapKey", expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		addAttribute("CONSTANT", expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {
		addAttribute(expression, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {
		addAttribute(expression, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		addAttribute(expression, String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {
		String name = literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_LAST_PATH);
		addAttribute(name, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		String name = literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_LAST_PATH);
		addAttribute(name, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {
		addAttribute(expression, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {

		// Calculate the type
		Class<?> type = queryContext.getJavaType(expression);

		if (type == Object.class) {
			type = null;
		}

		// Add the attribute
		addAttribute("multiply", expression, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {
		addAttribute(NullIfExpression.NULLIF, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {
		addAttribute("CONSTANT", expression);
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
		addAttribute(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {

		// First create create the Expression that is added to the query as an attribute
		expression.getSelectExpression().accept(this);

		// Now cache the Expression for future retrieval by the ORDER BY clause
		String variableName = expression.getResultVariable().toParsedText();
		List<ReportItem> items = getDatabaseQuery().getItems();
		ReportItem lastItem = items.get(items.size() - 1);
		queryContext.addQueryExpression(variableName, lastItem.getAttributeExpression());
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
	public void visit(SimpleSelectClause expression) {
		visitAbstractSelectClause(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression) {

		// Add the attribute
		org.eclipse.persistence.expressions.Expression queryExpression = queryContext.buildQueryExpression(expression.getExpression());
		addAttribute("size", queryExpression.count(), Integer.class);

		// Now add the GROUP BY expression
		SizeGroupByExpressionVisitor visitor = sizeGroupByExpressionVisitor();

		try {
			expression.accept(visitor);
			getDatabaseQuery().addGrouping(queryContext.resolve(visitor.resolver));
		}
		finally {
			visitor.resolver = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {
		addAttribute(expression, Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {

		org.eclipse.persistence.expressions.Expression queryExpression;

		// Retrieve the Resolver rather than building the Expression
		Resolver resolver = queryContext.getResolver(expression);
		IMapping mapping = resolver.getMapping();

		// Make the Expression allowing null if the state field is a foreign reference mapping
		if (MappingTypeHelper.isRelationshipMapping(mapping)) {

			// Temporarily change the null allowed flag in order to properly create the Expression
			boolean oldNullAllowed = resolver.isNullAllowed();
			resolver.setNullAllowed(true);

			// Now we can correctly create the Expression
			queryExpression = queryContext.buildQueryExpression(expression);

			// Revert the flag just in case
			resolver.setNullAllowed(oldNullAllowed);
		}
		else {
			queryExpression = queryContext.buildQueryExpression(expression);
		}

		// Register the EclipseLink Expression with the state field name
		String name = expression.getPath(expression.pathSize() - 1);
		addAttribute(name, queryExpression);
      getDatabaseQuery().dontRetrievePrimaryKeys();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		addAttribute("CONSTANT", expression);
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
		addAttribute(expression, String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {

		// Calculate the type
		Class<?> type = queryContext.getJavaType(expression);

		if (type == Object.class) {
			type = null;
		}

		// Add the attribute
		addAttribute("minus", expression, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {
		String name = literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_LAST_PATH);
		addAttributeWithType(name, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {
		addAttribute(expression, String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {
		addAttribute(expression, String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {
		String name = literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE);
		addAttribute(name, expression);
	}

	private void visitAbstractSelectClause(AbstractSelectClause expression) {

		multipleSelects = false;
		expression.getSelectExpression().accept(this);

		if (multipleSelects) {
			getDatabaseQuery().returnWithoutReportQueryResult();
		}
		else {
			getDatabaseQuery().returnSingleAttribute();
		}
	}

	/**
	 * This visitor takes care of creating the {@link org.eclipse.persistence.expressions.Expression
	 * expressions} for the constructor arguments.
	 */
	private class ConstructorExpressionVisitor extends AnonymousExpressionVisitor {

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
		public void visit(ConstructorExpression expression) {
			expression.getConstructorItems().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.accept(ReportItemExpressionBuilder.this);
		}
	}

	/**
	 * This visitor creates the {@link org.eclipse.persistence.expressions.Expression Expression} for
	 * the collection-valued path expression but does not resolve the very last one.
	 */
	private class SizeGroupByExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The {@link Resolver} of the collection-valued path expression up to the path before the
		 * collection-field member.
		 */
		Resolver resolver;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			// Retrieve the chain of Resolvers
			resolver = queryContext.getResolver(expression);

			// and skip the last leaf
			resolver = resolver.getParent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			resolver = queryContext.getResolver(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression) {
			expression.getExpression().accept(this);
		}
	}
}