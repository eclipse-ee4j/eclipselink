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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.indirection.arraylist;

import java.util.ArrayList;

import org.eclipse.persistence.indirection.*;

import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.Project;

public class Employee  {

  private String firstName;
    private ValueHolderInterface projects;

  public Employee() {
    super();
    this.projects = new ValueHolder(new ArrayList());
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }

  public ArrayList getProjects() {
     return (ArrayList) projects.getValue();
  }

  public void setProjects(ArrayList newProjects) {
        this.projects.setValue(newProjects);
  }

  public void addProject(Project project) {
    getProjects().add(project);
  }

  public String toString()
  {
        String returnString =  "Employee: " + this.getFirstName() + " ";
        if(getProjects() != null)
        {
            returnString += "Projects: ";
            returnString += getProjects().toString() + " ";
        }

        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;

    if(this.getProjects()==null && employeeObject.getProjects()!=null)
    {
      return false;
    }

    if((this.getFirstName().equals(employeeObject.getFirstName())) &&
      ((this.getProjects()==null && employeeObject.getProjects()==null) || (this.getProjects().equals(employeeObject.getProjects()))))
          return true;

    return false;
  }
}
