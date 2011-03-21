/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.io.InputStream;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.Document;

public class BinderTestCases extends XMLTestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/xmlelement.xml";
    private static final String CONTROL_FIRST_NAME = "First";
    private static final String CONTROL_LAST_NAME = "Last";
    private static final String CONTROL_SURNAME = "Surname";
    private static final String CONTROL_NICKNAME_1 = "A";
    private static final String CONTROL_NICKNAME_2 = "B";
    private static final String CONTROL_STREET = "123 A Street";
    private static final String CONTROL_PHONE_NUMBER_1 = "555-1111";
    private static final String CONTROL_PHONE_NUMBER_2 = "555-2222";

    public BinderTestCases(String name) {
        super(name);
    }

    public void testUnmarshal() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Customer.class}, null);
        Binder binder = jc.createBinder();
        Customer test = (Customer) binder.unmarshal(getControlDocument());

        assertEquals(getControlObject(), test);
    }
/*
    public void testMarshal() throws Exception { 
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Customer.class}, null);
        Binder binder = jc.createBinder();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document testDocument = db.newDocument();
        binder.marshal(getControlObject(), testDocument);
        assertXMLIdentical(getControlDocument(), testDocument);
    }
*/
    protected Object getControlObject() {
        Customer customer = new Customer();
        customer.setFirstName(CONTROL_FIRST_NAME);
        customer.setLastName(CONTROL_LAST_NAME);
        customer.setSurname(CONTROL_SURNAME);
        customer.getNicknames().add("A");
        customer.getNicknames().add("B");

        Address address = new Address();
        address.setStreet(CONTROL_STREET);
        customer.setAddress(address);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setValue(CONTROL_PHONE_NUMBER_1);
        customer.getPhoneNumbers().add(phoneNumber1);

        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setValue(CONTROL_PHONE_NUMBER_2);
        customer.getPhoneNumbers().add(phoneNumber2);

        return customer;
    }

    protected Document getControlDocument() throws Exception {
        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_RESOURCE);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(xml);
        xml.close();
        return document;
    }
}