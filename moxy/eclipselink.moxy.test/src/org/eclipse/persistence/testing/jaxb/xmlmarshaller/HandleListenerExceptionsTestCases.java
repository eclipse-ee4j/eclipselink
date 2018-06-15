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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import junit.framework.TestCase;

public class HandleListenerExceptionsTestCases extends TestCase {

    private static String AFTER_MARSHAL = "afterMarshal";
    private static String AFTER_UNMARSHAL = "afterUnmarshal";
    private static String BEFORE_MARSHAL = "beforeMarshal";
    private static String BEFORE_UNMARSHAL = "beforeUnmarshal";
    private static String XML = "<foo><bar/></foo>";

    private JAXBContext jc;

    @Override
    protected void setUp() throws Exception {
        jc = JAXBContextFactory.createContext(new Class[] {Foo.class}, null);
        //jc = JAXBContext.newInstance(Foo.class);
    }

    public void testUnmarshal() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        TestValidationEventHandler veh = new TestValidationEventHandler();
        unmarshaller.setEventHandler(veh);

        StringReader reader = new StringReader(XML);
        unmarshaller.unmarshal(reader);

        assertEquals(2, veh.validationEvents.size());

        ValidationEvent veh1 = veh.validationEvents.get(0);
        assertEquals(ValidationEvent.FATAL_ERROR, veh1.getSeverity());
        assertTrue(veh1.getLinkedException() instanceof XMLMarshalException);
        assertEquals(BEFORE_UNMARSHAL, veh1.getLinkedException().getCause().getCause().getMessage());

        ValidationEvent veh2 = veh.validationEvents.get(1);
        assertEquals(ValidationEvent.FATAL_ERROR, veh2.getSeverity());
        assertTrue(veh2.getLinkedException() instanceof XMLMarshalException);
        assertEquals(AFTER_UNMARSHAL, veh2.getLinkedException().getCause().getCause().getMessage());
    }

    public void testMarshal() throws Exception {
        Marshaller marshaller = jc.createMarshaller();
        TestValidationEventHandler veh = new TestValidationEventHandler();
        marshaller.setEventHandler(veh);

        Foo foo = new Foo();
        foo.bar = new Bar();

        marshaller.marshal(foo, new StringWriter());

        assertEquals(2, veh.validationEvents.size());

        ValidationEvent veh1 = veh.validationEvents.get(0);
        assertEquals(ValidationEvent.FATAL_ERROR, veh1.getSeverity());
        assertTrue(veh1.getLinkedException() instanceof XMLMarshalException);
        assertEquals(BEFORE_MARSHAL, veh1.getLinkedException().getCause().getCause().getMessage());

        ValidationEvent veh2 = veh.validationEvents.get(1);
        assertEquals(ValidationEvent.FATAL_ERROR, veh2.getSeverity());
        assertTrue(veh2.getLinkedException() instanceof XMLMarshalException);
        assertEquals(AFTER_MARSHAL, veh2.getLinkedException().getCause().getCause().getMessage());

    }

    @XmlRootElement
    public static class Foo {

        public Bar bar;

    }

    public static class Bar {

        private void afterMarshal(Marshaller marshaller) {
            throw new RuntimeException(AFTER_MARSHAL);
        }

        private void afterUnmarshal(Unmarshaller unmarshaller, Object object) {
            throw new RuntimeException(AFTER_UNMARSHAL);
        }

        private void beforeMarshal(Marshaller marshaller) {
            throw new RuntimeException(BEFORE_MARSHAL);
        }

        private void beforeUnmarshal(Unmarshaller unmarshaller, Object object) {
            throw new RuntimeException(BEFORE_UNMARSHAL);
        }

    }

    public static class TestValidationEventHandler implements ValidationEventHandler {

        List<ValidationEvent> validationEvents = new ArrayList<ValidationEvent>(2);

        public List<ValidationEvent> getValidationEvents() {
            return validationEvents;
        }

        @Override
        public boolean handleEvent(ValidationEvent validationEvent) {
            this.validationEvents.add(validationEvent);
            return true;
        }

    }

}
