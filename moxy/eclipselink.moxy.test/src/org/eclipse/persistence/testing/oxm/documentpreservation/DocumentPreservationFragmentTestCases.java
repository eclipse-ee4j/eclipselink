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
package org.eclipse.persistence.testing.oxm.documentpreservation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

public class DocumentPreservationFragmentTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public DocumentBuilder parser;

    public DocumentPreservationFragmentTestCases() {
        super("Document Preservation Fragment Tests");
    }

    public DocumentPreservationFragmentTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext("DocumentPreservationSession");
        marshaller = context.createMarshaller();
        marshaller.setFragment(true);
        unmarshaller = context.createUnmarshaller();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();

    }

    public void tearDown() {
        marshaller.setFragment(false);
    }

    public void testMarshalFragmentToContentHandler() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        MyContentHandler handler = new MyContentHandler();

        marshaller.marshal(emp, handler);

        assertFalse("end document was triggered and should not have been", handler.isEndTriggered());
        assertFalse("start  document was triggered and should not have been", handler.isStartTriggered());

    }

    public void testMarshalFragmentToStreamResult() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result_no_header.xml");
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outStream);

        marshaller.marshal(emp, result);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        String writerStringNoWS = removeWhiteSpaceFromString(result.getOutputStream().toString());

        log("\nWRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);
    }

    public void testMarshalFragmentToDOMResult() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result_no_header.xml");
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        Document document = parser.newDocument();
        DOMResult result = new DOMResult(document);

        marshaller.marshal(emp, result);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        String writerStringNoWS = removeWhiteSpaceFromString("");

        log("\nWRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertXMLIdentical(sourceDocument, document);
    }

    public void testMarshalFragmentToSAXResult() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        MyContentHandler handler = new MyContentHandler();
        SAXResult result = new SAXResult(handler);

        marshaller.marshal(emp, result);

        assertFalse("end document was triggered and should not have been", handler.isEndTriggered());
        assertFalse("start  document was triggered and should not have been", handler.isStartTriggered());
    }

    public void testMarshalFragmentToNode() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result_no_header.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        Document outputDoc = parser.newDocument();

        marshaller.marshal(emp, outputDoc);

        log("CONTROL:");
        log(controlDocument);
        log("\nACTUAL:");
        log(outputDoc);

        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testMarshalFragmentToWriter() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result_no_header.xml");
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        StringWriter writer = new StringWriter();

        marshaller.marshal(emp, writer);

        String writerStringNoWS = removeWhiteSpaceFromString(writer.toString());
        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));

        log("\nWRITER  STRING:" + writerStringNoWS);
        log("CONTROL STRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);
    }

    public void testMarshalFragmentToOutputStream() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_result_no_header.xml");
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/missing_element_source.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(getNewAddress());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        marshaller.marshal(emp, outStream);

        String writerStringNoWS = removeWhiteSpaceFromString(outStream.toString());
        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        log("\nWRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);
    }

    private Address getNewAddress() {
        CanadianAddress addr = new CanadianAddress();
        addr.setStreet("2001 Odessy Drive");
        addr.setCity("Ottawa");
        addr.setProvince("ON");
        addr.setPostalCode("A1A 1A1");
        return addr;
    }

    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
