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

/**
 * This default resolver simply holds onto the fully qualified class name since it is already
 * determined.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class ClassNameTypeResolver extends AbstractTypeResolver
{
	/**
	 * The fully qualified name of the type.
	 */
	private final String className;

	/**
	 * Creates a new <code>ClassNameTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param type The fully qualified name of the type
	 */
	ClassNameTypeResolver(TypeResolver parent, String className)
	{
		super(parent);
		this.className = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return getType(className);
	}
}