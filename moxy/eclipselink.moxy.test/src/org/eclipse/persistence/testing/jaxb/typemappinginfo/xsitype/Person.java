/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -  February, 2010 - 2.1 
 ******************************************************************************/  
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