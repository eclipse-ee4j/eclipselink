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

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;

/**
 * This visitor is responsible to complete the initialization of the {@link ObjectLevelReadQuery} by
 * visiting the expression clauses.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ObjectLevelReadQueryVisitor extends AbstractObjectLevelReadQueryVisitor<ObjectLevelReadQuery> {

	/**
	 * This visitor is responsible to add the joined attributes to the query.
	 */
	private JoinedAttributeExpressionVisitor joinedAttributeExpressionVisitor;

	/**
	 * Creates a new <code>ObjectLevelReadQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata
	 */
	ObjectLevelReadQueryVisitor(QueryBuilderContext queryContext) {
		super(queryContext);
	}

	private JoinedAttributeExpressionVisitor joinedAttributeExpressionVisitor() {
		if (joinedAttributeExpressionVisitor == null) {
			joinedAttributeExpressionVisitor = new JoinedAttributeExpressionVisitor();
		}
		return joinedAttributeExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		super.visit(expression);
		expression.getSelectExpression().accept(joinedAttributeExpressionVisitor());
	}

	/**
	 * This visitor is responsible to add joined attributes when the <b>SELECT</b> clause has
	 * an identification variable that is mapped to an abstract schema name (defined in a range
	 * variable declaration) and that identification variable declaration was defined with join
	 * fetches. The join fetches will be added as joined attributes.
	 */
	private class JoinedAttributeExpressionVisitor extends AbstractExpressionVisitor {

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
		public void visit(IdentificationVariable expression) {

			String variableName = expression.getText();

			// Retrieve the join fetches that were defined in the same identification variable
			// declaration, if the identification variable is mapped to a join, then there will
			// not be any join fetch associated with it
			for (JoinFetch joinFetch : queryContext.joinFetches(variableName)) {

				// Retrieve the join association path expression's identification variable
				String joinFetchVariableName = queryContext.variableName(joinFetch, Type.PATH_EXPRESSION_IDENTIFICATION_VARIABLE);

				// Both identification variables are the same.
				// Then add the join associated path expression as a joined attribute
				// Example: FROM Employee e JOIN FETCH e.employees
				if (variableName.equalsIgnoreCase(joinFetchVariableName)) {
					query().addJoinedAttribute(queryContext.buildExpression(joinFetch));
				}
			}
		}
	}
}