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
 * This {@link Resolver} is responsible to resolve the type of an identification variable.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class IdentificationVariableResolver extends Resolver {

	/**
	 * The name of the identification variable, which is never <code>null</code> nor an empty string.
	 */
	private final String variableName;

	/**
	 * Creates a new <code>IdentificationVariableResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param variableName The name of the identification variable, which should never be
	 * <code>null</code> and it should not be an empty string
	 */
	IdentificationVariableResolver(DeclarationResolver parent, String variableName) {
		super(parent);
		this.variableName = variableName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType buildType() {
		Resolver resolver = getParent().getResolver(variableName);
		return (resolver == null) ? null : resolver.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration buildTypeDeclaration() {
		Resolver resolver = getParent().getResolver(variableName);
		return (resolver == null) ? null : resolver.getTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		Resolver resolver = getParent().getResolver(variableName);
		return (resolver == null) ? null : resolver.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping getMapping() {
		Resolver resolver = getParent().getResolver(variableName);
		return (resolver == null) ? null : resolver.getMapping();
	}

	private DeclarationResolver getParent() {
		return (DeclarationResolver) parent;
	}
}