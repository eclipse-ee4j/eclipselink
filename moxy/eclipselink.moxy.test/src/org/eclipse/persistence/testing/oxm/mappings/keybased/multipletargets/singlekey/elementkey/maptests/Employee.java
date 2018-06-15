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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.maptests;

import java.util.Map;
import java.util.Iterator;

import org.eclipse.persistence.testing.oxm.mappings.keybased.Address;

public class Employee extends org.eclipse.persistence.testing.oxm.mappings.keybased.Employee {
    public Map addresses;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }

        Employee tgtEmp = ((Employee) obj);
        Map tgtAddresses = tgtEmp.addresses;

        if (this.addresses == null) {
            return tgtAddresses == null;
        }

        if (tgtAddresses == null || tgtAddresses.size() != addresses.size()) {
            return false;
        }

        for (Iterator addIt = this.addresses.values().iterator(); addIt.hasNext(); ) {
            Address address = (Address) addIt.next();
            if (!(tgtAddresses.containsValue(address))) {
                return false;
            }
        }
        return true;
    }
}
