/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.core.mappings;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

import java.util.List;

/**
 * INTERNAL
 * A abstraction of mapping capturing behavior common to all persistence types.
 */
public abstract class CoreMapping<
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    ABSTRACT_SESSION extends CoreAbstractSession,
    CONTAINER_POLICY extends CoreContainerPolicy,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField> {

    /**
     * Default constructor.
     */
    protected CoreMapping() {
    }

    /**
     * ADVANCED: Return the attributeAccessor. The attribute accessor is
     * responsible for setting and retrieving the attribute value from the
     * object for this mapping.
     *
     * @return TODO
     */
    public abstract ATTRIBUTE_ACCESSOR getAttributeAccessor();

    /**
     * PUBLIC: The classification type for the attribute this mapping represents
     *
     * @return TODO
     */
    public abstract Class<?> getAttributeClassification();

    /**
     * PUBLIC:
     * Return the name of the attribute set in the mapping.
     *
     * @return TODO
     */
    public abstract String getAttributeName();

    /**
     * INTERNAL: Return the value of an attribute which this mapping represents
     * for an object.
     *
     * @param object TODO
     * @return  TODO
     */
    public abstract Object getAttributeValueFromObject(Object object);

    /**
     * INTERNAL: Return the mapping's containerPolicy.
     *
     * @return TODO
     */
    public abstract CONTAINER_POLICY getContainerPolicy();

    /**
     * INTERNAL: Return the descriptor to which this mapping belongs
     *
     * @return TODO
     */
    public abstract DESCRIPTOR getDescriptor();

    /**
     * INTERNAL:
     * Return the field associated with this mapping if there is exactly one.
     * This is required for object relational mapping to print them, but because
     * they are defined in an Enterprise context they cannot be cast to.
     * Mappings that have a field include direct mappings and object relational mappings.
     *
     * @return TODO
     */
    public abstract FIELD getField();

    /**
     * INTERNAL:
     * Returns a list of all the fields this mapping represents.
     *
     * @return TODO
     */
    public abstract List<FIELD> getFields();

    /**
     * PUBLIC:
     * Return the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     *
     * @return TODO
     */
    public abstract DESCRIPTOR getReferenceDescriptor();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isAbstractCompositeCollectionMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isAbstractCompositeDirectCollectionMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isAbstractCompositeObjectMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isAbstractDirectMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isCollectionMapping();


    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isDirectToFieldMapping();

    /**
     * INTERNAL:
     * Returns true if mapping is read only else false.
     *
     * @return TODO
     */
    public abstract boolean isReadOnly();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isReferenceMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     *
     * @return TODO
     */
    public abstract boolean isTransformationMapping();

    /**
     * INTERNAL:
     * Some mappings support no attribute (transformation and multitenant primary key).
     *
     * @return TODO
     */
    public abstract boolean isWriteOnly();

    /**
     * ADVANCED: Set the attributeAccessor. The attribute accessor is
     * responsible for setting and retrieving the attribute value from the
     * object for this mapping. This can be set to an implementor of
     * AttributeAccessor if the attribute requires advanced conversion of the
     * mapping value, or a real attribute does not exist.
     *
     * @param attributeAccessor TODO
     */
    public abstract void setAttributeAccessor(ATTRIBUTE_ACCESSOR attributeAccessor);

    /**
     * PUBLIC:
     * Sets the name of the attribute in the mapping.
     *
     * @param attributeName TODO
     */
    public abstract void setAttributeName(String attributeName);

    /**
     * INTERNAL:
     * Set the value of the attribute mapped by this mapping.
     *
     * @param object TODO
     * @param value TODO
     */
    public abstract void setAttributeValueInObject(Object object, Object value);

    /**
     * INTERNAL:
     * Set the descriptor to which this mapping belongs
     *
     * @param descriptor TODO
     */
    public abstract void setDescriptor(DESCRIPTOR descriptor);

    /**
     * INTERNAL:
     * Set the mapping's field collection.
     *
     * @param fields TODO
     */
    protected abstract void setFields(List<FIELD> fields);

    /**
     * INTERNAL:
     * A subclass should extract the value from the object for the field, if it does not map the field then
     * it should return null.
     * Return the Value from the object.
     *
     * @param anObject TODO
     * @param field TODO
     * @param session TODO
     * @return TODO
     */
    public abstract Object valueFromObject(Object anObject, FIELD field, ABSTRACT_SESSION session);

}
