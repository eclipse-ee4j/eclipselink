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

import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The abstract implementation of a {@link TypeResolver} that has a parent.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class AbstractTypeResolver implements TypeResolver {

	/**
	 * The parent of this resolver, which is never <code>null</code>.
	 */
	private final TypeResolver parent;

	/**
	 * Creates a new <code>AbstractTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 */
	AbstractTypeResolver(TypeResolver parent) {
		super();
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType() {
		return parent.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping getMapping() {
		return parent.getMapping();
	}

	/**
	 * Returns the managed type of the parent resolver.
	 *
	 * @return The managed type of the parent resolver
	 */
	final IManagedType getParentManagedType() {
		return parent.getManagedType();
	}

	/**
	 * Returns the {@link IMapping} of the parent resolver.
	 *
	 * @return The {@link IMapping} of the parent resolver
	 */
	final IMapping getParentMapping() {
		return parent.getMapping();
	}

	/**
	 * Returns the type of the parent resolver.
	 *
	 * @return The type of the parent resolver
	 */
	final IType getParentType() {
		return parent.getType();
	}

	/**
	 * Returns the type declaration of the parent resolver.
	 *
	 * @return The type declaration of the parent resolver
	 */
	final ITypeDeclaration getParentTypeDeclaration() {
		return parent.getTypeDeclaration();
	}

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The container holding the managed types
	 */
	final IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getQuery() {
		return parent.getQuery();
	}

	/**
	 * Returns the {@link IType} of the given Java type.
	 *
	 * @param type The Java type for which its external form will be returned
	 * @return The {@link IType} representing the given Java type
	 */
	final IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	final IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration getTypeDeclaration() {
		return getType().getTypeDeclaration();
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	final TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the class repository, which gives access to the application's classes.
	 *
	 * @return The external form of the class repository
	 */
	final ITypeRepository getTypeRepository() {
		return getQuery().getProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType resolveManagedType(IType type) {
		return parent.resolveManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType resolveManagedType(String abstractSchemaName) {
		return parent.resolveManagedType(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping resolveMapping(String variableName) {
		return parent.resolveMapping(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType resolveType(String variableName) {
		return parent.resolveType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration resolveTypeDeclaration(String variableName) {
		return parent.resolveTypeDeclaration(variableName);
	}
}