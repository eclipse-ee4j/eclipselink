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

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AddressAsNestedWithCdnAddressXsiTestCases extends XMLWithJSONMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/employee_with_address_cdnaddressxsi.xml";
    private static final String JSON_READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/employee_with_address_cdnaddressxsi.json";

    public AddressAsNestedWithCdnAddressXsiTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
        setControlJSON(JSON_READ_DOC);
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        CanadianAddress add = new CanadianAddress();
        add.setId("123");
        add.setStreet("1 A Street");
        add.setPostalCode("A1B 2C3");
        emp.setAddress(add);
        return emp;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.AddressAsNestedWithCdnAddressXsiTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}