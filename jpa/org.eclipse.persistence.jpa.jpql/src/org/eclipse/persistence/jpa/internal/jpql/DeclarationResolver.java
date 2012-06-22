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

import java.util.AbstractMap.SimpleEntry;
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
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
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
 * query or a subquery) and gathers the information from the declaration clause. For a <b>SELECT</b>
 * or <b>DELETE</b> clause, the information will be retrieved from the <b>FROM</b> clause. For an
 * <code>UDPATE</code> clause, it will be retrieved from the unique identification range variable
 * declaration.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class DeclarationResolver extends Resolver {

	/**
	 * The {@link Declaration Declarations} of the current query that was parsed.
	 */
	private List<Declaration> declarations;

	/**
	 * This visitor is responsible to visit the current query's declaration and populate this
	 * resolver with the list of declarations.
	 */
	private DeclarationVisitor declarationVisitor;

	/**
	 * This visitor is responsible to convert the abstract schema name into a path expression.
	 */
	private QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor;

	/**
	 * The context used to query information about the query.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * The identification variable names mapped to their resolvers.
	 */
	private Map<String, Resolver> resolvers;

	/**
	 * The variables identifying the select expressions, if any was defined or an empty set if none
	 * were defined.
	 */
	private Map<IdentificationVariable, String> resultVariables;

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
	public void accept(ResolverVisitor visitor) {
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
	 * Converts the given {@link Declaration} from being set as a range variable declaration to
	 * a path expression declaration.
	 * <p>
	 * In this query "<code>UPDATE Employee SET firstName = 'MODIFIED' WHERE (SELECT COUNT(m) FROM
	 * managedEmployees m) > 0</code>" <em>managedEmployees</em> is an unqualified collection-valued
	 * path expression (<code>employee.managedEmployees</code>).
	 *
	 * @param declaration The {@link Declaration} that was parsed to range over an abstract schema
	 * name but is actually ranging over a path expression
	 * @param outerVariableName The identification variable coming from the parent identification
	 * variable declaration
	 */
	void convertUnqualifiedDeclaration(Declaration declaration, String outerVariableName) {

		QualifyRangeDeclarationVisitor visitor = qualifyRangeDeclarationVisitor();

		try {
			visitor.declaration       = declaration;
			visitor.outerVariableName = outerVariableName;

			declaration.declarationExpression.accept(visitor);
		}
		finally {
			visitor.declaration       = null;
			visitor.outerVariableName = null;
		}
	}

	private DeclarationVisitor declarationVisitor() {
		if (declarationVisitor == null) {
			declarationVisitor = new DeclarationVisitor();
		}
		return declarationVisitor;
	}

	/**
	 * Disposes the internal data.
	 */
	void dispose() {
		resolvers      .clear();
		declarations   .clear();
		resultVariables.clear();
	}

	/**
	 * Returns the ordered list of {@link Declaration Declarations}.
	 *
	 * @return The {@link Declaration Declarations} of the current query that was parsed
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
		if (joinFetches.isEmpty() && (getParent() != null)) {
			joinFetches = getParent().getJoinFetchesImp(variableName);
		}
		return joinFetches;
	}

	private Collection<JoinFetch> getJoinFetchesImp(String variableName) {

		for (Declaration declaration : declarations) {
			if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
				return declaration.getJoinFetches();
			}
		}

		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationResolver getParent() {
		return (DeclarationResolver) super.getParent();
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

		if ((resolver == null) && (getParent() != null)) {
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
	 * Returns the variables that got defined in the select expression. This only applies to JPQL
	 * queries built for JPA 2.0.
	 *
	 * @return The variables identifying the select expressions, if any was defined or an empty set
	 * if none were defined
	 */
	Set<String> getResultVariables() {
		return new HashSet<String>(resultVariables.values());
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
	 * @param queryContext The context used to query information about the query
	 */
	private void initialize(JPQLQueryContext queryContext) {
		this.queryContext    = queryContext;
		this.resolvers       = new HashMap<String, Resolver>();
		this.declarations    = new ArrayList<Declaration>();
		this.resultVariables = new HashMap<IdentificationVariable, String>();
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
		if (!result && (getParent() != null)) {
			result = getParent().isCollectionIdentificationVariableImp(variableName);
		}
		return result;
	}

	private boolean isCollectionIdentificationVariableImp(String variableName) {

		for (Declaration declaration : declarations) {

			// Check for a collection member declaration
			if (!declaration.rangeDeclaration &&
			     declaration.getVariableName().equalsIgnoreCase(variableName)) {

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
		if (!result && (getParent() != null)) {
			result = getParent().isRangeIdentificationVariableImp(variableName);
		}
		return result;
	}

	private boolean isRangeIdentificationVariableImp(String variableName) {

		for (Declaration declaration : declarations) {

			if (declaration.rangeDeclaration &&
			    declaration.getVariableName().equalsIgnoreCase(variableName)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Visits the current query (which is either the top-level query or a subquery) and gathers the
	 * information from the declaration clause.
	 *
	 * @param expression The {@link Expression} to visit in order to retrieve the information
	 * contained in the given query's declaration
	 */
	void populate(Expression expression) {
		expression.accept(declarationVisitor());
	}

	private QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor() {
		if (qualifyRangeDeclarationVisitor == null) {
			qualifyRangeDeclarationVisitor = new QualifyRangeDeclarationVisitor();
		}
		return qualifyRangeDeclarationVisitor;
	}

	private String visitDeclaration(Expression expression, Expression identificationVariable) {

		// Visit the identification variable expression and retrieve the identification variable name
		String variableName = queryContext.literal(
			identificationVariable,
			LiteralType.IDENTIFICATION_VARIABLE
		);

		// If it's not empty, then we can create a Resolver
		if (ExpressionTools.stringIsNotEmpty(variableName)) {

			// Always make the identification variable be upper case since it's
			// case insensitive, the get will also use upper case
			String internalVariableName = variableName.toUpperCase();

			// Make sure the identification variable was not declared more than once,
			// this could cause issues when trying to resolve it
			if (!resolvers.containsKey(internalVariableName)) {

				// Resolve the expression and map it with the identification variable
				Resolver resolver = queryContext.getResolver(expression);
				resolver = new IdentificationVariableResolver(resolver, variableName);
				resolvers.put(internalVariableName, resolver);
			}

			return variableName;
		}

		return ExpressionTools.EMPTY_STRING;
	}

	/**
	 * A <code>Declaration</code> represents either an identification variable declaration or a
	 * collection member declaration.
	 */
	public static class Declaration {

		/**
		 * The abstract schema name, which is the entity name or <code>null</code> if this {@link
		 * Declaration} is a collection member declaration.
		 */
		String abstractSchemaName;

		/**
		 * Either the range variable declaration if this is a range declaration otherwise the
		 * collection-valued path expression when this is a collection member declaration.
		 */
		Expression baseExpression;

		/**
		 * The declaration expression, which is either an {@link IdentificationVariableDeclaration} or
		 * a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
		 * either the {@link DeleteClause} or the {@link UpdateClause}.
		 */
		Expression declarationExpression;

		/**
		 * The identification variable used to declare an abstract schema name or a collection-valued
		 * path expression.
		 */
		IdentificationVariable identificationVariable;

		/**
		 * The list of <b>JOIN FETCH</b> expressions that are declared in the same declaration than
		 * the range variable declaration.
		 */
		List<JoinFetch> joinFetches;

		/**
		 * The identification variables that are defined in the <b>JOIN</b> expressions.
		 */
		Set<String> joinIdentificationVariables;

		/**
		 * The list of <b>JOIN</b> expressions that are declared in the same declaration than the
		 * range variable declaration.
		 */
		Map<Join, IdentificationVariable> joins;

		/**
		 * Flag used to determine if this declaration is for a range variable declaration
		 * (<code>true</code>) or for a collection member declaration (<code>false</code>).
		 */
		boolean rangeDeclaration;

		private void addJoin(Join join, IdentificationVariable identificationVariable) {
			if (joins == null) {
				joins = new LinkedHashMap<Join, IdentificationVariable>();
			}
			joins.put(join, identificationVariable);
		}

		private void addJoinFetch(JoinFetch joinFetch) {
			if (joinFetches == null) {
				joinFetches = new ArrayList<JoinFetch>();
			}
			joinFetches.add(joinFetch);
		}

		private Set<String> buildJoinIdentificationVariables() {

			Set<String> variables = new HashSet<String>();
			Set<String> upperCaseVariables = new HashSet<String>();

			// Add the non-empty join identification variables
			for (IdentificationVariable identificationVariable : joins.values()) {
				String joinVariable = identificationVariable.getText();
				// Make sure the same variable name but with different case is not added more than once
				if ((joinVariable.length() > 0) &&
				    !upperCaseVariables.contains(joinVariable.toUpperCase())) {

					variables.add(joinVariable);
				}
			}

			return Collections.unmodifiableSet(variables);
		}

		private Map.Entry<Join, String> buildMapEntry(Map.Entry<Join, IdentificationVariable> entry) {
			IdentificationVariable variable = entry.getValue();
			String variableName = (variable != null) ? variable.getText() : ExpressionTools.EMPTY_STRING;
			return new SimpleEntry<Join, String>(entry.getKey(), variableName);
		}

		/**
		 * Returns the abstract schema name, which is the entity name.
		 *
		 * @return The abstract schema name, which is the entity name or <code>null</code> if this
		 * {@link Declaration} is a collection member declaration
		 */
		public String getAbstractSchemaName() {
			return abstractSchemaName;
		}

		/**
		 * Returns the range variable declaration if this is a range declaration otherwise the
		 * collection-valued path expression when this is a collection member declaration.
		 *
		 * @return Either the range variable declaration or the collection-valued path expression
		 */
		public Expression getBaseExpression() {
			return baseExpression;
		}

		/**
		 * Returns the declaration expression, which is either an {@link IdentificationVariableDeclaration}
		 * or a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
		 * either the {@link DeleteClause} or the {@link UpdateClause}.
		 *
		 * @return The root of the declaration expression
		 */
		public Expression getDeclarationExpression() {
			return declarationExpression;
		}

		/**
		 * Returns the <b>JOIN</b> expressions mapped to their identification variables. The set
		 * returns the <b>JOIN</b> expressions in ordered they were parsed.
		 *
		 * @return The <b>JOIN</b> expressions mapped to their identification variables
		 */
		public List<Map.Entry<Join, String>> getJoinEntries() {
			List<Map.Entry<Join, String>> entries = new ArrayList<Map.Entry<Join, String>>();
			for (Map.Entry<Join, IdentificationVariable> entry : joins.entrySet()) {
				entries.add(buildMapEntry(entry));
			}
			return entries;
		}

		/**
		 * Returns the <b>JOIN FETCH</b> expressions that were part of the range variable declaration
		 * in the ordered they were parsed.
		 *
		 * @return The ordered list of <b>JOIN FETCH</b> expressions or an empty collection if none
		 * was present
		 */
		public List<JoinFetch> getJoinFetches() {
			return joinFetches;
		}

		/**
		 * Returns the identification variables that are defined in the <b>JOIN</b> expressions.
		 *
		 * @return The identification variables that are defined in the <b>JOIN</b> expressions
		 */
		public Set<String> getJoinIdentificationVariables() {

			if (joinIdentificationVariables == null) {
				if (hasJoins()) {
					joinIdentificationVariables = buildJoinIdentificationVariables();
				}
				else {
					joinIdentificationVariables = Collections.emptySet();
				}
			}

			return joinIdentificationVariables;
		}

		/**
		 * Returns the <b>JOIN</b> expressions that were part of the range variable declaration in the
		 * ordered they were parsed.
		 *
		 * @return The ordered list of <b>JOIN</b> expressions or an empty collection if none was
		 * present
		 */
		public Collection<Join> getJoins() {
			return joins.keySet();
		}

		/**
		 * Returns the identification variable name that is defining either the abstract schema name
		 * or the collection-valued path expression
		 *
		 * @return The name of the identification variable
		 */
		public String getVariableName() {
			if (identificationVariable == null) {
				return ExpressionTools.EMPTY_STRING;
			}
			return identificationVariable.getText();
		}

		/**
		 * Determines whether the declaration contains <b>JOIN FETCH</b> expressions. This can be
		 * <code>true</code> only when {@link #isRange()} returns <code>true</code>. A collection
		 * member declaration does not have <b>JOIN FETCH</b> expressions.
		 *
		 * @return <code>true</code> if at least one <b>JOIN FETCH</b> expression was parsed;
		 * otherwise <code>false</code>
		 */
		public boolean hasJoinFetches() {
			return !joinFetches.isEmpty();
		}

		/**
		 * Determines whether the declaration contains <b>JOIN</b> expressions. This can be
		 * <code>true</code> only when {@link #isRange()} returns <code>true</code>. A collection
		 * member declaration does not have <b>JOIN</b> expressions.
		 *
		 * @return <code>true</code> if at least one <b>JOIN</b> expression was parsed;
		 * otherwise <code>false</code>
		 */
		public boolean hasJoins() {
			return !joins.isEmpty();
		}

		/**
		 * Determines whether this {@link Declaration} represents a range identification variable
		 * declaration, example: "Employee e".
		 * <p>
		 * Note: There is a case where this can be <code>true</code> but the range expression is not
		 * an abstract schema name but a derived path. It only happens in a subquery defined in the
		 * <b>WHERE</b> clause of an <b>UPDATE</b> or <b>DELETE</b> statement because the
		 * identification variable is optional.
		 *
		 * @return <code>true</code> if the declaration is over an abstract schema name; <code>false</code>
		 * if it's over a collection-valued path expression
		 */
		public boolean isRange() {
			return rangeDeclaration;
		}

		/**
		 * Make sure the list of <b>JOIN</b> expressions and the map of <b>JOIN FETCHS</b> expressions
		 * can not be modified.
		 */
		private void lockData() {

			if (joins != null) {
				joins = Collections.unmodifiableMap(joins);
			}
			else {
				joins = Collections.emptyMap();
			}

			if (joinFetches != null) {
				joinFetches = Collections.unmodifiableList(joinFetches);
			}
			else {
				joinFetches = Collections.emptyList();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return declarationExpression.toParsedText();
		}
	}

	private class DeclarationVisitor extends AbstractExpressionVisitor {

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

			Expression identificationVariable = expression.getIdentificationVariable();
			String variableName = visitDeclaration(expression, identificationVariable);

			if (variableName.length() > 0) {
				declaration.identificationVariable = (IdentificationVariable) identificationVariable;
			}

			declaration.declarationExpression = expression;
			declaration.baseExpression        = expression.getCollectionValuedPathExpression();
			declaration.lockData();
			declarations.add(declaration);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {

			Declaration declaration = new Declaration();
			declaration.declarationExpression = expression;
			declaration.rangeDeclaration = true;
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				currentDeclaration.lockData();
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
			resultVariables.put(expression, expression.getText());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {

			Declaration declaration = new Declaration();
			declaration.declarationExpression = expression;
			declaration.baseExpression        = expression.getRangeVariableDeclaration();
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
				expression.getJoins().accept(this);
			}
			finally {
				currentDeclaration.lockData();
				currentDeclaration = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			Expression identificationVariable = expression.getIdentificationVariable();
			String variableName = visitDeclaration(expression, identificationVariable);
			if (variableName.length() > 0) {
				currentDeclaration.addJoin(expression, (IdentificationVariable) identificationVariable);
			}
			else {
				currentDeclaration.addJoin(expression, null);
			}
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

			Expression abstractSchemaName     = expression.getAbstractSchemaName();
			Expression identificationVariable = expression.getIdentificationVariable();

			// Identification variable
			String variableName = visitDeclaration(abstractSchemaName, identificationVariable);

			if (variableName.length() > 0) {
				currentDeclaration.identificationVariable = (IdentificationVariable) identificationVariable;
			}

			// Abstract schema name or join association (for subqueries)
			String entityName = queryContext.literal(
				abstractSchemaName,
				LiteralType.ABSTRACT_SCHEMA_NAME
			);

			if (ExpressionTools.stringIsNotEmpty(entityName)) {
				currentDeclaration.rangeDeclaration = true;
				currentDeclaration.abstractSchemaName = entityName;
			}
			else {
				String joinAssociation = queryContext.literal(
					abstractSchemaName,
					LiteralType.PATH_EXPRESSION_ALL_PATH
				);

				currentDeclaration.abstractSchemaName = joinAssociation;
			}
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
			declaration.declarationExpression = expression;
			declaration.rangeDeclaration = true;
			declarations.add(declaration);

			currentDeclaration = declaration;

			try {
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				currentDeclaration.lockData();
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

	private class QualifyRangeDeclarationVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link Declaration} being modified.
		 */
		Declaration declaration;

		/**
		 * The identification variable coming from the parent identification variable declaration.
		 */
		String outerVariableName;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			declaration.baseExpression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			expression.getRangeVariableDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {

			declaration.rangeDeclaration = false;

			expression.setVirtualIdentificationVariable(outerVariableName, declaration.abstractSchemaName);
			expression.getAbstractSchemaName().accept(this);
		}
	}
}