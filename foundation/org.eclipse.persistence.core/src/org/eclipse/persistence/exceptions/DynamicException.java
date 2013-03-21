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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.exceptions;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Custom exception type that provides information about failure cases
 * encountered when using a GenericEntity with TopLink. Any failures that are
 * not specific to GenericEntity use will still involve the standard TopLink
 * exceptions.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicException extends EclipseLinkException {

    public final static int INVALID_PROPERTY_NAME = 51000;
    public final static int INVALID_PROPERTY_GET_WRONG_TYPE = 51001;
    public final static int INVALID_PROPERTY_SET_WRONG_TYPE = 51002;
    public final static int INVALID_PROPERTY_INDEX = 51003;
    public final static int ILLEGAL_DYNAMIC_CLASSWRITER = 51004;
    public final static int DYNAMIC_ENTITY_NOT_FOUND = 51005;
    public final static int DYNAMIC_ENTITY_HAS_NULL_TYPE = 51006;
    public final static int ILLEGAL_PARENT_CLASSNAME = 51007;
    public final static int INCOMPATIBLE_DYNAMIC_CLASSWRITERS = 51008;

    protected DynamicException(String message) {
        super(message);
    }

    protected DynamicException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * A request to get a persistent value from a DynamicEntity was made
     * providing a propertyName that does not correspond to any mappings in the
     * underlying descriptor.
     * 
     * @see DynamicEntity#get(String)
     * */
    public static DynamicException invalidPropertyName(DynamicType type, String propertyName) {
        DynamicException de = new DynamicException("Invalid DynamicEntity[" + type + "] property name: " + propertyName);
        de.setErrorCode(INVALID_PROPERTY_NAME);
        return de;
    }

    /**
     * A request to get a persistent value from a DynamicEntity was made
     * providing a propertyName that does exist but the provided return type
     * failed when casting. The generic type specified on the get method must be
     * supported by the underlying value stored in the dynamic entity.
     * 
     * @see DynamicEntity#get(String)
     */
    public static DynamicException invalidGetPropertyType(DatabaseMapping mapping, ClassCastException cce) {
        DynamicException de = new DynamicException("DynamicEntity:: Cannot return: " + mapping + ": " + cce.getMessage(), cce);
        de.setErrorCode(INVALID_PROPERTY_GET_WRONG_TYPE);
        return de;
    }

    /**
     * Invalid value attempted to be set into a {@link DynamicEntity}'s
     * property. This could be caused by:
     * <ul>
     * <li>Putting null into an property which is classified as primitive or a
     * collection
     * <li>Putting a value into a property that cannot be assigned to the
     * configured classification
     * </ul>
     * 
     * @param mapping
     * @param value
     * @return
     */
    public static DynamicException invalidSetPropertyType(DatabaseMapping mapping, Object value) {
        DynamicException de = new DynamicException("DynamicEntity:: Cannot set: " + mapping + " with: " + value);
        de.setErrorCode(INVALID_PROPERTY_SET_WRONG_TYPE);
        return de;
    }

    /**
     * Exception throw when attempting to access a dynamic property by index
     * which does not have an associated mapping. Make sure the index used is
     * less then {@link DynamicType#getNumberOfProperties()}.
     * 
     * @see DynamicTypeImpl#getMapping(int)
     */
    public static DynamicException invalidPropertyIndex(DynamicType type, int propertyIndex) {
        DynamicException de = new DynamicException("Invalid DynamicEntity[" + type + "] property index: " + propertyIndex);
        de.setErrorCode(INVALID_PROPERTY_INDEX);
        return de;
    }

    /**
     * A {@link DynamicClassWriter} was attempted to be instantiated with a null
     * loader or invalid parentClassName. The parentClassName must not be null
     * or an empty string.
     */
    public static DynamicException illegalDynamicClassWriter(DynamicClassLoader loader, String parentClassName) {
        DynamicException de = new DynamicException("Illegal DynamicClassWriter(" + loader + ", " + parentClassName + ")");
        de.setErrorCode(ILLEGAL_DYNAMIC_CLASSWRITER);
        return de;
    }

    /**
     * A {@link DynamicEntity} could not be found
     */
    public static DynamicException entityNotFoundException(String message) {
        DynamicException de = new DynamicException("DynamicEntity not found: " + message);
        de.setErrorCode(DYNAMIC_ENTITY_NOT_FOUND);
        return de;
    }

    /**
     * The {@link DynamicEntity} has a null type indicating an illegal state
     * of the entity.
     * 
     * @see DynamicEntityImpl#getType()
     */
    /*
     * This should not happen in the current implementation but may be supported
     * when detachment through serialization is added.
     */
    public static DynamicException entityHasNullType(DynamicEntity entity) {
        DynamicException de = new DynamicException("DynamicEntity has null type: " + entity);
        de.setErrorCode(DYNAMIC_ENTITY_HAS_NULL_TYPE);
        return de;
    }

    /**
     * A null or empty string was provided as the parent class for a dynamic
     * class being registered for creation.
     * 
     * @see DynamicClassWriter(String)
     */
    public static DynamicException illegalParentClassName(String parentClassName) {
        DynamicException de = new DynamicException("Illegal parent class name for dynamic type: " + parentClassName);
        de.setErrorCode(ILLEGAL_PARENT_CLASSNAME);
        return de;
    }

    /**
     * A call to {@link DynamicClassLoader#addClass(String, DynamicClassWriter)}
     * or
     * {@link DynamicClassLoader#creatDynamicClass(String, DynamicClassWriter)}
     * was invoked with a className that already had a
     * {@link DynamicClassWriter} that is not compatible with the provided
     * writer.
     */
    public static DynamicException incompatibleDuplicateWriters(String className, EclipseLinkClassWriter existingWriter, EclipseLinkClassWriter writer) {
        DynamicException de = new DynamicException("Duplicate addClass request with incompatible writer: " + className + " - existing: " + existingWriter + " - new: " + writer);
        de.setErrorCode(INCOMPATIBLE_DYNAMIC_CLASSWRITERS);
        return de;
    }
}
