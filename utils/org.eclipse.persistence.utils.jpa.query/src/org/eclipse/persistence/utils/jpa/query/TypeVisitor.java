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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AllOrAnyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticExpression;
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
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
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
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * Calculates the type of an {@link Expression}.
 * <p>
 * The type of the query result specified by the <b>SELECT</b> clause of a query is an entity
 * abstract schema type, a state field type, the result of a scalar expression, the result of an
 * aggregate function, the result of a construction operation, or some sequence of these.
 * <p>
 * The result type of the <b>SELECT</b> clause is defined by the the result types of the select
 * expressions contained in it. When multiple select expressions are used in the SELECT clause, the
 * elements in this result correspond in order to the order of their specification in the
 * <b>SELECT</b> clause and in type to the result types of each of the select expressions.
 * <p>
 * <ul>
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
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class TypeVisitor implements ExpressionVisitor, TypeResolver {

	/**
	 * The external form representing the JPA query.
	 */
	private IQuery query;

	/**
	 * This resolver returns the type for the expression that was visited.
	 */
	private TypeResolver resolver;

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
	 * Creates a new <code>TypeVisitor</code>.
	 *
	 * @param query The external form representing the JPA query
	 */
	TypeVisitor(IQuery query) {
		super();
		initialize(query);
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the already determined type by using its
	 * fully qualified class name.
	 *
	 * @param typeName The fully qualified name of the class
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildClassNameTypeResolver(String typeName) {
		return new ClassNameTypeResolver(this, typeName);
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the already determined type.
	 *
	 * @param type The class type
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildClassTypeResolver(Class<?> type) {
		return new ClassTypeResolver(this, type);
	}

	/**
	 * Creates a {@link TypeResolver} that will be able to resolve any identification variable to
	 * an entity or collection-valued to a collection field member.
	 *
	 * @param expression The {@link Expression} used to traverse the declaration clause
	 * @return A {@link TypeResolver} that can resolve identification variables
	 */
	final TypeResolver buildDeclarationVisitor(Expression expression) {

		// Locate the declaration expression
		DeclarationExpressionLocator locator = new DeclarationExpressionLocator(true);
		expression.accept(locator);

		// Create the resolver/visitor that will be able to resolve the variables
		DeclarationTypeResolver declarationResolver = null;

		for (Expression declarationExpression : locator.reversedDeclarationExpresions()) {
			if (declarationResolver == null) {
				declarationResolver = new DeclarationTypeResolver(this);
			}
			else {
				declarationResolver = new DeclarationTypeResolver(declarationResolver);
			}
			declarationExpression.accept(declarationResolver);
		}

		return declarationResolver;
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the entity type name.
	 *
	 * @param entityTypeName The name of the entity type
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildEntityTypeResolver(String entityTypeName) {
		return new EntityTypeResolver(this, entityTypeName);
	}

	private IQuery checkQuery(IQuery query) {
		if (query == null) {
			throw new IllegalArgumentException("IQuery cannot be null");
		}

		return query;
	}

	private void checkResolver(TypeResolver resolver) {
		if (resolver == null) {
			throw new IllegalArgumentException("TypeResolver cannot be null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType() {
		return resolver.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping getMapping() {
		return resolver.getMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	public final IQuery getQuery() {
		return query;
	}

	/**
	 * Returns the {@link TypeResolver} for the expression that got visited.
	 *
	 * @return The resolver of the type of an expression
	 */
	final TypeResolver getResolver() {
		return resolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IType getType() {
		return resolver.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public final ITypeDeclaration getTypeDeclaration() {
		return resolver.getTypeDeclaration();
	}

	private void initialize(IQuery query) {
		this.query    = checkQuery(query);
		this.resolver = buildClassTypeResolver(Object.class);
	}

	private IManagedTypeProvider provider() {
		return query.getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	public final IManagedType resolveManagedType(IType type) {
		return provider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public final IManagedType resolveManagedType(String abstractSchemaName) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping resolveMapping(String variableName) {
		return resolver.resolveMapping(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public final IType resolveType(String variableName) {
		return resolver.resolveType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public final ITypeDeclaration resolveTypeDeclaration(String variableName) {
		return resolver.resolveTypeDeclaration(variableName);
	}

	/**
	 * Sets the resolver to be the following one.
	 *
	 * @param resolver The resolver used to determine the type; which cannot be
	 * <code>null</code>
	 */
	final void setResolver(TypeResolver resolver) {
		checkResolver(resolver);
		this.resolver = resolver;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AbsExpression expression) {

		// Visit the child expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new AbsFunctionResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AbstractSchemaName expression) {

		String entityName = expression.toParsedText();

		if (ExpressionTools.stringIsNotEmpty(entityName)) {
			resolver = buildClassNameTypeResolver(entityName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AdditionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AllOrAnyExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AndExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ArithmeticFactor expression) {

		// First traverse the expression
		expression.getExpression().accept(this);

		// Make sure the type is a numeric type
		resolver = new NumericTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final AvgFunction expression) {
		resolver = buildClassTypeResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final BadExpression expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final BetweenExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CaseExpression expression) {
		visitCollectionEquivalentExpression(
			expression.getWhenClauses(),
			expression.getElseExpression()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CoalesceExpression expression) {
		visitCollectionEquivalentExpression(expression.getExpression(), null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CollectionExpression expression) {
		expression.acceptChildren(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CollectionMemberDeclaration expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CollectionMemberExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CollectionValuedPathExpression expression) {

		// Resolve the FROM clause so abstract schema names are resolvable
		TypeResolver visitor = buildDeclarationVisitor(expression);

		// Visit the collection-valued path expression so the resolver tree can be created
		PathDeclarationVisitor pathVisitor = new PathDeclarationVisitor(visitor);
		expression.accept(pathVisitor);

		resolver = pathVisitor.resolver();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ComparisonExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ConcatExpression expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ConstructorExpression expression) {

		String className = expression.getClassName();

		if (ExpressionTools.stringIsNotEmpty(className)) {
			resolver = buildClassNameTypeResolver(className);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final CountFunction expression) {
		resolver = buildClassTypeResolver(Long.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final DateTime expression) {

		if (expression.isCurrentDate()) {
			resolver = buildClassTypeResolver(Date.class);
		}
		else if (expression.isCurrentTime()) {
			resolver = buildClassTypeResolver(Time.class);
		}
		else if (expression.isCurrentTimestamp()) {
			resolver = buildClassTypeResolver(Timestamp.class);
		}
		else {
			String text = expression.getText();

			if (text.startsWith("{d")) {
				resolver = buildClassTypeResolver(Date.class);
			}
			else if (text.startsWith("{ts")) {
				resolver = buildClassTypeResolver(Timestamp.class);
			}
			else if (text.startsWith("{t")) {
				resolver = buildClassTypeResolver(Time.class);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final DeleteClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final DeleteStatement expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final DivisionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final EmptyCollectionComparisonExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final EntityTypeLiteral expression) {

		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
			resolver = buildEntityTypeResolver(entityTypeName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final EntryExpression expression) {
		resolver = buildClassTypeResolver(Map.Entry.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ExistsExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final FromClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final FuncExpression expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final GroupByClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final HavingClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final IdentificationVariable expression) {

		// Build the visitor/resolver that will be able to find the type of the identification
		// variable, which is found in the declaration expression
		TypeResolver visitor = buildDeclarationVisitor(expression);

		// The declaration clause is not defined or is malformed
		if (visitor == null) {
			resolver = new NullTypeResolver(this);
		}
		// Now create the resolver of the identification variable
		else {
			resolver = new IdentificationVariableResolver(visitor, expression.getText());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final IdentificationVariableDeclaration expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final IndexExpression expression) {
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final InExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final InputParameter expression) {
		resolver = buildClassNameTypeResolver(IType.UNRESOLVABLE_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final Join expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final JoinFetch expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final JPQLExpression expression) {
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final KeyExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new KeyTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final KeywordExpression expression) {

		String text = expression.toParsedText();

		if (KeywordExpression.FALSE.equalsIgnoreCase(text) ||
		    KeywordExpression.TRUE .equalsIgnoreCase(text))
		{
			resolver = buildClassTypeResolver(Boolean.class);
		}
		else {
			resolver = buildClassTypeResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final LengthExpression expression) {
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final LikeExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final LocateExpression expression) {
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final LowerExpression expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final MaxFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new NumericTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final MinFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new NumericTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ModExpression expression) {
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final MultiplicationExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final NotExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final NullComparisonExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final NullExpression expression) {
		resolver = buildClassNameTypeResolver(IType.UNRESOLVABLE_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final NullIfExpression expression) {
		expression.getFirstExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final NumericLiteral expression) {
		try {
			String text = expression.getText();

			// Long value
			// Integer value
			if (LONG_REGEXP   .matcher(text).matches() ||
			    INTEGER_REGEXP.matcher(text).matches()) {

				Long value = Long.parseLong(text);

				if (value <= Integer.MAX_VALUE) {
					resolver = buildClassTypeResolver(Integer.class);
				}
				else {
					resolver = buildClassTypeResolver(Long.class);
				}
			}
			// Float
			else if (FLOAT_REGEXP.matcher(text).matches()) {
				resolver = buildClassTypeResolver(Float.class);
			}
			// Decimal
			else if (DOUBLE_REGEXP.matcher(text).matches()) {

				resolver = buildClassTypeResolver(Double.class);
//				// 1000D is parsed as a Long
//				if (text.endsWith("d") || text.endsWith("D")) {
//					resolver = buildClassTypeResolver(Double.class);
//				}
//				else {
//					Number number = DecimalFormat.getInstance().parse(text);
//
//					if (number instanceof Double) {
//						if (number.doubleValue() <= Float.MAX_VALUE) {
//							resolver = buildClassTypeResolver(Float.class);
//						}
//						else {
//							resolver = buildClassTypeResolver(Double.class);
//						}
//					}
//					else {
//						resolver = buildClassTypeResolver(number.getClass());
//					}
//				}
			}
		}
		catch (Exception e) {
			resolver = buildClassTypeResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ObjectExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final OrderByClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final OrderByItem expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final OrExpression expression) {
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final RangeVariableDeclaration expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ResultVariable expression) {
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SelectClause expression) {

		// visit(CollectionExpression) iterates through the children but for a
		// SELECT clause, a CollectionExpression means the result type is Object[]
		CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
		expression.getSelectExpression().accept(visitor);

		if (visitor.expression != null) {
			resolver = buildClassTypeResolver(Object[].class);
		}
		else {
			expression.getSelectExpression().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SelectStatement expression) {
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SimpleFromClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SimpleSelectClause expression) {
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SimpleSelectStatement expression) {
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SizeExpression expression) {
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SqrtExpression expression) {
		resolver = buildClassTypeResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final StateFieldPathExpression expression) {

		// If the path ends with '.', then the path is incomplete
		// so we can't resolve the type
		if (!expression.endsWithDot()) {

			// The first path is always a general identification variable
			expression.getIdentificationVariable().accept(this);

			// The rest of the path is always a property
			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {

				if (index + 1 < count) {
					resolver = new SingleValuedObjectFieldTypeResolver(resolver, expression.getPath(index));
				}
				else {
					resolver = new StateFieldTypeResolver(resolver, expression.getPath(index));
				}
			}

			// Wrap the TypeResolver so it can check for an enum type
			resolver = new EnumTypeResolver(resolver, expression.toParsedText());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final StringLiteral expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SubExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SubtractionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SubstringExpression expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final SumFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new SumFunctionResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final TrimExpression expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final TypeExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final UnknownExpression expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final UpdateClause expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final UpdateItem expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final UpdateStatement expression) {
		resolver = buildClassTypeResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final UpperExpression expression) {
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final ValueExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new ValueTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final WhenClause expression) {
		expression.getThenExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(final WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	private void visitArithmeticExpression(ArithmeticExpression expression) {

		List<TypeResolver> resolvers = new ArrayList<TypeResolver>(2);

		// Visit the first expression
		expression.getLeftExpression().accept(this);
		resolvers.add(resolver);

		// Visit the second expression
		expression.getRightExpression().accept(this);
		resolvers.add(resolver);

		// This will resolve the correct numeric type
		resolver = new NumericTypeResolver(this, resolvers);
	}

	private void visitCollectionEquivalentExpression(Expression expression,
	                                                 Expression extraExpression) {

		List<TypeResolver> resolvers = new ArrayList<TypeResolver>();

		CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
		expression.accept(visitor);

		// Gather the resolver for all children
		if (visitor.expression != null) {
			for (Expression child : visitor.expression.getChildren()) {
				// Ignore InputParameter since its type is always Object
				InputParameterVisitor inputParameterVisitor = new InputParameterVisitor();
				child.accept(inputParameterVisitor);

				if (inputParameterVisitor.expression == null) {
					child.accept(this);
					resolvers.add(resolver);
				}
			}
		}
		// Otherwise visit the actual expression
		else {
			expression.accept(this);
			resolvers.add(resolver);
		}

		// Add the resolver for the other expression
		if (extraExpression != null) {
			// Ignore InputParameter since its type is always Object
			InputParameterVisitor inputParameterVisitor = new InputParameterVisitor();
			extraExpression.accept(inputParameterVisitor);

			if (inputParameterVisitor.expression == null) {
				extraExpression.accept(this);
				resolvers.add(resolver);
			}
		}

		resolver = new CollectionEquivalentTypeResolver(this, resolvers);
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
		public void visit(final CollectionExpression expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor is used to check if the expression visited is a {@link InputParameter}.
	 */
	private static final class InputParameterVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link InputParameter} that was visited, otherwise <code>null</code>.
		 */
		InputParameter expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(final InputParameter expression) {
			this.expression = expression;
		}
	}
}