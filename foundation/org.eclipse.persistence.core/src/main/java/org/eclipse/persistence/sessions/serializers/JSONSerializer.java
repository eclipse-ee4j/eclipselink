/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
