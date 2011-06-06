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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.oxm.XMLRoot;

public class Company {
    String companyName;
    Employee manager;
    List departments;

    public Company() {
        departments = new ArrayList();
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public Employee getManager() {
        return manager;
    }

    public void setDepartments(List departments) {
        this.departments = departments;
    }

    public List getDepartments() {
        return departments;
    }

    public boolean equals(Object o) {
        try {
            Company theCompany = (Company)o;
            if (!theCompany.getCompanyName().equals(getCompanyName())) {
                return false;
            }
            if (((getManager() == null) && (theCompany.getManager() != null)) || ((theCompany.getManager() == null) && (getManager() != null))) {
                return false;
            }
            if ((getManager() != null) && (!getManager().equals(theCompany.getManager()))) {
                return false;
            }
            if (getDepartments().size() != theCompany.getDepartments().size()) {
                return false;
            }
            //if ((!theCompany.getDepartments().containsAll(getDepartments())) || (!getDepartments().containsAll(theCompany.getDepartments()))) {
                //return false;
            //}
            for (int i = 0; i < getDepartments().size(); i++) {
                Object first = getDepartments().get(i);
                Object second = theCompany.getDepartments().get(i);
                if (first instanceof Department && second instanceof Department) {
                    if (!first.equals(second)) {
                        return false;
                    }
                } else if (first instanceof XMLRoot && second instanceof XMLRoot) {
                    if (!(((XMLRoot)first).getLocalName().equals(((XMLRoot)second).getLocalName()))) {
                        return false;
                    }
                    if (!(((XMLRoot)first).getNamespaceURI().equals(((XMLRoot)second).getNamespaceURI()))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }
     public String toString() {
        String string = "Company( name:";
          string += getCompanyName() +" ";           
          if(getManager() != null){
            string += getManager().toString();
          }
        
        for (int i = 0; i < getDepartments().size(); i++) {
            string += getDepartments().get(i).toString();
        }
        return string;
    }
}
