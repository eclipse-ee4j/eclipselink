/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Specify type helper methods.
 */
public interface TypeHelper {

    /** Returns the name of the specified type. */
    public String getTypeName(Object type);
    
    /** Returns the class object of the specified type. */
    public Class getJavaClass(Object type);

    /** Returns a type representation for the specified type name or null if
     * there is no such type. */
    public Object resolveTypeName(String typeName);

    /** Returns the type of the attribute with the specified name in the
     * specified owner class. */ 
    public Object resolveAttribute(Object ownerClass, String attribute);
    
    /**
     * Returns a query key associated with the name of the attribute
     */
    public QueryKey resolveQueryKey(Object ownerClass, String attribute);

    /** Returns the type of the map key for the mapping on ownerClass named attribute
     * Returns null if that mapping does not exist or does not contain a map key
     */ 
    public Object resolveMapKey(Object ownerClass, String attribute);

    /** Returns the type of the class corresponding to the specified abstract
     * schema type. */
    public Object resolveSchema(String schemaName);

    /** Returns the enum constant if the specified type denotes an enum type
     * and the specified constant denotes a constant of the enum type. */
    public Object resolveEnumConstant(Object enumType, String constant);

    /** Returns the type representation of class Object.*/
    public Object getObjectType();

    /** Returns the boolean type representation.*/
    public Object getBooleanType();

    /** Returns the char type representation.*/
    public Object getCharType();
    
    /** Returns the char type representation.*/
    public Object getSQLDateType();
    
    /** Returns the char type representation.*/
    public Object getTimeType();
    
    /** Returns the char type representation.*/
    public Object getTimestampType();

    /** Returns the int type representation.*/
    public Object getIntType();

    /** Returns the long type representation.*/
    public Object getLongType();

    /** Returns the type representation of class Long.*/
    public Object getLongClassType();

    /** Returns the type representation of class Map.Entry.*/
    public Object getMapEntryType();
    
    /** Returns the float type representation.*/
    public Object getFloatType();

    /** Returns the double type representation.*/
    public Object getDoubleType();

    /** Returns the type representation of class Double.*/
    public Object getDoubleClassType();

    /** Returns the type representation oc class String.*/
    public Object getStringType();

    /** Returns the type representation of class BigInteger.*/
    public Object getBigIntegerType();

    /** Returns the type representation of class BigDecimal.*/
    public Object getBigDecimalType();

    /** Returns true if the specified type denotes an enum type. */
    public boolean isEnumType(Object type);

    /** Returns true if the specified type represents an
     * integral type (or wrapper), a floating point type (or wrapper),
     * BigInteger or BigDecimal. */
    public boolean isNumericType(Object type);

    /** Returns true if the specified type represents an
     * integral type or a wrapper class of an integral type. */
    public boolean isIntegralType(Object type);
    
    /** Returns true if the specified type represents an floating point type
     * or a wrapper class of an floating point type. */ 
    public boolean isFloatingPointType(Object type);

    /** Returns true if the specified type represents java.lang.String. */
    public boolean isStringType(Object type);

    /** Returns true if the specified type represents java.math.BigInteger. */
    public boolean isBigIntegerType(Object type);

    /** Returns true if the specified type represents java.math.BigDecimal. */
    public boolean isBigDecimalType(Object type);

    /** Returns true if the specified type denotes an orderable type. */
    public boolean isOrderableType(Object type);

    /** Returns true if the specified type denotes an entity class. */
    public boolean isEntityClass(Object type);

    /** Returns true if the specified type denotes an embedded class. */
    public boolean isEmbeddable(Object type);
    
    /** Returns true if the specified type denotes an embedded attribute. */
    public boolean isEmbeddedAttribute(Object ownerClass, String attribute);

    /** Returns true if the specified type denotes a simple state attribute. */
    public boolean isSimpleStateAttribute(Object ownerClass, String attribute);
    
    /** Returns true if the specified attribute denotes a single valued
     * or collection valued relationship attribute. 
     */
    public boolean isRelationship(Object ownerClass, String attribute);
    
    /** Returns true if the specified attribute denotes a single valued
     * relationship attribute. 
     */
    public boolean isSingleValuedRelationship(
        Object ownerClass, String attribute);
    
    /** Returns true if the specified attribute denotes a collection valued 
     * relationship attribute. 
     */
    public boolean isCollectionValuedRelationship(
        Object ownerClass, String attribute);
    
    /** Returns true if left is assignable from right. */
    public boolean isAssignableFrom(Object left, Object right);

    /** Binary numeric promotion as specified in the JLS, extended by
     * wrapper classes, BigDecimal and BigInteger.  */
    public Object extendedBinaryNumericPromotion(Object left, Object right);
}
