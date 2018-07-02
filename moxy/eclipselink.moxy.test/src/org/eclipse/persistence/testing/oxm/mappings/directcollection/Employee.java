/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directcollection;

import java.util.Calendar;
import java.util.Vector;

public class Employee  {

  private int id;
    private Vector responsibilities;
    private Vector outdoorResponsibilities;

  public Employee() {
    super();
  }

  public int getID() {
    return id;
  }

  public void setID(int newId) {
    id = newId;
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
        String returnString =  "Employee: " + this.getID() + " ";
        if(getResponsibilities() != null)
        {
            returnString += "Responsiblities: ";
            for(int i=0; i<getResponsibilities().size(); i++)
            {
                Object next = getResponsibilities().elementAt(i);
                if(next != null){
                    returnString += next.toString() + " ";
                }else{
                    returnString += "null_item" + " ";
                }
            }
        }

        if(getOutdoorResponsibilities() != null)
        {
            returnString += "Outdoor Responsiblities: ";
            for(int i=0; i<getOutdoorResponsibilities().size(); i++)
            {
                Object next = getOutdoorResponsibilities().elementAt(i);
                returnString += next.toString() + " ";
            }
        }
        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;

    if(this.getResponsibilities()==null && employeeObject.getResponsibilities()!=null)
    {
      return false;
    }
    if(employeeObject.getResponsibilities()==null && this.getResponsibilities()!=null)
    {
      return false;
    }


    if((this.getID() == employeeObject.getID()) &&
      ((this.getResponsibilities()==null && employeeObject.getResponsibilities()==null) ||(this.getResponsibilities().isEmpty() && employeeObject.getResponsibilities().isEmpty()) || (fixCalendar(this.getResponsibilities()).containsAll((fixCalendar(employeeObject.getResponsibilities()))))) &&
            ((this.getOutdoorResponsibilities()==null && employeeObject.getOutdoorResponsibilities()==null) ||(this.getOutdoorResponsibilities().isEmpty() && employeeObject.getOutdoorResponsibilities().isEmpty())|| (fixCalendar(this.getOutdoorResponsibilities()).containsAll(fixCalendar(employeeObject.getOutdoorResponsibilities()))))
            )
          return true;

    return false;
  }

  private Vector fixCalendar(Vector v) {

      if (null == v) {
          return null;
      }

      Vector result = new Vector();
      for (Object item : v) {
          if (null != item && item instanceof java.util.Calendar) {
              Calendar normalizedCalendar = Calendar.getInstance();
              normalizedCalendar.setTimeInMillis(((Calendar)item).getTimeInMillis());
              result.add(normalizedCalendar);
          } else {
              result.add(item);
          }
      }

      return result;
  }
}
