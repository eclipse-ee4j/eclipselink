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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist;

import java.util.ArrayList;

public class Employee {
    int idSetCounter;
    int responsibilitiesSetCounter;
    private int id;
    private ArrayList responsibilities;

    public Employee() {
        super();
    }

    public int getID() {
        return id;
    }

    public void setID(int newId) {
        id = newId;
        idSetCounter++;
    }

    public ArrayList getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(ArrayList newResponsibilities) {
        responsibilities = newResponsibilities;
        responsibilitiesSetCounter++;
    }

    public String toString() {
        StringBuilder returnString = new StringBuilder("Employee: " + this.getID() + " ");
        if (getResponsibilities() != null) {
            returnString.append("Responsiblities: ");
            for (int i = 0; i < getResponsibilities().size(); i++) {
                Object next = getResponsibilities().get(i);
                returnString.append(next.toString()).append(" ");
            }
        }

        return returnString.toString();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee employeeObject)) {
            return false;
        }
        if ((this.getResponsibilities() == null) && (employeeObject.getResponsibilities() != null)) {
            return false;
        }
        if ((employeeObject.getResponsibilities() == null) && (this.getResponsibilities() != null)) {
            return false;
        }

        return (this.getID() == employeeObject.getID()) && (((this.getResponsibilities() == null) && (employeeObject.getResponsibilities() == null)) || (this.getResponsibilities().isEmpty() && employeeObject.getResponsibilities().isEmpty()) || (this.getResponsibilities().containsAll(employeeObject.getResponsibilities())));
    }

    public void setIdSetCounter(int idSetCounter) {
        this.idSetCounter = idSetCounter;
    }

    public int getIdSetCounter() {
        return idSetCounter;
    }

    public void setResponsibilitiesSetCounter(int responsibilitiesSetCounter) {
        this.responsibilitiesSetCounter = responsibilitiesSetCounter;
    }

    public int getResponsibilitiesSetCounter() {
        return responsibilitiesSetCounter;
    }
}
