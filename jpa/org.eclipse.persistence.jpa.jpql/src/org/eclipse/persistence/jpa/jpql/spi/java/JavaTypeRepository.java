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
package org.eclipse.persistence.jpa.jpql.spi.java;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link ITypeRepository} that is wrapping the Java class loader.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JavaTypeRepository implements ITypeRepository {

	/**
	 * The repository used to access the application's classes.
	 */
	private final ClassLoader classLoader;

	/**
	 * The helper that gives access to the most common {@link IType types}.
	 */
	private TypeHelper typeHelper;

	/**
	 * The types that have been cached for faster access.
	 */
	private final Map<String, JavaType> types;

	/**
	 * The {@link IType} that represents a unresolvable or simply an unknown type, which is created
	 * when {@link #getType(String)} is invoked with {@link IType#UNRESOLVABLE_TYPE}.
	 */
	private JavaType unresolvableType;

	/**
	 * Creates a new <code>JavaTypeRepository</code>.
	 *
	 * @param classLoader The repository used to access the application's classes
	 */
	public JavaTypeRepository(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
		this.types       = new HashMap<String, JavaType>();
	}

	/**
	 * Retrieves the Java type for the given type name, which has to be the fully qualified type name.
	 *
	 * @param typeName The fully qualified type name
	 * @return The Java type if it could be retrieved; <code>null</code> otherwise
	 */
	protected Class<?> attemptLoadType(String typeName) {

		if (typeName.equals("boolean")) return Boolean  .TYPE;
		if (typeName.equals("byte"))    return Byte     .TYPE;
		if (typeName.equals("char"))    return Character.TYPE;
		if (typeName.equals("double"))  return Double   .TYPE;
		if (typeName.equals("float"))   return Float    .TYPE;
		if (typeName.equals("int"))     return Integer  .TYPE;
		if (typeName.equals("long"))    return Long     .TYPE;
		if (typeName.equals("short"))   return Short    .TYPE;

		try {
			return classLoader.loadClass(typeName);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}

	protected JavaType buildType(Class<?> javaType) {
		JavaType type = new JavaType(this, javaType);
		types.put(javaType.getName(), type);
		return type;
	}

	protected JavaType buildType(String typeName) {
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
		IType type = loadTypeImp(typeName);
		return type.isEnum() ? type : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaType getType(Class<?> javaClass) {
		return getType(javaClass.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaType getType(String typeName) {

		if (typeName == IType.UNRESOLVABLE_TYPE) {
			return unresolvableType();
		}

		if (typeName.charAt(0) == '[') {
			return loadArrayType(typeName);
		}

		return loadTypeImp(typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public TypeHelper getTypeHelper() {
		if (typeHelper == null) {
			typeHelper = new TypeHelper(this);
		}
		return typeHelper;
	}

	protected JavaType loadArrayType(String typeName) {

		JavaType type = types.get(typeName);

		if (type == null) {
			try {
				type = new JavaType(this, Class.forName(typeName));
			}
			catch (ClassNotFoundException f) {
				type = new JavaType(this, typeName);
			}
			types.put(typeName, type);
		}

		return type;
	}

	protected JavaType loadInnerType(String typeName) {

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

	protected JavaType loadTypeImp(String typeName) {

		JavaType type = types.get(typeName);

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

		if (type == null) {
			type = buildType(typeName);
		}

		return type;
	}

	protected JavaType unresolvableType() {
		if (unresolvableType == null) {
			unresolvableType = new JavaType(this, IType.UNRESOLVABLE_TYPE);
		}
		return unresolvableType;
	}
}