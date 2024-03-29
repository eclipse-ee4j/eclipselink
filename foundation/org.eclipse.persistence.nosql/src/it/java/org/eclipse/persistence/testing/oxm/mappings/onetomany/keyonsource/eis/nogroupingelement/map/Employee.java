/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.map;

import java.util.TreeMap;


public class Employee  {

  private String firstName;
    private TreeMap projects;

  public Employee() {
    super();
    this.projects = new TreeMap();
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }

  public TreeMap getProjects() {
     return  projects;
  }

    public Project getProject(String type) {
    return (Project)projects.get(type);
  }
  public void setProjects(TreeMap  newProjects) {
        this.projects = newProjects;
  }

  public void addProject(Project project) {
    projects.put(project.getType(), project);
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
    if(!(object instanceof Employee employeeObject))
      return false;

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
