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
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve the type of a collection-valued field.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class CollectionValuedFieldResolver extends AbstractPathResolver {

	/**
	 * The full collection-valued field path expression, which is used to determine if it's an enum
	 * type.
	 */
	private String collectionValuedField;

	/**
	 * The {@link IManagedType} representing this single valued object field path.
	 */
	private IManagedType managedType;

	/**
	 * This flag is used to prevent resolving the {@link IManagedType} more than once and no managed
	 * type could be found the first time.
	 */
	private boolean managedTypeResolved;

	/**
	 * Creates a new <code>CollectionValuedFieldResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param path The collection field path, which is the last path of the collection-valued path
	 * expression
	 * @param collectionValuedField The full collection-valued field path expression, which is used
	 * to determine if it's an enum type
	 */
	CollectionValuedFieldResolver(Resolver parent, String path, String collectionValuedField) {
		super(parent, path);
		this.collectionValuedField = collectionValuedField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IType buildType() {

		// Check first to see if the path expression is actually representing an enum constant
		IType type = (collectionValuedField != null) ? getTypeRepository().getEnumType(collectionValuedField) : null;

		if (type != null) {
			return type;
		}

		ITypeDeclaration typeDeclaration = getTypeDeclaration();
		type = typeDeclaration.getType();

		// For a collection type, return the first type parameter
		if (getTypeHelper().isCollectionType(type)) {
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length > 0) {
				type = typeParameters[0].getType();
			}
		}
		// For a map type, by default the value is the actual type to return
		else if (getTypeHelper().isMapType(type)) {
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length == 2) {
				type = typeParameters[1].getType();
			}
		}

		// A collection-valued path expression should not reference a primitive,
		// however, in an invalid query, this could potentially happen and the API
		// only deals with the primitive wrapper type
		return getTypeHelper().convertPrimitive(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ITypeDeclaration buildTypeDeclaration() {
		IType type = getEnumType();
		if (type != null) {
			return type.getTypeDeclaration();
		}
		return super.buildTypeDeclaration();
	}

	private IType getEnumType() {
		return (collectionValuedField != null) ? getTypeRepository().getEnumType(collectionValuedField) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IManagedType getManagedType() {
		if ((managedType == null) && !managedTypeResolved) {
			if (!getType().isEnum()) {
				managedType = resolveManagedType();
			}
			managedTypeResolved = true;
		}
		return managedType;
	}

	private IManagedType resolveManagedType() {

		IMapping mapping = getMapping();

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

	private static class MapManagedType implements IManagedType {

		private final IType mapType;
		private final IManagedTypeProvider provider;

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
		public int compareTo(IManagedType managedType) {
			return getType().getName().compareTo(managedType.getType().getName());
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
		public Iterable<IMapping> mappings() {
			return Collections.emptyList();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return getType().getName();
		}
	}
}