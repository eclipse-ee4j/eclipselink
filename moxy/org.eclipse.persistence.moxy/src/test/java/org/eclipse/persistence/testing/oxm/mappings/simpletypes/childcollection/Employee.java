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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection;

// JDK imports
import java.util.Vector;

public class Employee  {
    private String name;
    private Vector phones;

    public Employee() {
        phones = new Vector();
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public Vector getPhones() {
        return phones;
    }

    public void setPhones(Vector newPhones) {
        phones = newPhones;
    }

    public String toString() {
        return "Employee(name=" + name + ", numbers=" + phones.elementAt(0) + ", " + phones.elementAt(1)+ ", " + phones.elementAt(2) + ")";
    }

    public boolean equals(Object object) {
        if(!(object instanceof Employee)) {
            return false;
        }

        Employee employeeObject = (Employee)object;

        if (!(employeeObject.getName().equals(this.getName()))) {
            return false;
        }

        Vector phoneNumbers = employeeObject.getPhones();

        if (!(((Phone)(phoneNumbers.elementAt(0))).equals(phones.elementAt(0)))) {
            return false;
        }

        if (!(((Phone)(phoneNumbers.elementAt(1))).equals(phones.elementAt(1)))) {
            return false;
        }

        if (!(((Phone)(phoneNumbers.elementAt(2))).equals(phones.elementAt(2)))) {
            return false;
        }

        return true;
    }
}
