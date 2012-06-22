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
package org.eclipse.persistence.internal.jpa.jpql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationResolver;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.internal.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.internal.jpql.Resolver;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This extension over the default {@link JPQLQueryContext} adds the necessary functionality to
 * properly convert the JPQL query into a {@link DatabaseQuery}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class DefaultJPQLQueryContext extends JPQLQueryContext {

	/**
	 * The base {@link Expression} is the {@link Expression} for the current query, which is created
	 * when {@link #getBaseExpression()} is called on the current context for the first time.
	 */
	private Expression baseExpression;

	/**
	 * The abstract schema names mapped to their {@link Expression}, which are cached because a
	 * single one should exist for every abstract schema name on any given query (top-level and
	 * subqueries).
	 */
	private Map<String, Expression> entityExpressions;

	/**
	 * The builder that can convert a {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL
	 * Expression} into an {@link Expression EclipseLink Expression}.
	 */
	private ExpressionBuilderVisitor expressionBuilder;

	/**
	 * The cached {@link Expression Expressions} mapped by a variable name.
	 */
	private Map<String, Expression> expressions;

	/**
	 * The input parameter name mapped to its type. The input parameter name starts with the
	 * positional parameter ('?' or ':').
	 */
	private Map<String, Class<?>> inputParameters;

	/**
	 * This visitor is responsible to create and populate a {@link ObjectLevelReadQuery}.
	 */
	private ObjectLevelReadQueryVisitor objectLevelReadQueryVisitor;

	/**
	 * The {@link DatabaseQuery} being populated by visiting the parsed tree representation of the
	 * JPQL query.
	 */
	private DatabaseQuery query;

	/**
	 * This visitor is responsible to create the right read query based on the select expression.
	 */
	private ReadQueryBuilder readQueryBuilder;

	/**
	 * This visitor is responsible to create and populate a {@link ReportQuery}.
	 */
	private ReportQueryVisitor reportQueryVisitor;

	/**
	 * The identification variables that are found in the query and used in any clauses (except
	 * defined in the <b>FROM</b>, <b>DELETE</b> or <b>UPDATE</b> clauses).
	 */
	private Set<String> usedIdentificationVariables;

	/**
	 * Creates a new <code>DefaultJPQLQueryContext</code>.
	 */
	DefaultJPQLQueryContext() {
		super();
		inputParameters = new LinkedHashMap<String, Class<?>>();
	}

	/**
	 * Creates a new <code>DefaultJPQLQueryContext</code>.
	 *
	 * @param parent The parent of this context, which is never <code>null</code>
	 * @param currentQuery The parsed tree representation of the subquery
	 * @param query The {@link ReportQuery} that will be populated by visiting the parsed tree
	 * representation of the subquery
	 */
	private DefaultJPQLQueryContext(DefaultJPQLQueryContext parent,
	                                org.eclipse.persistence.jpa.internal.jpql.parser.Expression currentQuery,
	                                ReportQuery query) {

		super(parent, currentQuery);
		store(parent, query);
	}

	/**
	 * Caches the given input parameter name with its calculated type.
	 *
	 * @param parameterName The name of the input parameter
	 * @param type The calculated type based on its surrounding, which is never <code>null</code>
	 */
	void addInputParameter(String parameterName, Class<?> type) {
		inputParameters.put(parameterName, type);
	}

	/**
	 * Caches the given query {@link Expression} for future use.
	 *
	 * @param variableName The identification variable name that is the key to store the given
	 * {@link Expression}
	 * @param expression The {@link Expression} that can be reused rather than being recreated
	 */
	void addQueryExpression(String variableName, Expression expression) {
		getCurrentContext().addQueryExpressionImp(variableName, expression);
	}

	private void addQueryExpressionImp(String variableName, Expression expression) {
		expressions.put(variableName.toUpperCase(), expression);
	}

	/**
	 * Adds the given identification variable, which is done when it's used in a clause, except in
	 * the declaration portion of the query.
	 *
	 * @param variableName The identification variable that is used within a clause
	 */
	void addUsedIdentificationVariable(String variableName) {
		usedIdentificationVariables.add(variableName.toUpperCase());
	}

	/**
	 * Creates the EclipseLink {@link Expression} defined by the given {@link Declaration}.
	 *
	 * @param declaration The {@link Declaration} part for which a new {@link Expression} is created
	 * @return A new {@link Expression}
	 */
	Expression buildBaseExpression(Declaration declaration) {

		ClassDescriptor descriptor = getDescriptor(declaration.getAbstractSchemaName());

		// The abstract schema name can't be resolved, we'll assume it's actually an unqualified state
		// field path expression or collection-valued path expression declared in an UPDATE query
		if (descriptor == null) {

			// Convert the AbstractSchemaName into a CollectionValuedPathExpression since
			// it's an unqualified state field path expression or collection-valued path expression
			convertUnqualifiedDeclaration(declaration);

			// The abstract schema name is now a CollectionValuedPathExpression
			return buildQueryExpression(declaration.getBaseExpression());
		}
		else {
			return new ExpressionBuilder(descriptor.getJavaClass());
		}
	}

	private JavaManagedTypeProvider buildProvider(AbstractSession session) {
		return new JavaManagedTypeProvider(session);
	}

	/**
	 * Converts the given {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * into an {@link Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * to visit and to convert into an {@link Expression}
	 * @return The {@link Expression} representing the given parsed expression
	 */
	Expression buildQueryExpression(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
		ExpressionBuilderVisitor expressionBuilder = expressionBuilder();
		try {
			expression.accept(expressionBuilder);
			return expressionBuilder.getQueryExpression();
		}
		finally {
			expressionBuilder.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {

		super.dispose();

		query = null;
		baseExpression = null;

		expressions.clear();
		inputParameters.clear();
		entityExpressions.clear();
		usedIdentificationVariables.clear();
	}

	private ExpressionBuilderVisitor expressionBuilder() {
		if (expressionBuilder == null) {
			expressionBuilder = new ExpressionBuilderVisitor(this);
		}
		return expressionBuilder;
	}

	/**
	 * Returns the root {@link Expression} for which all new {@link Expression expressions} a child.
	 *
	 * @return The root {@link Expression} of the query or subquery
	 */
	Expression getBaseExpression() {
		return getCurrentContext().getBaseExpressionImp();
	}

	private Expression getBaseExpressionImp() {

		if (baseExpression == null) {

			// Retrieve the first declaration, which is the base declaration (For top-level query, it's
			// always a range over an abstract schema name. For subqueries, it's either a range over an
			// abstract schema name or a derived path expression)
			Declaration declaration = getDeclarations().get(0);

			// Create the Expression for the abstract schema name
			if (declaration.isRange()) {
				baseExpression = buildBaseExpression(declaration);
			}
			// Resolve the path expression
			else {
				baseExpression = buildQueryExpression(declaration.getBaseExpression());
			}

			// Cache the base expression with its identification variable as well
			addQueryExpressionImp(declaration.getVariableName(), baseExpression);
		}

		return baseExpression;
	}

	/**
	 * Returns the default {@link Constructor} for the given Java {@link Class}. The constructor to
	 * retrieve has no parameter types.
	 *
	 * @param type The Java type for which its default constructor should be retrieved
	 * @return The default {@link Constructor} or <code>null</code> if none exist or the priviledge
	 * access was denied
	 */
	Constructor<?> getConstructor(Class<?> type) {
		return getConstructor(type, new Class[0]);
	}

	/**
	 * Returns the {@link Constructor} for the given Java {@link Class}. The constructor to retrieve
	 * has the given parameter types.
	 *
	 * @param type The Java type for which its constructor should be retrieved
	 * @param parameterTypes The types of the constructor's parameters
	 * @return The {@link Constructor} or <code>null</code> if none exist or the priviledge access
	 * was denied
	 */
	@SuppressWarnings("unchecked")
	<T> Constructor<T> getConstructor(Class<?> type, Class<?>[] parameterTypes) {
		try {
			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (Constructor<T>) AccessController.doPrivileged(new PrivilegedGetConstructorFor(type, parameterTypes, true));
				}
				catch (PrivilegedActionException exception) {
					return null;
				}
			}

			return PrivilegedAccessHelper.getConstructorFor(type, parameterTypes, true);
		}
		catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DefaultJPQLQueryContext getCurrentContext() {
		return (DefaultJPQLQueryContext) super.getCurrentContext();
	}

	/**
	 * Returns the current {@link DatabaseQuery} being populated.
	 *
	 * @return The {@link DatabaseQuery} being populated, which can be the top-level query or a
	 * subquery depending on which query is been visited
	 */
	@SuppressWarnings("unchecked")
	<T extends DatabaseQuery> T getDatabaseQuery() {
		return (T) getCurrentContext().query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DeclarationResolver getDeclarationResolver(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
		// There is no need to find the DeclarationResolver based on the location of the given
		// Expression because this context keeps the current context in sync automatically
		return getDeclarationResolver();
	}

	/**
	 * Retrieves the descriptor associated with the given abstract schema name, which is the
	 * descriptor's alias.
	 *
	 * @param abstractSchemaName The managed type name associated with the managed type
	 * @return The EclipseLink {@link ClassDescriptor descriptor} representing the managed type
	 */
	ClassDescriptor getDescriptor(String abstractSchemaName) {
		return getSession().getDescriptorForAlias(abstractSchemaName);
	}

	/**
	 * Retrieves the Java type wrapped by the given {@link IType}.
	 *
	 * @param type The external form of the Java type to retrieve
	 * @return The Java type represented by the given external form
	 */
	Class<?> getJavaType(IType type) {
		Class<?> javaType = (type == null) ? null : ((JavaType) type).getType();
		if (javaType == null) {
			javaType = Object.class;
		}
		return javaType;
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	Class<?> getJavaType(org.eclipse.persistence.jpa.internal.jpql.parser.Expression expression) {
		return getJavaType(getType(expression));
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	Class<?> getJavaType(String typeName) {
		return getJavaType(getType(typeName));
	}

	private DefaultJPQLQueryContext getParent() {
		return (DefaultJPQLQueryContext) parent;
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is local to the current query or subquery
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getParentQueryExpression(String variableName) {
		return getCurrentContext().getParent().getQueryExpressionImp(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JavaManagedTypeProvider getProvider() {
		return (JavaManagedTypeProvider) super.getProvider();
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is local to the current query or subquery
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getQueryExpression(String variableName) {
		return getCurrentContext().getQueryExpressionImp(variableName);
	}

	private Expression getQueryExpressionImp(String variableName) {
		return expressions.get(variableName.toUpperCase());
	}

	/**
	 * Returns the EclipseLink {@link AbstractSession} that this query will execute against, which
	 * gives access to the JPA artifacts.
	 *
	 * @return The current EclipseLink session
	 */
	AbstractSession getSession() {
		return getProvider().getSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		this.expressions                 = new HashMap<String, Expression>();
		this.entityExpressions           = new HashMap<String, Expression>();
		this.usedIdentificationVariables = new HashSet<String>();
	}

	/**
	 * Returns the set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type.
	 *
	 * @return The set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type
	 */
	Set<Map.Entry<String, Class<?>>> inputParameters() {
		return inputParameters.entrySet();
	}

	/**
	 * Determines whether the given identification variable is used, at the exception of where it was
	 * defined.
	 *
	 * @param variableName The identification variable to check if it was used in a clause
	 * @return <code>true</code> if the identification is used in a clause, at the exception of where
	 * it was defined; <code>false</code> otherwise
	 */
	boolean isIdentificationVariableUsed(String variableName) {
		return getCurrentContext().isIdentificationVariableUsedImp(variableName.toUpperCase());
	}

	private boolean isIdentificationVariableUsedImp(String variableName) {
		boolean result = usedIdentificationVariables.contains(variableName);
		if (!result && (parent != null)) {
			result = getParent().isIdentificationVariableUsedImp(variableName);
		}
		return result;
	}

	/**
	 * Creates a new instance from the given Java type.
	 *
	 * @param type The Java type for which a new instance will be created
	 * @param parameterType The type of the constructor's single parameter
	 * @param parameter The object to pass during instantiation
	 * @return A new instance or <code>null</code> if a problem was encountered during instantiation
	 */
	@SuppressWarnings("unchecked")
	<T> T newInstance(Class<?> type, Class<?> parameterType, Object parameter) {
		return (T) newInstance(type, new Class[] { parameterType }, new Object[] { parameter });
	}

	/**
	 * Creates a new instance from the given Java type.
	 *
	 * @param type The Java type for which a new instance will be created
	 * @param parameterTypes The types of the constructor's parameters
	 * @param parameters The objects to pass during instantiation
	 * @return A new instance of the given type or <code>null</code> if a problem was encountered
	 * during instantiation
	 */
	<T> T newInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {

		Constructor<T> constructor = getConstructor(type, parameterTypes);

		if (constructor != null) {
			return newInstance(constructor, parameters);
		}

		return null;
	}

	/**
	 * Creates a new instance by using the given constructor.
	 *
	 * @param constructor The constructor used to create the new instance
	 * @param parameters The objects to pass during instantiation
	 * @return A new instance or <code>null</code> if a problem was encountered during instantiation
	 */
	@SuppressWarnings("unchecked")
	<T> T newInstance(Constructor<T> constructor, Object[] parameters) {
		try {
			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (T) AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, parameters));
				}
				catch (PrivilegedActionException exception) {
					return null;
				}
			}

			return (T) PrivilegedAccessHelper.invokeConstructor(constructor, parameters);
		}
		catch (InstantiationException e) {
			return null;
		}
		catch (InvocationTargetException e) {
			return null;
		}
		catch (IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * Changes the state of this context to use the given subquery. Once the subquery is created and
	 * populated, {@link #disposeSubQueryContext()} has to be called.
	 *
	 * @param currentQuery The parsed tree representation of the subquery that will become the
	 * current {@link DatabaseQuery}
	 * @param query The {@link ReportQuery} for the subquery
	 * @see #disposeSubQueryContext()
	 */
	void newSubQueryContext(org.eclipse.persistence.jpa.internal.jpql.parser.Expression currentQuery,
	                        ReportQuery query) {

		currentContext = new DefaultJPQLQueryContext(getCurrentContext(), currentQuery, query);
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate an {@link
	 * ObjectLevelReadQuery}.
	 *
	 * @return The visitor used for a query of object level read query type
	 */
	ObjectLevelReadQueryVisitor objectLevelReadQueryVisitor() {
		if (objectLevelReadQueryVisitor == null) {
			objectLevelReadQueryVisitor = new ObjectLevelReadQueryVisitor(this);
		}
		return objectLevelReadQueryVisitor;
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate an {@link ReadQuery}.
	 *
	 * @return The visitor used for a query of read query type
	 */
	ReadQueryBuilder readQueryBuilder() {
		if (readQueryBuilder == null) {
			readQueryBuilder = new ReadQueryBuilder(this);
		}
		return readQueryBuilder;
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate an {@link ReadQuery},
	 * but depends on the type of the query (report query or read query).
	 *
	 * @return The visitor used for a read query (read or report query)
	 */
	AbstractObjectLevelReadQueryVisitor<? extends ObjectLevelReadQuery> readQueryVisitor() {
		return query.isReportQuery() ? reportQueryVisitor() : objectLevelReadQueryVisitor();
	}

	/**
	 * Returns the visitor that will visit the parsed JPQL query and populate an {@link ReportQuery}.
	 *
	 * @return The visitor used for a query of report query type
	 */
	ReportQueryVisitor reportQueryVisitor() {
		if (reportQueryVisitor == null) {
			reportQueryVisitor = new ReportQueryVisitor(this);
		}
		return reportQueryVisitor;
	}

	/**
	 * Resolves the {@link Expression} of the path expression represented by the given {@link Resolver}.
	 *
	 * @param resolver The {@link Resolver} used to properly navigate a path expression
	 * @return The {@link Expression} representing the path expression
	 */
	Expression resolve(Resolver resolver) {
		return expressionBuilder().resolve(resolver);
	}

	/**
	 * Sets the given named query and string representation of the JPQL query.
	 *
	 * @param session The EclipseLink {@link AbstractSession} this query will use
	 * @param jpqlQuery The JPQL query
	 */
	void setQuery(AbstractSession session, String jpqlQuery) {
		setQuery(new JavaQuery(buildProvider(session), jpqlQuery));
	}

	/**
	 * Stores the {@link DatabaseQuery query} into this context.
	 *
	 * @param query The query that will be populated by visiting the parsed tree representation of
	 * the JPQL query
	 */
	void setQuery(DatabaseQuery query) {
		getCurrentContext().query = query;
	}

	private void store(DefaultJPQLQueryContext parent, ReportQuery query) {
		this.query                       = query;
		this.inputParameters             = parent.inputParameters;
		this.readQueryBuilder            = parent.readQueryBuilder;
		this.expressionBuilder           = parent.expressionBuilder;
		this.reportQueryVisitor          = parent.reportQueryVisitor;
		this.objectLevelReadQueryVisitor = parent.objectLevelReadQueryVisitor;
	}
}