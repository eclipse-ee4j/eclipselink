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

import java.util.Collections;
import java.util.Iterator;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This resolver is responsible to resolve the type of a collection-valued field.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class CollectionValuedFieldTypeResolver extends AbstractPathTypeResolver {

	/**
	 * Creates a new <code>CollectionValuedFieldTypeResolver</code>.
	 *
	 * @param parent The parent visitor is used to retrieve the type from where the property should
	 * be retrieved
	 * @param path The name of the collection-valued field
	 */
	CollectionValuedFieldTypeResolver(TypeResolver parent, String path) {
		super(parent, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		return resolveManagedType(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping getMapping() {
		return resolveMapping(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName) {

		// Retrieve the managed type that is the owner of the property
		IManagedType managedType = getParentManagedType();

		if (managedType == null) {
			return null;
		}

		// Retrieve the mapping for the property
		IMapping mapping = managedType.getMappingNamed(abstractSchemaName);

		if (mapping == null) {
			return null;
		}

		ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
		IType type = typeDeclaration.getType();

		// Collection type cannot be traversed
		if (getTypeHelper().isCollectionType(type)) {
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length == 0) {
				return null;
			}
			type = typeParameters[0].getType();
		}
		// Wrap the Map into a virtual IManagedType so it can be returned and the
		// IType for the Map can be used to retrieve the type of the key and value
		else if (getTypeHelper().isMapType(type)) {
			return new MapManagedType(getProvider(), type);
		}

		// Retrieve the corresponding managed type for the mapping's type
		return getProvider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMapping resolveMapping(String variableName) {

		// Retrieve the managed type that is the owner of the property
		IManagedType managedType = getParentManagedType();

		// Retrieve the mapping for the property
		if (managedType != null) {
			return managedType.getMappingNamed(variableName);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName) {

		ITypeDeclaration typeDeclaration = resolveTypeDeclaration(variableName);
		IType type = typeDeclaration.getType();

		// For a collection type, return the first type parameter
		if (getTypeHelper().isCollectionType(type)) {
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length > 0) {
				return typeParameters[0].getType();
			}
		}
		// For a map type, by default the value is the actual type to return
		else if (getTypeHelper().isMapType(type)) {
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length == 2) {
				return typeParameters[1].getType();
			}
		}

		return typeDeclaration.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName) {

		IMapping mapping = resolveMapping(variableName);

		if (mapping != null) {
			return mapping.getTypeDeclaration();
		}

		return getTypeHelper().unknownTypeDeclaration();
	}

	private static class MapManagedType implements IManagedType {

		private IType mapType;
		private IManagedTypeProvider provider;

		MapManagedType(IManagedTypeProvider provider, IType mapType) {
			super();

			this.provider = provider;
			this.mapType  = mapType;
		}

		/**
		 * {@inheritDoc}
		 */
		public void accept(IManagedTypeVisitor visitor) {
		}

		/**
		 * {@inheritDoc}
		 */
		public IMapping getMappingNamed(String name) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public IManagedTypeProvider getProvider() {
			return provider;
		}

		/**
		 * {@inheritDoc}
		 */
		public IType getType() {
			return mapType;
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator<IMapping> mappings() {
			return Collections.<IMapping>emptyList().iterator();
		}
	}
}