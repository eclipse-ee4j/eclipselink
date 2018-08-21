/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
public class ContainerMethodAccessorTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/containeraccessor/containeraccessor.xml";

    public ContainerMethodAccessorTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmployeeProject(true));
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
