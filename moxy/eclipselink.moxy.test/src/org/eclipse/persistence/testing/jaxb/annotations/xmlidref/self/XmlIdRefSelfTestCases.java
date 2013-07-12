/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefSelfTestCases extends JAXBWithJSONTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/self/control.json";
    private static final String CONTROL_XML = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/self/control.xml";

    public XmlIdRefSelfTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Customer.class});
        setControlJSON(CONTROL_JSON);
        setControlDocument(CONTROL_XML);
    }

    @Override
    protected Customer getControlObject() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.id = "A";

        Contact contact = new Contact();
        contact.idRefPhoneNumber = phoneNumber;
        contact.idRefPhoneNumbers.add(phoneNumber);

        AddressSelfTarget address = new AddressSelfTarget();
        address.contact = contact;

        AddressSelfSource adaptedAddress = new AddressSelfSource();
        adaptedAddress.address = address;
        
        Customer customer = new Customer();
        customer.address = adaptedAddress;
        customer.phoneNumber = phoneNumber;

        return customer;
    }

}
