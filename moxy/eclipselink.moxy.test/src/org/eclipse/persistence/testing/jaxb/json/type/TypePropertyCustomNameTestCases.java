/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.type;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests to marshall, unmarshal JSON with JSON_TYPE_ATTRIBUTE_NAME marshall and unmarshall property.
 *
 * @author Radek Felcman
 *
 */
public class TypePropertyCustomNameTestCases extends JSONMarshalUnmarshalTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/type/type_property_custom_name.json";

    public TypePropertyCustomNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{PersonWithType.class, Contact.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();

        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonMarshaller.setProperty(MarshallerProperties.JSON_TYPE_ATTRIBUTE_NAME, "mytype");

        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_TYPE_ATTRIBUTE_NAME, "mytype");
    }

    protected Object getControlObject() {

        Address a = new Address();
        a.contactId = 1;
        a.street = "Main Street 1";
        a.city = "Prague";
        a.zip = "110 00";

        List<Contact> secondaryContacts = new ArrayList<>();
        Address a1 = new Address();
        a1.contactId = 2;
        a1.street = "Under Bridge";
        a1.city = "Berlin";
        a1.zip = "123456";
        secondaryContacts.add(a1);

        Phone p = new Phone();
        p.contactId = 3;
        p.number = "987654321";
        secondaryContacts.add(p);

        Contact c = new Contact();
        c.contactId = 4;
        secondaryContacts.add(c);

        CustomerWithInheritance customer = new CustomerWithInheritance();
        customer.name = "theName";
        customer.type = "propertyType";
        customer.primaryContact = a;

        customer.secondaryContacts = secondaryContacts;

        QName name = new QName("");
        JAXBElement<Object> jbe = new JAXBElement<Object>(name, Object.class, customer);
        return jbe;
    }

}
