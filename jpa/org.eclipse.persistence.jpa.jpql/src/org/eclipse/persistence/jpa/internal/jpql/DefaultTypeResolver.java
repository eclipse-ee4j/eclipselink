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

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * The default implementation of a {@link TypeResolver} that can be used as the root of the
 * {@link TypeResolver} hierarchy.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class DefaultTypeResolver extends AbstractTypeResolver {

	/**
	 * The external form representing the JPA query.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>DefaultTypeResolver</code>.
	 *
	 * @param query The external form representing the JPA query
	 */
	DefaultTypeResolver(IQuery query) {
		super(null);
		this.query = query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery() {
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getTypeHelper().objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(IType type) {
		return getProvider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName) {
		return getProvider().getManagedType(abstractSchemaName);
	}
}