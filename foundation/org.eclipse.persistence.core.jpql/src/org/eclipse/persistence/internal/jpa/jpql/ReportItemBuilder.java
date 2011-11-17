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
import java.util.List;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ReportQuery;

import static org.eclipse.persistence.jpa.jpql.LiteralType.*;

/**
 * This builder is responsible to create the EclipseLink {@link Expression Expressions} representing
 * the select items of the <code><b>SELECT</b></code> clause.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ReportItemBuilder extends EclipseLinkAnonymousExpressionVisitor {

	/**
	 * The visitor responsible to visit the constructor items.
	 */
	private ConstructorExpressionVisitor constructorExpressionVisitor;

	/**
	 * Determines whether the <code><b>SELECT</b></code> clause has more than one select item.
	 */
	private boolean multipleSelects;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>ReportItemBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ReportItemBuilder(JPQLQueryContext queryContext) {
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
		addAttribute(name, queryContext.buildExpression(expression), type);
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
		addAttribute(name, expression, queryContext.getType(expression));
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
		Class<?> type = queryContext.getType(expression);

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
		CollectionValuedPathExpression pathExpression = (CollectionValuedPathExpression) expression.getExpression();
		String name = pathExpression.getPath(pathExpression.pathSize() - 1);
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
		Class<?> type = queryContext.getType(expression.getClassName());

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
		String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);

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
		Class<?> type = queryContext.getType(expression);

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

		String variableName = expression.getVariableName();

		// Retrieve the FETCH JOIN expressions using the identification variable
		List<org.eclipse.persistence.expressions.Expression> joinFetchExpressions = null;

		// Retrieve the join fetches that were defined in the same identification variable
		// declaration, if the identification variable is mapped to a join, then there will
		// not be any join fetch associated with it
		Collection<JoinFetch> joinFetches = queryContext.getJoinFetches(variableName);

		if (joinFetches != null) {

			for (JoinFetch joinFetch : queryContext.getJoinFetches(variableName)) {

				// Retrieve the join association path expression's identification variable
				String joinFetchVariableName = queryContext.literal(
					joinFetch,
					PATH_EXPRESSION_IDENTIFICATION_VARIABLE
				);

				if (variableName.equals(joinFetchVariableName)) {
					if (joinFetchExpressions == null) {
						joinFetchExpressions = new ArrayList<org.eclipse.persistence.expressions.Expression>();
					}
					joinFetchExpressions.add(queryContext.buildExpression(joinFetch));
				}
			}
		}

		// Add the attribute without any JOIN FETCH
		if (joinFetchExpressions == null) {
			addAttribute(expression.getText(), queryContext.buildExpression(expression));
		}
		// Add the attribute with the JOIN FETCH expressions
		else {
			getDatabaseQuery().addItem(
				expression.getText(),
				queryContext.buildExpression(expression),
				joinFetchExpressions
			);
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
		String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
		addAttribute(name, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
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
		Class<?> type = queryContext.getType(expression);

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
	protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
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
		String variableName = queryContext.literal(expression.getResultVariable(), RESULT_VARIABLE);

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
		org.eclipse.persistence.expressions.Expression queryExpression =
			queryContext.buildExpression(expression.getExpression());

		addAttribute("size", queryExpression.count(), int.class);

		// Now add the GROUP BY expression
		CollectionValuedPathExpression pathExpression = (CollectionValuedPathExpression) expression.getExpression();
		queryExpression = queryContext.buildExpression(pathExpression, pathExpression.pathSize() - 1);
		getDatabaseQuery().addGrouping(queryExpression);
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

		// Retrieve the Resolver rather than building the Expression
		DatabaseMapping mapping = queryContext.resolveMapping(expression);

		// Temporarily change the null allowed flag if the state field is a foreign reference mapping
		org.eclipse.persistence.expressions.Expression queryExpression =
			queryContext.buildExpression(
				expression,
				(mapping != null) && mapping.isForeignReferenceMapping()
			);

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
		Class<?> type = queryContext.getType(expression);

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
		String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
		addAttributeWithType(name, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {
		// Nothing to do
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
		IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
		addAttribute(identificationVariable.getText(), expression);
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
	private class ConstructorExpressionVisitor extends EclipseLinkAnonymousExpressionVisitor {

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
			expression.accept(ReportItemBuilder.this);
		}
	}
}