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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute;

public class Employee {

  private Integer id;

  public Employee() {
    super();
  }

  public int getID() {
    return id.intValue();
  }

  public void setID(int newID) {
    id = new Integer(newID);
  }
    public Integer getIntegerId() {
        return id;
    }
    public void setIntegerId(Integer id) {
        this.id = id;
    }
  public boolean equals(Object object) {
    try {
      Employee employee = (Employee) object;
            if(getIntegerId() == null && employee.getIntegerId() == null) {
                return true;
            }
            else if(getIntegerId() == null || employee.getIntegerId() == null) {
                return false;
            }
            return this.getID() == employee.getID();
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    return "Employee: " + this.getIntegerId();
  }
}
