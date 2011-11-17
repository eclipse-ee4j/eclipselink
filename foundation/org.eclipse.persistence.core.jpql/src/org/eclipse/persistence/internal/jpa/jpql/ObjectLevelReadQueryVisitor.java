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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.Collection;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;

/**
 * This visitor is responsible to populate a {@link ReadAllQuery}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ObjectLevelReadQueryVisitor extends AbstractReadAllQueryVisitor {

	/**
	 * This visitor is responsible to add the joined attributes to the query.
	 */
	private JoinedAttributeExpressionVisitor joinedAttributesVisitor;

	/**
	 * Creates a new <code>ReadAllQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ObjectLevelReadQueryVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	private JoinedAttributeExpressionVisitor joindAttributesVisitor() {
		if (joinedAttributesVisitor == null) {
			joinedAttributesVisitor = new JoinedAttributeExpressionVisitor();
		}
		return joinedAttributesVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		super.visit(expression);
		expression.getSelectExpression().accept(joindAttributesVisitor());
	}

	/**
	 * This visitor is responsible to add joined attributes when the <b>SELECT</b> clause has
	 * an identification variable that is mapped to an abstract schema name (defined in a range
	 * variable declaration) and that identification variable declaration was defined with join
	 * fetches. The join fetches will be added as joined attributes.
	 */
	private class JoinedAttributeExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

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

			String variableName = expression.getVariableName();

			// Retrieve the join fetches that were defined in the same identification variable
			// declaration, if the identification variable is mapped to a join, then there will
			// not be any join fetch associated with it
			Collection<JoinFetch> joinFetches = queryContext.getJoinFetches(variableName);

			if (joinFetches != null ) {

				for (JoinFetch joinFetch : queryContext.getJoinFetches(variableName)) {

					// Retrieve the join association path expression's identification variable
					String joinFetchVariableName = queryContext.literal(
						joinFetch,
						LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
					);

					// Both identification variables are the same.
					// Then add the join associated path expression as a joined attribute
					// Example: FROM Employee e JOIN FETCH e.employees
					if (variableName.equals(joinFetchVariableName)) {
						getDatabaseQuery().addJoinedAttribute(queryContext.buildExpression(joinFetch));
					}
				}
			}
		}
	}
}