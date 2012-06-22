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
 *     Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar;

@XmlRootElement(name="employee")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee {

	public static final int uid = 1234567;
	
	public BigDecimal id;

	public String firstName;

	@XmlElement(name = "lastname")
	public String lastName;

	public MyCalendar birthday;

	@XmlTransient
	public int age;
	
	public Address address;


	public String toString() {
		return "EMPLOYEE: " + id + " " + firstName + " " + lastName + " "
				+ birthday;
	}

	public boolean equals(Object object) {
        Employee emp = ((Employee)object);
        if(!id.equals(emp.id)){
        	return false;
        }
		if((!(emp.firstName.equals(this.firstName))) || (!(emp.lastName.equals(this.lastName))) ||(emp.age != this.age)){
			return false;
		}
				
		if(!(emp.birthday.equals(this.birthday))){
  		    return false;
  	    }
		
	    if((this.address == null) && (emp.address != null)){
		    return false;
    	}
	    if((emp.address == null) && (this.address != null)){
		    return false;
	    }
	    if(!address.equals(emp.address)){
	    	return false;
	    }
	    return true;
	}

}
