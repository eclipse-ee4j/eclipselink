/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JSONUnmarshalTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.xml";
    private static final String JSON_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.json";


    public JSONUnmarshalTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_VALID);
        setControlDocument(XML_RESOURCE_VALID);
        setClasses(new Class[]{TestObject.class});
    }

    @Override
    protected Object getControlObject() {
        TestObject controlObject = new TestObject();

        String controlString ="This is testing that if an unmarshal operation fails the unmarshaller will be left in a clean state so it can be reused to unmarshal subsequent documents";
        controlObject.bytes = controlString.getBytes();
        return controlObject;
    }

    public void testUnmarshalFile() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE_VALID).getFile());
        Object testObject = jaxbUnmarshaller.unmarshal(file);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalInputSource() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        InputStream is = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE_VALID);
        InputSource inputSource = new InputSource(is);
        Object testObject = jaxbUnmarshaller.unmarshal(inputSource);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalNode() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        Document testDocument = parser.parse(inputStream);

        Object testObject = jaxbUnmarshaller.unmarshal(testDocument);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalDOMSource() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        Document testDocument = parser.parse(inputStream);
        DOMSource source = new DOMSource(testDocument);

        Object testObject = jaxbUnmarshaller.unmarshal(source);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalStreamSource() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE_VALID);
        StreamSource source = new StreamSource(inputStream);

        Object testObject = jaxbUnmarshaller.unmarshal(source);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalSAXSource() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream is = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE_VALID);
        InputSource inputSource = new InputSource(is);
        SAXSource source = new SAXSource(inputSource);

        Object testObject = jaxbUnmarshaller.unmarshal(source);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalURL() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        URL url = ClassLoader.getSystemResource(JSON_RESOURCE_VALID);

        Object testObject = jaxbUnmarshaller.unmarshal(url);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalXMLStreamReader() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        XMLStreamReader xmlStreamReader =  XML_INPUT_FACTORY.createXMLStreamReader(stream);
        Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalXMLEventReader() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);

        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        XMLEventReader xmlEventReader =  XML_INPUT_FACTORY.createXMLEventReader(stream);

        Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader);
        jsonToObjectTest(testObject);
    }

    public void testUnmarshalReader() throws Exception{
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE_VALID);
        InputStreamReader reader = new InputStreamReader(stream);

        Object testObject = jaxbUnmarshaller.unmarshal(reader);
        jsonToObjectTest(testObject);
    }


}
