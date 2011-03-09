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
 * This resolver resolves the type of an identification variable.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class IdentificationVariableResolver extends AbstractTypeResolver {

	/**
	 * The name of the identification variable, which is never <code>null</code> or an empty string.
	 */
	private final String variableName;

	/**
	 * Creates a new <code>IdentificationVariableResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param variableName The name of the identification variable, which should never be
	 * <code>null</code> and it should not be an empty string
	 */
	IdentificationVariableResolver(TypeResolver parent, String variableName) {
		super(parent);

		// Always make the identification variable be lower case since it's
		// case insensitive, the get will also use lower case
		this.variableName = variableName.toLowerCase();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		return resolveManagedType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping getMapping() {
		return resolveMapping(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return resolveType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration() {
		return resolveTypeDeclaration(variableName);
	}
}