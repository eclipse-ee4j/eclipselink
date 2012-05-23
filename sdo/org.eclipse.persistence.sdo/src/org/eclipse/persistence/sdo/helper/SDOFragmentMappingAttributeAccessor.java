/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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


import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.AttributeAccessor;
import commonj.sdo.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.Node;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import commonj.sdo.helper.HelperContext;
import org.w3c.dom.Document;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDODataObject;

import org.eclipse.persistence.platform.xml.XMLPlatform;

/**
 * <p><b>Purpose</b>: A wrapper class for handling cases when the domain object attributes are
 * to be accessed thru the accessor methods that are called "get" and "set". This is to be used
 * when marsalling/unmarshalling SDODataObjects. The propertyName is the name of the property on
 * the DataObject and that must be set on this accessor.
 */
public class SDOFragmentMappingAttributeAccessor extends AttributeAccessor {
    protected SDOProperty property;
    protected HelperContext helperContext;

    public SDOFragmentMappingAttributeAccessor(SDOProperty property, HelperContext helper) {
        this.property = property;
        this.helperContext = helper;
    }

    /**
     * Gets the value of an instance variable in the object.
     */
    public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
        Object attributeValue = ((SDODataObject)anObject).get(property);
        if (attributeValue != null) {
            if (property.isMany()) {
                //  handle collection case
                ArrayList<Object> fragments = new ArrayList<Object>();
                Iterator<Object> objects = ((Collection)attributeValue).iterator();                
                while (objects.hasNext()) {
                    fragments.add(buildFragment(property, (SDODataObject)anObject, ((SDODataObject)objects.next())));
                }
                attributeValue = fragments;                
            } else {
                attributeValue = buildFragment(property, (SDODataObject)anObject, (SDODataObject)attributeValue);
            }
        }

        return attributeValue;
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
        XMLUnmarshaller unmarshaller = ((SDOXMLHelper)helperContext.getXMLHelper()).getXmlContext().createUnmarshaller();
        unmarshaller.setUnmarshalListener(new org.eclipse.persistence.sdo.helper.SDOCSUnmarshalListener(helperContext));

        if (attributeValue instanceof Collection) {
            //handle collection case
            ArrayList result = new ArrayList();
            Iterator fragments = ((Collection)attributeValue).iterator();
            while (fragments.hasNext()) {
                Node next = (Node)fragments.next();

                //Handle Simple Case here
                Object dataObject = unmarshaller.unmarshal(next);
                if(dataObject instanceof org.eclipse.persistence.oxm.XMLRoot) {
                    dataObject = ((XMLRoot)dataObject).getObject();
                }
                result.add(dataObject);
            }
            ((SDODataObject)domainObject).set(property, result);
        } else {
            Object result = null;
            if (!(attributeValue == null)) {
                Node value = (Node)attributeValue;
                result = unmarshaller.unmarshal(value);
            if(result instanceof org.eclipse.persistence.oxm.XMLRoot) {
                    result = ((XMLRoot)result).getObject();
                }
            }
            ((SDODataObject)domainObject).set(property, result);
        }
    }

    /**
      * INTERNAL:
      */
    public void setProperty(SDOProperty property) {
        this.property = property;
    }

    /**
     * INTERNAL:
     */
    public SDOProperty getPropertyName() {
        return property;
    }

    private Object buildFragment(Property property, SDODataObject parentObject, SDODataObject value) {
        //build an XML Fragment from this SDO
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        XMLMarshaller xmlMarshaller = ((SDOXMLHelper)helperContext.getXMLHelper()).getXmlContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        XMLRoot root = new XMLRoot();
        root.setObject(value);
        root.setLocalName(property.getName());
        if(((SDOProperty)property).isNamespaceQualified()){
          root.setNamespaceURI(parentObject.getType().getURI());
        }
        xmlMarshaller.marshal(root, doc);
        return doc.getDocumentElement();
    }
}
