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