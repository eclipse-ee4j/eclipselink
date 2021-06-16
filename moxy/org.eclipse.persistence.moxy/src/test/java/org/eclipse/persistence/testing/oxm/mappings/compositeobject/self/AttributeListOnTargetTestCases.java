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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AttributeListOnTargetTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/AttributeListOnTarget.xml";
    private final static String CONTROL_STREET = "123 A St.";
    private final static String PROVINCE_1 = "Nova_Scotia";
    private final static String PROVINCE_2 = "Ontario";

    public AttributeListOnTargetTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new AttributeListOnTargetTestProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();

        Address address = new Address();
        address.street = CONTROL_STREET;
        employee.address = address;
        address.provinces.add(PROVINCE_1);
        address.provinces.add(PROVINCE_2);

        return employee;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeListOnTargetTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
