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

public abstract class Person {

  private int id;

  private String name;

   public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean equals(Object theObject){
      if(!(theObject instanceof Person)){
          return false;
      }
      if(getId() != ((Person)theObject).getId()){
          return false;
      }

      if(!getName().equals(((Person)theObject).getName())){
          return false;
      }
      return true;
  }
}
