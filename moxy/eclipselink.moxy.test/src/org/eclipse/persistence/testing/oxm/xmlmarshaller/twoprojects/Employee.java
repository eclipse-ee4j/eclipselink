/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

import java.util.Vector;

public class Employee {
    private Address address;
    private Vector addresses;

    public Employee() {
        super();
        addresses = new Vector();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Vector getAddresses() {
        return addresses;
    }

    public void setAddresses(Vector addresses) {
        this.addresses = addresses;
    }

    public boolean equals(Object object) {
        try {
            if (null == object) {
                return false;
            }
            Employee employee = (Employee)object;

            if (null == address) {
                if (null != employee.getAddress()) {
                    return false;
                }
            } else {
                if (!address.equals(employee.getAddress())) {
                    return false;
                }
            }

            if (null == addresses) {
                if (null != employee.getAddresses()) {
                    return false;
                }
            } else {
                if (null == employee.getAddresses()) {
                    return false;
                }
                if (addresses.size() != employee.getAddresses().size()) {
                    return false;
                }
                int addressesSize = addresses.size();
                for (int x = 0; x < addressesSize; x++) {
                    if (!addresses.get(x).equals(employee.getAddresses().get(x))) {
                        return false;
                    }
                }
            }

            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Employee(address=";
        string += address;
        string += " addresses=";
        string += addresses;
        string += ")";
        return string;
    }
}
