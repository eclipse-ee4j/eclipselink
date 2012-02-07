/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
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
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;

/**
 * This visitor visits the declaration clause of the query and creates the list of {@link Declaration}
 * objects. Those objects will then be used to query information of that declaration clause, a
 * {@link Declaration} is mapped to its identification variable for fast retrieval.
 *
 * @version 2.4
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
	private Map<String, Declaration> declarations;

	/**
	 * This visitor is responsible to visit the current query's declaration and populate this
	 * resolver with the list of declarations.
	 */
	private DeclarationVisitor declarationVisitor;

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
	 * This visitor is responsible to convert the abstract schema name into a path expression.
	 */
	private QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * The result variables used to identify select expressions.
	 */
	private Set<String> resultVariables;

	/**
	 * Determines whether the <code><b>SELECT</b></code> clause was visited in order to retrieve the
	 * list of result variables.
	 */
	private boolean resultVariablesPopulated;

	/**
	 * The visitor that visits the <code><b>SELECT</b></code> clause and gather the list of result
	 * variables.
	 */
	private ResultVariableVisitor resultVariableVisitor;

	/**
	 * Creates a new <code>DeclarationResolver</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param parent The parent {@link DeclarationResolver} which represents the superquery's
	 * declaration
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
	 * @return The {@link RangeDeclaration} that contains the information of the "virtual" range
	 * variable declaration
	 */
	RangeDeclaration addRangeVariableDeclaration(String entityName, String variableName) {

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

		// Identification variable is always used as upper case because .equals() is used instead of
		// .equalsIgnoreCase(), which means equivalency will be used because intern() was used everywhere
		variableName = variableName.toUpperCase().intern();
		declarations.put(variableName, declaration);

		// Make sure it marked as the base declaration
		if (baseDeclaration == null) {
			baseDeclaration = declaration;
		}

		return declaration;
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

		QualifyRangeDeclarationVisitor visitor = qualifyRangeDeclarationVisitor();

		try {
			// Convert the declaration expression into a derived declaration
			visitor.declaration       = declaration;
			visitor.outerVariableName = outerVariableName;
			visitor.queryContext      = queryContext.getCurrentContext();

			declaration.declarationExpression.accept(visitor);

			String variableName = null;

			// Now replace the old declaration with the new one
			for (Map.Entry<String, Declaration> entry : declarations.entrySet()) {
				if (entry.getValue() == declaration) {
					variableName = entry.getKey();
					break;
				}
			}

			declarations.put(variableName, visitor.declaration);

			// Update the base declaration
			if (baseDeclaration == declaration) {
				baseDeclaration = visitor.declaration;
			}
		}
		finally {
			visitor.declaration       = null;
			visitor.queryContext      = null;
			visitor.outerVariableName = null;
		}
	}

	private DeclarationVisitor declarationVisitor() {

		if (parent != null) {
			return parent.declarationVisitor();
		}

		if (declarationVisitor == null) {
			declarationVisitor = new DeclarationVisitor();
		}

		return declarationVisitor;
	}

	/**
	 * Disposes the internal data.
	 */
	void dispose() {

		populated = false;
		baseDeclaration = null;
		resultVariablesPopulated = false;
		declarations.clear();

		if (resultVariables != null) {
			resultVariables.clear();
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
		return declarations.get(variableName);
	}

	/**
	 * Returns the ordered list of {@link Declaration Declarations}.
	 *
	 * @return The {@link Declaration Declarations} of the current query that was parsed
	 */
	Collection<Declaration> getDeclarations() {
		return declarations.values();
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
	 * @param variableName The name of the identification variable that should be used to define an
	 * abstract schema name
	 * @return The <b>JOIN FETCH</b> expressions used in the same declaration or an empty collection
	 * if none was defined
	 */
	Collection<JoinFetch> getJoinFetches(String variableName) {

		Declaration declaration = getDeclaration(variableName);

		if ((declaration != null) && declaration.isRange()) {
			RangeDeclaration rangeDeclaration = (RangeDeclaration) declaration;
			if (rangeDeclaration.hasJoinFetches()) {
				return rangeDeclaration.getJoinFetches();
			}
		}

		return null;
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
		this.declarations = new LinkedHashMap<String, Declaration>();
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
		return (declaration != null) && declaration.isRange();
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

		if (!resultVariablesPopulated) {
			resultVariablesPopulated = true;
			queryContext.getJPQLExpression().accept(resultVariableVisitor());
		}

		return resultVariables.contains(variableName);
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

		DeclarationVisitor visitor = declarationVisitor();

		try {
			visitor.queryContext = queryContext.getCurrentContext();
			visitor.declarations = declarations;

			expression.accept(visitor);
			baseDeclaration = visitor.baseDeclaration;
		}
		finally {
			visitor.queryContext    = null;
			visitor.declarations    = null;
			visitor.baseDeclaration = null;
		}
	}

	private QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor() {

		if (parent != null) {
			return parent.qualifyRangeDeclarationVisitor();
		}

		if (qualifyRangeDeclarationVisitor == null) {
			qualifyRangeDeclarationVisitor = new QualifyRangeDeclarationVisitor();
		}

		return qualifyRangeDeclarationVisitor;
	}

	private ResultVariableVisitor resultVariableVisitor() {
		if (resultVariableVisitor == null) {
			resultVariables = new HashSet<String>();
			resultVariableVisitor = new ResultVariableVisitor();
		}
		return resultVariableVisitor;
	}

	private static class DeclarationVisitor extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * The first {@link Declaration} that was created when visiting the declaration clause.
		 */
		private Declaration baseDeclaration;

		/**
		 * The {@link Declaration} being populated.
		 */
		private Declaration currentDeclaration;

		/**
		 * The list of {@link Declaration} objects to which new ones will be added by traversing the
		 * declaration clause.
		 */
		Map<String, Declaration> declarations;

		/**
		 * The {@link JPQLQueryContext} is used to query information about the application metadata and
		 * cached information.
		 */
		JPQLQueryContext queryContext;

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
			declaration.baseExpression = expression.getCollectionValuedPathExpression();
			declaration.declarationExpression = expression;

			// A derived collection member declaration does not have an identification variable
			if (expression.isDerived()) {
				declarations.put(declaration.baseExpression.toParsedText(), declaration);
			}
			else {
				IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
				declaration.identificationVariable = identificationVariable;
				declarations.put(identificationVariable.getVariableName(), declaration);
			}

			if (baseDeclaration == null) {
				baseDeclaration = declaration;
			}
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
				expression.getRangeVariableDeclaration().accept(this);
				currentDeclaration.declarationExpression = expression;

				if (expression.hasJoins()) {
					expression.getJoins().accept(this);
				}
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
			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();

			JoinDeclaration declaration = new JoinDeclaration(queryContext);
			declaration.baseExpression = expression;
			declaration.identificationVariable = identificationVariable;
			declarations.put(identificationVariable.getVariableName(), declaration);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JoinFetch expression) {
			((RangeDeclaration) currentDeclaration).addJoinFetch(expression);
			if (expression.hasIdentificationVariable()) {
                            IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
    
                            JoinDeclaration declaration = new JoinDeclaration(queryContext);
                            declaration.baseExpression = expression;
                            declaration.identificationVariable = identificationVariable;
                            declarations.put(identificationVariable.getVariableName(), declaration);
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

			IdentificationVariable identificationVariable = (IdentificationVariable) expression.getIdentificationVariable();
			String rootPath = expression.getAbstractSchemaName().toParsedText();

			// Abstract schema name
			if (rootPath.indexOf('.') == -1) {
				currentDeclaration = new RangeDeclaration(queryContext);
			}
			// Derived path expression (for subqueries)
			else {
				currentDeclaration = new DerivedDeclaration(queryContext);
			}

			currentDeclaration.identificationVariable = identificationVariable;
			currentDeclaration.baseExpression = expression;
			currentDeclaration.rootPath = rootPath;
			declarations.put(identificationVariable.getVariableName(), currentDeclaration);

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
			expression.getFromClause().accept(this);
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
			expression.getAbstractSchemaName().accept(this);
		}
	}

	/**
	 * This visitor traverses the <code><b>SELECT</b></code> clause and retrieves the result variables.
	 */
	private class ResultVariableVisitor extends AbstractEclipseLinkExpressionVisitor {

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
			resultVariables.add(identificationVariable.getVariableName());
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