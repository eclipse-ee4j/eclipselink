/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// bdoughan - August 5/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.sessions.Session;

/**
 * Convert between instances of XMLRoot and JAXBElement
 */
public class JAXBElementRootConverter implements XMLConverter {

    private Class declaredType;
    private XMLConverter nestedConverter;

    public JAXBElementRootConverter(Class declaredType) {
        this.declaredType = declaredType;
    }

    public Converter getNestedConverter() {
        return nestedConverter;
    }

    public void setNestedConverter(XMLConverter nestedConverter) {
        this.nestedConverter = nestedConverter;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        if(null != nestedConverter) {
            nestedConverter.initialize(mapping, session);
        }
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return this.convertDataValueToObjectValue(dataValue, session, null);
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller) {
        if(null != nestedConverter) {
            dataValue = nestedConverter.convertDataValueToObjectValue(dataValue, session, unmarshaller);
        }
        if(dataValue instanceof JAXBElement) {
            return dataValue;
        } else if(dataValue instanceof Root) {
            Root root = (Root)dataValue;
            QName name = new QName(root.getNamespaceURI(), root.getLocalName());
            dataValue = root.getObject();
            if(null == dataValue) {
                return createJAXBElement(name, Object.class, null);
            }else{
                return createJAXBElement(name, declaredType, dataValue);
            }
        }
        return dataValue;
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return this.convertObjectValueToDataValue(objectValue, session, null);
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller) {
        if(null != nestedConverter) {
            objectValue = nestedConverter.convertObjectValueToDataValue(objectValue, session, marshaller);
        }

        if(objectValue instanceof JAXBElement) {
            ClassDescriptor desc = session.getDescriptor(objectValue);
            if(desc == null || objectValue instanceof WrappedValue){
                JAXBElement element = (JAXBElement) objectValue;
                Root root = new XMLRoot();
                root.setLocalName(element.getName().getLocalPart());
                root.setNamespaceURI(element.getName().getNamespaceURI());
                root.setObject(element.getValue());
                root.setDeclaredType(element.getDeclaredType());
                root.setNil(element.isNil());
                return root;
            }
        }
        return objectValue;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    private JAXBElement createJAXBElement(QName qname, Class theClass, Object value){
        if(value != null && value instanceof JAXBElement){
            return (JAXBElement)value;
        }
        if(CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(theClass)){
            theClass = CoreClassConstants.XML_GREGORIAN_CALENDAR;
        }else if(CoreClassConstants.DURATION.isAssignableFrom(theClass)){
            theClass = CoreClassConstants.DURATION;
        }
        return new JAXBElement(qname, theClass, value);
    }

}
