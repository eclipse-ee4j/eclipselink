/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * This helper contains methods related to {@link IType} and can perform equivalency checks.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class TypeHelper {

	/**
	 * The {@link IType} for the <code>Object</code> class.
	 */
	private IType objectType;

	/**
	 * The {@link IType} for the <code>String</code> class.
	 */
	private IType stringType;

	/**
	 * The external form of the provider of {@link IType ITypes}.
	 */
	private final ITypeRepository typeRepository;

	/**
	 * The {@link IType} representing an unknown type.
	 */
	private IType unknownType;

	/**
	 * Creates a new <code>TypeHelper</code>.
	 *
	 * @param typeRepository The repository used to retrieve the types
	 */
	public TypeHelper(ITypeRepository typeRepository) {
		super();
		this.typeRepository = typeRepository;
	}

	/**
	 * Retrieves the {@link IType} for {@link BigDecimal}.
	 *
	 * @return The external form of the <code>BigDecimal</code> class
	 */
	public IType bigDecimal() {
		return getType(BigDecimal.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link BigInteger}.
	 *
	 * @return The external form of the <code>BigInteger</code> class
	 */
	public IType bigInteger() {
		return getType(BigInteger.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Boolean}.
	 *
	 * @return The external form of the <code>Boolean</code> class
	 */
	public IType booleanType() {
		return getType(Boolean.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Byte}.
	 *
	 * @return The external form of the <code>Byte</code> class
	 */
	public IType byteType() {
		return getType(Byte.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Collection}.
	 *
	 * @return The external form of the <code>Collection</code> class
	 */
	public IType collectionType() {
		return getType(Collection.class);
	}

	/**
	 * Converts the given {@link IType}, if it's representing a primitive type, into the class of the
	 * same type.
	 *
	 * @param type Type to possibly convert from the primitive into the class
	 * @return The given {@link IType} if it's not a primitive type otherwise the primitive type will
	 * have been converted into the class of that primitive
	 */
	public IType convertPrimitive(IType type) {

		// byte
		IType newType = toByteType(type);
		if (newType != type) {
			return newType;
		}

		// short
		newType = toShortType(type);
		if (newType != type) {
			return newType;
		}

		// int
		newType = toIntegerType(type);
		if (newType != type) {
			return newType;
		}

		// long
		newType = longType(type);
		if (newType != type) {
			return newType;
		}

		// float
		newType = toFloatType(type);
		if (newType != type) {
			return newType;
		}

		// double
		newType = toDoubleType(type);
		if (newType != type) {
			return newType;
		}

		// boolean
		newType = toBooleanType(type);
		if (newType != type) {
			return newType;
		}

		return type;
	}

	/**
	 * Retrieves the {@link IType} for {@link Date}.
	 *
	 * @return The external form of the <code>Date</code> class
	 */
	public IType dateType() {
		return getType(Date.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Double}.
	 *
	 * @return The external form of the <code>Double</code> class
	 */
	public IType doubleType() {
		return getType(Double.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Enum}.
	 *
	 * @return The external form of the <code>Enum</code> class
	 */
	public IType enumType() {
		return getType(Enum.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Float}.
	 *
	 * @return The external form of the <code>Float</code> class
	 */
	public IType floatType() {
		return getType(Float.class);
	}

	/**
	 * Returns the {@link IType} of the given Java type.
	 *
	 * @param type The Java type for which its external form will be returned
	 * @return The {@link IType} representing the given Java type
	 */
	public IType getType(Class<?> type) {
		return typeRepository.getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	public IType getType(String typeName) {
		return typeRepository.getType(typeName);
	}

	/**
	 * Returns the {@link ITypeRepository} used by this helper
	 *
	 * @return The external form of the provider of {@link IType ITypes}.
	 */
	public ITypeRepository getTypeRepository() {
		return typeRepository;
	}

	/**
	 * Retrieves the {@link IType} for {@link Integer}.
	 *
	 * @return The external form of the <code>Integer</code> class
	 */
	public IType integerType() {
		return getType(Integer.class);
	}

	/**
	 * Determines whether the given {@link IType} is a {@link Boolean}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a {@link Boolean}; <code>false</code>
	 * otherwise
	 */
	public boolean isBooleanType(IType type) {
		return type.equals(booleanType());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Collection}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of {@link Collection};
	 * <code>false</code> otherwise
	 */
	public boolean isCollectionType(IType type) {
		return type.isAssignableTo(collectionType());
	}

	/**
	 * Determines whether the given {@link IType} is a {@link Date}, {@link Timestamp} or
	 * {@link Calendar}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a {@link Date}, {@link Timestamp} or
	 * {@link Calendar}
	 */
	public boolean isDateType(IType type) {
		return type.equals(dateType())      ||
		       type.equals(timestampType()) ||
		       type.equals(getType(Calendar.class));
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Enum}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of {@link Enum};
	 * <code>false</code> otherwise
	 */
	public boolean isEnumType(IType type) {
		return type.isAssignableTo(enumType());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of a floating type, which is either
	 * <code>Float</code>, <code>Double</code>, float or double.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a floating type; <code>false</code>
	 * otherwise
	 */
	public boolean isFloatingType(IType type) {
		return type.equals(floatType())      ||
		       type.equals(doubleType())     ||
		       type.equals(primitiveFloat()) ||
		       type.equals(primitiveDouble());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of a floating type, which is either
	 * <code>Integer</code>, <code>Long</code>, int or float.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is a integral type; <code>false</code>
	 * otherwise
	 */
	public boolean isIntegralType(IType type) {
		return type.equals(integerType())      ||
		       type.equals(longType())         ||
		       type.equals(primitiveInteger()) ||
		       type.equals(primitiveLong());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Map}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of {@link Map};
	 * <code>false</code> otherwise
	 */
	public boolean isMapType(IType type) {
		return type.isAssignableTo(mapType());
	}

	/**
	 * Determines whether the given {@link IType} is an instance of {@link Numeric}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is an instance of {@link Numeric};
	 * <code>false</code> otherwise
	 */
	public boolean isNumericType(IType type) {
		return type.isAssignableTo(numberType());
	}

	/**
	 * Determines whether the given {@link IType} is the external form of {@link Object}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} is the external form of {@link Object}
	 */
	public boolean isObjectType(IType type) {
		return type.equals(objectType());
	}

	/**
	 * Determines whether the given {@link IType} represents a primitive type.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} represents a primitive; <code>false</code>
	 * otherwise
	 */
	public boolean isPrimitiveType(IType type) {
		return type == primitiveBoolean() ||
		       type == primitiveByte()    ||
		       type == primitiveDouble()  ||
		       type == primitiveFloat()   ||
		       type == primitiveInteger() ||
		       type == primitiveLong()    ||
		       type == primitiveShort();
	}

	/**
	 * Determines whether the given {@link IType} represents the <code>String</code> class.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given {@link IType} represents the <code>String</code> class;
	 * <code>false</code> otherwise
	 */
	public boolean isStringType(IType type) {
		return type.equals(stringType());
	}

	/**
	 * Retrieves the {@link IType} for {@link Long}.
	 *
	 * @return The external form of the <code>Long</code> class
	 */
	public IType longType() {
		return getType(Long.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive long, into the <code>Long</code> type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive long or the {@link IType} for the class
	 * <code>Long</code>
	 */
	public IType longType(IType type) {
		if (type.equals(primitiveLong())) {
			return longType();
		}
		return type;
	}

	/**
	 * Retrieves the {@link IType} for {@link Map}.
	 *
	 * @return The external form of the <code>Map</code> class
	 */
	public IType mapType() {
		return getType(Map.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Number}.
	 *
	 * @return The external form of the <code>Number</code> class
	 */
	public IType numberType() {
		return getType(Number.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link Object}.
	 *
	 * @return The external form of the <code>Object</code> class
	 */
	public IType objectType() {
		if (objectType == null) {
			objectType = getType(Object.class);
		}
		return objectType;
	}

	/**
	 * Returns the {@link ITypeDeclaration} for the {@link IType} representing the <code>Object</code>
	 * class.
	 *
	 * @return The {@link ITypeDeclaration} of the <code>Object</code> class
	 */
	public ITypeDeclaration objectTypeDeclaration() {
		return objectType().getTypeDeclaration();
	}

	/**
	 * Retrieves the {@link IType} for the primitive boolean.
	 *
	 * @return The external form of the primitive boolean
	 */
	public IType primitiveBoolean() {
		return getType(Boolean.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive byte.
	 *
	 * @return The external form of the primitive byte
	 */
	public IType primitiveByte() {
		return getType(Byte.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive double.
	 *
	 * @return The external form of the primitive double
	 */
	public IType primitiveDouble() {
		return getType(Double.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive float.
	 *
	 * @return The external form of the primitive float
	 */
	public IType primitiveFloat() {
		return getType(Float.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive int.
	 *
	 * @return The external form of the primitive int
	 */
	public IType primitiveInteger() {
		return getType(Integer.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive long.
	 *
	 * @return The external form of the primitive long
	 */
	public IType primitiveLong() {
		return getType(Long.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for the primitive short.
	 *
	 * @return The external form of the primitive short
	 */
	public IType primitiveShort() {
		return getType(Short.TYPE);
	}

	/**
	 * Retrieves the {@link IType} for {@link Short}.
	 *
	 * @return The external form of the <code>Short</code> class
	 */
	public IType shortType() {
		return getType(Short.class);
	}

	/**
	 * Retrieves the {@link IType} for {@link String}.
	 *
	 * @return The external form of the <code>String</code> class
	 */
	public IType stringType() {
		if (stringType == null) {
			stringType = getType(String.class);
		}
		return stringType;
	}

	/**
	 * Retrieves the {@link IType} for {@link Timestamp}.
	 *
	 * @return The external form of the <code>Timestamp</code> class
	 */
	public IType timestampType() {
		return getType(Timestamp.class);
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive boolean, into the <code>Boolean</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive boolean or the {@link IType} for the class
	 * <code>Boolean</code>
	 */
	public IType toBooleanType(IType type) {
		if (type.equals(primitiveBoolean())) {
			return booleanType();
		}
		return type;
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive byte, into the <code>Byte</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive byte or the {@link IType} for the class
	 * <code>Byte</code>
	 */
	public IType toByteType(IType type) {
		if (type.equals(primitiveByte())) {
			return byteType();
		}
		return type;
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive double, into the <code>Double</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive double or the {@link IType} for the class
	 * <code>Double</code>
	 */
	public IType toDoubleType(IType type) {
		if (type.equals(primitiveDouble())) {
			return doubleType();
		}
		return type;
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive float, into the <code>Float</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive float or the {@link IType} for the class
	 * <code>Float</code>
	 */
	public IType toFloatType(IType type) {
		if (type.equals(primitiveFloat())) {
			return floatType();
		}
		return type;
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive int, into the <code>Integer</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive int or the {@link IType} for the class
	 * <code>Integer</code>
	 */
	public IType toIntegerType(IType type) {
		if (type.equals(primitiveInteger())) {
			return integerType();
		}
		return type;
	}

	/**
	 * Converts the given {@link IType}, if it's the primitive short, into the <code>Short</code>
	 * type.
	 *
	 * @param type The {@link IType} to possibly convert
	 * @return The given type if it's not the primitive short or the {@link IType} for the class
	 * <code>Short</code>
	 */
	public IType toShortType(IType type) {
		if (type.equals(primitiveShort())) {
			return shortType();
		}
		return type;
	}

	/**
	 * Retrieves the {@link IType} that represents an unknown type.
	 *
	 * @return The external form of an unknown type
	 */
	public IType unknownType() {
		if (unknownType == null) {
			unknownType = getType(IType.UNRESOLVABLE_TYPE);
		}
		return unknownType;
	}

	/**
	 * Returns the {@link ITypeDeclaration} for the {@link IType} representing an unknown type.
	 *
	 * @return The {@link ITypeDeclaration} of the unknown type
	 */
	public ITypeDeclaration unknownTypeDeclaration() {
		return unknownType().getTypeDeclaration();
	}
}