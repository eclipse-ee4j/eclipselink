/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class JAXBContextXPathTestCases extends TestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/xmlelement.xml";
    private static final String CONTROL_FIRST_NAME = "First";
    private static final String CONTROL_LAST_NAME = "Last";
    private static final String CONTROL_SURNAME = "Surname";
    private static final String CONTROL_NICKNAME_1 = "A";
    private static final String CONTROL_NICKNAME_2 = "B";
    private static final String CONTROL_STREET = "123 A Street";
    private static final String CONTROL_PHONE_NUMBER_1 = "555-1111";
    private static final String CONTROL_PHONE_NUMBER_2 = "555-2222";

    private JAXBContext jaxbContext;

    public JAXBContextXPathTestCases(String name) {
        super(name);
    }


    @Override
    protected void setUp() throws Exception {
        jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] {Customer.class}, null);
    }

    public void testDirectXPath() {
        String xPath = "personal-info[@pi-type='last-name']/name[@name-type='surname']/text()";
        String test = jaxbContext.getValueByXPath(getControlObject(), xPath, null, String.class);
        assertEquals("Last", test);
    }

    public void testDirectCollectionXPath() {
        Customer control = getControlObject();
        String xPath = "personal-info[@pi-type='nickname']/name[@name-type='nickname']/text()";
        List test = jaxbContext.getValueByXPath(control, xPath, null, List.class);
        assertSame(control.getNicknames(), test);
    }
/*
    public void testDirectCollectionXPathByPosition() {
        String xPath = "personal-info[@pi-type='nickname']/name[@name-type='nickname'][2]/text()";
        String test = jaxbContext.getValueByXPath(getControlObject(), xPath, null, String.class);
        assertEquals("B", test);
    }
*/
    public void testCompositeObject() {
        Customer control = getControlObject();
        String xPath = "contact[@method=\"address\"]";
        Address test = jaxbContext.getValueByXPath(control, xPath, null, Address.class);
        assertSame(control.getAddress(), test);
    }

    public void testCompositeCollection() {
        Customer control = getControlObject();
        String xPath = "contact[@method=\"phone number\"]";
        List test = jaxbContext.getValueByXPath(control, xPath, null, List.class);
        assertSame(control.getPhoneNumbers(), test);
    }
/*
    public void testCompositeCollectionByPosition() {
        Customer control = getControlObject();
        String xPath = "contact[@method=\"phone number\"][2]";
        PhoneNumber test = jaxbContext.getValueByXPath(control, xPath, null, PhoneNumber.class);
        assertSame(control.getPhoneNumbers().get(1), test);
    }
*/
    protected Customer getControlObject() {
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

}
