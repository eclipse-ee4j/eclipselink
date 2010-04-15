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

public class Root {

    private List<Customer> customers;
    private List<Address> addresses;
    private List<PhoneNumber> phoneNumbers;

    public Root() {
        customers = new ArrayList<Customer>();
        addresses = new ArrayList<Address>();
        phoneNumbers = new ArrayList<PhoneNumber>();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> address) {
        this.addresses = address;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Root testRoot = (Root) object;
            if(!equals(customers, testRoot.getCustomers())) {
                return false;
            }
            if(!equals(addresses, testRoot.getAddresses())) {
                return false;
            }
            if(!equals(phoneNumbers, testRoot.getPhoneNumbers())) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

    private boolean equals(List controlList, List testList) {
        if(null == controlList) {
            return null == testList;
        }
        if(controlList.size() != testList.size()) {
            return false;
        }
        for(int x=0; x<controlList.size(); x++) {
            if(!controlList.get(x).equals(testList.get(x))) {
                return false;
            }
        }
        return true;
    }
}