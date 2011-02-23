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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;

/**
 * This {@link PathExpressionResolver} is responsible to gather the {@link PathExpressionResolver
 * builders} where the abstract schema name is mapped to an identification variable and a
 * collection-valued path expression to an identification variable.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class DeclarationExpressionResolver extends AbstractExpressionVisitor
	                                       implements PathExpressionResolver {

	/**
	 * The identification variable names mapped to their resolvers.
	 */
	private Map<String, PathExpressionResolver> expressions;

	/**
	 * The map where the JPQL expression is mapped to its identification variable and any <b>JOIN
	 * FETCH</b> expressions it may have. If it does not have <b>JOIN FETCH</b> expressions, then
	 * the value is <code>null</code>.
	 */
	private Map<IdentificationVariableDeclaration, JoinFetchAssociation> joinFetches;

	/**
	 * The collection of <b>JOIN</b> expressions that were found in the <b>FROM</b> clause.
	 */
	private Collection<Join> joins;

	/**
	 * The parent of this {@link PathExpressionResolver}.
	 */
	private DeclarationExpressionResolver parent;

	/**
	 * The very first {@link ExpressionBuilder} that is responsible to create {@link Expression
	 * expressions} represented by the main reference class.
	 */
	private Expression parentExpression;

	/**
	 * The {@link PathExpressionResolver} that can resolves any expression defined in the declaration
	 * expression (<b>FROM<b> clause, <b>DELETE FROM</b> clause, or <b>UPDATE</b> clause).
	 */
	private PathDeclarationExpressionResolver pathDeclarationExpressionResolver;

	/**
	 * The context used to query information about the application metadata.
	 */
	private QueryBuilderContext queryContext;

	/**
	 * The list of identification variables that were defined in range variable declaration.
	 */
	private List<String> rangeIdentificationVariables;

	/**
	 * This visitor is responsibles to visit the range variable declaration of a query and registering
	 * the information it contains.
	 */
	private RangeVariableDeclarationVisitor rangeVariableDeclarationVisitor;

	/**
	 * Flag used to register <b>JOIN FETCH</b> expressions or to simply ignore it, which happens when
	 * the declaration is part of an <b>UPDATE</b> or <b>DELETE</b> query.
	 */
	private boolean registerJoinFetches;

	/**
	 * Creates a new <code>DeclarationExpressionResolver</code>.
	 *
	 * @param parent The parent of this {@link PathExpressionResolver}
	 * @param queryContext The context used to query information about the application metadata
	 */
	DeclarationExpressionResolver(DeclarationExpressionResolver parent, QueryBuilderContext queryContext) {
		super();
		initialize(parent, queryContext);
	}

	private PathDeclarationExpressionResolver buildPathDeclarationExpressionResolver() {
		return new PathDeclarationExpressionResolver(this, queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationExpressionResolver clone() {
		return this;
	}

	/**
	 * Disposes this visitor.
	 */
	void dispose() {
		parentExpression = null;
		joins.clear();
		expressions.clear();
		joinFetches.clear();
		rangeIdentificationVariables.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return parentExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		PathExpressionResolver resolver = getResolver(variableName);
		return (resolver == null) ? null : resolver.getExpression();
	}

	/**
	 * Returns the {@link PathExpressionResolver} for the given variable name. There will always be
	 * a resolver for an identification variable declaration, join and collection-member declaration.
	 * <p>
	 * If this is a declaration resolver for a subquery, then the search will continue up the parent
	 * hierarchy.
	 *
	 * @param variableName The name of the identification variable for which its associated resolver
	 * is requested
	 * @return The {@link PathExpressionResolver} associated with the given variable name
	 */
	PathExpressionResolver getResolver(String variableName) {
		PathExpressionResolver resolver = expressions.get(variableName.toLowerCase());
		if ((resolver == null) && (parent != null)) {
			resolver = parent.getResolver(variableName);
			if (resolver != null) {
				resolver = resolver.clone();
				registerPathExpressionResolver(variableName, resolver);
			}
		}
		return resolver;
	}

	/**
	 * Determines whether the JPQL expression has <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
	 * expressions; <code>false</code> otherwise
	 */
	boolean hasJoins() {
		return !joins.isEmpty();
	}

	/**
	 * Initializes this <code>DeclarationExpressionResolver</code>.
	 *
	 * @param parent The parent of this {@link PathExpressionResolver}
	 * @param queryContext The context used to query information about the application metadata
	 */
	private void initialize(DeclarationExpressionResolver parent, QueryBuilderContext queryContext) {
		this.parent       = parent;
		this.queryContext = queryContext;
		this.joins        = new ArrayList<Join>();
		this.expressions  = new HashMap<String, PathExpressionResolver>();
		this.joinFetches  = new HashMap<IdentificationVariableDeclaration, JoinFetchAssociation>();
		this.rangeIdentificationVariables = new ArrayList<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed(String variableName) {
		PathExpressionResolver resolver = getResolver(variableName);
		return (resolver == null) ? false : resolver.isNullAllowed();
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
		return isRangeIdentificationVariableImp(variableName.toLowerCase());
	}

	private boolean isRangeIdentificationVariableImp(String variableName) {
		boolean result = rangeIdentificationVariables.contains(variableName);
		if (!result && (parent != null)) {
			result = parent.isRangeIdentificationVariableImp(variableName);
		}
		return result;
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
		Collection<JoinFetch> joinFetches = joinFetchesImp(variableName);
		if (joinFetches.isEmpty() && (parent != null)) {
			joinFetches = parent.joinFetchesImp(variableName);
		}
		return joinFetches;
	}

	private Collection<JoinFetch> joinFetchesImp(String variableName) {

		for (Map.Entry<IdentificationVariableDeclaration, JoinFetchAssociation> entry : joinFetches.entrySet()) {
			JoinFetchAssociation association = entry.getValue();
			if ((association != null) && variableName.equalsIgnoreCase(association.variableName)) {
				return association.getJoinFetches();
			}
		}

		return Collections.emptyList();
	}

	private PathDeclarationExpressionResolver pathDeclarationExpressionResolver() {
		if (pathDeclarationExpressionResolver == null) {
			pathDeclarationExpressionResolver = buildPathDeclarationExpressionResolver();
		}
		return pathDeclarationExpressionResolver;
	}

	private RangeVariableDeclarationVisitor rangeVariableDeclarationVisitor() {
		if (rangeVariableDeclarationVisitor == null) {
			rangeVariableDeclarationVisitor = new RangeVariableDeclarationVisitor();
		}
		return rangeVariableDeclarationVisitor;
	}

	private void registerJoinFetch(org.eclipse.persistence.utils.jpa.query.parser.Expression expression,
	                               JoinFetch joinFetch) {

		if (joinFetches.containsKey(expression)) {

			JoinFetchAssociation association = joinFetches.get(expression);

			if (association == null) {
				association = new JoinFetchAssociation();
				joinFetches.put((IdentificationVariableDeclaration) expression, association);
			}

			association.addJoinFetch(joinFetch);
		}
		else {
			registerJoinFetch(expression.getParent(), joinFetch);
		}
	}

	private void registerJoinFetchIdentificationVariable(org.eclipse.persistence.utils.jpa.query.parser.Expression expression,
	                                                     String variableName) {

		if (joinFetches.containsKey(expression)) {
			JoinFetchAssociation association = joinFetches.get(expression);

			if (association == null) {
				association = new JoinFetchAssociation();
				joinFetches.put((IdentificationVariableDeclaration) expression, association);
			}
			association.variableName = variableName;
		}
		else {
			registerJoinFetchIdentificationVariable(expression.getParent(), variableName);
		}
	}

	private void registerPathExpressionResolver(String variableName, PathExpressionResolver resolver) {
		expressions.put(variableName.toLowerCase(), resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNullAllowed(boolean nullAllowed) {
		// Not supported
	}

	private void visit(AbstractExpression expression,
	                   org.eclipse.persistence.utils.jpa.query.parser.Expression identificationVariable) {

		PathDeclarationExpressionResolver pathExpressionVisitor = pathDeclarationExpressionResolver();

		try {
			// Resolve the collection-valued path expression
			expression.accept(pathExpressionVisitor);

			// Map the identification variable name with its resolver
			registerPathExpressionResolver(
				queryContext.variableName(identificationVariable, Type.IDENTIFICATION_VARIABLE),
				pathExpressionVisitor.getResolver()
			);
		}
		finally {
			pathExpressionVisitor.dispose();
		}
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
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		registerJoinFetches = false;
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		registerJoinFetches = false;
		expression.getDeleteClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		registerJoinFetches = true;
		expression.getDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {

		if (registerJoinFetches) {
			joinFetches.put(expression, null);
		}

		expression.getRangeVariableDeclaration().accept(this);

		if (expression.hasJoins()) {
			expression.getJoins().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		joins.add(expression);
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {
		if (registerJoinFetches) {
			registerJoinFetch(expression.getParent(), expression);
		}
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
		RangeVariableDeclarationVisitor visitor = rangeVariableDeclarationVisitor();
		try {
			expression.accept(visitor);
			if (registerJoinFetches) {
				registerJoinFetchIdentificationVariable(expression, visitor.variableName);
			}
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		expression.getFromClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		registerJoinFetches = true;
		expression.getDeclaration().accept(this);
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
		registerJoinFetches = false;
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression) {
		registerJoinFetches = false;
		expression.getUpdateClause().accept(this);
	}

	/**
	 * This class maps an identification variable (which is the range variable declaration's
	 * identification variable) to the <b>JOIN FETCH</b> expression of the same identification
	 * variable declaration.
	 */
	private static class JoinFetchAssociation {

		/**
		 * The collection of <b>JOIN FETCH</b> expressions that are declared in the same declaration
		 * than the range variable declaration.
		 */
		private Collection<JoinFetch> joinFetches;

		/**
		 * The range variable declaration's identification variable.
		 */
		String variableName;

		void addJoinFetch(JoinFetch joinFetch) {
			if (joinFetches == null) {
				joinFetches = new ArrayList<JoinFetch>();
			}
			joinFetches.add(joinFetch);
		}

		Collection<JoinFetch> getJoinFetches() {
			return (joinFetches != null) ? joinFetches : Collections.<JoinFetch>emptyList();
		}
	}

	/**
	 * This visitor is responsible to traverse a range variable declaration and to register its
	 * information.
	 */
	private class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link RangeVariableDeclaration} being visited.
		 */
		private RangeVariableDeclaration declaration;

		/**
		 * The range variable declaration's identification variable.
		 */
		private String variableName;

		/**
		 * Disposes this visitor.
		 */
		void dispose() {
			declaration  = null;
			variableName = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {

			String abstractSchemaName = expression.getText();
			ClassDescriptor descriptor = queryContext.getDescriptor(abstractSchemaName);

			// The abstract schema name can't be resolved, we'll assume it's actually an unqualified
			// state field path expression or collection-valued path expression declared in an UPDATE
			// query
			if (descriptor == null) {
				// Convert the AbstractSchemaName into a CollectionValuedPathExpression since
				// it's an unqualified state field path expression or collection-valued path expression
				String outerVriableName = parent.rangeIdentificationVariables.get(0);
				declaration.setVirtualIdentificationVariable(outerVriableName, abstractSchemaName);

				// The abstract schema name is now a CollectionValuedPathExpression
				declaration.getAbstractSchemaName().accept(this);
			}
			else {
				EntityPathExpressionResolver resolver = new EntityPathExpressionResolver(
					DeclarationExpressionResolver.this,
					queryContext,
					abstractSchemaName
				);

				registerPathExpressionResolver(variableName, resolver);
				rangeIdentificationVariables.add(variableName);

				// The first Expression will be used as the parent expression
				if (parentExpression == null) {
					parentExpression = resolver.getExpression();
					queryContext.addExpression(variableName, parentExpression);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			PathDeclarationExpressionResolver visitor = pathDeclarationExpressionResolver();

			try {
				// Resolve the collection-valued path expression
				expression.accept(visitor);

				// Register the variable name with the path expression's resolver
				registerPathExpressionResolver(variableName, visitor.getResolver());

				// The first Expression will be used as the parent expression
				if (parentExpression == null) {
					parentExpression = visitor.getExpression();
				}
			}
			finally {
				visitor.dispose();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			variableName = expression.getText().toLowerCase();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {

			this.declaration = expression;

			// First visit the identification variable to retrieve it's variable name
			expression.getIdentificationVariable().accept(this);

			// Now visit the abstract schema name, which can also be a path expression if the
			// declaration is defined in a subquery
			expression.getAbstractSchemaName().accept(this);
		}
	}
}