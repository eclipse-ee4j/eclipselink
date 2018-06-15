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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CanadianAddressAsRootTestCases extends XMLMappingTestCases {
    private static final String READ_DOC = "org/eclipse/persistence/testing/oxm/inheritance/typetests/cdnaddress_noxsi.xml";

    public CanadianAddressAsRootTestCases(String name) throws Exception {
        super(name);
        setProject(new TypeProject());
        setControlDocument(READ_DOC);
    }

    public Object getControlObject() {
        CanadianAddress cadd = new CanadianAddress();
        cadd.setId("123");
        cadd.setStreet("1 A Street");
        cadd.setPostalCode("A1B 2C3");
        return cadd;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.typetests.CanadianAddressAsRootTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
