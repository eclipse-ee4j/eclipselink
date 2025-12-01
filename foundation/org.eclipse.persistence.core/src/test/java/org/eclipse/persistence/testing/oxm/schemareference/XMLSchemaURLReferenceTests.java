/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.schemareference;

import org.eclipse.persistence.oxm.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class XMLSchemaURLReferenceTests extends org.eclipse.persistence.testing.oxm.XMLTestCase {
    private XMLSchemaURLReference schemaRef;
    private java.net.URL schemaUrl;
    private DocumentBuilder parser;

    public XMLSchemaURLReferenceTests(String name) {
        super(name);
    }

    @Override
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
            assertEquals("An unexpected XMLMarshalException was caught", XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA, e.getErrorCode());
            return;
        }
        fail("An XMLValidation should have been caught but wasn't.");
    }

    public void testInvalidURL() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
        try {
            schemaReference.setURL(new URL("http://"));
        } catch (java.net.MalformedURLException malformedException) {
            fail();
            return;
        }

        try {
            Document doc = parser.parse(stream);
            boolean isValid = schemaReference.isValid(doc, null);
        } catch (XMLMarshalException e) {
            assertEquals("An unexpected XMLMarshalException was caught", XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA, e.getErrorCode());
            return;
        }
        fail("An XMLValidation should have been caught but wasn't.");
    }
}
