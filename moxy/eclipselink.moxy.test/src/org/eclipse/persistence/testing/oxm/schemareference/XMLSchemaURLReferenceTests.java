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
package org.eclipse.persistence.testing.oxm.schemareference;

import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.schema.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import java.io.InputStream;
import org.w3c.dom.Document;
import java.net.*;

public class XMLSchemaURLReferenceTests extends org.eclipse.persistence.testing.oxm.XMLTestCase {
    private XMLSchemaURLReference schemaRef;
    private java.net.URL schemaUrl;
    private DocumentBuilder parser;

    public XMLSchemaURLReferenceTests(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        schemaUrl = ClassLoader.getSystemClassLoader().getResource("org/eclipse/persistence/testing/oxm/schemareference/employee.xsd");
        schemaRef = new XMLSchemaURLReference(schemaUrl);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testValidDocument() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_good.xml");
        Document doc = parser.parse(stream);
        boolean isValid = schemaRef.isValid(doc, null);
        assertTrue("Document was reported as invalid", isValid);
    }

    public void testInvalidDocument() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");
        Document doc = parser.parse(stream);
        boolean isValid = schemaRef.isValid(doc, null);
        assertFalse("Invalid Document found to be valid", isValid);
    }

    public void testGetURLInvalidURL() throws Exception {
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        try {
            schemaReference.setURL(new URL("http://", "", ""));
        } catch (MalformedURLException malformedException) {
            // don't do anything we want to test an Invalid URL
        }

        try {
            java.net.URL resultingURL = schemaReference.getURL();
        } catch (XMLMarshalException e) {
            assertTrue("An unexpected XMLMarshalException was caught", e.getErrorCode() == XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testInvalidURL() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        try {
            schemaReference.setURL(new URL("http://"));
        } catch (java.net.MalformedURLException malformedException) {
            assertTrue(false);
            return;
        }

        try {
            Document doc = parser.parse(stream);
            boolean isValid = schemaReference.isValid(doc, null);
        } catch (XMLMarshalException e) {
            assertTrue("An unexpected XMLMarshalException was caught", e.getErrorCode() == XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }
}
