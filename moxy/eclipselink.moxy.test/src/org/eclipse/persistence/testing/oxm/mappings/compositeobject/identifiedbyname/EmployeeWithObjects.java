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
 *     Denise Smith - April 21/2009 - 2.0 - Initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

public class EmployeeWithObjects  {

  private int id;
  private Object emailAddress;
  private Object mailingAddress;
  private Object salary;
  
  public EmployeeWithObjects() {
    super();
  }

  public int getID() {
    return id;
  }

  public void setID(int newId) {
    id = newId;
  }

  public Object getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(Object newEmailAddress) {
    emailAddress = newEmailAddress;
  }

  public Object getMailingAddress() {
    return mailingAddress;
  }

  public void setMailingAddress(Object newMailingAddress) {
    mailingAddress = newMailingAddress;
  }

  public String toString()
  {
		String returnString =  "Employee: " + this.getID() + " ";
		if(getMailingAddress() != null)
			returnString += getMailingAddress().toString() + " ";
		if(getEmailAddress() != null)
			returnString += getEmailAddress().toString();    
		if(getSalary() != null)
			returnString += getSalary().toString();    
		return returnString;
  } 

  public boolean equals(Object object)
  {
    if(!(object instanceof EmployeeWithObjects))
      return false;
    EmployeeWithObjects employeeObject = (EmployeeWithObjects)object;
    if((this.getID() == employeeObject.getID()) &&
      ((this.getEmailAddress()==null && employeeObject.getEmailAddress()==null) || (this.getEmailAddress().equals(employeeObject.getEmailAddress()))) &&
      ((this.getSalary()==null && employeeObject.getSalary()==null) || (this.getSalary().equals(employeeObject.getSalary()))) &&
      ((this.getMailingAddress()==null && employeeObject.getMailingAddress()==null) ||(this.getMailingAddress().equals(employeeObject.getMailingAddress()))))    	
          return true;

    return false;
  }

public Object getSalary() {
	return salary;
}

public void setSalary(Object salary) {
	this.salary = salary;
}
}
