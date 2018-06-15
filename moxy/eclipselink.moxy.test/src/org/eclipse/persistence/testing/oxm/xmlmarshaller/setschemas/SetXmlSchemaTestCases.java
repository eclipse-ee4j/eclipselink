/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas;

import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class SetXmlSchemaTestCases extends OXTestCase {
    private static final String VALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/valid.xml";
    private static final String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/invalid.xml";
    private static final String XML_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/schema.xsd";

    private XMLContext xmlContext;
    private XMLUnmarshaller xmlUnmarshaller;
    private Schema schema;

    public SetXmlSchemaTestCases(String name) {
        super(name);
    }

    public void setUp() {
        EmployeeProject project = new EmployeeProject();
        xmlContext = getXMLContext(project);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshaller.setErrorHandler(new MyErrorHandler());

        SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory.newSchema(new java.io.File(Thread.currentThread().getContextClassLoader().getResource(XML_SCHEMA_RESOURCE).toURI()));
        } catch(SAXException ex) {
            throw new RuntimeException(ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void testInvalidFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(INVALID_XML_RESOURCE);
        xmlUnmarshaller.unmarshal(url);
    }

    public void testValidateInvalidFile() {
        boolean wasExceptionCaught = false;
        try {
            xmlUnmarshaller.setSchema(schema);
            URL url = Thread.currentThread().getContextClassLoader().getResource(INVALID_XML_RESOURCE);
            xmlUnmarshaller.unmarshal(url);
        } catch (Exception e) {
            wasExceptionCaught = true;
        }
        assertTrue("An exception should have been thrown.", wasExceptionCaught);
    }

    public void testValidFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(VALID_XML_RESOURCE);
        xmlUnmarshaller.unmarshal(url);
    }

    public void testValidateValidFile() {
        try {
            xmlUnmarshaller.setSchema(schema);
            URL url = Thread.currentThread().getContextClassLoader().getResource(VALID_XML_RESOURCE);
            xmlUnmarshaller.unmarshal(url);
        } catch(UnsupportedOperationException uoe) {
        } catch(XMLMarshalException xme) {
            //if the parser doesn't support the setSchema API, this is a valid outcome
            //for the test.
        }
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
