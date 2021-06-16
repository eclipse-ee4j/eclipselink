/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
public class ContainerInstanceVariableAccessorTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/containeraccessor/containeraccessor.xml";

    public ContainerInstanceVariableAccessorTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmployeeProject(false));
    }

    @Override
    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.id = 10;
        emp.firstName = "Jane";
        emp.lastName = "Doe";

        emp.address = new Address();
        emp.address.street = "123 Fake Street";
        emp.address.city = "Ottawa";
        emp.address.state = "Ontario";
        emp.address.country = "Canada";
        emp.address.owningEmployee = emp;

        emp.phoneNumbers = new ArrayList<PhoneNumber>();

        PhoneNumber num1 = new PhoneNumber();
        num1.number = "123-4567";
        num1.owningEmployee = emp;
        emp.phoneNumbers.add(num1);

        PhoneNumber num2 = new PhoneNumber();
        num2.number = "234-5678";
        num2.owningEmployee = emp;
        emp.phoneNumbers.add(num2);

        return emp;
    }

}
