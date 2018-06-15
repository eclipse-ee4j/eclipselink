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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nonstringkeytype;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype.Address;

public class Employee extends org.eclipse.persistence.testing.oxm.mappings.keybased.Employee {
    public ArrayList addresses;

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

}
