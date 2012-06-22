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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

public class Employee {
    public static final int DEFAULT_ID = 123;

    // Factory method
    public static Employee getInstance() {
        return new Employee(DEFAULT_ID, "Jane", "Doe");
    }

    private int id;

    //private Vector tasks;	// of type <String>
    private String firstName;
    private String lastName;
    private boolean isSetFirstName = false;

    public Employee() {
        super();
        //tasks = new Vector();
    }

    public Employee(int id) {
        super();
        //tasks = new Vector();
        //isSetTasks = true;
        this.id = id;
    }

    public Employee(int id, String aFirstName, String aLastName) {
        super();
        this.id = id;
        //setTasks(aVector);
        setFirstName(aFirstName);
        setLastName(aLastName);
    }

    // override default equals
    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employeeObject = (Employee)object;
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
        if ((getFirstName() != null) && (employeeObject.getFirstName() != null) &&//
                !getFirstName().equals(employeeObject.getFirstName())) {
            return false;
        }
        if ((getLastName() != null) && (employeeObject.getLastName() != null) &&//
                !getLastName().equals(employeeObject.getLastName())) {
            return false;
        }
        return true;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    //public Vector getTasks() {
    //	return tasks;
    //}
    public boolean isSetFirstName() {
        return isSetFirstName;
    }

    public void setFirstName(String firstName) {
        // no unset for now
        isSetFirstName = true;
        this.firstName = firstName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    //public void setTasks(Vector tasks) {
    //	this.tasks = tasks;
    //	isSetFirstName = true;
    //}
    public String toString() {
        return "Employee(" + getId() + "," +//firstName + "," +
               //tasks +  "," +
        firstName + "," + lastName + ")";
    }
}
