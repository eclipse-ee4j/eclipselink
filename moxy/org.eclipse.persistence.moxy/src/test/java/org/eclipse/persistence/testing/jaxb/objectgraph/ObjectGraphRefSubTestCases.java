/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ObjectGraphRefSubTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_complex.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_complex_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_complex.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_complex_write.json";

    public ObjectGraphRefSubTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Customer.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.OBJECT_GRAPH, "complex");
        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, "complex");
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.gender = "Male";
        cust.address = new Address();
        cust.address.street = "123 Fake Street";
        cust.phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber pn = new PhoneNumber();
        pn.number = "123-4567";
        cust.phoneNumbers.add(pn);

        pn = new PhoneNumber();
        pn.number = "345-6789";
        cust.phoneNumbers.add(pn);
        return cust;
    }

    @Override
    public Object getWriteControlObject() {
        Customer cust = new Customer();
        cust.age = "35";
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.gender = "Male";
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
