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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;

/**
 * This visitor visits the declaration clause of the JPQL query and creates the list of
 * {@link Declaration Declarations}.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class DeclarationResolver {

	/**
	 * The first {@link Declaration} that was created when visiting the declaration clause.
	 */
	private Declaration baseDeclaration;

	/**
	 * The {@link Declaration} objects mapped to their identification variable.
	 */
	private List<Declaration> declarations;

	/**
	 * The parent {@link DeclarationResolver} which represents the superquery's declaration or
	 * <code>null</code> if this is used for the top-level query.
	 */
	private DeclarationResolver parent;

	/**
	 * Determines whether the {@link Declaration Declaration} objects were created after visiting the
	 * query's declaration clause.
	 */
	private boolean populated;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * The result variables used to identify select expressions.
	 */
	private Collection<IdentificationVariable> resultVariables;

	/**
	 * Creates a new <code>DeclarationResolver</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param parent The parent {@link DeclarationResolver} which represents the superquery's declaration
	 */
	DeclarationResolver(JPQLQueryContext queryContext, DeclarationResolver parent) {
		super();
		initialize(queryContext, parent);
	}

	/**
	 * Adds a "virtual" range variable declaration that will be used when parsing a JPQL fragment.
	 *
	 * @param entityName The name of the entity to be accessible with the given variable name
	 * @param variableName The identification variable used to navigate to the entity
	 */
	void addRangeVariableDeclaration(String entityName, String variableName) {

		// This method should only be used by HermesParser.buildSelectionCriteria(),
		// initializes these variables right away since this method should only be
		// called by HermesParser.buildSelectionCriteria()
		populated = true;
		resultVariables = Collections.emptySet();

		// Create the "virtual" range variable declaration
		RangeVariableDeclaration rangeVariableDeclaration = new RangeVariableDeclaration(
			entityName,
			variableName
		);

		// Make sure the identification variable was not declared more than once,
		// this could cause issues when trying to resolve it
		RangeDeclaration declaration = new RangeDeclaration(queryContext);
		declaration.rootPath               = entityName;
		declaration.baseExpression         = rangeVariableDeclaration;
		declaration.identificationVariable = (IdentificationVariable) rangeVariableDeclaration.getIdentificationVariable();

		declarations.add(declaration);

		// Make sure it is marked as the base declaration and the base Expression is created
		if (baseDeclaration == null) {
			baseDeclaration = declaration;

			// Make sure the base Expression is initialized, which will cache it
			// into the right context as well (the top-level context)
			declaration.getQueryExpression();
		}
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
	void convertUnqualifiedDeclaration(RangeDeclaration declaration, String outerVariableName) {

		QualifyRangeDeclarationVisitor visitor = new QualifyRangeDeclarationVisitor();

		// Convert the declaration expression into a derived declaration
		visitor.declaration       = declaration;
		visitor.outerVariableName = outerVariableName;
		visitor.queryContext      = queryContext.getCurrentContext();

		declaration.declarationExpression.accept(visitor);

		// Now replace the old declaration with the new one
		int index = declarations.indexOf(declaration);
		declarations.set(index, visitor.declaration);

		// Update the base declaration
		if (baseDeclaration == declaration) {
			baseDeclaration = visitor.declaration;
		}
	}

	/**
	 * Retrieves the {@link Declaration} for which the given variable name is used to navigate to the
	 * "root" object.
	 *
	 * @param variableName The name of the identification variable that is used to navigate a "root"
	 * object
	 * @return The {@link Declaration} containing the information about the identification variable
	 * declaration
	 */
	Declaration getDeclaration(String variableName) {

		for (Declaration declaration : declarations) {
			if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
				return declaration;
			}
		}

		return null;
	}

	/**
	 * Returns the ordered list of {@link Declaration Declarations}.
	 *
	 * @return The {@link Declaration Declarations} of the current query that was parsed
	 */
	List<Declaration> getDeclarations() {
		return declarations;
	}

	/**
	 * Returns the first {@link Declaration} that was created after visiting the declaration clause.
	 *
	 * @return The first {@link Declaration} object
	 */
	Declaration getFirstDeclaration() {
		return baseDeclaration;
	}

	/**
	 * Returns the parsed representation of a <b>JOIN FETCH</b> that were defined in the same
	 * declaration than the given range identification variable name.
	 *
	 * @param variableName The name of the identification variable that should be used to define an entity
	 * @return The <b>JOIN FETCH</b> expressions used in the same declaration or an empty collection
	 * if none was defined
	 */
	Collection<Join> getJoinFetches(String variableName) {

		Declaration declaration = getDeclaration(variableName);

		if ((declaration != null) && (declaration.getType() == Type.RANGE)) {
			RangeDeclaration rangeDeclaration = (RangeDeclaration) declaration;
			if (rangeDeclaration.hasJoins()) {
				return rangeDeclaration.getJoinFetches();
			}
		}

		return null;
	}

	/**
	 * Returns the parent of this {@link DeclarationResolver}.
	 *
	 * @return The parent of this {@link DeclarationResolver} if this is used for a subquery or
	 * <code>null</code> if this is used for the top-level query
	 */
	DeclarationResolver getParent() {
		return parent;
	}

	/**
	 * Returns the variables that got defined in the select expression. This only applies to JPQL
	 * queries built for JPA 2.0 or later.
	 *
	 * @return The variables identifying the select expressions, if any was defined or an empty set
	 * if none were defined
	 */
	Collection<IdentificationVariable> getResultVariables() {

		if (parent != null) {
			return parent.getResultVariables();
		}

		if (resultVariables == null) {
			ResultVariableVisitor visitor = new ResultVariableVisitor();
			queryContext.getJPQLExpression().accept(visitor);
			resultVariables = visitor.resultVariables;
		}

		return resultVariables;
	}

	/**
	 * Initializes this <code>DeclarationResolver</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 * @param parent The parent {@link DeclarationResolver}, which is not <code>null</code> when this
	 * resolver is created for a subquery
	 */
	private void initialize(JPQLQueryContext queryContext, DeclarationResolver parent) {
		this.parent       = parent;
		this.queryContext = queryContext;
		this.declarations = new LinkedList<Declaration>();
	}

	/**
	 * Determines whether the given identification variable is defining a <b>JOIN</b> expression or
	 * in a <code>IN</code> expressions for a collection-valued field. If the search didn't find the
	 * identification in this resolver, then it will traverse the parent hierarchy.
	 *
	 * @param variableName The identification variable to check for what it maps
	 * @return <code>true</code> if the given identification variable maps a collection-valued field
	 * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> otherwise
	 */
	boolean isCollectionIdentificationVariable(String variableName) {

		boolean result = isCollectionIdentificationVariableImp(variableName);

		if (!result && (parent != null)) {
			result = parent.isCollectionIdentificationVariableImp(variableName);
		}

		return result;
	}

	/**
	 * Determines whether the given identification variable is defining a <b>JOIN</b> expression or
	 * in a <code>IN</code> expressions for a collection-valued field. The search does not traverse
	 * the parent hierarchy.
	 *
	 * @param variableName The identification variable to check for what it maps
	 * @return <code>true</code> if the given identification variable maps a collection-valued field
	 * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> otherwise
	 */
	boolean isCollectionIdentificationVariableImp(String variableName) {

		for (Declaration declaration : declarations) {

			switch (declaration.getType()) {

				case COLLECTION: {
					if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
						return true;
					}
					return false;
				}

				case RANGE:
				case DERIVED: {

					AbstractRangeDeclaration rangeDeclaration = (AbstractRangeDeclaration) declaration;

					// Check the JOIN expressions
					for (Join join : rangeDeclaration.getJoins()) {

						String joinVariableName = queryContext.literal(
							join.getIdentificationVariable(),
							LiteralType.IDENTIFICATION_VARIABLE
						);

						if (joinVariableName.equalsIgnoreCase(variableName)) {
							// Make sure the JOIN expression maps a collection mapping
							Declaration joinDeclaration = queryContext.getDeclaration(joinVariableName);
							return joinDeclaration.getMapping().isCollectionMapping();
						}
					}
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
			result = parent.isRangeIdentificationVariableImp(variableName);
		}
		return result;
	}

	private boolean isRangeIdentificationVariableImp(String variableName) {
		Declaration declaration = getDeclaration(variableName);
		return (declaration != null) && declaration.getType().isRange();
	}

	/**
	 * Determines whether the given variable is a result variable or not.
	 *
	 * @param variableName The variable to check if it used to identify a select expression
	 * @return <code>true</code> if the given variable is defined as a result variable;
	 * <code>false</code> otherwise
	 */
	boolean isResultVariable(String variableName) {

		// Only the top-level SELECT query has result variables
		if (parent != null) {
			return parent.isResultVariable(variableName);
		}

		for (IdentificationVariable resultVariable : getResultVariables()) {
			if (resultVariable.getText().equalsIgnoreCase(variableName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Visits the given {@link Expression} (which is either the top-level query or a subquery) and
	 * retrieve the information from its declaration clause.
	 *
	 * @param expression The {@link Expression} to visit in order to retrieve the information
	 * contained in the given query's declaration
	 */
	void populate(Expression expression) {
		if (!populated) {
			populated = true;
			populateImp(expression);
		}
	}

	private void populateImp(Expression expression) {

		DeclarationVisitor visitor = new DeclarationVisitor();
		visitor.queryContext = queryContext;
		visitor.declarations = declarations;

		expression.accept(visitor);
		baseDeclaration = visitor.baseDeclaration;
	}

	private static class DeclarationVisitor extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * The first {@link Declaration} that was created when visiting the declaration clause.
		 */
		private Declaration baseDeclaration;

		/**
		 * This flag is used to determine what to do in {@link #visit(SimpleSelectStatement)}.
		 */
		private boolean buildingDeclaration;

		/**
		 * The {@link Declaration} being populated.
		 */
		private Declaration currentDeclaration;

		/**
		 * The list of {@link Declaration} objects to which new ones will be added by traversing the
		 * declaration clause.
		 */
		List<Declaration> declarations;

		/**
		 * The {@link JPQLQueryContext} is used to query information about the application metadata and
		 * cached information.
		 */
		JPQLQueryContext queryContext;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {

			String rootPath = expression.getText();

			// Abstract schema name (entity name)
			if (rootPath.indexOf('.') == -1) {
				currentDeclaration = new RangeDeclaration(queryContext);
			}
			else {

				// Check to see if the "root" path is a class name before assuming it's a derived path
				Class<?> type = queryContext.getType(rootPath);

				// Fully qualified class name
				if (type != null) {
					RangeDeclaration declaration = new RangeDeclaration(queryContext);
					declaration.type = type;
					currentDeclaration = declaration;
				}
				// Derived path expression (for subqueries)
				else {
					currentDeclaration = new DerivedDeclaration(queryContext);
				}
			}

			currentDeclaration.rootPath = rootPath;
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

			Declaration declaration = new CollectionDeclaration(queryContext);
			declaration.baseExpression        = expression.getCollectionValuedPathExpression();
			declaration.rootPath              = declaration.baseExpression.toActualText();
			declaration.declarationExpression = expression;
			declarations.add(declaration);

			// A derived collection member declaration does not have an identification variable
			if (!expression.isDerived()) {
				IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
				declaration.identificationVariable = identificationVariable;
			}

			// This collection member declaration is the first defined,
			// it is then the base Declaration
			if (baseDeclaration == null) {
				baseDeclaration = declaration;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			String rootPath = expression.toParsedText();

			// Check to see if the "root" path is a class name before assuming it's a derived path
			Class<?> type = queryContext.getType(rootPath);

			// Fully qualified class name
			if (type != null) {
				RangeDeclaration declaration = new RangeDeclaration(queryContext);
				declaration.type = type;
				currentDeclaration = declaration;
			}
			// Derived path expression (for subqueries)
			else {
				currentDeclaration = new DerivedDeclaration(queryContext);
			}

			currentDeclaration.rootPath = rootPath;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
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
		public void visit(IdentificationVariableDeclaration expression) {

			try {
				// Visit the RangeVariableDeclaration, it will create the right Declaration
				expression.getRangeVariableDeclaration().accept(this);
				currentDeclaration.declarationExpression = expression;

				// Now visit the JOIN expressions
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

			((AbstractRangeDeclaration) currentDeclaration).addJoin(expression);

			if (!expression.hasFetch() || expression.hasIdentificationVariable()) {
				IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();

				JoinDeclaration declaration = new JoinDeclaration(queryContext);
				declaration.baseExpression = expression;
				declaration.identificationVariable = identificationVariable;
				declarations.add(declaration);
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

			// Traverse the "root" object, it will create the right Declaration
			buildingDeclaration = true;
			expression.getRootObject().accept(this);
			buildingDeclaration = false;

			// Cache more information
			currentDeclaration.identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
			currentDeclaration.baseExpression = expression;
			declarations.add(currentDeclaration);

			// This range variable declaration is the first defined,
			// it is then the base declaration
			if (baseDeclaration == null) {
				baseDeclaration = currentDeclaration;
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

			// The parent query is using a subquery in the FROM clause
			if (buildingDeclaration) {
				currentDeclaration = new SubqueryDeclaration(queryContext);
				currentDeclaration.rootPath = ExpressionTools.EMPTY_STRING;
			}
			// Simply traversing the tree to create the declarations
			else {
				expression.getFromClause().accept(this);
			}
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
		public void visit(TableVariableDeclaration expression) {

			TableDeclaration declaration = new TableDeclaration(queryContext);
			declaration.declarationExpression  = expression;
			declaration.baseExpression         = expression.getTableExpression();
			declaration.rootPath               = declaration.baseExpression.toParsedText();
			declaration.identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
			declarations.add(declaration);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
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

	private static class QualifyRangeDeclarationVisitor extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * The {@link Declaration} being modified.
		 */
		AbstractRangeDeclaration declaration;

		/**
		 * The identification variable coming from the parent identification variable declaration.
		 */
		String outerVariableName;

		/**
		 * The {@link JPQLQueryContext} is used to query information about the application metadata and
		 * cached information.
		 */
		JPQLQueryContext queryContext;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			// Create the path because CollectionValuedPathExpression.toParsedText()
			// does not contain the virtual identification variable
			StringBuilder rootPath = new StringBuilder();
			rootPath.append(outerVariableName);
			rootPath.append(".");
			rootPath.append(expression.toParsedText());
			declaration.rootPath = rootPath.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			expression.getRangeVariableDeclaration().accept(this);
			declaration.declarationExpression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {

			DerivedDeclaration derivedDeclaration = new DerivedDeclaration(queryContext);
			derivedDeclaration.joins                       = declaration.joins;
			derivedDeclaration.rootPath                    = declaration.rootPath;
			derivedDeclaration.baseExpression              = declaration.baseExpression;
			derivedDeclaration.identificationVariable      = declaration.identificationVariable;
			declaration = derivedDeclaration;

			expression.setVirtualIdentificationVariable(outerVariableName, declaration.rootPath);
			expression.getRootObject().accept(this);
		}
	}

	/**
	 * This visitor traverses the <code><b>SELECT</b></code> clause and retrieves the result variables.
	 */
	private static class ResultVariableVisitor extends AbstractEclipseLinkExpressionVisitor {

		Set<IdentificationVariable> resultVariables;

		/**
		 * Creates a new <code>ResultVariableVisitor</code>.
		 */
		public ResultVariableVisitor() {
			super();
			resultVariables = new HashSet<IdentificationVariable>();
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
		public void visit(JPQLExpression expression) {
			expression.getQueryStatement().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getResultVariable();
			resultVariables.add(identificationVariable);
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
			expression.getSelectClause().accept(this);
		}
	}
}