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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement;

public class Employee  {
    private String name;
    private String lastName;
    private int age;
    private boolean married;

    public Employee() {}

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int newAge) {
        age = newAge;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String newLastName) {
        lastName = newLastName;
    }

    public String toString() {
        return "Employee(name=" + name + " " + lastName + " " + age+ " " + married +")";
    }

    public boolean equals(Object object) {
        if(!(object instanceof Employee)) {
            return false;
        }

        Employee emp = ((Employee)object);
        if(emp.isMarried() != this.isMarried())
        {
            return false;
        }
        if(((emp.getName()==null && this.getName()==null)|| (emp.getName().equals(this.getName()))) &&
            ((emp.getLastName()==null && this.getLastName()==null)  || (emp.getLastName().equals(this.getLastName())) )&&
                (emp.getAge() == this.getAge())){
            return true;
        }
        return false;
    }


    public void setMarried(boolean married)
    {
        this.married = married;
    }


    public boolean isMarried()
    {
        return married;
    }
}
