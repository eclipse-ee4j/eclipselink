/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 19 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XmlLocationSchemaGenTests extends SchemaGenTestCases {

    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmllocation/schema.xsd";
    private static final String XSD_NONTRANS_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmllocation/schema-nontransient.xsd";

    public XmlLocationSchemaGenTests(String name) {
        super(name);
    }

    public void testSchemaGen() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{ Data.class, SubData.class, DetailData.class, LeafData.class }, null);
        StringOutputResolver sor = new StringOutputResolver();
        ctx.generateSchema(sor);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource testSchemaInputSource = new InputSource(new StringReader(sor.getSchema()));
        Document testSchemaDocument = db.parse(testSchemaInputSource);

        InputStream controlSchemaInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(XSD_RESOURCE);
        Document controlSchemaDocument = db.parse(controlSchemaInputStream);

        /*
        try {
            System.out.println("CONTROL");
            printDocument(controlSchemaDocument, System.out);
            System.out.println("TEST");
            printDocument(testSchemaDocument, System.out);
        } catch (Exception e) {
            // TODO: handle exception
        }
        */

        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        assertTrue("Test schema did not match Control schema.", xmlComparer.isSchemaEqual(controlSchemaDocument, testSchemaDocument));
    }

    public void testSchemaGenNonTransient() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{ DataNT.class, SubDataNT.class, DetailDataNT.class, LeafDataNT.class }, null);
        StringOutputResolver sor = new StringOutputResolver();
        ctx.generateSchema(sor);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource testSchemaInputSource = new InputSource(new StringReader(sor.getSchema()));
        Document testSchemaDocument = db.parse(testSchemaInputSource);

        InputStream controlSchemaInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(XSD_NONTRANS_RESOURCE);
        Document controlSchemaDocument = db.parse(controlSchemaInputStream);

        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        assertTrue("Test schema did not match Control schema.", xmlComparer.isSchemaEqual(controlSchemaDocument, testSchemaDocument));
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

    public static void printDocument(Document doc, OutputStream out) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

}