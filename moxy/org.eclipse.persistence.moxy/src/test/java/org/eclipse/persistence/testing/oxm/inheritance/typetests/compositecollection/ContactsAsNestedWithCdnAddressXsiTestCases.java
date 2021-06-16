/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class ContactsAsNestedWithCdnAddressXsiTestCases extends XMLWithJSONMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/ns_customer_with_contacts_cdnaddressxsi.xml";
    private static final String JSON_READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/ns_customer_with_contacts_cdnaddressxsi.json";

    public ContactsAsNestedWithCdnAddressXsiTestCases(String name) throws Exception {
        super(name);
        setProject(new COMCollectionTypeProject());
        setControlDocument(READ_DOC);
        setControlJSON(JSON_READ_DOC);
    }

    public Object getControlObject() {
        Customer cust = new Customer();
        ArrayList addresses = new ArrayList();
        CanadianAddress add = new CanadianAddress();
        add.setId("123");
        add.setStreet("1 A Street");
        add.setPostalCode("A1B 2C3");
        addresses.add(add);
        add = new CanadianAddress();
        add.setId("456");
        add.setStreet("2 A Street");
        add.setPostalCode("A1B 2C3");
        addresses.add(add);
        add = new CanadianAddress();
        add.setId("789");
        add.setStreet("3 A Street");
        add.setPostalCode("A1B 2C3");
        addresses.add(add);
        cust.setContactMethods(addresses);
        return cust;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithCdnAddressXsiTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
