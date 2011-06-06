/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.doubletest;

public class Employee {

  private int id;
  private String firstName;
  private String lastName;
  private Double salary;

  public Employee() {
    super();
  }

  public int getID() {
    return id;
  }

  public void setID(int newID) {
    id = newID;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String newLastName) {
    lastName = newLastName;
  }  

  public boolean equals(Object object) {
    try {
      Employee employee = (Employee) object;
      if(this.getID() != employee.getID()) {return false;}
	 
      if(!(this.getFirstName() == null && employee.getFirstName()==null)){
        if(!this.getFirstName().equals(employee.getFirstName())) {return false;}
      }
      if(!(this.getSalary() == null && employee.getSalary()==null)){
          if(!this.getSalary().equals(employee.getSalary())) {return false;}
        }
      if(!(this.getLastName() == null && employee.getLastName()==null)){
          if(!this.getLastName().equals(employee.getLastName())) {return false;}
        }
      return true;
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    return "Employee: " + this.getID() + " fname:" + this.getFirstName() + " lname:" + this.getLastName() + " salary:" + this.getSalary();
  }

public Double getSalary() {
	return salary;
}

public void setSalary(Double salary) {
	this.salary = salary;
} 




}
