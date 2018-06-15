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
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.List;

public class Employee {
    private List addresses;

    public Employee() {
    }

    public void setAddresses(List theAddresses) {
        this.addresses = theAddresses;
    }

    public List getAddresses() {
        return addresses;
    }

    public boolean equals(Object theEmployee) {
        if (theEmployee instanceof Employee) {
            return addresses.equals(((Employee)theEmployee).getAddresses());
        }
        return false;
    }

    public String toString() {
        return "Employee: " + addresses.toString();
    }
}
