/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ArithmeticExpression;
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
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
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
 * {@link java.util.Map#Entry}, where the key and value types of the map entry are determined by the
 * above rules as applied to the map key and map value respectively.</li>
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
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ResolverBuilder implements ExpressionVisitor {

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
	Resolver resolver;

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a double.
	 */
	private static Pattern DOUBLE_REGEXP = Pattern.compile("^[-+]?[0-9]*(\\.[0-9]+)?([dD]|([eE][-+]?[0-9]+))?$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a float.
	 */
	private static Pattern FLOAT_REGEXP = Pattern.compile("^[-+]?[0-9]*(\\.[0-9]+)?[fF]$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as an integer.
	 */
	private static Pattern INTEGER_REGEXP = Pattern.compile("^[-+]?[0-9]+$");

	/**
	 * The {@link Pattern} representing the regular expression of a numerical value as a long.
	 */
	private static Pattern LONG_REGEXP = Pattern.compile("^[-+]?[0-9]+[lL]?$");

	/**
	 * Creates a new <code>ResolverBuilder</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 */
	ResolverBuilder(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	/**
	 * Creates a new {link Resolver} that simply wraps the already determined type by using its
	 * fully qualified class name.
	 *
	 * @param type The fully qualified class name of the Java type to wrap with a {@link Resolver}
	 * @return A new {@link Resolver}
	 */
	private Resolver buildClassNameResolver(String typeName) {
		return new ClassNameResolver(getDeclarationResolver(), typeName);
	}

	/**
	 * Creates a new {link Resolver} that simply wraps the already determined type.
	 *
	 * @param type The Java type to wrap with a {@link Resolver}
	 * @return A new {@link Resolver}
	 */
	private Resolver buildClassResolver(Class<?> type) {
		return new ClassResolver(getDeclarationResolver(), type);
	}

	private Resolver buildCollectionValuedFieldResolver(String variableName,
	                                                    String collectionValuedField) {

		Resolver resolver = this.resolver.getChild(variableName);

		if (resolver == null) {
			resolver = new CollectionValuedFieldResolver(this.resolver, variableName, collectionValuedField);
		}

		return resolver;
	}

	private Resolver buildNullResolver() {
		return new NullResolver(getDeclarationResolver());
	}

	private Resolver buildSingleValuedObjectFieldResolver(String variableName) {

		Resolver resolver = this.resolver.getChild(variableName);

		if (resolver == null) {
			resolver = new SingleValuedObjectFieldResolver(this.resolver, variableName);
		}

		return resolver;
	}

	private Resolver buildStateFieldResolver(String variableName, String stateFieldPath) {

		Resolver resolver = this.resolver.getChild(variableName);

		if (resolver == null) {
			resolver = new StateFieldResolver(this.resolver, variableName, stateFieldPath);
		}

		return resolver;
	}

	private CollectionExpression collectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = collectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private CollectionExpressionVisitor collectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = new CollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	private DeclarationResolver getDeclarationResolver() {
		return queryContext.getDeclarationResolver();
	}

	private DeclarationResolver getDeclarationResolver(Expression expression) {
		return queryContext.getDeclarationResolver(expression);
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

			expression.getIdentificationVariable().accept(this);

			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {
				if (index + 1 < count) {
					resolver = buildSingleValuedObjectFieldResolver(expression.getPath(index));
				}
				else {
					resolver = buildCollectionValuedFieldResolver(expression.getPath(index), expression.toParsedText());
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
	public void visit(FuncExpression expression) {
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
		resolver = parent.getResolver(expression.getText());
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
	public void visit(JoinFetch expression) {
		expression.getJoinAssociationPath().accept(this);
		resolver.setNullAllowed(expression.isLeftJoinFetch());
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
//				// 1000D is parsed as a Long
//				if (text.endsWith("d") || text.endsWith("D")) {
//					resolver = buildClassResolver(Double.class);
//				}
//				else {
//					Number number = DecimalFormat.getInstance().parse(text);
//
//					if (number instanceof Double) {
//						if (number.doubleValue() <= Float.MAX_VALUE) {
//							resolver = buildClassResolver(Float.class);
//						}
//						else {
//							resolver = buildClassResolver(Double.class);
//						}
//					}
//					else {
//						resolver = buildClassResolver(number.getClass());
//					}
//				}
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
		CollectionExpression collectionExpression = collectionExpression(selectExpression);

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

			expression.getIdentificationVariable().accept(this);

			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {
				if (index + 1 < count) {
					resolver = buildSingleValuedObjectFieldResolver(expression.getPath(index));
				}
				else {
					resolver = buildStateFieldResolver(expression.getPath(index), expression.toParsedText());
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
		String entityTypeName = queryContext.literal(
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

	private void visitArithmeticExpression(ArithmeticExpression expression) {

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

	private void visitCollectionEquivalentExpression(Expression expression,
	                                                 Expression extraExpression) {

		List<Resolver> resolvers = new ArrayList<Resolver>();
		CollectionExpression collectionExpression = collectionExpression(expression);

		// Gather the resolver for all children
		if (collectionExpression != null) {
			for (Expression child : collectionExpression.getChildren()) {
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
	private static final class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} that was visited, otherwise <code>null</code>.
		 */
		CollectionExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			this.expression = expression;
		}
	}
}