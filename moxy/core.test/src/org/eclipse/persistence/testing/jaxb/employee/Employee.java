/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.employee;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
public class Employee 
{
	@XmlAttribute(name="id")
	public int id;

	public String firstName;

	public String lastName;
	
	public java.util.Calendar birthday;

	@XmlTransient
	public int age;

	@XmlElement(name="responsibility")
	@XmlElementWrapper(name="responsibilities")
	public java.util.Collection responsibilities;
	
  public String toString()
	{
		return "EMPLOYEE: " + id +" " + firstName +" " + lastName +" " + birthday;
	}
	
	public boolean equals(Object object) {
		 Employee emp = ((Employee)object);
		if((emp.id != this.id) ||(!(emp.firstName.equals(this.firstName))) || (!(emp.lastName.equals(this.lastName))) ||(emp.age != this.age))
		{
			return false;
		}
		if(!(emp.birthday.equals(this.birthday)))
  	{
  		return false;
  	}
		if((emp.responsibilities == null) && (this.responsibilities != null))
		{
			return false;
		}
  	if((this.responsibilities == null) && (emp.responsibilities != null))
  	{
  		return false;
  	}
		if(this.responsibilities !=null)
		{
			if(!(this.responsibilities.containsAll(emp.responsibilities))){
				return false;
			}
  		if(!(emp.responsibilities.containsAll(this.responsibilities))){
  			return false;
  		}
		}
		//need to compare responsibilities
		return true;
	}
}