/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.eclipse.persistence.sessions.Session;

import java.io.StringReader;
import java.io.StringWriter;

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

    @Override
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

    @Override
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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
