/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 30 August 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance.generics;

import java.util.ArrayList;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class GenericAdapterTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/inheritance/generics/link.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/inheritance/generics/link.json";

    public GenericAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[] { Customer.class };
        setClasses(classes);

        // Setup adapter caches
        getControlObject();
    }

    protected Object getControlObject() {
        Customer c = new Customer();

        AddressAdapter addressAdapter = new AddressAdapter();
        Address linkedAddress = new Address();
        linkedAddress.setHref("http://www.example.com/address/1");
        addressAdapter.cacheObject(linkedAddress);
        getJAXBUnmarshaller().setAdapter(addressAdapter);

        c.setAddress(linkedAddress);

        PhoneNumberAdapter phoneNumberAdapter = new PhoneNumberAdapter();
        PhoneNumber linkedPhoneNumber = new PhoneNumber();
        linkedPhoneNumber.setHref("http://www.example.com/phoneNumber/1");
        phoneNumberAdapter.cacheObject(linkedPhoneNumber);
        getJAXBUnmarshaller().setAdapter(phoneNumberAdapter);

        PhoneNumber regPhoneNumber = new PhoneNumber();
        regPhoneNumber.setNumber("555-1111");

        ArrayList<PhoneNumber> phones = new ArrayList<PhoneNumber>(10);
        phones.add(linkedPhoneNumber);
        phones.add(regPhoneNumber);
        c.setPhoneNumber(phones);

        return c;
    }

}
