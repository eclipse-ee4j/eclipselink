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
package org.eclipse.persistence.utils.jpa.query;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * The concrete implementation of {@link ITypeRepository} that is wrapping the Java class loader.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaTypeRepository implements ITypeRepository {

	/**
	 * The repository used to access the application's classes.
	 */
	private final ClassLoader classLoader;

	/**
	 * The types that have been cached for faster access.
	 */
	private final Map<String, IType> types;

	/**
	 * Creates a new <code>JavaTypeRepository</code>.
	 *
	 * @param classRepository The repository used to access the application's classes
	 */
	JavaTypeRepository(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
		this.types       = new HashMap<String, IType>();
	}

	private IType buildType(Class<?> javaType) {
		IType type = new JavaType(this, javaType);
		types.put(javaType.getName(), type);
		return type;
	}

	private IType buildType(String typeName) {
		return new JavaType(this, typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getEnumType(String enumTypeName) {

		// Get the position of the last dot so we can remove the constant
		int lastDotIndex = enumTypeName.lastIndexOf(".");

		if (lastDotIndex == -1) {
			return null;
		}

		// Retrieve the fully qualified enum type name
		String typeName = enumTypeName.substring(0, lastDotIndex);

		// Attempt to load the enum type
		IType type = getType(typeName);
		return type.isEnum() ? type : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType(Class<?> javaClass) {
		return getType(javaClass.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType(String typeName) {
		return loadTypeImp(typeName);
	}

	private IType loadInnerType(String typeName) {

		int index = typeName.lastIndexOf(".");

		if (index == -1) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(typeName.substring(0, index));
		sb.append("$");
		sb.append(typeName.substring(index + 1, typeName.length()));
		typeName = sb.toString();

		return loadTypeImp(typeName);
	}

	/**
	 * Retrieves the Java type for the given type name, which has to be the fully qualified type name.
	 *
	 * @param typeName The fully qualified type name
	 * @return The Java type if it could be retrieved; <code>null</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	private Class<?> attemptLoadType(String typeName) {
		try {
			if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
				try {
					return (Class<?>) AccessController.doPrivileged(new PrivilegedClassForName(typeName, true, classLoader));
				}
				catch (PrivilegedActionException exception) {
					return null;
				}
			}

			return PrivilegedAccessHelper.getClassForName(typeName, true, classLoader);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	private IType loadTypeImp(String typeName) {

		IType type = types.get(typeName);

		// The type was already cached, simply return it
		if (type != null) {
			return type;
		}

		// Attempt to load the Java type
		Class<?> javaType = attemptLoadType(typeName);

		// A Java type exists, return it
		if (javaType != null) {
			return buildType(javaType);
		}

		// Now try with a possible inner enum type
		type = loadInnerType(typeName);

		// No Java type exists, create a "null" IType
		if (type == null) {
			type = buildType(typeName);
		}

		return type;
	}
}