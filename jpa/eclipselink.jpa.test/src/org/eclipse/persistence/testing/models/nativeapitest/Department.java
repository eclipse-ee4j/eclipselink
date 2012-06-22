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
package org.eclipse.persistence.testing.models.nativeapitest;

import java.io.Serializable;

import java.util.Collection;
import java.util.Vector;

/**
 * <p><b>Purpose</b>: Represents the department of an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
public class Department implements Serializable {  
    private Integer id;
    private String name;
    private Collection<Employee> employees;
    private Collection<Employee> managers;
    public Department() {
        this("");
    }

    public Department(String name) {
        this.name = name;
        this.managers = new Vector();
    }
    
    public void addManager(Employee employee) {
        if (employee != null && managers != null && !managers.contains(employee)) { 
            this.managers.add(employee); 
        }
    }    
	
	public Collection<Employee> getEmployees() { 
        return employees; 
    }
    
    public Integer getId() { 
        return id; 
    }
    
    //To test default 1-M mapping    
    public Collection<Employee> getManagers() {
        return managers;
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setEmployees(Collection<Employee> employees) {
		this.employees = employees;
	}
    
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setManagers(Collection<Employee> managers) {
        this.managers = managers;
    }
    
	public void setName(String name) { 
        this.name = name; 
    }
}
