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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode;

import java.util.ArrayList;

public class Employee  {

  private int id;
    private ArrayList responsibilities;

  public Employee() {
    super();
  }

  public int getID() {
    return id;
  }

  public void setID(int newId) {
    id = newId;
  }

  public ArrayList getResponsibilities() {
    return responsibilities;
  }

  public void setResponsibilities(ArrayList newResponsibilities) {
    responsibilities = newResponsibilities;
  }

    public String toString()
  {
        String returnString =  "Employee: " + this.getID() + " ";
        if(getResponsibilities() != null)
        {
            returnString += "Responsiblities: ";
            for(int i=0; i<getResponsibilities().size(); i++)
            {
                Object next = getResponsibilities().get(i);
                returnString += next.toString() + " ";
            }
        }

        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;
    if(this.getResponsibilities()==null && employeeObject.getResponsibilities()!=null)
    {
      return false;
    }
    if(employeeObject.getResponsibilities()==null && this.getResponsibilities()!=null)
    {
      return false;
    }

    if((this.getID() == employeeObject.getID()) &&
      ((this.getResponsibilities()==null && employeeObject.getResponsibilities()==null) ||(this.getResponsibilities().isEmpty() && employeeObject.getResponsibilities().isEmpty()) || (this.getResponsibilities().containsAll(employeeObject.getResponsibilities())))
            )
          return true;

    return false;
  }
}
