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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject;

public class Employee  {

  private int id;
  private EmailAddress emailAddress;
  private MailingAddress mailingAddress;

  public Employee() {
    super();
  }

  public int getID() {
    return id;
  }

  public void setID(int newId) {
    id = newId;
  }

  public EmailAddress getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(EmailAddress newEmailAddress) {
    emailAddress = newEmailAddress;
  }

  public MailingAddress getMailingAddress() {
    return mailingAddress;
  }

  public void setMailingAddress(MailingAddress newMailingAddress) {
    mailingAddress = newMailingAddress;
  }

  public String toString()
  {
        String returnString =  "Employee: " + this.getID() + " ";
        if(getMailingAddress() != null)
            returnString += getMailingAddress().toString() + " ";
        if(getEmailAddress() != null)
            returnString += getEmailAddress().toString();

        return returnString;
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof Employee))
      return false;
    Employee employeeObject = (Employee)object;
    if((this.getID() == employeeObject.getID()) &&
      ((this.getEmailAddress()==null && employeeObject.getEmailAddress()==null) || (this.getEmailAddress().equals(employeeObject.getEmailAddress()))) &&
      ((this.getMailingAddress()==null && employeeObject.getMailingAddress()==null) ||(this.getMailingAddress().equals(employeeObject.getMailingAddress()))))
          return true;

    return false;
  }
}
