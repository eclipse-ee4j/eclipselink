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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute;

import java.util.Vector;

public class Employee  {

  private Object identifier;
    private Vector responsibilities;
    private Vector outdoorResponsibilities;

  public Employee() {
    super();
  }

  public Object getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Object newIdentifier) {
    identifier= newIdentifier;
  }

  public Vector getResponsibilities() {
    return responsibilities;
  }

  public void setResponsibilities(Vector newResponsibilities) {
    responsibilities = newResponsibilities;
  }

    public Vector getOutdoorResponsibilities() {
    return outdoorResponsibilities;
  }

  public void setOutdoorResponsibilities(Vector newOutdoorResponsibilities) {
    outdoorResponsibilities = newOutdoorResponsibilities;
  }

  public String toString()
  {
        StringBuilder returnString = new StringBuilder("Employee: " + this.getIdentifier() + " ");
        if(getResponsibilities() != null)
        {
            returnString.append("Responsiblities: ");
            for(int i=0; i<getResponsibilities().size(); i++)
            {
                Object next = getResponsibilities().elementAt(i);
                returnString.append(next.toString()).append(" ");
            }
        }

        if(getOutdoorResponsibilities() != null)
        {
            returnString.append("Outdoor Responsiblities: ");
            for(int i=0; i<getOutdoorResponsibilities().size(); i++)
            {
                Object next = getOutdoorResponsibilities().elementAt(i);
                returnString.append(next.toString()).append(" ");
            }
        }
        return returnString.toString();
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee employeeObject))
      return false;

      if(this.getResponsibilities()==null && employeeObject.getResponsibilities()!=null)
    {
      return false;
    }
    if(employeeObject.getResponsibilities()==null && this.getResponsibilities()!=null)
    {
      return false;
    }


      return (this.getIdentifier().equals(employeeObject.getIdentifier())) &&
              ((this.getResponsibilities() == null && employeeObject.getResponsibilities() == null) || (this.getResponsibilities().isEmpty() && employeeObject.getResponsibilities().isEmpty()) || (this.getResponsibilities().containsAll(employeeObject.getResponsibilities()))) &&
              ((this.getOutdoorResponsibilities() == null && employeeObject.getOutdoorResponsibilities() == null) || (this.getOutdoorResponsibilities().isEmpty() && employeeObject.getOutdoorResponsibilities().isEmpty()) || (this.getOutdoorResponsibilities().containsAll(employeeObject.getOutdoorResponsibilities())));
  }
}
