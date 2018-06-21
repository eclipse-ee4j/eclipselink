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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified.a;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.unqualified.b.Address;

@XmlRootElement
public class Customer {

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Customer customer = (Customer) obj;
        if(null == address) {
            return null == customer.getAddress();
        }
        return address.equals(customer.getAddress());
    }

}
