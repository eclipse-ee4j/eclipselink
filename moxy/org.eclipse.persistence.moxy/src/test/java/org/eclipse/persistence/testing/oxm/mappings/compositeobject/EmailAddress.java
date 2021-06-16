/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositeobject;

public class EmailAddress  {

  private String userID;
  private String domain;

  public EmailAddress() {
    super();
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String newUserID) {
    userID = newUserID;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String newDomain) {
    domain = newDomain;
  }
  public String toString()
  {
    return " EmailAddress: " + getUserID() +"@" +getDomain();
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof EmailAddress))
      return false;
    EmailAddress emailObject = (EmailAddress)object;
    if((((this.getUserID() == null) && (emailObject.getUserID() == null)) || this.getUserID().equals(emailObject.getUserID()) ) &&
       (((this.getDomain() == null) && (emailObject.getDomain() == null)) || this.getDomain().equals(emailObject.getDomain()) ))
        return true;

    return false;
  }
}
