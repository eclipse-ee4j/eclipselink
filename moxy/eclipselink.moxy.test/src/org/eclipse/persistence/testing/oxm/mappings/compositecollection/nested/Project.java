/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested;

import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;

public class Project  {
  
  private String name;
  private Vector employees;

  public Project() {
		employees = new Vector();
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    name = newName;
  }

	public Vector getEmployees()
	{
		return employees;
	}

	public void setEmployees(Vector newEmployees)
	{
		employees = newEmployees;
	}
	public String toString()
  {
    String returnString= "Project: " + this.getName() + " ";
		if(employees !=null)
		{
			for(int i=0; i<employees.size(); i++)
			{
				returnString += employees.elementAt(i) + " ";
			}
		}
		return returnString;
 } 

  public boolean equals(Object object)
  {
    if(!(object instanceof Project))
      return false;
    Project projectObject = (Project)object;
    if((this.getName().equals(projectObject.getName())) &&
      (this.getEmployees().containsAll(projectObject.getEmployees())))
          return true;

    return false;
  }	

}
