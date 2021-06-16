/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

import java.util.LinkedList;
import java.util.List;

public class Employee {

    public int id;
    public String name;
    public List<Address> addresses = new LinkedList<Address>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee) {
            Employee empObj = (Employee) obj;
            if (this.id != empObj.id) {
                return false;
            }
            if (!(this.name.equals(empObj.name))) {
                return false;
            }
            if (!(this.addresses.equals(empObj.addresses))) {
                return false;
            }
            if (!(this.addresses.getClass().equals(empObj.addresses.getClass()))) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String toString = "Employee[" + id + ", " + name + "], addresses[" + addresses.getClass() + "]:\n";

        for (int i = 0; i < addresses.size(); i++) {
            Address a = addresses.get(i);
            toString += "\tAddress[" +  a.id + "] " + a.info + "\n";
        }

        return toString;
    }

}
