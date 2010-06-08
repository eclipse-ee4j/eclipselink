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
package org.eclipse.persistence.testing.oxm.documentpreservation;

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;

/**
 *  @version $Header: CompositeObjectTestCases.java 30-jul-2007.15:32:49 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class CompositeObjectTestCases extends OXTestCase {
    public XMLContext context;
    public XMLUnmarshaller unmarshaller;
    public XMLMarshaller marshaller;
    public DocumentBuilder parser;

    public CompositeObjectTestCases() {
        super("Doc Pres Comp Object Tests");
    }

    public CompositeObjectTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext("DocumentPreservationSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        parser = documentBuilderFactory.newDocumentBuilder();
    }

    public void testUpdateFields() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_object_1.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.getAddress().setCity("New Minas");
        ((CanadianAddress)emp.getAddress()).setProvince("NS");
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testNullObject() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_object_2.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setAddress(null);
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testSwitchedAddress() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_object_3.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        USAddress addr = new USAddress();
        addr.setStreet("2001 Odessy Drive");
        addr.setCity("New York");
        addr.setState("NY");
        addr.setZipCode("90210");
        emp.setAddress(addr);
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
