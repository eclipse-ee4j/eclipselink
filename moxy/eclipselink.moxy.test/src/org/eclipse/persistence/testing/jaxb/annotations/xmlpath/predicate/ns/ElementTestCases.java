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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class ElementTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/ns/xmlelement.xml";
    private static final String CONTROL_FIRST_NAME = "First";
    private static final String CONTROL_LAST_NAME = "Last";
    private static final String CONTROL_SURNAME = "Surname";
    private static final String CONTROL_NICKNAME_1 = "A";
    private static final String CONTROL_NICKNAME_2 = "B";
    private static final String CONTROL_STREET = "123 A Street";
    private static final String CONTROL_PHONE_NUMBER_1 = "555-1111";
    private static final String CONTROL_PHONE_NUMBER_2 = "555-2222";

    public ElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Customer.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
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

}