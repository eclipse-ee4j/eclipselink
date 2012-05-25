/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.exceptions.i18n.JAXBExceptionResource;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.dynamic.util.NoExtensionEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DynamicJAXBContextCreationTestCases extends TestCase {

    public DynamicJAXBContextCreationTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "Dynamic JAXB: Context Creation: " + super.getName();
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
        Class[] classes = new Class[] { this.getClass() };

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
        Class[] classes = new Class[] { this.getClass() };

        JAXBException ex = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance(classes, new HashMap());
        } catch (JAXBException e) {
            ex = e;
        }

        assertNotNull("Did not catch exception as expected.", ex);
        assertEquals("Incorrect exception thrown.", 50038, ((org.eclipse.persistence.exceptions.JAXBException) ex.getLinkedException()).getErrorCode());
    }

    public void testNewInstanceOXM() throws JAXBException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_OXM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_OXM + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.dynamic", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        // Have to include a path to a jaxb.properties, so just reusing a context path that does contain one.
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON_CLASS_NAME);
        assertNotNull(person);
    }

    public void testNewInstanceXSD() throws JAXBException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_XSD);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_XSD + "]");
        }

        Map<String, InputStream> properties = new HashMap<String, InputStream>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);

        // Have to include a path to a jaxb.properties, so just reusing a context path that does contain one.
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testNewInstanceXSDExternalBinding() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);

        InputStream xsdStream = classLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        Source xsdSource = new StreamSource(xsdStream);
        // Set SYSTEM_ID to the filename part of the fully qualified EXAMPLE_XSD
        xsdSource.setSystemId(EXAMPLE_XSD.substring(EXAMPLE_XSD.lastIndexOf('/') + 1));

        InputStream xjbStream = classLoader.getResourceAsStream(EXTERNAL_BINDINGS);
        Source xjbSource = new StreamSource(xjbStream);
        // Set SYSTEM_ID to be the same as the XSD
        xjbSource.setSystemId(xsdSource.getSystemId());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, xsdSource);
        properties.put(DynamicJAXBContextFactory.EXTERNAL_BINDINGS_KEY, xjbSource);

        // Have to include a path to a jaxb.properties, so just reusing a context path that does contain one.
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity emp = jaxbContext.newDynamicEntity(ALT_EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);

        try {
            // These sets will fail if the external bindings file was not read properly
            emp.set("empId", "747");
        } catch (DynamicException e) {
            fail("External bindings file not applied.");
        }
    }

    public void testNewInstanceXSDExternalBindingError() throws Exception {
        // To use external bindings files, both schema and .xjb must be given as Sources
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_XSD);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_XSD + "]");
        }

        InputStream xjbStream = classLoader.getResourceAsStream(EXTERNAL_BINDINGS);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);

        ArrayList<InputStream> xjbs = new ArrayList<InputStream>();
        xjbs.add(xjbStream);
        xjbs.add(xjbStream);

        properties.put(DynamicJAXBContextFactory.EXTERNAL_BINDINGS_KEY, xjbs);

        JAXBException caughtEx = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        } catch (JAXBException e) {
            caughtEx = e;
        } catch (Exception e) {
            fail("Unexpected exception thrown. " + e);
        }

        org.eclipse.persistence.exceptions.JAXBException jEx = null;
        Exception currentException = caughtEx;
        // Walk the exception looking for an EclipseLink JAXBException
        while (true) {
            if (currentException instanceof JAXBException) {
                Exception linkedEx = (Exception) ((JAXBException) currentException).getLinkedException();
                if (linkedEx instanceof org.eclipse.persistence.exceptions.JAXBException) {
                    jEx = (org.eclipse.persistence.exceptions.JAXBException) linkedEx;
                    break;
                } else {
                    currentException = linkedEx;
                }
            } else {
                break;
            }
        }

        if (jEx == null) {
            fail("Unexpected exception thrown. " + caughtEx);
        }

        assertEquals("Unexpected EclipseLink exception thrown.", org.eclipse.persistence.exceptions.JAXBException.XJB_NOT_SOURCE, jEx.getErrorCode());
    }

    public void testNewInstanceXSDImportError() throws Exception {
        // To use schema imports, schemas must be given as Sources
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream inputStream = classLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document xsdDocument = docBuilder.parse(inputStream);
        Element xsdElement = xsdDocument.getDocumentElement();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, xsdElement);

        NoExtensionEntityResolver re = new NoExtensionEntityResolver();
        properties.put(DynamicJAXBContextFactory.ENTITY_RESOLVER_KEY, re);

        JAXBException caughtEx = null;
        try {
            DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);

            DynamicEntity person = jaxbContext.newDynamicEntity("mynamespace.Person");
            assertNotNull("Could not create Dynamic Entity.", person);

            DynamicEntity salary = jaxbContext.newDynamicEntity("banknamespace.CdnCurrency");
            assertNotNull("Could not create Dynamic Entity.", salary);
        } catch (JAXBException e) {
                caughtEx = e;
        } catch (Exception e) {
            fail("Unexpected exception thrown. " + e);
        }

        org.eclipse.persistence.exceptions.JAXBException jEx = null;
        Exception currentException = caughtEx;
        // Walk the exception looking for an EclipseLink JAXBException
        while (true) {
            if (currentException instanceof JAXBException) {
                Exception linkedEx = (Exception) ((JAXBException) currentException).getLinkedException();
                if (linkedEx instanceof org.eclipse.persistence.exceptions.JAXBException) {
                    jEx = (org.eclipse.persistence.exceptions.JAXBException) linkedEx;
                    break;
                } else {
                    currentException = linkedEx;
                }
            } else {
                break;
            }
        }

        if (jEx == null) {
            fail("Unexpected exception thrown. " + caughtEx);
        }

        assertEquals("Unexpected EclipseLink exception thrown.", org.eclipse.persistence.exceptions.JAXBException.XSD_IMPORT_NOT_SOURCE, jEx.getErrorCode());
    }

    public void testNewInstanceXSDExternalBindings() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);

        InputStream xsdStream = classLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        Source xsdSource = new StreamSource(xsdStream);
        // Set SYSTEM_ID to the filename part of the fully qualified EXAMPLE_XSD
        xsdSource.setSystemId(EXAMPLE_XSD.substring(EXAMPLE_XSD.lastIndexOf('/') + 1));

        InputStream xjbStream = classLoader.getResourceAsStream(EXTERNAL_BINDINGS);
        Source xjbSource = new StreamSource(xjbStream);
        // Set SYSTEM_ID to be the same as the XSD
        xjbSource.setSystemId(xsdSource.getSystemId());

        InputStream xjbStream2 = classLoader.getResourceAsStream(EXTERNAL_BINDINGS_2);
        Source xjbSource2 = new StreamSource(xjbStream2);
        // Set SYSTEM_ID to be the same as the XSD
        xjbSource2.setSystemId(xsdSource.getSystemId());

        ArrayList<Source> extBindings = new ArrayList<Source>(3);
        extBindings.add(xjbSource);
        extBindings.add(xjbSource2);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, xsdSource);
        properties.put(DynamicJAXBContextFactory.EXTERNAL_BINDINGS_KEY, extBindings);

        // Have to include a path to a jaxb.properties, so just reusing a context path that does contain one.
        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity emp = jaxbContext.newDynamicEntity(ALT_EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);

        try {
            // These sets will fail if the external bindings file was not read properly
            emp.set("empId", "747");
            emp.set("lastNameCommaFirstName", "Neilson, Leslie");
        } catch (DynamicException e) {
            fail("External bindings file not applied.");
        }
    }

    // ========================================================================

    public void testCreateContextFromXSDInputStream() throws JAXBException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextFromXSDInputStreamNull() throws Exception {
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

    public void testCreateContextFromXSDNode() throws Exception {
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

    public void testCreateContextFromXSDNodeError() throws Exception {
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

    public void testCreateContextFromXSDNodeNull() throws Exception {
        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD((Node) null, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50045, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextFromXSDSource() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(EXAMPLE_XSD);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        Document xsdDocument = docFactory.newDocumentBuilder().parse(inputStream);
        Source domSource = new DOMSource(xsdDocument);

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(domSource, null, null, null);

        DynamicEntity emp = jaxbContext.newDynamicEntity(EMPLOYEE_CLASS_NAME);
        assertNotNull(emp);
    }

    public void testCreateContextFromXSDSourceNull() throws Exception {
        JAXBException caughtException = null;
        try {
            DynamicJAXBContextFactory.createContextFromXSD((Source) null, null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50043, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextFromSessionsXMLString() throws JAXBException {
        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContext(SESSION_NAMES, null, null);
        DynamicEntity docWrapper = jaxbContext.newDynamicEntity(DOCWRAPPER_CLASS_NAME);
        assertNotNull(docWrapper);
    }

    public void testCreateContextFromSessionsXMLStringNull() throws JAXBException {
        JAXBException caughtException = null;

        try {
            DynamicJAXBContextFactory.createContext(null, null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50042, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    public void testCreateContextFromOXM() throws JAXBException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_OXM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_OXM + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.dynamic", new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new HashMap<String, Source>());

        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON_CLASS_NAME);
        assertNotNull(person);

        //DynamicEntity person2 = jaxbContext.getObjectFactory().createInstance(PERSON_CLASS_NAME);
        //assertNotNull(person2);

        //JAXBElement<DynamicEntity> personElem = jaxbContext.getObjectFactory().createElementInstance(new QName("mynamespace", "human"), person2);
        //assertNotNull(personElem);
    }

    public void testCreateContextFromOXMError() throws JAXBException {
        JAXBException caughtException = null;

        try {
            DynamicJAXBContextFactory.createContextFromOXM(null, null);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50055, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());

        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            DynamicJAXBContextFactory.createContextFromOXM(null, properties);
        } catch (JAXBException e) {
            caughtException = e;
        }

        assertNotNull("Did not catch exception as expected.", caughtException);
        assertEquals("Incorrect exception thrown.", 50055, ((org.eclipse.persistence.exceptions.JAXBException) caughtException.getLinkedException()).getErrorCode());
    }

    private static final String SESSION_NAMES =
        "org.eclipse.persistence.testing.jaxb.dynamic:org.eclipse.persistence.testing.jaxb.dynamic.secondproject";

    private static final String EXAMPLE_XSD =
        "org/eclipse/persistence/testing/jaxb/dynamic/contextcreation.xsd";
    private static final String INVALID_XSD =
        "org/eclipse/persistence/testing/jaxb/dynamic/invalid.xsd";
    private static final String IMPORT_XSD =
            "org/eclipse/persistence/testing/jaxb/dynamic/xmlschema-import.xsd";
    private static final String EXAMPLE_OXM =
        "org/eclipse/persistence/testing/jaxb/dynamic/contextcreation-oxm.xml";
    private static final String EXTERNAL_BINDINGS =
        "org/eclipse/persistence/testing/jaxb/dynamic/bindings.xjb";
    private static final String EXTERNAL_BINDINGS_2 =
        "org/eclipse/persistence/testing/jaxb/dynamic/bindings2.xjb";

    private static final String EMPLOYEE_CLASS_NAME =
        "mynamespace.Employee";
    private static final String ALT_EMPLOYEE_CLASS_NAME =
        "ext.bindings.Employee";
    private static final String ALT_PHONE_CLASS_NAME =
        "ext.bindings.Phone";
    private static final String PERSON_CLASS_NAME =
        "org.eclipse.persistence.testing.jaxb.dynamic.Person";
    private static final String DOCWRAPPER_CLASS_NAME =
        "org.persistence.testing.jaxb.dynamic.xxx.DocWrapper";

}