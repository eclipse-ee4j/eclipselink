/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
 * <p><b>Purpose</b>: A wrapper class for handling cases when the domain object attributes are
 * to be accessed thru the accessor methods that are called "get" and "set". This is to be used
 * when marsalling/unmarshalling SDODataObjects. The propertyName is the name of the property on
 * the DataObject and that must be set on this accessor.
 */
public class SDOMethodAttributeAccessor extends AttributeAccessor {
    protected String propertyName;
    protected Class attributeClass;

    public SDOMethodAttributeAccessor(String name) {
        setPropertyName(name);
    }

    public SDOMethodAttributeAccessor(String name, Class attributeClass) {
        setPropertyName(name);
        this.attributeClass = attributeClass;
    }

    /**
      * Return the return type of the method accessor.
      */
    public Class getAttributeClass() {
        if (attributeClass != null) {
            return attributeClass;
        }
        return java.lang.Object.class;
    }

    /**
     * Gets the value of an instance variable in the object.
     */
    public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
        
        boolean isSet = ((DataObject)anObject).isSet(getPropertyName());
        if (!isSet) {
            return null;
        }

        return ((DataObject)anObject).get(getPropertyName());
    }

    /**
     * Set get and set method after creating these methods by using
     * get and set method names
     */
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
        if (getAttributeName() == null) {
            throw DescriptorException.attributeNameNotSpecified();
        }
    }

    /**
     * INTERNAL:   
     */
    public boolean isMethodAttributeAccessor() {
        return true;
    }

    /**
     * Sets the value of the instance variable in the object to the value.
     */
    public void setAttributeValueInObject(Object domainObject, Object attributeValue) throws DescriptorException {
        ((DataObject)domainObject).set(getPropertyName(), attributeValue);

    }

   /**
     * INTERNAL:   
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * INTERNAL:   
     */
    public String getPropertyName() {
        return propertyName;
    }
}