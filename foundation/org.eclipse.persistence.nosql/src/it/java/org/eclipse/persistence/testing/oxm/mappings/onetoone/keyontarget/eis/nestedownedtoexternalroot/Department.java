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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.eis.nestedownedtoexternalroot;

import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.Employee;

public class Department
{
  private Vector employees;
  private String deptName;

  public Department()
  {
    employees = new Vector();
  }

  public void addEmployee(Employee newEmployee)
  {
    employees.add(newEmployee);
  }

  public Vector getEmployees()
  {
    return employees;
  }

  public void setEmployees(Vector newEmployees)
  {
    employees = newEmployees;
  }

  public String getDeptName()
  {
    return deptName;
  }

  public void setDeptName(String newDeptName)
  {
    deptName = newDeptName;
  }

  public String toString(){
    String result = "DEPARTMENT: "+ this.getDeptName();

    result += "EMPLOYEES: ";
    for(int i=0; i<getEmployees().size();i++)
    {
      result+= getEmployees().elementAt(i);
    }

    return result;
  }

   public boolean equals(Object object)
   {
     if(!(object instanceof Department))
      return false;

     Department deptObject = (Department)object;
     if((this.getDeptName().equalsIgnoreCase(deptObject.getDeptName())) &&
        (((this.getEmployees() == null) && (deptObject.getEmployees() == null))||
          (this.getEmployees().containsAll(deptObject.getEmployees()))) )
          return true;

      return false;
   }
}
