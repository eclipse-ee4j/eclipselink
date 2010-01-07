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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.schema.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;

import java.io.InputStream;
import org.w3c.dom.Document;

public class XMLSchemaCPReferenceTests extends org.eclipse.persistence.testing.oxm.XMLTestCase {
    private XMLSchemaClassPathReference schemaRef;
    private DocumentBuilder parser;

    public XMLSchemaCPReferenceTests(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        schemaRef = new XMLSchemaClassPathReference("org/eclipse/persistence/testing/oxm/schemareference/employee.xsd");
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

    public void testInvalidClasspath() throws Exception {
        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference("");
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");

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
