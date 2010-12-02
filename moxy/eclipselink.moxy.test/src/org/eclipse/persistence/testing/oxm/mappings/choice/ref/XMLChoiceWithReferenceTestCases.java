/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - December 01/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLChoiceWithReferenceTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ref/read_doc.xml";
    
    public XMLChoiceWithReferenceTestCases(String name) throws Exception {
      super(name);
      setControlDocument(XML_RESOURCE);
      setWriteControlDocument(XML_RESOURCE);
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
