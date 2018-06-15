/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested;

import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;

public class Project  {

  private String name;
  private Employee leader;

  public Project() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    name = newName;
  }

    public Employee getLeader()
    {
        return leader;
    }

    public void setLeader(Employee newLeader)
    {
        leader = newLeader;
    }
    public String toString()
  {
    return "Project: " + this.getName() + " " + this.getLeader();
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Project))
      return false;
    Project projectObject = (Project)object;
    if((this.getName().equals(projectObject.getName())) &&
      (this.getLeader().equals(projectObject.getLeader())))
          return true;

    return false;
  }

}
