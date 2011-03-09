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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.JPQLQueryHelper;

/**
 * This context caches information related to the JPQL query.
 * <p>
 * NOTE: UNDER DEVELOPMENT
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("unused")
public class JPQLQueryContext {

	/**
	 * The current {@link JPQLQueryContext} is the context used for the current query or subquery.
	 * If the current context is not the global context, then its parent is non <code>null</code>.
	 */
	private JPQLQueryContext currentContext;

	/**
	 * The parsed {@link Expression JPQL Expression} being visited.
	 */
	private Expression currentQuery;

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
	 * When this context is a sub-context used for a subquery, then this is the context for the
	 * parent query.
	 */
	private JPQLQueryContext parent;

	/**
	 * This helper is used to retrieve to access the application's metadata information and to
	 * calculate the input parameter.
	 */
	private JPQLQueryHelper<?> queryHelper;

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
	protected JPQLQueryContext() {
		super();
		initialize();
	}

	/**
	 * Disposes of the internal data.
	 */
	protected void dispose() {

		jpqlQuery = null;
		traversed = false;
		queryHelper = null;
		typeVisitor = null;
		currentQuery = null;
		currentContext = this;

		expressions.clear();
		inputParameters.clear();
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	public final JPQLExpression getJPQLExpression() {
		return currentQuery.getRoot();
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return The string representation of the JPQL query
	 */
	public final String getJPQLQuery() {
		return jpqlQuery;
	}

	/**
	 * Initializes this {@link QueryBuilderContext}.
	 */
	protected void initialize() {
		this.currentContext  = this;
		this.expressions     = new HashMap<String, Expression>();
		this.inputParameters = new LinkedHashMap<String, Class<?>>();
	}

	/**
	 * Returns the set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type.
	 *
	 * @return The set of {@link Map.Entry Map.Entry} of the input parameters mapped to its type
	 */
	protected final Set<Map.Entry<String, Class<?>>> inputParameters() {
		return inputParameters.entrySet();
	}

	/**
	 * Sets
	 *
	 * @param currentQuery
	 */
	protected final void setCurrentQuery(Expression currentQuery) {
		this.currentQuery = currentQuery;
	}

	protected final TypeVisitor typeVisitor() {
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
	public final String variableName(Expression expression, VariableNameType type) {
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