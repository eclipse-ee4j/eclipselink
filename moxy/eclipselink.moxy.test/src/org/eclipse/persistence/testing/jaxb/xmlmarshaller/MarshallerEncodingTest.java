/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.util.JAXBResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.oxm.OXTestCase;

public class MarshallerEncodingTest extends OXTestCase {
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xml";
    private Object controlObject;
    private DocumentBuilder parser;
    private String encoding;
    private String controlString;
    private String originalEncoding;
    private Boolean originalFormatting;
    private Marshaller marshaller;

    private String contextPath;

    public MarshallerEncodingTest(String name, String encoding, String controlString) {
        super(name);
        this.encoding = encoding;
        this.controlString = controlString;
    }

    public void setUp() throws Exception {
        contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);

        JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
        marshaller = jaxbContext.createMarshaller();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        parser = documentBuilderFactory.newDocumentBuilder();

        originalEncoding = (String)marshaller.getProperty(Marshaller.JAXB_ENCODING);
        originalFormatting = (Boolean)marshaller.getProperty(Marshaller.JAXB_FORMATTED_OUTPUT);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(false));
        controlObject = setupControlObject();

    }

    protected Employee setupControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_EMPLOYEE_ID);

        employee.setName("Bob\u0A00Jones");

        Phone p = new Phone();
        p.setNumber("123456789");
        employee.setPhone(p);

        return employee;
    }

    public void testXMLHeader() throws Exception {

        /*
        // A JAXP version of this test is required.
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        marshaller.marshal(controlObject, outStream);

        String stringToParse = outStream.toString(encoding);
        StringReader reader = new StringReader(stringToParse);
        parser.parse(reader);

        log(parser.getDocument());
        assertEquals(encoding, parser.getDocument().getEncoding());
        */
    }

    public void testXMLEncoding() throws Exception {
        log("\nEncoding is: " + encoding);

        byte[] controlBytes = controlString.getBytes(encoding);
        log("\nCONTROL_STRING:" + new String(controlBytes, encoding));
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        marshaller.marshal(controlObject, byteOutStream);
        log("TESTING_STRING:" + byteOutStream.toString());

        byte[] testBytes = byteOutStream.toByteArray();

        log("CONTROL_BYTES");
        log(controlBytes);
        log("TESTING_BYTES");
        log(testBytes);

        assertTrue(compareByteArrays(testBytes, controlBytes));
    }

    public void testXMLEncodingWriter() throws Exception {
        log("towriter ###########");
        log("\nEncoding is: " + encoding);

        byte[] controlBytes = controlString.getBytes(encoding);
        log("\nCONTROL_STRING:" + new String(controlBytes, encoding));
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        StringWriter writer = new StringWriter();
        marshaller.marshal(controlObject, writer);
        log("TESTING_STRING:" + writer.toString());
        byte[] testBytes = writer.toString().getBytes();

        log("CONTROL_BYTES");
        log(controlBytes);
        log("TESTING_BYTES");
        log(testBytes);

        assertTrue(compareByteArrays(testBytes, controlBytes));
    }

    public void testXMLEncodingFileOutputStream() throws Exception {
        log("\ntowriter ###########");
        log("\nEncoding is: " + encoding);

        byte[] controlBytes = controlString.getBytes();
        log("\nCONTROL_STRING:" + new String(controlBytes));
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        String filename = "FileOutStream" + encoding + ".xml";
        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(filename));

        marshaller.marshal(controlObject, outStream);
        log("TESTING_STRING:" + outStream.toString());

        FileInputStream inStream = new FileInputStream(filename);
        byte[] testBytes = new byte[inStream.available()];
        inStream.read(testBytes);

        log("CONTROL_BYTES");
        log(controlBytes);
        log("TESTING_BYTES");
        log(testBytes);

        if (!useLogging) {
            java.io.File f = new java.io.File("filename");
            if (f.exists()) {
                f.delete();
            }
        }

        assertTrue(compareByteArrays(testBytes, controlBytes));
    }

    public void testXMLEncodingFileOutputStreamEnc() throws Exception {
        log("\ntowriter ###########");
        log("\nEncoding is: " + encoding);

        byte[] controlBytes = controlString.getBytes(encoding);
        log("\nCONTROL_STRING:" + new String(controlBytes, encoding));
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        String filename = "FileOutStreamEnc" + encoding + ".xml";
        OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(filename), encoding);

        marshaller.marshal(controlObject, outStream);

        FileInputStream inStream = new FileInputStream(filename);
        byte[] testBytes = new byte[inStream.available()];
        inStream.read(testBytes);

        log("CONTROL_BYTES");
        log(controlBytes);
        log("TESTING_BYTES");
        log(testBytes);

        if (!useLogging) {
            java.io.File f = new java.io.File("filename");
            if (f.exists()) {
                f.delete();
            }
        }

        assertTrue(compareByteArrays(testBytes, controlBytes));
    }

    public void testXMLEncodingFileWriter() throws Exception {
        log("\ntowriter ###########");
        log("\nEncoding is: " + encoding);

        byte[] controlBytes = controlString.getBytes();
        log("\nCONTROL_STRING:" + new String(controlBytes, encoding));
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        String filename = "FileWriter" + encoding + ".xml";
        FileWriter writer = new FileWriter(filename);

        marshaller.marshal(controlObject, writer);
        FileInputStream inStream = new FileInputStream(filename);
        byte[] testBytes = new byte[inStream.available()];
        inStream.read(testBytes);

        log("CONTROL_BYTES");
        log(controlBytes);
        log("TESTING_BYTES");
        log(testBytes);

        if (!useLogging) {
            java.io.File f = new java.io.File("filename");
            if (f.exists()) {
                f.delete();
            }
        }

        assertTrue(compareByteArrays(testBytes, controlBytes));
    }
   
    public void testInvalidEncoding() throws Exception {
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(controlObject, byteOutStream);
        } catch (MarshalException e) {
            assertTrue(true);
            return;
        } catch (Exception e) {
            fail("Unexpected exception. A MarshalException should have been thrown but was not");
            return;
        }
        fail("A MarshalException should have been thrown but was not");
    }

    public void tearDown() throws PropertyException {
        marshaller.setProperty(Marshaller.JAXB_ENCODING, originalEncoding);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, originalFormatting);
    }

    private boolean compareByteArrays(byte[] first, byte[] second) {
        if (first.length != second.length) {
            return false;
        }

        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
                return false;
            }
        }
        return true;
    }

}
