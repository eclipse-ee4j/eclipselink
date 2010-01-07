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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.oxm.XMLRoot;

public class Department {
    List teams;
    String deptName;

    public Department() {
        teams = new ArrayList();
    }

    public void setTeams(List teams) {
        this.teams = teams;
    }

    public List getTeams() {
        return teams;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptName() {
        return deptName;
    }

    public boolean equals(Object o) {
        try {         
            Department dept = (Department)o;
            if (!this.getDeptName().equals(dept.getDeptName())) {
                return false;
            }
            if((dept.getTeams() == null && getTeams()!=null) || (getTeams() ==null && dept.getTeams()!=null))
            {
              return false;
            }
            if(dept.getTeams()!=null){
              if (dept.getTeams().size() != getTeams().size()) {
                  return false;
              }
              for(int i=0;i<dept.getTeams().size(); i++)
              {
                Object nextInList = dept.getTeams().get(i);
                Object nextInOtherList = getTeams().get(i);                    
                if(nextInList instanceof Team){
                    if(!nextInList.equals(nextInOtherList)){
                      return false;
                    }
                }else if(nextInList instanceof XMLRoot)  {
                    if(!(nextInOtherList instanceof XMLRoot)){
                      return false;
                    }
                    if (!(((XMLRoot)nextInList).getLocalName().equals(((XMLRoot)nextInOtherList).getLocalName()))) {
                      return false;
                    }if (!(((XMLRoot)nextInList).getNamespaceURI().equals(((XMLRoot)nextInOtherList).getNamespaceURI()))){
                      return false;
                    }                  
                }
              }
              //if ((!dept.getTeams().containsAll(getTeams())) || (!getTeams().containsAll(dept.getTeams()))) {
                //  return false;
              //}
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public String toString() {
        String string = "Dept- name:";
        string += this.getDeptName();
        if(getTeams() != null){
          for (int i = 0; i < getTeams().size(); i++) {
              Object next = getTeams().get(i);
              if(next instanceof Team){
                string += " " + getTeams().get(i).toString();
              }else if(next instanceof XMLRoot)
              {
                string += " xmlRoot:" + ((XMLRoot)next).getLocalName() +" " + ((XMLRoot)next).getNamespaceURI();
              }
          }
        }

        return string;
    }
}
