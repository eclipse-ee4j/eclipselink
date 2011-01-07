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
package org.eclipse.persistence.testing.oxm.platform;

import java.io.InputStream;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

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

        this.assertEquals(controlDocType.getName(), systemDocType.getName());
        this.assertEquals(controlDocType.getNamespaceURI(), systemDocType.getNamespaceURI());
        this.assertEquals(controlDocType.getPublicId(), systemDocType.getPublicId());
        this.assertEquals(controlDocType.getSystemId(), systemDocType.getSystemId());

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

        this.assertEquals(controlDocType.getName(), publicDocType.getName());
        this.assertEquals(controlDocType.getNamespaceURI(), publicDocType.getNamespaceURI());
        this.assertEquals(controlDocType.getPublicId(), publicDocType.getPublicId());
        this.assertEquals(controlDocType.getSystemId(), publicDocType.getSystemId());

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

        this.assertEquals(controlDocType.getName(), publicDocType.getName());
        this.assertEquals(controlDocType.getNamespaceURI(), publicDocType.getNamespaceURI());
        this.assertEquals(controlDocType.getPublicId(), publicDocType.getPublicId());
        this.assertEquals(controlDocType.getSystemId(), publicDocType.getSystemId());

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
        Class originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
        XMLPlatformFactory.getInstance().setXMLPlatformClass(PlatformCreateDocumentTestCases.class);
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
        Class originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
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
        Class originalClass = XMLPlatformFactory.getInstance().getXMLPlatformClass();
        String originalPlatform = System.getProperty("eclipselink.xml.platform");
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
