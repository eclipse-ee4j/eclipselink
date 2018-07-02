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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.persistence.testing.oxm.mappings.keybased.Address;

public class Employee extends org.eclipse.persistence.testing.oxm.mappings.keybased.Employee {
    public ArrayList<Address> addresses;

    public Employee() {
        addresses = new ArrayList<Address>();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee tgtEmp = ((Employee) obj);
        ArrayList tgtAddresses = tgtEmp.addresses;
        if (this.addresses == null) {
            return tgtAddresses == null;
        }

        if (tgtAddresses == null || tgtAddresses.size() != addresses.size()) {
            return false;
        }

        for (Iterator addIt = this.addresses.iterator(); addIt.hasNext(); ) {
            Address address = (Address) addIt.next();
            if (!(tgtAddresses.contains(address))) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        String addressString="";
        for (Address address : addresses) {
            addressString = address.toString();
        }
        return "Employee: id="+ id + ", name=" + name + ", addresses=[" + addressString+"]";
    }

}
