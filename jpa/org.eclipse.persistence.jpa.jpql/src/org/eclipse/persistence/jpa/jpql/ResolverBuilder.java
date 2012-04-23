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
package org.eclipse.persistence.jpa.jpql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
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
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
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
import org.eclipse.persistence.jpa.jpql.parser.OnClause;
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
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
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
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This visitor creates a {@link Resolver} that gives information about the visited {@link Expression}.
 * The actual {@link Resolver} will calculate the proper {@link IType} as well.
 * <p>
 * The type of an {@link Expression} follows the following rules:
 * <ul>
 * <li>The type of the query result specified by the <b>SELECT</b> clause of a query is an entity
 * abstract schema type, a state field type, the result of a scalar expression, the result of an
 * aggregate function, the result of a construction operation, or some sequence of these.</li>
 *
 * <li>The result type of the <b>SELECT</b> clause is defined by the result types of the select
 * expressions contained in it. When multiple select expressions are used in the <b>SELECT</b>
 * clause, the elements in this result correspond in order to the order of their specification in
 * the <b>SELECT</b> clause and in type to the result types of each of the select expressions.</li>
 *
 * <li>The result type of an <code>identification_variable</code> is the type of the entity object
 * or embeddable object to which the identification variable corresponds. The type of an
 * <code>identification_variable</code> that refers to an entity abstract schema type is the type of
 * the entity to which that identification variable corresponds or a subtype as determined by the
 * object/relational mapping.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is a
 * <code>state_field_path_expression</code> is the same type as the corresponding state field of the
 * entity or embeddable class. If the state field of the entity is a primitive type, the result type
 * is the corresponding object type.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is a
 * <code>single_valued_object_path_expression</code> is the type of the entity object or embeddable
 * object to which the path expression corresponds. A
 * <code>single_valued_object_path_expression</code> that results in an entity object will result in
 * an entity of the type of the relationship field or the subtype of the relationship field of the
 * entity object as determined by the object/relational mapping.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is an
 * identification_variable to which the <code>KEY</code> or <code>VALUE</code> function has been
 * applied is determined by the type of the map key or value respectively, as defined by the above
 * rules.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is an
 * identification_variable to which the <code>ENTRY</code> function has been applied is
 * {@link java.util.Map.Entry Map.Entry}, where the key and value types of the map entry are
 * determined by the above rules as applied to the map key and map value respectively.</li>
 *
 * <li>The result type of a <code>scalar_expression</code> is the type of the scalar value to which
 * the expression evaluates.</li>
 *
 * <li>The result type of an <code>entity_type_expression</code> scalar expression is the Java class
 * to which the resulting abstract schema type corresponds.</li>
 *
 * <li>The result type of a <code>constructor_expression</code> is the type of the class for which
 * the constructor is defined. The types of the arguments to the constructor are defined by the
 * above rules.</li>
 * </ul>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ResolverBuilder implements ExpressionVisitor {

	/**
	 * This visitor is responsible to retrieve the {@link CollectionExpression} if it is visited.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 * The context used to query information about the JPQL query.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * The {@link Resolver} for the {@link Expression} that was visited.
	 */
	protected Resolver resolver;

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a double.
	 */
	public static Pattern DOUBLE_REGEXP = Pattern.compile("^[-+]?[0-9]*(\\.[0-9]+)?([dD]|([eE][-+]?[0-9]+))?$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a float.
	 */
	public static Pattern FLOAT_REGEXP = Pattern.compile("^[-+]?[0-9]*(\\.[0-9]+)?[fF]$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as an integer.
	 */
	public static Pattern INTEGER_REGEXP = Pattern.compile("^[-+]?[0-9]+$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a long.
	 */
	public static Pattern LONG_REGEXP = Pattern.compile("^[-+]?[0-9]+[lL]?$");

	/**
	 * Creates a new <code>ResolverBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public ResolverBuilder(JPQLQueryContext queryContext) {
		super();
		Assert.isNotNull(queryContext, "The JPQLQueryContext cannot be null");
		this.queryContext = queryContext;
	}

	/**
	 * Creates a new {link Resolver} that simply wraps the already determined type by using its
	 * fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the Java type to wrap with a {@link Resolver}
	 * @return A new {@link Resolver}
	 */
	protected Resolver buildClassNameResolver(String typeName) {
		return new ClassNameResolver(getDeclarationResolver(), typeName);
	}

	/**
	 * Creates a new {link Resolver} that simply wraps the already determined type.
	 *
	 * @param type The Java type to wrap with a {@link Resolver}
	 * @return A new {@link Resolver}
	 */
	protected Resolver buildClassResolver(Class<?> type) {
		return new ClassResolver(getDeclarationResolver(), type);
	}

	/**
	 * Creates a visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return A new {@link CollectionExpressionVisitor}
	 */
	protected CollectionExpressionVisitor buildCollectionExpressionVisitor() {
		return new CollectionExpressionVisitor();
	}

	/**
	 * Creates a new {@link Resolver} for the given collection-valued path expression.
	 *
	 * @param variableName The last segment of the collection-valued path expression
	 * @return A new {@link Resolver} that can get the information for a collection-valued path
	 */
	protected Resolver buildCollectionValuedFieldResolver(String variableName) {

		Resolver resolver = this.resolver.getChild(variableName);

		if (resolver == null) {
			resolver = new CollectionValuedFieldResolver(this.resolver, variableName);
		}

		return resolver;
	}

	/**
	 * Creates a new {@link Resolver} that will resolve the given enum literal.
	 *
	 * @param expression The {@link Expression} that represents the enum literal
	 * @param type The {@link IType} representing the {@link Enum} type
	 * @param enumLiteral The fully qualified enum constant
	 * @return The {@link Resolver} for an enum literal
	 */
	protected Resolver buildEnumResolver(AbstractPathExpression expression,
	                                     IType type,
	                                     String enumLiteral) {

		DeclarationResolver parent = getDeclarationResolver(expression);
		return new EnumLiteralResolver(parent, type, enumLiteral);
	}

	/**
	 * Creates a new {@link Resolver} that is used when nothing can be resolved.
	 *
	 * @return A "<code>null</code>" version of a {@link Resolver}
	 */
	protected Resolver buildNullResolver() {
		return new NullResolver(getDeclarationResolver());
	}

	/**
	 * Creates a new {@link Resolver} for the given state field path expression.
	 *
	 * @param variableName The last segment of the state field path expression
	 * @return A new {@link Resolver} that can get the information for a state field path
	 */
	protected Resolver buildStateFieldResolver(String variableName) {

		Resolver resolver = this.resolver.getChild(variableName);

		if (resolver == null) {
			resolver = new StateFieldResolver(this.resolver, variableName);
		}

		return resolver;
	}

	/**
	 * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
	 * object of that type.
	 *
	 * @param expression The {@link Expression} to cast
	 * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
	 * if it is any other object
	 */
	protected CollectionExpression getCollectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = getCollectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return The {@link CollectionExpressionVisitor}
	 * @see #buildCollectionExpressionVisitor()
	 */
	protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = buildCollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	/**
	 * Returns the {@link DeclarationResolver} of the current query's declaration. For a
	 * <b>SELECT</b> query, it contains the information defined in the <b>FROM</b> clause. For
	 * <b>DELETE</b> and <b>UPDATE</b> queries, it contains a single range declaration variable. If
	 * the current query is a subquery, then it contains the information defined in the
	 * <code>FROM</code> clause.
	 *
	 * @return The {@link DeclarationResolver} for the current query being visited
	 */
	protected DeclarationResolver getDeclarationResolver() {
		return queryContext.getDeclarationResolver();
	}

	/**
	 * Returns the {@link DeclarationResolver} of the current query's declaration. For a
	 * <b>SELECT</b> query, it contains the information defined in the <b>FROM</b> clause. For
	 * <b>DELETE</b> and <b>UPDATE</b> queries, it contains a single range variable declaration. If
	 * the current query is a subquery, then it contains the information defined in the subquery
	 * <code>FROM</code> clause.
	 *
	 * @param expression The {@link Expression} that will be used to retrieve its query expression,
	 * i.e. either {@link JPQLExpression} or {@link SimpleSelectStatement}
	 * @return The {@link DeclarationResolver} for the current query being visited
	 */
	protected DeclarationResolver getDeclarationResolver(Expression expression) {
		return queryContext.getDeclarationResolver(expression);
	}

	/**
	 * Returns the {@link JPQLQueryContext} that contains information related to the JPQL query.
	 *
	 * @return The {@link JPQLQueryContext}, which is never <code>null</code>
	 */
	protected JPQLQueryContext getQueryContext() {
		return queryContext;
	}

	/**
	 * Returns the current {@link Resolver} used to resolve an {@link Expression}.
	 *
	 * @return The current {@link Resolver}, which should never be <code>null</code>
	 */
	public Resolver getResolver() {
		return resolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpression expression) {

		// Visit the child expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new AbsFunctionResolver(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {

		String abstractSchemaName = expression.getText();

		// If the abstract schema name exists, then map it to its entity
		if (ExpressionTools.stringIsNotEmpty(abstractSchemaName)) {
			DeclarationResolver parent = getDeclarationResolver(expression);
			resolver = new EntityResolver(parent, abstractSchemaName);
		}
		else {
			resolver = buildNullResolver();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {

		// First traverse the expression
		expression.getExpression().accept(this);

		// Make sure the type is a numeric type
		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = new NumericResolver(parent, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {
		resolver = buildClassResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {
		visitCollectionEquivalentExpression(
			expression.getWhenClauses(),
			expression.getElseExpression()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {
		visitCollectionEquivalentExpression(expression.getExpression(), null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {
		expression.acceptChildren(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
		expression.getCollectionValuedPathExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {

		// If the path ends with '.', then the path is incomplete
		// so we can't resolve the type
		if (!expression.endsWithDot()) {

			// Check first to see if it's an enum type
			String path = expression.toActualText();
			IType type = queryContext.getTypeRepository().getEnumType(path);

			if (type != null) {
				resolver = buildEnumResolver(expression, type, path);
			}
			else {
				expression.getIdentificationVariable().accept(this);

				for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {
					path = expression.getPath(index);
					if (index + 1 < count) {
						resolver = buildStateFieldResolver(path);
					}
					else {
						resolver = buildCollectionValuedFieldResolver(path);
					}
				}
			}
		}
		else {
			resolver = buildNullResolver();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {
		resolver = buildClassResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {

		String className = expression.getClassName();

		if (ExpressionTools.stringIsNotEmpty(className)) {
			resolver = buildClassNameResolver(className);
		}
		else {
			resolver = buildNullResolver();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {
		resolver = buildClassResolver(Long.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {

		if (expression.isCurrentDate()) {
			resolver = buildClassResolver(Date.class);
		}
		else if (expression.isCurrentTime()) {
			resolver = buildClassResolver(Time.class);
		}
		else if (expression.isCurrentTimestamp()) {
			resolver = buildClassResolver(Timestamp.class);
		}
		else {
			String text = expression.getText();

			if (text.startsWith("{d")) {
				resolver = buildClassResolver(Date.class);
			}
			else if (text.startsWith("{ts")) {
				resolver = buildClassResolver(Timestamp.class);
			}
			else if (text.startsWith("{t")) {
				resolver = buildClassResolver(Time.class);
			}
			else {
				resolver = buildNullResolver();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {

		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
			DeclarationResolver parent = getDeclarationResolver(expression);
			resolver = new EntityResolver(parent, entityTypeName);
		}
		else {
			resolver = buildNullResolver();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {
		resolver = buildClassResolver(Map.Entry.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FunctionExpression expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariable expression) {
		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = parent.getResolver(expression.getVariableName());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclaration expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {
		resolver = buildClassResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {
		resolver = buildClassNameResolver(IType.UNRESOLVABLE_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {
		expression.getJoinAssociationPath().accept(this);
		resolver.setNullAllowed(expression.isLeftJoin());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new KeyResolver(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {

		String text = expression.getText();

		if (text == KeywordExpression.FALSE ||
		    text == KeywordExpression.TRUE) {

			resolver = buildClassResolver(Boolean.class);
		}
		else {
			resolver = buildClassResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {
		resolver = buildClassResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpression expression) {
		resolver = buildClassResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {
		resolver = buildClassResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = new NumericResolver(parent, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = new NumericResolver(parent, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {
		resolver = buildClassResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
		resolver = buildClassNameResolver(IType.UNRESOLVABLE_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {
		expression.getFirstExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {

		try {
			String text = expression.getText();

			// Long value
			// Integer value
			if (LONG_REGEXP   .matcher(text).matches() ||
			    INTEGER_REGEXP.matcher(text).matches()) {

				Long value = Long.parseLong(text);

				if (value <= Integer.MAX_VALUE) {
					resolver = buildClassResolver(Integer.class);
				}
				else {
					resolver = buildClassResolver(Long.class);
				}
			}
			// Float
			else if (FLOAT_REGEXP.matcher(text).matches()) {
				resolver = buildClassResolver(Float.class);
			}
			// Decimal
			else if (DOUBLE_REGEXP.matcher(text).matches()) {
				resolver = buildClassResolver(Double.class);
			}
		}
		catch (Exception e) {
			resolver = buildClassResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OnClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItem expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {
		resolver = buildClassResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclaration expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariable expression) {
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClause expression) {

		Expression selectExpression = expression.getSelectExpression();

		// visit(CollectionExpression) iterates through the children but for a
		// SELECT clause, a CollectionExpression means the result type is Object[]
		CollectionExpression collectionExpression = getCollectionExpression(selectExpression);

		if (collectionExpression != null) {
			resolver = buildClassResolver(Object[].class);
		}
		else {
			selectExpression.accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClause expression) {
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {
		queryContext.newSubqueryContext(expression);
		try {
			expression.getSelectClause().accept(this);
		}
		finally {
			queryContext.disposeSubqueryContext();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {
		resolver = buildClassResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {
		resolver = buildClassResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {

		// If the path ends with '.', then the path is incomplete
		// so we can't resolve the type
		if (!expression.endsWithDot()) {

			// Check first to see if it's an enum type
			String path = expression.toActualText();
			IType type = queryContext.getTypeRepository().getEnumType(path);

			if (type != null) {
				resolver = buildEnumResolver(expression, type, path);
			}
			else {
				expression.getIdentificationVariable().accept(this);

				for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {
					path = expression.getPath(index);
					resolver = buildStateFieldResolver(expression.getPath(index));
				}
			}
		}
		else {
			resolver = buildNullResolver();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {
		resolver = buildClassResolver(String.class);
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
		resolver = buildClassResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new SumFunctionResolver(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getCollectionValuedPathExpression().accept(this);

		// Retrieve the entity type name
		String entityTypeName = getQueryContext().literal(
			expression.getEntityType(),
			LiteralType.ENTITY_TYPE
		);

		// Wrap the Resolver for down casting
		resolver = new TreatResolver(resolver, entityTypeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {
		resolver = buildClassResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClause expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItem expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {
		resolver = buildClassResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new ValueResolver(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClause expression) {
		expression.getThenExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * Visits the given {@link ArithmeticExpression} and create the appropriate {@link Resolver}.
	 *
	 * @param expression The {@link ArithmeticExpression} to visit
	 */
	protected void visitArithmeticExpression(ArithmeticExpression expression) {

		List<Resolver> resolvers = new ArrayList<Resolver>(2);

		// Visit the first expression
		expression.getLeftExpression().accept(this);
		resolvers.add(resolver);

		// Visit the second expression
		expression.getRightExpression().accept(this);
		resolvers.add(resolver);

		// This will resolve the correct numeric type
		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = new NumericResolver(parent, resolvers);
	}

	/**
	 * Visits the given {@link Expression} and creates a {@link Resolver} that will check the type
	 * for each of its children. If the type is the same, then it's the {@link Expression}'s type;
	 * otherwise the type will be {@link Object}.
	 *
	 * @param expression The {@link Expression} to calculate the type of its children
	 * @param extraExpression This {@link Expression} will be resolved, if it's not <code>null</code>
	 * and its type will be added to the collection of types
	 */
	protected void visitCollectionEquivalentExpression(Expression expression,
	                                                   Expression extraExpression) {

		List<Resolver> resolvers = new ArrayList<Resolver>();
		CollectionExpression collectionExpression = getCollectionExpression(expression);

		// Gather the resolver for all children
		if (collectionExpression != null) {
			for (Expression child : collectionExpression.children()) {
				child.accept(this);
				resolvers.add(resolver);
			}
		}
		// Otherwise visit the actual expression
		else {
			expression.accept(this);
			resolvers.add(resolver);
		}

		// Add the resolver for the other expression
		if (extraExpression != null) {
			extraExpression.accept(this);
			resolvers.add(resolver);
		}

		DeclarationResolver parent = getDeclarationResolver(expression);
		resolver = new CollectionEquivalentResolver(parent, resolvers);
	}

	/**
	 * This visitor is used to check if the expression visited is a {@link CollectionExpression}.
	 */
	protected static final class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} that was visited, otherwise <code>null</code>.
		 */
		protected CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		public CollectionExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			this.expression = expression;
		}
	}
}