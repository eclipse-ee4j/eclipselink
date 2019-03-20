/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.mappings;

import java.util.Map;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;

public interface Mapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    XML_RECORD extends XMLRecord> {

    void convertClassNamesToClasses(ClassLoader classLoader);

    ATTRIBUTE_ACCESSOR getAttributeAccessor();

    /**
     * The classification type for the attribute this mapping represents
     */
    Class getAttributeClassification();

    /**
     * Return the name of the attribute set in the mapping.
     */
    String getAttributeName();

    Object getAttributeValueFromObject(Object object);

    CONTAINER_POLICY getContainerPolicy();

    /**
     * Return the descriptor to which this mapping belongs
     */
    DESCRIPTOR getDescriptor();

    FIELD getField();

    DESCRIPTOR getReferenceDescriptor();

    /**
     * Related mapping should implement this method to return true.
     */
    boolean isAbstractCompositeCollectionMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    boolean isAbstractCompositeDirectCollectionMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    boolean isAbstractCompositeObjectMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    boolean isAbstractDirectMapping();

    boolean isCollectionMapping();

    boolean isReadOnly();

    boolean isReferenceMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    boolean isTransformationMapping();

    /**
     * ADVANCED:
     * Set the attributeAccessor.
     * The attribute accessor is responsible for setting and retrieving the attribute value
     * from the object for this mapping.
     * This can be set to an implementor of AttributeAccessor if the attribute
     * requires advanced conversion of the mapping value, or a real attribute does not exist.
     */
    void setAttributeAccessor(ATTRIBUTE_ACCESSOR attributeAccessor);


    /**
     * Sets the name of the attribute in the mapping.
     */
    void setAttributeName(String attributeName);

    void setAttributeValueInObject(Object object, Object value);

    void writeSingleValue(Object value, Object object, XML_RECORD record, ABSTRACT_SESSION session);
    /**
     * This method is invoked reflectively on the reference object to return the value of the
     * attribute in the object. This method sets the name of the getMethodName.
     */
    void setGetMethodName(String methodName);

    /**
     * Set this mapping to be read only.
     * Read-only mappings can be used if two attributes map to the same field.
     * Read-only mappings cannot be used for the primary key or other required fields.
     */
    void setIsReadOnly(boolean aBoolean);



    /**
     * INTERNAL:
     * Allow user defined properties.
     */
    void setProperties(Map properties);

    /**
     * Set the methodName used to set the value for the mapping's attribute into the object.
     */
    void setSetMethodName(String methodName);

}
