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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested;

import java.util.Vector;

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
    StringBuilder returnString= new StringBuilder("Project: " + this.getName() + " ");
        if(employees !=null)
        {
            for(int i=0; i<employees.size(); i++)
            {
                returnString.append(employees.elementAt(i)).append(" ");
            }
        }
        return returnString.toString();
 }

  public boolean equals(Object object)
  {
    if(!(object instanceof Project))
      return false;
    Project projectObject = (Project)object;
      return (this.getName().equals(projectObject.getName())) &&
              (this.getEmployees().containsAll(projectObject.getEmployees()));
  }

}
