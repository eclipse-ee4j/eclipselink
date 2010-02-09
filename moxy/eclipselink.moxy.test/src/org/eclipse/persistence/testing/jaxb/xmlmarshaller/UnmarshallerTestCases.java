/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;

public class UnmarshallerTestCases extends TestCase  {

    private static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    private final static String CONTROL_XML_FILE_NAME = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "Jane Doe";

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;
    private DocumentBuilder parser;
    private String contextPath;

    public UnmarshallerTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);

        jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
        unmarshaller = jaxbContext.createUnmarshaller();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    /**
    * Since JAXB delegates all of its functionality to XMLMarshaller, there is no need to
    * re-test all of the various marshal/unmarshal methods.
    */
    public void testUnmarshalFile() throws Exception {
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        unmarshaller.setValidating(true);
        Object testObject = unmarshaller.unmarshal(file);
        assertEquals(getControlObject(), testObject);
    }

    public void testUnmarshalFileNotValidating() throws Exception {
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        unmarshaller.setValidating(false);
        Object testObject = unmarshaller.unmarshal(file);
        assertEquals(getControlObject(), testObject);
    }

    public void testUnmarshalFromJAXBSource() throws Exception {
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Object unmarshalledObject = unmarshaller.unmarshal(file);

        JAXBSource jaxbSource = new JAXBSource(jaxbContext.createMarshaller(), unmarshalledObject);

        Object objectFromSource = unmarshaller.unmarshal(jaxbSource);
        assertEquals(getControlObject(), objectFromSource);
    }

    // Invalid sources tests=============================================================

    public void testUnmarshalFromInvalidFile() throws Exception {
        File file = new File("blah.txt");
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(file);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromInvalidURL() throws Exception {
        URL url = new URL("http://");
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(url);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromInvalidNode()  throws Exception {
        Document doc = parser.newDocument();
        Node node = doc.createAttribute("aaa");
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(node);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromInvalidInputStream() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CONTROL_XML_FILE_NAME);
        stream.close();
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(stream);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromInvalidSource() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONTROL_XML_FILE_NAME);
        StreamSource source = new StreamSource(inputStream);
        source.getInputStream().close();
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(source);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFromInvalidInputSource() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CONTROL_XML_FILE_NAME);
        InputSource source = new InputSource(stream);
        source.getByteStream().close();
        try {
            Object unmarshalledObject = unmarshaller.unmarshal(source);
        } catch (UnmarshalException e) {
            assertTrue(true);
            return;
        }
        assertTrue("An UnmarshalException should have been caught but wasn't.", false);
    }

    public void testUnmarshalFileException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((File) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testUnmarshalInputStreamException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((InputStream) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testUnmarshalInputSourceException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((InputSource) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testUnmarshalSourceException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((Source) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testUnmarshalURLException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((URL) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testUnmarshalNodeException() {
        boolean caughtException = false;
        try {
            unmarshaller.unmarshal((Node) null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testJAXBClassLoader() throws Exception {
        // Determine the directory that Employee.class is in
        URL classURL = ClassLoader.getSystemClassLoader().getResource("org/eclipse/persistence/testing/jaxb/xmlmarshaller/Employee.class");
        URL directoryURL = new File(classURL.getFile()).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().toURL();

        // Create a URLClassLoader specifically for this class
        URLClassLoader classLoader = new URLClassLoader(new URL[] { directoryURL });

        // Set up a control object using this ClassLoader
        Object controlObject = Class.forName("org.eclipse.persistence.testing.jaxb.xmlmarshaller.Employee", true, classLoader).newInstance();
        Method setMethod = controlObject.getClass().getMethod("setName", new Class[] { String.class });
        setMethod.invoke(controlObject, new Object[] { CONTROL_EMPLOYEE_NAME });

        // Unmarshall the control file, passing JAXB this classLoader
        JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, classLoader);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Object testObject = unmarshaller.unmarshal(file);

        assertEquals(controlObject, testObject);
    }

    public void testSetNullPropertyException() {
        boolean caughtException = false;
        try {
            unmarshaller.setProperty(null, null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testGetNullPropertyException() {
        boolean caughtException = false;
        try {
            unmarshaller.getProperty(null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testSetInvalidPropertyException() {
        boolean caughtException = false;
        try {
            unmarshaller.setProperty("thisIsAnInvalidProperty", "thisIsAnInvalidValue");
        } catch (PropertyException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw PropertyException as expected.", caughtException);
    }

    public void testGetInvalidPropertyException() {
        boolean caughtException = false;
        try {
            unmarshaller.getProperty("thisIsAnInvalidProperty");
        } catch (PropertyException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBUnmarshaller did not throw PropertyException as expected.", caughtException);
    }

    // =============================================================

    private Object getControlObject() {
        Employee employee = new Employee();
        employee.setName(CONTROL_EMPLOYEE_NAME);
        return employee;
    }

}
