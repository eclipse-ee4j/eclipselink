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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

public class Identifier{

  private Integer sinNumber;
  private String initials;

  public Identifier() {
    super();
  }

  public Integer getSinNumber() {
    return sinNumber;
  }

  public void setSinNumber(Integer newSinNumber) {
    sinNumber= newSinNumber;
  }

  public String getInitials() {
    return initials;
  }

  public void setInitials(String newInitials) {
    initials= newInitials;
  }

  public boolean equals(Object object) {
    try {
      Identifier identifier = (Identifier) object;
      if(!this.getSinNumber().equals(identifier.getSinNumber())) {return false;}
      if(!this.getInitials().equals(identifier.getInitials())) {return false;}
      return true;
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    //return "Identifier:" + "sinnumber:" + this.getSinNumber + " fname:" + this.getInitials() + " lname:" + this.getLastName();
return getSinNumber() + "#"+ getInitials();
  }
}
