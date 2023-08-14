/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.platform;

import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class PlatformCreateDocumentTestCases extends OXTestCase {
    public PlatformCreateDocumentTestCases(String name) {
        super(name);
    }

    private Document getControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        parser.setEntityResolver(new MyEntityResolver());
        return parser.parse(inputStream);
    }

    public void testCreateDocumentWithSystemIdentifier() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document systemDocument = xmlPlatform.createDocumentWithSystemIdentifier("manual", "http://127.0.0.1/manual.dtd");
        Document controlDocument = getControlDocument("org/eclipse/persistence/testing/oxm/platform/create_system_identifier.xml");
        DocumentType systemDocType = systemDocument.getDoctype();
        DocumentType controlDocType = controlDocument.getDoctype();

        log(systemDocument);
        log(controlDocument);

        assertEquals(controlDocType.getName(), systemDocType.getName());
        assertEquals(controlDocType.getNamespaceURI(), systemDocType.getNamespaceURI());
        assertEquals(controlDocType.getPublicId(), systemDocType.getPublicId());
        assertEquals(controlDocType.getSystemId(), systemDocType.getSystemId());

        assertXMLIdentical(controlDocument, systemDocument);

    }

    public void testCreateDocumentWithSystemIdentifierNullName() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        try {
            Document systemDocument = xmlPlatform.createDocumentWithSystemIdentifier(null, "http://127.0.0.1/manual.dtd");
        } catch (XMLPlatformException platformException) {
            assertTrue("The wrong XMLPlatformException was thrown.", platformException.getErrorCode() == XMLPlatformException.XML_PLATFORM_COULD_NOT_CREATE_DOCUMENT);
            return;
        } catch (Exception e) {
            fail("An XMLPlatformException should have been thrown but another Exception was thrown.");
            return;
        }

        fail("An XMLPlatformException should have been thrown but wasn't");

    }

    public void testCreateDocumentWithNullSystemIdentifier() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document systemDocument = xmlPlatform.createDocumentWithSystemIdentifier("manual", null);
        Document controlDocument = getControlDocument("org/eclipse/persistence/testing/oxm/platform/create_empty.xml");

        DocumentType systemDocType = systemDocument.getDoctype();
        DocumentType controlDocType = controlDocument.getDoctype();

        log(systemDocument);
        log(controlDocument);

        assertXMLIdentical(controlDocument, systemDocument);

        assertNull(systemDocType);
        assertNull(controlDocType);
    }

    public void testCreateDocumentWithPublicIdentifier() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document publicDocument = xmlPlatform.createDocumentWithPublicIdentifier("manual", "-//loopbackInc//DTD manual//EN", "http://127.0.0.1/manual.dtd");
        Document controlDocument = getControlDocument("org/eclipse/persistence/testing/oxm/platform/create_public_identifier.xml");
        DocumentType publicDocType = publicDocument.getDoctype();
        DocumentType controlDocType = controlDocument.getDoctype();

        log(publicDocument);
        log(controlDocument);

        assertEquals(controlDocType.getName(), publicDocType.getName());
        assertEquals(controlDocType.getNamespaceURI(), publicDocType.getNamespaceURI());
        assertEquals(controlDocType.getPublicId(), publicDocType.getPublicId());
        assertEquals(controlDocType.getSystemId(), publicDocType.getSystemId());

        assertXMLIdentical(controlDocument, publicDocument);
    }

    public void testCreateDocumentWithNullPublicIdentifier() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document publicDocument = xmlPlatform.createDocumentWithPublicIdentifier("manual", null, "http://127.0.0.1/manual.dtd");
        Document controlDocument = getControlDocument("org/eclipse/persistence/testing/oxm/platform/create_system_identifier.xml");
        DocumentType publicDocType = publicDocument.getDoctype();
        DocumentType controlDocType = controlDocument.getDoctype();

        log(publicDocument);
        log(controlDocument);

        assertEquals(controlDocType.getName(), publicDocType.getName());
        assertEquals(controlDocType.getNamespaceURI(), publicDocType.getNamespaceURI());
        assertEquals(controlDocType.getPublicId(), publicDocType.getPublicId());
        assertEquals(controlDocType.getSystemId(), publicDocType.getSystemId());

        assertXMLIdentical(controlDocument, publicDocument);

    }

    public void testCreateDocumentWithPublicIdentifierNullSystemIdentifier() throws Exception {
        try {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            Document publicDocument = xmlPlatform.createDocumentWithPublicIdentifier(null, "-//loopbackInc//DTD manual//EN", null);
        } catch (XMLPlatformException platformException) {
            assertTrue("The wrong XMLPlatformException was thrown.", platformException.getErrorCode() == XMLPlatformException.XML_PLATFORM_COULD_NOT_CREATE_DOCUMENT);
            return;
        } catch (Exception e) {
            fail("An XMLPlatformException should have been thrown but another Exception was thrown.");
            return;
        }

        fail("An XMLPlatformException should have been thrown but wasn't");
    }

    public void testCreateDocument() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document systemDocument = xmlPlatform.createDocument();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document controlDocument = parser.newDocument();

        log(systemDocument);
        log(controlDocument);

        assertXMLIdentical(controlDocument, systemDocument);

        DocumentType systemDocType = systemDocument.getDoctype();
        DocumentType controlDocType = controlDocument.getDoctype();

        assertNull(systemDocType);
        assertNull(controlDocType);
    }

    public void testCreateInvalidXMLPlatform() throws Exception {
        Class<? extends XMLPlatform> originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
        XMLPlatformFactory.getInstance().setXMLPlatformClass((Class) PlatformCreateDocumentTestCases.class);
        try {
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        } catch (XMLPlatformException e) {
            assertTrue("An incorrect PlatformException was thrown.", e.getErrorCode() == XMLPlatformException.XML_PLATFORM_COULD_NOT_INSTANTIATE);
            return;
        } catch (Exception e) {
            fail("An incorrect exception was thrown, should have been XMLPlatformException");
            return;
        } finally {
            XMLPlatformFactory.getInstance().setXMLPlatformClass(originalClass);
        }
        fail("A platform exception should have been thrown but wasn't");
    }

    public void testCreateInvalidXMLPlatform2() throws Exception {
        Class<? extends XMLPlatform> originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
        String originalPlatform = System.getProperty("eclipselink.xml.platform");
        XMLPlatformFactory.getInstance().setXMLPlatformClass(null);
        try {
            System.setProperty("eclipselink.xml.platform", "a.b.c.class");
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        } catch (XMLPlatformException e) {
            assertTrue("An incorrect PlatformException was thrown.", e.getErrorCode() == XMLPlatformException.XML_PLATFORM_CLASS_NOT_FOUND);
            return;
        } catch (Exception e) {
            fail("An incorrect exception was thrown, should have been XMLPlatformException");
            return;
        } finally {
            XMLPlatformFactory.getInstance().setXMLPlatformClass(originalClass);
            System.setProperty("eclipselink.xml.platform", originalPlatform);

        }
        fail("A platform exception should have been thrown but wasn't");
    }

    public void testCreateNullXMLPlatform() throws Exception {
        Class<? extends XMLPlatform> originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
        String originalPlatform = System.getProperty("eclipselink.xml.platform");
        if (null == originalPlatform) {
            originalPlatform = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
        }
        XMLPlatformFactory.getInstance().setXMLPlatformClass(null);
        try {
            System.getProperties().remove("eclipselink.xml.platform");
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            assertEquals("The platform class should have been the default JAXPPlatform", XMLPlatformFactory.getInstance().getXMLPlatformClass().getName(), XMLPlatformFactory.JAXP_PLATFORM_CLASS_NAME);
        } catch (NoClassDefFoundError e) {
            // If the XDK is not on the classpath this error will be thrown.
        } catch (Exception e) {
            fail("An unexpected error occurred:" + e.getMessage());
            return;
        } finally {
            XMLPlatformFactory.getInstance().setXMLPlatformClass(originalClass);
            System.setProperty("eclipselink.xml.platform", originalPlatform);
        }
    }

    public class MyEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(String string, String string2) {
            try {
                InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/platform/manual.dtd");
                InputSource source = new InputSource(inputStream);
                return source;

            } catch (Exception e) {
                return null;
            }
        }
    }
}
