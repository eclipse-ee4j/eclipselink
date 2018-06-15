/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class PhoneNumberWriter implements MessageBodyWriter<PhoneNumber> {

    private JAXBContext jc;

    public PhoneNumberWriter() {
        try {
            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
                    "META-INF/binding-phonenumber.xml");
            jc = JAXBContext.newInstance(new Class[] { PhoneNumber.class },
                    properties);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public long getSize(PhoneNumber t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type == PhoneNumber.class;
    }

    public void writeTo(PhoneNumber t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        try {
            jc.createMarshaller().marshal(t, entityStream);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        }
    }

}
