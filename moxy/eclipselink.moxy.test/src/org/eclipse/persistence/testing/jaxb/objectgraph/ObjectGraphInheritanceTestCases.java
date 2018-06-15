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


public class ObjectGraphInheritanceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/employee.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/employee_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/employee.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/employee_write.json";

    public ObjectGraphInheritanceTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Employee.class, ContactInfo.class, AddressInh.class, PhoneNumberInh.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.OBJECT_GRAPH, "simple");
        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, "simple");
    }

    @Override
    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.setContactInfo(new ArrayList<ContactInfo>());
        AddressInh address = new AddressInh();
        address.contactType="billingAddress";
        address.setCity("Any Town");
        emp.getContactInfo().add(address);

        PhoneNumberInh pn = new PhoneNumberInh();
        pn.number = "555-1111";
        pn.contactType = "work";
        emp.getContactInfo().add(pn);
        return emp;
    }

    @Override
    public Object getWriteControlObject() {
        Employee emp = new Employee();
        emp.setContactInfo(new ArrayList<ContactInfo>());
        AddressInh address = new AddressInh();
        address.id=1;
        address.contactType="billingAddress";
        address.setCity("Any Town");
        address.setStreet("1 A St");
        emp.getContactInfo().add(address);

        PhoneNumberInh pn = new PhoneNumberInh();
        pn.id = 2;
        pn.number = "555-1111";
        pn.contactType = "work";
        emp.getContactInfo().add(pn);
        return emp;
    }
}
