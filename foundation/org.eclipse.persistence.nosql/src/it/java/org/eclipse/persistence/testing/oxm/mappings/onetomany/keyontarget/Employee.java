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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget;

import java.util.Vector;

public class Employee  {

  private String firstName;
  //private Project project;
  private Vector projects;

  public Employee() {
    super();
    //project = new Project();
    projects = new Vector();
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }

  public Vector getProjects() {
    return projects;
  }

  public void setProjects(Vector newProjects) {
    for(int i=0; i<newProjects.size(); i++)
    {
      addProject((Project)newProjects.elementAt(i));
    }
    //newProjects.setLeader(this);
    //projects = newProjects;
  }

  public void addProject(Project newProject)
  {
    newProject.setLeader(this);
    projects.add(newProject);
  }

  public String toString()
  {
        String returnString =  "Employee: " + this.getFirstName() + " ";

        if(getProjects() != null)
        {
      returnString += "Projects: ";
      for(int i=0; i<projects.size(); i++)
      {
        returnString += ((Project)projects.elementAt(i)).toString() + " ";
      }
        }

        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;
/*
    if(this.getProject()==null && employeeObject.getProject()!=null)
    {
      return false;
    }

    if((this.getFirstName().equals(employeeObject.getFirstName())) &&
      ((this.getProject()==null && employeeObject.getProject()==null) || (this.getProject().equals(employeeObject.getProject()))))
          return true;
*/
    if(this.getFirstName().equals(employeeObject.getFirstName())){
      return true;
    }
    return false;
  }
}

