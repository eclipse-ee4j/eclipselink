/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.floattest;

public class Employee {

  private int id;
  private String firstName;
  private String lastName;
  private Float salary;

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

public Float getSalary() {
    return salary;
}

public void setSalary(Float salary) {
    this.salary = salary;
}




}
