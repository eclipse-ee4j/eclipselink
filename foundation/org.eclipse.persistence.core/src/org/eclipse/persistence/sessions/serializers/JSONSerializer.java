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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.sessions.Session;

/**
 * Uses EclipseLink Moxy to convert an object to JSON.
 * @author James Sutherland
 */
public class JSONSerializer extends XMLSerializer {
    
    public byte[] serialize(Object object, Session session) {
        try {
            if (this.context == null) {
                String packageName = object.getClass().getPackage().getName();
                this.context = JAXBContext.newInstance(packageName);
            }
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty("eclipselink.media-type", "application/json");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            marshaller.marshal(object, stream);
            return stream.toByteArray();
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public Object deserialize(byte[] bytes, Session session) {
        try {
            if (this.context == null) {
                String packageName = session.getDescriptors().keySet().iterator().next().getPackage().getName();
                this.context = JAXBContext.newInstance(packageName);
            }
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            unmarshaller.setProperty("eclipselink.media-type", "application/json");
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            return unmarshaller.unmarshal(stream);
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
        
    public String toString() {
        return getClass().getSimpleName();
    }
}
