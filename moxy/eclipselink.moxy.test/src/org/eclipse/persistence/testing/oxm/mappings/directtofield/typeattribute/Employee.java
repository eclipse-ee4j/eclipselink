/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

public class Employee {

  private Object identifier;
  private String firstName;
  private String lastName;

  public Employee() {
    super();
  }

  public Object getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Object newIdentifier) {
    identifier= newIdentifier;
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
      if(!this.getIdentifier().equals(employee.getIdentifier())) {return false;}
      if(!this.getFirstName().equals(employee.getFirstName())) {return false;}
      if(!this.getLastName().equals(employee.getLastName())) {return false;}
      return true;
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    return "Employee-->Identifier:" + this.getIdentifier().getClass().getName() + " " + this.getIdentifier().toString() + " fname:" + this.getFirstName() + " lname:" + this.getLastName();
  } 
}
