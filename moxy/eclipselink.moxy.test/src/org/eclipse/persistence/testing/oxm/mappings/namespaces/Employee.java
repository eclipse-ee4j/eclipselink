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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

public class Employee  {

	private int id;
	private String name;

	public Employee() {
		super();
	}

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
	
	public String toString() {
		String string = "Employee(id=";
		string += this.getId();
		string += ", name='";
		string += this.getName();
		string += "')";
		return string;
	}
	
	public boolean equals(Object o) {
		try {
			Employee employee = (Employee) o;
			
			if(this.getId() != employee.getId()) {
				return false;
			}
			
			if(this.getName() == employee.getName()) {
				return true;
			}
			
			if(this.getName() == null) {
				return false;
			}
			
			return this.getName().equals(employee.getName());
		} catch(ClassCastException e) {
			return false;
		}	
	}	

}
