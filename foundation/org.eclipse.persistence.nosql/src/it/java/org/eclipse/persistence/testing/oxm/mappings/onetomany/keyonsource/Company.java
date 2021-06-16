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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource;

import java.util.Vector;

public class Company
{
  private Vector departments;
  private String name;

  public Company()
  {
    departments = new Vector();
  }

  public void addDepartment(Department newDepartment)
  {
    departments.add(newDepartment);
  }

  public Vector getDepartments()
  {
    return departments;
  }

  public void setDepartments(Vector newDepartments)
  {
    departments = newDepartments;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String newName)
  {
    name = newName;
  }

  public String toString(){
    String result = "COMPANY: "+ this.getName();

    result += " DEPARTMENTS: ";
    for(int i=0; i<getDepartments().size();i++)
    {
      result+= getDepartments().elementAt(i);
    }

    return result;
  }

   public boolean equals(Object object)
   {
     if(!(object instanceof Company))
      return false;

     Company companyObject = (Company)object;
     if((this.getName().equalsIgnoreCase(companyObject.getName())) &&
        (((this.getDepartments() == null) && (companyObject.getDepartments() == null))||
          (this.getDepartments().containsAll(companyObject.getDepartments()))) )
          return true;

      return false;
   }

}
