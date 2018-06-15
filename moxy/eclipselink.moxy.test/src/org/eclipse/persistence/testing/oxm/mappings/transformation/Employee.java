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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import java.util.Calendar;
import org.eclipse.persistence.sessions.Record;
/**
 *  @version $Header: Employee.java 12-apr-2007.15:52:55 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 *  A simple class that contains a transformation mapping;
 */

public class Employee
{
    protected String name;
  protected String[] normalHours;

  public Employee()
  {
    normalHours = new String[2];
  }
  public String[] getNormalHours()
  {
    return normalHours;
  }
  public String getStartTime()
  {
    return normalHours[0];
  }
  public String getEndTime()
  {
    return normalHours[1];
  }
  public void setNormalHours(String[] newHours)
  {
    normalHours = newHours;
  }
  public void setStartTime(String startTime)
  {
    normalHours[0] = startTime;
  }
  public void setEndTime(String endTime)
  {
    normalHours[1] = endTime;
  }
    public String getName()
    {
        return name;
    }
    public void setName(String newName)
    {
        name = newName;
    }

    public Object buildNameAttribute(Record row)
    {
        return row.get("name/text()");
    }
    public Object buildNameField()
    {
        return getName();
    }

  public boolean equals(Object obj)
  {
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee)obj;
        if(getStartTime() == emp.getStartTime() && getEndTime() == emp.getEndTime()) {
            return true;
        }
        return (getStartTime().equalsIgnoreCase(emp.getStartTime()) && getEndTime().equalsIgnoreCase(emp.getEndTime()) && getName().equalsIgnoreCase(emp.getName()));
  }
  public String toString()
  {
    return "Name: " + getName() + "/Start: " + getStartTime() + "/End: " + getEndTime();
  }
}

