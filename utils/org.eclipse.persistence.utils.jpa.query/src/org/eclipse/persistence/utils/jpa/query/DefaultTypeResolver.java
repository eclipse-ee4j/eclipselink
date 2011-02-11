/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * The default implementation of a {@link TypeResolver} that can be used as the root of the
 * {@link TypeResolver} hierarchy.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class DefaultTypeResolver extends AbstractTypeResolver
{
	/**
	 * The external form representing the JPA query.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>DefaultTypeResolver</code>.
	 *
	 * @param query The external form representing the JPA query
	 */
	DefaultTypeResolver(IQuery query)
	{
		super(null);
		this.query = query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery()
	{
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(IType type)
	{
		return getProvider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName)
	{
		return getProvider().getManagedType(abstractSchemaName);
	}
}