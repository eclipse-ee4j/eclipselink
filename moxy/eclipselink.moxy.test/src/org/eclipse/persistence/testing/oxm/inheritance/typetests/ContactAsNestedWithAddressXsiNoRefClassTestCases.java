/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;

public class ContactAsNestedWithAddressXsiNoRefClassTestCases extends ContactAsNestedWithAddressXsiTestCases {
    public ContactAsNestedWithAddressXsiNoRefClassTestCases(String name) throws Exception {
        super(name);
        Project p = new TypeProject();
        ((XMLCompositeObjectMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contact")).setReferenceClass(null);
        ((XMLCompositeObjectMapping)p.getDescriptor(Customer.class).getMappingForAttributeName("contact")).setReferenceClassName(null);
        setProject(p);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.ContactAsNestedWithAddressXsiNoRefClassTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
