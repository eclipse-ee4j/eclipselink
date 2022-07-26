/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - October 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefMissingIdEventHandlerTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmlidref/missing_id.json";

    public XmlIdRefMissingIdEventHandlerTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class<?>[]{Owner.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbUnmarshaller.setEventHandler(new MyValidationEventHandler());
    }

    @Override
    protected Object getControlObject() {
        Owner owner = new Owner();
        owner.m_activityId = "1";
        Thing theThing = new Thing();
        theThing.m_calendarId = "2";
        owner.m_calendar = theThing;
        return owner;
    }

    //Not applicable
    @Override
    public void testRoundTrip(){}

    @Override
    public void testJSONUnmarshalFromInputSource() throws Exception{
        try{
            super.testJSONUnmarshalFromInputSource();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }


    @Override
    public void testJSONUnmarshalFromJsonStructureSource() throws Exception{
        try{
            super.testJSONUnmarshalFromJsonStructureSource();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testJsonUnmarshalFromJsonParserSource() throws Exception{
        try{
            super.testJsonUnmarshalFromJsonParserSource();
            fail("An Exception should have occurred");
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
        }
    }

    @Override
    public void testJSONUnmarshalFromInputStream() throws Exception{
        try{
            super.testJSONUnmarshalFromInputStream();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testJSONUnmarshalFromReader() throws Exception{
        try{
            super.testJSONUnmarshalFromReader();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testUnmarshalAutoDetect() throws Exception{
        try{
            super.testUnmarshalAutoDetect();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testJSONUnmarshalFromSource() throws Exception{
        try{
            super.testJSONUnmarshalFromSource();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testJSONUnmarshalFromURL() throws Exception{
        try{
            super.testJSONUnmarshalFromURL();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromInputStream() throws Exception{
        try{
            super.testXMLToObjectFromInputStream();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromNode() throws Exception{
        try{
            super.testXMLToObjectFromNode();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromURL() throws Exception{
        try{
            super.testXMLToObjectFromURL();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testUnmarshallerHandler() throws Exception{
        try{
            super.testUnmarshallerHandler();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromXMLEventReader() throws Exception{
        try{
            super.testXMLToObjectFromXMLEventReader();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception{
        try{
            super.testXMLToObjectFromXMLStreamReaderEx();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }

    @Override
    public void testXMLToObjectFromXMLStreamReader() throws Exception{
        try{
            super.testXMLToObjectFromXMLStreamReader();
        }catch (JAXBException e) {
            assertEquals(ValidationEvent.ERROR, ((MyValidationEventHandler)jaxbUnmarshaller.getEventHandler()).severity);
            return;
        }
        fail("An Exception should have occurred");
    }
    public class MyValidationEventHandler implements ValidationEventHandler{
        public int severity = -1;
        @Override
        public boolean handleEvent(ValidationEvent arg0) {
            severity = arg0.getSeverity();
            return false;
        }

    }
}
