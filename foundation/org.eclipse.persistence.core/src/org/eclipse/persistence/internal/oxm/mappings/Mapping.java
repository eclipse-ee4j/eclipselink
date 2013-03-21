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

    public void convertClassNamesToClasses(ClassLoader classLoader);

    public ATTRIBUTE_ACCESSOR getAttributeAccessor();

    /**
     * The classification type for the attribute this mapping represents
     */
    public abstract Class getAttributeClassification();

    /**
     * Return the name of the attribute set in the mapping. 
     */
    public abstract String getAttributeName();

    public Object getAttributeValueFromObject(Object object);

    public abstract CONTAINER_POLICY getContainerPolicy();

    /**
     * Return the descriptor to which this mapping belongs
     */
    public DESCRIPTOR getDescriptor();

    public FIELD getField();

    public DESCRIPTOR getReferenceDescriptor();

    /**
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeCollectionMapping();
    
    /**
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeDirectCollectionMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isAbstractCompositeObjectMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    public boolean isAbstractDirectMapping();

    public boolean isCollectionMapping();

    public boolean isReadOnly();

    public boolean isReferenceMapping();

    /**
     * Related mapping should implement this method to return true.
     */
    public abstract boolean isTransformationMapping();

    /**
     * ADVANCED:
     * Set the attributeAccessor.
     * The attribute accessor is responsible for setting and retrieving the attribute value
     * from the object for this mapping.
     * This can be set to an implementor of AttributeAccessor if the attribute
     * requires advanced conversion of the mapping value, or a real attribute does not exist.
     */    
    public void setAttributeAccessor(ATTRIBUTE_ACCESSOR attributeAccessor);
    
    
    /**
     * Sets the name of the attribute in the mapping.
     */    
    public void setAttributeName(String attributeName);
    
    public void setAttributeValueInObject(Object object, Object value);

    public void writeSingleValue(Object value, Object object, XML_RECORD record, ABSTRACT_SESSION session);
    /**
     * This method is invoked reflectively on the reference object to return the value of the
     * attribute in the object. This method sets the name of the getMethodName.
     */
    public void setGetMethodName(String methodName);
    
    /**
     * Set this mapping to be read only.
     * Read-only mappings can be used if two attributes map to the same field.
     * Read-only mappings cannot be used for the primary key or other required fields.
     */
    public void setIsReadOnly(boolean aBoolean);
    

    
    /**
     * INTERNAL:
     * Allow user defined properties.
     */
    public void setProperties(Map properties);
    
    /**
     * Set the methodName used to set the value for the mapping's attribute into the object.
     */
    public void setSetMethodName(String methodName);

}