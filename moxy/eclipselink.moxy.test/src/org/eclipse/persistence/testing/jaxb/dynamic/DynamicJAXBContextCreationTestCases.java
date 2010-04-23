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
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import junit.framework.TestCase;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DynamicJAXBContextCreationTestCases extends TestCase {

    private static final String SESSION_NAMES =
        "org.eclipse.persistence.testing.jaxb.dynamic:org.eclipse.persistence.testing.jaxb.dynamic.secondproject";
    private static final String DOCWRAPPER_CLASS_NAME =
        "org.persistence.testing.jaxb.dynamic.xxx.DocWrapper";

    private static final String EXAMPLE_XSD =
        "org/eclipse/persistence/testing/jaxb/dynamic/xmlseealso.xsd";
    private static final String INVALID_XSD =
        "org/eclipse/persistence/testing/jaxb/dynamic/invalid.xsd";

    private static final String EMPLOYEE_CLASS_NAME =
        "mynamespace.Employee";

    public DynamicJAXBContextCreationTestCases(String name) throws Exception {
        super(name);
    }

    public void testNewInstanceString() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES);
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testNewInstanceStringLoader() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES, Thread.currentThread().getContextClassLoader());
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testNewInstanceStringLoaderProps() throws JAXBException {
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(SESSION_NAMES, Thread.currentThread().getContextClassLoader(), new HashMap());
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testNewInstanceClassesError() throws JAXBException {
        Class[] classes = new Class[] { FirstFieldTransformer.class, SecondFieldTransformer.class };

        JAXBException caughtException = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50038, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testNewInstanceClassesPropsError() throws JAXBException {
        Class[] classes = new Class[] { FirstFieldTransformer.class, SecondFieldTransformer.class };
        JAXBException ex = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes, new HashMap());
        } catch (JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
        assertEquals("Incorrect exception thrown.", 50038, ((org.eclipse.persistence.exceptions.JAXBException) ex.getLinkedException()).getErrorCode());
    }

    public void testCreateContextInputStream() throws JAXBException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextInputStreamNull() throws Exception {
        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD((InputStream) null, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50044, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    /*
    public void testCreateContextInputStreamInvalidSchema() throws Exception {
        // 'createContextFromXSD' can catch some schema errors (e.g. undeclared namespace), but
        // other more basic syntax problems (e.g. a "weak correctness check") will end up getting thrown
        // as a SAXParseException and is uncatchable.

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(INVALID_XSD);

        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50046, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }
    */

    public void testCreateContextNode() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Element xsdElement = xsdDocument.getDocumentElement();

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(xsdElement, null, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextNodeError() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Node textNode = xsdDocument.createTextNode("TEXT NODE");

        JAXBException caughtException = null;
        try {
            DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(textNode, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50039, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextNodeNull() throws Exception {
        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD((Node) null, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50045, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextSource() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        Document xsdDocument = docFactory.newDocumentBuilder().parse(inputStream);
        Source domSource = new DOMSource(xsdDocument);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(domSource, null, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextSourceNull() throws Exception {
        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD((Source) null, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50043, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextString() throws JAXBException {
        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContext(SESSION_NAMES, null, null);
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testCreateContextStringNull() throws JAXBException {
        JAXBException caughtException = null;

        try {
            DynamicJAXBContextFactory.createContext(null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50042, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

}