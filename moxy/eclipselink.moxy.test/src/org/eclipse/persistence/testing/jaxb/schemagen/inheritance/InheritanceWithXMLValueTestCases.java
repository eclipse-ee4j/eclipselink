/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.6 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.inheritance;

import junit.framework.TestCase;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class InheritanceWithXMLValueTestCases extends TestCase {
    private static final String CONTROL_SCHEMA = "org/eclipse/persistence/testing/jaxb/schemagen/inheritance/inheritance_xml_value.xsd";

    public InheritanceWithXMLValueTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "JAXB SchemaGen: Inheritance: With XMLValue" + super.getName();
    }

    public void testSchemaGen() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{ParentEntity.class, ChildEntity.class});
        StringOutputResolver sor = new StringOutputResolver();
        jaxbContext.generateSchema(sor);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource testSchemaInputSource = new InputSource(new StringReader(sor.getSchema()));
        Document testSchemaDocument = db.parse(testSchemaInputSource);

        InputStream controlSchemaInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONTROL_SCHEMA);
        Document controlSchemaDocument = db.parse(controlSchemaInputStream);

        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        assertTrue("Test schema did not match control schema.", xmlComparer.isSchemaEqual(controlSchemaDocument, testSchemaDocument));
    }

    private class StringOutputResolver extends SchemaOutputResolver {

        private StringWriter stringWriter;

        public StringOutputResolver() {
            stringWriter = new StringWriter();
        }

        @Override
        public Result createOutput(String arg0, String arg1) throws IOException {
            return new StreamResult(stringWriter);
        }

        private String getSchema() {
            return stringWriter.toString();
        }
    }
}
