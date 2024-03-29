/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

import java.util.LinkedList;
import java.util.List;

public class Employee {

    public int id;
    public String name;
    public List<Address> addresses = new LinkedList<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee empObj) {
            if (this.id != empObj.id) {
                return false;
            }
            if (!(this.name.equals(empObj.name))) {
                return false;
            }
            if (!(this.addresses.equals(empObj.addresses))) {
                return false;
            }
            return this.addresses.getClass().equals(empObj.addresses.getClass());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder("Employee[" + id + ", " + name + "], addresses[" + addresses.getClass() + "]:\n");

        for (Address a : addresses) {
            toString.append("\tAddress[").append(a.id).append("] ").append(a.info).append("\n");
        }

        return toString.toString();
    }

}
