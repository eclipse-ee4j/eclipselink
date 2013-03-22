/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.mappings;

import java.util.Vector;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

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
     * ADVANCED: Return the attributeAccessor. The attribute accessor is
     * responsible for setting and retrieving the attribute value from the
     * object for this mapping.
     */
    public abstract ATTRIBUTE_ACCESSOR getAttributeAccessor();

    /**
     * PUBLIC: The classification type for the attribute this mapping represents
     */
    public abstract Class getAttributeClassification();

    /**
     * PUBLIC:
     * Return the name of the attribute set in the mapping. 
     */
    public abstract String getAttributeName();

    /**
     * INTERNAL: Return the value of an attribute which this mapping represents
     * for an object.
     */
    public abstract Object getAttributeValueFromObject(Object object);

    /**
     * INTERNAL: Return the mapping's containerPolicy.
     */
    public abstract CONTAINER_POLICY getContainerPolicy();

    /**
     * INTERNAL: Return the descriptor to which this mapping belongs
     */
    public abstract DESCRIPTOR getDescriptor();

    /**
     * INTERNAL:
     * Return the field associated with this mapping if there is exactly one.
     * This is required for object relational mapping to print them, but because
     * they are defined in in an Enterprise context they cannot be cast to.
     * Mappings that have a field include direct mappings and object relational mappings.
     */
    public abstract FIELD getField();

    /**
     * INTERNAL:
     * Returns a vector of all the fields this mapping represents.
     */
    public abstract Vector<FIELD> getFields();

    /**
     * PUBLIC:
     * Return the referenceDescriptor. This is a descriptor which is associated with
     * the reference class.
     */
    public abstract DESCRIPTOR getReferenceDescriptor();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeCollectionMapping();
    
    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeDirectCollectionMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeObjectMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractDirectMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isCollectionMapping();

    
    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isDirectToFieldMapping();
    
    /**
     * INTERNAL:
     * Returns true if mapping is read only else false.
     */
    public abstract boolean isReadOnly();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isReferenceMapping();

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isTransformationMapping();

    /**
     * INTERNAL:
     * Some mappings support no attribute (transformation and multitenant primary key).
     */
    public abstract boolean isWriteOnly();

    /**
     * ADVANCED: Set the attributeAccessor. The attribute accessor is
     * responsible for setting and retrieving the attribute value from the
     * object for this mapping. This can be set to an implementor of
     * AttributeAccessor if the attribute requires advanced conversion of the
     * mapping value, or a real attribute does not exist.
     */
    public abstract void setAttributeAccessor(ATTRIBUTE_ACCESSOR attributeAccessor);

    /**
     * PUBLIC:
     * Sets the name of the attribute in the mapping.
     */
    public abstract void setAttributeName(String attributeName);

    /**
     * INTERNAL:
     * Set the value of the attribute mapped by this mapping.
     */
    public abstract void setAttributeValueInObject(Object object, Object value);

    /**
     * INTERNAL:
     * Set the descriptor to which this mapping belongs
     */
    public abstract void setDescriptor(DESCRIPTOR descriptor);
    
    /**
     * INTERNAL:
     * Set the mapping's field collection.
     */
    protected abstract void setFields(Vector<FIELD> fields);

    /**
     * INTERNAL:
     * A subclass should extract the value from the object for the field, if it does not map the field then
     * it should return null.
     * Return the Value from the object.
     */
    public abstract Object valueFromObject(Object anObject, FIELD field, ABSTRACT_SESSION session);
 
}