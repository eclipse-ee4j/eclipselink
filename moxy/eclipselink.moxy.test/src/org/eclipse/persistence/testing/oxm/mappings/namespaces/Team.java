/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import java.util.ArrayList;
import java.util.List;

public class Team {
    List employees;
    String teamName;
    Object teamLeader;
    Project project;

    public Team() {
        employees = new ArrayList();
    }

    public void setEmployees(List employees) {
        this.employees = employees;
    }

    public List getEmployees() {
        return employees;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean equals(Object o) {
        try {           
            Team team = (Team)o;
            if (!this.getTeamName().equals(team.getTeamName())) {
                return false;
            }
            
            if((getTeamLeader()== null && team.getTeamLeader()!=null)|| (team.getTeamLeader()==null && getTeamLeader()!= null))
            {
              return false;
            }
            if(getTeamLeader()!=null && (!getTeamLeader().equals(team.getTeamLeader())))
            {
              return false;
            }
            
            if (team.getEmployees().size() != getEmployees().size()) {
                return false;
            }            
            if ((!team.getEmployees().containsAll(getEmployees())) || (!getEmployees().containsAll(team.getEmployees()))) {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public String toString() {
        String string = "Team- name:";
        string += this.getTeamName();
        
        if(getTeamLeader()!= null)
        {
          string += "Leader:" +getTeamLeader().toString();
        }
        
        for (int i = 0; i < getEmployees().size(); i++) {
            string = " " + getEmployees().get(i).toString();
        }

        return string;
    }


  public void setTeamLeader(Object teamLeader)
  {
    this.teamLeader = teamLeader;
  }


  public Object getTeamLeader()
  {
    return teamLeader;
  }
}
