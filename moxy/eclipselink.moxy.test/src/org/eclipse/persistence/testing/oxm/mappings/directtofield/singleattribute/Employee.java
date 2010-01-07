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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute;

public class Employee {

  private Integer id;
	
  public Employee() {
    super();
  }

  public int getID() {
    return id.intValue();
  }

  public void setID(int newID) {
    id = new Integer(newID);
  }
	public Integer getIntegerId() {
		return id;
	}
	public void setIntegerId(Integer id) {
		this.id = id;
	}
  public boolean equals(Object object) {
    try {
      Employee employee = (Employee) object;
			if(getIntegerId() == null && employee.getIntegerId() == null) {
				return true;
			}
			else if(getIntegerId() == null || employee.getIntegerId() == null) {
				return false;
			}
			return this.getID() == employee.getID();
    } catch(ClassCastException e) {
      return false;
    }
  }

  public String toString()
  {
    return "Employee: " + this.getIntegerId();
  }
}
