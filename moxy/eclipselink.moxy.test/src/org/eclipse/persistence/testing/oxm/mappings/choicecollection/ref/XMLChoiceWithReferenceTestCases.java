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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class XMLChoiceWithReferenceTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ref/read_doc.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ref/write_doc.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ref/read_doc.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ref/write_doc.json";
    public XMLChoiceWithReferenceTestCases(String name) throws Exception {
      super(name);
      setControlDocument(XML_RESOURCE);
      setWriteControlDocument(XML_WRITE_RESOURCE);
      setControlJSON(JSON_RESOURCE);
      setControlJSONWrite(JSON_WRITE_RESOURCE);
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
        employee.contact.add(addr);

        addr = new Address();
        addr.id = "2";
        addr.street = "321 Cba Street";
        addr.zip = "654321";

        root.addresses.add(addr);
        employee.contact.add(addr);

        root.employees.add(employee);

        PhoneNumber phone = new PhoneNumber();
        phone.id = "1";
        phone.number = "123-3456";
        employee.contact.add(phone);
        root.phones.add(phone);

        return root;

    }

}
