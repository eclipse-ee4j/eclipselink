/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
