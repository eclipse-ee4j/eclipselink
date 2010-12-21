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
*     bdoughan - March 16/2010 - 2.0.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.date;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DateTestCases extends SchemaGenTestCases {

    public DateTestCases(String name) {
        super(name);
    }

    public void testSchemaGen() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Employee.class);
        StringOutputResolver sor = new StringOutputResolver();
        jaxbContext.generateSchema(sor);
        //System.out.println(sor.getSchema());

        Employee employee = new Employee();
        employee.setUtilDateProperty(new java.util.Date());
        employee.setSqlDateProperty(new java.sql.Date(75, 1, 21));
        employee.setSqlTimeProperty(new java.sql.Time(7,47,0));
        employee.setSqlTimestampProperty(new java.sql.Timestamp(75, 1, 21, 7, 47, 0, 0));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource testSchemaInputSource = new InputSource(new StringReader(sor.getSchema()));
        Document testSchemaDocument = db.parse(testSchemaInputSource);

        InputStream controlSchemaInputStream = new FileInputStream(new File("org/eclipse/persistence/testing/jaxb/schemagen/date/Date.xsd"));
        Document controlSchemaDocument = db.parse(controlSchemaInputStream);

        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        assertTrue("test did not match control document", xmlComparer.isSchemaEqual(controlSchemaDocument, testSchemaDocument));
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