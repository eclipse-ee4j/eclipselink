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
package org.eclipse.persistence.testing.oxm.converter.jaxbenum;

import java.util.ArrayList;

public class Employee {
	private String firstName;

	public Employee() {
		super();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String newFirstName) {
		firstName = newFirstName;
	}

	public boolean equals(Object object) {
		try {
			Employee employee = (Employee) object;
			if (!this.getFirstName().equals(employee.getFirstName())) {
				return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return "Employee: " + " fname:" + getFirstName();
	}

}
