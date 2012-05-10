/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dsmith  - Dec 17/2008 - 1.1 - Initial implementation
* dmccann - Dec 31/2008 - 1.1 - Initial implementation
* ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.leafelement;

import java.util.Calendar;

public class Employee {
	private int id;
	private String firstName;
	private String lastName;
	private Calendar birthdate;

	public Employee() {
	    super();
		birthdate = Calendar.getInstance();
	  }	
	
	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}	

	public int getID() {
		return id;
	}

	public void setID(int newID) {
		id = newID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String newFirstName) {
		firstName = newFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String newLastName) {
		lastName = newLastName;
	}

	public boolean equals(Object object) {
		try {
			Employee employee = (Employee) object;
			if (this.getID() != employee.getID()) {
				return false;
			}
			if (!(this.getFirstName() == employee.getFirstName() || this
					.getFirstName().equals(employee.getFirstName()))) {
				return false;
			}
			if (!(this.getLastName() == employee.getLastName() || this
					.getLastName().equals(employee.getLastName()))) {
				return false;
			}
			
			if((this.getBirthdate()!=null && ((Employee)object).getBirthdate()==null)||
			   (this.getBirthdate()==null && ((Employee)object).getBirthdate()!=null)||
			   (!(this.getBirthdate().equals(((Employee)object).getBirthdate())))){
				   return false;
			   }						
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return "Employee: " + this.getID() + " fname:" + this.getFirstName()
				+ " lname:"	+ this.getLastName()
				+ " Birthdate:" + this.getBirthdate().get(Calendar.YEAR) +" Month:" +this.getBirthdate().get(Calendar.MONTH)+ " Day:" +this.getBirthdate().get(Calendar.DAY_OF_MONTH) + " Time:" + this.getBirthdate().get(Calendar.HOUR_OF_DAY) +":"+this.getBirthdate().get(Calendar.MINUTE)+":"+this.getBirthdate().get(Calendar.SECOND);
	}

}
