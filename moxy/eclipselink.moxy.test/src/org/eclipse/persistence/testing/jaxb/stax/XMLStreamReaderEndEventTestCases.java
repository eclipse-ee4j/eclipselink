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
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.stax;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class XMLStreamReaderEndEventTestCases extends TestCase {

    private static final String XML = "<root><foo><bar>Hello World</bar></foo></root>";

    private Unmarshaller unmarshaller;
    private XMLStreamReader xsr;

    @Override
    protected void setUp() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {EndEventRoot.class}, null);
        unmarshaller = jc.createUnmarshaller();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        xsr = xif.createXMLStreamReader(new StringReader(XML));
    }

    public void testUnmarshalFromXMLStreamReaderStartDocumentEvent() throws Exception {
        assertEquals(XMLStreamReader.START_DOCUMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalFromXMLStreamReaderStartElementEvent() throws Exception {
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalFromXMLStreamReaderNestedStartElementEvent() throws Exception {
        xsr.next();
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr);
        assertEquals(XMLStreamReader.END_ELEMENT, xsr.getEventType());
        assertEquals("root", xsr.getLocalName());
    }

    public void testUnmarshalWithClassFromXMLStreamReaderStartDocumentEvent() throws Exception {
        assertEquals(XMLStreamReader.START_DOCUMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalWithClassFromXMLStreamReaderStartElementEvent() throws Exception {
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalWithClassFromXMLStreamReaderNestedStartElementEvent() throws Exception {
        xsr.next();
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        unmarshaller.unmarshal(xsr, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_ELEMENT, xsr.getEventType());
        assertEquals("root", xsr.getLocalName());
    }

    public void testUnmarshalFromStAXSourceStartDocumentEvent() throws Exception {
        assertEquals(XMLStreamReader.START_DOCUMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalFromStAXSourceStartElementEvent() throws Exception {
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalFromStAXSourceNestedStartElementEvent() throws Exception {
        xsr.next();
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource);
        assertEquals(XMLStreamReader.END_ELEMENT, xsr.getEventType());
        assertEquals("root", xsr.getLocalName());
    }

    public void testUnmarshalWithClassFromStAXSourceStartDocumentEvent() throws Exception {
        assertEquals(XMLStreamReader.START_DOCUMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalWithClassFromStAXSourceStartElementEvent() throws Exception {
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_DOCUMENT, xsr.getEventType());
    }

    public void testUnmarshalWithClassFromStAXSourceNestedStartElementEvent() throws Exception {
        xsr.next();
        xsr.next();
        assertEquals(XMLStreamReader.START_ELEMENT, xsr.getEventType());
        StAXSource staxSource = new StAXSource(xsr);
        unmarshaller.unmarshal(staxSource, EndEventRoot.class);
        assertEquals(XMLStreamReader.END_ELEMENT, xsr.getEventType());
        assertEquals("root", xsr.getLocalName());
    }

}
