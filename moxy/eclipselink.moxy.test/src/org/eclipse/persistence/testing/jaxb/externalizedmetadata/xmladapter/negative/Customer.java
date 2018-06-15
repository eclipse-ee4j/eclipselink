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
//     bdoughan - 2010/06/29 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative;

public class Customer {

    private int id;
    private Customer customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object object) {
        if(null == object || Customer.class != object.getClass()) {
            return false;
        }
        Customer testCustomer = (Customer) object;
        if(null == customer) {
            if(null != testCustomer.getCustomer()) {
                return false;
            }
        } else {
            if(!customer.equals(testCustomer.getCustomer())) {
                return false;
            }
        }
        return testCustomer.getId() == id;
    }

}
