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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.events;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.jaxb.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class RootWithCompositeCollectionTestCases extends JAXBWithJSONTestCases {
    public JAXBMarshalListenerImpl listener;
    public JAXBUnmarshalListenerImpl unmarshalListener;
    public ArrayList expectedMarshalEvents;
    public ArrayList expectedUnmarshalEvents;

    public ArrayList expectedClassBasedMarshalEvents;
    public ArrayList expectedClassBasedUnmarshalEvents;

    public Object writeControlObject;

    public RootWithCompositeCollectionTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class, Address.class, PhoneNumber.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/events/composite_collection.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/events/composite_collection.json");

        expectedMarshalEvents = new ArrayList();
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_BEFORE_MARSHAL);
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.PHONE_BEFORE_MARSHAL);
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.PHONE_AFTER_MARSHAL);
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.PHONE_BEFORE_MARSHAL);
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.PHONE_AFTER_MARSHAL);
        expectedMarshalEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_AFTER_MARSHAL);

        expectedUnmarshalEvents = new ArrayList();
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_BEFORE_UNMARSHAL);
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.PHONE_BEFORE_UNMARSHAL);
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.PHONE_AFTER_UNMARSHAL);
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.PHONE_BEFORE_UNMARSHAL);
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.PHONE_AFTER_UNMARSHAL);
        expectedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_AFTER_UNMARSHAL);

        expectedClassBasedUnmarshalEvents = new ArrayList();
        expectedClassBasedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_BEFORE_UNMARSHAL);
        expectedClassBasedUnmarshalEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_AFTER_UNMARSHAL);

        expectedClassBasedMarshalEvents = new ArrayList();
        expectedClassBasedMarshalEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_BEFORE_MARSHAL);
        expectedClassBasedMarshalEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_AFTER_MARSHAL);

    }

    public void setUp() throws Exception {
        super.setUp();
        listener = new JAXBMarshalListenerImpl();
        unmarshalListener = new JAXBUnmarshalListenerImpl();

        this.getJAXBMarshaller().setListener((Marshaller.Listener)listener);
        this.getJAXBUnmarshaller().setListener((Unmarshaller.Listener)unmarshalListener);
    }
    public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        assertTrue("Class based callbacks not correct", ((Employee)testObject).triggeredEvents.equals(expectedClassBasedUnmarshalEvents));
        assertTrue("Expected sequence of Unmarshal events not found", expectedUnmarshalEvents.equals(unmarshalListener.events));
        unmarshalListener.events = new ArrayList();

    }

    public void jsonToObjectTest(Object testObject) throws Exception {
        super.jsonToObjectTest(testObject);
        assertTrue("Class based callbacks not correct", ((Employee)testObject).triggeredEvents.equals(expectedClassBasedUnmarshalEvents));
        assertTrue("Expected sequence of Unmarshal events not found", expectedUnmarshalEvents.equals(unmarshalListener.events));
        unmarshalListener.events = new ArrayList();

    }

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertTrue("Class based callbacks not correct", ((Employee)getWriteControlObject()).triggeredEvents.equals(expectedClassBasedMarshalEvents));
        assertTrue("Expected sequence of Marshal events not found", expectedMarshalEvents.equals(listener.events));

    }

    public Object getControlObject() {
        Employee employee = new Employee();
        employee.phoneNumbers = new ArrayList();

        PhoneNumber phone = new PhoneNumber();
        phone.number = "123-4567";
        employee.phoneNumbers.add(phone);

        phone = new PhoneNumber();
        phone.number = "891-0111";
        employee.phoneNumbers.add(phone);
        return employee;
    }


    public Object getWriteControlObject() {
        if(writeControlObject == null) {
            writeControlObject = getControlObject();
        }
        return writeControlObject;
    }

    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = getJAXBUnmarshaller().unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testObjectToXMLDocument() throws Exception {
        //Document testDocument = getJAXBMarshaller().objectToXML(getWriteControlObject());
        //objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        getJAXBMarshaller().marshal(getWriteControlObject(), writer);

        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testRoundTrip() throws Exception{
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            Object testObject = getJAXBUnmarshaller().unmarshal(instream);
            instream.close();
            super.xmlToObjectTest(testObject);

            StringWriter writer = new StringWriter();
            getJAXBMarshaller().marshal(testObject, writer);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();

            super.objectToXMLDocumentTest(testDocument);

            assertTrue("Expected sequence of Unmarshal events not found", expectedUnmarshalEvents.equals(unmarshalListener.events));

            assertTrue("Expected sequence of Marshal events not found", expectedMarshalEvents.equals(listener.events));

            ArrayList expectedEvents = new ArrayList();
            expectedEvents.addAll(expectedClassBasedUnmarshalEvents);
            expectedEvents.addAll(expectedClassBasedMarshalEvents);
            assertTrue("Class based callbacks not corrent", ((Employee)testObject).triggeredEvents.equals(expectedEvents));
        }
    }

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        getJAXBMarshaller().marshal(getWriteControlObject(), builder);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("**testObjectToXMLDocument**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);

        assertXMLIdentical(controlDocument, testDocument);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = getJAXBUnmarshaller().unmarshal(url);
        xmlToObjectTest(testObject);
    }

    public void testUnmarshallerHandler() throws Exception {
    }
}
