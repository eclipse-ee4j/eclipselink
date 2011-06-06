 /*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 22 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

public class Employee 
{
  public String firstName;
  public String lastName;
  public String gender; 
  
  public boolean equals(Object obj){
	  Employee empObj = (Employee)obj;
	  if(!firstName.equals(empObj.firstName)){
		  return false;
	  }
	  if(!lastName.equals(empObj.lastName)){
		  return false;
	  }
	  
	  if(gender == null){
		  if(empObj.gender != null){
			  return false;
		  }
	  } else if(!gender.equals(empObj.gender)){
		  return false;
	  }
	  return true;
  }
}
