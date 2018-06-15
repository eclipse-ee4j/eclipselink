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
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ObjectGraphBasicTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_basic.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_basic_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_basic.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_basic_write.json";

    public ObjectGraphBasicTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Customer.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.OBJECT_GRAPH, "basic");
        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, "basic");
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.address = new Address();
        cust.address.city = "Ottawa";
        cust.address.country = "Canada";

        return cust;
    }

    @Override
    public Object getWriteControlObject() {
        Customer cust = new Customer();
        cust.age = "35";
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.gender = "Make";
        cust.address = new Address();
        cust.address.city = "Ottawa";
        cust.address.country = "Canada";
        cust.address.street = "123 Fake Street";
        cust.phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "123-4567";
        cust.phoneNumbers.add(pn);

        pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "345-6789";
        cust.phoneNumbers.add(pn);
        return cust;
    }
}
