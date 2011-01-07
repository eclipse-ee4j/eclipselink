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

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.Car;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public class MarshallerTestCases extends OXTestCase {
    private final static String CONTROL_XML_FILE_NAME = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "Jane Doe";
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private DocumentBuilder parser;
    private String contextPath;

    public MarshallerTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);

        jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
        marshaller = jaxbContext.createMarshaller();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    /**
     * Since JAXB delegates all of its functionality to XMLMarshaller, there is
     * no need to re-test all of the various marshal/unmarshal methods.
     */
    public void testMarshalObjectToWriter() throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(getControlObject(), writer);

        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        removeEmptyTextNodes(testDocument);

        assertXMLIdentical(getControlDocument(), testDocument);
    }

    public void testMarshalObjectToJAXBResult() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBResult jaxbResult = new JAXBResult(unmarshaller);

        marshaller.marshal(getControlObject(), jaxbResult);
    }

    public void testMarshalToWriterException() {
        boolean caughtException = false;
        try {
            marshaller.marshal(null, (Writer)null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBMarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testMarshalToContentHandlerException() {
        boolean caughtException = false;
        try {
            marshaller.marshal(null, (ContentHandler)null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBMarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testMarshalObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();

        marshaller.marshal(getControlObject(), builder);

        log(getControlDocument());
        log(builder.getDocument());

        assertXMLIdentical(getControlDocument(), builder.getDocument());
    }

    public void testMarshalToNodeException() {
        boolean caughtException = false;
        try {
            marshaller.marshal(null, (Node)null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBMarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testMarshalToNullOutputStream() {
        boolean caughtException = false;
        try {
            marshaller.marshal(null, (OutputStream)null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBMarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testMarshalToInvalidOutputStream() {
        try {
            FileDescriptor fd = new FileDescriptor();
            FileOutputStream filestream = new FileOutputStream(fd);

            marshaller.marshal(getControlObject(), filestream);
        } catch (MarshalException e) {
            assertTrue(true);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exeption was thrown.");
            return;
        }
        fail("JAXBMarshaller did not throw an exception as expected.");
    }

    /**
     * We should throw an exception if users try to marshal an object which is
     * not of a type that is mapped in the project. The class Car is not mapped
     * in the project being used so an exception should be thrown
     */
    public void testMarshalInvalidObject() {
        StringWriter writer = new StringWriter();

        try {
            marshaller.marshal(new Car(), writer);
        } catch (MarshalException marshalException) {
            Throwable linkedException = marshalException.getLinkedException();
            assertTrue("LinkedException was not an XMLMarshalException as expected. ", linkedException instanceof XMLMarshalException);
            assertTrue("LinkedException was not an XMLMarshalException as expected. ", ((XMLMarshalException)linkedException).getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT);
            return;
        } catch (Exception e) {
            log(e.getMessage());
            assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
    }

    public void testMarshalToResultException() {
        boolean caughtException = false;
        try {
            marshaller.marshal(null, (Result)null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBMarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    // =============================================================
    public void testMarshalToInvalidResult() throws Exception {
        boolean caughtException = false;
        Document document = parser.newDocument();
        DOMResult result = new DOMResult(document.createAttribute("test"));

        try {
            marshaller.marshal(getControlObject(), result);
        } catch (IllegalArgumentException e) {
            caughtException = false;
        } catch (MarshalException e) {
            caughtException = true;
        }
        assertTrue("JAXBMarshaller did not throw MarshalException as expected.", caughtException);
    }

    public void testMarshalToInvalidNode() throws Exception {
        boolean caughtException = false;
        Document document = parser.newDocument();

        try {
            marshaller.marshal(getControlObject(), document.createAttribute("test"));
        } catch (IllegalArgumentException e) {
            caughtException = false;
        } catch (MarshalException e) {
            caughtException = true;
        }
        assertTrue("JAXBMarshaller did not throw MarshalException as expected.", caughtException);
    }

    // =============================================================
    private Object getControlObject() {
        Employee employee = new Employee();
        employee.setName(CONTROL_EMPLOYEE_NAME);
        return employee;
    }

    private Document getControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONTROL_XML_FILE_NAME);
        Document document = parser.parse(inputStream);
        removeEmptyTextNodes(document);
        return document;
    }
}
