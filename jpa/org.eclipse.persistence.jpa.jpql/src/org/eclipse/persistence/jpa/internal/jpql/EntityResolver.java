/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} retrieves the type for an abstract schema name (entity name).
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class EntityResolver extends Resolver {

	/**
	 * The abstract schema name is the name of the entity.
	 */
	private final String abstractSchemaName;

	/**
	 * The {@link IManagedType} with the same abstract schema name.
	 */
	private IManagedType managedType;

	/**
	 * Creates a new <code>EntityResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param abstractSchemaName The name of the entity
	 */
	EntityResolver(Resolver parent, String abstractSchemaName) {
		super(parent);
		this.abstractSchemaName = abstractSchemaName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ResolverVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IType buildType() {
		IManagedType entity = getManagedType();
		return (entity != null) ? entity.getType() : getTypeHelper().objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		return getType().getTypeDeclaration();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public String getAbstractSchemaName() {
		return abstractSchemaName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		if (managedType == null) {
			managedType = getProvider().getManagedType(abstractSchemaName);
		}
		return managedType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return abstractSchemaName;
	}
}