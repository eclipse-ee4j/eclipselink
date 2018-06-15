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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childelement;

// JDK imports
import java.util.Vector;


public class Employee  {
    private String name;
    private Phone phone;

    public Employee() {
        phone = new Phone();
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone newPhone) {
        phone = newPhone;
    }

    public String toString() {
        return "Employee(name=" + name + ", number=" + phone.getNumber() + ")";
    }

    public boolean equals(Object object) {
        if(!(object instanceof Employee)) {
            return false;
        }

        Employee employeeObject = (Employee)object;

        if (!(employeeObject.getName().equals(this.getName()))) {
            return false;
        }

        if (!(employeeObject.getPhone().equals(this.getPhone()))) {
            return false;
        }

        return true;
    }
}
