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

import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This visitor is responsible to resolve the type of a state field, which is the leaf of the state
 * field path expression.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class StateFieldTypeResolver extends AbstractPathTypeResolver
{
	/**
	 * Creates a new <code>StateFieldTypeResolver</code>.
	 *
	 * @param parent The parent visitor is used to retrieve the type from where the property should
	 * be retrieved
	 * @param path The state field path
	 */
	StateFieldTypeResolver(TypeResolver parent, String path)
	{
		super(parent, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return convertPrimitive(super.getType());
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
			IMapping mapping = managedType.getMappingNamed(variableName);

			if (mapping != null)
			{
				return mapping.getTypeDeclaration();
			}
		}

		// Nothing was found
		return objectTypeDeclaration();
	}
}