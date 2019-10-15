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
package org.eclipse.persistence.testing.jaxb.jaxbelement;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;

/**
 * This class should not be used directly - only it's subclasses should
 * be setup and used for testing.
 */
public class JAXBElementTestCases extends JAXBWithJSONTestCases {
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static String CONTROL_NAMESPACE_URI = "test";
    protected String resourceName;
    protected Class target;

    public JAXBElementTestCases(String name) throws Exception {
        super(name);
        setClasses(getClasses());
    }

    public Class[] getClasses(){
        Class[] classes = new Class[1];
        classes[0] = Person.class;
        return classes;
    }

    /**
     * Satisfy the abstract method declaration in JAXBTestCases
     */
    protected Object getControlObject() {
        return null;
    }

    protected void setControlDocument(String xmlResource) throws Exception {
        super.setControlDocument(xmlResource);
        resourceName = xmlResource;
    }

    protected void setTargetClass(Class targetCls) {
        target = targetCls;
    }

    public void testXMLToObjectFromNode() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(instream);
        Document testDocument = parser.parse(inputSource);
        Object testObject = getJAXBUnmarshaller().unmarshal(testDocument, target);
        instream.close();
        xmlToObjectTest(testObject);
    }


    public void testRoundTrip() throws Exception{
        if(isUnmarshalTest()) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            InputSource inputSource = new InputSource(instream);
            Document testDocument = parser.parse(inputSource);
            Object testObject = getJAXBUnmarshaller().unmarshal(testDocument, target);
            instream.close();
            xmlToObjectTest(testObject);

            objectToXMLStringWriter(testObject);
        }
    }

    public void testXMLToObjectFromSource() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        StreamSource source = new StreamSource(instream);
        Object testObject = getJAXBUnmarshaller().unmarshal(source, target);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader, target);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    @Override
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

            ExtendedXMLStreamReaderReader xmlStreamReaderReaderEx = new ExtendedXMLStreamReaderReader();
            XMLStreamReaderInputSource xmlStreamReaderInputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            SAXSource saxSource = new SAXSource(xmlStreamReaderReaderEx, xmlStreamReaderInputSource);

            Object testObject = jaxbUnmarshaller.unmarshal(saxSource, target);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader, target);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testObjectToStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        getJAXBMarshaller().marshal(getWriteControlObject(), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();
        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToByteArrayOutputStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        getJAXBMarshaller().marshal(getWriteControlObject(), outputStream);
        StringReader reader = new StringReader(outputStream.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        outputStream.close();
        reader.close();
        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToStreamResult() throws Exception {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        getJAXBMarshaller().marshal(getWriteControlObject(), result);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();
        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLDocument() throws Exception {
        Document testDocument = new SAXDocumentBuilder().getInitializedDocument();
        getJAXBMarshaller().marshal(getWriteControlObject(), testDocument);
        objectToXMLDocumentTest(testDocument);
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

    // TODO:  add support for StAX
    public void testObjectToStreamWriter() throws Exception {}

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        log("**testObjectToXMLDocument**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(testDocument);

        assertXMLIdentical(getWriteControlDocument(), testDocument);
    }

    // THESE TESTS DO NOT APPLY
    public void testXMLToObjectFromInputStream() throws Exception {}
    public void testXMLToObjectFromURL() throws Exception {}
    public void testObjectToXMLStringWriter() throws Exception {}
    public void testUnmarshallerHandler() throws Exception { }
}
