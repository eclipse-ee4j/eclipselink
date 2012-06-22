/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
 ******************************************************************************/
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