/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import java.util.Iterator;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This resolver is responsible to return the map value, which means that for identification
 * variables referring to an instance of an association or collection represented as a {@link
 * java.util.Map Map}, the identification variable is of the abstract schema type of the map value.
 *
 * @see KeyTypeResolver
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class ValueTypeResolver extends AbstractTypeResolver
{
	/**
	 * The resolver used to find the type of the identification variable.
	 */
	private final TypeResolver typeResolver;

	/**
	 * Creates a new <code>ValueTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param typeResolver The resolver used to find the type of the
	 * identification variable
	 */
	ValueTypeResolver(TypeResolver parent, TypeResolver typeResolver)
	{
		super(parent);
		this.typeResolver = typeResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType()
	{
		return typeResolver.resolveManagedType(getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		// Retrieve the type declaration because the type alone will never be a collection type
		ITypeDeclaration typeDeclaration = getTypeDeclaration();

		if (isMapType(typeDeclaration.getType()))
		{
			Iterator<IType> types = typeDeclaration.parameterTypes();

			if (types.hasNext())
			{
				// Skip the type for the key
				types.next();

				if (types.hasNext())
				{
					// Return the type for the value
					return types.next();
				}
			}
		}

		return objectType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration()
	{
		return typeResolver.getTypeDeclaration();
	}
}