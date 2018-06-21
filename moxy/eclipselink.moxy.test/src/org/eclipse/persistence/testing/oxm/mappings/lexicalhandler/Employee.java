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
//     bdoughan - December 4/2009 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.lexicalhandler;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private String firstName;
    private Address address;
    private List<PhoneNumber> phoneNumbers;
    private String lastName;

    public Employee() {
        phoneNumbers = new ArrayList<PhoneNumber>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean equals(Object object) {
        try {
            Employee test = (Employee) object;
            if(!equals(firstName, test.getFirstName())) {
                return false;
            }
            if(!equals(address, test.getAddress())) {
                return false;
            }
            if(!equals(phoneNumbers, test.getPhoneNumbers())) {
                return false;
            }
            if(!equals(lastName, test.getLastName())) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }

}
