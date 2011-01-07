/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     bdoughan - 2010/06/29 - initial implementation
 ******************************************************************************/
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