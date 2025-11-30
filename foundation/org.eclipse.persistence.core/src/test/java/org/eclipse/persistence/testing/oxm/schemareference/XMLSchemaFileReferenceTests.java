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
import org.eclipse.persistence.oxm.schema.XMLSchemaFileReference;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;

public class XMLSchemaFileReferenceTests extends org.eclipse.persistence.testing.oxm.XMLTestCase {
    private java.io.File schemaFile;
    private DocumentBuilder parser;

  public XMLSchemaFileReferenceTests(String name) {
    super(name);
  }
  @Override
  public void setUp() throws Exception {
    java.net.URL schemaURL = ClassLoader.getSystemClassLoader().getResource("org/eclipse/persistence/testing/oxm/schemareference/employee.xsd");
    schemaFile = new java.io.File(schemaURL.getFile());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
  }
  public void testValidDocument() throws Exception {
    XMLSchemaFileReference schemaRef = new XMLSchemaFileReference(schemaFile);
    InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_good.xml");
    Document doc = parser.parse(stream);
    boolean isValid = schemaRef.isValid(doc, null);
    assertTrue("Document was reported as invalid", isValid);
  }
  public void testInvalidDocument() throws Exception {
    XMLSchemaFileReference schemaRef = new XMLSchemaFileReference(schemaFile);
    InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");
    Document doc = parser.parse(stream);
    boolean isValid = schemaRef.isValid(doc, null);
    assertFalse("Invalid Document found to be valid", isValid);
  }

    public void testInvalidFile() throws Exception
    {
    XMLSchemaFileReference schemaReference = new XMLSchemaFileReference();
        File file = new File("");
        schemaReference.setFile(file);
    InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");

        try{
          Document doc = parser.parse(stream);
            boolean isValid = schemaReference.isValid(doc, null);
        }catch(XMLMarshalException e)
        {
            assertEquals("An unexpected XMLMarshalException was caught", XMLMarshalException.ERROR_RESOLVING_XML_SCHEMA, e.getErrorCode());
            return;
        }
        fail("An XMLValidation should have been caught but wasn't.");
    }
}
