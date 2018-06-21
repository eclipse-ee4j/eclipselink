/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
      if (null != this.getIdentifier() && null != employee.getIdentifier() && this.getIdentifier() instanceof java.util.Calendar && employee.getIdentifier() instanceof java.util.Calendar) {
          if (((java.util.Calendar)this.getIdentifier()).getTimeInMillis() != ((java.util.Calendar)employee.getIdentifier()).getTimeInMillis()) {
              return false;
          }
      } else
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
