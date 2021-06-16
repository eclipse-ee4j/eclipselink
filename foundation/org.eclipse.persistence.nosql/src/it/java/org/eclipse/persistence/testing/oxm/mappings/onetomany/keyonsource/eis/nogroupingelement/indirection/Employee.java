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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.indirection;

import java.util.Vector;

import org.eclipse.persistence.indirection.*;

import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.Project;

public class Employee  {

  private String firstName;
    private String lastName;
    private ValueHolderInterface projects;

  public Employee() {
    super();
    this.projects = new ValueHolder(new Vector());
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
  }

  public void setLastName(String newLastName) {
    lastName = newLastName;
  }

  public String getLastName() {
    return lastName;
  }


  public Vector getProjects() {
     return (Vector) projects.getValue();
  }

  public void setProjects(Vector newProjects) {
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
