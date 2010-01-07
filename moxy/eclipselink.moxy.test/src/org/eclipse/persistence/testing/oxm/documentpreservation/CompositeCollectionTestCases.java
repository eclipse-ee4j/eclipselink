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
 *  @version $Header: CompositeCollectionTestCases.java 30-jul-2007.15:32:40 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class CompositeCollectionTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public DocumentBuilder parser;

    public CompositeCollectionTestCases() {
        super("Doc Pres Comp Collection Tests");
    }

    public CompositeCollectionTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext("DocumentPreservationSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testSwitchedOrder() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_1.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        Vector phones = emp.getPhoneNumbers();
        Vector newPhones = new Vector();
        for (int i = 0; i < phones.size(); i++) {
            PhoneNumber pn = (PhoneNumber)phones.elementAt(phones.size() - (i + 1));
            newPhones.addElement(pn);
        }
        emp.setPhoneNumbers(newPhones);
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);

    }

    public void testRemoveElement() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_2.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        Vector phones = emp.getPhoneNumbers();
        phones.remove(0);

        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);

    }

    public void testRemoveAndAddElement() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_3.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        Vector phones = emp.getPhoneNumbers();
        phones.remove(0);
        PhoneNumber num = new PhoneNumber();
        num.setAreaCode(905);
        num.setExchange(123);
        num.setNumber(4321);
        phones.addElement(num);

        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testAddElement() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_4.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        Vector phones = emp.getPhoneNumbers();
        PhoneNumber num = new PhoneNumber();
        num.setAreaCode(123);
        num.setExchange(456);
        num.setNumber(7890);
        phones.addElement(num);
        System.out.println("WOIDUHWOUIRH");
        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testNullCollection() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_5.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setPhoneNumbers(null);

        Document outputDoc = (Document)marshaller.objectToXML(emp);
        assertXMLIdentical(controlDocument, outputDoc);
    }

    public void testEmptyCollection() throws Exception {
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/composite_collection_5.xml");
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/documentpreservation/roundTrip.xml");

        Employee emp = (Employee)unmarshaller.unmarshal(sourceDocument);
        emp.setPhoneNumbers(new Vector());

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
