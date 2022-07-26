/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Specify type helper methods.
 */
public interface TypeHelper {

    /** Returns the name of the specified type. */
    String getTypeName(Object type);

    /** Returns the class object of the specified type. */
    Class<?> getJavaClass(Object type);

    /** Returns a type representation for the specified type name or null if
     * there is no such type. */
    Object resolveTypeName(String typeName);

    /** Returns the type of the attribute with the specified name in the
     * specified owner class. */
    Object resolveAttribute(Object ownerClass, String attribute);

    /**
     * Returns a query key associated with the name of the attribute
     */
    QueryKey resolveQueryKey(Object ownerClass, String attribute);

    /** Returns the type of the map key for the mapping on ownerClass named attribute
     * Returns null if that mapping does not exist or does not contain a map key
     */
    Object resolveMapKey(Object ownerClass, String attribute);

    /** Returns the type of the class corresponding to the specified abstract
     * schema type. */
    Object resolveSchema(String schemaName);

    /** Returns the enum constant if the specified type denotes an enum type
     * and the specified constant denotes a constant of the enum type. */
    Object resolveEnumConstant(Object enumType, String constant);

    /** Returns the type representation of class Object.*/
    Object getObjectType();

    /** Returns the boolean type representation.*/
    Object getBooleanType();

    /** Returns the char type representation.*/
    Object getCharType();

    /** Returns the char type representation.*/
    Object getSQLDateType();

    /** Returns the char type representation.*/
    Object getTimeType();

    /** Returns the char type representation.*/
    Object getTimestampType();

    /** Returns the int type representation.*/
    Object getIntType();

    /** Returns the long type representation.*/
    Object getLongType();

    /** Returns the type representation of class Long.*/
    Object getLongClassType();

    /** Returns the type representation of class Map.Entry.*/
    Object getMapEntryType();

    /** Returns the float type representation.*/
    Object getFloatType();

    /** Returns the double type representation.*/
    Object getDoubleType();

    /** Returns the type representation of class Double.*/
    Object getDoubleClassType();

    /** Returns the type representation oc class String.*/
    Object getStringType();

    /** Returns the type representation of class BigInteger.*/
    Object getBigIntegerType();

    /** Returns the type representation of class BigDecimal.*/
    Object getBigDecimalType();

    /** Returns true if the specified type denotes an enum type. */
    boolean isEnumType(Object type);

    /** Returns true if the specified type represents an
     * integral type (or wrapper), a floating point type (or wrapper),
     * BigInteger or BigDecimal. */
    boolean isNumericType(Object type);

    /** Returns true if the specified type represents an
     * integral type or a wrapper class of an integral type. */
    boolean isIntegralType(Object type);

    /** Returns true if the specified type represents an floating point type
     * or a wrapper class of an floating point type. */
    boolean isFloatingPointType(Object type);

    /** Returns true if the specified type represents java.lang.String. */
    boolean isStringType(Object type);

    /** Returns true if the specified type represents java.math.BigInteger. */
    boolean isBigIntegerType(Object type);

    /** Returns true if the specified type represents java.math.BigDecimal. */
    boolean isBigDecimalType(Object type);

    /** Returns true if the specified type denotes an orderable type. */
    boolean isOrderableType(Object type);

    /** Returns true if the specified type denotes an entity class. */
    boolean isEntityClass(Object type);

    /** Returns true if the specified type denotes an embedded class. */
    boolean isEmbeddable(Object type);

    /** Returns true if the specified type denotes an embedded attribute. */
    boolean isEmbeddedAttribute(Object ownerClass, String attribute);

    /** Returns true if the specified type denotes a simple state attribute. */
    boolean isSimpleStateAttribute(Object ownerClass, String attribute);

    /** Returns true if the specified attribute denotes a single valued
     * or collection valued relationship attribute.
     */
    boolean isRelationship(Object ownerClass, String attribute);

    /** Returns true if the specified attribute denotes a single valued
     * relationship attribute.
     */
    boolean isSingleValuedRelationship(
            Object ownerClass, String attribute);

    /** Returns true if the specified attribute denotes a collection valued
     * relationship attribute.
     */
    boolean isCollectionValuedRelationship(
            Object ownerClass, String attribute);

    /** Returns true if left is assignable from right. */
    boolean isAssignableFrom(Object left, Object right);

    /** Binary numeric promotion as specified in the JLS, extended by
     * wrapper classes, BigDecimal and BigInteger.  */
    Object extendedBinaryNumericPromotion(Object left, Object right);
}
