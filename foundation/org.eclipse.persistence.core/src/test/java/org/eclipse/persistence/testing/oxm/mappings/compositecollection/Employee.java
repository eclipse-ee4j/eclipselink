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
    StringBuilder output = new StringBuilder("Employee: " + this.getID());
        if(getMailingAddresses() != null)
        {
       output.append(" MailingAddresses:");
       for(int i=0; i<getMailingAddresses().size(); i++)
       {
         output.append(getMailingAddresses().elementAt(i));
       }
        }

    for(int i=0; i<getEmailAddresses().size(); i++)
    {
      output.append(getEmailAddresses().elementAt(i));
    }
    return output.toString();
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee employeeObject))
      return false;

      if(this.getMailingAddresses()==null && employeeObject.getMailingAddresses()!=null){
      return false;
    }

    if(employeeObject.getMailingAddresses()==null && this.getMailingAddresses()!=null){
      return false;
    }

      return (this.getID() == employeeObject.getID()) &&
              (this.getEmailAddresses().containsAll(employeeObject.getEmailAddresses())) &&
              ((this.getMailingAddresses() == null && employeeObject.getMailingAddresses() == null) || (this.getMailingAddresses().containsAll(employeeObject.getMailingAddresses())));
  }
}
