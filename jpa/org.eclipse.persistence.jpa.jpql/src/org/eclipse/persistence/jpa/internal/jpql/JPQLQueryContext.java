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
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * This context caches information related to the JPQL query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public class JPQLQueryContext {

	/**
	 * This map caches the {@link JPQLQueryContext contexts} in order to keep them in memory and for
	 * fast access to the information of any query (top-level query and subqueries).
	 */
	private Map<Expression, JPQLQueryContext> contexts;

	/**
	 * The current {@link JPQLQueryContext} is the context used for the current query or subquery.
	 * If the current context is not the global context, then its parent is non <code>null</code>.
	 */
	private JPQLQueryContext currentContext;

	/**
	 * The parsed {@link Expression JPQL Expression} currently visited.
	 */
	private Expression currentQuery;

	/**
	 * The resolver of the current query's declaration. For a <b>SELECT</b> query, it contains the
	 * information defined in the <b>FROM</b> clause. For <b>DELETE</b> and <b>UPDATE</b> queries,
	 * it contains a single range declaration variable.
	 */
	private DeclarationResolver declarationResolver;

	/**
	 * The cached {@link Expression Expressions} mapped by their variable names.
	 */
	private Map<String, Expression> expressions;

	/**
	 * The input parameter name mapped to its type. The input parameter name starts with the
	 * positional parameter ('?' or ':').
	 */
	private Map<String, IType> inputParameters;

	/**
	 * The parsed tree representation of the JPQL query.
	 */
	private JPQLExpression jpqlExpression;

	/**
	 *
	 */
	private ParameterTypeVisitor parameterTypeVisitor;

	/**
	 * When this context is a sub-context used for a subquery, then this is the context for the
	 * parent query.
	 */
	private JPQLQueryContext parent;

	/**
	 * The external form of the JPQL query being manipulated.
	 */
	private IQuery query;

	/**
	 *
	 */
	private QueryExpressionVisitor queryExpressionVisitor;

	/**
	 * Internal flag used to determine if the declaration portion of the query was visited.
	 */
	private boolean traversed;

	/**
	 * This visitor can determine the type of a JPQL expression.
	 */
	private TypeVisitor typeVisitor;

	/**
	 * This visitor is used to retrieve a variable name from various type of
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}.
	 */
	private VariableNameVisitor variableNameVisitor;

	/**
	 * Creates a new <code>JPQLQueryContext</code>.
	 */
	public JPQLQueryContext() {
		super();
		initialize();
	}

	/**
	 * Creates a new sub-<code>JPQLQueryContext</code>.
	 *
	 * @param parent The parent context
	 * @param currentQuery The parsed tree representation of the subquery
	 * @param query The {@link ReportQuery} that will be populated by visiting the parsed tree
	 * representation of the subquery
	 */
	private JPQLQueryContext(JPQLQueryContext parent, Expression currentQuery) {
		this();
		store(parent, currentQuery);
	}

	/**
	 * Caches the given input parameter name with its calculated type.
	 *
	 * @param parameterName The name of the input parameter
	 * @param type The calculated type based on its surrounding, which is never <code>null</code>
	 */
	public final void addInputParameter(String parameterName, IType type) {
		inputParameters.put(parameterName, type);
	}

	private DeclarationResolver buildDeclarationResolver() {
		DeclarationResolver parentResolver = (parent != null) ? parent.getDeclarationResolverImp() : null;
		return new DeclarationResolver(parentResolver, this);
	}

	/**
	 * Disposes of the internal data.
	 */
	public void dispose() {

		query = null;
		traversed = false;
		currentQuery = null;
		currentContext = this;
		jpqlExpression = null;

		contexts.clear();
		expressions.clear();
		inputParameters.clear();

		if (declarationResolver != null) {
			declarationResolver.dispose();
		}
	}

	/**
	 * Disposes this context, which is the current context being used for a subquery. Once it's
	 * disposed, any information retrieved will be for the subquery's parent query.
	 */
	public final void disposeSubQueryContext() {
		currentContext = currentContext.parent;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public final Expression getCurrentQuery() {
		return currentQuery;
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
	public final DeclarationResolver getDeclarationResolver() {
		return currentContext.getDeclarationResolverImp();
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
	public final DeclarationResolver getDeclarationResolver(Expression expression) {

		// Retrieve the expression of the root query (either the top-level query or subquery)
		expression = queryExpression(expression);

		JPQLQueryContext context = contexts.get(expression);

		if (context != null) {
			return context.getDeclarationResolverImp();
		}

		try {
			// Create the context for the subquery and retrieve its DeclarationResolver
			newSubQueryContext(expression);
			return getDeclarationResolver();
		}
		finally {
			// Make sure the currentContext is not modified
			currentContext = this;
		}
	}

	private DeclarationResolver getDeclarationResolverImp() {

		if (declarationResolver == null) {
			declarationResolver = buildDeclarationResolver();
		}

		// Only traverse the declaration once
		if (!traversed) {
			traversed = true;
			declarationResolver.populate(currentQuery);
		}

		return declarationResolver;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	List<Declaration> getDeclarations() {
		return getDeclarationResolver().getDeclarations();
	}

	/**
	 * Returns the {@link IType} representing the possible given enum type. If the type name
	 *
	 * @param enumTypeName The fully qualified enum type with the constant
	 * @return The external form for the given Enum type
	 */
	public final IType getEnumType(String enumTypeName) {
		return getTypeRepository().getEnumType(enumTypeName);
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
	public final Collection<JoinFetch> getJoinFetches(String variableName) {
		return getDeclarationResolver().getJoinFetches(variableName);
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	public final JPQLExpression getJPQLExpression() {
		return jpqlExpression;
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return The string representation of the JPQL query
	 */
	public final String getJPQLQuery() {
		return query.getExpression();
	}

	/**
	 * Returns
	 *
	 * @param expression
	 * @return
	 */
	public final IMapping getMapping(Expression expression) {
		return getResolver(expression).getMapping();
	}

	/**
	 * Returns
	 *
	 * @param inputParameter
	 * @return
	 */
	public final IType getParameterType(InputParameter inputParameter) {
		ParameterTypeVisitor visitor = parameterTypeVisitor();
		try {
			inputParameter.accept(visitor);
			return visitor.getType();
		}
		finally {
			visitor.dispose();
		}
	}

	public final IManagedTypeProvider getProvider() {
		return query.getProvider();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public final IQuery getQuery() {
		return query;
	}

	/**
	 * Creates the {@link Resolver} for
	 *
	 * @param expression
	 * @return
	 */
	public final Resolver getResolver(Expression expression) {
		TypeVisitor visitor = typeVisitor();
		try {
			expression.accept(visitor);
			return visitor.resolver;
		}
		finally {
			visitor.resolver = null;
		}
	}

	/**
	 * Retrieves the {@link Resolver} mapped with the given identification variable. If the
	 * identification is not defined in the declaration traversed by this resolver, than the search
	 * will traverse the parent hierarchy.
	 *
	 * @param variableName The identification variable that maps a {@link Resolver}
	 * @return The {@link Resolver} mapped with the given identification variable
	 */
	public final Resolver getResolver(String variableName) {
		return getDeclarationResolver().getResolver(variableName);
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public final Set<String> getResultVariables() {
		return getDeclarationResolver().getResultVariables();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public final IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves
	 *
	 * @param expression
	 * @return
	 */
	public final IType getType(Expression expression) {
		return getResolver(expression).getType();
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	public final IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * Retrieves
	 *
	 * @param expression
	 * @return
	 */
	public final ITypeDeclaration getTypeDeclaration(Expression expression) {
		return getResolver(expression).getTypeDeclaration();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public final TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	public final ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Determines whether the JPQL expression has <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
	 * expressions; <code>false</code> otherwise
	 */
	public final boolean hasJoins() {
		return getDeclarationResolver().hasJoins();
	}

	/**
	 * Initializes this {@link QueryBuilderContext}.
	 */
	protected void initialize() {
		this.currentContext  = this;
		this.contexts        = new HashMap<Expression, JPQLQueryContext>();
		this.expressions     = new HashMap<String, Expression>();
		this.inputParameters = new LinkedHashMap<String, IType>();
	}

	private void initializeRoot() {

		if (jpqlExpression == null) {
			jpqlExpression = new JPQLExpression(
				query.getExpression(),
				IJPAVersion.DEFAULT_VERSION,
				true
			);
		}

		currentQuery = jpqlExpression;
		contexts.put(currentQuery, this);
	}

	/**
	 * Returns the set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type.
	 *
	 * @return The set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type
	 */
	public final Set<Map.Entry<String, IType>> inputParameters() {
		return inputParameters.entrySet();
	}

	/**
	 * Determines whether the given identification variable is defining a <b>JOIN</b> or <code>IN</code>
	 * expressions for a collection-valued field.
	 *
	 * @param variableName The identification variable to check for what it maps
	 * @return <code>true</code> if the given identification variable maps a collection-valued field
	 * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> if it's not
	 * defined or it's mapping an abstract schema name
	 */
	public final boolean isCollectionVariableName(String variableName) {
		return getDeclarationResolver().isCollectionIdentificationVariable(variableName);
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
	public final boolean isRangeIdentificationVariable(String variableName) {
		return getDeclarationResolver().isRangeIdentificationVariable(variableName);
	}

	/**
	 * Changes the state of this context to use the given subquery.
	 *
	 * @param currentQuery The parsed tree representation of the subquery that will become the
	 * current query
	 * @see #disposeSubQueryContext()
	 */
	public final void newSubQueryContext(Expression currentQuery) {
		currentContext = new JPQLQueryContext(currentContext, currentQuery);
	}

	private ParameterTypeVisitor parameterTypeVisitor() {
		if (parameterTypeVisitor == null) {
			parameterTypeVisitor = new ParameterTypeVisitor(this);
		}
		return parameterTypeVisitor;
	}

	private Expression queryExpression(Expression expression) {
		QueryExpressionVisitor visitor = queryExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private QueryExpressionVisitor queryExpressionVisitor() {
		if (queryExpressionVisitor == null) {
			queryExpressionVisitor = new QueryExpressionVisitor();
		}
		return queryExpressionVisitor;
	}

	void setJPQLExpression(JPQLExpression jpqlExpression) {
		this.jpqlExpression = jpqlExpression;
	}

	/**
	 * Sets the external form of the JPQL query, which will be parsed and information will be
	 * extracted for later access.
	 *
	 * @param query The external form of the JPQL query
	 */
	public final void setQuery(IQuery query) {
		this.query = query;
		initializeRoot();
	}

	/**
	 * Stores the information contained in the given parent into this one.
	 *
	 * @param parent The parent context, which is the context of the parent query
	 * @param currentQuery The subquery becoming the current query
	 */
	public void store(JPQLQueryContext parent, Expression currentQuery) {

		this.parent = parent;

		// Copy the information from the parent to this one, only a single instance is required
		this.currentQuery         = currentQuery;
		this.query                = parent.query;
		this.contexts             = parent.contexts;
		this.typeVisitor          = parent.typeVisitor;
		this.jpqlExpression       = parent.jpqlExpression;
		this.variableNameVisitor  = parent.variableNameVisitor;
		this.parameterTypeVisitor = parent.parameterTypeVisitor;

		// Cache the context
		this.contexts.put(currentQuery, this);
	}

	/**
	 * Returns a visitor that can calculate the type of any {@link Expression}.
	 *
	 * @return A visitor
	 */
	private TypeVisitor typeVisitor() {
		if (typeVisitor == null) {
			typeVisitor = new TypeVisitor(this);
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
	public final String variableName(Expression expression, VariableNameType type) {
		VariableNameVisitor visitor = variableNameVisitor();
		try {
			visitor.setType(type);
			expression.accept(visitor);
			return visitor.variableName;
		}
		finally {
			visitor.variableName = null;
		}
	}

	private VariableNameVisitor variableNameVisitor() {
		if (variableNameVisitor == null) {
			variableNameVisitor = new VariableNameVisitor();
		}
		return variableNameVisitor;
	}

	private static class QueryExpressionVisitor extends AbstractTraverseParentVisitor {

		/**
		 *
		 */
		Expression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			this.expression = expression;
		}
	}
}