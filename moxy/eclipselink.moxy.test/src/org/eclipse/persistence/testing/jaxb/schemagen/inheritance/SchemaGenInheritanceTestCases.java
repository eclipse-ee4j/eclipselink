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
package org.eclipse.persistence.testing.jaxb.schemagen.inheritance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.schemagen.date.Employee;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaGenInheritanceTestCases extends TestCase {

    private static final String CONTROL_SCHEMA = "org/eclipse/persistence/testing/jaxb/schemagen/inheritance/inheritance.xsd";
    private static final String CONTROL_SCHEMA_2 = "org/eclipse/persistence/testing/jaxb/schemagen/inheritance/inheritance2.xsd";

    public SchemaGenInheritanceTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "JAXB SchemaGen: Inheritance: " + super.getName();
    }

    public void testSchemaGen() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Car.class, Toyota.class);
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

    public void testSchemaGenTransient() throws Exception {
        JAXBException e = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TransientCar.class, Mazda.class);
        } catch(javax.xml.bind.JAXBException ex) {
            e = (JAXBException)ex.getCause();
            fail("Exception was thrown incorrectly.");
        }
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
