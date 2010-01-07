/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.indirection;

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

  public void setLastName(String newLastName) {
    lastName = newLastName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setFirstName(String newFirstName) {
    firstName = newFirstName;
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
