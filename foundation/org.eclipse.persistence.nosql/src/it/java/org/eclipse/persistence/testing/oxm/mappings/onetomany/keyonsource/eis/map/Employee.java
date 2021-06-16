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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.map;

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
     return projects;
  }

    public Project getProject(String type) {
    return (Project)projects.get(type);
  }

  public void setProjects(TreeMap newProjects) {
        this.projects = newProjects;
  }

  public void addProject(Project project) {
    projects.put(project.getType(), project);
  }

  public String toString()
  {
        String returnString =  "Employee: " + this.getFirstName() + " ";
        if(projects != null)
        {
            returnString += "Projects: ";
            returnString += projects.toString() + " ";
        }

        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;

    if(this.projects==null && employeeObject.projects !=null)
    {
      return false;
    }

    if((this.getFirstName().equals(employeeObject.getFirstName())) &&
      ((this.getProjects()==null && employeeObject.getProjects()==null) || (this.getProjects().equals(employeeObject.getProjects()))))
          return true;

    return false;
  }
}
