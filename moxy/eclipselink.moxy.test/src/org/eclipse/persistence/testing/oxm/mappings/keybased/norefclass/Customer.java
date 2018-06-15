/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - April 9/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private Object address;
    private List<Object> phoneNumbers;

    public Customer() {
        phoneNumbers = new ArrayList<Object>();
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public List<Object> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<Object> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Customer testCustomer = (Customer) object;
            if(null == address) {
                if(null != testCustomer.getAddress()) {
                    return false;
                }
            } else {
                if(!address.equals(testCustomer.getAddress())) {
                    return false;
                }
            }
            if(null == phoneNumbers) {
                return null == testCustomer.getPhoneNumbers();
            }
            List<Object> testCustomerPhoneNumbers = testCustomer.getPhoneNumbers();
            if(phoneNumbers.size() != testCustomerPhoneNumbers.size()) {
                return false;
            }
            for(int x=0; x<phoneNumbers.size(); x++) {
                if(!phoneNumbers.get(x).equals(testCustomerPhoneNumbers.get(x))) {
                    return false;
                }
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

}
