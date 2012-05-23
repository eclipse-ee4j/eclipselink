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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection;

// JDK imports
import java.util.Vector;

public class Employee  {
	private String name;
	private Vector phones;

	public Employee() {
		phones = new Vector();
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public Vector getPhones() {
		return phones;
	}
	
	public void setPhones(Vector newPhones) {
		phones = newPhones;
	}
	
	public String toString() {
		return "Employee(name=" + name + ", numbers=" + phones.elementAt(0) + ", " + phones.elementAt(1)+ ", " + phones.elementAt(2) + ")";
	}

	public boolean equals(Object object) {
		if(!(object instanceof Employee)) {
			return false;
		}
	
		Employee employeeObject = (Employee)object;

		if (!(employeeObject.getName().equals(this.getName()))) {
			return false;
		}

		Vector phoneNumbers = employeeObject.getPhones();

		if (!(((Phone)(phoneNumbers.elementAt(0))).equals(phones.elementAt(0)))) {
			return false;
		}

		if (!(((Phone)(phoneNumbers.elementAt(1))).equals(phones.elementAt(1)))) {
			return false;
		}
		
		if (!(((Phone)(phoneNumbers.elementAt(2))).equals(phones.elementAt(2)))) {
			return false;
		}

		return true;
	}
}
