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
 * The concrete implementation of {@link IType} that is wrapping the Java type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class JavaType implements IType
{
	/**
	 * The fully qualified name of the Java type.
	 */
	private String className;

	/**
	 * The actual Java type.
	 */
	private final Class<?> type;

	/**
	 * Creates a new <code>JavaType</code>.
	 *
	 * @param type The actual Java type
	 */
	JavaType(Class<?> type)
	{
		super();

		this.type      = type;
		this.className = type.getName();
	}

	/**
	 * Creates a new <code>JavaType</code>.
	 *
	 * @param type The actual Java type
	 */
	JavaType(String className)
	{
		super();

		this.type      = null;
		this.className = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(IType type)
	{
		return getName().equals(type.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName()
	{
		return className;
	}

	/**
	 * Returns the encapsulated {@link Class}, which is the actual type.
	 *
	 * @return The actual Java type, if <code>null</code> is returned; then the class could not be resolved
	 */
	Class<?> getType()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration()
	{
		return new JavaTypeDeclaration(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAssignableTo(IType type)
	{
		if (this.type == null)
		{
			return false;
		}

		Class<?> otherType = ((JavaType) type).type;
		return otherType.isAssignableFrom(this.type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResolvable()
	{
		return (type != null);
	}
}