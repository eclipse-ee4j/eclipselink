/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement;

public class EmailAddress  {

  private String userID;

  public EmailAddress() {
    super();
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String newUserID) {
    userID = newUserID;
  }

  public String toString()
  {
    return " EmailAddress: " + getUserID();
  }

  public boolean equals(Object object)
  {
    if(!(object instanceof EmailAddress))
      return false;
    EmailAddress emailObject = (EmailAddress)object;
    if((((this.getUserID() == null) && (emailObject.getUserID() == null)) || this.getUserID().equals(emailObject.getUserID()) ))
        return true;

    return false;
  }
}
