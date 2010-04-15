/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - April 9/2010 - 2.1 - Initial implementation
******************************************************************************/
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