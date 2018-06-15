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
package org.eclipse.persistence.testing.jaxb.employee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class JAXBEmployeeTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee.json";
    private final static String JSON_RESOURCE_FORMATTED = "org/eclipse/persistence/testing/jaxb/employee/employee_formatted.json";
    private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
    private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
    private final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
    private final static String CONTROL_FIRST_NAME = "Bob";
    private final static String CONTROL_LAST_NAME = "Smith";
    private final static int CONTROL_ID = 10;

    public JAXBEmployeeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("examplenamespace", "x");
        return namespaces;
    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);
        responsibilities.add(10);

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

    public String getWriteControlJSONFormatted(){
        return JSON_RESOURCE_FORMATTED;
    }

    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
        return false;
    }

    public void testRepeatedUnmarshals() throws Exception{
    Unmarshaller u= jaxbContext.createUnmarshaller();
    addXsiTypeToUnmarshaller(u);
    unmarshalXML(u);
    unmarshalJSON(u);
    unmarshalJSON(u);
        unmarshalXML(u);
        unmarshalJSON(u);

    }

    private void unmarshalXML(Unmarshaller u) throws Exception{
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML );
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlDocumentLocation);
        Object testObject = u.unmarshal(new StreamSource(inputStream));

        inputStream.close();
        xmlToObjectTest(testObject);
    }

    private void unmarshalJSON(Unmarshaller u) throws Exception{
         u.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON );
         InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
         Object testObject = u.unmarshal(new StreamSource(inputStream));
         inputStream.close();
         jsonToObjectTest(testObject);
    }

    public void testRepeatedMarshals() throws Exception{
    Marshaller m= jaxbContext.createMarshaller();
    addXsiTypeToMarshaller(m);
    marshalXML(m);
    marshalJSON(m);
    marshalJSON(m);
    marshalXML(m);
    marshalJSON(m);
    }

    private void marshalXML(Marshaller m) throws Exception{
        m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_XML );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
        jaxbMarshaller.marshal(getWriteControlObject(), stream);
        InputStream is = new ByteArrayInputStream(stream.toByteArray());         Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);

    }

    private void marshalJSON(Marshaller m) throws Exception{
          m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON );
          ByteArrayOutputStream os = new ByteArrayOutputStream();
          m.marshal(getWriteControlObject(), os);
          compareStringToControlFile("testJSONMarshalToOutputStream", new String(os.toByteArray()));
          os.close();
      }

}
