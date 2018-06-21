/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - April 9/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

import java.math.BigInteger;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public abstract class AbstractTestCases extends XMLMappingTestCases {

    public AbstractTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();

        Customer customer1 = new Customer();
        root.getCustomers().add(customer1);

        Customer customer2 = new Customer();
        root.getCustomers().add(customer2);

        Address address1 = new Address();
        address1.setId(1);
        root.getAddresses().add(address1);

        Address address2 = new Address();
        address2.setId(2);
        root.getAddresses().add(address2);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setId("A");
        root.getPhoneNumbers().add(phoneNumber1);

        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setId("B");
        root.getPhoneNumbers().add(phoneNumber2);

        PhoneNumber phoneNumber3 = new PhoneNumber();
        phoneNumber3.setId("C");
        root.getPhoneNumbers().add(phoneNumber3);

        customer1.setAddress(address1);
        customer1.getPhoneNumbers().add(phoneNumber1);
        customer1.getPhoneNumbers().add(phoneNumber2);

        customer2.setAddress(address2);
        customer2.getPhoneNumbers().add(phoneNumber2);
        customer2.getPhoneNumbers().add(phoneNumber3);

        return root;
    }

}
