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
import org.eclipse.persistence.jaxb.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.DynamicJAXBContextFactory;
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
        JAXBException ex = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
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
    }

    public void testCreateContextInputStream() throws JAXBException {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, nsResolver, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextNode() throws Exception {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Element xsdElement = xsdDocument.getDocumentElement();

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(xsdElement, nsResolver, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextNodeError() throws Exception {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Node textNode = xsdDocument.createTextNode("TEXT NODE");

        Exception ex = null;
        try {
            DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(textNode, nsResolver, null, null);
        } catch (org.eclipse.persistence.exceptions.JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
    }

    public void testCreateContextSource() throws Exception {
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        Document xsdDocument = docFactory.newDocumentBuilder().parse(inputStream);
        Source domSource = new DOMSource(xsdDocument);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(domSource, nsResolver, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextString() throws JAXBException {
        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContext(SESSION_NAMES, null, null);
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

}