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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas;

import java.net.URL;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.ErrorHandler;

public class SetSchemasTestCases extends OXTestCase {
    private static final String VALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/valid.xml";
    private static final String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/invalid.xml";
    private XMLContext xmlContext;
    private XMLUnmarshaller xmlUnmarshaller;

    public SetSchemasTestCases(String name) {
        super(name);
    }

    public void setUp() {
        EmployeeProject project = new EmployeeProject();
        xmlContext = getXMLContext(project);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshaller.setErrorHandler(new MyErrorHandler());
    }

    public void testInvalidFile() {
        URL url = ClassLoader.getSystemResource(INVALID_XML_RESOURCE);
        xmlUnmarshaller.unmarshal(url);
    }

    public void testValidateInvalidFile() {
        boolean wasExceptionCaught = false;
        try {
            xmlUnmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
            URL url = ClassLoader.getSystemResource(INVALID_XML_RESOURCE);
            xmlUnmarshaller.unmarshal(url);
        } catch (Exception e) {
            wasExceptionCaught = true;
        }
        assertTrue("An exception should have been thrown.", wasExceptionCaught);
    }

    public void testValidFile() {
        URL url = ClassLoader.getSystemResource(VALID_XML_RESOURCE);
        xmlUnmarshaller.unmarshal(url);
    }

    public void testValidateValidFile() {
        xmlUnmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        URL url = ClassLoader.getSystemResource(VALID_XML_RESOURCE);
        xmlUnmarshaller.unmarshal(url);
    }

    /**
     * Error handler implementation for handling parser errors
     */
    class MyErrorHandler implements ErrorHandler {
        public void warning(org.xml.sax.SAXParseException sex) throws org.xml.sax.SAXParseException {
        }
        public void error(org.xml.sax.SAXParseException sex) throws org.xml.sax.SAXParseException {
            throw sex;
        }
        public void fatalError(org.xml.sax.SAXParseException sex) throws org.xml.sax.SAXParseException {
            throw sex;
        }
    }
}
