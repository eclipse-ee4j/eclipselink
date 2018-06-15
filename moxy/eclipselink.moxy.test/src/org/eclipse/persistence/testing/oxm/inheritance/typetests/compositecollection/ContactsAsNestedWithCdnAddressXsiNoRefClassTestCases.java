/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;

public class ContactsAsNestedWithCdnAddressXsiNoRefClassTestCases extends ContactsAsNestedWithCdnAddressXsiTestCases {
    public ContactsAsNestedWithCdnAddressXsiNoRefClassTestCases(String name) throws Exception {
        super(name);
        Project p = new COMCollectionTypeProject();
        ((XMLCompositeCollectionMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contactMethods")).setReferenceClass(null);
        ((XMLCompositeCollectionMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contactMethods")).setReferenceClassName(null);
        setProject(p);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection.ContactsAsNestedWithCdnAddressXsiNoRefClassTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
