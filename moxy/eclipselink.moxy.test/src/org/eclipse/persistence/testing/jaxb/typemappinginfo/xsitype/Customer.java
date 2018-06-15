/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
