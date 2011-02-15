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

import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This visitor is responsible to resolve a single path of a path expression (state field path
 * expression or a collection-valued path expression).
 *
 * @see CollectionValuedFieldTypeResolver
 * @see SingleValuedObjectFieldTypeResolver
 * @see StateFieldTypeResolver
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
abstract class AbstractPathTypeResolver extends AbstractTypeResolver
{
	/**
	 * The name of the path for which its type will be retrieved.
	 */
	final String path;

	/**
	 * Creates a new <code>AbstractPathTypeResolver</code>.
	 *
	 * @param parent The parent visitor is used to retrieve the type from where
	 * the property should be retrieved
	 * @param path The name of the path
	 */
	AbstractPathTypeResolver(TypeResolver parent, String path)
	{
		super(parent);
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return resolveType(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration()
	{
		return resolveTypeDeclaration(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName)
	{
		return resolveTypeDeclaration(variableName).getType();
	}
}