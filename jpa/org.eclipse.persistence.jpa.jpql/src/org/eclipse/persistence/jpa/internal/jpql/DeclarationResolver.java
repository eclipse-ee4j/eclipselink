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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to visit the current query (which is either the top-level
 * query or a subquery) and gathers the information from the declaration clause.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class DeclarationResolver extends Resolver {

	/**
	 *
	 */
	private List<Declaration> declarations;

	/**
	 * The context used to query information about the query.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * The identification variable names mapped to their resolvers.
	 */
	private Map<String, Resolver> resolvers;

	private Set<String> resultVariables;

	/**
	 * This visitor is responsible to visit the current query's declaration and populate this
	 * resolver with the information.
	 */
	private Visitor visitor;

	/**
	 * Creates a new <code>DeclarationResolver</code>.
	 *
	 * @param parent The parent resolver if this is used for a subquery or null if it's used for the
	 * top-level query
	 * @param queryContext The context used to query information about the query
	 */
	DeclarationResolver(DeclarationResolver parent, JPQLQueryContext queryContext) {
		super(parent);
		initialize(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IType buildType() {
		return getTypeHelper().unknownType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		return getTypeHelper().unknownTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void checkParent(Resolver parent) {
		// Don't do anything, this is the root
	}

	/**
	 * Disposes this {@link Resolver}.
	 */
	protected void dispose() {
		resolvers      .clear();
		declarations   .clear();
		resultVariables.clear();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	List<Declaration> getDeclarations() {
		return Collections.unmodifiableList(declarations);
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
	Collection<JoinFetch> getJoinFetches(String variableName) {
		Collection<JoinFetch> joinFetches = getJoinFetchesImp(variableName);
		if (joinFetches.isEmpty() && (parent != null)) {
			joinFetches = getParent().getJoinFetchesImp(variableName);
		}
		return joinFetches;
	}

	private Collection<JoinFetch> getJoinFetchesImp(String variableName) {

		for (Declaration declaration : declarations) {
			if (declaration.variableName.equalsIgnoreCase(variableName)) {
				return declaration.getJoinFetches();
			}
		}

		return Collections.emptyList();
	}

	private DeclarationResolver getParent() {
		return (DeclarationResolver) parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IQuery getQuery() {
		return queryContext.getQuery();
	}

	/**
	 * Retrieves the {@link Resolver} mapped with the given identification variable. If the
	 * identification is not defined in the declaration traversed by this resolver, than the search
	 * will traverse the parent hierarchy.
	 *
	 * @param variableName The identification variable that maps a {@link Resolver}
	 * @return The {@link Resolver} mapped with the given identification variable
	 */
	Resolver getResolver(String variableName) {

		variableName = variableName.toUpperCase();
		Resolver resolver = getResolverImp(variableName);

		if ((resolver == null) && (parent != null)) {
			resolver = getParent().getResolver(variableName);
		}

		if (resolver == null) {
			resolver = new NullResolver(this);
			resolvers.put(variableName, resolver);
		}

		return resolver;
	}

	private Resolver getResolverImp(String variableName) {
		return resolvers.get(variableName);
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	Set<String> getResultVariables() {
		return Collections.unmodifiableSet(resultVariables);
	}

	/**
	 * Determines whether the JPQL expression has <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
	 * expressions; <code>false</code> otherwise
	 */
	boolean hasJoins() {
		for (Declaration declaration : declarations) {
			if (declaration.hasJoins()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initializes this <code>DeclarationResolver</code>.
	 *
	 * @param parent The parent resolver if this is used for a subquery or null if it's used for the
	 * top-level query
	 */
	protected void initialize(JPQLQueryContext queryContext) {
		this.queryContext    = queryContext;
		this.resolvers       = new HashMap<String, Resolver>();
		this.declarations    = new ArrayList<Declaration>();
		this.resultVariables = new HashSet<String>();
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
	boolean isCollectionIdentificationVariable(String variableName) {
		boolean result = isCollectionIdentificationVariableImp(variableName);
		if (!result && (parent != null)) {
			result = getParent().isCollectionIdentificationVariableImp(variableName);
		}
		return result;
	}

	private boolean isCollectionIdentificationVariableImp(String variableName) {

		for (Declaration declaration : declarations) {

			// Check for a collection member declaration
			if (!declaration.rangeDeclaration &&
			     declaration.variableName.equalsIgnoreCase(variableName)) {

				return true;
			}

			// Check the JOIN expressions
			for (String joinIdentificationVariable : declaration.getJoinIdentificationVariables()) {
				if (joinIdentificationVariable.equalsIgnoreCase(variableName)) {
					return true;
				}
			}
		}

		return false;
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
		boolean result = isRangeIdentificationVariableImp(variableName);
		if (!result && (parent != null)) {
			result = getParent().isRangeIdentificationVariableImp(variableName);
		}
		return result;
	}

	private boolean isRangeIdentificationVariableImp(String variableName) {

		for (Declaration declaration : declarations) {

			if (declaration.rangeDeclaration &&
			    declaration.variableName.equalsIgnoreCase(variableName)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Visit the current query (which is either the top-level query or a subquery) and gathers the
	 * information from the declaration clause.
	 *
	 * @param expression The {@link Expression} to visit in order to retrieve the information
	 * contained in the given query's declaration
	 */
	void populate(Expression expression) {
		expression.accept(visitor());
	}

	private String visitDeclaration(Expression expression, Expression identificationVariable) {

		// Visit the identification variable expression and retrieve the identification variable name
		String variableName = queryContext.variableName(
			identificationVariable,
			VariableNameType.IDENTIFICATION_VARIABLE
		);

		// If it's not empty, then we can create a Resolver
		if (ExpressionTools.stringIsNotEmpty(variableName)) {

			// Always make the identification variable be upper case since it's
			// case insensitive, the get will also use upper case
			String internalVariableName = variableName.toUpperCase();

			// Make sure the identification variable was not declared more than once,
			// this could cause issues when trying to resolve it
			if (!resolvers.containsKey(internalVariableName)) {

				// Resolve the expression and map the Resolver with the identification variable
				Resolver resolver = queryContext.getResolver(expression);
				resolvers.put(internalVariableName, resolver);
			}

			return variableName;
		}

		return ExpressionTools.EMPTY_STRING;
	}

	private ExpressionVisitor visitor() {
		if (visitor == null) {
			visitor = new Visitor();
		}
		return visitor;
	}

	public static class Declaration {

		/**
		 * The abstract schema name, which is the entity name.
		 */
		String abstractSchemaName;

		/**
		 * The actual expression that visited (either a {@link IdentificationVariableDeclaration} or
		 * a {@link CollectionMemberDeclaration}.
		 */
		Expression expression;

		/**
		 * The list of <b>JOIN FETCH</b> expressions that are declared in the same declaration than
		 * the range variable declaration.
		 */
		private List<JoinFetch> joinFetches;

		/**
		 * The list of <b>JOIN</b> expressions that are declared in the same declaration than the
		 * range variable declaration.
		 */
		private Map<Join, String> joins;

		/**
		 * Flag used to determine if this declaration is for a range variable declaration
		 * (<code>true</code>) or for a collection member declaration (<code>false</code>).
		 */
		boolean rangeDeclaration;

		/**
		 * The identification variable used to declare an abstract schema name or a collection-valued
		 * path expression.
		 */
		String variableName;

		/**
		 * Adds
		 *
		 * @param join
		 */
		public void addJoin(String variableName, Join join) {
			if (joins == null) {
				joins = new LinkedHashMap<Join, String>();
			}
			joins.put(join, variableName);
		}

		/**
		 * Adds
		 *
		 * @param joinFetch
		 */
		public void addJoinFetch(JoinFetch joinFetch) {
			if (joinFetches == null) {
				joinFetches = new ArrayList<JoinFetch>();
			}
			joinFetches.add(joinFetch);
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public String getAbstractSchemaName() {
			return abstractSchemaName;
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public Expression getExpression() {
			return expression;
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public List<JoinFetch> getJoinFetches() {
			return (joinFetches != null) ? joinFetches : Collections.<JoinFetch>emptyList();
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public Set<String> getJoinIdentificationVariables() {

			Set<String> variables = new HashSet<String>();
			Set<String> upperCaseVariables = new HashSet<String>();

			// Add the non-empty join identification variables
			for (String joinVariable : (joins != null) ? joins.values() : Collections.<String>emptySet()) {
				// Make sure the same variable name but with different case is not added more than once
				if ((joinVariable.length() > 0) && !upperCaseVariables.contains(joinVariable.toUpperCase())) {
					variables.add(joinVariable);
				}
			}
			return  variables;
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public Set<Map.Entry<Join, String>> getJoinEntries() {
			if (joins != null) {
				return Collections.unmodifiableSet(joins.entrySet());
			}
			return Collections.<Map.Entry<Join, String>>emptySet();
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public Collection<Join> getJoins() {
			return (joins != null) ? joins.keySet() : Collections.<Join>emptyList();
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public String getVariableName() {
			return variableName;
		}

		/**
		 * Determines whether
		 *
		 * @return
		 */
		public boolean hasJoinFetches() {
			return (joinFetches != null) && !joinFetches.isEmpty();
		}

		/**
		 * Determines whether
		 *
		 * @return
		 */
		public boolean hasJoins() {
			return (joins != null) && !joins.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return expression.toParsedText();
		}
	}

	private class Visitor extends AbstractExpressionVisitor {

		/**
		 * The {@link Declaration} being populated.
		 */
		private Declaration currentDeclaration;

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

			Declaration declaration = new Declaration();
			declaration.expression = expression;
			declarations.add(declaration);

			String variableName = visitDeclaration(expression, expression.getIdentificationVariable());
			declaration.variableName = variableName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {

			Declaration declaration = new Declaration();
			declaration.expression = expression;
			declaration.rangeDeclaration = true;
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				currentDeclaration = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			expression.getDeleteClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			String resultVariable = expression.getText();
			if (ExpressionTools.stringIsNotEmpty(resultVariable)) {
				resultVariables.add(resultVariable);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {

			Declaration declaration = new Declaration();
			declaration.expression = expression;
			declaration.rangeDeclaration = true;
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
				expression.getJoins().accept(this);
			}
			finally {
				currentDeclaration = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			String variableName = visitDeclaration(expression, expression.getIdentificationVariable());
			currentDeclaration.addJoin(variableName, expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JoinFetch expression) {
			currentDeclaration.addJoinFetch(expression);
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
		public void visit(RangeVariableDeclaration expression) {

			String variableName = visitDeclaration(
				expression.getAbstractSchemaName(),
				expression.getIdentificationVariable()
			);

			String abstractSchemaName = queryContext.variableName(
				expression.getAbstractSchemaName(),
				VariableNameType.ABSTRACT_SCHEMA_NAME
			);

			currentDeclaration.variableName       = variableName;
			currentDeclaration.abstractSchemaName = abstractSchemaName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			expression.getResultVariable().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {
			expression.getFromClause().accept(this);
			expression.getSelectClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
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
			expression.getFromClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {

			Declaration declaration = new Declaration();
			declaration.expression = expression;
			declaration.rangeDeclaration = true;
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				currentDeclaration = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			expression.getUpdateClause().accept(this);
		}
	}
}