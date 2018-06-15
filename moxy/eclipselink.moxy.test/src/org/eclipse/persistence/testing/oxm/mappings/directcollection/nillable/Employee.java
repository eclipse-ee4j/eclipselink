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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import java.util.Vector;

public class Employee {

    public static final int DEFAULT_ID = 123;

    private int id;
    private Vector tasks;
    private String firstName;
    private String lastName;

    private boolean isSetTasks = false;

    // ==============================================

    public Employee() {
        super();
        setTasks(new Vector());
        isSetTasks = true;
    }

    public Employee(int id) {
        super();
        this.id = id;
        setTasks(new Vector());
    }

    public Employee(int id, Vector aVector, String lastName) {
        super();
        this.id = id;
        setTasks(aVector);
        this.lastName = lastName;
    }

    // Factory method
    public static Employee getInstance() {
        return new Employee(DEFAULT_ID, new Vector(), "Doe");
    }

    // ==============================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Vector getTasks() {
        return tasks;
    }

    public void setTasks(Vector tasks) {
        this.tasks = tasks;
        isSetTasks = (tasks == null) ? false : true;
    }

    public boolean isSetTasks() {
        return isSetTasks;
    }

    // override default equals
    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee) object;
        if (getId() != employeeObject.getId()) {
            return false;
        }
        if ((employeeObject.getLastName() == null) && (getLastName() != null)) {
            return false;
        }
        if ((employeeObject.getFirstName() == null) && (getFirstName() != null)) {
            return false;
        }
        if ((employeeObject.getLastName() != null) && (getLastName() == null)) {
            return false;
        }
        if ((employeeObject.getFirstName() != null) && (getFirstName() == null)) {
            return false;
        }
        if ((getFirstName() != null) && (employeeObject.getFirstName() != null) && !getFirstName().equals(employeeObject.getFirstName())) {
            return false;
        }
        if ((getLastName() != null) && (employeeObject.getLastName() != null) && !getLastName().equals(employeeObject.getLastName())) {
            return false;
        }
        if ((employeeObject.getTasks() == null) && (getTasks() != null)) {
            return false;
        }
        if ((employeeObject.getTasks() != null) && (getTasks() == null)) {
            return false;
        }
        if ((employeeObject.getTasks() != null) && (getTasks() != null) && (!(employeeObject.getTasks() instanceof Vector) || !(getTasks() instanceof Vector))) {
            return false;
        }

        if((this.getId() == employeeObject.getId()) && //
                ((this.getTasks()==null && employeeObject.getTasks()==null) || //
                        (this.getTasks().isEmpty() && employeeObject.getTasks().isEmpty()) || //
                        (this.getTasks().containsAll(employeeObject.getTasks()))) && //
                        (this.getTasks().size() == employeeObject.getTasks().size())) {
            return true;
        }

        return false;
    }

    // ==============================================

    public String toString() {
        return "Employee(" + getId() + "," + firstName + "," + tasks +  "," + lastName + ")";
    }

}
