/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ContactAsNestedWithAddressXsiTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contact_addressxsi.xml";
    
    public ContactAsNestedWithAddressXsiTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
		Customer cust = new Customer();
        Address add = new Address();
        add.setId("123");
		add.setStreet("1 A Street");
		cust.setContact(add);
        return cust;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactAsNestedWithAddressXsiTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}