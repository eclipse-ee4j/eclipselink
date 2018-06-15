/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self;

import java.io.InputStream;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefSelfTestCases extends JAXBWithJSONTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/self/control.json";
    private static final String CONTROL_JSON_SCHEMA = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/self/controlSchema.json";
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

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(CONTROL_JSON_SCHEMA);
        super.generateJSONSchema(controlSchema);

    }


}
