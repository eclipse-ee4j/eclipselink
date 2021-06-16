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
package org.eclipse.persistence.testing.oxm.inheritance.typetests.any.collection;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactMethod;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class ContactsAsAnyNestedTestCases extends XMLWithJSONMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contacts_as_any_noxsi.xml";
    private static final String JSON_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/customer_with_contacts_as_any_noxsi.json";

    public ContactsAsAnyNestedTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionTypeProject());
        setControlDocument(READ_DOC);
        setControlJSON(JSON_DOC);
    }

    public Object getControlObject() {
        Customer cust = new Customer();
        ArrayList contacts = new ArrayList();
        ContactMethod cm = new ContactMethod();
        cm.setId("123");
        contacts.add(cm);
        cm = new ContactMethod();
        cm.setId("456");
        contacts.add(cm);
        cm = new ContactMethod();
        cm.setId("789");
        contacts.add(cm);
        cust.setContactMethods(contacts);
        return cust;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.any.collection.ContactAsAnyNestedTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
