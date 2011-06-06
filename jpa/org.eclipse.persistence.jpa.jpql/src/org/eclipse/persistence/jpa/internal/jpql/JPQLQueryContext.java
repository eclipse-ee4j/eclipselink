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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * This context is used to store information related to the JPQL query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
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
	protected JPQLQueryContext currentContext;

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
	 * This visitor is responsible to find the {@link InputParameter} with a specific named parameter
	 * or positional parameter.
	 */
	private InputParameterVisitor inputParameterVisitor;

	/**
	 * The parsed tree representation of the JPQL query.
	 */
	private JPQLExpression jpqlExpression;

	/**
	 * This visitor is used to retrieve a variable name from various type of
	 * {@link org.eclipse.persistence.jpa.query.parser.Expression JPQL Expression}.
	 */
	private LiteralVisitor literalVisitor;

	/**
	 * This visitor is responsible to calculate the closest type of any input parameter.
	 */
	private ParameterTypeVisitor parameterTypeVisitor;

	/**
	 * When this context is a sub-context used for a subquery, then this is the context for the
	 * parent query.
	 */
	protected JPQLQueryContext parent;

	/**
	 * The external form of the JPQL query being manipulated.
	 */
	private IQuery query;

	/**
	 * This visitor is responsible to retrieve the {@link Expression} that is the beginning of a
	 * query. For a subquery, it will retrieve {@link SimpleSelectStatement} and for a top-level
	 * query, it will retrieve {@link JPQLExpression}. The search goes through the parent hierarchy.
	 */
	private QueryExpressionVisitor queryExpressionVisitor;

	/**
	 * This visitor creates a {@link Resolver} that gives information about the visited {@link
	 * Expression}. The actual {@link Resolver} will calculate the proper {@link IType} as well.
	 */
	private ResolverBuilder resolverBuilder;

	/**
	 * Internal flag used to determine if the declaration portion of the query was visited.
	 */
	private boolean traversed;

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
	protected JPQLQueryContext(JPQLQueryContext parent, Expression currentQuery) {
		this();
		store(parent, currentQuery);
	}

	private DeclarationResolver buildDeclarationResolver() {
		DeclarationResolver parentResolver = (parent != null) ? parent.getDeclarationResolverImp() : null;
		return new DeclarationResolver(parentResolver, this);
	}

	/**
	 * Converts the given {@link Declaration} from being set as a range variable declaration to
	 * a path expression declaration.
	 * <p>
	 * In this query "<code>UPDATE Employee SET firstName = 'MODIFIED' WHERE (SELECT COUNT(m) FROM
	 * managedEmployees m) > 0</code>" <em>managedEmployees</em> is an unqualified collection-valued
	 * path expression (<code>employee.managedEmployees</code>).
	 *
	 * @param declaration The {@link Declaration} that was parsed to range over an abstract schema
	 * name but is actually ranging over a path expression
	 */
	protected void convertUnqualifiedDeclaration(Declaration declaration) {

		if (parent != null) {

			// Retrieve the range identification variable from the parent declaration
			Declaration parentDeclaration = parent.getDeclarationResolverImp().getDeclarations().get(0);
			String outerVariableName = parentDeclaration.getVariableName();

			// Qualify the range expression to be fully qualified
			getDeclarationResolverImp().convertUnqualifiedDeclaration(declaration, outerVariableName);
		}
	}

	/**
	 * Disposes the internal data.
	 */
	public void dispose() {

		query          = null;
		traversed      = false;
		currentQuery   = null;
		currentContext = this;
		jpqlExpression = null;

		contexts.clear();

		if (declarationResolver != null) {
			declarationResolver.dispose();
		}
	}

	/**
	 * Disposes this context, which is the current context being used for a subquery. Once it's
	 * disposed, any information retrieved will be for the subquery's parent query.
	 */
	public void disposeSubqueryContext() {
		currentContext = currentContext.parent;
	}

	/**
	 * Retrieves all the {@link InputParameter InputParameters} with the given parameter name.
	 *
	 * @param parameterName The parameter used to find the {@link InputParameter InputParameters}
	 * with the same value
	 * @return Either the {@link InputParameter InputParameters} that has the given parameter or an
	 * empty collection
	 */
	protected Collection<InputParameter> findInputParameters(String parameterName) {

		InputParameterVisitor visitor = inputParameterVisitor();

		try {
			visitor.parameterName   = parameterName;
			visitor.inputParameters = new ArrayList<InputParameter>();

			jpqlExpression.accept(visitor);

			return visitor.inputParameters;
		}
		finally {
			visitor.parameterName   = null;
			visitor.inputParameters = null;
		}
	}

	/**
	 * Returns the current {@link JPQLQueryContext}, i.e. the context of the query being manipulated,
	 * which can either be the top-level query or a subquery.
	 *
	 * @return The active context
	 */
	protected JPQLQueryContext getCurrentContext() {
		return currentContext;
	}

	/**
	 * Returns the current {@link Expression} being manipulated, which is either the top-level query
	 * or a subquery.
	 *
	 * @return Either the top-level query or a subquery
	 */
	public Expression getCurrentQuery() {
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
	protected DeclarationResolver getDeclarationResolver() {
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
	protected DeclarationResolver getDeclarationResolver(Expression expression) {

		// Retrieve the expression for the query (either the top-level query or subquery)
		// owning the given Expression
		expression = queryExpression(expression);

		// Retrieve the cached JPQLQueryContext
		JPQLQueryContext context = contexts.get(expression);

		if (context != null) {
			return context.getDeclarationResolverImp();
		}

		// The JPQLQueryContext has not been created yet,
		// create the parent JPQLQueryContexts first
		getDeclarationResolver(expression.getParent());

		// Create the JPQLQueryContext and DeclarationResolver for the subquery
		newSubqueryContext(expression);
		return currentContext.getDeclarationResolverImp();
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
	 * Returns the ordered list of {@link Declaration Declarations}.
	 *
	 * @return The {@link Declaration Declarations} of the current query that was parsed
	 */
	public List<Declaration> getDeclarations() {
		return getDeclarationResolver().getDeclarations();
	}

	/**
	 * Returns the {@link IType} representing the possible given enum type. If the type name
	 *
	 * @param enumTypeName The fully qualified enum type with the constant
	 * @return The external form for the given Enum type
	 */
	public IType getEnumType(String enumTypeName) {
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
	public Collection<JoinFetch> getJoinFetches(String variableName) {
		return getDeclarationResolver().getJoinFetches(variableName);
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	public JPQLExpression getJPQLExpression() {
		return jpqlExpression;
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return The string representation of the JPQL query
	 */
	public String getJPQLQuery() {
		return query.getExpression();
	}

	/**
	 * Returns the {@link IMapping} for the field represented by the given {@link Expession}.
	 *
	 * @param expression The {@link Expression} representing a state field path expression or a
	 * collection-valued path expression
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	public IMapping getMapping(Expression expression) {
		return getResolver(expression).getMapping();
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
	public IType getParameterType(InputParameter inputParameter) {
		ParameterTypeVisitor visitor = parameterTypeVisitor();
		try {
			inputParameter.accept(visitor);
			return visitor.getType();
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	public IManagedTypeProvider getProvider() {
		return query.getProvider();
	}

	/**
	 * Returns the external form of the JPQL query.
	 *
	 * @return The external form of the JPQL query
	 */
	public IQuery getQuery() {
		return query;
	}

	/**
	 * Creates or retrieved the cached {@link Resolver} for the given {@link Expression}. The
	 * {@link Resolver} can return the {@link IType} and {@link ITypeDeclaration} of the {@link
	 * Expression} and either the {@link IManagedType} or the {@link IMapping}.
	 *
	 * @param expression The {@link Expression} for which its {@link Resolver} will be retrieved
	 * @return {@link Resolver} for the given {@link Expression}
	 */
	public Resolver getResolver(Expression expression) {
		ResolverBuilder visitor = resolverBuilder();
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
	public Resolver getResolver(String variableName) {
		return getDeclarationResolver().getResolver(variableName);
	}

	/**
	 * Returns the variables that got defined in the select expression. This only applies to JPQL
	 * queries built for JPA 2.0.
	 *
	 * @return The variables identifying the select expressions, if any was defined or an empty set
	 * if none were defined
	 */
	public Set<String> getResultVariables() {
		return getDeclarationResolver().getResultVariables();
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	public IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Returns the {@link IType} of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} for which its type will be calculated
	 * @return Either the {@link IType} that was resolved by this {@link Resolver} or the
	 * {@link IType} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public IType getType(Expression expression) {
		return getResolver(expression).getType();
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	public IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this {@link Resolver}.
	 *
	 * @param expression The {@link Expression} for which its type declaration will be calculated
	 * @return Either the {@link ITypeDeclaration} that was resolved by this {@link Resolver} or the
	 * {@link ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public ITypeDeclaration getTypeDeclaration(Expression expression) {
		return getResolver(expression).getTypeDeclaration();
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	public TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	public ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Determines whether the JPQL expression has <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
	 * expressions; <code>false</code> otherwise
	 */
	public boolean hasJoins() {
		return getDeclarationResolver().hasJoins();
	}

	/**
	 * Initializes this {@link JPQLQueryContext}.
	 */
	protected void initialize() {
		this.currentContext = this;
		this.contexts = new HashMap<Expression, JPQLQueryContext>();
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

	private InputParameterVisitor inputParameterVisitor() {
		if (inputParameterVisitor == null) {
			inputParameterVisitor = new InputParameterVisitor();
		}
		return inputParameterVisitor;
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
	public boolean isCollectionVariableName(String variableName) {
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
	public boolean isRangeIdentificationVariable(String variableName) {
		return getDeclarationResolver().isRangeIdentificationVariable(variableName);
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
	public String literal(Expression expression, LiteralType type) {
		LiteralVisitor visitor = literalVisitor();
		try {
			visitor.setType(type);
			expression.accept(visitor);
			return visitor.literal;
		}
		finally {
			visitor.literal = ExpressionTools.EMPTY_STRING;
		}
	}

	private LiteralVisitor literalVisitor() {
		if (literalVisitor == null) {
			literalVisitor = new LiteralVisitor();
		}
		return literalVisitor;
	}

	/**
	 * Changes the state of this context to use the given subquery.
	 *
	 * @param currentQuery The parsed tree representation of the subquery that will become the
	 * current query
	 * @see #disposeSubQueryContext()
	 */
	public void newSubqueryContext(Expression currentQuery) {

		JPQLQueryContext context = contexts.get(currentQuery);

		if (context != null) {
			currentContext = context;
		}
		else {
			currentContext = new JPQLQueryContext(currentContext, currentQuery);
		}
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

	private ResolverBuilder resolverBuilder() {
		if (resolverBuilder == null) {
			resolverBuilder = new ResolverBuilder(this);
		}
		return resolverBuilder;
	}

	/**
	 * Sets the parsed tree representation of the JPQL query. If the expression was parsed outside of
	 * the scope of this context, then this method has to be invoked before {@link #setQuery(IQuery)}
	 * because the JPQL query is automatically parsed by that method.
	 *
	 * @param jpqlExpression The parsed representation of the JPQL query to manipulate
	 * @see #setQuery(IQuery)
	 */
	public void setJPQLExpression(JPQLExpression jpqlExpression) {
		this.jpqlExpression = jpqlExpression;
	}

	/**
	 * Sets the external form of the JPQL query, which will be parsed and information will be
	 * extracted for later access.
	 *
	 * @param query The external form of the JPQL query
	 * @see #setJPQLExpression(JPQLExpression)
	 */
	public void setQuery(IQuery query) {
		this.query = query;
		initializeRoot();
	}

	/**
	 * Stores the information contained in the given parent into this one.
	 *
	 * @param parent The parent context, which is the context of the parent query
	 * @param currentQuery The subquery becoming the current query
	 */
	protected void store(JPQLQueryContext parent, Expression currentQuery) {

		this.parent = parent;

		// Copy the information from the parent to this one, only a single instance is required
		this.currentQuery           = currentQuery;
		this.query                  = parent.query;
		this.contexts               = parent.contexts;
		this.jpqlExpression         = parent.jpqlExpression;
		this.literalVisitor         = parent.literalVisitor;
		this.resolverBuilder        = parent.resolverBuilder;
		this.parameterTypeVisitor   = parent.parameterTypeVisitor;
		this.queryExpressionVisitor = parent.queryExpressionVisitor;

		// Cache the context
		this.contexts.put(currentQuery, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Current JPQL query=");
		sb.append(currentContext.currentQuery.toParsedText());
		return sb.toString();
	}

	/**
	 * This visitor is responsible to find the {@link InputParameter InputParameters} with a certain
	 * parameter name.
	 */
	private class InputParameterVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 * The collection of {@link InputParameter InputParameters} that was retrieved by traversing the
		 * parsed tree.
		 */
		Collection<InputParameter> inputParameters;

		/**
		 * The name of the input parameter to search.
		 */
		String parameterName;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {
			if (parameterName.equals(expression.getParameter())) {
				inputParameters.add(expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			newSubqueryContext(expression);
			try {
				super.visit(expression);
			}
			finally {
				disposeSubqueryContext();
			}
		}
	}

	/**
	 * This visitor is responsible to retrieve the {@link Expression} that is the beginning of a
	 * query. For a subquery, it will retrieve {@link SimpleSelectStatement} and for a top-level
	 * query, it will retrieve {@link JPQLExpression}. The search goes through the parent hierarchy.
	 */
	private static class QueryExpressionVisitor extends AbstractTraverseParentVisitor {

		/**
		 * The {@link Expression} that is the beginning of a query.
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