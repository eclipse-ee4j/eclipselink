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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLUnmarshalTestCases extends OXTestCase {
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String SESSION_NAME = "XMLMarshallerTestSession";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee.json";
    private final static String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car.xml";
    private final static String EMAIL_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/EmailAddress.xml";
    private final static String UNMARSHAL_FROM_NODE_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNode.xml";
    private final static String CONTROL_EMPTY_XML_FILE_NAME = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee_Empty_Content.xml";
    private Object controlObject;
    private XMLUnmarshaller unmarshaller;
    private XMLContext context;

    public XMLUnmarshalTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLUnmarshalTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        context = getXMLContext(SESSION_NAME);
        unmarshaller = context.createUnmarshaller();
        controlObject = setupControlObject();
    }

    protected Employee setupControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_EMPLOYEE_ID);

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setUserID(CONTROL_EMAIL_ADDRESS_USER_ID);
        emailAddress.setDomain(CONTROL_EMAIL_ADDRESS_DOMAIN);
        employee.setEmailAddress(emailAddress);

        return employee;
    }

    protected DocumentBuilder getParser() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        return builderFactory.newDocumentBuilder();
    }

    public void testGetEntityResolver() {
        EntityResolver control = new DefaultHandler();
        unmarshaller.setEntityResolver(control);
        EntityResolver test = unmarshaller.getEntityResolver();
        assertEquals(control, test);
    }

    public void testGetErrorHandler() {
        ErrorHandler control = new DefaultHandler();
        unmarshaller.setErrorHandler(control);
        ErrorHandler test = unmarshaller.getErrorHandler();
        assertEquals(control, test);
    }

    public void testUnmarshalFromFile() throws Exception {
        File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
        Object unmarshalledObject = unmarshaller.unmarshal(file);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromFileJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(file);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromFileWithClass() throws Exception {
        File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
        Object unmarshalledObject = unmarshaller.unmarshal(file, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromFileWithClassJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            File file = new File(ClassLoader.getSystemResource(JSON_RESOURCE).getFile());
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(file, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromFileWithNullClass() throws Exception {
        try {
            File file = new File(ClassLoader.getSystemResource(XML_RESOURCE).getFile());
            unmarshaller.unmarshal(file, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromDocument() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Document testDocument = getParser().parse(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(testDocument);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromDocumentJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(testDocument);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromDocumentWithClass() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Document testDocument = getParser().parse(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(testDocument, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromDocumentWithClassJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(testDocument, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromDocumentWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(stream);
            unmarshaller.unmarshal(testDocument, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromNode() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(UNMARSHAL_FROM_NODE_RESOURCE);
        Document testDocument = getParser().parse(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(testDocument.getDocumentElement().getFirstChild());
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromNodeJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(UNMARSHAL_FROM_NODE_RESOURCE);
            Document testDocument = getParser().parse(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(testDocument.getDocumentElement().getFirstChild());
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputStream() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(stream);

        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromInputStreamJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(stream);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputStreamWithClass() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(stream, Employee.class);

        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromInputStreamWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(stream, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromXMLStreamReader() throws Exception {
        if(XML_INPUT_FACTORY != null){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            XMLStreamReader xmlStreamReader =  XML_INPUT_FACTORY.createXMLStreamReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceStreamReaderConstructor, new Object[]{xmlStreamReader});
            Object unmarshalledObject = unmarshaller.unmarshal(ss);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromXMLStreamReaderWithClass() throws Exception {
        if(XML_INPUT_FACTORY != null){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            XMLStreamReader xmlStreamReader =  XML_INPUT_FACTORY.createXMLStreamReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceStreamReaderConstructor, new Object[]{xmlStreamReader});
            Object unmarshalledObject = unmarshaller.unmarshal(ss, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromXMLEventReader() throws Exception {
        if(XML_INPUT_FACTORY != null){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceEventReaderConstructor, new Object[]{reader});
            Object unmarshalledObject = unmarshaller.unmarshal(ss);
            assertEquals(controlObject, unmarshalledObject);
        }
    }


    public void testUnmarshalFromXMLEventReaderJSON() throws Exception {
        if(XML_INPUT_FACTORY != null && platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceEventReaderConstructor, new Object[]{reader});
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(ss);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromXMLEventReaderWithClass() throws Exception {
        if(XML_INPUT_FACTORY != null){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceEventReaderConstructor, new Object[]{reader});
            Object unmarshalledObject = unmarshaller.unmarshal(ss, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromXMLEventReaderWithClassJSON() throws Exception {
        if(XML_INPUT_FACTORY != null && platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            javax.xml.stream.XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            Source ss = (Source)PrivilegedAccessHelper.invokeConstructor(staxSourceEventReaderConstructor, new Object[]{reader});
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(ss, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputStreamWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            unmarshaller.unmarshal(stream, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromInputSource() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputSource source = new InputSource(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(source);

        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromInputSourceJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputSource source = new InputSource(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source);

            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputSourceWithClass() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            InputSource source = new InputSource(stream);
            Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);

            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputSourceWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputSource source = new InputSource(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromInputSourceWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            InputSource source = new InputSource(stream);
            unmarshaller.unmarshal(source, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromReader() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputStreamReader reader = new InputStreamReader(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(reader);

        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromReaderWithClass() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputStreamReader reader = new InputStreamReader(stream);
        Object unmarshalledObject = unmarshaller.unmarshal(reader, Employee.class);

        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromReaderJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputStreamReader reader = new InputStreamReader(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(reader);

            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromReaderWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream stream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputStreamReader reader = new InputStreamReader(stream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);

            Object unmarshalledObject = unmarshaller.unmarshal(reader, Employee.class);

            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromReaderWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            InputStreamReader reader = new InputStreamReader(stream);
            unmarshaller.unmarshal(reader, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromStreamSource() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Source source = new StreamSource(inputStream);
        Object unmarshalledObject = unmarshaller.unmarshal(source);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromStreamSourceWithClass() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Source source = new StreamSource(inputStream);
        Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromStreamSourceJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            Source source = new StreamSource(inputStream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromStreamSourceWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            Source source = new StreamSource(inputStream);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }
    public void testUnmarshalFromStreamSourceWithNullClass() throws Exception {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Source source = new StreamSource(inputStream);
            unmarshaller.unmarshal(source, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromDOMSource() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Document testDocument = getParser().parse(inputStream);
        DOMSource source = new DOMSource(testDocument);
        Object unmarshalledObject = unmarshaller.unmarshal(source);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromDOMSourceJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(inputStream);
            DOMSource source = new DOMSource(testDocument);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromDOMSourceWithClass() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Document testDocument = getParser().parse(inputStream);
        DOMSource source = new DOMSource(testDocument);
        Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromDOMSourceWithClassJSON() throws Exception {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(inputStream);
            DOMSource source = new DOMSource(testDocument);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromDOMSourceWithNullClass() throws Exception {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document testDocument = getParser().parse(inputStream);
            DOMSource source = new DOMSource(testDocument);
            unmarshaller.unmarshal(source, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromSAXSource() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputSource inputSource = new InputSource(inputStream);
        SAXSource source = new SAXSource(inputSource);
        Object unmarshalledObject = unmarshaller.unmarshal(source);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromSAXSourceWithClass() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputSource inputSource = new InputSource(inputStream);
        SAXSource source = new SAXSource(inputSource);
        Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }


    public void testUnmarshalFromSAXSourceJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputSource inputSource = new InputSource(inputStream);
            SAXSource source = new SAXSource(inputSource);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromSAXSourceWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);
            InputSource inputSource = new InputSource(inputStream);
            SAXSource source = new SAXSource(inputSource);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(source, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromSAXSourceWithNullClass() throws Exception {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            InputSource inputSource = new InputSource(inputStream);
            SAXSource source = new SAXSource(inputSource);
            unmarshaller.unmarshal(source, Employee.class);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalFromURL() {
        URL url = ClassLoader.getSystemResource(XML_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(url);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromURLWithClass() {
        URL url = ClassLoader.getSystemResource(XML_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(url, Employee.class);
        assertEquals(controlObject, unmarshalledObject);
    }

    public void testUnmarshalFromURLJSON() {
        if(platform.equals(PLATFORM_SAX)){
            URL url = ClassLoader.getSystemResource(JSON_RESOURCE);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(url);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromURLWithClassJSON() {
        if(platform.equals(PLATFORM_SAX)){
            URL url = ClassLoader.getSystemResource(JSON_RESOURCE);
            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            Object unmarshalledObject = unmarshaller.unmarshal(url, Employee.class);
            assertEquals(controlObject, unmarshalledObject);
        }
    }

    public void testUnmarshalFromURLWithNullClass() throws Exception {
        try {
            URL url = ClassLoader.getSystemResource(XML_RESOURCE);
            Object unmarshalledObject = unmarshaller.unmarshal(url, null);
        } catch (XMLMarshalException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
        }
    }

    public void testUnmarshalInvalidReader() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        stream.close();
        try {
            InputStreamReader reader = new InputStreamReader(stream);
            reader.close();
            Object unmarshalledObject = unmarshaller.unmarshal(reader);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromEmptyReader() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CONTROL_EMPTY_XML_FILE_NAME);
        try {
            InputStreamReader reader = new InputStreamReader(stream);
            Object unmarshalledObject = unmarshaller.unmarshal(reader);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromEmptyURL() throws Exception {
        try {
            URL url = ClassLoader.getSystemResource(CONTROL_EMPTY_XML_FILE_NAME);
            Object unmarshalledObject = unmarshaller.unmarshal(url);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromEmptyFile() throws Exception {
        try {
            File file = new File(ClassLoader.getSystemResource(CONTROL_EMPTY_XML_FILE_NAME).getFile());
            Object unmarshalledObject = unmarshaller.unmarshal(file);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalInvalidDocument() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(INVALID_XML_RESOURCE);

        try {
            Object unmarshalledObject = unmarshaller.unmarshal(stream);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT);
            return;
        } catch (Exception e) {
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);

    }

    public void testUnmarshalInvalidFile() throws Exception {
        try {
            File file = new File("a/b/c");

            Object testObject = unmarshaller.unmarshal(file);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLMarshalException should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("The wrong exception was thrown ... should have been an XMLMarshalException.", false);
            return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromEmptyInputStream() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CONTROL_EMPTY_XML_FILE_NAME);
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(stream);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLMarshalException should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("The wrong exception was thrown ... should have been an XMLMarshalException.", false);
            return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromEmptyInputSource() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CONTROL_EMPTY_XML_FILE_NAME);
        try {
            InputSource source = new InputSource(stream);
            Object unmarshalledObject = unmarshaller.unmarshal(source);
        } catch (XMLMarshalException validationException) {
            assertTrue("An XMLMarshalException should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("The wrong exception was thrown ... should have been an XMLMarshalException.", false);
            return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalNonRoot() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(EMAIL_RESOURCE);

        try {
            Document testDocument = getParser().parse(stream);
            Object unmarshalledObject = unmarshaller.unmarshal(testDocument);
        } catch (XMLMarshalException validationException) {
            validationException.printStackTrace();
            assertTrue("An XMLValidation should have been caught but wasn't.", validationException.getErrorCode() == XMLMarshalException.NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }
        System.out.println("NO EXCEPTION THROWN");
        assertTrue("An XMLValidation should have been caught but wasn't.", false);

    }

    // XMLToObjectTests=========================================================================================
    public void testXMLToObject() throws Exception {

        /*
        InputStream stream = ClassLoader.getSystemResourceAsStream(INVALID_XML_RESOURCE);
        Document testDocument = getParser().parse(stream);
        try {
            Object unmarshalledObject = unmarshaller.xmlToObject(new DOMRecord(testDocument.getDocumentElement()), Car.class);
        } catch (XMLMarshalException e) {
            assertTrue("An unexpected XMLMarshalException was caught", e.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
        */
    }

    // NULL TESTS=========================================================================================
    public void testUnmarshalFromNullFile() {
        File input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullInputStream() {
        InputStream input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullInputSource() {
        InputSource input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullNode() {
        Node input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullReader() {
        InputStreamReader reader = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(reader);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullSource() {
        Source input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromNullURL() {
        URL input = null;
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(input);
        } catch (XMLMarshalException exception) {
            assertTrue("An unexpected XMLMarshalException was caught", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }


    public void testUnmarshalFromInputSourceDOMPlatformJSON(){
        InputStream stream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputStream stream2 = ClassLoader.getSystemResourceAsStream(JSON_RESOURCE);

        if(platform.equals(PLATFORM_DOM)){
            assertEquals("org.eclipse.persistence.oxm.platform.DOMPlatform", unmarshaller.getXMLContext().getSession(0).getProject().getDatasourceLogin().getDatasourcePlatform().getClass().getName());

            unmarshaller.setMediaType(MediaType.APPLICATION_XML);
            Object unmarshalledObject = unmarshaller.unmarshal(stream);
            assertEquals(controlObject, unmarshalledObject);

            unmarshaller.setMediaType(MediaType.APPLICATION_JSON);
            try{
               unmarshalledObject = unmarshaller.unmarshal(stream2);
            }catch(XMLMarshalException e){
                assertEquals(XMLMarshalException.PLATFORM_NOT_SUPPORTED_WITH_JSON_MEDIA_TYPE,e.getErrorCode());
                return;
            }
            fail("A XMLMarshalException should have been thrown");
        }
    }
}
