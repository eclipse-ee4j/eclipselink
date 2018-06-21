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
//     rbarkhouse - 2009-10-09 14:17:31 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.reuse;

import java.util.Stack;
import java.util.Vector;

public class Employee {

    private int id;
    private Vector emailAddresses = new Stack();

    public Employee() {
        super();
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public Vector getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Vector newEmailAddresses) {
        emailAddresses = newEmailAddresses;
    }

    public String toString() {
        String output = "Employee: " + this.getID();

        for (int i = 0; i < getEmailAddresses().size(); i++) {
            output += getEmailAddresses().elementAt(i);
        }

        return output;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }

        Employee employeeObject = (Employee) object;

        if (!(this.getEmailAddresses().getClass().equals(employeeObject.getEmailAddresses().getClass()))) {
            return false;
        }

        if ((this.getID() == employeeObject.getID()) && (this.getEmailAddresses().containsAll(employeeObject.getEmailAddresses()))) {
            return true;
        }

        return false;
    }

}
