/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith -  February, 2010 - 2.1
package org.eclipse.persistence.testing.jaxb.typemappinginfo.xsitype;

public class Customer extends Person {

  private int customerId;

  private String branch;

  public int getCustomerId() {
    return customerId;
  }

  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public boolean equals(Object theObject){
      if(!(theObject instanceof Customer)){
          return false;
      }
      if(getId() != ((Customer)theObject).getId()){
          return false;
      }

      if(!getName().equals(((Customer)theObject).getName())){
          return false;
      }

      if(getCustomerId() != ((Customer)theObject).getCustomerId()){
          return false;
      }
      return true;
  }
}
