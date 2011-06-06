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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.inheritance;

public class Person  {
	private String firstname;
	private String lastname;

	public Person() {}

	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String newFirstName) {
		firstname = newFirstName;
	}

	public String getLastName() {
		return lastname;
	}

	public void setLastName(String newLastName) {
		lastname = newLastName;
	}

	public String toString() {
		return "Person(name=" + firstname + " " + lastname + ")";
	}

	public boolean equals(Object object) {
		if(!(object instanceof Person)) {
			return false;
		}

		String firstname = ((Person)object).getFirstName();

		if (firstname == null) {
			if (this.getFirstName() == null) {
				return true;
			}
			return false;
		}
		
		if(firstname.equals(this.getFirstName())) {
			return true;
		}
		
		return false;
	}
}
