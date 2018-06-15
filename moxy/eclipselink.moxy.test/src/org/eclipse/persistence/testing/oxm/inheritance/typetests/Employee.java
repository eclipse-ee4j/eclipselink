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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class Employee {
    private Address address;

    public Employee() {
    }

    public void setAddress(Address theAddress) {
        this.address = theAddress;
    }

    public Address getAddress() {
        return address;
    }

    public boolean equals(Object theEmployee) {
        if (theEmployee instanceof Employee) {
            return address.equals(((Employee)theEmployee).getAddress());
        }
        return false;
    }

    public String toString() {
        return "Employee: " + address.toString();
    }
}
