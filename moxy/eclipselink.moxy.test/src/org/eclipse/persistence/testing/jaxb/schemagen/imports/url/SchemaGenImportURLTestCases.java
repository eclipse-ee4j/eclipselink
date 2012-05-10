/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - July 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.imports.url;

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

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.url.a.Employee;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaGenImportURLTestCases extends TestCase {
    private static final String CONTROL_SCHEMA = "org/eclipse/persistence/testing/jaxb/schemagen/imports/url/employee.xsd";
    
    public SchemaGenImportURLTestCases(String name) throws Exception {
        super(name);
    }
    
    public String getName() {
        return "JAXB SchemaGen: Import URL: " + super.getName();
    }

    public void testSchemaGen() throws Exception {
        JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{Employee.class}, null);
        StringOutputResolver sor = new StringOutputResolver();
        jaxbContext.generateSchema(sor);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource testSchemaInputSource = new InputSource(new StringReader(sor.getSchema()));
        Document testSchemaDocument = db.parse(testSchemaInputSource);

        InputStream controlSchemaInputStream = new FileInputStream(new File(CONTROL_SCHEMA));
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
