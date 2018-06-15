/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementNoNamespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_nonamespace.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_nonamespace.json";
    private final static String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_nonamespace_invalid.xml";

    private final static int CONTROL_ID = 10;

    public XmlElementNoNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNoNamespace.class;
        setClasses(classes);

    }

    protected Object getControlObject() {
        EmployeeNoNamespace employee = new EmployeeNoNamespace();
        employee.id = CONTROL_ID;
        return employee;
    }

    public void testEventHandler() throws Exception{
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID);
        MyEventHandler handler = new MyEventHandler();
        jaxbUnmarshaller.setEventHandler(handler);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/xml");
        try{
            Object testObject = jaxbUnmarshaller.unmarshal(instream);
        }catch(Exception e){
            assertNotNull(handler);
            assertNotNull(handler.getLocator());
            assertEquals(3, handler.getLocator().getLineNumber());
            return;
        }finally{
            instream.close();
        }
        fail("An exception should have been thrown.");
    }

    protected class MyEventHandler implements ValidationEventHandler{
        private ValidationEventLocator locator;
        @Override
        public boolean handleEvent(ValidationEvent arg0) {
            locator = arg0.getLocator();
            return false;
        }

        public ValidationEventLocator getLocator(){
            return locator;
        }

    }
}
