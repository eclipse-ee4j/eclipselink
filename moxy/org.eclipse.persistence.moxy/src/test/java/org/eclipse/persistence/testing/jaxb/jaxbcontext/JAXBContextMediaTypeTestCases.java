/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;

/**
 * Test variations of setting/resetting Media Type on the context, marshaller and unmarshaller.
 */
public class JAXBContextMediaTypeTestCases extends TestCase{

    public void testCreateContextWithMediaTypeJSONString() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        Map props = new HashMap();
        props.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        JAXBContext ctx = JAXBContextFactory.createContext(classes, props);

        // TEST DEFAULT MEDIA TYPE
        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));

        // TEST OVERRIDE DEFAULT MEDIA TYPE
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        assertEquals(MediaType.APPLICATION_XML, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/xml");
        assertEquals(MediaType.APPLICATION_XML, u.getProperty(MarshallerProperties.MEDIA_TYPE));

        // TEST DEFATULT MEDIA TYPE AGAIN TO ENSURE IT IS STILL JSON
        JAXBMarshaller m2 = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals(MediaType.APPLICATION_JSON, m2.getProperty(MarshallerProperties.MEDIA_TYPE));
        JAXBUnmarshaller u2 = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals(MediaType.APPLICATION_JSON, u2.getProperty(UnmarshallerProperties.MEDIA_TYPE));
    }

    public void testCreateContextWithMediaTypeJSONEnum() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        Map props = new HashMap();
        props.put(JAXBContextProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        JAXBContext ctx = JAXBContextFactory.createContext(classes, props);

        // TEST DEFAULT MEDIA TYPE
        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));

        // TEST OVERRIDE DEFAULT MEDIA TYPE
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        assertEquals(MediaType.APPLICATION_XML, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/xml");
        assertEquals(MediaType.APPLICATION_XML, u.getProperty(MarshallerProperties.MEDIA_TYPE));
    }

    public void testCreateContextWithNoMediaType() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals(MediaType.APPLICATION_XML, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals(MediaType.APPLICATION_XML, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());
    }

    public void testCreateContextWithNullMediaType() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        Map props = new HashMap();
        props.put(JAXBContextProperties.MEDIA_TYPE, null);

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);

        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals(MediaType.APPLICATION_XML, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals(MediaType.APPLICATION_XML, u.getProperty(UnmarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());

    }

    public void testCreateMarshallerSetMediaTypeJSONString() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_JSON,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
    }

    public void testCreateMarshallerSetMediaTypeJSONEnum() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        assertEquals(MediaType.APPLICATION_JSON, m.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_JSON,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
    }

    public void testCreateUnmarshallerSetMediaTypeJSONString() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Unmarshaller u = ctx.createUnmarshaller();
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_JSON,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());
    }


    public void testCreateUnmarshallerSetMediaTypeJSONEnum() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;

        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Unmarshaller u = ctx.createUnmarshaller();
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        assertEquals(MediaType.APPLICATION_JSON, u.getProperty(MarshallerProperties.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_JSON,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());
    }

}
