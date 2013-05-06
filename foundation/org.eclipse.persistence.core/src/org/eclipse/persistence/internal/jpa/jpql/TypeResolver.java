/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
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
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
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
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * This visitor resolves the type of any given {@link Expression}.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class TypeResolver implements EclipseLinkExpressionVisitor {

	/**
	 * This visitor is responsible to retrieve the {@link CollectionExpression} if it is visited.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 * This {@link Comparator} compares numeric types and sorts them based on precedence.
	 */
	private Comparator<Class<?>> numericTypeComparator;

	/**
	 * This visitor resolves a path expression by retrieving the mapping and descriptor of the last segment.
	 */
	private PathResolver pathResolver;

	/**
	 * The context used to query information about the application metadata and cached information.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * The well defined type, which does not have to be calculated.
	 */
	private Class<?> type;

	/**
	 * A constant representing an unresolvable type.
	 */
	private static final Class<?> UNRESOLVABLE_TYPE = TypeResolver.class;

	/**
	 * Creates a new <code>TypeResolver</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	TypeResolver(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	/**
	 * Returns the type of the given {@link DatabaseMapping}, which is the persistent field type.
	 *
	 * @param mapping The {@link DatabaseMapping} to retrieve its persistent field type
	 * @return The persistent field type
	 */
	@SuppressWarnings("null")
	Class<?> calculateMappingType(DatabaseMapping mapping) {

		// For aggregate mappings (@Embedded and @EmbeddedId), we need to use the descriptor
		// because its mappings have to be retrieve from this one and not from the descriptor
		// returned when querying it with a Java type
		if (mapping.isAggregateMapping()) {
			ClassDescriptor descriptor = ((AggregateMapping) mapping).getReferenceDescriptor();
			if (descriptor != null) {
				return descriptor.getJavaClass();
			}
		}

		// Relationship mapping
		if (mapping.isForeignReferenceMapping()) {
			ClassDescriptor descriptor = mapping.getReferenceDescriptor();
			if (descriptor != null) {
				return descriptor.getJavaClass();
			}
		}
		// Collection mapping
		else if (mapping.isCollectionMapping()) {
			return mapping.getContainerPolicy().getContainerClass();
		}

		// Property mapping
		AttributeAccessor accessor = mapping.getAttributeAccessor();

		// Attribute
		if (accessor.isInstanceVariableAttributeAccessor()) {
			InstanceVariableAttributeAccessor attributeAccessor = (InstanceVariableAttributeAccessor) accessor;
			Field field = attributeAccessor.getAttributeField();

			if (field == null) {
				try {
					field = mapping.getDescriptor().getJavaClass().getDeclaredField(attributeAccessor.getAttributeName());
				}
				catch (Exception e) {}
			}

			return field.getType();
		}

		// Property
		if (accessor.isMethodAttributeAccessor()) {
			MethodAttributeAccessor methodAccessor = (MethodAttributeAccessor) accessor;
			Method method = methodAccessor.getGetMethod();

			if (method == null) {
				try {
					method = mapping.getDescriptor().getJavaClass().getDeclaredMethod(methodAccessor.getGetMethodName());
				}
				catch (Exception e) {}
			}

			return method.getReturnType();
		}

		// Anything else
		return accessor.getAttributeClass();
	}

	/**
	 * Returns the type of the given {@link QueryKey}, which is the persistent field type.
	 *
	 * @param queryKey The {@link QueryKey} to retrieve its persistent field type
	 * @return The persistent field type
	 */
	Class<?> calculateQueryKeyType(QueryKey queryKey) {

		// ForeignReferenceQueryKey
		if (queryKey.isForeignReferenceQueryKey()) {
			ForeignReferenceQueryKey foreignReferenceQueryKey = (ForeignReferenceQueryKey) queryKey;
			return foreignReferenceQueryKey.getReferenceClass();
		}

		// DirectQueryKey
		DirectQueryKey key = (DirectQueryKey) queryKey;
		Class<?> type = key.getField().getType();
		return (type != null) ? type : Object.class;
	}

	/**
	 * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return The {@link CollectionExpressionVisitor}
	 */
	protected CollectionExpressionVisitor collectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = new CollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	Class<?> compareCollectionEquivalentTypes(List<Class<?>> types) {

		Class<?> localType = null;

		for (Class<?> anotherType : types) {

			if (anotherType == UNRESOLVABLE_TYPE) {
				continue;
			}

			if (localType == null) {
				localType = anotherType;
			}
			// Two types are not the same, then the type is Object
			else if (localType != anotherType) {
				return Object.class;
			}
		}

		if (localType == null) {
			localType = UNRESOLVABLE_TYPE;
		}

		return localType;
	}

	Class<?> convertSumFunctionType(Class<?> type) {

		// Integral types
		if ((type == Integer.TYPE)   ||
		    (type == Integer.class)  ||
		    (type == Long.TYPE)      ||
		    (type == Long.class)     ||
		    (type == Byte.TYPE)      ||
		    (type == Byte.class)     ||
		    (type == Short.TYPE)     ||
		    (type == Short.class)    ||
		    (type == Character.TYPE) ||
		    (type == Character.class)) {

			type = Long.class;
		}

		// Floating types
		else if ((type == Float.TYPE)  ||
		         (type == Float.class) ||
		         (type == Double.TYPE) ||
		         (type == Double.class)) {

			type = Double.class;
		}

		// Anything else, use Object
		else if ((type != BigDecimal.class) &&
		         (type != BigInteger.class)) {

			type = Object.class;
		}

		return type;
	}

	/**
	 * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
	 * object of that type.
	 *
	 * @param expression The {@link Expression} to cast
	 * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
	 * if it is any other object
	 */
	private CollectionExpression getCollectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = collectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private boolean isNumericType() {
		return type == Integer.TYPE     || type == Integer.class ||
		       type == Long.TYPE        || type == Long.class    ||
		       type == Float.TYPE       || type == Float.class   ||
		       type == Double.TYPE      || type == Double.class  ||
		       type == BigInteger.class || type == BigDecimal.class;
	}

	private PathResolver pathResolver() {
		if (pathResolver == null) {
			pathResolver = new PathResolver();
		}
		return pathResolver;
	}

	/**
	 * Returns the type of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to resolve its type
	 * @return Either the closest type or {@link Object} if it could not be determined
	 */
	Class<?> resolve(Expression expression) {
		Class<?> oldType = type;
		try {
			expression.accept(this);
			return (type == UNRESOLVABLE_TYPE) ? Object.class : type;
		}
		finally {
			type = oldType;
		}
	}

	ClassDescriptor resolveDescriptor(Expression expression) {

		PathResolver resolver = pathResolver();

		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;

			expression.accept(resolver);

			return resolver.descriptor;
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.descriptor = oldDescriptor;
		}
	}

	DatabaseMapping resolveMapping(Expression expression) {

		PathResolver resolver = pathResolver();

		QueryKey oldQueryKey          = resolver.queryKey;
		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;
			resolver.queryKey   = null;

			expression.accept(resolver);

			return resolver.mapping;
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.queryKey   = oldQueryKey;
			resolver.descriptor = oldDescriptor;
		}
	}

	/**
	 * Resolves the given {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} and
	 * either returns the {@link DatabaseMapping} or the {@link QueryKey} object.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * resolve by traversing its path expression
	 * @return Either the {@link DatabaseMapping} or the {@link QueryKey} that is representing the
	 * last path or <code>null</code> if the path expression could not be resolved
	 */
	Object resolveMappingObject(Expression expression) {

		PathResolver resolver = pathResolver();

		QueryKey oldQueryKey          = resolver.queryKey;
		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;
			resolver.queryKey   = null;

			expression.accept(resolver);
			return (resolver.mapping != null) ? resolver.mapping : resolver.queryKey;
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.queryKey   = oldQueryKey;
			resolver.descriptor = oldDescriptor;
		}
	}

	private Class<?> resolveMappingType(AbstractPathExpression expression) {

		PathResolver resolver = pathResolver();

		QueryKey oldQueryKey          = resolver.queryKey;
		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;
			resolver.queryKey   = null;

			expression.accept(resolver);

			if (resolver.mapping != null) {
				return calculateMappingType(resolver.mapping);
			}
			else if (resolver.queryKey != null) {
				return calculateQueryKeyType(resolver.queryKey);
			}
			else {
				return queryContext.getEnumType(expression.toParsedText());
			}
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.queryKey   = oldQueryKey;
			resolver.descriptor = oldDescriptor;
		}
	}

	QueryKey resolveQueryKey(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {

		PathResolver resolver = pathResolver();

		QueryKey oldQueryKey          = resolver.queryKey;
		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;
			resolver.queryKey   = null;

			expression.accept(resolver);
			return resolver.queryKey;
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.queryKey   = oldQueryKey;
			resolver.descriptor = oldDescriptor;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {
		// Visit the child expression in order to create the resolver
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		ClassDescriptor descriptor = queryContext.getDescriptor(expression.getText());
		type = descriptor.getJavaClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {

		// First traverse the expression
		expression.getExpression().accept(this);

		// Make sure the type is a numeric type
		if (!isNumericType()) {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AsOfClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		type = Double.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {
		visitCollectionEquivalentExpression(
			expression.getWhenClauses(),
			expression.getElseExpression()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CastExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {
		visitCollectionEquivalentExpression(expression.getExpression(), null);
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
		expression.getCollectionValuedPathExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		type = resolveMappingType(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {
		type = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConnectByClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {
		type = queryContext.getType(expression.getClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {
		type = Long.class;
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

		if (expression.isCurrentDate()) {
			type = Date.class;
		}
		else if (expression.isCurrentTime()) {
			type = Time.class;
		}
		else if (expression.isCurrentTimestamp()) {
			type = Timestamp.class;
		}
		else {
			String text = expression.getText();

			if (text.startsWith("{d")) {
				type = Date.class;
			}
			else if (text.startsWith("{ts")) {
				type = Timestamp.class;
			}
			else if (text.startsWith("{t")) {
				type = Time.class;
			}
			else {
				type = Object.class;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {
		String entityTypeName = expression.getEntityTypeName();
		ClassDescriptor descriptor = queryContext.getDescriptor(entityTypeName);
		type = descriptor.getJavaClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {
		type = Map.Entry.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExtractExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FunctionExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HierarchicalQueryClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		PathResolver resolver = pathResolver();

		DatabaseMapping oldMapping    = resolver.mapping;
		ClassDescriptor oldDescriptor = resolver.descriptor;

		try {
			resolver.mapping    = null;
			resolver.descriptor = null;

			expression.accept(resolver);

			if (resolver.mapping != null) {
				type = calculateMappingType(resolver.mapping);
			}
			else if (resolver.descriptor != null) {
				type = resolver.descriptor.getJavaClass();
			}
			else {
				type = Object.class;
			}
		}
		finally {
			resolver.mapping    = oldMapping;
			resolver.descriptor = oldDescriptor;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {
		type = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {
		type = UNRESOLVABLE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		expression.getJoinAssociationPath().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {
		IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
		Declaration declaration = queryContext.findDeclaration(identificationVariable.getVariableName());
		DatabaseMapping mapping = declaration.getMapping();
		MappedKeyMapContainerPolicy mapContainerPolicy = (MappedKeyMapContainerPolicy) mapping.getContainerPolicy();
		type = (Class<?>) mapContainerPolicy.getKeyType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {

		String text = expression.getText();

		if (text == KeywordExpression.FALSE ||
		    text == KeywordExpression.TRUE) {

			type = Boolean.class;
		}
		else {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {
		type = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {
		type = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		type = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		if (!isNumericType()) {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		if (!isNumericType()) {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {
		type = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression) {
		type = UNRESOLVABLE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {
		expression.getFirstExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {

		try {
			String text = expression.getText();

			// Long value
			// Integer value
			if (ExpressionTools.LONG_REGEXP   .matcher(text).matches() ||
			    ExpressionTools.INTEGER_REGEXP.matcher(text).matches()) {

				Long value = Long.parseLong(text);

				if (value <= Integer.MAX_VALUE) {
					type = Integer.class;
				}
				else {
					type = Long.class;
				}
			}
			// Float
			else if (ExpressionTools.FLOAT_REGEXP.matcher(text).matches()) {
				type = Float.class;
			}
			// Decimal
			else if (ExpressionTools.DOUBLE_REGEXP.matcher(text).matches()) {
				type = Double.class;
			}
		}
		catch (Exception e) {
			type = Object.class;
		}
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
	public void visit(OnClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderSiblingsByClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression) {
		type = Boolean.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RegexpExpression expression) {
		type = Boolean.class;
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

		Expression selectExpression = expression.getSelectExpression();

		// visit(CollectionExpression) iterates through the children but for a
		// SELECT clause, a CollectionExpression means the result type is Object[]
		CollectionExpression collectionExpression = getCollectionExpression(selectExpression);

		if (collectionExpression != null) {
			type = Object[].class;
		}
		else {
			selectExpression.accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		type = Object.class;
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
	public void visit(SimpleSelectStatement expression) {
		queryContext.newSubQueryContext(expression, null);
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
	@Override
	public void visit(SizeExpression expression) {
		type = Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {
		type = Double.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StartWithClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		type = resolveMappingType(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		type = String.class;
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
		type = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {

		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the Resolver used to determine the type of the state field
		// path expression so we can return the actual type
		type = convertSumFunctionType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TableExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TableVariableDeclaration expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {
		expression.getEntityType().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {
		type = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnionClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {
		type = String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {

		IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
		Declaration declaration = queryContext.findDeclaration(identificationVariable.getVariableName());
		DatabaseMapping mapping = declaration.getMapping();

		if (mapping.isDirectMapMapping()) {

			DirectMapMapping mapMapping = (DirectMapMapping) mapping;
			type = mapMapping.getValueClass();

			if (type == null) {
				type = mapMapping.getDirectField().getType();
			}
		}
		else {
			type = calculateMappingType(declaration.getMapping());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {
		expression.getThenExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		expression.getConditionalExpression().accept(this);
	}

	/**
	 * Visits the given {@link ArithmeticExpression} and creates the appropriate {@link org.eclipse.
	 * persistence.expressions.Expression Expression}.
	 *
	 * @param expression The {@link ArithmeticExpression} to visit
	 */
	private void visitArithmeticExpression(ArithmeticExpression expression) {

		List<Class<?>> types = new ArrayList<Class<?>>(2);

		// Visit the first expression
		expression.getLeftExpression().accept(this);
		if (isNumericType()) {
			types.add(type);
		}

		// Visit the second expression
		expression.getRightExpression().accept(this);
		if (isNumericType()) {
			types.add(type);
		}

		if (types.size() == 2) {
			Collections.sort(types, NumericTypeComparator.instance());
			type = types.get(0);
		}
		else {
			type = Object.class;
		}
	}

	/**
	 * Visits the given {@link Expression} and creates the appropriate {@link org.eclipse.persistence.
	 * expressions.Expression Expression} that will check the type for each of its children. If the
	 * type is the same, then it's the {@link Expression}'s type; otherwise the type will be {@link Object}.
	 *
	 * @param expression The {@link Expression} to calculate the type of its children
	 * @param extraExpression This {@link Expression} will be resolved, if it's not <code>null</code>
	 * and its type will be added to the collection of types
	 */
	private void visitCollectionEquivalentExpression(Expression expression,
	                                                 Expression extraExpression) {

		List<Class<?>> types = new ArrayList<Class<?>>();
		CollectionExpression collectionExpression = getCollectionExpression(expression);

		// Gather the resolver for all children
		if (collectionExpression != null) {
			for (Expression child : collectionExpression.children()) {
				child.accept(this);
				types.add(type);
			}
		}
		// Otherwise visit the actual expression
		else {
			expression.accept(this);
			types.add(type);
		}

		// Add the resolver for the other expression
		if (extraExpression != null) {
			extraExpression.accept(this);
			types.add(type);
		}

		// Now compare the types
		type = compareCollectionEquivalentTypes(types);
	}

	/**
	 * This visitor is used to check if the expression visited is a {@link CollectionExpression}.
	 */
	private static class CollectionExpressionVisitor extends AbstractExpressionVisitor {

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

	private class PathResolver extends AbstractEclipseLinkExpressionVisitor {

		ClassDescriptor descriptor;
		DatabaseMapping mapping;
		QueryKey queryKey;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			descriptor = queryContext.getDescriptor(expression.getText());
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			visitPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntityTypeLiteral expression) {
			descriptor = queryContext.getDescriptor(expression.getEntityTypeName());
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			// Check to see if the identification variable is "virtual" and internally
			// changed to a state field path expression. If so, it means it's an unqualified
			// path found in an UPDATE or DELETE query
			StateFieldPathExpression pathExpression = expression.isVirtual() ? expression.getStateFieldPathExpression() : null;

			if (pathExpression != null) {
				pathExpression.accept(this);
			}
			else {
				Declaration declaration = queryContext.findDeclaration(expression.getVariableName());

				// A null declaration Expression would mean it's the first package of an enum type
				if (declaration != null) {
					descriptor = declaration.getDescriptor();
				}
			}
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(Join expression) {
			expression.getJoinAssociationPath().accept(this);
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(KeyExpression expression) {

			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
			Declaration declaration = queryContext.getDeclaration(identificationVariable.getVariableName());
			DatabaseMapping mapping = declaration.getMapping();

			ContainerPolicy containerPolicy = mapping.getContainerPolicy();
			MappedKeyMapContainerPolicy mapPolicy = (MappedKeyMapContainerPolicy) containerPolicy;
			descriptor = mapPolicy.getKeyMapping().getReferenceDescriptor();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			expression.getIdentificationVariable().accept(this);
		}

		@Override
		public void visit(StateFieldPathExpression expression) {
			visitPathExpression(expression);
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(TreatExpression expression) {
			expression.getEntityType().accept(this);
		}

		/**
		 * {@link InputParameter}
		 */
		@Override
		public void visit(ValueExpression expression) {
			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
			Declaration declaration = queryContext.getDeclaration(identificationVariable.getVariableName());
			descriptor = declaration.getDescriptor();
		}

		private void visitPathExpression(AbstractPathExpression expression) {

			expression.getIdentificationVariable().accept(this);

			if (descriptor == null) {
				return;
			}

			// Now traverse the rest of the path
			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {

				String path = expression.getPath(index);
				mapping = descriptor.getObjectBuilder().getMappingForAttributeName(path);

				if (mapping == null) {
					queryKey = descriptor.getQueryKeyNamed(path);

					if ((queryKey != null) && queryKey.isForeignReferenceQueryKey()) {
						ForeignReferenceQueryKey referenceQueryKey = (ForeignReferenceQueryKey) queryKey;
						descriptor = queryContext.getDescriptor(referenceQueryKey.getReferenceClass());
					}
					else {
						if (index + 1 < count) {
							mapping = null;
						}
						descriptor = null;
					}
				}
				// A collection mapping cannot be used in a path (if it's not the last path)
				else if (mapping.isCollectionMapping() && (index + 1 < count)) {
					mapping    = null;
					descriptor = null;
				}
				else {
					descriptor = mapping.getReferenceDescriptor();
				}

				if (descriptor == null) {
					if (index + 1 < count) {
						mapping = null;
					}
					break;
				}
			}
		}
	}
}