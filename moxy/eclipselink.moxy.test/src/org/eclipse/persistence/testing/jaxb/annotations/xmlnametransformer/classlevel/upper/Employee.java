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
 *     Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.classlevel.upper;

import javax.xml.bind.annotation.*;

import org.eclipse.persistence.oxm.annotations.XmlNameTransformer;

@XmlNameTransformer(MyUpperNameTransformer.class)
@XmlRootElement(name = "employee-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee {

	public static final int uid = 1234567;

	@XmlAttribute(name = "id")
	public int id;

	public String firstName;

	@XmlElement(name = "lastname")
	public String lastName;

	private transient String blah;	

	@XmlAttribute
	public java.util.Calendar birthday;

	@XmlTransient
	public int age;

	public Address address;
	
	@XmlElement(name = "responsibility")
	@XmlElementWrapper(name = "responsibilities")
	public java.util.Collection responsibilities;

	public java.util.Collection responsibilities2;
	
	public String toString() {
		return "EMPLOYEE: " + id + " " + firstName + " " + lastName + " "
				+ birthday;
	}

	public boolean equals(Object object) {
        Employee emp = ((Employee)object);
		if((emp.id != this.id) ||(!(emp.firstName.equals(this.firstName))) || (!(emp.lastName.equals(this.lastName))) ||(emp.age != this.age)){
			return false;
		}
		if(!(emp.birthday.equals(this.birthday))){
  		    return false;
  	    }
		if((emp.responsibilities == null) && (this.responsibilities != null)){
			return false;
		}
  	    if((this.responsibilities == null) && (emp.responsibilities != null)){
  		    return false;
  	    }
	    if(this.responsibilities !=null){
		    if(!(this.responsibilities.containsAll(emp.responsibilities))){
			    return false;
		    }
		    if(!(emp.responsibilities.containsAll(this.responsibilities))){
   		    	return false;
	    	}
	    }
  	    if((emp.responsibilities2 == null) && (this.responsibilities2 != null)){
		    return false;
	    }
	    if((this.responsibilities2 == null) && (emp.responsibilities2 != null)){
		    return false;
    	}
	    if(this.responsibilities2 !=null){
			if(!(this.responsibilities2.containsAll(emp.responsibilities2))){
				return false;
			}
  		    if(!(emp.responsibilities2.containsAll(this.responsibilities2))){
  			    return false;
  		    }
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
		//need to compare responsibilities
		return true;
	}

	public void setBlah(String blah) {
	    this.blah = blah;
	}
	
	@XmlElement
	public int getSomeNumber(){
		return 10;
	}
	
		
}
