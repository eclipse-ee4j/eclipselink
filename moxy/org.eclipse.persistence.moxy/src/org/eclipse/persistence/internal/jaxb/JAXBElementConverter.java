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
* bdoughan - July 29/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.sessions.Session;

public class JAXBElementConverter implements XMLConverter {

    private XPathFragment rootFragment;
    private Field associatedField;
    private CoreMapping mapping;
    private CoreConverter nestedConverter;
    private Class declaredType;
    private Class scope;

    public JAXBElementConverter(Field associatedField, Class declaredType, Class scope) {
        this.associatedField = associatedField;
        this.declaredType = declaredType;
        this.scope = scope;
    }

    public CoreConverter getNestedConverter() {
        return nestedConverter;
    }

    public void setNestedConverter(CoreConverter nestedConverter) {
        this.nestedConverter = nestedConverter;
    }

    public Object convertDataValueToObjectValue(Object dataValue,
            Session session, XMLUnmarshaller unmarshaller) {
        return convertDataValueToObjectValue(dataValue, session);
    }

    public Object convertObjectValueToDataValue(Object objectValue,
            Session session, XMLMarshaller marshaller) {
        return convertObjectValueToDataValue(objectValue, session);
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        QName name = new QName(rootFragment.getNamespaceURI(), rootFragment.getLocalName());

        if(mapping.isAbstractDirectMapping() && null == nestedConverter){
            if ((dataValue == null) || (dataValue.getClass() != mapping.getAttributeClassification())) {
                try {
                    dataValue = session.getDatasourcePlatform().convertObject(dataValue, mapping.getAttributeClassification());
                } catch (ConversionException e) {
                    throw ConversionException.couldNotBeConverted(this, mapping.getDescriptor(), e);
                }
            }
        }

        if(null != nestedConverter) {
            dataValue = nestedConverter.convertDataValueToObjectValue(dataValue, session);
        }
        if(dataValue instanceof JAXBElement) {
            return dataValue;
        }
        if(null == declaredType) {
            return new JAXBElement(name, Object.class, scope, dataValue);
        } else {
            return new JAXBElement(name, declaredType, scope, dataValue);
        }
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if(objectValue instanceof JAXBElement) {
        	ClassDescriptor desc = session.getDescriptor(objectValue);
        	if(desc == null || objectValue instanceof WrappedValue){        		
                objectValue = ((JAXBElement)objectValue).getValue();
        	}
        } else if(objectValue instanceof Root) {
            objectValue = ((Root) objectValue).getObject();
        }
        if(null != nestedConverter) {
            objectValue = nestedConverter.convertObjectValueToDataValue(objectValue, session);
        }
        return objectValue;
    }

     public void initialize(DatabaseMapping mapping, Session session) {
        if(null != nestedConverter) {
            nestedConverter.initialize(mapping, session);
        }
        XPathFragment fragment = associatedField.getXPathFragment();
        while(fragment.getNextFragment() != null && !(fragment.getNextFragment().nameIsText())) {
            fragment = fragment.getNextFragment();
        }
        if(fragment.hasNamespace() && associatedField.getNamespaceResolver() != null){
            String uri = associatedField.getNamespaceResolver().resolveNamespacePrefix(fragment.getPrefix());
            fragment.setNamespaceURI(uri);
        }
        this.rootFragment = fragment;
        this.mapping = mapping;
    }

    public boolean isMutable() {
        return false;
    }

}