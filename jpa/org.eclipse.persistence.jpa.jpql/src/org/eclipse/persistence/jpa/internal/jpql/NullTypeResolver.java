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
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * A "null" implementation of a {@link TypeResolver}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class NullTypeResolver extends AbstractTypeResolver {

	/**
	 * Creates a new <code>NullTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 */
	NullTypeResolver(TypeResolver parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping getMapping() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getTypeHelper().unknownType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration() {
		return getTypeHelper().unknownTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(IType type) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping resolveMapping(String variableName) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName) {
		return null;
	}
}