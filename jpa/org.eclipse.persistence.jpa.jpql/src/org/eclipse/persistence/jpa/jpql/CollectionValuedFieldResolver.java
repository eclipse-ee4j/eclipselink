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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.NullIterator;

/**
 * This {@link Resolver} is responsible to resolve the type of a collection-valued field.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public class CollectionValuedFieldResolver extends AbstractPathResolver {

	/**
	 * Creates a new <code>CollectionValuedFieldResolver</code>.
	 *
	 * @param parent The parent {@link Resolver}, which is never <code>null</code>
	 * @param path A single segment of the collection-valued path expression
	 */
	public CollectionValuedFieldResolver(Resolver parent, String path) {
		super(parent, path);
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
	protected IType buildType() {

		ITypeDeclaration typeDeclaration = getTypeDeclaration();
		IType type = typeDeclaration.getType();

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
	protected IManagedType resolveManagedType(IMapping mapping) {

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
			ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();
			if (typeParameters.length != 2) {
				return null;
			}
			type = typeParameters[1].getType();
		}

		// Retrieve the corresponding managed type for the mapping's type
		return getProvider().getManagedType(type);
	}

	protected static class MapManagedType implements IManagedType {

		protected final IType mapType;
		protected final IManagedTypeProvider provider;

		protected MapManagedType(IManagedTypeProvider provider, IType mapType) {
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
		public IterableIterator<IMapping> mappings() {
			return NullIterator.instance();
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