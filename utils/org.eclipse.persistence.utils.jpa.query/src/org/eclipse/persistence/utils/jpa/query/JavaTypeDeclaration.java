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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * The concrete implementation of {@link ITypeDeclaration} that is wrapping the representation
 * of the declaration description of a type.
 *
 * @see IMapping
 * @see IType
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class JavaTypeDeclaration implements ITypeDeclaration
{
	private JavaType type;

	/**
	 * Creates a new <code>JavaTypeDeclaration</code>.
	 *
	 * @param type The type TODO
	 */
	JavaTypeDeclaration(JavaType type)
	{
		super();
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<IType> parameterTypes()
	{
		List<IType> types = new ArrayList<IType>();

		// TODO
//		for (TypeVariable<?> typeVariable : type.getType().getTypeParameters())
//		{
//			typeVariable.getBounds()
//		}

		return types.iterator();
	}
}