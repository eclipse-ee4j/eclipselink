/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse License is available at http://www.eclipse.org/legal/epl-v10.html
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.jpql.EclipseLinkLiteralVisitor;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * This context is used when creating and populating a {@link DatabaseQuery} when traversing the
 * parsed representation of a JPQL query.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class JPQLQueryContext {

	/**
	 * The current {@link JPQLQueryContext} is the context used for the current query or subquery.
	 * If the current context is not the global context, then its parent is non <code>null</code>.
	 */
	private JPQLQueryContext currentContext;

	/**
	 * The parsed {@link Expression JPQL Expression} currently visited.
	 */
	private org.eclipse.persistence.jpa.jpql.parser.Expression currentQuery;

	/**
	 * The resolver of the current query's declaration. For a <b>SELECT</b> query, it contains the
	 * information defined in the <b>FROM</b> clause. For <b>DELETE</b> and <b>UPDATE</b> queries,
	 * it contains a single range declaration variable.
	 */
	private DeclarationResolver declarationResolver;

	/**
	 * The builder that can convert a {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL
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
	Map<InputParameter, Expression> inputParameters;

	/**
	 * The parsed representation of the JPQL query.
	 */
	private JPQLExpression jpqlExpression;

	/**
	 * The JPQL grammar that defines how to parse a JPQL query.
	 */
	private JPQLGrammar jpqlGrammar;

	/**
	 * The JPQL query to convert into an EclipseLink {@link Expression}.
	 */
	private CharSequence jpqlQuery;

	/**
	 * This visitor is used to retrieve a variable name from various type of
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}.
	 */
	private EclipseLinkLiteralVisitor literalVisitor;

	/**
	 * This visitor is responsible to calculate the closest type of any input parameter.
	 */
	private ParameterTypeVisitor parameterTypeVisitor;

	/**
	 * When this context is a sub-context used for a subquery, then this is the context for the
	 * parent query.
	 */
	private JPQLQueryContext parent;

	/**
	 * The {@link DatabaseQuery} being populated by visiting the parsed tree representation of the
	 * JPQL query.
	 */
	private DatabaseQuery query;

	/**
	 * The EclipseLink {@link AbstractSession} this context will use.
	 */
	private AbstractSession session;

	/**
	 * This resolver is used to calculate the type of any {@link org.eclipse.persistence.jpa.jpql.
	 * parser.Expression JPQL Expression}.
	 */
	private TypeResolver typeResolver;

	/**
	 * The types that have been cached for faster access.
	 */
	private Map<String, Class<?>> types;

	/**
	 * The identification variables that were used in the current query.
	 */
	private Set<String> usedIdentificationVariables;

	/**
	 * An empty array used when the type of an {@link org.eclipse.persistence.jpa.jpql.parser.
	 * Expression JPQL Expression} is not required.
	 */
	private static final Class<?>[] EMPTY_TYPE = new Class<?>[1];

	/**
	 * Creates a new <code>JPQLQueryContext</code>.
	 *
	 * @param query The {@link DatabaseQuery} from which information will be queries
	 * @param jpqlGrammar The JPQL grammar that defines how the JPQL query was parsed
	 * @since 2.5
	 */
	JPQLQueryContext(DatabaseQuery query, JPQLGrammar jpqlGrammar) {
		this(jpqlGrammar);
		String jpqlQuery = query.getJPQLString();
		cache(query.getSession(), query, parse(jpqlQuery), jpqlQuery);
	}

	/**
	 * Creates a new <code>JPQLQueryContext</code>.
	 *
	 * @param jpqlGrammar The JPQL grammar that defines how the JPQL query was parsed
	 */
	JPQLQueryContext(JPQLGrammar jpqlGrammar) {
		super();
		this.jpqlGrammar    = jpqlGrammar;
		this.currentContext = this;
	}

	/**
	 * Creates a new <code>JPQLQueryContext</code>.
	 *
	 * @param parent The parent of this context, which is never <code>null</code>
	 * @param currentQuery The parsed tree representation of the subquery
	 * @param query The {@link ReportQuery} that will be populated by visiting the parsed tree
	 * representation of the subquery
	 */
	private JPQLQueryContext(JPQLQueryContext parent,
	                         org.eclipse.persistence.jpa.jpql.parser.Expression currentQuery,
	                         ReportQuery query) {

		this(parent.jpqlGrammar);
		this.query        = query;
		this.parent       = parent;
		this.currentQuery = currentQuery;
	}

	/**
	 * Caches the given input parameter name with its calculated type.
	 *
	 * @param parameterName The name of the input parameter
	 * @param type The calculated type based on its surrounding, which is never <code>null</code>
	 */
	void addInputParameter(InputParameter inputParameter, Expression queryExpression) {

		if (parent != null) {
			parent.addInputParameter(inputParameter, queryExpression);
		}
		else {
			if (inputParameters == null) {
				inputParameters = new HashMap<InputParameter, Expression>();
			}
			inputParameters.put(inputParameter, queryExpression);
		}
	}

	/**
	 * Caches the given query {@link Expression} for future use.
	 *
	 * @param variableName The identification variable name that is the key to store the given
	 * {@link Expression}
	 * @param expression The {@link Expression} that can be reused rather than being recreated
	 */
	void addQueryExpression(String variableName, Expression expression) {
		currentContext.addQueryExpressionImp(variableName, expression);
	}

	/**
	 * Caches the given query {@link Expression} for future use.
	 *
	 * @param variableName The identification variable name that is the key to store the given
	 * {@link Expression}
	 * @param expression The {@link Expression} that can be reused rather than being recreated
	 */
	void addQueryExpressionImp(String variableName, Expression expression) {
		if (expressions == null) {
			expressions = new HashMap<String, Expression>();
		}
		expressions.put(variableName, expression);
	}

	/**
	 * Adds a virtual range variable declaration that will be used when a JPQL fragment is parsed.
	 *
	 * @param entityName The name of the entity to be accessible with the given variable name
	 * @param variableName The identification variable used to navigate to the entity
	 */
	void addRangeVariableDeclaration(String entityName, String variableName) {

		// This method should only be used by HermesParser.buildSelectionCriteria()
		declarationResolver = new DeclarationResolver(this, null);

		declarationResolver.addRangeVariableDeclaration(
			entityName,
			variableName
		);
	}

	/**
	 * Adds the given identification variable, which is done when it's used in a clause, except in
	 * the declaration portion of the query.
	 *
	 * @param variableName The identification variable that is used within a clause
	 */
	void addUsedIdentificationVariable(String variableName) {
		currentContext.addUsedIdentificationVariableImp(variableName);
	}

	/**
	 * Adds the given identification variable, which is done when it's used in a clause, except in
	 * the declaration portion of the query.
	 *
	 * @param variableName The identification variable that is used within a clause
	 */
	void addUsedIdentificationVariableImp(String variableName) {
		if (usedIdentificationVariables == null) {
			usedIdentificationVariables = new HashSet<String>();
		}
		usedIdentificationVariables.add(variableName);
	}

	/**
	 * Retrieves the Java type for the given type name, which has to be the fully qualified type name.
	 *
	 * @param typeName The fully qualified type name
	 * @return The Java type if it could be retrieved; <code>null</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	private Class<?> attemptLoadType(String typeName) {

		try {

			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (Class<?>) AccessController.doPrivileged(
						new PrivilegedClassForName(typeName, true, getClassLoader())
					);
				}
				catch (PrivilegedActionException exception) {
					return null;
				}
			}

			return PrivilegedAccessHelper.getClassForName(typeName, true, getClassLoader());
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Converts the given {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression}
	 * into an {@link Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression}
	 * to visit and to convert into an {@link Expression}
	 * @return The {@link Expression} representing the given parsed expression
	 */
	Expression buildExpression(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		return expressionBuilder().buildExpression(expression, EMPTY_TYPE);
	}

	/**
	 * Converts the given {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression}
	 * into an {@link Expression}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression}
	 * to visit and to convert into an {@link Expression}
	 * @param type The given array will be used to store the type of the given expression
	 * @return The {@link Expression} representing the given parsed expression
	 */
	Expression buildExpression(org.eclipse.persistence.jpa.jpql.parser.Expression expression,
	                           Class<?>[] type) {

		return expressionBuilder().buildExpression(expression, type);
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
		return expressionBuilder().buildGroupByExpression(expression);
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
		return expressionBuilder().buildModifiedPathExpression(expression);
	}

	/**
	 * Creates a new {@link ReportQuery} by visiting the given {@link SimpleSelectStatement}.
	 *
	 * @param expression The {@link SimpleSelectStatement} to convert into a {@link ReportQuery}
	 * @return A fully initialized {@link ReportQuery}
	 */
	ReportQuery buildSubquery(SimpleSelectStatement expression) {
		return expressionBuilder().buildSubquery(expression);
	}

	/**
	 * Sets the given named query and string representation of the JPQL query.
	 *
	 * @param session The EclipseLink {@link AbstractSession} this context will use
	 * @param query The {@link DatabaseQuery} that may already exist and it will be populated with
	 * the information contained in the JPQL query or <code>null</code> if the query will need to be created
	 * @param jpqlExpression The parsed tree representation of the JPQL query
	 * @param jpqlQuery The JPQL query
	 */
	void cache(AbstractSession session,
	           DatabaseQuery query,
	           JPQLExpression jpqlExpression,
	           CharSequence jpqlQuery) {

		this.query          = query;
		this.session        = session;
		this.jpqlQuery      = jpqlQuery;
		this.currentQuery   = jpqlExpression;
		this.jpqlExpression = jpqlExpression;
	}

	/**
	 * Returns the type of the given {@link DatabaseMapping}, which is the persistent field type.
	 *
	 * @param mapping The {@link DatabaseMapping} to retrieve its persistent field type
	 * @return The persistent field type
	 */
	Class<?> calculateMappingType(DatabaseMapping mapping) {
		return typeResolver().calculateMappingType(mapping);
	}

	/**
	 * Returns the type of the given {@link QueryKey}, which is the persistent field type.
	 *
	 * @param queryKey The {@link QueryKey} to retrieve its persistent field type
	 * @return The persistent field type
	 */
	Class<?> calculateQueryKeyType(QueryKey queryKey) {
		return typeResolver().calculateQueryKeyType(queryKey);
	}

	/**
	 * Disposes this context, which is the current context being used by a subquery. Once it is
	 * disposed, any information retrieved will be for the subquery's parent query.
	 */
	void disposeSubqueryContext() {
		currentContext = currentContext.parent;
	}

	private ExpressionBuilderVisitor expressionBuilder() {

		if (parent != null) {
			return parent.expressionBuilder();
		}

		if (expressionBuilder == null) {
			expressionBuilder = new ExpressionBuilderVisitor(this);
		}

		return expressionBuilder;
	}

	/**
	 * Retrieves the {@link Declaration} for which the given variable name is used to navigate to the
	 * "root" object. This goes up the hierarchy when looking for the {@link Declaration} if it's not
	 * defined in the current context.
	 *
	 * @param variableName The name of the identification variable that is used to navigate a "root" object
	 * @return The {@link Declaration} containing the information about the identification variable declaration
	 * @see #getDeclaration(String)
	 */
	Declaration findDeclaration(String variableName) {
		return currentContext.findDeclarationImp(variableName);
	}

	Declaration findDeclarationImp(String variableName) {
		Declaration declaration = getDeclarationResolverImp().getDeclaration(variableName);
		if ((declaration == null) && (parent != null)) {
			declaration = parent.findDeclarationImp(variableName);
		}
		return declaration;
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is the entire query (going from the current query, if it's a subquery, up to the
	 * top-level query).
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression findQueryExpression(String variableName) {
		return currentContext.findQueryExpressionImp(variableName);
	}

	Expression findQueryExpressionImp(String variableName) {
		Expression expression = getQueryExpressionImp(variableName);
		if ((expression == null) && (parent != null)) {
			expression = parent.findQueryExpressionImp(variableName);
		}
		return expression;
	}

	/**
	 * Returns the parent context if the current context is not the root context.
	 *
	 * @return The parent context or <code>null</code> if the current context is the root
	 */
	JPQLQueryContext getActualParent() {
		return parent;
	}

	/**
	 * Returns the root {@link Expression} for which all new {@link Expression expressions} a child.
	 *
	 * @return The root {@link Expression} of the query or subquery
	 */
	Expression getBaseExpression() {
		// Retrieve the first declaration, which is the base declaration (For top-level query, it's
		// always a range over an abstract schema name. For subqueries, it's either a range over an
		// abstract schema name or a derived path expression)
		return getDeclarationResolver().getFirstDeclaration().getQueryExpression();
	}

	/**
	 * Returns the {@link ClassLoader} used by EclipseLink to load the application's classes.
	 *
	 * @return The application's {@link ClassLoader}
	 */
	private ClassLoader getClassLoader() {
		return getSession().getDatasourcePlatform().getConversionManager().getLoader();
	}

	/**
	 * Returns the {@link Constructor} for the given Java {@link Class}. The constructor to retrieve
	 * has the given parameter types.
	 *
	 * @param type The Java type for which its constructor should be retrieved
	 * @param parameterTypes The types of the constructor's parameters
	 * @return The {@link Constructor} or <code>null</code> if none exist or the privilege access
	 * was denied
	 */
	@SuppressWarnings("unchecked")
	<T> Constructor<T> getConstructor(Class<?> type, Class<?>[] parameterTypes) {

		try {
			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (Constructor<T>) AccessController.doPrivileged(
						new PrivilegedGetConstructorFor(type, parameterTypes, true)
					);
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
	 * Returns the current {@link JPQLQueryContext}, i.e. the context of the query being manipulated,
	 * which can either be the top-level query or a subquery.
	 *
	 * @return The active context
	 */
	JPQLQueryContext getCurrentContext() {
		return currentContext;
	}

	/**
	 * Returns the current {@link Expression} being manipulated, which is either the top-level query
	 * or a subquery.
	 *
	 * @return Either the top-level query or a subquery
	 */
	org.eclipse.persistence.jpa.jpql.parser.Expression getCurrentQuery() {
		return currentContext.currentQuery;
	}

	/**
	 * Returns the current {@link DatabaseQuery} being populated.
	 *
	 * @return The {@link DatabaseQuery} being populated, which can be the top-level query or a
	 * subquery depending on which query is been visited
	 */
	@SuppressWarnings("unchecked")
	<T extends DatabaseQuery> T getDatabaseQuery() {
		return (T) currentContext.query;
	}

	/**
	 * Retrieves the {@link Declaration} for which the given variable name is used to navigate to the
	 * "root" object. This does not go up the hierarchy when looking for the {@link Declaration}.
	 *
	 * @param variableName The name of the identification variable that is used to navigate a "root" object
	 * @return The {@link Declaration} containing the information about the identification variable declaration
	 * @see #findDeclaration(String)
	 */
	Declaration getDeclaration(String variableName) {
		return getDeclarationResolver().getDeclaration(variableName);
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
	DeclarationResolver getDeclarationResolver() {
		return currentContext.getDeclarationResolverImp();
	}

	/**
	 * Returns the {@link DeclarationResolver} of the current query's declaration.
	 *
	 * @return The {@link DeclarationResolver} for the current query being visited
	 */
	DeclarationResolver getDeclarationResolverImp() {

		if (declarationResolver == null) {
			DeclarationResolver parentResolver = (parent == null) ? null : parent.getDeclarationResolverImp();
			declarationResolver = new DeclarationResolver(this, parentResolver);
		}

		declarationResolver.populate(currentQuery);
		return declarationResolver;
	}

	/**
	 * Returns the ordered list of {@link Declaration Declarations}.
	 *
	 * @return The {@link Declaration Declarations} of the current query that was parsed
	 */
	List<Declaration> getDeclarations() {
		return getDeclarationResolver().getDeclarations();
	}

	/**
	 * Retrieves the descriptor associated with the given type.
	 *
	 * @param type The type associated with the descriptor
	 * @return The EclipseLink {@link ClassDescriptor descriptor} representing the managed type
	 */
	ClassDescriptor getDescriptor(Class<?> type) {
		return getSession().getClassDescriptor(type);
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
	 * Returns the {@link Class} representing the possible given enum constant. If the given value
	 * does not represent an enum constant, then <code>null</code> is returned.
	 *
	 * @param enumTypeName The fully qualified enum type with the constant
	 * @return The external form for the given Enum type or <code>null</code> if none exists
	 */
	Class<?> getEnumType(String enumTypeName) {

		// Get the position of the last dot so we can remove the constant
		int lastDotIndex = enumTypeName.lastIndexOf(".");

		if (lastDotIndex == -1) {
			return null;
		}

		// Retrieve the fully qualified enum type name
		String typeName = enumTypeName.substring(0, lastDotIndex);

		// Attempt to load the enum type
		Class<?> type = getType(typeName);
		return ((type != null) && type.isEnum()) ? type : null;
	}

	/**
	 * Returns the first {@link Declaration} of the current context that was created after visiting
	 * the declaration clause.
	 *
	 * @return The first {@link Declaration} object
	 */
	Declaration getFirstDeclaration() {
		return currentContext.getFirstDeclarationImp();
	}

	/**
	 * Returns the first {@link Declaration}, from this context, that was created after visiting the
	 * declaration clause.
	 *
	 * @return The first {@link Declaration} object
	 */
	Declaration getFirstDeclarationImp() {
		return getDeclarationResolverImp().getFirstDeclaration();
	}

	/**
	 * Returns the {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query
	 */
	JPQLGrammar getGrammar() {
		return jpqlGrammar;
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
	Collection<Join> getJoinFetches(String variableName) {
		return getDeclarationResolver().getJoinFetches(variableName);
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	JPQLExpression getJPQLExpression() {
		return (parent != null) ? parent.getJPQLExpression() : jpqlExpression;
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return The string representation of the JPQL query
	 */
	CharSequence getJPQLQuery() {
		return (parent != null) ? parent.getJPQLQuery() : jpqlQuery;
	}

	/**
	 * Retrieves, if it can be determined, the type of the given {@link InputParameter}. The type
	 * will be guessed based on its location within expression.
	 * <p>
	 * Note: Both named and positional input parameter can be used.
	 *
	 * @param inputParameter The {@link InputParameter} to retrieve its type
	 * @return Either the closest type of the input parameter or <code>null</code> if the type could
	 * not be determined
	 */
	Class<?> getParameterType(InputParameter inputParameter) {
		ParameterTypeVisitor resolver = parameterTypeVisitor();
		try {
			inputParameter.accept(resolver);
			return resolver.getType();
		}
		finally {
			resolver.dispose();
		}
	}

	/**
	 * Returns the parent context if the current context is not the root context.
	 *
	 * @return The parent context or <code>null</code> if the current context is the root
	 */
	JPQLQueryContext getParent() {
		return currentContext.parent;
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name. The scope of
	 * the search is local to the current query or subquery.
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getQueryExpression(String variableName) {
		return currentContext.getQueryExpressionImp(variableName);
	}

	/**
	 * Retrieves the cached {@link Expression} associated with the given variable name.
	 *
	 * @param variableName The variable name for which its associated {@link Expression} is requested
	 * @return The cached {@link Expression} associated with the given variable name or <code>null</code>
	 * if none was cached.
	 */
	Expression getQueryExpressionImp(String variableName) {
		return (expressions == null) ? null : expressions.get(variableName);
	}

	/**
	 * Returns the EclipseLink {@link AbstractSession} that this query will execute against, which
	 * gives access to the JPA artifacts.
	 *
	 * @return The current EclipseLink session
	 */
	AbstractSession getSession() {
		return (parent != null) ? parent.getSession() : session;
	}

	/**
	 * Returns the Java class of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} for which its type will be calculated
	 * @return Either the Java class that was resolved or the <code>null</code> if it could not be resolved
	 */
	Class<?> getType(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		return typeResolver().resolve(expression);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	Class<?> getType(String typeName) {
		return loadTypeImp(typeName);
	}

	private Map<String, Class<?>> getTypes() {

		if (parent != null) {
			return parent.getTypes();
		}

		if (types == null) {
			types = new HashMap<String, Class<?>>();
		}

		return types;
	}

	/**
	 * Returns the identification variables that was used in the current query.
	 *
	 * @return The identification variables that were used throughout the current query at the
	 * exception of their declaration
	 */
	Set<String> getUsedIdentificationVariables() {
		return currentContext.getUsedIdentificationVariablesImp();
	}

	/**
	 * Returns the identification variables that are registered in this context.
	 *
	 * @return The identification variables that were used throughout the current query at the
	 * exception of their declaration
	 */
	Set<String> getUsedIdentificationVariablesImp() {
		if (usedIdentificationVariables == null) {
			return Collections.emptySet();
		}
		return usedIdentificationVariables;
	}

	/**
	 * Determines whether the given identification variable is defining a join or a collection member
	 * declaration expressions.
	 *
	 * @param variableName The identification variable to check for what it maps
	 * @return <code>true</code> if the given identification variable maps a collection-valued field
	 * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> if it's not
	 * defined or it's mapping an abstract schema name
	 */
	boolean isCollectionIdentificationVariable(String variableName) {
		return getDeclarationResolver().isCollectionIdentificationVariable(variableName);
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
		return currentContext.isIdentificationVariableUsedImp(variableName);
	}

	private boolean isIdentificationVariableUsedImp(String variableName) {
		boolean result = (usedIdentificationVariables != null) && usedIdentificationVariables.contains(variableName);
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
		return getDeclarationResolverImp().isRangeIdentificationVariable(variableName);
	}

	/**
	 * Determines if the given variable is a result variable.
	 *
	 * @param variableName The variable to check if it's a result variable
	 * @return <code>true</code> if the given variable is defined as a result variable;
	 * <code>false</code> otherwise
	 */
	boolean isResultVariable(String variableName) {
		return getDeclarationResolverImp().isResultVariable(variableName);
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
	String literal(org.eclipse.persistence.jpa.jpql.parser.Expression expression, LiteralType type) {

		LiteralVisitor visitor = literalVisitor();

		try {
			visitor.setType(type);
			expression.accept(visitor);

			// Make sure the identification variable is capitalized and unique since
			// String.equals() is used rather than String.equalsIgnoreCase()
			if (visitor.literal != ExpressionTools.EMPTY_STRING &&
			   (type == LiteralType.IDENTIFICATION_VARIABLE ||
			    type == LiteralType.RESULT_VARIABLE||
			    type == LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE)) {

				return visitor.literal.toUpperCase().intern();
			}

			return visitor.literal;
		}
		finally {
			visitor.literal = ExpressionTools.EMPTY_STRING;
		}
	}

	private LiteralVisitor literalVisitor() {

		if (parent != null) {
			return parent.literalVisitor();
		}

		if (literalVisitor == null) {
			literalVisitor = new EclipseLinkLiteralVisitor();
		}

		return literalVisitor;
	}

	private Class<?> loadInnerType(String typeName) {

		int index = typeName.lastIndexOf(".");

		if (index == -1) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(typeName.substring(0, index));
		sb.append("$");
		sb.append(typeName.substring(index + 1, typeName.length()));
		typeName = sb.toString();

		return loadTypeImp(typeName);
	}

	private Class<?> loadTypeImp(String typeName) {

		Map<String, Class<?>> types = getTypes();
		Class<?> type = types.get(typeName);

		// The type was already cached, simply return it
		if (type != null) {
			return type;
		}

		// Attempt to load the Java type
		Class<?> javaType = attemptLoadType(typeName);

		// A Java type exists, return it
		if (javaType != null) {
			types.put(javaType.getName(), javaType);
			return javaType;
		}

		// Now try with a possible inner enum type
		return loadInnerType(typeName);
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
					return (T) AccessController.doPrivileged(
						new PrivilegedInvokeConstructor(constructor, parameters)
					);
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
	 * populated, {@link #disposeSubqueryContext()} has to be called.
	 *
	 * @param currentQuery The parsed tree representation of the subquery that will become the
	 * current {@link DatabaseQuery}
	 * @param query The {@link ReportQuery} for the subquery
	 * @see #disposeSubqueryContext()
	 */
	void newSubQueryContext(org.eclipse.persistence.jpa.jpql.parser.Expression currentQuery,
	                        ReportQuery query) {

		currentContext = new JPQLQueryContext(currentContext, currentQuery, query);
	}

	private ParameterTypeVisitor parameterTypeVisitor() {

		if (parent != null) {
			return parent.parameterTypeVisitor();
		}

		if (parameterTypeVisitor == null) {
			parameterTypeVisitor = new ParameterTypeVisitor(this);
		}

		return parameterTypeVisitor;
	}

	private JPQLExpression parse(String jpqlQuery) {
		return new JPQLExpression(jpqlQuery, jpqlGrammar);
	}

	/**
	 * Resolves the given {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} and
	 * returns the corresponding descriptor. Usually this method is used when retrieving the
	 * descriptor for an identification variable or a path expression. In the case of a range
	 * variable declaration, the descriptor of the entity is returned. In the case of a path
	 * expression, the mapping reference descriptor will be returned.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
	 */
	ClassDescriptor resolveDescriptor(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		return typeResolver().resolveDescriptor(expression);
	}

	/**
	 * Resolves the given {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} and
	 * returns the {@link DatabaseMapping}.
	 *
	 * @param expression The {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to
	 * resolve by traversing its path expression
	 * @return The {@link DatabaseMapping} that is representing the last path or <code>null</code> if
	 * the path expression could not be resolved
	 */
	DatabaseMapping resolveMapping(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		return typeResolver().resolveMapping(expression);
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
	Object resolveMappingObject(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
		return typeResolver().resolveMappingObject(expression);
	}

	/**
	 * Sets the query to populate.
	 *
	 * @param query The {@link DatabaseQuery} that was created before populating it
	 */
	void setDatabasQuery(DatabaseQuery query) {
		this.query = query;
	}

	TypeResolver typeResolver() {

		if (parent != null) {
			return parent.typeResolver();
		}

		if (typeResolver == null) {
			typeResolver = new TypeResolver(this);
		}

		return typeResolver;
	}
}