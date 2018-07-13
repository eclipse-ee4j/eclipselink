/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.net.URL;
import org.w3c.dom.Document;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

public class PlatformValidateDocumentTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private XMLPlatform xmlPlatform;
    private XMLParser xmlParser;

    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/oxm/platform/validate_document.xsd";
    private static final String XML_RESOURCE_VALID = "org/eclipse/persistence/testing/oxm/platform/validate_document_valid.xml";
    private static final String XML_RESOURCE_INVALID = "org/eclipse/persistence/testing/oxm/platform/validate_document_invalid.xml";

    public PlatformValidateDocumentTestCases(String name) {
        super(name);
    }

    public void setUp() {
        xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        xmlParser = xmlPlatform.newXMLParser();
    }

    public void testValidDocument() {
        URL xmlSchemaURL = ClassLoader.getSystemResource(XSD_RESOURCE);
        InputStream validXmlInputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_VALID);
        Document validDocument = xmlParser.parse(validXmlInputStream);
        assertTrue(xmlPlatform.validateDocument(validDocument, xmlSchemaURL, null));
    }

    public void testInvalidDocument() {
        try {
            URL xmlSchemaURL = ClassLoader.getSystemResource(XSD_RESOURCE);
            InputStream invalidXmlInputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_INVALID);
            Document invalidDocument = xmlParser.parse(invalidXmlInputStream);
            xmlPlatform.validateDocument(invalidDocument, xmlSchemaURL, null);
            fail();
        } catch(XMLPlatformException e) {
            assertEquals(XMLPlatformException.XML_PLATFORM_VALIDATION_EXCEPTION, e.getErrorCode());
        }
    }

}
