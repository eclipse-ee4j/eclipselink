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
//     rbarkhouse - 2009-10-02 16:25:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directcollection.reuse;

import java.util.LinkedList;
import java.util.List;

public class Employee {

    private int id;
    private List responsibilities = new LinkedList();

    public Employee() {
        super();
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
    }

    public List getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List newResponsibilities) {
        responsibilities = newResponsibilities;
    }

    public String toString() {
        StringBuilder returnString = new StringBuilder("Employee: " + this.getID() + " ");
        if (getResponsibilities() != null) {
            returnString.append("Responsiblities (").append(getResponsibilities().getClass()).append("): ");
            for (int i = 0; i < getResponsibilities().size(); i++) {
                Object next = getResponsibilities().get(i);
                returnString.append(next.toString()).append(" ");
            }
        }

        return returnString.toString();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee) object;
        if ((this.getResponsibilities() == null)
                && (employeeObject.getResponsibilities() != null)) {
            return false;
        }
        if ((employeeObject.getResponsibilities() == null)
                && (this.getResponsibilities() != null)) {
            return false;
        }

        if (employeeObject.getResponsibilities().getClass() != this.getResponsibilities().getClass()) {
            return false;
        }

        return (this.getID() == employeeObject.getID())
                && (((this.getResponsibilities() == null) && (employeeObject
                .getResponsibilities() == null))
                || (this.getResponsibilities().isEmpty() && employeeObject
                .getResponsibilities().isEmpty()) || (this
                .getResponsibilities().containsAll(employeeObject
                        .getResponsibilities())));
    }

}
