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
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This resolver is responsible to resolve the type of a single valued object field, which is
 * usually part of a path expression. It is not the first path nor the last path.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class SingleValuedObjectFieldTypeResolver extends AbstractPathTypeResolver
{
	/**
	 * Creates a new <code>SingleValuedObjectFieldTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param path The singled value object path
	 */
	SingleValuedObjectFieldTypeResolver(TypeResolver parent, String path)
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
		// Example: SELECT e.employees. FROM Employee e where employees is a collection,
		// it cannot be traversed
		if (isCollectionType(type))
		{
			return null;
		}

		// Retrieve the corresponding managed type for the mapping's type
		return getProvider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName)
	{
		IManagedType managedType = getManagedType();

		if (managedType != null)
		{
			return managedType.getType().getTypeDeclaration();
		}

		return objectTypeDeclaration();
	}
}