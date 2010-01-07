/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import org.eclipse.persistence.testing.oxm.inheritance.typetests.Address;

public class ContactsAsNestedWithAddressXsiTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contacts_addressxsi.xml";
    
    public ContactsAsNestedWithAddressXsiTestCases(String name) throws Exception {
        super(name);
        setProject(new COMCollectionTypeProject());
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
		Customer cust = new Customer();
		ArrayList addresses = new ArrayList();
        Address add = new Address();
        add.setId("123");
		add.setStreet("1 A Street");
		addresses.add(add);
        add = new Address();
        add.setId("456");
		add.setStreet("2 A Street");
		addresses.add(add);
        add = new Address();
        add.setId("789");
		add.setStreet("3 A Street");
		addresses.add(add);
		cust.setContactMethods(addresses);
        return cust;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithAddressXsiTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
