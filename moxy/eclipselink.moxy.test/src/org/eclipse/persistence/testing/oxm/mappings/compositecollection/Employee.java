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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection;

import java.util.Vector;

public class Employee  {

  private int id;
  private Vector emailAddresses;
  private Vector mailingAddresses;
  
  public Employee() {
    super();
    emailAddresses = new Vector();
    mailingAddresses = new Vector();
  }

  public int getID() {
    return id;
  }

  public void setID(int newId) {
    id = newId;
  }

  public Vector getEmailAddresses() {
    return emailAddresses;
  }

  public void setEmailAddresses(Vector newEmailAddresses) {
    emailAddresses = newEmailAddresses;
  }

  public Vector getMailingAddresses() {
    return mailingAddresses;
  }

  public void setMailingAddresses(Vector newMailingAddresses) {
    mailingAddresses = newMailingAddresses;
  }

  public String toString()
  {
    String output =  "Employee: " + this.getID();
		if(getMailingAddresses() != null)
		{
       output += " MailingAddresses:";
       for(int i=0; i<getMailingAddresses().size(); i++)
       {
         output += getMailingAddresses().elementAt(i);
       }
		}
    
    for(int i=0; i<getEmailAddresses().size(); i++)
    {
      output += getEmailAddresses().elementAt(i);      
    }
    return output;
  } 

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;

    if(this.getMailingAddresses()==null && employeeObject.getMailingAddresses()!=null){
      return false;
    }

    if(employeeObject.getMailingAddresses()==null && this.getMailingAddresses()!=null){
      return false;
    }
    
    if((this.getID() == employeeObject.getID()) &&
      (this.getEmailAddresses().containsAll(employeeObject.getEmailAddresses())) &&
      ((this.getMailingAddresses()==null && employeeObject.getMailingAddresses()==null) || (this.getMailingAddresses().containsAll(employeeObject.getMailingAddresses()))))
          return true;

    return false;
  }
}
