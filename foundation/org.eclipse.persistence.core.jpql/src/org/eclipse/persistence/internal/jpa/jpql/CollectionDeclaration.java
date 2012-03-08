/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
import java.util.List;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * This <code>CollectionDeclaration</code> represents a collection member declaration that was
 * declared in the <code><b>FROM</b></code> clause of a <code><b>SELECT</b></code> top-level query
 * or subquery.
 *
 * @see CollectionMemberDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
final class CollectionDeclaration extends Declaration {

	/**
	 * Creates a new <code>CollectionDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	CollectionDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Expression buildQueryExpression() {
		return queryContext.buildExpression(baseExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionValuedPathExpression getBaseExpression() {
		return (CollectionValuedPathExpression) super.getBaseExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionMemberDeclaration getDeclarationExpression() {
		return (CollectionMemberDeclaration) super.getDeclarationExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Join> getJoins() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasJoins() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollection() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDerived() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRange() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ClassDescriptor resolveDescriptor() {
		return queryContext.resolveDescriptor(baseExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatabaseMapping resolveMapping() {
		return queryContext.resolveMapping(baseExpression);
	}
}