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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * The concrete implementation of {@link ITypeRepository} that is wrapping the Java classloader.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class JavaTypeRepository implements ITypeRepository
{
	/**
	 * The repository used to access the application's classes.
	 */
	private final ClassLoader classLoader;

	/**
	 * The types that have been cached for faster access.
	 */
	private final Map<String, IType> types;

	/**
	 * Creates a new <code>JDeveloperTypeRepository</code>.
	 *
	 * @param classRepository The repository used to access the application's classes
	 */
	JavaTypeRepository(ClassLoader classLoader)
	{
		super();

		this.classLoader = classLoader;
		this.types       = new HashMap<String, IType>();
	}

	private IType buildType(String name)
	{
		try
		{
			Class<?> javaClass = classLoader.loadClass(name);
			return new JavaType(javaClass);
		}
		catch (ClassNotFoundException e)
		{
			return new JavaType(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType(Class<?> javaClass)
	{
		return getType(javaClass.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType(String name)
	{
		IType type = types.get(name);

		if (type == null)
		{
			type = buildType(name);
			types.put(name, type);
		}

		return type;
	}
}