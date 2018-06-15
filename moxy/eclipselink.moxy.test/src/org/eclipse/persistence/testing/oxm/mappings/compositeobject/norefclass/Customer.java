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
//     bdoughan - March 11/2010 - 2.0.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.norefclass;

public class Customer {

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean equals(Object object) {
        if(null == object) {
            return false;
        }
        try {
            Customer testCustomer = (Customer) object;
            if(null == address) {
                return null == testCustomer.getAddress();
            } else {
                return address.equals(testCustomer.getAddress());
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

}
