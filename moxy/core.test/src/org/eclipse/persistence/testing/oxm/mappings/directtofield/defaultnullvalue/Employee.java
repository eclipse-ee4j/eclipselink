/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue;

public class Employee {
    private int id;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean equals(Object object) {
        try {
            Employee employee = (Employee)object;
            return id == employee.id;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Employee(id=";
        string += id;
        string += ")";
        return string;
    }
}