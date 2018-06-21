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
//     Praba Vijayaratnam - 2.3 - inital implementation
package org.eclipse.persistence.testing.jaxrs.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class Customers implements Serializable {

    private List<Customer> customer;

    public List<Customer> getCustomer() {
        return this.customer;
    }

    public void setCustomer(List<Customer> customerList) {
        this.customer = customerList;
    }

    public boolean equals(Object object) {
        Customers testCustomers = (Customers) object;
        if (testCustomers.getCustomer().size() != 2) {
            return false;
        }

        return ((Customer) (testCustomers.getCustomer().get(0)))
                .equals((Customer) (testCustomers.getCustomer().get(0)))
                && ((Customer) (testCustomers.getCustomer().get(1)))
                        .equals((Customer) (testCustomers.getCustomer().get(1)));

    }

}
