/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable;

import java.util.Vector;

public class Employee {
    public static final int DEFAULT_ID = 123;
    
    // Factory method
    public static Employee getInstance() {
        return new Employee(DEFAULT_ID, new Vector(), "Doe");
    }
    
    private int id;
    private Vector tasks;	// of type <String>
    //private String firstName;
    private String lastName;

    //public String getFirstName() {
    //    return firstName;
    //}

    private boolean isSetTasks = false;

    //public void setFirstName(String firstName) {
        // no unset for now
    //    isSetFirstName = true;
    //    this.firstName = firstName;
    //}

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

    public Employee(int id, Vector aVector, String lastName) {
        super();
        this.id = id;
        setTasks(aVector);
        //this.firstName = firstName;
        this.lastName = lastName;
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
        //if ((employeeObject.getFirstName() == null) && (getFirstName() != null)) {
        //    return false;
        //}
        if ((employeeObject.getLastName() != null) && (getLastName() == null)) {
            return false;
        }
        //if ((employeeObject.getFirstName() != null) && (getFirstName() == null)) {
        //    return false;
        //}
        //if ((getFirstName() != null) && (employeeObject.getFirstName() != null) && !getFirstName().equals(employeeObject.getFirstName())) {
        //    return false;
        //}
        if ((getLastName() != null) && (employeeObject.getLastName() != null) && // 
        		!getLastName().equals(employeeObject.getLastName())) {
            return false;
        }
        if ((employeeObject.getTasks() == null) && (getTasks() != null)) {
            return false;
        }
        if ((employeeObject.getTasks() != null) && (getTasks() == null)) {
            return false;
        }
        if ((employeeObject.getTasks() != null) && (getTasks() != null) && //
        		(!(employeeObject.getTasks() instanceof Vector) || !(getTasks() instanceof Vector))) {
            return false;
        }

        if((this.getId() == employeeObject.getId()) && //
        	      ((this.getTasks()==null && employeeObject.getTasks()==null) || //
        	    		  (this.getTasks().isEmpty() && employeeObject.getTasks().isEmpty()) || //
        	    		  (this.getTasks().containsAll(employeeObject.getTasks()))) && //
        	    		  (this.getTasks().size() == employeeObject.getTasks().size())) {
        	return true;
        }
        
        // todo handle :
        /*Expected:
        	Employee(123,[null, write code, null],Doe)
        	Actual:
        	Employee(123,[write code],Doe)
        */
        return false;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Vector getTasks() {
		return tasks;
	}

    public boolean isSetTasks() {
        return isSetTasks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

	public void setTasks(Vector tasks) {
		this.tasks = tasks;
		isSetTasks = true;
	}

	public String toString() {
        return "Employee(" + getId() + "," + //firstName + "," +
        	tasks +  "," +
        	lastName + ")";
    }
}
