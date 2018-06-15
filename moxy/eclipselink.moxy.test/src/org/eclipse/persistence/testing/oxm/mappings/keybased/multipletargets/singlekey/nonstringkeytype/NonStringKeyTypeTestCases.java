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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nonstringkeytype;

import java.util.ArrayList;
import org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype.Address;

public class NonStringKeyTypeTestCases extends KeyBasedMappingTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/multipletargets/singlekey/attributekey/instance.xml";

    public NonStringKeyTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/keybased/multipletargets/singlekey/attributekey/instance.xml");
        setProject(new NonStringKeyTypeProject());
    }

    public Object getControlObject() {
        ArrayList addresses = new ArrayList();
        Address address = new Address();
        address.id = 199;
        address.street = "Some Other St.";
        address.city = "Anyothertown";
        address.country = "Canada";
        address.zip = "X0X0X0";
        addresses.add(address);
        address = new Address();
        address.id = 99;
        address.street = "Some St.";
        address.city = "Anytown";
        address.country = "Canada";
        address.zip = "X0X0X0";
        addresses.add(address);
        address = new Address();
        address.id = 11199;
        address.street = "Another St.";
        address.city = "Anytown";
        address.country = "Canada";
        address.zip = "Y0Y0Y0";
        addresses.add(address);
        Employee employee = new Employee();
        employee.id = "222";
        employee.name = "Joe Smith";
        employee.addresses = addresses;
        Root root = new Root();
        root.employee = employee;
        return root;
    }

    public Object getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();
        ArrayList empAddresses = new ArrayList();
        Address address = new Address();
        address.id = 199;
        address.street = "Some Other St.";
        address.city = "Anyothertown";
        address.country = "Canada";
        address.zip = "X0X0X0";
        empAddresses.add(address);
        rootAddresses.add(address);
        address = new Address();
        address.id = 99;
        address.street = "Some St.";
        address.city = "Anytown";
        address.country = "Canada";
        address.zip = "X0X0X0";
        empAddresses.add(address);
        rootAddresses.add(address);
        address = new Address();
        address.id = 11199;
        address.street = "Another St.";
        address.city = "Anytown";
        address.country = "Canada";
        address.zip = "Y0Y0Y0";
        empAddresses.add(address);
        rootAddresses.add(address);
        address = new Address();
        address.id = 1199;
        address.street = "Some St.";
        address.city = "Sometown";
        address.country = "Canada";
        address.zip = "X0X0X0";
        rootAddresses.add(address);
        Employee employee = new Employee();
        employee.id = "222";
        employee.name = "Joe Smith";
        employee.addresses = empAddresses;
        Root root = new Root();
        root.employee = employee;
        root.addresses = rootAddresses;
        return root;
    }
}
