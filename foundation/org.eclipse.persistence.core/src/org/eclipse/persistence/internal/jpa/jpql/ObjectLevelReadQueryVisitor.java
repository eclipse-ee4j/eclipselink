/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This visitor is responsible to populate an {@link ObjectLevelReadQueryVisitor}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class ObjectLevelReadQueryVisitor extends AbstractReadAllQueryVisitor {

	/**
	 * Creates a new <code>ObjectLevelReadQueryVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param query The {@link ObjectLevelReadQuery} to populate by using this visitor to visit the
	 * parsed tree representation of the JPQL query
	 */
	ObjectLevelReadQueryVisitor(JPQLQueryContext queryContext, ObjectLevelReadQuery query) {
		super(queryContext, query);
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
		Collection<Join> joinFetches = queryContext.getJoinFetches(variableName);

		if (joinFetches != null ) {

			for (Join joinFetch : joinFetches) {

				// Retrieve the join association path expression's identification variable
				String joinFetchVariableName = queryContext.literal(
					joinFetch,
					LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
				);

				// Both identification variables are the same.
				// Then add the join associated path expression as a joined attribute
				// Example: FROM Employee e JOIN FETCH e.employees
				if (variableName.equals(joinFetchVariableName)) {
					org.eclipse.persistence.expressions.Expression queryExpression = queryContext.buildExpression(joinFetch);
					query.addJoinedAttribute(queryExpression);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		super.visit(expression);

		// Visit the select expression so we can add a joined attribute if required
		expression.getSelectExpression().accept(this);
	}
}