/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;

/**
 * <p><b>Purpose</b>: A wrapper class for handling cases when the domain object attributes are
 * to be accessed thru the accessor methods that are called "get" and "set". This is to be used
 * when marshalling/unmarshalling SDODataObjects. The propertyName is the name of the property on
 * the DataObject and that must be set on this accessor.
 */
public class SDOMethodAttributeAccessor extends MethodAttributeAccessor {
    protected Class attributeClass;
    protected SDOProperty property;

    public SDOMethodAttributeAccessor(Property property) {
        setProperty(property);
    }

    public SDOMethodAttributeAccessor(Property property, Class attributeClass) {
        setProperty(property);
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
        
        boolean isSet = ((DataObject)anObject).isSet(getProperty());
        if (!isSet) {
            return null;
        }

        return ((DataObject)anObject).get(getProperty());
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
        ((SDODataObject)domainObject).setInternal(property, attributeValue, false);

    }

   /**
     * INTERNAL:
     */
    public void setProperty(Property property) {
        this.property = (SDOProperty) property;
    }

    /**
     * INTERNAL:
     */
    public Property getProperty() {
        return property;
    }

    public Class getGetMethodReturnType() {
        return attributeClass;
    }

    public Class getSetMethodParameterType() {
        return attributeClass;
    }

}
