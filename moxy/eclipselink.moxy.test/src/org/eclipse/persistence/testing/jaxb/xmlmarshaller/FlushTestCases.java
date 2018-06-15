/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.stream.*;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;


import junit.framework.TestCase;

public class FlushTestCases extends TestCase {

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    @Override
    protected void setUp() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {FlushRoot.class}, null);
        marshaller = jc.createMarshaller();
        unmarshaller = jc.createUnmarshaller();
    }

    public void testXMLEventWriter() throws Exception {
        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLEventWriter xew = xof.createXMLEventWriter(baos);
        marshaller.marshal(control, xew);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLEventReader xer = xif.createXMLEventReader(new ByteArrayInputStream(baos.toByteArray()));
        Object test = unmarshaller.unmarshal(xer);

        assertEquals(control, test);
    }

    public void testXMLStreamWriter() throws Exception {
        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(baos);
        marshaller.marshal(control, xsw);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        Object test = unmarshaller.unmarshal(xsr);

        assertEquals(control, test);
    }

    public void testOutputStreamUTF8() throws Exception {
        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.marshal(control, baos);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        Object test = unmarshaller.unmarshal(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(control, test);
    }

    public void testOutputStreamNotUTF8() throws Exception {
        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
        marshaller.marshal(control, baos);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        Object test = unmarshaller.unmarshal(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(control, test);
    }

    public void testJSONOutputStreamUTF8() throws Exception {
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");

        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.marshal(control, baos);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        Object test = unmarshaller.unmarshal(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(control, test);
    }

    public void testJSONOutputStreamNotUTF8() throws Exception {
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");

        FlushRoot control = getControlObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
        marshaller.marshal(control, baos);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        Object test = unmarshaller.unmarshal(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(control, test);
    }

    public FlushRoot getControlObject() {
        FlushRoot control = new FlushRoot();
        control.id = 123;
        return control;
    }

}
