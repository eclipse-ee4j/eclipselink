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
package org.eclipse.persistence.jpa.jpql;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The concrete implementation of {@link ITypeDeclaration} that is wrapping the representation
 * of the declaration description of a type.
 *
 * @see IMapping
 * @see IType
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaTypeDeclaration implements ITypeDeclaration {

	/**
	 * Determines whether the type declaration represents an array.
	 */
	private boolean array;

	/**
	 * The actual type that contains the generics, if any is present.
	 */
	private Object genericType;

	/**
	 * The cached {@link ITypeDeclaration ITypeDeclarations} representing the generics of the {@link
	 * Type}.
	 */
	private ITypeDeclaration[] genericTypes;

	/**
	 * The external form of the Java type.
	 */
	private final IType type;

	/**
	 * The repository of {@link IType ITypes}.
	 */
	private ITypeRepository typeRepository;

	/**
	 * Creates a new <code>JavaTypeDeclaration</code>.
	 *
	 * @param typeRepository The repository of {@link IType ITypes}
	 * @param type The external form of the Java type
	 * @param genericType The actual type that contains the generics, if any is present
	 * @param array Determines whether the type declaration represents an array
	 */
	JavaTypeDeclaration(ITypeRepository typeRepository,
	                    IType type,
	                    Object genericType,
	                    boolean array) {

		super();
		this.type           = type;
		this.array          = array;
		this.genericType    = genericType;
		this.typeRepository = typeRepository;
	}

	private String buildArrayTypeName(String arrayTypeName) {

		StringBuilder sb = new StringBuilder();
		int index = arrayTypeName.indexOf('[');
		int dimensionality = (arrayTypeName.length() - index) / 2;
		String typeName = arrayTypeName.substring(0, index);

		while (--dimensionality >= 0) {
			sb.append("[");
		}

		String elementType = elementType(typeName);

		sb.append(elementType);
		sb.append(typeName);

		if (elementType.equals("L")) {
			sb.append(";");
		}

		return sb.toString();
	}

	private ITypeDeclaration[] buildParameterTypes() {

		List<ITypeDeclaration> parameterTypes = new ArrayList<ITypeDeclaration>();

		// Example: Class<T>
		if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			for (Type type : parameterizedType.getActualTypeArguments()) {
				ITypeDeclaration typeParameter = buildTypeDeclaration(type);
				parameterTypes.add(typeParameter);
			}
		}
		// T[]
		else if (genericType instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) genericType;
			parameterTypes.add(buildTypeDeclaration(genericArrayType.getGenericComponentType()));
		}
		// Example: Class
		else if (genericType.getClass() == Class.class) {
			ITypeDeclaration typeParameter = buildTypeDeclaration((Class<?>) genericType);
			parameterTypes.add(typeParameter);
		}
		// Example: <K, V>
		else if (genericType.getClass() == Class[].class) {
			for (Class<?> javaType : ((Class<?>[]) genericType)) {
				ITypeDeclaration typeParameter = buildTypeDeclaration(javaType);
				parameterTypes.add(typeParameter);
			}
		}
		// Example: <K, V>
		else if (genericType.getClass() == IType[].class) {
			for (IType type : ((IType[]) genericType)) {
				ITypeDeclaration typeParameter = new JavaTypeDeclaration(typeRepository, type, null, false);
				parameterTypes.add(typeParameter);
			}
		}

		return parameterTypes.toArray(new ITypeDeclaration[parameterTypes.size()]);
	}

	private JavaTypeDeclaration buildTypeDeclaration(Class<?> javaType) {
		return new JavaTypeDeclaration(
			typeRepository,
			getType(javaType),
			null,
			javaType.isArray()
		);
	}

	private JavaTypeDeclaration buildTypeDeclaration(Object genericType) {

		// <T1, ..., Tn>
		if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			return buildTypeDeclaration(parameterizedType.getRawType());
		}

		// <T>
		if (genericType instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) genericType;
			for (Type type : typeVariable.getBounds()) {
				return buildTypeDeclaration(type);
			}
			return buildTypeDeclaration(Object.class);
		}

		// ?
		if (genericType instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) genericType;
			for (Type type : wildcardType.getUpperBounds()) {
				return buildTypeDeclaration(type);
			}
			return buildTypeDeclaration(Object.class);
		}

		// T[]
		if (genericType instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) genericType;
			String arrayTypeName = buildArrayTypeName(genericArrayType.toString());
			IType arrayType = typeRepository.getType(arrayTypeName);

			return new JavaTypeDeclaration(
				typeRepository,
				arrayType,
				genericArrayType.getGenericComponentType(),
				true
			);
		}

		return buildTypeDeclaration((Class<?>) genericType);
	}

	private String elementType(String typeName) {

		if (typeName.equals("boolean")) return "Z";
		if (typeName.equals("byte"))    return "B";
		if (typeName.equals("char"))    return "C";
		if (typeName.equals("double"))  return "D";
		if (typeName.equals("float"))   return "F";
		if (typeName.equals("int"))     return "I";
		if (typeName.equals("long"))    return "J";
		if (typeName.equals("short"))   return "S";

		return "L";
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDimensionality() {
		if (array) {
			String name = type.getName();
			int index = 0;
			while (name.charAt(index) == '[') {
				index++;
			}
			return index;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return type;
	}

	private IType getType(Class<?> type) {
		return typeRepository.getType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration[] getTypeParameters() {
		if (genericTypes == null) {
			if (genericType == null) {
				genericTypes = new ITypeDeclaration[0];
			}
			else {
				genericTypes = buildParameterTypes();
			}
		}
		return genericTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isArray() {
		return array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return (genericType != null) ? genericType.toString() : type.toString();
	}
}