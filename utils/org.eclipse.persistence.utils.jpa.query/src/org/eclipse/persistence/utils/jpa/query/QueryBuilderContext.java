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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpression;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This context is used to store information related to the JPQL query and is used to properly
 * convert a JPQL query into an EclipseLink {@link Expression}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class QueryBuilderContext {

	/**
	 * The current {@link QueryBuilderContext} is the context used for the current query or subquery.
	 * If the current context is not the global context, then its parent is non <code>null</code>.
	 */
	private QueryBuilderContext currentContext;

	/**
	 * The parsed {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression} being
	 * visited.
	 */
	private org.eclipse.persistence.utils.jpa.query.parser.Expression currentQuery;

	/**
	 * The resolver of the current query's declaration. For a <b>SELECT</b> query, it contains the
	 * information defined in the <b>FROM</b> clause. For <b>DELETE</b> and <b>UPDATE</b> queries,
	 * it contains a single range declaration variable.
	 */
	private DeclarationExpressionResolver declarationPathExpressionResolver;

	/**
	 * The builder that can convert a {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL
	 * Expression} into an {@link Expression EclipseLink Expression}.
	 */
	private ExpressionBuilderVisitor expressionBuilder;

	/**
	 * The cached {@link Expression Expressions} mapped by their variable names.
	 */
	private Map<String, Expression> expressions;

	/**
	 * The input parameter name mapped to its type. The input parameter name starts with the
	 * positional parameter ('?' or ':').
	 */
	private Map<String, Class<?>> inputParameters;

	/**
	 * The string representation of the JPQL query.
	 */
	private String jpqlQuery;

	/**
	 * This visitor is responsible to create and populate a {@link ObjectLevelReadQuery}.
	 */
	private ObjectLevelReadQueryVisitor objectLevelReadQueryVisitor;

	/**
	 * When this context is a sub-context used for a subquery, then this is the context for the
	 * parent query.
	 */
	private QueryBuilderContext parent;

	/**
	 * The {@link DatabaseQuery} being populated by visiting the parsed tree representation of the
	 * JPQL query.
	 */
	private DatabaseQuery query;

	/**
	 * This helper is used to retrieve to access the application's metadata information and to
	 * calculate the input parameter.
	 */
	private DefaultJPQLQueryHelper queryHelper;

	/**
	 * This visitor is responsible to create the right read query based on the select expression.
	 */
	private ReadQueryBuilder readQueryBuilder;

	/**
	 * This visitor is responsible to create and populate a {@link ReportQuery}.
	 */
	private ReportQueryVisitor reportQueryVisitor;

	/**
	 * The EclipseLink {@link AbstractSession} that this query will execute against, which gives
	 * access to the JPA artifacts.
	 */
	private AbstractSession session;

	/**
	 * Internal flag used to determine if the declaration portion of the query was visited.
	 */
	private boolean traversed;

	/**
	 * This visitor can determine the type of a JPQL expression.
	 */
	private TypeVisitor typeVisitor;

	/**
	 * The identification variables that are found in the query and used in any clauses (except
	 * defined in the <b>FROM</b>, <b>DELETE</b> or <b>UPDATE</b> clauses).
	 */
	private Set<String> usedIdentificationVariables;

	/**
	 * This visitor is used to retrieve a variable name from various type of
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}.
	 */
	private VariableNameVisitor variableNameVisitor;

	/**
	 * Creates a new <code>QueryBuilderContext</code>.
	 */
	QueryBuilderContext() {
		super();
		initialize();
	}

	/**
	 * Creates a new sub-<code>QueryBuilderContext</code>.
	 *
	 * @param parent The parent context
	 * @param currentQuery The parsed tree representation of the subquery
	 * @param query The {@link ReportQuery} that will be populated by visiting the parsed tree
	 * representation of the subquery
	 */
	private QueryBuilderContext(QueryBuilderContext parent,
	                            org.eclipse.persistence.utils.jpa.query.parser.Expression currentQuery,
	                            ReportQuery query) {

		this();
		this.parent      = parent;
		this.typeVisitor = parent.typeVisitor;
		store(parent.session, query, currentQuery, parent.jpqlQuery, parent.queryHelper);
	}

	/**
	 * Caches the given query {@link Expression} for future use.
	 *
	 * @param variableName The identification variable name that is the key to store the given
	 * {@link Expression}
	 * @param expression The {@link Expression} that can be reused rather than being recreated
	 */
	void addExpression(String variableName, Expression expression) {
		currentContext.expressions.put(variableName.toLowerCase(), expression);
	}

	/**
	 * Adds the given identification variable, which is done when it's used in a clause, except in
	 * the declaration portion of the query.
	 *
	 * @param variableName The identification variable that is used within a clause
	 */
	void addIdentificationVariable(String variableName) {
		usedIdentificationVariables.add(variableName.toLowerCase());
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

	private DeclarationExpressionResolver buildDeclarationPathExpressionResolver() {
		DeclarationExpressionResolver parentResolver = (parent != null) ? parent.getDeclarationPathExpressionResolverImp() : null;
		return new DeclarationExpressionResolver(parentResolver, currentContext);
	}

	/**
	 * Converts the given {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * into an {@link Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * to visit and to convert into an {@link Expression}
	 * @return The {@link Expression} representing the given parsed expression
	 */
	Expression buildExpression(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
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
	 * Converts the given {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * into an {@link Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}
	 * to visit and to retrieve a {@link PathExpressionResolver} that can create a {@link Expression}
	 * @return The {@link PathExpressionResolver} that can resolve the given JPQL expression
	 */
	PathExpressionResolver buildPathExpressionResolver(AbstractExpression expression) {
		ExpressionBuilderVisitor expressionBuilder = expressionBuilder();
		try {
			expressionBuilder.setResolverOnly(true);
			expression.accept(expressionBuilder);
			return expressionBuilder.getResolver();
		}
		finally {
			expressionBuilder.dispose();
		}
	}

	/**
	 * Disposes of the internal data.
	 */
	void dispose() {

		query = null;
		session = null;
		jpqlQuery = null;
		traversed = false;
		queryHelper = null;
		typeVisitor = null;
		currentQuery = null;
		currentContext = this;

		expressions.clear();
		inputParameters.clear();
		usedIdentificationVariables.clear();
		declarationPathExpressionResolver.dispose();
	}

	/**
	 * Disposes this context, which is the current context being used for a subquery. Once it's
	 * disposed, any information retrieved will be for the subquery's parent query.
	 */
	void disposeSubQueryContext() {
		currentContext = currentContext.parent;
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
		return getDeclarationPathExpressionResolver().getExpression();
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
	 * Returns the resolver of the current query's declaration. For a <b>SELECT</b> query, it
	 * contains the information defined in the <b>FROM</b> clause. For <b>DELETE</b> and <b>UPDATE</b>
	 * queries, it contains a single range declaration variable.
	 *
	 * @return The top-level {@link PathExpressionResolver} for the current query being visited
	 */
	DeclarationExpressionResolver getDeclarationPathExpressionResolver() {
		return currentContext.getDeclarationPathExpressionResolverImp();
	}

	private DeclarationExpressionResolver getDeclarationPathExpressionResolverImp() {

		if (declarationPathExpressionResolver == null) {
			declarationPathExpressionResolver = buildDeclarationPathExpressionResolver();
		}

		// Only traverse the range variable declaration once
		if (!traversed) {
			traversed = true;
			currentQuery.accept(declarationPathExpressionResolver);
		}

		return declarationPathExpressionResolver;
	}

	/**
	 * Retrieves the descriptor associated with the given abstract schema name, which is the
	 * descriptor's alias.
	 *
	 * @param abstractSchemaName The managed type name associated with the managed type
	 * @return The EclipseLink {@link ClassDescriptor descriptor} representing the managed type
	 */
	ClassDescriptor getDescriptor(String abstractSchemaName) {
		return session.getDescriptorForAlias(abstractSchemaName);
	}

	/**
	 * Returns the {@link IType} representing the possible given enum type. If the given value is not
	 * representing an enum constant, then <code>null</code> is returned.
	 *
	 * @param enumTypeName The fully qualified enum type with the constant
	 * @return The Java type for the given enum type name or <code>null</code> if none could be
	 * retrieved
	 */
	Class<?> getEnumJavaType(String enumTypeName) {
		return getType(getEnumType(enumTypeName));
	}

	/**
	 * Returns the {@link IType} representing the possible given enum type. If the type name
	 *
	 * @param enumTypeName The fully qualified enum type with the constant
	 * @return The external form for the given Enum type
	 */
	IType getEnumType(String enumTypeName) {
		return queryHelper.getTypeRepository().getEnumType(enumTypeName);
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is local to the current query or subquery
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getExpression(String variableName) {
		return currentContext.expressions.get(variableName.toLowerCase());
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	Class<?> getJavaType(String typeName) {
		return getType(queryHelper.getTypeRepository().getType(typeName));
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	JPQLExpression getJPQLExpression() {
		return currentQuery.getRoot();
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return The string representation of the JPQL query
	 */
	String getJPQLQuery() {
		return jpqlQuery;
	}

	/**
	 * Returns the external form of the mapping for the given expression.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.query.parser.Expression Expression}
	 * that can represent a state field or a collection-valued field
	 * @return The {@link IMapping} for the given expression or <code>null</code> if the expression
	 * does not represent a state field or collection-valued field
	 */
	IMapping getMapping(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
		TypeVisitor visitor = typeVisitor();
		expression.accept(visitor);
		return visitor.getMapping();
	}

	/**
	 * Retrieves, if it can be determined, the type of the given input parameter with the given name.
	 * The type will be guessed based on its location within expression.
	 * <p>
	 * Note: Both named and positional input parameter can be used.
	 *
	 * @param parameterName The name of the input parameter to retrieve its type, which needs to be
	 * prepended by ':' or '?'
	 * @return Either the closest type of the input parameter or <code>null</code> if the type
	 * couldn't be determined
	 */
	Class<?> getParameterType(JPQLExpression expression, String parameterName) {
		IType type = queryHelper.getParameterType(expression, parameterName);
		return getType(type);
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is local to the current query or subquery
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getParentExpression(String variableName) {
		return currentContext.parent.expressions.get(variableName.toLowerCase());
	}

	/**
	 * Returns the current query being populated.
	 *
	 * @return The {@link DatabaseQuery} being populated, which can be the top-level query or a
	 * subquery depending on which query is been visited
	 */
	@SuppressWarnings("unchecked")
	<T extends DatabaseQuery> T getQuery() {
		return (T) currentContext.query;
	}

	/**
	 * Returns the EclipseLink {@link AbstractSession} that this query will execute against, which
	 * gives access to the JPA artifacts.
	 *
	 * @return The current session
	 */
	AbstractSession getSession() {
		return session;
	}

	/**
	 * Retrieves the Java type wrapped by the given {@link IType}.
	 *
	 * @param type The external form of the Java type to retrieve
	 * @return The Java type represented by the given external form
	 */
	Class<?> getType(IType type) {
		return (type == null) ? null : ((JavaType) type).getType();
	}

	/**
	 * Retrieves the Java type for the given {@link org.eclipse.persistence.jpa.query.parser.Expression
	 * Expression}.
	 *
	 * @param expression The parsed {@link org.eclipse.persistence.jpa.query.parser.Expression
	 * Expression} for which its type is required
	 * @return The Java type of the given expression or <code>Object</code> if it cannot be calculated
	 */
	Class<?> getType(org.eclipse.persistence.utils.jpa.query.parser.Expression expression) {
		TypeVisitor visitor = typeVisitor();
		expression.accept(visitor);
		return getType(visitor.getType());
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	IType getType(String typeName) {
		return queryHelper.getTypeRepository().getType(typeName);
	}

	/**
	 * Determines whether the JPQL expression has <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
	 * expressions; <code>false</code> otherwise
	 */
	boolean hasJoins() {
		return getDeclarationPathExpressionResolver().hasJoins();
	}

	/**
	 * Initializes this {@link QueryBuilderContext}.
	 */
	private void initialize() {
		this.currentContext  = this;
		this.expressions     = new HashMap<String, Expression>();
		this.inputParameters = new LinkedHashMap<String, Class<?>>();
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
		return currentContext.isIdentificationVariableUsedImp(variableName.toLowerCase());
	}

	private boolean isIdentificationVariableUsedImp(String variableName) {
		boolean result = usedIdentificationVariables.contains(variableName);
		if (!result && (parent != null)) {
			result = parent.isIdentificationVariableUsedImp(variableName);
		}
		return result;
	}

	/**
	 * Determines whether the given variable name is an identification variable name used to define
	 * an abstract schema name.
	 *
	 * @param variableName The name of the variable to verify if it's defined in a range variable
	 * declaration in the current query or any parent query
	 * @return <code>true</code> if the variable name is mapping an abstract schema name; <code>false</code>
	 * if it's defined in a collection member declaration
	 */
	boolean isRangeIdentificationVariable(String variableName) {
		return getDeclarationPathExpressionResolver().isRangeIdentificationVariable(variableName);
	}

	/**
	 * Returns the parsed representation of a <b>JOIN FETCH</b> that were defined in the same
	 * declaration than the given range identification variable name.
	 *
	 * @param variableName The name of the identification variable that should be used to define an
	 * abstract schema name
	 * @return The <b>JOIN FETCH</b> expressions used in the same declaration or an empty collection
	 * if none was defined
	 */
	Collection<JoinFetch> joinFetches(String variableName) {
		return getDeclarationPathExpressionResolver().joinFetches(variableName);
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
	 * @return A new instance or <code>null</code> if a problem was encountered during instantiation
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
	 * Changes the state of this context to use the given subquery.
	 *
	 * @param currentQuery The parsed tree representation of the subquery that will become the
	 * current query
	 * @param query The {@link ReportQuery} for the subquery
	 * @see #disposeSubQueryContext()
	 */
	void newSubQueryContext(org.eclipse.persistence.utils.jpa.query.parser.Expression currentQuery,
	                        ReportQuery query) {

		currentContext = new QueryBuilderContext(currentContext, currentQuery, query);
	}

	ObjectLevelReadQueryVisitor objectLevelReadQueryVisitor() {
		if (objectLevelReadQueryVisitor == null) {
			objectLevelReadQueryVisitor = new ObjectLevelReadQueryVisitor(this);
		}
		return objectLevelReadQueryVisitor;
	}

	ReadQueryBuilder readQueryBuilder() {
		if (readQueryBuilder == null) {
			readQueryBuilder = new ReadQueryBuilder(this);
		}
		return readQueryBuilder;
	}

	AbstractObjectLevelReadQueryVisitor<? extends ObjectLevelReadQuery> readQueryVisitor() {
		return query.isReportQuery() ? reportQueryVisitor() : objectLevelReadQueryVisitor();
	}

	ReportQueryVisitor reportQueryVisitor() {
		if (reportQueryVisitor == null) {
			reportQueryVisitor = new ReportQueryVisitor(this);
		}
		return reportQueryVisitor;
	}

	/**
	 * Stores the {@link DatabaseQuery query} into this context.
	 *
	 * @param query The query that will be populated by visiting the parsed tree representation of
	 * the JPQL query
	 */
	void setQuery(DatabaseQuery query) {
		currentContext.query = query;
	}

	/**
	 * Stores the given information into this context for usage.
	 *
	 * @param session The EclipseLink {@link AbstractSession} that this query will execute against
	 * @param query
	 * @param currentQuery The parsed tree representation of the JPQL query
	 * @param jpqlQuery A non-<code>null</code> string representation of the query to parse and to
	 * convert into a {@link DatabaseQuery}
	 * @param queryHelper The helper that can calculate an expression's type
	 */
	void store(AbstractSession session,
	           DatabaseQuery query,
	           org.eclipse.persistence.utils.jpa.query.parser.Expression currentQuery,
	           String jpqlQuery,
	           DefaultJPQLQueryHelper queryHelper) {

		this.query        = query;
		this.session      = session;
		this.jpqlQuery    = jpqlQuery;
		this.queryHelper  = queryHelper;
		this.currentQuery = currentQuery;
	}

	private TypeVisitor typeVisitor() {
		if (typeVisitor == null) {
			typeVisitor = new TypeVisitor(queryHelper.getQuery());
		}
		return typeVisitor;
	}

	/**
	 * Retrieves the variable name from the given {@link org.eclipse.persistence.jpa.query.parser.Expression
	 * JPQL Expression}.
	 *
	 * @param expression The expression to visit
	 * @param type The type helps to determine how to visit the expression and to retrieve the
	 * desired variable name
	 * @return The variable name of the given expression
	 */
	String variableName(org.eclipse.persistence.utils.jpa.query.parser.Expression expression, Type type) {
		VariableNameVisitor visitor = variableNameVisitor();
		visitor.setType(type);
		expression.accept(visitor);
		return visitor.variableName;
	}

	private VariableNameVisitor variableNameVisitor() {
		if (variableNameVisitor == null) {
			variableNameVisitor = new VariableNameVisitor();
		}
		return variableNameVisitor;
	}
}