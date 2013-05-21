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
 * Uses EclipseLink Moxy to convert an object to JSON.
 * @author James Sutherland
 */
public class JSONSerializer extends XMLSerializer {
    
    public JSONSerializer() {
    }
    
    public JSONSerializer(String packageName) {
        super(packageName);
    }
    
    public JSONSerializer(JAXBContext context) {
        super(context);
    }
    
    public Object serialize(Object object, Session session) {
        try {
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty("eclipselink.media-type", "application/json");
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public Object deserialize(Object json, Session session) {
        try {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            unmarshaller.setProperty("eclipselink.media-type", "application/json");
            StringReader reader = new StringReader((String)json);
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }
    }
        
    public String toString() {
        return getClass().getSimpleName();
    }
}
