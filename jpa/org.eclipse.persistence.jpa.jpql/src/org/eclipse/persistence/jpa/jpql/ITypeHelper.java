/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface ITypeHelper {

	/**
	 * Retrieves the;@link IType} for;@link BigDecimal}.
	 *
	 * @return The external form of the <code>BigDecimal</code> class
	 */
	Object bigDecimal();

	/**
	 * Retrieves the;@link IType} for;@link BigInteger}.
	 *
	 * @return The external form of the <code>BigInteger</code> class
	 */
	Object bigInteger();

	/**
	 * Retrieves the;@link IType} for;@link Boolean}.
	 *
	 * @return The external form of the <code>Boolean</code> class
	 */
	Object booleanType();

	/**
	 * Retrieves the;@link IType} for;@link Byte}.
	 *
	 * @return The external form of the <code>Byte</code> class
	 */
	Object byteType();

	/**
	 * Retrieves the;@link IType} for;@link Character}.
	 *
	 * @return The external form of the <code>Character</code> class
	 */
	Object characterType();

	/**
	 * Retrieves the;@link IType} for;@link Collection}.
	 *
	 * @return The external form of the <code>Collection</code> class
	 */
	Object collectionType();

	/**
	 * Converts the given;@link IType}, if it's representing a primitive type, into the class of the
	 * same type.
	 *
	 * @param type Type to possibly convert from the primitive into the class
	 * @return The given;@link IType} if it's not a primitive type otherwise the primitive type will
	 * have been converted into the class of that primitive
	 */
	Object convertPrimitive(Object type);

	/**
	 * Retrieves the;@link IType} for;@link Date}.
	 *
	 * @return The external form of the <code>Date</code> class
	 */
	Object dateType();

	/**
	 * Retrieves the;@link IType} for;@link Double}.
	 *
	 * @return The external form of the <code>Double</code> class
	 */
	Object doubleType();

	/**
	 * Retrieves the;@link IType} for;@link Enum}.
	 *
	 * @return The external form of the <code>Enum</code> class
	 */
	Object enumType();

	/**
	 * Retrieves the;@link IType} for;@link Float}.
	 *
	 * @return The external form of the <code>Float</code> class
	 */
	Object floatType();

	/**
	 * Returns the;@link IType} of the given Java type.
	 *
	 * @param type The Java type for which its external form will be returned
	 * @return The;@link IType} representing the given Java type
	 */
	Object getType(Class<?> type);

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	Object getType(String typeName);

	/**
	 * Retrieves the;@link IType} for;@link Integer}.
	 *
	 * @return The external form of the <code>Integer</code> class
	 */
	Object integerType();

	/**
	 * Determines whether the given;@link IType} is a;@link Boolean}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is a;@link Boolean}; <code>false</code>
	 * otherwise
	 */
	boolean isBooleanType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of;@link Collection}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is an instance of;@link Collection};
	 * <code>false</code> otherwise
	 */
	boolean isCollectionType(Object type);

	/**
	 * Determines whether the given;@link IType} is a;@link Date},;@link Timestamp} or
	 *;@link Calendar}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is a;@link Date},;@link Timestamp} or
	 *;@link Calendar}
	 */
	boolean isDateType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of;@link Enum}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is an instance of;@link Enum};
	 * <code>false</code> otherwise
	 */
	boolean isEnumType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of a floating type, which is either
	 * <code>Float</code>, <code>Double</code>, float or double.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is a floating type; <code>false</code>
	 * otherwise
	 */
	boolean isFloatingType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of a floating type, which is either
	 * <code>Integer</code>, <code>Long</code>, int or float.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is a integral type; <code>false</code>
	 * otherwise
	 */
	boolean isIntegralType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of;@link Map}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is an instance of;@link Map};
	 * <code>false</code> otherwise
	 */
	boolean isMapType(Object type);

	/**
	 * Determines whether the given;@link IType} is an instance of;@link Numeric}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is an instance of;@link Numeric};
	 * <code>false</code> otherwise
	 */
	boolean isNumericType(Object type);

	/**
	 * Determines whether the given;@link IType} is the external form of;@link Object}.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} is the external form of;@link Object}
	 */
	boolean isObjectType(Object type);

	/**
	 * Determines whether the given;@link IType} represents a primitive type.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} represents a primitive; <code>false</code>
	 * otherwise
	 */
	boolean isPrimitiveType(Object type);

	/**
	 * Determines whether the given;@link IType} represents the <code>String</code> class.
	 *
	 * @param type The type to check it's assignability
	 * @return <code>true</code> if the given;@link IType} represents the <code>String</code> class;
	 * <code>false</code> otherwise
	 */
	boolean isStringType(Object type);

	/**
	 * Retrieves the;@link IType} for;@link Long}.
	 *
	 * @return The external form of the <code>Long</code> class
	 */
	Object longType();

	/**
	 * Converts the given;@link IType}, if it's the primitive long, into the <code>Long</code> type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive long or the;@link IType} for the class
	 * <code>Long</code>
	 */
	Object longType(Object type);

	/**
	 * Retrieves the;@link IType} for;@link Map}.
	 *
	 * @return The external form of the <code>Map</code> class
	 */
	Object mapType();

	/**
	 * Retrieves the;@link IType} for;@link Number}.
	 *
	 * @return The external form of the <code>Number</code> class
	 */
	Object numberType();

	/**
	 * Retrieves the;@link IType} for;@link Object}.
	 *
	 * @return The external form of the <code>Object</code> class
	 */
	Object objectType();

	/**
	 * Returns the;@link Object } for the;@link IType} representing the <code>Object</code>
	 * class.
	 *
	 * @return The;@link Object } of the <code>Object</code> class
	 */
	Object  objectTypeDeclaration();

	/**
	 * Retrieves the;@link IType} for the primitive boolean.
	 *
	 * @return The external form of the primitive boolean
	 */
	Object primitiveBoolean();

	/**
	 * Retrieves the;@link IType} for the primitive byte.
	 *
	 * @return The external form of the primitive byte
	 */
	Object primitiveByte();

	/**
	 * Retrieves the;@link IType} for the primitive char.
	 *
	 * @return The external form of the primitive char
	 */
	Object primitiveChar();

	/**
	 * Retrieves the;@link IType} for the primitive double.
	 *
	 * @return The external form of the primitive double
	 */
	Object primitiveDouble();

	/**
	 * Retrieves the;@link IType} for the primitive float.
	 *
	 * @return The external form of the primitive float
	 */
	Object primitiveFloat();

	/**
	 * Retrieves the;@link IType} for the primitive int.
	 *
	 * @return The external form of the primitive int
	 */
	Object primitiveInteger();

	/**
	 * Retrieves the;@link IType} for the primitive long.
	 *
	 * @return The external form of the primitive long
	 */
	Object primitiveLong();

	/**
	 * Retrieves the;@link IType} for the primitive short.
	 *
	 * @return The external form of the primitive short
	 */
	Object primitiveShort();

	/**
	 * Retrieves the;@link IType} for;@link Short}.
	 *
	 * @return The external form of the <code>Short</code> class
	 */
	Object shortType();

	/**
	 * Retrieves the;@link IType} for;@link String}.
	 *
	 * @return The external form of the <code>String</code> class
	 */
	Object stringType();

	/**
	 * Retrieves the;@link IType} for;@link Timestamp}.
	 *
	 * @return The external form of the <code>Timestamp</code> class
	 */
	Object timestampType();

	/**
	 * Converts the given;@link IType}, if it's the primitive boolean, into the <code>Boolean</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive boolean or the;@link IType} for the class
	 * <code>Boolean</code>
	 */
	Object toBooleanType(Object type);

	/**
	 * Converts the given;@link IType}, if it's the primitive byte, into the <code>Byte</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive byte or the;@link IType} for the class
	 * <code>Byte</code>
	 */
	Object toByteType(Object type);

	/**
	 * Converts the given;@link IType}, if it's the primitive double, into the <code>Double</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive double or the;@link IType} for the class
	 * <code>Double</code>
	 */
	Object toDoubleType(Object type);

	/**
	 * Converts the given;@link IType}, if it's the primitive float, into the <code>Float</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive float or the;@link IType} for the class
	 * <code>Float</code>
	 */
	Object toFloatType(Object type);

	/**
	 * Converts the given;@link IType}, if it's the primitive int, into the <code>Integer</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive int or the;@link IType} for the class
	 * <code>Integer</code>
	 */
	Object toIntegerType(Object type);

	/**
	 * Converts the given;@link IType}, if it's the primitive short, into the <code>Short</code>
	 * type.
	 *
	 * @param type The;@link IType} to possibly convert
	 * @return The given type if it's not the primitive short or the;@link IType} for the class
	 * <code>Short</code>
	 */
	Object toShortType(Object type);

	/**
	 * Retrieves the;@link IType} that represents an unknown type.
	 *
	 * @return The external form of an unknown type
	 */
	Object unknownType();

	/**
	 * Returns the;@link Object } for the;@link IType} representing an unknown type.
	 *
	 * @return The;@link Object } of the unknown type
	 */
	Object  unknownTypeDeclaration();
}