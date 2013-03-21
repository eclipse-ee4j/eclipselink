/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * This <code>RangeDeclaration</code> represents an identification variable declaration that was
 * declared in the <code><b>FROM</b></code> clause of a <code><b>SELECT</b></code> top-level query
 * or subquery.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration IdentificationVariableDeclaration
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
final class RangeDeclaration extends AbstractRangeDeclaration {

	/**
	 * The list of <b>JOIN FETCH</b> expressions that are declared in the same declaration than
	 * the range variable declaration.
	 */
	private List<Join> joinFetches;

	/**
	 * If the "root" path is the fully qualified class name, then this will be set.
	 */
	public Class<?> type;

	/**
	 * Creates a new <code>RangeDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	RangeDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addJoin(Join join) {
		super.addJoin(join);

		if (join.hasFetch()) {
			addJoinFetch(join);
		}
	}

	private void addJoinFetch(Join join) {
		if (joinFetches == null) {
			joinFetches = new LinkedList<Join>();
		}
		joinFetches.add(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Expression buildQueryExpression() {

		ClassDescriptor descriptor = getDescriptor();

		// The abstract schema name can't be resolved, we'll assume it's actually an unqualified
		// state field path expression or collection-valued path expression declared in an UPDATE
		// or DELETE query
		if (descriptor == null) {

			// Convert the AbstractSchemaName into a CollectionValuedPathExpression since
			// it's an unqualified state field path expression or collection-valued path expression
			convertUnqualifiedDeclaration();

			// The abstract schema name is now a CollectionValuedPathExpression, request the context
			// to return the Expression for the new Declaration
			return queryContext.getBaseExpression();
		}

		return new ExpressionBuilder(descriptor.getJavaClass());
	}

	/**
	 * Converts the given {@link Declaration} from being set as a range variable declaration to
	 * a path expression declaration.
	 * <p>
	 * In this query "<code>UPDATE Employee SET firstName = 'MODIFIED' WHERE (SELECT COUNT(m) FROM
	 * managedEmployees m) > 0</code>" <em>managedEmployees</em> is an unqualified collection-valued
	 * path expression (<code>employee.managedEmployees</code>).
	 */
	private void convertUnqualifiedDeclaration() {

		// Retrieve the range identification variable from the parent declaration
		Declaration parentDeclaration = queryContext.getParent().getFirstDeclarationImp();
		String outerVariableName = parentDeclaration.getVariableName();

		// Qualify the range expression to be fully qualified
		queryContext.getDeclarationResolver().convertUnqualifiedDeclaration(this, outerVariableName);
	}

	/**
	 * Returns the <b>JOIN FETCH</b> expressions that were part of the range variable declaration
	 * in the ordered they were parsed.
	 *
	 * @return The ordered list of <b>JOIN FETCH</b> expressions or an empty collection if none
	 * was present
	 */
	List<Join> getJoinFetches() {
		return (joinFetches == null) ? Collections.<Join>emptyList() : joinFetches;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type getType() {
		return (type != null) ? Type.CLASS_NAME : Type.RANGE;
	}

	/**
	 * Determines whether the "root" path is either the abstract schema name (entity name) or the
	 * abstract schema name.
	 *
	 * @return <code>true</code> if it is the fully qualified class name; <code>false</code> if it
	 * is the entity name
	 */
	public boolean isFullyQualifiedClassName() {
		return type != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ClassDescriptor resolveDescriptor() {

		if (type != null) {
			return queryContext.getDescriptor(type);
		}

		return queryContext.getDescriptor(rootPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatabaseMapping resolveMapping() {
		// A range declaration does not have a mapping, only a descriptor
		return null;
	}
}