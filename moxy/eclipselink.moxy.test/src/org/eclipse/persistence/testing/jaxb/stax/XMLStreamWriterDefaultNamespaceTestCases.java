/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class XMLStreamWriterDefaultNamespaceTestCases extends OXTestCase {

    public XMLStreamWriterDefaultNamespaceTestCases(String name) {
        super(name);
    }

    public void testDefaultNamespaceOverride() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { Employee.class }, null);
        StringWriter writer = new StringWriter();
        XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
        streamWriter.writeStartElement("", "root", "someNamespace");
        streamWriter.writeDefaultNamespace("someNamespace");
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, new Boolean(true));

        marshaller.marshal(new JAXBElement(new QName("employee"), Employee.class, new Employee()), streamWriter);
        streamWriter.writeEndElement();
        streamWriter.writeEndDocument();
        streamWriter.flush();

        String xml = "<root xmlns=\"someNamespace\"><employee xmlns=\"\"></employee></root>";
        String xml2 = "<root xmlns=\"someNamespace\"><employee xmlns=\"\"/></root>";

        String writerString = writer.toString();

        assertTrue("Incorrect XML: " + writerString, writerString.equals(xml) || writerString.equals(xml2));
    }

    /**
     * Testcase for Bug 387464
     */
    public void testDuplicateDefaultNamespace() throws Exception {
        String testXmlData = "<child xmlns=\"someDefaultNameSpace\" xmlns:bi=\"definedBINameSpace\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test</child>";
        EmployeeLax employeeLax = createEmployeeLax(testXmlData);
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeLax.class}, null);

        StringWriter writer = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        // Set IS_REPAIRING_NAMESPACES to true.
        factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(true));

        XMLStreamWriter streamWriter = factory.createXMLStreamWriter(writer);

        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(employeeLax, streamWriter);

        // Flush the contents of stream writer.
        streamWriter.flush();

        String xml = "<?xml version=\"1.0\"?><employee xmlns=\"\">" + testXmlData + "</employee>";
        String xml2 = "<?xml version=\'1.0\'?><employee>" + testXmlData + "</employee>";

        String writerString = writer.toString();
        boolean equals = writerString.equals(xml) || writerString.equals(xml2);
        if(!equals){
           log("EXPECTED XML:" + xml);
           log("OR EXPECTED XML:" + xml2);
           log("ACTUAL XML:" + writerString);
        }
        assertTrue("Incorrect XML: " + writerString, equals);
    }

    protected EmployeeLax createEmployeeLax(String testXmlData) throws Exception {
        EmployeeLax employeeLax = new ObjectFactory().createEmployeeLax();
        Element childElement = parseXml(new ByteArrayInputStream(testXmlData.getBytes()));
        employeeLax.setChild(childElement);

        return employeeLax;
    }

    protected static Element parseXml(InputStream in) throws Exception {
       DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
       docBuilderFactory.setNamespaceAware(true);
       DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
       Document xmlDocument = docBuilder.parse(in);
       return xmlDocument.getDocumentElement();
   }
}
