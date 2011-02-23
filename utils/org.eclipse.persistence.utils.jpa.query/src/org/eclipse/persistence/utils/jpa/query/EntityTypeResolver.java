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

import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This resolver retrieve the type for an abstract schema name (entity name).
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class EntityTypeResolver extends AbstractTypeResolver {

	/**
	 * The abstract schema name is the name of the entity.
	 */
	private final String abstractSchemaName;

	/**
	 * Creates a new <code>EntityTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param abstractSchemaName The name of the entity
	 */
	EntityTypeResolver(TypeResolver parent, String abstractSchemaName) {
		super(parent);
		this.abstractSchemaName = abstractSchemaName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		return resolveManagedType(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		IManagedType entity = resolveManagedType(abstractSchemaName);
		return (entity != null) ? entity.getType() : TypeHelper.objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName) {
		return getProvider().getManagedType(abstractSchemaName);
	}
}