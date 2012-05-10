/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

public class XMLMarshalFragmentTestCases extends OXTestCase {
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee.xml";
    private Object controlObject;
    private Document controlDocument;
    private DocumentBuilder parser;
    private XMLMarshaller marshaller;

    public XMLMarshalFragmentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalFragmentTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
        XMLContext context = getXMLContext(new XMLMarshallerTestProject());
        marshaller = context.createMarshaller();
        marshaller.setFragment(true);
        controlObject = setupControlObject();
        controlDocument = setupControlDocument(XML_RESOURCE);
    }

    public void tearDown() {
        marshaller.setFragment(false);
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

    protected Document setupControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        Document document = parser.parse(inputStream);
        removeEmptyTextNodes(document);
        return document;
    }

    public void testMarshalFragmentObjectToContentHandler() throws Exception {
        MyContentHandler handler = new MyContentHandler();
        marshaller.marshal(controlObject, handler);

        assertFalse("end document was triggered and should not have been", handler.isEndTriggered());
        assertFalse("start  document was triggered and should not have been", handler.isStartTriggered());
    }

    public void testMarshalFragmentObjectToDocument() {
        Document document = parser.newDocument();
        marshaller.marshal(controlObject, document);
        log(controlDocument);
        log(document);

        assertXMLIdentical(controlDocument, document);
    }

    public void testMarshalFragmentObjectToDOMResult() throws Exception {
        Document document = parser.newDocument();

        DOMResult result = new DOMResult(document);
        marshaller.marshal(controlObject, result);

        log(controlDocument);
        log((Document)result.getNode());

        assertXMLIdentical(controlDocument, (Document)result.getNode());
    }

    public void testMarshalFragmentObjectToOutputStream() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        marshaller.marshal(controlObject, outStream);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        String writerStringNoWS = removeWhiteSpaceFromString(outStream.toString());

        log("WRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);

    }

    public void testMarshalFragmentObjectToSAXResult() {
        MyContentHandler handler = new MyContentHandler();
        SAXResult result = new SAXResult(handler);

        marshaller.marshal(controlObject, result);

        assertFalse("end document was triggered and should not have been", handler.isEndTriggered());
        assertFalse("start  document was triggered and should not have been", handler.isStartTriggered());
    }

    public void testMarshalFragmentObjectToStreamResult() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outStream);
        marshaller.marshal(controlObject, result);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        String writerStringNoWS = removeWhiteSpaceFromString(result.getOutputStream().toString());

        log("WRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);

    }

    public void testMarshalFragmentObjectToWriter() throws Exception {
        StringWriter writer = new StringWriter();

        marshaller.marshal(controlObject, writer);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        String writerStringNoWS = removeWhiteSpaceFromString(writer.toString());

        log("\nWRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);

    }
}
