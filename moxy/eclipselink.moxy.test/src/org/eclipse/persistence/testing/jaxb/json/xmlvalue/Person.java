/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

    private String firstName;	
	private List<String> middleNames = new ArrayList<String>();
    private String lastName;    	   
    private Address address;   
    private PhoneNumber phoneNumber;	

	public Person(){      
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    

	public List<String> getMiddleNames() {
		return middleNames;
	}

	public void setMiddleNames(List<String> middleNames) {
		this.middleNames = middleNames;
	}


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(PhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
    public boolean equals(Object obj) {
        Person person;
        try {
        	person = (Person) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if(address == null){
            if(person.address != null){
                return false;
            }
        } else if(!address.equals(person.address)){
            return false;
        }
        
        if(phoneNumber == null){
            if(person.phoneNumber != null){
                return false;
            }
        } else if(!phoneNumber.equals(person.phoneNumber)){
            return false;
        }
        
        if(middleNames == null){
        	if(person.middleNames != null){
        		return false;
        	}
        }else {
        	if(middleNames.size() != person.middleNames.size()){
        		return false;
        	}
        	if(!(middleNames.containsAll(person.middleNames) && person.middleNames.containsAll(middleNames))){
        		return false;
        	}
        }

        return firstName.equals(person.firstName) && lastName.equals(person.lastName);
    }

}
