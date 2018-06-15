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
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref.Address;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref.Employee;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref.PhoneNumber;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref.Root;

public class AutoDetectMediaTypeTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-collection.json";

    public AutoDetectMediaTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TestObjectFactory.class;
        setClasses(classes);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
    }

    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
        ArrayList choices = new ArrayList();
        choices.add(new JAXBElement(new QName("integer-root"), Integer.class, new Integer(21)));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value1"));
        EmployeeCollection nestedEmployee = new EmployeeCollection();
        nestedEmployee.refs = new ArrayList();
        nestedEmployee.refs.add(new JAXBElement(new QName("integer-root"), Integer.class, new Integer(29)));
        choices.add(nestedEmployee);
        choices.add(new JAXBElement(new QName("root"), String.class, "Value2"));
        employee.refs = choices;
        return employee;
     }


    protected Object getJSONReadControlObject(){
        //same as getReadControl Except order is different
        EmployeeCollection employee = new EmployeeCollection();
        ArrayList choices = new ArrayList();
        choices.add(new JAXBElement(new QName("integer-root"), Integer.class, new Integer(21)));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value1"));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value2"));

        EmployeeCollection nestedEmployee = new EmployeeCollection();
        nestedEmployee.refs = new ArrayList();
        nestedEmployee.refs.add(new JAXBElement(new QName("integer-root"), Integer.class, new Integer(29)));
        choices.add(nestedEmployee);
        employee.refs = choices;
        return employee;
    }

    public MediaType getXMLUnmarshalMediaType(){
          return MediaType.APPLICATION_JSON;
    }

    public MediaType getJSONUnmarshalMediaType(){
       return MediaType.APPLICATION_XML;
    }

    public void testUnmarshalStreamSourceURLJSON() throws Exception{
        File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
        String systemId = file.toURI().toURL().toExternalForm();

        StreamSource ss = new StreamSource(systemId);
        Object testObject = jaxbUnmarshaller.unmarshal(ss);
        jsonToObjectTest(testObject);

    }

    public void testUnmarshalStreamSourceURLXML() throws Exception{
        File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
        String systemId = file.toURI().toURL().toExternalForm();

        StreamSource ss = new StreamSource(systemId);
        Object testObject = jaxbUnmarshaller.unmarshal(ss);
        xmlToObjectTest(testObject);

    }

    public void testUnmarshalStreamSourceURLJSONWithClass() throws Exception{
        File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
        String systemId = file.toURI().toURL().toExternalForm();

        StreamSource ss = new StreamSource(systemId);
        JAXBElement jbe = jaxbUnmarshaller.unmarshal(ss, EmployeeCollection.class);

        jsonToObjectTest(jbe.getValue());

    }

    public void testUnmarshalStreamSourceURLXMLWithClass() throws Exception{
        File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
        String systemId = file.toURI().toURL().toExternalForm();

        StreamSource ss = new StreamSource(systemId);
        JAXBElement jbe  = jaxbUnmarshaller.unmarshal(ss, EmployeeCollection.class);
        xmlToObjectTest(jbe.getValue());

    }

    public void testUnmarshalStreamSourceNoProtocolJSONWithClass() throws Exception{
        File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
        String systemId = file.getAbsolutePath();

        StreamSource ss = new StreamSource(systemId);
        JAXBElement jbe = jaxbUnmarshaller.unmarshal(ss, EmployeeCollection.class);

        jsonToObjectTest(jbe.getValue());

    }

    public void testUnmarshalStreamSourceNoProtocolXMLWithClass() throws Exception{
        File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
        String systemId = file.getAbsolutePath();

        StreamSource ss = new StreamSource(systemId);
        JAXBElement jbe  = jaxbUnmarshaller.unmarshal(ss, EmployeeCollection.class);
        xmlToObjectTest(jbe.getValue());

    }

    public void testJSONToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            jaxbUnmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
            jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }
}
