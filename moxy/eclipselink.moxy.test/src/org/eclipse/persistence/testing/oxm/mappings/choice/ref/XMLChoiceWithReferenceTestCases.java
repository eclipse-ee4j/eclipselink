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
// dmccann - December 01/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class XMLChoiceWithReferenceTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ref/read_doc.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ref/read_doc.json";

    public XMLChoiceWithReferenceTestCases(String name) throws Exception {
      super(name);
      setControlDocument(XML_RESOURCE);
      setControlJSON(JSON_RESOURCE);
      setProject(new XMLChoiceWithReferenceProject());
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.employees = new ArrayList<Employee>();
        root.addresses = new ArrayList<Address>();
        root.phones = new ArrayList<PhoneNumber>();

        Employee employee = new Employee();
        employee.name = "Jane Doe";
        employee.contact = new ArrayList();

        Address addr = new Address();
        addr.id = "1";
        addr.street = "123 Abc Street";
        addr.zip = "123456";

        root.addresses.add(addr);

        addr = new Address();
        addr.id = "2";
        addr.street = "321 Cba Street";
        addr.zip = "654321";

        root.addresses.add(addr);
        root.employees.add(employee);

        PhoneNumber phone = new PhoneNumber();
        phone.id = "0";
        phone.number = "234-4321";
        root.phones.add(phone);

        phone = new PhoneNumber();
        phone.id = "1";
        phone.number = "123-3456";
        employee.contact = phone;
        root.phones.add(phone);

        phone = new PhoneNumber();
        phone.id = "11";
        phone.number = "555-7777";
        root.phones.add(phone);

        return root;
    }
}
