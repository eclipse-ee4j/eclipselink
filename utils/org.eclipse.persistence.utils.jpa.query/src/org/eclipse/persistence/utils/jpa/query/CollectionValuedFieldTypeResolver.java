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

import java.util.Collections;
import java.util.Iterator;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeVisitor;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This resolver is responsible to resolve the type of a collection-valued field.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class CollectionValuedFieldTypeResolver extends AbstractPathTypeResolver
{
	/**
	 * Creates a new <code>CollectionValuedFieldTypeResolver</code>.
	 *
	 * @param parent The parent visitor is used to retrieve the type from where the property should
	 * be retrieved
	 * @param path The name of the collection-valued field
	 */
	CollectionValuedFieldTypeResolver(TypeResolver parent, String path)
	{
		super(parent, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType()
	{
		return resolveManagedType(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName)
	{
		// Retrieve the managed type that is the owner of the property
		IManagedType managedType = getParentManagedType();

		if (managedType == null)
		{
			return null;
		}

		// Retrieve the mapping for the property
		IMapping mapping = managedType.getMappingNamed(abstractSchemaName);

		if (mapping == null)
		{
			return null;
		}

		ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
		IType type = typeDeclaration.getType();

		// Collection type cannot be traversed
		if (isCollectionType(type))
		{
			Iterator<IType> componentTypes = typeDeclaration.parameterTypes();

			if (!componentTypes.hasNext())
			{
				return null;
			}

			type = componentTypes.next();
		}
		// Wrap the Map into a virtual IManagedType so it can be returned and the
		// IType for the Map can be used to retrieve the type of the key and value
		else if (isMapType(type))
		{
			return new MapManagedType(getProvider(), type);
		}

		// Retrieve the corresponding managed type for the mapping's type
		return getProvider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName)
	{
		ITypeDeclaration typeDeclaration = resolveTypeDeclaration(variableName);
		IType type = typeDeclaration.getType();

		// For a collection type, return the first type parameter
		if (isCollectionType(type))
		{
			Iterator<IType> componentTypes = typeDeclaration.parameterTypes();

			if (componentTypes.hasNext())
			{
				return componentTypes.next();
			}
		}
		// For a map type, by default the value is the actual type to return
		else if (isMapType(type))
		{
			Iterator<IType> componentTypes = typeDeclaration.parameterTypes();

			// Skip the key type
			if (componentTypes.hasNext())
			{
				componentTypes.next();

				// Return the value type
				if (componentTypes.hasNext())
				{
					return componentTypes.next();
				}
			}
		}

		return typeDeclaration.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName)
	{
		// Retrieve the managed type that is the owner of the property
		IManagedType managedType = getParentManagedType();

		if (managedType == null)
		{
			return objectTypeDeclaration();
		}

		// Retrieve the mapping for the property
		IMapping mapping = managedType.getMappingNamed(variableName);

		if (mapping == null)
		{
			return objectTypeDeclaration();
		}

		return mapping.getTypeDeclaration();
	}

	private static class MapManagedType implements IManagedType
	{
		private IType mapType;
		private IManagedTypeProvider provider;

		MapManagedType(IManagedTypeProvider provider, IType mapType)
		{
			super();

			this.provider = provider;
			this.mapType  = mapType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(IManagedTypeVisitor visitor)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IMapping getMappingNamed(String name)
		{
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IManagedTypeProvider getProvider()
		{
			return provider;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IType getType()
		{
			return mapType;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<IMapping> mappings()
		{
			return Collections.<IMapping>emptyList().iterator();
		}
	}
}