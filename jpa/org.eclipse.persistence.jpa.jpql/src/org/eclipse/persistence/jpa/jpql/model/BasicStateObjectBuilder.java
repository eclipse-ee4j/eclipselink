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
package org.eclipse.persistence.jpa.jpql.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractRangeVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSchemaNameStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractStateObjectVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.AdditionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AllOrAnyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AndExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ArithmeticFactorStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BadExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BetweenExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CoalesceExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionValuedPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConstructorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CountFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DateTimeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DivisionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EmptyCollectionComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntityTypeLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntryExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ExistsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.GroupByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.HavingClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IndexExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InputParameterStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LengthExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LikeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LocateExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LowerExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MaxFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MinFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ModExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MultiplicationExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NotExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullIfExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NumericLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ObjectExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ResultVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SizeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SqrtExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObjectVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubtractionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TrimExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TypeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UnknownExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpperExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ValueExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhenClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhereClauseStateObject;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
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
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;

/**
 * The default implementation of a {@link IBuilder}, which creates a {@link StateObject}
 * representation of the {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} being
 * visited.
 *
 * @see Expression
 * @see StateObject
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class BasicStateObjectBuilder implements ExpressionVisitor {

	/**
	 *
	 */
	private IBuilder<CollectionMemberDeclarationStateObject, AbstractFromClauseStateObject> collectionDeclarationBuilder;

	/**
	 *
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 *
	 */
	private IBuilder<DeleteStatementStateObject, JPQLQueryStateObject> deleteStatementBuilder;

	/**
	 * This {@link IBuilder} is responsible to build an <code><b>DELETE</b></code> statement,
	 * which populates the <code><b>DELETE</b></code> clause and the optional <code><b>WHERE</b></code>
	 * clause by converting the information contained in the parsed JPQL query.
	 */
	private IBuilder<JoinStateObject, AbstractIdentificationVariableDeclarationStateObject> joinBuilder;

	/**
	 *
	 */
	IJPQLQueryBuilder jpqlQueryBuilder;

	/**
	 * This visitor is used to retrieve a variable name from various type of an {@link Expression}.
	 */
	private LiteralVisitor literalVisitor;

	/**
	 *
	 */
	protected IManagedTypeProvider managedTypeProvider;

	/**
	 * The parent {@link StateObject} of the {@link StateObject} to create.
	 */
	protected JPQLQueryStateObject parent;

	/**
	 *
	 */
	private IBuilder<AbstractIdentificationVariableDeclarationStateObject, FromClauseStateObject> rangeDeclarationBuilder;

	/**
	 *
	 */
	private IBuilder<StateObject, SelectClauseStateObject> selectItemBuilder;

	/**
	 * This {@link IBuilder} is responsible to build a top-level <code><b>SELECT</b></code> statement,
	 * which populates the various clauses by converting the information contained in the parsed JPQL
	 * query.
	 */
	private IBuilder<SelectStatementStateObject, JPQLQueryStateObject> selectStatementBuilder;

	/**
	 *
	 */
	private IBuilder<AbstractIdentificationVariableDeclarationStateObject, SimpleFromClauseStateObject> simpleRangeDeclarationBuilder;

	/**
	 * This {@link IBuilder} is responsible to build a subquery <code><b>SELECT</b></code> statement,
	 * which populates the various clauses by converting the information contained in the parsed JPQL
	 * query.
	 */
	private IBuilder<SimpleSelectStatementStateObject, StateObject> simpleSelectStatementBuilder;

	/**
	 * The {@link StateObject} that was created based on the visited {@link Expression}.
	 */
	protected StateObject stateObject;

	/**
	 * This {@link IBuilder} is responsible to build an <code><b>UPDATE</b></code> statement,
	 * which populates the <code><b>UPDATE</b></code> clause and the optional <code><b>WHERE</b></code>
	 * clause by converting the information contained in the parsed JPQL query.
	 */
	private IBuilder<UpdateStatementStateObject, JPQLQueryStateObject> updateStatementBuilder;

	/**
	 * This {@link IBuilder} is responsible to build a <code><b>WHEN</b></code> clause for the
	 * <code><b>CASE</b></code> expression by converting the information contained in the parsed
	 * JPQL query.
	 */
	private IBuilder<CaseExpressionStateObject, CaseExpressionStateObject> whenClauseBuilder;

	/**
	 * Creates a new <code>StateObjectBuilder</code>.
	 */
	protected BasicStateObjectBuilder() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected <T extends StateObject> List<T> buildChildren(Expression expression) {

		StateObject oldStateObject = stateObject;
		List<T> stateObjects = new ArrayList<T>();

		for (Expression child : children(expression)) {
			child.accept(this);
			stateObjects.add((T) stateObject);
			stateObject = oldStateObject;
		}

		return stateObjects;
	}

	protected IBuilder<CollectionMemberDeclarationStateObject, AbstractFromClauseStateObject> buildCollectionDeclarationBuilder() {
		return new CollectionMemberDeclarationBuilder();
	}

	protected IBuilder<DeleteStatementStateObject, JPQLQueryStateObject> buildDeleteStatementBuilder() {
		return new DeleteStatementBuilder();
	}

	protected IBuilder<JoinStateObject, AbstractIdentificationVariableDeclarationStateObject> buildJoinBuilder() {
		return new JoinBuilder();
	}

	/**
	 * Creates the visitor that can retrieve the "literal" value from a given {@link Expression}
	 * based on the desired {@link LiteralType}.
	 *
	 * @return A new concrete instance of {@link LiteralVisitor}
	 */
	protected abstract LiteralVisitor buildLiteralVisitor();

	protected IBuilder<AbstractIdentificationVariableDeclarationStateObject, FromClauseStateObject> buildRangeDeclarationBuilder() {
		return new RangeDeclarationBuilder();
	}

	protected IBuilder<StateObject, SelectClauseStateObject> buildSelectItemBuilder() {
		return new SelectItemBuilder();
	}

	protected IBuilder<SelectStatementStateObject, JPQLQueryStateObject> buildSelectStatementBuilder() {
		return new SelectStatementBuilder();
	}

	protected IBuilder<AbstractIdentificationVariableDeclarationStateObject, SimpleFromClauseStateObject> buildSimpleRangeDeclarationBuilder() {
		return new SimpleRangeDeclarationBuilder();
	}

	protected IBuilder<SimpleSelectStatementStateObject, StateObject> buildSimpleSelectStatementBuilder() {
		return new SimpleSelectStatementBuilder();
	}

	/**
	 * Visits the given {@link Expression} and returned its {@link StateObject}.
	 *
	 * @param expression The {@link Expression} to be visited by this builder
	 * @return The {@link StateObject} representation of the given {@link Expression} or <code>null</code>
	 * if nothing could be created
	 */
	protected final StateObject buildStateObjectImp(Expression expression) {
		expression.accept(this);
		return stateObject;
	}

	protected IBuilder<UpdateStatementStateObject, JPQLQueryStateObject> buildUpdateStatementBuilder() {
		return new UpdateStatementBuilder();
	}

	protected IBuilder<CaseExpressionStateObject, CaseExpressionStateObject> buildWhenClauseBuilder() {
		return new WhenClauseBuilder();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Expression> List<T> children(Expression expression) {
		CollectionExpressionVisitor visitor = getCollectionExpressionVisitor();

		try {
			expression.accept(visitor);
			return (List<T>) visitor.children;
		}
		finally {
			visitor.reset();
		}
	}

	protected IBuilder<CollectionMemberDeclarationStateObject, AbstractFromClauseStateObject> getCollectionDeclarationBuilder() {
		if (collectionDeclarationBuilder == null) {
			collectionDeclarationBuilder = buildCollectionDeclarationBuilder();
		}
		return collectionDeclarationBuilder;
	}

	protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = new CollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	protected IBuilder<DeleteStatementStateObject, JPQLQueryStateObject> getDeleteStatementBuilder() {
		if (deleteStatementBuilder == null) {
			deleteStatementBuilder = buildDeleteStatementBuilder();
		}
		return deleteStatementBuilder;
	}

	protected IBuilder<JoinStateObject, AbstractIdentificationVariableDeclarationStateObject> getJoinBuilder() {
		if (joinBuilder == null) {
			joinBuilder = buildJoinBuilder();
		}
		return joinBuilder;
	}

	protected LiteralVisitor getLiteralVisitor() {
		if (literalVisitor == null) {
			literalVisitor = buildLiteralVisitor();
		}
		return literalVisitor;
	}

	protected IBuilder<AbstractIdentificationVariableDeclarationStateObject, FromClauseStateObject> getRangeDeclarationBuilder() {
		if (rangeDeclarationBuilder == null) {
			rangeDeclarationBuilder = buildRangeDeclarationBuilder();
		}
		return rangeDeclarationBuilder;
	}

	protected IBuilder<StateObject, SelectClauseStateObject> getSelectItemBuilder() {
		if (selectItemBuilder == null) {
			selectItemBuilder = buildSelectItemBuilder();
		}
		return selectItemBuilder;
	}

	protected IBuilder<SelectStatementStateObject, JPQLQueryStateObject> getSelectStatementBuilder() {
		if (selectStatementBuilder == null) {
			selectStatementBuilder = buildSelectStatementBuilder();
		}
		return selectStatementBuilder;
	}

	protected IBuilder<AbstractIdentificationVariableDeclarationStateObject, SimpleFromClauseStateObject> getSimpleRangeDeclarationBuilder() {
		if (simpleRangeDeclarationBuilder == null) {
			simpleRangeDeclarationBuilder = buildSimpleRangeDeclarationBuilder();
		}
		return simpleRangeDeclarationBuilder;
	}

	protected IBuilder<SimpleSelectStatementStateObject, StateObject> getSimpleSelectStatementBuilder() {
		if (simpleSelectStatementBuilder == null) {
			simpleSelectStatementBuilder = buildSimpleSelectStatementBuilder();
		}
		return simpleSelectStatementBuilder;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	protected IBuilder<UpdateStatementStateObject, JPQLQueryStateObject> getUpdateStatementBuilder() {
		if (updateStatementBuilder == null) {
			updateStatementBuilder = buildUpdateStatementBuilder();
		}
		return updateStatementBuilder;
	}

	/**
	 * Retrieves the "literal" from the given {@link Expression}. The literal to retrieve depends on
	 * the given {@link LiteralType type}. The literal is basically a string value like an
	 * identification variable name, an input parameter, a path expression, an abstract schema name,
	 * etc.
	 *
	 * @param expression The {@link Expression} to visit
	 * @param type The {@link LiteralType} helps to determine what to retrieve from the visited
	 * {@link Expression}
	 * @return A value from the given {@link Expression} or an empty string if the given {@link
	 * Expression} and the {@link LiteralType} do not match
	 */
	protected String literal(Expression expression, LiteralType type) {
		LiteralVisitor visitor = getLiteralVisitor();
		try {
			visitor.setType(type);
			visitor.literal = null;
			expression.accept(visitor);
			return visitor.literal;
		}
		finally {
			visitor.literal = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpression expression) {

		expression.getExpression().accept(this);

		AbsExpressionStateObject stateObject = new AbsExpressionStateObject(parent, this.stateObject);
		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {

		AbstractSchemaNameStateObject stateObject = new AbstractSchemaNameStateObject(
			parent,
			expression.getText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		AdditionExpressionStateObject stateObject = new AdditionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {

		expression.getExpression().accept(this);

		AllOrAnyExpressionStateObject stateObject = new AllOrAnyExpressionStateObject(
			parent,
			expression.getIdentifier(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		AndExpressionStateObject stateObject = new AndExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {

		expression.getExpression().accept(this);

		ArithmeticFactorStateObject stateObject = new ArithmeticFactorStateObject(
			parent,
			expression.isPlusSign(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {

		expression.getExpression().accept(this);

		AvgFunctionStateObject stateObject = new AvgFunctionStateObject(
			parent,
			expression.hasDistinct(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {

		BadExpressionStateObject stateObject = new BadExpressionStateObject(
			parent,
			expression.getExpression().toActualText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {

		expression.getExpression().accept(this);
		StateObject betweenStateObject = stateObject;

		expression.getLowerBoundExpression().accept(this);
		StateObject lowerBoundStateObject = stateObject;

		expression.getUpperBoundExpression().accept(this);
		StateObject upperBoundStateObject = stateObject;

		BetweenExpressionStateObject stateObject = new BetweenExpressionStateObject(
			parent,
			betweenStateObject,
			expression.hasNot(),
			lowerBoundStateObject,
			upperBoundStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {

		expression.getCaseOperand().accept(this);
		StateObject caseOperand = stateObject;

		expression.getElseExpression().accept(this);
		StateObject elseStateObject = stateObject;

		CaseExpressionStateObject caseExpressionStateObject = new CaseExpressionStateObject(
			parent,
			caseOperand,
			Collections.<WhenClauseStateObject>emptyList(),
			elseStateObject
		);

		whenClauseBuilder().buildStateObject(caseExpressionStateObject, expression);

		caseExpressionStateObject.setExpression(expression);
		this.stateObject = caseExpressionStateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {

		CoalesceExpressionStateObject stateObject = new CoalesceExpressionStateObject(
			parent,
			buildChildren(expression.getExpression())
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {

		List<StateObject> stateObjects = buildChildren(expression);

		CollectionExpressionStateObject stateObject = new CollectionExpressionStateObject(
			parent,
			stateObjects
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
		// Not done here
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {

		expression.getEntityExpression().accept(this);
		StateObject entityExpression = stateObject;

		CollectionMemberExpressionStateObject stateObject = new CollectionMemberExpressionStateObject(
			parent,
			entityExpression,
			expression.hasNot(),
			expression.hasOf(),
			literal(expression.getCollectionValuedPathExpression(), LiteralType.PATH_EXPRESSION_ALL_PATH)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {

		CollectionValuedPathExpressionStateObject stateObject = new CollectionValuedPathExpressionStateObject(
			parent,
			expression.toActualText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		ComparisonExpressionStateObject stateObject = new ComparisonExpressionStateObject(
			parent,
			leftStateObject,
			expression.getComparisonOperator(),
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {

		ConcatExpressionStateObject stateObject = new ConcatExpressionStateObject(
			parent,
			buildChildren(expression.getExpression())
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {

		ConstructorExpressionStateObject stateObject = new ConstructorExpressionStateObject(
			parent,
			expression.getClassName(),
			buildChildren(expression.getConstructorItems())
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {

		expression.getExpression().accept(this);

		CountFunctionStateObject stateObject = new CountFunctionStateObject(
			parent,
			expression.hasDistinct(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {
		DateTimeStateObject stateObject = new DateTimeStateObject(parent, expression.getText());
		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
		// Done via DeleteStatementBuilder
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
		stateObject = getDeleteStatementBuilder().buildStateObject(parent, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		DivisionExpressionStateObject stateObject = new DivisionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {

		EmptyCollectionComparisonExpressionStateObject stateObject = new EmptyCollectionComparisonExpressionStateObject(
			parent,
			expression.hasNot(),
			literal(expression.getExpression(), LiteralType.PATH_EXPRESSION_ALL_PATH)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {

		EntityTypeLiteralStateObject stateObject = new EntityTypeLiteralStateObject(
			parent,
			expression.getEntityTypeName()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {

		EntryExpressionStateObject stateObject = new EntryExpressionStateObject(
			parent,
			literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {

		expression.getExpression().accept(this);

		ExistsExpressionStateObject stateObject = new ExistsExpressionStateObject(
			parent,
			expression.hasNot(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(FromClause expression) {
		// Not done here
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(GroupByClause expression) {
		// Not done here
		stateObject = null;
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

		IdentificationVariableStateObject stateObject = new IdentificationVariableStateObject(
			parent,
			expression.getText()
		);

		stateObject.setVirtual(expression.isVirtual());
		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(final IdentificationVariableDeclaration expression) {

		StateObjectVisitor visitor = new AbstractStateObjectVisitor() {
			@Override
			public void visit(FromClauseStateObject stateObject) {
				getRangeDeclarationBuilder().buildStateObject(stateObject, expression);
			}
			@Override
			public void visit(SimpleFromClauseStateObject stateObject) {
				getSimpleRangeDeclarationBuilder().buildStateObject(stateObject, expression);
			}
		};

		stateObject.accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {

		IndexExpressionStateObject stateObject = new IndexExpressionStateObject(
			parent,
			literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {

		expression.getExpression().accept(this);

		InExpressionStateObject stateObject = new InExpressionStateObject(
			parent,
			this.stateObject,
			expression.hasNot(),
			buildChildren(expression.getInItems())
		);

		stateObject.setSingleInputParameter(expression.isSingleInputParameter());
		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {

		InputParameterStateObject stateObject = new InputParameterStateObject(
			parent,
			expression.getParameter()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(Join expression) {
		stateObject = getJoinBuilder().buildStateObject(
			(IdentificationVariableDeclarationStateObject) stateObject,
			expression
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {

		parent = new JPQLQueryStateObject(jpqlQueryBuilder, managedTypeProvider);
		parent.setExpression(expression);

		expression.getQueryStatement().accept(this);
		parent.setQueryStatement(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {

		KeyExpressionStateObject stateObject = new KeyExpressionStateObject(
			parent,
			literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {

		KeywordExpressionStateObject stateObject = new KeywordExpressionStateObject(
			parent,
			expression.getText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {

		expression.getExpression().accept(this);

		LengthExpressionStateObject stateObject = new LengthExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {

		expression.getStringExpression().accept(this);
		StateObject stringStateObject = stateObject;

		expression.getPatternValue().accept(this);
		StateObject patternValue = stateObject;

		LikeExpressionStateObject stateObject = new LikeExpressionStateObject(
			parent,
			stringStateObject,
			expression.hasNot(),
			patternValue,
			literal(expression.getEscapeCharacter(), LiteralType.STRING_LITERAL)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpression expression) {

		expression.getFirstExpression().accept(this);
		StateObject firstStateObject = stateObject;

		expression.getSecondExpression().accept(this);
		StateObject secondStateObject = stateObject;

		expression.getThirdExpression().accept(this);
		StateObject thirdStateObject = stateObject;

		LocateExpressionStateObject stateObject = new LocateExpressionStateObject(
			parent,
			firstStateObject,
			secondStateObject,
			thirdStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {

		expression.getExpression().accept(this);

		LowerExpressionStateObject stateObject = new LowerExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {

		expression.getExpression().accept(this);

		MaxFunctionStateObject stateObject = new MaxFunctionStateObject(
			parent,
			expression.hasDistinct(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {

		expression.getExpression().accept(this);

		MinFunctionStateObject stateObject = new MinFunctionStateObject(
			parent,
			expression.hasDistinct(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {

		expression.getFirstExpression().accept(this);
		StateObject firstStateObject = stateObject;

		expression.getSecondExpression().accept(this);
		StateObject secondStateObject = stateObject;

		ModExpressionStateObject stateObject = new ModExpressionStateObject(
			parent,
			firstStateObject,
			secondStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		MultiplicationExpressionStateObject stateObject = new MultiplicationExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {

		expression.getExpression().accept(this);

		NotExpressionStateObject stateObject = new NotExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {

		expression.getExpression().accept(this);

		NullComparisonExpressionStateObject stateObject = new NullComparisonExpressionStateObject(
			parent,
			expression.hasNot(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {

		expression.getFirstExpression().accept(this);
		StateObject firstStateObject = stateObject;

		expression.getSecondExpression().accept(this);
		StateObject secondStateObject = stateObject;

		NullIfExpressionStateObject stateObject = new NullIfExpressionStateObject(
			parent,
			firstStateObject,
			secondStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {

		NumericLiteralStateObject stateObject = new NumericLiteralStateObject(
			parent,
			expression.getText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {

		ObjectExpressionStateObject stateObject = new ObjectExpressionStateObject(
			parent,
			literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(OrderByClause expression) {
		// Not done here
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(OrderByItem expression) {

		OrderByClauseStateObject orderByClause = (OrderByClauseStateObject) stateObject;

		OrderByItemStateObject orderByItem = orderByClause.addItem(expression.getOrdering());
		orderByItem.setExpression(expression);

		expression.getExpression().accept(this);
		orderByItem.setStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		OrExpressionStateObject stateObject = new OrExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(RangeVariableDeclaration expression) {
		// Not done here
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(final ResultVariable expression) {

		StateObjectVisitor visitor = new AbstractStateObjectVisitor() {
			@Override
			public void visit(SelectClauseStateObject stateObject) {
				BasicStateObjectBuilder.this.stateObject = getSelectItemBuilder().buildStateObject(
					stateObject,
					expression
				);
			}
		};

		stateObject.accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(SelectClause expression) {
		// Not done here
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
		stateObject = getSelectStatementBuilder().buildStateObject(parent, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(SimpleFromClause expression) {
		// Done via SimpleSelectStatementBuilder
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(SimpleSelectClause expression) {
		// Done via SimpleSelectStatementBuilder
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {
		stateObject = getSimpleSelectStatementBuilder().buildStateObject(parent, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {

		expression.getExpression().accept(this);

		SizeExpressionStateObject stateObject = new SizeExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {

		expression.getExpression().accept(this);

		SqrtExpressionStateObject stateObject = new SqrtExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {

		StateFieldPathExpressionStateObject stateObject = new StateFieldPathExpressionStateObject(
			parent,
			expression.toActualText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {

		StringLiteralStateObject stateObject = new StringLiteralStateObject(
			parent,
			expression.getText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpression expression) {

		expression.getExpression().accept(this);

		SubExpressionStateObject stateObject = new SubExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpression expression) {

		expression.getFirstExpression().accept(this);
		StateObject firstExpression = stateObject;

		expression.getSecondExpression().accept(this);
		StateObject secondExpression = stateObject;

		expression.getThirdExpression().accept(this);
		StateObject thirdExpression = stateObject;

		SubstringExpressionStateObject stateObject = new SubstringExpressionStateObject(
			parent,
			firstExpression,
			secondExpression,
			thirdExpression
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {

		expression.getLeftExpression().accept(this);
		StateObject leftStateObject = stateObject;

		expression.getRightExpression().accept(this);
		StateObject rightStateObject = stateObject;

		SubtractionExpressionStateObject stateObject = new SubtractionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {

		expression.getExpression().accept(this);

		SumFunctionStateObject stateObject = new SumFunctionStateObject(
			parent,
			expression.hasDistinct(),
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {

		expression.getExpression().accept(this);
		StateObject trimStateObject = stateObject;

		expression.getTrimCharacter().accept(this);
		StateObject trimCharacter = stateObject;

		TrimExpressionStateObject stateObject = new TrimExpressionStateObject(
			parent,
			expression.getSpecification(),
			trimCharacter,
			trimStateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {

		expression.getExpression().accept(this);

		TypeExpressionStateObject stateObject = new TypeExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {

		UnknownExpressionStateObject stateObject = new UnknownExpressionStateObject(
			parent,
			expression.getText()
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(UpdateClause expression) {
		// Done via UpdateStatementBuilder
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(UpdateItem expression) {
		// Done via UpdateStatementBuilder
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
		stateObject = getUpdateStatementBuilder().buildStateObject(parent, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {

		expression.getExpression().accept(this);

		UpperExpressionStateObject stateObject = new UpperExpressionStateObject(
			parent,
			this.stateObject
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {

		ValueExpressionStateObject stateObject = new ValueExpressionStateObject(
			parent,
			literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE)
		);

		stateObject.setExpression(expression);
		this.stateObject = stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void visit(WhenClause expression) {
		// Done throw WhenClauseBuilder
		stateObject = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * Returns the {@link IBuilder} that is responsible to visit each {@link WhenClause} and to
	 * create the corresponding {@link WhenClauseStateObject}. The new state objects have to be added
	 * to {@link CaseExpressionStateObject} by the builder.
	 *
	 * @return The builder of the <code><b>WHEN</b></code> clauses
	 */
	protected IBuilder<CaseExpressionStateObject, CaseExpressionStateObject> whenClauseBuilder() {
		if (whenClauseBuilder == null) {
			whenClauseBuilder = buildWhenClauseBuilder();
		}
		return whenClauseBuilder;
	}

	/**
	 * This builder is responsible to create a new identification variable declaration and to add it
	 * to the state object representing the <code><b>FROM</b></code> clause.
	 */
	protected abstract class AbstractRangeDeclarationBuilder<S extends AbstractFromClauseStateObject>
	          extends AbstractTraverseChildrenVisitor
	          implements IBuilder<AbstractIdentificationVariableDeclarationStateObject, S> {

		/**
		 * The concrete instance of {@link AbstractFromClauseStateObject} where the new identification
		 * variable declaration is added.
		 */
		protected S parent;

		/**
		 * The concrete instance of {@link IdentificationVariableDeclarationStateObject}
		 * that represents the visited {@link IdentificationVariableDeclaration}.
		 */
		protected AbstractIdentificationVariableDeclarationStateObject stateObject;

		/**
		 * Creates the concrete instance of an {@link AbstractIdentificationVariableDeclarationStateObject}
		 * for the given {@link IdentificationVariableDeclaration}.
		 *
		 * @param expression The {@link IdentificationVariableDeclaration} to convert into a
		 * {@link StateObject}
		 * @return A new {@link StateObject} representing an identification variable declaration
		 */
		protected abstract AbstractIdentificationVariableDeclarationStateObject addRangeDeclaration(IdentificationVariableDeclaration expression);

		/**
		 * {@inheritDoc}
		 */
		public AbstractIdentificationVariableDeclarationStateObject buildStateObject(S parent, Expression expression) {
			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent      = null;
				this.stateObject = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			stateObject.setRootPath(expression.getText());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			stateObject.setIdentificationVariable(expression.getText());

			IdentificationVariableStateObject variable = stateObject.getRangeVariableDeclaration().getIdentificationVariableStateObject();
			variable.setVirtual(expression.isVirtual());
			variable.setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {

			stateObject = addRangeDeclaration(expression);
			stateObject.setExpression(expression);

			getJoinBuilder().buildStateObject(stateObject, expression.getJoins());

			expression.getRangeVariableDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			AbstractRangeVariableDeclarationStateObject declaration = stateObject.getRangeVariableDeclaration();
			declaration.setAs(expression.hasAs());
			declaration.setExpression(expression);
			super.visit(expression);
		}
	}

	/**
	 * The abstract definition of the builder that is responsible to create the {@link StateObject}
	 * representation of the <code><b>SELECT</b></code> statement.
	 */
	protected abstract class AbstractSelectStatementBuilder<T extends AbstractSelectStatementStateObject, P extends StateObject>
	                   extends    AbstractTraverseChildrenVisitor
	                   implements IBuilder<T, P> {

		/**
		 * The parent of the <code><b>SELECT</b></code> statement.
		 */
		protected P parent;

		/**
		 * The concrete class of {@link AbstractSelectStatementStateObject}.
		 */
		protected T stateObject;

		/**
		 * {@inheritDoc}
		 */
		public T buildStateObject(P parent, Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			getCollectionDeclarationBuilder().buildStateObject(stateObject.getFromClause(), expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression) {
			GroupByClauseStateObject groupByClause = stateObject.addGroupByClause();
			groupByClause.setExpression(expression);
			groupByClause.addItems(buildChildren(expression.getGroupByItems()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(HavingClause expression) {
			HavingClauseStateObject havingClause = stateObject.addHavingClause();
			havingClause.setConditional(buildStateObjectImp(expression));
			havingClause.setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract void visit(IdentificationVariableDeclaration expression);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {
			WhereClauseStateObject whereClause = stateObject.addWhereClause();
			whereClause.setConditional(buildStateObjectImp(expression));
			whereClause.setExpression(expression);
		}
	}

	protected class CollectionExpressionVisitor extends AnonymousExpressionVisitor {

		List<Expression> children;

		CollectionExpressionVisitor() {
			super();
			reset();
		}

		void reset() {
			children = new ArrayList<Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			CollectionTools.addAll(children, expression.children());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			children.add(expression);
		}
	}

	protected class CollectionMemberDeclarationBuilder extends AbstractTraverseChildrenVisitor
	                                                   implements IBuilder<CollectionMemberDeclarationStateObject, AbstractFromClauseStateObject> {

		protected AbstractFromClauseStateObject parent;
		protected CollectionMemberDeclarationStateObject stateObject;

		/**
		 * {@inheritDoc}
		 */
		public CollectionMemberDeclarationStateObject buildStateObject(AbstractFromClauseStateObject parent,
		                                                               Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent      = null;
				this.stateObject = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			stateObject = parent.addCollectionDeclaration();
			stateObject.setAs(expression.hasAs());
			stateObject.setExpression(expression);
			stateObject.setDerived(expression.isDerived());
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			CollectionValuedPathExpressionStateObject path = stateObject.getCollectionValuedPath();
			path.setPaths(expression.paths());
			path.setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			IdentificationVariableStateObject variable = stateObject.getIdentificationVariable();
			variable.setExpression(expression);
			variable.setText(expression.getText());
			variable.setVirtual(expression.isVirtual());
		}
	}

	/**
	 * This builder is responsible to create the {@link StateObject} representation of the
	 * <code><b>DELETE</b></code> query statement.
	 */
	protected class DeleteStatementBuilder extends AbstractTraverseChildrenVisitor
	                                       implements IBuilder<DeleteStatementStateObject, JPQLQueryStateObject> {

		protected JPQLQueryStateObject parent;
		protected DeleteStatementStateObject stateObject;

		/**
		 * {@inheritDoc}
		 */
		public DeleteStatementStateObject buildStateObject(JPQLQueryStateObject parent,
		                                                   Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			AbstractSchemaNameStateObject entityStateObject = stateObject.getAbstractSchemaNameStateObject();
			entityStateObject.setText(expression.getText());
			entityStateObject.setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
			stateObject.getModifyClause().setExpression(expression);
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			stateObject = parent.addDeleteStatement();
			stateObject.setExpression(expression);
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			IdentificationVariableStateObject variable = stateObject.getIdentificationVariableStateObject();
			variable.setExpression(expression);
			variable.setText(expression.getText());
			variable.setVirtual(expression.isVirtual());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			stateObject.getModifyClause().getRangeVariableDeclaration().setExpression(expression);
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {

			WhereClauseStateObject whereClause = stateObject.addWhereClause();
			whereClause.setExpression(expression);

			expression.getConditionalExpression().accept(BasicStateObjectBuilder.this);
			StateObject conditionalStateObject = BasicStateObjectBuilder.this.stateObject;

			whereClause.setConditional(conditionalStateObject);
		}
	}

	protected class JoinBuilder extends AbstractExpressionVisitor
	                            implements IBuilder<JoinStateObject, AbstractIdentificationVariableDeclarationStateObject> {

		protected AbstractIdentificationVariableDeclarationStateObject parent;
		protected JoinStateObject stateObject;

		/**
		 * {@inheritDoc}
		 */
		public JoinStateObject buildStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
		                                        Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent      = null;
				this.stateObject = null;
			}
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
		public void visit(IdentificationVariableDeclaration expression) {
			expression.getRangeVariableDeclaration().accept(this);
			expression.getJoins().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {

			stateObject = parent.addJoin(
				expression.getIdentifier(),
				literal(expression.getJoinAssociationPath(),    LiteralType.PATH_EXPRESSION_ALL_PATH),
				literal(expression.getIdentificationVariable(), LiteralType.IDENTIFICATION_VARIABLE)
			);

			stateObject.setAs(expression.hasAs());
			stateObject.setExpression(expression);

			expression.getJoinAssociationPath().accept(this);
			expression.getIdentificationVariable().accept(this);
		}
	}

	/**
	 * This builder is responsible to create a new identification variable declaration and to add it
	 * to the state object representing the <code><b>FROM</b></code> clause of the top-level query.
	 */
	protected class RangeDeclarationBuilder extends AbstractRangeDeclarationBuilder<FromClauseStateObject> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected AbstractIdentificationVariableDeclarationStateObject addRangeDeclaration(IdentificationVariableDeclaration expression) {
			return parent.addRangeDeclaration();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			super.visit(expression);
			IdentificationVariableDeclarationStateObject stateObject = (IdentificationVariableDeclarationStateObject) this.stateObject;
			stateObject.getRootStateObject().setExpression(expression);
		}
	}

	/**
	 * This builder is responsible to create the items owned by the top-level
	 * <code><b>SELECT</b></code> clause.
	 */
	protected class SelectItemBuilder extends AnonymousExpressionVisitor
	                                  implements IBuilder<StateObject, SelectClauseStateObject> {

		protected SelectClauseStateObject parent;
		protected StateObject stateObject;

		/**
		 * {@inheritDoc}
		 */
		public StateObject buildStateObject(SelectClauseStateObject parent, Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return this.stateObject;
			}
			finally {
				this.parent      = null;
				this.stateObject = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.accept(BasicStateObjectBuilder.this);
			stateObject = BasicStateObjectBuilder.this.stateObject;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {

			expression.getSelectExpression().accept(BasicStateObjectBuilder.this);

			ResultVariableStateObject stateObject = new ResultVariableStateObject(
				parent,
				BasicStateObjectBuilder.this.stateObject,
				expression.hasAs(),
				literal(expression.getResultVariable(), LiteralType.RESULT_VARIABLE)
			);

			stateObject.setExpression(expression);
			this.stateObject = stateObject;
		}
	}

	/**
	 * This builder is responsible to create the {@link StateObject} representation of the
	 * <code><b>SELECT</b></code> query statement.
	 */
	protected class SelectStatementBuilder extends AbstractSelectStatementBuilder<SelectStatementStateObject, JPQLQueryStateObject> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {

			stateObject.getFromClause().setExpression(expression);

			for (Expression child : children(expression.getDeclaration())) {
				child.accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			getRangeDeclarationBuilder().buildStateObject(stateObject.getFromClause(), expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression) {

			OrderByClauseStateObject orderByClause = stateObject.addOrderByClause();
			orderByClause.setExpression(expression);

			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression) {

			OrderByClauseStateObject orderByClause = stateObject.getOrderByClause();

			OrderByItemStateObject orderByItem = orderByClause.addItem(expression.getOrdering());
			orderByItem.setExpression(expression);

			expression.getExpression().accept(BasicStateObjectBuilder.this);
			orderByItem.setStateObject(BasicStateObjectBuilder.this.stateObject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {

			SelectClauseStateObject selectClause = stateObject.getSelectClause();
			selectClause.setExpression(expression);
			selectClause.setDistinct(expression.hasDistinct());

			for (Expression child : children(expression.getSelectExpression())) {

				StateObject stateObject = getSelectItemBuilder().buildStateObject(selectClause, child);

				if (stateObject != null) {
					selectClause.addItem(stateObject);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {

			SelectStatementStateObject stateObject = parent.addSelectStatement();
			stateObject.setExpression(expression);
			this.stateObject = stateObject;

			super.visit(expression);
		}
	}

	/**
	 * This builder is responsible to create a new identification variable declaration and to add it
	 * to the state object representing the <code><b>FROM</b></code> clause of a subquery.
	 */
	protected class SimpleRangeDeclarationBuilder extends AbstractRangeDeclarationBuilder<SimpleFromClauseStateObject> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected AbstractIdentificationVariableDeclarationStateObject addRangeDeclaration(IdentificationVariableDeclaration expression) {

			String root = expression.getRangeVariableDeclaration().toActualText();
			int index = root.indexOf(AbstractExpression.SPACE);

			if (index > 0) {
				root = root.substring(0, index);
			}

			if (root.indexOf(AbstractExpression.DOT) > 0) {
				return parent.addDerivedPathDeclaration();
			}

			return parent.addRangeDeclaration();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			DerivedPathIdentificationVariableDeclarationStateObject stateObject = (DerivedPathIdentificationVariableDeclarationStateObject) this.stateObject;
			stateObject.setRootPath(expression.toActualText());
			stateObject.getRootStateObject().setExpression(expression);
		}
	}

	/**
	 * This builder is responsible to create the {@link StateObject} representation of the
	 * <code><b>SELECT</b></code> subquery.
	 */
	protected class SimpleSelectStatementBuilder extends AbstractSelectStatementBuilder<SimpleSelectStatementStateObject, StateObject> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			getSimpleRangeDeclarationBuilder().buildStateObject(stateObject.getFromClause(), expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {

			stateObject.getFromClause().setExpression(expression);

			for (Expression child : children(expression.getDeclaration())) {
				child.accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression) {

			SimpleSelectClauseStateObject selectClause = stateObject.getSelectClause();
			selectClause.setDistinct(expression.hasDistinct());
			selectClause.setExpression(expression);

			List<StateObject> children = buildChildren(expression.getSelectExpression());

			if (!children.isEmpty()) {
				selectClause.setSelectItem(children.get(0));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {

			stateObject = new SimpleSelectStatementStateObject(parent);
			stateObject.setExpression(expression);
			stateObject.setParent(parent);

			super.visit(expression);
		}
	}

	/**
	 * This builder is responsible to create the {@link StateObject} representation of the
	 * <code><b>UPDATE</b></code> query statement.
	 */
	protected class UpdateStatementBuilder extends AbstractTraverseChildrenVisitor
	                                       implements IBuilder<UpdateStatementStateObject, JPQLQueryStateObject> {

		protected JPQLQueryStateObject parent;
		protected UpdateStatementStateObject stateObject;
		protected UpdateItemStateObject updateItem;

		/**
		 * {@inheritDoc}
		 */
		public UpdateStatementStateObject buildStateObject(JPQLQueryStateObject parent,
		                                                   Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return stateObject;
			}
			finally {
				this.parent      = null;
				this.stateObject = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			stateObject.setEntityName(expression.getText());
			stateObject.getModifyClause().getAbstractSchemaNameStateObject().setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			if (updateItem == null) {

				IdentificationVariableStateObject variable = stateObject.getIdentificationVariableStateObject();

				variable.setText(expression.getText());
				variable.setVirtual(expression.isVirtual());
				variable.setExpression(expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			stateObject.getModifyClause().getRangeVariableDeclaration().setExpression(expression);
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			updateItem.getStateFieldPath().setPaths(expression.paths());
			updateItem.getStateFieldPath().setExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			stateObject.getModifyClause().setExpression(expression);
			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateItem expression) {

			UpdateClauseStateObject updateClause = stateObject.getModifyClause();
			String identificationVariable = updateClause.getIdentificationVariable();

			updateItem = updateClause.addItem();
			updateItem.setExpression(expression);

			try {
				// Retrieve the state field path expression
				String path = literal(
					expression.getStateFieldPathExpression(),
					LiteralType.PATH_EXPRESSION_ALL_PATH
				);

				if (!path.startsWith(identificationVariable + ".")) {
					updateItem.setPath(identificationVariable + "." + path);
				}
				else {
					updateItem.setPath(path);
				}

				// Set the virtual identification variable
				if (!updateClause.isIdentificationVariableDefined()) {
					updateItem.setVirtualIdentificationVariable(updateClause.getIdentificationVariable());
				}

				// Create the new value
				StateObject stateObject = buildStateObjectImp(expression.getNewValue());
				updateItem.setNewValue(stateObject);
			}
			finally {
				updateItem = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {

			UpdateStatementStateObject stateObject = parent.addUpdateStatement();
			stateObject.setExpression(expression);
			this.stateObject = stateObject;

			super.visit(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {

			WhereClauseStateObject whereClause = stateObject.addWhereClause();
			whereClause.setExpression(expression);

			StateObject stateObject = buildStateObjectImp(expression.getConditionalExpression());
			whereClause.setConditional(stateObject);
		}
	}

	/**
	 * This builder is responsible to create the <code><b>WHEN</b></code> clauses for a
	 * <code><b>CASE</b></code> expression.
	 */
	protected class WhenClauseBuilder extends AbstractTraverseChildrenVisitor
	                                  implements IBuilder<CaseExpressionStateObject, CaseExpressionStateObject> {

		/**
		 * The {@link CaseExpressionStateObject} for which its {@link WhenClauseStateObject
		 * WhenClauseStateObjects} are created by this builder.
		 */
		private CaseExpressionStateObject parent;

		/**
		 * {@inheritDoc}
		 */
		public CaseExpressionStateObject buildStateObject(CaseExpressionStateObject parent,
		                                                  Expression expression) {

			try {
				this.parent = parent;
				expression.accept(this);
				return parent;
			}
			finally {
				this.parent = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhenClause expression) {

			StateObject whenStateObject = buildStateObjectImp(expression.getWhenExpression());
			StateObject thenStateObject = buildStateObjectImp(expression.getThenExpression());

			WhenClauseStateObject stateObject = parent.addWhenClause(whenStateObject, thenStateObject);
			stateObject.setExpression(expression);
		}
	}
}