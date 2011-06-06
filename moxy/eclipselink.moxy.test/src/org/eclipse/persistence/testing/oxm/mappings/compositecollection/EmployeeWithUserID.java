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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection;

import java.util.ArrayList;
import java.util.List;

public class EmployeeWithUserID {
    private String userID;
    private List emailAddresses;
    private String name;

    public EmployeeWithUserID() {
    emailAddresses = new ArrayList();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String newUserID) {
        userID = newUserID;
    }

    public List getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List newEmailAddresses) {
        emailAddresses = newEmailAddresses;
    }

    public String toString() {
        String output = "Employee: " + this.getUserID() + " " + this.getName();

        if (getEmailAddresses() != null) {
            for (int i = 0; i < getEmailAddresses().size(); i++) {
                output += getEmailAddresses().get(i);
            }
        }
        return output;
    }

    public boolean equals(Object object) {
        if (!(object instanceof EmployeeWithUserID)) {
            return false;
        }
        EmployeeWithUserID employeeObject = (EmployeeWithUserID)object;

        if (!this.getUserID().equals(employeeObject.getUserID()))
        {
          return false;
        }
        
           if (!this.getName().equals(employeeObject.getName()))
        {
          return false;
        }
        if (this.getEmailAddresses().size() != employeeObject.getEmailAddresses().size())
        {
          return false;
        }
        if (!this.getEmailAddresses().containsAll(employeeObject.getEmailAddresses())) {
            return false;
        }

        return true;
    }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getName()
  {
    return name;
  }
}
