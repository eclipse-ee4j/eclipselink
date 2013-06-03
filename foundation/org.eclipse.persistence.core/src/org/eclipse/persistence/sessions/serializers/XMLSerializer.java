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
 *      Oracle - initial impl
 ******************************************************************************/
package org.eclipse.persistence.sessions.serializers;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.sessions.Session;

/**
 * Uses JAXB to convert an object to XML.
 * @author James Sutherland
 */
public class XMLSerializer extends AbstractSerializer {
    JAXBContext context;
    
    public XMLSerializer() {
    }
    
    public XMLSerializer(String packageName) {
        try {
            this.context = JAXBContext.newInstance(packageName);
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public XMLSerializer(JAXBContext context) {
        this.context = context;
    }
    
    @Override
    public void initialize(Class serializeClass, String serializePackage, Session session) {
        if (this.context == null) {
            if (serializePackage == null) {
                serializePackage = serializeClass.getPackage().getName();
            }
            try {
                this.context = JAXBContext.newInstance(serializePackage, serializeClass.getClassLoader());
            } catch (JAXBException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
    
    public Object serialize(Object object, Session session) {
        try {
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public Object deserialize(Object xml, Session session) {
        try {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            StringReader reader = new StringReader((String)xml);
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
