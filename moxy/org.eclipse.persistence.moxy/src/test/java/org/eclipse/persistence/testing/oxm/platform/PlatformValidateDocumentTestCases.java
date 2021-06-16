/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
