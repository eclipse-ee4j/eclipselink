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
// mmacivor - June 09/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.employee;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class EmployeeNamespaceCancellationTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee_namespacecancellation.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee_namespacecancellation_write.xml";
    private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
    private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
    private final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
    private final static String CONTROL_FIRST_NAME = "Bob";
    private final static String CONTROL_LAST_NAME = "Smith";
    private final static int CONTROL_ID = 10;

    public EmployeeNamespaceCancellationTestCases(String name) throws Exception {
        super(name);

        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
    }

    public void testRoundTrip(){

    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
        employee.firstName = CONTROL_FIRST_NAME;
        employee.lastName = CONTROL_LAST_NAME;
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2005,04,24,16,06,53);

        employee.birthday = cal;

        employee.id = CONTROL_ID;

        employee.responsibilities = responsibilities;

        employee.setBlah("Some String");

        return employee;
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
        javax.xml.stream.XMLStreamReader reader = factory.createXMLStreamReader(instream);

        Object obj = getJAXBUnmarshaller().unmarshal(reader);
        this.xmlToObjectTest(obj);
    }

    // Bug #283424 needs to be fixed then this test can be added
    public void testXMLToObjectFromXMLEventReader() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        javax.xml.stream.XMLInputFactory factory = javax.xml.stream.XMLInputFactory.newInstance();
        javax.xml.stream.XMLEventReader reader = factory.createXMLEventReader(instream);

        Object obj = getJAXBUnmarshaller().unmarshal(reader);
        this.xmlToObjectTest(obj);
    }

    public void testObjectToXMLStreamWriter() throws Exception {
        StringWriter writer = new StringWriter();
        Object objectToWrite = getWriteControlObject();
        javax.xml.stream.XMLOutputFactory factory = javax.xml.stream.XMLOutputFactory.newInstance();
        javax.xml.stream.XMLStreamWriter streamWriter = factory.createXMLStreamWriter(writer);

        getJAXBMarshaller().marshal(objectToWrite, streamWriter);

        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

}
