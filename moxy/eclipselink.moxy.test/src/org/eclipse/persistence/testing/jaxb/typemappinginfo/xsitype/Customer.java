/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
